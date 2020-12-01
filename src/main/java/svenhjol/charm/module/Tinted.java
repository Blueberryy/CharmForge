package svenhjol.charm.module;

import net.minecraft.item.DyeItem;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.loot.*;
import net.minecraft.loot.conditions.ILootCondition;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.event.AnvilUpdateEvent;
import net.minecraftforge.event.LootTableLoadEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import svenhjol.charm.Charm;
import svenhjol.charm.base.CharmModule;
import svenhjol.charm.base.handler.ModuleHandler;
import svenhjol.charm.base.handler.RegistryHandler;
import svenhjol.charm.base.helper.EnchantmentsHelper;
import svenhjol.charm.base.iface.Config;
import svenhjol.charm.base.iface.Module;
import svenhjol.charm.enchantment.TintedEnchantment;
import svenhjol.charm.handler.ColoredGlintHandler;
import svenhjol.charm.loot.TintedEnchantmentLootFunction;

@Module(mod = Charm.MOD_ID, hasSubscriptions = true, description = "When applied, this enchantment lets you change the color of the enchanted glint using dye on an anvil. Requires Core 'Enchantment glint override' to be true.")
public class Tinted extends CharmModule {
    public static final ResourceLocation LOOT_ID = new ResourceLocation(Charm.MOD_ID, "tinted_book_loot");
    public static LootFunctionType LOOT_FUNCTION;
    public static TintedEnchantment TINTED;

    @Config(name = "XP cost", description = "Number of levels required to change a tinted item using dye on an anvil.")
    public static int xpCost = 0;

    @Override
    public void register() {
        TINTED = new TintedEnchantment(this);
        LOOT_FUNCTION = RegistryHandler.lootFunctionType(LOOT_ID, new LootFunctionType(new TintedEnchantmentLootFunction.Serializer()));
    }

    @Override
    public boolean depends() {
        return Core.overrideGlint;
    }

    @Override
    public void init() {
        if (!ModuleHandler.enabled("charm:anvil_improvements") && xpCost < 1)
            xpCost = 1;
    }

    @SubscribeEvent
    public void onAnvilUpdate(AnvilUpdateEvent event) {
        if (!event.isCanceled())
            handleAnvilBehavior(event);
    }

    @SubscribeEvent
    public void onLootTableLoad(LootTableLoadEvent event) {
        if (!event.isCanceled())
            handleLootTables(event);
    }

    /**
     * Adds the enchantment and color directly to the input stack with no sanity checking.
     */
    public static void applyTint(ItemStack stack, String color) {
        EnchantmentsHelper.apply(stack, TINTED, 1);
        stack.getOrCreateTag().putString(ColoredGlintHandler.GLINT_TAG, color);
    }

    private void handleAnvilBehavior(AnvilUpdateEvent event) {
        ItemStack out;
        ItemStack left = event.getLeft();
        ItemStack right = event.getRight();

        if (left.isEmpty() || right.isEmpty())
            return;

        if (!EnchantmentsHelper.has(left, TINTED) || !(right.getItem() instanceof DyeItem))
            return;

        int cost = Math.max(0, xpCost);
        out = left.copy();
        DyeItem dye = (DyeItem)right.getItem();
        String color = dye.getDyeColor().getString();

        applyTint(out, color);
        event.setMaterialCost(1);
        event.setCost(cost);
        event.setOutput(out);
    }

    private void handleLootTables(LootTableLoadEvent event) {
        ResourceLocation id = event.getName();

        if (id.equals(LootTables.CHESTS_STRONGHOLD_LIBRARY)) {
            LootPool pool = LootPool.builder()
                .rolls(ConstantRange.of(1))
                .addEntry(ItemLootEntry.builder(Items.BOOK)
                    .weight(1)
                    .acceptFunction(() -> new TintedEnchantmentLootFunction(new ILootCondition[0])))
                .build();

            event.getTable().addPool(pool);
        }
    }
}
