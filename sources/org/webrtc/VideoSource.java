package org.webrtc;

public class VideoSource extends MediaSource {
    private final CapturerObserver capturerObserver = new CapturerObserver() {
        public void onCapturerStarted(boolean success) {
            VideoSource.this.nativeAndroidVideoTrackSource.setState(success);
            synchronized (VideoSource.this.videoProcessorLock) {
                boolean unused = VideoSource.this.isCapturerRunning = success;
                if (VideoSource.this.videoProcessor != null) {
                    VideoSource.this.videoProcessor.onCapturerStarted(success);
                }
            }
        }

        public void onCapturerStopped() {
            VideoSource.this.nativeAndroidVideoTrackSource.setState(false);
            synchronized (VideoSource.this.videoProcessorLock) {
                boolean unused = VideoSource.this.isCapturerRunning = false;
                if (VideoSource.this.videoProcessor != null) {
                    VideoSource.this.videoProcessor.onCapturerStopped();
                }
            }
        }

        /* JADX WARNING: Code restructure failed: missing block: B:10:0x0029, code lost:
            if (r1 == null) goto L_?;
         */
        /* JADX WARNING: Code restructure failed: missing block: B:11:0x002b, code lost:
            org.webrtc.VideoSource.access$000(r3.this$0).onFrameCaptured(r1);
            r1.release();
         */
        /* JADX WARNING: Code restructure failed: missing block: B:19:?, code lost:
            return;
         */
        /* JADX WARNING: Code restructure failed: missing block: B:20:?, code lost:
            return;
         */
        /* JADX WARNING: Code restructure failed: missing block: B:9:0x0025, code lost:
            r1 = org.webrtc.VideoProcessor.CC.applyFrameAdaptationParameters(r4, r0);
         */
        /* Code decompiled incorrectly, please refer to instructions dump. */
        public void onFrameCaptured(org.webrtc.VideoFrame r4) {
            /*
                r3 = this;
                org.webrtc.VideoSource r0 = org.webrtc.VideoSource.this
                org.webrtc.NativeAndroidVideoTrackSource r0 = r0.nativeAndroidVideoTrackSource
                org.webrtc.VideoProcessor$FrameAdaptationParameters r0 = r0.adaptFrame(r4)
                org.webrtc.VideoSource r1 = org.webrtc.VideoSource.this
                java.lang.Object r1 = r1.videoProcessorLock
                monitor-enter(r1)
                org.webrtc.VideoSource r2 = org.webrtc.VideoSource.this     // Catch:{ all -> 0x0038 }
                org.webrtc.VideoProcessor r2 = r2.videoProcessor     // Catch:{ all -> 0x0038 }
                if (r2 == 0) goto L_0x0024
                org.webrtc.VideoSource r2 = org.webrtc.VideoSource.this     // Catch:{ all -> 0x0038 }
                org.webrtc.VideoProcessor r2 = r2.videoProcessor     // Catch:{ all -> 0x0038 }
                r2.onFrameCaptured(r4, r0)     // Catch:{ all -> 0x0038 }
                monitor-exit(r1)     // Catch:{ all -> 0x0038 }
                return
            L_0x0024:
                monitor-exit(r1)     // Catch:{ all -> 0x0038 }
                org.webrtc.VideoFrame r1 = org.webrtc.VideoProcessor.CC.applyFrameAdaptationParameters(r4, r0)
                if (r1 == 0) goto L_0x0037
                org.webrtc.VideoSource r2 = org.webrtc.VideoSource.this
                org.webrtc.NativeAndroidVideoTrackSource r2 = r2.nativeAndroidVideoTrackSource
                r2.onFrameCaptured(r1)
                r1.release()
            L_0x0037:
                return
            L_0x0038:
                r2 = move-exception
                monitor-exit(r1)     // Catch:{ all -> 0x0038 }
                throw r2
            */
            throw new UnsupportedOperationException("Method not decompiled: org.webrtc.VideoSource.AnonymousClass1.onFrameCaptured(org.webrtc.VideoFrame):void");
        }
    };
    /* access modifiers changed from: private */
    public boolean isCapturerRunning;
    /* access modifiers changed from: private */
    public final NativeAndroidVideoTrackSource nativeAndroidVideoTrackSource;
    /* access modifiers changed from: private */
    public VideoProcessor videoProcessor;
    /* access modifiers changed from: private */
    public final Object videoProcessorLock = new Object();

    public static class AspectRatio {
        public static final AspectRatio UNDEFINED = new AspectRatio(0, 0);
        public final int height;
        public final int width;

        public AspectRatio(int width2, int height2) {
            this.width = width2;
            this.height = height2;
        }
    }

    public VideoSource(long nativeSource) {
        super(nativeSource);
        this.nativeAndroidVideoTrackSource = new NativeAndroidVideoTrackSource(nativeSource);
    }

    public void adaptOutputFormat(int width, int height, int fps) {
        int maxSide = Math.max(width, height);
        int minSide = Math.min(width, height);
        adaptOutputFormat(maxSide, minSide, minSide, maxSide, fps);
    }

    public void adaptOutputFormat(int landscapeWidth, int landscapeHeight, int portraitWidth, int portraitHeight, int fps) {
        adaptOutputFormat(new AspectRatio(landscapeWidth, landscapeHeight), Integer.valueOf(landscapeWidth * landscapeHeight), new AspectRatio(portraitWidth, portraitHeight), Integer.valueOf(portraitWidth * portraitHeight), Integer.valueOf(fps));
    }

    public void adaptOutputFormat(AspectRatio targetLandscapeAspectRatio, Integer maxLandscapePixelCount, AspectRatio targetPortraitAspectRatio, Integer maxPortraitPixelCount, Integer maxFps) {
        this.nativeAndroidVideoTrackSource.adaptOutputFormat(targetLandscapeAspectRatio, maxLandscapePixelCount, targetPortraitAspectRatio, maxPortraitPixelCount, maxFps);
    }

    public void setIsScreencast(boolean isScreencast) {
        this.nativeAndroidVideoTrackSource.setIsScreencast(isScreencast);
    }

    public void setVideoProcessor(VideoProcessor newVideoProcessor) {
        synchronized (this.videoProcessorLock) {
            VideoProcessor videoProcessor2 = this.videoProcessor;
            if (videoProcessor2 != null) {
                videoProcessor2.setSink((VideoSink) null);
                if (this.isCapturerRunning) {
                    this.videoProcessor.onCapturerStopped();
                }
            }
            this.videoProcessor = newVideoProcessor;
            if (newVideoProcessor != null) {
                newVideoProcessor.setSink(new VideoSource$$ExternalSyntheticLambda1(this));
                if (this.isCapturerRunning) {
                    newVideoProcessor.onCapturerStarted(true);
                }
            }
        }
    }

    /* renamed from: lambda$setVideoProcessor$0$org-webrtc-VideoSource  reason: not valid java name */
    public /* synthetic */ void m4129lambda$setVideoProcessor$0$orgwebrtcVideoSource(VideoFrame frame) {
        this.nativeAndroidVideoTrackSource.onFrameCaptured(frame);
    }

    /* renamed from: lambda$setVideoProcessor$1$org-webrtc-VideoSource  reason: not valid java name */
    public /* synthetic */ void m4130lambda$setVideoProcessor$1$orgwebrtcVideoSource(VideoFrame frame) {
        runWithReference(new VideoSource$$ExternalSyntheticLambda0(this, frame));
    }

    public CapturerObserver getCapturerObserver() {
        return this.capturerObserver;
    }

    /* access modifiers changed from: package-private */
    public long getNativeVideoTrackSource() {
        return getNativeMediaSource();
    }

    public void dispose() {
        setVideoProcessor((VideoProcessor) null);
        super.dispose();
    }
}
