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

package io.github.zerthick.playershopsrpg.utils.config.serializers.shop;

import com.google.common.reflect.TypeToken;
import io.github.zerthick.playershopsrpg.shop.Shop;
import io.github.zerthick.playershopsrpg.shop.ShopItem;
import ninja.leaping.configurate.ConfigurationNode;
import ninja.leaping.configurate.objectmapping.ObjectMappingException;
import ninja.leaping.configurate.objectmapping.serialize.TypeSerializer;
import ninja.leaping.configurate.objectmapping.serialize.TypeSerializers;

import java.util.*;
import java.util.stream.Collectors;

public class ShopSerializer implements TypeSerializer<Shop> {

    public static void register() {
        TypeSerializers.getDefaultSerializers().registerType(TypeToken.of(Shop.class), new ShopSerializer());
    }

    @Override
    public Shop deserialize(TypeToken<?> type, ConfigurationNode value) throws ObjectMappingException {
        UUID shopUUID = value.getNode("shopUUID").getValue(TypeToken.of(UUID.class));
        String name = value.getNode("shopName").getString();
        UUID ownerUUID = value.getNode("ownerUUID").getValue(TypeToken.of(UUID.class));
        UUID renterUUID = value.getNode("renterUUID").getValue(TypeToken.of(UUID.class));
        Set<UUID> managerUUIDset = new HashSet<>(value.getNode("managerSet").getList(TypeToken.of(UUID.class)));
        Map<UUID, ShopItem> items = value.getNode("items").getList(TypeToken.of(ShopItem.class)).stream().collect(Collectors.toMap(ShopItem::getShopItemUUID, shopItem -> shopItem));
        boolean unlimitedMoney = value.getNode("unlimitedMoney").getBoolean();
        boolean unlimitedStock = value.getNode("unlimitedStock").getBoolean();
        String shopType = value.getNode("type").getString();
        double price = value.getNode("price").getDouble(-1);
        double rent = value.getNode("rent").getDouble(-1);

        return new Shop(shopUUID, name, ownerUUID, renterUUID, managerUUIDset, items, unlimitedMoney, unlimitedStock, shopType, price, rent);
    }

    @Override
    public void serialize(TypeToken<?> type, Shop obj, ConfigurationNode value) throws ObjectMappingException {
        value.getNode("shopUUID").setValue(TypeToken.of(UUID.class), obj.getUUID());
        value.getNode("shopName").setValue(obj.getName());
        value.getNode("ownerUUID").setValue(TypeToken.of(UUID.class), obj.getOwnerUUID());
        value.getNode("renterUUID").setValue(TypeToken.of(UUID.class), obj.getRenterUUID());
        Set<UUID> managerUUIDSet = obj.getManagerUUIDSet();
        value.getNode("managerSet").setValue(new TypeToken<List<UUID>>() {
        }, new ArrayList<>(managerUUIDSet));
        value.getNode("items").setValue(new TypeToken<List<ShopItem>>() {
        }, new ArrayList<>(obj.getItems().values()));
        value.getNode("unlimitedMoney").setValue(obj.isUnlimitedMoney());
        value.getNode("unlimitedStock").setValue(obj.isUnlimitedStock());
        value.getNode("type").setValue(obj.getType());
        value.getNode("price").setValue(obj.getPrice());
        value.getNode("rent").setValue(obj.getRent());
    }
}
