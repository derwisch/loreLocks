package com.github.derwisch.loreLocks;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class LoreLocksCommandExecutor implements CommandExecutor {
    
	private LoreLocks plugin;
 
	public LoreLocksCommandExecutor(LoreLocks plugin) {
		this.plugin = plugin;
		this.plugin.getLogger().info("ItemMailCommandExecutor initialized");
	}
 
	@Override
	public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {		
		if (cmd.getName().equalsIgnoreCase("lorelocks")){
			if (!(sender instanceof Player) && args.length > 0) {
				sender.sendMessage("Current Version of LoreLocks is " + LoreLocks.instance.getDescription().getVersion());
				return true;
			} else {
				sender.sendMessage("Current Version of LoreLocks is " + LoreLocks.instance.getDescription().getVersion());
				return true;
			}
		}
		return false;
	}
}