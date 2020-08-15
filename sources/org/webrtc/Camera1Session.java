package org.webrtc;

import android.content.Context;
import android.hardware.Camera;
import android.os.Handler;
import android.os.SystemClock;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.util.List;
import java.util.concurrent.TimeUnit;
import org.webrtc.Camera1Session;
import org.webrtc.CameraEnumerationAndroid;
import org.webrtc.CameraSession;

class Camera1Session implements CameraSession {
    private static final int NUMBER_OF_CAPTURE_BUFFERS = 3;
    private static final String TAG = "Camera1Session";
    private static final Histogram camera1ResolutionHistogram = Histogram.createEnumeration("WebRTC.Android.Camera1.Resolution", CameraEnumerationAndroid.COMMON_RESOLUTIONS.size());
    /* access modifiers changed from: private */
    public static final Histogram camera1StartTimeMsHistogram = Histogram.createCounts("WebRTC.Android.Camera1.StartTimeMs", 1, 10000, 50);
    private static final Histogram camera1StopTimeMsHistogram = Histogram.createCounts("WebRTC.Android.Camera1.StopTimeMs", 1, 10000, 50);
    private final Context applicationContext;
    /* access modifiers changed from: private */
    public final Camera camera;
    private final int cameraId;
    /* access modifiers changed from: private */
    public final Handler cameraThreadHandler = new Handler();
    /* access modifiers changed from: private */
    public final CameraEnumerationAndroid.CaptureFormat captureFormat;
    private final boolean captureToTexture;
    /* access modifiers changed from: private */
    public final long constructionTimeNs;
    /* access modifiers changed from: private */
    public final CameraSession.Events events;
    /* access modifiers changed from: private */
    public boolean firstFrameReported;
    private final Camera.CameraInfo info;
    private OrientationHelper orientationHelper;
    /* access modifiers changed from: private */
    public SessionState state;
    private final SurfaceTextureHelper surfaceTextureHelper;

    private enum SessionState {
        RUNNING,
        STOPPED
    }

    public static void create(CameraSession.CreateSessionCallback createSessionCallback, CameraSession.Events events2, boolean z, Context context, SurfaceTextureHelper surfaceTextureHelper2, int i, int i2, int i3, int i4) {
        CameraSession.CreateSessionCallback createSessionCallback2 = createSessionCallback;
        boolean z2 = z;
        int i5 = i;
        int i6 = i2;
        int i7 = i3;
        long nanoTime = System.nanoTime();
        Logging.d("Camera1Session", "Open camera " + i5);
        events2.onCameraOpening();
        try {
            Camera open = Camera.open(i);
            if (open == null) {
                CameraSession.FailureType failureType = CameraSession.FailureType.ERROR;
                createSessionCallback.onFailure(failureType, "android.hardware.Camera.open returned null for camera id = " + i5);
                return;
            }
            try {
                open.setPreviewTexture(surfaceTextureHelper2.getSurfaceTexture());
                Camera.CameraInfo cameraInfo = new Camera.CameraInfo();
                Camera.getCameraInfo(i5, cameraInfo);
                try {
                    Camera.Parameters parameters = open.getParameters();
                    CameraEnumerationAndroid.CaptureFormat findClosestCaptureFormat = findClosestCaptureFormat(parameters, i6, i7, i4);
                    updateCameraParameters(open, parameters, findClosestCaptureFormat, findClosestPictureSize(parameters, i6, i7), z2);
                    if (!z2) {
                        int frameSize = findClosestCaptureFormat.frameSize();
                        for (int i8 = 0; i8 < 3; i8++) {
                            open.addCallbackBuffer(ByteBuffer.allocateDirect(frameSize).array());
                        }
                    }
                    open.setDisplayOrientation(0);
                    createSessionCallback.onDone(new Camera1Session(events2, z, context, surfaceTextureHelper2, i, open, cameraInfo, findClosestCaptureFormat, nanoTime));
                } catch (RuntimeException e) {
                    open.release();
                    createSessionCallback.onFailure(CameraSession.FailureType.ERROR, e.getMessage());
                }
            } catch (IOException | RuntimeException e2) {
                open.release();
                createSessionCallback.onFailure(CameraSession.FailureType.ERROR, e2.getMessage());
            }
        } catch (RuntimeException e3) {
            createSessionCallback.onFailure(CameraSession.FailureType.ERROR, e3.getMessage());
        }
    }

    private static void updateCameraParameters(Camera camera2, Camera.Parameters parameters, CameraEnumerationAndroid.CaptureFormat captureFormat2, Size size, boolean z) {
        List<String> supportedFocusModes = parameters.getSupportedFocusModes();
        CameraEnumerationAndroid.CaptureFormat.FramerateRange framerateRange = captureFormat2.framerate;
        parameters.setPreviewFpsRange(framerateRange.min, framerateRange.max);
        parameters.setPreviewSize(captureFormat2.width, captureFormat2.height);
        parameters.setPictureSize(size.width, size.height);
        if (!z) {
            captureFormat2.getClass();
            parameters.setPreviewFormat(17);
        }
        if (parameters.isVideoStabilizationSupported()) {
            parameters.setVideoStabilization(true);
        }
        if (supportedFocusModes.contains("continuous-video")) {
            parameters.setFocusMode("continuous-video");
        }
        camera2.setParameters(parameters);
    }

    private static CameraEnumerationAndroid.CaptureFormat findClosestCaptureFormat(Camera.Parameters parameters, int i, int i2, int i3) {
        List<CameraEnumerationAndroid.CaptureFormat.FramerateRange> convertFramerates = Camera1Enumerator.convertFramerates(parameters.getSupportedPreviewFpsRange());
        Logging.d("Camera1Session", "Available fps ranges: " + convertFramerates);
        CameraEnumerationAndroid.CaptureFormat.FramerateRange closestSupportedFramerateRange = CameraEnumerationAndroid.getClosestSupportedFramerateRange(convertFramerates, i3);
        Size closestSupportedSize = CameraEnumerationAndroid.getClosestSupportedSize(Camera1Enumerator.convertSizes(parameters.getSupportedPreviewSizes()), i, i2);
        CameraEnumerationAndroid.reportCameraResolution(camera1ResolutionHistogram, closestSupportedSize);
        return new CameraEnumerationAndroid.CaptureFormat(closestSupportedSize.width, closestSupportedSize.height, closestSupportedFramerateRange);
    }

    private static Size findClosestPictureSize(Camera.Parameters parameters, int i, int i2) {
        return CameraEnumerationAndroid.getClosestSupportedSize(Camera1Enumerator.convertSizes(parameters.getSupportedPictureSizes()), i, i2);
    }

    private Camera1Session(CameraSession.Events events2, boolean z, Context context, SurfaceTextureHelper surfaceTextureHelper2, int i, Camera camera2, Camera.CameraInfo cameraInfo, CameraEnumerationAndroid.CaptureFormat captureFormat2, long j) {
        Logging.d("Camera1Session", "Create new camera1 session on camera " + i);
        this.events = events2;
        this.captureToTexture = z;
        this.applicationContext = context;
        this.surfaceTextureHelper = surfaceTextureHelper2;
        this.cameraId = i;
        this.camera = camera2;
        this.info = cameraInfo;
        this.captureFormat = captureFormat2;
        this.constructionTimeNs = j;
        this.orientationHelper = new OrientationHelper();
        surfaceTextureHelper2.setTextureSize(captureFormat2.width, captureFormat2.height);
        startCapturing();
    }

    public void stop() {
        Logging.d("Camera1Session", "Stop camera1 session on camera " + this.cameraId);
        checkIsOnCameraThread();
        if (this.state != SessionState.STOPPED) {
            long nanoTime = System.nanoTime();
            stopInternal();
            camera1StopTimeMsHistogram.addSample((int) TimeUnit.NANOSECONDS.toMillis(System.nanoTime() - nanoTime));
        }
    }

    private void startCapturing() {
        Logging.d("Camera1Session", "Start capturing");
        checkIsOnCameraThread();
        this.state = SessionState.RUNNING;
        this.camera.setErrorCallback(new Camera.ErrorCallback() {
            public void onError(int i, Camera camera) {
                String str;
                if (i == 100) {
                    str = "Camera server died!";
                } else {
                    str = "Camera error: " + i;
                }
                Logging.e("Camera1Session", str);
                Camera1Session.this.stopInternal();
                if (i == 2) {
                    Camera1Session.this.events.onCameraDisconnected(Camera1Session.this);
                } else {
                    Camera1Session.this.events.onCameraError(Camera1Session.this, str);
                }
            }
        });
        if (this.captureToTexture) {
            listenForTextureFrames();
        } else {
            listenForBytebufferFrames();
        }
        this.orientationHelper.start();
        try {
            this.camera.startPreview();
        } catch (RuntimeException e) {
            stopInternal();
            this.events.onCameraError(this, e.getMessage());
        }
    }

    /* access modifiers changed from: private */
    public void stopInternal() {
        Logging.d("Camera1Session", "Stop internal");
        checkIsOnCameraThread();
        SessionState sessionState = this.state;
        SessionState sessionState2 = SessionState.STOPPED;
        if (sessionState == sessionState2) {
            Logging.d("Camera1Session", "Camera is already stopped");
            return;
        }
        this.state = sessionState2;
        this.surfaceTextureHelper.stopListening();
        this.camera.stopPreview();
        this.camera.release();
        this.events.onCameraClosed(this);
        OrientationHelper orientationHelper2 = this.orientationHelper;
        if (orientationHelper2 != null) {
            orientationHelper2.stop();
        }
        Logging.d("Camera1Session", "Stop done");
    }

    private void listenForTextureFrames() {
        this.surfaceTextureHelper.startListening(new VideoSink() {
            public final void onFrame(VideoFrame videoFrame) {
                Camera1Session.this.lambda$listenForTextureFrames$0$Camera1Session(videoFrame);
            }
        });
    }

    public /* synthetic */ void lambda$listenForTextureFrames$0$Camera1Session(VideoFrame videoFrame) {
        checkIsOnCameraThread();
        if (this.state != SessionState.RUNNING) {
            Logging.d("Camera1Session", "Texture frame captured but camera is no longer running.");
            return;
        }
        boolean z = true;
        if (!this.firstFrameReported) {
            camera1StartTimeMsHistogram.addSample((int) TimeUnit.NANOSECONDS.toMillis(System.nanoTime() - this.constructionTimeNs));
            this.firstFrameReported = true;
        }
        TextureBufferImpl textureBufferImpl = (TextureBufferImpl) videoFrame.getBuffer();
        if (this.info.facing != 1) {
            z = false;
        }
        VideoFrame videoFrame2 = new VideoFrame(CameraSession.CC.createTextureBufferWithModifiedTransformMatrix(textureBufferImpl, z, 0), getFrameOrientation(), videoFrame.getTimestampNs());
        this.events.onFrameCaptured(this, videoFrame2);
        videoFrame2.release();
    }

    private void listenForBytebufferFrames() {
        this.camera.setPreviewCallbackWithBuffer(new Camera.PreviewCallback() {
            public void onPreviewFrame(byte[] bArr, Camera camera) {
                Camera1Session.this.checkIsOnCameraThread();
                if (camera != Camera1Session.this.camera) {
                    Logging.e("Camera1Session", "Callback from a different camera. This should never happen.");
                } else if (Camera1Session.this.state != SessionState.RUNNING) {
                    Logging.d("Camera1Session", "Bytebuffer frame captured but camera is no longer running.");
                } else {
                    long nanos = TimeUnit.MILLISECONDS.toNanos(SystemClock.elapsedRealtime());
                    if (!Camera1Session.this.firstFrameReported) {
                        Camera1Session.camera1StartTimeMsHistogram.addSample((int) TimeUnit.NANOSECONDS.toMillis(System.nanoTime() - Camera1Session.this.constructionTimeNs));
                        boolean unused = Camera1Session.this.firstFrameReported = true;
                    }
                    VideoFrame videoFrame = new VideoFrame(new NV21Buffer(bArr, Camera1Session.this.captureFormat.width, Camera1Session.this.captureFormat.height, new Runnable(bArr) {
                        public final /* synthetic */ byte[] f$1;

                        {
                            this.f$1 = r2;
                        }

                        public final void run() {
                            Camera1Session.AnonymousClass2.this.lambda$onPreviewFrame$1$Camera1Session$2(this.f$1);
                        }
                    }), Camera1Session.this.getFrameOrientation(), nanos);
                    Camera1Session.this.events.onFrameCaptured(Camera1Session.this, videoFrame);
                    videoFrame.release();
                }
            }

            public /* synthetic */ void lambda$onPreviewFrame$1$Camera1Session$2(byte[] bArr) {
                Camera1Session.this.cameraThreadHandler.post(new Runnable(bArr) {
                    public final /* synthetic */ byte[] f$1;

                    {
                        this.f$1 = r2;
                    }

                    public final void run() {
                        Camera1Session.AnonymousClass2.this.lambda$null$0$Camera1Session$2(this.f$1);
                    }
                });
            }

            public /* synthetic */ void lambda$null$0$Camera1Session$2(byte[] bArr) {
                if (Camera1Session.this.state == SessionState.RUNNING) {
                    Camera1Session.this.camera.addCallbackBuffer(bArr);
                }
            }
        });
    }

    /* access modifiers changed from: private */
    public int getFrameOrientation() {
        int orientation = this.orientationHelper.getOrientation();
        if (this.info.facing == 1) {
            orientation = 360 - orientation;
        }
        OrientationHelper.cameraRotation = orientation;
        return (this.info.orientation + orientation) % 360;
    }

    /* access modifiers changed from: private */
    public void checkIsOnCameraThread() {
        if (Thread.currentThread() != this.cameraThreadHandler.getLooper().getThread()) {
            throw new IllegalStateException("Wrong thread");
        }
    }
}
