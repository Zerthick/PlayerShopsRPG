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
    public static final String DROPIN_SHOP_TYPE = "{SHOP_TYPE}";
    public static final String DROPIN_SHOP_CORDS = "{SHOP_CORDS}";
    public static final String DROPIN_ITEM_NAME = "{ITEM_NAME}";
    public static final String DROPIN_ITEM_AMOUNT = "{ITEM_AMOUNT}";
    public static final String DROPIN_ITEM_INDEX = "{ITEM_INDEX}";

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
    public static final String YOU_ARE_NOT_IN_A_SHOP = messageProps.getProperty("YOU_ARE_NOT_IN_A_SHOP");
    public static final String ADD_ITEM_CONSOLE_REJECT = messageProps.getProperty("ADD_ITEM_CONSOLE_REJECT");
    public static final String BUY_ITEM_CONSOLE_REJECT = messageProps.getProperty("BUY_ITEM_CONSOLE_REJECT");
    public static final String CREATE_ITEM_SUCCESS = messageProps.getProperty("CREATE_ITEM_SUCCESS");
    public static final String CREATE_ITEM_TYPE_REJECT = messageProps.getProperty("CREATE_ITEM_TYPE_REJECT");
    public static final String CREATE_ITEM_CONSOLE_REJECT = messageProps.getProperty("CREATE_ITEM_CONSOLE_REJECT");
    public static final String DESTROY_ITEM_SUCCESS = messageProps.getProperty("DESTROY_ITEM_SUCCESS");
    public static final String DESTROY_ITEM_CONSOLE_REJECT = messageProps.getProperty("DESTROY_ITEM_CONSOLE_REJECT");
    public static final String REMOVE_ITEM_CONSOLE_REJECT = messageProps.getProperty("REMOVE_ITEM_CONSOLE_REJECT");
    public static final String SELL_ITEM_CONSOLE_REJECT = messageProps.getProperty("SELL_ITEM_CONSOLE_REJECT");
    public static final String SET_ITEM_UNKNOWN_ATTRIBUTE = messageProps.getProperty("SET_ITEM_UNKNOWN_ATTRIBUTE");
    public static final String SET_ITEM_CONSOLE_REJECT = messageProps.getProperty("SET_ITEM_CONSOLE_REJECT");
    public static final String ADD_MANAGER_SUCCESS = messageProps.getProperty("ADD_MANAGER_SUCCESS");
    public static final String ADD_MANAGER_CONSOLE_REJECT = messageProps.getProperty("ADD_MANAGER_CONSOLE_REJECT");
    public static final String REMOVE_MANAGER_SUCCESS = messageProps.getProperty("REMOVE_MANAGER_SUCCESS");
    public static final String REMOVE_MANAGER_CONSOLE_REJECT = messageProps.getProperty("REMOVE_MANAGER_CONSOLE_REJECT");
    public static final String BALANCE_DEPOSIT_CONSOLE_REJECT = messageProps.getProperty("BALANCE_DEPOSIT_CONSOLE_REJECT");
    public static final String BALANCE_WITHDRAW_CONSOLE_REJECT = messageProps.getProperty("BALANCE_WITHDRAW_CONSOLE_REJECT");
    public static final String BROWSE_CONSOLE_REJECT = messageProps.getProperty("BROWSE_CONSOLE_REJECT");
    public static final String BUY_CONSOLE_REJECT = messageProps.getProperty("BUY_CONSOLE_REJECT");
    public static final String CREATE_SUCCESS = messageProps.getProperty("CREATE_SUCCESS");
    public static final String CREATE_NOT_ENOUGH_POINTS = messageProps.getProperty("CREATE_NOT_ENOUGH_POINTS");
    public static final String CREATE_NO_REGION = messageProps.getProperty("CREATE_NO_REGION");
    public static final String CREATE_NO_NAME = messageProps.getProperty("CREATE_NO_NAME");
    public static final String CREATE_CONSOLE_REJECT = messageProps.getProperty("CREATE_CONSOLE_REJECT");
    public static final String DESTROY_SUCCESS = messageProps.getProperty("DESTROY_SUCCESS");
    public static final String DESTROY_NO_PERMISSION = messageProps.getProperty("DESTROY_NO_PERMISSION");
    public static final String DESTROY_CONSOLE_REJECT = messageProps.getProperty("DESTROY_CONSOLE_REJECT");
    public static final String RENT_CONSOLE_REJECT = messageProps.getProperty("RENT_CONSOLE_REJECT");
    public static final String SELECT_CLEAR = messageProps.getProperty("SELECT_CLEAR");
    public static final String SELECT_UNKNOWN_TYPE = messageProps.getProperty("SELECT_UNKNOWN_TYPE");
    public static final String SELECT_CONSOLE_REJECT = messageProps.getProperty("SELECT_CONSOLE_REJECT");
    public static final String SET_OWNER_CONSOLE_REJECT = messageProps.getProperty("SET_OWNER_CONSOLE_REJECT");
    public static final String SET_NAME_CONSOLE_REJECT = messageProps.getProperty("SET_NAME_CONSOLE_REJECT");
    public static final String SET_PRICE_CONSOLE_REJECT = messageProps.getProperty("SET_PRICE_CONSOLE_REJECT");
    public static final String SET_RENT_CONSOLE_REJECT = messageProps.getProperty("SET_RENT_CONSOLE_REJECT");
    public static final String SET_TYPE_CONSOLE_REJECT = messageProps.getProperty("SET_TYPE_CONSOLE_REJECT");
    public static final String SET_UNLIMITED_UNKNOWN_SHOP_ATTRIBUTE = messageProps.getProperty("SET_UNLIMITED_UNKNOWN_SHOP_ATTRIBUTE");
    public static final String SET_UNLIMITED_CONSOLE_REJECT = messageProps.getProperty("SET_UNLIMITED_CONSOLE_REJECT");

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
