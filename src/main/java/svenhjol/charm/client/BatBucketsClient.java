package svenhjol.charm.client;

import net.minecraft.client.Minecraft;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import svenhjol.charm.base.CharmClientModule;
import svenhjol.charm.base.CharmModule;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Predicate;

public class BatBucketsClient extends CharmClientModule {
    public static int ticks;
    public static double range;
    public static List<LivingEntity> entities = new ArrayList<>();

    public BatBucketsClient(CharmModule module) {
        super(module);
    }

    @SubscribeEvent
    public void onClientTick(TickEvent.ClientTickEvent event) {
        handleClientTick(Minecraft.getInstance());
    }

    private void handleClientTick(Minecraft minecraft) {
        if (minecraft.player != null
            && ticks > 0
            && range > 0
        ) {
            if (ticks % 10 == 0 || entities.isEmpty()) {
                setGlowing(false);
                setNearbyEntities(minecraft.player);
                setGlowing(true);
            }

            if (--ticks <= 0)
                setGlowing(false);
        }
    }

    private void setNearbyEntities(PlayerEntity player) {
        entities.clear();
        AxisAlignedBB box = player.getBoundingBox().grow(range, range / 2.0, range);
        Predicate<LivingEntity> selector = entity -> true;
        entities = player.world.getEntitiesWithinAABB(LivingEntity.class, box, selector);
    }

    private void setGlowing(boolean glowing) {
        for (Entity entity : entities) {
            entity.setGlowing(glowing);
        }
    }
}
