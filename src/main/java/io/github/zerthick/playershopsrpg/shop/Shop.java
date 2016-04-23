package io.github.zerthick.playershopsrpg.shop;

import io.github.zerthick.playershopsrpg.utils.econ.EconManager;
import io.github.zerthick.playershopsrpg.utils.inventory.InventoryUtils;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.event.cause.Cause;
import org.spongepowered.api.event.cause.NamedCause;
import org.spongepowered.api.item.inventory.ItemStack;
import org.spongepowered.api.service.economy.account.UniqueAccount;
import org.spongepowered.api.service.economy.transaction.ResultType;
import org.spongepowered.api.service.economy.transaction.TransactionResult;

import java.math.BigDecimal;
import java.util.*;

public class Shop {

    private final UUID shopUUID;
    private String name;
    private UUID ownerUUID;
    private Set<UUID> managerUUIDset;
    private List<ShopItem> items;
    private boolean unlimitedMoney;
    private boolean unlimitedStock;
    private String type;

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
        managerUUIDset = new HashSet<>();
        items = new ArrayList<>();
        unlimitedMoney = false;
        unlimitedStock = false;
        type = "";
    }

    public Shop(UUID shopUUID, String name, UUID ownerUUID, Set<UUID> managerUUIDset, List<ShopItem> items, boolean unlimitedMoney, boolean unlimitedStock, String type) {
        this.shopUUID = shopUUID;
        this.name = name;
        this.ownerUUID = ownerUUID;
        this.managerUUIDset = managerUUIDset;
        this.items = items;
        this.unlimitedMoney = unlimitedMoney;
        this.unlimitedStock = unlimitedStock;
        this.type = type;
    }

    public ShopTransactionResult createItem(Player player, ItemStack itemStack) {

        //If the player is not the owner of the shop return a message to the player
        if (!hasOwnerPermissions(player)) {
            return new ShopTransactionResult("You are not the owner of this shop!");
        }

        //If the item already exists return a message to the player
        for (ShopItem item : items) {
            if (InventoryUtils.itemStackEqualsIgnoreSize(item.getItemStack(), itemStack)) {
                return new ShopTransactionResult("The specified item is already in this shop!");
            }
        }

        //The item is not already in the shop, we need to add it
        ItemStack itemToAdd = itemStack.copy();
        itemToAdd.setQuantity(1);
        ShopItem newShopItem = new ShopItem(itemToAdd, 0, -1, -1, -1);
        items.add(newShopItem);
        return ShopTransactionResult.SUCCESS;
    }

    public ShopTransactionResult destroyItem(Player player, int index) {

        //If the player is not the owner of the shop return a message to the player
        if (!hasOwnerPermissions(player)) {
            return new ShopTransactionResult("You are not the owner of this shop!");
        }

        //Bounds Check
        if (index >= 0 && index < items.size()) {
            items.remove(index);
            return ShopTransactionResult.SUCCESS;
        }
        return new ShopTransactionResult("The specified item is not in this shop!");
    }

    public ShopTransactionResult buyItem(Player player, int index, int amount) {

        //Bounds Check
        if (index >= 0 && index < items.size()) {
            ShopItem item = items.get(index);

            //Check if the shop does not buy this item
            if (item.getItemBuyPrice() == -1) {
                return new ShopTransactionResult(getName() + " does not buy " + InventoryUtils.getItemName(item.getItemStack()).toPlain() + "!");
            }

            //Check if the shop has enough room otherwise only buy as much as we can hold
            if ((amount + item.getItemAmount() > item.getItemMaxAmount()) && item.getItemMaxAmount() != -1) {
                amount = item.getItemMaxAmount() - item.getItemAmount();
            }

            //Check if the player has enough of the item in their inventory to sell
            if (InventoryUtils.getItemCount(player.getInventory(), item.getItemStack()) < amount) {
                return new ShopTransactionResult("You dont have " + amount + " " + InventoryUtils.getItemName(item.getItemStack()).toPlain() + "(s)!");
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
                    InventoryUtils.removeItem(player.getInventory(), item.getItemStack(), amount);
                    return ShopTransactionResult.SUCCESS;
                } else {
                    return new ShopTransactionResult(getName() + " doesn't have enough funds!");
                }
            }
        }

        return new ShopTransactionResult("The specified item is not in this shop!");
    }

    public ShopTransactionResult sellItem(Player player, int index, int amount) {

        //Bounds Check
        if (index >= 0 && index < items.size()) {
            ShopItem item = items.get(index);

            //Check if the shop does not sell this item
            if (item.getItemSellPrice() == -1) {
                return new ShopTransactionResult(getName() + " does not sell " + InventoryUtils.getItemName(item.getItemStack()).toPlain() + "!");
            }

            //Check if the player has enough room otherwise only sell as much as they can hold
            int availableSpace = InventoryUtils.getAvailableSpace(player.getInventory(), item.getItemStack());
            if (availableSpace < amount) {
                amount = availableSpace;
            }

            //Check if the shop has enough of the item in their inventory to sell
            if (item.getItemAmount() < amount) {
                return new ShopTransactionResult(getName() + " doesn't have " + amount + " " + InventoryUtils.getItemName(item.getItemStack()).toPlain() + "(s)!");
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
                    InventoryUtils.addItem(player.getInventory(), item.getItemStack(), amount);
                    return ShopTransactionResult.SUCCESS;
                } else {
                    return new ShopTransactionResult("You don't have enough funds!");
                }
            }
        }

        return new ShopTransactionResult("The specified item is not in this shop!");
    }

    public ShopTransactionResult addItem(Player player, ItemStack itemStack, int amount) {

        //If the player is not a manager of the shop return a message to the player
        if (!hasManagerPermissions(player)) {
            return new ShopTransactionResult("You are not a manager of this shop!");
        }

        //If the item exists add to it's total to the shopitem and remove it from the player's inventory
        for (ShopItem item : items) {
            if (InventoryUtils.itemStackEqualsIgnoreSize(item.getItemStack(), itemStack)) {

                if ((amount + item.getItemAmount() > item.getItemMaxAmount()) && item.getItemMaxAmount() != -1) {
                    amount = item.getItemMaxAmount() - item.getItemAmount();
                }

                InventoryUtils.removeItem(player.getInventory(), itemStack, amount);

                item.setItemAmount(item.getItemAmount() + amount);
                return ShopTransactionResult.SUCCESS;
            }
        }

        return new ShopTransactionResult("The specified item is not in this shop!");
    }

    public ShopTransactionResult removeItem(Player player, int index, int amount) {

        //If the player is not a manager of the shop return a message to the player
        if (!hasManagerPermissions(player)) {
            return new ShopTransactionResult("You are not a manager of this shop!");
        }

        //Bounds Check
        if (index >= 0 && index < items.size()) {
            ShopItem item = items.get(index);
            if (amount > item.getItemAmount()) {
                amount = item.getItemAmount();
            }

            InventoryUtils.addItem(player.getInventory(), item.getItemStack(), amount);

            item.setItemAmount(item.getItemAmount() - amount);
            return ShopTransactionResult.SUCCESS;
        }

        return new ShopTransactionResult("The specified item is not in this shop!");
    }

    public ShopTransactionResult setItemMax(Player player, int index, int amount) {

        //If the player is not a manager of the shop return a message to the player
        if (!hasManagerPermissions(player)) {
            return new ShopTransactionResult("You are not a manager of this shop!");
        }

        //Bounds Check
        if (index >= 0 && index < items.size()) {

            // Check if the new max amount is valid (-1) for infinite
            if (amount >= -1) {
                items.get(index).setItemMaxAmount(amount);
                return ShopTransactionResult.SUCCESS;
            }

            return new ShopTransactionResult("The max item amount must be either -1 (for infinite) or a positive number!");
        }

        return new ShopTransactionResult("The specified item is not in this shop!");
    }

    public ShopTransactionResult setItemBuyPrice(Player player, int index, double amount) {

        //If the player is not a manager of the shop return a message to the player
        if (!hasManagerPermissions(player)) {
            return new ShopTransactionResult("You are not a manager of this shop!");
        }

        //Bounds Check
        if (index >= 0 && index < items.size()) {

            // Check if the new max amount is valid (-1) for infinite
            if (amount >= -1) {
                items.get(index).setItemBuyPrice(amount);
                return ShopTransactionResult.SUCCESS;
            }

            return new ShopTransactionResult("The item buy price must be either -1 (for not buying) or a positive number!");
        }

        return new ShopTransactionResult("The specified item is not in this shop!");
    }

    public ShopTransactionResult setItemSellPrice(Player player, int index, double amount) {

        //If the player is not a manager of the shop return a message to the player
        if (!hasManagerPermissions(player)) {
            return new ShopTransactionResult("You are not a manager of this shop!");
        }

        //Bounds Check
        if (index >= 0 && index < items.size()) {

            // Check if the new max amount is valid (-1) for infinite
            if (amount >= -1) {
                items.get(index).setItemSellPrice(amount);
                return ShopTransactionResult.SUCCESS;
            }

            return new ShopTransactionResult("The item sell price must be either -1 (for not selling) or a positive number!");
        }

        return new ShopTransactionResult("The specified item is not in this shop!");
    }

    public ShopTransactionResult setOwner(Player player, UUID ownerUUID) {

        //If the player is not the owner of the shop return a message to the player
        if (!hasOwnerPermissions(player)) {
            return new ShopTransactionResult("You are not the owner of this shop!");
        }

        this.ownerUUID = ownerUUID;
        return ShopTransactionResult.SUCCESS;
    }

    public ShopTransactionResult addManager(Player player, UUID managerUUID) {

        //If the player is not the owner of the shop return a message to the player
        if (!hasOwnerPermissions(player)) {
            return new ShopTransactionResult("You are not the owner of this shop!");
        }

        managerUUIDset.add(managerUUID);
        return ShopTransactionResult.SUCCESS;
    }

    public ShopTransactionResult removeManager(Player player, UUID managerUUID) {

        //If the player is not the owner of the shop return a message to the player
        if (!hasOwnerPermissions(player)) {
            return new ShopTransactionResult("You are not the owner of this shop!");
        }

        if (!managerUUIDset.contains(managerUUID)) {
            return new ShopTransactionResult("The user with the UUID " + managerUUID + " is not a manager of this shop!");
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
        //If the player is not the owner of the shop return a message to the player
        if (!hasOwnerPermissions(player)) {
            return new ShopTransactionResult("You are not the owner of this shop!");
        }

        this.name = name;
        return ShopTransactionResult.SUCCESS;
    }

    public ShopTransactionResult showBuyView(Player player) {
        ShopItemUtils.sendShopBuyView(player, this);

        return ShopTransactionResult.SUCCESS;
    }

    public ShopTransactionResult showManagerView(Player player) {

        if (!hasManagerPermissions(player)) {
            return new ShopTransactionResult("You are not a manager of this shop!");
        }

        ShopItemUtils.sendShopManagerView(player, this);
        return ShopTransactionResult.SUCCESS;
    }

    public ShopTransactionResult showOwnerView(Player player) {

        if (!hasManagerPermissions(player)) {
            return new ShopTransactionResult("You are not the owner of this shop!");
        }

        ShopItemUtils.sendShopOwnerView(player, this);
        return ShopTransactionResult.SUCCESS;
    }

    public ShopTransactionResult depositFunds(Player player, BigDecimal amount) {
        //If the player is not the owner of the shop return a message to the player
        if (!hasOwnerPermissions(player)) {
            return new ShopTransactionResult("You are not the owner of this shop!");
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
        if (!hasOwnerPermissions(player)) {
            return new ShopTransactionResult("You are not the owner of this shop!");
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

        if (shopAccountOptional.isPresent()) {
            return shopAccountOptional.get().getBalance(manager.getDefaultCurrency());
        }
        return BigDecimal.valueOf(0);
    }

    public boolean hasOwnerPermissions(Player player) {
        return ownerUUID.equals(player.getUniqueId()) || player.hasPermission("playershopsrpg.override.owner");
    }

    public boolean hasManagerPermissions(Player player) {
        return managerUUIDset.contains(player.getUniqueId()) || player.hasPermission("playershopsrpg.override.manager") ||
                hasOwnerPermissions(player);
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

    public Set<UUID> getManagerUUIDset() {
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
}