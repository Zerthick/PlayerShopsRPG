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

package io.github.zerthick.playershopsrpg.cmd.cmdexecutors.shop.item;

import io.github.zerthick.playershopsrpg.cmd.cmdexecutors.AbstractCmdExecutor;
import io.github.zerthick.playershopsrpg.shop.Shop;
import io.github.zerthick.playershopsrpg.shop.ShopContainer;
import io.github.zerthick.playershopsrpg.shop.ShopTransactionResult;
import io.github.zerthick.playershopsrpg.utils.inventory.InventoryUtils;
import org.spongepowered.api.command.CommandException;
import org.spongepowered.api.command.CommandResult;
import org.spongepowered.api.command.CommandSource;
import org.spongepowered.api.command.args.CommandContext;
import org.spongepowered.api.data.type.HandTypes;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.item.inventory.ItemStack;
import org.spongepowered.api.plugin.PluginContainer;
import org.spongepowered.api.text.Text;
import org.spongepowered.api.text.chat.ChatTypes;
import org.spongepowered.api.text.format.TextColors;

import java.util.Optional;

public class ShopCreateItemExecutor extends AbstractCmdExecutor {

    public ShopCreateItemExecutor(PluginContainer pluginContainer) {
        super(pluginContainer);
    }

    @Override
    public CommandResult execute(CommandSource src, CommandContext args) throws CommandException {

        if (src instanceof Player) {
            Player player = (Player) src;

            Optional<ShopContainer> shopContainerOptional = shopManager.getShop(player);
            if (shopContainerOptional.isPresent()) {
                ShopContainer shopContainer = shopContainerOptional.get();
                Shop shop = shopContainer.getShop();
                ShopTransactionResult transactionResult;
                if (player.getItemInHand(HandTypes.MAIN_HAND).isPresent()) {
                    ItemStack item = player.getItemInHand(HandTypes.MAIN_HAND).get();
                    //Check if this shop is allowed to hold this item
                    if (plugin.getShopTypeManager().isItemStackAllowed(item, shop.getType())) {
                        transactionResult = shop.createItem(player, item);

                        if (transactionResult != ShopTransactionResult.SUCCESS) {
                            player.sendMessage(ChatTypes.CHAT, Text.of(TextColors.RED, transactionResult.getMessage()));
                        } else {
                            player.sendMessage(ChatTypes.CHAT, Text.of(TextColors.BLUE, "Successfully created ",
                                    TextColors.AQUA, InventoryUtils.getItemName(item), TextColors.BLUE, " in ", TextColors.AQUA, shop.getName()));
                        }
                    } else {
                        player.sendMessage(ChatTypes.CHAT, Text.of(TextColors.RED, shop.getType(),
                                "s are not allowed to buy and sell ", InventoryUtils.getItemName(item), "!"));
                    }
                }
            } else {
                player.sendMessage(ChatTypes.CHAT, Text.of(TextColors.RED, "You are not in a shop!"));
            }
            return CommandResult.success();
        }

        src.sendMessage(Text.of("You cannot create items in shops from the console!"));
        return CommandResult.success();
    }
}
