package svenhjol.charm.module;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.world.World;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import svenhjol.charm.Charm;
import svenhjol.charm.base.CharmModule;
import svenhjol.charm.base.handler.ModuleHandler;
import svenhjol.charm.base.iface.Module;
import svenhjol.charm.mixin.accessor.PlayerEntityAccessor;

@Module(mod = Charm.MOD_ID, description = "Parrots stay on your shoulder when jumping and falling. Crouch to make them dismount.", hasSubscriptions = true)
public class ParrotsStayOnShoulder extends CharmModule {
    @SubscribeEvent
    public void onPlayerTick(TickEvent.PlayerTickEvent event) {
        if (!event.isCanceled())
            tryDismountParrot(event.player);
    }

    public static boolean shouldParrotStayMounted(World world, long shoulderTime) {
        return shoulderTime + 20L < world.getGameTime() && ModuleHandler.enabled(ParrotsStayOnShoulder.class);
    }

    public void tryDismountParrot(PlayerEntity player) {
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
