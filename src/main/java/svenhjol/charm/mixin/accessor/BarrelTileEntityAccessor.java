package svenhjol.charm.mixin.accessor;

import net.minecraft.block.BlockState;
import net.minecraft.tileentity.BarrelTileEntity;
import net.minecraft.tileentity.TileEntityType;
import net.minecraft.util.SoundEvent;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;
import org.spongepowered.asm.mixin.gen.Invoker;

@Mixin(BarrelTileEntity.class)
public interface BarrelTileEntityAccessor {
    @Invoker("<init>")
    static BarrelTileEntity invokeConstructor(TileEntityType<?> typeEntityType) {
        throw new IllegalStateException();
    }

    @Accessor
    int getNumPlayersUsing();

    @Accessor
    void setNumPlayersUsing(int numPlayersUsing);

    @Invoker()
    void invokeScheduleTick();

    @Invoker
    void invokeSetOpenProperty(BlockState state, boolean open);

    @Invoker
    void invokePlaySound(BlockState state, SoundEvent sound);
}
