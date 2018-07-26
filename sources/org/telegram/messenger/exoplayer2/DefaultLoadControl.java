package org.telegram.messenger.exoplayer2;

import org.telegram.messenger.exoplayer2.source.TrackGroupArray;
import org.telegram.messenger.exoplayer2.trackselection.TrackSelectionArray;
import org.telegram.messenger.exoplayer2.upstream.Allocator;
import org.telegram.messenger.exoplayer2.upstream.DefaultAllocator;
import org.telegram.messenger.exoplayer2.util.Assertions;
import org.telegram.messenger.exoplayer2.util.PriorityTaskManager;
import org.telegram.messenger.exoplayer2.util.Util;

public class DefaultLoadControl implements LoadControl {
    public static final int DEFAULT_BUFFER_FOR_PLAYBACK_AFTER_REBUFFER_MS = 5000;
    public static final int DEFAULT_BUFFER_FOR_PLAYBACK_MS = 2500;
    public static final int DEFAULT_MAX_BUFFER_MS = 50000;
    public static final int DEFAULT_MIN_BUFFER_MS = 15000;
    public static final boolean DEFAULT_PRIORITIZE_TIME_OVER_SIZE_THRESHOLDS = true;
    public static final int DEFAULT_TARGET_BUFFER_BYTES = -1;
    private final DefaultAllocator allocator;
    private final long bufferForPlaybackAfterRebufferUs;
    private final long bufferForPlaybackUs;
    private boolean isBuffering;
    private final long maxBufferUs;
    private final long minBufferUs;
    private final boolean prioritizeTimeOverSizeThresholds;
    private final PriorityTaskManager priorityTaskManager;
    private final int targetBufferBytesOverwrite;
    private int targetBufferSize;

    public static final class Builder {
        private DefaultAllocator allocator = null;
        private int bufferForPlaybackAfterRebufferMs = DefaultLoadControl.DEFAULT_BUFFER_FOR_PLAYBACK_AFTER_REBUFFER_MS;
        private int bufferForPlaybackMs = DefaultLoadControl.DEFAULT_BUFFER_FOR_PLAYBACK_MS;
        private int maxBufferMs = DefaultLoadControl.DEFAULT_MAX_BUFFER_MS;
        private int minBufferMs = DefaultLoadControl.DEFAULT_MIN_BUFFER_MS;
        private boolean prioritizeTimeOverSizeThresholds = true;
        private PriorityTaskManager priorityTaskManager = null;
        private int targetBufferBytes = -1;

        public Builder setAllocator(DefaultAllocator allocator) {
            this.allocator = allocator;
            return this;
        }

        public Builder setBufferDurationsMs(int minBufferMs, int maxBufferMs, int bufferForPlaybackMs, int bufferForPlaybackAfterRebufferMs) {
            this.minBufferMs = minBufferMs;
            this.maxBufferMs = maxBufferMs;
            this.bufferForPlaybackMs = bufferForPlaybackMs;
            this.bufferForPlaybackAfterRebufferMs = bufferForPlaybackAfterRebufferMs;
            return this;
        }

        public Builder setTargetBufferBytes(int targetBufferBytes) {
            this.targetBufferBytes = targetBufferBytes;
            return this;
        }

        public Builder setPrioritizeTimeOverSizeThresholds(boolean prioritizeTimeOverSizeThresholds) {
            this.prioritizeTimeOverSizeThresholds = prioritizeTimeOverSizeThresholds;
            return this;
        }

        public Builder setPriorityTaskManager(PriorityTaskManager priorityTaskManager) {
            this.priorityTaskManager = priorityTaskManager;
            return this;
        }

        public DefaultLoadControl createDefaultLoadControl() {
            if (this.allocator == null) {
                this.allocator = new DefaultAllocator(true, C0621C.DEFAULT_BUFFER_SEGMENT_SIZE);
            }
            return new DefaultLoadControl(this.allocator, this.minBufferMs, this.maxBufferMs, this.bufferForPlaybackMs, this.bufferForPlaybackAfterRebufferMs, this.targetBufferBytes, this.prioritizeTimeOverSizeThresholds, this.priorityTaskManager);
        }
    }

    public DefaultLoadControl() {
        this(new DefaultAllocator(true, C0621C.DEFAULT_BUFFER_SEGMENT_SIZE));
    }

    @Deprecated
    public DefaultLoadControl(DefaultAllocator allocator) {
        this(allocator, DEFAULT_MIN_BUFFER_MS, DEFAULT_MAX_BUFFER_MS, DEFAULT_BUFFER_FOR_PLAYBACK_MS, DEFAULT_BUFFER_FOR_PLAYBACK_AFTER_REBUFFER_MS, -1, true);
    }

    @Deprecated
    public DefaultLoadControl(DefaultAllocator allocator, int minBufferMs, int maxBufferMs, int bufferForPlaybackMs, int bufferForPlaybackAfterRebufferMs, int targetBufferBytes, boolean prioritizeTimeOverSizeThresholds) {
        this(allocator, minBufferMs, maxBufferMs, bufferForPlaybackMs, bufferForPlaybackAfterRebufferMs, targetBufferBytes, prioritizeTimeOverSizeThresholds, null);
    }

    @Deprecated
    public DefaultLoadControl(DefaultAllocator allocator, int minBufferMs, int maxBufferMs, int bufferForPlaybackMs, int bufferForPlaybackAfterRebufferMs, int targetBufferBytes, boolean prioritizeTimeOverSizeThresholds, PriorityTaskManager priorityTaskManager) {
        assertGreaterOrEqual(bufferForPlaybackMs, 0, "bufferForPlaybackMs", "0");
        assertGreaterOrEqual(bufferForPlaybackAfterRebufferMs, 0, "bufferForPlaybackAfterRebufferMs", "0");
        assertGreaterOrEqual(minBufferMs, bufferForPlaybackMs, "minBufferMs", "bufferForPlaybackMs");
        assertGreaterOrEqual(minBufferMs, bufferForPlaybackAfterRebufferMs, "minBufferMs", "bufferForPlaybackAfterRebufferMs");
        assertGreaterOrEqual(maxBufferMs, minBufferMs, "maxBufferMs", "minBufferMs");
        this.allocator = allocator;
        this.minBufferUs = ((long) minBufferMs) * 1000;
        this.maxBufferUs = ((long) maxBufferMs) * 1000;
        this.bufferForPlaybackUs = ((long) bufferForPlaybackMs) * 1000;
        this.bufferForPlaybackAfterRebufferUs = ((long) bufferForPlaybackAfterRebufferMs) * 1000;
        this.targetBufferBytesOverwrite = targetBufferBytes;
        this.prioritizeTimeOverSizeThresholds = prioritizeTimeOverSizeThresholds;
        this.priorityTaskManager = priorityTaskManager;
    }

    public void onPrepared() {
        reset(false);
    }

    public void onTracksSelected(Renderer[] renderers, TrackGroupArray trackGroups, TrackSelectionArray trackSelections) {
        this.targetBufferSize = this.targetBufferBytesOverwrite == -1 ? calculateTargetBufferSize(renderers, trackSelections) : this.targetBufferBytesOverwrite;
        this.allocator.setTargetBufferSize(this.targetBufferSize);
    }

    public void onStopped() {
        reset(true);
    }

    public void onReleased() {
        reset(true);
    }

    public Allocator getAllocator() {
        return this.allocator;
    }

    public long getBackBufferDurationUs() {
        return 0;
    }

    public boolean retainBackBufferFromKeyframe() {
        return false;
    }

    public boolean shouldContinueLoading(long bufferedDurationUs, float playbackSpeed) {
        boolean z = true;
        boolean targetBufferSizeReached;
        if (this.allocator.getTotalBytesAllocated() >= this.targetBufferSize) {
            targetBufferSizeReached = true;
        } else {
            targetBufferSizeReached = false;
        }
        boolean wasBuffering = this.isBuffering;
        long minBufferUs = this.minBufferUs;
        if (playbackSpeed > 1.0f) {
            minBufferUs = Math.min(Util.getMediaDurationForPlayoutDuration(minBufferUs, playbackSpeed), this.maxBufferUs);
        }
        if (bufferedDurationUs < minBufferUs) {
            if (!this.prioritizeTimeOverSizeThresholds && targetBufferSizeReached) {
                z = false;
            }
            this.isBuffering = z;
        } else if (bufferedDurationUs > this.maxBufferUs || targetBufferSizeReached) {
            this.isBuffering = false;
        }
        if (!(this.priorityTaskManager == null || this.isBuffering == wasBuffering)) {
            if (this.isBuffering) {
                this.priorityTaskManager.add(0);
            } else {
                this.priorityTaskManager.remove(0);
            }
        }
        return this.isBuffering;
    }

    public boolean shouldStartPlayback(long bufferedDurationUs, float playbackSpeed, boolean rebuffering) {
        bufferedDurationUs = Util.getPlayoutDurationForMediaDuration(bufferedDurationUs, playbackSpeed);
        long minBufferDurationUs = rebuffering ? this.bufferForPlaybackAfterRebufferUs : this.bufferForPlaybackUs;
        return minBufferDurationUs <= 0 || bufferedDurationUs >= minBufferDurationUs || (!this.prioritizeTimeOverSizeThresholds && this.allocator.getTotalBytesAllocated() >= this.targetBufferSize);
    }

    protected int calculateTargetBufferSize(Renderer[] renderers, TrackSelectionArray trackSelectionArray) {
        int targetBufferSize = 0;
        for (int i = 0; i < renderers.length; i++) {
            if (trackSelectionArray.get(i) != null) {
                targetBufferSize += Util.getDefaultBufferSize(renderers[i].getTrackType());
            }
        }
        return targetBufferSize;
    }

    private void reset(boolean resetAllocator) {
        this.targetBufferSize = 0;
        if (this.priorityTaskManager != null && this.isBuffering) {
            this.priorityTaskManager.remove(0);
        }
        this.isBuffering = false;
        if (resetAllocator) {
            this.allocator.reset();
        }
    }

    private static void assertGreaterOrEqual(int value1, int value2, String name1, String name2) {
        Assertions.checkArgument(value1 >= value2, name1 + " cannot be less than " + name2);
    }
}
