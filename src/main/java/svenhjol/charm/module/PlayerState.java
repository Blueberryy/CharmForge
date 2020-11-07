package svenhjol.charm.module;

import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.gen.feature.structure.Structure;
import net.minecraft.world.server.ServerWorld;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import svenhjol.charm.Charm;
import svenhjol.charm.base.CharmModule;
import svenhjol.charm.base.helper.PosHelper;
import svenhjol.charm.base.iface.Config;
import svenhjol.charm.base.iface.Module;
import svenhjol.charm.client.PlayerStateClient;
import svenhjol.charm.message.ClientUpdatePlayerState;
import svenhjol.charm.message.ServerUpdatePlayerState;

import java.util.ArrayList;
import java.util.List;
import java.util.function.BiConsumer;

@Module(mod = Charm.MOD_ID, description = "Synchronize additional state from server to client.", alwaysEnabled = true, hasSubscriptions = true)
public class PlayerState extends CharmModule {
    public static List<BiConsumer<ServerPlayerEntity, CompoundNBT>> listeners = new ArrayList<>();

    public static PlayerStateClient client;

    @Config(name = "Server state update interval", description = "Interval (in ticks) on which additional world state will be synchronised to the client.")
    public static int serverStateInverval = 120;

    @SubscribeEvent
    public void onPlayerTick(TickEvent.PlayerTickEvent event) {
        if (event.phase == TickEvent.Phase.END
            && event.player.world.isRemote
            && event.player.world.getGameTime() % serverStateInverval == 0
        ) {
            Charm.PACKET_HANDLER.sendToServer(new ServerUpdatePlayerState());
        }
    }

    @Override
    @OnlyIn(Dist.CLIENT)
    public void clientRegister() {
        client = new PlayerStateClient();
    }

    /**
     * Populates an NBT tag of state information about the player,
     * sends a compressed string of data to the client to unpack.
     */
    public static void serverCallback(ServerPlayerEntity player) {
        ServerWorld world = player.getServerWorld();
        BlockPos pos = player.getPosition();
        long dayTime = world.getDayTime() % 24000;
        CompoundNBT tag = new CompoundNBT();

        tag.putBoolean("mineshaft", PosHelper.isInsideStructure(world, pos, Structure.field_236367_c_));
        tag.putBoolean("stronghold", PosHelper.isInsideStructure(world, pos, Structure.field_236375_k_));
        tag.putBoolean("fortress", PosHelper.isInsideStructure(world, pos, Structure.field_236378_n_));
        tag.putBoolean("shipwreck", PosHelper.isInsideStructure(world, pos, Structure.field_236373_i_));
        tag.putBoolean("village", world.isVillage(pos));
        tag.putBoolean("day", dayTime > 0 && dayTime < 12700);

        // send updated player data to listeners
        listeners.forEach(action -> action.accept(player, tag));

        // send updated player data to client
        Charm.PACKET_HANDLER.sendToPlayer(new ClientUpdatePlayerState(tag), player);
    }

    /**
     * Unpack the received server data from the NBT tag.
     */
    @OnlyIn(Dist.CLIENT)
    public static void clientCallback(CompoundNBT data) {
        client.mineshaft = data.getBoolean("mineshaft");
        client.stronghold = data.getBoolean("stronghold");
        client.fortress = data.getBoolean("fortress");
        client.shipwreck = data.getBoolean("shipwreck");
        client.village = data.getBoolean("village");
        client.isDaytime = data.getBoolean("day");
    }
}
