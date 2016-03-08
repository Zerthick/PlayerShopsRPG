package io.github.zerthick.playershopsrpg.shop;

import com.flowpowered.math.vector.Vector3i;
import io.github.zerthick.playershopsrpg.region.Region;

public class ShopContainer {
    private Shop shop;
    private Region shopRegion;

    public boolean isShop( Vector3i location){
       return shopRegion.contains(location);
    }

    public Shop getShop() {
        return shop;
    }
}
