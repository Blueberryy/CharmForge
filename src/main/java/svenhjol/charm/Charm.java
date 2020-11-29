package svenhjol.charm;

import net.minecraftforge.fml.common.Mod;
import svenhjol.charm.base.CharmMessages;
import svenhjol.charm.base.CharmSounds;
import svenhjol.charm.base.CharmStructures;
import svenhjol.charm.base.CharmTags;
import svenhjol.charm.base.handler.LogHandler;
import svenhjol.charm.base.handler.ModuleHandler;
import svenhjol.charm.base.handler.PacketHandler;
import svenhjol.charm.module.*;

import java.util.Arrays;

@Mod(Charm.MOD_ID)
public class Charm {
    public static final String MOD_ID = "charm";
    public static LogHandler LOG = new LogHandler("Charm");
    public static PacketHandler PACKET_HANDLER = new PacketHandler();

    public Charm() {
        ModuleHandler.INSTANCE.registerForgeMod(MOD_ID, Arrays.asList(
            Acquisition.class,
            AnvilImprovements.class,
            ArmorInvisibility.class,
            AutomaticRecipeUnlock.class,
            AutoRestock.class,
            BatBuckets.class,
            BeaconsHealMobs.class,
            Beekeepers.class,
            BlockOfEnderPearls.class,
            BlockOfGunpowder.class,
            BlockOfSugar.class,
            Bookcases.class,
            CampfiresNoDamage.class,
            Candles.class,
            CaveSpidersDropCobwebs.class,
            ChickensDropFeathers.class,
            CoralSeaLanterns.class,
            CoralSquids.class,
            Core.class,
            Crates.class,
            DecreaseRepairCost.class,
            DirtToPath.class,
            EndermitePowder.class,
            EntitySpawners.class,
            ExtendNetherite.class,
            ExtractEnchantments.class,
            FeatherFallingCrops.class,
            Glowballs.class,
            GoldBars.class,
            GoldChains.class,
            GoldLanterns.class,
            HoeHarvesting.class,
            HuskImprovements.class,
            InventoryTidying.class,
            Kilns.class,
            Lumberjacks.class,
            MineshaftImprovements.class,
            Mooblooms.class,
            MoreVillageBiomes.class,
            MusicImprovements.class,
            NetheriteNuggets.class,
            ParrotsStayOnShoulder.class,
            PathToDirt.class,
            PlayerState.class,
            PortableCrafting.class,
            PortableEnderChest.class,
            Quark.class,
            RaidHorns.class,
            RedstoneLanterns.class,
            RedstoneSand.class,
            RefinedObsidian.class,
            RemoveNitwits.class,
            RemovePotionGlint.class,
            ShulkerBoxTooltips.class,
            SleepImprovements.class,
            SmoothGlowstone.class,
            SnowStorms.class,
            StackableEnchantedBooks.class,
            StackablePotions.class,
            StrayImprovements.class,
            TamedAnimalsNoDamage.class,
            Tinted.class,
            UseTotemFromInventory.class,
            VariantBarrels.class,
            VariantBookshelves.class,
            VariantChests.class,
            VariantLadders.class,
            VariantMobTextures.class,
            VillagersFollowEmeraldBlocks.class,
            WanderingTraderImprovements.class,
            WitchesDropLuck.class,
            Woodcutters.class
        ));

        CharmMessages.init();
        CharmStructures.init();
        CharmSounds.init();
        CharmTags.init();

        ModuleHandler.INSTANCE.init();
    }
}
