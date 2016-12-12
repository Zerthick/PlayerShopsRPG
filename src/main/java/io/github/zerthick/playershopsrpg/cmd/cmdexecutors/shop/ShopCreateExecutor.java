/*
 * Copyright (C) 2016  Zerthick
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

package io.github.zerthick.playershopsrpg.cmd.cmdexecutors.shop;

import com.google.common.collect.ImmutableMap;
import io.github.zerthick.playershopsrpg.PlayerShopsRPG;
import io.github.zerthick.playershopsrpg.cmd.cmdexecutors.AbstractCmdExecutor;
import io.github.zerthick.playershopsrpg.cmd.cmdexecutors.CommandArgs;
import io.github.zerthick.playershopsrpg.region.Region;
import io.github.zerthick.playershopsrpg.region.selectbuffer.RegionBuffer;
import io.github.zerthick.playershopsrpg.shop.Shop;
import io.github.zerthick.playershopsrpg.shop.ShopContainer;
import io.github.zerthick.playershopsrpg.utils.messages.Messages;
import org.spongepowered.api.command.CommandException;
import org.spongepowered.api.command.CommandResult;
import org.spongepowered.api.command.CommandSource;
import org.spongepowered.api.command.args.CommandContext;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.text.Text;
import org.spongepowered.api.text.chat.ChatTypes;
import org.spongepowered.api.text.format.TextColors;

import java.util.Optional;

public class ShopCreateExecutor extends AbstractCmdExecutor {

    public ShopCreateExecutor(PlayerShopsRPG plugin) {
        super(plugin);
    }

    @Override
    public CommandResult execute(CommandSource src, CommandContext args) throws CommandException {

        if (src instanceof Player) {
            Player player = (Player) src;

            Optional<String> shopNameOptional = args.getOne(CommandArgs.SHOP_NAME);

            if (shopNameOptional.isPresent()) {
                String shopName = shopNameOptional.get();
                Optional<RegionBuffer> regionBufferOptional = plugin.getRegionSelectBuffer().getBuffer(player.getUniqueId());
                //If the player has previously selected points for the shop
                if (regionBufferOptional.isPresent()) {
                    RegionBuffer regionBuffer = regionBufferOptional.get();
                    Optional<Region> regionOptional = regionBuffer.getRegion();
                    //If the points selected are enough to create a region
                    if (regionOptional.isPresent()) {
                        ShopContainer shopContainer =
                                new ShopContainer(new Shop(shopName, player.getUniqueId()), regionOptional.get());
                        shopManager.addShop(player.getWorld().getUniqueId(), shopContainer);
                        plugin.getRegionSelectBuffer().removeBuffer(player.getUniqueId());
                        player.sendMessage(ChatTypes.CHAT, Text.of(TextColors.BLUE, Messages.processDropins(Messages.CREATE_SUCCESS,
                                ImmutableMap.of(Messages.DROPIN_SHOP_NAME, shopName, Messages.DROPIN_SHOP_CORDS, regionOptional.get().toString()))));
                    } else {
                        player.sendMessage(ChatTypes.CHAT,
                                Text.of(TextColors.RED, Messages.CREATE_NOT_ENOUGH_POINTS));
                    }
                } else {
                    player.sendMessage(ChatTypes.CHAT,
                            Text.of(TextColors.RED, Messages.CREATE_NO_REGION));
                }
            } else {
                player.sendMessage(ChatTypes.CHAT,
                        Text.of(TextColors.RED, Messages.CREATE_NO_NAME));
            }
            return CommandResult.success();
        }

        src.sendMessage(Text.of(Messages.CREATE_CONSOLE_REJECT));
        return CommandResult.success();
    }
}
