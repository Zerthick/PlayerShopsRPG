package io.github.zerthick.playershopsrpg.cmd.cmdexecutors.shop.item;

import io.github.zerthick.playershopsrpg.cmd.cmdexecutors.AbstractCmdExecutor;
import io.github.zerthick.playershopsrpg.shop.Shop;
import io.github.zerthick.playershopsrpg.shop.ShopContainer;
import io.github.zerthick.playershopsrpg.shop.ShopTransactionResult;
import org.spongepowered.api.command.CommandException;
import org.spongepowered.api.command.CommandResult;
import org.spongepowered.api.command.CommandSource;
import org.spongepowered.api.command.args.CommandContext;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.plugin.PluginContainer;
import org.spongepowered.api.text.Text;
import org.spongepowered.api.text.chat.ChatTypes;
import org.spongepowered.api.text.format.TextColors;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

public class ShopSetItemExecutor extends AbstractCmdExecutor {

    public ShopSetItemExecutor(PluginContainer pluginContainer) {
        super(pluginContainer);
    }

    public static Map<String, String> selectChoices() {
        Map<String, String> selectChoices = new HashMap<>();
        selectChoices.put("max", "max");
        selectChoices.put("buy", "buy");
        selectChoices.put("sell", "sell");
        return selectChoices;
    }

    @Override
    public CommandResult execute(CommandSource src, CommandContext args) throws CommandException {

        if (src instanceof Player) {
            Player player = (Player) src;

            Optional<String> selectTypeOptional = args.getOne(Text.of("SelectionType"));
            Optional<Integer> itemIndexArgumentOptional = args.getOne(Text.of("ItemIndex"));
            Optional<Double> doubleArgumentOptional = args.getOne(Text.of("DoubleArgument"));

            if (selectTypeOptional.isPresent() && itemIndexArgumentOptional.isPresent() && doubleArgumentOptional.isPresent()) {
                String selectType = selectTypeOptional.get();
                int itemIndex = itemIndexArgumentOptional.get();
                double doubleArg = doubleArgumentOptional.get();

                Optional<ShopContainer> shopContainerOptional = shopManager.getShop(player);
                if (shopContainerOptional.isPresent()) {
                    ShopContainer shopContainer = shopContainerOptional.get();
                    Shop shop = shopContainer.getShop();
                    ShopTransactionResult transactionResult;

                    switch (selectType) {
                        case "max":
                            transactionResult = shop.setItemMax(player, itemIndex, (int) doubleArg);
                            break;
                        case "buy":
                            transactionResult = shop.setItemBuyPrice(player, itemIndex, doubleArg);
                            break;
                        case "sell":
                            transactionResult = shop.setItemSellPrice(player, itemIndex, doubleArg);
                            break;
                        default:
                            transactionResult = new ShopTransactionResult("Unknown item attribute!");
                    }

                    if (transactionResult != ShopTransactionResult.SUCCESS) {
                        player.sendMessage(ChatTypes.CHAT, Text.of(TextColors.RED, transactionResult.getMessage()));
                    }
                } else {
                    player.sendMessage(ChatTypes.CHAT, Text.of(TextColors.RED, "You are not in a shop!"));
                }

                return CommandResult.success();
            }
        }

        src.sendMessage(Text.of("You cannot set item attributes from the console!"));
        return CommandResult.success();
    }
}
