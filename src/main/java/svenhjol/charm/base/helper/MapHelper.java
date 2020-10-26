package svenhjol.charm.base.helper;

import net.minecraft.item.FilledMapItem;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraft.world.server.ServerWorld;
import net.minecraft.world.storage.MapData;
import net.minecraft.world.storage.MapDecoration;

public class MapHelper {
    public static ItemStack getMap(ServerWorld world, BlockPos pos, TranslationTextComponent mapName, MapDecoration.Type targetType, int color) {
        // generate the map
        ItemStack stack = FilledMapItem.setupNewMap(world, pos.getX(), pos.getZ(), (byte) 2, true, true);
        FilledMapItem.func_226642_a_(world, stack);
        MapData.addTargetDecoration(stack, pos, "+", targetType);
        stack.setDisplayName(mapName);

        // set map color based on structure
        CompoundNBT tag = ItemNBTHelper.getCompound(stack, "display");
        tag.putInt("MapColor", color);
        ItemNBTHelper.setCompound(stack, "display", tag);

        return stack;
    }
}
