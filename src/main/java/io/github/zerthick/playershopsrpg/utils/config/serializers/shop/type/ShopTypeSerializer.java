package io.github.zerthick.playershopsrpg.utils.config.serializers.shop.type;

import com.google.common.reflect.TypeToken;
import io.github.zerthick.playershopsrpg.shop.type.ShopType;
import ninja.leaping.configurate.ConfigurationNode;
import ninja.leaping.configurate.objectmapping.ObjectMappingException;
import ninja.leaping.configurate.objectmapping.serialize.TypeSerializer;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

public class ShopTypeSerializer implements TypeSerializer<ShopType> {

    @Override
    public ShopType deserialize(TypeToken<?> type, ConfigurationNode value) throws ObjectMappingException {

        Set<String> allowedItems = value.getNode("items").getList(TypeToken.of(String.class)).stream().collect(Collectors.toSet());

        return new ShopType(allowedItems);
    }

    @Override
    public void serialize(TypeToken<?> type, ShopType obj, ConfigurationNode value) throws ObjectMappingException {
        value.getNode("items").setValue(new TypeToken<List<String>>() {
                                        },
                obj.getAllowedItems().stream().collect(Collectors.toList()));
    }
}
