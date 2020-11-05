package svenhjol.charm.mixin.accessor;

import net.minecraft.inventory.container.RepairContainer;
import net.minecraft.util.IntReferenceHolder;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(RepairContainer.class)
public interface RepairContainerAccessor {
    @Accessor
    void setMaximumCost(IntReferenceHolder levelCost);

    @Accessor
    IntReferenceHolder getMaximumCost();

    @Accessor
    void setMaterialCost(int materialCost);
}
