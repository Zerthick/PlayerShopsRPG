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
import io.github.zerthick.playershopsrpg.region.CuboidRegion;
import io.github.zerthick.playershopsrpg.region.Region;
import io.github.zerthick.playershopsrpg.utils.messages.Messages;
import org.spongepowered.api.text.Text;
import org.spongepowered.api.text.format.TextColors;

import java.util.Optional;

public class CuboidRegionBuffer implements RegionBuffer {

    private State state;

    private Vector3i[] vector3iTuple;

    public CuboidRegionBuffer() {
        vector3iTuple = new Vector3i[2];
        state = State.INIT;
    }

    @Override
    public void addFront(Vector3i vector) {
        vector3iTuple[0] = vector;

        switch (state) {
            case INIT:
                state = State.ONE;
                break;
            case ONE:
                if (vector3iTuple[1] != null) {
                    if (isValid(vector3iTuple[1], vector)) {
                        state = State.COMPLETE;
                    } else {
                        state = State.ERROR;
                    }
                }
                break;
            case ERROR:
            case COMPLETE:
                if (isValid(vector3iTuple[1], vector)) {
                    state = State.COMPLETE;
                } else {
                    state = State.ERROR;
                }
                break;
        }
    }

    @Override
    public void addBack(Vector3i vector) {
        vector3iTuple[1] = vector;

        switch (state) {
            case INIT:
                state = State.ONE;
                break;
            case ONE:
                if (vector3iTuple[0] != null) {
                    if (isValid(vector3iTuple[0], vector)) {
                        state = State.COMPLETE;
                    } else {
                        state = State.ERROR;
                    }
                }
                break;
            case ERROR:
            case COMPLETE:
                if (isValid(vector3iTuple[0], vector)) {
                    state = State.COMPLETE;
                } else {
                    state = State.ERROR;
                }
                break;
        }
    }

    @Override
    public void clear() {
        vector3iTuple = new Vector3i[2];
        state = State.INIT;
    }

    @Override
    public Optional<Region> getRegion() {
        if (state == State.COMPLETE) {
            return Optional.of(new CuboidRegion(vector3iTuple[0], vector3iTuple[1]));
        }
        return Optional.empty();
    }

    @Override
    public Text getProgressionMessage() {
        switch (state) {
            case INIT:
                return Text.of(TextColors.BLUE, Messages.CUBOID_SELECT_BOTTOM_LEFT);
            case ONE:
                if (vector3iTuple[0] == null) {
                    return Text.of(TextColors.BLUE, Messages.CUBOID_SELECT_BOTTOM_LEFT);
                } else {
                    return Text.of(TextColors.BLUE, Messages.CUBOID_SELECT_TOP_RIGHT);
                }
            case COMPLETE:
                return Text.of(TextColors.BLUE, Messages.CUBOID_CREATE);
            case ERROR:
                return Text.of(TextColors.RED, Messages.CUBOID_ERROR);
        }
        return Text.EMPTY;
    }

    private boolean isValid(Vector3i a, Vector3i b) {
        return a.getX() != b.getX() &&
                a.getY() != b.getY() &&
                a.getZ() != b.getZ();
    }

    private enum State {
        INIT,
        ONE,
        COMPLETE,
        ERROR
    }
}
