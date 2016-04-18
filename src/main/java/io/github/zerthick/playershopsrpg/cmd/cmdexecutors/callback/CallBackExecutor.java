package io.github.zerthick.playershopsrpg.cmd.cmdexecutors.callback;

import io.github.zerthick.playershopsrpg.cmd.cmdexecutors.AbstractCmdExecutor;
import org.spongepowered.api.command.CommandException;
import org.spongepowered.api.command.CommandResult;
import org.spongepowered.api.command.CommandSource;
import org.spongepowered.api.command.args.CommandContext;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.plugin.PluginContainer;
import org.spongepowered.api.text.Text;
import org.spongepowered.api.text.format.TextColors;

import java.util.Optional;

public class CallBackExecutor extends AbstractCmdExecutor {

    public CallBackExecutor(PluginContainer pluginContainer) {
        super(pluginContainer);
    }

    @Override
    public CommandResult execute(CommandSource src, CommandContext args) throws CommandException {

        if (src instanceof Player) {
            Player player = (Player) src;

            Optional<String> message = args.getOne(Text.of("message"));
            Optional<String> command = args.getOne(Text.of("command"));

            if (message.isPresent() && command.isPresent()) {
                player.sendMessage(Text.of(TextColors.BLUE, message.get()));
                plugin.getCallBackBuffer().addCallBack(player, command.get());
            }
        }
        return CommandResult.success();
    }
}
