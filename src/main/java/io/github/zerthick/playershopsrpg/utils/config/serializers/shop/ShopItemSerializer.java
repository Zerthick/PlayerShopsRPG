package io.github.zerthick.playershopsrpg.utils.config.serializers.shop;

import com.google.common.reflect.TypeToken;
import io.github.zerthick.playershopsrpg.shop.ShopItem;
import ninja.leaping.configurate.ConfigurationNode;
import ninja.leaping.configurate.objectmapping.ObjectMappingException;
import ninja.leaping.configurate.objectmapping.serialize.TypeSerializer;
import org.spongepowered.api.item.inventory.ItemStack;

public class ShopItemSerializer implements TypeSerializer<ShopItem> {

    @Override
    public ShopItem deserialize(TypeToken<?> type, ConfigurationNode value) throws ObjectMappingException {

        ItemStack itemStack = value.getNode("itemStack").getValue(TypeToken.of(ItemStack.class));
        int itemAmount = value.getNode("itemAmount").getInt();
        int itemMaxAmount = value.getNode("itemMaxAmount").getInt();
        double itemBuyPrice = value.getNode("itemBuyPrice").getDouble();
        double itemSellPrice = value.getNode("itemSellPrice").getDouble();

        return new ShopItem(itemStack, itemAmount, itemMaxAmount, itemBuyPrice, itemSellPrice);
    }

    @Override
    public void serialize(TypeToken<?> type, ShopItem obj, ConfigurationNode value) throws ObjectMappingException {
        value.getNode("itemStack").setValue(TypeToken.of(ItemStack.class), obj.getItemStack());
        value.getNode("itemAmount").setValue(obj.getItemAmount());
        value.getNode("itemMaxAmount").setValue(obj.getItemMaxAmount());
        value.getNode("itemBuyPrice").setValue(obj.getItemBuyPrice());
        value.getNode("itemSellPrice").setValue(obj.getItemSellPrice());
    }
}
