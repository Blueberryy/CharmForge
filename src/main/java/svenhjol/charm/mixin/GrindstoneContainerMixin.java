package svenhjol.charm.mixin;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.container.Container;
import net.minecraft.inventory.container.GrindstoneContainer;
import net.minecraft.inventory.container.Slot;
import net.minecraft.util.IWorldPosCallable;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyArg;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import svenhjol.charm.module.ExtractEnchantments;

import javax.annotation.Nullable;

@Mixin(value = GrindstoneContainer.class, priority = 1)
public abstract class GrindstoneContainerMixin {
    @Nullable
    PlayerEntity player;

    @Shadow @Final private IInventory inputInventory;

    @Shadow @Final private IWorldPosCallable worldPosCallable;

    @Shadow @Final private IInventory outputInventory;

    @Inject(
        method = "<init>(ILnet/minecraft/entity/player/PlayerInventory;Lnet/minecraft/util/IWorldPosCallable;)V",
        at = @At("RETURN")
    )
    private void hookGetPlayer(int syncId, PlayerInventory playerInventory, IWorldPosCallable context, CallbackInfo ci) {
        this.player = playerInventory.player;
    }

    @ModifyArg(
        method = "<init>(ILnet/minecraft/entity/player/PlayerInventory;Lnet/minecraft/util/IWorldPosCallable;)V",
        at = @At(
            value = "INVOKE",
            target = "Lnet/minecraft/inventory/container/GrindstoneContainer;addSlot(Lnet/minecraft/inventory/container/Slot;)Lnet/minecraft/inventory/container/Slot;",
            ordinal = 0
        )
    )
    private Slot hookAddSlot0(Slot slot) {
        return ExtractEnchantments.getGrindstoneInputSlot(0, this.inputInventory);
    }

    @ModifyArg(
        method = "<init>(ILnet/minecraft/entity/player/PlayerInventory;Lnet/minecraft/util/IWorldPosCallable;)V",
        at = @At(
            value = "INVOKE",
            target = "Lnet/minecraft/inventory/container/GrindstoneContainer;addSlot(Lnet/minecraft/inventory/container/Slot;)Lnet/minecraft/inventory/container/Slot;",
            ordinal = 1
        )
    )
    private Slot hookAddSlot1(Slot slot) {
        return ExtractEnchantments.getGrindstoneInputSlot(1, this.inputInventory);
    }

    @ModifyArg(
        method = "<init>(ILnet/minecraft/entity/player/PlayerInventory;Lnet/minecraft/util/IWorldPosCallable;)V",
        at = @At(
            value = "INVOKE",
            target = "Lnet/minecraft/inventory/container/GrindstoneContainer;addSlot(Lnet/minecraft/inventory/container/Slot;)Lnet/minecraft/inventory/container/Slot;",
            ordinal = 2
        )
    )
    private Slot hookAddSlot2(Slot slot) {
        return ExtractEnchantments.getGrindstoneOutputSlot(this.worldPosCallable, this.inputInventory, this.outputInventory);
    }

    @Inject(
        method = "updateRecipeOutput",
        at = @At("RETURN"),
        cancellable = true
    )
    private void hookUpdateResult(CallbackInfo ci) {
        boolean result = ExtractEnchantments.tryUpdateResult(this.inputInventory, this.outputInventory, this.player);

        if (result) {
            ((Container) (Object) this).detectAndSendChanges();
            ci.cancel();
        }
    }
}
