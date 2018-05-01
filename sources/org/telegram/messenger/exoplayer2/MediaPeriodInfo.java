package org.telegram.messenger.exoplayer2;

import org.telegram.messenger.exoplayer2.source.MediaSource.MediaPeriodId;

final class MediaPeriodInfo {
    public final long contentPositionUs;
    public final long durationUs;
    public final long endPositionUs;
    public final MediaPeriodId id;
    public final boolean isFinal;
    public final boolean isLastInTimelinePeriod;
    public final long startPositionUs;

    MediaPeriodInfo(MediaPeriodId id, long startPositionUs, long endPositionUs, long contentPositionUs, long durationUs, boolean isLastInTimelinePeriod, boolean isFinal) {
        this.id = id;
        this.startPositionUs = startPositionUs;
        this.endPositionUs = endPositionUs;
        this.contentPositionUs = contentPositionUs;
        this.durationUs = durationUs;
        this.isLastInTimelinePeriod = isLastInTimelinePeriod;
        this.isFinal = isFinal;
    }

    public MediaPeriodInfo copyWithPeriodIndex(int periodIndex) {
        return new MediaPeriodInfo(this.id.copyWithPeriodIndex(periodIndex), this.startPositionUs, this.endPositionUs, this.contentPositionUs, this.durationUs, this.isLastInTimelinePeriod, this.isFinal);
    }

    public MediaPeriodInfo copyWithStartPositionUs(long startPositionUs) {
        return new MediaPeriodInfo(this.id, startPositionUs, this.endPositionUs, this.contentPositionUs, this.durationUs, this.isLastInTimelinePeriod, this.isFinal);
    }
}
