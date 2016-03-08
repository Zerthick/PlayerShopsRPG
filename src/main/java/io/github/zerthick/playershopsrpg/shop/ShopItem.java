package io.github.zerthick.playershopsrpg.shop;

import org.spongepowered.api.item.ItemType;

public class ShopItem {

    private final String itemName;
    private int itemAmount;
    private int itemMaxAmount;
    private double itemBuyPrice;
    private double itemSellPrice;

    public ShopItem(ItemType itemType, int itemAmount, int itemMaxAmount, double itemBuyPrice, double itemSellPrice) {
        this.itemName = itemType.getId();
        this.itemAmount = itemAmount;
        this.itemMaxAmount = itemMaxAmount;
        this.itemBuyPrice = itemBuyPrice;
        this.itemSellPrice = itemSellPrice;
    }

    public String getItemName() {
        return itemName;
    }

    public int getItemAmount() {
        return itemAmount;
    }

    public void setItemAmount(int itemAmount) {
        this.itemAmount = itemAmount;
    }

    public int getItemMaxAmount() {
        return itemMaxAmount;
    }

    public void setItemMaxAmount(int itemMaxAmount) {
        this.itemMaxAmount = itemMaxAmount;
    }

    public double getItemBuyPrice() {
        return itemBuyPrice;
    }

    public void setItemBuyPrice(double itemBuyPrice) {
        this.itemBuyPrice = itemBuyPrice;
    }

    public double getItemSellPrice() {
        return itemSellPrice;
    }

    public void setItemSellPrice(double itemSellPrice) {
        this.itemSellPrice = itemSellPrice;
    }
}
