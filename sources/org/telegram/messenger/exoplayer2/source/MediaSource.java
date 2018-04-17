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

        public MediaPeriodId(int periodIndex) {
            this(periodIndex, -1, -1);
        }

        public MediaPeriodId(int periodIndex, int adGroupIndex, int adIndexInAdGroup) {
            this.periodIndex = periodIndex;
            this.adGroupIndex = adGroupIndex;
            this.adIndexInAdGroup = adIndexInAdGroup;
        }

        public MediaPeriodId copyWithPeriodIndex(int newPeriodIndex) {
            return this.periodIndex == newPeriodIndex ? this : new MediaPeriodId(newPeriodIndex, this.adGroupIndex, this.adIndexInAdGroup);
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
                    MediaPeriodId periodId = (MediaPeriodId) obj;
                    if (this.periodIndex != periodId.periodIndex || this.adGroupIndex != periodId.adGroupIndex || this.adIndexInAdGroup != periodId.adIndexInAdGroup) {
                        z = false;
                    }
                    return z;
                }
            }
            return false;
        }

        public int hashCode() {
            return (31 * ((31 * ((31 * 17) + this.periodIndex)) + this.adGroupIndex)) + this.adIndexInAdGroup;
        }
    }

    MediaPeriod createPeriod(MediaPeriodId mediaPeriodId, Allocator allocator);

    void maybeThrowSourceInfoRefreshError() throws IOException;

    void prepareSource(ExoPlayer exoPlayer, boolean z, Listener listener);

    void releasePeriod(MediaPeriod mediaPeriod);

    void releaseSource();
}
