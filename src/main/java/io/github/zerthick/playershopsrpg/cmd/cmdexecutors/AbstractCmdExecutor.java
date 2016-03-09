package io.github.zerthick.playershopsrpg.cmd.cmdexecutors;

import io.github.zerthick.playershopsrpg.PlayerShopsRPG;
import io.github.zerthick.playershopsrpg.shop.ShopManager;
import org.spongepowered.api.command.spec.CommandExecutor;
import org.spongepowered.api.plugin.PluginContainer;

public abstract class AbstractCmdExecutor implements CommandExecutor {

    protected PluginContainer container;
    protected PlayerShopsRPG plugin;
    protected ShopManager shopManager;

    public AbstractCmdExecutor(PluginContainer pluginContainer) {
        super();
        container = pluginContainer;
        plugin = container.getInstance().get() instanceof PlayerShopsRPG ? (PlayerShopsRPG) container.getInstance().get() : null;
        shopManager = plugin.getShopManager();
    }
}
