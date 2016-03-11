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
import org.spongepowered.api.text.format.TextColors;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

public class ShopSelectExecutor extends AbstractCmdExecutor {


    public ShopSelectExecutor(PluginContainer pluginContainer) {
        super(pluginContainer);
    }

    public static Map<String, String> selectChoices() {
        Map<String, String> selectChoices = new HashMap<>();
        selectChoices.put("rectangular", "rectangular");
        selectChoices.put("clear", "clear");
        return selectChoices;
    }

    @Override
    public CommandResult execute(CommandSource src, CommandContext args) throws CommandException {

        if (src instanceof Player) {

            Player player = (Player) src;

            String selectType = "rectangular";

            Optional<String> selectTypeOption = args.getOne(Text.of("SelectionType"));

            if (selectTypeOption.isPresent()) {
                selectType = selectTypeOption.get();
            }

            switch (selectType) {
                case "rectangular":
                    RectangularRegionBuffer regionBuffer = new RectangularRegionBuffer();
                    plugin.getRegionSelectBuffer().addBuffer(player.getUniqueId(), regionBuffer);
                    player.sendMessage(ChatTypes.CHAT, regionBuffer.getProgressionMessage());
                    break;
                case "clear":
                    plugin.getRegionSelectBuffer().removeBuffer(player.getUniqueId());
                    player.sendMessage(ChatTypes.CHAT, Text.of(TextColors.BLUE, "Selection Cleared!"));
                    break;
                default:
                    player.sendMessage(ChatTypes.CHAT, Text.of(TextColors.RED, "Unknown Selection Type!"));
            }
            return CommandResult.success();
        }

        src.sendMessage(Text.of("You cannot select shop regions from the console!"));
        return CommandResult.success();
    }
}
