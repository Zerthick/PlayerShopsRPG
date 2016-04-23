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

package io.github.zerthick.playershopsrpg.region.selectbuffer;

import com.flowpowered.math.vector.Vector3i;
import io.github.zerthick.playershopsrpg.region.CuboidRegion;
import io.github.zerthick.playershopsrpg.region.Region;
import org.spongepowered.api.text.Text;
import org.spongepowered.api.text.format.TextColors;

import java.util.Optional;

public class CuboidRegionBuffer implements RegionBuffer {

    final int maxCapacity = 2;

    int currentCapacity;

    Vector3i[] vector3iTuple;

    public CuboidRegionBuffer() {
        vector3iTuple = new Vector3i[2];
    }

    @Override
    public void addFront(Vector3i vector) {
        vector3iTuple[0] = vector;
        currentCapacity = 1 + (vector3iTuple[1] == null ? 0 : 1);
    }

    @Override
    public void addBack(Vector3i vector) {
        vector3iTuple[1] = vector;
        currentCapacity = 1 + (vector3iTuple[0] == null ? 0 : 1);
    }

    @Override
    public void clear() {
        vector3iTuple = new Vector3i[2];
        currentCapacity = 0;
    }

    @Override
    public Optional<Region> getRegion() {
        if (currentCapacity == 2) {
            return Optional.of(new CuboidRegion(vector3iTuple[0], vector3iTuple[1]));
        }
        return Optional.empty();
    }

    @Override
    public Text getProgressionMessage() {
        switch (currentCapacity) {
            case 0:
                return Text.of(TextColors.BLUE, "Left-click on the bottom-left corner of the shop!");
            case 1:
                if (vector3iTuple[0] == null) {
                    return Text.of(TextColors.BLUE, "Left-click on the bottom-left corner of the shop!");
                } else {
                    return Text.of(TextColors.BLUE, "Right-click on the top-right corner of the shop!");
                }
            case 2:
                return Text.of(TextColors.BLUE, "Shop region selected, use /shop create <shop-name> to create the shop!");
        }
        return Text.EMPTY;
    }
}
