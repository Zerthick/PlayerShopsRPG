/*
 * Copyright (C) 2016  Zerthick
 *
 * This file is part of PlayerShopsRPG.
 *
 * PlayerShopsRPG is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 2 of the License, or
 * (at your option) any later version.
 *
 * PlayerShopsRPG is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with PlayerShopsRPG.  If not, see <http://www.gnu.org/licenses/>.
 */

package io.github.zerthick.playershopsrpg.shop;

import com.flowpowered.math.vector.Vector3i;
import io.github.zerthick.playershopsrpg.PlayerShopsRPG;
import org.spongepowered.api.Sponge;
import org.spongepowered.api.entity.living.player.Player;

import java.util.*;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

public class ShopManager {

    private Map<UUID, Set<ShopContainer>> shopMap;

    private Map<UUID, ShopContainer> shopUUIDMap;

    private Map<UUID, UUID> playerShopCache;

    private PlayerShopsRPG plugin;

    public ShopManager(Map<UUID, Set<ShopContainer>> shopMap, PlayerShopsRPG plugin) {
        this.shopMap = shopMap;
        shopUUIDMap = new HashMap<>();
        for (Set<ShopContainer> shopContainerSet : this.shopMap.values()) {
            shopUUIDMap.putAll(shopContainerSet.stream().collect(Collectors.toMap(c -> c.getShop().getUUID(), c -> c)));
        }
        playerShopCache = new HashMap<>();
        this.plugin = plugin;
    }

    public Optional<ShopContainer> getShop(UUID worldUUID, Vector3i location) {
        for (ShopContainer shopContainer : shopMap.getOrDefault(worldUUID, new HashSet<>())) {
            if (shopContainer.isShop(location)) {
                return Optional.of(shopContainer);
            }
        }
        return Optional.empty();
    }

    public Optional<ShopContainer> getShop(Player player) {

        // Check player cache first
        if (playerShopCache.containsKey(player.getUniqueId())) {
            Optional<ShopContainer> shopContainerOptional = getShopByUUID(playerShopCache.get(player.getUniqueId()), player);
            if (shopContainerOptional.isPresent()) {
                return shopContainerOptional;
            }
        }

        Optional<ShopContainer> shopContainerOptional = getShop(player.getWorld().getUniqueId(), player.getLocation().getBlockPosition());
        if (shopContainerOptional.isPresent()) {
            updateCache(player, shopContainerOptional.get());
        }
        return shopContainerOptional;

    }

    public Optional<ShopContainer> getShopByUUID(UUID shopUUID) {
        return Optional.ofNullable(shopUUIDMap.get(shopUUID));
    }

    public Optional<ShopContainer> getShopByUUID(UUID shopUUID, Vector3i location) {
        Optional<ShopContainer> shopContainerOptional = getShopByUUID(shopUUID);
        if (shopContainerOptional.isPresent()) {
            if (shopContainerOptional.get().isShop(location)) {
                return shopContainerOptional;
            }
        }
        return Optional.empty();
    }

    public Optional<ShopContainer> getShopByUUID(UUID shopUUID, Player player) {
        return getShopByUUID(shopUUID, player.getLocation().getBlockPosition());
    }

    public void addShop(UUID worldUUID, ShopContainer shopContainer) {
        Set<ShopContainer> shopContainers = shopMap.getOrDefault(worldUUID, new HashSet<>());
        shopContainers.add(shopContainer);
        shopMap.put(worldUUID, shopContainers);
        shopUUIDMap.put(shopContainer.getShop().getUUID(), shopContainer);
    }

    public Optional<ShopContainer> removeShop(UUID worldUUID, Vector3i location){
        Optional<ShopContainer> shopOptional = getShop(worldUUID, location);
        if(shopOptional.isPresent()){
            ShopContainer container = shopOptional.get();
            shopMap.get(worldUUID).remove(container);
            shopUUIDMap.remove(container.getShop().getUUID());
        }
        return shopOptional;
    }

    public Optional<ShopContainer> removeShop(Player player) {
        return removeShop(player.getWorld().getUniqueId(), player.getLocation().getBlockPosition());
    }

    public Map<UUID, Set<ShopContainer>> getShopMap() {
        return shopMap;
    }

    private void updateCache(Player player, ShopContainer shopContainer) {
        playerShopCache.put(player.getUniqueId(), shopContainer.getShop().getUUID());
        Sponge.getScheduler().createTaskBuilder().delay(5, TimeUnit.MINUTES).execute(() ->
        {
            if (playerShopCache.get(player.getUniqueId()).equals(shopContainer.getShop().getUUID())) {
                playerShopCache.remove(player.getUniqueId());
            }
        }).submit(plugin);
    }
}