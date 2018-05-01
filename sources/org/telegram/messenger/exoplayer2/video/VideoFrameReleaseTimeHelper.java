package org.telegram.messenger.exoplayer2.video;

import android.annotation.TargetApi;
import android.content.Context;
import android.hardware.display.DisplayManager;
import android.hardware.display.DisplayManager.DisplayListener;
import android.os.Handler;
import android.os.Handler.Callback;
import android.os.HandlerThread;
import android.os.Message;
import android.view.Choreographer;
import android.view.Choreographer.FrameCallback;
import android.view.Display;
import android.view.WindowManager;
import org.telegram.messenger.exoplayer2.C0542C;
import org.telegram.messenger.exoplayer2.util.Util;

@TargetApi(16)
public final class VideoFrameReleaseTimeHelper {
    private static final long CHOREOGRAPHER_SAMPLE_DELAY_MILLIS = 500;
    private static final long MAX_ALLOWED_DRIFT_NS = 20000000;
    private static final int MIN_FRAMES_FOR_ADJUSTMENT = 6;
    private static final long VSYNC_OFFSET_PERCENTAGE = 80;
    private long adjustedLastFrameTimeNs;
    private final DefaultDisplayListener displayListener;
    private long frameCount;
    private boolean haveSync;
    private long lastFramePresentationTimeUs;
    private long pendingAdjustedFrameTimeNs;
    private long syncFramePresentationTimeNs;
    private long syncUnadjustedReleaseTimeNs;
    private long vsyncDurationNs;
    private long vsyncOffsetNs;
    private final VSyncSampler vsyncSampler;
    private final WindowManager windowManager;

    @TargetApi(17)
    private final class DefaultDisplayListener implements DisplayListener {
        private final DisplayManager displayManager;

        public void onDisplayAdded(int i) {
        }

        public void onDisplayRemoved(int i) {
        }

        public DefaultDisplayListener(DisplayManager displayManager) {
            this.displayManager = displayManager;
        }

        public void register() {
            this.displayManager.registerDisplayListener(this, null);
        }

        public void unregister() {
            this.displayManager.unregisterDisplayListener(this);
        }

        public void onDisplayChanged(int i) {
            if (i == 0) {
                VideoFrameReleaseTimeHelper.this.updateDefaultDisplayRefreshRateParams();
            }
        }
    }

    private static final class VSyncSampler implements Callback, FrameCallback {
        private static final int CREATE_CHOREOGRAPHER = 0;
        private static final VSyncSampler INSTANCE = new VSyncSampler();
        private static final int MSG_ADD_OBSERVER = 1;
        private static final int MSG_REMOVE_OBSERVER = 2;
        private Choreographer choreographer;
        private final HandlerThread choreographerOwnerThread = new HandlerThread("ChoreographerOwner:Handler");
        private final Handler handler;
        private int observerCount;
        public volatile long sampledVsyncTimeNs = C0542C.TIME_UNSET;

        public static VSyncSampler getInstance() {
            return INSTANCE;
        }

        private VSyncSampler() {
            this.choreographerOwnerThread.start();
            this.handler = new Handler(this.choreographerOwnerThread.getLooper(), this);
            this.handler.sendEmptyMessage(0);
        }

        public void addObserver() {
            this.handler.sendEmptyMessage(1);
        }

        public void removeObserver() {
            this.handler.sendEmptyMessage(2);
        }

        public void doFrame(long j) {
            this.sampledVsyncTimeNs = j;
            this.choreographer.postFrameCallbackDelayed(this, VideoFrameReleaseTimeHelper.CHOREOGRAPHER_SAMPLE_DELAY_MILLIS);
        }

        public boolean handleMessage(Message message) {
            switch (message.what) {
                case null:
                    createChoreographerInstanceInternal();
                    return true;
                case 1:
                    addObserverInternal();
                    return true;
                case 2:
                    removeObserverInternal();
                    return true;
                default:
                    return null;
            }
        }

        private void createChoreographerInstanceInternal() {
            this.choreographer = Choreographer.getInstance();
        }

        private void addObserverInternal() {
            this.observerCount++;
            if (this.observerCount == 1) {
                this.choreographer.postFrameCallback(this);
            }
        }

        private void removeObserverInternal() {
            this.observerCount--;
            if (this.observerCount == 0) {
                this.choreographer.removeFrameCallback(this);
                this.sampledVsyncTimeNs = C0542C.TIME_UNSET;
            }
        }
    }

    public VideoFrameReleaseTimeHelper() {
        this(null);
    }

    public VideoFrameReleaseTimeHelper(Context context) {
        WindowManager windowManager;
        DefaultDisplayListener defaultDisplayListener = null;
        if (context == null) {
            windowManager = null;
        } else {
            windowManager = (WindowManager) context.getSystemService("window");
        }
        this.windowManager = windowManager;
        if (this.windowManager != null) {
            if (Util.SDK_INT >= 17) {
                defaultDisplayListener = maybeBuildDefaultDisplayListenerV17(context);
            }
            this.displayListener = defaultDisplayListener;
            this.vsyncSampler = VSyncSampler.getInstance();
        } else {
            this.displayListener = null;
            this.vsyncSampler = null;
        }
        this.vsyncDurationNs = C0542C.TIME_UNSET;
        this.vsyncOffsetNs = C0542C.TIME_UNSET;
    }

    public void enable() {
        this.haveSync = false;
        if (this.windowManager != null) {
            this.vsyncSampler.addObserver();
            if (this.displayListener != null) {
                this.displayListener.register();
            }
            updateDefaultDisplayRefreshRateParams();
        }
    }

    public void disable() {
        if (this.windowManager != null) {
            if (this.displayListener != null) {
                this.displayListener.unregister();
            }
            this.vsyncSampler.removeObserver();
        }
    }

    public long adjustReleaseTime(long j, long j2) {
        long j3;
        long j4;
        long j5 = j;
        long j6 = j2;
        long j7 = 1000 * j5;
        if (this.haveSync) {
            if (j5 != r0.lastFramePresentationTimeUs) {
                r0.frameCount++;
                r0.adjustedLastFrameTimeNs = r0.pendingAdjustedFrameTimeNs;
            }
            if (r0.frameCount >= 6) {
                j3 = r0.adjustedLastFrameTimeNs + ((j7 - r0.syncFramePresentationTimeNs) / r0.frameCount);
                if (isDriftTooLarge(j3, j6)) {
                    r0.haveSync = false;
                } else {
                    j4 = (r0.syncUnadjustedReleaseTimeNs + j3) - r0.syncFramePresentationTimeNs;
                    if (!r0.haveSync) {
                        r0.syncFramePresentationTimeNs = j7;
                        r0.syncUnadjustedReleaseTimeNs = j6;
                        r0.frameCount = 0;
                        r0.haveSync = true;
                    }
                    r0.lastFramePresentationTimeUs = j5;
                    r0.pendingAdjustedFrameTimeNs = j3;
                    if (r0.vsyncSampler != null) {
                        if (r0.vsyncDurationNs == C0542C.TIME_UNSET) {
                            j5 = r0.vsyncSampler.sampledVsyncTimeNs;
                            if (j5 != C0542C.TIME_UNSET) {
                                return j4;
                            }
                            return closestVsync(j4, j5, r0.vsyncDurationNs) - r0.vsyncOffsetNs;
                        }
                    }
                    return j4;
                }
            } else if (isDriftTooLarge(j7, j6)) {
                r0.haveSync = false;
            }
        }
        j4 = j6;
        j3 = j7;
        if (r0.haveSync) {
            r0.syncFramePresentationTimeNs = j7;
            r0.syncUnadjustedReleaseTimeNs = j6;
            r0.frameCount = 0;
            r0.haveSync = true;
        }
        r0.lastFramePresentationTimeUs = j5;
        r0.pendingAdjustedFrameTimeNs = j3;
        if (r0.vsyncSampler != null) {
            if (r0.vsyncDurationNs == C0542C.TIME_UNSET) {
                j5 = r0.vsyncSampler.sampledVsyncTimeNs;
                if (j5 != C0542C.TIME_UNSET) {
                    return closestVsync(j4, j5, r0.vsyncDurationNs) - r0.vsyncOffsetNs;
                }
                return j4;
            }
        }
        return j4;
    }

    @TargetApi(17)
    private DefaultDisplayListener maybeBuildDefaultDisplayListenerV17(Context context) {
        DisplayManager displayManager = (DisplayManager) context.getSystemService("display");
        if (displayManager == null) {
            return null;
        }
        return new DefaultDisplayListener(displayManager);
    }

    private void updateDefaultDisplayRefreshRateParams() {
        Display defaultDisplay = this.windowManager.getDefaultDisplay();
        if (defaultDisplay != null) {
            this.vsyncDurationNs = (long) (1.0E9d / ((double) defaultDisplay.getRefreshRate()));
            this.vsyncOffsetNs = (this.vsyncDurationNs * VSYNC_OFFSET_PERCENTAGE) / 100;
        }
    }

    private boolean isDriftTooLarge(long j, long j2) {
        return Math.abs((j2 - this.syncUnadjustedReleaseTimeNs) - (j - this.syncFramePresentationTimeNs)) > MAX_ALLOWED_DRIFT_NS ? 1 : 0;
    }

    private static long closestVsync(long j, long j2, long j3) {
        long j4 = j2 + (((j - j2) / j3) * j3);
        if (j <= j4) {
            j2 = j4;
            j4 -= j3;
        } else {
            j2 = j4 + j3;
        }
        return j2 - j < j - j4 ? j2 : j4;
    }
}
