package svenhjol.charm.mixin;

import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.world.storage.MapData;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;
import svenhjol.charm.module.Atlas;

@Mixin(MapData.class)
public class MapDataMixin {

    @Redirect(method = "updateVisiblePlayers",
            at = @At(value = "INVOKE", target = "Lnet/minecraft/entity/player/PlayerInventory;hasItemStack(Lnet/minecraft/item/ItemStack;)Z")
    )
    public boolean hookHasItemStack(PlayerInventory inventory, ItemStack itemStack) {
        return Atlas.inventoryContainsMap(inventory, itemStack);
    }
}
