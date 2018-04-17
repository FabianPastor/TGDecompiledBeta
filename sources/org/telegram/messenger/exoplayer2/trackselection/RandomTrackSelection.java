package org.telegram.messenger.exoplayer2.trackselection;

import android.os.SystemClock;
import java.util.Random;
import org.telegram.messenger.exoplayer2.source.TrackGroup;

public final class RandomTrackSelection extends BaseTrackSelection {
    private final Random random;
    private int selectedIndex;

    public static final class Factory implements org.telegram.messenger.exoplayer2.trackselection.TrackSelection.Factory {
        private final Random random;

        public Factory() {
            this.random = new Random();
        }

        public Factory(int seed) {
            this.random = new Random((long) seed);
        }

        public RandomTrackSelection createTrackSelection(TrackGroup group, int... tracks) {
            return new RandomTrackSelection(group, tracks, this.random);
        }
    }

    public RandomTrackSelection(TrackGroup group, int... tracks) {
        super(group, tracks);
        this.random = new Random();
        this.selectedIndex = this.random.nextInt(this.length);
    }

    public RandomTrackSelection(TrackGroup group, int[] tracks, long seed) {
        this(group, tracks, new Random(seed));
    }

    public RandomTrackSelection(TrackGroup group, int[] tracks, Random random) {
        super(group, tracks);
        this.random = random;
        this.selectedIndex = random.nextInt(this.length);
    }

    public void updateSelectedTrack(long playbackPositionUs, long bufferedDurationUs, long availableDurationUs) {
        int i;
        long nowMs = SystemClock.elapsedRealtime();
        int i2 = 0;
        int nonBlacklistedFormatCount = 0;
        for (i = 0; i < this.length; i++) {
            if (!isBlacklisted(i, nowMs)) {
                nonBlacklistedFormatCount++;
            }
        }
        this.selectedIndex = this.random.nextInt(nonBlacklistedFormatCount);
        if (nonBlacklistedFormatCount != this.length) {
            nonBlacklistedFormatCount = 0;
            while (true) {
                i = i2;
                if (i >= this.length) {
                    break;
                }
                if (!isBlacklisted(i, nowMs)) {
                    int nonBlacklistedFormatCount2 = nonBlacklistedFormatCount + 1;
                    if (this.selectedIndex == nonBlacklistedFormatCount) {
                        this.selectedIndex = i;
                        return;
                    }
                    nonBlacklistedFormatCount = nonBlacklistedFormatCount2;
                }
                i2 = i + 1;
            }
        }
    }

    public int getSelectedIndex() {
        return this.selectedIndex;
    }

    public int getSelectionReason() {
        return 3;
    }

    public Object getSelectionData() {
        return null;
    }
}
