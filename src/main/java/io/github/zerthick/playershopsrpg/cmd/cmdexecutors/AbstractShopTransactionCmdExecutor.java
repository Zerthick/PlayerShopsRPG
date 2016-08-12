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

package io.github.zerthick.playershopsrpg.cmd.cmdexecutors;

import io.github.zerthick.playershopsrpg.shop.Shop;
import io.github.zerthick.playershopsrpg.shop.ShopContainer;
import io.github.zerthick.playershopsrpg.shop.ShopTransactionResult;
import org.spongepowered.api.command.CommandException;
import org.spongepowered.api.command.CommandResult;
import org.spongepowered.api.command.CommandSource;
import org.spongepowered.api.command.args.CommandContext;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.plugin.PluginContainer;
import org.spongepowered.api.text.Text;
import org.spongepowered.api.text.chat.ChatTypes;
import org.spongepowered.api.text.format.TextColors;

import java.util.Optional;
import java.util.UUID;

public abstract class AbstractShopTransactionCmdExecutor extends AbstractCmdExecutor {

    public AbstractShopTransactionCmdExecutor(PluginContainer pluginContainer) {
        super(pluginContainer);
    }

    public CommandResult executeTransaction(CommandSource src, CommandContext args, ShopTransactionCommandProcessor processor, String consoleReject) throws CommandException {

        if (src instanceof Player) {
            Player player = (Player) src;

            Optional<String> shopUUIDOptional = args.getOne(Text.of(CommandArgs.SHOP_UUID));

            Optional<ShopContainer> shopContainerOptional = shopUUIDOptional.isPresent() ? shopManager.getShopByUUID(UUID.fromString(shopUUIDOptional.get()), player) : shopManager.getShop(player);
            if (shopContainerOptional.isPresent()) {

                ShopContainer shopContainer = shopContainerOptional.get();
                Shop shop = shopContainer.getShop();

                ShopTransactionResult transactionResult = processor.processCommand(player, args, shop);

                if (transactionResult != ShopTransactionResult.SUCCESS) {
                    player.sendMessage(ChatTypes.CHAT, Text.of(TextColors.RED, transactionResult.getMessage()));
                }
            } else {
                player.sendMessage(ChatTypes.CHAT, Text.of(TextColors.RED, "You are not in a shop!"));
            }
            return CommandResult.success();
        }
        src.sendMessage(Text.of(consoleReject));
        return CommandResult.success();
    }

    protected interface ShopTransactionCommandProcessor {

        ShopTransactionResult processCommand(Player player, CommandContext args, Shop shop);
    }
}
