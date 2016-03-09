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
