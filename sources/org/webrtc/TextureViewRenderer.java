package org.webrtc;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Point;
import android.graphics.SurfaceTexture;
import android.os.Looper;
import android.view.TextureView;
import java.util.concurrent.CountDownLatch;
import org.webrtc.EglBase;
import org.webrtc.EglRenderer;
import org.webrtc.RendererCommon;

public class TextureViewRenderer extends TextureView implements TextureView.SurfaceTextureListener, VideoSink, RendererCommon.RendererEvents {
    private static final String TAG = "TextureViewRenderer";
    private final TextureEglRenderer eglRenderer;
    private boolean enableFixedSize;
    private RendererCommon.RendererEvents rendererEvents;
    private final String resourceName;
    private int rotatedFrameHeight;
    private int rotatedFrameWidth;
    private int surfaceHeight;
    private int surfaceWidth;
    private final RendererCommon.VideoLayoutMeasure videoLayoutMeasure = new RendererCommon.VideoLayoutMeasure();

    public void setFirstFrameRendered() {
    }

    public static class TextureEglRenderer extends EglRenderer implements TextureView.SurfaceTextureListener {
        private static final String TAG = "TextureEglRenderer";
        private int frameRotation;
        /* access modifiers changed from: private */
        public boolean isFirstFrameRendered;
        private boolean isRenderingPaused;
        private final Object layoutLock = new Object();
        private RendererCommon.RendererEvents rendererEvents;
        private int rotatedFrameHeight;
        private int rotatedFrameWidth;

        public void onSurfaceTextureUpdated(SurfaceTexture surfaceTexture) {
        }

        public TextureEglRenderer(String str) {
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

        public void onSurfaceTextureAvailable(SurfaceTexture surfaceTexture, int i, int i2) {
            ThreadUtils.checkIsOnMainThread();
            createEglSurface(surfaceTexture);
        }

        public void onSurfaceTextureSizeChanged(SurfaceTexture surfaceTexture, int i, int i2) {
            ThreadUtils.checkIsOnMainThread();
            logD("surfaceChanged: size: " + i + "x" + i2);
        }

        public boolean onSurfaceTextureDestroyed(SurfaceTexture surfaceTexture) {
            ThreadUtils.checkIsOnMainThread();
            CountDownLatch countDownLatch = new CountDownLatch(1);
            countDownLatch.getClass();
            releaseEglSurface(new Runnable(countDownLatch) {
                public final /* synthetic */ CountDownLatch f$0;

                {
                    this.f$0 = r1;
                }

                public final void run() {
                    this.f$0.countDown();
                }
            });
            ThreadUtils.awaitUninterruptibly(countDownLatch);
            return true;
        }

        /* JADX WARNING: Code restructure failed: missing block: B:23:0x009e, code lost:
            return;
         */
        /* Code decompiled incorrectly, please refer to instructions dump. */
        private void updateFrameDimensionsAndReportEvents(org.webrtc.VideoFrame r6) {
            /*
                r5 = this;
                java.lang.Object r0 = r5.layoutLock
                monitor-enter(r0)
                boolean r1 = r5.isRenderingPaused     // Catch:{ all -> 0x009f }
                if (r1 == 0) goto L_0x0009
                monitor-exit(r0)     // Catch:{ all -> 0x009f }
                return
            L_0x0009:
                boolean r1 = r5.isFirstFrameRendered     // Catch:{ all -> 0x009f }
                if (r1 != 0) goto L_0x001e
                r1 = 1
                r5.isFirstFrameRendered = r1     // Catch:{ all -> 0x009f }
                java.lang.String r1 = "Reporting first rendered frame."
                r5.logD(r1)     // Catch:{ all -> 0x009f }
                org.webrtc.RendererCommon$RendererEvents r1 = r5.rendererEvents     // Catch:{ all -> 0x009f }
                if (r1 == 0) goto L_0x001e
                org.webrtc.RendererCommon$RendererEvents r1 = r5.rendererEvents     // Catch:{ all -> 0x009f }
                r1.onFirstFrameRendered()     // Catch:{ all -> 0x009f }
            L_0x001e:
                int r1 = r5.rotatedFrameWidth     // Catch:{ all -> 0x009f }
                int r2 = r6.getRotatedWidth()     // Catch:{ all -> 0x009f }
                if (r1 != r2) goto L_0x0036
                int r1 = r5.rotatedFrameHeight     // Catch:{ all -> 0x009f }
                int r2 = r6.getRotatedHeight()     // Catch:{ all -> 0x009f }
                if (r1 != r2) goto L_0x0036
                int r1 = r5.frameRotation     // Catch:{ all -> 0x009f }
                int r2 = r6.getRotation()     // Catch:{ all -> 0x009f }
                if (r1 == r2) goto L_0x009d
            L_0x0036:
                java.lang.StringBuilder r1 = new java.lang.StringBuilder     // Catch:{ all -> 0x009f }
                r1.<init>()     // Catch:{ all -> 0x009f }
                java.lang.String r2 = "Reporting frame resolution changed to "
                r1.append(r2)     // Catch:{ all -> 0x009f }
                org.webrtc.VideoFrame$Buffer r2 = r6.getBuffer()     // Catch:{ all -> 0x009f }
                int r2 = r2.getWidth()     // Catch:{ all -> 0x009f }
                r1.append(r2)     // Catch:{ all -> 0x009f }
                java.lang.String r2 = "x"
                r1.append(r2)     // Catch:{ all -> 0x009f }
                org.webrtc.VideoFrame$Buffer r2 = r6.getBuffer()     // Catch:{ all -> 0x009f }
                int r2 = r2.getHeight()     // Catch:{ all -> 0x009f }
                r1.append(r2)     // Catch:{ all -> 0x009f }
                java.lang.String r2 = " with rotation "
                r1.append(r2)     // Catch:{ all -> 0x009f }
                int r2 = r6.getRotation()     // Catch:{ all -> 0x009f }
                r1.append(r2)     // Catch:{ all -> 0x009f }
                java.lang.String r1 = r1.toString()     // Catch:{ all -> 0x009f }
                r5.logD(r1)     // Catch:{ all -> 0x009f }
                org.webrtc.RendererCommon$RendererEvents r1 = r5.rendererEvents     // Catch:{ all -> 0x009f }
                if (r1 == 0) goto L_0x008b
                org.webrtc.RendererCommon$RendererEvents r1 = r5.rendererEvents     // Catch:{ all -> 0x009f }
                org.webrtc.VideoFrame$Buffer r2 = r6.getBuffer()     // Catch:{ all -> 0x009f }
                int r2 = r2.getWidth()     // Catch:{ all -> 0x009f }
                org.webrtc.VideoFrame$Buffer r3 = r6.getBuffer()     // Catch:{ all -> 0x009f }
                int r3 = r3.getHeight()     // Catch:{ all -> 0x009f }
                int r4 = r6.getRotation()     // Catch:{ all -> 0x009f }
                r1.onFrameResolutionChanged(r2, r3, r4)     // Catch:{ all -> 0x009f }
            L_0x008b:
                int r1 = r6.getRotatedWidth()     // Catch:{ all -> 0x009f }
                r5.rotatedFrameWidth = r1     // Catch:{ all -> 0x009f }
                int r1 = r6.getRotatedHeight()     // Catch:{ all -> 0x009f }
                r5.rotatedFrameHeight = r1     // Catch:{ all -> 0x009f }
                int r6 = r6.getRotation()     // Catch:{ all -> 0x009f }
                r5.frameRotation = r6     // Catch:{ all -> 0x009f }
            L_0x009d:
                monitor-exit(r0)     // Catch:{ all -> 0x009f }
                return
            L_0x009f:
                r6 = move-exception
                monitor-exit(r0)     // Catch:{ all -> 0x009f }
                throw r6
            */
            throw new UnsupportedOperationException("Method not decompiled: org.webrtc.TextureViewRenderer.TextureEglRenderer.updateFrameDimensionsAndReportEvents(org.webrtc.VideoFrame):void");
        }

        private void logD(String str) {
            Logging.d("TextureEglRenderer", this.name + ": " + str);
        }
    }

    public TextureViewRenderer(Context context) {
        super(context);
        String resourceName2 = getResourceName();
        this.resourceName = resourceName2;
        this.eglRenderer = new TextureEglRenderer(resourceName2);
        setSurfaceTextureListener(this);
    }

    public void init(EglBase.Context context, RendererCommon.RendererEvents rendererEvents2) {
        init(context, rendererEvents2, EglBase.CONFIG_PLAIN, new GlRectDrawer());
    }

    public void init(EglBase.Context context, RendererCommon.RendererEvents rendererEvents2, int[] iArr, RendererCommon.GlDrawer glDrawer) {
        ThreadUtils.checkIsOnMainThread();
        this.rendererEvents = rendererEvents2;
        this.rotatedFrameWidth = 0;
        this.rotatedFrameHeight = 0;
        this.eglRenderer.init(context, this, iArr, glDrawer);
    }

    public void release() {
        this.eglRenderer.release();
    }

    public void addFrameListener(EglRenderer.FrameListener frameListener, float f, RendererCommon.GlDrawer glDrawer) {
        this.eglRenderer.addFrameListener(frameListener, f, glDrawer);
    }

    public void addFrameListener(EglRenderer.FrameListener frameListener, float f) {
        this.eglRenderer.addFrameListener(frameListener, f);
    }

    public void removeFrameListener(EglRenderer.FrameListener frameListener) {
        this.eglRenderer.removeFrameListener(frameListener);
    }

    public void setEnableHardwareScaler(boolean z) {
        ThreadUtils.checkIsOnMainThread();
        this.enableFixedSize = z;
        updateSurfaceSize();
    }

    public void setMirror(boolean z) {
        this.eglRenderer.setMirror(z);
    }

    public void setScalingType(RendererCommon.ScalingType scalingType) {
        ThreadUtils.checkIsOnMainThread();
        this.videoLayoutMeasure.setScalingType(scalingType);
        requestLayout();
    }

    public void setScalingType(RendererCommon.ScalingType scalingType, RendererCommon.ScalingType scalingType2) {
        ThreadUtils.checkIsOnMainThread();
        this.videoLayoutMeasure.setScalingType(scalingType, scalingType2);
        requestLayout();
    }

    public void setFpsReduction(float f) {
        this.eglRenderer.setFpsReduction(f);
    }

    public void disableFpsReduction() {
        this.eglRenderer.disableFpsReduction();
    }

    public void pauseVideo() {
        this.eglRenderer.pauseVideo();
    }

    public void onFrame(VideoFrame videoFrame) {
        this.eglRenderer.onFrame(videoFrame);
    }

    /* access modifiers changed from: protected */
    public void onMeasure(int i, int i2) {
        ThreadUtils.checkIsOnMainThread();
        Point measure = this.videoLayoutMeasure.measure(i, i2, this.rotatedFrameWidth, this.rotatedFrameHeight);
        setMeasuredDimension(measure.x, measure.y);
        logD("onMeasure(). New size: " + measure.x + "x" + measure.y);
    }

    /* access modifiers changed from: protected */
    public void onLayout(boolean z, int i, int i2, int i3, int i4) {
        ThreadUtils.checkIsOnMainThread();
        this.eglRenderer.setLayoutAspectRatio(((float) (i3 - i)) / ((float) (i4 - i2)));
        updateSurfaceSize();
    }

    private void updateSurfaceSize() {
        ThreadUtils.checkIsOnMainThread();
        if (!this.enableFixedSize || this.rotatedFrameWidth == 0 || this.rotatedFrameHeight == 0 || getWidth() == 0 || getHeight() == 0) {
            this.surfaceHeight = 0;
            this.surfaceWidth = 0;
            return;
        }
        float width = ((float) getWidth()) / ((float) getHeight());
        int i = this.rotatedFrameWidth;
        int i2 = this.rotatedFrameHeight;
        if (((float) i) / ((float) i2) > width) {
            i = (int) (((float) i2) * width);
        } else {
            i2 = (int) (((float) i) / width);
        }
        int min = Math.min(getWidth(), i);
        int min2 = Math.min(getHeight(), i2);
        logD("updateSurfaceSize. Layout size: " + getWidth() + "x" + getHeight() + ", frame size: " + this.rotatedFrameWidth + "x" + this.rotatedFrameHeight + ", requested surface size: " + min + "x" + min2 + ", old surface size: " + this.surfaceWidth + "x" + this.surfaceHeight);
        if (min != this.surfaceWidth || min2 != this.surfaceHeight) {
            this.surfaceWidth = min;
            this.surfaceHeight = min2;
        }
    }

    public void onSurfaceTextureAvailable(SurfaceTexture surfaceTexture, int i, int i2) {
        ThreadUtils.checkIsOnMainThread();
        this.surfaceHeight = 0;
        this.surfaceWidth = 0;
        updateSurfaceSize();
        this.eglRenderer.onSurfaceTextureAvailable(surfaceTexture, i, i2);
    }

    public void onSurfaceTextureSizeChanged(SurfaceTexture surfaceTexture, int i, int i2) {
        this.surfaceWidth = i;
        this.surfaceHeight = i2;
        this.eglRenderer.onSurfaceTextureSizeChanged(surfaceTexture, i, i2);
    }

    public boolean onSurfaceTextureDestroyed(SurfaceTexture surfaceTexture) {
        this.eglRenderer.onSurfaceTextureDestroyed(surfaceTexture);
        return true;
    }

    public void onSurfaceTextureUpdated(SurfaceTexture surfaceTexture) {
        this.eglRenderer.onSurfaceTextureUpdated(surfaceTexture);
    }

    private String getResourceName() {
        try {
            return getResources().getResourceEntryName(getId());
        } catch (Resources.NotFoundException unused) {
            return "";
        }
    }

    public void clearImage() {
        this.eglRenderer.clearImage();
    }

    public void onFirstFrameRendered() {
        RendererCommon.RendererEvents rendererEvents2 = this.rendererEvents;
        if (rendererEvents2 != null) {
            rendererEvents2.onFirstFrameRendered();
        }
    }

    public boolean isFirstFrameRendered() {
        return this.eglRenderer.isFirstFrameRendered;
    }

    public void onFrameResolutionChanged(int i, int i2, int i3) {
        RendererCommon.RendererEvents rendererEvents2 = this.rendererEvents;
        if (rendererEvents2 != null) {
            rendererEvents2.onFrameResolutionChanged(i, i2, i3);
        }
        int i4 = (i3 == 0 || i3 == 180) ? i : i2;
        if (i3 == 0 || i3 == 180) {
            i = i2;
        }
        postOrRun(new Runnable(i4, i) {
            public final /* synthetic */ int f$1;
            public final /* synthetic */ int f$2;

            {
                this.f$1 = r2;
                this.f$2 = r3;
            }

            public final void run() {
                TextureViewRenderer.this.lambda$onFrameResolutionChanged$0$TextureViewRenderer(this.f$1, this.f$2);
            }
        });
    }

    public /* synthetic */ void lambda$onFrameResolutionChanged$0$TextureViewRenderer(int i, int i2) {
        this.rotatedFrameWidth = i;
        this.rotatedFrameHeight = i2;
        updateSurfaceSize();
        requestLayout();
    }

    private void postOrRun(Runnable runnable) {
        if (Thread.currentThread() == Looper.getMainLooper().getThread()) {
            runnable.run();
        } else {
            post(runnable);
        }
    }

    private void logD(String str) {
        Logging.d("TextureViewRenderer", this.resourceName + ": " + str);
    }
}
