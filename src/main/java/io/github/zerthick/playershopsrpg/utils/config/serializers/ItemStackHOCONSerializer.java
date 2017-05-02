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
import ninja.leaping.configurate.ConfigurationNode;
import ninja.leaping.configurate.hocon.HoconConfigurationLoader;
import ninja.leaping.configurate.objectmapping.ObjectMappingException;
import org.spongepowered.api.item.inventory.ItemStackSnapshot;

import java.io.*;

public class ItemStackHOCONSerializer {

    public static String serializeSnapShot(ItemStackSnapshot snapshot) throws ObjectMappingException, IOException {
        ConfigurationNode node = HoconConfigurationLoader.builder().build().createEmptyNode();
        StringWriter stringWriter = new StringWriter();

        node.setValue(TypeToken.of(ItemStackSnapshot.class), snapshot);
        HoconConfigurationLoader.builder().setSink(() -> new BufferedWriter(stringWriter)).build().save(node);

        return stringWriter.toString();
    }

    public static ItemStackSnapshot deserializeSnapShot(String serializedSnapshot) throws ObjectMappingException, IOException {
        ConfigurationNode node = HoconConfigurationLoader.builder().setSource(() -> new BufferedReader(new StringReader(serializedSnapshot))).build().load();
        return node.getValue(TypeToken.of(ItemStackSnapshot.class));
    }
}
