package io.github.zerthick.playershopsrpg.region.selectbuffer;

import com.flowpowered.math.vector.Vector3i;
import io.github.zerthick.playershopsrpg.region.RectangularRegion;
import io.github.zerthick.playershopsrpg.region.Region;
import org.spongepowered.api.text.Text;
import org.spongepowered.api.text.format.TextColors;

import java.util.Optional;

public class RectangularRegionBuffer implements RegionBuffer {

    final int maxCapacity = 2;

    int currentCapacity;

    Vector3i[] vector3iTuple;

    public RectangularRegionBuffer() {
        vector3iTuple = new Vector3i[2];
    }

    @Override
    public void addFront(Vector3i vector) {
        vector3iTuple[0] = vector;
        if (currentCapacity < maxCapacity) {
            currentCapacity++;
        }
    }

    @Override
    public void addBack(Vector3i vector) {
        vector3iTuple[1] = vector;
        if (currentCapacity < maxCapacity) {
            currentCapacity++;
        }
    }

    @Override
    public void clear() {
        vector3iTuple = new Vector3i[2];
        currentCapacity = 0;
    }

    @Override
    public Optional<Region> getRegion() {
        if (currentCapacity == 2) {
            return Optional.of(new RectangularRegion(vector3iTuple[0], vector3iTuple[1]));
        }
        return Optional.empty();
    }

    @Override
    public Text getProgressionMessage() {
        switch (currentCapacity) {
            case 0:
                return Text.of(TextColors.BLUE, "Left-click on the bottom corner of the shop!");
            case 1:
                if (vector3iTuple[0] == null) {
                    return Text.of(TextColors.BLUE, "Left-click on the bottom-left corner of the shop!");
                } else {
                    return Text.of(TextColors.BLUE, "Right-click on the top-right corner of the shop!");
                }
            case 2:
                return Text.of(TextColors.BLUE, "Shop Region Selected, used /shop create to create the shop!");
        }
        return Text.EMPTY;
    }
}
