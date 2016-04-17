package io.github.zerthick.playershopsrpg;

import com.google.inject.Inject;
import io.github.zerthick.playershopsrpg.cmd.PlayerShopsRPGCommandRegister;
import io.github.zerthick.playershopsrpg.region.selectbuffer.RegionBuffer;
import io.github.zerthick.playershopsrpg.region.selectbuffer.RegionSelectBuffer;
import io.github.zerthick.playershopsrpg.shop.ShopManager;
import io.github.zerthick.playershopsrpg.utils.config.ConfigManager;
import io.github.zerthick.playershopsrpg.utils.econ.EconManager;
import ninja.leaping.configurate.commented.CommentedConfigurationNode;
import ninja.leaping.configurate.loader.ConfigurationLoader;
import org.slf4j.Logger;
import org.spongepowered.api.config.ConfigDir;
import org.spongepowered.api.config.DefaultConfig;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.event.Listener;
import org.spongepowered.api.event.block.InteractBlockEvent;
import org.spongepowered.api.event.filter.cause.Root;
import org.spongepowered.api.event.game.state.GameInitializationEvent;
import org.spongepowered.api.event.game.state.GameStartedServerEvent;
import org.spongepowered.api.event.game.state.GameStoppedEvent;
import org.spongepowered.api.event.service.ChangeServiceProviderEvent;
import org.spongepowered.api.plugin.Plugin;
import org.spongepowered.api.plugin.PluginContainer;
import org.spongepowered.api.service.economy.EconomyService;
import org.spongepowered.api.text.chat.ChatTypes;

import java.nio.file.Path;
import java.util.Optional;

@Plugin(id = "playershopsrpg", name = "PlayerShopsRPG", version = "0.0.1")
public class PlayerShopsRPG {

    private ShopManager shopManager;
    private EconManager econManager;
    private RegionSelectBuffer regionSelectBuffer;

    @Inject
    private Logger logger;
    @Inject
    private PluginContainer instance;

    //Config Stuff
    @Inject
    @DefaultConfig(sharedRoot = false)
    private Path defaultConfig;
    @Inject
    @DefaultConfig(sharedRoot = false)
    private ConfigurationLoader<CommentedConfigurationNode> configLoader;
    @Inject
    @ConfigDir(sharedRoot = false)
    private Path defaultConfigDir;

    private ConfigManager configManager;

    public ShopManager getShopManager() {
        return shopManager;
    }

    public RegionSelectBuffer getRegionSelectBuffer() {
        return regionSelectBuffer;
    }

    public Logger getLogger() {
        return logger;
    }

    public Path getDefaultConfig() {
        return defaultConfig;
    }

    public ConfigurationLoader<CommentedConfigurationNode> getConfigLoader() {
        return configLoader;
    }

    public Path getDefaultConfigDir() {
        return defaultConfigDir;
    }

    public PluginContainer getInstance() {
        return instance;
    }

    @Listener
    public void onGameInit(GameInitializationEvent event){
        configManager = new ConfigManager(this);
        shopManager = configManager.loadShops();
    }

    @Listener
    public void onServerStart(GameStartedServerEvent event){
        // Initialize Region Select Buffer
        regionSelectBuffer = new RegionSelectBuffer();

        // Register Commands
        PlayerShopsRPGCommandRegister commandRegister = new PlayerShopsRPGCommandRegister(instance);
        commandRegister.registerCmds();

        // Log Start Up to Console
        getLogger().info(
                instance.getName() + " version " + instance.getVersion().get()
                        + " enabled!");
    }

    @Listener
    public void onInteractBlockPrimary(InteractBlockEvent.Primary event, @Root Player player) {
        Optional<RegionBuffer> regionBufferOptional = regionSelectBuffer.getBuffer(player.getUniqueId());
        if (regionBufferOptional.isPresent()) {
            RegionBuffer regionBuffer = regionBufferOptional.get();
            regionBuffer.addFront(event.getTargetBlock().getPosition());
            player.sendMessage(ChatTypes.CHAT, regionBuffer.getProgressionMessage());
            event.setCancelled(true);
        }
    }

    @Listener
    public void onInteractBlockSecondary(InteractBlockEvent.Secondary event, @Root Player player) {
        Optional<RegionBuffer> regionBufferOptional = regionSelectBuffer.getBuffer(player.getUniqueId());
        if (regionBufferOptional.isPresent()) {
            RegionBuffer regionBuffer = regionBufferOptional.get();
            regionBuffer.addBack(event.getTargetBlock().getPosition());
            player.sendMessage(ChatTypes.CHAT, regionBuffer.getProgressionMessage());
            event.setCancelled(true);
        }
    }

    @Listener
    public void onChangeServiceProvider(ChangeServiceProviderEvent event) {
        econManager = EconManager.getInstance();
        EconomyService economyService;
        //Hook into economy service
        if (event.getService().equals(EconomyService.class)) {
            economyService = (EconomyService) event.getNewProviderRegistration().getProvider();
            econManager.hookEconService(economyService);
        }
    }

    @Listener
    public void onServerStop(GameStoppedEvent event) {
        configManager.saveShops();
    }
}