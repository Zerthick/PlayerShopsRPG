package io.github.zerthick.playershopsrpg.shop;

import io.github.zerthick.playershopsrpg.utils.inventory.InventoryUtils;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.item.inventory.ItemStack;

import java.util.*;

public class Shop {

    private final UUID shopUUID;
    private String name;
    private UUID ownerUUID;
    private Set<UUID> managerUUIDset;
    private List<ShopItem> items;
    private boolean unlimitedMoney;
    private boolean unlimitedStock;

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
    }

    /**
     * Full Constructor, used only for Object Serialization/Deserialization
     *
     * @param shopUUID The UUID of the shop.
     * @param name The name of the shop.
     * @param ownerUUID The UUID of the owner player.
     * @param managerUUIDset The UUID set of the manager players.
     * @param items The ShopItem set of the items contained within the shop.
     * @param unlimitedMoney Whether or not the shop can buy items without spending money.
     * @param unlimitedStock Whether or not the shop never runs out of items to sell.
     */
    public Shop(UUID shopUUID, String name, UUID ownerUUID, Set<UUID> managerUUIDset, List<ShopItem> items, boolean unlimitedMoney, boolean unlimitedStock) {
        this.shopUUID = shopUUID;
        this.name = name;
        this.ownerUUID = ownerUUID;
        this.managerUUIDset = managerUUIDset;
        this.items = items;
        this.unlimitedMoney = unlimitedMoney;
        this.unlimitedStock = unlimitedStock;
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

        return ShopTransactionResult.EMPTY;
    }

    public ShopTransactionResult sellItem(Player player, int index, int amount) {

        return ShopTransactionResult.EMPTY;
    }

    public ShopTransactionResult addItem(Player player, ItemStack itemStack, int amount) {

        //If the player is not a manager of the shop return a message to the player
        if (!hasManagerPermissions(player)) {
            return new ShopTransactionResult("You are not a manager of this shop!");
        }

        //If the item already exists add to it's total to the shopitem and remove it from the player's inventory
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

        //If the item already exists add to it's total to the player and empty the itemstack

        ShopItem item = items.get(index);
            if (InventoryUtils.itemStackEqualsIgnoreSize(item.getItemStack(), item.getItemStack())) {

                if (amount > item.getItemAmount()){
                    amount = item.getItemAmount();
                }

                InventoryUtils.addItem(player.getInventory(), item.getItemStack(), amount);

                item.setItemAmount(item.getItemAmount() - amount);
                return ShopTransactionResult.SUCCESS;
            }

        return new ShopTransactionResult("The specified item is not in the shop!");
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
        ShopItemUtils.sendShopBuyView(player, this, unlimitedStock);

        return ShopTransactionResult.SUCCESS;
    }

    public boolean hasOwnerPermissions(Player player) {
        return ownerUUID.equals(player.getUniqueId());
    }

    public boolean hasManagerPermissions(Player player) {
        return managerUUIDset.contains(player.getUniqueId()) || hasOwnerPermissions(player);
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
}
