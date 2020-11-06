package svenhjol.charm.module;

import net.minecraft.item.ItemStack;
import net.minecraftforge.event.AnvilUpdateEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import svenhjol.charm.Charm;
import svenhjol.charm.base.CharmModule;
import svenhjol.charm.base.handler.ModuleHandler;
import svenhjol.charm.base.iface.Config;
import svenhjol.charm.base.iface.Module;

@Module(mod = Charm.MOD_ID, description = "Combine a tool or armor with a netherite nugget on an anvil to reduce its repair cost.", hasSubscriptions = true)
public class DecreaseRepairCost extends CharmModule {
    @Config(name = "XP cost", description = "Number of levels required to reduce repair cost on the anvil.")
    public static int xpCost = 0;

    @Config(name = "Repair cost decrease", description = "The tool repair cost will be decreased by this amount.")
    public static int decreaseAmount = 5;

    @Override
    public void init() {
        // if anvil improvements are not enabled, then set the xpCost to 1.
        if (!ModuleHandler.enabled("charm:anvil_improvements") && xpCost < 1)
            xpCost = 1;
    }

    @SubscribeEvent
    public void onAnvilUpdate(AnvilUpdateEvent event) {
        if (!event.isCanceled())
            tryReduceRepairCost(event);
    }

    private void tryReduceRepairCost(AnvilUpdateEvent event) {
        ItemStack left = event.getLeft();
        ItemStack right = event.getRight();

        ItemStack out; // this will be the tool/armor with reduced repair cost

        if (left.isEmpty() || right.isEmpty())
            return; // if both the input and middle items are empty, do nothing

        if (right.getItem() != NetheriteNuggets.NETHERITE_NUGGET)
            return; // if the middle item is not a netherite nugget, do nothing

        if (left.getRepairCost() == 0)
            return; // if the input item does not need repairing, do nothing

        // get the repair cost from the input item
        int cost = left.getRepairCost();

        // copy the input item to the output item and reduce the repair cost by the amount in the config
        out = left.copy();
        out.setRepairCost(Math.max(0, cost - decreaseAmount));

        // apply the stuff to the anvil
        event.setCost(xpCost);
        event.setMaterialCost(1);
        event.setOutput(out);
    }
}
