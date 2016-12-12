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
    //Commands
    public static final String YOU_ARE_NOT_IN_A_SHOP = "You are not in a shop!";
    public static final String ADD_ITEM_CONSOLE_REJECT = "You cannot add items to shops from the console!";
    public static final String BUY_ITEM_CONSOLE_REJECT = "You cannot buy items from shops from the console!";
    public static final String CREATE_ITEM_SUCCESS = "Successfully created {ITEM_NAME} in {SHOP_NAME}.";
    public static final String CREATE_ITEM_TYPE_REJECT = "{SHOP_TYPE}s are not allowed to buy and sell {ITEM_NAME}!";
    public static final String CREATE_ITEM_CONSOLE_REJECT = "You cannot create items in shops from the console!";
    public static final String DESTROY_ITEM_SUCCESS = "Successfully destroyed item at index {ITEM_INDEX} in {SHOP_NAME}";
    public static final String DESTROY_ITEM_CONSOLE_REJECT = "You cannot destroy items from shops from the console!";
    public static final String REMOVE_ITEM_CONSOLE_REJECT = "You cannot remove items from shops from the console!";
    public static final String SELL_ITEM_CONSOLE_REJECT = "You cannot sell items to shops from the console!";
    public static final String SET_ITEM_UNKNOWN_ATTRIBUTE = "Unknown item attribute!";
    public static final String SET_ITEM_CONSOLE_REJECT = "You cannot set item attributes from the console!";
    public static final String ADD_MANAGER_SUCCESS = "Successfully added {PLAYER_NAME} as manager of {SHOP_NAME}.";
    public static final String ADD_MANAGER_CONSOLE_REJECT = "You cannot add managers to shops from the console!";
    public static final String REMOVE_MANAGER_SUCCESS = "Successfully removed {PLAYER_NAME} as manager of {SHOP_NAME}.";
    public static final String REMOVE_MANAGER_CONSOLE_REJECT = "You cannot remove managers from shops from the console!";
    public static final String BALANCE_DEPOSIT_CONSOLE_REJECT = "You cannot deposit funds to shops from the console!";
    public static final String BALANCE_WITHDRAW_CONSOLE_REJECT = "You cannot withdraw funds from shops from the console!";
    public static final String BROWSE_CONSOLE_REJECT = "You cannot browse shops from the console!";
    public static final String BUY_CONSOLE_REJECT = "You cannot buy shops from the console!";
    public static final String CREATE_SUCCESS = "Successfully created {SHOP_NAME} encompassing points: {SHOP_CORDS}";
    public static final String CREATE_NOT_ENOUGH_POINTS = "Not enough points selected! Use /shop select to select a region.";
    public static final String CREATE_NO_REGION = "No region selected! Use /shop select to select a region.";
    public static final String CREATE_NO_NAME = "You must specify a shop name!";
    public static final String CREATE_CONSOLE_REJECT = "You cannot create shop regions from the console!";
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
