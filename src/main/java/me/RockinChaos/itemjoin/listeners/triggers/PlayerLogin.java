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
package me.RockinChaos.itemjoin.listeners.triggers;

import me.RockinChaos.core.utils.ServerUtils;
import me.RockinChaos.itemjoin.ItemJoin;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerPreLoginEvent;
import org.bukkit.event.player.PlayerLoginEvent;

public class PlayerLogin implements Listener {

    /**
     * Called when the player attempts to connect to the server while it is starting.
     * Attempts to keep the player in loading limbo until the plugin is fully loaded.
     *
     * @param event - AsyncPlayerPreLoginEvent
     */
    @EventHandler(priority = EventPriority.LOWEST, ignoreCancelled = true)
    private void onPlayerPreLogin(AsyncPlayerPreLoginEvent event) {
        if (!ItemJoin.getCore().isStarted()) {
            ServerUtils.logDebug("Processing pre-login for " + (ServerUtils.hasSpecificUpdate("1_8") ? event.getUniqueId() : "Legacy") + " - " + event.getName());
            this.enableLatch();
            ServerUtils.logDebug("Accepted pre-login for " + (ServerUtils.hasSpecificUpdate("1_8") ? event.getUniqueId() : "Legacy") + " - " + event.getName());
        }
    }

    /**
     * Called when the player attempts to connect to the server before the plugin was even enabled.
     * Denies the player login attempt as they have managed to bypass the AsyncPlayerPreLoginEvent.
     *
     * @param event - PlayerLoginEvent
     */
    @EventHandler(priority = EventPriority.LOWEST, ignoreCancelled = true)
    public void onPlayerLogin(PlayerLoginEvent event) {
        final Player player = event.getPlayer();
        if (!ItemJoin.getCore().isStarted()) {
            ServerUtils.logDebug("Denied login for " + (ServerUtils.hasSpecificUpdate("1_8") ? player.getUniqueId() : "Legacy") + " - " + player.getName() + ", server is still starting!");
            event.disallow(PlayerLoginEvent.Result.KICK_OTHER, "Timed out");
        }
    }

    /**
     * A latching method to prevent a thread from continuing until the plugin has fully loaded.
     */
    @SuppressWarnings("BusyWait")
    private void enableLatch() {
        while (!ItemJoin.getCore().isStarted()) {
            try {
                Thread.sleep(2000);
            } catch (InterruptedException ignored) {
            }
        }
    }
}