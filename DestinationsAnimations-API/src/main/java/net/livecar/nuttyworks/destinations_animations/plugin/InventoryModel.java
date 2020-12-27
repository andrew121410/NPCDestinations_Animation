package net.livecar.nuttyworks.destinations_animations.plugin;

import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.List;

public class InventoryModel {
    public ItemStack[] srcInventory;
    public ItemStack[] dstInventory;
    public List<Integer> srcBlockedSlots;
    public List<Integer> dstBlockedSlots;
    
    public InventoryModel() {
        srcBlockedSlots = new ArrayList<>();
        dstBlockedSlots = new ArrayList<>();
    }
}
