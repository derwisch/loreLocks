package com.github.derwisch.loreLocks;


import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.Chest;
import org.bukkit.entity.Player;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.EventHandler;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.entity.EntityBreakDoorEvent;
import org.bukkit.event.entity.EntityExplodeEvent;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryMoveItemEvent;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.event.inventory.InventoryType.SlotType;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.metadata.FixedMetadataValue;
import org.bukkit.metadata.MetadataValue;
 
public class LoreLocksListener implements Listener {
	
    @EventHandler
    public void onPlayerInteract_Chest(PlayerInteractEvent event) {
    	Player player = event.getPlayer();
        
        Block interactedBlock = event.getClickedBlock();
        
        if (interactedBlock == null)
        	return;
        
        Material blockMat = interactedBlock.getType(); 
        
        if (blockMat != Material.CHEST && blockMat != Material.TRAPPED_CHEST)
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
				player.sendMessage(ChatColor.DARK_GREEN + Settings.Messages.Key_Used + ChatColor.RESET);
			} else {
				if (difficulty <= 5 && difficulty != -1 && !player.hasPermission(Permissions.BYPASS)) {
					if (player.hasPermission(Permissions.getPickPermission(difficulty))) {
		    			LockGUI lockGUI = new LockGUI(player, chest.getInventory(), lock, (byte)difficulty);
		    			lockGUI.ShowLock();
		    			event.setCancelled(true);
					} else {
						player.sendMessage(ChatColor.DARK_RED + Settings.Messages.PermissionForLevelMissing(difficulty) + ChatColor.RESET);
	        			event.setCancelled(true);
					}
				} else {
					if (!player.hasPermission(Permissions.BYPASS)) {
							player.sendMessage(ChatColor.DARK_RED + Settings.Messages.Unpickable_Lock + ChatColor.RESET);
		        			event.setCancelled(true);
					} else {
						player.sendMessage(ChatColor.DARK_GREEN + Settings.Messages.Bypass);
					}
				}
			}
        }
    }

    @EventHandler
    public void onPlayerInteract_DoorPick(PlayerInteractEvent event) {
    	Player player = event.getPlayer();
        
        Block interactedBlock = event.getClickedBlock();
        
        if (event.isCancelled())
        	return;
        
        if (interactedBlock == null)
        	return;

        interactedBlock = LockedDoor.getBottomDoorBlock(interactedBlock.getLocation());
        
        if (!LockedDoor.isValid(interactedBlock.getType()))
        	return;
        
        if (!LoreLocks.instance.IsLockedDoor(interactedBlock.getLocation()))
        	return;
        
        if (LoreLocks.instance.isDoorOpen(interactedBlock))
        	return;
        
        
    	LockedDoor door = LockedDoor.GetAt(interactedBlock.getLocation());
    	
    	if (door == null)
    		return;
    	
        ItemStack lock = door.Lock;
        
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
				player.sendMessage(ChatColor.DARK_GREEN + Settings.Messages.Key_Used + ChatColor.RESET);
			} else {
				if (difficulty <= 5 && difficulty != -1 && !player.hasPermission(Permissions.BYPASS)) {
					if (player.hasPermission(Permissions.getPickPermission(difficulty))) {
		    			LockGUI lockGUI = new LockGUI(player, door, lock, (byte)difficulty);
		    			lockGUI.ShowLock();
		    			event.setCancelled(true);
					} else {
						player.sendMessage(ChatColor.DARK_RED + Settings.Messages.PermissionForLevelMissing(difficulty) + ChatColor.RESET);
	        			event.setCancelled(true);
					}
				} else {
					if (!player.hasPermission(Permissions.BYPASS)) {
							player.sendMessage(ChatColor.DARK_RED + Settings.Messages.Unpickable_Lock + ChatColor.RESET);
		        			event.setCancelled(true);
					} else {
						player.sendMessage(ChatColor.DARK_GREEN + Settings.Messages.Bypass);
					}
				}
			}
        }
    }

    @EventHandler(priority = EventPriority.LOWEST)
    public void onPlayerInteract_DoorLock(PlayerInteractEvent event) {
    	Player player = event.getPlayer();

        Block interactedBlock = event.getClickedBlock();

        if (interactedBlock == null)
        	return;
        
        interactedBlock = LockedDoor.getBottomDoorBlock(interactedBlock.getLocation());
        Location blockLoc = interactedBlock.getLocation();
        
        if (!LockedDoor.isValid(interactedBlock.getType()))
        	return;

        if (LoreLocks.instance.IsLockedDoor(blockLoc))
        	return;
        
        ItemStack itemInHand = player.getItemInHand();
        
        if (!LoreLocks.instance.IsLock(itemInHand))
        	return;

    	if (itemInHand.getAmount() > 1)
    	{
    		itemInHand.setAmount(itemInHand.getAmount() - 1);
    		player.setItemInHand(itemInHand);
    	}
    	else
    	{
    		player.setItemInHand(null);
    	}
    	
    	itemInHand.setAmount(1);
    	
    	LockedDoor door = new LockedDoor(blockLoc, itemInHand);
		
    	LockedDoor.LockedDoors.add(door);
		door.Index = LockedDoor.LockedDoors.indexOf(door);
    	
    	player.sendMessage(ChatColor.DARK_GREEN + Settings.Messages.Door_Locked + ChatColor.RESET);
    	
    	event.setCancelled(true);
    }
    
    @EventHandler
    public void onBlockBreak_Door(BlockBreakEvent event) {
        Block interactedBlock = event.getBlock();
        
        if (event.isCancelled())
        	return;
        
        if (interactedBlock == null)
        	return;

        interactedBlock = LockedDoor.getBottomDoorBlock(interactedBlock.getLocation());
        
        if (!LockedDoor.isValid(interactedBlock.getType()))
        	return;
        
        if (!LoreLocks.instance.IsLockedDoor(interactedBlock.getLocation()))
        	return;
        
        LockedDoor door = LockedDoor.GetAt(interactedBlock.getLocation());
        LockedDoor.removeDoor(door);
    }
    
    @EventHandler
    public void onInventoryClick_LockGUIClick(InventoryClickEvent event) {
    	int hashCode = event.getView().getPlayer().getOpenInventory().hashCode();
    	
    	LockGUI gui = LockGUI.GetGUI(hashCode);
    	
    	if (gui != null) {
    		event.setCancelled(true);
    		if (event.getRawSlot() > 9 && event.getRawSlot() < (12 + LoreLocks.instance.GetDifficulty(gui.Lock)) && event.getSlot() != -999) {
	    		gui.Click(event.getSlot(), event.isRightClick(), event.isShiftClick());
	    	}
    	}
    }
    
    @EventHandler
    public void onInventoryClick_CreateKey(InventoryClickEvent event) {
    	ItemStack currentItem = event.getCurrentItem();
    	ItemStack cursorItem = event.getCursor();
    	if (cursorItem.getAmount() == 1) {
	    	if (LoreLocks.instance.IsLockPick(cursorItem)) {
	    		if (LoreLocks.instance.IsLock(currentItem)) {
	    			ItemStack key = LoreLocks.instance.CreateKey(currentItem);
	    			KeyCreateEvent keyCreateEvent = new KeyCreateEvent((Player) event.getWhoClicked(), key);
	    			LoreLocks.instance.getServer().getPluginManager().callEvent(keyCreateEvent);
	    			event.setCursor(keyCreateEvent.getKey());
	    	    	event.setCancelled(true);
	        	}
	    	}
    	}
    }
    
    @EventHandler
    public void onInventoryClick_ApplyKey(InventoryClickEvent event) {
    	ItemStack currentItem = event.getCurrentItem();
    	ItemStack cursorItem = event.getCursor();
    	if (cursorItem.getAmount() == 1) {
	    	if (LoreLocks.instance.IsKey(currentItem)) {
	    		if (LoreLocks.instance.IsLock(cursorItem)) {
	    			LoreLocks.instance.SetSignature(cursorItem, currentItem);
	    	    	event.setCancelled(true);
	        	}
	    	}
    	}
    }
    
    @EventHandler
    public void onInventoryClick_NameKeyPrepare(InventoryClickEvent event) {
    	if (event.getInventory().getType() == InventoryType.ANVIL) {
    		if (event.getSlotType() == SlotType.CRAFTING) {
        		
        		ItemStack stackCursor = event.getCursor(); 
        		ItemStack stackCurrent = event.getCurrentItem(); 
        		
        		if (LoreLocks.instance.IsKey(stackCursor)) {
        			ItemMeta stackMeta = stackCursor.getItemMeta();
        			String newName = stackMeta.getDisplayName();
        			newName = newName.replace(ChatColor.WHITE.toString(), "");
        			newName = newName.replace(ChatColor.RESET.toString(), "");
        			stackMeta.setDisplayName(newName);
        			stackCursor.setItemMeta(stackMeta);
        			event.setCurrentItem(stackCursor);
        			event.setCursor(null);
        			event.setCancelled(true);
        			return;
        		}
        		
        		if (LoreLocks.instance.IsKey(stackCurrent)) {
        			ItemMeta stackMeta = stackCurrent.getItemMeta();
        			String newName = stackMeta.getDisplayName();
        			newName = ChatColor.WHITE + newName + ChatColor.RESET;
        			stackMeta.setDisplayName(newName);
        			stackCurrent.setItemMeta(stackMeta);
        			event.setCurrentItem(null);
        			event.setCursor(stackCurrent);
        			event.setCancelled(true);
        			return;
        		} 
    		}
    	}
    }
    
    @EventHandler
    public void onInventoryClick_NameKeyResult(InventoryClickEvent event) {
    	if (event.getInventory().getType() == InventoryType.ANVIL) {
    		if (event.getSlotType() == SlotType.RESULT) {
        		
        		ItemStack stack = event.getCurrentItem(); 

        		if (LoreLocks.instance.IsKey(stack)) {
        			ItemMeta stackMeta = stack.getItemMeta();
        			String newName = stackMeta.getDisplayName();
        			newName = ChatColor.WHITE + newName + ChatColor.RESET;
        			stackMeta.setDisplayName(newName);
        			stack.setItemMeta(stackMeta);
        			event.setCurrentItem(null);
        			event.setCursor(stack);
        			event.getInventory().setContents(new ItemStack[0]);
        			event.getInventory().setItem(0, null);
        			event.setCancelled(true);
        		}
    		}
    	}
    }

    @EventHandler
    public void onInventoryMoveItem(InventoryMoveItemEvent event) {
    	if (Settings.EnableHopperProtection) {
	    	Inventory srcInv = event.getSource();
	    	ItemStack[] srcInvContents = srcInv.getContents(); 
	    	ItemStack movedItem = event.getItem();
	    	
	    	if (srcInvContents.length > 0 && srcInvContents[0] != null && LoreLocks.instance.IsLock(srcInvContents[0])) {
	    		event.setCancelled(true);	
	    	} else if (movedItem != null && LoreLocks.instance.IsLock(movedItem)) {
	    		event.setCancelled(true);
	    	}    	
    	}
    }
     
    static ArrayList<Material> supportedLockedContainerBlocks = new ArrayList<Material>();
    static ArrayList<Material> supportedLockedDoorBlocks = new ArrayList<Material>();
    static {
    	supportedLockedContainerBlocks.add(Material.CHEST);
    	supportedLockedContainerBlocks.add(Material.TRAPPED_CHEST);

    	supportedLockedDoorBlocks.add(Material.WOODEN_DOOR);
    	supportedLockedDoorBlocks.add(Material.IRON_DOOR_BLOCK);
    }
    
    @EventHandler
    public void onEntityExplode(EntityExplodeEvent event) {
    	if (Settings.EnableExplosionProtection) {
	    	Iterator<Block> blocks = event.blockList().iterator();
	
	    	while (blocks.hasNext()) {
	    		Block block = blocks.next();
	
	    		if (supportedLockedContainerBlocks.contains(block.getType())) {
	    			if (LoreLocks.instance.IsLock(((InventoryHolder)block.getState()).getInventory().getItem(0))) {
	    	    		blocks.remove();
	    			}
	    		}
	    		
	    		if (supportedLockedDoorBlocks.contains(block.getType())) {
	    			if (LockedDoor.GetAt(block.getLocation()) != null) {
	    	    		blocks.remove();
	    			}
	    		}
	    	}	
    	}
    }
    
    @EventHandler
    public void onEntityBreakDoor(EntityBreakDoorEvent event) {
    	if (Settings.EnableZombieProtection) {
	    	LockedDoor door = LockedDoor.GetAt(event.getBlock().getLocation());
	    	if (door != null) {
	        	event.setCancelled(true);	
	    	}
    	} else {
	        if (event.isCancelled())
	        	return;

	        if (!LoreLocks.instance.IsLockedDoor(event.getBlock().getLocation()))
	        	return;
	        
	        LockedDoor door = LockedDoor.GetAt(event.getBlock().getLocation());
	        LockedDoor.removeDoor(door);
    	}
    }
    
    @EventHandler
    public void onPlayerDeath(PlayerDeathEvent event) {
    	List<MetadataValue> metaValues = event.getEntity().getMetadata("LockPickDeath");
    	if (metaValues == null || metaValues.size() == 0)
    		return;

		boolean diedFromTrap = false;
    	
    	for (MetadataValue metaValue : metaValues) {
    		if (metaValue.getOwningPlugin() instanceof LoreLocks) {
    			diedFromTrap = metaValue.asBoolean();
    			break;
    		}
    	}
    	
    	if (diedFromTrap) {
    		event.setDeathMessage(event.getEntity().getDisplayName() + " died from a trapped chest.");
    	}
		event.getEntity().setMetadata("LockPickDeath", new FixedMetadataValue(LoreLocks.instance, false));
    }
}