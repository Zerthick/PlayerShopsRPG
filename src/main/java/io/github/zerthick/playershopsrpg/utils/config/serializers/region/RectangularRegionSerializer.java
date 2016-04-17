package io.github.zerthick.playershopsrpg.utils.config.serializers.region;

import com.flowpowered.math.vector.Vector3i;
import com.google.common.reflect.TypeToken;
import io.github.zerthick.playershopsrpg.region.RectangularRegion;
import ninja.leaping.configurate.ConfigurationNode;
import ninja.leaping.configurate.objectmapping.ObjectMappingException;
import ninja.leaping.configurate.objectmapping.serialize.TypeSerializer;

public class RectangularRegionSerializer implements TypeSerializer<RectangularRegion> {

    @Override
    public RectangularRegion deserialize(TypeToken<?> type, ConfigurationNode value) throws ObjectMappingException {
        int aX = value.getNode("parA", "x").getInt();
        int aY = value.getNode("parA", "y").getInt();
        int aZ = value.getNode("parA", "z").getInt();

        int bX = value.getNode("parB", "x").getInt();
        int bY = value.getNode("parB", "y").getInt();
        int bZ = value.getNode("parB", "z").getInt();

        return new RectangularRegion(new Vector3i(aX, aY, aZ), new Vector3i(bX, bY, bZ));
    }

    @Override
    public void serialize(TypeToken<?> type, RectangularRegion obj, ConfigurationNode value) throws ObjectMappingException {
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
