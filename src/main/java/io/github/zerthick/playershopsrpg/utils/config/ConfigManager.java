package io.github.zerthick.playershopsrpg.utils.config;

import com.google.common.reflect.TypeToken;
import io.github.zerthick.playershopsrpg.PlayerShopsRPG;
import io.github.zerthick.playershopsrpg.region.RectangularRegion;
import io.github.zerthick.playershopsrpg.shop.Shop;
import io.github.zerthick.playershopsrpg.shop.ShopContainer;
import io.github.zerthick.playershopsrpg.shop.ShopItem;
import io.github.zerthick.playershopsrpg.shop.ShopManager;
import io.github.zerthick.playershopsrpg.utils.config.serializers.region.RectangularRegionSerializer;
import io.github.zerthick.playershopsrpg.utils.config.serializers.shop.ShopContainerSerializer;
import io.github.zerthick.playershopsrpg.utils.config.serializers.shop.ShopItemSerializer;
import io.github.zerthick.playershopsrpg.utils.config.serializers.shop.ShopSerializer;
import ninja.leaping.configurate.commented.CommentedConfigurationNode;
import ninja.leaping.configurate.hocon.HoconConfigurationLoader;
import ninja.leaping.configurate.loader.ConfigurationLoader;
import ninja.leaping.configurate.objectmapping.ObjectMappingException;
import ninja.leaping.configurate.objectmapping.serialize.TypeSerializers;
import org.slf4j.Logger;

import java.io.File;
import java.io.IOException;
import java.util.*;

public class ConfigManager {

    private CommentedConfigurationNode shopsConfig;

    private PlayerShopsRPG plugin;
    private Logger logger;

    public ConfigManager(PlayerShopsRPG plugin) {
        this.plugin = plugin;
        logger = plugin.getLogger();

        TypeSerializers.getDefaultSerializers()
                .registerType(TypeToken.of(ShopItem.class), new ShopItemSerializer())
                .registerType(TypeToken.of(Shop.class), new ShopSerializer())
                .registerType(TypeToken.of(ShopContainer.class), new ShopContainerSerializer())
                .registerType(TypeToken.of(RectangularRegion.class), new RectangularRegionSerializer())
        ;
    }

    public ShopManager loadShops() {
        File shopsFile = new File(plugin.getDefaultConfigDir().toFile(), "shops.config");
        ConfigurationLoader<CommentedConfigurationNode> loader = HoconConfigurationLoader.builder().setFile(shopsFile).build();

        if (shopsFile.exists()) {
            try {
                shopsConfig = loader.load();

                Map<UUID, Set<ShopContainer>> shopContainerMap = new HashMap<>();
                Map<UUID, List<ShopContainer>> shopContainerMapList =
                        shopsConfig.getValue(new TypeToken<Map<UUID, List<ShopContainer>>>() {
                        });
                for (UUID uuid : shopContainerMapList.keySet()) {
                    Set<ShopContainer> shopContainerSet = new HashSet<>();
                    shopContainerSet.addAll(shopContainerMapList.get(uuid));
                    shopContainerMap.put(uuid, shopContainerSet);
                }

                return new ShopManager(shopContainerMap);
            } catch (IOException e) {
                logger.warn("Error loading shops config! Error:" + e.getMessage());
            } catch (ObjectMappingException e) {
                logger.warn("Error mapping shops config! Error:" + e.getMessage());
            }
        }

        return new ShopManager(new HashMap<>());
    }

    public void saveShops() {
        File shopsFile = new File(plugin.getDefaultConfigDir().toFile(), "shops.config");
        ConfigurationLoader<CommentedConfigurationNode> loader = HoconConfigurationLoader.builder().setFile(shopsFile).build();

        try {
            shopsConfig = loader.load();

            Map<UUID, Set<ShopContainer>> shopContainerMap = plugin.getShopManager().getShopMap();
            Map<UUID, List<ShopContainer>> shopContainerMapList = new HashMap<>();
            for (UUID uuid : shopContainerMap.keySet()) {
                List<ShopContainer> shopContainerList = new ArrayList<>();
                shopContainerList.addAll(shopContainerMap.get(uuid));
                shopContainerMapList.put(uuid, shopContainerList);
            }

            shopsConfig.setValue(new TypeToken<Map<UUID, List<ShopContainer>>>() {
            }, shopContainerMapList);
            loader.save(shopsConfig);
        } catch (IOException | ObjectMappingException e) {
            logger.warn("Error saving shops config! Error:" + e.getMessage());
        }
    }
}
