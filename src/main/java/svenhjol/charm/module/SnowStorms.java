package svenhjol.charm.module;

import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.SnowBlock;
import net.minecraft.util.Direction;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.gen.Heightmap;
import net.minecraft.world.server.ServerWorld;
import svenhjol.charm.Charm;
import svenhjol.charm.base.CharmModule;
import svenhjol.charm.base.handler.ModuleHandler;
import svenhjol.charm.base.iface.Config;
import svenhjol.charm.base.iface.Module;
import svenhjol.charm.client.SnowStormsClient;

@Module(mod = Charm.MOD_ID, client = SnowStormsClient.class, description = "Increases snow layers in cold biomes during thunderstorms.")
public class SnowStorms extends CharmModule {
    public static final ResourceLocation HEAVY_SNOW = new ResourceLocation(Charm.MOD_ID, "textures/environment/heavy_snow.png");

    @Config(name = "Snow layer chance", description = "Chance (out of 1.0) every tick of snow increasing by one layer during a thunderstorm.")
    public static double snowLayerChance = 0.15D;

    @Config(name = "Heavier snow texture", description = "If true, heavier snow textures are rendered during thunderstorms.")
    public static boolean heavierSnowTexture = true;

    public static boolean tryRandomTick(ServerWorld world) {
        return ModuleHandler.enabled(SnowStorms.class) && world.isThundering();
    }

    public static void tryPlaceSnow(ServerWorld world, int chunkX, int chunkZ) {
        if (!ModuleHandler.enabled(SnowStorms.class) || !world.isThundering())
            return;

        if (world.rand.nextDouble() < snowLayerChance) {
            BlockPos pos = world.getHeight(Heightmap.Type.MOTION_BLOCKING, world.getBlockRandomPos(chunkX, 0, chunkZ, 15));
            BlockPos downPos = pos.down();
            BlockState downState = world.getBlockState(downPos);
            Biome biome = world.getBiome(pos);

            if (biome.getTemperature(pos) < 0.15F && pos.getY() >= 0 && pos.getY() < 256) {
                BlockState state = world.getBlockState(pos);
                Block block = state.getBlock();

                if (state.isAir()) {
                    if (!downState.isIn(Blocks.ICE)
                        && !downState.isIn(Blocks.PACKED_ICE)
                        && !downState.isIn(Blocks.BARRIER)
                        && !downState.isIn(Blocks.HONEY_BLOCK)
                        && !downState.isIn(Blocks.SOUL_SAND)
                        && !downState.isIn(Blocks.SNOW)
                        && Block.doesSideFillSquare(downState.getCollisionShape(world, downPos), Direction.UP)
                    ) {
                        world.setBlockState(pos, Blocks.SNOW.getDefaultState());
                        return;
                    }
                }

                if (block == Blocks.SNOW) {
                    int layers = state.get(SnowBlock.LAYERS);
                    if (layers < 8) {
                        state = state.with(SnowBlock.LAYERS, ++layers);
                        world.setBlockState(pos, state);
                    }
                }
            }
        }
    }
}
