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

package io.github.zerthick.playershopsrpg.utils.config.sql;

import com.google.common.collect.ImmutableList;
import io.github.zerthick.playershopsrpg.region.CuboidRegion;
import io.github.zerthick.playershopsrpg.region.Region;
import io.github.zerthick.playershopsrpg.shop.Shop;
import io.github.zerthick.playershopsrpg.shop.ShopContainer;
import io.github.zerthick.playershopsrpg.shop.ShopItem;
import io.github.zerthick.playershopsrpg.utils.config.serializers.ItemStackHOCONSerializer;
import io.github.zerthick.playershopsrpg.utils.config.serializers.ShopRegionHOCONSerializer;
import ninja.leaping.configurate.objectmapping.ObjectMappingException;
import org.slf4j.Logger;
import org.spongepowered.api.item.inventory.ItemStackSnapshot;

import java.io.IOException;
import java.math.BigDecimal;
import java.sql.SQLException;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.*;

public class SQLDataUtil {

    public static void createTables(Logger logger) {
        try {
            createShopTable();
            createShopRegionTable();
            createShopRentTable();
            createShopItemTable();
            createShopManagerTable();
        } catch (SQLException e) {
            logger.error(e.getMessage());
        }
    }


    private static void createShopTable() throws SQLException {
        List<String> columns =
                ImmutableList.of("ID UUID PRIMARY KEY", "NAME VARCHAR(256)", "OWNER_ID UUID", "RENTER_ID UUID",
                        "UNLIMITED_MONEY BOOLEAN", "UNLIMITED_STOCK BOOLEAN", "TYPE VARCHAR(32)",
                        "PRICE DECIMAL", "RENT DECIMAL", "CURRENCY VARCHAR(32)");
        SQLUtil.createTable("SHOP", columns);
    }

    private static void createShopRegionTable() throws SQLException {
        List<String> columns =
                ImmutableList.of("ID UUID PRIMARY KEY", "TYPE VARCHAR(32)", "DATA VARCHAR", "SHOP_ID UUID",
                        "WORLD_ID UUID", "FOREIGN KEY(SHOP_ID) REFERENCES SHOP(ID) ON DELETE CASCADE");
        SQLUtil.createTable("SHOP_REGION", columns);
    }

    private static void createShopRentTable() throws SQLException {
        List<String> columns =
                ImmutableList.of("SHOP_ID UUID PRIMARY KEY", "EXPIRE_TIME BIGINT", "FOREIGN KEY(SHOP_ID) REFERENCES SHOP(ID) ON DELETE CASCADE");
        SQLUtil.createTable("SHOP_RENT", columns);
    }

    private static void createShopItemTable() throws SQLException {
        List<String> columns =
                ImmutableList.of("ID UUID PRIMARY KEY", "ITEMSTACK VARCHAR", "AMOUNT INT", "MAX_AMOUNT INT",
                        "BUY_PRICE DECIMAL", "SELL_PRICE DECIMAL", "SHOP_ID UUID",
                        "FOREIGN KEY(SHOP_ID) REFERENCES SHOP(ID) ON DELETE CASCADE");
        SQLUtil.createTable("SHOP_ITEM", columns);
    }

    private static void createShopManagerTable() throws SQLException {
        List<String> columns =
                ImmutableList.of("SHOP_ID UUID", "MANAGER_ID UUID", "PRIMARY KEY(SHOP_ID, MANAGER_ID)",
                        "FOREIGN KEY(SHOP_ID) REFERENCES SHOP(ID) ON DELETE CASCADE");
        SQLUtil.createTable("SHOP_MANAGERS", columns);
    }

    public static void createViews(Logger logger) {
        try {
            createShopRentView();
            createShopRegionView();
        } catch (SQLException e) {
            logger.error(e.getMessage());
        }
    }

    private static void createShopRentView() throws SQLException {
        SQLUtil.executeUpdate("CREATE VIEW IF NOT EXISTS SHOP_RENT_VIEW AS " +
                "SELECT S.ID, S.NAME, S.OWNER_ID, S.RENTER_ID, " +
                "S.UNLIMITED_MONEY, S.UNLIMITED_STOCK, S.TYPE, S.PRICE, S.RENT, " +
                "R.EXPIRE_TIME FROM SHOP S LEFT JOIN SHOP_RENT R ON S.ID = R.SHOP_ID");
    }

    private static void createShopRegionView() throws SQLException {
        SQLUtil.executeUpdate("CREATE VIEW IF NOT EXISTS SHOP_REGION_VIEW AS " +
                "SELECT S.ID, S.NAME, S.OWNER_ID, S.RENTER_ID, " +
                "S.UNLIMITED_MONEY, S.UNLIMITED_STOCK, S.TYPE SHOP_TYPE, S.PRICE, S.RENT, " +
                "R.ID REGION_ID, R.TYPE REGION_TYPE, R.DATA, R.WORLD_ID " +
                "FROM SHOP S JOIN SHOP_REGION R ON S.ID = R.SHOP_ID");
    }

    private static Set<UUID> loadShopManagers(UUID shopUUID, Logger logger) {
        Set<UUID> managerSet = new HashSet<>();
        try {
            SQLUtil.select("SHOP_MANAGERS", "SHOP_ID", shopUUID.toString(), resultSet -> {
                try {
                    while (resultSet.next()) {
                        managerSet.add((UUID) resultSet.getObject("MANAGER_ID"));
                    }
                } catch (SQLException e) {
                    logger.error(e.getMessage());
                }
            });
        } catch (SQLException e) {
            logger.error(e.getMessage());
        }
        return managerSet;
    }

    private static void saveShopManagers(UUID shopUUID, Set<UUID> managerSet, Logger logger) {
        try {
            SQLUtil.executeBatch("MERGE INTO SHOP_MANAGERS VALUES (?, ?)", preparedStatement -> managerSet.forEach(managerUUID -> {
                try {
                    preparedStatement.setObject(1, shopUUID);
                    preparedStatement.setObject(2, managerUUID);
                    preparedStatement.addBatch();
                } catch (SQLException e) {
                    logger.error(e.getMessage());
                }
            }));
        } catch (SQLException e) {
            logger.error(e.getMessage());
        }
    }

    private static Map<UUID, ShopItem> loadShopItems(UUID shopUUID, Logger logger) {
        Map<UUID, ShopItem> items = new HashMap<>();

        try {
            SQLUtil.select("SHOP_ITEM", "SHOP_ID", shopUUID.toString(), resultSet -> {
                try {
                    while (resultSet.next()) {
                        UUID itemUUID = (UUID) resultSet.getObject("ID");
                        ItemStackSnapshot snapshot = ItemStackHOCONSerializer.deserializeSnapShot(resultSet.getString("ITEMSTACK"));
                        int itemAmount = resultSet.getInt("AMOUNT");
                        int itemMaxAmount = resultSet.getInt("MAX_AMOUNT");
                        double itemBuyPrice = resultSet.getBigDecimal("BUY_PRICE").doubleValue();
                        double itemSellPrice = resultSet.getBigDecimal("SELL_PRICE").doubleValue();
                        items.put(itemUUID, new ShopItem(itemUUID, snapshot, itemAmount, itemMaxAmount, itemBuyPrice, itemSellPrice));
                    }
                } catch (IOException | ObjectMappingException | SQLException e) {
                    logger.info(e.getMessage());
                }
            });
        } catch (SQLException e) {
            logger.error(e.getMessage());
        }

        return items;
    }

    private static void saveShopItems(UUID shopUUID, Collection<ShopItem> items, Logger logger) {

        try {
            SQLUtil.executeBatch("MERGE INTO SHOP_ITEM VALUES (?, ?, ?, ?, ?, ?, ?)", preparedStatement -> items.forEach(shopItem -> {
                UUID itemID = shopItem.getShopItemUUID();
                String itemStack = "";
                try {
                    itemStack = ItemStackHOCONSerializer.serializeSnapShot(shopItem.getItemStackSnapShot());
                } catch (ObjectMappingException | IOException e) {
                    logger.error(e.getMessage());
                }
                int amount = shopItem.getItemAmount();
                int maxAmount = shopItem.getItemMaxAmount();
                double itemBuyPrice = shopItem.getItemBuyPrice();
                double itemSellPrice = shopItem.getItemSellPrice();

                try {
                    preparedStatement.setObject(1, itemID);
                    preparedStatement.setString(2, itemStack);
                    preparedStatement.setInt(3, amount);
                    preparedStatement.setInt(4, maxAmount);
                    preparedStatement.setBigDecimal(5, BigDecimal.valueOf(itemBuyPrice));
                    preparedStatement.setBigDecimal(6, BigDecimal.valueOf(itemSellPrice));
                    preparedStatement.setObject(7, shopUUID);
                    preparedStatement.addBatch();
                } catch (SQLException e) {
                    logger.error(e.getMessage());
                }
            }));
        } catch (SQLException e) {
            logger.error(e.getMessage());
        }
    }

    private static void saveShop(Shop shop, Logger logger) {
        UUID id = shop.getUUID();
        String name = shop.getName();
        UUID ownerId = shop.getOwnerUUID().orElse(null);
        UUID renterId = shop.getRenterUUID();
        boolean unlimitedMoney = shop.isUnlimitedMoney();
        boolean unlimitedStock = shop.isUnlimitedStock();
        String type = shop.getType();
        double price = shop.getPrice();
        double rent = shop.getRent();
        String currencyID = shop.getCurrencyID();
        try {
            SQLUtil.executeUpdate("MERGE INTO SHOP VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?)", preparedStatement -> {
                try {
                    preparedStatement.setObject(1, id);
                    preparedStatement.setString(2, name);
                    preparedStatement.setObject(3, ownerId);
                    preparedStatement.setObject(4, renterId);
                    preparedStatement.setBoolean(5, unlimitedMoney);
                    preparedStatement.setBoolean(6, unlimitedStock);
                    preparedStatement.setString(7, type);
                    preparedStatement.setBigDecimal(8, BigDecimal.valueOf(price));
                    preparedStatement.setBigDecimal(9, BigDecimal.valueOf(rent));
                    preparedStatement.setString(10, currencyID);
                } catch (SQLException e) {
                    logger.error(e.getMessage());
                }
            });
            saveShopItems(shop.getUUID(), shop.getItems().values(), logger);
            saveShopManagers(shop.getUUID(), shop.getManagerUUIDSet(), logger);
        } catch (SQLException e) {
            logger.error(e.getMessage());
        }
    }

    private static Shop loadShop(UUID shopUUID, Logger logger) {

        final Shop[] shop = {null};

        try {
            SQLUtil.select("SHOP", "ID", shopUUID.toString(), resultSet -> {
                try {
                    if (resultSet.next()) {
                        UUID id = (UUID) resultSet.getObject("ID");
                        String name = resultSet.getString("NAME");
                        UUID ownerId = (UUID) resultSet.getObject("OWNER_ID");
                        UUID renterId = (UUID) resultSet.getObject("RENTER_ID");
                        boolean unlimitedMoney = resultSet.getBoolean("UNLIMITED_MONEY");
                        boolean unlimitedStock = resultSet.getBoolean("UNLIMITED_STOCK");
                        String type = resultSet.getString("TYPE");
                        double price = resultSet.getBigDecimal("PRICE").doubleValue();
                        double rent = resultSet.getBigDecimal("RENT").doubleValue();
                        Set<UUID> managers = loadShopManagers(shopUUID, logger);
                        Map<UUID, ShopItem> items = loadShopItems(shopUUID, logger);
                        String currencyID = resultSet.getString("CURRENCY");
                        shop[0] = new Shop(id, name, ownerId, renterId, managers, items, unlimitedMoney, unlimitedStock, type, price, rent, currencyID);
                    }
                } catch (SQLException e) {
                    logger.error(e.getMessage());
                }
            });
        } catch (SQLException e) {
            logger.error(e.getMessage());
        }
        return shop[0];
    }

    public static void saveShopContainter(UUID worldUUID, ShopContainer shopContainer, Logger logger) {

        try {
            saveShop(shopContainer.getShop(), logger);
            Region shopRegion = shopContainer.getShopRegion();
            UUID regionID = shopRegion.getUUID();
            String type = shopRegion.getType();
            String data = ShopRegionHOCONSerializer.serializeShopRegion(shopRegion);
            UUID shopID = shopContainer.getShop().getUUID();
            SQLUtil.executeUpdate("MERGE INTO SHOP_REGION VALUES (?, ?, ?, ?, ?)", preparedStatement -> {
                try {
                    preparedStatement.setObject(1, regionID);
                    preparedStatement.setString(2, type);
                    preparedStatement.setString(3, data);
                    preparedStatement.setObject(4, shopID);
                    preparedStatement.setObject(5, worldUUID);
                } catch (SQLException e) {
                    logger.info(e.getMessage());
                }
            });
        } catch (ObjectMappingException | IOException | SQLException e) {
            logger.error(e.getMessage());
        }
    }

    public static void saveShopContainers(UUID worldUUID, Set<ShopContainer> shopContainers, Logger logger) {
        for (ShopContainer shopContainer : shopContainers) {
            saveShopContainter(worldUUID, shopContainer, logger);
        }
    }

    public static Map<UUID, Set<ShopContainer>> loadShopContainers(Logger logger) {

        Map<UUID, Set<ShopContainer>> shopContainerMap = new HashMap<>();

        try {
            SQLUtil.select("SHOP_REGION", resultSet -> {
                try {
                    while (resultSet.next()) {
                        UUID worldUUID = (UUID) resultSet.getObject("WORLD_ID");
                        Set<ShopContainer> worldContainerSet = shopContainerMap.getOrDefault(worldUUID, new HashSet<>());
                        UUID regionID = (UUID) resultSet.getObject("ID");
                        String regionType = resultSet.getString("TYPE");
                        Region region = ShopRegionHOCONSerializer.deserializeShopRegion(resultSet.getString("DATA"), regionType);
                        switch (region.getType()) {
                            case "cuboid":
                                CuboidRegion cuboidRegion = (CuboidRegion) region;
                                region = new CuboidRegion(regionID, cuboidRegion.getMin(), cuboidRegion.getMax());
                                break;
                        }
                        Shop shop = loadShop((UUID) resultSet.getObject("SHOP_ID"), logger);
                        worldContainerSet.add(new ShopContainer(shop, region));
                        shopContainerMap.put(worldUUID, worldContainerSet);
                    }
                } catch (SQLException | IOException | ObjectMappingException e) {
                    e.printStackTrace();
                }
            });
        } catch (SQLException e) {
            logger.error(e.getMessage());
        }

        return shopContainerMap;
    }

    public static void deleteShop(UUID shopUUID, Logger logger) {
        try {
            SQLUtil.delete("SHOP", "ID", shopUUID.toString());
        } catch (SQLException e) {
            logger.error(e.getMessage());
        }
    }

    public static void deleteShopItem(UUID shopItemUUID, Logger logger) {
        try {
            SQLUtil.delete("SHOP_ITEM", "ID", shopItemUUID.toString());
        } catch (SQLException e) {
            logger.error(e.getMessage());
        }
    }

    public static void deleteShopManager(UUID managagerID, UUID shopID, Logger logger) {
        try {
            SQLUtil.executeUpdate("DELETE FROM SHOP_MANAGER WHERE SHOP_ID = ? AND MANAGER_ID = ?", preparedStatement -> {
                try {
                    preparedStatement.setObject(1, shopID);
                    preparedStatement.setObject(2, managagerID);
                } catch (SQLException e) {
                    logger.error(e.getMessage());
                }
            });
        } catch (SQLException e) {
            logger.error(e.getMessage());
        }
    }

    public static Map<UUID,LocalDateTime> loadShopRent(Logger logger) {
        Map<UUID,LocalDateTime> rentMap = new HashMap<>();
        try {
            SQLUtil.select("SHOP_RENT", resultSet -> {
                try {
                    while(resultSet.next()){
                        rentMap.put(
                                (UUID) resultSet.getObject("SHOP_ID"),
                                LocalDateTime.ofEpochSecond(
                                        resultSet.getLong("EXPIRE_TIME"),
                                        0,
                                        ZoneOffset.UTC)
                        );
                    }
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            });
        } catch (SQLException e) {
            logger.error(e.getMessage());
        }
        return rentMap;
    }

    public static void saveShopRent(Map<UUID, LocalDateTime> rentMap, Logger logger){

        try {
            SQLUtil.executeBatch("MERGE INTO SHOP_RENT VALUES (?, ?)", preparedStatement -> rentMap.forEach(((uuid, localDateTime) -> {
                try {
                    preparedStatement.setObject(1, uuid);
                    preparedStatement.setLong(2, localDateTime.toEpochSecond(ZoneOffset.UTC));
                    preparedStatement.addBatch();
                } catch (SQLException e) {
                    logger.error(e.getMessage());
                }
            })));
        } catch (SQLException e) {
            logger.error(e.getMessage());
        }
    }

    public static void deleteShopRent(UUID shopUUID, Logger logger){
        try {
            SQLUtil.delete("SHOP_RENT", "SHOP_ID", shopUUID.toString());
        } catch (SQLException e){
            logger.error(e.getMessage());
        }
    }
}
