/*
 * ItemJoin
 * Copyright (C) CraftationGaming <https://www.craftationgaming.com/>
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package me.RockinChaos.itemjoin.listeners;

import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;

import me.RockinChaos.itemjoin.ItemJoin;
import me.RockinChaos.core.handlers.PlayerHandler;
import me.RockinChaos.itemjoin.item.ItemMap;
import me.RockinChaos.itemjoin.item.ItemUtilities;
import me.RockinChaos.core.utils.SchedulerUtils;
import me.RockinChaos.itemjoin.utils.api.ItemAPI;

public class Breaking implements Listener {
	
   /**
    * Handles the Block Break custom item drops.
    * 
    * @param event - BlockBreakEvent
    */
	@EventHandler(ignoreCancelled = true)
	public void onPlayerBreakBlock(BlockBreakEvent event) {
		final Block block = event.getBlock();
		final Material material = (block != null ? block.getType() : Material.AIR);
		final Player player = event.getPlayer();
		SchedulerUtils.runAsync(() -> {
			for (ItemMap itemMap: ItemUtilities.getUtilities().getItems()) {
				if (itemMap.blocksDrop() && block != null && material != Material.AIR && itemMap.getBlocksDrop().containsKey(material) 
				 && itemMap.inWorld(player.getWorld()) && itemMap.isLimitMode(player.getGameMode()) && !PlayerHandler.isCreativeMode(player) && itemMap.hasPermission(player, player.getWorld()) && ItemAPI.isToolable(player, material) && Math.random() <= itemMap.getBlocksDrop().get(material)) {
					for (String region : ((ItemJoin.getCore().getDependencies().getGuard().guardEnabled() && !itemMap.getEnabledRegions().isEmpty()) ? ItemJoin.getCore().getDependencies().getGuard().getRegionAtLocation(player.getLocation()).split(", ") : new String[]{"FALSE"})) {
						if (!ItemJoin.getCore().getDependencies().getGuard().guardEnabled() || itemMap.getEnabledRegions().isEmpty() || itemMap.inRegion(region)) { 
							SchedulerUtils.run(() -> block.getWorld().dropItemNaturally(block.getLocation(), itemMap.getItem(player)));
						}
					}
				}
			}
		});
	}
}