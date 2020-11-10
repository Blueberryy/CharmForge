package svenhjol.charm.item;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.EnderPearlItem;
import net.minecraft.item.Item;
import net.minecraft.item.ItemGroup;
import net.minecraft.item.ItemStack;
import net.minecraft.stats.Stats;
import net.minecraft.util.*;
import net.minecraft.world.World;
import svenhjol.charm.base.CharmModule;
import svenhjol.charm.base.item.ICharmItem;
import svenhjol.charm.entity.GlowballEntity;

public class GlowballItem extends EnderPearlItem implements ICharmItem {
    protected CharmModule module;

    public GlowballItem(CharmModule module) {
        super(new Item.Properties().maxStackSize(16).group(ItemGroup.MISC));
        this.module = module;
        this.register(module, "glowball");
    }

    @Override
    public void fillItemGroup(ItemGroup group, NonNullList<ItemStack> stacks) {
        if (enabled())
            super.fillItemGroup(group, stacks);
    }

    public ActionResult<ItemStack> onItemRightClick(World world, PlayerEntity user, Hand hand) {
        ItemStack itemStack = user.getHeldItem(hand);
        world.playSound(null, user.getPosX(), user.getPosY(), user.getPosZ(), SoundEvents.ENTITY_SNOWBALL_THROW, SoundCategory.NEUTRAL, 0.5F, 0.4F / (random.nextFloat() * 0.4F + 0.8F));
        user.getCooldownTracker().setCooldown(this, 10);

        if (!world.isRemote) {
            GlowballEntity entity = new GlowballEntity(world, user);
            entity.setItem(itemStack);
            entity.func_234612_a_(user, user.rotationPitch, user.rotationYaw, 0.0F, 1.5F, 1.0F);
            world.addEntity(entity);
        }

        user.addStat(Stats.ITEM_USED.get(this));
        if (!user.abilities.isCreativeMode) {
            itemStack.shrink(1);
        }

        return ActionResult.func_233538_a_(itemStack, world.isRemote());
    }

    @Override
    public boolean enabled() {
        return module.enabled;
    }
}
