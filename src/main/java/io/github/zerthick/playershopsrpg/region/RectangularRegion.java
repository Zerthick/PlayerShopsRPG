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

package io.github.zerthick.playershopsrpg.region;

import com.flowpowered.math.vector.Vector3i;

public class RectangularRegion implements Region{

    private Vector3i a;
    private Vector3i b;

    public RectangularRegion(Vector3i parA, Vector3i parB) {
        normalize(parA, parB);
    }

    @Override
    public String getType() {
        return "rectangular";
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

    public Vector3i getB() {
        return b;
    }

    public Vector3i getA() {
        return a;
    }

    @Override
    public String toString() {
        return getA().toString() + " " + getB().toString();
    }
}
