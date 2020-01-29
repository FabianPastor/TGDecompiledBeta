package org.telegram.messenger.camera;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.graphics.SurfaceTexture;
import android.graphics.drawable.BitmapDrawable;
import android.hardware.Camera;
import android.media.MediaRecorder;
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
import org.telegram.messenger.Bitmaps;
import org.telegram.messenger.BuildVars;
import org.telegram.messenger.FileLog;
import org.telegram.messenger.ImageLoader;
import org.telegram.messenger.NotificationCenter;
import org.telegram.messenger.Utilities;
import org.telegram.messenger.camera.CameraController;

public class CameraController implements MediaRecorder.OnInfoListener {
    private static final int CORE_POOL_SIZE = 1;
    private static volatile CameraController Instance = null;
    private static final int KEEP_ALIVE_SECONDS = 60;
    private static final int MAX_POOL_SIZE = 1;
    protected ArrayList<String> availableFlashModes = new ArrayList<>();
    protected volatile ArrayList<CameraInfo> cameraInfos;
    private boolean cameraInitied;
    private boolean loadingCameras;
    private ArrayList<Runnable> onFinishCameraInitRunnables = new ArrayList<>();
    private VideoTakeCallback onVideoTakeCallback;
    private String recordedFile;
    private MediaRecorder recorder;
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
                    private final /* synthetic */ boolean f$1;
                    private final /* synthetic */ Runnable f$2;

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

    /* JADX WARNING: Code restructure failed: missing block: B:39:0x00db, code lost:
        if (r15.height != 720) goto L_0x011b;
     */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public /* synthetic */ void lambda$initCamera$4$CameraController(boolean r20, java.lang.Runnable r21) {
        /*
            r19 = this;
            r1 = r19
            java.lang.String r0 = "cameraCache"
            java.util.ArrayList<org.telegram.messenger.camera.CameraInfo> r2 = r1.cameraInfos     // Catch:{ Exception -> 0x0256 }
            if (r2 != 0) goto L_0x024d
            android.content.SharedPreferences r2 = org.telegram.messenger.MessagesController.getGlobalMainSettings()     // Catch:{ Exception -> 0x0256 }
            r3 = 0
            java.lang.String r3 = r2.getString(r0, r3)     // Catch:{ Exception -> 0x0256 }
            org.telegram.messenger.camera.-$$Lambda$CameraController$dwxNXb3SuNA_od-SPUyypaONvgM r4 = org.telegram.messenger.camera.$$Lambda$CameraController$dwxNXb3SuNA_odSPUyypaONvgM.INSTANCE     // Catch:{ Exception -> 0x0256 }
            java.util.ArrayList r5 = new java.util.ArrayList     // Catch:{ Exception -> 0x0256 }
            r5.<init>()     // Catch:{ Exception -> 0x0256 }
            r6 = 0
            if (r3 == 0) goto L_0x0085
            org.telegram.tgnet.SerializedData r0 = new org.telegram.tgnet.SerializedData     // Catch:{ Exception -> 0x0256 }
            byte[] r2 = android.util.Base64.decode(r3, r6)     // Catch:{ Exception -> 0x0256 }
            r0.<init>((byte[]) r2)     // Catch:{ Exception -> 0x0256 }
            int r2 = r0.readInt32(r6)     // Catch:{ Exception -> 0x0256 }
            r3 = 0
        L_0x0029:
            if (r3 >= r2) goto L_0x0080
            org.telegram.messenger.camera.CameraInfo r7 = new org.telegram.messenger.camera.CameraInfo     // Catch:{ Exception -> 0x0256 }
            int r8 = r0.readInt32(r6)     // Catch:{ Exception -> 0x0256 }
            int r9 = r0.readInt32(r6)     // Catch:{ Exception -> 0x0256 }
            r7.<init>(r8, r9)     // Catch:{ Exception -> 0x0256 }
            int r8 = r0.readInt32(r6)     // Catch:{ Exception -> 0x0256 }
            r9 = 0
        L_0x003d:
            if (r9 >= r8) goto L_0x0054
            java.util.ArrayList<org.telegram.messenger.camera.Size> r10 = r7.previewSizes     // Catch:{ Exception -> 0x0256 }
            org.telegram.messenger.camera.Size r11 = new org.telegram.messenger.camera.Size     // Catch:{ Exception -> 0x0256 }
            int r12 = r0.readInt32(r6)     // Catch:{ Exception -> 0x0256 }
            int r13 = r0.readInt32(r6)     // Catch:{ Exception -> 0x0256 }
            r11.<init>(r12, r13)     // Catch:{ Exception -> 0x0256 }
            r10.add(r11)     // Catch:{ Exception -> 0x0256 }
            int r9 = r9 + 1
            goto L_0x003d
        L_0x0054:
            int r8 = r0.readInt32(r6)     // Catch:{ Exception -> 0x0256 }
            r9 = 0
        L_0x0059:
            if (r9 >= r8) goto L_0x0070
            java.util.ArrayList<org.telegram.messenger.camera.Size> r10 = r7.pictureSizes     // Catch:{ Exception -> 0x0256 }
            org.telegram.messenger.camera.Size r11 = new org.telegram.messenger.camera.Size     // Catch:{ Exception -> 0x0256 }
            int r12 = r0.readInt32(r6)     // Catch:{ Exception -> 0x0256 }
            int r13 = r0.readInt32(r6)     // Catch:{ Exception -> 0x0256 }
            r11.<init>(r12, r13)     // Catch:{ Exception -> 0x0256 }
            r10.add(r11)     // Catch:{ Exception -> 0x0256 }
            int r9 = r9 + 1
            goto L_0x0059
        L_0x0070:
            r5.add(r7)     // Catch:{ Exception -> 0x0256 }
            java.util.ArrayList<org.telegram.messenger.camera.Size> r8 = r7.previewSizes     // Catch:{ Exception -> 0x0256 }
            java.util.Collections.sort(r8, r4)     // Catch:{ Exception -> 0x0256 }
            java.util.ArrayList<org.telegram.messenger.camera.Size> r7 = r7.pictureSizes     // Catch:{ Exception -> 0x0256 }
            java.util.Collections.sort(r7, r4)     // Catch:{ Exception -> 0x0256 }
            int r3 = r3 + 1
            goto L_0x0029
        L_0x0080:
            r0.cleanup()     // Catch:{ Exception -> 0x0256 }
            goto L_0x0246
        L_0x0085:
            int r3 = android.hardware.Camera.getNumberOfCameras()     // Catch:{ Exception -> 0x0256 }
            android.hardware.Camera$CameraInfo r7 = new android.hardware.Camera$CameraInfo     // Catch:{ Exception -> 0x0256 }
            r7.<init>()     // Catch:{ Exception -> 0x0256 }
            r8 = 4
            r8 = 0
            r9 = 4
        L_0x0091:
            if (r8 >= r3) goto L_0x01c5
            android.hardware.Camera.getCameraInfo(r8, r7)     // Catch:{ Exception -> 0x0249 }
            org.telegram.messenger.camera.CameraInfo r10 = new org.telegram.messenger.camera.CameraInfo     // Catch:{ Exception -> 0x0249 }
            int r11 = r7.facing     // Catch:{ Exception -> 0x0249 }
            r10.<init>(r8, r11)     // Catch:{ Exception -> 0x0249 }
            boolean r11 = org.telegram.messenger.ApplicationLoader.mainInterfacePaused     // Catch:{ Exception -> 0x0249 }
            if (r11 == 0) goto L_0x00ae
            boolean r11 = org.telegram.messenger.ApplicationLoader.externalInterfacePaused     // Catch:{ Exception -> 0x0256 }
            if (r11 != 0) goto L_0x00a6
            goto L_0x00ae
        L_0x00a6:
            java.lang.RuntimeException r0 = new java.lang.RuntimeException     // Catch:{ Exception -> 0x0256 }
            java.lang.String r2 = "APP_PAUSED"
            r0.<init>(r2)     // Catch:{ Exception -> 0x0256 }
            throw r0     // Catch:{ Exception -> 0x0256 }
        L_0x00ae:
            int r11 = r10.getCameraId()     // Catch:{ Exception -> 0x0249 }
            android.hardware.Camera r11 = android.hardware.Camera.open(r11)     // Catch:{ Exception -> 0x0249 }
            android.hardware.Camera$Parameters r12 = r11.getParameters()     // Catch:{ Exception -> 0x0249 }
            java.util.List r13 = r12.getSupportedPreviewSizes()     // Catch:{ Exception -> 0x0249 }
            r14 = 0
        L_0x00bf:
            int r15 = r13.size()     // Catch:{ Exception -> 0x0249 }
            java.lang.String r6 = " "
            r16 = r7
            r7 = 1280(0x500, float:1.794E-42)
            if (r14 >= r15) goto L_0x0129
            java.lang.Object r15 = r13.get(r14)     // Catch:{ Exception -> 0x0249 }
            android.hardware.Camera$Size r15 = (android.hardware.Camera.Size) r15     // Catch:{ Exception -> 0x0249 }
            r17 = r13
            int r13 = r15.width     // Catch:{ Exception -> 0x0249 }
            if (r13 != r7) goto L_0x00de
            int r7 = r15.height     // Catch:{ Exception -> 0x0256 }
            r13 = 720(0x2d0, float:1.009E-42)
            if (r7 == r13) goto L_0x00de
            goto L_0x011b
        L_0x00de:
            int r7 = r15.height     // Catch:{ Exception -> 0x0249 }
            r13 = 2160(0x870, float:3.027E-42)
            if (r7 >= r13) goto L_0x011b
            int r7 = r15.width     // Catch:{ Exception -> 0x0249 }
            if (r7 >= r13) goto L_0x011b
            java.util.ArrayList<org.telegram.messenger.camera.Size> r7 = r10.previewSizes     // Catch:{ Exception -> 0x0249 }
            org.telegram.messenger.camera.Size r13 = new org.telegram.messenger.camera.Size     // Catch:{ Exception -> 0x0249 }
            int r1 = r15.width     // Catch:{ Exception -> 0x0249 }
            r18 = r0
            int r0 = r15.height     // Catch:{ Exception -> 0x0249 }
            r13.<init>(r1, r0)     // Catch:{ Exception -> 0x0249 }
            r7.add(r13)     // Catch:{ Exception -> 0x0249 }
            boolean r0 = org.telegram.messenger.BuildVars.LOGS_ENABLED     // Catch:{ Exception -> 0x0249 }
            if (r0 == 0) goto L_0x011d
            java.lang.StringBuilder r0 = new java.lang.StringBuilder     // Catch:{ Exception -> 0x0249 }
            r0.<init>()     // Catch:{ Exception -> 0x0249 }
            java.lang.String r1 = "preview size = "
            r0.append(r1)     // Catch:{ Exception -> 0x0249 }
            int r1 = r15.width     // Catch:{ Exception -> 0x0249 }
            r0.append(r1)     // Catch:{ Exception -> 0x0249 }
            r0.append(r6)     // Catch:{ Exception -> 0x0249 }
            int r1 = r15.height     // Catch:{ Exception -> 0x0249 }
            r0.append(r1)     // Catch:{ Exception -> 0x0249 }
            java.lang.String r0 = r0.toString()     // Catch:{ Exception -> 0x0249 }
            org.telegram.messenger.FileLog.d(r0)     // Catch:{ Exception -> 0x0249 }
            goto L_0x011d
        L_0x011b:
            r18 = r0
        L_0x011d:
            int r14 = r14 + 1
            r1 = r19
            r7 = r16
            r13 = r17
            r0 = r18
            r6 = 0
            goto L_0x00bf
        L_0x0129:
            r18 = r0
            java.util.List r0 = r12.getSupportedPictureSizes()     // Catch:{ Exception -> 0x0249 }
            r1 = 0
        L_0x0130:
            int r12 = r0.size()     // Catch:{ Exception -> 0x0249 }
            if (r1 >= r12) goto L_0x0198
            java.lang.Object r12 = r0.get(r1)     // Catch:{ Exception -> 0x0249 }
            android.hardware.Camera$Size r12 = (android.hardware.Camera.Size) r12     // Catch:{ Exception -> 0x0249 }
            int r13 = r12.width     // Catch:{ Exception -> 0x0249 }
            if (r13 != r7) goto L_0x0147
            int r13 = r12.height     // Catch:{ Exception -> 0x0249 }
            r14 = 720(0x2d0, float:1.009E-42)
            if (r13 == r14) goto L_0x0149
            goto L_0x0193
        L_0x0147:
            r14 = 720(0x2d0, float:1.009E-42)
        L_0x0149:
            java.lang.String r13 = "samsung"
            java.lang.String r15 = android.os.Build.MANUFACTURER     // Catch:{ Exception -> 0x0249 }
            boolean r13 = r13.equals(r15)     // Catch:{ Exception -> 0x0249 }
            if (r13 == 0) goto L_0x0163
            java.lang.String r13 = "jflteuc"
            java.lang.String r15 = android.os.Build.PRODUCT     // Catch:{ Exception -> 0x0249 }
            boolean r13 = r13.equals(r15)     // Catch:{ Exception -> 0x0249 }
            if (r13 == 0) goto L_0x0163
            int r13 = r12.width     // Catch:{ Exception -> 0x0249 }
            r15 = 2048(0x800, float:2.87E-42)
            if (r13 >= r15) goto L_0x0193
        L_0x0163:
            java.util.ArrayList<org.telegram.messenger.camera.Size> r13 = r10.pictureSizes     // Catch:{ Exception -> 0x0249 }
            org.telegram.messenger.camera.Size r15 = new org.telegram.messenger.camera.Size     // Catch:{ Exception -> 0x0249 }
            int r7 = r12.width     // Catch:{ Exception -> 0x0249 }
            int r14 = r12.height     // Catch:{ Exception -> 0x0249 }
            r15.<init>(r7, r14)     // Catch:{ Exception -> 0x0249 }
            r13.add(r15)     // Catch:{ Exception -> 0x0249 }
            boolean r7 = org.telegram.messenger.BuildVars.LOGS_ENABLED     // Catch:{ Exception -> 0x0249 }
            if (r7 == 0) goto L_0x0193
            java.lang.StringBuilder r7 = new java.lang.StringBuilder     // Catch:{ Exception -> 0x0249 }
            r7.<init>()     // Catch:{ Exception -> 0x0249 }
            java.lang.String r13 = "picture size = "
            r7.append(r13)     // Catch:{ Exception -> 0x0249 }
            int r13 = r12.width     // Catch:{ Exception -> 0x0249 }
            r7.append(r13)     // Catch:{ Exception -> 0x0249 }
            r7.append(r6)     // Catch:{ Exception -> 0x0249 }
            int r12 = r12.height     // Catch:{ Exception -> 0x0249 }
            r7.append(r12)     // Catch:{ Exception -> 0x0249 }
            java.lang.String r7 = r7.toString()     // Catch:{ Exception -> 0x0249 }
            org.telegram.messenger.FileLog.d(r7)     // Catch:{ Exception -> 0x0249 }
        L_0x0193:
            int r1 = r1 + 1
            r7 = 1280(0x500, float:1.794E-42)
            goto L_0x0130
        L_0x0198:
            r11.release()     // Catch:{ Exception -> 0x0249 }
            r5.add(r10)     // Catch:{ Exception -> 0x0249 }
            java.util.ArrayList<org.telegram.messenger.camera.Size> r0 = r10.previewSizes     // Catch:{ Exception -> 0x0249 }
            java.util.Collections.sort(r0, r4)     // Catch:{ Exception -> 0x0249 }
            java.util.ArrayList<org.telegram.messenger.camera.Size> r0 = r10.pictureSizes     // Catch:{ Exception -> 0x0249 }
            java.util.Collections.sort(r0, r4)     // Catch:{ Exception -> 0x0249 }
            java.util.ArrayList<org.telegram.messenger.camera.Size> r0 = r10.previewSizes     // Catch:{ Exception -> 0x0249 }
            int r0 = r0.size()     // Catch:{ Exception -> 0x0249 }
            java.util.ArrayList<org.telegram.messenger.camera.Size> r1 = r10.pictureSizes     // Catch:{ Exception -> 0x0249 }
            int r1 = r1.size()     // Catch:{ Exception -> 0x0249 }
            int r0 = r0 + r1
            int r0 = r0 * 8
            int r0 = r0 + 8
            int r9 = r9 + r0
            int r8 = r8 + 1
            r6 = 0
            r1 = r19
            r7 = r16
            r0 = r18
            goto L_0x0091
        L_0x01c5:
            r18 = r0
            org.telegram.tgnet.SerializedData r0 = new org.telegram.tgnet.SerializedData     // Catch:{ Exception -> 0x0249 }
            r0.<init>((int) r9)     // Catch:{ Exception -> 0x0249 }
            int r1 = r5.size()     // Catch:{ Exception -> 0x0249 }
            r0.writeInt32(r1)     // Catch:{ Exception -> 0x0249 }
            r1 = 0
        L_0x01d4:
            if (r1 >= r3) goto L_0x022b
            java.lang.Object r4 = r5.get(r1)     // Catch:{ Exception -> 0x0249 }
            org.telegram.messenger.camera.CameraInfo r4 = (org.telegram.messenger.camera.CameraInfo) r4     // Catch:{ Exception -> 0x0249 }
            int r6 = r4.cameraId     // Catch:{ Exception -> 0x0249 }
            r0.writeInt32(r6)     // Catch:{ Exception -> 0x0249 }
            int r6 = r4.frontCamera     // Catch:{ Exception -> 0x0249 }
            r0.writeInt32(r6)     // Catch:{ Exception -> 0x0249 }
            java.util.ArrayList<org.telegram.messenger.camera.Size> r6 = r4.previewSizes     // Catch:{ Exception -> 0x0249 }
            int r6 = r6.size()     // Catch:{ Exception -> 0x0249 }
            r0.writeInt32(r6)     // Catch:{ Exception -> 0x0249 }
            r7 = 0
        L_0x01f0:
            if (r7 >= r6) goto L_0x0207
            java.util.ArrayList<org.telegram.messenger.camera.Size> r8 = r4.previewSizes     // Catch:{ Exception -> 0x0249 }
            java.lang.Object r8 = r8.get(r7)     // Catch:{ Exception -> 0x0249 }
            org.telegram.messenger.camera.Size r8 = (org.telegram.messenger.camera.Size) r8     // Catch:{ Exception -> 0x0249 }
            int r9 = r8.mWidth     // Catch:{ Exception -> 0x0249 }
            r0.writeInt32(r9)     // Catch:{ Exception -> 0x0249 }
            int r8 = r8.mHeight     // Catch:{ Exception -> 0x0249 }
            r0.writeInt32(r8)     // Catch:{ Exception -> 0x0249 }
            int r7 = r7 + 1
            goto L_0x01f0
        L_0x0207:
            java.util.ArrayList<org.telegram.messenger.camera.Size> r6 = r4.pictureSizes     // Catch:{ Exception -> 0x0249 }
            int r6 = r6.size()     // Catch:{ Exception -> 0x0249 }
            r0.writeInt32(r6)     // Catch:{ Exception -> 0x0249 }
            r7 = 0
        L_0x0211:
            if (r7 >= r6) goto L_0x0228
            java.util.ArrayList<org.telegram.messenger.camera.Size> r8 = r4.pictureSizes     // Catch:{ Exception -> 0x0249 }
            java.lang.Object r8 = r8.get(r7)     // Catch:{ Exception -> 0x0249 }
            org.telegram.messenger.camera.Size r8 = (org.telegram.messenger.camera.Size) r8     // Catch:{ Exception -> 0x0249 }
            int r9 = r8.mWidth     // Catch:{ Exception -> 0x0249 }
            r0.writeInt32(r9)     // Catch:{ Exception -> 0x0249 }
            int r8 = r8.mHeight     // Catch:{ Exception -> 0x0249 }
            r0.writeInt32(r8)     // Catch:{ Exception -> 0x0249 }
            int r7 = r7 + 1
            goto L_0x0211
        L_0x0228:
            int r1 = r1 + 1
            goto L_0x01d4
        L_0x022b:
            android.content.SharedPreferences$Editor r1 = r2.edit()     // Catch:{ Exception -> 0x0249 }
            byte[] r2 = r0.toByteArray()     // Catch:{ Exception -> 0x0249 }
            r3 = 0
            java.lang.String r2 = android.util.Base64.encodeToString(r2, r3)     // Catch:{ Exception -> 0x0249 }
            r3 = r18
            android.content.SharedPreferences$Editor r1 = r1.putString(r3, r2)     // Catch:{ Exception -> 0x0249 }
            r1.commit()     // Catch:{ Exception -> 0x0249 }
            r0.cleanup()     // Catch:{ Exception -> 0x0249 }
            r1 = r19
        L_0x0246:
            r1.cameraInfos = r5     // Catch:{ Exception -> 0x0256 }
            goto L_0x024d
        L_0x0249:
            r0 = move-exception
            r1 = r19
            goto L_0x0257
        L_0x024d:
            org.telegram.messenger.camera.-$$Lambda$CameraController$llC2aHeeX-BAOaFu9N7370RXqsE r0 = new org.telegram.messenger.camera.-$$Lambda$CameraController$llC2aHeeX-BAOaFu9N7370RXqsE     // Catch:{ Exception -> 0x0256 }
            r0.<init>()     // Catch:{ Exception -> 0x0256 }
            org.telegram.messenger.AndroidUtilities.runOnUIThread(r0)     // Catch:{ Exception -> 0x0256 }
            goto L_0x0263
        L_0x0256:
            r0 = move-exception
        L_0x0257:
            org.telegram.messenger.camera.-$$Lambda$CameraController$88xHVzlx_IKS4IavCNfpJS9ZRAE r2 = new org.telegram.messenger.camera.-$$Lambda$CameraController$88xHVzlx_IKS4IavCNfpJS9ZRAE
            r3 = r20
            r4 = r21
            r2.<init>(r3, r0, r4)
            org.telegram.messenger.AndroidUtilities.runOnUIThread(r2)
        L_0x0263:
            return
        */
        throw new UnsupportedOperationException("Method not decompiled: org.telegram.messenger.camera.CameraController.lambda$initCamera$4$CameraController(boolean, java.lang.Runnable):void");
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

    public /* synthetic */ void lambda$null$3$CameraController(boolean z, Exception exc, Runnable runnable) {
        this.onFinishCameraInitRunnables.clear();
        this.loadingCameras = false;
        this.cameraInitied = false;
        if (!z && "APP_PAUSED".equals(exc.getMessage())) {
            AndroidUtilities.runOnUIThread(new Runnable(runnable) {
                private final /* synthetic */ Runnable f$1;

                {
                    this.f$1 = r2;
                }

                public final void run() {
                    CameraController.this.lambda$null$2$CameraController(this.f$1);
                }
            }, 1000);
        }
    }

    public /* synthetic */ void lambda$null$2$CameraController(Runnable runnable) {
        initCamera(runnable, true);
    }

    public boolean isCameraInitied() {
        return this.cameraInitied && this.cameraInfos != null && !this.cameraInfos.isEmpty();
    }

    public void runOnThreadPool(Runnable runnable) {
        this.threadPool.execute(runnable);
    }

    public void close(CameraSession cameraSession, CountDownLatch countDownLatch, Runnable runnable) {
        cameraSession.destroy();
        this.threadPool.execute(new Runnable(runnable, cameraSession, countDownLatch) {
            private final /* synthetic */ Runnable f$0;
            private final /* synthetic */ CameraSession f$1;
            private final /* synthetic */ CountDownLatch f$2;

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
        r2 = 0;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:34:0x0061, code lost:
        if (r2 <= 8) goto L_0x00bd;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:35:0x0063, code lost:
        r3 = pack(r10, r1, 4, false);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:36:0x006a, code lost:
        if (r3 == NUM) goto L_0x0072;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:38:0x006f, code lost:
        if (r3 == NUM) goto L_0x0072;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:39:0x0071, code lost:
        return 0;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:40:0x0072, code lost:
        if (r3 != NUM) goto L_0x0076;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:41:0x0074, code lost:
        r3 = true;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:42:0x0076, code lost:
        r3 = false;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:43:0x0077, code lost:
        r4 = pack(r10, r1 + 4, 4, r3) + 2;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:44:0x0080, code lost:
        if (r4 < 10) goto L_0x00bd;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:45:0x0082, code lost:
        if (r4 <= r2) goto L_0x0085;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:46:0x0085, code lost:
        r1 = r1 + r4;
        r2 = r2 - r4;
        r4 = pack(r10, r1 - 2, 2, r3);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:47:0x008d, code lost:
        r8 = r4 - 1;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:48:0x008f, code lost:
        if (r4 <= 0) goto L_0x00bd;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:50:0x0093, code lost:
        if (r2 < 12) goto L_0x00bd;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:52:0x009b, code lost:
        if (pack(r10, r1, 2, r3) != 274) goto L_0x00b7;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:53:0x009d, code lost:
        r10 = pack(r10, r1 + 8, 2, r3);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:54:0x00a2, code lost:
        if (r10 == 1) goto L_0x00b6;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:56:0x00a5, code lost:
        if (r10 == 3) goto L_0x00b3;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:58:0x00a8, code lost:
        if (r10 == 6) goto L_0x00b0;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:59:0x00aa, code lost:
        if (r10 == 8) goto L_0x00ad;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:60:0x00ac, code lost:
        return 0;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:61:0x00ad, code lost:
        return 270;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:62:0x00b0, code lost:
        return 90;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:63:0x00b3, code lost:
        return 180;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:64:0x00b6, code lost:
        return 0;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:65:0x00b7, code lost:
        r1 = r1 + 12;
        r2 = r2 - 12;
        r4 = r8;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:66:0x00bd, code lost:
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
            int r2 = r3 + -8
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
            r2 = 0
        L_0x0061:
            if (r2 <= r6) goto L_0x00bd
            int r3 = pack(r10, r1, r4, r0)
            r8 = 1229531648(0x49492a00, float:823968.0)
            if (r3 == r8) goto L_0x0072
            r9 = 1296891946(0x4d4d002a, float:2.14958752E8)
            if (r3 == r9) goto L_0x0072
            return r0
        L_0x0072:
            if (r3 != r8) goto L_0x0076
            r3 = 1
            goto L_0x0077
        L_0x0076:
            r3 = 0
        L_0x0077:
            int r8 = r1 + 4
            int r4 = pack(r10, r8, r4, r3)
            int r4 = r4 + r7
            r8 = 10
            if (r4 < r8) goto L_0x00bd
            if (r4 <= r2) goto L_0x0085
            goto L_0x00bd
        L_0x0085:
            int r1 = r1 + r4
            int r2 = r2 - r4
            int r4 = r1 + -2
            int r4 = pack(r10, r4, r7, r3)
        L_0x008d:
            int r8 = r4 + -1
            if (r4 <= 0) goto L_0x00bd
            r4 = 12
            if (r2 < r4) goto L_0x00bd
            int r4 = pack(r10, r1, r7, r3)
            r9 = 274(0x112, float:3.84E-43)
            if (r4 != r9) goto L_0x00b7
            int r1 = r1 + r6
            int r10 = pack(r10, r1, r7, r3)
            if (r10 == r5) goto L_0x00b6
            r1 = 3
            if (r10 == r1) goto L_0x00b3
            r1 = 6
            if (r10 == r1) goto L_0x00b0
            if (r10 == r6) goto L_0x00ad
            return r0
        L_0x00ad:
            r10 = 270(0x10e, float:3.78E-43)
            return r10
        L_0x00b0:
            r10 = 90
            return r10
        L_0x00b3:
            r10 = 180(0xb4, float:2.52E-43)
            return r10
        L_0x00b6:
            return r0
        L_0x00b7:
            int r1 = r1 + 12
            int r2 = r2 + -12
            r4 = r8
            goto L_0x008d
        L_0x00bd:
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
                private final /* synthetic */ File f$0;
                private final /* synthetic */ CameraInfo f$1;
                private final /* synthetic */ boolean f$2;
                private final /* synthetic */ Runnable f$3;

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
            float max = Math.max(((float) options.outWidth) / ((float) AndroidUtilities.getPhotoSize()), ((float) options.outHeight) / ((float) AndroidUtilities.getPhotoSize()));
            if (max < 1.0f) {
                max = 1.0f;
            }
            options.inJustDecodeBounds = false;
            options.inSampleSize = (int) max;
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
                    if (createBitmap != null) {
                        ImageLoader.getInstance().putImageToCache(new BitmapDrawable(createBitmap), format);
                    }
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
                private final /* synthetic */ Runnable f$1;
                private final /* synthetic */ SurfaceTexture f$2;
                private final /* synthetic */ Runnable f$3;

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
                Camera open = Camera.open(cameraSession.cameraInfo.cameraId);
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
                private final /* synthetic */ CameraSession f$1;
                private final /* synthetic */ Runnable f$2;
                private final /* synthetic */ SurfaceTexture f$3;
                private final /* synthetic */ Runnable f$4;

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

    public void recordVideo(CameraSession cameraSession, File file, VideoTakeCallback videoTakeCallback, Runnable runnable) {
        if (cameraSession != null) {
            CameraInfo cameraInfo = cameraSession.cameraInfo;
            this.threadPool.execute(new Runnable(cameraInfo.camera, cameraSession, file, cameraInfo, videoTakeCallback, runnable) {
                private final /* synthetic */ Camera f$1;
                private final /* synthetic */ CameraSession f$2;
                private final /* synthetic */ File f$3;
                private final /* synthetic */ CameraInfo f$4;
                private final /* synthetic */ CameraController.VideoTakeCallback f$5;
                private final /* synthetic */ Runnable f$6;

                {
                    this.f$1 = r2;
                    this.f$2 = r3;
                    this.f$3 = r4;
                    this.f$4 = r5;
                    this.f$5 = r6;
                    this.f$6 = r7;
                }

                public final void run() {
                    CameraController.this.lambda$recordVideo$11$CameraController(this.f$1, this.f$2, this.f$3, this.f$4, this.f$5, this.f$6);
                }
            });
        }
    }

    public /* synthetic */ void lambda$recordVideo$11$CameraController(Camera camera, CameraSession cameraSession, File file, CameraInfo cameraInfo, VideoTakeCallback videoTakeCallback, Runnable runnable) {
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
                this.recorder = new MediaRecorder();
                this.recorder.setCamera(camera);
                this.recorder.setVideoSource(1);
                this.recorder.setAudioSource(5);
                cameraSession.configureRecorder(1, this.recorder);
                this.recorder.setOutputFile(file.getAbsolutePath());
                this.recorder.setMaxFileSize(NUM);
                this.recorder.setVideoFrameRate(30);
                this.recorder.setMaxDuration(0);
                Size chooseOptimalSize = chooseOptimalSize(cameraInfo.getPictureSizes(), 720, 480, new Size(16, 9));
                this.recorder.setVideoEncodingBitRate(1800000);
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

    /* JADX WARNING: Removed duplicated region for block: B:19:0x0039 A[SYNTHETIC, Splitter:B:19:0x0039] */
    /* JADX WARNING: Removed duplicated region for block: B:32:0x008f A[SYNTHETIC, Splitter:B:32:0x008f] */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    private void finishRecordingVideo() {
        /*
            r10 = this;
            r0 = 0
            r1 = 0
            android.media.MediaMetadataRetriever r3 = new android.media.MediaMetadataRetriever     // Catch:{ Exception -> 0x0030, all -> 0x002c }
            r3.<init>()     // Catch:{ Exception -> 0x0030, all -> 0x002c }
            java.lang.String r0 = r10.recordedFile     // Catch:{ Exception -> 0x002a }
            r3.setDataSource(r0)     // Catch:{ Exception -> 0x002a }
            r0 = 9
            java.lang.String r0 = r3.extractMetadata(r0)     // Catch:{ Exception -> 0x002a }
            if (r0 == 0) goto L_0x0024
            long r4 = java.lang.Long.parseLong(r0)     // Catch:{ Exception -> 0x002a }
            float r0 = (float) r4     // Catch:{ Exception -> 0x002a }
            r4 = 1148846080(0x447a0000, float:1000.0)
            float r0 = r0 / r4
            double r4 = (double) r0     // Catch:{ Exception -> 0x002a }
            double r0 = java.lang.Math.ceil(r4)     // Catch:{ Exception -> 0x002a }
            int r0 = (int) r0
            long r1 = (long) r0
        L_0x0024:
            r3.release()     // Catch:{ Exception -> 0x0028 }
            goto L_0x0041
        L_0x0028:
            r0 = move-exception
            goto L_0x003e
        L_0x002a:
            r0 = move-exception
            goto L_0x0034
        L_0x002c:
            r1 = move-exception
            r3 = r0
            r0 = r1
            goto L_0x008d
        L_0x0030:
            r3 = move-exception
            r9 = r3
            r3 = r0
            r0 = r9
        L_0x0034:
            org.telegram.messenger.FileLog.e((java.lang.Throwable) r0)     // Catch:{ all -> 0x008c }
            if (r3 == 0) goto L_0x0041
            r3.release()     // Catch:{ Exception -> 0x003d }
            goto L_0x0041
        L_0x003d:
            r0 = move-exception
        L_0x003e:
            org.telegram.messenger.FileLog.e((java.lang.Throwable) r0)
        L_0x0041:
            r7 = r1
            java.lang.String r0 = r10.recordedFile
            r1 = 1
            android.graphics.Bitmap r6 = android.media.ThumbnailUtils.createVideoThumbnail(r0, r1)
            java.lang.StringBuilder r0 = new java.lang.StringBuilder
            r0.<init>()
            java.lang.String r1 = "-2147483648_"
            r0.append(r1)
            int r1 = org.telegram.messenger.SharedConfig.getLastLocalId()
            r0.append(r1)
            java.lang.String r1 = ".jpg"
            r0.append(r1)
            java.lang.String r0 = r0.toString()
            java.io.File r5 = new java.io.File
            r1 = 4
            java.io.File r1 = org.telegram.messenger.FileLoader.getDirectory(r1)
            r5.<init>(r1, r0)
            java.io.FileOutputStream r0 = new java.io.FileOutputStream     // Catch:{ all -> 0x007a }
            r0.<init>(r5)     // Catch:{ all -> 0x007a }
            android.graphics.Bitmap$CompressFormat r1 = android.graphics.Bitmap.CompressFormat.JPEG     // Catch:{ all -> 0x007a }
            r2 = 80
            r6.compress(r1, r2, r0)     // Catch:{ all -> 0x007a }
            goto L_0x007e
        L_0x007a:
            r0 = move-exception
            org.telegram.messenger.FileLog.e((java.lang.Throwable) r0)
        L_0x007e:
            org.telegram.messenger.SharedConfig.saveConfig()
            org.telegram.messenger.camera.-$$Lambda$CameraController$Z2e0QjeyPqVck_gD_FIZx1BqCLASSNAME r0 = new org.telegram.messenger.camera.-$$Lambda$CameraController$Z2e0QjeyPqVck_gD_FIZx1BqCLASSNAME
            r3 = r0
            r4 = r10
            r3.<init>(r5, r6, r7)
            org.telegram.messenger.AndroidUtilities.runOnUIThread(r0)
            return
        L_0x008c:
            r0 = move-exception
        L_0x008d:
            if (r3 == 0) goto L_0x0097
            r3.release()     // Catch:{ Exception -> 0x0093 }
            goto L_0x0097
        L_0x0093:
            r1 = move-exception
            org.telegram.messenger.FileLog.e((java.lang.Throwable) r1)
        L_0x0097:
            throw r0
        */
        throw new UnsupportedOperationException("Method not decompiled: org.telegram.messenger.camera.CameraController.finishRecordingVideo():void");
    }

    public /* synthetic */ void lambda$finishRecordingVideo$12$CameraController(File file, Bitmap bitmap, long j) {
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
                finishRecordingVideo();
            }
        }
    }

    public void stopVideoRecording(CameraSession cameraSession, boolean z) {
        this.threadPool.execute(new Runnable(cameraSession, z) {
            private final /* synthetic */ CameraSession f$1;
            private final /* synthetic */ boolean f$2;

            {
                this.f$1 = r2;
                this.f$2 = r3;
            }

            public final void run() {
                CameraController.this.lambda$stopVideoRecording$14$CameraController(this.f$1, this.f$2);
            }
        });
    }

    public /* synthetic */ void lambda$stopVideoRecording$14$CameraController(CameraSession cameraSession, boolean z) {
        try {
            Camera camera = cameraSession.cameraInfo.camera;
            if (!(camera == null || this.recorder == null)) {
                MediaRecorder mediaRecorder = this.recorder;
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
                private final /* synthetic */ Camera f$0;
                private final /* synthetic */ CameraSession f$1;

                {
                    this.f$0 = r1;
                    this.f$1 = r2;
                }

                public final void run() {
                    CameraController.lambda$null$13(this.f$0, this.f$1);
                }
            });
            if (z || this.onVideoTakeCallback == null) {
                this.onVideoTakeCallback = null;
            } else {
                finishRecordingVideo();
            }
        } catch (Exception unused) {
        }
    }

    static /* synthetic */ void lambda$null$13(Camera camera, CameraSession cameraSession) {
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

    static class CompareSizesByArea implements Comparator<Size> {
        CompareSizesByArea() {
        }

        public int compare(Size size, Size size2) {
            return Long.signum((((long) size.getWidth()) * ((long) size.getHeight())) - (((long) size2.getWidth()) * ((long) size2.getHeight())));
        }
    }
}
