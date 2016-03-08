package io.github.zerthick.playershopsrpg.shop;

import io.github.zerthick.playershopsrpg.utils.econ.EconManager;
import org.spongepowered.api.CatalogType;
import org.spongepowered.api.service.economy.account.Account;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class Shop {
    private final String name;
    private UUID ownerUUID;
    private String currencyID;
    private List<ShopItem> items;
    private boolean unlimitedMoney;
    private boolean unlimitedStock;

    public Shop(String name, UUID ownerUUID) {
        this.name = name;
        this.ownerUUID = ownerUUID;
        CatalogType currencyType = (CatalogType) EconManager.getInstance().getDefaultCurrency();
        currencyID = currencyType.getId();
        items = new ArrayList<>();
        unlimitedMoney = false;
        unlimitedStock = false;
    }

    public Shop(String name, UUID ownerUUID, String currency, List<ShopItem> items, boolean unlimitedMoney, boolean unlimitedStock) {
        this.name = name;
        this.ownerUUID = ownerUUID;
        this.currencyID = currency;
        this.items = items;
        this.unlimitedMoney = unlimitedMoney;
        this.unlimitedStock = unlimitedStock;
    }

    public String getName() {
        return name;
    }

    public UUID getOwnerUUID() {
        return ownerUUID;
    }

    public void setOwnerUUID(UUID ownerUUID) {
        this.ownerUUID = ownerUUID;
    }

    public String getCurrencyID() {
        return currencyID;
    }

    public void setCurrencyID(String currencyID) {
        this.currencyID = currencyID;
    }

    public List<ShopItem> getItems() {
        return items;
    }

    public void setItems(List<ShopItem> items) {
        this.items = items;
    }

    public boolean isUnlimitedMoney() {
        return unlimitedMoney;
    }

    public void setUnlimitedMoney(boolean unlimitedMoney) {
        this.unlimitedMoney = unlimitedMoney;
    }

    public boolean isUnlimitedStock() {
        return unlimitedStock;
    }

    public void setUnlimitedStock(boolean unlimitedStock) {
        this.unlimitedStock = unlimitedStock;
    }

    public void buyItem(Account playerAccount, int index){

    }

    public void sellItem(Account playerAccoount, int index){

    }

    public void addItem(ShopItem item){
        items.add(item);
    }

    public ShopItem removeItem(int index){
        return items.remove(index);
    }

    public ShopItem getItem(int index){
        return items.get(index);
    }
}
