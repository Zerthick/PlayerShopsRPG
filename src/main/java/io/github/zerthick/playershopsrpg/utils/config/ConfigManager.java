/*
 * Copyright (C) 2017  Zerthick
 *
 * This file is part of PlayerShopsRPG.
 *
 * PlayerShopsRPG is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 2 of the License, or
 * (at your option) any later version.
 *
 * PlayerShopsRPG is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with PlayerShopsRPG.  If not, see <http://www.gnu.org/licenses/>.
 */

package io.github.zerthick.playershopsrpg.utils.config;

import com.google.common.reflect.TypeToken;
import io.github.zerthick.playershopsrpg.PlayerShopsRPG;
import io.github.zerthick.playershopsrpg.shop.ShopContainer;
import io.github.zerthick.playershopsrpg.shop.ShopManager;
import io.github.zerthick.playershopsrpg.shop.type.ShopType;
import io.github.zerthick.playershopsrpg.shop.type.ShopTypeManager;
import io.github.zerthick.playershopsrpg.utils.config.serializers.region.CuboidRegionSerializer;
import io.github.zerthick.playershopsrpg.utils.config.serializers.shop.ShopContainerSerializer;
import io.github.zerthick.playershopsrpg.utils.config.serializers.shop.ShopItemSerializer;
import io.github.zerthick.playershopsrpg.utils.config.serializers.shop.ShopSerializer;
import io.github.zerthick.playershopsrpg.utils.config.serializers.shop.type.ShopTypeSerializer;
import ninja.leaping.configurate.commented.CommentedConfigurationNode;
import ninja.leaping.configurate.hocon.HoconConfigurationLoader;
import ninja.leaping.configurate.loader.ConfigurationLoader;
import ninja.leaping.configurate.objectmapping.ObjectMappingException;
import org.slf4j.Logger;
import org.spongepowered.api.asset.Asset;

import java.io.File;
import java.io.IOException;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.*;
import java.util.stream.Collectors;

public class ConfigManager {

    private static ConfigManager instance = null;
    private PlayerShopsRPG plugin;
    private Logger logger;

    private ConfigManager() {

        CuboidRegionSerializer.register();
        ShopTypeSerializer.register();
        ShopContainerSerializer.register();
        ShopItemSerializer.register();
        ShopSerializer.register();

    }

    public static ConfigManager getInstance() {
        if (instance == null) {
            instance = new ConfigManager();
        }
        return instance;
    }

    public void init(PlayerShopsRPG plugin) {
        this.plugin = plugin;
        this.logger = plugin.getLogger();
    }

    public ShopManager loadShops() {
        File shopsFile = new File(plugin.getDefaultConfigDir().toFile(), "shops.conf");
        ConfigurationLoader<CommentedConfigurationNode> loader = HoconConfigurationLoader.builder().setFile(shopsFile).build();

        if (shopsFile.exists()) {
            try {
                CommentedConfigurationNode shopsConfig = loader.load();

                Map<UUID, Set<ShopContainer>> shopContainerMap =
                        shopsConfig.getValue(new TypeToken<Map<UUID, List<ShopContainer>>>() {
                        }, new HashMap<>())
                                .entrySet().stream().collect(Collectors.toMap(Map.Entry::getKey, e -> e.getValue().stream().collect(Collectors.toSet())));

                if(shopContainerMap == null) {
                    shopContainerMap = new HashMap<>();
                }
                return new ShopManager(shopContainerMap, plugin);
            } catch (IOException e) {
                logger.warn("Error loading shops config! Error:" + e.getMessage());
            } catch (ObjectMappingException e) {
                logger.warn("Error mapping shops config! Error:" + e.getMessage());
            }
        }

        return new ShopManager(new HashMap<>(), plugin);
    }

    public void saveShops() {
        File shopsFile = new File(plugin.getDefaultConfigDir().toFile(), "shops.conf");
        ConfigurationLoader<CommentedConfigurationNode> loader = HoconConfigurationLoader.builder().setFile(shopsFile).build();

        try {
            CommentedConfigurationNode shopsConfig = loader.load();

            Map<UUID, List<ShopContainer>> shopContainerMap = plugin.getShopManager().getShopMap()
                    .entrySet().stream().collect(Collectors.toMap(Map.Entry::getKey, e -> e.getValue().stream().collect(Collectors.toList())));

            shopsConfig.setValue(new TypeToken<Map<UUID, List<ShopContainer>>>() {
            }, shopContainerMap);
            loader.save(shopsConfig);
        } catch (IOException | ObjectMappingException e) {
            logger.warn("Error saving shops config! Error:" + e.getMessage());
        }
    }

    public ShopTypeManager loadShopTypes() {
        File shopTypesFile = new File(plugin.getDefaultConfigDir().toFile(), "shopTypes.conf");
        ConfigurationLoader<CommentedConfigurationNode> loader = HoconConfigurationLoader.builder().setFile(shopTypesFile).build();

        if (shopTypesFile.exists()) {
            try {
                CommentedConfigurationNode shopTypes = loader.load();
                return new ShopTypeManager(shopTypes.getValue(new TypeToken<Map<String, ShopType>>() {
                }, new HashMap<>()));
            } catch (IOException e) {
                logger.warn("Error loading shop types config! Error:" + e.getMessage());
            } catch (ObjectMappingException e) {
                logger.warn("Error mapping shop types config! Error:" + e.getMessage());
            }
        }

        //Build Default shop types
        Map<String, ShopType> defaultTypes = new HashMap<>();

        //Blacksmith
        Set<String> allowedItems = new HashSet<>();
        allowedItems.add("minecraft:diamond_block");
        allowedItems.add("minecraft:iron_ingot");
        defaultTypes.put("Blacksmith", new ShopType(allowedItems));

        try {
            CommentedConfigurationNode shopTypes = loader.load();
            shopTypes.setValue(new TypeToken<Map<String, ShopType>>() {
            }, defaultTypes);
            loader.save(shopTypes);
        } catch (IOException e) {
            logger.warn("Error loading shop types default config! Error:" + e.getMessage());
        } catch (ObjectMappingException e) {
            logger.warn("Error mapping shop types config! Error:" + e.getMessage());
        }

        return new ShopTypeManager(defaultTypes);
    }

    public Map<UUID, LocalDateTime> loadShopRent() {
        File shopRentFile = new File(plugin.getDefaultConfigDir().toFile(), "shopRent.conf");
        ConfigurationLoader<CommentedConfigurationNode> loader = HoconConfigurationLoader.builder().setFile(shopRentFile).build();

        if (shopRentFile.exists()) {
            try {
                CommentedConfigurationNode shopRentConfig = loader.load();
                return shopRentConfig.getValue(new TypeToken<Map<UUID, Long>>() {
                }, new HashMap<>()).entrySet().stream().collect(Collectors.toMap(Map.Entry::getKey, entry -> LocalDateTime.ofEpochSecond(entry.getValue(), 0, ZoneOffset.UTC)));
            } catch (IOException e) {
                logger.warn("Error loading shop rent config! Error:" + e.getMessage());
            } catch (ObjectMappingException e) {
                logger.warn("Error mapping shop rent config! Error:" + e.getMessage());
            }
        }

        return new HashMap<>();
    }

    public void saveShopRent() {
        File shopRentFile = new File(plugin.getDefaultConfigDir().toFile(), "shopRent.conf");
        ConfigurationLoader<CommentedConfigurationNode> loader = HoconConfigurationLoader.builder().setFile(shopRentFile).build();

        try {
            CommentedConfigurationNode shopRentConfig = loader.load();

            Map<UUID, Long> shopRentMap = plugin.getShopRentManager().getShopRentMap().entrySet().stream().collect(Collectors.toMap(Map.Entry::getKey, entry -> entry.getValue().toEpochSecond(ZoneOffset.UTC)));

            shopRentConfig.setValue(new TypeToken<Map<UUID, Long>>() {
            }, shopRentMap);
            loader.save(shopRentConfig);
        } catch (IOException | ObjectMappingException e) {
            logger.warn("Error saving shop rent config! Error:" + e.getMessage());
        }
    }

    public Properties loadMessages() {
        File messagesFile = new File(plugin.getDefaultConfigDir().toFile(), "messages.conf");
        ConfigurationLoader<CommentedConfigurationNode> loader = HoconConfigurationLoader.builder().setFile(messagesFile).build();

        Properties properties = new Properties();

        if (!messagesFile.exists()) {
            Asset defaultConfig = plugin.getInstance().getAsset("messages_en_US.conf").get();
            try {
                defaultConfig.copyToFile(messagesFile.toPath());
                loader.save(loader.load());
            } catch (IOException e) {
                logger.warn("Error loading default messages config! Error:" + e.getMessage());
            }
        }

        try {
            CommentedConfigurationNode messagesConfig = loader.load();
            messagesConfig.getNode("messages").getValue(new TypeToken<Map<String, String>>() {
            }).entrySet().forEach(e -> properties.put(e.getKey(), e.getValue()));
            return properties;
        } catch (IOException | ObjectMappingException e) {
            logger.warn("Error loading messages config! Error:" + e.getMessage());
        }

        return properties;
    }
}
