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

package io.github.zerthick.playershopsrpg.cmd.cmdexecutors.shop.manager;


import io.github.zerthick.playershopsrpg.cmd.cmdexecutors.AbstractShopTransactionCmdExecutor;
import io.github.zerthick.playershopsrpg.cmd.cmdexecutors.CommandArgs;
import io.github.zerthick.playershopsrpg.shop.ShopTransactionResult;
import org.spongepowered.api.command.CommandException;
import org.spongepowered.api.command.CommandResult;
import org.spongepowered.api.command.CommandSource;
import org.spongepowered.api.command.args.CommandContext;
import org.spongepowered.api.entity.living.player.User;
import org.spongepowered.api.plugin.PluginContainer;
import org.spongepowered.api.text.Text;
import org.spongepowered.api.text.chat.ChatTypes;
import org.spongepowered.api.text.format.TextColors;

import java.util.Optional;

public class ShopRemoveManagerExecutor extends AbstractShopTransactionCmdExecutor {

    public ShopRemoveManagerExecutor(PluginContainer pluginContainer) {
        super(pluginContainer);
    }

    @Override
    public CommandResult execute(CommandSource src, CommandContext args) throws CommandException {

        return super.executeTransaction(src, args, (player, arg, shop) -> {
            Optional<User> userArgumentOptional = arg.getOne(CommandArgs.USER_ARGUMENT);
            if (userArgumentOptional.isPresent()) {
                User userArg = userArgumentOptional.get();
                ShopTransactionResult transactionResult = shop.removeManager(player, userArg.getUniqueId());
                if (transactionResult == ShopTransactionResult.SUCCESS) {
                    player.sendMessage(ChatTypes.CHAT, Text.of(TextColors.BLUE, "Successfully removed ",
                            TextColors.AQUA, userArg.getName(), TextColors.BLUE, " as a manager of ", TextColors.AQUA,
                            shop.getName(), TextColors.BLUE, "!"));
                }
            }
            return ShopTransactionResult.EMPTY;
        }, "You cannot remove managers from shops from the console!");
    }
}
