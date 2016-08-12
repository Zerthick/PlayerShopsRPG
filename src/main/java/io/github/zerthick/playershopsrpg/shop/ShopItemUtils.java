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

public class ShopItemUtils {

    private static PaginationService pagServ = Sponge.getServiceManager().provide(PaginationService.class).get();

    public static void sendShopBuyView(Player player, Shop shop) {

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
                itemAmount = Text.of("\u221E");
            } else {
                itemAmount = Text.of(item.getItemAmount() == -1 ? "--" : String.valueOf(item.getItemAmount()));
            }

            //Grab the necessary info we need from the item
            Text itemName = InventoryUtils.getItemName(item.getItemStack());
            Text itemMax = Text.of(item.getItemMaxAmount() == -1 ? "\u221E" : String.valueOf(item.getItemMaxAmount()));
            Text itemSell = Text.of(item.getItemBuyPrice() == -1 ? "--" : String.valueOf(item.getItemBuyPrice()));
            Text itemBuy = Text.of(item.getItemSellPrice() == -1 ? "--" : String.valueOf(item.getItemSellPrice()));

            //Add the appropriate actions to the text
            itemName = itemName.toBuilder().onHover(TextActions.showItem(item.getItemStack())).style(TextStyles.UNDERLINE).build();
            Text buy = Text.builder("Buy")
                    .onClick(TextActions.runCommand("/shop cb \"How many " + itemName.toPlain() + " would you like to buy?\" shop item buy " + i + " %c"))
                    .style(TextStyles.UNDERLINE).build();
            Text sell = Text.builder("Sell")
                    .onClick(TextActions.runCommand("/shop cb \"How many " + itemName.toPlain() + " would you like to sell?\" shop item sell " + i + " %c"))
                    .style(TextStyles.UNDERLINE).build();

            //Build the full line of text
            Text fullLine = Text.of(itemName, " ", itemAmount, "/", itemMax, " | ", itemBuy, " | ", itemSell, " ", buy, " ", sell);

            //Add the text to the shop display
            contents.add(fullLine);
            contents.add(Text.of(""));
        }

        //Build header
        Text header;
        if (shop.isUnlimitedMoney()) {
            header = Text.of(TextColors.BLUE, "Shop's Balance: ", TextColors.WHITE, Text.of("\u221E"));
        } else {
            header = Text.of(TextColors.BLUE, "Shop's Balance: ", TextColors.WHITE, shop.getBalance());
        }
        if (shop.hasManagerPermissions(player)) {
            Text manager = Text.builder("Manager")
                    .onClick(TextActions.runCommand("/shop browse manager"))
                    .style(TextStyles.UNDERLINE).build();
            header = header.concat(Text.of("  |  ", manager));
        }
        if (shop.hasOwnerPermissions(player)) {
            Text owner = Text.builder("Owner")
                    .onClick(TextActions.runCommand("/shop browse owner"))
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
                itemAmount = Text.of("\u221E");
            } else {
                itemAmount = Text.of(item.getItemAmount() == -1 ? "--" : String.valueOf(item.getItemAmount()));
            }

            //Grab the necessary info we need from the item and add appropriate actions
            Text itemName = InventoryUtils.getItemName(item.getItemStack());
            Text itemMax = Text.builder(item.getItemMaxAmount() == -1 ? "\u221E" : String.valueOf(item.getItemMaxAmount()))
                    .onClick(TextActions.runCommand("/shop cb \"Enter " + itemName.toPlain() + " max (-1 for infinite):\" shop item set max " + i + " %c"))
                    .style(TextStyles.UNDERLINE).build();
            Text itemSell = Text.builder(item.getItemBuyPrice() == -1 ? "--" : String.valueOf(item.getItemBuyPrice()))
                    .onClick(TextActions.runCommand("/shop cb \"Enter " + itemName.toPlain() + " buy price (-1 for none):\" shop item set buy " + i + " %c"))
                    .style(TextStyles.UNDERLINE).build();
            Text itemBuy = Text.builder(item.getItemSellPrice() == -1 ? "--" : String.valueOf(item.getItemSellPrice()))
                    .onClick(TextActions.runCommand("/shop cb \"Enter " + itemName.toPlain() + " sell price (-1 for none):\" shop item set sell " + i + " %c"))
                    .style(TextStyles.UNDERLINE).build();

            itemName = itemName.toBuilder().onHover(TextActions.showItem(item.getItemStack())).style(TextStyles.UNDERLINE).build();
            /*Text add = Text.builder("Add")
                    .onClick(TextActions.runCommand("/shop callBack \"How many " + itemName.toPlain() + " would you like to add?\" shop item add " + i + " %c"))
                    .style(TextStyles.UNDERLINE).build();*/
            Text remove = Text.builder("Remove")
                    .onClick(TextActions.runCommand("/shop cb \"How many " + itemName.toPlain() + " would you like to remove?\" shop item remove " + i + " %c"))
                    .style(TextStyles.UNDERLINE).build();

            //Build the full line of text
            Text fullLine = Text.of(itemName, " ", itemAmount, "/", itemMax, " | ", itemBuy, " | ", itemSell, " ", remove);

            //Add the text to the shop display
            contents.add(fullLine);
            contents.add(Text.of(""));
        }

        //Build header
        Text header;
        if (shop.isUnlimitedMoney()) {
            header = Text.of(TextColors.BLUE, "Shop's Balance: ", TextColors.WHITE, Text.of("\u221E"));
        } else {
            header = Text.of(TextColors.BLUE, "Shop's Balance: ", TextColors.WHITE, shop.getBalance());
        }

        Text browse = Text.builder("Browse")
                .onClick(TextActions.runCommand("/shop browse"))
                .style(TextStyles.UNDERLINE).build();
        header = header.concat(Text.of("  |  ", browse));

        if (shop.hasOwnerPermissions(player)) {
            Text owner = Text.builder("Owner")
                    .onClick(TextActions.runCommand("/shop browse owner"))
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

        //First build up the contents of the shop
        List<Text> contents = new ArrayList<>();

        //Add option to put shop up for sale
        Text putUpForSale = Text.builder("Put Up For Sale")
                .onClick(TextActions.runCommand("/shop cb \"Enter price (-1 to cancel sale):\" shop set price %c"))
                .style(TextStyles.UNDERLINE).build();

        contents.add(Text.of(TextColors.BLUE, putUpForSale));

        //Display shop type if present
        if (!shop.getType().isEmpty()) {
            contents.add(Text.of(TextColors.BLUE, "Shop Type: ", TextColors.WHITE, shop.getType()));
        }

        //Add option to change shop name
        Text changeShopName = Text.builder("Change")
                .onClick(TextActions.runCommand("/shop cb \"Enter new shop name:\" shop set name %c"))
                .style(TextStyles.UNDERLINE).build();

        contents.add(Text.of(TextColors.BLUE, "Shop Name: ", TextColors.WHITE, shop.getName(), " ", changeShopName));

        //Add option to change shop owner
        Text changeShopOwner = Text.builder("Change")
                .onClick(TextActions.runCommand("/shop cb \"Enter new shop owner:\" shop set owner %c"))
                .style(TextStyles.UNDERLINE).build();

        String ownerName;
        Optional<Player> ownerOptional = Sponge.getServer().getPlayer(shop.getOwnerUUID());
        if (ownerOptional.isPresent()) {
            ownerName = ownerOptional.get().getName();
        } else {
            ownerName = "Unknown";
        }
        contents.add(Text.of(TextColors.BLUE, "Shop Owner: ", TextColors.WHITE, ownerName, " ", changeShopOwner));

        //Add option to deposit and withdraw shop funds
        Text deposit = Text.builder("Deposit")
                .onClick(TextActions.runCommand("/shop cb \"How much would you like to deposit?\" shop balance deposit %c"))
                .style(TextStyles.UNDERLINE).build();

        Text withdraw = Text.builder("Withdraw")
                .onClick(TextActions.runCommand("/shop cb \"How much would you like to withdraw?\" shop balance withdraw %c"))
                .style(TextStyles.UNDERLINE).build();

        contents.add(Text.of(TextColors.BLUE, "Shop Balance: ", TextColors.WHITE, shop.getBalance(), " ", deposit, " ", withdraw));

        //Add option to add and remove managers
        contents.add(Text.of(""));

        Text addManager = Text.builder("Add Manager")
                .onClick(TextActions.runCommand("/shop cb \"Enter manager:\" shop manager add %c"))
                .style(TextStyles.UNDERLINE).build();
        contents.add(Text.of(TextColors.BLUE, "Managers: ", TextColors.WHITE, addManager));

        for (UUID manager : shop.getManagerUUIDset()) {
            Optional<Player> managerOptional = Sponge.getServer().getPlayer(manager);
            if (managerOptional.isPresent()) {
                String managerName = managerOptional.get().getName();

                Text removeManger = Text.builder("Remove")
                        .onClick(TextActions.runCommand("/shop manager remove " + managerName))
                        .style(TextStyles.UNDERLINE).build();

                contents.add(Text.of(managerName, " ", removeManger));
            }
        }

        //Add option to destroy items
        List<ShopItem> items = shop.getItems();
        if (!items.isEmpty()) {
            contents.add(Text.of(""));

            contents.add(Text.of(TextColors.BLUE, "Items:"));

            for (int i = 0; i < items.size(); i++) {
                Text destroyItem = Text.builder("Destroy")
                        .onClick(TextActions.runCommand("/shop item destroy " + i))
                        .style(TextStyles.UNDERLINE).build();

                Text itemName = InventoryUtils.getItemName(items.get(i).getItemStack());
                itemName = itemName.toBuilder().onHover(TextActions.showItem(items.get(i).getItemStack())).style(TextStyles.UNDERLINE).build();

                contents.add(Text.of(itemName, " ", destroyItem));
                contents.add(Text.of(""));
            }
        }

        //Build header
        Text header;
        if (shop.isUnlimitedMoney()) {
            header = Text.of(TextColors.BLUE, "Shop's Balance: ", TextColors.WHITE, Text.of("\u221E"));
        } else {
            header = Text.of(TextColors.BLUE, "Shop's Balance: ", TextColors.WHITE, shop.getBalance());
        }

        Text browse = Text.builder("Browse")
                .onClick(TextActions.runCommand("/shop browse"))
                .style(TextStyles.UNDERLINE).build();
        header = header.concat(Text.of("  |  ", browse));

        if (shop.hasOwnerPermissions(player)) {
            Text manager = Text.builder("Manager")
                    .onClick(TextActions.runCommand("/shop browse manager"))
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