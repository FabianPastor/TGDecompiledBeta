package org.telegram.messenger.exoplayer2.trackselection;

import android.os.SystemClock;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;
import org.telegram.messenger.exoplayer2.Format;
import org.telegram.messenger.exoplayer2.source.TrackGroup;
import org.telegram.messenger.exoplayer2.source.chunk.MediaChunk;
import org.telegram.messenger.exoplayer2.util.Assertions;

public abstract class BaseTrackSelection implements TrackSelection {
    private final long[] blacklistUntilTimes;
    private final Format[] formats;
    protected final TrackGroup group;
    private int hashCode;
    protected final int length;
    protected final int[] tracks;

    private static final class DecreasingBandwidthComparator implements Comparator<Format> {
        private DecreasingBandwidthComparator() {
        }

        public int compare(Format format, Format format2) {
            return format2.bitrate - format.bitrate;
        }
    }

    public void disable() {
    }

    public void enable() {
    }

    public void onPlaybackSpeed(float f) {
    }

    public BaseTrackSelection(TrackGroup trackGroup, int... iArr) {
        int i = 0;
        Assertions.checkState(iArr.length > 0);
        this.group = (TrackGroup) Assertions.checkNotNull(trackGroup);
        this.length = iArr.length;
        this.formats = new Format[this.length];
        for (int i2 = 0; i2 < iArr.length; i2++) {
            this.formats[i2] = trackGroup.getFormat(iArr[i2]);
        }
        Arrays.sort(this.formats, new DecreasingBandwidthComparator());
        this.tracks = new int[this.length];
        while (i < this.length) {
            this.tracks[i] = trackGroup.indexOf(this.formats[i]);
            i++;
        }
        this.blacklistUntilTimes = new long[this.length];
    }

    public final TrackGroup getTrackGroup() {
        return this.group;
    }

    public final int length() {
        return this.tracks.length;
    }

    public final Format getFormat(int i) {
        return this.formats[i];
    }

    public final int getIndexInTrackGroup(int i) {
        return this.tracks[i];
    }

    public final int indexOf(Format format) {
        for (int i = 0; i < this.length; i++) {
            if (this.formats[i] == format) {
                return i;
            }
        }
        return -1;
    }

    public final int indexOf(int i) {
        for (int i2 = 0; i2 < this.length; i2++) {
            if (this.tracks[i2] == i) {
                return i2;
            }
        }
        return -1;
    }

    public final Format getSelectedFormat() {
        return this.formats[getSelectedIndex()];
    }

    public final int getSelectedIndexInTrackGroup() {
        return this.tracks[getSelectedIndex()];
    }

    public int evaluateQueueSize(long j, List<? extends MediaChunk> list) {
        return list.size();
    }

    public final boolean blacklist(int i, long j) {
        long elapsedRealtime = SystemClock.elapsedRealtime();
        boolean isBlacklisted = isBlacklisted(i, elapsedRealtime);
        int i2 = 0;
        while (i2 < this.length && !isBlacklisted) {
            isBlacklisted = (i2 == i || isBlacklisted(i2, elapsedRealtime)) ? false : true;
            i2++;
        }
        if (!isBlacklisted) {
            return false;
        }
        this.blacklistUntilTimes[i] = Math.max(this.blacklistUntilTimes[i], elapsedRealtime + j);
        return true;
    }

    protected final boolean isBlacklisted(int i, long j) {
        return this.blacklistUntilTimes[i] > j;
    }

    public int hashCode() {
        if (this.hashCode == 0) {
            this.hashCode = (31 * System.identityHashCode(this.group)) + Arrays.hashCode(this.tracks);
        }
        return this.hashCode;
    }

    public boolean equals(Object obj) {
        boolean z = true;
        if (this == obj) {
            return true;
        }
        if (obj != null) {
            if (getClass() == obj.getClass()) {
                BaseTrackSelection baseTrackSelection = (BaseTrackSelection) obj;
                if (this.group != baseTrackSelection.group || Arrays.equals(this.tracks, baseTrackSelection.tracks) == null) {
                    z = false;
                }
                return z;
            }
        }
        return false;
    }
}
