package io.github.zerthick.playershopsrpg.utils.inventory;

import org.spongepowered.api.data.key.Keys;
import org.spongepowered.api.item.inventory.Inventory;
import org.spongepowered.api.item.inventory.ItemStack;
import org.spongepowered.api.item.inventory.ItemStackComparators;
import org.spongepowered.api.text.Text;

import java.util.Comparator;
import java.util.Optional;

public class InventoryUtils {

    public static int getItemCount(Inventory inventory, ItemStack itemStack) {

        ItemStack copy = itemStack.copy();
        copy.setQuantity(-1);

        return inventory.query(copy).totalItems();
    }

    public static int getAvailableSpace(Inventory inventory, ItemStack itemStack) {

        int total = 0;

        for (Inventory slot : inventory.slots()) {
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

    public static int addItem(Inventory inventory, ItemStack itemStack, int amount) {

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

    public static int removeItem(Inventory inventory, ItemStack itemStack, int amount) {

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

        Comparator<ItemStack> typeComparator = ItemStackComparators.TYPE;
        Comparator<ItemStack> dataComparator = ItemStackComparators.ITEM_DATA;
        Comparator<ItemStack> propertiesComparator = ItemStackComparators.PROPERTIES;

        return (typeComparator.compare(o1, o2) == 0) && (dataComparator.compare(o1, o2) == 0)
                && (propertiesComparator.compare(o1, o2) == 0) && (getItemName(o1).equals(getItemName(o2)));
    }

    public static Text getItemName(ItemStack itemStack) {
        return itemStack.get(Keys.DISPLAY_NAME).orElse(Text.of(itemStack.getTranslation().get()));
    }
}
