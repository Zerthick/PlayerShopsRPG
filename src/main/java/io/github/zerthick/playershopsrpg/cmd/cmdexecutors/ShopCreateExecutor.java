package io.github.zerthick.playershopsrpg.cmd.cmdexecutors;

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
            Optional<RegionBuffer> regionBufferOptional = plugin.getRegionSelectBuffer().getBuffer(player.getUniqueId());
            if (regionBufferOptional.isPresent()) {
                RegionBuffer regionBuffer = regionBufferOptional.get();
                Optional<Region> regionOptional = regionBuffer.getRegion();
                if (regionOptional.isPresent()) {
                    ShopContainer shopContainer =
                            new ShopContainer(new Shop("Test", player.getUniqueId()), regionOptional.get());
                    plugin.getShopManager().addShop(player.getWorld().getUniqueId(), shopContainer);
                    plugin.getRegionSelectBuffer().removeBuffer(player.getUniqueId());
                    player.sendMessage(ChatTypes.CHAT, Text.of(TextColors.BLUE, "Successfully created shop encompassing points: ", regionOptional.get()));
                } else {
                    player.sendMessage(ChatTypes.CHAT,
                            Text.of(TextColors.RED, "Not enough points selected! Use /shop select to select a region."));
                }
            } else {
                player.sendMessage(ChatTypes.CHAT,
                        Text.of(TextColors.RED, "No region selected! Use /shop select to select a region."));
            }
            return CommandResult.success();
        }

        src.sendMessage(Text.of("You cannot select shop regions from the console!"));
        return CommandResult.success();
    }
}
