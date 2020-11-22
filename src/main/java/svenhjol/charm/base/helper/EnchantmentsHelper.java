package svenhjol.charm.base.helper;

import net.minecraft.enchantment.Enchantment;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.enchantment.Enchantments;
import net.minecraft.entity.LivingEntity;
import net.minecraft.item.ItemStack;

import java.util.Map;

public class EnchantmentsHelper {
    public static void apply(ItemStack stack, Enchantment enchantment, int level) {
        Map<Enchantment, Integer> enchantments = EnchantmentHelper.getEnchantments(stack);
        enchantments.put(enchantment, level);
        EnchantmentHelper.setEnchantments(enchantments, stack);
    }

    public static boolean hasFeatherFalling(LivingEntity entity) {
        return EnchantmentHelper.getMaxEnchantmentLevel(Enchantments.FEATHER_FALLING, entity) > 0;
    }

    public static boolean has(ItemStack stack, Enchantment enchantment) {
        Map<Enchantment, Integer> enchantments = EnchantmentHelper.getEnchantments(stack);
        return enchantments.containsKey(enchantment);
    }
}
