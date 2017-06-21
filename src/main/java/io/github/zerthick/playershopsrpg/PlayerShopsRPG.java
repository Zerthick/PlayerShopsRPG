/*
 * Copyright (C) 2017  Zerthick
 *
 * This file is part of PlayerShopsRPG.
 *
 * PlayerShopsRPG is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 2 of the License, or
 * (at your option) any later version.
 *
 * PlayerShopsRPG is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with PlayerShopsRPG.  If not, see <http://www.gnu.org/licenses/>.
 */

package io.github.zerthick.playershopsrpg;

import com.google.inject.Inject;
import io.github.zerthick.playershopsrpg.cmd.CommandRegister;
import io.github.zerthick.playershopsrpg.cmd.callback.CallBackBuffer;
import io.github.zerthick.playershopsrpg.region.selectbuffer.RegionBuffer;
import io.github.zerthick.playershopsrpg.region.selectbuffer.RegionSelectBuffer;
import io.github.zerthick.playershopsrpg.shop.ShopManager;
import io.github.zerthick.playershopsrpg.shop.ShopRentManager;
import io.github.zerthick.playershopsrpg.shop.type.ShopTypeManager;
import io.github.zerthick.playershopsrpg.utils.config.ConfigManager;
import io.github.zerthick.playershopsrpg.utils.config.PluginConfig;
import io.github.zerthick.playershopsrpg.utils.econ.EconManager;
import io.github.zerthick.playershopsrpg.utils.messages.Messages;
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
import org.spongepowered.api.event.message.MessageChannelEvent;
import org.spongepowered.api.event.service.ChangeServiceProviderEvent;
import org.spongepowered.api.plugin.Plugin;
import org.spongepowered.api.plugin.PluginContainer;
import org.spongepowered.api.service.economy.EconomyService;
import org.spongepowered.api.text.chat.ChatTypes;

import java.nio.file.Path;
import java.util.Optional;

@Plugin(id = "playershopsrpg",
        name = "PlayerShopsRPG",
        version = "1.1.0",
        description = "A region-based player shop plugin.",
        authors = {
                "Zerthick"
        }
)
public class PlayerShopsRPG {

    private ShopManager shopManager;
    private EconManager econManager;
    private RegionSelectBuffer regionSelectBuffer;
    private ShopTypeManager shopTypeManager;
    private ShopRentManager shopRentManager;
    private Messages messages;

    @Inject
    private Logger logger;
    @Inject
    private PluginContainer instance;

    //Config Stuff
    @Inject
    @DefaultConfig(sharedRoot = false)
    private Path defaultConfig;
    @Inject
    @ConfigDir(sharedRoot = false)
    private Path defaultConfigDir;

    private PluginConfig pluginConfig;

    private ConfigManager configManager;

    public ShopManager getShopManager() {
        return shopManager;
    }

    public RegionSelectBuffer getRegionSelectBuffer() {
        return regionSelectBuffer;
    }

    public ShopTypeManager getShopTypeManager() {
        return shopTypeManager;
    }

    public ShopRentManager getShopRentManager() {
        return shopRentManager;
    }

    public Logger getLogger() {
        return logger;
    }

    public Path getDefaultConfig() {
        return defaultConfig;
    }

    public Path getDefaultConfigDir() {
        return defaultConfigDir;
    }

    public PluginContainer getInstance() {
        return instance;
    }

    public PluginConfig getPluginConfig() {
        return pluginConfig;
    }

    @Listener
    public void onGameInit(GameInitializationEvent event){

        configManager = ConfigManager.getInstance();
        configManager.init(this);

        pluginConfig = configManager.loadPluginConfig();

        shopManager = configManager.loadShops();
        shopTypeManager = configManager.loadShopTypes();
        shopRentManager = ShopRentManager.getInstance();
        shopRentManager.init(this, configManager.loadShopRent());
        messages = Messages.getInstance();

    }

    @Listener
    public void onServerStart(GameStartedServerEvent event){
        // Initialize Region Select Buffer
        regionSelectBuffer = new RegionSelectBuffer();

        // Register Commands
        CommandRegister.registerCommands(this);

        // Log Start Up to Console
        getLogger().info(
                instance.getName() + " version " + instance.getVersion().orElse("")
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
    public void onPlayerSendChat(MessageChannelEvent.Chat event, @Root Player player) {
        CallBackBuffer callBackBuffer = CallBackBuffer.getInstance();
        if (callBackBuffer.hasCallBack(player)) {
            callBackBuffer.executeCallBack(player, event.getRawMessage().toPlain());
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
        configManager.saveShopRent();
    }
}