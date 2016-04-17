package io.github.zerthick.playershopsrpg.utils.config;

import com.google.common.reflect.TypeToken;
import io.github.zerthick.playershopsrpg.PlayerShopsRPG;
import io.github.zerthick.playershopsrpg.shop.ShopContainer;
import io.github.zerthick.playershopsrpg.shop.ShopManager;
import ninja.leaping.configurate.ConfigurationOptions;
import ninja.leaping.configurate.commented.CommentedConfigurationNode;
import ninja.leaping.configurate.hocon.HoconConfigurationLoader;
import ninja.leaping.configurate.loader.ConfigurationLoader;
import ninja.leaping.configurate.objectmapping.ObjectMappingException;
import ninja.leaping.configurate.objectmapping.serialize.TypeSerializerCollection;
import ninja.leaping.configurate.objectmapping.serialize.TypeSerializers;
import org.slf4j.Logger;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Set;

public class ConfigManager {

    private CommentedConfigurationNode shopsConfig;

    private PlayerShopsRPG plugin;
    private Logger logger;
    private ConfigurationOptions configOptions;

    public ConfigManager(PlayerShopsRPG plugin) {
        this.plugin = plugin;
        logger = plugin.getLogger();

        TypeSerializerCollection serializers = TypeSerializers.getDefaultSerializers().newChild();
        serializers.registerType(new TypeToken<Set<ShopContainer>>() {
        }, new ShopContainerSetSerializer());
        serializers.registerType(TypeToken.of(ShopContainer.class), new ShopContainerSerializer());

        configOptions = ConfigurationOptions.defaults().setSerializers(serializers);

    }

    public ShopManager loadShops() {
        File shopsFile = new File(plugin.getDefaultConfigDir().toFile(), "shops.config");
        ConfigurationLoader<CommentedConfigurationNode> loader = HoconConfigurationLoader.builder().setFile(shopsFile).build();

        try {
            shopsConfig = loader.load(configOptions);
            if (shopsFile.exists()) {
                return shopsConfig.getValue(TypeToken.of(ShopManager.class));
            } else {
                return new ShopManager(new HashMap<>());
            }
        } catch (IOException e) {
            logger.warn("Error loading shops config! Error:" + e.getMessage());
        } catch (ObjectMappingException e) {
            logger.warn("Error mapping shops config! Error:" + e.getMessage());
        }

        return new ShopManager(new HashMap<>());
    }

    public void saveShops() {
        File shopsFile = new File(plugin.getDefaultConfigDir().toFile(), "shops.config");
        ConfigurationLoader<CommentedConfigurationNode> loader = HoconConfigurationLoader.builder().setFile(shopsFile).build();

        try {
            shopsConfig = loader.load(configOptions);
            shopsConfig.setValue(TypeToken.of(ShopManager.class), plugin.getShopManager());
            loader.save(shopsConfig);
        } catch (IOException e) {
            logger.warn("Error saving shops config! Error:" + e.getMessage());
        } catch (ObjectMappingException e) {
            logger.warn("Error mapping shops config! Error:" + e.getMessage());
        }
    }
}
