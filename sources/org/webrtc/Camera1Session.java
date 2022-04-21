package org.webrtc;

import android.content.Context;
import android.hardware.Camera;
import android.os.Handler;
import android.os.SystemClock;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.util.List;
import java.util.concurrent.TimeUnit;
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

    public static void create(CameraSession.CreateSessionCallback callback, CameraSession.Events events2, boolean captureToTexture2, Context applicationContext2, SurfaceTextureHelper surfaceTextureHelper2, int cameraId2, int width, int height, int framerate) {
        CameraSession.CreateSessionCallback createSessionCallback = callback;
        boolean z = captureToTexture2;
        int i = cameraId2;
        int i2 = width;
        int i3 = height;
        long constructionTimeNs2 = System.nanoTime();
        Logging.d("Camera1Session", "Open camera " + i);
        events2.onCameraOpening();
        try {
            Camera camera2 = Camera.open(cameraId2);
            if (camera2 == null) {
                CameraSession.FailureType failureType = CameraSession.FailureType.ERROR;
                createSessionCallback.onFailure(failureType, "android.hardware.Camera.open returned null for camera id = " + i);
                return;
            }
            try {
                camera2.setPreviewTexture(surfaceTextureHelper2.getSurfaceTexture());
                Camera.CameraInfo info2 = new Camera.CameraInfo();
                Camera.getCameraInfo(i, info2);
                try {
                    Camera.Parameters parameters = camera2.getParameters();
                    CameraEnumerationAndroid.CaptureFormat captureFormat2 = findClosestCaptureFormat(parameters, i2, i3, framerate);
                    updateCameraParameters(camera2, parameters, captureFormat2, findClosestPictureSize(parameters, i2, i3), z);
                    if (!z) {
                        int frameSize = captureFormat2.frameSize();
                        for (int i4 = 0; i4 < 3; i4++) {
                            camera2.addCallbackBuffer(ByteBuffer.allocateDirect(frameSize).array());
                        }
                    }
                    camera2.setDisplayOrientation(0);
                    Camera.CameraInfo cameraInfo = info2;
                    Camera camera3 = camera2;
                    createSessionCallback.onDone(new Camera1Session(events2, captureToTexture2, applicationContext2, surfaceTextureHelper2, cameraId2, camera2, info2, captureFormat2, constructionTimeNs2));
                } catch (RuntimeException e) {
                    Camera.CameraInfo cameraInfo2 = info2;
                    camera2.release();
                    createSessionCallback.onFailure(CameraSession.FailureType.ERROR, e.getMessage());
                }
            } catch (IOException | RuntimeException e2) {
                camera2.release();
                createSessionCallback.onFailure(CameraSession.FailureType.ERROR, e2.getMessage());
            }
        } catch (RuntimeException e3) {
            createSessionCallback.onFailure(CameraSession.FailureType.ERROR, e3.getMessage());
        }
    }

    private static void updateCameraParameters(Camera camera2, Camera.Parameters parameters, CameraEnumerationAndroid.CaptureFormat captureFormat2, Size pictureSize, boolean captureToTexture2) {
        List<String> focusModes = parameters.getSupportedFocusModes();
        parameters.setPreviewFpsRange(captureFormat2.framerate.min, captureFormat2.framerate.max);
        parameters.setPreviewSize(captureFormat2.width, captureFormat2.height);
        parameters.setPictureSize(pictureSize.width, pictureSize.height);
        if (!captureToTexture2) {
            captureFormat2.getClass();
            parameters.setPreviewFormat(17);
        }
        if (parameters.isVideoStabilizationSupported()) {
            parameters.setVideoStabilization(true);
        }
        if (focusModes.contains("continuous-video")) {
            parameters.setFocusMode("continuous-video");
        }
        camera2.setParameters(parameters);
    }

    private static CameraEnumerationAndroid.CaptureFormat findClosestCaptureFormat(Camera.Parameters parameters, int width, int height, int framerate) {
        List<CameraEnumerationAndroid.CaptureFormat.FramerateRange> supportedFramerates = Camera1Enumerator.convertFramerates(parameters.getSupportedPreviewFpsRange());
        Logging.d("Camera1Session", "Available fps ranges: " + supportedFramerates);
        CameraEnumerationAndroid.CaptureFormat.FramerateRange fpsRange = CameraEnumerationAndroid.getClosestSupportedFramerateRange(supportedFramerates, framerate);
        Size previewSize = CameraEnumerationAndroid.getClosestSupportedSize(Camera1Enumerator.convertSizes(parameters.getSupportedPreviewSizes()), width, height);
        CameraEnumerationAndroid.reportCameraResolution(camera1ResolutionHistogram, previewSize);
        return new CameraEnumerationAndroid.CaptureFormat(previewSize.width, previewSize.height, fpsRange);
    }

    private static Size findClosestPictureSize(Camera.Parameters parameters, int width, int height) {
        return CameraEnumerationAndroid.getClosestSupportedSize(Camera1Enumerator.convertSizes(parameters.getSupportedPictureSizes()), width, height);
    }

    private Camera1Session(CameraSession.Events events2, boolean captureToTexture2, Context applicationContext2, SurfaceTextureHelper surfaceTextureHelper2, int cameraId2, Camera camera2, Camera.CameraInfo info2, CameraEnumerationAndroid.CaptureFormat captureFormat2, long constructionTimeNs2) {
        Logging.d("Camera1Session", "Create new camera1 session on camera " + cameraId2);
        this.events = events2;
        this.captureToTexture = captureToTexture2;
        this.applicationContext = applicationContext2;
        this.surfaceTextureHelper = surfaceTextureHelper2;
        this.cameraId = cameraId2;
        this.camera = camera2;
        this.info = info2;
        this.captureFormat = captureFormat2;
        this.constructionTimeNs = constructionTimeNs2;
        this.orientationHelper = new OrientationHelper();
        surfaceTextureHelper2.setTextureSize(captureFormat2.width, captureFormat2.height);
        startCapturing();
    }

    public void stop() {
        Logging.d("Camera1Session", "Stop camera1 session on camera " + this.cameraId);
        checkIsOnCameraThread();
        if (this.state != SessionState.STOPPED) {
            long stopStartTime = System.nanoTime();
            stopInternal();
            camera1StopTimeMsHistogram.addSample((int) TimeUnit.NANOSECONDS.toMillis(System.nanoTime() - stopStartTime));
        }
    }

    private void startCapturing() {
        Logging.d("Camera1Session", "Start capturing");
        checkIsOnCameraThread();
        this.state = SessionState.RUNNING;
        this.camera.setErrorCallback(new Camera.ErrorCallback() {
            public void onError(int error, Camera camera) {
                String errorMessage;
                if (error == 100) {
                    errorMessage = "Camera server died!";
                } else {
                    errorMessage = "Camera error: " + error;
                }
                Logging.e("Camera1Session", errorMessage);
                Camera1Session.this.stopInternal();
                if (error == 2) {
                    Camera1Session.this.events.onCameraDisconnected(Camera1Session.this);
                } else {
                    Camera1Session.this.events.onCameraError(Camera1Session.this, errorMessage);
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
        if (this.state == SessionState.STOPPED) {
            Logging.d("Camera1Session", "Camera is already stopped");
            return;
        }
        this.state = SessionState.STOPPED;
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
        this.surfaceTextureHelper.startListening(new Camera1Session$$ExternalSyntheticLambda0(this));
    }

    /* renamed from: lambda$listenForTextureFrames$0$org-webrtc-Camera1Session  reason: not valid java name */
    public /* synthetic */ void m4608lambda$listenForTextureFrames$0$orgwebrtcCamera1Session(VideoFrame frame) {
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
        TextureBufferImpl textureBufferImpl = (TextureBufferImpl) frame.getBuffer();
        if (this.info.facing != 1) {
            z = false;
        }
        VideoFrame modifiedFrame = new VideoFrame(CameraSession.CC.createTextureBufferWithModifiedTransformMatrix(textureBufferImpl, z, 0), getFrameOrientation(), frame.getTimestampNs());
        this.events.onFrameCaptured(this, modifiedFrame);
        modifiedFrame.release();
    }

    private void listenForBytebufferFrames() {
        this.camera.setPreviewCallbackWithBuffer(new Camera.PreviewCallback() {
            public void onPreviewFrame(byte[] data, Camera callbackCamera) {
                Camera1Session.this.checkIsOnCameraThread();
                if (callbackCamera != Camera1Session.this.camera) {
                    Logging.e("Camera1Session", "Callback from a different camera. This should never happen.");
                } else if (Camera1Session.this.state != SessionState.RUNNING) {
                    Logging.d("Camera1Session", "Bytebuffer frame captured but camera is no longer running.");
                } else {
                    long captureTimeNs = TimeUnit.MILLISECONDS.toNanos(SystemClock.elapsedRealtime());
                    if (!Camera1Session.this.firstFrameReported) {
                        Camera1Session.camera1StartTimeMsHistogram.addSample((int) TimeUnit.NANOSECONDS.toMillis(System.nanoTime() - Camera1Session.this.constructionTimeNs));
                        boolean unused = Camera1Session.this.firstFrameReported = true;
                    }
                    VideoFrame frame = new VideoFrame(new NV21Buffer(data, Camera1Session.this.captureFormat.width, Camera1Session.this.captureFormat.height, new Camera1Session$2$$ExternalSyntheticLambda1(this, data)), Camera1Session.this.getFrameOrientation(), captureTimeNs);
                    Camera1Session.this.events.onFrameCaptured(Camera1Session.this, frame);
                    frame.release();
                }
            }

            /* renamed from: lambda$onPreviewFrame$1$org-webrtc-Camera1Session$2  reason: not valid java name */
            public /* synthetic */ void m4610lambda$onPreviewFrame$1$orgwebrtcCamera1Session$2(byte[] data) {
                Camera1Session.this.cameraThreadHandler.post(new Camera1Session$2$$ExternalSyntheticLambda0(this, data));
            }

            /* renamed from: lambda$onPreviewFrame$0$org-webrtc-Camera1Session$2  reason: not valid java name */
            public /* synthetic */ void m4609lambda$onPreviewFrame$0$orgwebrtcCamera1Session$2(byte[] data) {
                if (Camera1Session.this.state == SessionState.RUNNING) {
                    Camera1Session.this.camera.addCallbackBuffer(data);
                }
            }
        });
    }

    /* access modifiers changed from: private */
    public int getFrameOrientation() {
        int rotation = this.orientationHelper.getOrientation();
        OrientationHelper.cameraOrientation = rotation;
        if (this.info.facing == 1) {
            rotation = 360 - rotation;
        }
        OrientationHelper.cameraRotation = rotation;
        return (this.info.orientation + rotation) % 360;
    }

    /* access modifiers changed from: private */
    public void checkIsOnCameraThread() {
        if (Thread.currentThread() != this.cameraThreadHandler.getLooper().getThread()) {
            throw new IllegalStateException("Wrong thread");
        }
    }
}
