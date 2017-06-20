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

package io.github.zerthick.playershopsrpg.region;

import com.flowpowered.math.vector.Vector3i;
import org.spongepowered.api.util.AABB;

import java.util.UUID;

public class CuboidRegion implements Region {

    private final UUID regionUUID;
    private AABB region;

    public CuboidRegion(Vector3i parA, Vector3i parB) {
        this.regionUUID = UUID.randomUUID();
        region = new AABB(parA.toDouble(), parB.toDouble());
    }

    public CuboidRegion(UUID regionUUID, Vector3i parA, Vector3i parB) {
        this.regionUUID = regionUUID;
        region = new AABB(parA.toDouble(), parB.toDouble());
    }

    @Override
    public String getType() {
        return "cuboid";
    }

    @Override
    public UUID getUUID() {
        return this.regionUUID;
    }

    @Override
    public boolean contains(Vector3i location) {
        return region.contains(location);
    }

    public Vector3i getMax() {
        return region.getMax().toInt();
    }

    public Vector3i getMin() {
        return region.getMin().toInt();
    }

    @Override
    public String toString() {
        return getMin().toString() + " " + getMax().toString();
    }
}
