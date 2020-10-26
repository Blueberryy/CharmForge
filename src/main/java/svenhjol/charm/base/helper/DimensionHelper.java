package svenhjol.charm.base.helper;

import net.minecraft.util.RegistryKey;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.World;

import javax.annotation.Nullable;

public class DimensionHelper {
    public static boolean isOverworld(World world) {
        return world.getDimensionKey() == World.OVERWORLD;
    }

    public static boolean isNether(World world) {
        return world.getDimensionKey() == World.THE_NETHER;
    }

    public static boolean isEnd(World world) {
        return world.getDimensionKey() == World.THE_END;
    }

    public static boolean isDimension(World world, ResourceLocation dimension) {
        return getDimension(world).equals(dimension);
    }

    public static ResourceLocation getDimension(World world) {
        RegistryKey<World> key = world.getDimensionKey();
        return key.getLocation(); // TODO: might be key.getRegistryName()
    }

    @Nullable
    public static RegistryKey<World> getDimension(ResourceLocation dim) {
        if (World.OVERWORLD.getLocation().equals(dim)) {
            return World.OVERWORLD;
        } else if (World.THE_NETHER.getLocation().equals(dim)) {
            return World.THE_NETHER;
        } else if (World.THE_END.getLocation().equals(dim)) {
            return World.THE_END;
        }

        return null;
    }
}
