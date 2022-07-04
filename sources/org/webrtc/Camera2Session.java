package org.webrtc;

import android.content.Context;
import android.hardware.camera2.CameraAccessException;
import android.hardware.camera2.CameraCaptureSession;
import android.hardware.camera2.CameraCharacteristics;
import android.hardware.camera2.CameraDevice;
import android.hardware.camera2.CameraManager;
import android.hardware.camera2.CaptureFailure;
import android.hardware.camera2.CaptureRequest;
import android.os.Handler;
import android.util.Range;
import android.view.Surface;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.TimeUnit;
import org.webrtc.CameraEnumerationAndroid;
import org.webrtc.CameraSession;

class Camera2Session implements CameraSession {
    private static final String TAG = "Camera2Session";
    private static final Histogram camera2ResolutionHistogram = Histogram.createEnumeration("WebRTC.Android.Camera2.Resolution", CameraEnumerationAndroid.COMMON_RESOLUTIONS.size());
    /* access modifiers changed from: private */
    public static final Histogram camera2StartTimeMsHistogram = Histogram.createCounts("WebRTC.Android.Camera2.StartTimeMs", 1, 10000, 50);
    private static final Histogram camera2StopTimeMsHistogram = Histogram.createCounts("WebRTC.Android.Camera2.StopTimeMs", 1, 10000, 50);
    private final Context applicationContext;
    /* access modifiers changed from: private */
    public final CameraSession.CreateSessionCallback callback;
    /* access modifiers changed from: private */
    public CameraCharacteristics cameraCharacteristics;
    /* access modifiers changed from: private */
    public CameraDevice cameraDevice;
    private final String cameraId;
    private final CameraManager cameraManager;
    /* access modifiers changed from: private */
    public int cameraOrientation;
    /* access modifiers changed from: private */
    public final Handler cameraThreadHandler;
    /* access modifiers changed from: private */
    public CameraEnumerationAndroid.CaptureFormat captureFormat;
    /* access modifiers changed from: private */
    public CameraCaptureSession captureSession;
    /* access modifiers changed from: private */
    public final long constructionTimeNs;
    /* access modifiers changed from: private */
    public final CameraSession.Events events;
    /* access modifiers changed from: private */
    public boolean firstFrameReported;
    /* access modifiers changed from: private */
    public int fpsUnitFactor;
    private final int framerate;
    private final int height;
    /* access modifiers changed from: private */
    public boolean isCameraFrontFacing;
    private OrientationHelper orientationHelper;
    /* access modifiers changed from: private */
    public SessionState state = SessionState.RUNNING;
    /* access modifiers changed from: private */
    public Surface surface;
    /* access modifiers changed from: private */
    public final SurfaceTextureHelper surfaceTextureHelper;
    private final int width;

    private enum SessionState {
        RUNNING,
        STOPPED
    }

    private class CameraStateCallback extends CameraDevice.StateCallback {
        private CameraStateCallback() {
        }

        private String getErrorDescription(int errorCode) {
            switch (errorCode) {
                case 1:
                    return "Camera device is in use already.";
                case 2:
                    return "Camera device could not be opened because there are too many other open camera devices.";
                case 3:
                    return "Camera device could not be opened due to a device policy.";
                case 4:
                    return "Camera device has encountered a fatal error.";
                case 5:
                    return "Camera service has encountered a fatal error.";
                default:
                    return "Unknown camera error: " + errorCode;
            }
        }

        public void onDisconnected(CameraDevice camera) {
            Camera2Session.this.checkIsOnCameraThread();
            boolean startFailure = Camera2Session.this.captureSession == null && Camera2Session.this.state != SessionState.STOPPED;
            SessionState unused = Camera2Session.this.state = SessionState.STOPPED;
            Camera2Session.this.stopInternal();
            if (startFailure) {
                Camera2Session.this.callback.onFailure(CameraSession.FailureType.DISCONNECTED, "Camera disconnected / evicted.");
            } else {
                Camera2Session.this.events.onCameraDisconnected(Camera2Session.this);
            }
        }

        public void onError(CameraDevice camera, int errorCode) {
            Camera2Session.this.checkIsOnCameraThread();
            Camera2Session.this.reportError(getErrorDescription(errorCode));
        }

        public void onOpened(CameraDevice camera) {
            Camera2Session.this.checkIsOnCameraThread();
            Logging.d("Camera2Session", "Camera opened.");
            CameraDevice unused = Camera2Session.this.cameraDevice = camera;
            Camera2Session.this.surfaceTextureHelper.setTextureSize(Camera2Session.this.captureFormat.width, Camera2Session.this.captureFormat.height);
            Surface unused2 = Camera2Session.this.surface = new Surface(Camera2Session.this.surfaceTextureHelper.getSurfaceTexture());
            try {
                camera.createCaptureSession(Arrays.asList(new Surface[]{Camera2Session.this.surface}), new CaptureSessionCallback(), Camera2Session.this.cameraThreadHandler);
            } catch (CameraAccessException e) {
                Camera2Session camera2Session = Camera2Session.this;
                camera2Session.reportError("Failed to create capture session. " + e);
            }
        }

        public void onClosed(CameraDevice camera) {
            Camera2Session.this.checkIsOnCameraThread();
            Logging.d("Camera2Session", "Camera device closed.");
            Camera2Session.this.events.onCameraClosed(Camera2Session.this);
        }
    }

    private class CaptureSessionCallback extends CameraCaptureSession.StateCallback {
        private CaptureSessionCallback() {
        }

        public void onConfigureFailed(CameraCaptureSession session) {
            Camera2Session.this.checkIsOnCameraThread();
            session.close();
            Camera2Session.this.reportError("Failed to configure capture session.");
        }

        public void onConfigured(CameraCaptureSession session) {
            Camera2Session.this.checkIsOnCameraThread();
            Logging.d("Camera2Session", "Camera capture session configured.");
            CameraCaptureSession unused = Camera2Session.this.captureSession = session;
            try {
                CaptureRequest.Builder captureRequestBuilder = Camera2Session.this.cameraDevice.createCaptureRequest(3);
                captureRequestBuilder.set(CaptureRequest.CONTROL_AE_TARGET_FPS_RANGE, new Range(Integer.valueOf(Camera2Session.this.captureFormat.framerate.min / Camera2Session.this.fpsUnitFactor), Integer.valueOf(Camera2Session.this.captureFormat.framerate.max / Camera2Session.this.fpsUnitFactor)));
                captureRequestBuilder.set(CaptureRequest.CONTROL_AE_MODE, 1);
                captureRequestBuilder.set(CaptureRequest.CONTROL_AE_LOCK, false);
                chooseStabilizationMode(captureRequestBuilder);
                chooseFocusMode(captureRequestBuilder);
                captureRequestBuilder.addTarget(Camera2Session.this.surface);
                session.setRepeatingRequest(captureRequestBuilder.build(), new CameraCaptureCallback(), Camera2Session.this.cameraThreadHandler);
                Camera2Session.this.surfaceTextureHelper.startListening(new Camera2Session$CaptureSessionCallback$$ExternalSyntheticLambda0(this));
                Logging.d("Camera2Session", "Camera device successfully started.");
                Camera2Session.this.callback.onDone(Camera2Session.this);
            } catch (CameraAccessException e) {
                Camera2Session camera2Session = Camera2Session.this;
                camera2Session.reportError("Failed to start capture request. " + e);
            }
        }

        /* renamed from: lambda$onConfigured$0$org-webrtc-Camera2Session$CaptureSessionCallback  reason: not valid java name */
        public /* synthetic */ void m1644x8bd2b057(VideoFrame frame) {
            Camera2Session.this.checkIsOnCameraThread();
            if (Camera2Session.this.state != SessionState.RUNNING) {
                Logging.d("Camera2Session", "Texture frame captured but camera is no longer running.");
                return;
            }
            if (!Camera2Session.this.firstFrameReported) {
                boolean unused = Camera2Session.this.firstFrameReported = true;
                Camera2Session.camera2StartTimeMsHistogram.addSample((int) TimeUnit.NANOSECONDS.toMillis(System.nanoTime() - Camera2Session.this.constructionTimeNs));
            }
            VideoFrame modifiedFrame = new VideoFrame(CameraSession.CC.createTextureBufferWithModifiedTransformMatrix((TextureBufferImpl) frame.getBuffer(), Camera2Session.this.isCameraFrontFacing, -Camera2Session.this.cameraOrientation), Camera2Session.this.getFrameOrientation(), frame.getTimestampNs());
            Camera2Session.this.events.onFrameCaptured(Camera2Session.this, modifiedFrame);
            modifiedFrame.release();
        }

        private void chooseStabilizationMode(CaptureRequest.Builder captureRequestBuilder) {
            int[] availableOpticalStabilization = (int[]) Camera2Session.this.cameraCharacteristics.get(CameraCharacteristics.LENS_INFO_AVAILABLE_OPTICAL_STABILIZATION);
            if (availableOpticalStabilization != null) {
                for (int mode : availableOpticalStabilization) {
                    if (mode == 1) {
                        captureRequestBuilder.set(CaptureRequest.LENS_OPTICAL_STABILIZATION_MODE, 1);
                        captureRequestBuilder.set(CaptureRequest.CONTROL_VIDEO_STABILIZATION_MODE, 0);
                        Logging.d("Camera2Session", "Using optical stabilization.");
                        return;
                    }
                }
            }
            for (int mode2 : (int[]) Camera2Session.this.cameraCharacteristics.get(CameraCharacteristics.CONTROL_AVAILABLE_VIDEO_STABILIZATION_MODES)) {
                if (mode2 == 1) {
                    captureRequestBuilder.set(CaptureRequest.CONTROL_VIDEO_STABILIZATION_MODE, 1);
                    captureRequestBuilder.set(CaptureRequest.LENS_OPTICAL_STABILIZATION_MODE, 0);
                    Logging.d("Camera2Session", "Using video stabilization.");
                    return;
                }
            }
            Logging.d("Camera2Session", "Stabilization not available.");
        }

        private void chooseFocusMode(CaptureRequest.Builder captureRequestBuilder) {
            for (int mode : (int[]) Camera2Session.this.cameraCharacteristics.get(CameraCharacteristics.CONTROL_AF_AVAILABLE_MODES)) {
                if (mode == 3) {
                    captureRequestBuilder.set(CaptureRequest.CONTROL_AF_MODE, 3);
                    Logging.d("Camera2Session", "Using continuous video auto-focus.");
                    return;
                }
            }
            Logging.d("Camera2Session", "Auto-focus is not available.");
        }
    }

    private static class CameraCaptureCallback extends CameraCaptureSession.CaptureCallback {
        private CameraCaptureCallback() {
        }

        public void onCaptureFailed(CameraCaptureSession session, CaptureRequest request, CaptureFailure failure) {
            Logging.d("Camera2Session", "Capture failed: " + failure);
        }
    }

    public static void create(CameraSession.CreateSessionCallback callback2, CameraSession.Events events2, Context applicationContext2, CameraManager cameraManager2, SurfaceTextureHelper surfaceTextureHelper2, String cameraId2, int width2, int height2, int framerate2) {
        new Camera2Session(callback2, events2, applicationContext2, cameraManager2, surfaceTextureHelper2, cameraId2, width2, height2, framerate2);
    }

    private Camera2Session(CameraSession.CreateSessionCallback callback2, CameraSession.Events events2, Context applicationContext2, CameraManager cameraManager2, SurfaceTextureHelper surfaceTextureHelper2, String cameraId2, int width2, int height2, int framerate2) {
        Logging.d("Camera2Session", "Create new camera2 session on camera " + cameraId2);
        this.constructionTimeNs = System.nanoTime();
        this.cameraThreadHandler = new Handler();
        this.callback = callback2;
        this.events = events2;
        this.applicationContext = applicationContext2;
        this.cameraManager = cameraManager2;
        this.surfaceTextureHelper = surfaceTextureHelper2;
        this.cameraId = cameraId2;
        this.width = width2;
        this.height = height2;
        this.framerate = framerate2;
        this.orientationHelper = new OrientationHelper();
        start();
    }

    private void start() {
        checkIsOnCameraThread();
        Logging.d("Camera2Session", "start");
        try {
            this.cameraCharacteristics = this.cameraManager.getCameraCharacteristics(this.cameraId);
            this.orientationHelper.start();
            this.cameraOrientation = ((Integer) this.cameraCharacteristics.get(CameraCharacteristics.SENSOR_ORIENTATION)).intValue();
            this.isCameraFrontFacing = ((Integer) this.cameraCharacteristics.get(CameraCharacteristics.LENS_FACING)).intValue() == 0;
            findCaptureFormat();
            openCamera();
        } catch (Throwable e) {
            reportError("getCameraCharacteristics(): " + e.getMessage());
        }
    }

    private void findCaptureFormat() {
        checkIsOnCameraThread();
        Range<Integer>[] fpsRanges = (Range[]) this.cameraCharacteristics.get(CameraCharacteristics.CONTROL_AE_AVAILABLE_TARGET_FPS_RANGES);
        int fpsUnitFactor2 = Camera2Enumerator.getFpsUnitFactor(fpsRanges);
        this.fpsUnitFactor = fpsUnitFactor2;
        List<CameraEnumerationAndroid.CaptureFormat.FramerateRange> framerateRanges = Camera2Enumerator.convertFramerates(fpsRanges, fpsUnitFactor2);
        List<Size> sizes = Camera2Enumerator.getSupportedSizes(this.cameraCharacteristics);
        Logging.d("Camera2Session", "Available preview sizes: " + sizes);
        Logging.d("Camera2Session", "Available fps ranges: " + framerateRanges);
        if (framerateRanges.isEmpty() || sizes.isEmpty()) {
            reportError("No supported capture formats.");
            return;
        }
        CameraEnumerationAndroid.CaptureFormat.FramerateRange bestFpsRange = CameraEnumerationAndroid.getClosestSupportedFramerateRange(framerateRanges, this.framerate);
        Size bestSize = CameraEnumerationAndroid.getClosestSupportedSize(sizes, this.width, this.height);
        CameraEnumerationAndroid.reportCameraResolution(camera2ResolutionHistogram, bestSize);
        this.captureFormat = new CameraEnumerationAndroid.CaptureFormat(bestSize.width, bestSize.height, bestFpsRange);
        Logging.d("Camera2Session", "Using capture format: " + this.captureFormat);
    }

    private void openCamera() {
        checkIsOnCameraThread();
        Logging.d("Camera2Session", "Opening camera " + this.cameraId);
        this.events.onCameraOpening();
        try {
            this.cameraManager.openCamera(this.cameraId, new CameraStateCallback(), this.cameraThreadHandler);
        } catch (Exception e) {
            reportError("Failed to open camera: " + e);
        }
    }

    public void stop() {
        Logging.d("Camera2Session", "Stop camera2 session on camera " + this.cameraId);
        checkIsOnCameraThread();
        if (this.state != SessionState.STOPPED) {
            long stopStartTime = System.nanoTime();
            this.state = SessionState.STOPPED;
            stopInternal();
            camera2StopTimeMsHistogram.addSample((int) TimeUnit.NANOSECONDS.toMillis(System.nanoTime() - stopStartTime));
        }
    }

    /* access modifiers changed from: private */
    public void stopInternal() {
        Logging.d("Camera2Session", "Stop internal");
        checkIsOnCameraThread();
        this.surfaceTextureHelper.stopListening();
        CameraCaptureSession cameraCaptureSession = this.captureSession;
        if (cameraCaptureSession != null) {
            cameraCaptureSession.close();
            this.captureSession = null;
        }
        Surface surface2 = this.surface;
        if (surface2 != null) {
            surface2.release();
            this.surface = null;
        }
        CameraDevice cameraDevice2 = this.cameraDevice;
        if (cameraDevice2 != null) {
            cameraDevice2.close();
            this.cameraDevice = null;
        }
        OrientationHelper orientationHelper2 = this.orientationHelper;
        if (orientationHelper2 != null) {
            orientationHelper2.stop();
        }
        Logging.d("Camera2Session", "Stop done");
    }

    /* access modifiers changed from: private */
    public void reportError(String error) {
        checkIsOnCameraThread();
        Logging.e("Camera2Session", "Error: " + error);
        boolean startFailure = this.captureSession == null && this.state != SessionState.STOPPED;
        this.state = SessionState.STOPPED;
        stopInternal();
        if (startFailure) {
            this.callback.onFailure(CameraSession.FailureType.ERROR, error);
        } else {
            this.events.onCameraError(this, error);
        }
    }

    /* access modifiers changed from: private */
    public int getFrameOrientation() {
        int rotation = this.orientationHelper.getOrientation();
        OrientationHelper.cameraOrientation = rotation;
        if (this.isCameraFrontFacing) {
            rotation = 360 - rotation;
        }
        OrientationHelper.cameraRotation = rotation;
        return (this.cameraOrientation + rotation) % 360;
    }

    /* access modifiers changed from: private */
    public void checkIsOnCameraThread() {
        if (Thread.currentThread() != this.cameraThreadHandler.getLooper().getThread()) {
            throw new IllegalStateException("Wrong thread");
        }
    }
}
