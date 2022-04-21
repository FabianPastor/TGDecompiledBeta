package org.webrtc;

import android.content.Context;
import android.content.Intent;
import android.hardware.display.VirtualDisplay;
import android.media.projection.MediaProjection;
import android.media.projection.MediaProjectionManager;
import android.os.Handler;
import android.view.Surface;
import org.telegram.messenger.FileLog;
import org.webrtc.VideoSink;

public class ScreenCapturerAndroid implements VideoCapturer, VideoSink {
    private static final int DISPLAY_FLAGS = 3;
    private static final int VIRTUAL_DISPLAY_DPI = 400;
    private CapturerObserver capturerObserver;
    private int height;
    private boolean isDisposed;
    private MediaProjection mediaProjection;
    private final MediaProjection.Callback mediaProjectionCallback;
    private MediaProjectionManager mediaProjectionManager;
    private final Intent mediaProjectionPermissionResultData;
    private long numCapturedFrames;
    private SurfaceTextureHelper surfaceTextureHelper;
    private VirtualDisplay virtualDisplay;
    private int width;

    public /* synthetic */ void setParentSink(VideoSink videoSink) {
        VideoSink.CC.$default$setParentSink(this, videoSink);
    }

    public ScreenCapturerAndroid(Intent mediaProjectionPermissionResultData2, MediaProjection.Callback mediaProjectionCallback2) {
        this.mediaProjectionPermissionResultData = mediaProjectionPermissionResultData2;
        this.mediaProjectionCallback = mediaProjectionCallback2;
    }

    private void checkNotDisposed() {
        if (this.isDisposed) {
            throw new RuntimeException("capturer is disposed.");
        }
    }

    public MediaProjection getMediaProjection() {
        return this.mediaProjection;
    }

    public synchronized void initialize(SurfaceTextureHelper surfaceTextureHelper2, Context applicationContext, CapturerObserver capturerObserver2) {
        checkNotDisposed();
        if (capturerObserver2 != null) {
            this.capturerObserver = capturerObserver2;
            if (surfaceTextureHelper2 != null) {
                this.surfaceTextureHelper = surfaceTextureHelper2;
                this.mediaProjectionManager = (MediaProjectionManager) applicationContext.getSystemService("media_projection");
            } else {
                throw new RuntimeException("surfaceTextureHelper not set.");
            }
        } else {
            throw new RuntimeException("capturerObserver not set.");
        }
    }

    /* JADX WARNING: Code restructure failed: missing block: B:14:0x0042, code lost:
        return;
     */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public synchronized void startCapture(int r4, int r5, int r6) {
        /*
            r3 = this;
            monitor-enter(r3)
            android.media.projection.MediaProjection r0 = r3.mediaProjection     // Catch:{ all -> 0x0043 }
            if (r0 != 0) goto L_0x0041
            android.media.projection.MediaProjectionManager r0 = r3.mediaProjectionManager     // Catch:{ all -> 0x0043 }
            if (r0 != 0) goto L_0x000a
            goto L_0x0041
        L_0x000a:
            r3.checkNotDisposed()     // Catch:{ all -> 0x0036 }
            r3.width = r4     // Catch:{ all -> 0x0036 }
            r3.height = r5     // Catch:{ all -> 0x0036 }
            android.media.projection.MediaProjectionManager r0 = r3.mediaProjectionManager     // Catch:{ all -> 0x0036 }
            r1 = -1
            android.content.Intent r2 = r3.mediaProjectionPermissionResultData     // Catch:{ all -> 0x0036 }
            android.media.projection.MediaProjection r0 = r0.getMediaProjection(r1, r2)     // Catch:{ all -> 0x0036 }
            r3.mediaProjection = r0     // Catch:{ all -> 0x0036 }
            android.media.projection.MediaProjection$Callback r1 = r3.mediaProjectionCallback     // Catch:{ all -> 0x0036 }
            org.webrtc.SurfaceTextureHelper r2 = r3.surfaceTextureHelper     // Catch:{ all -> 0x0036 }
            android.os.Handler r2 = r2.getHandler()     // Catch:{ all -> 0x0036 }
            r0.registerCallback(r1, r2)     // Catch:{ all -> 0x0036 }
            r3.createVirtualDisplay()     // Catch:{ all -> 0x0036 }
            org.webrtc.CapturerObserver r0 = r3.capturerObserver     // Catch:{ all -> 0x0036 }
            r1 = 1
            r0.onCapturerStarted(r1)     // Catch:{ all -> 0x0036 }
            org.webrtc.SurfaceTextureHelper r0 = r3.surfaceTextureHelper     // Catch:{ all -> 0x0036 }
            r0.startListening(r3)     // Catch:{ all -> 0x0036 }
            goto L_0x003f
        L_0x0036:
            r0 = move-exception
            android.media.projection.MediaProjection$Callback r1 = r3.mediaProjectionCallback     // Catch:{ all -> 0x0043 }
            r1.onStop()     // Catch:{ all -> 0x0043 }
            org.telegram.messenger.FileLog.e((java.lang.Throwable) r0)     // Catch:{ all -> 0x0043 }
        L_0x003f:
            monitor-exit(r3)
            return
        L_0x0041:
            monitor-exit(r3)
            return
        L_0x0043:
            r4 = move-exception
            monitor-exit(r3)
            throw r4
        */
        throw new UnsupportedOperationException("Method not decompiled: org.webrtc.ScreenCapturerAndroid.startCapture(int, int, int):void");
    }

    public synchronized void stopCapture() {
        checkNotDisposed();
        ThreadUtils.invokeAtFrontUninterruptibly(this.surfaceTextureHelper.getHandler(), (Runnable) new ScreenCapturerAndroid$$ExternalSyntheticLambda1(this));
    }

    /* renamed from: lambda$stopCapture$0$org-webrtc-ScreenCapturerAndroid  reason: not valid java name */
    public /* synthetic */ void m4624lambda$stopCapture$0$orgwebrtcScreenCapturerAndroid() {
        this.surfaceTextureHelper.stopListening();
        this.capturerObserver.onCapturerStopped();
        VirtualDisplay virtualDisplay2 = this.virtualDisplay;
        if (virtualDisplay2 != null) {
            virtualDisplay2.release();
            this.virtualDisplay = null;
        }
        MediaProjection mediaProjection2 = this.mediaProjection;
        if (mediaProjection2 != null) {
            mediaProjection2.unregisterCallback(this.mediaProjectionCallback);
            this.mediaProjection.stop();
            this.mediaProjection = null;
        }
    }

    public synchronized void dispose() {
        this.isDisposed = true;
    }

    public synchronized void changeCaptureFormat(int width2, int height2, int ignoredFramerate) {
        checkNotDisposed();
        this.width = width2;
        this.height = height2;
        if (this.virtualDisplay != null) {
            ThreadUtils.invokeAtFrontUninterruptibly(this.surfaceTextureHelper.getHandler(), (Runnable) new ScreenCapturerAndroid$$ExternalSyntheticLambda0(this));
        }
    }

    /* renamed from: lambda$changeCaptureFormat$1$org-webrtc-ScreenCapturerAndroid  reason: not valid java name */
    public /* synthetic */ void m4623lambda$changeCaptureFormat$1$orgwebrtcScreenCapturerAndroid() {
        this.virtualDisplay.release();
        createVirtualDisplay();
    }

    private void createVirtualDisplay() {
        this.surfaceTextureHelper.setTextureSize(this.width, this.height);
        try {
            this.virtualDisplay = this.mediaProjection.createVirtualDisplay("WebRTC_ScreenCapture", this.width, this.height, 400, 3, new Surface(this.surfaceTextureHelper.getSurfaceTexture()), (VirtualDisplay.Callback) null, (Handler) null);
        } catch (Throwable e) {
            FileLog.e(e);
        }
    }

    public void onFrame(VideoFrame frame) {
        this.numCapturedFrames++;
        this.capturerObserver.onFrameCaptured(frame);
    }

    public boolean isScreencast() {
        return true;
    }

    public long getNumCapturedFrames() {
        return this.numCapturedFrames;
    }
}
