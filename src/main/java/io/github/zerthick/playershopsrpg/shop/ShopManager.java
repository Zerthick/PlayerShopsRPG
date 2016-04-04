package io.github.zerthick.playershopsrpg.shop;

import com.flowpowered.math.vector.Vector3i;
import ninja.leaping.configurate.objectmapping.Setting;
import ninja.leaping.configurate.objectmapping.serialize.ConfigSerializable;
import org.spongepowered.api.entity.living.player.Player;

import java.util.*;

@ConfigSerializable
public class ShopManager {

    @Setting("shops")
    private Map<UUID, Set<ShopContainer>> shopMap;

    public ShopManager(Map<UUID, Set<ShopContainer>> shopMap) {
        this.shopMap = shopMap;
    }

    public Optional<ShopContainer> getShop(UUID worldUUID, Vector3i location){
        for (ShopContainer shopContainer : shopMap.getOrDefault(worldUUID, new HashSet<>())) {
            if (shopContainer.isShop(location)) {
                return Optional.of(shopContainer);
            }
        }
        return Optional.empty();
    }

    public Optional<ShopContainer> getShop(Player player) {
        return getShop(player.getWorld().getUniqueId(), player.getLocation().getBlockPosition());
    }

    public void addShop(UUID worldUUID, ShopContainer shopContainer) {
        Set<ShopContainer> shopContainers = shopMap.getOrDefault(worldUUID, new HashSet<>());
        shopContainers.add(shopContainer);
        shopMap.put(worldUUID, shopContainers);
    }

    public Optional<ShopContainer> removeShop(UUID worldUUID, Vector3i location){
        Optional<ShopContainer> shopOptional = getShop(worldUUID, location);
        if(shopOptional.isPresent()){
            shopMap.get(worldUUID).remove(shopOptional.get());
        }
        return shopOptional;
    }

    public Optional<ShopContainer> removeShop(Player player) {
        return removeShop(player.getWorld().getUniqueId(), player.getLocation().getBlockPosition());
    }
}
