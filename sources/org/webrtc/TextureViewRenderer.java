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
    private boolean rotateTextureWitchScreen;
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

    public void setBackgroundRenderer(TextureView textureView) {
        this.backgroundRenderer = textureView;
        textureView.setSurfaceTextureListener(new TextureView.SurfaceTextureListener() {
            public void onSurfaceTextureSizeChanged(SurfaceTexture surfaceTexture, int i, int i2) {
            }

            public void onSurfaceTextureUpdated(SurfaceTexture surfaceTexture) {
            }

            public void onSurfaceTextureAvailable(SurfaceTexture surfaceTexture, int i, int i2) {
                TextureViewRenderer.this.createBackgroundSurface(surfaceTexture);
            }

            public boolean onSurfaceTextureDestroyed(SurfaceTexture surfaceTexture) {
                ThreadUtils.checkIsOnMainThread();
                TextureViewRenderer.this.eglRenderer.releaseEglSurface((Runnable) null, true);
                return false;
            }
        });
    }

    public void clearFirstFrame() {
        TextureEglRenderer textureEglRenderer = this.eglRenderer;
        textureEglRenderer.firstFrameRendered = false;
        boolean unused = textureEglRenderer.isFirstFrameRendered = false;
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
            releaseEglSurface(new Theme$$ExternalSyntheticLambda2(countDownLatch), false);
            ThreadUtils.awaitUninterruptibly(countDownLatch);
            return true;
        }

        /* JADX WARNING: Code restructure failed: missing block: B:18:0x0087, code lost:
            return;
         */
        /* Code decompiled incorrectly, please refer to instructions dump. */
        private void updateFrameDimensionsAndReportEvents(org.webrtc.VideoFrame r6) {
            /*
                r5 = this;
                java.lang.Object r0 = r5.layoutLock
                monitor-enter(r0)
                boolean r1 = r5.isRenderingPaused     // Catch:{ all -> 0x0088 }
                if (r1 == 0) goto L_0x0009
                monitor-exit(r0)     // Catch:{ all -> 0x0088 }
                return
            L_0x0009:
                int r1 = r5.rotatedFrameWidth     // Catch:{ all -> 0x0088 }
                int r2 = r6.getRotatedWidth()     // Catch:{ all -> 0x0088 }
                if (r1 != r2) goto L_0x0021
                int r1 = r5.rotatedFrameHeight     // Catch:{ all -> 0x0088 }
                int r2 = r6.getRotatedHeight()     // Catch:{ all -> 0x0088 }
                if (r1 != r2) goto L_0x0021
                int r1 = r5.frameRotation     // Catch:{ all -> 0x0088 }
                int r2 = r6.getRotation()     // Catch:{ all -> 0x0088 }
                if (r1 == r2) goto L_0x0086
            L_0x0021:
                java.lang.StringBuilder r1 = new java.lang.StringBuilder     // Catch:{ all -> 0x0088 }
                r1.<init>()     // Catch:{ all -> 0x0088 }
                java.lang.String r2 = "Reporting frame resolution changed to "
                r1.append(r2)     // Catch:{ all -> 0x0088 }
                org.webrtc.VideoFrame$Buffer r2 = r6.getBuffer()     // Catch:{ all -> 0x0088 }
                int r2 = r2.getWidth()     // Catch:{ all -> 0x0088 }
                r1.append(r2)     // Catch:{ all -> 0x0088 }
                java.lang.String r2 = "x"
                r1.append(r2)     // Catch:{ all -> 0x0088 }
                org.webrtc.VideoFrame$Buffer r2 = r6.getBuffer()     // Catch:{ all -> 0x0088 }
                int r2 = r2.getHeight()     // Catch:{ all -> 0x0088 }
                r1.append(r2)     // Catch:{ all -> 0x0088 }
                java.lang.String r2 = " with rotation "
                r1.append(r2)     // Catch:{ all -> 0x0088 }
                int r2 = r6.getRotation()     // Catch:{ all -> 0x0088 }
                r1.append(r2)     // Catch:{ all -> 0x0088 }
                java.lang.String r1 = r1.toString()     // Catch:{ all -> 0x0088 }
                r5.logD(r1)     // Catch:{ all -> 0x0088 }
                org.webrtc.RendererCommon$RendererEvents r1 = r5.rendererEvents     // Catch:{ all -> 0x0088 }
                if (r1 == 0) goto L_0x0074
                org.webrtc.VideoFrame$Buffer r2 = r6.getBuffer()     // Catch:{ all -> 0x0088 }
                int r2 = r2.getWidth()     // Catch:{ all -> 0x0088 }
                org.webrtc.VideoFrame$Buffer r3 = r6.getBuffer()     // Catch:{ all -> 0x0088 }
                int r3 = r3.getHeight()     // Catch:{ all -> 0x0088 }
                int r4 = r6.getRotation()     // Catch:{ all -> 0x0088 }
                r1.onFrameResolutionChanged(r2, r3, r4)     // Catch:{ all -> 0x0088 }
            L_0x0074:
                int r1 = r6.getRotatedWidth()     // Catch:{ all -> 0x0088 }
                r5.rotatedFrameWidth = r1     // Catch:{ all -> 0x0088 }
                int r1 = r6.getRotatedHeight()     // Catch:{ all -> 0x0088 }
                r5.rotatedFrameHeight = r1     // Catch:{ all -> 0x0088 }
                int r6 = r6.getRotation()     // Catch:{ all -> 0x0088 }
                r5.frameRotation = r6     // Catch:{ all -> 0x0088 }
            L_0x0086:
                monitor-exit(r0)     // Catch:{ all -> 0x0088 }
                return
            L_0x0088:
                r6 = move-exception
                monitor-exit(r0)     // Catch:{ all -> 0x0088 }
                throw r6
            */
            throw new UnsupportedOperationException("Method not decompiled: org.webrtc.TextureViewRenderer.TextureEglRenderer.updateFrameDimensionsAndReportEvents(org.webrtc.VideoFrame):void");
        }

        private void logD(String str) {
            Logging.d("TextureEglRenderer", this.name + ": " + str);
        }

        /* access modifiers changed from: protected */
        public void onFirstFrameRendered() {
            AndroidUtilities.runOnUIThread(new TextureViewRenderer$TextureEglRenderer$$ExternalSyntheticLambda0(this));
        }

        /* access modifiers changed from: private */
        public /* synthetic */ void lambda$onFirstFrameRendered$0() {
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
        OrientationHelper orientationHelper2 = this.orientationHelper;
        if (orientationHelper2 != null) {
            orientationHelper2.stop();
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
            AnonymousClass2 r1 = new OrientationHelper() {
                /* access modifiers changed from: protected */
                public void onOrientationUpdate(int i) {
                    if (!TextureViewRenderer.this.isCamera) {
                        TextureViewRenderer.this.updateRotation();
                    }
                }
            };
            this.orientationHelper = r1;
            r1.start();
        }
    }

    public void setEnableHardwareScaler(boolean z) {
        ThreadUtils.checkIsOnMainThread();
        this.enableFixedSize = z;
        updateSurfaceSize();
    }

    /* access modifiers changed from: private */
    public void updateRotation() {
        View view;
        float f;
        float f2;
        float f3;
        if (this.orientationHelper != null && this.rotatedFrameWidth != 0 && this.rotatedFrameHeight != 0 && (view = (View) getParent()) != null) {
            int orientation = this.orientationHelper.getOrientation();
            float measuredWidth = (float) getMeasuredWidth();
            float measuredHeight = (float) getMeasuredHeight();
            float measuredWidth2 = (float) view.getMeasuredWidth();
            float measuredHeight2 = (float) view.getMeasuredHeight();
            if (orientation == 90 || orientation == 270) {
                f = measuredWidth;
                f2 = measuredHeight;
            } else {
                f2 = measuredWidth;
                f = measuredHeight;
            }
            if (f2 < f) {
                f3 = Math.max(f2 / measuredWidth, f / measuredHeight);
            } else {
                f3 = Math.min(f2 / measuredWidth, f / measuredHeight);
            }
            float f4 = f2 * f3;
            float f5 = f * f3;
            if (Math.abs((f4 / f5) - (measuredWidth2 / measuredHeight2)) < 0.1f) {
                f3 *= Math.max(measuredWidth2 / f4, measuredHeight2 / f5);
            }
            if (orientation == 270) {
                orientation = -90;
            }
            animate().scaleX(f3).scaleY(f3).rotation((float) (-orientation)).setDuration(180).start();
        }
    }

    public void setMirror(boolean z) {
        if (this.mirror != z) {
            this.mirror = z;
            if (this.rotateTextureWitchScreen) {
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

    public void onFrame(VideoFrame videoFrame) {
        this.eglRenderer.onFrame(videoFrame);
    }

    /* access modifiers changed from: protected */
    public void onMeasure(int i, int i2) {
        Point point;
        ThreadUtils.checkIsOnMainThread();
        if (!this.isCamera && this.rotateTextureWitchScreen) {
            updateVideoSizes();
        }
        int i3 = this.maxTextureSize;
        if (i3 > 0) {
            point = this.videoLayoutMeasure.measure(this.isCamera, View.MeasureSpec.makeMeasureSpec(Math.min(i3, View.MeasureSpec.getSize(i)), View.MeasureSpec.getMode(i)), View.MeasureSpec.makeMeasureSpec(Math.min(this.maxTextureSize, View.MeasureSpec.getSize(i2)), View.MeasureSpec.getMode(i2)), this.rotatedFrameWidth, this.rotatedFrameHeight);
        } else {
            point = this.videoLayoutMeasure.measure(this.isCamera, i, i2, this.rotatedFrameWidth, this.rotatedFrameHeight);
        }
        setMeasuredDimension(point.x, point.y);
        if (!(this.rotatedFrameWidth == 0 || this.rotatedFrameHeight == 0)) {
            this.eglRenderer.setLayoutAspectRatio(((float) getMeasuredWidth()) / ((float) getMeasuredHeight()));
        }
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
            i2 = (int) (((float) i2) / width);
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
        } catch (Resources.NotFoundException unused) {
            return "";
        }
    }

    public void clearImage() {
        this.eglRenderer.clearImage();
        boolean unused = this.eglRenderer.isFirstFrameRendered = false;
    }

    public void setParentSink(VideoSink videoSink) {
        this.parentSink = videoSink;
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

    /*  JADX ERROR: IndexOutOfBoundsException in pass: RegionMakerVisitor
        java.lang.IndexOutOfBoundsException: Index: 0, Size: 0
        	at java.util.ArrayList.rangeCheck(ArrayList.java:659)
        	at java.util.ArrayList.get(ArrayList.java:435)
        	at jadx.core.dex.nodes.InsnNode.getArg(InsnNode.java:101)
        	at jadx.core.dex.visitors.regions.RegionMaker.traverseMonitorExits(RegionMaker.java:611)
        	at jadx.core.dex.visitors.regions.RegionMaker.traverseMonitorExits(RegionMaker.java:619)
        	at jadx.core.dex.visitors.regions.RegionMaker.traverseMonitorExits(RegionMaker.java:619)
        	at jadx.core.dex.visitors.regions.RegionMaker.traverseMonitorExits(RegionMaker.java:619)
        	at jadx.core.dex.visitors.regions.RegionMaker.traverseMonitorExits(RegionMaker.java:619)
        	at jadx.core.dex.visitors.regions.RegionMaker.traverseMonitorExits(RegionMaker.java:619)
        	at jadx.core.dex.visitors.regions.RegionMaker.traverseMonitorExits(RegionMaker.java:619)
        	at jadx.core.dex.visitors.regions.RegionMaker.processMonitorEnter(RegionMaker.java:561)
        	at jadx.core.dex.visitors.regions.RegionMaker.traverse(RegionMaker.java:133)
        	at jadx.core.dex.visitors.regions.RegionMaker.makeRegion(RegionMaker.java:86)
        	at jadx.core.dex.visitors.regions.RegionMaker.processIf(RegionMaker.java:698)
        	at jadx.core.dex.visitors.regions.RegionMaker.traverse(RegionMaker.java:123)
        	at jadx.core.dex.visitors.regions.RegionMaker.makeRegion(RegionMaker.java:86)
        	at jadx.core.dex.visitors.regions.RegionMakerVisitor.visit(RegionMakerVisitor.java:49)
        */
    public void onFrameResolutionChanged(int r8, int r9, int r10) {
        /*
            r7 = this;
            org.webrtc.RendererCommon$RendererEvents r0 = r7.rendererEvents
            if (r0 == 0) goto L_0x0007
            r0.onFrameResolutionChanged(r8, r9, r10)
        L_0x0007:
            r7.textureRotation = r10
            boolean r0 = r7.rotateTextureWitchScreen
            r1 = -180(0xffffffffffffff4c, float:NaN)
            r2 = 180(0xb4, float:2.52E-43)
            if (r0 == 0) goto L_0x0039
            boolean r10 = r7.isCamera
            if (r10 == 0) goto L_0x0018
            r7.onRotationChanged()
        L_0x0018:
            boolean r10 = r7.useCameraRotation
            if (r10 == 0) goto L_0x0026
            int r10 = r7.screenRotation
            if (r10 != 0) goto L_0x0022
            r0 = r9
            goto L_0x0023
        L_0x0022:
            r0 = r8
        L_0x0023:
            if (r10 != 0) goto L_0x005b
            goto L_0x0059
        L_0x0026:
            int r10 = r7.textureRotation
            if (r10 == 0) goto L_0x0031
            if (r10 == r2) goto L_0x0031
            if (r10 != r1) goto L_0x002f
            goto L_0x0031
        L_0x002f:
            r0 = r9
            goto L_0x0032
        L_0x0031:
            r0 = r8
        L_0x0032:
            if (r10 == 0) goto L_0x005b
            if (r10 == r2) goto L_0x005b
            if (r10 != r1) goto L_0x0059
            goto L_0x005b
        L_0x0039:
            boolean r0 = r7.isCamera
            if (r0 == 0) goto L_0x0045
            org.webrtc.TextureViewRenderer$TextureEglRenderer r0 = r7.eglRenderer
            int r3 = org.webrtc.OrientationHelper.cameraRotation
            int r3 = -r3
            r0.setRotation(r3)
        L_0x0045:
            int r0 = org.webrtc.OrientationHelper.cameraOrientation
            int r10 = r10 - r0
            if (r10 == 0) goto L_0x0051
            if (r10 == r2) goto L_0x0051
            if (r10 != r1) goto L_0x004f
            goto L_0x0051
        L_0x004f:
            r0 = r9
            goto L_0x0052
        L_0x0051:
            r0 = r8
        L_0x0052:
            if (r10 == 0) goto L_0x005b
            if (r10 == r2) goto L_0x005b
            if (r10 != r1) goto L_0x0059
            goto L_0x005b
        L_0x0059:
            r10 = r8
            goto L_0x005c
        L_0x005b:
            r10 = r9
        L_0x005c:
            r6 = r10
            r5 = r0
            org.webrtc.TextureViewRenderer$TextureEglRenderer r10 = r7.eglRenderer
            java.lang.Object r10 = r10.layoutLock
            monitor-enter(r10)
            java.lang.Runnable r0 = r7.updateScreenRunnable     // Catch:{ all -> 0x007c }
            if (r0 == 0) goto L_0x006c
            org.telegram.messenger.AndroidUtilities.cancelRunOnUIThread(r0)     // Catch:{ all -> 0x007c }
        L_0x006c:
            org.webrtc.TextureViewRenderer$$ExternalSyntheticLambda1 r0 = new org.webrtc.TextureViewRenderer$$ExternalSyntheticLambda1     // Catch:{ all -> 0x007c }
            r1 = r0
            r2 = r7
            r3 = r8
            r4 = r9
            r1.<init>(r2, r3, r4, r5, r6)     // Catch:{ all -> 0x007c }
            r7.updateScreenRunnable = r0     // Catch:{ all -> 0x007c }
            r7.postOrRun(r0)     // Catch:{ all -> 0x007c }
            monitor-exit(r10)     // Catch:{ all -> 0x007c }
            return
        L_0x007c:
            r8 = move-exception
            monitor-exit(r10)     // Catch:{ all -> 0x007c }
            throw r8
        */
        throw new UnsupportedOperationException("Method not decompiled: org.webrtc.TextureViewRenderer.onFrameResolutionChanged(int, int, int):void");
    }

    /* access modifiers changed from: private */
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
        int i2;
        int i3 = this.videoHeight;
        if (i3 != 0 && (i = this.videoWidth) != 0) {
            if (!this.rotateTextureWitchScreen) {
                int i4 = this.textureRotation - OrientationHelper.cameraOrientation;
                int i5 = (i4 == 0 || i4 == 180 || i4 == -180) ? this.videoWidth : this.videoHeight;
                i3 = (i4 == 0 || i4 == 180 || i4 == -180) ? this.videoHeight : this.videoWidth;
                i2 = i5;
            } else if (this.useCameraRotation) {
                int i6 = this.screenRotation;
                i2 = i6 == 0 ? i3 : i;
                if (i6 == 0) {
                    i3 = i;
                }
            } else {
                int i7 = this.textureRotation;
                int i8 = (i7 == 0 || i7 == 180 || i7 == -180) ? i : i3;
                if (!(i7 == 0 || i7 == 180 || i7 == -180)) {
                    i3 = i;
                }
                i2 = i8;
            }
            if (this.rotatedFrameWidth != i2 || this.rotatedFrameHeight != i3) {
                synchronized (this.eglRenderer.layoutLock) {
                    Runnable runnable = this.updateScreenRunnable;
                    if (runnable != null) {
                        AndroidUtilities.cancelRunOnUIThread(runnable);
                    }
                    TextureViewRenderer$$ExternalSyntheticLambda0 textureViewRenderer$$ExternalSyntheticLambda0 = new TextureViewRenderer$$ExternalSyntheticLambda0(this, i2, i3);
                    this.updateScreenRunnable = textureViewRenderer$$ExternalSyntheticLambda0;
                    postOrRun(textureViewRenderer$$ExternalSyntheticLambda0);
                }
            }
        }
    }

    /* access modifiers changed from: private */
    public /* synthetic */ void lambda$updateVideoSizes$1(int i, int i2) {
        this.updateScreenRunnable = null;
        this.rotatedFrameWidth = i;
        this.rotatedFrameHeight = i2;
        updateSurfaceSize();
        requestLayout();
    }

    public void setRotateTextureWitchScreen(boolean z) {
        if (this.rotateTextureWitchScreen != z) {
            this.rotateTextureWitchScreen = z;
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
