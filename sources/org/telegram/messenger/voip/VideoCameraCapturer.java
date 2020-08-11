package org.telegram.messenger.voip;

import android.annotation.TargetApi;
import android.os.Handler;
import android.os.HandlerThread;
import org.telegram.messenger.AndroidUtilities;
import org.telegram.messenger.ApplicationLoader;
import org.telegram.messenger.FileLog;
import org.telegram.messenger.voip.VideoCameraCapturer;
import org.webrtc.Camera1Enumerator;
import org.webrtc.Camera2Enumerator;
import org.webrtc.CameraEnumerator;
import org.webrtc.CameraVideoCapturer;
import org.webrtc.CapturerObserver;
import org.webrtc.EglBase;
import org.webrtc.NativeCapturerObserver;
import org.webrtc.SurfaceTextureHelper;
import org.webrtc.VideoSource;

@TargetApi(18)
public class VideoCameraCapturer {
    private static final int CAPTURE_FPS = 30;
    private static final int CAPTURE_HEIGHT = 960;
    private static final int CAPTURE_WIDTH = 1280;
    public static EglBase eglBase;
    private static VideoCameraCapturer instance;
    private Handler handler;
    private CapturerObserver nativeCapturerObserver;
    private long nativePtr;
    private HandlerThread thread;
    private CameraVideoCapturer videoCapturer;
    private SurfaceTextureHelper videoCapturerSurfaceTextureHelper;

    private static native CapturerObserver nativeGetJavaVideoCapturerObserver(long j);

    public static VideoCameraCapturer getInstance() {
        return instance;
    }

    public VideoCameraCapturer() {
        AndroidUtilities.runOnUIThread(new Runnable() {
            public final void run() {
                VideoCameraCapturer.this.lambda$new$0$VideoCameraCapturer();
            }
        });
    }

    public /* synthetic */ void lambda$new$0$VideoCameraCapturer() {
        instance = this;
        HandlerThread handlerThread = new HandlerThread("CallThread");
        this.thread = handlerThread;
        handlerThread.start();
        this.handler = new Handler(this.thread.getLooper());
        if (eglBase == null) {
            eglBase = EglBase.CC.create((EglBase.Context) null, EglBase.CONFIG_PLAIN);
        }
    }

    private void init(long j, boolean z) {
        AndroidUtilities.runOnUIThread(new Runnable(j, z) {
            public final /* synthetic */ long f$1;
            public final /* synthetic */ boolean f$2;

            {
                this.f$1 = r2;
                this.f$2 = r4;
            }

            public final void run() {
                VideoCameraCapturer.this.lambda$init$3$VideoCameraCapturer(this.f$1, this.f$2);
            }
        });
    }

    public /* synthetic */ void lambda$init$3$VideoCameraCapturer(long j, boolean z) {
        this.nativePtr = j;
        CameraEnumerator camera2Enumerator = Camera2Enumerator.isSupported(ApplicationLoader.applicationContext) ? new Camera2Enumerator(ApplicationLoader.applicationContext) : new Camera1Enumerator();
        String[] deviceNames = camera2Enumerator.getDeviceNames();
        int i = 0;
        int i2 = 0;
        while (true) {
            if (i2 >= deviceNames.length) {
                break;
            } else if (camera2Enumerator.isFrontFacing(deviceNames[i2]) == z) {
                i = i2;
                break;
            } else {
                i2++;
            }
        }
        String str = deviceNames[i];
        if (this.videoCapturer == null) {
            this.videoCapturer = camera2Enumerator.createCapturer(str, (CameraVideoCapturer.CameraEventsHandler) null);
            this.videoCapturerSurfaceTextureHelper = SurfaceTextureHelper.create("VideoCapturerThread", eglBase.getEglBaseContext());
            this.handler.post(new Runnable() {
                public final void run() {
                    VideoCameraCapturer.this.lambda$null$1$VideoCameraCapturer();
                }
            });
            return;
        }
        this.handler.post(new Runnable(str) {
            public final /* synthetic */ String f$1;

            {
                this.f$1 = r2;
            }

            public final void run() {
                VideoCameraCapturer.this.lambda$null$2$VideoCameraCapturer(this.f$1);
            }
        });
    }

    public /* synthetic */ void lambda$null$1$VideoCameraCapturer() {
        this.nativeCapturerObserver = nativeGetJavaVideoCapturerObserver(this.nativePtr);
        this.videoCapturer.initialize(this.videoCapturerSurfaceTextureHelper, ApplicationLoader.applicationContext, this.nativeCapturerObserver);
        this.videoCapturer.startCapture(1280, 960, 30);
    }

    public /* synthetic */ void lambda$null$2$VideoCameraCapturer(String str) {
        this.videoCapturer.switchCamera(new CameraVideoCapturer.CameraSwitchHandler() {
            public void onCameraSwitchError(String str) {
            }

            public void onCameraSwitchDone(boolean z) {
                AndroidUtilities.runOnUIThread(new Runnable(z) {
                    public final /* synthetic */ boolean f$0;

                    {
                        this.f$0 = r1;
                    }

                    public final void run() {
                        VideoCameraCapturer.AnonymousClass1.lambda$onCameraSwitchDone$0(this.f$0);
                    }
                });
            }

            static /* synthetic */ void lambda$onCameraSwitchDone$0(boolean z) {
                if (VoIPBaseService.getSharedInstance() != null) {
                    VoIPBaseService.getSharedInstance().setSwitchingCamera(false, z);
                }
            }
        }, str);
    }

    private void onAspectRatioRequested(float f) {
        if (f >= 1.0E-4f) {
            this.handler.post(new Runnable(f) {
                public final /* synthetic */ float f$1;

                {
                    this.f$1 = r2;
                }

                public final void run() {
                    VideoCameraCapturer.this.lambda$onAspectRatioRequested$4$VideoCameraCapturer(this.f$1);
                }
            });
        }
    }

    public /* synthetic */ void lambda$onAspectRatioRequested$4$VideoCameraCapturer(float f) {
        int i;
        int i2;
        if (this.nativeCapturerObserver instanceof NativeCapturerObserver) {
            if (f < 1.0f) {
                i2 = 960;
                i = (int) (((float) 960) / f);
            } else {
                i2 = (int) (((float) 1280) * f);
                i = 1280;
            }
            if (i > 0 && i2 > 0) {
                int i3 = i * i2;
                ((NativeCapturerObserver) this.nativeCapturerObserver).getNativeAndroidVideoTrackSource().adaptOutputFormat(new VideoSource.AspectRatio(i, i2), Integer.valueOf(i3), new VideoSource.AspectRatio(i2, i), Integer.valueOf(i3), 30);
            }
        }
    }

    private void onStateChanged(long j, int i) {
        AndroidUtilities.runOnUIThread(new Runnable(j, i) {
            public final /* synthetic */ long f$1;
            public final /* synthetic */ int f$2;

            {
                this.f$1 = r2;
                this.f$2 = r4;
            }

            public final void run() {
                VideoCameraCapturer.this.lambda$onStateChanged$6$VideoCameraCapturer(this.f$1, this.f$2);
            }
        });
    }

    public /* synthetic */ void lambda$onStateChanged$6$VideoCameraCapturer(long j, int i) {
        if (this.nativePtr == j) {
            this.handler.post(new Runnable(i) {
                public final /* synthetic */ int f$1;

                {
                    this.f$1 = r2;
                }

                public final void run() {
                    VideoCameraCapturer.this.lambda$null$5$VideoCameraCapturer(this.f$1);
                }
            });
        }
    }

    public /* synthetic */ void lambda$null$5$VideoCameraCapturer(int i) {
        if (i == 2) {
            this.videoCapturer.startCapture(1280, 960, 30);
            return;
        }
        try {
            this.videoCapturer.stopCapture();
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
    }

    private void onDestroy() {
        AndroidUtilities.runOnUIThread(new Runnable() {
            public final void run() {
                VideoCameraCapturer.this.lambda$onDestroy$8$VideoCameraCapturer();
            }
        });
    }

    public /* synthetic */ void lambda$onDestroy$8$VideoCameraCapturer() {
        EglBase eglBase2 = eglBase;
        if (eglBase2 != null) {
            eglBase2.release();
            eglBase = null;
        }
        if (instance == this) {
            instance = null;
        }
        this.handler.post(new Runnable() {
            public final void run() {
                VideoCameraCapturer.this.lambda$null$7$VideoCameraCapturer();
            }
        });
        try {
            this.thread.quitSafely();
        } catch (Exception e) {
            FileLog.e((Throwable) e);
        }
    }

    public /* synthetic */ void lambda$null$7$VideoCameraCapturer() {
        CameraVideoCapturer cameraVideoCapturer = this.videoCapturer;
        if (cameraVideoCapturer != null) {
            try {
                cameraVideoCapturer.stopCapture();
                this.videoCapturer.dispose();
                this.videoCapturer = null;
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
        }
        SurfaceTextureHelper surfaceTextureHelper = this.videoCapturerSurfaceTextureHelper;
        if (surfaceTextureHelper != null) {
            surfaceTextureHelper.dispose();
            this.videoCapturerSurfaceTextureHelper = null;
        }
    }
}
