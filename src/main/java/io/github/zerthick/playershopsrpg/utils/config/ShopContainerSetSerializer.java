package io.github.zerthick.playershopsrpg.utils.config;

import com.google.common.reflect.TypeToken;
import io.github.zerthick.playershopsrpg.shop.ShopContainer;
import ninja.leaping.configurate.ConfigurationNode;
import ninja.leaping.configurate.objectmapping.ObjectMappingException;
import ninja.leaping.configurate.objectmapping.serialize.TypeSerializer;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class ShopContainerSetSerializer implements TypeSerializer<Set<ShopContainer>> {

    @Override
    public Set<ShopContainer> deserialize(TypeToken<?> type, ConfigurationNode value) throws ObjectMappingException {
        List<ShopContainer> list = value.getList(TypeToken.of(ShopContainer.class));
        Set<ShopContainer> set = new HashSet<>();
        set.addAll(list);
        return set;
    }

    @Override
    public void serialize(TypeToken<?> type, Set<ShopContainer> obj, ConfigurationNode value) throws ObjectMappingException {
        List<ShopContainer> list = new ArrayList<>();
        list.addAll(obj);
        value.getNode("shopContainers").setValue(list);
    }
}
