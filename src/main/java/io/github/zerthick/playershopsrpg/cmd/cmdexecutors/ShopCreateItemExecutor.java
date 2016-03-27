package io.github.zerthick.playershopsrpg.cmd.cmdexecutors;

import io.github.zerthick.playershopsrpg.shop.Shop;
import io.github.zerthick.playershopsrpg.shop.ShopContainer;
import io.github.zerthick.playershopsrpg.shop.ShopItemUtils;
import io.github.zerthick.playershopsrpg.shop.ShopTransactionResult;
import org.spongepowered.api.command.CommandException;
import org.spongepowered.api.command.CommandResult;
import org.spongepowered.api.command.CommandSource;
import org.spongepowered.api.command.args.CommandContext;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.item.inventory.ItemStack;
import org.spongepowered.api.plugin.PluginContainer;
import org.spongepowered.api.text.Text;
import org.spongepowered.api.text.chat.ChatTypes;
import org.spongepowered.api.text.format.TextColors;

import java.util.Optional;

public class ShopCreateItemExecutor extends AbstractCmdExecutor {

    public ShopCreateItemExecutor(PluginContainer pluginContainer) {
        super(pluginContainer);
    }

    @Override
    public CommandResult execute(CommandSource src, CommandContext args) throws CommandException {

        if (src instanceof Player) {
            Player player = (Player) src;

            Optional<ShopContainer> shopContainerOptional = shopManager.getShop(player);
            if (shopContainerOptional.isPresent()) {
                ShopContainer shopContainer = shopContainerOptional.get();
                Shop shop = shopContainer.getShop();
                ShopTransactionResult transactionResult;
                if (player.getItemInHand().isPresent()) {
                    ItemStack item = player.getItemInHand().get();
                    transactionResult = shop.createItem(player, item);

                    if (transactionResult != ShopTransactionResult.SUCCESS) {
                        player.sendMessage(ChatTypes.CHAT, Text.of(TextColors.RED, transactionResult.getMessage()));
                    } else {
                        player.sendMessage(ChatTypes.CHAT, Text.of(TextColors.BLUE, "Successfully created ",
                                TextColors.AQUA, ShopItemUtils.getItemName(item), TextColors.BLUE, " in ", TextColors.AQUA, shop.getName()));
                    }
                }
            } else {
                player.sendMessage(ChatTypes.CHAT, Text.of(TextColors.RED, "You are not in a shop!"));
            }
            return CommandResult.success();
        }

        src.sendMessage(Text.of("You cannot create items in shops from the console!"));
        return CommandResult.success();
    }
}
