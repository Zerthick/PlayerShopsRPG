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

import org.spongepowered.api.item.inventory.ItemStack;

public class ShopItem {

    private final ItemStack itemStack;
    private int itemAmount;
    private int itemMaxAmount;
    private double itemBuyPrice;
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
