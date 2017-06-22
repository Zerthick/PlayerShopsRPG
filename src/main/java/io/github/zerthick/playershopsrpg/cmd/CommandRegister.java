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

package io.github.zerthick.playershopsrpg.cmd;

import io.github.zerthick.playershopsrpg.PlayerShopsRPG;
import io.github.zerthick.playershopsrpg.cmd.cmdexecutors.CommandArgs;
import io.github.zerthick.playershopsrpg.cmd.cmdexecutors.ShopSetCurrencyDefaultExecutor;
import io.github.zerthick.playershopsrpg.cmd.cmdexecutors.shop.*;
import io.github.zerthick.playershopsrpg.cmd.cmdexecutors.shop.item.*;
import io.github.zerthick.playershopsrpg.cmd.cmdexecutors.shop.manager.ShopAddManagerExecutor;
import io.github.zerthick.playershopsrpg.cmd.cmdexecutors.shop.manager.ShopRemoveManagerExecutor;
import io.github.zerthick.playershopsrpg.permissions.Permissions;
import io.github.zerthick.playershopsrpg.utils.econ.EconManager;
import org.spongepowered.api.Sponge;
import org.spongepowered.api.command.args.GenericArguments;
import org.spongepowered.api.command.spec.CommandSpec;
import org.spongepowered.api.service.economy.Currency;
import org.spongepowered.api.text.Text;

import java.util.Map;
import java.util.stream.Collectors;

public class CommandRegister {

    public static void registerCommands(PlayerShopsRPG plugin) {

        // shop rent
        CommandSpec shopRentCommand = CommandSpec.builder()
                .description(Text.of("Rent the shop you are currently standing in"))
                .permission(Permissions.PLAYERSHOPSRPG_COMMAND_RENT)
                .arguments(GenericArguments.doubleNum(CommandArgs.DOUBLE_ARGUMENT), GenericArguments.optional(GenericArguments.string(CommandArgs.SHOP_UUID)))
                .executor(new ShopRentExecutor(plugin))
                .build();

        // shop buy
        CommandSpec shopBuyCommand = CommandSpec.builder()
                .description(Text.of("Buy the shop you are currently standing in"))
                .permission(Permissions.PLAYERSHOPSRPG_COMMAND_BUY)
                .arguments(GenericArguments.optional(GenericArguments.string(CommandArgs.SHOP_UUID)))
                .executor(new ShopBuyExecutor(plugin))
                .build();

        // shop balance withdraw
        CommandSpec shopBalanceWithdrawCommmand = CommandSpec.builder()
                .description(Text.of("Transfer funds from the shop's account to your account"))
                .permission(Permissions.PLAYERSHOPSRPG_COMMAND_BALANCE_WITHDRAW)
                .arguments(GenericArguments.doubleNum(CommandArgs.DOUBLE_ARGUMENT), GenericArguments.optional(GenericArguments.string(CommandArgs.SHOP_UUID)))
                .executor(new ShopBalanceWithdrawExecutor(plugin))
                .build();

        // shop balance deposit
        CommandSpec shopBalanceDepositCommmand = CommandSpec.builder()
                .description(Text.of("Transfer funds from your account to the shop's account"))
                .permission(Permissions.PLAYERSHOPSRPG_COMMAND_BALANCE_DEPOSIT)
                .arguments(GenericArguments.doubleNum(CommandArgs.DOUBLE_ARGUMENT), GenericArguments.optional(GenericArguments.string(CommandArgs.SHOP_UUID)))
                .executor(new ShopBalanceDepositExecutor(plugin))
                .build();

        // shop balance
        CommandSpec shopBalanceCommmand = CommandSpec.builder()
                .child(shopBalanceDepositCommmand, "deposit")
                .child(shopBalanceWithdrawCommmand, "withdraw")
                .build();

        // shop item sell
        CommandSpec shopItemSellCommand = CommandSpec.builder()
                .description(Text.of("Sell an item to the shop you are currenlty standing in"))
                .permission(Permissions.PLAYERSHOPSRPG_COMMAND_ITEM_SELL)
                .arguments(GenericArguments.string(CommandArgs.ITEM_INDEX), GenericArguments.integer(CommandArgs.ITEM_AMOUNT), GenericArguments.optional(GenericArguments.string(CommandArgs.SHOP_UUID)))
                .executor(new ShopSellItemExecutor(plugin))
                .build();

        // shop item buy
        CommandSpec shopItemBuyCommand = CommandSpec.builder()
                .description(Text.of("Buy an item from the shop you are currenlty standing in"))
                .permission(Permissions.PLAYERSHOPSRPG_COMMAND_ITEM_BUY)
                .arguments(GenericArguments.string(CommandArgs.ITEM_INDEX), GenericArguments.integer(CommandArgs.ITEM_AMOUNT), GenericArguments.optional(GenericArguments.string(CommandArgs.SHOP_UUID)))
                .executor(new ShopBuyItemExecutor(plugin))
                .build();

        // shop item remove
        CommandSpec shopItemRemoveCommand = CommandSpec.builder()
                .description(Text.of("Remove an item from the shop you are currenlty standing in"))
                .permission(Permissions.PLAYERSHOPSRPG_COMMAND_ITEM_REMOVE)
                .arguments(GenericArguments.string(CommandArgs.ITEM_INDEX), GenericArguments.integer(CommandArgs.ITEM_AMOUNT), GenericArguments.optional(GenericArguments.string(CommandArgs.SHOP_UUID)))
                .executor(new ShopRemoveItemExecutor(plugin))
                .build();

        // shop item add
        CommandSpec shopItemAddCommand = CommandSpec.builder()
                .description(Text.of("Add an item to the shop you are currently standing in"))
                .permission(Permissions.PLAYERSHOPSRPG_COMMAND_ITEM_ADD)
                .arguments(GenericArguments.optional(GenericArguments.string(CommandArgs.SHOP_UUID)))
                .executor(new ShopAddItemExecutor(plugin))
                .build();

        // shop item set
        CommandSpec shopItemSetCommand = CommandSpec.builder()
                .description(Text.of("Set various attributes of a shop item (max amount / buy price / sell price"))
                .permission(Permissions.PLAYERSHOPSRPG_COMMAND_ITEM_SET)
                .arguments(GenericArguments.choices(CommandArgs.SELECTION_TYPE, ShopSetItemExecutor.selectChoices()),
                        GenericArguments.string(CommandArgs.ITEM_INDEX), GenericArguments.doubleNum(CommandArgs.DOUBLE_ARGUMENT), GenericArguments.optional(GenericArguments.string(CommandArgs.SHOP_UUID)))
                .executor(new ShopSetItemExecutor(plugin))
                .build();

        // shop item destroy
        CommandSpec shopItemDestroyCommand = CommandSpec.builder()
                .description(Text.of("Destroy an item in the shop you are currently standing in"))
                .permission(Permissions.PLAYERSHOPSRPG_COMMAND_ITEM_DESTROY)
                .arguments(GenericArguments.string(CommandArgs.ITEM_INDEX), GenericArguments.optional(GenericArguments.string(CommandArgs.SHOP_UUID)))
                .executor(new ShopDestroyItemExecutor(plugin))
                .build();

        // shop item create
        CommandSpec shopItemCreateCommand = CommandSpec.builder()
                .description(Text.of("Create an item in the shop you are currently standing in"))
                .permission(Permissions.PLAYERSHOPSRPG_COMMAND_ITEM_CREATE)
                .arguments(GenericArguments.optional(GenericArguments.string(CommandArgs.SHOP_UUID)))
                .executor(new ShopCreateItemExecutor(plugin))
                .build();

        // shop item
        CommandSpec shopItemCommand = CommandSpec.builder()
                .child(shopItemCreateCommand, "create")
                .child(shopItemDestroyCommand, "destroy")
                .child(shopItemSetCommand, "set")
                .child(shopItemAddCommand, "add")
                .child(shopItemRemoveCommand, "remove")
                .child(shopItemBuyCommand, "buy")
                .child(shopItemSellCommand, "sell")
                .build();

        // shop manager remove
        CommandSpec shopManagerRemoveCommand = CommandSpec.builder()
                .description(Text.of("Add a manager to the shop you are currently standing in"))
                .permission(Permissions.PLAYERSHOPSRPG_COMMAND_MANAGER_REMOVE)
                .arguments(GenericArguments.user(CommandArgs.USER_ARGUMENT), GenericArguments.optional(GenericArguments.string(CommandArgs.SHOP_UUID)))
                .executor(new ShopRemoveManagerExecutor(plugin))
                .build();

        // shop manager add
        CommandSpec shopManagerAddCommand = CommandSpec.builder()
                .description(Text.of("Add a manager to the shop you are currently standing in"))
                .permission(Permissions.PLAYERSHOPSRPG_COMMAND_MANAGER_ADD)
                .arguments(GenericArguments.user(CommandArgs.USER_ARGUMENT), GenericArguments.optional(GenericArguments.string(CommandArgs.SHOP_UUID)))
                .executor(new ShopAddManagerExecutor(plugin))
                .build();

        // shop manager
        CommandSpec shopManagerCommand = CommandSpec.builder()
                .child(shopManagerAddCommand, "add")
                .child(shopManagerRemoveCommand, "remove")
                .build();

        // shop set currency default
        CommandSpec shopSetCurrencyDefaultCommand = CommandSpec.builder()
                .description(Text.of("Set the currency of the shop you are currently standing in to the default currency"))
                .permission(Permissions.PLAYERSHOPSRPG_COMMAND_SET_CURRENCY)
                .arguments(GenericArguments.optional(GenericArguments.string(CommandArgs.SHOP_UUID)))
                .executor(new ShopSetCurrencyDefaultExecutor(plugin))
                .build();

        // shop set currency <currency>
        CommandSpec shopSetCurrencyCommand = CommandSpec.builder()
                .description(Text.of("Set the currency of the shop you are currently standing in"))
                .permission(Permissions.PLAYERSHOPSRPG_COMMAND_SET_CURRENCY)
                .arguments(GenericArguments.choices(CommandArgs.CURRENCY,
                        EconManager.getInstance().getCurrencies().stream().collect(Collectors.toMap(Currency::getName, c -> c))),
                        GenericArguments.optional(GenericArguments.string(CommandArgs.SHOP_UUID)))
                .executor(new ShopSetCurrencyExecutor(plugin))
                .child(shopSetCurrencyDefaultCommand, "default")
                .build();

        // shop set price <price>
        CommandSpec shopSetPriceCommand = CommandSpec.builder()
                .description(Text.of("Set the purchase price of the shop you are currently standing in"))
                .permission(Permissions.PLAYERSHOPSRPG_COMMAND_SET_PRICE)
                .arguments(GenericArguments.doubleNum(CommandArgs.DOUBLE_ARGUMENT), GenericArguments.optional(GenericArguments.string(CommandArgs.SHOP_UUID)))
                .executor(new ShopSetPriceExecutor(plugin))
                .build();

        // shop set rent <price>
        CommandSpec shopSetRentCommand = CommandSpec.builder()
                .description(Text.of("Set the rent price of the shop you are currently standing in"))
                .permission(Permissions.PLAYERSHOPSRPG_COMMAND_SET_RENT)
                .arguments(GenericArguments.doubleNum(CommandArgs.DOUBLE_ARGUMENT), GenericArguments.optional(GenericArguments.string(CommandArgs.SHOP_UUID)))
                .executor(new ShopSetRentExecutor(plugin))
                .build();

        // shop set type <type>
        Map<String, String> shopTypeChoices = plugin.getShopTypeManager().getShopTypes()
                .entrySet().stream().collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getKey));
        shopTypeChoices.put("none", "none");
        CommandSpec shopSetTypeCommand = CommandSpec.builder()
                .description(Text.of("Set the type of the shop you are currently standing in"))
                .permission(Permissions.PLAYERSHOPSRPG_COMMAND_SET_TYPE)
                .arguments(GenericArguments.choices(CommandArgs.TYPE_ARGUMENT, shopTypeChoices), GenericArguments.optional(GenericArguments.string(CommandArgs.SHOP_UUID)))
                .executor(new ShopSetTypeExecutor(plugin))
                .build();

        // shop set name <name>
        CommandSpec shopSetNameCommmand = CommandSpec.builder()
                .description(Text.of("Set the name of the shop you are currently standing in"))
                .permission(Permissions.PLAYERSHOPSRPG_COMMAND_SET_NAME)
                .arguments(GenericArguments.string(CommandArgs.SHOP_NAME), GenericArguments.optional(GenericArguments.string(CommandArgs.SHOP_UUID)))
                .executor(new ShopSetNameExecutor(plugin))
                .build();

        // shop set owner none
        CommandSpec shopSetOwnerNoneCommand = CommandSpec.builder()
                .description(Text.of("Set the owner of the shop to no one"))
                .permission(Permissions.PLAYERSHOPSRPG_COMMAND_SET_OWNER_NONE)
                .arguments(GenericArguments.optional(GenericArguments.string(CommandArgs.SHOP_UUID)))
                .executor(new ShopSetOwnerNoneExecutor(plugin))
                .build();

        // shop set owner <user>
        CommandSpec shopSetOwnerCommand = CommandSpec.builder()
                .description(Text.of("Set the owner of the shop you are currently standing in"))
                .permission(Permissions.PLAYERSHOPSRPG_COMMAND_SET_OWNER)
                .arguments(GenericArguments.user(CommandArgs.USER_ARGUMENT), GenericArguments.optional(GenericArguments.string(CommandArgs.SHOP_UUID)))
                .executor(new ShopSetOwnerExecutor(plugin))
                .child(shopSetOwnerNoneCommand, "none")
                .build();

        // shop set unlimited [stock | money] <bool>
        CommandSpec shopSetUnlimitedCommand = CommandSpec.builder()
                .description(Text.of("Set the shop you are currently standing in to have unlimited stock or money"))
                .permission(Permissions.PLAYERSHOPSRPG_COMMAND_SET_UNLIMITED)
                .arguments(GenericArguments.choices(CommandArgs.SELECTION_TYPE, ShopSetUnlimitedExecutor.selectChoices()), GenericArguments.bool(CommandArgs.BOOLEAN_ARGUMENT), GenericArguments.optional(GenericArguments.string(CommandArgs.SHOP_UUID)))
                .executor(new ShopSetUnlimitedExecutor(plugin))
                .build();

        // shop set
        CommandSpec shopSetCommand = CommandSpec.builder()
                .child(shopSetUnlimitedCommand, "unlimited")
                .child(shopSetOwnerCommand, "owner")
                .child(shopSetNameCommmand, "name")
                .child(shopSetTypeCommand, "type")
                .child(shopSetPriceCommand, "price")
                .child(shopSetRentCommand, "rent")
                .child(shopSetCurrencyCommand, "currency")
                .build();

        // shop browse manager
        CommandSpec shopBrowseManagerCommand = CommandSpec.builder()
                .arguments(GenericArguments.optional(GenericArguments.string(CommandArgs.SHOP_UUID)))
                .executor(new ShopBrowseManagerExecutor(plugin))
                .build();

        // shop browse owner
        CommandSpec shopBrowseOwnerCommand = CommandSpec.builder()
                .arguments(GenericArguments.optional(GenericArguments.string(CommandArgs.SHOP_UUID)))
                .executor(new ShopBrowseOwnerExecutor(plugin))
                .build();

        // shop browse
        CommandSpec shopBrowseCommand = CommandSpec.builder()
                .description(Text.of("Browses the shop you are currently standing in"))
                .permission(Permissions.PLAYERSHOPSRPG_COMMAND_BROWSE)
                .arguments(GenericArguments.optional(GenericArguments.string(CommandArgs.SHOP_UUID)))
                .executor(new ShopBrowseExecutor(plugin))
                .child(shopBrowseManagerCommand, "manager")
                .child(shopBrowseOwnerCommand, "owner")
                .build();

        // shop destroy force
        CommandSpec shopDestroyForceCommand = CommandSpec.builder()
                .description(Text.of("Destroys the shop you are currently standing in, regardless of if it contains items."))
                .permission(Permissions.PLAYERSHOPSRPG_COMMAND_DESTROY)
                .arguments(GenericArguments.optional(GenericArguments.string(CommandArgs.SHOP_UUID)))
                .executor(new ShopDestroyForceExecutor(plugin))
                .build();

        // shop destroy
        CommandSpec shopDestroyCommand = CommandSpec.builder()
                .description(Text.of("Destroys the shop you are currently standing in if it doesn't contain items."))
                .permission(Permissions.PLAYERSHOPSRPG_COMMAND_DESTROY)
                .arguments(GenericArguments.optional(GenericArguments.string(CommandArgs.SHOP_UUID)))
                .executor(new ShopDestroyExecutor(plugin))
                .child(shopDestroyForceCommand, "force")
                .build();

        // shop create <Name>
        CommandSpec shopCreateCommand = CommandSpec.builder()
                .description(Text.of("Creates a shop in the region selected by shop select command"))
                .permission(Permissions.PLAYERSHOPSRPG_COMMAND_CREATE)
                .arguments(GenericArguments.remainingJoinedStrings(CommandArgs.SHOP_NAME))
                .executor(new ShopCreateExecutor(plugin))
                .build();

        // shop select
        CommandSpec shopSelectCommmand = CommandSpec.builder()
                .description(Text.of("Selects a region to create a shop"))
                .permission(Permissions.PLAYERSHOPSRPG_COMMAND_SELECT)
                .arguments(GenericArguments.optional(GenericArguments.choices(CommandArgs.SELECTION_TYPE, ShopSelectExecutor.selectChoices())))
                .executor(new ShopSelectExecutor(plugin))
                .build();

        // shop
        CommandSpec shopCommand = CommandSpec.builder()
                .executor(new ShopExecutor(plugin))
                .child(shopSelectCommmand, "select")
                .child(shopCreateCommand, "create")
                .child(shopDestroyCommand, "destroy")
                .child(shopBrowseCommand, "browse")
                .child(shopItemCommand, "item")
                .child(shopSetCommand, "set")
                .child(shopManagerCommand, "manager")
                .child(shopBalanceCommmand, "balance")
                .child(shopBuyCommand, "buy")
                .child(shopRentCommand, "rent")
                .build();

        Sponge.getGame().getCommandManager().register(plugin, shopCommand, "shop");
    }
}
