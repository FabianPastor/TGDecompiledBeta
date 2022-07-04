package org.telegram.messenger.voip;

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
import org.webrtc.voiceengine.WebRtcAudioRecord;

public class VideoCapturerDevice {
    private static final int CAPTURE_FPS = 30;
    private static final int CAPTURE_HEIGHT = (Build.VERSION.SDK_INT <= 19 ? 320 : 720);
    private static final int CAPTURE_WIDTH = (Build.VERSION.SDK_INT <= 19 ? 480 : 1280);
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

    public VideoCapturerDevice(boolean screencast) {
        if (Build.VERSION.SDK_INT >= 18) {
            Logging.enableLogToDebugOutput(Logging.Severity.LS_INFO);
            Logging.d("VideoCapturerDevice", "device model = " + Build.MANUFACTURER + Build.MODEL);
            AndroidUtilities.runOnUIThread(new VideoCapturerDevice$$ExternalSyntheticLambda9(this, screencast));
        }
    }

    /* JADX WARNING: type inference failed for: r3v0, types: [boolean] */
    /* JADX WARNING: Unknown variable types count: 1 */
    /* renamed from: lambda$new$0$org-telegram-messenger-voip-VideoCapturerDevice  reason: not valid java name */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public /* synthetic */ void m512lambda$new$0$orgtelegrammessengervoipVideoCapturerDevice(boolean r3) {
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
            android.os.HandlerThread r0 = new android.os.HandlerThread
            java.lang.String r1 = "CallThread"
            r0.<init>(r1)
            r2.thread = r0
            r0.start()
            android.os.Handler r0 = new android.os.Handler
            android.os.HandlerThread r1 = r2.thread
            android.os.Looper r1 = r1.getLooper()
            r0.<init>(r1)
            r2.handler = r0
            return
        */
        throw new UnsupportedOperationException("Method not decompiled: org.telegram.messenger.voip.VideoCapturerDevice.m512lambda$new$0$orgtelegrammessengervoipVideoCapturerDevice(boolean):void");
    }

    public static void checkScreenCapturerSize() {
        if (instance[1] != null) {
            Point size = getScreenCaptureSize();
            if (instance[1].currentWidth != size.x || instance[1].currentHeight != size.y) {
                instance[1].currentWidth = size.x;
                instance[1].currentHeight = size.y;
                VideoCapturerDevice[] videoCapturerDeviceArr = instance;
                videoCapturerDeviceArr[1].handler.post(new VideoCapturerDevice$$ExternalSyntheticLambda7(videoCapturerDeviceArr[1], size));
            }
        }
    }

    static /* synthetic */ void lambda$checkScreenCapturerSize$1(VideoCapturerDevice device, Point size) {
        VideoCapturer videoCapturer2 = device.videoCapturer;
        if (videoCapturer2 != null) {
            videoCapturer2.changeCaptureFormat(size.x, size.y, 30);
        }
    }

    private static Point getScreenCaptureSize() {
        float aspect;
        Display display = ((WindowManager) ApplicationLoader.applicationContext.getSystemService("window")).getDefaultDisplay();
        Point size = new Point();
        display.getRealSize(size);
        if (size.x > size.y) {
            aspect = ((float) size.y) / ((float) size.x);
        } else {
            aspect = ((float) size.x) / ((float) size.y);
        }
        int dx = -1;
        int dy = -1;
        int a = 1;
        while (true) {
            if (a > 100) {
                break;
            }
            float val = ((float) a) * aspect;
            if (val != ((float) ((int) val))) {
                a++;
            } else if (size.x > size.y) {
                dx = a;
                dy = (int) (((float) a) * aspect);
            } else {
                dy = a;
                dx = (int) (((float) a) * aspect);
            }
        }
        if (dx != -1 && aspect != 1.0f) {
            while (true) {
                if (size.x <= 1000 && size.y <= 1000 && size.x % 4 == 0 && size.y % 4 == 0) {
                    break;
                }
                size.x -= dx;
                size.y -= dy;
                if (size.x < 800 && size.y < 800) {
                    dx = -1;
                    break;
                }
            }
        }
        if (dx == -1 || aspect == 1.0f) {
            float scale = Math.max(((float) size.x) / 970.0f, ((float) size.y) / 970.0f);
            size.x = ((int) Math.ceil((double) ((((float) size.x) / scale) / 4.0f))) * 4;
            size.y = ((int) Math.ceil((double) ((((float) size.y) / scale) / 4.0f))) * 4;
        }
        return size;
    }

    private void init(long ptr, String deviceName) {
        if (Build.VERSION.SDK_INT >= 18) {
            AndroidUtilities.runOnUIThread(new VideoCapturerDevice$$ExternalSyntheticLambda5(this, ptr, deviceName));
        }
    }

    /* renamed from: lambda$init$5$org-telegram-messenger-voip-VideoCapturerDevice  reason: not valid java name */
    public /* synthetic */ void m511lambda$init$5$orgtelegrammessengervoipVideoCapturerDevice(long ptr, String deviceName) {
        if (eglBase != null) {
            this.nativePtr = ptr;
            if (!"screen".equals(deviceName)) {
                CameraEnumerator enumerator = Camera2Enumerator.isSupported(ApplicationLoader.applicationContext) ? new Camera2Enumerator(ApplicationLoader.applicationContext) : new Camera1Enumerator();
                int index = -1;
                String[] names = enumerator.getDeviceNames();
                int a = 0;
                while (true) {
                    if (a >= names.length) {
                        break;
                    } else if (enumerator.isFrontFacing(names[a]) == "front".equals(deviceName)) {
                        index = a;
                        break;
                    } else {
                        a++;
                    }
                }
                if (index != -1) {
                    String cameraName = names[index];
                    if (this.videoCapturer == null) {
                        this.videoCapturer = enumerator.createCapturer(cameraName, new CameraVideoCapturer.CameraEventsHandler() {
                            public void onCameraError(String errorDescription) {
                            }

                            public void onCameraDisconnected() {
                            }

                            public void onCameraFreezed(String errorDescription) {
                            }

                            public void onCameraOpening(String cameraName) {
                            }

                            public void onFirstFrameAvailable() {
                                AndroidUtilities.runOnUIThread(VideoCapturerDevice$2$$ExternalSyntheticLambda0.INSTANCE);
                            }

                            static /* synthetic */ void lambda$onFirstFrameAvailable$0() {
                                if (VoIPService.getSharedInstance() != null) {
                                    VoIPService.getSharedInstance().onCameraFirstFrameAvailable();
                                }
                            }

                            public void onCameraClosed() {
                            }
                        });
                        this.videoCapturerSurfaceTextureHelper = SurfaceTextureHelper.create("VideoCapturerThread", eglBase.getEglBaseContext());
                        this.handler.post(new VideoCapturerDevice$$ExternalSyntheticLambda0(this));
                        return;
                    }
                    this.handler.post(new VideoCapturerDevice$$ExternalSyntheticLambda8(this, cameraName));
                }
            } else if (Build.VERSION.SDK_INT >= 21 && this.videoCapturer == null) {
                this.videoCapturer = new ScreenCapturerAndroid(mediaProjectionPermissionResultData, new MediaProjection.Callback() {
                    public void onStop() {
                        AndroidUtilities.runOnUIThread(VideoCapturerDevice$1$$ExternalSyntheticLambda0.INSTANCE);
                    }

                    static /* synthetic */ void lambda$onStop$0() {
                        if (VoIPService.getSharedInstance() != null) {
                            VoIPService.getSharedInstance().stopScreenCapture();
                        }
                    }
                });
                Point size = getScreenCaptureSize();
                this.currentWidth = size.x;
                this.currentHeight = size.y;
                this.videoCapturerSurfaceTextureHelper = SurfaceTextureHelper.create("ScreenCapturerThread", eglBase.getEglBaseContext());
                this.handler.post(new VideoCapturerDevice$$ExternalSyntheticLambda6(this, size));
            }
        }
    }

    /* renamed from: lambda$init$2$org-telegram-messenger-voip-VideoCapturerDevice  reason: not valid java name */
    public /* synthetic */ void m508lambda$init$2$orgtelegrammessengervoipVideoCapturerDevice(Point size) {
        if (this.videoCapturerSurfaceTextureHelper != null) {
            long j = this.nativePtr;
            if (j != 0) {
                this.nativeCapturerObserver = nativeGetJavaVideoCapturerObserver(j);
                this.videoCapturer.initialize(this.videoCapturerSurfaceTextureHelper, ApplicationLoader.applicationContext, this.nativeCapturerObserver);
                this.videoCapturer.startCapture(size.x, size.y, 30);
                WebRtcAudioRecord audioRecord = WebRtcAudioRecord.Instance;
                if (audioRecord != null) {
                    audioRecord.initDeviceAudioRecord(((ScreenCapturerAndroid) this.videoCapturer).getMediaProjection());
                }
            }
        }
    }

    /* renamed from: lambda$init$3$org-telegram-messenger-voip-VideoCapturerDevice  reason: not valid java name */
    public /* synthetic */ void m509lambda$init$3$orgtelegrammessengervoipVideoCapturerDevice() {
        if (this.videoCapturerSurfaceTextureHelper != null) {
            this.nativeCapturerObserver = nativeGetJavaVideoCapturerObserver(this.nativePtr);
            this.videoCapturer.initialize(this.videoCapturerSurfaceTextureHelper, ApplicationLoader.applicationContext, this.nativeCapturerObserver);
            this.videoCapturer.startCapture(CAPTURE_WIDTH, CAPTURE_HEIGHT, 30);
        }
    }

    /* renamed from: lambda$init$4$org-telegram-messenger-voip-VideoCapturerDevice  reason: not valid java name */
    public /* synthetic */ void m510lambda$init$4$orgtelegrammessengervoipVideoCapturerDevice(String cameraName) {
        ((CameraVideoCapturer) this.videoCapturer).switchCamera(new CameraVideoCapturer.CameraSwitchHandler() {
            public void onCameraSwitchDone(boolean isFrontCamera) {
                AndroidUtilities.runOnUIThread(new VideoCapturerDevice$3$$ExternalSyntheticLambda0(isFrontCamera));
            }

            static /* synthetic */ void lambda$onCameraSwitchDone$0(boolean isFrontCamera) {
                if (VoIPService.getSharedInstance() != null) {
                    VoIPService.getSharedInstance().setSwitchingCamera(false, isFrontCamera);
                }
            }

            public void onCameraSwitchError(String errorDescription) {
            }
        }, cameraName);
    }

    public static MediaProjection getMediaProjection() {
        VideoCapturerDevice[] videoCapturerDeviceArr = instance;
        if (videoCapturerDeviceArr[1] == null) {
            return null;
        }
        return ((ScreenCapturerAndroid) videoCapturerDeviceArr[1].videoCapturer).getMediaProjection();
    }

    private void onAspectRatioRequested(float aspectRatio) {
    }

    private void onStateChanged(long ptr, int state) {
        if (Build.VERSION.SDK_INT >= 18) {
            AndroidUtilities.runOnUIThread(new VideoCapturerDevice$$ExternalSyntheticLambda4(this, ptr, state));
        }
    }

    /* renamed from: lambda$onStateChanged$7$org-telegram-messenger-voip-VideoCapturerDevice  reason: not valid java name */
    public /* synthetic */ void m516x7b06e05b(long ptr, int state) {
        if (this.nativePtr == ptr) {
            this.handler.post(new VideoCapturerDevice$$ExternalSyntheticLambda3(this, state));
        }
    }

    /* renamed from: lambda$onStateChanged$6$org-telegram-messenger-voip-VideoCapturerDevice  reason: not valid java name */
    public /* synthetic */ void m515x7b7d465a(int state) {
        VideoCapturer videoCapturer2 = this.videoCapturer;
        if (videoCapturer2 != null) {
            if (state == 2) {
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
            this.nativePtr = 0;
            AndroidUtilities.runOnUIThread(new VideoCapturerDevice$$ExternalSyntheticLambda2(this));
        }
    }

    /* renamed from: lambda$onDestroy$9$org-telegram-messenger-voip-VideoCapturerDevice  reason: not valid java name */
    public /* synthetic */ void m514x8c9ffc3c() {
        int a = 0;
        while (true) {
            VideoCapturerDevice[] videoCapturerDeviceArr = instance;
            if (a >= videoCapturerDeviceArr.length) {
                break;
            } else if (videoCapturerDeviceArr[a] == this) {
                videoCapturerDeviceArr[a] = null;
                break;
            } else {
                a++;
            }
        }
        this.handler.post(new VideoCapturerDevice$$ExternalSyntheticLambda1(this));
        try {
            this.thread.quitSafely();
        } catch (Exception e) {
            FileLog.e((Throwable) e);
        }
    }

    /* renamed from: lambda$onDestroy$8$org-telegram-messenger-voip-VideoCapturerDevice  reason: not valid java name */
    public /* synthetic */ void m513x8d16623b() {
        WebRtcAudioRecord audioRecord;
        if ((this.videoCapturer instanceof ScreenCapturerAndroid) && (audioRecord = WebRtcAudioRecord.Instance) != null) {
            audioRecord.stopDeviceAudioRecord();
        }
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
