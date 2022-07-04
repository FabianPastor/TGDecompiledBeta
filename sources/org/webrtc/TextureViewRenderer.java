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
import org.telegram.ui.ActionBar.Theme$$ExternalSyntheticLambda4;
import org.webrtc.EglBase;
import org.webrtc.EglRenderer;
import org.webrtc.GlGenericDrawer;
import org.webrtc.RendererCommon;

public class TextureViewRenderer extends TextureView implements TextureView.SurfaceTextureListener, VideoSink, RendererCommon.RendererEvents {
    private static final String TAG = "TextureViewRenderer";
    private TextureView backgroundRenderer;
    private int cameraRotation;
    /* access modifiers changed from: private */
    public final TextureEglRenderer eglRenderer;
    private boolean enableFixedSize;
    /* access modifiers changed from: private */
    public boolean isCamera;
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
    private final RendererCommon.VideoLayoutMeasure videoLayoutMeasure = new RendererCommon.VideoLayoutMeasure();
    private int videoWidth;

    public void setBackgroundRenderer(TextureView backgroundRenderer2) {
        this.backgroundRenderer = backgroundRenderer2;
        if (backgroundRenderer2 == null) {
            ThreadUtils.checkIsOnMainThread();
            this.eglRenderer.releaseEglSurface((Runnable) null, true);
            return;
        }
        backgroundRenderer2.setSurfaceTextureListener(new TextureView.SurfaceTextureListener() {
            public void onSurfaceTextureAvailable(SurfaceTexture surfaceTexture, int i, int i1) {
                TextureViewRenderer.this.createBackgroundSurface(surfaceTexture);
            }

            public void onSurfaceTextureSizeChanged(SurfaceTexture surfaceTexture, int i, int i1) {
            }

            public boolean onSurfaceTextureDestroyed(SurfaceTexture surfaceTexture) {
                ThreadUtils.checkIsOnMainThread();
                TextureViewRenderer.this.eglRenderer.releaseEglSurface((Runnable) null, true);
                return false;
            }

            public void onSurfaceTextureUpdated(SurfaceTexture surfaceTexture) {
            }
        });
    }

    public void clearFirstFrame() {
        this.eglRenderer.firstFrameRendered = false;
        boolean unused = this.eglRenderer.isFirstFrameRendered = false;
    }

    public static class TextureEglRenderer extends EglRenderer implements TextureView.SurfaceTextureListener {
        private static final String TAG = "TextureEglRenderer";
        private int frameRotation;
        /* access modifiers changed from: private */
        public boolean isFirstFrameRendered;
        private boolean isRenderingPaused;
        /* access modifiers changed from: private */
        public final Object layoutLock = new Object();
        private RendererCommon.RendererEvents rendererEvents;
        private int rotatedFrameHeight;
        private int rotatedFrameWidth;

        public TextureEglRenderer(String name) {
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

        public void onSurfaceTextureAvailable(SurfaceTexture surfaceTexture, int width, int height) {
            ThreadUtils.checkIsOnMainThread();
            createEglSurface(surfaceTexture);
        }

        public void onSurfaceTextureSizeChanged(SurfaceTexture surfaceTexture, int width, int height) {
            ThreadUtils.checkIsOnMainThread();
            logD("surfaceChanged: size: " + width + "x" + height);
        }

        public boolean onSurfaceTextureDestroyed(SurfaceTexture surfaceTexture) {
            ThreadUtils.checkIsOnMainThread();
            CountDownLatch completionLatch = new CountDownLatch(1);
            completionLatch.getClass();
            releaseEglSurface(new Theme$$ExternalSyntheticLambda4(completionLatch), false);
            ThreadUtils.awaitUninterruptibly(completionLatch);
            return true;
        }

        public void onSurfaceTextureUpdated(SurfaceTexture surfaceTexture) {
        }

        /* JADX WARNING: Code restructure failed: missing block: B:18:0x0088, code lost:
            return;
         */
        /* Code decompiled incorrectly, please refer to instructions dump. */
        private void updateFrameDimensionsAndReportEvents(org.webrtc.VideoFrame r6) {
            /*
                r5 = this;
                java.lang.Object r0 = r5.layoutLock
                monitor-enter(r0)
                boolean r1 = r5.isRenderingPaused     // Catch:{ all -> 0x0089 }
                if (r1 == 0) goto L_0x0009
                monitor-exit(r0)     // Catch:{ all -> 0x0089 }
                return
            L_0x0009:
                int r1 = r5.rotatedFrameWidth     // Catch:{ all -> 0x0089 }
                int r2 = r6.getRotatedWidth()     // Catch:{ all -> 0x0089 }
                if (r1 != r2) goto L_0x0021
                int r1 = r5.rotatedFrameHeight     // Catch:{ all -> 0x0089 }
                int r2 = r6.getRotatedHeight()     // Catch:{ all -> 0x0089 }
                if (r1 != r2) goto L_0x0021
                int r1 = r5.frameRotation     // Catch:{ all -> 0x0089 }
                int r2 = r6.getRotation()     // Catch:{ all -> 0x0089 }
                if (r1 == r2) goto L_0x0087
            L_0x0021:
                java.lang.StringBuilder r1 = new java.lang.StringBuilder     // Catch:{ all -> 0x0089 }
                r1.<init>()     // Catch:{ all -> 0x0089 }
                java.lang.String r2 = "Reporting frame resolution changed to "
                r1.append(r2)     // Catch:{ all -> 0x0089 }
                org.webrtc.VideoFrame$Buffer r2 = r6.getBuffer()     // Catch:{ all -> 0x0089 }
                int r2 = r2.getWidth()     // Catch:{ all -> 0x0089 }
                r1.append(r2)     // Catch:{ all -> 0x0089 }
                java.lang.String r2 = "x"
                r1.append(r2)     // Catch:{ all -> 0x0089 }
                org.webrtc.VideoFrame$Buffer r2 = r6.getBuffer()     // Catch:{ all -> 0x0089 }
                int r2 = r2.getHeight()     // Catch:{ all -> 0x0089 }
                r1.append(r2)     // Catch:{ all -> 0x0089 }
                java.lang.String r2 = " with rotation "
                r1.append(r2)     // Catch:{ all -> 0x0089 }
                int r2 = r6.getRotation()     // Catch:{ all -> 0x0089 }
                r1.append(r2)     // Catch:{ all -> 0x0089 }
                java.lang.String r1 = r1.toString()     // Catch:{ all -> 0x0089 }
                r5.logD(r1)     // Catch:{ all -> 0x0089 }
                org.webrtc.RendererCommon$RendererEvents r1 = r5.rendererEvents     // Catch:{ all -> 0x0089 }
                if (r1 == 0) goto L_0x0075
                org.webrtc.VideoFrame$Buffer r2 = r6.getBuffer()     // Catch:{ all -> 0x0089 }
                int r2 = r2.getWidth()     // Catch:{ all -> 0x0089 }
                org.webrtc.VideoFrame$Buffer r3 = r6.getBuffer()     // Catch:{ all -> 0x0089 }
                int r3 = r3.getHeight()     // Catch:{ all -> 0x0089 }
                int r4 = r6.getRotation()     // Catch:{ all -> 0x0089 }
                r1.onFrameResolutionChanged(r2, r3, r4)     // Catch:{ all -> 0x0089 }
            L_0x0075:
                int r1 = r6.getRotatedWidth()     // Catch:{ all -> 0x0089 }
                r5.rotatedFrameWidth = r1     // Catch:{ all -> 0x0089 }
                int r1 = r6.getRotatedHeight()     // Catch:{ all -> 0x0089 }
                r5.rotatedFrameHeight = r1     // Catch:{ all -> 0x0089 }
                int r1 = r6.getRotation()     // Catch:{ all -> 0x0089 }
                r5.frameRotation = r1     // Catch:{ all -> 0x0089 }
            L_0x0087:
                monitor-exit(r0)     // Catch:{ all -> 0x0089 }
                return
            L_0x0089:
                r1 = move-exception
                monitor-exit(r0)     // Catch:{ all -> 0x0089 }
                throw r1
            */
            throw new UnsupportedOperationException("Method not decompiled: org.webrtc.TextureViewRenderer.TextureEglRenderer.updateFrameDimensionsAndReportEvents(org.webrtc.VideoFrame):void");
        }

        private void logD(String string) {
            Logging.d("TextureEglRenderer", this.name + ": " + string);
        }

        /* access modifiers changed from: protected */
        public void onFirstFrameRendered() {
            AndroidUtilities.runOnUIThread(new TextureViewRenderer$TextureEglRenderer$$ExternalSyntheticLambda0(this));
        }

        /* renamed from: lambda$onFirstFrameRendered$0$org-webrtc-TextureViewRenderer$TextureEglRenderer  reason: not valid java name */
        public /* synthetic */ void m1670xd9a95a80() {
            this.isFirstFrameRendered = true;
            this.rendererEvents.onFirstFrameRendered();
        }
    }

    public TextureViewRenderer(Context context) {
        super(context);
        String resourceName2 = getResourceName();
        this.resourceName = resourceName2;
        this.eglRenderer = new TextureEglRenderer(resourceName2);
        setSurfaceTextureListener(this);
    }

    public void init(EglBase.Context sharedContext, RendererCommon.RendererEvents rendererEvents2) {
        init(sharedContext, rendererEvents2, EglBase.CONFIG_PLAIN, new GlRectDrawer());
    }

    public void init(EglBase.Context sharedContext, RendererCommon.RendererEvents rendererEvents2, int[] configAttributes, RendererCommon.GlDrawer drawer) {
        ThreadUtils.checkIsOnMainThread();
        this.rendererEvents = rendererEvents2;
        this.rotatedFrameWidth = 0;
        this.rotatedFrameHeight = 0;
        this.eglRenderer.init(sharedContext, this, configAttributes, drawer);
    }

    public void release() {
        this.eglRenderer.release();
        OrientationHelper orientationHelper2 = this.orientationHelper;
        if (orientationHelper2 != null) {
            orientationHelper2.stop();
        }
    }

    public void addFrameListener(EglRenderer.FrameListener listener, float scale, RendererCommon.GlDrawer drawerParam) {
        this.eglRenderer.addFrameListener(listener, scale, drawerParam);
    }

    public void getRenderBufferBitmap(GlGenericDrawer.TextureCallback callback) {
        this.eglRenderer.getTexture(callback);
    }

    public void addFrameListener(EglRenderer.FrameListener listener, float scale) {
        this.eglRenderer.addFrameListener(listener, scale);
    }

    public void removeFrameListener(EglRenderer.FrameListener listener) {
        this.eglRenderer.removeFrameListener(listener);
    }

    public void setIsCamera(boolean value) {
        this.isCamera = value;
        if (!value) {
            AnonymousClass2 r0 = new OrientationHelper() {
                /* access modifiers changed from: protected */
                public void onOrientationUpdate(int orientation) {
                    if (!TextureViewRenderer.this.isCamera) {
                        TextureViewRenderer.this.updateRotation();
                    }
                }
            };
            this.orientationHelper = r0;
            r0.start();
        }
    }

    public void setEnableHardwareScaler(boolean enabled) {
        ThreadUtils.checkIsOnMainThread();
        this.enableFixedSize = enabled;
        updateSurfaceSize();
    }

    public void updateRotation() {
        View parentView;
        float h;
        float w;
        float scale;
        if (this.orientationHelper != null && this.rotatedFrameWidth != 0 && this.rotatedFrameHeight != 0 && (parentView = (View) getParent()) != null) {
            int orientation = this.orientationHelper.getOrientation();
            float viewWidth = (float) getMeasuredWidth();
            float viewHeight = (float) getMeasuredHeight();
            float targetWidth = (float) parentView.getMeasuredWidth();
            float targetHeight = (float) parentView.getMeasuredHeight();
            if (orientation == 90 || orientation == 270) {
                w = viewHeight;
                h = viewWidth;
            } else {
                w = viewWidth;
                h = viewHeight;
            }
            if (w < h) {
                scale = Math.max(w / viewWidth, h / viewHeight);
            } else {
                scale = Math.min(w / viewWidth, h / viewHeight);
            }
            float w2 = w * scale;
            float h2 = h * scale;
            if (Math.abs((w2 / h2) - (targetWidth / targetHeight)) < 0.1f) {
                scale *= Math.max(targetWidth / w2, targetHeight / h2);
            }
            if (orientation == 270) {
                orientation = -90;
            }
            animate().scaleX(scale).scaleY(scale).rotation((float) (-orientation)).setDuration(180).start();
        }
    }

    public void setMirror(boolean mirror2) {
        if (this.mirror != mirror2) {
            this.mirror = mirror2;
            if (this.rotateTextureWithScreen) {
                onRotationChanged();
            } else {
                this.eglRenderer.setMirror(mirror2);
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

    public void setScalingType(RendererCommon.ScalingType scalingTypeMatchOrientation, RendererCommon.ScalingType scalingTypeMismatchOrientation) {
        ThreadUtils.checkIsOnMainThread();
        this.videoLayoutMeasure.setScalingType(scalingTypeMatchOrientation, scalingTypeMismatchOrientation);
        requestLayout();
    }

    public void setFpsReduction(float fps) {
        this.eglRenderer.setFpsReduction(fps);
    }

    public void disableFpsReduction() {
        this.eglRenderer.disableFpsReduction();
    }

    public void pauseVideo() {
        this.eglRenderer.pauseVideo();
    }

    public void onFrame(VideoFrame frame) {
        this.eglRenderer.onFrame(frame);
    }

    /* access modifiers changed from: protected */
    public void onMeasure(int widthSpec, int heightSpec) {
        Point size;
        ThreadUtils.checkIsOnMainThread();
        if (!this.isCamera && this.rotateTextureWithScreen) {
            updateVideoSizes();
        }
        int i = this.maxTextureSize;
        if (i > 0) {
            size = this.videoLayoutMeasure.measure(this.isCamera, View.MeasureSpec.makeMeasureSpec(Math.min(i, View.MeasureSpec.getSize(widthSpec)), View.MeasureSpec.getMode(widthSpec)), View.MeasureSpec.makeMeasureSpec(Math.min(this.maxTextureSize, View.MeasureSpec.getSize(heightSpec)), View.MeasureSpec.getMode(heightSpec)), this.rotatedFrameWidth, this.rotatedFrameHeight);
        } else {
            size = this.videoLayoutMeasure.measure(this.isCamera, widthSpec, heightSpec, this.rotatedFrameWidth, this.rotatedFrameHeight);
        }
        setMeasuredDimension(size.x, size.y);
        if (!(this.rotatedFrameWidth == 0 || this.rotatedFrameHeight == 0)) {
            this.eglRenderer.setLayoutAspectRatio(((float) getMeasuredWidth()) / ((float) getMeasuredHeight()));
        }
        updateSurfaceSize();
    }

    private void updateSurfaceSize() {
        int drawnFrameWidth;
        int drawnFrameWidth2;
        ThreadUtils.checkIsOnMainThread();
        if (!this.enableFixedSize || this.rotatedFrameWidth == 0 || this.rotatedFrameHeight == 0 || getWidth() == 0 || getHeight() == 0) {
            this.surfaceHeight = 0;
            this.surfaceWidth = 0;
            return;
        }
        float layoutAspectRatio = ((float) getWidth()) / ((float) getHeight());
        int i = this.rotatedFrameHeight;
        if (((float) this.rotatedFrameWidth) / ((float) i) > layoutAspectRatio) {
            drawnFrameWidth2 = (int) (((float) i) * layoutAspectRatio);
            drawnFrameWidth = this.rotatedFrameHeight;
        } else {
            drawnFrameWidth = (int) (((float) i) / layoutAspectRatio);
            drawnFrameWidth2 = this.rotatedFrameWidth;
        }
        int width = Math.min(getWidth(), drawnFrameWidth2);
        int height = Math.min(getHeight(), drawnFrameWidth);
        logD("updateSurfaceSize. Layout size: " + getWidth() + "x" + getHeight() + ", frame size: " + this.rotatedFrameWidth + "x" + this.rotatedFrameHeight + ", requested surface size: " + width + "x" + height + ", old surface size: " + this.surfaceWidth + "x" + this.surfaceHeight);
        if (width != this.surfaceWidth || height != this.surfaceHeight) {
            this.surfaceWidth = width;
            this.surfaceHeight = height;
        }
    }

    public void onSurfaceTextureAvailable(SurfaceTexture surface, int width, int height) {
        ThreadUtils.checkIsOnMainThread();
        this.surfaceHeight = 0;
        this.surfaceWidth = 0;
        updateSurfaceSize();
        this.eglRenderer.onSurfaceTextureAvailable(surface, width, height);
    }

    public void onSurfaceTextureSizeChanged(SurfaceTexture surface, int width, int height) {
        this.surfaceWidth = width;
        this.surfaceHeight = height;
        this.eglRenderer.onSurfaceTextureSizeChanged(surface, width, height);
    }

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

    public void onSurfaceTextureUpdated(SurfaceTexture surfaceTexture) {
        this.eglRenderer.onSurfaceTextureUpdated(surfaceTexture);
    }

    private String getResourceName() {
        try {
            return getResources().getResourceEntryName(getId());
        } catch (Resources.NotFoundException e) {
            return "";
        }
    }

    public void clearImage() {
        this.eglRenderer.clearImage();
        boolean unused = this.eglRenderer.isFirstFrameRendered = false;
    }

    public void setParentSink(VideoSink parent) {
        this.parentSink = parent;
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

    public void onFrameResolutionChanged(int videoWidth2, int videoHeight2, int rotation) {
        int rotatedWidth;
        int rotatedHeight;
        RendererCommon.RendererEvents rendererEvents2 = this.rendererEvents;
        if (rendererEvents2 != null) {
            rendererEvents2.onFrameResolutionChanged(videoWidth2, videoHeight2, rotation);
        }
        this.textureRotation = rotation;
        if (this.rotateTextureWithScreen) {
            if (this.isCamera) {
                onRotationChanged();
            }
            if (this.useCameraRotation) {
                int i = this.screenRotation;
                int rotatedWidth2 = i == 0 ? videoHeight2 : videoWidth2;
                rotatedHeight = i == 0 ? videoWidth2 : videoHeight2;
                rotatedWidth = rotatedWidth2;
            } else {
                int rotatedHeight2 = this.textureRotation;
                int rotatedWidth3 = (rotatedHeight2 == 0 || rotatedHeight2 == 180 || rotatedHeight2 == -180) ? videoWidth2 : videoHeight2;
                rotatedHeight = (rotatedHeight2 == 0 || rotatedHeight2 == 180 || rotatedHeight2 == -180) ? videoHeight2 : videoWidth2;
                rotatedWidth = rotatedWidth3;
            }
        } else {
            if (this.isCamera != 0) {
                this.eglRenderer.setRotation(-OrientationHelper.cameraRotation);
            }
            int rotation2 = rotation - OrientationHelper.cameraOrientation;
            rotatedWidth = (rotation2 == 0 || rotation2 == 180 || rotation2 == -180) ? videoWidth2 : videoHeight2;
            rotatedHeight = (rotation2 == 0 || rotation2 == 180 || rotation2 == -180) ? videoHeight2 : videoWidth2;
        }
        synchronized (this.eglRenderer.layoutLock) {
            Runnable runnable = this.updateScreenRunnable;
            if (runnable != null) {
                AndroidUtilities.cancelRunOnUIThread(runnable);
            }
            TextureViewRenderer$$ExternalSyntheticLambda1 textureViewRenderer$$ExternalSyntheticLambda1 = new TextureViewRenderer$$ExternalSyntheticLambda1(this, videoWidth2, videoHeight2, rotatedWidth, rotatedHeight);
            this.updateScreenRunnable = textureViewRenderer$$ExternalSyntheticLambda1;
            postOrRun(textureViewRenderer$$ExternalSyntheticLambda1);
        }
    }

    /* renamed from: lambda$onFrameResolutionChanged$0$org-webrtc-TextureViewRenderer  reason: not valid java name */
    public /* synthetic */ void m1668lambda$onFrameResolutionChanged$0$orgwebrtcTextureViewRenderer(int videoWidth2, int videoHeight2, int rotatedWidth, int rotatedHeight) {
        this.updateScreenRunnable = null;
        this.videoWidth = videoWidth2;
        this.videoHeight = videoHeight2;
        this.rotatedFrameWidth = rotatedWidth;
        this.rotatedFrameHeight = rotatedHeight;
        updateSurfaceSize();
        requestLayout();
    }

    public void setScreenRotation(int screenRotation2) {
        this.screenRotation = screenRotation2;
        onRotationChanged();
        updateVideoSizes();
    }

    private void updateVideoSizes() {
        int i;
        int rotatedWidth;
        int rotation = this.videoHeight;
        if (rotation != 0 && (i = this.videoWidth) != 0) {
            if (!this.rotateTextureWithScreen) {
                int rotation2 = this.textureRotation - OrientationHelper.cameraOrientation;
                rotatedWidth = (rotation2 == 0 || rotation2 == 180 || rotation2 == -180) ? this.videoWidth : this.videoHeight;
                rotation = (rotation2 == 0 || rotation2 == 180 || rotation2 == -180) ? this.videoHeight : this.videoWidth;
            } else if (this.useCameraRotation) {
                int i2 = this.screenRotation;
                rotatedWidth = i2 == 0 ? rotation : i;
                if (i2 == 0) {
                    rotation = i;
                }
            } else {
                int i3 = this.textureRotation;
                int rotatedWidth2 = (i3 == 0 || i3 == 180 || i3 == -180) ? i : rotation;
                if (!(i3 == 0 || i3 == 180 || i3 == -180)) {
                    rotation = i;
                }
                rotatedWidth = rotatedWidth2;
            }
            if (this.rotatedFrameWidth != rotatedWidth || this.rotatedFrameHeight != rotation) {
                synchronized (this.eglRenderer.layoutLock) {
                    Runnable runnable = this.updateScreenRunnable;
                    if (runnable != null) {
                        AndroidUtilities.cancelRunOnUIThread(runnable);
                    }
                    TextureViewRenderer$$ExternalSyntheticLambda0 textureViewRenderer$$ExternalSyntheticLambda0 = new TextureViewRenderer$$ExternalSyntheticLambda0(this, rotatedWidth, rotation);
                    this.updateScreenRunnable = textureViewRenderer$$ExternalSyntheticLambda0;
                    postOrRun(textureViewRenderer$$ExternalSyntheticLambda0);
                }
            }
        }
    }

    /* renamed from: lambda$updateVideoSizes$1$org-webrtc-TextureViewRenderer  reason: not valid java name */
    public /* synthetic */ void m1669lambda$updateVideoSizes$1$orgwebrtcTextureViewRenderer(int rotatedWidth, int rotatedHeight) {
        this.updateScreenRunnable = null;
        this.rotatedFrameWidth = rotatedWidth;
        this.rotatedFrameHeight = rotatedHeight;
        updateSurfaceSize();
        requestLayout();
    }

    public void setRotateTextureWithScreen(boolean rotateTextureWithScreen2) {
        if (this.rotateTextureWithScreen != rotateTextureWithScreen2) {
            this.rotateTextureWithScreen = rotateTextureWithScreen2;
            requestLayout();
        }
    }

    public void setUseCameraRotation(boolean useCameraRotation2) {
        if (this.useCameraRotation != useCameraRotation2) {
            this.useCameraRotation = useCameraRotation2;
            onRotationChanged();
            updateVideoSizes();
        }
    }

    private void onRotationChanged() {
        int rotation = this.useCameraRotation ? OrientationHelper.cameraOrientation : 0;
        boolean z = this.mirror;
        if (z) {
            rotation = 360 - rotation;
        }
        int r = -rotation;
        if (this.useCameraRotation) {
            int i = this.screenRotation;
            if (i == 1) {
                r += z ? 90 : -90;
            } else if (i == 3) {
                r += z ? 270 : -270;
            }
        }
        this.eglRenderer.setRotation(r);
        this.eglRenderer.setMirror(this.mirror);
    }

    public void setRotation(float rotation) {
        super.setRotation(rotation);
    }

    public void setRotationY(float rotation) {
        super.setRotationY(rotation);
    }

    public void setRotationX(float rotation) {
        super.setRotationX(rotation);
    }

    private void postOrRun(Runnable r) {
        if (Thread.currentThread() == Looper.getMainLooper().getThread()) {
            r.run();
        } else {
            AndroidUtilities.runOnUIThread(r);
        }
    }

    private void logD(String string) {
        Logging.d("TextureViewRenderer", this.resourceName + ": " + string);
    }

    public void createBackgroundSurface(SurfaceTexture bluSurfaceTexturerRenderer) {
        this.eglRenderer.createBackgroundSurface(bluSurfaceTexturerRenderer);
    }

    public void setMaxTextureSize(int maxTextureSize2) {
        this.maxTextureSize = maxTextureSize2;
    }
}
