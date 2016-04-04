package io.github.zerthick.playershopsrpg.region;

import com.flowpowered.math.vector.Vector3i;
import ninja.leaping.configurate.objectmapping.serialize.ConfigSerializable;

@ConfigSerializable
public interface Region {

    boolean contains(Vector3i location);

}
