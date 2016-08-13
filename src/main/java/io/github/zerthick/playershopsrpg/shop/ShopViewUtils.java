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

package io.github.zerthick.playershopsrpg.shop;

import io.github.zerthick.playershopsrpg.cmd.callback.CallBackBuffer;
import io.github.zerthick.playershopsrpg.permissions.Permissions;
import io.github.zerthick.playershopsrpg.utils.econ.EconManager;
import io.github.zerthick.playershopsrpg.utils.inventory.InventoryUtils;
import org.spongepowered.api.Sponge;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.service.pagination.PaginationService;
import org.spongepowered.api.text.Text;
import org.spongepowered.api.text.action.TextActions;
import org.spongepowered.api.text.format.TextColors;
import org.spongepowered.api.text.format.TextStyles;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public class ShopViewUtils {

    private static final String INFINITY = "\u221E";
    private static PaginationService pagServ = Sponge.getServiceManager().provide(PaginationService.class).get();

    public static void sendShopBuyView(Player player, Shop shop) {
        //Currency Symbol
        Text curSym = EconManager.getInstance().getDefaultCurrency().getSymbol();

        //Callback buffer
        CallBackBuffer cb = CallBackBuffer.getInstance();

        //First build up the contents of the shop
        List<Text> contents = new ArrayList<>();
        List<ShopItem> items = shop.getItems();

        if (items.isEmpty()) {
            contents.add(Text.of(TextColors.BLUE, "No items to display."));
        }

        for (int i = 0; i < items.size(); i++) {
            ShopItem item = items.get(i);

            Text itemAmount;
            if (shop.isUnlimitedStock()) {
                itemAmount = Text.of(INFINITY);
            } else {
                itemAmount = Text.of(item.getItemAmount() == -1 ? "--" : String.valueOf(item.getItemAmount()));
            }

            //Grab the necessary info we need from the item
            Text itemName = InventoryUtils.getItemName(item.getItemStack());
            Text itemMax = Text.of(item.getItemMaxAmount() == -1 ? INFINITY : String.valueOf(item.getItemMaxAmount()));
            Text itemSell = Text.of(item.getItemBuyPrice() == -1 ? "--" : String.valueOf(item.getItemBuyPrice()));
            Text itemBuy = Text.of(item.getItemSellPrice() == -1 ? "--" : String.valueOf(item.getItemSellPrice()));

            //Add the appropriate actions to the text
            itemName = itemName.toBuilder().onHover(TextActions.showItem(item.getItemStack())).style(TextStyles.UNDERLINE).build();
            Text buy = Text.EMPTY;
            if (player.hasPermission(Permissions.PLAYERSHOPSRPG_COMMAND_ITEM_BUY)) {
                buy = Text.builder("Buy")
                        .onClick(TextActions.executeCallback(cb.getCallBack("How many " + itemName.toPlain() + " would you like to buy?", "shop item buy " + i + " %c " + shop.getUUID())))
                        .style(TextStyles.UNDERLINE).build();
            }
            Text sell = Text.EMPTY;
            if (player.hasPermission(Permissions.PLAYERSHOPSRPG_COMMAND_ITEM_SELL)) {
                sell = Text.builder("Sell")
                        .onClick(TextActions.executeCallback(cb.getCallBack("How many " + itemName.toPlain() + " would you like to sell?", "shop item sell " + i + " %c " + shop.getUUID())))
                        .style(TextStyles.UNDERLINE).build();
            }

            //Build the full line of text
            Text fullLine = Text.of(itemName, " ", itemAmount, "/", itemMax, " | ", curSym, itemBuy, " | ", curSym, itemSell, " ", buy, " ", sell);

            //Add the text to the shop display
            contents.add(fullLine);
            contents.add(Text.of(""));
        }

        //Build header
        Text header = Text.EMPTY;
        if (player.hasPermission(Permissions.PLAYERSHOPSRPG_COMMAND_BUY) && shop.isForSale()) {
            header = header.concat(Text.of(TextColors.AQUA, "FOR SALE: ", curSym, shop.getPrice()));
        }

        if (shop.isUnlimitedMoney()) {
            header = header.concat(Text.of(TextColors.BLUE, "Shop's Balance: ", TextColors.WHITE, curSym, Text.of(INFINITY)));
        } else {
            header = header.concat(Text.of(TextColors.BLUE, "Shop's Balance: ", TextColors.WHITE, curSym, shop.getBalance()));
        }

        if (shop.hasManagerPermissions(player)) {
            Text manager = Text.builder("Manager")
                    .onClick(TextActions.runCommand("/shop browse manager " + shop.getUUID()))
                    .style(TextStyles.UNDERLINE).build();
            header = header.concat(Text.of("  |  ", manager));
        }
        if (shop.hasOwnerPermissions(player)) {
            Text owner = Text.builder("Owner")
                    .onClick(TextActions.runCommand("/shop browse owner " + shop.getUUID()))
                    .style(TextStyles.UNDERLINE).build();
            header = header.concat(Text.of("  |  ", owner));
        }
        header = header.concat(Text.of("\n"));

        pagServ.builder()
                .title(Text.of(shop.getName()))
                .header(header)
                .padding(Text.of(TextColors.BLUE, "-"))
                .contents(contents)
                .sendTo(player);

    }

    public static void sendShopManagerView(Player player, Shop shop) {
        //Currency Symbol
        Text curSym = EconManager.getInstance().getDefaultCurrency().getSymbol();

        //Callback buffer
        CallBackBuffer cb = CallBackBuffer.getInstance();

        //First build up the contents of the shop
        List<Text> contents = new ArrayList<>();
        List<ShopItem> items = shop.getItems();
        if (items.isEmpty()) {
            contents.add(Text.of(TextColors.BLUE, "No items to display."));
        }

        for (int i = 0; i < items.size(); i++) {
            ShopItem item = items.get(i);

            Text itemAmount;
            if (shop.isUnlimitedStock()) {
                itemAmount = Text.of(INFINITY);
            } else {
                itemAmount = Text.of(item.getItemAmount() == -1 ? "--" : String.valueOf(item.getItemAmount()));
            }

            //Grab the necessary info we need from the item and add appropriate actions
            Text itemName = InventoryUtils.getItemName(item.getItemStack());
            Text itemMax = Text.EMPTY, itemSell = Text.EMPTY, itemBuy = Text.EMPTY;
            if (player.hasPermission(Permissions.PLAYERSHOPSRPG_COMMAND_ITEM_SET)) {
                itemMax = Text.builder(item.getItemMaxAmount() == -1 ? INFINITY : String.valueOf(item.getItemMaxAmount()))
                        .onClick(TextActions.executeCallback(cb.getCallBack("Enter " + itemName.toPlain() + " max amount (-1 for infinite):", "shop item set max " + i + " %c " + shop.getUUID())))
                        .style(TextStyles.UNDERLINE).build();
                itemSell = Text.builder(item.getItemBuyPrice() == -1 ? "--" : String.valueOf(item.getItemBuyPrice()))
                        .onClick(TextActions.executeCallback(cb.getCallBack("Enter " + itemName.toPlain() + " buy price (-1 for none):", "shop item set buy " + i + " %c " + shop.getUUID())))
                        .style(TextStyles.UNDERLINE).build();
                itemBuy = Text.builder(item.getItemSellPrice() == -1 ? "--" : String.valueOf(item.getItemSellPrice()))
                        .onClick(TextActions.executeCallback(cb.getCallBack("Enter " + itemName.toPlain() + " sell price (-1 for none):", "shop item set sell " + i + " %c " + shop.getUUID())))
                        .style(TextStyles.UNDERLINE).build();
            }
            itemName = itemName.toBuilder().onHover(TextActions.showItem(item.getItemStack())).style(TextStyles.UNDERLINE).build();

            Text remove = Text.EMPTY;
            if (player.hasPermission(Permissions.PLAYERSHOPSRPG_COMMAND_ITEM_REMOVE)) {
                remove = Text.builder("Remove")
                        .onClick(TextActions.executeCallback(cb.getCallBack("How many " + itemName.toPlain() + " would you like to remove?", " shop item remove " + i + " %c " + shop.getUUID())))
                        .style(TextStyles.UNDERLINE).build();
            }
            //Build the full line of text
            Text fullLine = Text.of(itemName, " ", itemAmount, "/", itemMax, " | ", itemBuy, " | ", itemSell, " ", remove);

            //Add the text to the shop display
            contents.add(fullLine);
            contents.add(Text.of(""));
        }

        //Build header
        Text header = Text.EMPTY;
        if (player.hasPermission(Permissions.PLAYERSHOPSRPG_COMMAND_BUY) && shop.isForSale()) {
            header = header.concat(Text.of(TextColors.AQUA, "FOR SALE: ", curSym, shop.getPrice()));
        }

        if (shop.isUnlimitedMoney()) {
            header = header.concat(Text.of(TextColors.BLUE, "Shop's Balance: ", TextColors.WHITE, curSym, Text.of(INFINITY)));
        } else {
            header = header.concat(Text.of(TextColors.BLUE, "Shop's Balance: ", TextColors.WHITE, curSym, shop.getBalance()));
        }

        Text browse = Text.builder("Browse")
                .onClick(TextActions.runCommand("/shop browse " + shop.getUUID()))
                .style(TextStyles.UNDERLINE).build();
        header = header.concat(Text.of("  |  ", browse));

        if (shop.hasOwnerPermissions(player)) {
            Text owner = Text.builder("Owner")
                    .onClick(TextActions.runCommand("/shop browse owner " + shop.getUUID()))
                    .style(TextStyles.UNDERLINE).build();
            header = header.concat(Text.of("  |  ", owner));
        }
        header = header.concat(Text.of("\n"));

        pagServ.builder()
                .title(Text.of(shop.getName()))
                .header(header)
                .padding(Text.of(TextColors.BLUE, "-"))
                .contents(contents)
                .sendTo(player);
    }

    public static void sendShopOwnerView(Player player, Shop shop) {
        //Currency Symbol
        Text curSym = EconManager.getInstance().getDefaultCurrency().getSymbol();

        //Callback buffer
        CallBackBuffer cb = CallBackBuffer.getInstance();

        //First build up the contents of the shop
        List<Text> contents = new ArrayList<>();

        //Add option to put shop up for sale
        if (player.hasPermission(Permissions.PLAYERSHOPSRPG_COMMAND_SET_PRICE)) {
            Text putUpForSale = Text.builder("Put Up For Sale")
                    .onClick(TextActions.executeCallback(cb.getCallBack("Enter shop sale price (-1 to cancel sale):", "shop set price %c " + shop.getUUID())))
                    .style(TextStyles.UNDERLINE).build();
            contents.add(Text.of(TextColors.BLUE, putUpForSale, "\n"));
        }

        //Display shop type if present
        if (!shop.getType().isEmpty()) {
            contents.add(Text.of(TextColors.BLUE, "Shop Type: ", TextColors.WHITE, shop.getType()));
        }

        //Add option to change shop name
        Text changeShopName = Text.EMPTY;
        if (player.hasPermission(Permissions.PLAYERSHOPSRPG_COMMAND_SET_NAME)) {
            changeShopName = Text.builder("Change")
                    .onClick(TextActions.executeCallback(cb.getCallBack("Enter new shop name:", "shop set name \"%c\" " + shop.getUUID())))
                    .style(TextStyles.UNDERLINE).build();
        }
        contents.add(Text.of(TextColors.BLUE, "Shop Name: ", TextColors.WHITE, shop.getName(), " ", changeShopName));

        //Add option to change shop owner
        Text changeShopOwner = Text.EMPTY;
        if (player.hasPermission(Permissions.PLAYERSHOPSRPG_COMMAND_SET_OWNER)) {
            changeShopOwner = Text.builder("Change")
                    .onClick(TextActions.executeCallback(cb.getCallBack("Enter new shop owner:", "shop set owner %c " + shop.getUUID())))
                    .style(TextStyles.UNDERLINE).build();
        }
        String ownerName;
        Optional<Player> ownerOptional = Sponge.getServer().getPlayer(shop.getOwnerUUID());
        if (ownerOptional.isPresent()) {
            ownerName = ownerOptional.get().getName();
        } else {
            ownerName = "Unknown";
        }
        contents.add(Text.of(TextColors.BLUE, "Shop Owner: ", TextColors.WHITE, ownerName, " ", changeShopOwner));

        //Add option to deposit and withdraw shop funds
        Text deposit = Text.EMPTY;
        if (player.hasPermission(Permissions.PLAYERSHOPSRPG_COMMAND_BALANCE_DEPOSIT)) {
            deposit = Text.builder("Deposit")
                    .onClick(TextActions.executeCallback(cb.getCallBack("How much would you like to deposit?", "shop balance deposit %c " + shop.getUUID())))
                    .style(TextStyles.UNDERLINE).build();
        }

        Text withdraw = Text.EMPTY;
        if (player.hasPermission(Permissions.PLAYERSHOPSRPG_COMMAND_BALANCE_WITHDRAW)) {
            withdraw = Text.builder("Withdraw")
                    .onClick(TextActions.executeCallback(cb.getCallBack("How much would you like to withdraw?", "shop balance withdraw %c " + shop.getUUID())))
                    .style(TextStyles.UNDERLINE).build();
        }

        contents.add(Text.of(TextColors.BLUE, "Shop Balance: ", TextColors.WHITE, shop.getBalance(), " ", deposit, " ", withdraw));

        //Add option to add and remove managers
        contents.add(Text.of(""));

        Text addManager = Text.EMPTY;
        if (player.hasPermission(Permissions.PLAYERSHOPSRPG_COMMAND_MANAGER_ADD)) {
            Text.builder("Add Manager")
                    .onClick(TextActions.executeCallback(cb.getCallBack("Enter manager:", "shop manager add %c " + shop.getUUID())))
                    .style(TextStyles.UNDERLINE).build();
        }
        contents.add(Text.of(TextColors.BLUE, "Managers: ", TextColors.WHITE, addManager));

        for (UUID manager : shop.getManagerUUIDset()) {
            Optional<Player> managerOptional = Sponge.getServer().getPlayer(manager);
            if (managerOptional.isPresent()) {
                String managerName = managerOptional.get().getName();

                Text removeManger = Text.EMPTY;
                if (player.hasPermission(Permissions.PLAYERSHOPSRPG_COMMAND_MANAGER_REMOVE)) {
                    removeManger = Text.builder("Remove")
                            .onClick(TextActions.runCommand("/shop manager remove " + managerName + " " + shop.getUUID()))
                            .style(TextStyles.UNDERLINE).build();
                }

                contents.add(Text.of(managerName, " ", removeManger));
            }
        }

        //Add option to destroy items
        List<ShopItem> items = shop.getItems();
        if (!items.isEmpty()) {
            contents.add(Text.of(""));

            contents.add(Text.of(TextColors.BLUE, "Items:"));

            for (int i = 0; i < items.size(); i++) {
                Text destroyItem = Text.EMPTY;
                if (player.hasPermission(Permissions.PLAYERSHOPSRPG_COMMAND_ITEM_DESTROY)) {
                    destroyItem = Text.builder("Destroy")
                            .onClick(TextActions.runCommand("/shop item destroy " + i))
                            .style(TextStyles.UNDERLINE).build();
                }

                Text itemName = InventoryUtils.getItemName(items.get(i).getItemStack());
                itemName = itemName.toBuilder().onHover(TextActions.showItem(items.get(i).getItemStack())).style(TextStyles.UNDERLINE).build();

                contents.add(Text.of(itemName, " ", destroyItem));
                contents.add(Text.of(""));
            }
        }

        //Build header
        Text header = Text.EMPTY;
        if (player.hasPermission(Permissions.PLAYERSHOPSRPG_COMMAND_BUY) && shop.isForSale()) {
            header = header.concat(Text.of(TextColors.AQUA, "FOR SALE: ", curSym, shop.getPrice()));
        }

        if (shop.isUnlimitedMoney()) {
            header = header.concat(Text.of(TextColors.BLUE, "Shop's Balance: ", TextColors.WHITE, Text.of(INFINITY)));
        } else {
            header = header.concat(Text.of(TextColors.BLUE, "Shop's Balance: ", TextColors.WHITE, shop.getBalance()));
        }

        Text browse = Text.builder("Browse")
                .onClick(TextActions.runCommand("/shop browse " + shop.getUUID()))
                .style(TextStyles.UNDERLINE).build();
        header = header.concat(Text.of("  |  ", browse));

        if (shop.hasOwnerPermissions(player)) {
            Text manager = Text.builder("Manager")
                    .onClick(TextActions.runCommand("/shop browse manager " + shop.getUUID()))
                    .style(TextStyles.UNDERLINE).build();
            header = header.concat(Text.of("  |  ", manager));
        }
        header = header.concat(Text.of("\n"));

        pagServ.builder()
                .title(Text.of(shop.getName()))
                .header(header)
                .padding(Text.of(TextColors.BLUE, "-"))
                .contents(contents)
                .sendTo(player);
    }
}