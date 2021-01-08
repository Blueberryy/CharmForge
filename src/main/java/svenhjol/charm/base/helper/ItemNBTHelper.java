package svenhjol.charm.base.helper;

import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.nbt.ListNBT;

import java.util.UUID;

@SuppressWarnings("unused")
public class ItemNBTHelper {
    public static int getInt(ItemStack stack, String tag, int defaultExpected) {
        return tagExists(stack, tag) ? getNBT(stack).getInt(tag) : defaultExpected;
    }

    public static boolean getBoolean(ItemStack stack, String tag, boolean defaultExpected) {
        return tagExists(stack, tag) ? getNBT(stack).getBoolean(tag) : defaultExpected;
    }

    public static double getDouble(ItemStack stack, String tag, double defaultExpected) {
        return tagExists(stack, tag) ? getNBT(stack).getDouble(tag) : defaultExpected;
    }

    public static long getLong(ItemStack stack, String tag, long defaultExpected) {
        return tagExists(stack, tag) ? getNBT(stack).getLong(tag) : defaultExpected;
    }

    public static String getString(ItemStack stack, String tag, String defaultExpected) {
        return tagExists(stack, tag) ? getNBT(stack).getString(tag) : defaultExpected;
    }

    public static CompoundNBT getCompound(ItemStack stack, String tag) {
        return getCompound(stack, tag, false);
    }

    public static CompoundNBT getCompound(ItemStack stack, String tag, boolean nullify) {
        return tagExists(stack, tag) ? getNBT(stack).getCompound(tag) : (nullify ? null : new CompoundNBT());
    }

    public static UUID getUuid(ItemStack stack, String tag) {
        CompoundNBT nbt = getNBT(stack);
        return nbt.hasUniqueId(tag) ? nbt.getUniqueId(tag) : null;
    }

    public static ListNBT getList(ItemStack stack, String tag) {
        return tagExists(stack, tag) ? getNBT(stack).getList(tag, 10) : new ListNBT();
    }

    public static void setInt(ItemStack stack, String tag, int i) {
        getNBT(stack).putInt(tag, i);
    }

    public static void setBoolean(ItemStack stack, String tag, boolean b) {
        getNBT(stack).putBoolean(tag, b);
    }

    public static void setCompound(ItemStack stack, String tag, CompoundNBT cmp) {
        getNBT(stack).put(tag, cmp);
    }

    public static void setDouble(ItemStack stack, String tag, double d) {
        getNBT(stack).putDouble(tag, d);
    }

    public static void setLong(ItemStack stack, String tag, long l) {
        getNBT(stack).putLong(tag, l);
    }

    public static void setString(ItemStack stack, String tag, String s) {
        getNBT(stack).putString(tag, s);
    }

    public static void setUuid(ItemStack stack, String tag, UUID uuid) {
        getNBT(stack).putUniqueId(tag, uuid);
    }

    public static void setList(ItemStack stack, String tag, ListNBT list) {
        getNBT(stack).put(tag, list);
    }


    public static boolean tagExists(ItemStack stack, String tag) {
        return !stack.isEmpty() && stack.hasTag() && getNBT(stack).contains(tag);
    }

    public static CompoundNBT getNBT(ItemStack stack) {
        if (!stack.hasTag()) {
            stack.setTag(new CompoundNBT());
        }
        return stack.getTag();
    }
}
