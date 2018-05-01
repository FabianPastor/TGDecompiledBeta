package org.telegram.messenger.exoplayer2.source;

import java.io.IOException;
import org.telegram.messenger.exoplayer2.ExoPlayer;
import org.telegram.messenger.exoplayer2.Timeline;
import org.telegram.messenger.exoplayer2.upstream.Allocator;

public interface MediaSource {
    public static final String MEDIA_SOURCE_REUSED_ERROR_MESSAGE = "MediaSource instances are not allowed to be reused.";

    public interface Listener {
        void onSourceInfoRefreshed(MediaSource mediaSource, Timeline timeline, Object obj);
    }

    public static final class MediaPeriodId {
        public static final MediaPeriodId UNSET = new MediaPeriodId(-1, -1, -1);
        public final int adGroupIndex;
        public final int adIndexInAdGroup;
        public final int periodIndex;

        public MediaPeriodId(int i) {
            this(i, -1, -1);
        }

        public MediaPeriodId(int i, int i2, int i3) {
            this.periodIndex = i;
            this.adGroupIndex = i2;
            this.adIndexInAdGroup = i3;
        }

        public MediaPeriodId copyWithPeriodIndex(int i) {
            return this.periodIndex == i ? this : new MediaPeriodId(i, this.adGroupIndex, this.adIndexInAdGroup);
        }

        public boolean isAd() {
            return this.adGroupIndex != -1;
        }

        public boolean equals(Object obj) {
            boolean z = true;
            if (this == obj) {
                return true;
            }
            if (obj != null) {
                if (getClass() == obj.getClass()) {
                    MediaPeriodId mediaPeriodId = (MediaPeriodId) obj;
                    if (this.periodIndex != mediaPeriodId.periodIndex || this.adGroupIndex != mediaPeriodId.adGroupIndex || this.adIndexInAdGroup != mediaPeriodId.adIndexInAdGroup) {
                        z = false;
                    }
                    return z;
                }
            }
            return false;
        }

        public int hashCode() {
            return (31 * (((527 + this.periodIndex) * 31) + this.adGroupIndex)) + this.adIndexInAdGroup;
        }
    }

    MediaPeriod createPeriod(MediaPeriodId mediaPeriodId, Allocator allocator);

    void maybeThrowSourceInfoRefreshError() throws IOException;

    void prepareSource(ExoPlayer exoPlayer, boolean z, Listener listener);

    void releasePeriod(MediaPeriod mediaPeriod);

    void releaseSource();
}
