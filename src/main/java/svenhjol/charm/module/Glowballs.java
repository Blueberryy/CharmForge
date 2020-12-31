package svenhjol.charm.module;

import net.minecraft.block.DispenserBlock;
import net.minecraft.dispenser.IPosition;
import net.minecraft.dispenser.ProjectileDispenseBehavior;
import net.minecraft.entity.EntityClassification;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.projectile.ProjectileEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.Util;
import net.minecraft.world.World;
import svenhjol.charm.Charm;
import svenhjol.charm.base.CharmModule;
import svenhjol.charm.base.handler.RegistryHandler;
import svenhjol.charm.base.iface.Module;
import svenhjol.charm.block.GlowballBlobBlock;
import svenhjol.charm.client.GlowballsClient;
import svenhjol.charm.entity.GlowballEntity;
import svenhjol.charm.item.GlowballItem;

@Module(mod = Charm.MOD_ID, client = GlowballsClient.class, description = "Glowballs can be thrown to produce a light source where they impact.")
public class Glowballs extends CharmModule {
    public static ResourceLocation ID = new ResourceLocation(Charm.MOD_ID, "glowball");
    public static GlowballItem GLOWBALL_ITEM;
    public static GlowballBlobBlock GLOWBALL_BLOCK;
    public static EntityType<GlowballEntity> GLOWBALL;

    @Override
    public void register() {
        GLOWBALL_ITEM = new GlowballItem(this);
        GLOWBALL_BLOCK = new GlowballBlobBlock(this);

        GLOWBALL = RegistryHandler.entity(ID, EntityType.Builder.<GlowballEntity>create(GlowballEntity::new, EntityClassification.MISC)
            .trackingRange(4)
            .setUpdateInterval(10)
            .size(0.25F, 0.25F));

        DispenserBlock.registerDispenseBehavior(GLOWBALL_ITEM, new ProjectileDispenseBehavior() {
            protected ProjectileEntity getProjectileEntity(World world, IPosition position, ItemStack stack) {
                return Util.make(new GlowballEntity(world, position.getX(), position.getY(), position.getZ()), (entity) -> {
                    entity.setItem(stack);
                });
            }
        });
    }
}
