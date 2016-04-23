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

package io.github.zerthick.playershopsrpg.cmd.callback;

import org.spongepowered.api.Sponge;
import org.spongepowered.api.entity.living.player.Player;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class CallBackBuffer {

    private Map<UUID, String> callBackMap;

    public CallBackBuffer() {
        callBackMap = new HashMap<>();
    }

    public boolean hasCallBack(Player player) {
        return callBackMap.containsKey(player.getUniqueId());
    }

    public void addCallBack(Player player, String callback) {
        callBackMap.put(player.getUniqueId(), callback);
    }

    public void executeCallBack(Player player, String callBackValue) {
        if (hasCallBack(player)) {
            Sponge.getGame().getCommandManager().process(player, callBackMap.remove(player.getUniqueId()).replace("%c", callBackValue));
        }
    }
}
