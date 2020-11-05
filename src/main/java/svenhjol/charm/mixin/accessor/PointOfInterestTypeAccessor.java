package svenhjol.charm.mixin.accessor;

import net.minecraft.village.PointOfInterestType;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(PointOfInterestType.class)
public interface PointOfInterestTypeAccessor {
    @Accessor
    void setMaxFreeTickets(int ticketCount);
}
