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


import com.google.common.collect.ImmutableMap;
import io.github.zerthick.playershopsrpg.PlayerShopsRPG;
import io.github.zerthick.playershopsrpg.cmd.cmdexecutors.AbstractCmdExecutor;
import io.github.zerthick.playershopsrpg.shop.ShopContainer;
import io.github.zerthick.playershopsrpg.utils.config.sql.SQLDataUtil;
import io.github.zerthick.playershopsrpg.utils.messages.Messages;
import org.spongepowered.api.command.CommandException;
import org.spongepowered.api.command.CommandResult;
import org.spongepowered.api.command.CommandSource;
import org.spongepowered.api.command.args.CommandContext;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.text.Text;
import org.spongepowered.api.text.chat.ChatTypes;
import org.spongepowered.api.text.format.TextColors;

import java.util.Optional;

public class ShopDestroyExecutor extends AbstractCmdExecutor {

    public ShopDestroyExecutor(PlayerShopsRPG plugin) {
        super(plugin);
    }

    @Override
    public CommandResult execute(CommandSource src, CommandContext args) throws CommandException {

        if (src instanceof Player) {
            Player player = (Player) src;

            Optional<ShopContainer> shopContainerOptional = shopManager.getShop(player);
            if (shopContainerOptional.isPresent()) {
                ShopContainer shopContainer = shopContainerOptional.get();
                if (shopContainer.getShop().isEmpty()) {
                    if (shopContainer.getShop().hasOwnerPermissions(player)) {
                        shopManager.removeShop(player).ifPresent(container -> SQLDataUtil.deleteShop(container.getShop().getUUID(), plugin.getLogger()));
                        player.sendMessage(ChatTypes.CHAT, Text.of(TextColors.BLUE, Messages.processDropins(Messages.DESTROY_SUCCESS,
                                ImmutableMap.of(Messages.DROPIN_SHOP_NAME, shopContainer.getShop().getName()))));
                    } else {
                        player.sendMessage(ChatTypes.CHAT, Text.of(TextColors.RED, Messages.DESTROY_NO_PERMISSION));
                    }
                } else {
                    player.sendMessage(ChatTypes.CHAT, Text.of(TextColors.RED, Messages.processDropins(Messages.DESTROY_CONTAINS_ITEMS,
                            ImmutableMap.of(Messages.DROPIN_SHOP_NAME, shopContainer.getShop().getName()))));
                }
            } else {
                player.sendMessage(ChatTypes.CHAT, Text.of(TextColors.RED, Messages.YOU_ARE_NOT_IN_A_SHOP));
            }
            return CommandResult.success();
        }

        src.sendMessage(Text.of(Messages.DESTROY_CONSOLE_REJECT));
        return CommandResult.success();
    }
}