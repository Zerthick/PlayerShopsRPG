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
import io.github.zerthick.playershopsrpg.cmd.cmdexecutors.AbstractShopTransactionCmdExecutor;
import io.github.zerthick.playershopsrpg.cmd.cmdexecutors.CommandArgs;
import io.github.zerthick.playershopsrpg.shop.ShopTransactionResult;
import io.github.zerthick.playershopsrpg.utils.messages.Messages;
import org.spongepowered.api.command.CommandException;
import org.spongepowered.api.command.CommandResult;
import org.spongepowered.api.command.CommandSource;
import org.spongepowered.api.command.args.CommandContext;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

public class ShopSetUnlimitedExecutor extends AbstractShopTransactionCmdExecutor {

    public ShopSetUnlimitedExecutor(PlayerShopsRPG plugin) {
        super(plugin);
    }

    public static Map<String, String> selectChoices() {
        Map<String, String> selectChoices = new HashMap<>();
        selectChoices.put("money", "money");
        selectChoices.put("stock", "stock");
        return selectChoices;
    }

    @Override
    public CommandResult execute(CommandSource src, CommandContext args) throws CommandException {

        return super.executeTransaction(src, args, (player, arg, shop) -> {

            Optional<String> selectTypeOptional = arg.getOne(CommandArgs.SELECTION_TYPE);
            Optional<Boolean> booleanArgumentOptional = arg.getOne(CommandArgs.BOOLEAN_ARGUMENT);

            if (selectTypeOptional.isPresent() && booleanArgumentOptional.isPresent()) {
                String selectType = selectTypeOptional.get();
                Boolean booleanArg = booleanArgumentOptional.get();

                ShopTransactionResult transactionResult;

                switch (selectType) {
                    case "money":
                        transactionResult = shop.setUnlimitedMoney(player, booleanArg);
                        break;
                    case "stock":
                        transactionResult = shop.setUnlimitedStock(player, booleanArg);
                        break;
                    default:
                        transactionResult = new ShopTransactionResult(Messages.SET_UNLIMITED_UNKNOWN_SHOP_ATTRIBUTE);
                }

                return transactionResult;
            }
            return ShopTransactionResult.EMPTY;
        }, Messages.SET_UNLIMITED_CONSOLE_REJECT);
    }
}
