package io.github.zerthick.playershopsrpg.cmd.cmdexecutors.shop;

import io.github.zerthick.playershopsrpg.cmd.cmdexecutors.AbstractCmdExecutor;
import io.github.zerthick.playershopsrpg.region.Region;
import io.github.zerthick.playershopsrpg.region.selectbuffer.RegionBuffer;
import io.github.zerthick.playershopsrpg.shop.Shop;
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

public class ShopCreateExecutor extends AbstractCmdExecutor {

    public ShopCreateExecutor(PluginContainer pluginContainer) {
        super(pluginContainer);
    }

    @Override
    public CommandResult execute(CommandSource src, CommandContext args) throws CommandException {

        if (src instanceof Player) {
            Player player = (Player) src;

            Optional<String> shopNameOptional = args.getOne(Text.of("ShopName"));

            if (shopNameOptional.isPresent()) {
                String shopName = shopNameOptional.get();
                Optional<RegionBuffer> regionBufferOptional = plugin.getRegionSelectBuffer().getBuffer(player.getUniqueId());
                //If the player has previously selected points for the shop
                if (regionBufferOptional.isPresent()) {
                    RegionBuffer regionBuffer = regionBufferOptional.get();
                    Optional<Region> regionOptional = regionBuffer.getRegion();
                    //If the points selected are enough to create a region
                    if (regionOptional.isPresent()) {
                        ShopContainer shopContainer =
                                new ShopContainer(new Shop(shopName, player.getUniqueId()), regionOptional.get());
                        shopManager.addShop(player.getWorld().getUniqueId(), shopContainer);
                        plugin.getRegionSelectBuffer().removeBuffer(player.getUniqueId());
                        player.sendMessage(ChatTypes.CHAT, Text.of(TextColors.BLUE, "Successfully created ", TextColors.AQUA, shopName, TextColors.BLUE, " encompassing points: ", regionOptional.get()));
                    } else {
                        player.sendMessage(ChatTypes.CHAT,
                                Text.of(TextColors.RED, "Not enough points selected! Use /shop select to select a region."));
                    }
                } else {
                    player.sendMessage(ChatTypes.CHAT,
                            Text.of(TextColors.RED, "No region selected! Use /shop select to select a region."));
                }
            } else {
                player.sendMessage(ChatTypes.CHAT,
                        Text.of(TextColors.RED, "You must specify a shop name!"));
            }
            return CommandResult.success();
        }

        src.sendMessage(Text.of("You cannot create shop regions from the console!"));
        return CommandResult.success();
    }
}
