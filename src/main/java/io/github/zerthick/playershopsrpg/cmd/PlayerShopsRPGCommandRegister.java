package io.github.zerthick.playershopsrpg.cmd;

import io.github.zerthick.playershopsrpg.cmd.cmdexecutors.ShopCreateExecutor;
import io.github.zerthick.playershopsrpg.cmd.cmdexecutors.ShopDestroyExecutor;
import io.github.zerthick.playershopsrpg.cmd.cmdexecutors.ShopExecutor;
import io.github.zerthick.playershopsrpg.cmd.cmdexecutors.ShopSelectExecutor;
import org.spongepowered.api.Sponge;
import org.spongepowered.api.command.spec.CommandSpec;
import org.spongepowered.api.plugin.PluginContainer;
import org.spongepowered.api.text.Text;

public class PlayerShopsRPGCommandRegister {

    private PluginContainer container;

    public PlayerShopsRPGCommandRegister(PluginContainer container) {
        this.container = container;
    }

    public void registerCmds() {

        // shop destroy
        CommandSpec shopDestroyCommand = CommandSpec.builder()
                .description(Text.of("Destroys the shop you are currently standing in"))
                .permission("playershopsrpg.command.destroy")
                .executor(new ShopDestroyExecutor(container))
                .build();

        // shop create <Name>
        CommandSpec shopCreateCommand = CommandSpec.builder()
                .description(Text.of("Creates a shop in the region selected by shop select command"))
                .permission("playershopsrpg.command.create")
                .executor(new ShopCreateExecutor(container))
                .build();

        // shop select
        CommandSpec shopSelectCommmand = CommandSpec.builder()
                .description(Text.of("Selects a region to create a shop"))
                .permission("playershopsrpg.command.select")
                .executor(new ShopSelectExecutor(container))
                .build();

        // shop
        CommandSpec shopCommand = CommandSpec.builder()
                .description(Text.of("/shop [select|create]"))
                .permission("playershopsrpg.command.help")
                .executor(new ShopExecutor(container))
                .child(shopSelectCommmand, "select")
                .child(shopCreateCommand, "create")
                .child(shopDestroyCommand, "destroy")
                .build();

        Sponge.getGame().getCommandManager().register(container.getInstance().get(), shopCommand, "shop");
    }
}
