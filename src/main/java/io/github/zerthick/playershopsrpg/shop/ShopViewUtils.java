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

package io.github.zerthick.playershopsrpg.shop;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import io.github.zerthick.playershopsrpg.cmd.callback.CallBackBuffer;
import io.github.zerthick.playershopsrpg.permissions.Permissions;
import io.github.zerthick.playershopsrpg.utils.econ.EconManager;
import io.github.zerthick.playershopsrpg.utils.inventory.InventoryUtils;
import io.github.zerthick.playershopsrpg.utils.messages.Messages;
import org.spongepowered.api.Sponge;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.entity.living.player.User;
import org.spongepowered.api.service.pagination.PaginationService;
import org.spongepowered.api.service.user.UserStorageService;
import org.spongepowered.api.text.Text;
import org.spongepowered.api.text.action.TextActions;
import org.spongepowered.api.text.format.TextColors;
import org.spongepowered.api.text.format.TextStyles;

import java.math.BigDecimal;
import java.time.format.DateTimeFormatter;
import java.util.*;

public class ShopViewUtils {

    private static final int LINES_PER_PAGE = 10;
    private static PaginationService pagServ = Sponge.getServiceManager().provide(PaginationService.class).get();

    public static void sendShopBuyView(Player player, Shop shop) {

        //Callback buffer
        CallBackBuffer cb = CallBackBuffer.getInstance();

        //First build up the contents of the shop
        List<Text> contents = new ArrayList<>();
        List<ShopItem> items = new ArrayList<>(shop.getItems().values());

        if (items.isEmpty()) {
            contents.add(Text.of(TextColors.BLUE, Messages.UI_NO_ITEMS_TO_DISPLAY));
        }

        //Sort items by display name
        items.sort(Comparator.comparing(o -> InventoryUtils.getItemNamePlain(o.getItemStack())));
        for (ShopItem item : items) {

            Text itemAmount;
            if (shop.isUnlimitedStock()) {
                itemAmount = Text.of(Messages.UI_INFINITY);
            } else {
                itemAmount = Text.of(item.getItemAmount() == -1 ? Messages.UI_EMPTY : String.valueOf(item.getItemAmount()));
            }

            //Grab the necessary info we need from the item
            Text itemName = InventoryUtils.getItemName(item.getItemStack());
            Text itemMax = Text.of(item.getItemMaxAmount() == -1 ? Messages.UI_INFINITY : String.valueOf(item.getItemMaxAmount()));
            Text itemSell = Text.of(item.getItemBuyPrice() == -1 ? Messages.UI_EMPTY : formatCurrency(item.getItemBuyPrice()));
            Text itemBuy = Text.of(item.getItemSellPrice() == -1 ? Messages.UI_EMPTY : formatCurrency(item.getItemSellPrice()));

            //Add the appropriate actions to the text
            itemName = itemName.toBuilder().onHover(TextActions.showItem(item.getItemStack().createSnapshot())).style(TextStyles.UNDERLINE).build();
            Text buy = Text.EMPTY;
            if (player.hasPermission(Permissions.PLAYERSHOPSRPG_COMMAND_ITEM_BUY) && item.getItemSellPrice() != -1) {
                if (item.getItemAmount() != 0 || shop.isUnlimitedStock()) {
                    buy = Text.builder(Messages.UI_BUY)
                            .onClick(TextActions.executeCallback(cb.getCallBack(Messages.processDropins(Messages.UI_BUY_PROMPT,
                                    ImmutableMap.of(Messages.DROPIN_ITEM_NAME, itemName.toPlain())),
                                    ImmutableList.of("shop item buy " + item.getShopItemUUID() + " %c " + shop.getUUID(), "shop browse"))))
                            .style(TextStyles.UNDERLINE).build();
                } else {
                    buy = Text.builder(Messages.UI_BUY)
                            .onHover(TextActions.showText(Text.of(TextColors.RED, Messages.processDropins(Messages.UI_BUY_SOLD_OUT,
                                    ImmutableMap.of(Messages.DROPIN_ITEM_NAME, itemName.toPlain(), Messages.DROPIN_SHOP_NAME, shop.getName())))))
                            .color(TextColors.DARK_GRAY)
                            .style(TextStyles.UNDERLINE).build();
                }
            }
            Text sell = Text.EMPTY;
            if (player.hasPermission(Permissions.PLAYERSHOPSRPG_COMMAND_ITEM_SELL) && item.getItemBuyPrice() != -1) {
                if (item.getItemAmount() != item.getItemMaxAmount()) {
                    sell = Text.builder(Messages.UI_SELL)
                            .onClick(TextActions.executeCallback(cb.getCallBack(Messages.processDropins(Messages.UI_SELL_PROMPT,
                                    ImmutableMap.of(Messages.DROPIN_ITEM_NAME, itemName.toPlain(), Messages.DROPIN_SHOP_NAME, shop.getName())),
                                    ImmutableList.of("shop item sell " + item.getShopItemUUID() + " %c " + shop.getUUID(), "shop browse"))))
                            .style(TextStyles.UNDERLINE).build();
                } else {
                    sell = Text.builder(Messages.UI_SELL)
                            .onHover(TextActions.showText(Text.of(Messages.processDropins(Messages.UI_SELL_FULL_STOCK,
                                    ImmutableMap.of(Messages.DROPIN_ITEM_NAME, itemName.toPlain(), Messages.DROPIN_SHOP_NAME, shop.getName())))))
                            .color(TextColors.DARK_GRAY)
                            .style(TextStyles.UNDERLINE).build();
                }
            }

            //Build the full line of text
            Text fullLine = Text.of(itemName, " ", itemAmount, "/", itemMax, " | ", itemBuy, " | ", itemSell, " ", buy, " ", sell);

            //Add the text to the shop display
            contents.add(fullLine);
            contents.add(Text.of(""));
        }

        //Build header
        Text header = Text.EMPTY;
        if (player.hasPermission(Permissions.PLAYERSHOPSRPG_COMMAND_BUY) && shop.isForSale()) {
            header = header.concat(Text.of(TextColors.AQUA, Messages.UI_FOR_SALE, formatCurrency(shop.getPrice()), "\n"));
        }

        if (player.hasPermission(Permissions.PLAYERSHOPSRPG_COMMAND_RENT) && shop.isForRent()) {
            header = header.concat(Text.of(TextColors.AQUA, Messages.UI_FOR_RENT, formatCurrency(shop.getRent()), "/hr\n"));
        }

        if (shop.isUnlimitedMoney()) {
            header = header.concat(Text.of(TextColors.BLUE, Messages.UI_SHOP_BALANCE, TextColors.WHITE, Text.of(Messages.UI_INFINITY)));
        } else {
            header = header.concat(Text.of(TextColors.BLUE, Messages.UI_SHOP_BALANCE, TextColors.WHITE, formatCurrency(shop.getBalance())));
        }

        if (shop.hasManagerPermissions(player)) {
            Text manager = Text.builder(Messages.UI_MANAGER)
                    .onClick(TextActions.runCommand("/shop browse manager " + shop.getUUID()))
                    .style(TextStyles.UNDERLINE).build();
            header = header.concat(Text.of("  |  ", manager));
        }
        if (shop.hasRenterPermissions(player)) {
            Text owner = Text.builder(Messages.UI_OWNER)
                    .onClick(TextActions.runCommand("/shop browse owner " + shop.getUUID()))
                    .style(TextStyles.UNDERLINE).build();
            header = header.concat(Text.of("  |  ", owner));
        }
        header = header.concat(Text.of("\n"));

        pagServ.builder()
                .title(Text.of(shop.getName()))
                .header(header)
                .padding(Text.of(TextColors.BLUE, Messages.UI_PADDING_STRING))
                .contents(contents)
                .linesPerPage(LINES_PER_PAGE)
                .sendTo(player);

    }

    public static void sendShopManagerView(Player player, Shop shop) {

        //Callback buffer
        CallBackBuffer cb = CallBackBuffer.getInstance();

        //First build up the contents of the shop
        List<Text> contents = new ArrayList<>();
        List<ShopItem> items = new ArrayList<>(shop.getItems().values());
        if (items.isEmpty()) {
            contents.add(Text.of(TextColors.BLUE, Messages.UI_NO_ITEMS_TO_DISPLAY));
        }

        //Sort items by display name
        items.sort(Comparator.comparing(o -> InventoryUtils.getItemNamePlain(o.getItemStack())));
        for (ShopItem item : items) {

            Text itemAmount;
            if (shop.isUnlimitedStock()) {
                itemAmount = Text.of(Messages.UI_INFINITY);
            } else {
                itemAmount = Text.of(item.getItemAmount() == -1 ? Messages.UI_EMPTY : String.valueOf(item.getItemAmount()));
            }

            //Grab the necessary info we need from the item and add appropriate actions
            Text itemName = InventoryUtils.getItemName(item.getItemStack());
            Text itemMax = Text.EMPTY, itemSell = Text.EMPTY, itemBuy = Text.EMPTY;
            if (player.hasPermission(Permissions.PLAYERSHOPSRPG_COMMAND_ITEM_SET)) {
                itemMax = Text.builder(item.getItemMaxAmount() == -1 ? Messages.UI_INFINITY : String.valueOf(item.getItemMaxAmount()))
                        .onClick(TextActions.executeCallback(cb.getCallBack(Messages.processDropins(Messages.UI_SET_ITEM_MAX_PROMPT,
                                ImmutableMap.of(Messages.DROPIN_ITEM_NAME, itemName.toPlain())),
                                ImmutableList.of("shop item set max " + item.getShopItemUUID() + " %c " + shop.getUUID(), "shop browse manager"))))
                        .style(TextStyles.UNDERLINE).build();
                itemSell = Text.builder(item.getItemBuyPrice() == -1 ? Messages.UI_EMPTY : formatCurrency(item.getItemBuyPrice()).toPlain())
                        .onClick(TextActions.executeCallback(cb.getCallBack(Messages.processDropins(Messages.UI_SET_ITEM_BUY_PROMPT,
                                ImmutableMap.of(Messages.DROPIN_ITEM_NAME, itemName.toPlain())),
                                ImmutableList.of("shop item set buy " + item.getShopItemUUID() + " %c " + shop.getUUID(), "shop browse manager"))))
                        .style(TextStyles.UNDERLINE).build();
                itemBuy = Text.builder(item.getItemSellPrice() == -1 ? Messages.UI_EMPTY : formatCurrency(item.getItemSellPrice()).toPlain())
                        .onClick(TextActions.executeCallback(cb.getCallBack(Messages.processDropins(Messages.UI_SET_ITEM_SELL_PROMPT,
                                ImmutableMap.of(Messages.DROPIN_ITEM_NAME, itemName.toPlain())),
                                ImmutableList.of("shop item set sell " + item.getShopItemUUID() + " %c " + shop.getUUID(), "shop browse manager"))))
                        .style(TextStyles.UNDERLINE).build();
            }
            itemName = itemName.toBuilder().onHover(TextActions.showItem(item.getItemStack().createSnapshot())).style(TextStyles.UNDERLINE).build();

            Text remove = Text.EMPTY;
            if (player.hasPermission(Permissions.PLAYERSHOPSRPG_COMMAND_ITEM_REMOVE)) {
                remove = Text.builder(Messages.UI_REMOVE)
                        .onClick(TextActions.executeCallback(cb.getCallBack(Messages.processDropins(Messages.UI_REMOVE_PROMPT,
                                ImmutableMap.of(Messages.DROPIN_ITEM_NAME, itemName.toPlain())),
                                ImmutableList.of("shop item remove " + item.getShopItemUUID() + " %c " + shop.getUUID(), "shop browse manager"))))
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
            header = header.concat(Text.of(TextColors.AQUA, Messages.UI_FOR_SALE, formatCurrency(shop.getPrice()), "\n"));
        }

        if (player.hasPermission(Permissions.PLAYERSHOPSRPG_COMMAND_RENT) && shop.isForRent()) {
            header = header.concat(Text.of(TextColors.AQUA, Messages.UI_FOR_RENT, formatCurrency(shop.getRent()), "/hr\n"));
        }

        if (shop.isUnlimitedMoney()) {
            header = header.concat(Text.of(TextColors.BLUE, Messages.UI_SHOP_BALANCE, TextColors.WHITE, Text.of(Messages.UI_INFINITY)));
        } else {
            header = header.concat(Text.of(TextColors.BLUE, Messages.UI_SHOP_BALANCE, TextColors.WHITE, formatCurrency(shop.getBalance())));
        }

        Text browse = Text.builder(Messages.UI_BROWSE)
                .onClick(TextActions.runCommand("/shop browse " + shop.getUUID()))
                .style(TextStyles.UNDERLINE).build();
        header = header.concat(Text.of("  |  ", browse));

        if (shop.hasRenterPermissions(player)) {
            Text owner = Text.builder(Messages.UI_OWNER)
                    .onClick(TextActions.runCommand("/shop browse owner " + shop.getUUID()))
                    .style(TextStyles.UNDERLINE).build();
            header = header.concat(Text.of("  |  ", owner));
        }
        header = header.concat(Text.of("\n"));

        pagServ.builder()
                .title(Text.of(shop.getName()))
                .header(header)
                .padding(Text.of(TextColors.BLUE, Messages.UI_PADDING_STRING))
                .contents(contents)
                .linesPerPage(LINES_PER_PAGE)
                .sendTo(player);
    }

    public static void sendShopOwnerView(Player player, Shop shop) {

        //Callback buffer
        CallBackBuffer cb = CallBackBuffer.getInstance();

        //First build up the contents of the shop
        List<Text> contents = new ArrayList<>();

        //Add option to put shop up for sale
        if (player.hasPermission(Permissions.PLAYERSHOPSRPG_COMMAND_SET_PRICE) && !shop.isBeingRented()) {
            Text putUpForSale;
            if (shop.isForSale()) {
                putUpForSale = formatCurrency(shop.getPrice()).toBuilder()
                        .onClick(TextActions.executeCallback(cb.getCallBack(Messages.UI_PUT_UP_FOR_SALE_PROMPT,
                                ImmutableList.of("shop set price %c " + shop.getUUID(), "shop browse owner"))))
                        .style(TextStyles.UNDERLINE).build();
            } else {
                putUpForSale = Text.builder(Messages.UI_PUT_UP_FOR_SALE)
                        .onClick(TextActions.executeCallback(cb.getCallBack(Messages.UI_PUT_UP_FOR_SALE_PROMPT,
                                ImmutableList.of("shop set price %c " + shop.getUUID(), "shop browse owner"))))
                        .style(TextStyles.UNDERLINE).build();
            }
            contents.add(Text.of(TextColors.BLUE, Messages.UI_SALE, TextColors.WHITE, putUpForSale, "\n"));
        }

        //Add option to put shop up for rent
        if (player.hasPermission(Permissions.PLAYERSHOPSRPG_COMMAND_SET_RENT) && !shop.isBeingRented()) {
            Text putUpForRent;
            if (shop.isForRent()) {
                putUpForRent = formatCurrency(shop.getRent()).toBuilder()
                        .append(Text.of("/hr"))
                        .onClick(TextActions.executeCallback(cb.getCallBack(Messages.UI_PUT_UP_FOR_RENT_PROMPT,
                                ImmutableList.of("shop set rent %c " + shop.getUUID(), "shop browse owner"))))
                        .style(TextStyles.UNDERLINE).build();
            } else {
                putUpForRent = Text.builder(Messages.UI_PUT_UP_FOR_RENT)
                        .onClick(TextActions.executeCallback(cb.getCallBack(Messages.UI_PUT_UP_FOR_RENT_PROMPT,
                                ImmutableList.of("shop set rent %c " + shop.getUUID(), "shop browse owner"))))
                        .style(TextStyles.UNDERLINE).build();
            }
            contents.add(Text.of(TextColors.BLUE, Messages.UI_RENT, TextColors.WHITE, putUpForRent, "\n"));
        }

        //Display shop type if present
        if (!shop.getType().isEmpty()) {
            contents.add(Text.of(TextColors.BLUE, Messages.UI_SHOP_TYPE, TextColors.WHITE, shop.getType()));
        }

        //Add option to change shop name
        Text changeShopName = Text.EMPTY;
        if (player.hasPermission(Permissions.PLAYERSHOPSRPG_COMMAND_SET_NAME)) {
            changeShopName = Text.builder(Messages.UI_CHANGE)
                    .onClick(TextActions.executeCallback(cb.getCallBack(Messages.UI_NEW_SHOP_NAME_PROMPT,
                            ImmutableList.of("shop set name \"%c\" " + shop.getUUID(), "shop browse owner"))))
                    .style(TextStyles.UNDERLINE).build();
        }
        contents.add(Text.of(TextColors.BLUE, Messages.UI_SHOP_NAME, TextColors.WHITE, shop.getName(), " ", changeShopName));

        //Add option to change shop owner
        Text changeShopOwner = Text.EMPTY;
        if (player.hasPermission(Permissions.PLAYERSHOPSRPG_COMMAND_SET_OWNER)) {
            changeShopOwner = Text.builder(Messages.UI_CHANGE)
                    .onClick(TextActions.executeCallback(cb.getCallBack(Messages.UI_NEW_SHOP_OWNER_PROMPT,
                            ImmutableList.of("shop set owner %c " + shop.getUUID(), "shop browse owner"))))
                    .style(TextStyles.UNDERLINE).build();
        }
        String ownerName = Messages.UI_UNKNOWN;
        Optional<UUID> ownerUUIDOptional = shop.getOwnerUUID();
        if (ownerUUIDOptional.isPresent()) {
            ownerName = getNameForUuid(ownerUUIDOptional.get()).orElse(Messages.UI_UNKNOWN);
        }
        contents.add(Text.of(TextColors.BLUE, Messages.UI_SHOP_OWNER, TextColors.WHITE, ownerName, " ", changeShopOwner));

        //Display shop renter if present
        if (shop.isBeingRented()) {
            String renterName;
            renterName = getNameForUuid(shop.getRenterUUID()).orElse(Messages.UI_UNKNOWN);
            String expireTime = ShopRentManager.getInstance().getShopExpireTime(shop).format(DateTimeFormatter.ofPattern("MMM d yyyy  hh:mm a", player.getLocale()));
            contents.add(Text.of(TextColors.BLUE, Messages.UI_RENT, TextColors.WHITE, formatCurrency(shop.getRent()), "/hr"));
            contents.add(Text.of(TextColors.BLUE, Messages.UI_SHOP_RENTER, TextColors.WHITE, renterName, TextColors.BLUE, " ", Messages.UI_UNTIL, " ", TextColors.AQUA, expireTime));
        }

        //Add option to deposit and withdraw shop funds
        Text deposit = Text.EMPTY;
        if (player.hasPermission(Permissions.PLAYERSHOPSRPG_COMMAND_BALANCE_DEPOSIT)) {
            deposit = Text.builder(Messages.UI_DEPOSIT)
                    .onClick(TextActions.executeCallback(cb.getCallBack(Messages.UI_DEPOSIT_PROMPT,
                            ImmutableList.of("shop balance deposit %c " + shop.getUUID(), "shop browse owner"))))
                    .style(TextStyles.UNDERLINE).build();
        }

        Text withdraw = Text.EMPTY;
        if (player.hasPermission(Permissions.PLAYERSHOPSRPG_COMMAND_BALANCE_WITHDRAW)) {
            withdraw = Text.builder(Messages.UI_WITHDRAW)
                    .onClick(TextActions.executeCallback(cb.getCallBack(Messages.UI_WITHDRAW_PROMPT,
                            ImmutableList.of("shop balance withdraw %c " + shop.getUUID(), "shop browse owner"))))
                    .style(TextStyles.UNDERLINE).build();
        }

        contents.add(Text.of(TextColors.BLUE, Messages.UI_SHOP_BALANCE, TextColors.WHITE, formatCurrency(shop.getBalance()), " ", deposit, " ", withdraw));

        //Add option to add and remove managers
        contents.add(Text.of(""));

        Text addManager = Text.EMPTY;
        if (player.hasPermission(Permissions.PLAYERSHOPSRPG_COMMAND_MANAGER_ADD)) {
            addManager = Text.builder(Messages.UI_ADD_MANAGER)
                    .onClick(TextActions.executeCallback(cb.getCallBack(Messages.UI_ADD_MANAGER_PROMPT,
                            ImmutableList.of("shop manager add %c " + shop.getUUID(), "shop browse owner"))))
                    .style(TextStyles.UNDERLINE).build();
        }
        contents.add(Text.of(TextColors.BLUE, Messages.UI_MANAGERS, TextColors.WHITE, addManager));

        for (UUID manager : shop.getManagerUUIDSet()) {
            Optional<Player> managerOptional = Sponge.getServer().getPlayer(manager);
            if (managerOptional.isPresent()) {
                String managerName = managerOptional.get().getName();

                Text removeManger = Text.EMPTY;
                if (player.hasPermission(Permissions.PLAYERSHOPSRPG_COMMAND_MANAGER_REMOVE)) {
                    removeManger = Text.builder(Messages.UI_REMOVE)
                            .onClick(TextActions.runCommand("/shop manager remove " + managerName + " " + shop.getUUID()))
                            .style(TextStyles.UNDERLINE).build();
                }

                contents.add(Text.of(managerName, " ", removeManger));
            }
        }

        //Add option to destroy items
        List<ShopItem> items = new ArrayList<>(shop.getItems().values());
        if (!items.isEmpty()) {
            //Sort items by display name
            items.sort(Comparator.comparing(o -> InventoryUtils.getItemNamePlain(o.getItemStack())));

            contents.add(Text.of(""));

            contents.add(Text.of(TextColors.BLUE, Messages.UI_ITEMS));

            for (ShopItem item : items) {
                Text destroyItem = Text.EMPTY;
                if (player.hasPermission(Permissions.PLAYERSHOPSRPG_COMMAND_ITEM_DESTROY)) {
                    destroyItem = Text.builder(Messages.UI_DESTROY)
                            .onClick(TextActions.runCommand("/shop item destroy " + item.getShopItemUUID()))
                            .color(TextColors.RED)
                            .style(TextStyles.UNDERLINE).build();
                }

                Text itemName = InventoryUtils.getItemName(item.getItemStack());
                itemName = itemName.toBuilder().onHover(TextActions.showItem(item.getItemStack().createSnapshot())).style(TextStyles.UNDERLINE).build();

                contents.add(Text.of(itemName, " ", destroyItem));
                contents.add(Text.of(""));
            }
        }

        //Build header
        Text header = Text.EMPTY;
        if (player.hasPermission(Permissions.PLAYERSHOPSRPG_COMMAND_BUY) && shop.isForSale()) {
            header = header.concat(Text.of(TextColors.AQUA, Messages.UI_FOR_SALE, formatCurrency(shop.getPrice()), "\n"));
        }

        if (player.hasPermission(Permissions.PLAYERSHOPSRPG_COMMAND_RENT) && shop.isForRent()) {
            header = header.concat(Text.of(TextColors.AQUA, Messages.UI_FOR_RENT, formatCurrency(shop.getRent()), "/hr\n"));
        }

        if (shop.isUnlimitedMoney()) {
            header = header.concat(Text.of(TextColors.BLUE, Messages.UI_SHOP_BALANCE, TextColors.WHITE, Text.of(Messages.UI_INFINITY)));
        } else {
            header = header.concat(Text.of(TextColors.BLUE, Messages.UI_SHOP_BALANCE, TextColors.WHITE, formatCurrency(shop.getBalance())));
        }

        Text browse = Text.builder(Messages.UI_BROWSE)
                .onClick(TextActions.runCommand("/shop browse " + shop.getUUID()))
                .style(TextStyles.UNDERLINE).build();
        header = header.concat(Text.of("  |  ", browse));

        if (shop.hasRenterPermissions(player)) {
            Text manager = Text.builder(Messages.UI_MANAGER)
                    .onClick(TextActions.runCommand("/shop browse manager " + shop.getUUID()))
                    .style(TextStyles.UNDERLINE).build();
            header = header.concat(Text.of("  |  ", manager));
        }
        header = header.concat(Text.of("\n"));

        pagServ.builder()
                .title(Text.of(shop.getName()))
                .header(header)
                .padding(Text.of(TextColors.BLUE, Messages.UI_PADDING_STRING))
                .contents(contents)
                .linesPerPage(LINES_PER_PAGE)
                .sendTo(player);
    }

    private static Text formatCurrency(BigDecimal value) {
        return EconManager.getInstance().getDefaultCurrency().format(value);
    }

    private static Text formatCurrency(Double value) {
        return formatCurrency(BigDecimal.valueOf(value));
    }

    private static Optional<String> getNameForUuid(UUID uuid) {
        UserStorageService uss = Sponge.getServiceManager().provideUnchecked(UserStorageService.class);
        Optional<User> userOptional = uss.get(uuid);

        if (userOptional.isPresent()) {
            // the name with which that player has been online the last time
            String name = userOptional.get().getName();
            return Optional.of(name);
        } else {
            // a player with that uuid has never been on your server
            return Optional.empty();
        }
    }
}