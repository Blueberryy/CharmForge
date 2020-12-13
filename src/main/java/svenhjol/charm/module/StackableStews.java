package svenhjol.charm.module;

import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.*;
import net.minecraftforge.event.entity.living.LivingEntityUseItemEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import svenhjol.charm.Charm;
import svenhjol.charm.base.CharmModule;
import svenhjol.charm.base.iface.Config;
import svenhjol.charm.base.iface.Module;
import svenhjol.charm.mixin.accessor.ItemAccessor;

@Module(mod = Charm.MOD_ID, hasSubscriptions = true, description = "Allows stews to stack.")
public class StackableStews extends CharmModule {
    @Config(name = "Stack size", description = "Maximum stew stack size.")
    public static int stackSize = 16;

    @Config(name = "Enable suspicious stew", description = "Also apply to suspicious stew.")
    public static boolean suspiciousStew = false;

    @Override
    public void init() {
        ((ItemAccessor) Items.MUSHROOM_STEW).setMaxStackSize(stackSize);
        ((ItemAccessor) Items.RABBIT_STEW).setMaxStackSize(stackSize);
        ((ItemAccessor) Items.BEETROOT_SOUP).setMaxStackSize(stackSize);
        if (suspiciousStew) {
            ((ItemAccessor) Items.SUSPICIOUS_STEW).setMaxStackSize(stackSize);
        }
    }

    @SubscribeEvent
    public void onItemUseFinish(LivingEntityUseItemEvent.Finish event) {
        ItemStack itemStack = event.getItem();
        Item item = itemStack.getItem();
        if (item instanceof SoupItem || item instanceof SuspiciousStewItem) {
            if (itemStack.getMaxStackSize() > 1) {
                LivingEntity entity = event.getEntityLiving();
                if (entity instanceof PlayerEntity) {
                    PlayerEntity player = (PlayerEntity) entity;
                    if (!player.abilities.isCreativeMode) {
                        player.addItemStackToInventory(event.getResultStack());
                        itemStack.shrink(1);
                        event.setResultStack(itemStack);
                    }
                }
            }
        }
    }
}
