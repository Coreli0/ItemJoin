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
package me.RockinChaos.itemjoin.giveitems.utils;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.UUID;

import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.scheduler.BukkitRunnable;

import com.mojang.authlib.GameProfile;
import com.mojang.authlib.properties.Property;

import me.RockinChaos.itemjoin.ItemJoin;
import me.RockinChaos.itemjoin.handlers.ItemHandler;
import me.RockinChaos.itemjoin.handlers.PlayerHandler;
import me.RockinChaos.itemjoin.handlers.ServerHandler;
import me.RockinChaos.itemjoin.listeners.InventoryClick;
import me.RockinChaos.itemjoin.utils.Utils;
import me.arcaniax.hdb.api.HeadDatabaseAPI;
import me.RockinChaos.itemjoin.utils.LegacyAPI;

public class ItemAnimation {
	
	private ItemMap itemMap;
	private List<String> dynamicNames = null;
	private List<List<String>> dynamicLores = null;
	private List<String> dynamicMaterials = null;
	private List<String> dynamicOwners = null;
	private List<String> dynamicTextures = null;
	private List<String> dynamicPages = null;
	private boolean stopAnimations = false;
	private boolean menu = false;
	private List<List<String>> menuLores = null;
	
   /**
    * Called on new ItemAnimation.
    * 
    * @param item - The ItemMap that handles item creation.
    */
	public ItemAnimation(final ItemMap item) {
		this.itemMap = item;
		if (item.getDynamicNames() != null && !item.getDynamicNames().isEmpty()) { this.dynamicNames = item.getDynamicNames(); }
		if (item.getDynamicLores() != null && !item.getDynamicLores().isEmpty()) { this.dynamicLores = item.getDynamicLores(); }
		if (item.getDynamicMaterials() != null && !item.getDynamicMaterials().isEmpty()) { this.dynamicMaterials = item.getDynamicMaterials(); }
		if (this.itemMap.getMaterial().toString().equalsIgnoreCase("WRITTEN_BOOK") && item.getPages() != null && !item.getPages().isEmpty()) { this.dynamicPages = item.getPages(); }
		if (item.getDynamicOwners() != null && !item.getDynamicOwners().isEmpty()) { this.dynamicOwners = item.getDynamicOwners(); }
		else if (item.getDynamicTextures() != null && !item.getDynamicTextures().isEmpty()) { this.dynamicTextures = item.getDynamicTextures(); }
	}
	
   /**
	* Starts the animations for the specified player.
	* 
	* @param player - The player which will have animations started.
	*/
	public void openAnimation(final Player player) {
		if (this.dynamicNames != null) { this.nameTasks(player); }
		if (this.dynamicLores != null) { this.loreTasks(player); }
		if (this.dynamicMaterials != null) { this.materialTasks(player); }
		if (this.dynamicPages != null) { this.pagesTasks(player); }
		if (this.dynamicOwners != null) { this.ownerTasks(player); }
		else if (this.dynamicTextures != null) { this.textureTasks(player); }
	}
	
  /**
   * Stops the animations for the specified player.
   * 
   * @param player - The player which will have animations stopped.
   */
	public void closeAnimation(final Player player) {
		this.stopAnimations = true;
		ServerHandler.getServer().logDebug("{Animation} Successfully closed all animations for the item " + this.itemMap.getConfigName() + " with the instanced player " + player.getName() + ".");
	}
	
   /**
	* Handles animations for the display name of a custom item.
	* 
	* @param player - The player which will have animations handled.
	*/
	private void nameTasks(final Player player) {
		long ticks = 0;
		final Iterator <String> it = this.dynamicNames.iterator();
		while (it.hasNext()) {
			final String name = it.next();
			if (Utils.getUtils().returnInteger(ItemHandler.getItem().getDelayFormat(name)) != null) { ticks = ticks + Utils.getUtils().returnInteger(ItemHandler.getItem().getDelayFormat(name)); }
			else { ticks = ticks + 180; }
			this.AnimateTask(player, it.hasNext(), name, null, null, null, null, null, ticks, 0);
		}
	}
	
   /**
	* Handles animations for the displayed lore of a custom item.
	* 
    * @param player - The player which will have animations handled.
    */
	private void loreTasks(final Player player) {
		long ticks = 0;
		int position = 0;
		final Iterator<List<String>> it = this.dynamicLores.iterator();
		while (it.hasNext()) {
			final List<String> lore = it.next();
			if (Utils.getUtils().returnInteger(ItemHandler.getItem().getDelayFormat(lore.get(0))) != null) { ticks = ticks + Utils.getUtils().returnInteger(ItemHandler.getItem().getDelayFormat(lore.get(0))); }
			else { ticks = ticks + 180; }
			this.AnimateTask(player, it.hasNext(), null, lore, null, null, null, null, ticks, position);
			position++;
		}
	}
	
   /**
	* Handles animations for the material of a custom item.
	* 
	* @param player - The player which will have animations handled.
	*/
	private void materialTasks(final Player player) {
		long ticks = 0;
		final Iterator<String> it = this.dynamicMaterials.iterator();
		while (it.hasNext()) {
			final String mat = it.next();
			if (Utils.getUtils().returnInteger(ItemHandler.getItem().getDelayFormat(mat)) != null) { ticks = ticks + Utils.getUtils().returnInteger(ItemHandler.getItem().getDelayFormat(mat)); }
			else { ticks = ticks + 180; }
			this.AnimateTask(player, it.hasNext(), null, null, mat, null, null, null, ticks, 0);
		}
	}
	
   /**
	* Handles animations for the book pages of a custom book item.
	* 
	* @param player - The player which will have animations handled.
	*/
	private void pagesTasks(final Player player) {
		long ticks = 0;
		if (Utils.getUtils().returnInteger(ItemHandler.getItem().getDelayFormat(this.dynamicPages.get(0))) != null) { ticks = ticks + Utils.getUtils().returnInteger(ItemHandler.getItem().getDelayFormat(this.dynamicPages.get(0))); }
		else { ticks = ticks + 180; }
		this.AnimateTask(player, false, null, null, null, null, null, this.dynamicPages, ticks, 0);
	}
	
   /**
	* Handles animations for the skull owner of a custom item.
    * 
    * @param player - The player which will have animations handled.
    */
	private void ownerTasks(final Player player) {
		long ticks = 0;
		final Iterator<String> it = this.dynamicOwners.iterator();
		while (it.hasNext()) {
			final String owner = it.next();
			if (Utils.getUtils().returnInteger(ItemHandler.getItem().getDelayFormat(owner)) != null) { ticks = ticks + Utils.getUtils().returnInteger(ItemHandler.getItem().getDelayFormat(owner)); }
			else { ticks = ticks + 180; }
			this.AnimateTask(player, it.hasNext(), null, null, null, owner, null, null, ticks, 0);
		}
	}
	
   /**
	* Handles animations for the skull texture of a custom item.
	* 
	* @param player - The player which will have animations handled.
	*/
	private void textureTasks(final Player player) {
		long ticks = 0;
		final Iterator<String> it = this.dynamicTextures.iterator();
		while (it.hasNext()) {
			final String texture = it.next();
			if (Utils.getUtils().returnInteger(ItemHandler.getItem().getDelayFormat(texture)) != null) { ticks = ticks + Utils.getUtils().returnInteger(ItemHandler.getItem().getDelayFormat(texture)); }
			else { ticks = ticks + 180; }
			this.AnimateTask(player, it.hasNext(), null, null, null, null, texture, null, ticks, 0);
		}
	}
	
   /**
	* Runs the animation task of a custom item.
	* 
	* @param player - The player which will have animations handled.
	* @param hasNext - The AnimationTask has another iteration.
	* @param nameString - The display name to be set to the custom item.
	* @param loreString - The displayed lore to be set to the custom item.
	* @param materialString - The material to be set to the custom item.
	* @param ownerString - The skull owner to be set to the custom item.
	* @param textureString - The skull texture to be set to the custom item.
	* @param pagesString - The book pages to be set to the custom item.
	* @param UpdateDelay - The delay to wait before starting the single animation.
	* @param position - The position in only the lore iteration.
    */
	private void AnimateTask(final Player player, final boolean hasNext, final String nameString, final List<String> loreString, final String materialString, final String ownerString, final String textureString, final List<String> pagesString, final long UpdateDelay, final int position) {
		final ItemMap itemMap = this.itemMap;
		new BukkitRunnable() {
			@Override
			public void run() {
				if (!stopAnimations) {
				// ============== Animate Within the Player Inventory ============== //
				for (ItemStack inPlayerInventory: player.getInventory().getContents()) {
					if (inPlayerInventory != null && itemMap.getTempItem() != null && itemMap.isSimilar(inPlayerInventory)) {
						if (nameString != null) { setNameData(player, inPlayerInventory, nameString); } 
						else if (loreString != null) { setLoreData(player, inPlayerInventory, loreString); }
						else if (materialString != null) { setMaterialData(player, inPlayerInventory, materialString); }
						else if (pagesString != null) { setPagesData(player, inPlayerInventory, pagesString); }
						else if (ownerString != null || textureString != null) { setSkull(player, inPlayerInventory, ownerString, textureString); }
					}
				}
				// =============== Animate Within the Player's Armor =============== //
				for (ItemStack inPlayerInventory: player.getInventory().getArmorContents()) {
					if (inPlayerInventory != null && itemMap.getTempItem() != null && itemMap.isSimilar(inPlayerInventory)) {
						if (nameString != null) { setNameData(player, inPlayerInventory, nameString); } 
						else if (loreString != null) { setLoreData(player, inPlayerInventory, loreString); }
						else if (materialString != null) { setMaterialData(player, inPlayerInventory, materialString); }
						else if (pagesString != null) { setPagesData(player, inPlayerInventory, pagesString); }
						else if (ownerString != null || textureString != null) { setSkull(player, inPlayerInventory, ownerString, textureString); }
					}
				}
				// ========== Animate Within the Player Crafting/Chests ============ //
				for (ItemStack inPlayerInventory: player.getOpenInventory().getTopInventory().getContents()) {
					if (inPlayerInventory != null && itemMap.getTempItem() != null && itemMap.isSimilar(inPlayerInventory)) {
						if (nameString != null) { setNameData(player, inPlayerInventory, nameString); } 
						else if (loreString != null) { if (menu) { setLoreData(player, inPlayerInventory, menuLores.get(position)); } else { setLoreData(player, inPlayerInventory, loreString); } }
						else if (materialString != null) { setMaterialData(player, inPlayerInventory, materialString); }
						else if (pagesString != null) { setPagesData(player, inPlayerInventory, pagesString); }
						else if (ownerString != null || textureString != null) { setSkull(player, inPlayerInventory, ownerString, textureString); }
					}
				}
				// ============== Animate Within the Player's Cursor =============== //
				if (player.getItemOnCursor().getType() != null && player.getItemOnCursor().getType() != Material.AIR && itemMap.getTempItem() != null && itemMap.isSimilar(player.getItemOnCursor())) {
					ItemStack item = new ItemStack(player.getItemOnCursor());
					if (InventoryClick.getCursor(PlayerHandler.getPlayer().getPlayerID(player)) != null && itemMap.isSimilar(InventoryClick.getCursor(PlayerHandler.getPlayer().getPlayerID(player)))) { item = new ItemStack(InventoryClick.getCursor(PlayerHandler.getPlayer().getPlayerID(player))); }
					if (nameString != null) { setNameData(player, player.getItemOnCursor(), nameString); } 
					else if (loreString != null) { setLoreData(player, player.getItemOnCursor(), loreString); }
					else if (materialString != null) { setMaterialData(player, player.getItemOnCursor(), materialString); }
					else if (pagesString != null) { setPagesData(player, player.getItemOnCursor(), pagesString); }
					else if (ownerString != null || textureString != null) { setSkull(player, player.getItemOnCursor(), ownerString, textureString); }
					InventoryClick.putCursor(PlayerHandler.getPlayer().getPlayerID(player), item);
				}
				// ============== This has Concluded all Animations.. ============== //
				if (!hasNext) { 
					if (nameString != null) { nameTasks(player); }
					else if (loreString != null) { loreTasks(player); }
					else if (materialString != null) { materialTasks(player); }
					else if (pagesString != null) { pagesTasks(player); }
					else if (ownerString != null) { ownerTasks(player); }
					else if (textureString != null) { textureTasks(player); }
				}
				}
			}
		}.runTaskLater(ItemJoin.getInstance(), UpdateDelay);
	}
	
   /**
	* Sets the specified display name to the specified item.
	* 
	* @param player - The player which will have animations handled.
	* @param reviseItem - The item to have its display name animated.
	* @param nameString - The name to set to the item.
	*/
	private void setNameData(final Player player, final ItemStack reviseItem, final String nameString) {
		final ItemMeta tempmeta = reviseItem.getItemMeta();
		tempmeta.setDisplayName(Utils.getUtils().translateLayout(ItemHandler.getItem().cutDelay(nameString) + this.itemMap.getLegacySecret(), player));
		reviseItem.setItemMeta(tempmeta);
	}
	
   /**
	* Sets the specified displayed lore to the specified item.
	* 
	* @param player - The player which will have animations handled.
	* @param reviseItem - The item to have its displayed lore animated.
	* @param loreString - The lore to set to the item.
	*/
	private void setLoreData(final Player player, final ItemStack reviseItem, final List<String> loreString) {
		final ItemMeta tempmeta = reviseItem.getItemMeta();
		final List < String > loreList = loreString;
		final List < String > loreFormatList = new ArrayList < String > ();
		for (int k = 0; k < loreList.size(); k++) {
			String formatLore = ItemHandler.getItem().cutDelay(loreList.get(k));
			formatLore = Utils.getUtils().translateLayout(formatLore, player);
			loreFormatList.add(formatLore);
		}
		tempmeta.setLore(loreFormatList);
		reviseItem.setItemMeta(tempmeta);
	}
	
   /**
	* Sets the specified bukkit material to the specified item.
	* 
	* @param player - The player which will have animations handled.
	* @param reviseItem - The item to have its material animated.
	* @param materialString - The material to set to the item.
	*/
	private void setMaterialData(final Player player, final ItemStack reviseItem, String materialString) {
		Material mat = null;
		materialString = ItemHandler.getItem().cutDelay(materialString);
		if (materialString.contains(":")) { 
			final String[] parts = materialString.split(":");
			if (ServerHandler.getServer().hasSpecificUpdate("1_13")) {
				if (!Utils.getUtils().isInt(parts[0])) { parts[0] = "LEGACY_" + parts[0]; }
				if (!Utils.getUtils().isInt(parts[0])) { mat = LegacyAPI.getLegacy().getMaterial(Material.getMaterial(parts[0].toUpperCase()), (byte) Integer.parseInt(parts[1])); } 
				else { mat = LegacyAPI.getLegacy().getMaterial(Integer.parseInt(parts[0]), (byte) Integer.parseInt(parts[1])); }
				if (mat != null && mat != Material.AIR) { reviseItem.setType(mat); }
			} else {
				mat = ItemHandler.getItem().getMaterial(parts[0], null);
				if (mat != null && mat != Material.AIR) { reviseItem.setType(mat); }	
				LegacyAPI.getLegacy().setDurability(reviseItem, (byte) Integer.parseInt(parts[1]));
			}
		} else {
			mat = ItemHandler.getItem().getMaterial(materialString, null);
			if (mat != null && mat != Material.AIR) { reviseItem.setType(mat); }
		}
	}
	
   /**
	* Sets the specified book pages to the specified item.
	* 
	* @param player - The player which will have animations handled.
	* @param reviseItem - The item to have its book pages animated.
	* @param pagesString - The book pages to set to the item.
	*/
	private void setPagesData(final Player player, final ItemStack reviseItem, final List<String> pagesString) {
		if (ServerHandler.getServer().hasSpecificUpdate("1_8")) {
			reviseItem.setItemMeta(this.itemMap.setJSONBookPages(player, reviseItem, pagesString).getItemMeta());
		} else {
			final ItemMeta itemMeta = LegacyAPI.getLegacy().setBookPages(player, reviseItem.getItemMeta(), pagesString, this.itemMap);
			reviseItem.setItemMeta(itemMeta);
		}
	}

   /**
	* Sets the specified skull owner and texture to the specified item.
	* 
	* @param player - The player which will have animations handled.
	* @param reviseItem - The item to have its skull owner and skull texture animated.
	* @param ownerString - The skull owner to set to the item.
	* @param textureString - The skull texture to set to the item.
	*/
	private void setSkull(final Player player, ItemStack reviseItem, final String ownerString, final String textureString) {
		ItemMeta tempMeta = reviseItem.getItemMeta();
		if (ownerString != null) {
			tempMeta = ItemHandler.getItem().setSkullOwner(tempMeta, Utils.getUtils().translateLayout(ItemHandler.getItem().cutDelay(ownerString), player));
		} else if (textureString != null && textureString.contains("hdb-") && this.itemMap.isHeadDatabase()) {
			final HeadDatabaseAPI api = new HeadDatabaseAPI();
			final ItemStack sk = api.getItemHead(ItemHandler.getItem().cutDelay(textureString).replace("hdb-", ""));
			reviseItem = (sk != null ? sk : reviseItem);
		} else if (textureString != null) {
			try {
				final GameProfile gameProfile = new GameProfile(UUID.randomUUID(), null);
				gameProfile.getProperties().put("textures", new Property("textures", new String(ItemHandler.getItem().cutDelay(textureString))));
				final Field declaredField = tempMeta.getClass().getDeclaredField("profile");
				declaredField.setAccessible(true);
				declaredField.set(tempMeta, gameProfile);
			} catch (Exception e) { ServerHandler.getServer().sendDebugTrace(e); }
		}
		reviseItem.setItemMeta(tempMeta);
	}
	
   /**
	* Sets the `/itemjoin menu` as being open for a player.
	* 
	* @param bool - Specifies if the menu is open or closed.
	* @param stage - The stage of the menu as being 0) click to modify or 1) a swap-item.
	*/
	public void setMenu(final boolean bool, final int stage) {
		this.menu = bool;
		if (bool && this.dynamicLores != null) {
			this.menuLores = new ArrayList<List<String>>();
			for (List<String> lores : this.dynamicLores) {
				final List<String> tempLores = new ArrayList<String>();
				for (String lore : lores) { tempLores.add(lore); }
				tempLores.add("&7");
				tempLores.add("&6---------------------------");
				if (stage == 1) { tempLores.add("&7*Click to set as a swap-item."); } 
				else { tempLores.add("&7*Click to modify this custom item."); }
				tempLores.add("&9&lNode: &a" + this.itemMap.getConfigName());
				tempLores.add("&7");
				this.menuLores.add(tempLores);
			}
		}
	}
}