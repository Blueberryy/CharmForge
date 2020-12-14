package svenhjol.charm.item;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemGroup;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.world.World;
import svenhjol.charm.base.CharmModule;
import svenhjol.charm.base.item.CharmItem;
import svenhjol.charm.module.Atlas;

public class AtlasItem extends CharmItem {

    public AtlasItem(CharmModule module) {
        super(module, "atlas", new Item.Properties()
                .group(ItemGroup.MISC)
                .maxStackSize(1));
    }

    @Override
    public ActionResult<ItemStack> onItemRightClick(World worldIn, PlayerEntity playerIn, Hand handIn) {
        ItemStack itemStack = playerIn.getHeldItem(handIn);
        if (worldIn.isRemote) {
            return ActionResult.resultFail(itemStack);
        }
        playerIn.openContainer(Atlas.getInventory(worldIn, itemStack));
        return ActionResult.resultConsume(itemStack);
    }
}
