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
     * @param name
     * @param ownerUUID
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
     * @param shopUUID
     * @param name
     * @param ownerUUID
     * @param managerUUIDset
     * @param items
     * @param unlimitedMoney
     * @param unlimitedStock
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
