package io.github.zerthick.playershopsrpg.region.selectbuffer;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

public class RegionSelectBuffer {

    Map<UUID, RegionBuffer> regionBufferMap;

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
