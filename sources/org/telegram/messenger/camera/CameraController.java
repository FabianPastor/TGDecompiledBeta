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
        CameraController localInstance = Instance;
        if (localInstance == null) {
            synchronized (CameraController.class) {
                localInstance = Instance;
                if (localInstance == null) {
                    CameraController cameraController = new CameraController();
                    localInstance = cameraController;
                    Instance = cameraController;
                }
            }
        }
        return localInstance;
    }

    public void cancelOnInitRunnable(Runnable onInitRunnable) {
        this.onFinishCameraInitRunnables.remove(onInitRunnable);
    }

    public void initCamera(Runnable onInitRunnable) {
        initCamera(onInitRunnable, false);
    }

    private void initCamera(Runnable onInitRunnable, boolean withDelay) {
        if (!this.cameraInitied) {
            if (onInitRunnable != null && !this.onFinishCameraInitRunnables.contains(onInitRunnable)) {
                this.onFinishCameraInitRunnables.add(onInitRunnable);
            }
            if (!this.loadingCameras && !this.cameraInitied) {
                this.loadingCameras = true;
                this.threadPool.execute(new CameraController$$ExternalSyntheticLambda4(this, withDelay, onInitRunnable));
            }
        }
    }

    /* renamed from: lambda$initCamera$4$org-telegram-messenger-camera-CameraController  reason: not valid java name */
    public /* synthetic */ void m1173x6e3d3dee(boolean withDelay, Runnable onInitRunnable) {
        String cache;
        Camera.CameraInfo info;
        List<Camera.Size> list;
        String str;
        CameraController cameraController = this;
        String str2 = "cameraCache";
        try {
            if (cameraController.cameraInfos == null) {
                SharedPreferences preferences = MessagesController.getGlobalMainSettings();
                String cache2 = preferences.getString(str2, (String) null);
                Comparator<Size> comparator = CameraController$$ExternalSyntheticLambda8.INSTANCE;
                ArrayList<CameraInfo> result = new ArrayList<>();
                if (cache2 != null) {
                    SerializedData serializedData = new SerializedData(Base64.decode(cache2, 0));
                    int count = serializedData.readInt32(false);
                    for (int a = 0; a < count; a++) {
                        CameraInfo cameraInfo = new CameraInfo(serializedData.readInt32(false), serializedData.readInt32(false));
                        int pCount = serializedData.readInt32(false);
                        for (int b = 0; b < pCount; b++) {
                            cameraInfo.previewSizes.add(new Size(serializedData.readInt32(false), serializedData.readInt32(false)));
                        }
                        int pCount2 = serializedData.readInt32(false);
                        for (int b2 = 0; b2 < pCount2; b2++) {
                            cameraInfo.pictureSizes.add(new Size(serializedData.readInt32(false), serializedData.readInt32(false)));
                        }
                        result.add(cameraInfo);
                        Collections.sort(cameraInfo.previewSizes, comparator);
                        Collections.sort(cameraInfo.pictureSizes, comparator);
                    }
                    serializedData.cleanup();
                    String str3 = cache2;
                } else {
                    int count2 = Camera.getNumberOfCameras();
                    Camera.CameraInfo info2 = new Camera.CameraInfo();
                    int bufferSize = 4;
                    int cameraId = 0;
                    while (cameraId < count2) {
                        Camera.getCameraInfo(cameraId, info2);
                        CameraInfo cameraInfo2 = new CameraInfo(cameraId, info2.facing);
                        if (ApplicationLoader.mainInterfacePaused) {
                            if (ApplicationLoader.externalInterfacePaused) {
                                throw new RuntimeException("APP_PAUSED");
                            }
                        }
                        try {
                            Camera camera = Camera.open(cameraInfo2.getCameraId());
                            Camera.Parameters params = camera.getParameters();
                            List<Camera.Size> list2 = params.getSupportedPreviewSizes();
                            int a2 = 0;
                            while (true) {
                                cache = cache2;
                                info = info2;
                                if (a2 >= list2.size()) {
                                    break;
                                }
                                Camera.Size size = list2.get(a2);
                                List<Camera.Size> list3 = list2;
                                if (size.width == 1280) {
                                    if (size.height != 720) {
                                        str = str2;
                                        a2++;
                                        cameraController = this;
                                        cache2 = cache;
                                        info2 = info;
                                        list2 = list3;
                                        str2 = str;
                                    }
                                }
                                if (size.height >= 2160 || size.width >= 2160) {
                                    str = str2;
                                    a2++;
                                    cameraController = this;
                                    cache2 = cache;
                                    info2 = info;
                                    list2 = list3;
                                    str2 = str;
                                } else {
                                    str = str2;
                                    cameraInfo2.previewSizes.add(new Size(size.width, size.height));
                                    if (BuildVars.LOGS_ENABLED) {
                                        FileLog.d("preview size = " + size.width + " " + size.height);
                                    }
                                    a2++;
                                    cameraController = this;
                                    cache2 = cache;
                                    info2 = info;
                                    list2 = list3;
                                    str2 = str;
                                }
                            }
                            String str4 = str2;
                            List<Camera.Size> list4 = list2;
                            List<Camera.Size> list5 = params.getSupportedPictureSizes();
                            int a3 = 0;
                            while (a3 < list5.size()) {
                                Camera.Size size2 = list5.get(a3);
                                if (size2.width == 1280) {
                                    if (size2.height != 720) {
                                        list = list5;
                                        a3++;
                                        list5 = list;
                                    }
                                }
                                if ("samsung".equals(Build.MANUFACTURER) && "jflteuc".equals(Build.PRODUCT)) {
                                    if (size2.width >= 2048) {
                                        list = list5;
                                        a3++;
                                        list5 = list;
                                    }
                                }
                                list = list5;
                                cameraInfo2.pictureSizes.add(new Size(size2.width, size2.height));
                                if (BuildVars.LOGS_ENABLED) {
                                    FileLog.d("picture size = " + size2.width + " " + size2.height);
                                }
                                a3++;
                                list5 = list;
                            }
                            camera.release();
                            result.add(cameraInfo2);
                            Collections.sort(cameraInfo2.previewSizes, comparator);
                            Collections.sort(cameraInfo2.pictureSizes, comparator);
                            bufferSize += ((cameraInfo2.previewSizes.size() + cameraInfo2.pictureSizes.size()) * 8) + 8;
                            cameraId++;
                            cameraController = this;
                            cache2 = cache;
                            info2 = info;
                            str2 = str4;
                        } catch (Exception e) {
                            e = e;
                            cameraController = this;
                            FileLog.e((Throwable) e);
                            AndroidUtilities.runOnUIThread(new CameraController$$ExternalSyntheticLambda3(cameraController, withDelay, e, onInitRunnable));
                        }
                    }
                    String str5 = str2;
                    String str6 = cache2;
                    Camera.CameraInfo cameraInfo3 = info2;
                    SerializedData serializedData2 = new SerializedData(bufferSize);
                    serializedData2.writeInt32(result.size());
                    for (int a4 = 0; a4 < count2; a4++) {
                        CameraInfo cameraInfo4 = result.get(a4);
                        serializedData2.writeInt32(cameraInfo4.cameraId);
                        serializedData2.writeInt32(cameraInfo4.frontCamera);
                        int pCount3 = cameraInfo4.previewSizes.size();
                        serializedData2.writeInt32(pCount3);
                        for (int b3 = 0; b3 < pCount3; b3++) {
                            Size size3 = cameraInfo4.previewSizes.get(b3);
                            serializedData2.writeInt32(size3.mWidth);
                            serializedData2.writeInt32(size3.mHeight);
                        }
                        int pCount4 = cameraInfo4.pictureSizes.size();
                        serializedData2.writeInt32(pCount4);
                        for (int b4 = 0; b4 < pCount4; b4++) {
                            Size size4 = cameraInfo4.pictureSizes.get(b4);
                            serializedData2.writeInt32(size4.mWidth);
                            serializedData2.writeInt32(size4.mHeight);
                        }
                    }
                    preferences.edit().putString(str5, Base64.encodeToString(serializedData2.toByteArray(), 0)).commit();
                    serializedData2.cleanup();
                }
                cameraController = this;
                cameraController.cameraInfos = result;
            }
            AndroidUtilities.runOnUIThread(new CameraController$$ExternalSyntheticLambda11(cameraController));
            boolean z = withDelay;
            Runnable runnable = onInitRunnable;
        } catch (Exception e2) {
            e = e2;
            FileLog.e((Throwable) e);
            AndroidUtilities.runOnUIThread(new CameraController$$ExternalSyntheticLambda3(cameraController, withDelay, e, onInitRunnable));
        }
    }

    static /* synthetic */ int lambda$initCamera$0(Size o1, Size o2) {
        if (o1.mWidth < o2.mWidth) {
            return 1;
        }
        if (o1.mWidth > o2.mWidth) {
            return -1;
        }
        if (o1.mHeight < o2.mHeight) {
            return 1;
        }
        if (o1.mHeight > o2.mHeight) {
            return -1;
        }
        return 0;
    }

    /* renamed from: lambda$initCamera$1$org-telegram-messenger-camera-CameraController  reason: not valid java name */
    public /* synthetic */ void m1170xc0dd5851() {
        this.loadingCameras = false;
        this.cameraInitied = true;
        if (!this.onFinishCameraInitRunnables.isEmpty()) {
            for (int a = 0; a < this.onFinishCameraInitRunnables.size(); a++) {
                this.onFinishCameraInitRunnables.get(a).run();
            }
            this.onFinishCameraInitRunnables.clear();
        }
        NotificationCenter.getGlobalInstance().postNotificationName(NotificationCenter.cameraInitied, new Object[0]);
    }

    /* renamed from: lambda$initCamera$3$org-telegram-messenger-camera-CameraController  reason: not valid java name */
    public /* synthetic */ void m1172x34729c0f(boolean withDelay, Exception e, Runnable onInitRunnable) {
        this.onFinishCameraInitRunnables.clear();
        this.loadingCameras = false;
        this.cameraInitied = false;
        if (!withDelay && "APP_PAUSED".equals(e.getMessage())) {
            AndroidUtilities.runOnUIThread(new CameraController$$ExternalSyntheticLambda16(this, onInitRunnable), 1000);
        }
    }

    /* renamed from: lambda$initCamera$2$org-telegram-messenger-camera-CameraController  reason: not valid java name */
    public /* synthetic */ void m1171xfaa7fa30(Runnable onInitRunnable) {
        initCamera(onInitRunnable, true);
    }

    public boolean isCameraInitied() {
        return this.cameraInitied && this.cameraInfos != null && !this.cameraInfos.isEmpty();
    }

    public void close(CameraSession session, CountDownLatch countDownLatch, Runnable beforeDestroyRunnable) {
        session.destroy();
        this.threadPool.execute(new CameraController$$ExternalSyntheticLambda10(beforeDestroyRunnable, session, countDownLatch));
        if (countDownLatch != null) {
            try {
                countDownLatch.await();
            } catch (Exception e) {
                FileLog.e((Throwable) e);
            }
        }
    }

    static /* synthetic */ void lambda$close$5(Runnable beforeDestroyRunnable, CameraSession session, CountDownLatch countDownLatch) {
        if (beforeDestroyRunnable != null) {
            beforeDestroyRunnable.run();
        }
        if (session.cameraInfo.camera != null) {
            try {
                session.cameraInfo.camera.stopPreview();
                session.cameraInfo.camera.setPreviewCallbackWithBuffer((Camera.PreviewCallback) null);
            } catch (Exception e) {
                FileLog.e((Throwable) e);
            }
            try {
                session.cameraInfo.camera.release();
            } catch (Exception e2) {
                FileLog.e((Throwable) e2);
            }
            session.cameraInfo.camera = null;
        }
        if (countDownLatch != null) {
            countDownLatch.countDown();
        }
    }

    public ArrayList<CameraInfo> getCameras() {
        return this.cameraInfos;
    }

    /* JADX WARNING: Code restructure failed: missing block: B:35:0x0067, code lost:
        if (r2 <= 8) goto L_0x00be;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:36:0x0069, code lost:
        r3 = pack(r10, r1, 4, false);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:37:0x0070, code lost:
        if (r3 == NUM) goto L_0x0078;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:39:0x0075, code lost:
        if (r3 == NUM) goto L_0x0078;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:40:0x0077, code lost:
        return 0;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:41:0x0078, code lost:
        if (r3 != NUM) goto L_0x007b;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:42:0x007b, code lost:
        r6 = false;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:43:0x007c, code lost:
        r4 = r6;
        r5 = pack(r10, r1 + 4, 4, r4) + 2;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:44:0x0086, code lost:
        if (r5 < 10) goto L_0x00bd;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:45:0x0088, code lost:
        if (r5 <= r2) goto L_0x008b;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:46:0x008b, code lost:
        r1 = r1 + r5;
        r2 = r2 - r5;
        r5 = pack(r10, r1 - 2, 2, r4);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:47:0x0093, code lost:
        r6 = r5 - 1;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:48:0x0095, code lost:
        if (r5 <= 0) goto L_0x00be;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:50:0x0099, code lost:
        if (r2 < 12) goto L_0x00be;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:52:0x00a1, code lost:
        if (pack(r10, r1, 2, r4) != 274) goto L_0x00b7;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:54:0x00a9, code lost:
        switch(pack(r10, r1 + 8, 2, r4)) {
            case 1: goto L_0x00b6;
            case 3: goto L_0x00b3;
            case 6: goto L_0x00b0;
            case 8: goto L_0x00ad;
            default: goto L_0x00ac;
        };
     */
    /* JADX WARNING: Code restructure failed: missing block: B:55:0x00ac, code lost:
        return 0;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:56:0x00ad, code lost:
        return 270;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:57:0x00b0, code lost:
        return 90;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:58:0x00b3, code lost:
        return 180;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:59:0x00b6, code lost:
        return 0;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:60:0x00b7, code lost:
        r1 = r1 + 12;
        r2 = r2 - 12;
        r5 = r6;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:61:0x00bd, code lost:
        return 0;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:62:0x00be, code lost:
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
            r2 = 0
        L_0x0006:
            int r3 = r1 + 3
            int r4 = r10.length
            r5 = 4
            r6 = 1
            r7 = 8
            r8 = 2
            if (r3 >= r4) goto L_0x0067
            int r3 = r1 + 1
            byte r1 = r10[r1]
            r4 = 255(0xff, float:3.57E-43)
            r1 = r1 & r4
            if (r1 != r4) goto L_0x0066
            byte r1 = r10[r3]
            r1 = r1 & r4
            if (r1 != r4) goto L_0x0020
            r1 = r3
            goto L_0x0006
        L_0x0020:
            int r3 = r3 + 1
            r4 = 216(0xd8, float:3.03E-43)
            if (r1 == r4) goto L_0x0064
            if (r1 != r6) goto L_0x0029
            goto L_0x0064
        L_0x0029:
            r4 = 217(0xd9, float:3.04E-43)
            if (r1 == r4) goto L_0x0062
            r4 = 218(0xda, float:3.05E-43)
            if (r1 != r4) goto L_0x0032
            goto L_0x0062
        L_0x0032:
            int r2 = pack(r10, r3, r8, r0)
            if (r2 < r8) goto L_0x0061
            int r4 = r3 + r2
            int r9 = r10.length
            if (r4 <= r9) goto L_0x003e
            goto L_0x0061
        L_0x003e:
            r4 = 225(0xe1, float:3.15E-43)
            if (r1 != r4) goto L_0x005d
            if (r2 < r7) goto L_0x005d
            int r4 = r3 + 2
            int r4 = pack(r10, r4, r5, r0)
            r9 = 1165519206(0x45786966, float:3974.5874)
            if (r4 != r9) goto L_0x005d
            int r4 = r3 + 6
            int r4 = pack(r10, r4, r8, r0)
            if (r4 != 0) goto L_0x005d
            int r3 = r3 + 8
            int r2 = r2 + -8
            r1 = r3
            goto L_0x0067
        L_0x005d:
            int r3 = r3 + r2
            r2 = 0
            r1 = r3
            goto L_0x0006
        L_0x0061:
            return r0
        L_0x0062:
            r1 = r3
            goto L_0x0067
        L_0x0064:
            r1 = r3
            goto L_0x0006
        L_0x0066:
            r1 = r3
        L_0x0067:
            if (r2 <= r7) goto L_0x00be
            int r3 = pack(r10, r1, r5, r0)
            r4 = 1229531648(0x49492a00, float:823968.0)
            if (r3 == r4) goto L_0x0078
            r7 = 1296891946(0x4d4d002a, float:2.14958752E8)
            if (r3 == r7) goto L_0x0078
            return r0
        L_0x0078:
            if (r3 != r4) goto L_0x007b
            goto L_0x007c
        L_0x007b:
            r6 = 0
        L_0x007c:
            r4 = r6
            int r6 = r1 + 4
            int r5 = pack(r10, r6, r5, r4)
            int r5 = r5 + r8
            r6 = 10
            if (r5 < r6) goto L_0x00bd
            if (r5 <= r2) goto L_0x008b
            goto L_0x00bd
        L_0x008b:
            int r1 = r1 + r5
            int r2 = r2 - r5
            int r6 = r1 + -2
            int r5 = pack(r10, r6, r8, r4)
        L_0x0093:
            int r6 = r5 + -1
            if (r5 <= 0) goto L_0x00be
            r5 = 12
            if (r2 < r5) goto L_0x00be
            int r3 = pack(r10, r1, r8, r4)
            r5 = 274(0x112, float:3.84E-43)
            if (r3 != r5) goto L_0x00b7
            int r5 = r1 + 8
            int r5 = pack(r10, r5, r8, r4)
            switch(r5) {
                case 1: goto L_0x00b6;
                case 3: goto L_0x00b3;
                case 6: goto L_0x00b0;
                case 8: goto L_0x00ad;
                default: goto L_0x00ac;
            }
        L_0x00ac:
            return r0
        L_0x00ad:
            r0 = 270(0x10e, float:3.78E-43)
            return r0
        L_0x00b0:
            r0 = 90
            return r0
        L_0x00b3:
            r0 = 180(0xb4, float:2.52E-43)
            return r0
        L_0x00b6:
            return r0
        L_0x00b7:
            int r1 = r1 + 12
            int r2 = r2 + -12
            r5 = r6
            goto L_0x0093
        L_0x00bd:
            return r0
        L_0x00be:
            return r0
        */
        throw new UnsupportedOperationException("Method not decompiled: org.telegram.messenger.camera.CameraController.getOrientation(byte[]):int");
    }

    private static int pack(byte[] bytes, int offset, int length, boolean littleEndian) {
        int step = 1;
        if (littleEndian) {
            offset += length - 1;
            step = -1;
        }
        int value = 0;
        while (true) {
            int length2 = length - 1;
            if (length <= 0) {
                return value;
            }
            value = (value << 8) | (bytes[offset] & 255);
            offset += step;
            length = length2;
        }
    }

    public boolean takePicture(File path, CameraSession session, Runnable callback) {
        if (session == null) {
            return false;
        }
        CameraInfo info = session.cameraInfo;
        boolean flipFront = session.isFlipFront();
        try {
            info.camera.takePicture((Camera.ShutterCallback) null, (Camera.PictureCallback) null, new CameraController$$ExternalSyntheticLambda0(path, info, flipFront, callback));
            return true;
        } catch (Exception e) {
            FileLog.e((Throwable) e);
            return false;
        }
    }

    static /* synthetic */ void lambda$takePicture$6(File path, CameraInfo info, boolean flipFront, Runnable callback, byte[] data, Camera camera1) {
        File file = path;
        byte[] bArr = data;
        Bitmap bitmap = null;
        int size = (int) (((float) AndroidUtilities.getPhotoSize()) / AndroidUtilities.density);
        String key = String.format(Locale.US, "%s@%d_%d", new Object[]{Utilities.MD5(path.getAbsolutePath()), Integer.valueOf(size), Integer.valueOf(size)});
        try {
            BitmapFactory.Options options = new BitmapFactory.Options();
            options.inJustDecodeBounds = true;
            BitmapFactory.decodeByteArray(bArr, 0, bArr.length, options);
            options.inJustDecodeBounds = false;
            options.inPurgeable = true;
            bitmap = BitmapFactory.decodeByteArray(bArr, 0, bArr.length, options);
        } catch (Throwable e) {
            FileLog.e(e);
        }
        try {
            if (info.frontCamera != 0 && flipFront) {
                try {
                    Matrix matrix = new Matrix();
                    matrix.setRotate((float) getOrientation(data));
                    matrix.postScale(-1.0f, 1.0f);
                    Bitmap scaled = Bitmaps.createBitmap(bitmap, 0, 0, bitmap.getWidth(), bitmap.getHeight(), matrix, true);
                    if (scaled != bitmap) {
                        bitmap.recycle();
                    }
                    FileOutputStream outputStream = new FileOutputStream(path);
                    scaled.compress(Bitmap.CompressFormat.JPEG, 80, outputStream);
                    outputStream.flush();
                    outputStream.getFD().sync();
                    outputStream.close();
                    if (scaled != null) {
                        ImageLoader.getInstance().putImageToCache(new BitmapDrawable(scaled), key, false);
                    }
                    if (callback != null) {
                        callback.run();
                        return;
                    }
                    return;
                } catch (Throwable e2) {
                    FileLog.e(e2);
                }
            }
            FileOutputStream outputStream2 = new FileOutputStream(path);
            outputStream2.write(bArr);
            outputStream2.flush();
            outputStream2.getFD().sync();
            outputStream2.close();
            if (bitmap != null) {
                ImageLoader.getInstance().putImageToCache(new BitmapDrawable(bitmap), key, false);
            }
        } catch (Exception e3) {
            FileLog.e((Throwable) e3);
        }
        if (callback != null) {
            callback.run();
        }
    }

    public void startPreview(CameraSession session) {
        if (session != null) {
            this.threadPool.execute(new CameraController$$ExternalSyntheticLambda5(session));
        }
    }

    static /* synthetic */ void lambda$startPreview$7(CameraSession session) {
        Camera camera = session.cameraInfo.camera;
        if (camera == null) {
            try {
                CameraInfo cameraInfo = session.cameraInfo;
                Camera open = Camera.open(session.cameraInfo.cameraId);
                cameraInfo.camera = open;
                camera = open;
            } catch (Exception e) {
                session.cameraInfo.camera = null;
                if (camera != null) {
                    camera.release();
                }
                FileLog.e((Throwable) e);
                return;
            }
        }
        camera.startPreview();
    }

    public void stopPreview(CameraSession session) {
        if (session != null) {
            this.threadPool.execute(new CameraController$$ExternalSyntheticLambda6(session));
        }
    }

    static /* synthetic */ void lambda$stopPreview$8(CameraSession session) {
        Camera camera = session.cameraInfo.camera;
        if (camera == null) {
            try {
                CameraInfo cameraInfo = session.cameraInfo;
                Camera open = Camera.open(session.cameraInfo.cameraId);
                cameraInfo.camera = open;
                camera = open;
            } catch (Exception e) {
                session.cameraInfo.camera = null;
                if (camera != null) {
                    camera.release();
                }
                FileLog.e((Throwable) e);
                return;
            }
        }
        camera.stopPreview();
    }

    public void openRound(CameraSession session, SurfaceTexture texture, Runnable callback, Runnable configureCallback) {
        if (session != null && texture != null) {
            this.threadPool.execute(new CameraController$$ExternalSyntheticLambda7(session, configureCallback, texture, callback));
        } else if (BuildVars.LOGS_ENABLED) {
            FileLog.d("failed to open round " + session + " tex = " + texture);
        }
    }

    static /* synthetic */ void lambda$openRound$9(CameraSession session, Runnable configureCallback, SurfaceTexture texture, Runnable callback) {
        Camera camera = session.cameraInfo.camera;
        try {
            if (BuildVars.LOGS_ENABLED) {
                FileLog.d("start creating round camera session");
            }
            if (camera == null) {
                CameraInfo cameraInfo = session.cameraInfo;
                Camera open = Camera.open(session.cameraInfo.cameraId);
                cameraInfo.camera = open;
                camera = open;
            }
            Camera.Parameters parameters = camera.getParameters();
            session.configureRoundCamera(true);
            if (configureCallback != null) {
                configureCallback.run();
            }
            camera.setPreviewTexture(texture);
            camera.startPreview();
            if (callback != null) {
                AndroidUtilities.runOnUIThread(callback);
            }
            if (BuildVars.LOGS_ENABLED) {
                FileLog.d("round camera session created");
            }
        } catch (Exception e) {
            session.cameraInfo.camera = null;
            if (camera != null) {
                camera.release();
            }
            FileLog.e((Throwable) e);
        }
    }

    public void open(CameraSession session, SurfaceTexture texture, Runnable callback, Runnable prestartCallback) {
        if (session != null && texture != null) {
            this.threadPool.execute(new CameraController$$ExternalSyntheticLambda17(this, session, prestartCallback, texture, callback));
        }
    }

    /* renamed from: lambda$open$10$org-telegram-messenger-camera-CameraController  reason: not valid java name */
    public /* synthetic */ void m1174lambda$open$10$orgtelegrammessengercameraCameraController(CameraSession session, Runnable prestartCallback, SurfaceTexture texture, Runnable callback) {
        Camera camera = session.cameraInfo.camera;
        if (camera == null) {
            try {
                CameraInfo cameraInfo = session.cameraInfo;
                Camera open = Camera.open(session.cameraInfo.cameraId);
                cameraInfo.camera = open;
                camera = open;
            } catch (Exception e) {
                session.cameraInfo.camera = null;
                if (camera != null) {
                    camera.release();
                }
                FileLog.e((Throwable) e);
                return;
            }
        }
        List<String> rawFlashModes = camera.getParameters().getSupportedFlashModes();
        this.availableFlashModes.clear();
        if (rawFlashModes != null) {
            for (int a = 0; a < rawFlashModes.size(); a++) {
                String rawFlashMode = rawFlashModes.get(a);
                if (rawFlashMode.equals("off") || rawFlashMode.equals("on") || rawFlashMode.equals("auto")) {
                    this.availableFlashModes.add(rawFlashMode);
                }
            }
            session.checkFlashMode(this.availableFlashModes.get(0));
        }
        if (prestartCallback != null) {
            prestartCallback.run();
        }
        session.configurePhotoCamera();
        camera.setPreviewTexture(texture);
        camera.startPreview();
        if (callback != null) {
            AndroidUtilities.runOnUIThread(callback);
        }
    }

    public void recordVideo(CameraSession session, File path, boolean mirror, VideoTakeCallback callback, Runnable onVideoStartRecord, CameraView cameraView) {
        CameraSession cameraSession = session;
        CameraView cameraView2 = cameraView;
        if (cameraSession != null) {
            CameraInfo info = cameraSession.cameraInfo;
            Camera camera = info.camera;
            if (cameraView2 != null) {
                this.recordingCurrentCameraView = cameraView2;
                this.onVideoTakeCallback = callback;
                this.recordedFile = path.getAbsolutePath();
                this.threadPool.execute(new CameraController$$ExternalSyntheticLambda13(this, camera, session, cameraView, path, onVideoStartRecord));
                return;
            }
            VideoTakeCallback videoTakeCallback = callback;
            ThreadPoolExecutor threadPoolExecutor = this.threadPool;
            CameraController$$ExternalSyntheticLambda14 cameraController$$ExternalSyntheticLambda14 = r0;
            CameraController$$ExternalSyntheticLambda14 cameraController$$ExternalSyntheticLambda142 = new CameraController$$ExternalSyntheticLambda14(this, camera, session, mirror, path, info, callback, onVideoStartRecord);
            threadPoolExecutor.execute(cameraController$$ExternalSyntheticLambda14);
        }
    }

    /* renamed from: lambda$recordVideo$12$org-telegram-messenger-camera-CameraController  reason: not valid java name */
    public /* synthetic */ void m1176x55var_fb8(Camera camera, CameraSession session, CameraView cameraView, File path, Runnable onVideoStartRecord) {
        if (camera != null) {
            try {
                Camera.Parameters params = camera.getParameters();
                params.setFlashMode(session.getCurrentFlashMode().equals("on") ? "torch" : "off");
                camera.setParameters(params);
                session.onStartRecord();
            } catch (Exception e) {
                try {
                    FileLog.e((Throwable) e);
                } catch (Exception e2) {
                    FileLog.e((Throwable) e2);
                    return;
                }
            }
            AndroidUtilities.runOnUIThread(new CameraController$$ExternalSyntheticLambda2(this, cameraView, path, onVideoStartRecord));
        }
    }

    /* renamed from: lambda$recordVideo$11$org-telegram-messenger-camera-CameraController  reason: not valid java name */
    public /* synthetic */ void m1175x1c2acdd9(CameraView cameraView, File path, Runnable onVideoStartRecord) {
        cameraView.startRecording(path, new CameraController$$ExternalSyntheticLambda12(this));
        if (onVideoStartRecord != null) {
            onVideoStartRecord.run();
        }
    }

    /* renamed from: lambda$recordVideo$13$org-telegram-messenger-camera-CameraController  reason: not valid java name */
    public /* synthetic */ void m1177x8fCLASSNAME(Camera camera, CameraSession session, boolean mirror, File path, CameraInfo info, VideoTakeCallback callback, Runnable onVideoStartRecord) {
        int bitrate;
        if (camera != null) {
            try {
                Camera.Parameters params = camera.getParameters();
                params.setFlashMode(session.getCurrentFlashMode().equals("on") ? "torch" : "off");
                camera.setParameters(params);
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
                this.mirrorRecorderVideo = mirror;
                MediaRecorder mediaRecorder = new MediaRecorder();
                this.recorder = mediaRecorder;
                mediaRecorder.setCamera(camera);
                this.recorder.setVideoSource(1);
                this.recorder.setAudioSource(5);
                session.configureRecorder(1, this.recorder);
                this.recorder.setOutputFile(path.getAbsolutePath());
                this.recorder.setMaxFileSize(NUM);
                this.recorder.setVideoFrameRate(30);
                this.recorder.setMaxDuration(0);
                Size pictureSize = chooseOptimalSize(info.getPictureSizes(), 720, 480, new Size(16, 9));
                if (Math.min(pictureSize.mHeight, pictureSize.mWidth) >= 720) {
                    bitrate = 3500000;
                } else {
                    bitrate = 1800000;
                }
                this.recorder.setVideoEncodingBitRate(bitrate);
                this.recorder.setVideoSize(pictureSize.getWidth(), pictureSize.getHeight());
                this.recorder.setOnInfoListener(this);
                this.recorder.prepare();
                this.recorder.start();
                this.onVideoTakeCallback = callback;
                this.recordedFile = path.getAbsolutePath();
                if (onVideoStartRecord != null) {
                    AndroidUtilities.runOnUIThread(onVideoStartRecord);
                }
            } catch (Exception e3) {
                this.recorder.release();
                this.recorder = null;
                FileLog.e((Throwable) e3);
            }
        }
    }

    /* access modifiers changed from: private */
    /* JADX WARNING: Removed duplicated region for block: B:20:0x004b  */
    /* JADX WARNING: Removed duplicated region for block: B:21:0x007e  */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public void finishRecordingVideo() {
        /*
            r13 = this;
            r0 = 0
            r1 = 0
            android.media.MediaMetadataRetriever r3 = new android.media.MediaMetadataRetriever     // Catch:{ Exception -> 0x002f }
            r3.<init>()     // Catch:{ Exception -> 0x002f }
            r0 = r3
            java.lang.String r3 = r13.recordedFile     // Catch:{ Exception -> 0x002f }
            r0.setDataSource(r3)     // Catch:{ Exception -> 0x002f }
            r3 = 9
            java.lang.String r3 = r0.extractMetadata(r3)     // Catch:{ Exception -> 0x002f }
            if (r3 == 0) goto L_0x0025
            long r4 = java.lang.Long.parseLong(r3)     // Catch:{ Exception -> 0x002f }
            float r4 = (float) r4     // Catch:{ Exception -> 0x002f }
            r5 = 1148846080(0x447a0000, float:1000.0)
            float r4 = r4 / r5
            double r4 = (double) r4     // Catch:{ Exception -> 0x002f }
            double r4 = java.lang.Math.ceil(r4)     // Catch:{ Exception -> 0x002f }
            int r4 = (int) r4
            long r1 = (long) r4
        L_0x0025:
            r0.release()     // Catch:{ Exception -> 0x002a }
            goto L_0x003e
        L_0x002a:
            r3 = move-exception
            goto L_0x003a
        L_0x002c:
            r3 = move-exception
            goto L_0x00c7
        L_0x002f:
            r3 = move-exception
            org.telegram.messenger.FileLog.e((java.lang.Throwable) r3)     // Catch:{ all -> 0x002c }
            if (r0 == 0) goto L_0x003e
            r0.release()     // Catch:{ Exception -> 0x0039 }
            goto L_0x003e
        L_0x0039:
            r3 = move-exception
        L_0x003a:
            org.telegram.messenger.FileLog.e((java.lang.Throwable) r3)
            goto L_0x003f
        L_0x003e:
        L_0x003f:
            r7 = r1
            java.lang.String r1 = r13.recordedFile
            r2 = 1
            android.graphics.Bitmap r1 = org.telegram.messenger.SendMessagesHelper.createVideoThumbnail(r1, r2)
            boolean r2 = r13.mirrorRecorderVideo
            if (r2 == 0) goto L_0x007e
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
            int r9 = r2.getHeight()
            int r9 = r9 / 2
            float r9 = (float) r9
            r3.scale(r4, r5, r6, r9)
            r4 = 0
            r5 = 0
            r3.drawBitmap(r1, r5, r5, r4)
            r1.recycle()
            r1 = r2
            r9 = r1
            goto L_0x007f
        L_0x007e:
            r9 = r1
        L_0x007f:
            java.lang.StringBuilder r1 = new java.lang.StringBuilder
            r1.<init>()
            java.lang.String r2 = "-2147483648_"
            r1.append(r2)
            int r2 = org.telegram.messenger.SharedConfig.getLastLocalId()
            r1.append(r2)
            java.lang.String r2 = ".jpg"
            r1.append(r2)
            java.lang.String r10 = r1.toString()
            java.io.File r1 = new java.io.File
            r2 = 4
            java.io.File r2 = org.telegram.messenger.FileLoader.getDirectory(r2)
            r1.<init>(r2, r10)
            r11 = r1
            java.io.FileOutputStream r1 = new java.io.FileOutputStream     // Catch:{ all -> 0x00b2 }
            r1.<init>(r11)     // Catch:{ all -> 0x00b2 }
            android.graphics.Bitmap$CompressFormat r2 = android.graphics.Bitmap.CompressFormat.JPEG     // Catch:{ all -> 0x00b2 }
            r3 = 87
            r9.compress(r2, r3, r1)     // Catch:{ all -> 0x00b2 }
            goto L_0x00b6
        L_0x00b2:
            r1 = move-exception
            org.telegram.messenger.FileLog.e((java.lang.Throwable) r1)
        L_0x00b6:
            org.telegram.messenger.SharedConfig.saveConfig()
            r5 = r7
            r4 = r9
            org.telegram.messenger.camera.CameraController$$ExternalSyntheticLambda15 r12 = new org.telegram.messenger.camera.CameraController$$ExternalSyntheticLambda15
            r1 = r12
            r2 = r13
            r3 = r11
            r1.<init>(r2, r3, r4, r5)
            org.telegram.messenger.AndroidUtilities.runOnUIThread(r12)
            return
        L_0x00c7:
            if (r0 == 0) goto L_0x00d2
            r0.release()     // Catch:{ Exception -> 0x00cd }
            goto L_0x00d2
        L_0x00cd:
            r4 = move-exception
            org.telegram.messenger.FileLog.e((java.lang.Throwable) r4)
            goto L_0x00d3
        L_0x00d2:
        L_0x00d3:
            throw r3
        */
        throw new UnsupportedOperationException("Method not decompiled: org.telegram.messenger.camera.CameraController.finishRecordingVideo():void");
    }

    /* renamed from: lambda$finishRecordingVideo$14$org-telegram-messenger-camera-CameraController  reason: not valid java name */
    public /* synthetic */ void m1169x1d61ce23(File cacheFile, Bitmap bitmapFinal, long durationFinal) {
        if (this.onVideoTakeCallback != null) {
            String path = cacheFile.getAbsolutePath();
            if (bitmapFinal != null) {
                ImageLoader.getInstance().putImageToCache(new BitmapDrawable(bitmapFinal), Utilities.MD5(path), false);
            }
            this.onVideoTakeCallback.onFinishVideoRecording(path, durationFinal);
            this.onVideoTakeCallback = null;
        }
    }

    public void onInfo(MediaRecorder mediaRecorder, int what, int extra) {
        if (what == 800 || what == 801 || what == 1) {
            MediaRecorder tempRecorder = this.recorder;
            this.recorder = null;
            if (tempRecorder != null) {
                tempRecorder.stop();
                tempRecorder.release();
            }
            if (this.onVideoTakeCallback != null) {
                finishRecordingVideo();
            }
        }
    }

    public void stopVideoRecording(CameraSession session, boolean abandon) {
        CameraView cameraView = this.recordingCurrentCameraView;
        if (cameraView != null) {
            cameraView.stopRecording();
            this.recordingCurrentCameraView = null;
            return;
        }
        this.threadPool.execute(new CameraController$$ExternalSyntheticLambda1(this, session, abandon));
    }

    /* renamed from: lambda$stopVideoRecording$16$org-telegram-messenger-camera-CameraController  reason: not valid java name */
    public /* synthetic */ void m1178x9d3b7c3c(CameraSession session, boolean abandon) {
        MediaRecorder tempRecorder;
        try {
            Camera camera = session.cameraInfo.camera;
            if (!(camera == null || (tempRecorder = this.recorder) == null)) {
                this.recorder = null;
                try {
                    tempRecorder.stop();
                } catch (Exception e) {
                    FileLog.e((Throwable) e);
                }
                try {
                    tempRecorder.release();
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
                    session.stopVideoRecording();
                } catch (Exception e4) {
                    FileLog.e((Throwable) e4);
                }
            }
            try {
                Camera.Parameters params = camera.getParameters();
                params.setFlashMode("off");
                camera.setParameters(params);
            } catch (Exception e5) {
                FileLog.e((Throwable) e5);
            }
            this.threadPool.execute(new CameraController$$ExternalSyntheticLambda9(camera, session));
            if (abandon || this.onVideoTakeCallback == null) {
                this.onVideoTakeCallback = null;
            } else {
                finishRecordingVideo();
            }
        } catch (Exception e6) {
        }
    }

    static /* synthetic */ void lambda$stopVideoRecording$15(Camera camera, CameraSession session) {
        try {
            Camera.Parameters params = camera.getParameters();
            params.setFlashMode(session.getCurrentFlashMode());
            camera.setParameters(params);
        } catch (Exception e) {
            FileLog.e((Throwable) e);
        }
    }

    public static Size chooseOptimalSize(List<Size> choices, int width, int height, Size aspectRatio) {
        List<Size> bigEnoughWithAspectRatio = new ArrayList<>();
        List<Size> bigEnough = new ArrayList<>();
        int w = aspectRatio.getWidth();
        int h = aspectRatio.getHeight();
        for (int a = 0; a < choices.size(); a++) {
            Size option = choices.get(a);
            if (option.getHeight() == (option.getWidth() * h) / w && option.getWidth() >= width && option.getHeight() >= height) {
                bigEnoughWithAspectRatio.add(option);
            } else if (option.getHeight() * option.getWidth() <= width * height * 4) {
                bigEnough.add(option);
            }
        }
        if (bigEnoughWithAspectRatio.size() > 0) {
            return (Size) Collections.min(bigEnoughWithAspectRatio, new CompareSizesByArea());
        }
        if (bigEnough.size() > 0) {
            return (Size) Collections.min(bigEnough, new CompareSizesByArea());
        }
        return (Size) Collections.max(choices, new CompareSizesByArea());
    }

    static class CompareSizesByArea implements Comparator<Size> {
        CompareSizesByArea() {
        }

        public int compare(Size lhs, Size rhs) {
            return Long.signum((((long) lhs.getWidth()) * ((long) lhs.getHeight())) - (((long) rhs.getWidth()) * ((long) rhs.getHeight())));
        }
    }
}
