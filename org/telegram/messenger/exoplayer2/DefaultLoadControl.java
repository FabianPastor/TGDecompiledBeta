package org.telegram.messenger.exoplayer2;

import org.telegram.messenger.exoplayer2.source.TrackGroupArray;
import org.telegram.messenger.exoplayer2.trackselection.TrackSelectionArray;
import org.telegram.messenger.exoplayer2.upstream.Allocator;
import org.telegram.messenger.exoplayer2.upstream.DefaultAllocator;
import org.telegram.messenger.exoplayer2.util.PriorityTaskManager;
import org.telegram.messenger.exoplayer2.util.Util;

public class DefaultLoadControl implements LoadControl {
    public static final int DEFAULT_BUFFER_FOR_PLAYBACK_AFTER_REBUFFER_MS = 5000;
    public static final int DEFAULT_BUFFER_FOR_PLAYBACK_MS = 2500;
    public static final int DEFAULT_MAX_BUFFER_MS = 30000;
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

    public DefaultLoadControl() {
        this(new DefaultAllocator(true, C.DEFAULT_BUFFER_SEGMENT_SIZE));
    }

    public DefaultLoadControl(DefaultAllocator allocator) {
        this(allocator, DEFAULT_MIN_BUFFER_MS, DEFAULT_MAX_BUFFER_MS, DEFAULT_BUFFER_FOR_PLAYBACK_MS, DEFAULT_BUFFER_FOR_PLAYBACK_AFTER_REBUFFER_MS, -1, true);
    }

    public DefaultLoadControl(DefaultAllocator allocator, int minBufferMs, int maxBufferMs, int bufferForPlaybackMs, int bufferForPlaybackAfterRebufferMs, int targetBufferBytes, boolean prioritizeTimeOverSizeThresholds) {
        this(allocator, minBufferMs, maxBufferMs, bufferForPlaybackMs, bufferForPlaybackAfterRebufferMs, targetBufferBytes, prioritizeTimeOverSizeThresholds, null);
    }

    public DefaultLoadControl(DefaultAllocator allocator, int minBufferMs, int maxBufferMs, int bufferForPlaybackMs, int bufferForPlaybackAfterRebufferMs, int targetBufferBytes, boolean prioritizeTimeOverSizeThresholds, PriorityTaskManager priorityTaskManager) {
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
        boolean targetBufferSizeReached;
        boolean z = true;
        if (this.allocator.getTotalBytesAllocated() >= this.targetBufferSize) {
            targetBufferSizeReached = true;
        } else {
            targetBufferSizeReached = false;
        }
        boolean wasBuffering = this.isBuffering;
        if (this.prioritizeTimeOverSizeThresholds) {
            if (bufferedDurationUs >= this.minBufferUs && (bufferedDurationUs > this.maxBufferUs || !this.isBuffering || targetBufferSizeReached)) {
                z = false;
            }
            this.isBuffering = z;
        } else {
            if (targetBufferSizeReached || (bufferedDurationUs >= this.minBufferUs && (bufferedDurationUs > this.maxBufferUs || !this.isBuffering))) {
                z = false;
            }
            this.isBuffering = z;
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
}
