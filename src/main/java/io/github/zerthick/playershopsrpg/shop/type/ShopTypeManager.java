package io.github.zerthick.playershopsrpg.shop.type;

import org.spongepowered.api.item.inventory.ItemStack;

import java.util.Map;
import java.util.Set;

public class ShopTypeManager {

    private Map<String, ShopType> shopTypes;

    public ShopTypeManager(Map<String, ShopType> shopTypes) {
        this.shopTypes = shopTypes;
    }

    public Map<String, ShopType> getShopTypes() {
        return shopTypes;
    }

    public boolean isItemStackAllowed(ItemStack itemStack, String shopType) {

        if (shopType.equals("")) {
            return true;
        } else {
            if (isShopType(shopType)) {
                return shopTypes.get(shopType).isAllowedItem(itemStack);
            }
        }

        return false;
    }

    public boolean isShopType(String shopType) {
        return shopTypes.containsKey(shopType);
    }

    public Set<String> getTypes() {
        return shopTypes.keySet();
    }
}
