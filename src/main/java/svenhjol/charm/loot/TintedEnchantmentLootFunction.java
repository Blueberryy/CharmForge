package svenhjol.charm.loot;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonObject;
import net.minecraft.enchantment.EnchantmentData;
import net.minecraft.item.EnchantedBookItem;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.loot.LootContext;
import net.minecraft.loot.LootFunction;
import net.minecraft.loot.LootFunctionType;
import net.minecraft.loot.conditions.ILootCondition;
import svenhjol.charm.module.Tinted;

public class TintedEnchantmentLootFunction extends LootFunction {

    public TintedEnchantmentLootFunction(ILootCondition[] conditions) {
        super(conditions);
    }

    @Override
    protected ItemStack doApply(ItemStack stack, LootContext context) {
        ItemStack book = new ItemStack(Items.ENCHANTED_BOOK);
        EnchantedBookItem.addEnchantment(book, new EnchantmentData(Tinted.TINTED, 1));
        return book;
    }

    @Override
    public LootFunctionType getFunctionType() {
        return Tinted.LOOT_FUNCTION;
    }

    public static class Serializer extends LootFunction.Serializer<TintedEnchantmentLootFunction> {
        @Override
        public TintedEnchantmentLootFunction deserialize(JsonObject json, JsonDeserializationContext context, ILootCondition[] conditions) {
            return new TintedEnchantmentLootFunction(conditions);
        }
    }
}
