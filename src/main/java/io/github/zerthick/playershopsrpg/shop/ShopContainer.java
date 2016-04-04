package io.github.zerthick.playershopsrpg.shop;

import com.flowpowered.math.vector.Vector3i;
import io.github.zerthick.playershopsrpg.region.Region;

public class ShopContainer {

    private Shop shop;
    private Region shopRegion;

    public ShopContainer(Shop shop, Region shopRegion) {
        this.shop = shop;
        this.shopRegion = shopRegion;
    }

    public boolean isShop(Vector3i location) {
       return shopRegion.contains(location);
    }

    public Shop getShop() {
        return shop;
    }

    public Region getShopRegion() {
        return shopRegion;
    }
}
