package org.webrtc;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Point;
import android.graphics.SurfaceTexture;
import android.os.Looper;
import android.view.TextureView;
import android.view.View;
import java.util.concurrent.CountDownLatch;
import org.telegram.messenger.AndroidUtilities;
import org.telegram.messenger.voip.VoIPService;
import org.telegram.ui.ActionBar.Theme$$ExternalSyntheticLambda2;
import org.webrtc.EglBase;
import org.webrtc.EglRenderer;
import org.webrtc.GlGenericDrawer;
import org.webrtc.RendererCommon;
import org.webrtc.TextureViewRenderer;
/* loaded from: classes.dex */
public class TextureViewRenderer extends TextureView implements TextureView.SurfaceTextureListener, VideoSink, RendererCommon.RendererEvents {
    private static final String TAG = "TextureViewRenderer";
    private TextureView backgroundRenderer;
    private int cameraRotation;
    private final TextureEglRenderer eglRenderer;
    private boolean enableFixedSize;
    private boolean isCamera;
    private int maxTextureSize;
    private boolean mirror;
    private OrientationHelper orientationHelper;
    private VideoSink parentSink;
    private RendererCommon.RendererEvents rendererEvents;
    private final String resourceName;
    private boolean rotateTextureWithScreen;
    public int rotatedFrameHeight;
    public int rotatedFrameWidth;
    private int screenRotation;
    private int surfaceHeight;
    private int surfaceWidth;
    int textureRotation;
    Runnable updateScreenRunnable;
    boolean useCameraRotation;
    private int videoHeight;
    private final RendererCommon.VideoLayoutMeasure videoLayoutMeasure;
    private int videoWidth;

    public void setBackgroundRenderer(TextureView textureView) {
        this.backgroundRenderer = textureView;
        if (textureView == null) {
            ThreadUtils.checkIsOnMainThread();
            this.eglRenderer.releaseEglSurface(null, true);
            return;
        }
        textureView.setSurfaceTextureListener(new TextureView.SurfaceTextureListener() { // from class: org.webrtc.TextureViewRenderer.1
            @Override // android.view.TextureView.SurfaceTextureListener
            public void onSurfaceTextureSizeChanged(SurfaceTexture surfaceTexture, int i, int i2) {
            }

            @Override // android.view.TextureView.SurfaceTextureListener
            public void onSurfaceTextureUpdated(SurfaceTexture surfaceTexture) {
            }

            @Override // android.view.TextureView.SurfaceTextureListener
            public void onSurfaceTextureAvailable(SurfaceTexture surfaceTexture, int i, int i2) {
                TextureViewRenderer.this.createBackgroundSurface(surfaceTexture);
            }

            @Override // android.view.TextureView.SurfaceTextureListener
            public boolean onSurfaceTextureDestroyed(SurfaceTexture surfaceTexture) {
                ThreadUtils.checkIsOnMainThread();
                TextureViewRenderer.this.eglRenderer.releaseEglSurface(null, true);
                return false;
            }
        });
    }

    public void clearFirstFrame() {
        TextureEglRenderer textureEglRenderer = this.eglRenderer;
        textureEglRenderer.firstFrameRendered = false;
        textureEglRenderer.isFirstFrameRendered = false;
    }

    /* loaded from: classes.dex */
    public static class TextureEglRenderer extends EglRenderer implements TextureView.SurfaceTextureListener {
        private static final String TAG = "TextureEglRenderer";
        private int frameRotation;
        private boolean isFirstFrameRendered;
        private boolean isRenderingPaused;
        private final Object layoutLock;
        private RendererCommon.RendererEvents rendererEvents;
        private int rotatedFrameHeight;
        private int rotatedFrameWidth;

        @Override // android.view.TextureView.SurfaceTextureListener
        public void onSurfaceTextureUpdated(SurfaceTexture surfaceTexture) {
        }

        public TextureEglRenderer(String str) {
            super(str);
            this.layoutLock = new Object();
        }

        public void init(EglBase.Context context, RendererCommon.RendererEvents rendererEvents, int[] iArr, RendererCommon.GlDrawer glDrawer) {
            ThreadUtils.checkIsOnMainThread();
            this.rendererEvents = rendererEvents;
            synchronized (this.layoutLock) {
                this.isFirstFrameRendered = false;
                this.rotatedFrameWidth = 0;
                this.rotatedFrameHeight = 0;
                this.frameRotation = 0;
            }
            super.init(context, iArr, glDrawer);
        }

        @Override // org.webrtc.EglRenderer
        public void init(EglBase.Context context, int[] iArr, RendererCommon.GlDrawer glDrawer) {
            init(context, (RendererCommon.RendererEvents) null, iArr, glDrawer);
        }

        @Override // org.webrtc.EglRenderer
        public void setFpsReduction(float f) {
            synchronized (this.layoutLock) {
                this.isRenderingPaused = f == 0.0f;
            }
            super.setFpsReduction(f);
        }

        @Override // org.webrtc.EglRenderer
        public void disableFpsReduction() {
            synchronized (this.layoutLock) {
                this.isRenderingPaused = false;
            }
            super.disableFpsReduction();
        }

        @Override // org.webrtc.EglRenderer
        public void pauseVideo() {
            synchronized (this.layoutLock) {
                this.isRenderingPaused = true;
            }
            super.pauseVideo();
        }

        @Override // org.webrtc.EglRenderer, org.webrtc.VideoSink
        public void onFrame(VideoFrame videoFrame) {
            updateFrameDimensionsAndReportEvents(videoFrame);
            super.onFrame(videoFrame);
        }

        @Override // android.view.TextureView.SurfaceTextureListener
        public void onSurfaceTextureAvailable(SurfaceTexture surfaceTexture, int i, int i2) {
            ThreadUtils.checkIsOnMainThread();
            createEglSurface(surfaceTexture);
        }

        @Override // android.view.TextureView.SurfaceTextureListener
        public void onSurfaceTextureSizeChanged(SurfaceTexture surfaceTexture, int i, int i2) {
            ThreadUtils.checkIsOnMainThread();
            logD("surfaceChanged: size: " + i + "x" + i2);
        }

        @Override // android.view.TextureView.SurfaceTextureListener
        public boolean onSurfaceTextureDestroyed(SurfaceTexture surfaceTexture) {
            ThreadUtils.checkIsOnMainThread();
            CountDownLatch countDownLatch = new CountDownLatch(1);
            releaseEglSurface(new Theme$$ExternalSyntheticLambda2(countDownLatch), false);
            ThreadUtils.awaitUninterruptibly(countDownLatch);
            return true;
        }

        private void updateFrameDimensionsAndReportEvents(VideoFrame videoFrame) {
            synchronized (this.layoutLock) {
                if (this.isRenderingPaused) {
                    return;
                }
                if (this.rotatedFrameWidth != videoFrame.getRotatedWidth() || this.rotatedFrameHeight != videoFrame.getRotatedHeight() || this.frameRotation != videoFrame.getRotation()) {
                    logD("Reporting frame resolution changed to " + videoFrame.getBuffer().getWidth() + "x" + videoFrame.getBuffer().getHeight() + " with rotation " + videoFrame.getRotation());
                    RendererCommon.RendererEvents rendererEvents = this.rendererEvents;
                    if (rendererEvents != null) {
                        rendererEvents.onFrameResolutionChanged(videoFrame.getBuffer().getWidth(), videoFrame.getBuffer().getHeight(), videoFrame.getRotation());
                    }
                    this.rotatedFrameWidth = videoFrame.getRotatedWidth();
                    this.rotatedFrameHeight = videoFrame.getRotatedHeight();
                    this.frameRotation = videoFrame.getRotation();
                }
            }
        }

        private void logD(String str) {
            Logging.d("TextureEglRenderer", this.name + ": " + str);
        }

        @Override // org.webrtc.EglRenderer
        protected void onFirstFrameRendered() {
            AndroidUtilities.runOnUIThread(new Runnable() { // from class: org.webrtc.TextureViewRenderer$TextureEglRenderer$$ExternalSyntheticLambda0
                @Override // java.lang.Runnable
                public final void run() {
                    TextureViewRenderer.TextureEglRenderer.this.lambda$onFirstFrameRendered$0();
                }
            });
        }

        /* JADX INFO: Access modifiers changed from: private */
        public /* synthetic */ void lambda$onFirstFrameRendered$0() {
            this.isFirstFrameRendered = true;
            this.rendererEvents.onFirstFrameRendered();
        }
    }

    public TextureViewRenderer(Context context) {
        super(context);
        this.videoLayoutMeasure = new RendererCommon.VideoLayoutMeasure();
        String resourceName = getResourceName();
        this.resourceName = resourceName;
        this.eglRenderer = new TextureEglRenderer(resourceName);
        setSurfaceTextureListener(this);
    }

    public void init(EglBase.Context context, RendererCommon.RendererEvents rendererEvents) {
        init(context, rendererEvents, EglBase.CONFIG_PLAIN, new GlRectDrawer());
    }

    public void init(EglBase.Context context, RendererCommon.RendererEvents rendererEvents, int[] iArr, RendererCommon.GlDrawer glDrawer) {
        ThreadUtils.checkIsOnMainThread();
        this.rendererEvents = rendererEvents;
        this.rotatedFrameWidth = 0;
        this.rotatedFrameHeight = 0;
        this.eglRenderer.init(context, this, iArr, glDrawer);
    }

    public void release() {
        this.eglRenderer.release();
        OrientationHelper orientationHelper = this.orientationHelper;
        if (orientationHelper != null) {
            orientationHelper.stop();
        }
    }

    public void addFrameListener(EglRenderer.FrameListener frameListener, float f, RendererCommon.GlDrawer glDrawer) {
        this.eglRenderer.addFrameListener(frameListener, f, glDrawer);
    }

    public void getRenderBufferBitmap(GlGenericDrawer.TextureCallback textureCallback) {
        this.eglRenderer.getTexture(textureCallback);
    }

    public void addFrameListener(EglRenderer.FrameListener frameListener, float f) {
        this.eglRenderer.addFrameListener(frameListener, f);
    }

    public void removeFrameListener(EglRenderer.FrameListener frameListener) {
        this.eglRenderer.removeFrameListener(frameListener);
    }

    public void setIsCamera(boolean z) {
        this.isCamera = z;
        if (!z) {
            OrientationHelper orientationHelper = new OrientationHelper() { // from class: org.webrtc.TextureViewRenderer.2
                @Override // org.webrtc.OrientationHelper
                protected void onOrientationUpdate(int i) {
                    if (!TextureViewRenderer.this.isCamera) {
                        TextureViewRenderer.this.updateRotation();
                    }
                }
            };
            this.orientationHelper = orientationHelper;
            orientationHelper.start();
        }
    }

    public void setEnableHardwareScaler(boolean z) {
        ThreadUtils.checkIsOnMainThread();
        this.enableFixedSize = z;
        updateSurfaceSize();
    }

    public void updateRotation() {
        View view;
        float f;
        float f2;
        float min;
        if (this.orientationHelper == null || this.rotatedFrameWidth == 0 || this.rotatedFrameHeight == 0 || (view = (View) getParent()) == null) {
            return;
        }
        int orientation = this.orientationHelper.getOrientation();
        float measuredWidth = getMeasuredWidth();
        float measuredHeight = getMeasuredHeight();
        float measuredWidth2 = view.getMeasuredWidth();
        float measuredHeight2 = view.getMeasuredHeight();
        if (orientation == 90 || orientation == 270) {
            f = measuredWidth;
            f2 = measuredHeight;
        } else {
            f2 = measuredWidth;
            f = measuredHeight;
        }
        if (f2 < f) {
            min = Math.max(f2 / measuredWidth, f / measuredHeight);
        } else {
            min = Math.min(f2 / measuredWidth, f / measuredHeight);
        }
        float f3 = f2 * min;
        float f4 = f * min;
        if (Math.abs((f3 / f4) - (measuredWidth2 / measuredHeight2)) < 0.1f) {
            min *= Math.max(measuredWidth2 / f3, measuredHeight2 / f4);
        }
        if (orientation == 270) {
            orientation = -90;
        }
        animate().scaleX(min).scaleY(min).rotation(-orientation).setDuration(180L).start();
    }

    public void setMirror(boolean z) {
        if (this.mirror != z) {
            this.mirror = z;
            if (this.rotateTextureWithScreen) {
                onRotationChanged();
            } else {
                this.eglRenderer.setMirror(z);
            }
            updateSurfaceSize();
            requestLayout();
        }
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

    @Override // org.webrtc.VideoSink
    public void onFrame(VideoFrame videoFrame) {
        this.eglRenderer.onFrame(videoFrame);
    }

    @Override // android.view.View
    protected void onMeasure(int i, int i2) {
        Point measure;
        ThreadUtils.checkIsOnMainThread();
        if (!this.isCamera && this.rotateTextureWithScreen) {
            updateVideoSizes();
        }
        int i3 = this.maxTextureSize;
        if (i3 > 0) {
            measure = this.videoLayoutMeasure.measure(this.isCamera, View.MeasureSpec.makeMeasureSpec(Math.min(i3, View.MeasureSpec.getSize(i)), View.MeasureSpec.getMode(i)), View.MeasureSpec.makeMeasureSpec(Math.min(this.maxTextureSize, View.MeasureSpec.getSize(i2)), View.MeasureSpec.getMode(i2)), this.rotatedFrameWidth, this.rotatedFrameHeight);
        } else {
            measure = this.videoLayoutMeasure.measure(this.isCamera, i, i2, this.rotatedFrameWidth, this.rotatedFrameHeight);
        }
        setMeasuredDimension(measure.x, measure.y);
        if (this.rotatedFrameWidth != 0 && this.rotatedFrameHeight != 0) {
            this.eglRenderer.setLayoutAspectRatio(getMeasuredWidth() / getMeasuredHeight());
        }
        updateSurfaceSize();
    }

    private void updateSurfaceSize() {
        ThreadUtils.checkIsOnMainThread();
        if (this.enableFixedSize && this.rotatedFrameWidth != 0 && this.rotatedFrameHeight != 0 && getWidth() != 0 && getHeight() != 0) {
            float width = getWidth() / getHeight();
            int i = this.rotatedFrameWidth;
            int i2 = this.rotatedFrameHeight;
            if (i / i2 > width) {
                i = (int) (i2 * width);
            } else {
                i2 = (int) (i2 / width);
            }
            int min = Math.min(getWidth(), i);
            int min2 = Math.min(getHeight(), i2);
            logD("updateSurfaceSize. Layout size: " + getWidth() + "x" + getHeight() + ", frame size: " + this.rotatedFrameWidth + "x" + this.rotatedFrameHeight + ", requested surface size: " + min + "x" + min2 + ", old surface size: " + this.surfaceWidth + "x" + this.surfaceHeight);
            if (min == this.surfaceWidth && min2 == this.surfaceHeight) {
                return;
            }
            this.surfaceWidth = min;
            this.surfaceHeight = min2;
            return;
        }
        this.surfaceHeight = 0;
        this.surfaceWidth = 0;
    }

    @Override // android.view.TextureView.SurfaceTextureListener
    public void onSurfaceTextureAvailable(SurfaceTexture surfaceTexture, int i, int i2) {
        ThreadUtils.checkIsOnMainThread();
        this.surfaceHeight = 0;
        this.surfaceWidth = 0;
        updateSurfaceSize();
        this.eglRenderer.onSurfaceTextureAvailable(surfaceTexture, i, i2);
    }

    @Override // android.view.TextureView.SurfaceTextureListener
    public void onSurfaceTextureSizeChanged(SurfaceTexture surfaceTexture, int i, int i2) {
        this.surfaceWidth = i;
        this.surfaceHeight = i2;
        this.eglRenderer.onSurfaceTextureSizeChanged(surfaceTexture, i, i2);
    }

    @Override // android.view.TextureView.SurfaceTextureListener
    public boolean onSurfaceTextureDestroyed(SurfaceTexture surfaceTexture) {
        VideoSink videoSink = this.parentSink;
        if (videoSink instanceof VoIPService.ProxyVideoSink) {
            VoIPService.ProxyVideoSink proxyVideoSink = (VoIPService.ProxyVideoSink) videoSink;
            proxyVideoSink.removeTarget(this);
            proxyVideoSink.removeBackground(this);
        }
        this.eglRenderer.onSurfaceTextureDestroyed(surfaceTexture);
        return true;
    }

    @Override // android.view.TextureView.SurfaceTextureListener
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
        this.eglRenderer.isFirstFrameRendered = false;
    }

    @Override // org.webrtc.VideoSink
    public void setParentSink(VideoSink videoSink) {
        this.parentSink = videoSink;
    }

    public void onFirstFrameRendered() {
        RendererCommon.RendererEvents rendererEvents = this.rendererEvents;
        if (rendererEvents != null) {
            rendererEvents.onFirstFrameRendered();
        }
    }

    public boolean isFirstFrameRendered() {
        return this.eglRenderer.isFirstFrameRendered;
    }

    /* JADX WARN: Code restructure failed: missing block: B:16:0x0023, code lost:
        if (r10 == 0) goto L30;
     */
    /* JADX WARN: Code restructure failed: missing block: B:27:0x0036, code lost:
        if (r10 != (-180)) goto L30;
     */
    /* JADX WARN: Code restructure failed: missing block: B:41:0x0056, code lost:
        if (r10 != (-180)) goto L30;
     */
    /* JADX WARN: Code restructure failed: missing block: B:43:0x0059, code lost:
        r10 = r8;
     */
    @Override // org.webrtc.RendererCommon.RendererEvents
    /*
        Code decompiled incorrectly, please refer to instructions dump.
        To view partially-correct add '--show-bad-code' argument
    */
    public void onFrameResolutionChanged(final int r8, final int r9, int r10) {
        /*
            r7 = this;
            org.webrtc.RendererCommon$RendererEvents r0 = r7.rendererEvents
            if (r0 == 0) goto L7
            r0.onFrameResolutionChanged(r8, r9, r10)
        L7:
            r7.textureRotation = r10
            boolean r0 = r7.rotateTextureWithScreen
            r1 = -180(0xffffffffffffff4c, float:NaN)
            r2 = 180(0xb4, float:2.52E-43)
            if (r0 == 0) goto L39
            boolean r10 = r7.isCamera
            if (r10 == 0) goto L18
            r7.onRotationChanged()
        L18:
            boolean r10 = r7.useCameraRotation
            if (r10 == 0) goto L26
            int r10 = r7.screenRotation
            if (r10 != 0) goto L22
            r0 = r9
            goto L23
        L22:
            r0 = r8
        L23:
            if (r10 != 0) goto L5b
            goto L59
        L26:
            int r10 = r7.textureRotation
            if (r10 == 0) goto L31
            if (r10 == r2) goto L31
            if (r10 != r1) goto L2f
            goto L31
        L2f:
            r0 = r9
            goto L32
        L31:
            r0 = r8
        L32:
            if (r10 == 0) goto L5b
            if (r10 == r2) goto L5b
            if (r10 != r1) goto L59
            goto L5b
        L39:
            boolean r0 = r7.isCamera
            if (r0 == 0) goto L45
            org.webrtc.TextureViewRenderer$TextureEglRenderer r0 = r7.eglRenderer
            int r3 = org.webrtc.OrientationHelper.cameraRotation
            int r3 = -r3
            r0.setRotation(r3)
        L45:
            int r0 = org.webrtc.OrientationHelper.cameraOrientation
            int r10 = r10 - r0
            if (r10 == 0) goto L51
            if (r10 == r2) goto L51
            if (r10 != r1) goto L4f
            goto L51
        L4f:
            r0 = r9
            goto L52
        L51:
            r0 = r8
        L52:
            if (r10 == 0) goto L5b
            if (r10 == r2) goto L5b
            if (r10 != r1) goto L59
            goto L5b
        L59:
            r10 = r8
            goto L5c
        L5b:
            r10 = r9
        L5c:
            r6 = r10
            r5 = r0
            org.webrtc.TextureViewRenderer$TextureEglRenderer r10 = r7.eglRenderer
            java.lang.Object r10 = org.webrtc.TextureViewRenderer.TextureEglRenderer.access$300(r10)
            monitor-enter(r10)
            java.lang.Runnable r0 = r7.updateScreenRunnable     // Catch: java.lang.Throwable -> L7c
            if (r0 == 0) goto L6c
            org.telegram.messenger.AndroidUtilities.cancelRunOnUIThread(r0)     // Catch: java.lang.Throwable -> L7c
        L6c:
            org.webrtc.TextureViewRenderer$$ExternalSyntheticLambda1 r0 = new org.webrtc.TextureViewRenderer$$ExternalSyntheticLambda1     // Catch: java.lang.Throwable -> L7c
            r1 = r0
            r2 = r7
            r3 = r8
            r4 = r9
            r1.<init>()     // Catch: java.lang.Throwable -> L7c
            r7.updateScreenRunnable = r0     // Catch: java.lang.Throwable -> L7c
            r7.postOrRun(r0)     // Catch: java.lang.Throwable -> L7c
            monitor-exit(r10)     // Catch: java.lang.Throwable -> L7c
            return
        L7c:
            r8 = move-exception
            monitor-exit(r10)     // Catch: java.lang.Throwable -> L7c
            throw r8
        */
        throw new UnsupportedOperationException("Method not decompiled: org.webrtc.TextureViewRenderer.onFrameResolutionChanged(int, int, int):void");
    }

    /* JADX INFO: Access modifiers changed from: private */
    public /* synthetic */ void lambda$onFrameResolutionChanged$0(int i, int i2, int i3, int i4) {
        this.updateScreenRunnable = null;
        this.videoWidth = i;
        this.videoHeight = i2;
        this.rotatedFrameWidth = i3;
        this.rotatedFrameHeight = i4;
        updateSurfaceSize();
        requestLayout();
    }

    public void setScreenRotation(int i) {
        this.screenRotation = i;
        onRotationChanged();
        updateVideoSizes();
    }

    private void updateVideoSizes() {
        int i;
        final int i2;
        final int i3 = this.videoHeight;
        if (i3 == 0 || (i = this.videoWidth) == 0) {
            return;
        }
        if (this.rotateTextureWithScreen) {
            if (this.useCameraRotation) {
                int i4 = this.screenRotation;
                i2 = i4 == 0 ? i3 : i;
                if (i4 == 0) {
                    i3 = i;
                }
            } else {
                int i5 = this.textureRotation;
                int i6 = (i5 == 0 || i5 == 180 || i5 == -180) ? i : i3;
                if (i5 != 0 && i5 != 180 && i5 != -180) {
                    i3 = i;
                }
                i2 = i6;
            }
        } else {
            int i7 = this.textureRotation - OrientationHelper.cameraOrientation;
            int i8 = (i7 == 0 || i7 == 180 || i7 == -180) ? this.videoWidth : this.videoHeight;
            i3 = (i7 == 0 || i7 == 180 || i7 == -180) ? this.videoHeight : this.videoWidth;
            i2 = i8;
        }
        if (this.rotatedFrameWidth == i2 && this.rotatedFrameHeight == i3) {
            return;
        }
        synchronized (this.eglRenderer.layoutLock) {
            Runnable runnable = this.updateScreenRunnable;
            if (runnable != null) {
                AndroidUtilities.cancelRunOnUIThread(runnable);
            }
            Runnable runnable2 = new Runnable() { // from class: org.webrtc.TextureViewRenderer$$ExternalSyntheticLambda0
                @Override // java.lang.Runnable
                public final void run() {
                    TextureViewRenderer.this.lambda$updateVideoSizes$1(i2, i3);
                }
            };
            this.updateScreenRunnable = runnable2;
            postOrRun(runnable2);
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public /* synthetic */ void lambda$updateVideoSizes$1(int i, int i2) {
        this.updateScreenRunnable = null;
        this.rotatedFrameWidth = i;
        this.rotatedFrameHeight = i2;
        updateSurfaceSize();
        requestLayout();
    }

    public void setRotateTextureWithScreen(boolean z) {
        if (this.rotateTextureWithScreen != z) {
            this.rotateTextureWithScreen = z;
            requestLayout();
        }
    }

    public void setUseCameraRotation(boolean z) {
        if (this.useCameraRotation != z) {
            this.useCameraRotation = z;
            onRotationChanged();
            updateVideoSizes();
        }
    }

    private void onRotationChanged() {
        int i;
        int i2 = this.useCameraRotation ? OrientationHelper.cameraOrientation : 0;
        boolean z = this.mirror;
        if (z) {
            i2 = 360 - i2;
        }
        int i3 = -i2;
        if (this.useCameraRotation) {
            int i4 = this.screenRotation;
            if (i4 == 1) {
                i = z ? 90 : -90;
            } else if (i4 == 3) {
                i = z ? 270 : -270;
            }
            i3 += i;
        }
        this.eglRenderer.setRotation(i3);
        this.eglRenderer.setMirror(this.mirror);
    }

    @Override // android.view.View
    public void setRotation(float f) {
        super.setRotation(f);
    }

    @Override // android.view.View
    public void setRotationY(float f) {
        super.setRotationY(f);
    }

    @Override // android.view.View
    public void setRotationX(float f) {
        super.setRotationX(f);
    }

    private void postOrRun(Runnable runnable) {
        if (Thread.currentThread() == Looper.getMainLooper().getThread()) {
            runnable.run();
        } else {
            AndroidUtilities.runOnUIThread(runnable);
        }
    }

    private void logD(String str) {
        Logging.d("TextureViewRenderer", this.resourceName + ": " + str);
    }

    public void createBackgroundSurface(SurfaceTexture surfaceTexture) {
        this.eglRenderer.createBackgroundSurface(surfaceTexture);
    }

    public void setMaxTextureSize(int i) {
        this.maxTextureSize = i;
    }
}
