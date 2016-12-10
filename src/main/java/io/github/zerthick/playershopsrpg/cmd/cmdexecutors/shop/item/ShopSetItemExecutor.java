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

import io.github.zerthick.playershopsrpg.PlayerShopsRPG;
import io.github.zerthick.playershopsrpg.cmd.cmdexecutors.AbstractShopTransactionCmdExecutor;
import io.github.zerthick.playershopsrpg.cmd.cmdexecutors.CommandArgs;
import io.github.zerthick.playershopsrpg.shop.ShopTransactionResult;
import org.spongepowered.api.command.CommandException;
import org.spongepowered.api.command.CommandResult;
import org.spongepowered.api.command.CommandSource;
import org.spongepowered.api.command.args.CommandContext;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

public class ShopSetItemExecutor extends AbstractShopTransactionCmdExecutor {

    public ShopSetItemExecutor(PlayerShopsRPG plugin) {
        super(plugin);
    }

    public static Map<String, String> selectChoices() {
        Map<String, String> selectChoices = new HashMap<>();
        selectChoices.put("max", "max");
        selectChoices.put("buy", "buy");
        selectChoices.put("sell", "sell");
        return selectChoices;
    }

    @Override
    public CommandResult execute(CommandSource src, CommandContext args) throws CommandException {

        return super.executeTransaction(src, args, (player, arg, shop) -> {

            Optional<String> selectTypeOptional = arg.getOne(CommandArgs.SELECTION_TYPE);
            Optional<Integer> itemIndexArgumentOptional = arg.getOne(CommandArgs.ITEM_INDEX);
            Optional<Double> doubleArgumentOptional = arg.getOne(CommandArgs.DOUBLE_ARGUMENT);

            if (selectTypeOptional.isPresent() && itemIndexArgumentOptional.isPresent() && doubleArgumentOptional.isPresent()) {
                String selectType = selectTypeOptional.get();
                int itemIndex = itemIndexArgumentOptional.get();
                double doubleArg = doubleArgumentOptional.get();

                ShopTransactionResult transactionResult;

                switch (selectType) {
                    case "max":
                        transactionResult = shop.setItemMax(player, itemIndex, (int) doubleArg);
                        break;
                    case "buy":
                        transactionResult = shop.setItemBuyPrice(player, itemIndex, doubleArg);
                        break;
                    case "sell":
                        transactionResult = shop.setItemSellPrice(player, itemIndex, doubleArg);
                        break;
                    default:
                        transactionResult = new ShopTransactionResult("Unknown item attribute!");
                }

                return transactionResult;
            }
            return ShopTransactionResult.EMPTY;
        }, "You cannot set item attributes from the console!");
    }
}
