package io.github.zerthick.playershopsrpg.shop;

import ninja.leaping.configurate.objectmapping.Setting;
import ninja.leaping.configurate.objectmapping.serialize.ConfigSerializable;
import org.spongepowered.api.item.inventory.ItemStack;

@ConfigSerializable
public class ShopItem {

    @Setting
    private final ItemStack itemStack;
    @Setting
    private int itemAmount;
    @Setting
    private int itemMaxAmount;
    @Setting
    private double itemBuyPrice;
    @Setting
    private double itemSellPrice;

    public ShopItem(ItemStack itemStack, int itemAmount, int itemMaxAmount, double itemBuyPrice, double itemSellPrice) {
        this.itemStack = itemStack;
        this.itemAmount = itemAmount;
        this.itemMaxAmount = itemMaxAmount;
        this.itemBuyPrice = itemBuyPrice;
        this.itemSellPrice = itemSellPrice;
    }

    public ItemStack getItemStack() {
        return itemStack;
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
