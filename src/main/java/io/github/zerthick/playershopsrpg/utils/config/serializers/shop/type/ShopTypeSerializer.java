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

package io.github.zerthick.playershopsrpg.utils.config.serializers.shop.type;

import com.google.common.reflect.TypeToken;
import io.github.zerthick.playershopsrpg.shop.type.ShopType;
import ninja.leaping.configurate.ConfigurationNode;
import ninja.leaping.configurate.objectmapping.ObjectMappingException;
import ninja.leaping.configurate.objectmapping.serialize.TypeSerializer;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

public class ShopTypeSerializer implements TypeSerializer<ShopType> {

    @Override
    public ShopType deserialize(TypeToken<?> type, ConfigurationNode value) throws ObjectMappingException {

        Set<String> allowedItems = value.getNode("items").getList(TypeToken.of(String.class)).stream().collect(Collectors.toSet());

        return new ShopType(allowedItems);
    }

    @Override
    public void serialize(TypeToken<?> type, ShopType obj, ConfigurationNode value) throws ObjectMappingException {
        value.getNode("items").setValue(new TypeToken<List<String>>() {
                                        },
                obj.getAllowedItems().stream().collect(Collectors.toList()));
    }
}
