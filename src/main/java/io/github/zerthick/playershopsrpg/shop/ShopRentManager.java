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

package io.github.zerthick.playershopsrpg.shop;

import java.time.LocalDateTime;
import java.util.Map;
import java.util.UUID;

public class ShopRentManager {

    private static ShopRentManager instance = null;

    private Map<UUID, LocalDateTime> shopRentMap;

    protected ShopRentManager() {
        //Singleton Design Pattern
    }

    public static ShopRentManager getInstance() {
        if (instance == null) {
            instance = new ShopRentManager();
        }
        return instance;
    }


}
