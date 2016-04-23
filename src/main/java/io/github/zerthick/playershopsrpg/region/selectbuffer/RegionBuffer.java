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
import io.github.zerthick.playershopsrpg.region.Region;
import org.spongepowered.api.text.Text;

import java.util.Optional;

public interface RegionBuffer {

    void addFront(Vector3i vector);

    void addBack(Vector3i vector);

    void clear();

    Optional<Region> getRegion();

    Text getProgressionMessage();
}
