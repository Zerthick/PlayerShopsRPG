package io.github.zerthick.playershopsrpg.shop;

import io.github.zerthick.playershopsrpg.utils.inventory.InventoryUtils;
import org.spongepowered.api.Sponge;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.service.pagination.PaginationService;
import org.spongepowered.api.text.Text;
import org.spongepowered.api.text.action.TextActions;
import org.spongepowered.api.text.format.TextColors;
import org.spongepowered.api.text.format.TextStyles;

import java.util.ArrayList;
import java.util.List;

public class ShopItemUtils {

    private static PaginationService pagServ = Sponge.getServiceManager().provide(PaginationService.class).get();

    public static void sendShopBuyView(Player player, Shop shop, boolean unlimitedStock) {

        //First build up the contents of the shop
        List<Text> contents = new ArrayList<>();
        List<ShopItem> items = shop.getItems();
        if (items.isEmpty()) {
            contents.add(Text.of(TextColors.BLUE, "No items to display."));
        }

        for (int i = 0; i < items.size(); i++) {
            ShopItem item = items.get(i);

            Text itemAmount;
            if (unlimitedStock) {
                itemAmount = Text.of("\u221E");
            } else {
                itemAmount = Text.of(item.getItemAmount() == -1 ? "--" : String.valueOf(item.getItemAmount()));
            }

            //Grab the necessary info we need from the item
            Text itemName = InventoryUtils.getItemName(item.getItemStack());
            Text itemMax = Text.of(item.getItemMaxAmount() == -1 ? "\u221E" : String.valueOf(item.getItemMaxAmount()));
            Text itemSell = Text.of(item.getItemBuyPrice() == -1 ? "--" : String.valueOf(item.getItemBuyPrice()));
            Text itemBuy = Text.of(item.getItemSellPrice() == -1 ? "--" : String.valueOf(item.getItemSellPrice()));

            //Add the appropriate actions to the text
            itemName = itemName.toBuilder().onHover(TextActions.showItem(item.getItemStack())).style(TextStyles.UNDERLINE).build();
            Text buy = Text.builder("Buy")
                    .onClick(TextActions.runCommand("/shop callBack \"How many " + itemName.toPlain() + " would you like to buy?\" shop item buy " + i + " %c"))
                    .style(TextStyles.UNDERLINE).build();
            Text sell = Text.builder("Sell")
                    .onClick(TextActions.runCommand("/shop callBack \"How many " + itemName.toPlain() + " would you like to sell?\" shop item sell " + i + " %c"))
                    .style(TextStyles.UNDERLINE).build();

            //Build the full line of text
            Text fullLine = Text.of(itemName, " ", itemAmount, "/", itemMax, " | ", itemBuy, " | ", itemSell, " ", buy, " ", sell);

            //Add the text to the shop display
            contents.add(fullLine);
        }

        //Builder header
        Text header = Text.of(TextColors.BLUE, "Shop's Balance: ", TextColors.WHITE, shop.getBalance());
        if (shop.hasManagerPermissions(player)) {
            Text manager = Text.builder("Manager")
                    .onClick(TextActions.runCommand("/shop browse manager"))
                    .style(TextStyles.UNDERLINE).build();
            header = header.concat(Text.of("  |  ", manager));
        }
        if (shop.hasOwnerPermissions(player)) {
            Text owner = Text.builder("Owner")
                    .onClick(TextActions.runCommand("/shop browse owner"))
                    .style(TextStyles.UNDERLINE).build();
            header = header.concat(Text.of("  |  ", owner));
        }
        header = header.concat(Text.of("\n"));

        pagServ.builder()
                .title(Text.of(shop.getName()))
                .header(header)
                .padding(Text.of(TextColors.BLUE, "-"))
                .contents(contents)
                .sendTo(player);

    }
}
