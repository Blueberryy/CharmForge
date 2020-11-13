package svenhjol.charm.client;

import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.JukeboxBlock;
import net.minecraft.client.Minecraft;
import net.minecraft.client.audio.BackgroundMusicSelector;
import net.minecraft.client.audio.ISound;
import net.minecraft.client.audio.SimpleSound;
import net.minecraft.client.audio.SoundHandler;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.MusicDiscItem;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.SoundEvents;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraftforge.client.event.sound.SoundEvent;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.event.entity.player.PlayerInteractEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import svenhjol.charm.Charm;
import svenhjol.charm.base.CharmClientModule;
import svenhjol.charm.base.CharmModule;
import svenhjol.charm.base.helper.DimensionHelper;
import svenhjol.charm.base.helper.SoundHelper;
import svenhjol.charm.module.MusicImprovements;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.function.Predicate;

@SuppressWarnings("unused")
public class MusicImprovementsClient extends CharmClientModule {
    private ISound musicToStop = null;
    private int ticksBeforeStop = 0;
    private static ISound currentMusic;
    private static ResourceLocation currentDim = null;
    private static int timeUntilNextMusic = 100;
    private static final List<MusicCondition> musicConditions = new ArrayList<>();
    public static boolean isEnabled;

    public MusicImprovementsClient(CharmModule module) {
        super(module);
    }

    @Override
    public void register() {
        // set statically so hooks can check this is enabled
        isEnabled = module.enabled;

        if (MusicImprovements.playCreativeMusic)
            addCreativeMusicCondition();
    }

    @SubscribeEvent
    public void onRightClickBlock(PlayerInteractEvent.RightClickBlock event) {
        if (!event.isCanceled())
            stopRecord(event.getEntity(), event.getPos(), event.getItemStack());
    }

    @SubscribeEvent
    public void onSoundSource(SoundEvent.SoundSourceEvent event) {
        if (!event.isCanceled())
            checkShouldStopMusic(event.getSound());
    }

    @SubscribeEvent
    public void onClientTick(TickEvent.ClientTickEvent event) {
        if (!event.isCanceled())
            checkActuallyStopMusic();
    }

    public void addCreativeMusicCondition() {
        musicConditions.add(new MusicCondition(
            SoundEvents.MUSIC_CREATIVE, 1200, 3600, mc -> mc.player != null
                && (!mc.player.isCreative() || !mc.player.isSpectator())
                && DimensionHelper.isDimension(mc.player.world, new ResourceLocation("overworld"))
                && new Random().nextFloat() < 0.25F
        ));
    }

    public void stopRecord(Entity entity, BlockPos pos, ItemStack stack) {
        if (entity.world.isRemote
            && entity instanceof PlayerEntity
            && stack.getItem() instanceof MusicDiscItem
        ) {
            BlockState state = entity.world.getBlockState(pos);
            if (state.getBlock() == Blocks.JUKEBOX && !state.get(JukeboxBlock.HAS_RECORD))
                SoundHelper.getSoundManager().stop(null, SoundCategory.MUSIC);
        }
    }

    public void checkShouldStopMusic(ISound sound) {
        if (sound.getCategory() == SoundCategory.MUSIC) {
            // check if there are any records playing
            SoundHelper.getPlayingSounds().forEach((category, s) -> {
                if (category == SoundCategory.RECORDS) {
                    musicToStop = sound;
                    Charm.LOG.debug("Triggered background music while record playing");
                }
            });
        }
    }

    public void checkActuallyStopMusic() {
        if (musicToStop != null
            && ++ticksBeforeStop % 10 == 0
        ) {
            SoundHelper.getSoundManager().stop(musicToStop);
            ticksBeforeStop = 0;
            musicToStop = null;
        }
    }

    public static boolean handleTick(ISound current) {
        Minecraft mc = Minecraft.getInstance();

        if (mc.world == null) return false;
        MusicCondition ambient = getMusicCondition();

        if (currentMusic != null) {
            if (!DimensionHelper.isDimension(mc.world, currentDim))
                forceStop();

            if (!mc.getSoundHandler().isPlaying(currentMusic)) {
                currentMusic = null;
                timeUntilNextMusic = Math.min(MathHelper.nextInt(new Random(), ambient.getMinDelay(), 3600), timeUntilNextMusic);
            }
        }

        timeUntilNextMusic = Math.min(timeUntilNextMusic, ambient.getMaxDelay());

        if (currentMusic == null && timeUntilNextMusic-- <= 0) {
            currentDim = DimensionHelper.getDimension(mc.world);
            currentMusic = SimpleSound.music(ambient.getSound());

            if (currentMusic.getSound() != SoundHandler.MISSING_SOUND) {
                mc.getSoundHandler().play(currentMusic);
                timeUntilNextMusic = Integer.MAX_VALUE;
            }
        }

        return true;
    }

    public static boolean handleStop() {
        if (currentMusic != null) {
            Minecraft.getInstance().getSoundHandler().stop(currentMusic);
            currentMusic = null;
            timeUntilNextMusic = 0;
        }
        return true;
    }

    public static boolean handlePlaying(BackgroundMusicSelector music) {
        return currentMusic != null && music.getSoundEvent().getName().equals(currentMusic.getSoundLocation());
    }

    public static void forceStop() {
        Minecraft.getInstance().getSoundHandler().stop(currentMusic);
        currentMusic = null;
        timeUntilNextMusic = 3600;
    }

    public static MusicCondition getMusicCondition() {
        MusicCondition condition = null;

        // select an available condition from the pool of conditions
        for (MusicCondition c : musicConditions) {
            if (c.handle()) {
                condition = c;
                break;
            }
        }

        // if none available, just play a default background track
        if (condition == null)
            condition = new MusicCondition(Minecraft.getInstance().getBackgroundMusicSelector());

        return condition;
    }

    public static List<MusicCondition> getMusicConditions() {
        return musicConditions;
    }

    public static class MusicCondition {
        private final net.minecraft.util.SoundEvent sound;
        private final int minDelay;
        private final int maxDelay;
        private Predicate<Minecraft> condition;

        public MusicCondition(net.minecraft.util.SoundEvent sound, int minDelay, int maxDelay, Predicate<Minecraft> condition) {
            this.sound = sound;
            this.minDelay = minDelay;
            this.maxDelay = maxDelay;
            this.condition = condition;
        }

        public MusicCondition(BackgroundMusicSelector music) {
            this.sound = music.getSoundEvent();
            this.minDelay = music.getMinDelay();
            this.maxDelay = music.getMaxDelay();
        }

        public boolean handle() {
            if (condition == null) return false;
            return condition.test(Minecraft.getInstance());
        }

        public net.minecraft.util.SoundEvent getSound() {
            return sound;
        }

        public int getMaxDelay() {
            return maxDelay;
        }

        public int getMinDelay() {
            return minDelay;
        }
    }
}
