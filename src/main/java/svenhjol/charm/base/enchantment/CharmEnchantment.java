package svenhjol.charm.base.enchantment;

import net.minecraft.enchantment.Enchantment;
import net.minecraft.enchantment.EnchantmentType;
import net.minecraft.inventory.EquipmentSlotType;
import net.minecraft.item.ItemStack;
import svenhjol.charm.base.CharmModule;

public abstract class CharmEnchantment extends Enchantment implements ICharmEnchantment {
    protected CharmModule module;

    public CharmEnchantment(CharmModule module, String name, Rarity rarity, EnchantmentType target, EquipmentSlotType[] slotTypes) {
        super(rarity, target, slotTypes);
        this.register(module, name);
        this.module = module;
    }

    public boolean enabled() {
        return module.enabled;
    }

    @Override
    public boolean canApply(ItemStack stack) {
        return module.enabled && super.canApply(stack);
    }

    @Override
    public boolean canVillagerTrade() {
        return module.enabled && super.canVillagerTrade();
    }

    @Override
    public boolean canGenerateInLoot() {
        return module.enabled && super.canGenerateInLoot();
    }
}
