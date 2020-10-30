package svenhjol.charm.mixin;

import net.minecraft.tileentity.BeaconTileEntity;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.tileentity.TileEntityType;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import svenhjol.charm.module.BeaconsHealMobs;

@Mixin(BeaconTileEntity.class)
public abstract class BeaconTileEntityMixin extends TileEntity {
    @Shadow private int level;
    @Shadow private StatusEffect primary;
    @Shadow private StatusEffect secondary;

    public BeaconTileEntityMixin(TileEntityType<?> type) {
        super(type);
    }

    @Inject(
        method = "applyPlayerEffects",
        at = @At("HEAD")
    )
    private void hookAddEffects(CallbackInfo ci) {
        if (this.world != null)
            BeaconsHealMobs.healInBeaconRange(this.world, this.level, this.pos, this.primary, this.secondary);
    }
}
