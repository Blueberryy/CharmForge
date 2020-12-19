package svenhjol.charm.mixin;

import net.minecraft.block.BlockState;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Hand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import svenhjol.charm.base.handler.ModuleHandler;
import svenhjol.charm.module.ArmorInvisibility;
import svenhjol.charm.module.UseTotemFromInventory;
import svenhjol.charm.module.VariantLadders;

@Mixin(LivingEntity.class)
public abstract class LivingEntityMixin extends Entity {
    public LivingEntityMixin(EntityType<?> type, World world) {
        super(type, world);
    }

    @Shadow
    public abstract Iterable<ItemStack> getArmorInventoryList();

    @Redirect(
        method = "checkTotemDeathProtection",
        at = @At(
            value = "INVOKE",
            target = "Lnet/minecraft/entity/LivingEntity;getHeldItem(Lnet/minecraft/util/Hand;)Lnet/minecraft/item/ItemStack;"
        )
    )
    private ItemStack hookTryUseTotem(LivingEntity livingEntity, Hand hand) {
        return UseTotemFromInventory.tryFromInventory(livingEntity, hand);
    }

    @Inject(
        method = "getArmorCoverPercentage",
        at = @At(value = "HEAD"),
        cancellable = true
    )
    private void hookArmorCover(CallbackInfoReturnable<Float> cir) {
        if (ModuleHandler.enabled(ArmorInvisibility.class)) {
            LivingEntity entity = (LivingEntity) (Object) this;
            Iterable<ItemStack> armorItems = this.getArmorInventoryList();

            int i = 0;
            int j = 0;

            for (ItemStack itemstack : armorItems) {
                if (!ArmorInvisibility.shouldArmorBeInvisible(entity, itemstack)) {
                    ++j;
                }
                ++i;
            }

            cir.setReturnValue(i > 0 ? (float)j / (float)i : 0.0F);
        }
    }

    /**
     * Checks trapdoor ladder is a variant ladder when player is climbing.
     * {@link VariantLadders#canEnterTrapdoor(World, BlockPos, BlockState)}
     */
    @Inject(
        method = "canGoThroughtTrapDoorOnLadder",
        at = @At("HEAD"),
        cancellable = true
    )
    private void hookCanEnterTrapdoor(BlockPos pos, BlockState state, CallbackInfoReturnable<Boolean> cir) {
        if (VariantLadders.canEnterTrapdoor(this.world, pos, state))
            cir.setReturnValue(true);
    }
}
