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
import io.github.zerthick.playershopsrpg.shop.ShopItem;
import ninja.leaping.configurate.ConfigurationNode;
import ninja.leaping.configurate.objectmapping.ObjectMappingException;
import ninja.leaping.configurate.objectmapping.serialize.TypeSerializer;
import ninja.leaping.configurate.objectmapping.serialize.TypeSerializers;
import org.spongepowered.api.item.inventory.ItemStackSnapshot;

public class ShopItemSerializer implements TypeSerializer<ShopItem> {

    public static void register() {
        TypeSerializers.getDefaultSerializers().registerType(TypeToken.of(ShopItem.class), new ShopItemSerializer());
    }

    @Override
    public ShopItem deserialize(TypeToken<?> type, ConfigurationNode value) throws ObjectMappingException {

        ItemStackSnapshot itemStackSnapshot = value.getNode("itemStack").getValue(TypeToken.of(ItemStackSnapshot.class));
        int itemAmount = value.getNode("itemAmount").getInt();
        int itemMaxAmount = value.getNode("itemMaxAmount").getInt();
        double itemBuyPrice = value.getNode("itemBuyPrice").getDouble();
        double itemSellPrice = value.getNode("itemSellPrice").getDouble();

        return new ShopItem(itemStackSnapshot, itemAmount, itemMaxAmount, itemBuyPrice, itemSellPrice);
    }

    @Override
    public void serialize(TypeToken<?> type, ShopItem obj, ConfigurationNode value) throws ObjectMappingException {
        value.getNode("itemStack").setValue(TypeToken.of(ItemStackSnapshot.class), obj.getItemStackSnapShot());
        value.getNode("itemAmount").setValue(obj.getItemAmount());
        value.getNode("itemMaxAmount").setValue(obj.getItemMaxAmount());
        value.getNode("itemBuyPrice").setValue(obj.getItemBuyPrice());
        value.getNode("itemSellPrice").setValue(obj.getItemSellPrice());
    }
}
