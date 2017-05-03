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

package io.github.zerthick.playershopsrpg.utils.config.serializers;

import com.google.common.reflect.TypeToken;
import io.github.zerthick.playershopsrpg.region.CuboidRegion;
import io.github.zerthick.playershopsrpg.region.Region;
import ninja.leaping.configurate.ConfigurationNode;
import ninja.leaping.configurate.hocon.HoconConfigurationLoader;
import ninja.leaping.configurate.objectmapping.ObjectMappingException;

import java.io.*;

/**
 * Created by Chase on 5/2/2017.
 */
public class ShopRegionHOCONSerializer {

    public static String serializeShopRegion(Region shopRegion) throws ObjectMappingException, IOException {
        ConfigurationNode node = HoconConfigurationLoader.builder().build().createEmptyNode();
        StringWriter stringWriter = new StringWriter();

        switch (shopRegion.getType()) {
            case "cuboid":
                node.setValue(TypeToken.of(CuboidRegion.class), (CuboidRegion) shopRegion);
                break;
        }
        HoconConfigurationLoader.builder().setSink(() -> new BufferedWriter(stringWriter)).build().save(node);

        return stringWriter.toString();
    }

    public static Region deserializeShopRegion(String serializedSnapshot, String type) throws ObjectMappingException, IOException {
        ConfigurationNode node = HoconConfigurationLoader.builder().setSource(() -> new BufferedReader(new StringReader(serializedSnapshot))).build().load();
        switch (type) {
            case "cuboid":
                return node.getValue(TypeToken.of(CuboidRegion.class));
        }
        return node.getValue(TypeToken.of(CuboidRegion.class));
    }
}
