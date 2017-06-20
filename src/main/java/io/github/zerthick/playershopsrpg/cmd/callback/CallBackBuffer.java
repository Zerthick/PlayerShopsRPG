/*
 * Copyright (C) 2017  Zerthick
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
import org.spongepowered.api.command.CommandSource;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.text.Text;
import org.spongepowered.api.text.format.TextColors;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.function.Consumer;

public class CallBackBuffer {

    private static CallBackBuffer instance = null;

    private Map<UUID, List<String>> callBackMap;

    protected CallBackBuffer() {
        callBackMap = new HashMap<>();
    }

    public static CallBackBuffer getInstance() {
        if (instance == null) {
            instance = new CallBackBuffer();
        }
        return instance;
    }

    public boolean hasCallBack(Player player) {
        return callBackMap.containsKey(player.getUniqueId());
    }

    public void addCallBack(Player player, List<String> callbacks) {
        callBackMap.put(player.getUniqueId(), callbacks);
    }

    public void executeCallBack(Player player, String callBackValue) {
        if (hasCallBack(player)) {
            for (String s : callBackMap.remove(player.getUniqueId())) {
                Sponge.getGame().getCommandManager().process(player, s.replace("%c", callBackValue));
            }
        }
    }

    public Consumer<CommandSource> getCallBack(String msg, List<String> commands) {
        return commandSource -> {
            if (commandSource instanceof Player) {
                Player player = (Player) commandSource;
                player.sendMessage(Text.of(TextColors.BLUE, msg));
                addCallBack(player, commands);
            }
        };
    }
}
