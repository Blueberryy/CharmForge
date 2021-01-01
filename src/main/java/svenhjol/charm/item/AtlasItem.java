package svenhjol.charm.item;

import net.minecraft.block.BlockState;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.item.*;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.tags.BlockTags;
import net.minecraft.util.ActionResult;
import net.minecraft.util.ActionResultType;
import net.minecraft.util.Hand;
import net.minecraft.world.World;
import net.minecraft.world.storage.MapData;
import svenhjol.charm.base.CharmModule;
import svenhjol.charm.base.item.CharmItem;
import svenhjol.charm.client.AtlasClient;
import svenhjol.charm.container.AtlasInventory;
import svenhjol.charm.module.Atlas;

import javax.annotation.Nullable;

public class AtlasItem extends CharmItem {

    public AtlasItem(CharmModule module) {
        super(module, "atlas", new Item.Properties()
                .group(ItemGroup.MISC)
                .maxStackSize(1));
    }

    @Override
    public ActionResult<ItemStack> onItemRightClick(World world, PlayerEntity player, Hand hand) {
        ItemStack itemStack = player.getHeldItem(hand);
        if (world.isRemote) {
            return ActionResult.resultConsume(itemStack);
        }
        if (hand == Hand.OFF_HAND && !Atlas.offHandOpen) {
            return ActionResult.resultPass(itemStack);
        }
        AtlasInventory inventory = Atlas.getInventory(world, itemStack);
        inventory.getMapInfos().values().forEach(it -> Atlas.sendMapToClient((ServerPlayerEntity) player, it.map));
        player.openContainer(inventory);
        return ActionResult.resultConsume(itemStack);
    }

    @Override
    public ActionResultType onItemUse(ItemUseContext context) {
        World world = context.getWorld();
        BlockState blockstate = world.getBlockState(context.getPos());
        if (blockstate.isIn(BlockTags.BANNERS)) {
            if (!world.isRemote) {
                PlayerEntity player = context.getPlayer();
                if (player instanceof ServerPlayerEntity) {
                    AtlasInventory inventory = Atlas.getInventory(world, context.getItem());
                    AtlasInventory.MapInfo info = inventory.updateActiveMap((ServerPlayerEntity) player);
                    if (info != null) {
                        MapData mapdata = FilledMapItem.getMapData(info.map, context.getWorld());
                        if (mapdata != null) {
                            mapdata.tryAddBanner(context.getWorld(), context.getPos());
                        }
                    }
                }
            }
            return ActionResultType.func_233537_a_(world.isRemote);
        } else {
            return super.onItemUse(context);
        }
    }

    @Override
    public void readShareTag(ItemStack stack, @Nullable CompoundNBT nbt) {
        super.readShareTag(stack, nbt);
        AtlasClient.updateInventory(stack);
    }
}
