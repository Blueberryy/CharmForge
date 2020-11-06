package svenhjol.charm.item;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemGroup;
import net.minecraft.item.ItemStack;
import net.minecraft.util.*;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.vector.Vector3d;
import net.minecraft.world.World;
import net.minecraft.world.gen.feature.structure.Structure;
import net.minecraft.world.server.ServerWorld;
import svenhjol.charm.base.CharmModule;
import svenhjol.charm.base.helper.DimensionHelper;
import svenhjol.charm.base.item.CharmItem;
import svenhjol.charm.entity.EndermitePowderEntity;

public class EndermitePowderItem extends CharmItem {
    public EndermitePowderItem(CharmModule module) {
        super(module, "endermite_powder", new Item.Properties().group(ItemGroup.MISC));
    }

    @Override
    public ActionResult<ItemStack> onItemRightClick(World worldIn, PlayerEntity playerIn, Hand handIn) {
        ItemStack stack = playerIn.getHeldItem(handIn);

        if (!DimensionHelper.isDimension(worldIn, new ResourceLocation("the_end")))
            return new ActionResult<>(ActionResultType.FAIL, stack);

        if (!playerIn.isCreative())
            stack.shrink(1);

        int x = playerIn.getPosition().getX();
        int y = playerIn.getPosition().getY();
        int z = playerIn.getPosition().getZ();

        playerIn.getCooldownTracker().setCooldown(this, 40);

        // client
        if (worldIn.isRemote) {
            playerIn.swingArm(handIn);
            worldIn.playSound(playerIn, x, y, z, SoundEvents.ENTITY_ENDER_EYE_LAUNCH, SoundCategory.PLAYERS, 1.0F, 1.0F);
        }

        // server
        if (!worldIn.isRemote) {
            ServerWorld serverWorld = (ServerWorld)worldIn;
            BlockPos pos = serverWorld.func_241117_a_(Structure.field_236379_o_, playerIn.getPosition(), 1500, false);
            if (pos != null) {
                EndermitePowderEntity entity = new EndermitePowderEntity(worldIn, pos.getX(), pos.getZ());
                Vector3d look = playerIn.getLookVec();

                entity.setPosition(x + look.x * 2, y + 0.5, z + look.z * 2);
                worldIn.addEntity(entity);
                return new ActionResult<>(ActionResultType.PASS, stack);
            }
        }

        return new ActionResult<>(ActionResultType.SUCCESS, stack);
    }
}
