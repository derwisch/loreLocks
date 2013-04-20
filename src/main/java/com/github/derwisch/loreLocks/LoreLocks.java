package com.github.derwisch.loreLocks;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.logging.Logger;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Server;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.ShapedRecipe;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.plugin.java.JavaPlugin;

public class LoreLocks extends JavaPlugin {
	
	
	public static LoreLocks instance;
	public static Server server;
	public static Logger logger;
	
	private LoreLocksListener listener;
	private FileConfiguration configuration;
	
    @Override
    public void onEnable() {
    	instance = this;
    	server = this.getServer();
    	logger = this.getLogger();
    	
    	saveDefaultConfig();
    	configuration = this.getConfig();
    	Settings.LoadConfiguration(configuration);
    	
    	listener = new LoreLocksListener();
        this.getServer().getPluginManager().registerEvents(listener, this);
        
        LockedDoor.LoadDoors();
        
    	logger.info("Enabled " + this.getDescription().getName() + " v" + this.getDescription().getVersion());
    }

    public ShapedRecipe LockPickRecipe; 

	@Override
    public void onDisable() {
		LockedDoor.SaveDoors();

    	logger.info("Disabled " + this.getDescription().getName() + " v" + this.getDescription().getVersion());
    }
    
	public ItemStack FactoryLock(Lock lock) {
		ItemStack lockStack = new ItemStack(Material.getMaterial(lock.LockID));
		ItemMeta lockMeta = lockStack.getItemMeta();
		ArrayList<String> lockLore = new ArrayList<String>();
		
		ChatColor color = ChatColor.WHITE;
		
		switch (lock.Difficulty) {
			case 1:
				color = ChatColor.WHITE;
				break;
			case 2:
				color = ChatColor.AQUA;
				break;
			case 3:
				color = ChatColor.DARK_PURPLE;
				break;
			case 4:
				color = ChatColor.GOLD;
				break;
			case 5:
				color = ChatColor.DARK_GREEN;
				break;
			case 6:
				color = ChatColor.DARK_RED;
				break;
		}
		
		lockMeta.setDisplayName(color + lock.LockName + ChatColor.RESET);
		if (lock.Difficulty == 6) {
			lockLore.add(ChatColor.GRAY + "Unpickable" + ChatColor.RESET);
			lockLore.add(ChatColor.GRAY + "Can only be opened" + ChatColor.RESET);
			lockLore.add(ChatColor.GRAY + "with the right key" + ChatColor.RESET);
		} else {
			lockLore.add(ChatColor.GRAY + "Difficulty: " + lock.Difficulty + ChatColor.RESET);
		}
		lockMeta.setLore(lockLore);
		lockStack.setItemMeta(lockMeta);
		lockStack.setDurability(lock.LockDV);
		
		return lockStack;
	}
	
	public boolean IsLock(ItemStack stack) {
		ItemMeta meta = stack.getItemMeta();
		if (meta == null) {
			return false;
		}
		
		String displayName = meta.getDisplayName();
		if (displayName == null || displayName == "") {
			return false;
		}
		
		List<String> lore = meta.getLore();
		
		if (lore == null) {
			return false;
		}
		
		for (int i = 1; i < 7; i++) {
			if (i == 6 && lore.contains(ChatColor.GRAY + "Unpickable" + ChatColor.RESET)) {
				return true;
			}
			if (lore.contains(ChatColor.GRAY + "Difficulty: " + i + ChatColor.RESET)) {
				return true;
			}
		}
		
		return false;
	}
	
	public boolean isDoorOpen(Block block) {
		Block blockTop = block.getRelative(BlockFace.UP);
		Block blockBottom = block.getRelative(BlockFace.DOWN);
		
		if (LockedDoor.isValid(blockTop.getType())) {
			return ((block.getData() & 0x4) == 4);
		} else if (LockedDoor.isValid(blockBottom.getType())) {
			return ((blockBottom.getData() & 0x4) == 4);
		} else {
    		return ((block.getData() & 0x4) == 4);
		}
	}
	
	public boolean IsLockedDoor(Location loc) {
		for (LockedDoor door : LockedDoor.LockedDoors) {
			if (door.isAt(loc)) {
				return true;
			}
		}
		return false;
	}
	
	public boolean IsLockPick(ItemStack stack) {
		ItemMeta meta = stack.getItemMeta();
		if (meta == null) {
			return false;
		}
		
		String displayName = meta.getDisplayName();
		if (displayName == null || displayName == "") {
			return false;
		}
		
		return displayName.equals(ChatColor.WHITE + Settings.LockPickName + ChatColor.RESET);
	}
	
	public ItemStack CreateKey(ItemStack lock) {
		if (!IsLock(lock)) {
			return null;
		}

		Date now = new Date();
		String keyInfo = ""; 
		
		ItemMeta meta = lock.getItemMeta();
		List<String> lore = meta.getLore();
		
		for (String line : lore) {
			if (line.startsWith(ChatColor.BLACK.toString() + "#" + ChatColor.MAGIC.toString())) {
				keyInfo = line;
			}
		}
		
		if (keyInfo == "") {
			keyInfo = ChatColor.BLACK.toString() + "#" + ChatColor.MAGIC.toString() + now.getTime() + ChatColor.RESET.toString();
			lore.add(keyInfo);
		}
		meta.setLore(lore);
		lock.setItemMeta(meta);
		
		
		ItemStack key = new ItemStack(Material.getMaterial(Settings.KeyID));
		key.setDurability((short)Settings.KeyDV);
		ItemMeta keyMeta = key.getItemMeta();
		keyMeta.setDisplayName(ChatColor.WHITE + Settings.KeyName + ChatColor.RESET);
		List<String> keyLore = new ArrayList<String>();
		keyLore.add(keyInfo);
		keyMeta.setLore(keyLore);
		key.setItemMeta(keyMeta);
		
		return key;
	}
	
	public boolean PlayerHasKey(Player player, ItemStack lock) {
		Inventory inventory = player.getInventory();

		for (int i = 0; i < inventory.getSize(); i++) {
			ItemStack stack = inventory.getItem(i);
			ItemMeta stackMeta = (stack != null) ? stack.getItemMeta() : null;
			//String stackName = (stackMeta != null) ? ((stackMeta.getDisplayName() != null) ? stackMeta.getDisplayName() : "") : "";
			//String requiredStackName = ChatColor.WHITE + Settings.KeyName + ChatColor.RESET;

			if (stack != null && /* stackName.equals(requiredStackName) && */stack.getTypeId() == Settings.KeyID && stack.getDurability() == Settings.KeyDV) {
				List<String> keyLore = stackMeta.getLore();
				List<String> lockLore = lock.getItemMeta().getLore();
				
				for (String line : lockLore) {
					if (line.startsWith(ChatColor.BLACK.toString() + "#" + ChatColor.MAGIC.toString())) {
						if (keyLore.contains(line)) {
							return true;
						}
					}
				}
			}
		}
		
		return false;
	}

	public int GetDifficulty(ItemStack lock) {
		ItemMeta meta = lock.getItemMeta();
		if (meta == null) {
			return -1;
		}
		
		String displayName = meta.getDisplayName();
		if (displayName == null || displayName == "") {
			return -1;
		}
		
		List<String> lore = meta.getLore();
		for (int i = 1; i < 7; i++) {
			if (i == 6 && lore.contains(ChatColor.GRAY + "Unpickable" + ChatColor.RESET)) {
				return i;
			}
			if (lore.contains(ChatColor.GRAY + "Difficulty: " + i + ChatColor.RESET)) {
				return i;
			}
		}
		
		return -1;
	}
	
    public void AddShapedRecipe(ShapedRecipe recipe) {
    	LoreLocks.server.addRecipe(recipe);
    }

	public void ExecuteFailEvents(LockGUI gui) {
		Player player = gui.Player;
		String lockName = gui.Lock.getItemMeta().getDisplayName();
		
		for (String eventKey : Settings.Events.keySet()) {
			LockEvent event = Settings.Events.get(eventKey);
			if (event.EventType == 1) {
				if (event.EventPermission == null || event.EventPermission.equals("") || player.hasPermission(event.EventPermission)) {
					
					String payload = event.ActionPayload.replace("<player>", player.getDisplayName()).replaceAll("<lock>", lockName);
					
					switch (event.ActionType) {
					case 1:
						player.sendMessage(payload);
						break;
					case 2:
						server.broadcastMessage(payload);
						break;
					case 3:
						server.dispatchCommand(player, payload);				
						break;
					case 4:
						server.dispatchCommand(Bukkit.getConsoleSender(), payload);
						break;
					}
				}
			}
		}
		
	}

	public void ExecuteSuccessEvents(LockGUI gui) {
		Player player = gui.Player;
		String lockName = gui.Lock.getItemMeta().getDisplayName();
		
		for (String eventKey : Settings.Events.keySet()) {
			LockEvent event = Settings.Events.get(eventKey);
			if (event.EventType == 2) {
				if (event.EventPermission == null || event.EventPermission.equals("") || player.hasPermission(event.EventPermission)) {
					
					String payload = event.ActionPayload.replace("<player>", player.getDisplayName()).replaceAll("<lock>", lockName);
					
					switch (event.ActionType) {
					case 1:
						player.sendMessage(payload);
						break;
					case 2:
						server.broadcastMessage(payload);
						break;
					case 3:
						server.dispatchCommand(player, payload);				
						break;
					case 4:
						server.dispatchCommand(Bukkit.getConsoleSender(), payload);
						break;
					}
				}
			}
		}
	}
	
	public double GetBreakChange(Player player) {
		return Math.max(Settings.LockPickMinBreakChance, Settings.LockPickBreakChance - (Settings.LockPickBreakChanceRate * player.getLevel()));
	}

	public boolean IsKey(ItemStack stack) {

		if (stack == null)
			return false;
		
		if (stack.getTypeId() != Settings.KeyID)
			return false;
		
		if (stack.getDurability() != Settings.KeyDV)
			return false;
		
		ItemMeta meta = stack.getItemMeta();
		if (meta == null) {
			return false;
		}
		
		List<String> lore = meta.getLore();
		
		for (String s : lore) {
			if (s.startsWith(ChatColor.BLACK + "#" + ChatColor.MAGIC)) {
				return true;
			}
		}
		
		return false;
	}
	
	public String GetKeySignature(ItemStack key) {
		ItemMeta meta = key.getItemMeta();
		List<String> lore = meta.getLore();
		
		for (String s : lore) {
			if (s.startsWith(ChatColor.BLACK + "#" + ChatColor.MAGIC)) {
				return s;
			}
		}
		return "";
	}
	
	public void SetSignature(ItemStack lock, ItemStack key) {
		ItemMeta lockMeta = lock.getItemMeta();
		List<String> lockLore = lockMeta.getLore();
		String sig = GetKeySignature(key);
		if (!lockLore.contains(sig)) {
			lockLore.add(sig);
		}
		lockMeta.setLore(lockLore);
		lock.setItemMeta(lockMeta);
	}
}
