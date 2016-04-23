package io.github.zerthick.playershopsrpg.shop.type;

import org.spongepowered.api.item.inventory.ItemStack;

import java.util.Set;

public class ShopType {

    private Set<String> allowedItems;

    public ShopType(Set<String> allowedItems) {
        this.allowedItems = allowedItems;
    }

    public boolean isAllowedItem(ItemStack itemStack) {
        return allowedItems.contains(itemStack.getItem().getId());
    }

    public Set<String> getAllowedItems() {
        return allowedItems;
    }
}
