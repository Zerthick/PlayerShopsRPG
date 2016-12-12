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

package io.github.zerthick.playershopsrpg.cmd.cmdexecutors.shop;

import io.github.zerthick.playershopsrpg.PlayerShopsRPG;
import io.github.zerthick.playershopsrpg.cmd.cmdexecutors.AbstractCmdExecutor;
import io.github.zerthick.playershopsrpg.cmd.cmdexecutors.CommandArgs;
import io.github.zerthick.playershopsrpg.region.selectbuffer.CuboidRegionBuffer;
import io.github.zerthick.playershopsrpg.utils.messages.Messages;
import org.spongepowered.api.command.CommandException;
import org.spongepowered.api.command.CommandResult;
import org.spongepowered.api.command.CommandSource;
import org.spongepowered.api.command.args.CommandContext;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.text.Text;
import org.spongepowered.api.text.chat.ChatTypes;
import org.spongepowered.api.text.format.TextColors;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

public class ShopSelectExecutor extends AbstractCmdExecutor {

    public ShopSelectExecutor(PlayerShopsRPG plugin) {
        super(plugin);
    }

    public static Map<String, String> selectChoices() {
        Map<String, String> selectChoices = new HashMap<>();
        selectChoices.put("cuboid", "cuboid");
        selectChoices.put("clear", "clear");
        return selectChoices;
    }

    @Override
    public CommandResult execute(CommandSource src, CommandContext args) throws CommandException {

        if (src instanceof Player) {

            Player player = (Player) src;

            String selectType = "cuboid";

            Optional<String> selectTypeOptional = args.getOne(Text.of(CommandArgs.SELECTION_TYPE));

            if (selectTypeOptional.isPresent()) {
                selectType = selectTypeOptional.get();
            }

            switch (selectType) {
                case "cuboid":
                    CuboidRegionBuffer regionBuffer = new CuboidRegionBuffer();
                    plugin.getRegionSelectBuffer().addBuffer(player.getUniqueId(), regionBuffer);
                    player.sendMessage(ChatTypes.CHAT, regionBuffer.getProgressionMessage());
                    break;
                case "clear":
                    plugin.getRegionSelectBuffer().removeBuffer(player.getUniqueId());
                    player.sendMessage(ChatTypes.CHAT, Text.of(TextColors.BLUE, Messages.SELECT_CLEAR));
                    break;
                default:
                    player.sendMessage(ChatTypes.CHAT, Text.of(TextColors.RED, Messages.SELECT_UNKNOWN_TYPE));
            }
            return CommandResult.success();
        }

        src.sendMessage(Text.of(Messages.SELECT_CONSOLE_REJECT));
        return CommandResult.success();
    }
}
