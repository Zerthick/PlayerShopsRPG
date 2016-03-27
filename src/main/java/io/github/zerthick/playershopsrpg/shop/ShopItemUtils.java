package io.github.zerthick.playershopsrpg.shop;

import org.spongepowered.api.Sponge;
import org.spongepowered.api.data.key.Keys;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.item.inventory.ItemStack;
import org.spongepowered.api.item.inventory.ItemStackComparators;
import org.spongepowered.api.service.pagination.PaginationService;
import org.spongepowered.api.text.Text;
import org.spongepowered.api.text.action.TextActions;
import org.spongepowered.api.text.format.TextColors;
import org.spongepowered.api.text.format.TextStyles;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

public class ShopItemUtils {

    private static PaginationService pagServ = Sponge.getServiceManager().provide(PaginationService.class).get();

    public static void sendShopBuyView(Player player, Shop shop, boolean unlimitedStock) {

        //First build up the contents of the shop
        List<Text> contents = new ArrayList<>();
        List<ShopItem> items = shop.getItems();
        for (int i = 0; i < items.size(); i++) {
            ShopItem item = items.get(i);

            Text itemAmount;
            if (unlimitedStock) {
                itemAmount = Text.of("\u221E");
            } else {
                itemAmount = Text.of(item.getItemAmount() == -1 ? "--" : String.valueOf(item.getItemAmount()));
            }

            //Grab the necessary info we need from the item
            Text itemName = getItemName(item.getItemStack());
            Text itemMax = Text.of(item.getItemMaxAmount() == -1 ? "\u221E" : String.valueOf(item.getItemMaxAmount()));
            Text itemBuy = Text.of(item.getItemBuyPrice() == -1 ? "--" : String.valueOf(item.getItemBuyPrice()));
            Text itemSell = Text.of(item.getItemSellPrice() == -1 ? "--" : String.valueOf(item.getItemSellPrice()));

            //Add the appropriate actions to the text
            itemName = itemName.toBuilder().onHover(TextActions.showItem(item.getItemStack())).style(TextStyles.UNDERLINE).build();
            Text buy = Text.builder("Buy").onClick(TextActions.runCommand("shop buy " + i)).style(TextStyles.UNDERLINE).build();
            Text sell = Text.builder("Sell").onClick(TextActions.runCommand("shop sell " + i)).style(TextStyles.UNDERLINE).build();

            //Build the full line of text
            Text fullLine = Text.of(itemName, " ", itemAmount, "/", itemMax, " ", itemBuy, "/", itemSell, " ", buy, " ", sell);

            //Add the text to the shop display
            contents.add(fullLine);
        }

        pagServ.builder()
                .header(Text.of(TextColors.AQUA, shop.getName()))
                .contents(contents)
                .sendTo(player);

    }

    public static boolean itemStackEqualsIgnoreSize(ItemStack o1, ItemStack o2) {
        Comparator<ItemStack> typeComparator = ItemStackComparators.TYPE;
        Comparator<ItemStack> dataComparator = ItemStackComparators.ITEM_DATA;
        Comparator<ItemStack> propertiesComparator = ItemStackComparators.PROPERTIES;

        return (typeComparator.compare(o1, o2) == 0) && (dataComparator.compare(o1, o2) == 0)
                && (propertiesComparator.compare(o1, o2) == 0) && (getItemName(o1).equals(getItemName(o2)));
    }

    public static Text getItemName(ItemStack itemStack) {
        return itemStack.get(Keys.DISPLAY_NAME).orElse(Text.of(itemStack.getTranslation().get()));
    }
}
