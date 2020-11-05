package svenhjol.charm.event;

import net.fabricmc.fabric.api.event.Event;
import net.fabricmc.fabric.api.event.EventFactory;
import net.minecraft.block.Block;
import net.minecraft.block.entity.TileEntity;

import javax.annotation.Nullable;

public interface BlockItemRenderCallback {
    Event<BlockItemRenderCallback> EVENT = EventFactory.createArrayBacked(BlockItemRenderCallback.class, (listeners) -> (block) -> {
        for (BlockItemRenderCallback listener : listeners) {
            TileEntity tileEntity = listener.interact(block);
            if (TileEntity != null)
                return TileEntity;
        }

        return null;
    });

    @Nullable
    TileEntity interact(Block block);
}
