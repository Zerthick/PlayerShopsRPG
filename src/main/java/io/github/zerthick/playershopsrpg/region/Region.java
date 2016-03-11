package io.github.zerthick.playershopsrpg.region;

import com.flowpowered.math.vector.Vector3i;

public interface Region {

    boolean contains(Vector3i location);

}
