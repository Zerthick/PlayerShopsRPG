package io.github.zerthick.playershopsrpg.region;

import com.flowpowered.math.vector.Vector3i;

public class RectangularRegion implements Region{

    private Vector3i a;
    private Vector3i b;

    public RectangularRegion(Vector3i parA, Vector3i parB) {
        normalize(parA, parB);
    }

    @Override
    public boolean contains(Vector3i location) {
        return contains(location.getX(), location.getY(), location.getZ());
    }

    private void normalize(Vector3i parA, Vector3i parB) {
        int ax, ay, az, bx, by, bz;
        if (parA.getX() < parB.getX()) {
            ax = parA.getX();
            bx = parB.getX();
        } else {
            ax = parB.getX();
            bx = parA.getX();
        }
        if (parA.getY() < parB.getY()) {
            ay = parA.getY();
            by = parB.getY();
        } else {
            ay = parB.getY();
            by = parA.getY();
        }
        if (parA.getZ() < parB.getZ()) {
            az = parA.getZ();
            bz = parB.getZ();
        } else {
            az = parB.getZ();
            bz = parA.getZ();
        }
        this.a = new Vector3i(ax, ay, az);
        this.b = new Vector3i(bx, by, bz);
    }

    private boolean contains(int x, int y, int z) {
        return (x >= this.a.getX() && x <= this.b.getX() &&
                z >= this.a.getZ() && z <= this.b.getZ() &&
                y >= this.a.getY() && y <= this.b.getY());
    }
}
