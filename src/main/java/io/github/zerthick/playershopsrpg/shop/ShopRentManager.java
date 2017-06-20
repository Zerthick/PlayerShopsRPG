/*
 * Copyright (C) 2017  Zerthick
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

import io.github.zerthick.playershopsrpg.PlayerShopsRPG;
import io.github.zerthick.playershopsrpg.utils.config.sql.SQLDataUtil;
import org.spongepowered.api.Sponge;

import java.time.LocalDateTime;
import java.util.Iterator;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

public class ShopRentManager {

    private static ShopRentManager instance = null;

    private Map<UUID, LocalDateTime> shopRentMap;
    private ShopManager shopManager;

    protected ShopRentManager() {
        //Singleton Design Pattern
    }

    public static ShopRentManager getInstance() {
        if (instance == null) {
            instance = new ShopRentManager();
        }
        return instance;
    }

    public void init(PlayerShopsRPG plugin, Map<UUID, LocalDateTime> shopRentMap) {
        this.shopRentMap = shopRentMap;
        shopManager = plugin.getShopManager();

        Sponge.getScheduler().createTaskBuilder().interval(15, TimeUnit.MINUTES).execute(() -> {
            Iterator<Map.Entry<UUID, LocalDateTime>> it = this.shopRentMap.entrySet().iterator();
            while (it.hasNext()) {
                Map.Entry<UUID, LocalDateTime> entry = it.next();
                if (LocalDateTime.now().isAfter(entry.getValue())) {
                    Optional<ShopContainer> shopContainerOptional = shopManager.getShopByUUID(entry.getKey());
                    shopContainerOptional.ifPresent(shopContainer -> {
                        SQLDataUtil.deleteShopRent(shopContainer.getShop().getUUID(),plugin.getLogger());
                        shopContainer.getShop().rentExpire(true, true);
                    });
                    it.remove();
                }
            }

        }).submit(plugin);
    }

    public void rentShop(Shop shop, long durationInHours) {
        shopRentMap.put(shop.getUUID(), LocalDateTime.now().plusHours(durationInHours));
    }

    public LocalDateTime getShopExpireTime(Shop shop) {
        return shopRentMap.get(shop.getUUID());
    }

    public Map<UUID, LocalDateTime> getShopRentMap() {
        return shopRentMap;
    }
}
