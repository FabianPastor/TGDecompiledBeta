package org.telegram.messenger.voip;

import android.annotation.TargetApi;
import android.content.Intent;
import android.graphics.Point;
import android.media.projection.MediaProjection;
import android.os.Build;
import android.os.Handler;
import android.os.HandlerThread;
import android.view.Display;
import android.view.WindowManager;
import org.telegram.messenger.AndroidUtilities;
import org.telegram.messenger.ApplicationLoader;
import org.telegram.messenger.FileLog;
import org.telegram.messenger.voip.VideoCapturerDevice;
import org.webrtc.Camera1Enumerator;
import org.webrtc.Camera2Enumerator;
import org.webrtc.CameraEnumerator;
import org.webrtc.CameraVideoCapturer;
import org.webrtc.CapturerObserver;
import org.webrtc.EglBase;
import org.webrtc.Logging;
import org.webrtc.ScreenCapturerAndroid;
import org.webrtc.SurfaceTextureHelper;
import org.webrtc.VideoCapturer;

@TargetApi(18)
public class VideoCapturerDevice {
    private static final int CAPTURE_FPS = 30;
    private static final int CAPTURE_HEIGHT;
    private static final int CAPTURE_WIDTH;
    public static EglBase eglBase;
    private static VideoCapturerDevice[] instance = new VideoCapturerDevice[2];
    public static Intent mediaProjectionPermissionResultData;
    private int currentHeight;
    private int currentWidth;
    private Handler handler;
    private CapturerObserver nativeCapturerObserver;
    private long nativePtr;
    private HandlerThread thread;
    private VideoCapturer videoCapturer;
    private SurfaceTextureHelper videoCapturerSurfaceTextureHelper;

    private static native CapturerObserver nativeGetJavaVideoCapturerObserver(long j);

    private void onAspectRatioRequested(float f) {
    }

    static {
        int i = Build.VERSION.SDK_INT;
        CAPTURE_WIDTH = i <= 19 ? 480 : 1280;
        CAPTURE_HEIGHT = i <= 19 ? 320 : 720;
    }

    public VideoCapturerDevice(boolean z) {
        if (Build.VERSION.SDK_INT >= 18) {
            Logging.enableLogToDebugOutput(Logging.Severity.LS_INFO);
            Logging.d("VideoCapturerDevice", "device model = " + Build.MANUFACTURER + Build.MODEL);
            AndroidUtilities.runOnUIThread(new Runnable(z) {
                public final /* synthetic */ boolean f$1;

                {
                    this.f$1 = r2;
                }

                public final void run() {
                    VideoCapturerDevice.this.lambda$new$0$VideoCapturerDevice(this.f$1);
                }
            });
        }
    }

    /* JADX WARNING: type inference failed for: r3v0, types: [boolean] */
    /* access modifiers changed from: private */
    /* JADX WARNING: Unknown variable types count: 1 */
    /* renamed from: lambda$new$0 */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public /* synthetic */ void lambda$new$0$VideoCapturerDevice(boolean r3) {
        /*
            r2 = this;
            org.webrtc.EglBase r0 = eglBase
            if (r0 != 0) goto L_0x000d
            r0 = 0
            int[] r1 = org.webrtc.EglBase.CONFIG_PLAIN
            org.webrtc.EglBase r0 = org.webrtc.EglBase.CC.create(r0, r1)
            eglBase = r0
        L_0x000d:
            org.telegram.messenger.voip.VideoCapturerDevice[] r0 = instance
            r0[r3] = r2
            android.os.HandlerThread r3 = new android.os.HandlerThread
            java.lang.String r0 = "CallThread"
            r3.<init>(r0)
            r2.thread = r3
            r3.start()
            android.os.Handler r3 = new android.os.Handler
            android.os.HandlerThread r0 = r2.thread
            android.os.Looper r0 = r0.getLooper()
            r3.<init>(r0)
            r2.handler = r3
            return
        */
        throw new UnsupportedOperationException("Method not decompiled: org.telegram.messenger.voip.VideoCapturerDevice.lambda$new$0$VideoCapturerDevice(boolean):void");
    }

    public static void checkScreenCapturerSize() {
        if (instance[1] != null) {
            Display defaultDisplay = ((WindowManager) ApplicationLoader.applicationContext.getSystemService("window")).getDefaultDisplay();
            Point point = new Point();
            defaultDisplay.getRealSize(point);
            float max = Math.max(((float) point.x) / 970.0f, ((float) point.y) / 970.0f);
            int i = (int) (((float) point.x) / max);
            int i2 = (int) (((float) point.y) / max);
            VideoCapturerDevice[] videoCapturerDeviceArr = instance;
            if (videoCapturerDeviceArr[1].currentWidth != i || videoCapturerDeviceArr[1].currentHeight != i2) {
                videoCapturerDeviceArr[1].currentWidth = i;
                videoCapturerDeviceArr[1].currentHeight = i2;
                videoCapturerDeviceArr[1].handler.post(new Runnable(i, i2) {
                    public final /* synthetic */ int f$0;
                    public final /* synthetic */ int f$1;

                    {
                        this.f$0 = r1;
                        this.f$1 = r2;
                    }

                    public final void run() {
                        VideoCapturerDevice.instance[1].videoCapturer.changeCaptureFormat(this.f$0, this.f$1, 30);
                    }
                });
            }
        }
    }

    private void init(long j, String str) {
        if (Build.VERSION.SDK_INT >= 18) {
            AndroidUtilities.runOnUIThread(new Runnable(j, str) {
                public final /* synthetic */ long f$1;
                public final /* synthetic */ String f$2;

                {
                    this.f$1 = r2;
                    this.f$2 = r4;
                }

                public final void run() {
                    VideoCapturerDevice.this.lambda$init$5$VideoCapturerDevice(this.f$1, this.f$2);
                }
            });
        }
    }

    /* access modifiers changed from: private */
    /* renamed from: lambda$init$5 */
    public /* synthetic */ void lambda$init$5$VideoCapturerDevice(long j, String str) {
        if (eglBase != null) {
            this.nativePtr = j;
            if (!"screen".equals(str)) {
                CameraEnumerator camera2Enumerator = Camera2Enumerator.isSupported(ApplicationLoader.applicationContext) ? new Camera2Enumerator(ApplicationLoader.applicationContext) : new Camera1Enumerator();
                String[] deviceNames = camera2Enumerator.getDeviceNames();
                int i = 0;
                while (true) {
                    if (i >= deviceNames.length) {
                        i = -1;
                        break;
                    } else if (camera2Enumerator.isFrontFacing(deviceNames[i]) == "front".equals(str)) {
                        break;
                    } else {
                        i++;
                    }
                }
                if (i != -1) {
                    String str2 = deviceNames[i];
                    if (this.videoCapturer == null) {
                        this.videoCapturer = camera2Enumerator.createCapturer(str2, (CameraVideoCapturer.CameraEventsHandler) null);
                        this.videoCapturerSurfaceTextureHelper = SurfaceTextureHelper.create("VideoCapturerThread", eglBase.getEglBaseContext());
                        this.handler.post(new Runnable() {
                            public final void run() {
                                VideoCapturerDevice.this.lambda$init$3$VideoCapturerDevice();
                            }
                        });
                        return;
                    }
                    this.handler.post(new Runnable(str2) {
                        public final /* synthetic */ String f$1;

                        {
                            this.f$1 = r2;
                        }

                        public final void run() {
                            VideoCapturerDevice.this.lambda$init$4$VideoCapturerDevice(this.f$1);
                        }
                    });
                }
            } else if (Build.VERSION.SDK_INT >= 21 && this.videoCapturer == null) {
                this.videoCapturer = new ScreenCapturerAndroid(mediaProjectionPermissionResultData, new MediaProjection.Callback() {
                    public void onStop() {
                    }
                });
                Display defaultDisplay = ((WindowManager) ApplicationLoader.applicationContext.getSystemService("window")).getDefaultDisplay();
                Point point = new Point();
                defaultDisplay.getRealSize(point);
                float max = Math.max(((float) point.x) / 970.0f, ((float) point.y) / 970.0f);
                int i2 = (int) (((float) point.x) / max);
                this.currentWidth = i2;
                int i3 = (int) (((float) point.y) / max);
                this.currentHeight = i3;
                this.videoCapturerSurfaceTextureHelper = SurfaceTextureHelper.create("ScreenCapturerThread", eglBase.getEglBaseContext());
                this.handler.post(new Runnable(i2, i3) {
                    public final /* synthetic */ int f$1;
                    public final /* synthetic */ int f$2;

                    {
                        this.f$1 = r2;
                        this.f$2 = r3;
                    }

                    public final void run() {
                        VideoCapturerDevice.this.lambda$init$2$VideoCapturerDevice(this.f$1, this.f$2);
                    }
                });
            }
        }
    }

    /* access modifiers changed from: private */
    /* renamed from: lambda$init$2 */
    public /* synthetic */ void lambda$init$2$VideoCapturerDevice(int i, int i2) {
        this.nativeCapturerObserver = nativeGetJavaVideoCapturerObserver(this.nativePtr);
        this.videoCapturer.initialize(this.videoCapturerSurfaceTextureHelper, ApplicationLoader.applicationContext, this.nativeCapturerObserver);
        this.videoCapturer.startCapture(i, i2, 30);
    }

    /* access modifiers changed from: private */
    /* renamed from: lambda$init$3 */
    public /* synthetic */ void lambda$init$3$VideoCapturerDevice() {
        this.nativeCapturerObserver = nativeGetJavaVideoCapturerObserver(this.nativePtr);
        this.videoCapturer.initialize(this.videoCapturerSurfaceTextureHelper, ApplicationLoader.applicationContext, this.nativeCapturerObserver);
        this.videoCapturer.startCapture(CAPTURE_WIDTH, CAPTURE_HEIGHT, 30);
    }

    /* access modifiers changed from: private */
    /* renamed from: lambda$init$4 */
    public /* synthetic */ void lambda$init$4$VideoCapturerDevice(String str) {
        ((CameraVideoCapturer) this.videoCapturer).switchCamera(new CameraVideoCapturer.CameraSwitchHandler() {
            public void onCameraSwitchError(String str) {
            }

            public void onCameraSwitchDone(boolean z) {
                AndroidUtilities.runOnUIThread(new Runnable(z) {
                    public final /* synthetic */ boolean f$0;

                    {
                        this.f$0 = r1;
                    }

                    public final void run() {
                        VideoCapturerDevice.AnonymousClass2.lambda$onCameraSwitchDone$0(this.f$0);
                    }
                });
            }

            static /* synthetic */ void lambda$onCameraSwitchDone$0(boolean z) {
                if (VoIPService.getSharedInstance() != null) {
                    VoIPService.getSharedInstance().setSwitchingCamera(false, z);
                }
            }
        }, str);
    }

    private void onStateChanged(long j, int i) {
        if (Build.VERSION.SDK_INT >= 18) {
            AndroidUtilities.runOnUIThread(new Runnable(j, i) {
                public final /* synthetic */ long f$1;
                public final /* synthetic */ int f$2;

                {
                    this.f$1 = r2;
                    this.f$2 = r4;
                }

                public final void run() {
                    VideoCapturerDevice.this.lambda$onStateChanged$7$VideoCapturerDevice(this.f$1, this.f$2);
                }
            });
        }
    }

    /* access modifiers changed from: private */
    /* renamed from: lambda$onStateChanged$7 */
    public /* synthetic */ void lambda$onStateChanged$7$VideoCapturerDevice(long j, int i) {
        if (this.nativePtr == j) {
            this.handler.post(new Runnable(i) {
                public final /* synthetic */ int f$1;

                {
                    this.f$1 = r2;
                }

                public final void run() {
                    VideoCapturerDevice.this.lambda$onStateChanged$6$VideoCapturerDevice(this.f$1);
                }
            });
        }
    }

    /* access modifiers changed from: private */
    /* renamed from: lambda$onStateChanged$6 */
    public /* synthetic */ void lambda$onStateChanged$6$VideoCapturerDevice(int i) {
        VideoCapturer videoCapturer2 = this.videoCapturer;
        if (videoCapturer2 != null) {
            if (i == 2) {
                videoCapturer2.startCapture(CAPTURE_WIDTH, CAPTURE_HEIGHT, 30);
                return;
            }
            try {
                videoCapturer2.stopCapture();
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
        }
    }

    private void onDestroy() {
        if (Build.VERSION.SDK_INT >= 18) {
            AndroidUtilities.runOnUIThread(new Runnable() {
                public final void run() {
                    VideoCapturerDevice.this.lambda$onDestroy$9$VideoCapturerDevice();
                }
            });
        }
    }

    /* access modifiers changed from: private */
    /* renamed from: lambda$onDestroy$9 */
    public /* synthetic */ void lambda$onDestroy$9$VideoCapturerDevice() {
        int i = 0;
        while (true) {
            VideoCapturerDevice[] videoCapturerDeviceArr = instance;
            if (i >= videoCapturerDeviceArr.length) {
                break;
            } else if (videoCapturerDeviceArr[i] == this) {
                videoCapturerDeviceArr[i] = null;
                break;
            } else {
                i++;
            }
        }
        this.handler.post(new Runnable() {
            public final void run() {
                VideoCapturerDevice.this.lambda$onDestroy$8$VideoCapturerDevice();
            }
        });
        try {
            this.thread.quitSafely();
        } catch (Exception e) {
            FileLog.e((Throwable) e);
        }
    }

    /* access modifiers changed from: private */
    /* renamed from: lambda$onDestroy$8 */
    public /* synthetic */ void lambda$onDestroy$8$VideoCapturerDevice() {
        VideoCapturer videoCapturer2 = this.videoCapturer;
        if (videoCapturer2 != null) {
            try {
                videoCapturer2.stopCapture();
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

    private EglBase.Context getSharedEGLContext() {
        if (eglBase == null) {
            eglBase = EglBase.CC.create((EglBase.Context) null, EglBase.CONFIG_PLAIN);
        }
        EglBase eglBase2 = eglBase;
        if (eglBase2 != null) {
            return eglBase2.getEglBaseContext();
        }
        return null;
    }

    public static EglBase getEglBase() {
        if (eglBase == null) {
            eglBase = EglBase.CC.create((EglBase.Context) null, EglBase.CONFIG_PLAIN);
        }
        return eglBase;
    }
}
