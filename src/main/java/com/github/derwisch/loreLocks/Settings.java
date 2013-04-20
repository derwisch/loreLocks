package com.github.derwisch.loreLocks;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.configuration.Configuration;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.ShapedRecipe;
import org.bukkit.inventory.meta.ItemMeta;

public class Settings {

	// Config values
	public static int KeyID = 369;
	public static int KeyDV = 1;
	public static String KeyName = "Key";
	public static int LockPickID = 369;
	public static int LockPickDV = 1;
	public static String LockPickName = "Lock Pick";
	public static double LockPickBreakChance = 0.3d;
	public static double LockPickBreakChanceRate = 0.01d;
	public static double LockPickMinBreakChance = 0.05d;
	public static boolean EnableExplosionProtection = false;
	public static boolean EnableHopperProtection = false;
	public static boolean EnableZombieProtection = false;
	public static String TrapText = "Trap";

	public static Map<String, Lock> Locks;
	public static Map<String, ChestTrap> Traps;
	public static Map<String, LockEvent> Events;
	
	public static class Messages
	{
		public static String PermissionForLevelMissing(int level) {
			switch (level)
			{
			case 1:
				return Level_1_Permission;
			case 2:
				return Level_2_Permission;
			case 3:
				return Level_3_Permission;
			case 4:
				return Level_4_Permission;
			case 5:
				return Level_5_Permission;
			default:
				return "This lock is to difficult for you!";
			}
		}
		
		public static String Level_1_Permission = "This lock is to difficult for you!";
		public static String Level_2_Permission = "This lock is to difficult for you!";
		public static String Level_3_Permission = "This lock is to difficult for you!";
		public static String Level_4_Permission = "This lock is to difficult for you!";
		public static String Level_5_Permission = "This lock is to difficult for you!";
		public static String Unpickable_Lock = "This lock can't be picked!";
		public static String Bypass = "Lock bypassed.";
		public static String Key_Used = "You opened the lock with your key.";
		public static String Pick_Needed = "You need a <lockpick> to open this chest!";
		public static String Pick_Break = "Your <lockpick> broke!";
		public static String Door_Locked = "You locked a door!";
	}
	
    public static void LoadConfiguration(Configuration config) {
        try {
        	KeyID = config.getInt("general.KeyID");
        	KeyDV = config.getInt("general.KeyDV");
        	KeyName = config.getString("general.KeyName");
        	LockPickID = config.getInt("general.LockPickID");
        	LockPickDV = config.getInt("general.LockPickDV");
        	LockPickName = config.getString("general.LockPickName");
        	LockPickBreakChance = config.getDouble("general.LockPickBreakChance");
        	LockPickBreakChanceRate = config.getDouble("general.LockPickBreakChanceRate");
        	LockPickMinBreakChance = config.getDouble("general.LockPickMinBreakChance");
        	EnableExplosionProtection = config.getBoolean("general.EnableExplosionProtection");
        	EnableHopperProtection = config.getBoolean("general.EnableHopperProtection");
        	EnableZombieProtection = config.getBoolean("general.EnableZombieProtection");
        	TrapText = config.getString("general.TrapText");

        	Set<String> lockKeys = config.getConfigurationSection("locks").getKeys(false);
        	Set<String> trapKeys = config.getConfigurationSection("traps").getKeys(false);
        	Set<String> eventKeys = config.getConfigurationSection("events").getKeys(false);

        	Locks = new HashMap<String, Lock>();
        	Traps = new HashMap<String, ChestTrap>();
        	Events = new HashMap<String, LockEvent>();

    		//====================================================================//

    		List<String> LockPickRecipeShape = config.getStringList("general.LockPickRecipeShape");
    		List<Character> LockPickRecipeChars = new ArrayList<Character>();
    		
    		for (String shapeLine : LockPickRecipeShape) {
    			char[] chars = shapeLine.toCharArray();
    			for (char c : chars) {
    				if (c != ' ' && !LockPickRecipeChars.contains(c)) {
    					LockPickRecipeChars.add(c);
    				}
    			}
    		}

    		ItemStack lockPick = new ItemStack(Material.getMaterial(Settings.LockPickID));
    		ItemMeta lockPickMeta = lockPick.getItemMeta();
    		lockPick.setDurability((short)Settings.LockPickDV);
    		lockPickMeta.setDisplayName(ChatColor.WHITE + Settings.LockPickName + ChatColor.RESET);
    		lockPick.setItemMeta(lockPickMeta);
    		lockPick.setAmount(3);
    		
    		ShapedRecipe LockPickRecipe = new ShapedRecipe(lockPick);
    		
    		String[] lockPickShape = new String[LockPickRecipeShape.size()];

			switch (LockPickRecipeShape.size()) {
				case 1:
					lockPickShape[0] = LockPickRecipeShape.get(0);
					break;
				case 2:
					lockPickShape[0] = LockPickRecipeShape.get(0);
	    			lockPickShape[1] = LockPickRecipeShape.get(1);
					break;
				case 3:
					lockPickShape[0] = LockPickRecipeShape.get(0);
	    			lockPickShape[1] = LockPickRecipeShape.get(1);
	    			lockPickShape[2] = LockPickRecipeShape.get(2);
					break;
			}
    		
			LockPickRecipe = LockPickRecipe.shape(lockPickShape);

    		for (char c : LockPickRecipeChars) {
        		int matID = config.getInt("general.LockPickRecipeIDs." + c);
        		LockPickRecipe = LockPickRecipe.setIngredient(c, Material.getMaterial(matID));
    		}

    		LoreLocks.instance.AddShapedRecipe(LockPickRecipe);
    		LoreLocks.instance.LockPickRecipe = LockPickRecipe;
    		
    		//====================================================================//
        	
        	for (String lockKey : lockKeys) {
        		int LockID = config.getInt("locks." + lockKey + ".LockID");
        		short LockDV = (short)config.getInt("locks." + lockKey + ".LockDV");
        		String LockName = config.getString("locks." + lockKey + ".LockName");
        		byte Difficulty = (byte)Math.max(Math.min(config.getInt("locks." + lockKey + ".Difficulty"), 6), 1);
        		
        		List<String> RecipeShape = config.getStringList("locks." + lockKey + ".RecipeShape");
        		
        		List<Character> RecipeChars = new ArrayList<Character>();
        		
        		for (String shapeLine : RecipeShape) {
        			char[] chars = shapeLine.toCharArray();
        			for (char c : chars) {
        				if (c != ' ' && !RecipeChars.contains(c)) {
        					RecipeChars.add(c);
        				}
        			}
        		}

        		Lock lock = new Lock(LockID, LockDV, LockName, Difficulty);
        		ShapedRecipe recipe = new ShapedRecipe(LoreLocks.instance.FactoryLock(lock));
        		
        		String[] shape = new String[RecipeShape.size()];

    			switch (RecipeShape.size()) {
    				case 1:
    	    			shape[0] = RecipeShape.get(0);
    					break;
    				case 2:
    	    			shape[0] = RecipeShape.get(0);
    	    			shape[1] = RecipeShape.get(1);
    					break;
    				case 3:
    	    			shape[0] = RecipeShape.get(0);
    	    			shape[1] = RecipeShape.get(1);
    	    			shape[2] = RecipeShape.get(2);
    					break;
    			}
        		
    			recipe = recipe.shape(shape);

    			
        		for (char c : RecipeChars) {
            		int matID = config.getInt("locks." + lockKey + ".RecipeIDs." + c);
            		recipe = recipe.setIngredient(c, Material.getMaterial(matID));
        		}
        		
        		lock.SetRecipe(recipe);
        		
        		Locks.put(lockKey, lock);
        	}
        	LoreLocks.logger.info("Loaded " + lockKeys.size() + " locks.");

    		//====================================================================//

        	for (String trapKey : trapKeys) {
        		int TrapID = config.getInt("traps." + trapKey + ".TrapID");
        		short TrapDV = (short)config.getInt("traps." + trapKey + ".TrapDV");
        		String TrapName = config.getString("traps." + trapKey + ".TrapName");
        		String Type = config.getString("traps." + trapKey + ".Type");
        		byte Power = (byte)config.getInt("traps." + trapKey + ".Power");
        		byte Uses = (byte)config.getInt("traps." + trapKey + ".Uses");
        		
        		List<String> RecipeShape = config.getStringList("traps." + trapKey + ".RecipeShape");
        		
        		List<Character> RecipeChars = new ArrayList<Character>();
        		
        		for (String shapeLine : RecipeShape) {
        			char[] chars = shapeLine.toCharArray();
        			for (char c : chars) {
        				if (c != ' ' && !RecipeChars.contains(c)) {
        					RecipeChars.add(c);
        				}
        			}
        		}
        		
        		ChestTrap.Type TrapType = ChestTrap.Type.Damage;

        		if (Type.toLowerCase().equals("damage")) {
        			TrapType = ChestTrap.Type.Damage;
        		} else if (Type.toLowerCase().equals("explosive")) {
        			TrapType = ChestTrap.Type.Explosive;
        		} else if (Type.toLowerCase().equals("poison")) {
        			TrapType = ChestTrap.Type.Poison;
        		} else if (Type.toLowerCase().equals("stone")) {
        			TrapType = ChestTrap.Type.Stone;
        		} 

        		ChestTrap trap = new ChestTrap(TrapID, TrapDV, TrapName, TrapType, Power, Uses);
        		
        		ShapedRecipe recipe = new ShapedRecipe(ChestTrap.createTrap(trap));
        		
        		String[] shape = new String[RecipeShape.size()];

    			switch (RecipeShape.size()) {
    				case 1:
    	    			shape[0] = RecipeShape.get(0);
    					break;
    				case 2:
    	    			shape[0] = RecipeShape.get(0);
    	    			shape[1] = RecipeShape.get(1);
    					break;
    				case 3:
    	    			shape[0] = RecipeShape.get(0);
    	    			shape[1] = RecipeShape.get(1);
    	    			shape[2] = RecipeShape.get(2);
    					break;
    			}
        		
    			recipe = recipe.shape(shape);

        		for (char c : RecipeChars) {
            		int matID = config.getInt("traps." + trapKey + ".RecipeIDs." + c);
            		recipe = recipe.setIngredient(c, Material.getMaterial(matID));
        		}
        		
        		trap.SetRecipe(recipe);
        		
        		Traps.put(trapKey, trap);
        	}
        	LoreLocks.logger.info("Loaded " + trapKeys.size() + " traps.");

    		//====================================================================//
        	
        	for (String eventKey : eventKeys) {
        		String eventType = config.getString("events." + eventKey + ".event");
        		String eventPerm = config.getString("events." + eventKey + ".permission");
        		String actionType = config.getString("events." + eventKey + ".action");
        		String actionLoad = config.getString("events." + eventKey + ".payload");
        		
        		LockEvent event = new LockEvent();
        		
        		if (eventType.toLowerCase().equals("fail")) {
            		event.EventType = 1;
        		} else if (eventType.toLowerCase().equals("success")) {
            		event.EventType = 2;
        		} else {
        			//Wrong eventtype, ignore and continue with next
        			continue;
        		}
        		
        		if (actionType.toLowerCase().equals("player-message")) {
            		event.ActionType = 1;
        		} else if (actionType.toLowerCase().equals("server-message")) {
            		event.ActionType = 2;
        		} else if (actionType.toLowerCase().equals("player-command")) {
            		event.ActionType = 3;
        		} else if (actionType.toLowerCase().equals("server-command")) {
            		event.ActionType = 4;
        		} else {
        			//Wrong eventtype, ignore and continue with next
        			continue;
        		}
        		event.EventPermission = eventPerm;
        		event.ActionPayload = actionLoad;
        		
        		Events.put(eventKey, event);
        	}
        	LoreLocks.logger.info("Loaded " + eventKeys.size() + " events.");

    		//====================================================================//
        	
    		Messages.Level_1_Permission = config.getString("messages.level-1-perm");
    		Messages.Level_2_Permission = config.getString("messages.level-2-perm");
    		Messages.Level_3_Permission = config.getString("messages.level-3-perm");
    		Messages.Level_4_Permission = config.getString("messages.level-4-perm");
    		Messages.Level_5_Permission = config.getString("messages.level-5-perm");
    		Messages.Unpickable_Lock = config.getString("messages.unpickable-lock");
    		Messages.Bypass = config.getString("messages.bypass");
    		Messages.Key_Used = config.getString("messages.key-used");
    		Messages.Pick_Needed = config.getString("messages.pick-needed");
    		Messages.Pick_Break = config.getString("messages.pick-break");
    		Messages.Door_Locked = config.getString("messages.door-locked");
        } catch (Exception e) {
        	LoreLocks.logger.info("Loading Settings has thrown an exception!");
        	for (StackTraceElement el : e.getStackTrace()) {
        		LoreLocks.logger.info(el.toString());
        	}
            e.printStackTrace();
            System.out.println("Please make sure you have updated your config after the last update.");
        }
    }
    
    @Deprecated
    public static void SaveConfiguration(Configuration config) {
    	//Add saving logic here when needed, not needed ATM.
    }
    
}
