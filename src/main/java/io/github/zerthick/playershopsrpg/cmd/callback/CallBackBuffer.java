package io.github.zerthick.playershopsrpg.cmd.callback;

import org.spongepowered.api.Sponge;
import org.spongepowered.api.entity.living.player.Player;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class CallBackBuffer {

    private Map<UUID, String> callBackMap;

    public CallBackBuffer() {
        callBackMap = new HashMap<>();
    }

    public boolean hasCallBack(Player player) {
        return callBackMap.containsKey(player.getUniqueId());
    }

    public void addCallBack(Player player, String callback) {
        callBackMap.put(player.getUniqueId(), callback);
    }

    public void executeCallBack(Player player, String callBackValue) {
        if (hasCallBack(player)) {
            Sponge.getGame().getCommandManager().process(player, callBackMap.remove(player.getUniqueId()).replace("%c", callBackValue));
        }
    }
}
