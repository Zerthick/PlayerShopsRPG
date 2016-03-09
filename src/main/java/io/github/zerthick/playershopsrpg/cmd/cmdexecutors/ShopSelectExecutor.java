package io.github.zerthick.playershopsrpg.cmd.cmdexecutors;

import io.github.zerthick.playershopsrpg.region.selectbuffer.RectangularRegionBuffer;
import org.spongepowered.api.command.CommandException;
import org.spongepowered.api.command.CommandResult;
import org.spongepowered.api.command.CommandSource;
import org.spongepowered.api.command.args.CommandContext;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.plugin.PluginContainer;
import org.spongepowered.api.text.Text;
import org.spongepowered.api.text.chat.ChatTypes;

public class ShopSelectExecutor extends AbstractCmdExecutor {


    public ShopSelectExecutor(PluginContainer pluginContainer) {
        super(pluginContainer);
    }

    @Override
    public CommandResult execute(CommandSource src, CommandContext args) throws CommandException {

        if (src instanceof Player) {
            Player player = (Player) src;
            RectangularRegionBuffer regionBuffer = new RectangularRegionBuffer();
            plugin.getRegionSelectBuffer().addBuffer(player.getUniqueId(), regionBuffer);
            player.sendMessage(ChatTypes.CHAT, regionBuffer.getProgressionMessage());
            return CommandResult.success();
        }

        src.sendMessage(Text.of("You cannot select shop regions from the console!"));
        return CommandResult.success();
    }
}
