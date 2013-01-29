package com.github.derwisch.loreLocks;


import java.util.List;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.Chest;
import org.bukkit.entity.Player;
import org.bukkit.event.Listener;
import org.bukkit.event.EventHandler;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
 
public class LoreLocksListener implements Listener {
	
    @EventHandler
    public void onClick(PlayerInteractEvent event) {
    	Player player = event.getPlayer();
        
        Block interactedBlock = event.getClickedBlock();
        
        if (interactedBlock == null)
        	return;
        
        if (interactedBlock.getType() != Material.CHEST)
        	return;
        
        Chest chest = (Chest)interactedBlock.getState();
        
        ItemStack lock = chest.getInventory().getItem(0);
        
        if (lock == null)
        	return;
        
        ItemMeta lockMeta = lock.getItemMeta();
        
        if (lockMeta == null)
        	return;
        
        List<String> lockLore = lockMeta.getLore();

        if (lockLore == null)
        	return;
        
        if (LoreLocks.instance.IsLock(lock)) {
			int difficulty = LoreLocks.instance.GetDifficulty(lock);
			if (LoreLocks.instance.PlayerHasKey(player, lock)) {
				player.sendMessage(ChatColor.DARK_GREEN + "You opened the chest with your key. " + ChatColor.RESET);
			} else {
				if (difficulty <= 5 && difficulty != -1 && !player.hasPermission(Permissions.BYPASS)) {
	    			LockGUI lockGUI = new LockGUI(player, chest.getBlockInventory(), lock, (byte)difficulty);
	    			lockGUI.ShowLock();
	    			event.setCancelled(true);
				} else {
					if (!player.hasPermission(Permissions.BYPASS)) {
						
							player.sendMessage(ChatColor.DARK_RED + "This lock can't be picked!" + ChatColor.RESET);
		        			event.setCancelled(true);
					} else {
						player.sendMessage(ChatColor.DARK_GREEN + "[LoreLocks] " + ChatColor.RESET + "Lock bypassed");
					}
				}
			}
        }
        
    }
    
    @EventHandler
    public void onInventoryClick(InventoryClickEvent event) {
    	int hashCode = event.getView().getPlayer().getOpenInventory().hashCode();
    	LockGUI gui = LockGUI.GetGUI(hashCode);
    	
    	if (gui != null && event.getSlot() != -999) {
    		gui.Click(event.getSlot(), event.isRightClick(), event.isShiftClick());
    		event.setCancelled(true);
    	}
    }
    
    @EventHandler
    public void onInventoryClick_CreateKey(InventoryClickEvent event) {
    	ItemStack currentItem = event.getCurrentItem();
    	ItemStack cursorItem = event.getCursor();
    	
    	if (LoreLocks.instance.IsLockPick(cursorItem)) {
    		if (LoreLocks.instance.IsLock(currentItem)) {
    			
    			ItemStack key = LoreLocks.instance.CreateKey(currentItem);
    			
    	    	event.setCursor(key);
    	    	event.setCancelled(true);
        	}
    	}
    }
}