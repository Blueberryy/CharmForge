package svenhjol.charm.client;

import net.minecraft.client.Minecraft;
import net.minecraft.client.audio.BackgroundMusicSelector;
import net.minecraft.util.SoundEvent;

import java.util.function.Predicate;

public class MusicCondition {
    private final SoundEvent sound;
    private final int minDelay;
    private final int maxDelay;
    private Predicate<Minecraft> condition;

    public MusicCondition(SoundEvent sound, int minDelay, int maxDelay, Predicate<Minecraft> condition) {
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

    public SoundEvent getSound() {
        return sound;
    }

    public int getMaxDelay() {
        return maxDelay;
    }

    public int getMinDelay() {
        return minDelay;
    }
}
