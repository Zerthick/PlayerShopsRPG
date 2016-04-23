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

package io.github.zerthick.playershopsrpg.utils.config.serializers.shop;

import com.google.common.reflect.TypeToken;
import io.github.zerthick.playershopsrpg.region.RectangularRegion;
import io.github.zerthick.playershopsrpg.region.Region;
import io.github.zerthick.playershopsrpg.shop.Shop;
import io.github.zerthick.playershopsrpg.shop.ShopContainer;
import ninja.leaping.configurate.ConfigurationNode;
import ninja.leaping.configurate.objectmapping.ObjectMappingException;
import ninja.leaping.configurate.objectmapping.serialize.TypeSerializer;

public class ShopContainerSerializer implements TypeSerializer<ShopContainer> {

    @Override
    public ShopContainer deserialize(TypeToken<?> type, ConfigurationNode value) throws ObjectMappingException {

        Shop shop = value.getNode("shop").getValue(TypeToken.of(Shop.class));
        Region region = null;
        String RegionType = value.getNode("region", "type").getString();
        switch (RegionType) {
            case "rectangular":
                region = value.getNode("region", "data").getValue(TypeToken.of(RectangularRegion.class));
        }

        return new ShopContainer(shop, region);
    }

    @Override
    public void serialize(TypeToken<?> type, ShopContainer obj, ConfigurationNode value) throws ObjectMappingException {
        value.getNode("shop").setValue(TypeToken.of(Shop.class), obj.getShop());

        switch (obj.getShopRegion().getType()) {
            case "rectangular":
                value.getNode("region", "type").setValue("rectangular");
                value.getNode("region", "data").setValue(TypeToken.of(RectangularRegion.class), (RectangularRegion) obj.getShopRegion());
        }
    }
}
