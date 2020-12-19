package svenhjol.charm.module;

import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.server.MinecraftServer;
import net.minecraft.world.GameRules;
import net.minecraft.world.World;
import net.minecraft.world.server.ServerWorld;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.LogicalSide;
import svenhjol.charm.Charm;
import svenhjol.charm.base.CharmModule;
import svenhjol.charm.base.handler.ModuleHandler;
import svenhjol.charm.base.helper.ModHelper;
import svenhjol.charm.base.iface.Config;
import svenhjol.charm.base.iface.Module;
import svenhjol.charm.mixin.accessor.ServerWorldAccessor;

import java.util.List;
import java.util.stream.Collectors;

@Module(mod = Charm.MOD_ID, description = "Allows the night to pass when a specified number of players are asleep.", hasSubscriptions = true)
public class SleepImprovements extends CharmModule {
    @Config(name = "Faster sleep", description = "If true, the sleeping player does not need to wait as long before ending the night.")
    public static boolean fasterSleep = false;

    @Config(name = "Number of required players", description = "The number of players required to sleep in order to bring the next day.")
    public static int requiredPlayers = 1;

    @Config(name = "Override", description = "This module is automatically disabled if Quark is present. Set true to force enable.")
    public static boolean override = false;

    @Override
    public boolean depends() {
        return !ModuleHandler.enabled("quark:tweaks.module.improved_sleeping_module") || override;
    }

    @SubscribeEvent
    public void onWorldTick(TickEvent.WorldTickEvent event) {
        if (!event.isCanceled() && event.side != LogicalSide.CLIENT)
            tryEndNight((ServerWorld)event.world);
    }

    private void tryEndNight(ServerWorld world) {
        if (world == null || world.getGameTime() % 20 != 0 || !world.getDimensionKey().equals(World.OVERWORLD))
            return;

        MinecraftServer server = world.getServer();

        int currentPlayerCount = world.getServer().getCurrentPlayerCount();
        if (currentPlayerCount < requiredPlayers)
            return;

        List<ServerPlayerEntity> validPlayers = server.getPlayerList().getPlayers().stream()
            .filter(p -> !p.isSpectator() && (fasterSleep ? p.isSleeping() : p.isPlayerFullyAsleep()))
            .collect(Collectors.toList());

        if (validPlayers.size() < requiredPlayers)
            return;

        /** copypasta from {@link ServerWorld#tick} */
        if (world.getGameRules().getBoolean(GameRules.DO_DAYLIGHT_CYCLE)) {
            long l = world.getDayTime() + 24000L;
            world.setDayTime(l - l % 24000L);
        }

        ((ServerWorldAccessor)world).invokeWakeUpAllPlayers();
        if (world.getGameRules().getBoolean(GameRules.DO_WEATHER_CYCLE))
            ((ServerWorldAccessor)world).invokeResetRainAndThunder();
    }
}
