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

import org.spongepowered.api.data.DataContainer;
import org.spongepowered.api.data.persistence.DataFormats;
import org.spongepowered.api.item.inventory.ItemStack;
import org.spongepowered.api.item.inventory.ItemStackSnapshot;

import java.io.IOException;
import java.io.StringWriter;

public class ItemStackHOCONSerializer {

    public static String serializeSnapShot(ItemStackSnapshot snapshot) throws IOException {

        DataContainer container = snapshot.toContainer();
        StringWriter stringWriter = new StringWriter();

        DataFormats.HOCON.writeTo(stringWriter, container);

        return stringWriter.toString();
    }

    public static ItemStackSnapshot deserializeSnapShot(String serializedSnapshot) throws IOException {

        DataContainer container = DataFormats.HOCON.read(serializedSnapshot);

        return ItemStack.builder().fromContainer(container).build().createSnapshot();
    }
}
