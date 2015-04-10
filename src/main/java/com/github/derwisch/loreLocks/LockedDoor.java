package com.github.derwisch.loreLocks;

import java.util.ArrayList;
import java.util.Set;

import org.bukkit.Bukkit;
import org.bukkit.Effect;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.inventory.ItemStack;


public class LockedDoor {
	
	public Location Location;
	public ItemStack Lock;
	public Block[] DoorBlocks = new Block[2];

	public int Index = -1;
	
	private static ConfigAccessor configAccessor;
	public static ArrayList<LockedDoor> LockedDoors = new ArrayList<LockedDoor>();
	
	public static void LoadDoors() {
		configAccessor = new ConfigAccessor(LoreLocks.instance, "doors.yml");
		//configAccessor.saveConfig();
		FileConfiguration cfg = configAccessor.getConfig();
		
		Set<String> keys = cfg.getKeys(false);
		
		int doorAmount = 0;
		
		for (String key : keys) {
			String worldName = cfg.getString(key + ".loc.world");
			int x = cfg.getInt(key + ".loc.x");
			int y = cfg.getInt(key + ".loc.y");
			int z = cfg.getInt(key + ".loc.z"); 

			ItemStack lock = cfg.getItemStack(key + ".lock");

        	//System.out.print("[LoreLocks][Door] {i} Loading door \"" + key + "\" at [" + worldName + ":" + x + "|" + y + "|" + z + "] locked with a " + lock.getItemMeta().getDisplayName());
        	LockedDoor door = new LockedDoor(new Location(Bukkit.getWorld(worldName),x,y,z), lock);
			LockedDoors.add(door);
			door.Index = LockedDoors.indexOf(door);
			doorAmount++;
		}
		LoreLocks.logger.info("Loaded " + doorAmount + " doors.");
	}
	
	public static void SaveDoors() {
		FileConfiguration cfg = configAccessor.getConfig();
		
		// Clear config to prevent ghost doors
		Set<String> keys = cfg.getKeys(false);
		for (String key : keys) {
			cfg.set(key, null);
		}
		
		int i = 0;
		for (LockedDoor door : LockedDoors) {
			String worldName = door.Location.getWorld().getName();
			int x = door.Location.getBlockX();
			int y = door.Location.getBlockY();
			int z = door.Location.getBlockZ();
			
			ItemStack lock = door.Lock;

			cfg.set(i + ".loc.world", worldName);
			cfg.set(i + ".loc.x", x);
			cfg.set(i + ".loc.y", y);
			cfg.set(i + ".loc.z", z);

			cfg.set(i + ".lock", lock);
			
			i++;
		}
		
		configAccessor.saveConfig();
	}
	
	public static LockedDoor GetAt(Location loc) {
		for (LockedDoor door : LockedDoor.LockedDoors) {
			if (door.Location.equals(loc)) {
				return door;
			}
			if (door.Location.add(0, 1, 0).equals(loc)) {
				return door;
			}
			if (door.Location.subtract(0, 1, 0).equals(loc)) {
				return door;
			}
		}
		return null;
	}
	
	public LockedDoor(Location loc, ItemStack lock) {
		Location = loc;
		Lock = lock;
	}
	
	public void OpenDoor() {
		Block block = Location.getWorld().getBlockAt(Location);
		Block blockTop = block.getRelative(BlockFace.UP);
		Block blockBottom = block.getRelative(BlockFace.DOWN);
		
		if (isValid(blockTop.getType())) {
			toggleDoor(block);
		} else if (isValid(blockBottom.getType())) {
			toggleDoor(blockBottom);
		} else {
        	toggleDoor(block);
		}
	}
	
    private void toggleDoor(Block door) {
        Block topHalf = door.getRelative(BlockFace.UP);

        door.setData((byte) (door.getData() ^ 0x4));
        
        door.getWorld().playEffect(door.getLocation(), Effect.DOOR_TOGGLE, 0);

        if (isValid(topHalf.getType())) {
            topHalf.setData((byte) (topHalf.getData() ^ 0x4));
        }
    }
    
    public static boolean isValid(Material material) {
        return material == Material.IRON_DOOR_BLOCK || material == Material.WOODEN_DOOR || material == Material.SPRUCE_DOOR || material == Material.BIRCH_DOOR || material == Material.JUNGLE_DOOR || material == Material.ACACIA_DOOR || material == Material.DARK_OAK_DOOR || material == Material.FENCE_GATE || material == Material.SPRUCE_FENCE_GATE || material == Material.BIRCH_FENCE_GATE || material == Material.JUNGLE_FENCE_GATE || material == Material.DARK_OAK_FENCE_GATE || material == Material.ACACIA_FENCE_GATE;
    }
    
    public static Block getBottomDoorBlock(Location location) {
		Block block = location.getWorld().getBlockAt(location);
		Block blockTop = block.getRelative(BlockFace.UP);
		Block blockBottom = block.getRelative(BlockFace.DOWN);
		
		if (isValid(blockTop.getType())) {
			return block;
		} else if (isValid(blockBottom.getType())) {
			return blockBottom;
		} else {
			return block;
		}
    }

	public boolean isAt(Location loc) {
		if (Location.equals(loc))
			return true;
		if (Location.add(0,1,0).equals(loc))
			return true;
		if (Location.subtract(0,1,0).equals(loc))
			return true;
		
		return false;
	}

	public static void removeDoor(LockedDoor door) {
		
		ItemStack lock = door.Lock;
		
		door.Location.getWorld().dropItem(door.Location.add(0, 1, 0), lock);
		
		LockedDoors.remove(door.Index);
	}
}
