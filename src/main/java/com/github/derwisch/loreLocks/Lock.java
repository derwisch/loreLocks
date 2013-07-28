package com.github.derwisch.loreLocks;

import org.bukkit.inventory.ShapedRecipe;

public class Lock {

	public int LockID;
	public short LockDV;
	public String LockName;
	public byte Difficulty;
	public ShapedRecipe Recipe = null;
	
	public Lock(int id, short dv, String name, byte diff) {
		LockID = id;
		LockDV = dv;
		LockName = name;
		Difficulty = diff;
	}
	
	public void SetRecipe(ShapedRecipe rec) {
		if (rec != null) {
			Recipe = rec;
			LoreLocks.instance.addShapedRecipe(rec);
		}
	}
	
	@Override
	public String toString() {
		return "{Hash:" + hashCode() + "; ID:" + LockID + "; DV:" + LockDV + "; Name:\"" + LockName + "\"; Difficulty:" + Difficulty + "}";
	}
}
