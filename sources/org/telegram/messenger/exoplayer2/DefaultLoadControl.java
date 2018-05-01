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

    public long getBackBufferDurationUs() {
        return 0;
    }

    public boolean retainBackBufferFromKeyframe() {
        return false;
    }

    public DefaultLoadControl() {
        this(new DefaultAllocator(true, C0542C.DEFAULT_BUFFER_SEGMENT_SIZE));
    }

    public DefaultLoadControl(DefaultAllocator defaultAllocator) {
        this(defaultAllocator, DEFAULT_MIN_BUFFER_MS, DEFAULT_MAX_BUFFER_MS, DEFAULT_BUFFER_FOR_PLAYBACK_MS, DEFAULT_BUFFER_FOR_PLAYBACK_AFTER_REBUFFER_MS, -1, true);
    }

    public DefaultLoadControl(DefaultAllocator defaultAllocator, int i, int i2, int i3, int i4, int i5, boolean z) {
        this(defaultAllocator, i, i2, i3, i4, i5, z, null);
    }

    public DefaultLoadControl(DefaultAllocator defaultAllocator, int i, int i2, int i3, int i4, int i5, boolean z, PriorityTaskManager priorityTaskManager) {
        this.allocator = defaultAllocator;
        this.minBufferUs = ((long) i) * 1000;
        this.maxBufferUs = ((long) i2) * 1000;
        this.bufferForPlaybackUs = ((long) i3) * 1000;
        this.bufferForPlaybackAfterRebufferUs = ((long) i4) * 1000;
        this.targetBufferBytesOverwrite = i5;
        this.prioritizeTimeOverSizeThresholds = z;
        this.priorityTaskManager = priorityTaskManager;
    }

    public void onPrepared() {
        reset(false);
    }

    public void onTracksSelected(Renderer[] rendererArr, TrackGroupArray trackGroupArray, TrackSelectionArray trackSelectionArray) {
        this.targetBufferSize = this.targetBufferBytesOverwrite == -1 ? calculateTargetBufferSize(rendererArr, trackSelectionArray) : this.targetBufferBytesOverwrite;
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

    public boolean shouldContinueLoading(long j, float f) {
        boolean z = true;
        f = this.allocator.getTotalBytesAllocated() >= this.targetBufferSize ? Float.MIN_VALUE : 0.0f;
        boolean z2 = this.isBuffering;
        if (this.prioritizeTimeOverSizeThresholds) {
            if (j >= this.minBufferUs) {
                if (j > this.maxBufferUs || this.isBuffering == null || f != null) {
                    z = false;
                }
            }
            this.isBuffering = z;
        } else {
            if (f == null) {
                if (j >= this.minBufferUs) {
                    if (j <= this.maxBufferUs && this.isBuffering != null) {
                    }
                }
                this.isBuffering = z;
            }
            z = false;
            this.isBuffering = z;
        }
        if (!(this.priorityTaskManager == null || this.isBuffering == z2)) {
            if (this.isBuffering != null) {
                this.priorityTaskManager.add(0);
            } else {
                this.priorityTaskManager.remove(0);
            }
        }
        return this.isBuffering;
    }

    public boolean shouldStartPlayback(long j, float f, boolean z) {
        j = Util.getPlayoutDurationForMediaDuration(j, f);
        f = z ? this.bufferForPlaybackAfterRebufferUs : this.bufferForPlaybackUs;
        if (f > 0 && j < f) {
            if (this.prioritizeTimeOverSizeThresholds != null || this.allocator.getTotalBytesAllocated() < this.targetBufferSize) {
                return 0;
            }
        }
        return 1;
    }

    protected int calculateTargetBufferSize(Renderer[] rendererArr, TrackSelectionArray trackSelectionArray) {
        int i = 0;
        int i2 = 0;
        while (i < rendererArr.length) {
            if (trackSelectionArray.get(i) != null) {
                i2 += Util.getDefaultBufferSize(rendererArr[i].getTrackType());
            }
            i++;
        }
        return i2;
    }

    private void reset(boolean z) {
        this.targetBufferSize = 0;
        if (this.priorityTaskManager != null && this.isBuffering) {
            this.priorityTaskManager.remove(0);
        }
        this.isBuffering = false;
        if (z) {
            this.allocator.reset();
        }
    }
}
