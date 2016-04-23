/*
 * Copyright (C) 2016  Zerthick
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
import io.github.zerthick.playershopsrpg.region.RectangularRegion;
import io.github.zerthick.playershopsrpg.shop.Shop;
import io.github.zerthick.playershopsrpg.shop.ShopContainer;
import io.github.zerthick.playershopsrpg.shop.ShopItem;
import io.github.zerthick.playershopsrpg.shop.ShopManager;
import io.github.zerthick.playershopsrpg.shop.type.ShopType;
import io.github.zerthick.playershopsrpg.shop.type.ShopTypeManager;
import io.github.zerthick.playershopsrpg.utils.config.serializers.region.RectangularRegionSerializer;
import io.github.zerthick.playershopsrpg.utils.config.serializers.shop.ShopContainerSerializer;
import io.github.zerthick.playershopsrpg.utils.config.serializers.shop.ShopItemSerializer;
import io.github.zerthick.playershopsrpg.utils.config.serializers.shop.ShopSerializer;
import io.github.zerthick.playershopsrpg.utils.config.serializers.shop.type.ShopTypeSerializer;
import ninja.leaping.configurate.commented.CommentedConfigurationNode;
import ninja.leaping.configurate.hocon.HoconConfigurationLoader;
import ninja.leaping.configurate.loader.ConfigurationLoader;
import ninja.leaping.configurate.objectmapping.ObjectMappingException;
import ninja.leaping.configurate.objectmapping.serialize.TypeSerializers;
import org.slf4j.Logger;

import java.io.File;
import java.io.IOException;
import java.util.*;
import java.util.stream.Collectors;

public class ConfigManager {

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
                .registerType(TypeToken.of(ShopType.class), new ShopTypeSerializer())
        ;
    }

    public ShopManager loadShops() {
        File shopsFile = new File(plugin.getDefaultConfigDir().toFile(), "shops.config");
        ConfigurationLoader<CommentedConfigurationNode> loader = HoconConfigurationLoader.builder().setFile(shopsFile).build();

        if (shopsFile.exists()) {
            try {
                CommentedConfigurationNode shopsConfig = loader.load();

                Map<UUID, Set<ShopContainer>> shopContainerMap =
                        shopsConfig.getValue(new TypeToken<Map<UUID, List<ShopContainer>>>() {
                        }, new HashMap<>())
                                .entrySet().stream().collect(Collectors.toMap(Map.Entry::getKey, e -> e.getValue().stream().collect(Collectors.toSet())));

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
        File shopTypesFile = new File(plugin.getDefaultConfigDir().toFile(), "shopTypes.config");
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
}
