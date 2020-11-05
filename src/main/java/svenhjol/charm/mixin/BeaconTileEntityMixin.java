package svenhjol.charm.mixin;

import net.minecraft.potion.Effect;
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
    @Shadow private int levels;
    @Shadow private Effect primaryEffect;
    @Shadow private Effect secondaryEffect;

    public BeaconTileEntityMixin(TileEntityType<?> type) {
        super(type);
    }

    @Inject(
        method = "addEffectsToPlayers",
        at = @At("HEAD")
    )
    private void hookAddEffects(CallbackInfo ci) {
        if (this.world != null)
            BeaconsHealMobs.healInBeaconRange(this.world, this.levels, this.pos, this.primaryEffect, this.secondaryEffect);
    }
}
