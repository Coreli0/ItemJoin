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
package me.RockinChaos.itemjoin.handlers;

import java.util.ArrayList;
import java.util.Collection;
import java.util.UUID;
import java.util.function.Consumer;

import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.inventory.InventoryView;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.SkullMeta;

import de.domedd.betternick.BetterNick;
import me.RockinChaos.itemjoin.ItemJoin;
import me.RockinChaos.itemjoin.utils.DependAPI;
import me.RockinChaos.itemjoin.utils.LegacyAPI;

public class PlayerHandler {
	
	private static PlayerHandler player;
	private final int PLAYER_CRAFT_INV_SIZE = 5;
	
   /**
    * Restores the crafting items for all currently online players.
    * 
    */
    public void restoreCraftItems() {
    	this.forOnlinePlayers(player -> { ItemHandler.getItem().restoreCraftItems(player); } );
    }
	
   /**
    * Checks if the InventoryView is a player crafting inventory.
    * 
    * @param view - The InventoryView to be checked.
    * @return If the currently open inventory is a player crafting inventory.
    */
    public boolean isCraftingInv(final InventoryView view) {
        return view.getTopInventory().getSize() == PLAYER_CRAFT_INV_SIZE;
    }
	
   /**
    * Checks if the player is currently in creative mode.
    * 
    * @param player - The player to be checked.
    * @return If the player is currently in creative mode.
    */
	public boolean isCreativeMode(final Player player) {
		if (player.getGameMode() == GameMode.CREATIVE) {
			return true;
		}
		return false;
	}
	
   /**
    * Checks if the player is currently in adventure mode.
    * 
    * @param player - The player to be checked.
    * @return If the player is currently in adventure mode.
    */
	public boolean isAdventureMode(final Player player) {
		if (player.getGameMode() == GameMode.ADVENTURE) {
			return true;
		}
		return false;
	}
	
   /**
    * Sets the currently selected hotbar slot for the specified player.
    * 
    * @param player - The player to have their slot set.
    */
	public void setHotbarSlot(final Player player) {
		if (ConfigHandler.getConfig(false).getHotbarSlot() != -1 && ConfigHandler.getConfig(false).getHotbarSlot() <= 8 && ConfigHandler.getConfig(false).getHotbarSlot() >= 0) {
			player.getInventory().setHeldItemSlot(ConfigHandler.getConfig(false).getHotbarSlot());
		}
	}
	
   /**
    * Gets the current ItemStack in the players Main Hand,
    * If it is empty it will get the ItemStack in the Off Hand,
    * If the server version is below MC 1.9 it will use the 
    * legacy hand method to get the single hand.
    * 
    * @param player - The player to be checked.
    * @return The current ItemStack in the players hand.
    */
	public ItemStack getHandItem(final Player player) {
		if (ServerHandler.getServer().hasSpecificUpdate("1_9") && player.getInventory().getItemInMainHand().getType() != null && player.getInventory().getItemInMainHand().getType() != Material.AIR) {
			return player.getInventory().getItemInMainHand();
		} else if (ServerHandler.getServer().hasSpecificUpdate("1_9") && player.getInventory().getItemInOffHand().getType() != null && player.getInventory().getItemInOffHand().getType() != Material.AIR) {
			return player.getInventory().getItemInOffHand();
		} else if (!ServerHandler.getServer().hasSpecificUpdate("1_9")) {
			return LegacyAPI.getLegacy().getInHandItem(player);
		}
		return null;
	}
	
   /**
    * Gets the current ItemStack in the players hand.
    * If the server version is below MC 1.9 it will use the 
    * legacy hand method to get the single hand.
    * 
    * @param player - The player to be checked.
    * @param type - The hand type to get.
    * @return The current ItemStack in the players hand.
    */
	public ItemStack getPerfectHandItem(final Player player, String type) {
		if (ServerHandler.getServer().hasSpecificUpdate("1_9") && type != null && type.equalsIgnoreCase("HAND")) {
			return player.getInventory().getItemInMainHand();
		} else if (ServerHandler.getServer().hasSpecificUpdate("1_9") && type != null && type.equalsIgnoreCase("OFF_HAND")) {
			return player.getInventory().getItemInOffHand();
		} else if (!ServerHandler.getServer().hasSpecificUpdate("1_9")) {
			return LegacyAPI.getLegacy().getInHandItem(player);
		}
		return null;
	}
	
   /**
    * Gets the current ItemStack in the players Main Hand.
    * If the server version is below MC 1.9 it will use the 
    * legacy hand method to get the single hand.
    * 
    * @param player - The player to be checked.
    * @return The current ItemStack in the players hand.
    */
	public ItemStack getMainHandItem(final Player player) {
		if (ServerHandler.getServer().hasSpecificUpdate("1_9")) {
			return player.getInventory().getItemInMainHand();
		} else if (!ServerHandler.getServer().hasSpecificUpdate("1_9")) {
			return LegacyAPI.getLegacy().getInHandItem(player);
		}
		return null;
	}
	
   /**
    * Gets the current ItemStack in the players Off Hand.
    * If the server version is below MC 1.9 it will use the 
    * legacy hand method to get the single hand.
    * 
    * @param player - The player to be checked.
    * @return The current ItemStack in the players hand.
    */
	public ItemStack getOffHandItem(final Player player) {
		if (ServerHandler.getServer().hasSpecificUpdate("1_9")) {
			return player.getInventory().getItemInOffHand();
		} else if (!ServerHandler.getServer().hasSpecificUpdate("1_9")) {
			return LegacyAPI.getLegacy().getInHandItem(player);
		}
		return null;
	}
	
   /**
    * Sets the specified ItemStack to the players Main Hand.
    * If the server version is below MC 1.9 it will use the 
    * legacy hand method to get the single hand.
    * 
    * @param player - The player to have the item set.
    * @param item - The ItemStack to be set.
    */
	public void setMainHandItem(final Player player, final ItemStack item) {
		if (ServerHandler.getServer().hasSpecificUpdate("1_9")) {
			player.getInventory().setItemInMainHand(item);
		} else if (!ServerHandler.getServer().hasSpecificUpdate("1_9")) {
			LegacyAPI.getLegacy().setInHandItem(player, item);
		}
	}
	
   /**
    * Sets the specified ItemStack to the players Off Hand.
    * If the server version is below MC 1.9 it will use the 
    * legacy hand method to get the single hand.
    * 
    * @param player - The player to have the item set.
    * @param item - The ItemStack to be set.
    */
	public void setOffHandItem(final Player player, final ItemStack item) {
		if (ServerHandler.getServer().hasSpecificUpdate("1_9")) {
			player.getInventory().setItemInOffHand(item);
		} else if (!ServerHandler.getServer().hasSpecificUpdate("1_9")) {
			LegacyAPI.getLegacy().setInHandItem(player, item);
		}
	}
	
   /**
    * Resolves a bug where canceling an experience level event causes it to visually glitch
    * and remain showing the uncanceled experience levels.
    * 
    * This simply gets the players current experience levels and resets 
    * them to cause a clientside update.
    * 
    * @param player - The player to have their levels set.
    */
	public void updateExperienceLevels(final Player player) {
        Bukkit.getScheduler().scheduleSyncDelayedTask(ItemJoin.getInstance(), new Runnable() {
            @Override
			public void run() {
            	player.setExp(player.getExp());
            	player.setLevel(player.getLevel());
            }
        }, 1L);
	}
	
   /**
    * Updates the specified players inventory.
    * 
    * @param player - The player to have their inventory updated.
    * @param delay - The ticks to wait before updating the inventory.
    */
	public void updateInventory(final Player player, final long delay) {
        Bukkit.getScheduler().scheduleSyncDelayedTask(ItemJoin.getInstance(), new Runnable() {
            @Override
			public void run() {
            	LegacyAPI.getLegacy().updateInventory(player);
            }
        }, delay);
	}
	
   /**
    * Checks if the server is using the new skull method.
    * 
    * @return If the server is using the new skull method.
    */
	private boolean usesOwningPlayer() {
		try {
			if (Class.forName("org.bukkit.inventory.meta.SkullMeta").getMethod("getOwningPlayer") != null) { return true; }
		} catch (Exception e) { }
		return false;
	}
	
   /**
    * Gets the current skull owner of the specified item.
    * 
    * @param item - The item to have its skull owner fetched.
    * @return The ItemStacks current skull owner.
    */
	public String getSkullOwner(final ItemStack item) {
		if (ServerHandler.getServer().hasSpecificUpdate("1_12") && item != null && item.hasItemMeta() && ItemHandler.getItem().isSkull(item.getType()) 
				&& ((SkullMeta) item.getItemMeta()).hasOwner() && this.usesOwningPlayer() != false) {
			String owner =  ((SkullMeta) item.getItemMeta()).getOwningPlayer().getName();
			if (owner != null) { return owner; }
		} else if (item != null && item.hasItemMeta() 
				&& ItemHandler.getItem().isSkull(item.getType())
				&& ((SkullMeta) item.getItemMeta()).hasOwner()) {
			String owner = LegacyAPI.getLegacy().getSkullOwner(((SkullMeta) item.getItemMeta()));
			if (owner != null) { return owner; }
		} 
		return "NULL";
	}

   /**
    * Gets the Player instance from their String name.
    * 
    * @param playerName - The player name to be transformed.
    * @return The fetched Player instance.
    */
	public Player getPlayerString(final String playerName) {
		Player args = null;
		try { args = Bukkit.getPlayer(UUID.fromString(playerName)); } catch (Exception e) {}
		if (playerName != null && DependAPI.getDepends(false).nickEnabled()) {
			try { 
				de.domedd.betternick.api.nickedplayer.NickedPlayer np = new de.domedd.betternick.api.nickedplayer.NickedPlayer(LegacyAPI.getLegacy().getPlayer(playerName));
				if (np.isNicked()) { return LegacyAPI.getLegacy().getPlayer(np.getRealName()); }
				else { return LegacyAPI.getLegacy().getPlayer(playerName); }
			} catch (NoClassDefFoundError e) {
				if (BetterNick.getApi().isPlayerNicked(LegacyAPI.getLegacy().getPlayer(playerName))) { return LegacyAPI.getLegacy().getPlayer(BetterNick.getApi().getRealName(LegacyAPI.getLegacy().getPlayer(playerName))); }
				else { return LegacyAPI.getLegacy().getPlayer(playerName); }
			}
		} else if (args == null) { return LegacyAPI.getLegacy().getPlayer(playerName); }
		return args;
	}
	
   /**
    * Gets the UUID of the Player.
    * If the UUID does not exist it will fetch their String name.
    * 
    * @param player - The player to have their UUID fetched.
    * @return The UUID of the player or if not found, their String name.
    */
	public String getPlayerID(final Player player) {
		if (player != null && player.getUniqueId() != null) {
			return player.getUniqueId().toString();
		} else if (player != null && DependAPI.getDepends(false).nickEnabled()) {
			try {
				de.domedd.betternick.api.nickedplayer.NickedPlayer np = new de.domedd.betternick.api.nickedplayer.NickedPlayer(player);
				if (np.isNicked()) { return np.getRealName();
				} else { return player.getName(); }
			} catch (NoClassDefFoundError e) {
				if (BetterNick.getApi().isPlayerNicked(player)) { return BetterNick.getApi().getRealName(player);
				} else { return player.getName(); }
			}
		} else if (player != null) {
			return player.getName();
		}
		return "";
	}
	
   /**
    * Gets the UUID of the OfflinePlayer.
    * If the UUID does not exist it will fetch their String name.
    * 
    * @param player - The OfflinePlayer instance to have their UUID fetched.
    * @return The UUID of the player or if not found, their String name.
    */
	public String getOfflinePlayerID(final OfflinePlayer player) {
		if (player != null && player.getUniqueId() != null) {
			return player.getUniqueId().toString();
		} else if (player != null && DependAPI.getDepends(false).nickEnabled()) {
			try {
				de.domedd.betternick.api.nickedplayer.NickedPlayer np = new de.domedd.betternick.api.nickedplayer.NickedPlayer((BetterNick) player);
				if (np.isNicked()) { return np.getRealName();
				} else { return player.getName(); }
			} catch (NoClassDefFoundError e) {
				if (BetterNick.getApi().isPlayerNicked((Player) player)) { return BetterNick.getApi().getRealName((Player) player);
				} else { return player.getName(); }
			}
		} else if (player != null) {
			return player.getName();
		}
		return "";
	}
	
   /**
    * Gets the Nearby Players from the specified Players Location inside the Range.
    * 
    * @param player - The Player that is searching for Nearby Players.
    * @param range - The distance to check for Nearby Players.
    * @return The String name of the Nearby Player.
    */
    public String getNearbyPlayer(Player player, int range) {
	    ArrayList < Location > sight = new ArrayList < Location > ();
	    ArrayList < Entity > entities = (ArrayList < Entity > ) player.getNearbyEntities(range, range, range);
	    Location origin = player.getEyeLocation();
	    sight.add(origin.clone().add(origin.getDirection()));
	    sight.add(origin.clone().add(origin.getDirection().multiply(range)));
	    sight.add(origin.clone().add(origin.getDirection().multiply(range + 3)));
	   	for (int i = 0; i < sight.size(); i++) {
	    	for (int k = 0; k < entities.size(); k++) {
	    		if (Math.abs(entities.get(k).getLocation().getX() - sight.get(i).getX()) < 1.3) {
	    			if (Math.abs(entities.get(k).getLocation().getY() - sight.get(i).getY()) < 1.5) {
	    				if (Math.abs(entities.get(k).getLocation().getZ() - sight.get(i).getZ()) < 1.3) {
	    					if (entities.get(k) instanceof Player) {
	    						if (ServerHandler.getServer().hasSpecificUpdate("1_8")) {
	    							return entities.get(k).getName();
	    						} else {
	    							return ((Player) entities.get(k)).getName();
	    						}
	    					}
	    				}
	    			}
	    		}
	    	}
	    }
    	return "INVALID";
    }
	
   /**
    * Executes an input of methods for the currently online players.
    * 
    * @param input - The methods to be executed.
    */
    public void forOnlinePlayers(final Consumer<Player> input) {
		try {
		  /** New method for getting the current online players.
			* This is for MC 1.12+
			*/
			if (Bukkit.class.getMethod("getOnlinePlayers", new Class < ? > [0]).getReturnType() == Collection.class) {
				for (Object objPlayer: ((Collection < ? > ) Bukkit.class.getMethod("getOnlinePlayers", new Class < ? > [0]).invoke(null, new Object[0]))) { 
					input.accept(((Player) objPlayer));
				}
			} 
		  /** New old for getting the current online players.
			* This is for MC versions below 1.12.
			* 
			* @deprecated Legacy version of getting online players.
			*/
			else {
				for (Player player: ((Player[]) Bukkit.class.getMethod("getOnlinePlayers", new Class < ? > [0]).invoke(null, new Object[0]))) {
					input.accept(player);
				}
			}
		} catch (Exception e) { ServerHandler.getServer().sendDebugTrace(e); }
	}
	
   /**
    * Gets the OfflinePlayer from their string name.
    * 
    * @param playerName - The name of the player to be fetched.
    * @return The Offline instance of the player.
    */
	public OfflinePlayer getOfflinePlayer(final String playerName) {
		Collection<?> playersOfflineNew;
		OfflinePlayer[] playersOfflineOld;
		try {
			if (Bukkit.class.getMethod("getOfflinePlayers", new Class < ? > [0]).getReturnType() == Collection.class) {
				playersOfflineNew = ((Collection < ? > ) Bukkit.class.getMethod("getOfflinePlayers", new Class < ? > [0]).invoke(null, new Object[0]));
				for (Object objPlayer: playersOfflineNew) {
					Player player = ((Player)objPlayer);
					if (player.getName().equalsIgnoreCase(playerName)) {
						return player;
					}
				}
			} else {
				playersOfflineOld = ((OfflinePlayer[]) Bukkit.class.getMethod("getOfflinePlayers", new Class < ? > [0]).invoke(null, new Object[0]));
				for (OfflinePlayer player: playersOfflineOld) {
					if (player.getName().equalsIgnoreCase(playerName)) {
						return player;
					}
				}
			}
		} catch (Exception e) { ServerHandler.getServer().sendDebugTrace(e); } 
		return null;
	}

   /**
    * Gets the instance of the PlayerHandler.
    * 
    * @return The PlayerHandler instance.
    */
    public static PlayerHandler getPlayer() { 
        if (player == null) { player = new PlayerHandler(); }
        return player; 
    } 
}