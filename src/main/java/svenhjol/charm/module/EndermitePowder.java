package svenhjol.charm.module;

import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityClassification;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.item.ItemEntity;
import net.minecraft.entity.monster.EndermiteEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.util.DamageSource;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.event.entity.living.LivingDropsEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import svenhjol.charm.Charm;
import svenhjol.charm.base.CharmModule;
import svenhjol.charm.base.handler.RegistryHandler;
import svenhjol.charm.base.helper.ItemHelper;
import svenhjol.charm.base.iface.Config;
import svenhjol.charm.base.iface.Module;
import svenhjol.charm.client.EndermitePowderClient;
import svenhjol.charm.entity.EndermitePowderEntity;
import svenhjol.charm.item.EndermitePowderItem;

@Module(mod = Charm.MOD_ID, client = EndermitePowderClient.class, description = "Endermites drop endermite powder that can be used to locate an End City.", hasSubscriptions = true)
public class EndermitePowder extends CharmModule {
    public static ResourceLocation ID = new ResourceLocation(Charm.MOD_ID, "endermite_powder");
    public static EntityType<EndermitePowderEntity> ENTITY;
    public static EndermitePowderItem ENDERMITE_POWDER;
    public static double lootingBoost = 0.3D;

    @Config(name = "Maximum drops", description = "Maximum endermite powder dropped when endermite is killed.")
    public static int maxDrops = 2;

    @Override
    public void register() {
        ENDERMITE_POWDER = new EndermitePowderItem(this);

        // setup and register the entity
        ENTITY = RegistryHandler.entity(ID, EntityType.Builder.<EndermitePowderEntity>create(EndermitePowderEntity::new, EntityClassification.MISC)
            .trackingRange(80)
            .setUpdateInterval(10)
            .size(2.0F, 2.0F));
    }

    @SubscribeEvent
    public void onLivingDrops(LivingDropsEvent event) {
        if (!event.isCanceled())
            tryDrop(event.getEntityLiving(), event.getSource(), event.getLootingLevel());
    }

    private void tryDrop(Entity entity, DamageSource source, int lootingLevel) {
        if (!entity.world.isRemote && entity instanceof EndermiteEntity) {
            World world = entity.getEntityWorld();
            BlockPos pos = entity.getPosition();
            int amount = ItemHelper.getAmountWithLooting(world.rand, maxDrops, lootingLevel, (float)lootingBoost);
            world.addEntity(new ItemEntity(world, pos.getX(), pos.getY(), pos.getZ(), new ItemStack(ENDERMITE_POWDER, amount)));
        }
    }
}
