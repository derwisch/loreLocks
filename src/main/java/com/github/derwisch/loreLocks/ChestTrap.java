package com.github.derwisch.loreLocks;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.entity.LivingEntity;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.ShapedRecipe;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.metadata.FixedMetadataValue;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

public class ChestTrap {
	public int id = 280;
	public short dv = 1;
	public String name = "Chest Trap";
	public Type type = Type.Damage;
	public byte power = 1;
	public byte uses = 1;
	public ShapedRecipe recipe;
	
	public ChestTrap(int id, short dv, String name, Type type, byte power, byte uses) {
		this.id = id;
		this.dv = dv;
		this.name = name;
		this.type = type;
		this.power = power;
		this.uses = uses;
	}
	
	public void SetRecipe(ShapedRecipe rec) {
		if (rec != null) {
			recipe = rec;
			LoreLocks.instance.AddShapedRecipe(rec);
		}
	}

	//====================================================================//
	
	public static enum Type {
		Damage,
		Explosive,
		Poison,
		Stone
	}
	
	public static String TrapIdentifier = ChatColor.DARK_GRAY + "" + ChatColor.ITALIC + "Trap" + ChatColor.RESET;  
	
	public static ItemStack createTrap(ChestTrap trap) {
		ItemStack stack = new ItemStack(trap.id, 1, trap.dv);
		ItemMeta meta = stack.getItemMeta();
		meta.setDisplayName(ChatColor.WHITE + trap.name + ChatColor.RESET);
		List<String> lore = new ArrayList<String>();
		lore.add(TrapIdentifier);
		lore.add(ChatColor.GRAY + "Type: " + trap.type.toString());
		lore.add(ChatColor.GRAY + "Power: " + trap.power);
		lore.add(ChatColor.GRAY + "Uses: " + trap.uses + "/" + trap.uses);
		meta.setLore(lore);
		stack.setItemMeta(meta);
		
		return stack;
	}
	
	public static boolean isTrap(ItemStack stack) {
		try {
			return TrapIdentifier.equals(stack.getItemMeta().getLore().get(0));
		}
		catch (Exception ex) {
			return false;
		}
	}
	
	public static ItemStack setTrapOff(LivingEntity ent, ItemStack trap) {
		if (trap == null || ent == null) 
			return trap;
		ItemMeta trapMeta = trap.getItemMeta();
		List<String> trapLore = trapMeta.getLore();
		String type = "unknown";
		int power = 0;
		int uses = 0;
		int maxUses = 0;
		int index = 0;
		int usesIndex = 0;
		for (String line : trapLore) {
			if (line.startsWith(ChatColor.GRAY + "Type: ")) {
				type = line.replace(ChatColor.GRAY + "Type: ", "");
			}
			if (line.startsWith(ChatColor.GRAY + "Power: ")) {
				power = Integer.parseInt(line.replace(ChatColor.GRAY + "Power: ", ""));
			}
			if (line.startsWith(ChatColor.GRAY + "Uses: ")) {
				String usesString = line.replace(ChatColor.GRAY + "Uses: ", "");
				String[] usesParts = usesString.split("/");
				uses = Integer.parseInt(usesParts[0]) - 1;
				maxUses = Integer.parseInt(usesParts[1]);
				usesIndex = index;
			}
			index++;
		}
		
		
		Location loc = ent.getLocation();
		
		if (type.equals(Type.Damage.toString())) {
			ent.damage(power * 2);
			ent.setMetadata("LockPickDeath", new FixedMetadataValue(LoreLocks.instance, true));
		}
		if (type.equals(Type.Explosive.toString())) {
			loc.getWorld().createExplosion(loc.getX(), loc.getY(), loc.getZ(), power, false, false); 
		}
		if (type.equals(Type.Poison.toString())) {
			ent.addPotionEffect(new PotionEffect(PotionEffectType.POISON, 400, power - 1));
		}
		/*
		if (type.equals(Type.Stone.toString())) {
			FallingBlock fBlock = loc.getWorld().spawnFallingBlock(loc.add(0, 3, 0), Material.STONE, (byte)0);
			fBlock.setDropItem(false);
			//TODO: Check how damage is calculated!
		}
		*/
		
		if (uses <= 0) {
			trap.setAmount(trap.getAmount() - 1);
			uses = maxUses;
		}

		trapLore.set(usesIndex, ChatColor.GRAY + "Uses: " + uses + "/" + maxUses);

		trapMeta.setLore(trapLore);
		trap.setItemMeta(trapMeta);
		return trap;
	}
}
