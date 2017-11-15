package me.RockinChaos.itemjoin.handlers;

import java.util.HashMap;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.scheduler.BukkitRunnable;

import me.RockinChaos.itemjoin.ItemJoin;
import me.RockinChaos.itemjoin.cacheitems.CreateItems;
import me.RockinChaos.itemjoin.utils.Utils;

public class AnimationHandler {
	
	// This is a currently unimplemented feature that is currently in development so it is blocked so only the DEV can work on it.
	// This will soon be the handler for animations that allows you to update your items if placeholders change or to have an animated lore or item name.
	
	private static HashMap < Player, Boolean > setCanceled = new HashMap < Player, Boolean > ();
	private static HashMap < Player, Boolean > SafeReady = new HashMap < Player, Boolean > ();

	public static void cancelRefresh(Player player) {
		setCanceled.put(player, true);
	}

	public static void refreshItems(final Player player) {
		cancelRefresh(player);
		if (ConfigHandler.getConfig("items.yml").getBoolean("items-Dynamic") == true) {
			if (SafeReady.get(player) != null && SafeReady.get(player) == false) {
				new BukkitRunnable() {
					public void run() {
						if (SafeReady.get(player) == true) {
							setCanceled.put(player, false);
							setUpdating(player);
							SafeReady.remove(player);
							this.cancel();
						}
					}
				}.runTaskTimer(ItemJoin.getInstance(), 80L, 80L);
			} else {
				setCanceled.put(player, false);
				SafeReady.put(player, false);
				setUpdating(player);
			}
		}
	}

	public static void setUpdating(final Player player) {
		long UpdateDelay = 1;
		if (ConfigHandler.getConfig("items.yml").getString("items-UpdateDelay") != null) {
			UpdateDelay = ConfigHandler.getConfig("items.yml").getInt("items-UpdateDelay");
		}
		if (Utils.isConfigurable()) {
			for (final String item: ConfigHandler.getConfigurationSection().getKeys(false)) {
				final ConfigurationSection items = ConfigHandler.getItemSection(item);
				final String world = player.getWorld().getName();
				int Arbitrary = 0;
				String ItemID;
				if (WorldHandler.inWorld(items, world) && items.getString(".slot") != null) {
					String slotlist = items.getString(".slot").replace(" ", "");
					String[] slots = slotlist.split(",");
					for (String slot: slots) {
						if (slot.equalsIgnoreCase("Arbitrary")) {
							Arbitrary = Arbitrary + 1;
							ItemID = slot + Arbitrary;
						} else {
							ItemID = slot;
						}
						final String finalID = ItemID;
						new BukkitRunnable() {
							public void run() {
								if (player.isOnline() && setCanceled.get(player) != null && setCanceled.get(player) != true || player.isOnline() && setCanceled.get(player) == null) {
									ItemStack inStoredItems = CreateItems.items.get(world + "." + player.getName().toString() + ".items." + finalID + item);
									for (final ItemStack inPlayerInventory: player.getInventory().getContents()) {
										if (inPlayerInventory != null && inStoredItems != null && ItemHandler.isSimilar(inPlayerInventory, inStoredItems)) {
											setRefresh(items, item, world, inPlayerInventory, player, finalID);
										}
									}
									for (final ItemStack inPlayerInventory: player.getInventory().getArmorContents()) {
										if (inPlayerInventory != null && inStoredItems != null && ItemHandler.isSimilar(inPlayerInventory, inStoredItems)) {
											setRefresh(items, item, world, inPlayerInventory, player, finalID);
										}
									}
								} else if (!player.isOnline() || setCanceled.get(player) == true) {
									new BukkitRunnable() {
										public void run() {
											setCanceled.remove(player);
											SafeReady.put(player, true);
										}
									}.runTaskLater(ItemJoin.getInstance(), 5L);
									this.cancel();
								}
							}
						}.runTaskTimer(ItemJoin.getInstance(), 20L, 20L * UpdateDelay);
					}
				}
			}
		}
	}

	public static void setRefresh(ConfigurationSection items, String item, String world, ItemStack inPlayerInventory, Player player, String ItemID) {
		ItemMeta tempmeta = inPlayerInventory.getItemMeta();
		CreateItems.setName(items, tempmeta, inPlayerInventory, player, ItemID);
		CreateItems.setLore(items, tempmeta, player);
		inPlayerInventory.setItemMeta(tempmeta);
		CreateItems.items.remove(world + "." + player.getName().toString() + ".items." + ItemID + item);
		CreateItems.items.put(world + "." + player.getName().toString() + ".items." + ItemID + item, inPlayerInventory);
	}
}