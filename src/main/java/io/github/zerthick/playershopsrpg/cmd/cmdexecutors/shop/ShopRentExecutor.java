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

package io.github.zerthick.playershopsrpg.cmd.cmdexecutors.shop;

import com.google.common.collect.ImmutableMap;
import io.github.zerthick.playershopsrpg.PlayerShopsRPG;
import io.github.zerthick.playershopsrpg.cmd.cmdexecutors.AbstractShopTransactionCmdExecutor;
import io.github.zerthick.playershopsrpg.cmd.cmdexecutors.CommandArgs;
import io.github.zerthick.playershopsrpg.shop.ShopTransactionResult;
import io.github.zerthick.playershopsrpg.utils.messages.Messages;
import org.spongepowered.api.command.CommandException;
import org.spongepowered.api.command.CommandResult;
import org.spongepowered.api.command.CommandSource;
import org.spongepowered.api.command.args.CommandContext;
import org.spongepowered.api.text.Text;
import org.spongepowered.api.text.chat.ChatTypes;
import org.spongepowered.api.text.format.TextColors;

import java.math.BigDecimal;
import java.util.Optional;

public class ShopRentExecutor extends AbstractShopTransactionCmdExecutor {

    public ShopRentExecutor(PlayerShopsRPG plugin) {
        super(plugin);
    }

    @Override
    public CommandResult execute(CommandSource src, CommandContext args) throws CommandException {

        return super.executeTransaction(src, args, (player, arg, shop) -> {
            Optional<Double> doubleArgOptional = arg.getOne(CommandArgs.DOUBLE_ARGUMENT);
            if (doubleArgOptional.isPresent()) {
                ShopTransactionResult transactionResult = shop.rentShop(player, BigDecimal.valueOf(doubleArgOptional.get()));

                if (transactionResult == ShopTransactionResult.SUCCESS) {
                    player.sendMessage(ChatTypes.CHAT, Text.of(TextColors.BLUE, Messages.processDropins(Messages.RENT_SHOP_SUCCESS,
                            ImmutableMap.of(Messages.DROPIN_SHOP_NAME, shop.getName()))));
                }

                return transactionResult;
            }
            return ShopTransactionResult.EMPTY;
        }, Messages.RENT_CONSOLE_REJECT);
    }
}
