package org.webrtc;

import android.annotation.TargetApi;
import android.content.Context;
import android.content.Intent;
import android.hardware.display.VirtualDisplay;
import android.media.projection.MediaProjection;
import android.media.projection.MediaProjectionManager;
import android.os.Handler;
import android.view.Surface;
import org.telegram.messenger.FileLog;
import org.webrtc.VideoSink;

@TargetApi(21)
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

    public boolean isScreencast() {
        return true;
    }

    public /* synthetic */ void setParentSink(VideoSink videoSink) {
        VideoSink.CC.$default$setParentSink(this, videoSink);
    }

    public ScreenCapturerAndroid(Intent intent, MediaProjection.Callback callback) {
        this.mediaProjectionPermissionResultData = intent;
        this.mediaProjectionCallback = callback;
    }

    private void checkNotDisposed() {
        if (this.isDisposed) {
            throw new RuntimeException("capturer is disposed.");
        }
    }

    public MediaProjection getMediaProjection() {
        return this.mediaProjection;
    }

    public synchronized void initialize(SurfaceTextureHelper surfaceTextureHelper2, Context context, CapturerObserver capturerObserver2) {
        checkNotDisposed();
        if (capturerObserver2 != null) {
            this.capturerObserver = capturerObserver2;
            if (surfaceTextureHelper2 != null) {
                this.surfaceTextureHelper = surfaceTextureHelper2;
                this.mediaProjectionManager = (MediaProjectionManager) context.getSystemService("media_projection");
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
    public synchronized void startCapture(int r1, int r2, int r3) {
        /*
            r0 = this;
            monitor-enter(r0)
            android.media.projection.MediaProjection r3 = r0.mediaProjection     // Catch:{ all -> 0x0043 }
            if (r3 != 0) goto L_0x0041
            android.media.projection.MediaProjectionManager r3 = r0.mediaProjectionManager     // Catch:{ all -> 0x0043 }
            if (r3 != 0) goto L_0x000a
            goto L_0x0041
        L_0x000a:
            r0.checkNotDisposed()     // Catch:{ all -> 0x0036 }
            r0.width = r1     // Catch:{ all -> 0x0036 }
            r0.height = r2     // Catch:{ all -> 0x0036 }
            android.media.projection.MediaProjectionManager r1 = r0.mediaProjectionManager     // Catch:{ all -> 0x0036 }
            r2 = -1
            android.content.Intent r3 = r0.mediaProjectionPermissionResultData     // Catch:{ all -> 0x0036 }
            android.media.projection.MediaProjection r1 = r1.getMediaProjection(r2, r3)     // Catch:{ all -> 0x0036 }
            r0.mediaProjection = r1     // Catch:{ all -> 0x0036 }
            android.media.projection.MediaProjection$Callback r2 = r0.mediaProjectionCallback     // Catch:{ all -> 0x0036 }
            org.webrtc.SurfaceTextureHelper r3 = r0.surfaceTextureHelper     // Catch:{ all -> 0x0036 }
            android.os.Handler r3 = r3.getHandler()     // Catch:{ all -> 0x0036 }
            r1.registerCallback(r2, r3)     // Catch:{ all -> 0x0036 }
            r0.createVirtualDisplay()     // Catch:{ all -> 0x0036 }
            org.webrtc.CapturerObserver r1 = r0.capturerObserver     // Catch:{ all -> 0x0036 }
            r2 = 1
            r1.onCapturerStarted(r2)     // Catch:{ all -> 0x0036 }
            org.webrtc.SurfaceTextureHelper r1 = r0.surfaceTextureHelper     // Catch:{ all -> 0x0036 }
            r1.startListening(r0)     // Catch:{ all -> 0x0036 }
            goto L_0x003f
        L_0x0036:
            r1 = move-exception
            android.media.projection.MediaProjection$Callback r2 = r0.mediaProjectionCallback     // Catch:{ all -> 0x0043 }
            r2.onStop()     // Catch:{ all -> 0x0043 }
            org.telegram.messenger.FileLog.e((java.lang.Throwable) r1)     // Catch:{ all -> 0x0043 }
        L_0x003f:
            monitor-exit(r0)
            return
        L_0x0041:
            monitor-exit(r0)
            return
        L_0x0043:
            r1 = move-exception
            monitor-exit(r0)
            throw r1
        */
        throw new UnsupportedOperationException("Method not decompiled: org.webrtc.ScreenCapturerAndroid.startCapture(int, int, int):void");
    }

    public synchronized void stopCapture() {
        checkNotDisposed();
        ThreadUtils.invokeAtFrontUninterruptibly(this.surfaceTextureHelper.getHandler(), (Runnable) new Runnable() {
            public final void run() {
                ScreenCapturerAndroid.this.lambda$stopCapture$0$ScreenCapturerAndroid();
            }
        });
    }

    /* access modifiers changed from: private */
    /* renamed from: lambda$stopCapture$0 */
    public /* synthetic */ void lambda$stopCapture$0$ScreenCapturerAndroid() {
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

    public synchronized void changeCaptureFormat(int i, int i2, int i3) {
        checkNotDisposed();
        this.width = i;
        this.height = i2;
        if (this.virtualDisplay != null) {
            ThreadUtils.invokeAtFrontUninterruptibly(this.surfaceTextureHelper.getHandler(), (Runnable) new Runnable() {
                public final void run() {
                    ScreenCapturerAndroid.this.lambda$changeCaptureFormat$1$ScreenCapturerAndroid();
                }
            });
        }
    }

    /* access modifiers changed from: private */
    /* renamed from: lambda$changeCaptureFormat$1 */
    public /* synthetic */ void lambda$changeCaptureFormat$1$ScreenCapturerAndroid() {
        this.virtualDisplay.release();
        createVirtualDisplay();
    }

    private void createVirtualDisplay() {
        this.surfaceTextureHelper.setTextureSize(this.width, this.height);
        try {
            this.virtualDisplay = this.mediaProjection.createVirtualDisplay("WebRTC_ScreenCapture", this.width, this.height, 400, 3, new Surface(this.surfaceTextureHelper.getSurfaceTexture()), (VirtualDisplay.Callback) null, (Handler) null);
        } catch (Throwable th) {
            FileLog.e(th);
        }
    }

    public void onFrame(VideoFrame videoFrame) {
        this.numCapturedFrames++;
        this.capturerObserver.onFrameCaptured(videoFrame);
    }

    public long getNumCapturedFrames() {
        return this.numCapturedFrames;
    }
}
