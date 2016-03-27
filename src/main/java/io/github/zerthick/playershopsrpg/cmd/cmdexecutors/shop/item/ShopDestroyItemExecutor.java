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

import java.util.Optional;

public class ShopDestroyItemExecutor extends AbstractCmdExecutor {

    public ShopDestroyItemExecutor(PluginContainer pluginContainer) {
        super(pluginContainer);
    }

    @Override
    public CommandResult execute(CommandSource src, CommandContext args) throws CommandException {

        if (src instanceof Player) {
            Player player = (Player) src;

            Optional<Integer> itemIndexOptional = args.getOne(Text.of("ItemIndex"));
            if (itemIndexOptional.isPresent()) {
                Optional<ShopContainer> shopContainerOptional = shopManager.getShop(player);
                if (shopContainerOptional.isPresent()) {
                    ShopContainer shopContainer = shopContainerOptional.get();
                    Shop shop = shopContainer.getShop();

                    ShopTransactionResult transactionResult = shop.destroyItem(player, itemIndexOptional.get());
                    if (transactionResult != ShopTransactionResult.SUCCESS) {
                        player.sendMessage(ChatTypes.CHAT, Text.of(TextColors.RED, transactionResult.getMessage()));
                    } else {
                        player.sendMessage(ChatTypes.CHAT, Text.of(TextColors.BLUE, "Successfully destroyed item at index ",
                                TextColors.AQUA, itemIndexOptional.get(), TextColors.BLUE, " in ", TextColors.AQUA, shop.getName()));
                    }
                } else {
                    player.sendMessage(ChatTypes.CHAT, Text.of(TextColors.RED, "You are not in a shop!"));
                }
            } else {
                player.sendMessage(ChatTypes.CHAT, Text.of(TextColors.RED, "You must specify an item index!"));
            }
            return CommandResult.success();
        }

        src.sendMessage(Text.of("You cannot create items in shops from the console!"));
        return CommandResult.success();
    }
}
