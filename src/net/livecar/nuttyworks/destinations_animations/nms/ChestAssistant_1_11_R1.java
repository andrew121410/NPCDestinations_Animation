package net.livecar.nuttyworks.destinations_animations.nms;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Sound;
import org.bukkit.entity.Player;

import org.bukkit.craftbukkit.v1_11_R1.entity.CraftPlayer;
import net.minecraft.server.v1_11_R1.Block;
import net.minecraft.server.v1_11_R1.BlockPosition;
import net.minecraft.server.v1_11_R1.PacketPlayOutBlockAction;

public class ChestAssistant_1_11_R1 implements ChestAssistant {
    @SuppressWarnings("deprecation")
    public void changeChestState(Location blockLocation, Boolean open) {
        byte dataByte = (open) ? (byte) 1 : 0; // The byte of data used for the
                                               // note and animation packet (1
                                               // if true, 0 if false)
        for (Player player : Bukkit.getOnlinePlayers()) {
            if (open)
                player.getWorld().playSound(blockLocation, Sound.valueOf("BLOCK_CHEST_OPEN"), 1F, 1F);
            if (!open)
                player.getWorld().playSound(blockLocation, Sound.valueOf("BLOCK_CHEST_CLOSE"), 1F, 1F);

            BlockPosition position = new BlockPosition(blockLocation.getBlockX(), blockLocation.getBlockY(), blockLocation.getBlockZ());
            PacketPlayOutBlockAction blockActionPacket = new PacketPlayOutBlockAction(position, Block.getById(blockLocation.getBlock().getTypeId()), (byte) 1, dataByte);
            ((CraftPlayer) player).getHandle().playerConnection.sendPacket(blockActionPacket);
        }
    }
}
