package svenhjol.charm.base.helper;

import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.enchantment.Enchantments;
import net.minecraft.entity.LivingEntity;

public class EnchantmentsHelper {
    public static boolean hasFeatherFalling(LivingEntity entity) {
        return EnchantmentHelper.getMaxEnchantmentLevel(Enchantments.FEATHER_FALLING, entity) > 0;
    }
}
