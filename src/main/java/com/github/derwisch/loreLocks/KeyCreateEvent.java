package com.github.derwisch.loreLocks;

import org.bukkit.entity.Player;
import org.bukkit.event.HandlerList;
import org.bukkit.event.player.PlayerEvent;
import org.bukkit.inventory.ItemStack;

public class KeyCreateEvent extends PlayerEvent {
    private ItemStack key;

    public KeyCreateEvent(Player player, ItemStack key) {
        super(player);

        this.setKey(key);
    }

    /**
     * @return The key being created
     */
    public ItemStack getKey() {
        return key;
    }

    /**
     * @return Set the key being created
     */
    public void setKey(ItemStack key) {
        this.key = key;
    }

    private static final HandlerList handlers = new HandlerList();

    @Override
    public HandlerList getHandlers() {
        return handlers;
    }

    public static HandlerList getHandlerList() {
        return handlers;
    }
}
