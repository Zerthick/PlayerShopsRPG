package io.github.zerthick.playershopsrpg.utils.config;

import com.google.common.reflect.TypeToken;
import io.github.zerthick.playershopsrpg.region.RectangularRegion;
import io.github.zerthick.playershopsrpg.region.Region;
import io.github.zerthick.playershopsrpg.shop.Shop;
import io.github.zerthick.playershopsrpg.shop.ShopContainer;
import ninja.leaping.configurate.ConfigurationNode;
import ninja.leaping.configurate.objectmapping.ObjectMappingException;
import ninja.leaping.configurate.objectmapping.serialize.TypeSerializer;

public class ShopContainerSerializer implements TypeSerializer<ShopContainer> {

    @Override
    public ShopContainer deserialize(TypeToken<?> type, ConfigurationNode value) throws ObjectMappingException {

        Shop shop = value.getNode("shop").getValue(TypeToken.of(Shop.class));
        Region region = value.getNode("region").getValue(TypeToken.of(RectangularRegion.class));

        return new ShopContainer(shop, region);
    }

    @Override
    public void serialize(TypeToken<?> type, ShopContainer obj, ConfigurationNode value) throws ObjectMappingException {
        value.getNode("shop").setValue(TypeToken.of(Shop.class), obj.getShop());
        value.getNode("region").setValue(TypeToken.of(RectangularRegion.class), (RectangularRegion) obj.getShopRegion());
    }
}
