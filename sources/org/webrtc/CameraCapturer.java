package org.webrtc;

import android.content.Context;
import android.media.MediaRecorder;
import android.os.Handler;
import android.os.Looper;
import java.util.Arrays;
import java.util.List;
import org.webrtc.CameraSession;
import org.webrtc.CameraVideoCapturer;

abstract class CameraCapturer implements CameraVideoCapturer {
    private static final int MAX_OPEN_CAMERA_ATTEMPTS = 3;
    private static final int OPEN_CAMERA_DELAY_MS = 500;
    private static final int OPEN_CAMERA_TIMEOUT = 10000;
    private static final String TAG = "CameraCapturer";
    /* access modifiers changed from: private */
    public Context applicationContext;
    /* access modifiers changed from: private */
    public final CameraEnumerator cameraEnumerator;
    /* access modifiers changed from: private */
    public String cameraName;
    /* access modifiers changed from: private */
    public final CameraSession.Events cameraSessionEventsHandler = new CameraSession.Events() {
        public void onCameraOpening() {
            CameraCapturer.this.checkIsOnCameraThread();
            synchronized (CameraCapturer.this.stateLock) {
                if (CameraCapturer.this.currentSession != null) {
                    Logging.w("CameraCapturer", "onCameraOpening while session was open.");
                } else {
                    CameraCapturer.this.eventsHandler.onCameraOpening(CameraCapturer.this.cameraName);
                }
            }
        }

        public void onCameraError(CameraSession session, String error) {
            CameraCapturer.this.checkIsOnCameraThread();
            synchronized (CameraCapturer.this.stateLock) {
                if (session != CameraCapturer.this.currentSession) {
                    Logging.w("CameraCapturer", "onCameraError from another session: " + error);
                    return;
                }
                CameraCapturer.this.eventsHandler.onCameraError(error);
                CameraCapturer.this.stopCapture();
            }
        }

        public void onCameraDisconnected(CameraSession session) {
            CameraCapturer.this.checkIsOnCameraThread();
            synchronized (CameraCapturer.this.stateLock) {
                if (session != CameraCapturer.this.currentSession) {
                    Logging.w("CameraCapturer", "onCameraDisconnected from another session.");
                    return;
                }
                CameraCapturer.this.eventsHandler.onCameraDisconnected();
                CameraCapturer.this.stopCapture();
            }
        }

        public void onCameraClosed(CameraSession session) {
            CameraCapturer.this.checkIsOnCameraThread();
            synchronized (CameraCapturer.this.stateLock) {
                if (session == CameraCapturer.this.currentSession || CameraCapturer.this.currentSession == null) {
                    CameraCapturer.this.eventsHandler.onCameraClosed();
                } else {
                    Logging.d("CameraCapturer", "onCameraClosed from another session.");
                }
            }
        }

        public void onFrameCaptured(CameraSession session, VideoFrame frame) {
            CameraCapturer.this.checkIsOnCameraThread();
            synchronized (CameraCapturer.this.stateLock) {
                if (session != CameraCapturer.this.currentSession) {
                    Logging.w("CameraCapturer", "onFrameCaptured from another session.");
                    return;
                }
                if (!CameraCapturer.this.firstFrameObserved) {
                    CameraCapturer.this.eventsHandler.onFirstFrameAvailable();
                    boolean unused = CameraCapturer.this.firstFrameObserved = true;
                }
                CameraCapturer.this.cameraStatistics.addFrame();
                CameraCapturer.this.capturerObserver.onFrameCaptured(frame);
            }
        }
    };
    /* access modifiers changed from: private */
    public CameraVideoCapturer.CameraStatistics cameraStatistics;
    private Handler cameraThreadHandler;
    /* access modifiers changed from: private */
    public CapturerObserver capturerObserver;
    /* access modifiers changed from: private */
    public final CameraSession.CreateSessionCallback createSessionCallback = new CameraSession.CreateSessionCallback() {
        public void onDone(CameraSession session) {
            CameraCapturer.this.checkIsOnCameraThread();
            Logging.d("CameraCapturer", "Create session done. Switch state: " + CameraCapturer.this.switchState);
            CameraCapturer.this.uiThreadHandler.removeCallbacks(CameraCapturer.this.openCameraTimeoutRunnable);
            synchronized (CameraCapturer.this.stateLock) {
                CameraCapturer.this.capturerObserver.onCapturerStarted(true);
                boolean unused = CameraCapturer.this.sessionOpening = false;
                CameraSession unused2 = CameraCapturer.this.currentSession = session;
                CameraVideoCapturer.CameraStatistics unused3 = CameraCapturer.this.cameraStatistics = new CameraVideoCapturer.CameraStatistics(CameraCapturer.this.surfaceHelper, CameraCapturer.this.eventsHandler);
                boolean unused4 = CameraCapturer.this.firstFrameObserved = false;
                CameraCapturer.this.stateLock.notifyAll();
                if (CameraCapturer.this.switchState == SwitchState.IN_PROGRESS) {
                    SwitchState unused5 = CameraCapturer.this.switchState = SwitchState.IDLE;
                    if (CameraCapturer.this.switchEventsHandler != null) {
                        CameraCapturer.this.switchEventsHandler.onCameraSwitchDone(CameraCapturer.this.cameraEnumerator.isFrontFacing(CameraCapturer.this.cameraName));
                        CameraVideoCapturer.CameraSwitchHandler unused6 = CameraCapturer.this.switchEventsHandler = null;
                    }
                } else if (CameraCapturer.this.switchState == SwitchState.PENDING) {
                    String selectedCameraName = CameraCapturer.this.pendingCameraName;
                    String unused7 = CameraCapturer.this.pendingCameraName = null;
                    SwitchState unused8 = CameraCapturer.this.switchState = SwitchState.IDLE;
                    CameraCapturer cameraCapturer = CameraCapturer.this;
                    cameraCapturer.switchCameraInternal(cameraCapturer.switchEventsHandler, selectedCameraName);
                }
            }
        }

        public void onFailure(CameraSession.FailureType failureType, String error) {
            CameraCapturer.this.checkIsOnCameraThread();
            CameraCapturer.this.uiThreadHandler.removeCallbacks(CameraCapturer.this.openCameraTimeoutRunnable);
            synchronized (CameraCapturer.this.stateLock) {
                CameraCapturer.this.capturerObserver.onCapturerStarted(false);
                CameraCapturer.access$1710(CameraCapturer.this);
                if (CameraCapturer.this.openAttemptsRemaining <= 0) {
                    Logging.w("CameraCapturer", "Opening camera failed, passing: " + error);
                    boolean unused = CameraCapturer.this.sessionOpening = false;
                    CameraCapturer.this.stateLock.notifyAll();
                    if (CameraCapturer.this.switchState != SwitchState.IDLE) {
                        if (CameraCapturer.this.switchEventsHandler != null) {
                            CameraCapturer.this.switchEventsHandler.onCameraSwitchError(error);
                            CameraVideoCapturer.CameraSwitchHandler unused2 = CameraCapturer.this.switchEventsHandler = null;
                        }
                        SwitchState unused3 = CameraCapturer.this.switchState = SwitchState.IDLE;
                    }
                    if (failureType == CameraSession.FailureType.DISCONNECTED) {
                        CameraCapturer.this.eventsHandler.onCameraDisconnected();
                    } else {
                        CameraCapturer.this.eventsHandler.onCameraError(error);
                    }
                } else {
                    Logging.w("CameraCapturer", "Opening camera failed, retry: " + error);
                    CameraCapturer.this.createSessionInternal(500);
                }
            }
        }
    };
    /* access modifiers changed from: private */
    public CameraSession currentSession;
    /* access modifiers changed from: private */
    public final CameraVideoCapturer.CameraEventsHandler eventsHandler;
    /* access modifiers changed from: private */
    public boolean firstFrameObserved;
    /* access modifiers changed from: private */
    public int framerate;
    /* access modifiers changed from: private */
    public int height;
    /* access modifiers changed from: private */
    public int openAttemptsRemaining;
    /* access modifiers changed from: private */
    public final Runnable openCameraTimeoutRunnable = new Runnable() {
        public void run() {
            CameraCapturer.this.eventsHandler.onCameraError("Camera failed to start within timeout.");
        }
    };
    /* access modifiers changed from: private */
    public String pendingCameraName;
    /* access modifiers changed from: private */
    public boolean sessionOpening;
    /* access modifiers changed from: private */
    public final Object stateLock = new Object();
    /* access modifiers changed from: private */
    public SurfaceTextureHelper surfaceHelper;
    /* access modifiers changed from: private */
    public CameraVideoCapturer.CameraSwitchHandler switchEventsHandler;
    /* access modifiers changed from: private */
    public SwitchState switchState = SwitchState.IDLE;
    /* access modifiers changed from: private */
    public final Handler uiThreadHandler;
    /* access modifiers changed from: private */
    public int width;

    enum SwitchState {
        IDLE,
        PENDING,
        IN_PROGRESS
    }

    public /* synthetic */ void addMediaRecorderToCamera(MediaRecorder mediaRecorder, CameraVideoCapturer.MediaRecorderHandler mediaRecorderHandler) {
        CameraVideoCapturer.CC.$default$addMediaRecorderToCamera(this, mediaRecorder, mediaRecorderHandler);
    }

    /* access modifiers changed from: protected */
    public abstract void createCameraSession(CameraSession.CreateSessionCallback createSessionCallback2, CameraSession.Events events, Context context, SurfaceTextureHelper surfaceTextureHelper, String str, int i, int i2, int i3);

    public /* synthetic */ void removeMediaRecorderFromCamera(CameraVideoCapturer.MediaRecorderHandler mediaRecorderHandler) {
        CameraVideoCapturer.CC.$default$removeMediaRecorderFromCamera(this, mediaRecorderHandler);
    }

    static /* synthetic */ int access$1710(CameraCapturer x0) {
        int i = x0.openAttemptsRemaining;
        x0.openAttemptsRemaining = i - 1;
        return i;
    }

    public CameraCapturer(String cameraName2, CameraVideoCapturer.CameraEventsHandler eventsHandler2, CameraEnumerator cameraEnumerator2) {
        this.eventsHandler = eventsHandler2 == null ? new CameraVideoCapturer.CameraEventsHandler() {
            public void onCameraError(String errorDescription) {
            }

            public void onCameraDisconnected() {
            }

            public void onCameraFreezed(String errorDescription) {
            }

            public void onCameraOpening(String cameraName) {
            }

            public void onFirstFrameAvailable() {
            }

            public void onCameraClosed() {
            }
        } : eventsHandler2;
        this.cameraEnumerator = cameraEnumerator2;
        this.cameraName = cameraName2;
        List<String> deviceNames = Arrays.asList(cameraEnumerator2.getDeviceNames());
        this.uiThreadHandler = new Handler(Looper.getMainLooper());
        if (deviceNames.isEmpty()) {
            throw new RuntimeException("No cameras attached.");
        } else if (!deviceNames.contains(this.cameraName)) {
            throw new IllegalArgumentException("Camera name " + this.cameraName + " does not match any known camera device.");
        }
    }

    public void initialize(SurfaceTextureHelper surfaceTextureHelper, Context applicationContext2, CapturerObserver capturerObserver2) {
        this.applicationContext = applicationContext2;
        this.capturerObserver = capturerObserver2;
        this.surfaceHelper = surfaceTextureHelper;
        this.cameraThreadHandler = surfaceTextureHelper.getHandler();
    }

    public void startCapture(int width2, int height2, int framerate2) {
        Logging.d("CameraCapturer", "startCapture: " + width2 + "x" + height2 + "@" + framerate2);
        if (this.applicationContext != null) {
            synchronized (this.stateLock) {
                if (!this.sessionOpening) {
                    if (this.currentSession == null) {
                        this.width = width2;
                        this.height = height2;
                        this.framerate = framerate2;
                        this.sessionOpening = true;
                        this.openAttemptsRemaining = 3;
                        createSessionInternal(0);
                        return;
                    }
                }
                Logging.w("CameraCapturer", "Session already open");
                return;
            }
        }
        throw new RuntimeException("CameraCapturer must be initialized before calling startCapture.");
    }

    /* access modifiers changed from: private */
    public void createSessionInternal(int delayMs) {
        this.uiThreadHandler.postDelayed(this.openCameraTimeoutRunnable, (long) (delayMs + 10000));
        this.cameraThreadHandler.postDelayed(new Runnable() {
            public void run() {
                CameraCapturer cameraCapturer = CameraCapturer.this;
                cameraCapturer.createCameraSession(cameraCapturer.createSessionCallback, CameraCapturer.this.cameraSessionEventsHandler, CameraCapturer.this.applicationContext, CameraCapturer.this.surfaceHelper, CameraCapturer.this.cameraName, CameraCapturer.this.width, CameraCapturer.this.height, CameraCapturer.this.framerate);
            }
        }, (long) delayMs);
    }

    public void stopCapture() {
        Logging.d("CameraCapturer", "Stop capture");
        synchronized (this.stateLock) {
            while (this.sessionOpening) {
                Logging.d("CameraCapturer", "Stop capture: Waiting for session to open");
                try {
                    this.stateLock.wait();
                } catch (InterruptedException e) {
                    Logging.w("CameraCapturer", "Stop capture interrupted while waiting for the session to open.");
                    Thread.currentThread().interrupt();
                    return;
                }
            }
            if (this.currentSession != null) {
                Logging.d("CameraCapturer", "Stop capture: Nulling session");
                this.cameraStatistics.release();
                this.cameraStatistics = null;
                final CameraSession oldSession = this.currentSession;
                this.cameraThreadHandler.post(new Runnable() {
                    public void run() {
                        oldSession.stop();
                    }
                });
                this.currentSession = null;
                this.capturerObserver.onCapturerStopped();
            } else {
                Logging.d("CameraCapturer", "Stop capture: No session open");
            }
        }
        Logging.d("CameraCapturer", "Stop capture done");
    }

    public void changeCaptureFormat(int width2, int height2, int framerate2) {
        Logging.d("CameraCapturer", "changeCaptureFormat: " + width2 + "x" + height2 + "@" + framerate2);
        synchronized (this.stateLock) {
            stopCapture();
            startCapture(width2, height2, framerate2);
        }
    }

    public void dispose() {
        Logging.d("CameraCapturer", "dispose");
        stopCapture();
    }

    public void switchCamera(final CameraVideoCapturer.CameraSwitchHandler switchEventsHandler2) {
        Logging.d("CameraCapturer", "switchCamera");
        this.cameraThreadHandler.post(new Runnable() {
            public void run() {
                List<String> deviceNames = Arrays.asList(CameraCapturer.this.cameraEnumerator.getDeviceNames());
                if (deviceNames.size() < 2) {
                    CameraCapturer.this.reportCameraSwitchError("No camera to switch to.", switchEventsHandler2);
                    return;
                }
                CameraCapturer.this.switchCameraInternal(switchEventsHandler2, deviceNames.get((deviceNames.indexOf(CameraCapturer.this.cameraName) + 1) % deviceNames.size()));
            }
        });
    }

    public void switchCamera(final CameraVideoCapturer.CameraSwitchHandler switchEventsHandler2, final String cameraName2) {
        Logging.d("CameraCapturer", "switchCamera");
        this.cameraThreadHandler.post(new Runnable() {
            public void run() {
                CameraCapturer.this.switchCameraInternal(switchEventsHandler2, cameraName2);
            }
        });
    }

    public boolean isScreencast() {
        return false;
    }

    public void printStackTrace() {
        Thread cameraThread = null;
        Handler handler = this.cameraThreadHandler;
        if (handler != null) {
            cameraThread = handler.getLooper().getThread();
        }
        if (cameraThread != null) {
            StackTraceElement[] cameraStackTrace = cameraThread.getStackTrace();
            if (cameraStackTrace.length > 0) {
                Logging.d("CameraCapturer", "CameraCapturer stack trace:");
                for (StackTraceElement traceElem : cameraStackTrace) {
                    Logging.d("CameraCapturer", traceElem.toString());
                }
            }
        }
    }

    /* access modifiers changed from: private */
    public void reportCameraSwitchError(String error, CameraVideoCapturer.CameraSwitchHandler switchEventsHandler2) {
        Logging.e("CameraCapturer", error);
        if (switchEventsHandler2 != null) {
            switchEventsHandler2.onCameraSwitchError(error);
        }
    }

    /* access modifiers changed from: private */
    public void switchCameraInternal(CameraVideoCapturer.CameraSwitchHandler switchEventsHandler2, String selectedCameraName) {
        Logging.d("CameraCapturer", "switchCamera internal");
        if (!Arrays.asList(this.cameraEnumerator.getDeviceNames()).contains(selectedCameraName)) {
            reportCameraSwitchError("Attempted to switch to unknown camera device " + selectedCameraName, switchEventsHandler2);
            return;
        }
        synchronized (this.stateLock) {
            if (this.switchState != SwitchState.IDLE) {
                reportCameraSwitchError("Camera switch already in progress.", switchEventsHandler2);
                return;
            }
            boolean z = this.sessionOpening;
            if (z || this.currentSession != null) {
                this.switchEventsHandler = switchEventsHandler2;
                if (z) {
                    this.switchState = SwitchState.PENDING;
                    this.pendingCameraName = selectedCameraName;
                    return;
                }
                this.switchState = SwitchState.IN_PROGRESS;
                Logging.d("CameraCapturer", "switchCamera: Stopping session");
                this.cameraStatistics.release();
                this.cameraStatistics = null;
                final CameraSession oldSession = this.currentSession;
                this.cameraThreadHandler.post(new Runnable() {
                    public void run() {
                        oldSession.stop();
                    }
                });
                this.currentSession = null;
                this.cameraName = selectedCameraName;
                this.sessionOpening = true;
                this.openAttemptsRemaining = 1;
                createSessionInternal(0);
                Logging.d("CameraCapturer", "switchCamera done");
                return;
            }
            reportCameraSwitchError("switchCamera: camera is not running.", switchEventsHandler2);
        }
    }

    /* access modifiers changed from: private */
    public void checkIsOnCameraThread() {
        if (Thread.currentThread() != this.cameraThreadHandler.getLooper().getThread()) {
            Logging.e("CameraCapturer", "Check is on camera thread failed.");
            throw new RuntimeException("Not on camera thread.");
        }
    }

    /* access modifiers changed from: protected */
    public String getCameraName() {
        String str;
        synchronized (this.stateLock) {
            str = this.cameraName;
        }
        return str;
    }
}
