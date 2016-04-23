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

package io.github.zerthick.playershopsrpg.utils.config.serializers.region;

import com.flowpowered.math.vector.Vector3i;
import com.google.common.reflect.TypeToken;
import io.github.zerthick.playershopsrpg.region.CuboidRegion;
import ninja.leaping.configurate.ConfigurationNode;
import ninja.leaping.configurate.objectmapping.ObjectMappingException;
import ninja.leaping.configurate.objectmapping.serialize.TypeSerializer;

public class CuboidRegionSerializer implements TypeSerializer<CuboidRegion> {

    @Override
    public CuboidRegion deserialize(TypeToken<?> type, ConfigurationNode value) throws ObjectMappingException {
        int aX = value.getNode("parA", "x").getInt();
        int aY = value.getNode("parA", "y").getInt();
        int aZ = value.getNode("parA", "z").getInt();

        int bX = value.getNode("parB", "x").getInt();
        int bY = value.getNode("parB", "y").getInt();
        int bZ = value.getNode("parB", "z").getInt();

        return new CuboidRegion(new Vector3i(aX, aY, aZ), new Vector3i(bX, bY, bZ));
    }

    @Override
    public void serialize(TypeToken<?> type, CuboidRegion obj, ConfigurationNode value) throws ObjectMappingException {
        Vector3i parA = obj.getA();
        Vector3i parB = obj.getB();

        value.getNode("parA", "x").setValue(parA.getX());
        value.getNode("parA", "y").setValue(parA.getY());
        value.getNode("parA", "z").setValue(parA.getZ());

        value.getNode("parB", "x").setValue(parB.getX());
        value.getNode("parB", "y").setValue(parB.getY());
        value.getNode("parB", "z").setValue(parB.getZ());
    }
}
