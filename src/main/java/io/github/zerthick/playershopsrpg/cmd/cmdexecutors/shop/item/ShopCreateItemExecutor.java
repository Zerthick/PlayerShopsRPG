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

package io.github.zerthick.playershopsrpg.cmd.cmdexecutors.shop.item;

import com.google.common.collect.ImmutableMap;
import io.github.zerthick.playershopsrpg.PlayerShopsRPG;
import io.github.zerthick.playershopsrpg.cmd.cmdexecutors.AbstractShopTransactionCmdExecutor;
import io.github.zerthick.playershopsrpg.shop.ShopTransactionResult;
import io.github.zerthick.playershopsrpg.utils.inventory.InventoryUtils;
import io.github.zerthick.playershopsrpg.utils.messages.Messages;
import org.spongepowered.api.command.CommandException;
import org.spongepowered.api.command.CommandResult;
import org.spongepowered.api.command.CommandSource;
import org.spongepowered.api.command.args.CommandContext;
import org.spongepowered.api.item.inventory.ItemStack;
import org.spongepowered.api.text.Text;
import org.spongepowered.api.text.chat.ChatTypes;
import org.spongepowered.api.text.format.TextColors;

public class ShopCreateItemExecutor extends AbstractShopTransactionCmdExecutor {

    public ShopCreateItemExecutor(PlayerShopsRPG plugin) {
        super(plugin);
    }

    @Override
    public CommandResult execute(CommandSource src, CommandContext args) throws CommandException {

        return super.executeTransaction(src, args, (player, arg, shop) -> {

            if (player.getItemInHand().isPresent()) {

                ItemStack item = player.getItemInHand().get();
                ShopTransactionResult transactionResult;

                //Check if this shop is allowed to hold this item
                if (plugin.getShopTypeManager().isItemStackAllowed(item, shop.getType())) {
                    transactionResult = shop.createItem(player, item);

                    if (transactionResult == ShopTransactionResult.SUCCESS) {
                        player.sendMessage(ChatTypes.CHAT, Text.of(TextColors.BLUE, Messages.processDropins(Messages.CREATE_ITEM_SUCCESS,
                                ImmutableMap.of(Messages.DROPIN_ITEM_NAME, InventoryUtils.getItemNamePlain(item),
                                        Messages.DROPIN_SHOP_NAME, shop.getName()))));
                    }
                } else {
                    transactionResult = new ShopTransactionResult(Messages.processDropins(Messages.CREATE_ITEM_TYPE_REJECT,
                            ImmutableMap.of(Messages.DROPIN_SHOP_NAME, shop.getName(), Messages.DROPIN_SHOP_TYPE, shop.getType())));
                }

                return transactionResult;
            }
            return ShopTransactionResult.EMPTY;
        }, Messages.CREATE_ITEM_CONSOLE_REJECT);
    }
}
