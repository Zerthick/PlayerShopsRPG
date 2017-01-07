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
import org.spongepowered.api.item.inventory.entity.HumanInventory;
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
    private List<ShopItem> items;
    private boolean unlimitedMoney;
    private boolean unlimitedStock;
    private String type;
    private double price;
    private double rent;

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
        items = new ArrayList<>();
        unlimitedMoney = false;
        unlimitedStock = false;
        type = "";
        price = -1;
        rent = -1;
    }

    public Shop(UUID shopUUID, String name, UUID ownerUUID, UUID renterUUID, Set<UUID> managerUUIDset, List<ShopItem> items, boolean unlimitedMoney, boolean unlimitedStock, String type, double price, double rent) {
        this.shopUUID = shopUUID;
        this.name = name;
        this.ownerUUID = ownerUUID;
        this.renterUUID = renterUUID;
        this.managerUUIDset = new HashSet<>(managerUUIDset);
        this.items = new ArrayList<>(items);
        this.unlimitedMoney = unlimitedMoney;
        this.unlimitedStock = unlimitedStock;
        this.type = type;
        this.price = price;
        this.rent = rent;
    }

    public ShopTransactionResult createItem(Player player, ItemStack itemStack) {

        //If the player is not the owner of the shop return a message to the player
        if (!hasRenterPermissions(player)) {
            return new ShopTransactionResult(Messages.YOU_ARE_NOT_THE_OWNER_OF_THIS_SHOP);
        }

        //If the item already exists return a message to the player
        for (ShopItem item : items) {
            if (InventoryUtils.itemStackEqualsIgnoreSize(item.getItemStack(), itemStack)) {
                return new ShopTransactionResult(Messages.THE_SPECIFIED_ITEM_IS_ALREADY_IN_THIS_SHOP);
            }
        }

        //The item is not already in the shop, we need to add it
        ItemStack itemToAdd = itemStack.copy();
        itemToAdd.setQuantity(1);
        ShopItem newShopItem = new ShopItem(itemToAdd.createSnapshot(), 0, -1, -1, -1);
        items.add(newShopItem);
        return ShopTransactionResult.SUCCESS;
    }

    public ShopTransactionResult destroyItem(Player player, int index) {

        //If the player is not the owner of the shop return a message to the player
        if (!hasRenterPermissions(player)) {
            return new ShopTransactionResult(Messages.YOU_ARE_NOT_THE_OWNER_OF_THIS_SHOP);
        }

        //Bounds Check
        if (index >= 0 && index < items.size()) {
            items.remove(index);
            return ShopTransactionResult.SUCCESS;
        }
        return new ShopTransactionResult(Messages.THE_SPECIFIED_ITEM_IS_NOT_IN_THIS_SHOP);
    }

    public ShopTransactionResult buyItem(Player player, int index, int amount) {

        //Amount Bounds Check
        if (amount < 0) {
            return new ShopTransactionResult(Messages.INVALID_ITEM_BUY_AMOUNT);
        }

        //Bounds Check
        if (index >= 0 && index < items.size()) {
            ShopItem item = items.get(index);

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
            if (InventoryUtils.getItemCount((HumanInventory) player.getInventory(), item.getItemStack()) < amount) {
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
                    result = playerAccountOptional.get().deposit(manager.getDefaultCurrency(),
                            BigDecimal.valueOf(amount * item.getItemBuyPrice()),
                            Cause.of(NamedCause.notifier(this)));
                } else {
                    result = shopAccountOptional.get().transfer(playerAccountOptional.get(),
                            manager.getDefaultCurrency(), BigDecimal.valueOf(amount * item.getItemBuyPrice()),
                            Cause.of(NamedCause.notifier(this)));
                }
                if (result.getResult() == ResultType.SUCCESS) {
                    item.setItemAmount(item.getItemAmount() + amount);
                    InventoryUtils.removeItem((HumanInventory) player.getInventory(), item.getItemStack(), amount);
                    return ShopTransactionResult.SUCCESS;
                } else {
                    return new ShopTransactionResult(Messages.processDropins(Messages.SHOP_DOES_NOT_HAVE_ENOUGH_FUNDS,
                            ImmutableMap.of(Messages.DROPIN_SHOP_NAME, getName())));
                }
            }
        }

        return new ShopTransactionResult(Messages.THE_SPECIFIED_ITEM_IS_NOT_IN_THIS_SHOP);
    }

    public ShopTransactionResult sellItem(Player player, int index, int amount) {

        //Amount Bounds Check
        if (amount < 0) {
            return new ShopTransactionResult(Messages.INVALID_ITEM_SELL_AMOUNT);
        }

        //Bounds Check
        if (index >= 0 && index < items.size()) {
            ShopItem item = items.get(index);

            //Check if the shop does not sell this item
            if (item.getItemSellPrice() == -1) {
                return new ShopTransactionResult(Messages.processDropins(Messages.SHOP_DOES_NOT_SELL_ITEM,
                        ImmutableMap.of(Messages.DROPIN_SHOP_NAME, getName(), Messages.DROPIN_ITEM_NAME,
                                InventoryUtils.getItemNamePlain(item.getItemStack()))));
            }

            //Check if the player has enough room otherwise only sell as much as they can hold
            int availableSpace = InventoryUtils.getAvailableSpace((HumanInventory) player.getInventory(), item.getItemStack());
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
                    result = playerAccountOptional.get().withdraw(manager.getDefaultCurrency(),
                            BigDecimal.valueOf(amount * item.getItemSellPrice()),
                            Cause.of(NamedCause.notifier(this)));
                } else {
                    result = playerAccountOptional.get().transfer(shopAccountOptional.get(),
                            manager.getDefaultCurrency(), BigDecimal.valueOf(amount * item.getItemSellPrice()),
                            Cause.of(NamedCause.notifier(this)));
                }
                if (result.getResult() == ResultType.SUCCESS) {
                    item.setItemAmount(item.getItemAmount() - amount);
                    InventoryUtils.addItem((HumanInventory) player.getInventory(), item.getItemStack(), amount);
                    return ShopTransactionResult.SUCCESS;
                } else {
                    return new ShopTransactionResult(Messages.YOU_DON_T_HAVE_ENOUGH_FUNDS);
                }
            }
        }

        return new ShopTransactionResult(Messages.THE_SPECIFIED_ITEM_IS_NOT_IN_THIS_SHOP);
    }

    public ShopTransactionResult addItem(Player player, ItemStack itemStack, int amount) {

        //If the player is not a manager of the shop return a message to the player
        if (!hasManagerPermissions(player)) {
            return new ShopTransactionResult(Messages.YOU_ARE_NOT_A_MANAGER_OF_THIS_SHOP);
        }

        //If the item exists add to it's total to the shopitem and remove it from the player's inventory
        for (ShopItem item : items) {
            if (InventoryUtils.itemStackEqualsIgnoreSize(item.getItemStack(), itemStack)) {

                if ((amount + item.getItemAmount() > item.getItemMaxAmount()) && item.getItemMaxAmount() != -1) {
                    amount = item.getItemMaxAmount() - item.getItemAmount();
                }

                InventoryUtils.removeItem((HumanInventory) player.getInventory(), itemStack, amount);

                item.setItemAmount(item.getItemAmount() + amount);
                return ShopTransactionResult.SUCCESS;
            }
        }

        return new ShopTransactionResult(Messages.THE_SPECIFIED_ITEM_IS_NOT_IN_THIS_SHOP);
    }

    public ShopTransactionResult removeItem(Player player, int index, int amount) {

        //If the player is not a manager of the shop return a message to the player
        if (!hasManagerPermissions(player)) {
            return new ShopTransactionResult(Messages.YOU_ARE_NOT_A_MANAGER_OF_THIS_SHOP);
        }

        //Bounds Check
        if (index >= 0 && index < items.size()) {
            ShopItem item = items.get(index);
            if (amount > item.getItemAmount()) {
                amount = item.getItemAmount();
            }

            InventoryUtils.addItem((HumanInventory) player.getInventory(), item.getItemStack(), amount);

            item.setItemAmount(item.getItemAmount() - amount);
            return ShopTransactionResult.SUCCESS;
        }

        return new ShopTransactionResult(Messages.THE_SPECIFIED_ITEM_IS_NOT_IN_THIS_SHOP);
    }

    public ShopTransactionResult setItemMax(Player player, int index, int amount) {

        //If the player is not a manager of the shop return a message to the player
        if (!hasManagerPermissions(player)) {
            return new ShopTransactionResult(Messages.YOU_ARE_NOT_A_MANAGER_OF_THIS_SHOP);
        }

        //Bounds Check
        if (index >= 0 && index < items.size()) {

            // Check if the new max amount is valid (-1) for infinite
            if (amount >= -1) {
                items.get(index).setItemMaxAmount(amount);
                return ShopTransactionResult.SUCCESS;
            }

            return new ShopTransactionResult(Messages.INVALID_MAX_ITEM_AMOUNT);
        }

        return new ShopTransactionResult(Messages.THE_SPECIFIED_ITEM_IS_NOT_IN_THIS_SHOP);
    }

    public ShopTransactionResult setItemBuyPrice(Player player, int index, double amount) {

        //If the player is not a manager of the shop return a message to the player
        if (!hasManagerPermissions(player)) {
            return new ShopTransactionResult(Messages.YOU_ARE_NOT_A_MANAGER_OF_THIS_SHOP);
        }

        //Bounds Check
        if (index >= 0 && index < items.size()) {

            // Check if the new max amount is valid (-1) for infinite
            if (amount >= -1) {
                items.get(index).setItemBuyPrice(amount);
                return ShopTransactionResult.SUCCESS;
            }

            return new ShopTransactionResult(Messages.INVALID_ITEM_BUY_PRICE);
        }

        return new ShopTransactionResult(Messages.THE_SPECIFIED_ITEM_IS_NOT_IN_THIS_SHOP);
    }

    public ShopTransactionResult setItemSellPrice(Player player, int index, double amount) {

        //If the player is not a manager of the shop return a message to the player
        if (!hasManagerPermissions(player)) {
            return new ShopTransactionResult(Messages.YOU_ARE_NOT_A_MANAGER_OF_THIS_SHOP);
        }

        //Bounds Check
        if (index >= 0 && index < items.size()) {

            // Check if the new max amount is valid (-1) for infinite
            if (amount >= -1) {
                items.get(index).setItemSellPrice(amount);
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
                        .transfer(ownerAccountOptional.get(), manager.getDefaultCurrency(), BigDecimal.valueOf(price), Cause.of(NamedCause.notifier(this)));

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
        Optional<UniqueAccount> renterAccountOptional = manager.getOrCreateAccount(player.getUniqueId());
        Optional<UniqueAccount> ownerAccountOptional = manager.getOrCreateAccount(ownerUUID);

        TransactionResult result =
                renterAccountOptional.get()
                        .transfer(ownerAccountOptional.get(), manager.getDefaultCurrency(), BigDecimal.valueOf(price * duration), Cause.of(NamedCause.notifier(this)));

        if (result.getResult() == ResultType.SUCCESS) {
            ShopRentManager.getInstance().rentShop(this, duration);
            renterUUID = player.getUniqueId();
            price = -1;
            managerUUIDset.clear();

            return ShopTransactionResult.SUCCESS;
        } else {
            return new ShopTransactionResult(Messages.YOU_DON_T_HAVE_ENOUGH_FUNDS);
        }
    }

    public void rentExpire() {
        managerUUIDset.clear();
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
            shopAccountOptional.get().transfer(playerAccountOptional.get(), manager.getDefaultCurrency(), amount, Cause.of(NamedCause.notifier(this)));
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
            playerAccountOptional.get().transfer(shopAccountOptional.get(), manager.getDefaultCurrency(), amount, Cause.of(NamedCause.notifier(this)));
        }

        return ShopTransactionResult.SUCCESS;
    }

    public BigDecimal getBalance() {

        EconManager manager = EconManager.getInstance();

        Optional<UniqueAccount> shopAccountOptional = manager.getOrCreateAccount(getUUID());

        return shopAccountOptional.map(uniqueAccount -> uniqueAccount.getBalance(manager.getDefaultCurrency())).orElse(BigDecimal.ZERO);
    }

    public boolean hasOwnerPermissions(Player player) {
        return ownerUUID.equals(player.getUniqueId()) || player.hasPermission(Permissions.PLAYERSHOPSRPG_BYPASS_OWNER);
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

    public String getName() {
        return name;
    }

    public UUID getUUID() {
        return shopUUID;
    }

    public List<ShopItem> getItems() {
        return items;
    }

    public UUID getOwnerUUID() {
        return ownerUUID;
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

    public boolean isForSale() {
        return getPrice() >= 0;
    }

    public boolean isForRent() {
        return getRenterUUID() == null && getRent() != -1;
    }

    public boolean isEmpty() {
        for (ShopItem item : getItems()) {
            if (item.getItemAmount() != 0) {
                return false;
            }
        }
        return true;
    }
}