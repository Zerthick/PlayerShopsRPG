package io.github.zerthick.playershopsrpg.utils.config.serializers.shop;

import com.google.common.reflect.TypeToken;
import io.github.zerthick.playershopsrpg.shop.Shop;
import io.github.zerthick.playershopsrpg.shop.ShopItem;
import ninja.leaping.configurate.ConfigurationNode;
import ninja.leaping.configurate.objectmapping.ObjectMappingException;
import ninja.leaping.configurate.objectmapping.serialize.TypeSerializer;

import java.util.*;

public class ShopSerializer implements TypeSerializer<Shop> {

    @Override
    public Shop deserialize(TypeToken<?> type, ConfigurationNode value) throws ObjectMappingException {
        UUID shopUUID = value.getNode("shopUUID").getValue(TypeToken.of(UUID.class));
        String name = value.getNode("shopName").getString();
        UUID ownerUUID = value.getNode("ownerUUID").getValue(TypeToken.of(UUID.class));
        Set<UUID> managerUUIDset = new HashSet<>();
        managerUUIDset.addAll(value.getNode("managerSet").getList(TypeToken.of(UUID.class)));
        List<ShopItem> items = value.getNode("items").getList(TypeToken.of(ShopItem.class));
        boolean unlimitedMoney = value.getNode("unlimitedMoney").getBoolean();
        boolean unlimitedStock = value.getNode("unlimitedStock").getBoolean();

        return new Shop(shopUUID, name, ownerUUID, managerUUIDset, items, unlimitedMoney, unlimitedStock);
    }

    @Override
    public void serialize(TypeToken<?> type, Shop obj, ConfigurationNode value) throws ObjectMappingException {
        value.getNode("shopUUID").setValue(TypeToken.of(UUID.class), obj.getUUID());
        value.getNode("shopName").setValue(obj.getName());
        value.getNode("ownerUUID").setValue(TypeToken.of(UUID.class), obj.getOwnerUUID());
        Set<UUID> managerUUIDSet = obj.getManagerUUIDset();
        List<UUID> managerUUIDList = new ArrayList<>();
        managerUUIDList.addAll(managerUUIDSet);
        value.getNode("managerSet").setValue(new TypeToken<List<UUID>>() {
        }, managerUUIDList);
        value.getNode("items").setValue(new TypeToken<List<ShopItem>>() {
        }, obj.getItems());
        value.getNode("unlimitedMoney").setValue(obj.isUnlimitedMoney());
        value.getNode("unlimitedStock").setValue(obj.isUnlimitedStock());
    }
}
