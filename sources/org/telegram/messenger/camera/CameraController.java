package org.telegram.messenger.camera;

import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.graphics.SurfaceTexture;
import android.graphics.drawable.BitmapDrawable;
import android.hardware.Camera;
import android.media.MediaRecorder;
import android.os.Build;
import android.util.Base64;
import j$.util.Comparator;
import j$.util.function.Function;
import j$.util.function.ToDoubleFunction;
import j$.util.function.ToIntFunction;
import j$.util.function.ToLongFunction;
import java.io.File;
import java.io.FileOutputStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Locale;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import org.telegram.messenger.AndroidUtilities;
import org.telegram.messenger.ApplicationLoader;
import org.telegram.messenger.Bitmaps;
import org.telegram.messenger.BuildVars;
import org.telegram.messenger.FileLog;
import org.telegram.messenger.ImageLoader;
import org.telegram.messenger.MessagesController;
import org.telegram.messenger.NotificationCenter;
import org.telegram.messenger.Utilities;
import org.telegram.messenger.camera.CameraController;
import org.telegram.tgnet.SerializedData;

public class CameraController implements MediaRecorder.OnInfoListener {
    private static final int CORE_POOL_SIZE = 1;
    private static volatile CameraController Instance = null;
    private static final int KEEP_ALIVE_SECONDS = 60;
    private static final int MAX_POOL_SIZE = 1;
    protected ArrayList<String> availableFlashModes = new ArrayList<>();
    protected volatile ArrayList<CameraInfo> cameraInfos;
    private boolean cameraInitied;
    private boolean loadingCameras;
    private boolean mirrorRecorderVideo;
    private ArrayList<Runnable> onFinishCameraInitRunnables = new ArrayList<>();
    private VideoTakeCallback onVideoTakeCallback;
    private String recordedFile;
    private MediaRecorder recorder;
    CameraView recordingCurrentCameraView;
    private ThreadPoolExecutor threadPool = new ThreadPoolExecutor(1, 1, 60, TimeUnit.SECONDS, new LinkedBlockingQueue());

    public interface VideoTakeCallback {
        void onFinishVideoRecording(String str, long j);
    }

    public static CameraController getInstance() {
        CameraController cameraController = Instance;
        if (cameraController == null) {
            synchronized (CameraController.class) {
                cameraController = Instance;
                if (cameraController == null) {
                    cameraController = new CameraController();
                    Instance = cameraController;
                }
            }
        }
        return cameraController;
    }

    public void cancelOnInitRunnable(Runnable runnable) {
        this.onFinishCameraInitRunnables.remove(runnable);
    }

    public void initCamera(Runnable runnable) {
        initCamera(runnable, false);
    }

    private void initCamera(Runnable runnable, boolean z) {
        if (!this.cameraInitied) {
            if (runnable != null && !this.onFinishCameraInitRunnables.contains(runnable)) {
                this.onFinishCameraInitRunnables.add(runnable);
            }
            if (!this.loadingCameras && !this.cameraInitied) {
                this.loadingCameras = true;
                this.threadPool.execute(new Runnable(z, runnable) {
                    public final /* synthetic */ boolean f$1;
                    public final /* synthetic */ Runnable f$2;

                    {
                        this.f$1 = r2;
                        this.f$2 = r3;
                    }

                    public final void run() {
                        CameraController.this.lambda$initCamera$4$CameraController(this.f$1, this.f$2);
                    }
                });
            }
        }
    }

    /* access modifiers changed from: private */
    /* renamed from: lambda$initCamera$4 */
    public /* synthetic */ void lambda$initCamera$4$CameraController(boolean z, Runnable runnable) {
        Camera.CameraInfo cameraInfo;
        String str;
        CameraController cameraController = this;
        String str2 = "cameraCache";
        try {
            if (cameraController.cameraInfos == null) {
                SharedPreferences globalMainSettings = MessagesController.getGlobalMainSettings();
                String string = globalMainSettings.getString(str2, (String) null);
                $$Lambda$CameraController$E3tDUXRg82Vus5hSAMEPVmSGY_I r4 = $$Lambda$CameraController$E3tDUXRg82Vus5hSAMEPVmSGY_I.INSTANCE;
                ArrayList<CameraInfo> arrayList = new ArrayList<>();
                if (string != null) {
                    SerializedData serializedData = new SerializedData(Base64.decode(string, 0));
                    int readInt32 = serializedData.readInt32(false);
                    for (int i = 0; i < readInt32; i++) {
                        CameraInfo cameraInfo2 = new CameraInfo(serializedData.readInt32(false), serializedData.readInt32(false));
                        int readInt322 = serializedData.readInt32(false);
                        for (int i2 = 0; i2 < readInt322; i2++) {
                            cameraInfo2.previewSizes.add(new Size(serializedData.readInt32(false), serializedData.readInt32(false)));
                        }
                        int readInt323 = serializedData.readInt32(false);
                        for (int i3 = 0; i3 < readInt323; i3++) {
                            cameraInfo2.pictureSizes.add(new Size(serializedData.readInt32(false), serializedData.readInt32(false)));
                        }
                        arrayList.add(cameraInfo2);
                        Collections.sort(cameraInfo2.previewSizes, r4);
                        Collections.sort(cameraInfo2.pictureSizes, r4);
                    }
                    serializedData.cleanup();
                } else {
                    int numberOfCameras = Camera.getNumberOfCameras();
                    Camera.CameraInfo cameraInfo3 = new Camera.CameraInfo();
                    int i4 = 4;
                    int i5 = 0;
                    while (i5 < numberOfCameras) {
                        try {
                            Camera.getCameraInfo(i5, cameraInfo3);
                            CameraInfo cameraInfo4 = new CameraInfo(i5, cameraInfo3.facing);
                            if (ApplicationLoader.mainInterfacePaused) {
                                if (ApplicationLoader.externalInterfacePaused) {
                                    throw new RuntimeException("APP_PAUSED");
                                }
                            }
                            Camera open = Camera.open(cameraInfo4.getCameraId());
                            Camera.Parameters parameters = open.getParameters();
                            List<Camera.Size> supportedPreviewSizes = parameters.getSupportedPreviewSizes();
                            int i6 = 0;
                            while (true) {
                                cameraInfo = cameraInfo3;
                                if (i6 >= supportedPreviewSizes.size()) {
                                    break;
                                }
                                Camera.Size size = supportedPreviewSizes.get(i6);
                                int i7 = size.width;
                                List<Camera.Size> list = supportedPreviewSizes;
                                if (i7 != 1280 || size.height == 720) {
                                    int i8 = size.height;
                                    if (i8 < 2160 && i7 < 2160) {
                                        str = str2;
                                        cameraInfo4.previewSizes.add(new Size(i7, i8));
                                        if (BuildVars.LOGS_ENABLED) {
                                            FileLog.d("preview size = " + size.width + " " + size.height);
                                        }
                                        i6++;
                                        cameraInfo3 = cameraInfo;
                                        supportedPreviewSizes = list;
                                        str2 = str;
                                    }
                                }
                                str = str2;
                                i6++;
                                cameraInfo3 = cameraInfo;
                                supportedPreviewSizes = list;
                                str2 = str;
                            }
                            String str3 = str2;
                            List<Camera.Size> supportedPictureSizes = parameters.getSupportedPictureSizes();
                            int i9 = 0;
                            while (i9 < supportedPictureSizes.size()) {
                                Camera.Size size2 = supportedPictureSizes.get(i9);
                                if (size2.width == 1280) {
                                    if (size2.height != 720) {
                                        i9++;
                                    }
                                }
                                if (!"samsung".equals(Build.MANUFACTURER) || !"jflteuc".equals(Build.PRODUCT) || size2.width < 2048) {
                                    cameraInfo4.pictureSizes.add(new Size(size2.width, size2.height));
                                    if (BuildVars.LOGS_ENABLED) {
                                        FileLog.d("picture size = " + size2.width + " " + size2.height);
                                    }
                                    i9++;
                                } else {
                                    i9++;
                                }
                            }
                            open.release();
                            arrayList.add(cameraInfo4);
                            Collections.sort(cameraInfo4.previewSizes, r4);
                            Collections.sort(cameraInfo4.pictureSizes, r4);
                            i4 += ((cameraInfo4.previewSizes.size() + cameraInfo4.pictureSizes.size()) * 8) + 8;
                            i5++;
                            cameraController = this;
                            cameraInfo3 = cameraInfo;
                            str2 = str3;
                        } catch (Exception e) {
                            e = e;
                            cameraController = this;
                            AndroidUtilities.runOnUIThread(new Runnable(z, e, runnable) {
                                public final /* synthetic */ boolean f$1;
                                public final /* synthetic */ Exception f$2;
                                public final /* synthetic */ Runnable f$3;

                                {
                                    this.f$1 = r2;
                                    this.f$2 = r3;
                                    this.f$3 = r4;
                                }

                                public final void run() {
                                    CameraController.this.lambda$null$3$CameraController(this.f$1, this.f$2, this.f$3);
                                }
                            });
                        }
                    }
                    String str4 = str2;
                    SerializedData serializedData2 = new SerializedData(i4);
                    serializedData2.writeInt32(arrayList.size());
                    for (int i10 = 0; i10 < numberOfCameras; i10++) {
                        CameraInfo cameraInfo5 = arrayList.get(i10);
                        serializedData2.writeInt32(cameraInfo5.cameraId);
                        serializedData2.writeInt32(cameraInfo5.frontCamera);
                        int size3 = cameraInfo5.previewSizes.size();
                        serializedData2.writeInt32(size3);
                        for (int i11 = 0; i11 < size3; i11++) {
                            Size size4 = cameraInfo5.previewSizes.get(i11);
                            serializedData2.writeInt32(size4.mWidth);
                            serializedData2.writeInt32(size4.mHeight);
                        }
                        int size5 = cameraInfo5.pictureSizes.size();
                        serializedData2.writeInt32(size5);
                        for (int i12 = 0; i12 < size5; i12++) {
                            Size size6 = cameraInfo5.pictureSizes.get(i12);
                            serializedData2.writeInt32(size6.mWidth);
                            serializedData2.writeInt32(size6.mHeight);
                        }
                    }
                    globalMainSettings.edit().putString(str4, Base64.encodeToString(serializedData2.toByteArray(), 0)).commit();
                    serializedData2.cleanup();
                    cameraController = this;
                }
                cameraController.cameraInfos = arrayList;
            }
            AndroidUtilities.runOnUIThread(new Runnable() {
                public final void run() {
                    CameraController.this.lambda$null$1$CameraController();
                }
            });
        } catch (Exception e2) {
            e = e2;
            AndroidUtilities.runOnUIThread(new Runnable(z, e, runnable) {
                public final /* synthetic */ boolean f$1;
                public final /* synthetic */ Exception f$2;
                public final /* synthetic */ Runnable f$3;

                {
                    this.f$1 = r2;
                    this.f$2 = r3;
                    this.f$3 = r4;
                }

                public final void run() {
                    CameraController.this.lambda$null$3$CameraController(this.f$1, this.f$2, this.f$3);
                }
            });
        }
    }

    static /* synthetic */ int lambda$null$0(Size size, Size size2) {
        int i = size.mWidth;
        int i2 = size2.mWidth;
        if (i < i2) {
            return 1;
        }
        if (i > i2) {
            return -1;
        }
        int i3 = size.mHeight;
        int i4 = size2.mHeight;
        if (i3 < i4) {
            return 1;
        }
        if (i3 > i4) {
            return -1;
        }
        return 0;
    }

    /* access modifiers changed from: private */
    /* renamed from: lambda$null$1 */
    public /* synthetic */ void lambda$null$1$CameraController() {
        this.loadingCameras = false;
        this.cameraInitied = true;
        if (!this.onFinishCameraInitRunnables.isEmpty()) {
            for (int i = 0; i < this.onFinishCameraInitRunnables.size(); i++) {
                this.onFinishCameraInitRunnables.get(i).run();
            }
            this.onFinishCameraInitRunnables.clear();
        }
        NotificationCenter.getGlobalInstance().postNotificationName(NotificationCenter.cameraInitied, new Object[0]);
    }

    /* access modifiers changed from: private */
    /* renamed from: lambda$null$3 */
    public /* synthetic */ void lambda$null$3$CameraController(boolean z, Exception exc, Runnable runnable) {
        this.onFinishCameraInitRunnables.clear();
        this.loadingCameras = false;
        this.cameraInitied = false;
        if (!z && "APP_PAUSED".equals(exc.getMessage())) {
            AndroidUtilities.runOnUIThread(new Runnable(runnable) {
                public final /* synthetic */ Runnable f$1;

                {
                    this.f$1 = r2;
                }

                public final void run() {
                    CameraController.this.lambda$null$2$CameraController(this.f$1);
                }
            }, 1000);
        }
    }

    /* access modifiers changed from: private */
    /* renamed from: lambda$null$2 */
    public /* synthetic */ void lambda$null$2$CameraController(Runnable runnable) {
        initCamera(runnable, true);
    }

    public boolean isCameraInitied() {
        return this.cameraInitied && this.cameraInfos != null && !this.cameraInfos.isEmpty();
    }

    public void close(CameraSession cameraSession, CountDownLatch countDownLatch, Runnable runnable) {
        cameraSession.destroy();
        this.threadPool.execute(new Runnable(runnable, cameraSession, countDownLatch) {
            public final /* synthetic */ Runnable f$0;
            public final /* synthetic */ CameraSession f$1;
            public final /* synthetic */ CountDownLatch f$2;

            {
                this.f$0 = r1;
                this.f$1 = r2;
                this.f$2 = r3;
            }

            public final void run() {
                CameraController.lambda$close$5(this.f$0, this.f$1, this.f$2);
            }
        });
        if (countDownLatch != null) {
            try {
                countDownLatch.await();
            } catch (Exception e) {
                FileLog.e((Throwable) e);
            }
        }
    }

    static /* synthetic */ void lambda$close$5(Runnable runnable, CameraSession cameraSession, CountDownLatch countDownLatch) {
        if (runnable != null) {
            runnable.run();
        }
        Camera camera = cameraSession.cameraInfo.camera;
        if (camera != null) {
            try {
                camera.stopPreview();
                cameraSession.cameraInfo.camera.setPreviewCallbackWithBuffer((Camera.PreviewCallback) null);
            } catch (Exception e) {
                FileLog.e((Throwable) e);
            }
            try {
                cameraSession.cameraInfo.camera.release();
            } catch (Exception e2) {
                FileLog.e((Throwable) e2);
            }
            cameraSession.cameraInfo.camera = null;
        }
        if (countDownLatch != null) {
            countDownLatch.countDown();
        }
    }

    public ArrayList<CameraInfo> getCameras() {
        return this.cameraInfos;
    }

    /* JADX WARNING: Code restructure failed: missing block: B:32:0x005f, code lost:
        r1 = r2;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:33:0x0060, code lost:
        r3 = 0;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:34:0x0061, code lost:
        if (r3 <= 8) goto L_0x00b9;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:35:0x0063, code lost:
        r2 = pack(r10, r1, 4, false);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:36:0x006a, code lost:
        if (r2 == NUM) goto L_0x0072;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:38:0x006f, code lost:
        if (r2 == NUM) goto L_0x0072;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:39:0x0071, code lost:
        return 0;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:40:0x0072, code lost:
        if (r2 != NUM) goto L_0x0075;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:41:0x0075, code lost:
        r5 = false;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:42:0x0076, code lost:
        r2 = pack(r10, r1 + 4, 4, r5) + 2;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:43:0x007f, code lost:
        if (r2 < 10) goto L_0x00b9;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:44:0x0081, code lost:
        if (r2 <= r3) goto L_0x0084;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:45:0x0084, code lost:
        r1 = r1 + r2;
        r3 = r3 - r2;
        r2 = pack(r10, r1 - 2, 2, r5);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:46:0x008c, code lost:
        r4 = r2 - 1;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:47:0x008e, code lost:
        if (r2 <= 0) goto L_0x00b9;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:49:0x0092, code lost:
        if (r3 < 12) goto L_0x00b9;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:51:0x009a, code lost:
        if (pack(r10, r1, 2, r5) != 274) goto L_0x00b3;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:52:0x009c, code lost:
        r10 = pack(r10, r1 + 8, 2, r5);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:53:0x00a2, code lost:
        if (r10 == 3) goto L_0x00b0;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:55:0x00a5, code lost:
        if (r10 == 6) goto L_0x00ad;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:56:0x00a7, code lost:
        if (r10 == 8) goto L_0x00aa;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:57:0x00a9, code lost:
        return 0;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:58:0x00aa, code lost:
        return 270;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:59:0x00ad, code lost:
        return 90;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:60:0x00b0, code lost:
        return 180;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:61:0x00b3, code lost:
        r1 = r1 + 12;
        r3 = r3 - 12;
        r2 = r4;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:62:0x00b9, code lost:
        return 0;
     */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    private static int getOrientation(byte[] r10) {
        /*
            r0 = 0
            if (r10 != 0) goto L_0x0004
            return r0
        L_0x0004:
            r1 = 0
        L_0x0005:
            int r2 = r1 + 3
            int r3 = r10.length
            r4 = 4
            r5 = 1
            r6 = 8
            r7 = 2
            if (r2 >= r3) goto L_0x0060
            int r2 = r1 + 1
            byte r1 = r10[r1]
            r3 = 255(0xff, float:3.57E-43)
            r1 = r1 & r3
            if (r1 != r3) goto L_0x005f
            byte r1 = r10[r2]
            r1 = r1 & r3
            if (r1 != r3) goto L_0x001e
            goto L_0x005d
        L_0x001e:
            int r2 = r2 + 1
            r3 = 216(0xd8, float:3.03E-43)
            if (r1 == r3) goto L_0x005d
            if (r1 != r5) goto L_0x0027
            goto L_0x005d
        L_0x0027:
            r3 = 217(0xd9, float:3.04E-43)
            if (r1 == r3) goto L_0x005f
            r3 = 218(0xda, float:3.05E-43)
            if (r1 != r3) goto L_0x0030
            goto L_0x005f
        L_0x0030:
            int r3 = pack(r10, r2, r7, r0)
            if (r3 < r7) goto L_0x005c
            int r8 = r2 + r3
            int r9 = r10.length
            if (r8 <= r9) goto L_0x003c
            goto L_0x005c
        L_0x003c:
            r9 = 225(0xe1, float:3.15E-43)
            if (r1 != r9) goto L_0x005a
            if (r3 < r6) goto L_0x005a
            int r1 = r2 + 2
            int r1 = pack(r10, r1, r4, r0)
            r9 = 1165519206(0x45786966, float:3974.5874)
            if (r1 != r9) goto L_0x005a
            int r1 = r2 + 6
            int r1 = pack(r10, r1, r7, r0)
            if (r1 != 0) goto L_0x005a
            int r1 = r2 + 8
            int r3 = r3 + -8
            goto L_0x0061
        L_0x005a:
            r1 = r8
            goto L_0x0005
        L_0x005c:
            return r0
        L_0x005d:
            r1 = r2
            goto L_0x0005
        L_0x005f:
            r1 = r2
        L_0x0060:
            r3 = 0
        L_0x0061:
            if (r3 <= r6) goto L_0x00b9
            int r2 = pack(r10, r1, r4, r0)
            r8 = 1229531648(0x49492a00, float:823968.0)
            if (r2 == r8) goto L_0x0072
            r9 = 1296891946(0x4d4d002a, float:2.14958752E8)
            if (r2 == r9) goto L_0x0072
            return r0
        L_0x0072:
            if (r2 != r8) goto L_0x0075
            goto L_0x0076
        L_0x0075:
            r5 = 0
        L_0x0076:
            int r2 = r1 + 4
            int r2 = pack(r10, r2, r4, r5)
            int r2 = r2 + r7
            r4 = 10
            if (r2 < r4) goto L_0x00b9
            if (r2 <= r3) goto L_0x0084
            goto L_0x00b9
        L_0x0084:
            int r1 = r1 + r2
            int r3 = r3 - r2
            int r2 = r1 + -2
            int r2 = pack(r10, r2, r7, r5)
        L_0x008c:
            int r4 = r2 + -1
            if (r2 <= 0) goto L_0x00b9
            r2 = 12
            if (r3 < r2) goto L_0x00b9
            int r2 = pack(r10, r1, r7, r5)
            r8 = 274(0x112, float:3.84E-43)
            if (r2 != r8) goto L_0x00b3
            int r1 = r1 + r6
            int r10 = pack(r10, r1, r7, r5)
            r1 = 3
            if (r10 == r1) goto L_0x00b0
            r1 = 6
            if (r10 == r1) goto L_0x00ad
            if (r10 == r6) goto L_0x00aa
            return r0
        L_0x00aa:
            r10 = 270(0x10e, float:3.78E-43)
            return r10
        L_0x00ad:
            r10 = 90
            return r10
        L_0x00b0:
            r10 = 180(0xb4, float:2.52E-43)
            return r10
        L_0x00b3:
            int r1 = r1 + 12
            int r3 = r3 + -12
            r2 = r4
            goto L_0x008c
        L_0x00b9:
            return r0
        */
        throw new UnsupportedOperationException("Method not decompiled: org.telegram.messenger.camera.CameraController.getOrientation(byte[]):int");
    }

    private static int pack(byte[] bArr, int i, int i2, boolean z) {
        int i3;
        if (z) {
            i += i2 - 1;
            i3 = -1;
        } else {
            i3 = 1;
        }
        byte b = 0;
        while (true) {
            int i4 = i2 - 1;
            if (i2 <= 0) {
                return b;
            }
            b = (bArr[i] & 255) | (b << 8);
            i += i3;
            i2 = i4;
        }
    }

    public boolean takePicture(File file, CameraSession cameraSession, Runnable runnable) {
        if (cameraSession == null) {
            return false;
        }
        CameraInfo cameraInfo = cameraSession.cameraInfo;
        boolean isFlipFront = cameraSession.isFlipFront();
        try {
            cameraInfo.camera.takePicture((Camera.ShutterCallback) null, (Camera.PictureCallback) null, new Camera.PictureCallback(file, cameraInfo, isFlipFront, runnable) {
                public final /* synthetic */ File f$0;
                public final /* synthetic */ CameraInfo f$1;
                public final /* synthetic */ boolean f$2;
                public final /* synthetic */ Runnable f$3;

                {
                    this.f$0 = r1;
                    this.f$1 = r2;
                    this.f$2 = r3;
                    this.f$3 = r4;
                }

                public final void onPictureTaken(byte[] bArr, Camera camera) {
                    CameraController.lambda$takePicture$6(this.f$0, this.f$1, this.f$2, this.f$3, bArr, camera);
                }
            });
            return true;
        } catch (Exception e) {
            FileLog.e((Throwable) e);
            return false;
        }
    }

    static /* synthetic */ void lambda$takePicture$6(File file, CameraInfo cameraInfo, boolean z, Runnable runnable, byte[] bArr, Camera camera) {
        Bitmap bitmap;
        int photoSize = (int) (((float) AndroidUtilities.getPhotoSize()) / AndroidUtilities.density);
        String format = String.format(Locale.US, "%s@%d_%d", new Object[]{Utilities.MD5(file.getAbsolutePath()), Integer.valueOf(photoSize), Integer.valueOf(photoSize)});
        try {
            BitmapFactory.Options options = new BitmapFactory.Options();
            options.inJustDecodeBounds = true;
            BitmapFactory.decodeByteArray(bArr, 0, bArr.length, options);
            options.inJustDecodeBounds = false;
            options.inPurgeable = true;
            bitmap = BitmapFactory.decodeByteArray(bArr, 0, bArr.length, options);
        } catch (Throwable th) {
            FileLog.e(th);
            bitmap = null;
        }
        try {
            if (cameraInfo.frontCamera != 0 && z) {
                try {
                    Matrix matrix = new Matrix();
                    matrix.setRotate((float) getOrientation(bArr));
                    matrix.postScale(-1.0f, 1.0f);
                    Bitmap createBitmap = Bitmaps.createBitmap(bitmap, 0, 0, bitmap.getWidth(), bitmap.getHeight(), matrix, true);
                    if (createBitmap != bitmap) {
                        bitmap.recycle();
                    }
                    FileOutputStream fileOutputStream = new FileOutputStream(file);
                    createBitmap.compress(Bitmap.CompressFormat.JPEG, 80, fileOutputStream);
                    fileOutputStream.flush();
                    fileOutputStream.getFD().sync();
                    fileOutputStream.close();
                    ImageLoader.getInstance().putImageToCache(new BitmapDrawable(createBitmap), format);
                    if (runnable != null) {
                        runnable.run();
                        return;
                    }
                    return;
                } catch (Throwable th2) {
                    FileLog.e(th2);
                }
            }
            FileOutputStream fileOutputStream2 = new FileOutputStream(file);
            fileOutputStream2.write(bArr);
            fileOutputStream2.flush();
            fileOutputStream2.getFD().sync();
            fileOutputStream2.close();
            if (bitmap != null) {
                ImageLoader.getInstance().putImageToCache(new BitmapDrawable(bitmap), format);
            }
        } catch (Exception e) {
            FileLog.e((Throwable) e);
        }
        if (runnable != null) {
            runnable.run();
        }
    }

    public void startPreview(CameraSession cameraSession) {
        if (cameraSession != null) {
            this.threadPool.execute(new Runnable() {
                public final void run() {
                    CameraController.lambda$startPreview$7(CameraSession.this);
                }
            });
        }
    }

    static /* synthetic */ void lambda$startPreview$7(CameraSession cameraSession) {
        CameraInfo cameraInfo = cameraSession.cameraInfo;
        Camera camera = cameraInfo.camera;
        if (camera == null) {
            try {
                Camera open = Camera.open(cameraInfo.cameraId);
                cameraInfo.camera = open;
                camera = open;
            } catch (Exception e) {
                cameraSession.cameraInfo.camera = null;
                if (camera != null) {
                    camera.release();
                }
                FileLog.e((Throwable) e);
                return;
            }
        }
        camera.startPreview();
    }

    public void stopPreview(CameraSession cameraSession) {
        if (cameraSession != null) {
            this.threadPool.execute(new Runnable() {
                public final void run() {
                    CameraController.lambda$stopPreview$8(CameraSession.this);
                }
            });
        }
    }

    static /* synthetic */ void lambda$stopPreview$8(CameraSession cameraSession) {
        CameraInfo cameraInfo = cameraSession.cameraInfo;
        Camera camera = cameraInfo.camera;
        if (camera == null) {
            try {
                Camera open = Camera.open(cameraInfo.cameraId);
                cameraInfo.camera = open;
                camera = open;
            } catch (Exception e) {
                cameraSession.cameraInfo.camera = null;
                if (camera != null) {
                    camera.release();
                }
                FileLog.e((Throwable) e);
                return;
            }
        }
        camera.stopPreview();
    }

    public void openRound(CameraSession cameraSession, SurfaceTexture surfaceTexture, Runnable runnable, Runnable runnable2) {
        if (cameraSession != null && surfaceTexture != null) {
            this.threadPool.execute(new Runnable(runnable2, surfaceTexture, runnable) {
                public final /* synthetic */ Runnable f$1;
                public final /* synthetic */ SurfaceTexture f$2;
                public final /* synthetic */ Runnable f$3;

                {
                    this.f$1 = r2;
                    this.f$2 = r3;
                    this.f$3 = r4;
                }

                public final void run() {
                    CameraController.lambda$openRound$9(CameraSession.this, this.f$1, this.f$2, this.f$3);
                }
            });
        } else if (BuildVars.LOGS_ENABLED) {
            FileLog.d("failed to open round " + cameraSession + " tex = " + surfaceTexture);
        }
    }

    static /* synthetic */ void lambda$openRound$9(CameraSession cameraSession, Runnable runnable, SurfaceTexture surfaceTexture, Runnable runnable2) {
        Camera camera = cameraSession.cameraInfo.camera;
        try {
            if (BuildVars.LOGS_ENABLED) {
                FileLog.d("start creating round camera session");
            }
            if (camera == null) {
                CameraInfo cameraInfo = cameraSession.cameraInfo;
                Camera open = Camera.open(cameraInfo.cameraId);
                cameraInfo.camera = open;
                camera = open;
            }
            camera.getParameters();
            cameraSession.configureRoundCamera();
            if (runnable != null) {
                runnable.run();
            }
            camera.setPreviewTexture(surfaceTexture);
            camera.startPreview();
            if (runnable2 != null) {
                AndroidUtilities.runOnUIThread(runnable2);
            }
            if (BuildVars.LOGS_ENABLED) {
                FileLog.d("round camera session created");
            }
        } catch (Exception e) {
            cameraSession.cameraInfo.camera = null;
            if (camera != null) {
                camera.release();
            }
            FileLog.e((Throwable) e);
        }
    }

    public void open(CameraSession cameraSession, SurfaceTexture surfaceTexture, Runnable runnable, Runnable runnable2) {
        if (cameraSession != null && surfaceTexture != null) {
            this.threadPool.execute(new Runnable(cameraSession, runnable2, surfaceTexture, runnable) {
                public final /* synthetic */ CameraSession f$1;
                public final /* synthetic */ Runnable f$2;
                public final /* synthetic */ SurfaceTexture f$3;
                public final /* synthetic */ Runnable f$4;

                {
                    this.f$1 = r2;
                    this.f$2 = r3;
                    this.f$3 = r4;
                    this.f$4 = r5;
                }

                public final void run() {
                    CameraController.this.lambda$open$10$CameraController(this.f$1, this.f$2, this.f$3, this.f$4);
                }
            });
        }
    }

    /* access modifiers changed from: private */
    /* renamed from: lambda$open$10 */
    public /* synthetic */ void lambda$open$10$CameraController(CameraSession cameraSession, Runnable runnable, SurfaceTexture surfaceTexture, Runnable runnable2) {
        CameraInfo cameraInfo = cameraSession.cameraInfo;
        Camera camera = cameraInfo.camera;
        if (camera == null) {
            try {
                Camera open = Camera.open(cameraInfo.cameraId);
                cameraInfo.camera = open;
                camera = open;
            } catch (Exception e) {
                cameraSession.cameraInfo.camera = null;
                if (camera != null) {
                    camera.release();
                }
                FileLog.e((Throwable) e);
                return;
            }
        }
        List<String> supportedFlashModes = camera.getParameters().getSupportedFlashModes();
        this.availableFlashModes.clear();
        if (supportedFlashModes != null) {
            for (int i = 0; i < supportedFlashModes.size(); i++) {
                String str = supportedFlashModes.get(i);
                if (str.equals("off") || str.equals("on") || str.equals("auto")) {
                    this.availableFlashModes.add(str);
                }
            }
            cameraSession.checkFlashMode(this.availableFlashModes.get(0));
        }
        if (runnable != null) {
            runnable.run();
        }
        cameraSession.configurePhotoCamera();
        camera.setPreviewTexture(surfaceTexture);
        camera.startPreview();
        if (runnable2 != null) {
            AndroidUtilities.runOnUIThread(runnable2);
        }
    }

    public void recordVideo(CameraSession cameraSession, File file, boolean z, VideoTakeCallback videoTakeCallback, Runnable runnable, CameraView cameraView) {
        CameraSession cameraSession2 = cameraSession;
        CameraView cameraView2 = cameraView;
        if (cameraSession2 != null) {
            CameraInfo cameraInfo = cameraSession2.cameraInfo;
            Camera camera = cameraInfo.camera;
            if (cameraView2 != null) {
                this.recordingCurrentCameraView = cameraView2;
                this.onVideoTakeCallback = videoTakeCallback;
                this.recordedFile = file.getAbsolutePath();
                this.threadPool.execute(new Runnable(camera, cameraSession, cameraView, file, runnable) {
                    public final /* synthetic */ Camera f$1;
                    public final /* synthetic */ CameraSession f$2;
                    public final /* synthetic */ CameraView f$3;
                    public final /* synthetic */ File f$4;
                    public final /* synthetic */ Runnable f$5;

                    {
                        this.f$1 = r2;
                        this.f$2 = r3;
                        this.f$3 = r4;
                        this.f$4 = r5;
                        this.f$5 = r6;
                    }

                    public final void run() {
                        CameraController.this.lambda$recordVideo$13$CameraController(this.f$1, this.f$2, this.f$3, this.f$4, this.f$5);
                    }
                });
                return;
            }
            this.threadPool.execute(new Runnable(camera, cameraSession, z, file, cameraInfo, videoTakeCallback, runnable) {
                public final /* synthetic */ Camera f$1;
                public final /* synthetic */ CameraSession f$2;
                public final /* synthetic */ boolean f$3;
                public final /* synthetic */ File f$4;
                public final /* synthetic */ CameraInfo f$5;
                public final /* synthetic */ CameraController.VideoTakeCallback f$6;
                public final /* synthetic */ Runnable f$7;

                {
                    this.f$1 = r2;
                    this.f$2 = r3;
                    this.f$3 = r4;
                    this.f$4 = r5;
                    this.f$5 = r6;
                    this.f$6 = r7;
                    this.f$7 = r8;
                }

                public final void run() {
                    CameraController.this.lambda$recordVideo$14$CameraController(this.f$1, this.f$2, this.f$3, this.f$4, this.f$5, this.f$6, this.f$7);
                }
            });
        }
    }

    /* access modifiers changed from: private */
    /* renamed from: lambda$recordVideo$13 */
    public /* synthetic */ void lambda$recordVideo$13$CameraController(Camera camera, CameraSession cameraSession, CameraView cameraView, File file, Runnable runnable) {
        if (camera != null) {
            try {
                Camera.Parameters parameters = camera.getParameters();
                parameters.setFlashMode(cameraSession.getCurrentFlashMode().equals("on") ? "torch" : "off");
                camera.setParameters(parameters);
            } catch (Exception e) {
                try {
                    FileLog.e((Throwable) e);
                } catch (Exception e2) {
                    FileLog.e((Throwable) e2);
                    return;
                }
            }
            AndroidUtilities.runOnUIThread(new Runnable(cameraView, file, runnable) {
                public final /* synthetic */ CameraView f$1;
                public final /* synthetic */ File f$2;
                public final /* synthetic */ Runnable f$3;

                {
                    this.f$1 = r2;
                    this.f$2 = r3;
                    this.f$3 = r4;
                }

                public final void run() {
                    CameraController.this.lambda$null$12$CameraController(this.f$1, this.f$2, this.f$3);
                }
            });
        }
    }

    /* access modifiers changed from: private */
    /* renamed from: lambda$null$12 */
    public /* synthetic */ void lambda$null$12$CameraController(CameraView cameraView, File file, Runnable runnable) {
        cameraView.startRecording(file, new Runnable() {
            public final void run() {
                CameraController.this.lambda$null$11$CameraController();
            }
        });
        if (runnable != null) {
            runnable.run();
        }
    }

    /* access modifiers changed from: private */
    /* renamed from: lambda$recordVideo$14 */
    public /* synthetic */ void lambda$recordVideo$14$CameraController(Camera camera, CameraSession cameraSession, boolean z, File file, CameraInfo cameraInfo, VideoTakeCallback videoTakeCallback, Runnable runnable) {
        if (camera != null) {
            try {
                Camera.Parameters parameters = camera.getParameters();
                parameters.setFlashMode(cameraSession.getCurrentFlashMode().equals("on") ? "torch" : "off");
                camera.setParameters(parameters);
            } catch (Exception e) {
                try {
                    FileLog.e((Throwable) e);
                } catch (Exception e2) {
                    FileLog.e((Throwable) e2);
                    return;
                }
            }
            camera.unlock();
            try {
                this.mirrorRecorderVideo = z;
                MediaRecorder mediaRecorder = new MediaRecorder();
                this.recorder = mediaRecorder;
                mediaRecorder.setCamera(camera);
                this.recorder.setVideoSource(1);
                this.recorder.setAudioSource(5);
                cameraSession.configureRecorder(1, this.recorder);
                this.recorder.setOutputFile(file.getAbsolutePath());
                this.recorder.setMaxFileSize(NUM);
                this.recorder.setVideoFrameRate(30);
                this.recorder.setMaxDuration(0);
                Size chooseOptimalSize = chooseOptimalSize(cameraInfo.getPictureSizes(), 720, 480, new Size(16, 9));
                this.recorder.setVideoEncodingBitRate(Math.min(chooseOptimalSize.mHeight, chooseOptimalSize.mWidth) >= 720 ? 3500000 : 1800000);
                this.recorder.setVideoSize(chooseOptimalSize.getWidth(), chooseOptimalSize.getHeight());
                this.recorder.setOnInfoListener(this);
                this.recorder.prepare();
                this.recorder.start();
                this.onVideoTakeCallback = videoTakeCallback;
                this.recordedFile = file.getAbsolutePath();
                if (runnable != null) {
                    AndroidUtilities.runOnUIThread(runnable);
                }
            } catch (Exception e3) {
                this.recorder.release();
                this.recorder = null;
                FileLog.e((Throwable) e3);
            }
        }
    }

    /* access modifiers changed from: private */
    /* JADX WARNING: Removed duplicated region for block: B:19:0x0038 A[SYNTHETIC, Splitter:B:19:0x0038] */
    /* JADX WARNING: Removed duplicated region for block: B:25:0x004c  */
    /* JADX WARNING: Removed duplicated region for block: B:26:0x007d  */
    /* JADX WARNING: Removed duplicated region for block: B:36:0x00c4 A[SYNTHETIC, Splitter:B:36:0x00c4] */
    /* renamed from: finishRecordingVideo */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public void lambda$null$11() {
        /*
            r10 = this;
            r0 = 0
            r1 = 0
            android.media.MediaMetadataRetriever r3 = new android.media.MediaMetadataRetriever     // Catch:{ Exception -> 0x0031, all -> 0x002c }
            r3.<init>()     // Catch:{ Exception -> 0x0031, all -> 0x002c }
            java.lang.String r4 = r10.recordedFile     // Catch:{ Exception -> 0x002a }
            r3.setDataSource(r4)     // Catch:{ Exception -> 0x002a }
            r4 = 9
            java.lang.String r4 = r3.extractMetadata(r4)     // Catch:{ Exception -> 0x002a }
            if (r4 == 0) goto L_0x0024
            long r4 = java.lang.Long.parseLong(r4)     // Catch:{ Exception -> 0x002a }
            float r4 = (float) r4     // Catch:{ Exception -> 0x002a }
            r5 = 1148846080(0x447a0000, float:1000.0)
            float r4 = r4 / r5
            double r4 = (double) r4     // Catch:{ Exception -> 0x002a }
            double r1 = java.lang.Math.ceil(r4)     // Catch:{ Exception -> 0x002a }
            int r1 = (int) r1
            long r1 = (long) r1
        L_0x0024:
            r3.release()     // Catch:{ Exception -> 0x0028 }
            goto L_0x0040
        L_0x0028:
            r3 = move-exception
            goto L_0x003d
        L_0x002a:
            r4 = move-exception
            goto L_0x0033
        L_0x002c:
            r1 = move-exception
            r3 = r0
            r0 = r1
            goto L_0x00c2
        L_0x0031:
            r4 = move-exception
            r3 = r0
        L_0x0033:
            org.telegram.messenger.FileLog.e((java.lang.Throwable) r4)     // Catch:{ all -> 0x00c1 }
            if (r3 == 0) goto L_0x0040
            r3.release()     // Catch:{ Exception -> 0x003c }
            goto L_0x0040
        L_0x003c:
            r3 = move-exception
        L_0x003d:
            org.telegram.messenger.FileLog.e((java.lang.Throwable) r3)
        L_0x0040:
            r8 = r1
            java.lang.String r1 = r10.recordedFile
            r2 = 1
            android.graphics.Bitmap r1 = org.telegram.messenger.SendMessagesHelper.createVideoThumbnail(r1, r2)
            boolean r2 = r10.mirrorRecorderVideo
            if (r2 == 0) goto L_0x007d
            int r2 = r1.getWidth()
            int r3 = r1.getHeight()
            android.graphics.Bitmap$Config r4 = android.graphics.Bitmap.Config.ARGB_8888
            android.graphics.Bitmap r2 = android.graphics.Bitmap.createBitmap(r2, r3, r4)
            android.graphics.Canvas r3 = new android.graphics.Canvas
            r3.<init>(r2)
            r4 = -1082130432(0xffffffffbvar_, float:-1.0)
            r5 = 1065353216(0x3var_, float:1.0)
            int r6 = r2.getWidth()
            int r6 = r6 / 2
            float r6 = (float) r6
            int r7 = r2.getHeight()
            int r7 = r7 / 2
            float r7 = (float) r7
            r3.scale(r4, r5, r6, r7)
            r4 = 0
            r3.drawBitmap(r1, r4, r4, r0)
            r1.recycle()
            r7 = r2
            goto L_0x007e
        L_0x007d:
            r7 = r1
        L_0x007e:
            java.lang.StringBuilder r0 = new java.lang.StringBuilder
            r0.<init>()
            java.lang.String r1 = "-2147483648_"
            r0.append(r1)
            int r1 = org.telegram.messenger.SharedConfig.getLastLocalId()
            r0.append(r1)
            java.lang.String r1 = ".jpg"
            r0.append(r1)
            java.lang.String r0 = r0.toString()
            java.io.File r6 = new java.io.File
            r1 = 4
            java.io.File r1 = org.telegram.messenger.FileLoader.getDirectory(r1)
            r6.<init>(r1, r0)
            java.io.FileOutputStream r0 = new java.io.FileOutputStream     // Catch:{ all -> 0x00af }
            r0.<init>(r6)     // Catch:{ all -> 0x00af }
            android.graphics.Bitmap$CompressFormat r1 = android.graphics.Bitmap.CompressFormat.JPEG     // Catch:{ all -> 0x00af }
            r2 = 87
            r7.compress(r1, r2, r0)     // Catch:{ all -> 0x00af }
            goto L_0x00b3
        L_0x00af:
            r0 = move-exception
            org.telegram.messenger.FileLog.e((java.lang.Throwable) r0)
        L_0x00b3:
            org.telegram.messenger.SharedConfig.saveConfig()
            org.telegram.messenger.camera.-$$Lambda$CameraController$slW3dxdoaR-kkcvar__SO3G77e68 r0 = new org.telegram.messenger.camera.-$$Lambda$CameraController$slW3dxdoaR-kkcvar__SO3G77e68
            r4 = r0
            r5 = r10
            r4.<init>(r6, r7, r8)
            org.telegram.messenger.AndroidUtilities.runOnUIThread(r0)
            return
        L_0x00c1:
            r0 = move-exception
        L_0x00c2:
            if (r3 == 0) goto L_0x00cc
            r3.release()     // Catch:{ Exception -> 0x00c8 }
            goto L_0x00cc
        L_0x00c8:
            r1 = move-exception
            org.telegram.messenger.FileLog.e((java.lang.Throwable) r1)
        L_0x00cc:
            throw r0
        */
        throw new UnsupportedOperationException("Method not decompiled: org.telegram.messenger.camera.CameraController.lambda$null$11():void");
    }

    /* access modifiers changed from: private */
    /* renamed from: lambda$finishRecordingVideo$15 */
    public /* synthetic */ void lambda$finishRecordingVideo$15$CameraController(File file, Bitmap bitmap, long j) {
        if (this.onVideoTakeCallback != null) {
            String absolutePath = file.getAbsolutePath();
            if (bitmap != null) {
                ImageLoader.getInstance().putImageToCache(new BitmapDrawable(bitmap), Utilities.MD5(absolutePath));
            }
            this.onVideoTakeCallback.onFinishVideoRecording(absolutePath, j);
            this.onVideoTakeCallback = null;
        }
    }

    public void onInfo(MediaRecorder mediaRecorder, int i, int i2) {
        if (i == 800 || i == 801 || i == 1) {
            MediaRecorder mediaRecorder2 = this.recorder;
            this.recorder = null;
            if (mediaRecorder2 != null) {
                mediaRecorder2.stop();
                mediaRecorder2.release();
            }
            if (this.onVideoTakeCallback != null) {
                lambda$null$11();
            }
        }
    }

    public void stopVideoRecording(CameraSession cameraSession, boolean z) {
        CameraView cameraView = this.recordingCurrentCameraView;
        if (cameraView != null) {
            cameraView.stopRecording();
            this.recordingCurrentCameraView = null;
            return;
        }
        this.threadPool.execute(new Runnable(cameraSession, z) {
            public final /* synthetic */ CameraSession f$1;
            public final /* synthetic */ boolean f$2;

            {
                this.f$1 = r2;
                this.f$2 = r3;
            }

            public final void run() {
                CameraController.this.lambda$stopVideoRecording$17$CameraController(this.f$1, this.f$2);
            }
        });
    }

    /* access modifiers changed from: private */
    /* renamed from: lambda$stopVideoRecording$17 */
    public /* synthetic */ void lambda$stopVideoRecording$17$CameraController(CameraSession cameraSession, boolean z) {
        MediaRecorder mediaRecorder;
        try {
            Camera camera = cameraSession.cameraInfo.camera;
            if (!(camera == null || (mediaRecorder = this.recorder) == null)) {
                this.recorder = null;
                try {
                    mediaRecorder.stop();
                } catch (Exception e) {
                    FileLog.e((Throwable) e);
                }
                try {
                    mediaRecorder.release();
                } catch (Exception e2) {
                    FileLog.e((Throwable) e2);
                }
                try {
                    camera.reconnect();
                    camera.startPreview();
                } catch (Exception e3) {
                    FileLog.e((Throwable) e3);
                }
                try {
                    cameraSession.stopVideoRecording();
                } catch (Exception e4) {
                    FileLog.e((Throwable) e4);
                }
            }
            try {
                Camera.Parameters parameters = camera.getParameters();
                parameters.setFlashMode("off");
                camera.setParameters(parameters);
            } catch (Exception e5) {
                FileLog.e((Throwable) e5);
            }
            this.threadPool.execute(new Runnable(camera, cameraSession) {
                public final /* synthetic */ Camera f$0;
                public final /* synthetic */ CameraSession f$1;

                {
                    this.f$0 = r1;
                    this.f$1 = r2;
                }

                public final void run() {
                    CameraController.lambda$null$16(this.f$0, this.f$1);
                }
            });
            if (z || this.onVideoTakeCallback == null) {
                this.onVideoTakeCallback = null;
            } else {
                lambda$null$11();
            }
        } catch (Exception unused) {
        }
    }

    static /* synthetic */ void lambda$null$16(Camera camera, CameraSession cameraSession) {
        try {
            Camera.Parameters parameters = camera.getParameters();
            parameters.setFlashMode(cameraSession.getCurrentFlashMode());
            camera.setParameters(parameters);
        } catch (Exception e) {
            FileLog.e((Throwable) e);
        }
    }

    public static Size chooseOptimalSize(List<Size> list, int i, int i2, Size size) {
        ArrayList arrayList = new ArrayList();
        int width = size.getWidth();
        int height = size.getHeight();
        for (int i3 = 0; i3 < list.size(); i3++) {
            Size size2 = list.get(i3);
            if (size2.getHeight() == (size2.getWidth() * height) / width && size2.getWidth() >= i && size2.getHeight() >= i2) {
                arrayList.add(size2);
            }
        }
        if (arrayList.size() > 0) {
            return (Size) Collections.min(arrayList, new CompareSizesByArea());
        }
        return (Size) Collections.max(list, new CompareSizesByArea());
    }

    static class CompareSizesByArea implements Comparator<Size>, j$.util.Comparator {
        public /* synthetic */ Comparator reversed() {
            return Comparator.CC.$default$reversed(this);
        }

        public /* synthetic */ java.util.Comparator thenComparing(Function function) {
            return Comparator.CC.$default$thenComparing((java.util.Comparator) this, function);
        }

        public /* synthetic */ java.util.Comparator thenComparing(Function function, java.util.Comparator comparator) {
            return Comparator.CC.$default$thenComparing(this, function, comparator);
        }

        public /* synthetic */ java.util.Comparator thenComparing(java.util.Comparator comparator) {
            return Comparator.CC.$default$thenComparing((java.util.Comparator) this, comparator);
        }

        public /* synthetic */ java.util.Comparator thenComparingDouble(ToDoubleFunction toDoubleFunction) {
            return Comparator.CC.$default$thenComparingDouble(this, toDoubleFunction);
        }

        public /* synthetic */ java.util.Comparator thenComparingInt(ToIntFunction toIntFunction) {
            return Comparator.CC.$default$thenComparingInt(this, toIntFunction);
        }

        public /* synthetic */ java.util.Comparator thenComparingLong(ToLongFunction toLongFunction) {
            return Comparator.CC.$default$thenComparingLong(this, toLongFunction);
        }

        CompareSizesByArea() {
        }

        public int compare(Size size, Size size2) {
            return Long.signum((((long) size.getWidth()) * ((long) size.getHeight())) - (((long) size2.getWidth()) * ((long) size2.getHeight())));
        }
    }
}
