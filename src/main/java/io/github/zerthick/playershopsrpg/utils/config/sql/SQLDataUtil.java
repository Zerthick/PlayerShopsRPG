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

import java.sql.SQLException;
import java.util.List;

public class SQLDataUtil {

    public static void createTables() throws SQLException {
        createShopTable();
        createShopRegionTable();
        createShopItemTable();
        createShopManagerTable();
    }

    private static void createShopTable() throws SQLException {
        List<String> columns =
                ImmutableList.of("ID UUID PRIMARY KEY", "NAME VARCHAR(256)", "OWNER_ID UUID", "RENTER_ID UUID",
                        "UNLIMITED_MONEY BOOLEAN", "UNLIMITED_STOCK BOOLEAN", "TYPE VARCHAR(32)",
                        "PRICE DECIMAL", "RENT DECIMAL");
        SQLUtil.createTable("SHOP", columns);
    }

    private static void createShopRegionTable() throws SQLException {
        List<String> columns =
                ImmutableList.of("ID UUID PRIMARY KEY", "TYPE VARCHAR(32)", "DATA VARCHAR", "SHOP_ID UUID",
                        "WORLD_ID UUID", "FOREIGN KEY(SHOP_ID) REFERENCES SHOP(ID) ON DELETE CASCADE");
        SQLUtil.createTable("SHOP_REGION", columns);
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
}
