package svenhjol.charm.enchantment;

import net.minecraft.enchantment.EnchantmentType;
import net.minecraft.inventory.EquipmentSlotType;
import svenhjol.charm.base.CharmModule;
import svenhjol.charm.base.enchantment.CharmEnchantment;

public class AerialAffinityEnchantment extends CharmEnchantment {
    public AerialAffinityEnchantment(CharmModule module) {
        super(module, "aerial_affinity", Rarity.RARE, EnchantmentType.ARMOR_FEET, new EquipmentSlotType[] { EquipmentSlotType.FEET });
    }

    @Override
    public int getMinEnchantability(int level) {
        return 1;
    }

    @Override
    public int getMaxEnchantability(int enchantmentLevel) {
        return this.getMinEnchantability(enchantmentLevel) + 40;
    }

    @Override
    public boolean isTreasureEnchantment() {
        return false;
    }
}
