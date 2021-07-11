package org.webrtc;

import android.view.SurfaceHolder;
import java.util.concurrent.CountDownLatch;
import org.webrtc.EglBase;
import org.webrtc.RendererCommon;

public class SurfaceEglRenderer extends EglRenderer implements SurfaceHolder.Callback {
    private static final String TAG = "SurfaceEglRenderer";
    private int frameRotation;
    private boolean isFirstFrameRendered;
    private boolean isRenderingPaused;
    private final Object layoutLock = new Object();
    private RendererCommon.RendererEvents rendererEvents;
    private int rotatedFrameHeight;
    private int rotatedFrameWidth;

    public SurfaceEglRenderer(String str) {
        super(str);
    }

    public void init(EglBase.Context context, RendererCommon.RendererEvents rendererEvents2, int[] iArr, RendererCommon.GlDrawer glDrawer) {
        ThreadUtils.checkIsOnMainThread();
        this.rendererEvents = rendererEvents2;
        synchronized (this.layoutLock) {
            this.isFirstFrameRendered = false;
            this.rotatedFrameWidth = 0;
            this.rotatedFrameHeight = 0;
            this.frameRotation = 0;
        }
        super.init(context, iArr, glDrawer);
    }

    public void init(EglBase.Context context, int[] iArr, RendererCommon.GlDrawer glDrawer) {
        init(context, (RendererCommon.RendererEvents) null, iArr, glDrawer);
    }

    public void setFpsReduction(float f) {
        synchronized (this.layoutLock) {
            this.isRenderingPaused = f == 0.0f;
        }
        super.setFpsReduction(f);
    }

    public void disableFpsReduction() {
        synchronized (this.layoutLock) {
            this.isRenderingPaused = false;
        }
        super.disableFpsReduction();
    }

    public void pauseVideo() {
        synchronized (this.layoutLock) {
            this.isRenderingPaused = true;
        }
        super.pauseVideo();
    }

    public void onFrame(VideoFrame videoFrame) {
        updateFrameDimensionsAndReportEvents(videoFrame);
        super.onFrame(videoFrame);
    }

    public void surfaceCreated(SurfaceHolder surfaceHolder) {
        ThreadUtils.checkIsOnMainThread();
        createEglSurface(surfaceHolder.getSurface());
    }

    public void surfaceDestroyed(SurfaceHolder surfaceHolder) {
        ThreadUtils.checkIsOnMainThread();
        CountDownLatch countDownLatch = new CountDownLatch(1);
        releaseEglSurface(new Runnable(countDownLatch) {
            public final /* synthetic */ CountDownLatch f$0;

            {
                this.f$0 = r1;
            }

            public final void run() {
                this.f$0.countDown();
            }
        }, false);
        ThreadUtils.awaitUninterruptibly(countDownLatch);
    }

    public void surfaceChanged(SurfaceHolder surfaceHolder, int i, int i2, int i3) {
        ThreadUtils.checkIsOnMainThread();
        logD("surfaceChanged: format: " + i + " size: " + i2 + "x" + i3);
    }

    /* JADX WARNING: Code restructure failed: missing block: B:23:0x009a, code lost:
        return;
     */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    private void updateFrameDimensionsAndReportEvents(org.webrtc.VideoFrame r6) {
        /*
            r5 = this;
            java.lang.Object r0 = r5.layoutLock
            monitor-enter(r0)
            boolean r1 = r5.isRenderingPaused     // Catch:{ all -> 0x009b }
            if (r1 == 0) goto L_0x0009
            monitor-exit(r0)     // Catch:{ all -> 0x009b }
            return
        L_0x0009:
            boolean r1 = r5.isFirstFrameRendered     // Catch:{ all -> 0x009b }
            if (r1 != 0) goto L_0x001c
            r1 = 1
            r5.isFirstFrameRendered = r1     // Catch:{ all -> 0x009b }
            java.lang.String r1 = "Reporting first rendered frame."
            r5.logD(r1)     // Catch:{ all -> 0x009b }
            org.webrtc.RendererCommon$RendererEvents r1 = r5.rendererEvents     // Catch:{ all -> 0x009b }
            if (r1 == 0) goto L_0x001c
            r1.onFirstFrameRendered()     // Catch:{ all -> 0x009b }
        L_0x001c:
            int r1 = r5.rotatedFrameWidth     // Catch:{ all -> 0x009b }
            int r2 = r6.getRotatedWidth()     // Catch:{ all -> 0x009b }
            if (r1 != r2) goto L_0x0034
            int r1 = r5.rotatedFrameHeight     // Catch:{ all -> 0x009b }
            int r2 = r6.getRotatedHeight()     // Catch:{ all -> 0x009b }
            if (r1 != r2) goto L_0x0034
            int r1 = r5.frameRotation     // Catch:{ all -> 0x009b }
            int r2 = r6.getRotation()     // Catch:{ all -> 0x009b }
            if (r1 == r2) goto L_0x0099
        L_0x0034:
            java.lang.StringBuilder r1 = new java.lang.StringBuilder     // Catch:{ all -> 0x009b }
            r1.<init>()     // Catch:{ all -> 0x009b }
            java.lang.String r2 = "Reporting frame resolution changed to "
            r1.append(r2)     // Catch:{ all -> 0x009b }
            org.webrtc.VideoFrame$Buffer r2 = r6.getBuffer()     // Catch:{ all -> 0x009b }
            int r2 = r2.getWidth()     // Catch:{ all -> 0x009b }
            r1.append(r2)     // Catch:{ all -> 0x009b }
            java.lang.String r2 = "x"
            r1.append(r2)     // Catch:{ all -> 0x009b }
            org.webrtc.VideoFrame$Buffer r2 = r6.getBuffer()     // Catch:{ all -> 0x009b }
            int r2 = r2.getHeight()     // Catch:{ all -> 0x009b }
            r1.append(r2)     // Catch:{ all -> 0x009b }
            java.lang.String r2 = " with rotation "
            r1.append(r2)     // Catch:{ all -> 0x009b }
            int r2 = r6.getRotation()     // Catch:{ all -> 0x009b }
            r1.append(r2)     // Catch:{ all -> 0x009b }
            java.lang.String r1 = r1.toString()     // Catch:{ all -> 0x009b }
            r5.logD(r1)     // Catch:{ all -> 0x009b }
            org.webrtc.RendererCommon$RendererEvents r1 = r5.rendererEvents     // Catch:{ all -> 0x009b }
            if (r1 == 0) goto L_0x0087
            org.webrtc.VideoFrame$Buffer r2 = r6.getBuffer()     // Catch:{ all -> 0x009b }
            int r2 = r2.getWidth()     // Catch:{ all -> 0x009b }
            org.webrtc.VideoFrame$Buffer r3 = r6.getBuffer()     // Catch:{ all -> 0x009b }
            int r3 = r3.getHeight()     // Catch:{ all -> 0x009b }
            int r4 = r6.getRotation()     // Catch:{ all -> 0x009b }
            r1.onFrameResolutionChanged(r2, r3, r4)     // Catch:{ all -> 0x009b }
        L_0x0087:
            int r1 = r6.getRotatedWidth()     // Catch:{ all -> 0x009b }
            r5.rotatedFrameWidth = r1     // Catch:{ all -> 0x009b }
            int r1 = r6.getRotatedHeight()     // Catch:{ all -> 0x009b }
            r5.rotatedFrameHeight = r1     // Catch:{ all -> 0x009b }
            int r6 = r6.getRotation()     // Catch:{ all -> 0x009b }
            r5.frameRotation = r6     // Catch:{ all -> 0x009b }
        L_0x0099:
            monitor-exit(r0)     // Catch:{ all -> 0x009b }
            return
        L_0x009b:
            r6 = move-exception
            monitor-exit(r0)     // Catch:{ all -> 0x009b }
            throw r6
        */
        throw new UnsupportedOperationException("Method not decompiled: org.webrtc.SurfaceEglRenderer.updateFrameDimensionsAndReportEvents(org.webrtc.VideoFrame):void");
    }

    private void logD(String str) {
        Logging.d("SurfaceEglRenderer", this.name + ": " + str);
    }
}
