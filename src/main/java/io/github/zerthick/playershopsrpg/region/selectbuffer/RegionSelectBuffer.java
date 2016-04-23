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

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

public class RegionSelectBuffer {

    private Map<UUID, RegionBuffer> regionBufferMap;

    public RegionSelectBuffer() {
        regionBufferMap = new HashMap<>();
    }

    public void addBuffer(UUID playerUUID, RegionBuffer buffer) {
        regionBufferMap.put(playerUUID, buffer);
    }

    public Optional<RegionBuffer> getBuffer(UUID playerUUID) {
        if (regionBufferMap.containsKey(playerUUID)) {
            return Optional.of(regionBufferMap.get(playerUUID));
        }
        return Optional.empty();
    }

    public void removeBuffer(UUID playerUUID) {
        if (regionBufferMap.containsKey(playerUUID)) {
            regionBufferMap.remove(playerUUID);
        }
    }

    public boolean hasBuffer(UUID playerUUID) {
        return getBuffer(playerUUID).isPresent();
    }
}
