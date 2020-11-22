package svenhjol.charm.enchantment;

import net.minecraft.enchantment.EnchantmentType;
import net.minecraft.inventory.EquipmentSlotType;
import svenhjol.charm.base.CharmModule;
import svenhjol.charm.base.enchantment.CharmEnchantment;

public class TintedEnchantment extends CharmEnchantment {
    public TintedEnchantment(CharmModule module) {
        super(module, "tinted", Rarity.RARE, EnchantmentType.BREAKABLE, EquipmentSlotType.values());
    }
}
