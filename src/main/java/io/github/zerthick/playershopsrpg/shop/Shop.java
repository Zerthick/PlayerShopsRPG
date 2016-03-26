package io.github.zerthick.playershopsrpg.shop;

import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.item.inventory.ItemStack;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.UUID;

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

        //If the item already exists, just update that item's amount
        for (ShopItem item : items) {
            if (ShopItemUtils.itemStackEqualsIgnoreSize(item.getItemStack(), itemStack)) {
                int oldItemAmount = item.getItemAmount();
                item.setItemAmount(oldItemAmount + itemStack.getQuantity());
                return ShopTransactionResult.SUCCESS;
            }
        }

        //The item is not already in the shop, we need to add it
        ShopItem newShopItem = new ShopItem(itemStack, itemStack.getQuantity(), -1, -1, -1);
        items.add(newShopItem);
        return ShopTransactionResult.SUCCESS;
    }

    public ShopTransactionResult destroyItem(Player player, int index) {

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

    public ShopTransactionResult addItem(Player player, ItemStack item, int amount) {

        return ShopTransactionResult.EMPTY;
    }

    public ShopTransactionResult removeItem(Player player, int index, int amount) {

        return ShopTransactionResult.EMPTY;
    }

    public ShopTransactionResult setItemMax(Player player, int index, int amount) {

        return ShopTransactionResult.EMPTY;
    }

    public ShopTransactionResult setItemBuyPrice(Player player, int index, int amount) {

        return ShopTransactionResult.EMPTY;
    }

    public ShopTransactionResult setItemSellPrice(Player player, int index, int amount) {

        return ShopTransactionResult.EMPTY;
    }

    public ShopTransactionResult setOwner(Player player, Player owner) {

        return ShopTransactionResult.EMPTY;
    }

    public ShopTransactionResult addManager(Player player, Player manager) {

        return ShopTransactionResult.EMPTY;
    }

    public ShopTransactionResult removeManager(Player player, Player manager) {

        return ShopTransactionResult.EMPTY;
    }

    public ShopTransactionResult setUnlimitedStock(Player player, boolean bool) {

        return ShopTransactionResult.EMPTY;
    }

    public ShopTransactionResult setUnlimitedMoney(Player player, boolean bool) {

        return ShopTransactionResult.EMPTY;
    }

    public ShopTransactionResult showBuyView(Player player) {
        ShopItemUtils.sendShopBuyView(player, items, unlimitedStock);

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
}
