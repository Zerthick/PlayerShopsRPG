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

public class PluginConfig {

    private boolean rentExpireClearInventory;
    private boolean rentExpireTransferFunds;

    public PluginConfig(boolean rentExpireClearInventory, boolean rentExpireTransferFunds) {
        this.rentExpireClearInventory = rentExpireClearInventory;
        this.rentExpireTransferFunds = rentExpireTransferFunds;
    }

    public boolean isRentExpireClearInventory() {
        return rentExpireClearInventory;
    }

    public boolean isRentExpireTransferFunds() {
        return rentExpireTransferFunds;
    }
}
