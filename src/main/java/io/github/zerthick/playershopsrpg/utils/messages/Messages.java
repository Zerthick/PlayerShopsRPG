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

package io.github.zerthick.playershopsrpg.utils.messages;

import io.github.zerthick.playershopsrpg.utils.config.ConfigManager;

import java.util.Map;
import java.util.Properties;


public class Messages {

    // Drop-Ins
    public static final String DROPIN_PLAYER_NAME = "{PLAYER_NAME}";
    public static final String DROPIN_SHOP_NAME = "{SHOP_NAME}";
    public static final String DROPIN_ITEM_NAME = "{ITEM_NAME}";
    public static final String DROPIN_ITEM_AMOUNT = "{ITEM_AMOUNT}";


    private final static Properties messageProps = ConfigManager.getInstance().loadMessages();

    //Shop Transactions
    public static final String YOU_ARE_NOT_THE_OWNER_OF_THIS_SHOP = messageProps.getProperty("YOU_ARE_NOT_THE_OWNER_OF_THIS_SHOP");
    public static final String THE_SPECIFIED_ITEM_IS_ALREADY_IN_THIS_SHOP = messageProps.getProperty("THE_SPECIFIED_ITEM_IS_ALREADY_IN_THIS_SHOP");
    public static final String THE_SPECIFIED_ITEM_IS_NOT_IN_THIS_SHOP = messageProps.getProperty("THE_SPECIFIED_ITEM_IS_NOT_IN_THIS_SHOP");
    public static final String YOU_ARE_NOT_A_MANAGER_OF_THIS_SHOP = messageProps.getProperty("YOU_ARE_NOT_A_MANAGER_OF_THIS_SHOP");
    public static final String YOU_DON_T_HAVE_ENOUGH_FUNDS = messageProps.getProperty("YOU_DON_T_HAVE_ENOUGH_FUNDS");
    public static final String THIS_SHOP_IS_NOT_FOR_RENT = messageProps.getProperty("THIS_SHOP_IS_NOT_FOR_RENT");
    public static final String THIS_SHOP_IS_NOT_FOR_SALE = messageProps.getProperty("THIS_SHOP_IS_NOT_FOR_SALE");
    public static final String INVALID_SHOP_RENT_PRICE = messageProps.getProperty("INVALID_SHOP_RENT_PRICE");
    public static final String INVALID_SHOP_SELL_PRICE = messageProps.getProperty("INVALID_SHOP_SELL_PRICE");
    public static final String INVALID_ITEM_SELL_PRICE = messageProps.getProperty("INVALID_ITEM_SELL_PRICE");
    public static final String INVALID_MAX_ITEM_AMOUNT = messageProps.getProperty("INVALID_MAX_ITEM_AMOUNT");
    public static final String INVALID_ITEM_BUY_PRICE = messageProps.getProperty("INVALID_ITEM_BUY_PRICE");
    public static final String SHOP_DOES_NOT_BUY_ITEM = messageProps.getProperty("SHOP_DOES_NOT_BUY_ITEM");
    public static final String PLAYER_DOES_NOT_HAVE_ENOUGH_ITEM = messageProps.getProperty("PLAYER_DOES_NOT_HAVE_ENOUGH_ITEM ");
    public static final String SHOP_DOES_NOT_HAVE_ENOUGH_FUNDS = messageProps.getProperty("SHOP_DOES_NOT_HAVE_ENOUGH_FUNDS");
    public static final String SHOP_DOES_NOT_SELL_ITEM = messageProps.getProperty("SHOP_DOES_NOT_SELL_ITEM");
    public static final String SHOP_DOES_NOT_HAVE_ENOUGH_ITEM = messageProps.getProperty("SHOP_DOES_NOT_HAVE_ENOUGH_ITEM");
    public static final String PLAYER_IS_NOT_A_MANAGER = messageProps.getProperty("PLAYER_IS_NOT_A_MANAGER");

    //Commands


    private static Messages instance = null;

    private Messages() {
        //Singleton Design Pattern
    }

    public static Messages getInstance() {
        if (instance == null) {
            instance = new Messages();
        }
        return instance;
    }

    public static String processDropins(String msg, Map<String, String> dropins) {
        String out = msg;
        for (String key : dropins.keySet()) {
            out = out.replace(key, dropins.get(key));
        }
        return out;
    }
}
