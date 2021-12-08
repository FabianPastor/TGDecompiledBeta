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
            AndroidUtilities.runOnUIThread(new VideoCapturerDevice$$ExternalSyntheticLambda9(this, z));
        }
    }

    /* JADX WARNING: type inference failed for: r3v0, types: [boolean] */
    /* access modifiers changed from: private */
    /* JADX WARNING: Unknown variable types count: 1 */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public /* synthetic */ void lambda$new$0(boolean r3) {
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
        throw new UnsupportedOperationException("Method not decompiled: org.telegram.messenger.voip.VideoCapturerDevice.lambda$new$0(boolean):void");
    }

    public static void checkScreenCapturerSize() {
        if (instance[1] != null) {
            Point screenCaptureSize = getScreenCaptureSize();
            VideoCapturerDevice[] videoCapturerDeviceArr = instance;
            int i = videoCapturerDeviceArr[1].currentWidth;
            int i2 = screenCaptureSize.x;
            if (i != i2 || videoCapturerDeviceArr[1].currentHeight != screenCaptureSize.y) {
                videoCapturerDeviceArr[1].currentWidth = i2;
                videoCapturerDeviceArr[1].currentHeight = screenCaptureSize.y;
                videoCapturerDeviceArr[1].handler.post(new VideoCapturerDevice$$ExternalSyntheticLambda7(videoCapturerDeviceArr[1], screenCaptureSize));
            }
        }
    }

    /* access modifiers changed from: private */
    public static /* synthetic */ void lambda$checkScreenCapturerSize$1(VideoCapturerDevice videoCapturerDevice, Point point) {
        VideoCapturer videoCapturer2 = videoCapturerDevice.videoCapturer;
        if (videoCapturer2 != null) {
            videoCapturer2.changeCaptureFormat(point.x, point.y, 30);
        }
    }

    private static Point getScreenCaptureSize() {
        int i;
        int i2;
        Display defaultDisplay = ((WindowManager) ApplicationLoader.applicationContext.getSystemService("window")).getDefaultDisplay();
        Point point = new Point();
        defaultDisplay.getRealSize(point);
        int i3 = point.x;
        int i4 = point.y;
        float f = i3 > i4 ? ((float) i4) / ((float) i3) : ((float) i3) / ((float) i4);
        int i5 = 1;
        while (true) {
            if (i5 > 100) {
                i5 = -1;
                i = -1;
                break;
            }
            float f2 = ((float) i5) * f;
            i = (int) f2;
            if (f2 != ((float) i)) {
                i5++;
            } else if (point.x <= point.y) {
                int i6 = i;
                i = i5;
                i5 = i6;
            }
        }
        if (i5 != -1 && f != 1.0f) {
            while (true) {
                int i7 = point.x;
                if (i7 <= 1000 && (i2 = point.y) <= 1000 && i7 % 4 == 0 && i2 % 4 == 0) {
                    break;
                }
                int i8 = i7 - i5;
                point.x = i8;
                int i9 = point.y - i;
                point.y = i9;
                if (i8 < 800 && i9 < 800) {
                    i5 = -1;
                    break;
                }
            }
        }
        if (i5 == -1 || f == 1.0f) {
            float max = Math.max(((float) point.x) / 970.0f, ((float) point.y) / 970.0f);
            point.x = ((int) Math.ceil((double) ((((float) point.x) / max) / 4.0f))) * 4;
            point.y = ((int) Math.ceil((double) ((((float) point.y) / max) / 4.0f))) * 4;
        }
        return point;
    }

    private void init(long j, String str) {
        if (Build.VERSION.SDK_INT >= 18) {
            AndroidUtilities.runOnUIThread(new VideoCapturerDevice$$ExternalSyntheticLambda5(this, j, str));
        }
    }

    /* access modifiers changed from: private */
    public /* synthetic */ void lambda$init$5(long j, String str) {
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
                        this.videoCapturer = camera2Enumerator.createCapturer(str2, new CameraVideoCapturer.CameraEventsHandler() {
                            public void onCameraClosed() {
                            }

                            public void onCameraDisconnected() {
                            }

                            public void onCameraError(String str) {
                            }

                            public void onCameraFreezed(String str) {
                            }

                            public void onCameraOpening(String str) {
                            }

                            public void onFirstFrameAvailable() {
                                AndroidUtilities.runOnUIThread(VideoCapturerDevice$2$$ExternalSyntheticLambda0.INSTANCE);
                            }

                            /* access modifiers changed from: private */
                            public static /* synthetic */ void lambda$onFirstFrameAvailable$0() {
                                if (VoIPService.getSharedInstance() != null) {
                                    VoIPService.getSharedInstance().onCameraFirstFrameAvailable();
                                }
                            }
                        });
                        this.videoCapturerSurfaceTextureHelper = SurfaceTextureHelper.create("VideoCapturerThread", eglBase.getEglBaseContext());
                        this.handler.post(new VideoCapturerDevice$$ExternalSyntheticLambda0(this));
                        return;
                    }
                    this.handler.post(new VideoCapturerDevice$$ExternalSyntheticLambda8(this, str2));
                }
            } else if (Build.VERSION.SDK_INT >= 21 && this.videoCapturer == null) {
                this.videoCapturer = new ScreenCapturerAndroid(mediaProjectionPermissionResultData, new MediaProjection.Callback() {
                    public void onStop() {
                        AndroidUtilities.runOnUIThread(VideoCapturerDevice$1$$ExternalSyntheticLambda0.INSTANCE);
                    }

                    /* access modifiers changed from: private */
                    public static /* synthetic */ void lambda$onStop$0() {
                        if (VoIPService.getSharedInstance() != null) {
                            VoIPService.getSharedInstance().stopScreenCapture();
                        }
                    }
                });
                Point screenCaptureSize = getScreenCaptureSize();
                this.currentWidth = screenCaptureSize.x;
                this.currentHeight = screenCaptureSize.y;
                this.videoCapturerSurfaceTextureHelper = SurfaceTextureHelper.create("ScreenCapturerThread", eglBase.getEglBaseContext());
                this.handler.post(new VideoCapturerDevice$$ExternalSyntheticLambda6(this, screenCaptureSize));
            }
        }
    }

    /* access modifiers changed from: private */
    public /* synthetic */ void lambda$init$2(Point point) {
        if (this.videoCapturerSurfaceTextureHelper != null) {
            long j = this.nativePtr;
            if (j != 0) {
                this.nativeCapturerObserver = nativeGetJavaVideoCapturerObserver(j);
                this.videoCapturer.initialize(this.videoCapturerSurfaceTextureHelper, ApplicationLoader.applicationContext, this.nativeCapturerObserver);
                this.videoCapturer.startCapture(point.x, point.y, 30);
                WebRtcAudioRecord webRtcAudioRecord = WebRtcAudioRecord.Instance;
                if (webRtcAudioRecord != null) {
                    webRtcAudioRecord.initDeviceAudioRecord(((ScreenCapturerAndroid) this.videoCapturer).getMediaProjection());
                }
            }
        }
    }

    /* access modifiers changed from: private */
    public /* synthetic */ void lambda$init$3() {
        if (this.videoCapturerSurfaceTextureHelper != null) {
            this.nativeCapturerObserver = nativeGetJavaVideoCapturerObserver(this.nativePtr);
            this.videoCapturer.initialize(this.videoCapturerSurfaceTextureHelper, ApplicationLoader.applicationContext, this.nativeCapturerObserver);
            this.videoCapturer.startCapture(CAPTURE_WIDTH, CAPTURE_HEIGHT, 30);
        }
    }

    /* access modifiers changed from: private */
    public /* synthetic */ void lambda$init$4(String str) {
        ((CameraVideoCapturer) this.videoCapturer).switchCamera(new CameraVideoCapturer.CameraSwitchHandler() {
            public void onCameraSwitchError(String str) {
            }

            public void onCameraSwitchDone(boolean z) {
                AndroidUtilities.runOnUIThread(new VideoCapturerDevice$3$$ExternalSyntheticLambda0(z));
            }

            /* access modifiers changed from: private */
            public static /* synthetic */ void lambda$onCameraSwitchDone$0(boolean z) {
                if (VoIPService.getSharedInstance() != null) {
                    VoIPService.getSharedInstance().setSwitchingCamera(false, z);
                }
            }
        }, str);
    }

    public static MediaProjection getMediaProjection() {
        VideoCapturerDevice[] videoCapturerDeviceArr = instance;
        if (videoCapturerDeviceArr[1] == null) {
            return null;
        }
        return ((ScreenCapturerAndroid) videoCapturerDeviceArr[1].videoCapturer).getMediaProjection();
    }

    private void onStateChanged(long j, int i) {
        if (Build.VERSION.SDK_INT >= 18) {
            AndroidUtilities.runOnUIThread(new VideoCapturerDevice$$ExternalSyntheticLambda4(this, j, i));
        }
    }

    /* access modifiers changed from: private */
    public /* synthetic */ void lambda$onStateChanged$7(long j, int i) {
        if (this.nativePtr == j) {
            this.handler.post(new VideoCapturerDevice$$ExternalSyntheticLambda3(this, i));
        }
    }

    /* access modifiers changed from: private */
    public /* synthetic */ void lambda$onStateChanged$6(int i) {
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
            this.nativePtr = 0;
            AndroidUtilities.runOnUIThread(new VideoCapturerDevice$$ExternalSyntheticLambda1(this));
        }
    }

    /* access modifiers changed from: private */
    public /* synthetic */ void lambda$onDestroy$9() {
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
        this.handler.post(new VideoCapturerDevice$$ExternalSyntheticLambda2(this));
        try {
            this.thread.quitSafely();
        } catch (Exception e) {
            FileLog.e((Throwable) e);
        }
    }

    /* access modifiers changed from: private */
    public /* synthetic */ void lambda$onDestroy$8() {
        WebRtcAudioRecord webRtcAudioRecord;
        if ((this.videoCapturer instanceof ScreenCapturerAndroid) && (webRtcAudioRecord = WebRtcAudioRecord.Instance) != null) {
            webRtcAudioRecord.stopDeviceAudioRecord();
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
