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

package io.github.zerthick.playershopsrpg.shop.type;

import org.spongepowered.api.item.inventory.ItemStack;

import java.util.Set;

public class ShopType {

    private Set<String> allowedItems;

    public ShopType(Set<String> allowedItems) {
        this.allowedItems = allowedItems;
    }

    public boolean isAllowedItem(ItemStack itemStack) {
        return allowedItems.contains(itemStack.getType().getId());
    }

    public Set<String> getAllowedItems() {
        return allowedItems;
    }
}
