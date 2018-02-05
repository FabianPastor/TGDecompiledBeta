package org.telegram.messenger.exoplayer2.source;

import java.io.IOException;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.util.ArrayList;
import org.telegram.messenger.exoplayer2.C;
import org.telegram.messenger.exoplayer2.ExoPlayer;
import org.telegram.messenger.exoplayer2.Timeline;
import org.telegram.messenger.exoplayer2.Timeline.Period;
import org.telegram.messenger.exoplayer2.Timeline.Window;
import org.telegram.messenger.exoplayer2.source.MediaSource.Listener;
import org.telegram.messenger.exoplayer2.source.MediaSource.MediaPeriodId;
import org.telegram.messenger.exoplayer2.upstream.Allocator;
import org.telegram.messenger.exoplayer2.util.Assertions;

public final class ClippingMediaSource extends CompositeMediaSource<Void> {
    private IllegalClippingException clippingError;
    private final boolean enableInitialDiscontinuity;
    private final long endUs;
    private final ArrayList<ClippingMediaPeriod> mediaPeriods;
    private final MediaSource mediaSource;
    private Listener sourceListener;
    private final long startUs;

    public static final class IllegalClippingException extends IOException {
        public static final int REASON_INVALID_PERIOD_COUNT = 0;
        public static final int REASON_NOT_SEEKABLE_TO_START = 2;
        public static final int REASON_PERIOD_OFFSET_IN_WINDOW = 1;
        public static final int REASON_START_EXCEEDS_END = 3;
        public final int reason;

        @Retention(RetentionPolicy.SOURCE)
        public @interface Reason {
        }

        public IllegalClippingException(int reason) {
            this.reason = reason;
        }
    }

    private static final class ClippingTimeline extends ForwardingTimeline {
        private final long endUs;
        private final long startUs;

        public ClippingTimeline(Timeline timeline, long startUs, long endUs) throws IllegalClippingException {
            super(timeline);
            if (timeline.getPeriodCount() != 1) {
                throw new IllegalClippingException(0);
            } else if (timeline.getPeriod(0, new Period()).getPositionInWindowUs() != 0) {
                throw new IllegalClippingException(1);
            } else {
                long resolvedEndUs;
                Window window = timeline.getWindow(0, new Window(), false);
                if (endUs == Long.MIN_VALUE) {
                    resolvedEndUs = window.durationUs;
                } else {
                    resolvedEndUs = endUs;
                }
                if (window.durationUs != C.TIME_UNSET) {
                    if (resolvedEndUs > window.durationUs) {
                        resolvedEndUs = window.durationUs;
                    }
                    if (startUs != 0 && !window.isSeekable) {
                        throw new IllegalClippingException(2);
                    } else if (startUs > resolvedEndUs) {
                        throw new IllegalClippingException(3);
                    }
                }
                this.startUs = startUs;
                this.endUs = resolvedEndUs;
            }
        }

        public Window getWindow(int windowIndex, Window window, boolean setIds, long defaultPositionProjectionUs) {
            window = this.timeline.getWindow(0, window, setIds, defaultPositionProjectionUs);
            window.durationUs = this.endUs != C.TIME_UNSET ? this.endUs - this.startUs : C.TIME_UNSET;
            if (window.defaultPositionUs != C.TIME_UNSET) {
                long j;
                window.defaultPositionUs = Math.max(window.defaultPositionUs, this.startUs);
                if (this.endUs == C.TIME_UNSET) {
                    j = window.defaultPositionUs;
                } else {
                    j = Math.min(window.defaultPositionUs, this.endUs);
                }
                window.defaultPositionUs = j;
                window.defaultPositionUs -= this.startUs;
            }
            long startMs = C.usToMs(this.startUs);
            if (window.presentationStartTimeMs != C.TIME_UNSET) {
                window.presentationStartTimeMs += startMs;
            }
            if (window.windowStartTimeMs != C.TIME_UNSET) {
                window.windowStartTimeMs += startMs;
            }
            return window;
        }

        public Period getPeriod(int periodIndex, Period period, boolean setIds) {
            long j = C.TIME_UNSET;
            period = this.timeline.getPeriod(0, period, setIds);
            if (this.endUs != C.TIME_UNSET) {
                j = this.endUs - this.startUs;
            }
            period.durationUs = j;
            return period;
        }
    }

    public ClippingMediaSource(MediaSource mediaSource, long startPositionUs, long endPositionUs) {
        this(mediaSource, startPositionUs, endPositionUs, true);
    }

    public ClippingMediaSource(MediaSource mediaSource, long startPositionUs, long endPositionUs, boolean enableInitialDiscontinuity) {
        Assertions.checkArgument(startPositionUs >= 0);
        this.mediaSource = (MediaSource) Assertions.checkNotNull(mediaSource);
        this.startUs = startPositionUs;
        this.endUs = endPositionUs;
        this.enableInitialDiscontinuity = enableInitialDiscontinuity;
        this.mediaPeriods = new ArrayList();
    }

    public void prepareSource(ExoPlayer player, boolean isTopLevelSource, Listener listener) {
        super.prepareSource(player, isTopLevelSource, listener);
        this.sourceListener = listener;
        prepareChildSource(null, this.mediaSource);
    }

    public void maybeThrowSourceInfoRefreshError() throws IOException {
        if (this.clippingError != null) {
            throw this.clippingError;
        }
        super.maybeThrowSourceInfoRefreshError();
    }

    public MediaPeriod createPeriod(MediaPeriodId id, Allocator allocator) {
        ClippingMediaPeriod mediaPeriod = new ClippingMediaPeriod(this.mediaSource.createPeriod(id, allocator), this.enableInitialDiscontinuity);
        this.mediaPeriods.add(mediaPeriod);
        mediaPeriod.setClipping(this.startUs, this.endUs);
        return mediaPeriod;
    }

    public void releasePeriod(MediaPeriod mediaPeriod) {
        Assertions.checkState(this.mediaPeriods.remove(mediaPeriod));
        this.mediaSource.releasePeriod(((ClippingMediaPeriod) mediaPeriod).mediaPeriod);
    }

    public void releaseSource() {
        super.releaseSource();
        this.clippingError = null;
        this.sourceListener = null;
    }

    protected void onChildSourceInfoRefreshed(Void id, MediaSource mediaSource, Timeline timeline, Object manifest) {
        if (this.clippingError == null) {
            try {
                this.sourceListener.onSourceInfoRefreshed(this, new ClippingTimeline(timeline, this.startUs, this.endUs), manifest);
                int count = this.mediaPeriods.size();
                for (int i = 0; i < count; i++) {
                    ((ClippingMediaPeriod) this.mediaPeriods.get(i)).setClipping(this.startUs, this.endUs);
                }
            } catch (IllegalClippingException e) {
                this.clippingError = e;
            }
        }
    }
}
