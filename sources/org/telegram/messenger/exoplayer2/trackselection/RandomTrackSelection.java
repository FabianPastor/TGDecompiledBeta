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

        public Factory(int i) {
            this.random = new Random((long) i);
        }

        public RandomTrackSelection createTrackSelection(TrackGroup trackGroup, int... iArr) {
            return new RandomTrackSelection(trackGroup, iArr, this.random);
        }
    }

    public Object getSelectionData() {
        return null;
    }

    public int getSelectionReason() {
        return 3;
    }

    public RandomTrackSelection(TrackGroup trackGroup, int... iArr) {
        super(trackGroup, iArr);
        this.random = new Random();
        this.selectedIndex = this.random.nextInt(this.length);
    }

    public RandomTrackSelection(TrackGroup trackGroup, int[] iArr, long j) {
        this(trackGroup, iArr, new Random(j));
    }

    public RandomTrackSelection(TrackGroup trackGroup, int[] iArr, Random random) {
        super(trackGroup, iArr);
        this.random = random;
        this.selectedIndex = random.nextInt(this.length);
    }

    public void updateSelectedTrack(long j, long j2, long j3) {
        j = SystemClock.elapsedRealtime();
        j2 = null;
        int i = 0;
        j3 = i;
        while (i < this.length) {
            if (!isBlacklisted(i, j)) {
                j3++;
            }
            i++;
        }
        this.selectedIndex = this.random.nextInt(j3);
        if (j3 != this.length) {
            i = 0;
            while (j2 < this.length) {
                if (isBlacklisted(j2, j) == null) {
                    int i2 = i + 1;
                    if (this.selectedIndex == i) {
                        this.selectedIndex = j2;
                        return;
                    }
                    i = i2;
                }
                j2++;
            }
        }
    }

    public int getSelectedIndex() {
        return this.selectedIndex;
    }
}
