package svenhjol.charm.block;

import net.minecraft.block.AbstractBlock;
import net.minecraft.block.BlockState;
import net.minecraft.block.SoundType;
import net.minecraft.block.material.Material;
import net.minecraft.item.ItemGroup;
import net.minecraft.item.PickaxeItem;
import net.minecraft.particles.ParticleTypes;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import svenhjol.charm.base.CharmModule;
import svenhjol.charm.base.block.CharmBlock;

import java.util.Random;

public class EnderPearlBlock extends CharmBlock {
    public EnderPearlBlock(CharmModule module) {
        super(module, "ender_pearl_block", AbstractBlock.Properties
            .create(Material.GLASS)
            .sound(SoundType.GLASS)
            .hardnessAndResistance(2.0F)
        );

        this.setEffectiveTool(PickaxeItem.class);
    }

    @Override
    public ItemGroup getItemGroup() {
        return ItemGroup.BUILDING_BLOCKS;
    }

    /**
     * Copypasta from MyceliumBlock
     */
    @Override
    @OnlyIn(Dist.CLIENT)
    public void animateTick(BlockState state, World world, BlockPos pos, Random random) {
        super.animateTick(state, world, pos, random);
        if (random.nextInt(10) == 0) {
            world.addParticle(ParticleTypes.PORTAL, (double)pos.getX() + random.nextDouble(), (double)pos.getY() + 1.1D, (double)pos.getZ() + random.nextDouble(), 0.0D, 0.0D, 0.0D);
        }

    }
}
