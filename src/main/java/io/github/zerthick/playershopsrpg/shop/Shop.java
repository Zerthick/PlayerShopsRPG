package io.github.zerthick.playershopsrpg.shop;

import org.spongepowered.api.service.economy.account.Account;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class Shop {
    private final String name;
    private UUID ownerUUID;
    private List<ShopItem> items;
    private boolean unlimitedMoney;
    private boolean unlimitedStock;

    public Shop(String name, UUID ownerUUID) {
        this.name = name;
        this.ownerUUID = ownerUUID;
        items = new ArrayList<>();
        unlimitedMoney = false;
        unlimitedStock = false;
    }

    public Shop(String name, UUID ownerUUID, List<ShopItem> items, boolean unlimitedMoney, boolean unlimitedStock) {
        this.name = name;
        this.ownerUUID = ownerUUID;
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
