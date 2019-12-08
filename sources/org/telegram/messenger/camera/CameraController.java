package org.telegram.messenger.camera;

import android.graphics.Bitmap;
import android.graphics.Bitmap.CompressFormat;
import android.graphics.BitmapFactory;
import android.graphics.BitmapFactory.Options;
import android.graphics.Matrix;
import android.graphics.SurfaceTexture;
import android.graphics.drawable.BitmapDrawable;
import android.hardware.Camera;
import android.hardware.Camera.Parameters;
import android.media.MediaRecorder;
import android.media.MediaRecorder.OnInfoListener;
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

public class CameraController implements OnInfoListener {
    private static final int CORE_POOL_SIZE = 1;
    private static volatile CameraController Instance = null;
    private static final int KEEP_ALIVE_SECONDS = 60;
    private static final int MAX_POOL_SIZE = 1;
    protected ArrayList<String> availableFlashModes = new ArrayList();
    protected volatile ArrayList<CameraInfo> cameraInfos;
    private boolean cameraInitied;
    private boolean loadingCameras;
    private ArrayList<Runnable> onFinishCameraInitRunnables = new ArrayList();
    private VideoTakeCallback onVideoTakeCallback;
    private String recordedFile;
    private MediaRecorder recorder;
    private ThreadPoolExecutor threadPool = new ThreadPoolExecutor(1, 1, 60, TimeUnit.SECONDS, new LinkedBlockingQueue());

    static class CompareSizesByArea implements Comparator<Size> {
        CompareSizesByArea() {
        }

        public int compare(Size size, Size size2) {
            return Long.signum((((long) size.getWidth()) * ((long) size.getHeight())) - (((long) size2.getWidth()) * ((long) size2.getHeight())));
        }
    }

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
            if (!(runnable == null || this.onFinishCameraInitRunnables.contains(runnable))) {
                this.onFinishCameraInitRunnables.add(runnable);
            }
            if (!(this.loadingCameras || this.cameraInitied)) {
                this.loadingCameras = true;
                this.threadPool.execute(new -$$Lambda$CameraController$ggbrNYdASCmrNMDDtuD6yOnkL70(this, z, runnable));
            }
        }
    }

    /* JADX WARNING: Missing block: B:39:0x00db, code skipped:
            if (r15.height != 720) goto L_0x011b;
     */
    public /* synthetic */ void lambda$initCamera$4$CameraController(boolean r20, java.lang.Runnable r21) {
        /*
        r19 = this;
        r1 = r19;
        r0 = "cameraCache";
        r2 = r1.cameraInfos;	 Catch:{ Exception -> 0x0256 }
        if (r2 != 0) goto L_0x024d;
    L_0x0008:
        r2 = org.telegram.messenger.MessagesController.getGlobalMainSettings();	 Catch:{ Exception -> 0x0256 }
        r3 = 0;
        r3 = r2.getString(r0, r3);	 Catch:{ Exception -> 0x0256 }
        r4 = org.telegram.messenger.camera.-$$Lambda$CameraController$dwxNXb3SuNA_od-SPUyypaONvgM.INSTANCE;	 Catch:{ Exception -> 0x0256 }
        r5 = new java.util.ArrayList;	 Catch:{ Exception -> 0x0256 }
        r5.<init>();	 Catch:{ Exception -> 0x0256 }
        r6 = 0;
        if (r3 == 0) goto L_0x0085;
    L_0x001b:
        r0 = new org.telegram.tgnet.SerializedData;	 Catch:{ Exception -> 0x0256 }
        r2 = android.util.Base64.decode(r3, r6);	 Catch:{ Exception -> 0x0256 }
        r0.<init>(r2);	 Catch:{ Exception -> 0x0256 }
        r2 = r0.readInt32(r6);	 Catch:{ Exception -> 0x0256 }
        r3 = 0;
    L_0x0029:
        if (r3 >= r2) goto L_0x0080;
    L_0x002b:
        r7 = new org.telegram.messenger.camera.CameraInfo;	 Catch:{ Exception -> 0x0256 }
        r8 = r0.readInt32(r6);	 Catch:{ Exception -> 0x0256 }
        r9 = r0.readInt32(r6);	 Catch:{ Exception -> 0x0256 }
        r7.<init>(r8, r9);	 Catch:{ Exception -> 0x0256 }
        r8 = r0.readInt32(r6);	 Catch:{ Exception -> 0x0256 }
        r9 = 0;
    L_0x003d:
        if (r9 >= r8) goto L_0x0054;
    L_0x003f:
        r10 = r7.previewSizes;	 Catch:{ Exception -> 0x0256 }
        r11 = new org.telegram.messenger.camera.Size;	 Catch:{ Exception -> 0x0256 }
        r12 = r0.readInt32(r6);	 Catch:{ Exception -> 0x0256 }
        r13 = r0.readInt32(r6);	 Catch:{ Exception -> 0x0256 }
        r11.<init>(r12, r13);	 Catch:{ Exception -> 0x0256 }
        r10.add(r11);	 Catch:{ Exception -> 0x0256 }
        r9 = r9 + 1;
        goto L_0x003d;
    L_0x0054:
        r8 = r0.readInt32(r6);	 Catch:{ Exception -> 0x0256 }
        r9 = 0;
    L_0x0059:
        if (r9 >= r8) goto L_0x0070;
    L_0x005b:
        r10 = r7.pictureSizes;	 Catch:{ Exception -> 0x0256 }
        r11 = new org.telegram.messenger.camera.Size;	 Catch:{ Exception -> 0x0256 }
        r12 = r0.readInt32(r6);	 Catch:{ Exception -> 0x0256 }
        r13 = r0.readInt32(r6);	 Catch:{ Exception -> 0x0256 }
        r11.<init>(r12, r13);	 Catch:{ Exception -> 0x0256 }
        r10.add(r11);	 Catch:{ Exception -> 0x0256 }
        r9 = r9 + 1;
        goto L_0x0059;
    L_0x0070:
        r5.add(r7);	 Catch:{ Exception -> 0x0256 }
        r8 = r7.previewSizes;	 Catch:{ Exception -> 0x0256 }
        java.util.Collections.sort(r8, r4);	 Catch:{ Exception -> 0x0256 }
        r7 = r7.pictureSizes;	 Catch:{ Exception -> 0x0256 }
        java.util.Collections.sort(r7, r4);	 Catch:{ Exception -> 0x0256 }
        r3 = r3 + 1;
        goto L_0x0029;
    L_0x0080:
        r0.cleanup();	 Catch:{ Exception -> 0x0256 }
        goto L_0x0246;
    L_0x0085:
        r3 = android.hardware.Camera.getNumberOfCameras();	 Catch:{ Exception -> 0x0256 }
        r7 = new android.hardware.Camera$CameraInfo;	 Catch:{ Exception -> 0x0256 }
        r7.<init>();	 Catch:{ Exception -> 0x0256 }
        r8 = 4;
        r8 = 0;
        r9 = 4;
    L_0x0091:
        if (r8 >= r3) goto L_0x01c5;
    L_0x0093:
        android.hardware.Camera.getCameraInfo(r8, r7);	 Catch:{ Exception -> 0x0249 }
        r10 = new org.telegram.messenger.camera.CameraInfo;	 Catch:{ Exception -> 0x0249 }
        r11 = r7.facing;	 Catch:{ Exception -> 0x0249 }
        r10.<init>(r8, r11);	 Catch:{ Exception -> 0x0249 }
        r11 = org.telegram.messenger.ApplicationLoader.mainInterfacePaused;	 Catch:{ Exception -> 0x0249 }
        if (r11 == 0) goto L_0x00ae;
    L_0x00a1:
        r11 = org.telegram.messenger.ApplicationLoader.externalInterfacePaused;	 Catch:{ Exception -> 0x0256 }
        if (r11 != 0) goto L_0x00a6;
    L_0x00a5:
        goto L_0x00ae;
    L_0x00a6:
        r0 = new java.lang.RuntimeException;	 Catch:{ Exception -> 0x0256 }
        r2 = "APP_PAUSED";
        r0.<init>(r2);	 Catch:{ Exception -> 0x0256 }
        throw r0;	 Catch:{ Exception -> 0x0256 }
    L_0x00ae:
        r11 = r10.getCameraId();	 Catch:{ Exception -> 0x0249 }
        r11 = android.hardware.Camera.open(r11);	 Catch:{ Exception -> 0x0249 }
        r12 = r11.getParameters();	 Catch:{ Exception -> 0x0249 }
        r13 = r12.getSupportedPreviewSizes();	 Catch:{ Exception -> 0x0249 }
        r14 = 0;
    L_0x00bf:
        r15 = r13.size();	 Catch:{ Exception -> 0x0249 }
        r6 = " ";
        r16 = r7;
        r7 = 1280; // 0x500 float:1.794E-42 double:6.324E-321;
        if (r14 >= r15) goto L_0x0129;
    L_0x00cb:
        r15 = r13.get(r14);	 Catch:{ Exception -> 0x0249 }
        r15 = (android.hardware.Camera.Size) r15;	 Catch:{ Exception -> 0x0249 }
        r17 = r13;
        r13 = r15.width;	 Catch:{ Exception -> 0x0249 }
        if (r13 != r7) goto L_0x00de;
    L_0x00d7:
        r7 = r15.height;	 Catch:{ Exception -> 0x0256 }
        r13 = 720; // 0x2d0 float:1.009E-42 double:3.557E-321;
        if (r7 == r13) goto L_0x00de;
    L_0x00dd:
        goto L_0x011b;
    L_0x00de:
        r7 = r15.height;	 Catch:{ Exception -> 0x0249 }
        r13 = 2160; // 0x870 float:3.027E-42 double:1.067E-320;
        if (r7 >= r13) goto L_0x011b;
    L_0x00e4:
        r7 = r15.width;	 Catch:{ Exception -> 0x0249 }
        if (r7 >= r13) goto L_0x011b;
    L_0x00e8:
        r7 = r10.previewSizes;	 Catch:{ Exception -> 0x0249 }
        r13 = new org.telegram.messenger.camera.Size;	 Catch:{ Exception -> 0x0249 }
        r1 = r15.width;	 Catch:{ Exception -> 0x0249 }
        r18 = r0;
        r0 = r15.height;	 Catch:{ Exception -> 0x0249 }
        r13.<init>(r1, r0);	 Catch:{ Exception -> 0x0249 }
        r7.add(r13);	 Catch:{ Exception -> 0x0249 }
        r0 = org.telegram.messenger.BuildVars.LOGS_ENABLED;	 Catch:{ Exception -> 0x0249 }
        if (r0 == 0) goto L_0x011d;
    L_0x00fc:
        r0 = new java.lang.StringBuilder;	 Catch:{ Exception -> 0x0249 }
        r0.<init>();	 Catch:{ Exception -> 0x0249 }
        r1 = "preview size = ";
        r0.append(r1);	 Catch:{ Exception -> 0x0249 }
        r1 = r15.width;	 Catch:{ Exception -> 0x0249 }
        r0.append(r1);	 Catch:{ Exception -> 0x0249 }
        r0.append(r6);	 Catch:{ Exception -> 0x0249 }
        r1 = r15.height;	 Catch:{ Exception -> 0x0249 }
        r0.append(r1);	 Catch:{ Exception -> 0x0249 }
        r0 = r0.toString();	 Catch:{ Exception -> 0x0249 }
        org.telegram.messenger.FileLog.d(r0);	 Catch:{ Exception -> 0x0249 }
        goto L_0x011d;
    L_0x011b:
        r18 = r0;
    L_0x011d:
        r14 = r14 + 1;
        r1 = r19;
        r7 = r16;
        r13 = r17;
        r0 = r18;
        r6 = 0;
        goto L_0x00bf;
    L_0x0129:
        r18 = r0;
        r0 = r12.getSupportedPictureSizes();	 Catch:{ Exception -> 0x0249 }
        r1 = 0;
    L_0x0130:
        r12 = r0.size();	 Catch:{ Exception -> 0x0249 }
        if (r1 >= r12) goto L_0x0198;
    L_0x0136:
        r12 = r0.get(r1);	 Catch:{ Exception -> 0x0249 }
        r12 = (android.hardware.Camera.Size) r12;	 Catch:{ Exception -> 0x0249 }
        r13 = r12.width;	 Catch:{ Exception -> 0x0249 }
        if (r13 != r7) goto L_0x0147;
    L_0x0140:
        r13 = r12.height;	 Catch:{ Exception -> 0x0249 }
        r14 = 720; // 0x2d0 float:1.009E-42 double:3.557E-321;
        if (r13 == r14) goto L_0x0149;
    L_0x0146:
        goto L_0x0193;
    L_0x0147:
        r14 = 720; // 0x2d0 float:1.009E-42 double:3.557E-321;
    L_0x0149:
        r13 = "samsung";
        r15 = android.os.Build.MANUFACTURER;	 Catch:{ Exception -> 0x0249 }
        r13 = r13.equals(r15);	 Catch:{ Exception -> 0x0249 }
        if (r13 == 0) goto L_0x0163;
    L_0x0153:
        r13 = "jflteuc";
        r15 = android.os.Build.PRODUCT;	 Catch:{ Exception -> 0x0249 }
        r13 = r13.equals(r15);	 Catch:{ Exception -> 0x0249 }
        if (r13 == 0) goto L_0x0163;
    L_0x015d:
        r13 = r12.width;	 Catch:{ Exception -> 0x0249 }
        r15 = 2048; // 0x800 float:2.87E-42 double:1.0118E-320;
        if (r13 >= r15) goto L_0x0193;
    L_0x0163:
        r13 = r10.pictureSizes;	 Catch:{ Exception -> 0x0249 }
        r15 = new org.telegram.messenger.camera.Size;	 Catch:{ Exception -> 0x0249 }
        r7 = r12.width;	 Catch:{ Exception -> 0x0249 }
        r14 = r12.height;	 Catch:{ Exception -> 0x0249 }
        r15.<init>(r7, r14);	 Catch:{ Exception -> 0x0249 }
        r13.add(r15);	 Catch:{ Exception -> 0x0249 }
        r7 = org.telegram.messenger.BuildVars.LOGS_ENABLED;	 Catch:{ Exception -> 0x0249 }
        if (r7 == 0) goto L_0x0193;
    L_0x0175:
        r7 = new java.lang.StringBuilder;	 Catch:{ Exception -> 0x0249 }
        r7.<init>();	 Catch:{ Exception -> 0x0249 }
        r13 = "picture size = ";
        r7.append(r13);	 Catch:{ Exception -> 0x0249 }
        r13 = r12.width;	 Catch:{ Exception -> 0x0249 }
        r7.append(r13);	 Catch:{ Exception -> 0x0249 }
        r7.append(r6);	 Catch:{ Exception -> 0x0249 }
        r12 = r12.height;	 Catch:{ Exception -> 0x0249 }
        r7.append(r12);	 Catch:{ Exception -> 0x0249 }
        r7 = r7.toString();	 Catch:{ Exception -> 0x0249 }
        org.telegram.messenger.FileLog.d(r7);	 Catch:{ Exception -> 0x0249 }
    L_0x0193:
        r1 = r1 + 1;
        r7 = 1280; // 0x500 float:1.794E-42 double:6.324E-321;
        goto L_0x0130;
    L_0x0198:
        r11.release();	 Catch:{ Exception -> 0x0249 }
        r5.add(r10);	 Catch:{ Exception -> 0x0249 }
        r0 = r10.previewSizes;	 Catch:{ Exception -> 0x0249 }
        java.util.Collections.sort(r0, r4);	 Catch:{ Exception -> 0x0249 }
        r0 = r10.pictureSizes;	 Catch:{ Exception -> 0x0249 }
        java.util.Collections.sort(r0, r4);	 Catch:{ Exception -> 0x0249 }
        r0 = r10.previewSizes;	 Catch:{ Exception -> 0x0249 }
        r0 = r0.size();	 Catch:{ Exception -> 0x0249 }
        r1 = r10.pictureSizes;	 Catch:{ Exception -> 0x0249 }
        r1 = r1.size();	 Catch:{ Exception -> 0x0249 }
        r0 = r0 + r1;
        r0 = r0 * 8;
        r0 = r0 + 8;
        r9 = r9 + r0;
        r8 = r8 + 1;
        r6 = 0;
        r1 = r19;
        r7 = r16;
        r0 = r18;
        goto L_0x0091;
    L_0x01c5:
        r18 = r0;
        r0 = new org.telegram.tgnet.SerializedData;	 Catch:{ Exception -> 0x0249 }
        r0.<init>(r9);	 Catch:{ Exception -> 0x0249 }
        r1 = r5.size();	 Catch:{ Exception -> 0x0249 }
        r0.writeInt32(r1);	 Catch:{ Exception -> 0x0249 }
        r1 = 0;
    L_0x01d4:
        if (r1 >= r3) goto L_0x022b;
    L_0x01d6:
        r4 = r5.get(r1);	 Catch:{ Exception -> 0x0249 }
        r4 = (org.telegram.messenger.camera.CameraInfo) r4;	 Catch:{ Exception -> 0x0249 }
        r6 = r4.cameraId;	 Catch:{ Exception -> 0x0249 }
        r0.writeInt32(r6);	 Catch:{ Exception -> 0x0249 }
        r6 = r4.frontCamera;	 Catch:{ Exception -> 0x0249 }
        r0.writeInt32(r6);	 Catch:{ Exception -> 0x0249 }
        r6 = r4.previewSizes;	 Catch:{ Exception -> 0x0249 }
        r6 = r6.size();	 Catch:{ Exception -> 0x0249 }
        r0.writeInt32(r6);	 Catch:{ Exception -> 0x0249 }
        r7 = 0;
    L_0x01f0:
        if (r7 >= r6) goto L_0x0207;
    L_0x01f2:
        r8 = r4.previewSizes;	 Catch:{ Exception -> 0x0249 }
        r8 = r8.get(r7);	 Catch:{ Exception -> 0x0249 }
        r8 = (org.telegram.messenger.camera.Size) r8;	 Catch:{ Exception -> 0x0249 }
        r9 = r8.mWidth;	 Catch:{ Exception -> 0x0249 }
        r0.writeInt32(r9);	 Catch:{ Exception -> 0x0249 }
        r8 = r8.mHeight;	 Catch:{ Exception -> 0x0249 }
        r0.writeInt32(r8);	 Catch:{ Exception -> 0x0249 }
        r7 = r7 + 1;
        goto L_0x01f0;
    L_0x0207:
        r6 = r4.pictureSizes;	 Catch:{ Exception -> 0x0249 }
        r6 = r6.size();	 Catch:{ Exception -> 0x0249 }
        r0.writeInt32(r6);	 Catch:{ Exception -> 0x0249 }
        r7 = 0;
    L_0x0211:
        if (r7 >= r6) goto L_0x0228;
    L_0x0213:
        r8 = r4.pictureSizes;	 Catch:{ Exception -> 0x0249 }
        r8 = r8.get(r7);	 Catch:{ Exception -> 0x0249 }
        r8 = (org.telegram.messenger.camera.Size) r8;	 Catch:{ Exception -> 0x0249 }
        r9 = r8.mWidth;	 Catch:{ Exception -> 0x0249 }
        r0.writeInt32(r9);	 Catch:{ Exception -> 0x0249 }
        r8 = r8.mHeight;	 Catch:{ Exception -> 0x0249 }
        r0.writeInt32(r8);	 Catch:{ Exception -> 0x0249 }
        r7 = r7 + 1;
        goto L_0x0211;
    L_0x0228:
        r1 = r1 + 1;
        goto L_0x01d4;
    L_0x022b:
        r1 = r2.edit();	 Catch:{ Exception -> 0x0249 }
        r2 = r0.toByteArray();	 Catch:{ Exception -> 0x0249 }
        r3 = 0;
        r2 = android.util.Base64.encodeToString(r2, r3);	 Catch:{ Exception -> 0x0249 }
        r3 = r18;
        r1 = r1.putString(r3, r2);	 Catch:{ Exception -> 0x0249 }
        r1.commit();	 Catch:{ Exception -> 0x0249 }
        r0.cleanup();	 Catch:{ Exception -> 0x0249 }
        r1 = r19;
    L_0x0246:
        r1.cameraInfos = r5;	 Catch:{ Exception -> 0x0256 }
        goto L_0x024d;
    L_0x0249:
        r0 = move-exception;
        r1 = r19;
        goto L_0x0257;
    L_0x024d:
        r0 = new org.telegram.messenger.camera.-$$Lambda$CameraController$llC2aHeeX-BAOaFu9N7370RXqsE;	 Catch:{ Exception -> 0x0256 }
        r0.<init>(r1);	 Catch:{ Exception -> 0x0256 }
        org.telegram.messenger.AndroidUtilities.runOnUIThread(r0);	 Catch:{ Exception -> 0x0256 }
        goto L_0x0263;
    L_0x0256:
        r0 = move-exception;
    L_0x0257:
        r2 = new org.telegram.messenger.camera.-$$Lambda$CameraController$88xHVzlx_IKS4IavCNfpJS9ZRAE;
        r3 = r20;
        r4 = r21;
        r2.<init>(r1, r3, r0, r4);
        org.telegram.messenger.AndroidUtilities.runOnUIThread(r2);
    L_0x0263:
        return;
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
                ((Runnable) this.onFinishCameraInitRunnables.get(i)).run();
            }
            this.onFinishCameraInitRunnables.clear();
        }
        NotificationCenter.getGlobalInstance().postNotificationName(NotificationCenter.cameraInitied, new Object[0]);
    }

    public /* synthetic */ void lambda$null$3$CameraController(boolean z, Exception exception, Runnable runnable) {
        this.onFinishCameraInitRunnables.clear();
        this.loadingCameras = false;
        this.cameraInitied = false;
        if (!z) {
            if ("APP_PAUSED".equals(exception.getMessage())) {
                AndroidUtilities.runOnUIThread(new -$$Lambda$CameraController$ES-nh3SDMnEUzgixhCiAfadofL0(this, runnable), 1000);
            }
        }
    }

    public /* synthetic */ void lambda$null$2$CameraController(Runnable runnable) {
        initCamera(runnable, true);
    }

    public boolean isCameraInitied() {
        return (!this.cameraInitied || this.cameraInfos == null || this.cameraInfos.isEmpty()) ? false : true;
    }

    public void runOnThreadPool(Runnable runnable) {
        this.threadPool.execute(runnable);
    }

    public void close(CameraSession cameraSession, CountDownLatch countDownLatch, Runnable runnable) {
        cameraSession.destroy();
        this.threadPool.execute(new -$$Lambda$CameraController$OPlkNPWb4gYZLA_NLxCoTx8pFu8(runnable, cameraSession, countDownLatch));
        if (countDownLatch != null) {
            try {
                countDownLatch.await();
            } catch (Exception e) {
                FileLog.e(e);
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
                cameraSession.cameraInfo.camera.setPreviewCallbackWithBuffer(null);
            } catch (Exception e) {
                FileLog.e(e);
            }
            try {
                cameraSession.cameraInfo.camera.release();
            } catch (Exception e2) {
                FileLog.e(e2);
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

    private static int getOrientation(byte[] bArr) {
        if (bArr == null) {
            return 0;
        }
        int i;
        int pack;
        int i2;
        int i3 = 0;
        while (i3 + 3 < bArr.length) {
            i = i3 + 1;
            if ((bArr[i3] & 255) == 255) {
                i3 = bArr[i] & 255;
                if (i3 != 255) {
                    i++;
                    if (!(i3 == 216 || i3 == 1)) {
                        if (i3 != 217 && i3 != 218) {
                            pack = pack(bArr, i, 2, false);
                            if (pack >= 2) {
                                i2 = i + pack;
                                if (i2 <= bArr.length) {
                                    if (i3 == 225 && pack >= 8 && pack(bArr, i + 2, 4, false) == NUM && pack(bArr, i + 6, 2, false) == 0) {
                                        i3 = i + 8;
                                        i = pack - 8;
                                        break;
                                    }
                                    i3 = i2;
                                }
                            }
                            return 0;
                        }
                    }
                }
                i3 = i;
            }
            i3 = i;
        }
        i = 0;
        if (i > 8) {
            pack = pack(bArr, i3, 4, false);
            if (pack != NUM && pack != NUM) {
                return 0;
            }
            boolean z = pack == NUM;
            int pack2 = pack(bArr, i3 + 4, 4, z) + 2;
            if (pack2 >= 10 && pack2 <= i) {
                i3 += pack2;
                i -= pack2;
                pack2 = pack(bArr, i3 - 2, 2, z);
                while (true) {
                    i2 = pack2 - 1;
                    if (pack2 <= 0 || i < 12) {
                        break;
                    } else if (pack(bArr, i3, 2, z) == 274) {
                        int pack3 = pack(bArr, i3 + 8, 2, z);
                        if (pack3 == 1) {
                            return 0;
                        }
                        if (pack3 == 3) {
                            return 180;
                        }
                        if (pack3 == 6) {
                            return 90;
                        }
                        if (pack3 != 8) {
                            return 0;
                        }
                        return 270;
                    } else {
                        i3 += 12;
                        i -= 12;
                        pack2 = i2;
                    }
                }
            }
        }
        return 0;
    }

    private static int pack(byte[] bArr, int i, int i2, boolean z) {
        int i3;
        if (z) {
            i += i2 - 1;
            i3 = -1;
        } else {
            i3 = 1;
        }
        int i4 = 0;
        while (true) {
            int i5 = i2 - 1;
            if (i2 <= 0) {
                return i4;
            }
            i4 = (bArr[i] & 255) | (i4 << 8);
            i += i3;
            i2 = i5;
        }
    }

    public boolean takePicture(File file, CameraSession cameraSession, Runnable runnable) {
        if (cameraSession == null) {
            return false;
        }
        CameraInfo cameraInfo = cameraSession.cameraInfo;
        boolean isFlipFront = cameraSession.isFlipFront();
        try {
            cameraInfo.camera.takePicture(null, null, new -$$Lambda$CameraController$c9CThCYGhCTBXh4Mr7ku0l0BAnk(file, cameraInfo, isFlipFront, runnable));
            return true;
        } catch (Exception e) {
            FileLog.e(e);
            return false;
        }
    }

    static /* synthetic */ void lambda$takePicture$6(File file, CameraInfo cameraInfo, boolean z, Runnable runnable, byte[] bArr, Camera camera) {
        Bitmap decodeByteArray;
        int photoSize = (int) (((float) AndroidUtilities.getPhotoSize()) / AndroidUtilities.density);
        String format = String.format(Locale.US, "%s@%d_%d", new Object[]{Utilities.MD5(file.getAbsolutePath()), Integer.valueOf(photoSize), Integer.valueOf(photoSize)});
        try {
            Options options = new Options();
            options.inJustDecodeBounds = true;
            BitmapFactory.decodeByteArray(bArr, 0, bArr.length, options);
            float max = Math.max(((float) options.outWidth) / ((float) AndroidUtilities.getPhotoSize()), ((float) options.outHeight) / ((float) AndroidUtilities.getPhotoSize()));
            if (max < 1.0f) {
                max = 1.0f;
            }
            options.inJustDecodeBounds = false;
            options.inSampleSize = (int) max;
            options.inPurgeable = true;
            decodeByteArray = BitmapFactory.decodeByteArray(bArr, 0, bArr.length, options);
        } catch (Throwable th) {
            FileLog.e(th);
            decodeByteArray = null;
        }
        try {
            if (cameraInfo.frontCamera != 0 && z) {
                try {
                    Matrix matrix = new Matrix();
                    matrix.setRotate((float) getOrientation(bArr));
                    matrix.postScale(-1.0f, 1.0f);
                    Bitmap createBitmap = Bitmaps.createBitmap(decodeByteArray, 0, 0, decodeByteArray.getWidth(), decodeByteArray.getHeight(), matrix, true);
                    if (createBitmap != decodeByteArray) {
                        decodeByteArray.recycle();
                    }
                    FileOutputStream fileOutputStream = new FileOutputStream(file);
                    createBitmap.compress(CompressFormat.JPEG, 80, fileOutputStream);
                    fileOutputStream.flush();
                    fileOutputStream.getFD().sync();
                    fileOutputStream.close();
                    if (createBitmap != null) {
                        ImageLoader.getInstance().putImageToCache(new BitmapDrawable(createBitmap), format);
                    }
                    if (runnable != null) {
                        runnable.run();
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
            if (decodeByteArray != null) {
                ImageLoader.getInstance().putImageToCache(new BitmapDrawable(decodeByteArray), format);
            }
        } catch (Exception e) {
            FileLog.e(e);
        }
        if (runnable != null) {
            runnable.run();
        }
    }

    public void startPreview(CameraSession cameraSession) {
        if (cameraSession != null) {
            this.threadPool.execute(new -$$Lambda$CameraController$L-F-rOx5-4PSuT2nsIojJf6WzoI(cameraSession));
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
                FileLog.e(e);
                return;
            }
        }
        camera.startPreview();
    }

    public void stopPreview(CameraSession cameraSession) {
        if (cameraSession != null) {
            this.threadPool.execute(new -$$Lambda$CameraController$Sppc2sdna-XMD3CITaI1TGL0nUQ(cameraSession));
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
                FileLog.e(e);
                return;
            }
        }
        camera.stopPreview();
    }

    public void openRound(CameraSession cameraSession, SurfaceTexture surfaceTexture, Runnable runnable, Runnable runnable2) {
        if (cameraSession == null || surfaceTexture == null) {
            if (BuildVars.LOGS_ENABLED) {
                StringBuilder stringBuilder = new StringBuilder();
                stringBuilder.append("failed to open round ");
                stringBuilder.append(cameraSession);
                stringBuilder.append(" tex = ");
                stringBuilder.append(surfaceTexture);
                FileLog.d(stringBuilder.toString());
            }
            return;
        }
        this.threadPool.execute(new -$$Lambda$CameraController$oNeYarP4kUSxbQehHxX4kN35IvE(cameraSession, runnable2, surfaceTexture, runnable));
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
            FileLog.e(e);
        }
    }

    public void open(CameraSession cameraSession, SurfaceTexture surfaceTexture, Runnable runnable, Runnable runnable2) {
        if (cameraSession != null && surfaceTexture != null) {
            this.threadPool.execute(new -$$Lambda$CameraController$SyY9K-zIbzI3Sk7osY9JY9OVXaQ(this, cameraSession, runnable2, surfaceTexture, runnable));
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
                FileLog.e(e);
                return;
            }
        }
        List supportedFlashModes = camera.getParameters().getSupportedFlashModes();
        this.availableFlashModes.clear();
        if (supportedFlashModes != null) {
            for (int i = 0; i < supportedFlashModes.size(); i++) {
                String str = (String) supportedFlashModes.get(i);
                if (str.equals("off") || str.equals("on") || str.equals("auto")) {
                    this.availableFlashModes.add(str);
                }
            }
            cameraSession.checkFlashMode((String) this.availableFlashModes.get(0));
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
            this.threadPool.execute(new -$$Lambda$CameraController$PsMXI_giaQVKK9Lkw8itFSRPhsU(this, cameraInfo.camera, cameraSession, file, cameraInfo, videoTakeCallback, runnable));
        }
    }

    public /* synthetic */ void lambda$recordVideo$11$CameraController(Camera camera, CameraSession cameraSession, File file, CameraInfo cameraInfo, VideoTakeCallback videoTakeCallback, Runnable runnable) {
        if (camera != null) {
            try {
                Parameters parameters = camera.getParameters();
                parameters.setFlashMode(cameraSession.getCurrentFlashMode().equals("on") ? "torch" : "off");
                camera.setParameters(parameters);
            } catch (Exception e) {
                try {
                    FileLog.e(e);
                } catch (Exception e2) {
                    FileLog.e(e2);
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
            } catch (Exception e22) {
                this.recorder.release();
                this.recorder = null;
                FileLog.e(e22);
            }
        }
    }

    /* JADX WARNING: Removed duplicated region for block: B:19:0x0039 A:{SYNTHETIC, Splitter:B:19:0x0039} */
    /* JADX WARNING: Removed duplicated region for block: B:32:0x008f A:{SYNTHETIC, Splitter:B:32:0x008f} */
    private void finishRecordingVideo() {
        /*
        r10 = this;
        r0 = 0;
        r1 = 0;
        r3 = new android.media.MediaMetadataRetriever;	 Catch:{ Exception -> 0x0030, all -> 0x002c }
        r3.<init>();	 Catch:{ Exception -> 0x0030, all -> 0x002c }
        r0 = r10.recordedFile;	 Catch:{ Exception -> 0x002a }
        r3.setDataSource(r0);	 Catch:{ Exception -> 0x002a }
        r0 = 9;
        r0 = r3.extractMetadata(r0);	 Catch:{ Exception -> 0x002a }
        if (r0 == 0) goto L_0x0024;
    L_0x0015:
        r4 = java.lang.Long.parseLong(r0);	 Catch:{ Exception -> 0x002a }
        r0 = (float) r4;	 Catch:{ Exception -> 0x002a }
        r4 = NUM; // 0x447a0000 float:1000.0 double:5.676053805E-315;
        r0 = r0 / r4;
        r4 = (double) r0;	 Catch:{ Exception -> 0x002a }
        r0 = java.lang.Math.ceil(r4);	 Catch:{ Exception -> 0x002a }
        r0 = (int) r0;
        r1 = (long) r0;
    L_0x0024:
        r3.release();	 Catch:{ Exception -> 0x0028 }
        goto L_0x0041;
    L_0x0028:
        r0 = move-exception;
        goto L_0x003e;
    L_0x002a:
        r0 = move-exception;
        goto L_0x0034;
    L_0x002c:
        r1 = move-exception;
        r3 = r0;
        r0 = r1;
        goto L_0x008d;
    L_0x0030:
        r3 = move-exception;
        r9 = r3;
        r3 = r0;
        r0 = r9;
    L_0x0034:
        org.telegram.messenger.FileLog.e(r0);	 Catch:{ all -> 0x008c }
        if (r3 == 0) goto L_0x0041;
    L_0x0039:
        r3.release();	 Catch:{ Exception -> 0x003d }
        goto L_0x0041;
    L_0x003d:
        r0 = move-exception;
    L_0x003e:
        org.telegram.messenger.FileLog.e(r0);
    L_0x0041:
        r7 = r1;
        r0 = r10.recordedFile;
        r1 = 1;
        r6 = android.media.ThumbnailUtils.createVideoThumbnail(r0, r1);
        r0 = new java.lang.StringBuilder;
        r0.<init>();
        r1 = "-2147483648_";
        r0.append(r1);
        r1 = org.telegram.messenger.SharedConfig.getLastLocalId();
        r0.append(r1);
        r1 = ".jpg";
        r0.append(r1);
        r0 = r0.toString();
        r5 = new java.io.File;
        r1 = 4;
        r1 = org.telegram.messenger.FileLoader.getDirectory(r1);
        r5.<init>(r1, r0);
        r0 = new java.io.FileOutputStream;	 Catch:{ all -> 0x007a }
        r0.<init>(r5);	 Catch:{ all -> 0x007a }
        r1 = android.graphics.Bitmap.CompressFormat.JPEG;	 Catch:{ all -> 0x007a }
        r2 = 80;
        r6.compress(r1, r2, r0);	 Catch:{ all -> 0x007a }
        goto L_0x007e;
    L_0x007a:
        r0 = move-exception;
        org.telegram.messenger.FileLog.e(r0);
    L_0x007e:
        org.telegram.messenger.SharedConfig.saveConfig();
        r0 = new org.telegram.messenger.camera.-$$Lambda$CameraController$Z2e0QjeyPqVck_gD_FIZx1BqCLASSNAME;
        r3 = r0;
        r4 = r10;
        r3.<init>(r4, r5, r6, r7);
        org.telegram.messenger.AndroidUtilities.runOnUIThread(r0);
        return;
    L_0x008c:
        r0 = move-exception;
    L_0x008d:
        if (r3 == 0) goto L_0x0097;
    L_0x008f:
        r3.release();	 Catch:{ Exception -> 0x0093 }
        goto L_0x0097;
    L_0x0093:
        r1 = move-exception;
        org.telegram.messenger.FileLog.e(r1);
    L_0x0097:
        throw r0;
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
            mediaRecorder = this.recorder;
            this.recorder = null;
            if (mediaRecorder != null) {
                mediaRecorder.stop();
                mediaRecorder.release();
            }
            if (this.onVideoTakeCallback != null) {
                finishRecordingVideo();
            }
        }
    }

    public void stopVideoRecording(CameraSession cameraSession, boolean z) {
        this.threadPool.execute(new -$$Lambda$CameraController$Zappa2-Jg9Ryz3u1wD3YwAhMXaQ(this, cameraSession, z));
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
                    FileLog.e(e);
                }
                try {
                    mediaRecorder.release();
                } catch (Exception e2) {
                    FileLog.e(e2);
                }
                try {
                    camera.reconnect();
                    camera.startPreview();
                } catch (Exception e22) {
                    FileLog.e(e22);
                }
                try {
                    cameraSession.stopVideoRecording();
                } catch (Exception e222) {
                    FileLog.e(e222);
                }
            }
            try {
                Parameters parameters = camera.getParameters();
                parameters.setFlashMode("off");
                camera.setParameters(parameters);
            } catch (Exception e2222) {
                FileLog.e(e2222);
            }
            this.threadPool.execute(new -$$Lambda$CameraController$Wd0zMwxKwKvF9Nuyki-rVHJrIDw(camera, cameraSession));
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
            Parameters parameters = camera.getParameters();
            parameters.setFlashMode(cameraSession.getCurrentFlashMode());
            camera.setParameters(parameters);
        } catch (Exception e) {
            FileLog.e(e);
        }
    }

    public static Size chooseOptimalSize(List<Size> list, int i, int i2, Size size) {
        ArrayList arrayList = new ArrayList();
        int width = size.getWidth();
        int height = size.getHeight();
        for (int i3 = 0; i3 < list.size(); i3++) {
            Size size2 = (Size) list.get(i3);
            if (size2.getHeight() == (size2.getWidth() * height) / width && size2.getWidth() >= i && size2.getHeight() >= i2) {
                arrayList.add(size2);
            }
        }
        if (arrayList.size() > 0) {
            return (Size) Collections.min(arrayList, new CompareSizesByArea());
        }
        return (Size) Collections.max(list, new CompareSizesByArea());
    }
}
