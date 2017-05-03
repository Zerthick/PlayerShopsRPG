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

package io.github.zerthick.playershopsrpg.utils.econ;

import org.spongepowered.api.service.economy.Currency;
import org.spongepowered.api.service.economy.EconomyService;
import org.spongepowered.api.service.economy.account.Account;
import org.spongepowered.api.service.economy.account.UniqueAccount;

import java.util.Optional;
import java.util.Set;
import java.util.UUID;

public class EconManager {

    private static EconManager instance = null;
    private  EconomyService economyService;

    protected EconManager(){
        //Singleton Design Pattern
    }

    public static EconManager getInstance(){
        if(instance == null){
            instance = new EconManager();
        }
        return instance;
    }

    public void hookEconService(EconomyService economyService) {
        this.economyService = economyService;
    }

    public Set<Currency> getCurrencies(){
        return economyService.getCurrencies();
    }
    public Currency getDefaultCurrency(){
        return economyService.getDefaultCurrency();
    }

    public Optional<Account> getOrCreateAccount(String identifier){
        return economyService.getOrCreateAccount(identifier);
    }

    public Optional<UniqueAccount> getOrCreateAccount(UUID uuid){
        return economyService.getOrCreateAccount(uuid);
    }

    public boolean hasAccount(String identifier){
        return economyService.hasAccount(identifier);
    }
    public boolean hasAccount(UUID uuid){
        return economyService.hasAccount(uuid);
    }

}
