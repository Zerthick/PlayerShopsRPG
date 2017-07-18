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

package io.github.zerthick.playershopsrpg.utils.inventory;

import org.spongepowered.api.data.key.Keys;
import org.spongepowered.api.item.inventory.Inventory;
import org.spongepowered.api.item.inventory.ItemStack;
import org.spongepowered.api.item.inventory.entity.PlayerInventory;
import org.spongepowered.api.text.Text;

import java.util.Optional;

public class InventoryUtils {

    public static int getItemCount(PlayerInventory inventory, ItemStack itemStack) {

        ItemStack copy = itemStack.copy();
        copy.setQuantity(-1);

        return inventory.query(copy).totalItems();
    }

    public static int getAvailableSpace(PlayerInventory inventory, ItemStack itemStack) {

        int total = 0;

        for (Inventory slot : inventory.getMain().slots()) {
            Optional<ItemStack> itemStackOptional = slot.peek();
            if (itemStackOptional.isPresent()) {
                if (itemStackEqualsIgnoreSize(itemStack, itemStackOptional.get())) {
                    total += itemStack.getMaxStackQuantity() - itemStackOptional.get().getQuantity();
                }
            } else {
                total += itemStack.getMaxStackQuantity();
            }
        }

        for (Inventory slot : inventory.getHotbar().slots()) {
            Optional<ItemStack> itemStackOptional = slot.peek();
            if (itemStackOptional.isPresent()) {
                if (itemStackEqualsIgnoreSize(itemStack, itemStackOptional.get())) {
                    total += itemStack.getMaxStackQuantity() - itemStackOptional.get().getQuantity();
                }
            } else {
                total += itemStack.getMaxStackQuantity();
            }
        }

        return total;
    }

    public static int addItem(PlayerInventory inventory, ItemStack itemStack, int amount) {

        int overflow = 0;
        int total = amount;

        int availableSpace = getAvailableSpace(inventory, itemStack);
        if (amount > availableSpace) {
            overflow = amount - availableSpace;
            total = availableSpace;
        }

        int maxStackQuantity = itemStack.getMaxStackQuantity();
        ItemStack copy = itemStack.copy();

        while (total > 0) {
            if (total > maxStackQuantity) {
                copy.setQuantity(maxStackQuantity);
                total -= maxStackQuantity;
            } else {
                copy.setQuantity(total);
                total = 0;
            }
            inventory.offer(copy);
        }
        return overflow;
    }

    public static int removeItem(PlayerInventory inventory, ItemStack itemStack, int amount) {

        int underflow = 0;
        int total = amount;

        int availableItems = getItemCount(inventory, itemStack);
        if (amount > availableItems) {
            underflow = amount - availableItems;
            total = availableItems;
        }

        ItemStack copy = itemStack.copy();
        copy.setQuantity(-1);
        while (total > 0) {
            if (total > copy.getMaxStackQuantity()) {
                inventory.query(copy).poll(copy.getMaxStackQuantity());
                total -= copy.getMaxStackQuantity();
            } else {
                inventory.query(copy).poll(total);
                total = 0;
            }
        }
        return underflow;
    }

    public static boolean itemStackEqualsIgnoreSize(ItemStack o1, ItemStack o2) {

        ItemStack copy1 = o1.copy();
        ItemStack copy2 = o2.copy();

        copy1.setQuantity(1);
        copy2.setQuantity(1);

        return copy1.equalTo(copy2);
    }

    public static Text getItemName(ItemStack itemStack) {
        return itemStack.get(Keys.DISPLAY_NAME).orElse(Text.of(itemStack.getTranslation()));
    }

    public static String getItemNamePlain(ItemStack itemStack) {
        return getItemName(itemStack).toPlain();
    }
}
