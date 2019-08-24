package svenhjol.charm.enchanting.module;

import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.util.text.ITextComponent;
import net.minecraftforge.event.AnvilUpdateEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import svenhjol.charm.Charm;
import svenhjol.charm.base.CharmCategories;
import svenhjol.charm.enchanting.enchantment.CurseBreakEnchantment;
import svenhjol.meson.MesonModule;
import svenhjol.meson.helper.EnchantmentsHelper;
import svenhjol.meson.iface.Config;
import svenhjol.meson.iface.Module;

@Module(mod = Charm.MOD_ID, category = CharmCategories.ENCHANTING, hasSubscriptions = true)
public class CurseBreak extends MesonModule
{
    public static CurseBreakEnchantment enchantment;

    @Config(name = "XP cost", description = "Amount of levels to remove a curse from an item.")
    public static int xpCost = 1;

    @Override
    public void init()
    {
        enchantment = new CurseBreakEnchantment(this);
    }

    @SubscribeEvent
    public void onAnvilUpdate(AnvilUpdateEvent event)
    {
        ItemStack left = event.getLeft();
        ItemStack right = event.getRight();
        ItemStack out;

        if (!left.isEmpty()
            && !right.isEmpty()
            && right.getItem() == Items.ENCHANTED_BOOK
            && EnchantmentsHelper.hasEnchantment(enchantment, right)
        ) {
            out = left.copy();
            ITextComponent displayName = out.getDisplayName();
            EnchantmentsHelper.removeRandomCurse(out);

            if (!displayName.getString().isEmpty()) {
                out.setDisplayName(displayName);
            }

            event.setCost(CurseBreak.xpCost);
            event.setOutput(out);
        }
    }
}
