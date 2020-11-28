package svenhjol.charm.base.helper;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.network.play.server.SPlayerPositionLookPacket;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.world.World;
import net.minecraft.world.server.ServerWorld;
import net.minecraft.world.server.TicketType;

import java.util.EnumSet;
import java.util.Set;

public class PlayerHelper {
    /**
     * Tries to add item stack to player, drops if not possible.
     *
     * @param player The player
     * @param stack  The stack to add/drop
     * @return True if able to add to player inv, false if dropped
     */
    public static boolean addOrDropStack(PlayerEntity player, ItemStack stack) {
        if (!player.inventory.addItemStackToInventory(stack)) {
            player.dropItem(stack, true);
            return false;
        }
        return true;
    }

    public static void teleport(World world, BlockPos pos, PlayerEntity player) {
        if (!world.isRemote) {
            ServerWorld serverWorld = (ServerWorld) world;

            double x = pos.getX() + 0.5D;
            double y = pos.getY() + 0.25D;
            double z = pos.getZ() + 0.5D;
            float yaw = player.rotationYaw;
            float pitch = player.rotationPitch;
            Set<SPlayerPositionLookPacket.Flags> flags = EnumSet.noneOf(SPlayerPositionLookPacket.Flags.class);

            ChunkPos chunkPos = new ChunkPos(new BlockPos(x, y, z));
            serverWorld.getChunkProvider().registerTicket(TicketType.POST_TELEPORT, chunkPos, 1, player.getEntityId());
            player.stopRiding();

            if (player.isSleeping())
                player.stopSleepInBed(true, true);

            if (world == player.world) {
                ((ServerPlayerEntity)player).connection.setPlayerLocation(x, y, z, yaw, pitch, flags);
            } else {
                ((ServerPlayerEntity)player).teleport(serverWorld, x, y, z, yaw, pitch);
            }
        }
    }
}
