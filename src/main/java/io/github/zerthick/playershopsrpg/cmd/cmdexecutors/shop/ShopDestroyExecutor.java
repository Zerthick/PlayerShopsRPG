package io.github.zerthick.playershopsrpg.cmd.cmdexecutors.shop;


import io.github.zerthick.playershopsrpg.cmd.cmdexecutors.AbstractCmdExecutor;
import io.github.zerthick.playershopsrpg.shop.ShopContainer;
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

public class ShopDestroyExecutor extends AbstractCmdExecutor {

    public ShopDestroyExecutor(PluginContainer pluginContainer) {
        super(pluginContainer);
    }

    @Override
    public CommandResult execute(CommandSource src, CommandContext args) throws CommandException {

        if (src instanceof Player) {
            Player player = (Player) src;

            Optional<ShopContainer> shopContainerOptional = shopManager.getShop(player);
            if (shopContainerOptional.isPresent()) {
                ShopContainer shopContainer = shopContainerOptional.get();
                if (shopContainer.getShop().hasOwnerPermissions(player) || player.hasPermission("playershopsrpg.bypass.destroy")) {
                    shopManager.removeShop(player);
                    player.sendMessage(ChatTypes.CHAT, Text.of(TextColors.BLUE, "Successfully destroyed ", TextColors.AQUA, shopContainer.getShop().getName(), TextColors.BLUE, "!"));
                } else {
                    player.sendMessage(ChatTypes.CHAT, Text.of(TextColors.RED, "You do not have permission to destroy this shop!"));
                }
            } else {
                player.sendMessage(ChatTypes.CHAT, Text.of(TextColors.RED, "You are not in a shop!"));
            }
            return CommandResult.success();
        }

        src.sendMessage(Text.of("You cannot destroy shop regions from the console!"));
        return CommandResult.success();
    }
}