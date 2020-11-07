package svenhjol.charm.module;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.world.World;
import svenhjol.charm.Charm;
import svenhjol.charm.base.CharmModule;
import svenhjol.charm.base.iface.Module;
import svenhjol.charm.mixin.accessor.PlayerEntityAccessor;

@Module(mod = Charm.MOD_ID, description = "Parrots stay on your shoulder when jumping and falling. Crouch to make them dismount.")
public class ParrotsStayOnShoulder extends CharmModule {
    private static boolean isEnabled = false;

    @Override
    public void init() {
        isEnabled = true;
        PlayerTickCallback.EVENT.register(this::dismountParrot);
    }

    public static boolean shouldParrotStayMounted(World world, long shoulderTime) {
        return shoulderTime + 20L < world.getGameTime() && isEnabled;
    }

    public void dismountParrot(PlayerEntity player) {
        if (!player.world.isRemote
            && player.world.getGameTime() % 10 == 0
            && player.isSneaking()
        ) {
            final ServerPlayerEntity serverPlayer = (ServerPlayerEntity)player;
            if (!serverPlayer.getLeftShoulderEntity().isEmpty()) {
                ((PlayerEntityAccessor)serverPlayer).invokeSpawnShoulderEntity(serverPlayer.getLeftShoulderEntity());
                ((PlayerEntityAccessor)serverPlayer).invokeSetLeftShoulderEntity(new CompoundNBT());
            }
            if (!serverPlayer.getRightShoulderEntity().isEmpty()) {
                ((PlayerEntityAccessor)serverPlayer).invokeSpawnShoulderEntity(serverPlayer.getRightShoulderEntity());
                ((PlayerEntityAccessor)serverPlayer).invokeSetRightShoulderEntity(new CompoundNBT());
            }
        }
    }
}
