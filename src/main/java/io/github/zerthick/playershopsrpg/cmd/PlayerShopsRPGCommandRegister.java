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

package io.github.zerthick.playershopsrpg.cmd;

import io.github.zerthick.playershopsrpg.PlayerShopsRPG;
import io.github.zerthick.playershopsrpg.cmd.cmdexecutors.callback.CallBackExecutor;
import io.github.zerthick.playershopsrpg.cmd.cmdexecutors.shop.*;
import io.github.zerthick.playershopsrpg.cmd.cmdexecutors.shop.item.*;
import io.github.zerthick.playershopsrpg.cmd.cmdexecutors.shop.manager.ShopAddManagerExecutor;
import io.github.zerthick.playershopsrpg.cmd.cmdexecutors.shop.manager.ShopRemoveManagerExecutor;
import io.github.zerthick.playershopsrpg.permissions.Permissions;
import org.spongepowered.api.Sponge;
import org.spongepowered.api.command.args.GenericArguments;
import org.spongepowered.api.command.spec.CommandSpec;
import org.spongepowered.api.plugin.PluginContainer;
import org.spongepowered.api.text.Text;

import java.util.Map;
import java.util.stream.Collectors;

public class PlayerShopsRPGCommandRegister {

    private PluginContainer container;
    private PlayerShopsRPG plugin;

    public PlayerShopsRPGCommandRegister(PluginContainer container) {
        this.container = container;
        plugin = container.getInstance().get() instanceof PlayerShopsRPG ? (PlayerShopsRPG) container.getInstance().get() : null;
    }

    public void registerCmds() {

        // shop buy <name>
        CommandSpec shopBuyCommand = CommandSpec.builder()
                .description(Text.of("But the shop you are currently standing in"))
                .permission(Permissions.PLAYERSHOPSRPG_COMMAND_BUY)
                .arguments(GenericArguments.string(Text.of("NameArgument")))
                .executor(new ShopBuyExecutor(container))
                .build();

        // shop balance withdraw
        CommandSpec shopBalanceWithdrawCommmand = CommandSpec.builder()
                .description(Text.of("Transfer funds from the shop's account to your account"))
                .permission(Permissions.PLAYERSHOPSRPG_COMMAND_BALANCE_WITHDRAW)
                .arguments(GenericArguments.doubleNum(Text.of("DoubleArgument")))
                .executor(new ShopBalanceWithdrawExecutor(container))
                .build();

        // shop balance deposit
        CommandSpec shopBalanceDepositCommmand = CommandSpec.builder()
                .description(Text.of("Transfer funds from your account to the shop's account"))
                .permission(Permissions.PLAYERSHOPSRPG_COMMAND_BALANCE_DEPOSIT)
                .arguments(GenericArguments.doubleNum(Text.of("DoubleArgument")))
                .executor(new ShopBalanceDepositExecutor(container))
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
                .arguments(GenericArguments.integer(Text.of("ItemIndex")), GenericArguments.integer(Text.of("ItemAmount")))
                .executor(new ShopSellItemExecutor(container))
                .build();

        // shop item buy
        CommandSpec shopItemBuyCommand = CommandSpec.builder()
                .description(Text.of("Buy an item from the shop you are currenlty standing in"))
                .permission(Permissions.PLAYERSHOPSRPG_COMMAND_ITEM_BUY)
                .arguments(GenericArguments.integer(Text.of("ItemIndex")), GenericArguments.integer(Text.of("ItemAmount")))
                .executor(new ShopBuyItemExecutor(container))
                .build();

        // shop item remove
        CommandSpec shopItemRemoveCommand = CommandSpec.builder()
                .description(Text.of("Remove an item from the shop you are currenlty standing in"))
                .permission(Permissions.PLAYERSHOPSRPG_COMMAND_ITEM_REMOVE)
                .arguments(GenericArguments.integer(Text.of("ItemIndex")), GenericArguments.integer(Text.of("ItemAmount")))
                .executor(new ShopRemoveItemExecutor(container))
                .build();

        // shop item add
        CommandSpec shopItemAddCommand = CommandSpec.builder()
                .description(Text.of("Add an item to the shop you are currently standing in"))
                .permission(Permissions.PLAYERSHOPSRPG_COMMAND_ITEM_ADD)
                .executor(new ShopAddItemExecutor(container))
                .build();

        // shop item set
        CommandSpec shopItemSetCommand = CommandSpec.builder()
                .description(Text.of("Set various attributes of a shop item (max amount / buy price / sell price"))
                .permission(Permissions.PLAYERSHOPSRPG_COMMAND_ITEM_SET)
                .arguments(GenericArguments.choices(Text.of("SelectionType"), ShopSetItemExecutor.selectChoices()),
                        GenericArguments.integer(Text.of("ItemIndex")), GenericArguments.doubleNum(Text.of("DoubleArgument")))
                .executor(new ShopSetItemExecutor(container))
                .build();

        // shop item destroy
        CommandSpec shopItemDestroyCommand = CommandSpec.builder()
                .description(Text.of("Destroy an item in the shop you are currently standing in"))
                .permission(Permissions.PLAYERSHOPSRPG_COMMAND_ITEM_DESTROY)
                .arguments(GenericArguments.integer(Text.of("ItemIndex")))
                .executor(new ShopDestroyItemExecutor(container))
                .build();

        // shop item create
        CommandSpec shopItemCreateCommand = CommandSpec.builder()
                .description(Text.of("Create an item in the shop you are currently standing in"))
                .permission(Permissions.PLAYERSHOPSRPG_COMMAND_ITEM_CREATE)
                .executor(new ShopCreateItemExecutor(container))
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
                .arguments(GenericArguments.user(Text.of("UserArgument")))
                .executor(new ShopRemoveManagerExecutor(container))
                .build();

        // shop manager add
        CommandSpec shopManagerAddCommand = CommandSpec.builder()
                .description(Text.of("Add a manager to the shop you are currently standing in"))
                .permission(Permissions.PLAYERSHOPSRPG_COMMAND_MANAGER_ADD)
                .arguments(GenericArguments.user(Text.of("UserArgument")))
                .executor(new ShopAddManagerExecutor(container))
                .build();

        // shop manager
        CommandSpec shopManagerCommand = CommandSpec.builder()
                .child(shopManagerAddCommand, "add")
                .child(shopManagerRemoveCommand, "remove")
                .build();

        // shop set price <price>
        CommandSpec shopSetPriceCommmand = CommandSpec.builder()
                .description(Text.of("Set the purchase price of the shop you are currently standing in"))
                .permission(Permissions.PLAYERSHOPSRPG_COMMAND_SET_PRICE)
                .arguments(GenericArguments.doubleNum(Text.of("DoubleArgument")))
                .executor(new ShopSetPriceExecutor(container))
                .build();

        // shop set rent <price>
        CommandSpec shopSetRentCommmand = CommandSpec.builder()
                .description(Text.of("Set the rent price of the shop you are currently standing in"))
                .permission(Permissions.PLAYERSHOPSRPG_COMMAND_SET_RENT)
                .arguments(GenericArguments.doubleNum(Text.of("DoubleArgument")))
                .executor(new ShopSetRentExecutor(container))
                .build();

        // shop set type <type>
        Map<String, String> shopTypeChoices = plugin.getShopTypeManager().getShopTypes()
                .entrySet().stream().collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getKey));
        shopTypeChoices.put("none", "none");
        CommandSpec shopSetTypeCommand = CommandSpec.builder()
                .description(Text.of("Set the type of the shop you are currently standing in"))
                .permission(Permissions.PLAYERSHOPSRPG_COMMAND_SET_TYPE)
                .arguments(GenericArguments.choices(Text.of("TypeArgument"), shopTypeChoices))
                .executor(new ShopSetTypeExecutor(container))
                .build();

        // shop set name <name>
        CommandSpec shopSetNameCommmand = CommandSpec.builder()
                .description(Text.of("Set the name of the shop you are currently standing in"))
                .permission(Permissions.PLAYERSHOPSRPG_COMMAND_SET_NAME)
                .arguments(GenericArguments.remainingJoinedStrings(Text.of("NameArgument")))
                .executor(new ShopSetNameExecutor(container))
                .build();

        // shop set owner <user>
        CommandSpec shopSetOwnerCommand = CommandSpec.builder()
                .description(Text.of("Set the owner of the shop you are currently standing in"))
                .permission(Permissions.PLAYERSHOPSRPG_COMMAND_SET_OWNER)
                .arguments(GenericArguments.user(Text.of("UserArgument")))
                .executor(new ShopSetOwnerExecutor(container))
                .build();

        // shop set unlimited [stock | money] <bool>
        CommandSpec shopSetUnlimitedCommand = CommandSpec.builder()
                .description(Text.of("Set the shop you are currently standing in to have unlimited stock or money"))
                .permission(Permissions.PLAYERSHOPSRPG_COMMAND_SET_UNLIMITED)
                .arguments(GenericArguments.choices(Text.of("SelectionType"), ShopSetUnlimitedExecutor.selectChoices()), GenericArguments.bool(Text.of("BooleanArgument")))
                .executor(new ShopSetUnlimitedExecutor(container))
                .build();

        // shop set
        CommandSpec shopSetCommand = CommandSpec.builder()
                .permission(Permissions.PLAYERSHOPSRPG_COMMAND_SET)
                .child(shopSetUnlimitedCommand, "unlimited")
                .child(shopSetOwnerCommand, "owner")
                .child(shopSetNameCommmand, "name")
                .child(shopSetTypeCommand, "type")
                .child(shopSetPriceCommmand, "price")
                .child(shopSetRentCommmand, "rent")
                .build();

        // shop browse
        CommandSpec shopBrowseCommand = CommandSpec.builder()
                .description(Text.of("Browses the shop you are currently standing in"))
                .permission(Permissions.PLAYERSHOPSRPG_COMMAND_BROWSE)
                .arguments(GenericArguments.optional(GenericArguments.choices(Text.of("SelectionType"), ShopBrowseExecutor.selectChoices())))
                .executor(new ShopBrowseExecutor(container))
                .build();

        // shop destroy
        CommandSpec shopDestroyCommand = CommandSpec.builder()
                .description(Text.of("Destroys the shop you are currently standing in"))
                .permission(Permissions.PLAYERSHOPSRPG_COMMAND_DESTROY)
                .executor(new ShopDestroyExecutor(container))
                .build();

        // shop create <Name>
        CommandSpec shopCreateCommand = CommandSpec.builder()
                .description(Text.of("Creates a shop in the region selected by shop select command"))
                .permission(Permissions.PLAYERSHOPSRPG_COMMAND_CREATE)
                .arguments(GenericArguments.remainingJoinedStrings(Text.of("ShopName")))
                .executor(new ShopCreateExecutor(container))
                .build();

        // shop select
        CommandSpec shopSelectCommmand = CommandSpec.builder()
                .description(Text.of("Selects a region to create a shop"))
                .permission(Permissions.PLAYERSHOPSRPG_COMMAND_SELECT)
                .arguments(GenericArguments.optional(GenericArguments.choices(Text.of("SelectionType"), ShopSelectExecutor.selectChoices())))
                .executor(new ShopSelectExecutor(container))
                .build();

        // shop callBack
        CommandSpec shopCallBackCommand = CommandSpec.builder()
                .arguments(GenericArguments.string(Text.of("message")), GenericArguments.remainingJoinedStrings(Text.of("command")))
                .executor(new CallBackExecutor(container))
                .build();

        // shop
        CommandSpec shopCommand = CommandSpec.builder()
                .executor(new ShopExecutor(container))
                .child(shopSelectCommmand, "select")
                .child(shopCreateCommand, "create")
                .child(shopDestroyCommand, "destroy")
                .child(shopBrowseCommand, "browse")
                .child(shopItemCommand, "item")
                .child(shopSetCommand, "set")
                .child(shopManagerCommand, "manager")
                .child(shopBalanceCommmand, "balance")
                .child(shopBuyCommand, "buy")
                .child(shopCallBackCommand, "callBack", "cb")
                .build();

        Sponge.getGame().getCommandManager().register(container.getInstance().get(), shopCommand, "shop");
    }
}
