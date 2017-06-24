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

import com.google.common.collect.ImmutableMap;
import io.github.zerthick.playershopsrpg.permissions.Permissions;
import io.github.zerthick.playershopsrpg.utils.econ.EconManager;
import io.github.zerthick.playershopsrpg.utils.inventory.InventoryUtils;
import io.github.zerthick.playershopsrpg.utils.messages.Messages;
import org.spongepowered.api.Sponge;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.event.cause.Cause;
import org.spongepowered.api.event.cause.NamedCause;
import org.spongepowered.api.item.inventory.ItemStack;
import org.spongepowered.api.item.inventory.entity.PlayerInventory;
import org.spongepowered.api.service.economy.Currency;
import org.spongepowered.api.service.economy.account.UniqueAccount;
import org.spongepowered.api.service.economy.transaction.ResultType;
import org.spongepowered.api.service.economy.transaction.TransactionResult;

import java.math.BigDecimal;
import java.util.*;

public class Shop {

    private final UUID shopUUID;
    private String name;
    private UUID ownerUUID;
    private UUID renterUUID;
    private Set<UUID> managerUUIDset;
    private Map<UUID, ShopItem> items;
    private boolean unlimitedMoney;
    private boolean unlimitedStock;
    private String type;
    private double price;
    private double rent;
    private String currencyID;

    /**
     * Basic Constructor, used for creating shops for the first time
     *
     * @param name The name of the shop.
     * @param ownerUUID The UUID of the owner player.
     */
    public Shop(String name, UUID ownerUUID) {
        shopUUID = UUID.randomUUID();
        this.name = name;
        this.ownerUUID = ownerUUID;
        renterUUID = null;
        managerUUIDset = new HashSet<>();
        items = new HashMap<>();
        unlimitedMoney = false;
        unlimitedStock = false;
        type = "";
        price = -1;
        rent = -1;
        currencyID = null;
    }

    public Shop(UUID shopUUID,
                String name,
                UUID ownerUUID,
                UUID renterUUID,
                Set<UUID> managerUUIDset,
                Map<UUID, ShopItem> items,
                boolean unlimitedMoney,
                boolean unlimitedStock,
                String type,
                double price,
                double rent,
                String currencyID) {
        this.shopUUID = shopUUID;
        this.name = name;
        this.ownerUUID = ownerUUID;
        this.renterUUID = renterUUID;
        this.managerUUIDset = new HashSet<>(managerUUIDset);
        this.items = items;
        this.unlimitedMoney = unlimitedMoney;
        this.unlimitedStock = unlimitedStock;
        this.type = type;
        this.price = price;
        this.rent = rent;
        this.currencyID = currencyID;
    }

    public ShopTransactionResult createItem(Player player, ItemStack itemStack) {

        //If the player is not the owner of the shop return a message to the player
        if (!hasRenterPermissions(player)) {
            return new ShopTransactionResult(Messages.YOU_ARE_NOT_THE_OWNER_OF_THIS_SHOP);
        }

        //If the item already exists return a message to the player
        for (ShopItem item : items.values()) {
            if (InventoryUtils.itemStackEqualsIgnoreSize(item.getItemStack(), itemStack)) {
                return new ShopTransactionResult(Messages.THE_SPECIFIED_ITEM_IS_ALREADY_IN_THIS_SHOP);
            }
        }

        //The item is not already in the shop, we need to add it
        ItemStack itemToAdd = itemStack.copy();
        itemToAdd.setQuantity(1);
        ShopItem newShopItem = new ShopItem(itemToAdd.createSnapshot(), 0, -1, -1, -1);
        items.put(newShopItem.getShopItemUUID(), newShopItem);
        return ShopTransactionResult.SUCCESS;
    }

    public ShopTransactionResult destroyItem(Player player, UUID itemUUID) {

        //If the player is not the owner of the shop return a message to the player
        if (!hasRenterPermissions(player)) {
            return new ShopTransactionResult(Messages.YOU_ARE_NOT_THE_OWNER_OF_THIS_SHOP);
        }

        //If the item is in the shop, remove it
        if (items.containsKey(itemUUID)) {
            items.remove(itemUUID);
            return ShopTransactionResult.SUCCESS;
        }

        return new ShopTransactionResult(Messages.THE_SPECIFIED_ITEM_IS_NOT_IN_THIS_SHOP);
    }

    public ShopTransactionResult buyItem(Player player, UUID itemUUID, int amount) {

        //Amount Bounds Check
        if (amount < 0) {
            return new ShopTransactionResult(Messages.INVALID_ITEM_BUY_AMOUNT);
        }

        //Bounds Check
        if (items.containsKey(itemUUID)) {
            ShopItem item = items.get(itemUUID);

            //Check if the shop does not buy this item
            if (item.getItemBuyPrice() == -1) {
                return new ShopTransactionResult(Messages.processDropins(Messages.SHOP_DOES_NOT_BUY_ITEM,
                        ImmutableMap.of(Messages.DROPIN_SHOP_NAME, getName(), Messages.DROPIN_ITEM_NAME,
                                InventoryUtils.getItemNamePlain(item.getItemStack()))));
            }

            //Check if the shop has enough room otherwise only buy as much as we can hold
            if ((amount + item.getItemAmount() > item.getItemMaxAmount()) && item.getItemMaxAmount() != -1) {
                amount = item.getItemMaxAmount() - item.getItemAmount();
            }

            //Check if the player has enough of the item in their inventory to sell
            if (InventoryUtils.getItemCount((PlayerInventory) player.getInventory(), item.getItemStack()) < amount) {
                return new ShopTransactionResult(Messages.processDropins(Messages.PLAYER_DOES_NOT_HAVE_ENOUGH_ITEM,
                        ImmutableMap.of(Messages.DROPIN_ITEM_AMOUNT, String.valueOf(amount), Messages.DROPIN_ITEM_NAME, InventoryUtils.getItemNamePlain(item.getItemStack()))));
            }

            //Transfer the funds from the shop's account to the player's account if it doesn't have unlimited money
            EconManager manager = EconManager.getInstance();
            Optional<UniqueAccount> playerAccountOptional = manager.getOrCreateAccount(player.getUniqueId());
            Optional<UniqueAccount> shopAccountOptional = manager.getOrCreateAccount(getUUID());

            if (playerAccountOptional.isPresent() && shopAccountOptional.isPresent()) {

                TransactionResult result;

                if (unlimitedMoney) {
                    result = playerAccountOptional.get().deposit(getShopCurrency(),
                            BigDecimal.valueOf(amount * item.getItemBuyPrice()),
                            Cause.of(NamedCause.notifier(this)));
                } else {
                    result = shopAccountOptional.get().transfer(playerAccountOptional.get(),
                            getShopCurrency(), BigDecimal.valueOf(amount * item.getItemBuyPrice()),
                            Cause.of(NamedCause.notifier(this)));
                }
                if (result.getResult() == ResultType.SUCCESS) {
                    item.setItemAmount(item.getItemAmount() + amount);
                    InventoryUtils.removeItem((PlayerInventory) player.getInventory(), item.getItemStack(), amount);
                    return ShopTransactionResult.SUCCESS;
                } else {
                    return new ShopTransactionResult(Messages.processDropins(Messages.SHOP_DOES_NOT_HAVE_ENOUGH_FUNDS,
                            ImmutableMap.of(Messages.DROPIN_SHOP_NAME, getName())));
                }
            }
        }

        return new ShopTransactionResult(Messages.THE_SPECIFIED_ITEM_IS_NOT_IN_THIS_SHOP);
    }

    public ShopTransactionResult sellItem(Player player, UUID itemUUID, int amount) {

        //Amount Bounds Check
        if (amount < 0) {
            return new ShopTransactionResult(Messages.INVALID_ITEM_SELL_AMOUNT);
        }

        //Bounds Check
        if (items.containsKey(itemUUID)) {
            ShopItem item = items.get(itemUUID);

            //Check if the shop does not sell this item
            if (item.getItemSellPrice() == -1) {
                return new ShopTransactionResult(Messages.processDropins(Messages.SHOP_DOES_NOT_SELL_ITEM,
                        ImmutableMap.of(Messages.DROPIN_SHOP_NAME, getName(), Messages.DROPIN_ITEM_NAME,
                                InventoryUtils.getItemNamePlain(item.getItemStack()))));
            }

            //Check if the player has enough room otherwise only sell as much as they can hold
            int availableSpace = InventoryUtils.getAvailableSpace((PlayerInventory) player.getInventory(), item.getItemStack());
            if (availableSpace < amount) {
                amount = availableSpace;
            }

            //Check if the shop has enough of the item in their inventory to sell
            if ((item.getItemAmount() < amount) && !unlimitedStock) {
                return new ShopTransactionResult(Messages.processDropins(Messages.SHOP_DOES_NOT_HAVE_ENOUGH_ITEM,
                        ImmutableMap.of(Messages.DROPIN_SHOP_NAME, getName(), Messages.DROPIN_ITEM_AMOUNT, String.valueOf(amount)
                                , Messages.DROPIN_ITEM_NAME, InventoryUtils.getItemNamePlain(item.getItemStack()))));
            }

            //Transfer the funds from the player's account to the shop's account
            EconManager manager = EconManager.getInstance();
            Optional<UniqueAccount> playerAccountOptional = manager.getOrCreateAccount(player.getUniqueId());
            Optional<UniqueAccount> shopAccountOptional = manager.getOrCreateAccount(getUUID());

            if (playerAccountOptional.isPresent() && shopAccountOptional.isPresent()) {

                TransactionResult result;

                if (unlimitedMoney) {
                    result = playerAccountOptional.get().withdraw(getShopCurrency(),
                            BigDecimal.valueOf(amount * item.getItemSellPrice()),
                            Cause.of(NamedCause.notifier(this)));
                } else {
                    result = playerAccountOptional.get().transfer(shopAccountOptional.get(),
                            getShopCurrency(), BigDecimal.valueOf(amount * item.getItemSellPrice()),
                            Cause.of(NamedCause.notifier(this)));
                }
                if (result.getResult() == ResultType.SUCCESS) {
                    if (!unlimitedStock) {
                        item.setItemAmount(item.getItemAmount() - amount);
                    }
                    InventoryUtils.addItem((PlayerInventory) player.getInventory(), item.getItemStack(), amount);
                    return ShopTransactionResult.SUCCESS;
                } else {
                    return new ShopTransactionResult(Messages.YOU_DON_T_HAVE_ENOUGH_FUNDS);
                }
            }
        }

        return new ShopTransactionResult(Messages.THE_SPECIFIED_ITEM_IS_NOT_IN_THIS_SHOP);
    }

    public ShopTransactionResult addItem(Player player, ItemStack itemStack, int amount) {

        //Amount Bounds Check
        if (amount < 0) {
            return new ShopTransactionResult(Messages.INVALID_ITEM_ADD_AMOUNT);
        }

        //If the player is not a manager of the shop return a message to the player
        if (!hasManagerPermissions(player)) {
            return new ShopTransactionResult(Messages.YOU_ARE_NOT_A_MANAGER_OF_THIS_SHOP);
        }

        //If the item exists add to it's total to the shopitem and remove it from the player's inventory
        for (ShopItem item : items.values()) {
            if (InventoryUtils.itemStackEqualsIgnoreSize(item.getItemStack(), itemStack)) {

                if ((amount + item.getItemAmount() > item.getItemMaxAmount()) && item.getItemMaxAmount() != -1) {
                    amount = item.getItemMaxAmount() - item.getItemAmount();
                }

                InventoryUtils.removeItem((PlayerInventory) player.getInventory(), itemStack, amount);

                item.setItemAmount(item.getItemAmount() + amount);
                return ShopTransactionResult.SUCCESS;
            }
        }

        return new ShopTransactionResult(Messages.THE_SPECIFIED_ITEM_IS_NOT_IN_THIS_SHOP);
    }

    public ShopTransactionResult removeItem(Player player, UUID itemUUID, int amount) {

        //Amount Bounds Check
        if (amount < 0) {
            return new ShopTransactionResult(Messages.INVALID_ITEM_REMOVE_AMOUNT);
        }

        //If the player is not a manager of the shop return a message to the player
        if (!hasManagerPermissions(player)) {
            return new ShopTransactionResult(Messages.YOU_ARE_NOT_A_MANAGER_OF_THIS_SHOP);
        }

        //Bounds Check
        if (items.containsKey(itemUUID)) {
            ShopItem item = items.get(itemUUID);
            if (amount > item.getItemAmount()) {
                amount = item.getItemAmount();
            }

            InventoryUtils.addItem((PlayerInventory) player.getInventory(), item.getItemStack(), amount);

            item.setItemAmount(item.getItemAmount() - amount);
            return ShopTransactionResult.SUCCESS;
        }

        return new ShopTransactionResult(Messages.THE_SPECIFIED_ITEM_IS_NOT_IN_THIS_SHOP);
    }

    public ShopTransactionResult setItemMax(Player player, UUID itemUUID, int amount) {

        //If the player is not a manager of the shop return a message to the player
        if (!hasManagerPermissions(player)) {
            return new ShopTransactionResult(Messages.YOU_ARE_NOT_A_MANAGER_OF_THIS_SHOP);
        }

        //Bounds Check
        if (items.containsKey(itemUUID)) {

            // Check if the new max amount is valid (-1) for infinite
            if (amount >= -1) {
                items.get(itemUUID).setItemMaxAmount(amount);
                return ShopTransactionResult.SUCCESS;
            }

            return new ShopTransactionResult(Messages.INVALID_MAX_ITEM_AMOUNT);
        }

        return new ShopTransactionResult(Messages.THE_SPECIFIED_ITEM_IS_NOT_IN_THIS_SHOP);
    }

    public ShopTransactionResult setItemBuyPrice(Player player, UUID itemUUID, double amount) {

        //If the player is not a manager of the shop return a message to the player
        if (!hasManagerPermissions(player)) {
            return new ShopTransactionResult(Messages.YOU_ARE_NOT_A_MANAGER_OF_THIS_SHOP);
        }

        //Bounds Check
        if (items.containsKey(itemUUID)) {

            // Check if the new max amount is valid (-1) for infinite
            if (amount >= -1) {
                items.get(itemUUID).setItemBuyPrice(amount);
                return ShopTransactionResult.SUCCESS;
            }

            return new ShopTransactionResult(Messages.INVALID_ITEM_BUY_PRICE);
        }

        return new ShopTransactionResult(Messages.THE_SPECIFIED_ITEM_IS_NOT_IN_THIS_SHOP);
    }

    public ShopTransactionResult setItemSellPrice(Player player, UUID itemUUID, double amount) {

        //If the player is not a manager of the shop return a message to the player
        if (!hasManagerPermissions(player)) {
            return new ShopTransactionResult(Messages.YOU_ARE_NOT_A_MANAGER_OF_THIS_SHOP);
        }

        //Bounds Check
        if (items.containsKey(itemUUID)) {

            // Check if the new max amount is valid (-1) for infinite
            if (amount >= -1) {
                items.get(itemUUID).setItemSellPrice(amount);
                return ShopTransactionResult.SUCCESS;
            }

            return new ShopTransactionResult(Messages.INVALID_ITEM_SELL_PRICE);
        }

        return new ShopTransactionResult(Messages.THE_SPECIFIED_ITEM_IS_NOT_IN_THIS_SHOP);
    }

    public ShopTransactionResult setOwner(Player player, UUID ownerUUID) {

        //If the player is not the owner of the shop return a message to the player
        if (!hasOwnerPermissions(player)) {
            return new ShopTransactionResult(Messages.YOU_ARE_NOT_THE_OWNER_OF_THIS_SHOP);
        }

        this.ownerUUID = ownerUUID;
        return ShopTransactionResult.SUCCESS;
    }

    public ShopTransactionResult addManager(Player player, UUID managerUUID) {

        //If the player is not the owner of the shop return a message to the player
        if (!hasRenterPermissions(player)) {
            return new ShopTransactionResult(Messages.YOU_ARE_NOT_THE_OWNER_OF_THIS_SHOP);
        }

        managerUUIDset.add(managerUUID);
        return ShopTransactionResult.SUCCESS;
    }

    public ShopTransactionResult removeManager(Player player, UUID managerUUID) {

        //If the player is not the owner of the shop return a message to the player
        if (!hasRenterPermissions(player)) {
            return new ShopTransactionResult(Messages.YOU_ARE_NOT_THE_OWNER_OF_THIS_SHOP);
        }

        if (!managerUUIDset.contains(managerUUID)) {

            String managerName = "";

            Optional<Player> manager = Sponge.getGame().getServer().getPlayer(managerUUID);
            if (manager.isPresent()) {
                managerName = manager.get().getName();
            }

            return new ShopTransactionResult(Messages.processDropins(Messages.PLAYER_IS_NOT_A_MANAGER,
                    ImmutableMap.of(Messages.DROPIN_PLAYER_NAME, managerName)));
        }

        managerUUIDset.remove(managerUUID);
        return ShopTransactionResult.SUCCESS;
    }

    public ShopTransactionResult setCurrency(Player player, Currency currency) {

        // /If the player is not the owner of the shop return a message to the player
        if (!hasOwnerPermissions(player)) {
            return new ShopTransactionResult(Messages.YOU_ARE_NOT_THE_OWNER_OF_THIS_SHOP);
        }

        if (currency == null) {
            currencyID = null;
        } else {
            currencyID = currency.getId();
        }

        return ShopTransactionResult.SUCCESS;
    }

    public ShopTransactionResult setUnlimitedStock(Player player, boolean bool) {

        unlimitedStock = bool;
        return ShopTransactionResult.SUCCESS;
    }

    public ShopTransactionResult setUnlimitedMoney(Player player, boolean bool) {

        unlimitedMoney = bool;
        return ShopTransactionResult.SUCCESS;
    }

    public ShopTransactionResult setName(Player player, String name) {

        // /If the player is not the owner of the shop return a message to the player
        if (!hasRenterPermissions(player)) {
            return new ShopTransactionResult(Messages.YOU_ARE_NOT_THE_OWNER_OF_THIS_SHOP);
        }

        this.name = name;
        return ShopTransactionResult.SUCCESS;
    }

    public ShopTransactionResult setPrice(Player player, double price) {

        // If the player is not the owner of the shop return a message to the player
        if (!hasOwnerPermissions(player)) {
            return new ShopTransactionResult(Messages.YOU_ARE_NOT_THE_OWNER_OF_THIS_SHOP);
        }

        if (price != -1 && price < 0) {
            return new ShopTransactionResult(Messages.INVALID_SHOP_SELL_PRICE);
        }

        if (isBeingRented()) {
            return new ShopTransactionResult(Messages.SET_PRICE_WHILE_RENTED_REJECT);
        }

        if (isForRent()) {
            return new ShopTransactionResult(Messages.SET_PRICE_FOR_RENT_REJECT);
        }

        this.price = price;
        return ShopTransactionResult.SUCCESS;
    }

    public ShopTransactionResult setRent(Player player, double rent) {

        // If the player is not the owner of the shop return a message to the player
        if (!hasOwnerPermissions(player)) {
            return new ShopTransactionResult(Messages.YOU_ARE_NOT_THE_OWNER_OF_THIS_SHOP);
        }

        if (rent != -1 && rent < 0) {
            return new ShopTransactionResult(Messages.INVALID_SHOP_RENT_PRICE);
        }

        if (isBeingRented()) {
            return new ShopTransactionResult(Messages.SET_RENT_WHILE_RENTED_REJECT);
        }

        if (isForSale()) {
            return new ShopTransactionResult(Messages.SET_RENT_FOR_SALE_REJECT);
        }

        this.rent = rent;
        return ShopTransactionResult.SUCCESS;
    }

    public ShopTransactionResult buyShop(Player player) {

        if (!isForSale()) {
            return new ShopTransactionResult(Messages.THIS_SHOP_IS_NOT_FOR_SALE);
        }

        // Transfer funds from the purchaser to the owner
        EconManager manager = EconManager.getInstance();
        Optional<UniqueAccount> playerAccountOptional = manager.getOrCreateAccount(player.getUniqueId());
        Optional<UniqueAccount> ownerAccountOptional = manager.getOrCreateAccount(ownerUUID);

        TransactionResult result =
                playerAccountOptional.get()
                        .transfer(ownerAccountOptional.get(), getShopCurrency(), BigDecimal.valueOf(price), Cause.of(NamedCause.notifier(this)));

        if(result.getResult() == ResultType.SUCCESS) {
            ownerUUID = player.getUniqueId();
            price = -1;
            rent = -1;
            managerUUIDset.clear();
            return ShopTransactionResult.SUCCESS;
        } else {
            return new ShopTransactionResult(Messages.YOU_DON_T_HAVE_ENOUGH_FUNDS);
        }
    }

    public ShopTransactionResult rentShop(Player player, BigDecimal amount) {

        if (!isForRent() && !player.getUniqueId().equals(renterUUID)) {
            return new ShopTransactionResult(Messages.THIS_SHOP_IS_NOT_FOR_RENT);
        }

        long duration = amount.divide(BigDecimal.valueOf(rent), BigDecimal.ROUND_FLOOR).longValue();

        if (duration < 1) {
            return new ShopTransactionResult(Messages.YOU_DON_T_HAVE_ENOUGH_FUNDS);
        }

        // Transfer funds from the purchaser to the owner
        EconManager manager = EconManager.getInstance();
        TransactionResult result;
        Optional<UniqueAccount> renterAccountOptional = manager.getOrCreateAccount(player.getUniqueId());
        if (ownerUUID != null) {
            Optional<UniqueAccount> ownerAccountOptional = manager.getOrCreateAccount(ownerUUID);

            result =
                    renterAccountOptional.get()
                            .transfer(ownerAccountOptional.get(), getShopCurrency(), BigDecimal.valueOf(rent * duration), Cause.of(NamedCause.notifier(this)));

        } else {
            result = renterAccountOptional.get().withdraw(getShopCurrency(), BigDecimal.valueOf(rent * duration), Cause.of(NamedCause.notifier(this)));
        }
        if (result.getResult() == ResultType.SUCCESS) {
            ShopRentManager.getInstance().rentShop(this, duration);
            renterUUID = player.getUniqueId();
            return ShopTransactionResult.SUCCESS;
        } else {
            return new ShopTransactionResult(Messages.YOU_DON_T_HAVE_ENOUGH_FUNDS);
        }
    }

    public void rentExpire(boolean transferFunds, boolean clearItems) {
        managerUUIDset.clear();

        //Transfer shop's balance to renter on rent expire
        if (transferFunds) {
            EconManager manager = EconManager.getInstance();
            Optional<UniqueAccount> renterAccountOptional = manager.getOrCreateAccount(renterUUID);
            Optional<UniqueAccount> shopAccountOptional = manager.getOrCreateAccount(getUUID());

            if (renterAccountOptional.isPresent() && shopAccountOptional.isPresent()) {
                UniqueAccount renterAccount = renterAccountOptional.get();
                UniqueAccount shopAccount = shopAccountOptional.get();

                shopAccount.transfer(renterAccount,
                        getShopCurrency(),
                        shopAccount.getBalance(getShopCurrency()),
                        Cause.of(NamedCause.notifier(this)));
            }
        }

        if (clearItems) {
            items.clear();
        }

        renterUUID = null;
    }

    public ShopTransactionResult showBuyView(Player player) {
        ShopViewUtils.sendShopBuyView(player, this);

        return ShopTransactionResult.SUCCESS;
    }

    public ShopTransactionResult showManagerView(Player player) {

        if (!hasManagerPermissions(player)) {
            return new ShopTransactionResult(Messages.YOU_ARE_NOT_A_MANAGER_OF_THIS_SHOP);
        }

        ShopViewUtils.sendShopManagerView(player, this);
        return ShopTransactionResult.SUCCESS;
    }


    public ShopTransactionResult showOwnerView(Player player) {

        if (!hasRenterPermissions(player)) {
            return new ShopTransactionResult(Messages.YOU_ARE_NOT_THE_OWNER_OF_THIS_SHOP);
        }

        ShopViewUtils.sendShopOwnerView(player, this);
        return ShopTransactionResult.SUCCESS;
    }

    public ShopTransactionResult depositFunds(Player player, BigDecimal amount) {
        //If the player is not the owner of the shop return a message to the player
        if (!hasRenterPermissions(player)) {
            return new ShopTransactionResult(Messages.YOU_ARE_NOT_THE_OWNER_OF_THIS_SHOP);
        }
        EconManager manager = EconManager.getInstance();

        Optional<UniqueAccount> playerAccountOptional = manager.getOrCreateAccount(player.getUniqueId());
        Optional<UniqueAccount> shopAccountOptional = manager.getOrCreateAccount(getUUID());

        if (playerAccountOptional.isPresent() && shopAccountOptional.isPresent()) {
            shopAccountOptional.get().transfer(playerAccountOptional.get(), getShopCurrency(), amount, Cause.of(NamedCause.notifier(this)));
        }

        return ShopTransactionResult.SUCCESS;
    }

    public ShopTransactionResult withdrawFunds(Player player, BigDecimal amount) {
        //If the player is not the owner of the shop return a message to the player
        if (!hasRenterPermissions(player)) {
            return new ShopTransactionResult(Messages.YOU_ARE_NOT_THE_OWNER_OF_THIS_SHOP);
        }
        EconManager manager = EconManager.getInstance();

        Optional<UniqueAccount> playerAccountOptional = manager.getOrCreateAccount(player.getUniqueId());
        Optional<UniqueAccount> shopAccountOptional = manager.getOrCreateAccount(getUUID());

        if (playerAccountOptional.isPresent() && shopAccountOptional.isPresent()) {
            playerAccountOptional.get().transfer(shopAccountOptional.get(), getShopCurrency(), amount, Cause.of(NamedCause.notifier(this)));
        }

        return ShopTransactionResult.SUCCESS;
    }

    public BigDecimal getBalance() {

        EconManager manager = EconManager.getInstance();

        Optional<UniqueAccount> shopAccountOptional = manager.getOrCreateAccount(getUUID());

        return shopAccountOptional.map(uniqueAccount -> uniqueAccount.getBalance(getShopCurrency())).orElse(BigDecimal.ZERO);
    }

    public boolean hasOwnerPermissions(Player player) {
        if (player.hasPermission(Permissions.PLAYERSHOPSRPG_BYPASS_OWNER)) {
            return true;
        }
        return ownerUUID != null && ownerUUID.equals(player.getUniqueId());
    }

    public boolean hasRenterPermissions(Player player) {
        if (renterUUID != null) { //If the shop is being rented check the renter
            return renterUUID.equals(player.getUniqueId()) || player.hasPermission(Permissions.PLAYERSHOPSRPG_BYPASS_OWNER);
        } else { // The shop is not being rented, use the owner instead
            return hasOwnerPermissions(player);
        }
    }

    public boolean hasManagerPermissions(Player player) {
        return managerUUIDset.contains(player.getUniqueId()) || player.hasPermission(Permissions.PLAYERSHOPSRPG_BYPASS_MANAGER) ||
                hasRenterPermissions(player);
    }

    public String getCurrencyID() {
        return currencyID;
    }

    public String getName() {
        return name;
    }

    public UUID getUUID() {
        return shopUUID;
    }

    public Map<UUID, ShopItem> getItems() {
        return items;
    }

    public Optional<ShopItem> getShopItem(UUID itemUUID) {
        return Optional.ofNullable(items.get(itemUUID));
    }

    public Optional<UUID> getOwnerUUID() {
        return Optional.ofNullable(ownerUUID);
    }

    public UUID getRenterUUID() {
        return renterUUID;
    }

    public Set<UUID> getManagerUUIDSet() {
        return managerUUIDset;
    }

    public boolean isUnlimitedMoney() {
        return unlimitedMoney;
    }

    public boolean isUnlimitedStock() {
        return unlimitedStock;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public double getPrice() { return price; }

    public double getRent() { return rent; }

    public Currency getShopCurrency() {

        EconManager econManager = EconManager.getInstance();

        if (currencyID == null) {
            return econManager.getDefaultCurrency();
        } else {
            return econManager.getCurrency(currencyID).orElse(econManager.getDefaultCurrency());
        }
    }

    public boolean isForSale() {
        return getPrice() >= 0;
    }

    public boolean isForRent() {
        return getRenterUUID() == null && getRent() != -1;
    }

    public boolean isBeingRented() {
        return getRenterUUID() != null;
    }

    public boolean isEmpty() {
        for (ShopItem item : getItems().values()) {
            if (item.getItemAmount() != 0) {
                return false;
            }
        }
        return true;
    }
}