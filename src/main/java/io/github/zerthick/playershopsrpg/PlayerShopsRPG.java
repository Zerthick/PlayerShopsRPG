package io.github.zerthick.playershopsrpg;

import com.google.inject.Inject;
import io.github.zerthick.playershopsrpg.shop.ShopManager;
import io.github.zerthick.playershopsrpg.utils.econ.EconManager;
import org.slf4j.Logger;
import org.spongepowered.api.event.Listener;
import org.spongepowered.api.event.game.state.GameInitializationEvent;
import org.spongepowered.api.event.game.state.GameStartedServerEvent;
import org.spongepowered.api.event.game.state.GameStoppedEvent;
import org.spongepowered.api.event.service.ChangeServiceProviderEvent;
import org.spongepowered.api.plugin.Plugin;
import org.spongepowered.api.plugin.PluginContainer;
import org.spongepowered.api.service.economy.EconomyService;

@Plugin(id = "PlayerShopsRPG", name = "PlayerShopsRPG", version = "0.0.1")
public class PlayerShopsRPG {

    private ShopManager shopManager;
    private EconManager econManager;

    @Inject
    private Logger logger;
    @Inject
    private PluginContainer instance;

    public ShopManager getShopManager() {
        return shopManager;
    }

    public Logger getLogger() {
        return logger;
    }

    public PluginContainer getInstance() {
        return instance;
    }

    @Listener
    public void onGameInit(GameInitializationEvent event){
        //TODO load config
    }

    @Listener
    public void onServerStart(GameStartedServerEvent event){

        // Log Start Up to Console
        getLogger().info(
                instance.getName() + " version " + instance.getVersion()
                        + " enabled!");
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

    }
}
