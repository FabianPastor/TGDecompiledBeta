package org.webrtc;

import android.view.SurfaceHolder;
import java.util.concurrent.CountDownLatch;
import org.telegram.ui.ActionBar.Theme$$ExternalSyntheticLambda4;
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

    public SurfaceEglRenderer(String name) {
        super(name);
    }

    public void init(EglBase.Context sharedContext, RendererCommon.RendererEvents rendererEvents2, int[] configAttributes, RendererCommon.GlDrawer drawer) {
        ThreadUtils.checkIsOnMainThread();
        this.rendererEvents = rendererEvents2;
        synchronized (this.layoutLock) {
            this.isFirstFrameRendered = false;
            this.rotatedFrameWidth = 0;
            this.rotatedFrameHeight = 0;
            this.frameRotation = 0;
        }
        super.init(sharedContext, configAttributes, drawer);
    }

    public void init(EglBase.Context sharedContext, int[] configAttributes, RendererCommon.GlDrawer drawer) {
        init(sharedContext, (RendererCommon.RendererEvents) null, configAttributes, drawer);
    }

    public void setFpsReduction(float fps) {
        synchronized (this.layoutLock) {
            this.isRenderingPaused = fps == 0.0f;
        }
        super.setFpsReduction(fps);
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

    public void onFrame(VideoFrame frame) {
        updateFrameDimensionsAndReportEvents(frame);
        super.onFrame(frame);
    }

    public void surfaceCreated(SurfaceHolder holder) {
        ThreadUtils.checkIsOnMainThread();
        createEglSurface(holder.getSurface());
    }

    public void surfaceDestroyed(SurfaceHolder holder) {
        ThreadUtils.checkIsOnMainThread();
        CountDownLatch completionLatch = new CountDownLatch(1);
        completionLatch.getClass();
        releaseEglSurface(new Theme$$ExternalSyntheticLambda4(completionLatch), false);
        ThreadUtils.awaitUninterruptibly(completionLatch);
    }

    public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {
        ThreadUtils.checkIsOnMainThread();
        logD("surfaceChanged: format: " + format + " size: " + width + "x" + height);
    }

    /* JADX WARNING: Code restructure failed: missing block: B:23:0x009b, code lost:
        return;
     */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    private void updateFrameDimensionsAndReportEvents(org.webrtc.VideoFrame r6) {
        /*
            r5 = this;
            java.lang.Object r0 = r5.layoutLock
            monitor-enter(r0)
            boolean r1 = r5.isRenderingPaused     // Catch:{ all -> 0x009c }
            if (r1 == 0) goto L_0x0009
            monitor-exit(r0)     // Catch:{ all -> 0x009c }
            return
        L_0x0009:
            boolean r1 = r5.isFirstFrameRendered     // Catch:{ all -> 0x009c }
            if (r1 != 0) goto L_0x001c
            r1 = 1
            r5.isFirstFrameRendered = r1     // Catch:{ all -> 0x009c }
            java.lang.String r1 = "Reporting first rendered frame."
            r5.logD(r1)     // Catch:{ all -> 0x009c }
            org.webrtc.RendererCommon$RendererEvents r1 = r5.rendererEvents     // Catch:{ all -> 0x009c }
            if (r1 == 0) goto L_0x001c
            r1.onFirstFrameRendered()     // Catch:{ all -> 0x009c }
        L_0x001c:
            int r1 = r5.rotatedFrameWidth     // Catch:{ all -> 0x009c }
            int r2 = r6.getRotatedWidth()     // Catch:{ all -> 0x009c }
            if (r1 != r2) goto L_0x0034
            int r1 = r5.rotatedFrameHeight     // Catch:{ all -> 0x009c }
            int r2 = r6.getRotatedHeight()     // Catch:{ all -> 0x009c }
            if (r1 != r2) goto L_0x0034
            int r1 = r5.frameRotation     // Catch:{ all -> 0x009c }
            int r2 = r6.getRotation()     // Catch:{ all -> 0x009c }
            if (r1 == r2) goto L_0x009a
        L_0x0034:
            java.lang.StringBuilder r1 = new java.lang.StringBuilder     // Catch:{ all -> 0x009c }
            r1.<init>()     // Catch:{ all -> 0x009c }
            java.lang.String r2 = "Reporting frame resolution changed to "
            r1.append(r2)     // Catch:{ all -> 0x009c }
            org.webrtc.VideoFrame$Buffer r2 = r6.getBuffer()     // Catch:{ all -> 0x009c }
            int r2 = r2.getWidth()     // Catch:{ all -> 0x009c }
            r1.append(r2)     // Catch:{ all -> 0x009c }
            java.lang.String r2 = "x"
            r1.append(r2)     // Catch:{ all -> 0x009c }
            org.webrtc.VideoFrame$Buffer r2 = r6.getBuffer()     // Catch:{ all -> 0x009c }
            int r2 = r2.getHeight()     // Catch:{ all -> 0x009c }
            r1.append(r2)     // Catch:{ all -> 0x009c }
            java.lang.String r2 = " with rotation "
            r1.append(r2)     // Catch:{ all -> 0x009c }
            int r2 = r6.getRotation()     // Catch:{ all -> 0x009c }
            r1.append(r2)     // Catch:{ all -> 0x009c }
            java.lang.String r1 = r1.toString()     // Catch:{ all -> 0x009c }
            r5.logD(r1)     // Catch:{ all -> 0x009c }
            org.webrtc.RendererCommon$RendererEvents r1 = r5.rendererEvents     // Catch:{ all -> 0x009c }
            if (r1 == 0) goto L_0x0088
            org.webrtc.VideoFrame$Buffer r2 = r6.getBuffer()     // Catch:{ all -> 0x009c }
            int r2 = r2.getWidth()     // Catch:{ all -> 0x009c }
            org.webrtc.VideoFrame$Buffer r3 = r6.getBuffer()     // Catch:{ all -> 0x009c }
            int r3 = r3.getHeight()     // Catch:{ all -> 0x009c }
            int r4 = r6.getRotation()     // Catch:{ all -> 0x009c }
            r1.onFrameResolutionChanged(r2, r3, r4)     // Catch:{ all -> 0x009c }
        L_0x0088:
            int r1 = r6.getRotatedWidth()     // Catch:{ all -> 0x009c }
            r5.rotatedFrameWidth = r1     // Catch:{ all -> 0x009c }
            int r1 = r6.getRotatedHeight()     // Catch:{ all -> 0x009c }
            r5.rotatedFrameHeight = r1     // Catch:{ all -> 0x009c }
            int r1 = r6.getRotation()     // Catch:{ all -> 0x009c }
            r5.frameRotation = r1     // Catch:{ all -> 0x009c }
        L_0x009a:
            monitor-exit(r0)     // Catch:{ all -> 0x009c }
            return
        L_0x009c:
            r1 = move-exception
            monitor-exit(r0)     // Catch:{ all -> 0x009c }
            throw r1
        */
        throw new UnsupportedOperationException("Method not decompiled: org.webrtc.SurfaceEglRenderer.updateFrameDimensionsAndReportEvents(org.webrtc.VideoFrame):void");
    }

    private void logD(String string) {
        Logging.d("SurfaceEglRenderer", this.name + ": " + string);
    }
}
