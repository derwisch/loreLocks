package com.github.derwisch.loreLocks;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.bukkit.Material;
import org.bukkit.configuration.Configuration;
import org.bukkit.inventory.ShapedRecipe;

public class Settings {

	// Config values
	public static int KeyID = 369;
	public static int KeyDV = 1;
	public static String KeyName = "Key";
	public static int LockPickID = 369;
	public static int LockPickDV = 1;
	public static String LockPickName = "Lock Pick";
	public static double LockPickBreakChance = 369;
	public static boolean EnableEXP = true;
	
	public static Map<String, Lock> Locks;
	
	public static int CurrentLockID = 0;
	
    public static void LoadConfiguration(Configuration config) {
        try {
        	KeyID = config.getInt("general.KeyID");
        	KeyDV = config.getInt("general.KeyDV");
        	KeyName = config.getString("general.KeyName");
        	LockPickID = config.getInt("general.LockPickID");
        	LockPickDV = config.getInt("general.LockPickDV");
        	LockPickName = config.getString("general.LockPickName");
        	LockPickBreakChance = config.getDouble("general.LockPickBreakChance");
        	EnableEXP = config.getBoolean("general.EnableEXP");
        	
        	CurrentLockID = config.getInt("storage.currentID");
        	
        	Set<String> lockKeys = config.getConfigurationSection("locks").getKeys(false);
        	
        	Locks = new HashMap<String, Lock>();
        	
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
        	
        	
        } catch (Exception e) {
        	System.out.println("[LoreLocks] Loading Settings has thrown an exception!");
        	System.out.println("[LoreLocks] A small team of well trained monkeys under");
        	System.out.println("[LoreLocks] the lead of the main dev has been dispatched");
        	System.out.println("[LoreLocks] If you meet them give 'em this info:");
            e.printStackTrace();
        }
    }
    
    public static void SaveConfiguration(Configuration config) {
    	/*
    	config.set("general.KeyID", KeyID);
    	config.set("general.KeyDV", KeyDV);
    	config.set("general.KeyName", KeyName);
    	
    	for (String lockKey : Locks.keySet()) {

    		Lock lock = Locks.get(lockKey);
    		
    		config.set("locks." + lockKey + ".LockID", lock.LockID);
    		config.set("locks." + lockKey + ".LockDV", lock.LockDV);
    		config.set("locks." + lockKey + ".LockName", lock.LockName);
    		config.set("locks." + lockKey + ".Difficulty", lock.Difficulty);
    		config.set("locks." + lockKey + ".RClick", lock.RClick);
    		config.set("locks." + lockKey + ".MClick", lock.MClick);
    		config.set("locks." + lockKey + ".Shift", lock.Shift);
    	}
    	//*/
    }
    
}
