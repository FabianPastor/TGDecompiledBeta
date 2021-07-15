package org.telegram.messenger.camera;

import android.graphics.Rect;
import android.hardware.Camera;
import android.media.CamcorderProfile;
import android.media.MediaRecorder;
import android.os.Build;
import android.view.OrientationEventListener;
import android.view.WindowManager;
import java.util.ArrayList;
import org.telegram.messenger.ApplicationLoader;
import org.telegram.messenger.FileLog;

public class CameraSession {
    public static final int ORIENTATION_HYSTERESIS = 5;
    private Camera.AutoFocusCallback autoFocusCallback = $$Lambda$CameraSession$aUpvoh7skCacjuNQtDivSRvySJM.INSTANCE;
    protected CameraInfo cameraInfo;
    private String currentFlashMode;
    private int currentOrientation;
    private float currentZoom;
    private int diffOrientation;
    private boolean flipFront = true;
    /* access modifiers changed from: private */
    public boolean initied;
    private boolean isRound;
    /* access modifiers changed from: private */
    public boolean isVideo;
    /* access modifiers changed from: private */
    public int jpegOrientation;
    /* access modifiers changed from: private */
    public int lastDisplayOrientation = -1;
    /* access modifiers changed from: private */
    public int lastOrientation = -1;
    private int maxZoom;
    private boolean meteringAreaSupported;
    private boolean optimizeForBarcode;
    /* access modifiers changed from: private */
    public OrientationEventListener orientationEventListener;
    private final int pictureFormat;
    private final Size pictureSize;
    private final Size previewSize;
    private boolean sameTakePictureOrientation;
    private boolean useTorch;

    static /* synthetic */ void lambda$new$0(boolean z, Camera camera) {
    }

    public CameraSession(CameraInfo cameraInfo2, Size size, Size size2, int i, boolean z) {
        this.previewSize = size;
        this.pictureSize = size2;
        this.pictureFormat = i;
        this.cameraInfo = cameraInfo2;
        this.isRound = z;
        this.currentFlashMode = ApplicationLoader.applicationContext.getSharedPreferences("camera", 0).getString(this.cameraInfo.frontCamera != 0 ? "flashMode_front" : "flashMode", "off");
        AnonymousClass1 r2 = new OrientationEventListener(ApplicationLoader.applicationContext) {
            public void onOrientationChanged(int i) {
                if (CameraSession.this.orientationEventListener != null && CameraSession.this.initied && i != -1) {
                    CameraSession cameraSession = CameraSession.this;
                    int unused = cameraSession.jpegOrientation = cameraSession.roundOrientation(i, cameraSession.jpegOrientation);
                    int rotation = ((WindowManager) ApplicationLoader.applicationContext.getSystemService("window")).getDefaultDisplay().getRotation();
                    if (CameraSession.this.lastOrientation != CameraSession.this.jpegOrientation || rotation != CameraSession.this.lastDisplayOrientation) {
                        if (!CameraSession.this.isVideo) {
                            CameraSession.this.configurePhotoCamera();
                        }
                        int unused2 = CameraSession.this.lastDisplayOrientation = rotation;
                        CameraSession cameraSession2 = CameraSession.this;
                        int unused3 = cameraSession2.lastOrientation = cameraSession2.jpegOrientation;
                    }
                }
            }
        };
        this.orientationEventListener = r2;
        if (r2.canDetectOrientation()) {
            this.orientationEventListener.enable();
            return;
        }
        this.orientationEventListener.disable();
        this.orientationEventListener = null;
    }

    /* access modifiers changed from: private */
    public int roundOrientation(int i, int i2) {
        boolean z = true;
        if (i2 != -1) {
            int abs = Math.abs(i - i2);
            if (Math.min(abs, 360 - abs) < 50) {
                z = false;
            }
        }
        return z ? (((i + 45) / 90) * 90) % 360 : i2;
    }

    public void setOptimizeForBarcode(boolean z) {
        this.optimizeForBarcode = z;
        configurePhotoCamera();
    }

    public void checkFlashMode(String str) {
        if (!CameraController.getInstance().availableFlashModes.contains(this.currentFlashMode)) {
            this.currentFlashMode = str;
            configurePhotoCamera();
            ApplicationLoader.applicationContext.getSharedPreferences("camera", 0).edit().putString(this.cameraInfo.frontCamera != 0 ? "flashMode_front" : "flashMode", str).commit();
        }
    }

    public void setCurrentFlashMode(String str) {
        this.currentFlashMode = str;
        configurePhotoCamera();
        ApplicationLoader.applicationContext.getSharedPreferences("camera", 0).edit().putString(this.cameraInfo.frontCamera != 0 ? "flashMode_front" : "flashMode", str).commit();
    }

    public void setTorchEnabled(boolean z) {
        try {
            this.currentFlashMode = z ? "torch" : "off";
            configurePhotoCamera();
        } catch (Exception e) {
            FileLog.e((Throwable) e);
        }
    }

    public String getCurrentFlashMode() {
        return this.currentFlashMode;
    }

    public String getNextFlashMode() {
        ArrayList<String> arrayList = CameraController.getInstance().availableFlashModes;
        int i = 0;
        while (i < arrayList.size()) {
            if (!arrayList.get(i).equals(this.currentFlashMode)) {
                i++;
            } else if (i < arrayList.size() - 1) {
                return arrayList.get(i + 1);
            } else {
                return arrayList.get(0);
            }
        }
        return this.currentFlashMode;
    }

    public void setInitied() {
        this.initied = true;
    }

    public boolean isInitied() {
        return this.initied;
    }

    public int getCurrentOrientation() {
        return this.currentOrientation;
    }

    public boolean isFlipFront() {
        return this.flipFront;
    }

    public void setFlipFront(boolean z) {
        this.flipFront = z;
    }

    public int getWorldAngle() {
        return this.diffOrientation;
    }

    public boolean isSameTakePictureOrientation() {
        return this.sameTakePictureOrientation;
    }

    /* access modifiers changed from: protected */
    /* JADX WARNING: Removed duplicated region for block: B:29:0x0058 A[Catch:{ Exception -> 0x016a, Exception -> 0x0014, all -> 0x0171 }] */
    /* JADX WARNING: Removed duplicated region for block: B:32:0x005e A[Catch:{ Exception -> 0x016a, Exception -> 0x0014, all -> 0x0171 }] */
    /* JADX WARNING: Removed duplicated region for block: B:33:0x0068 A[Catch:{ Exception -> 0x016a, Exception -> 0x0014, all -> 0x0171 }] */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public void configureRoundCamera(boolean r10) {
        /*
            r9 = this;
            r0 = 1
            r9.isVideo = r0     // Catch:{ all -> 0x0171 }
            org.telegram.messenger.camera.CameraInfo r1 = r9.cameraInfo     // Catch:{ all -> 0x0171 }
            android.hardware.Camera r1 = r1.camera     // Catch:{ all -> 0x0171 }
            if (r1 == 0) goto L_0x0175
            android.hardware.Camera$CameraInfo r2 = new android.hardware.Camera$CameraInfo     // Catch:{ all -> 0x0171 }
            r2.<init>()     // Catch:{ all -> 0x0171 }
            r3 = 0
            android.hardware.Camera$Parameters r3 = r1.getParameters()     // Catch:{ Exception -> 0x0014 }
            goto L_0x0018
        L_0x0014:
            r4 = move-exception
            org.telegram.messenger.FileLog.e((java.lang.Throwable) r4)     // Catch:{ all -> 0x0171 }
        L_0x0018:
            org.telegram.messenger.camera.CameraInfo r4 = r9.cameraInfo     // Catch:{ all -> 0x0171 }
            int r4 = r4.getCameraId()     // Catch:{ all -> 0x0171 }
            android.hardware.Camera.getCameraInfo(r4, r2)     // Catch:{ all -> 0x0171 }
            int r4 = r9.getDisplayOrientation(r2, r0)     // Catch:{ all -> 0x0171 }
            java.lang.String r5 = "samsung"
            java.lang.String r6 = android.os.Build.MANUFACTURER     // Catch:{ all -> 0x0171 }
            boolean r5 = r5.equals(r6)     // Catch:{ all -> 0x0171 }
            r6 = 0
            if (r5 == 0) goto L_0x003d
            java.lang.String r5 = "sf2wifixx"
            java.lang.String r7 = android.os.Build.PRODUCT     // Catch:{ all -> 0x0171 }
            boolean r5 = r5.equals(r7)     // Catch:{ all -> 0x0171 }
            if (r5 == 0) goto L_0x003d
            r5 = 0
            goto L_0x006f
        L_0x003d:
            r5 = 90
            if (r4 == 0) goto L_0x0049
            if (r4 == r0) goto L_0x0051
            r7 = 2
            if (r4 == r7) goto L_0x004e
            r7 = 3
            if (r4 == r7) goto L_0x004b
        L_0x0049:
            r7 = 0
            goto L_0x0053
        L_0x004b:
            r7 = 270(0x10e, float:3.78E-43)
            goto L_0x0053
        L_0x004e:
            r7 = 180(0xb4, float:2.52E-43)
            goto L_0x0053
        L_0x0051:
            r7 = 90
        L_0x0053:
            int r8 = r2.orientation     // Catch:{ all -> 0x0171 }
            int r8 = r8 % r5
            if (r8 == 0) goto L_0x005a
            r2.orientation = r6     // Catch:{ all -> 0x0171 }
        L_0x005a:
            int r5 = r2.facing     // Catch:{ all -> 0x0171 }
            if (r5 != r0) goto L_0x0068
            int r5 = r2.orientation     // Catch:{ all -> 0x0171 }
            int r5 = r5 + r7
            int r5 = r5 % 360
            int r5 = 360 - r5
            int r5 = r5 % 360
            goto L_0x006f
        L_0x0068:
            int r5 = r2.orientation     // Catch:{ all -> 0x0171 }
            int r5 = r5 - r7
            int r5 = r5 + 360
            int r5 = r5 % 360
        L_0x006f:
            r9.currentOrientation = r5     // Catch:{ all -> 0x0171 }
            r1.setDisplayOrientation(r5)     // Catch:{ all -> 0x0171 }
            int r5 = r9.currentOrientation     // Catch:{ all -> 0x0171 }
            int r5 = r5 - r4
            r9.diffOrientation = r5     // Catch:{ all -> 0x0171 }
            if (r3 == 0) goto L_0x0175
            java.lang.String r5 = " "
            if (r10 == 0) goto L_0x00a9
            boolean r7 = org.telegram.messenger.BuildVars.LOGS_ENABLED     // Catch:{ all -> 0x0171 }
            if (r7 == 0) goto L_0x00a9
            java.lang.StringBuilder r7 = new java.lang.StringBuilder     // Catch:{ all -> 0x0171 }
            r7.<init>()     // Catch:{ all -> 0x0171 }
            java.lang.String r8 = "set preview size = "
            r7.append(r8)     // Catch:{ all -> 0x0171 }
            org.telegram.messenger.camera.Size r8 = r9.previewSize     // Catch:{ all -> 0x0171 }
            int r8 = r8.getWidth()     // Catch:{ all -> 0x0171 }
            r7.append(r8)     // Catch:{ all -> 0x0171 }
            r7.append(r5)     // Catch:{ all -> 0x0171 }
            org.telegram.messenger.camera.Size r8 = r9.previewSize     // Catch:{ all -> 0x0171 }
            int r8 = r8.getHeight()     // Catch:{ all -> 0x0171 }
            r7.append(r8)     // Catch:{ all -> 0x0171 }
            java.lang.String r7 = r7.toString()     // Catch:{ all -> 0x0171 }
            org.telegram.messenger.FileLog.d(r7)     // Catch:{ all -> 0x0171 }
        L_0x00a9:
            org.telegram.messenger.camera.Size r7 = r9.previewSize     // Catch:{ all -> 0x0171 }
            int r7 = r7.getWidth()     // Catch:{ all -> 0x0171 }
            org.telegram.messenger.camera.Size r8 = r9.previewSize     // Catch:{ all -> 0x0171 }
            int r8 = r8.getHeight()     // Catch:{ all -> 0x0171 }
            r3.setPreviewSize(r7, r8)     // Catch:{ all -> 0x0171 }
            if (r10 == 0) goto L_0x00e4
            boolean r10 = org.telegram.messenger.BuildVars.LOGS_ENABLED     // Catch:{ all -> 0x0171 }
            if (r10 == 0) goto L_0x00e4
            java.lang.StringBuilder r10 = new java.lang.StringBuilder     // Catch:{ all -> 0x0171 }
            r10.<init>()     // Catch:{ all -> 0x0171 }
            java.lang.String r7 = "set picture size = "
            r10.append(r7)     // Catch:{ all -> 0x0171 }
            org.telegram.messenger.camera.Size r7 = r9.pictureSize     // Catch:{ all -> 0x0171 }
            int r7 = r7.getWidth()     // Catch:{ all -> 0x0171 }
            r10.append(r7)     // Catch:{ all -> 0x0171 }
            r10.append(r5)     // Catch:{ all -> 0x0171 }
            org.telegram.messenger.camera.Size r5 = r9.pictureSize     // Catch:{ all -> 0x0171 }
            int r5 = r5.getHeight()     // Catch:{ all -> 0x0171 }
            r10.append(r5)     // Catch:{ all -> 0x0171 }
            java.lang.String r10 = r10.toString()     // Catch:{ all -> 0x0171 }
            org.telegram.messenger.FileLog.d(r10)     // Catch:{ all -> 0x0171 }
        L_0x00e4:
            org.telegram.messenger.camera.Size r10 = r9.pictureSize     // Catch:{ all -> 0x0171 }
            int r10 = r10.getWidth()     // Catch:{ all -> 0x0171 }
            org.telegram.messenger.camera.Size r5 = r9.pictureSize     // Catch:{ all -> 0x0171 }
            int r5 = r5.getHeight()     // Catch:{ all -> 0x0171 }
            r3.setPictureSize(r10, r5)     // Catch:{ all -> 0x0171 }
            int r10 = r9.pictureFormat     // Catch:{ all -> 0x0171 }
            r3.setPictureFormat(r10)     // Catch:{ all -> 0x0171 }
            r3.setRecordingHint(r0)     // Catch:{ all -> 0x0171 }
            int r10 = r3.getMaxZoom()     // Catch:{ all -> 0x0171 }
            r9.maxZoom = r10     // Catch:{ all -> 0x0171 }
            java.lang.String r10 = "continuous-video"
            java.util.List r5 = r3.getSupportedFocusModes()     // Catch:{ all -> 0x0171 }
            boolean r5 = r5.contains(r10)     // Catch:{ all -> 0x0171 }
            if (r5 == 0) goto L_0x0111
            r3.setFocusMode(r10)     // Catch:{ all -> 0x0171 }
            goto L_0x0120
        L_0x0111:
            java.lang.String r10 = "auto"
            java.util.List r5 = r3.getSupportedFocusModes()     // Catch:{ all -> 0x0171 }
            boolean r5 = r5.contains(r10)     // Catch:{ all -> 0x0171 }
            if (r5 == 0) goto L_0x0120
            r3.setFocusMode(r10)     // Catch:{ all -> 0x0171 }
        L_0x0120:
            int r10 = r9.jpegOrientation     // Catch:{ all -> 0x0171 }
            r5 = -1
            if (r10 == r5) goto L_0x0137
            int r5 = r2.facing     // Catch:{ all -> 0x0171 }
            if (r5 != r0) goto L_0x0131
            int r5 = r2.orientation     // Catch:{ all -> 0x0171 }
            int r5 = r5 - r10
            int r5 = r5 + 360
            int r5 = r5 % 360
            goto L_0x0138
        L_0x0131:
            int r5 = r2.orientation     // Catch:{ all -> 0x0171 }
            int r5 = r5 + r10
            int r5 = r5 % 360
            goto L_0x0138
        L_0x0137:
            r5 = 0
        L_0x0138:
            r3.setRotation(r5)     // Catch:{ Exception -> 0x014e }
            int r10 = r2.facing     // Catch:{ Exception -> 0x014e }
            if (r10 != r0) goto L_0x0149
            int r10 = 360 - r4
            int r10 = r10 % 360
            if (r10 != r5) goto L_0x0146
            r6 = 1
        L_0x0146:
            r9.sameTakePictureOrientation = r6     // Catch:{ Exception -> 0x014e }
            goto L_0x014e
        L_0x0149:
            if (r4 != r5) goto L_0x014c
            r6 = 1
        L_0x014c:
            r9.sameTakePictureOrientation = r6     // Catch:{ Exception -> 0x014e }
        L_0x014e:
            java.lang.String r10 = "off"
            r3.setFlashMode(r10)     // Catch:{ all -> 0x0171 }
            float r10 = r9.currentZoom     // Catch:{ all -> 0x0171 }
            int r2 = r9.maxZoom     // Catch:{ all -> 0x0171 }
            float r2 = (float) r2     // Catch:{ all -> 0x0171 }
            float r10 = r10 * r2
            int r10 = (int) r10     // Catch:{ all -> 0x0171 }
            r3.setZoom(r10)     // Catch:{ all -> 0x0171 }
            r1.setParameters(r3)     // Catch:{ Exception -> 0x016a }
            int r10 = r3.getMaxNumMeteringAreas()     // Catch:{ all -> 0x0171 }
            if (r10 <= 0) goto L_0x0175
            r9.meteringAreaSupported = r0     // Catch:{ all -> 0x0171 }
            goto L_0x0175
        L_0x016a:
            r10 = move-exception
            java.lang.RuntimeException r0 = new java.lang.RuntimeException     // Catch:{ all -> 0x0171 }
            r0.<init>(r10)     // Catch:{ all -> 0x0171 }
            throw r0     // Catch:{ all -> 0x0171 }
        L_0x0171:
            r10 = move-exception
            org.telegram.messenger.FileLog.e((java.lang.Throwable) r10)
        L_0x0175:
            return
        */
        throw new UnsupportedOperationException("Method not decompiled: org.telegram.messenger.camera.CameraSession.configureRoundCamera(boolean):void");
    }

    /* access modifiers changed from: protected */
    /* JADX WARNING: Can't wrap try/catch for region: R(12:36|(4:38|(1:42)|43|(1:45))(2:46|(1:48))|49|(2:51|(1:53)(1:54))(1:55)|56|57|(3:59|(1:61)(1:62)|63)(2:(1:65)(1:66)|67)|68|69|(1:71)(1:72)|73|(3:74|75|83)) */
    /* JADX WARNING: Missing exception handler attribute for start block: B:68:0x0117 */
    /* JADX WARNING: Removed duplicated region for block: B:29:0x0058 A[Catch:{ Exception -> 0x0013, all -> 0x0128 }] */
    /* JADX WARNING: Removed duplicated region for block: B:32:0x005e A[Catch:{ Exception -> 0x0013, all -> 0x0128 }] */
    /* JADX WARNING: Removed duplicated region for block: B:33:0x0068 A[Catch:{ Exception -> 0x0013, all -> 0x0128 }] */
    /* JADX WARNING: Removed duplicated region for block: B:71:0x011b A[Catch:{ Exception -> 0x0013, all -> 0x0128 }] */
    /* JADX WARNING: Removed duplicated region for block: B:72:0x011f A[Catch:{ Exception -> 0x0013, all -> 0x0128 }] */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public void configurePhotoCamera() {
        /*
            r10 = this;
            java.lang.String r0 = "barcode"
            org.telegram.messenger.camera.CameraInfo r1 = r10.cameraInfo     // Catch:{ all -> 0x0128 }
            android.hardware.Camera r1 = r1.camera     // Catch:{ all -> 0x0128 }
            if (r1 == 0) goto L_0x012c
            android.hardware.Camera$CameraInfo r2 = new android.hardware.Camera$CameraInfo     // Catch:{ all -> 0x0128 }
            r2.<init>()     // Catch:{ all -> 0x0128 }
            r3 = 0
            android.hardware.Camera$Parameters r3 = r1.getParameters()     // Catch:{ Exception -> 0x0013 }
            goto L_0x0017
        L_0x0013:
            r4 = move-exception
            org.telegram.messenger.FileLog.e((java.lang.Throwable) r4)     // Catch:{ all -> 0x0128 }
        L_0x0017:
            org.telegram.messenger.camera.CameraInfo r4 = r10.cameraInfo     // Catch:{ all -> 0x0128 }
            int r4 = r4.getCameraId()     // Catch:{ all -> 0x0128 }
            android.hardware.Camera.getCameraInfo(r4, r2)     // Catch:{ all -> 0x0128 }
            r4 = 1
            int r5 = r10.getDisplayOrientation(r2, r4)     // Catch:{ all -> 0x0128 }
            java.lang.String r6 = "samsung"
            java.lang.String r7 = android.os.Build.MANUFACTURER     // Catch:{ all -> 0x0128 }
            boolean r6 = r6.equals(r7)     // Catch:{ all -> 0x0128 }
            r7 = 0
            if (r6 == 0) goto L_0x003d
            java.lang.String r6 = "sf2wifixx"
            java.lang.String r8 = android.os.Build.PRODUCT     // Catch:{ all -> 0x0128 }
            boolean r6 = r6.equals(r8)     // Catch:{ all -> 0x0128 }
            if (r6 == 0) goto L_0x003d
            r6 = 0
            goto L_0x006f
        L_0x003d:
            r6 = 90
            if (r5 == 0) goto L_0x0049
            if (r5 == r4) goto L_0x0051
            r8 = 2
            if (r5 == r8) goto L_0x004e
            r8 = 3
            if (r5 == r8) goto L_0x004b
        L_0x0049:
            r8 = 0
            goto L_0x0053
        L_0x004b:
            r8 = 270(0x10e, float:3.78E-43)
            goto L_0x0053
        L_0x004e:
            r8 = 180(0xb4, float:2.52E-43)
            goto L_0x0053
        L_0x0051:
            r8 = 90
        L_0x0053:
            int r9 = r2.orientation     // Catch:{ all -> 0x0128 }
            int r9 = r9 % r6
            if (r9 == 0) goto L_0x005a
            r2.orientation = r7     // Catch:{ all -> 0x0128 }
        L_0x005a:
            int r6 = r2.facing     // Catch:{ all -> 0x0128 }
            if (r6 != r4) goto L_0x0068
            int r6 = r2.orientation     // Catch:{ all -> 0x0128 }
            int r6 = r6 + r8
            int r6 = r6 % 360
            int r6 = 360 - r6
            int r6 = r6 % 360
            goto L_0x006f
        L_0x0068:
            int r6 = r2.orientation     // Catch:{ all -> 0x0128 }
            int r6 = r6 - r8
            int r6 = r6 + 360
            int r6 = r6 % 360
        L_0x006f:
            r10.currentOrientation = r6     // Catch:{ all -> 0x0128 }
            r1.setDisplayOrientation(r6)     // Catch:{ all -> 0x0128 }
            int r6 = r10.currentOrientation     // Catch:{ all -> 0x0128 }
            int r6 = r6 - r5
            r10.diffOrientation = r6     // Catch:{ all -> 0x0128 }
            if (r3 == 0) goto L_0x012c
            org.telegram.messenger.camera.Size r6 = r10.previewSize     // Catch:{ all -> 0x0128 }
            int r6 = r6.getWidth()     // Catch:{ all -> 0x0128 }
            org.telegram.messenger.camera.Size r8 = r10.previewSize     // Catch:{ all -> 0x0128 }
            int r8 = r8.getHeight()     // Catch:{ all -> 0x0128 }
            r3.setPreviewSize(r6, r8)     // Catch:{ all -> 0x0128 }
            org.telegram.messenger.camera.Size r6 = r10.pictureSize     // Catch:{ all -> 0x0128 }
            int r6 = r6.getWidth()     // Catch:{ all -> 0x0128 }
            org.telegram.messenger.camera.Size r8 = r10.pictureSize     // Catch:{ all -> 0x0128 }
            int r8 = r8.getHeight()     // Catch:{ all -> 0x0128 }
            r3.setPictureSize(r6, r8)     // Catch:{ all -> 0x0128 }
            int r6 = r10.pictureFormat     // Catch:{ all -> 0x0128 }
            r3.setPictureFormat(r6)     // Catch:{ all -> 0x0128 }
            r6 = 100
            r3.setJpegQuality(r6)     // Catch:{ all -> 0x0128 }
            r3.setJpegThumbnailQuality(r6)     // Catch:{ all -> 0x0128 }
            int r6 = r3.getMaxZoom()     // Catch:{ all -> 0x0128 }
            r10.maxZoom = r6     // Catch:{ all -> 0x0128 }
            float r8 = r10.currentZoom     // Catch:{ all -> 0x0128 }
            float r6 = (float) r6     // Catch:{ all -> 0x0128 }
            float r8 = r8 * r6
            int r6 = (int) r8     // Catch:{ all -> 0x0128 }
            r3.setZoom(r6)     // Catch:{ all -> 0x0128 }
            boolean r6 = r10.optimizeForBarcode     // Catch:{ all -> 0x0128 }
            if (r6 == 0) goto L_0x00d8
            java.util.List r6 = r3.getSupportedSceneModes()     // Catch:{ all -> 0x0128 }
            if (r6 == 0) goto L_0x00c8
            boolean r6 = r6.contains(r0)     // Catch:{ all -> 0x0128 }
            if (r6 == 0) goto L_0x00c8
            r3.setSceneMode(r0)     // Catch:{ all -> 0x0128 }
        L_0x00c8:
            java.lang.String r0 = "continuous-video"
            java.util.List r6 = r3.getSupportedFocusModes()     // Catch:{ all -> 0x0128 }
            boolean r6 = r6.contains(r0)     // Catch:{ all -> 0x0128 }
            if (r6 == 0) goto L_0x00e7
            r3.setFocusMode(r0)     // Catch:{ all -> 0x0128 }
            goto L_0x00e7
        L_0x00d8:
            java.lang.String r0 = "continuous-picture"
            java.util.List r6 = r3.getSupportedFocusModes()     // Catch:{ all -> 0x0128 }
            boolean r6 = r6.contains(r0)     // Catch:{ all -> 0x0128 }
            if (r6 == 0) goto L_0x00e7
            r3.setFocusMode(r0)     // Catch:{ all -> 0x0128 }
        L_0x00e7:
            int r0 = r10.jpegOrientation     // Catch:{ all -> 0x0128 }
            r6 = -1
            if (r0 == r6) goto L_0x00fe
            int r6 = r2.facing     // Catch:{ all -> 0x0128 }
            if (r6 != r4) goto L_0x00f8
            int r6 = r2.orientation     // Catch:{ all -> 0x0128 }
            int r6 = r6 - r0
            int r6 = r6 + 360
            int r6 = r6 % 360
            goto L_0x00ff
        L_0x00f8:
            int r6 = r2.orientation     // Catch:{ all -> 0x0128 }
            int r6 = r6 + r0
            int r6 = r6 % 360
            goto L_0x00ff
        L_0x00fe:
            r6 = 0
        L_0x00ff:
            r3.setRotation(r6)     // Catch:{ Exception -> 0x0117 }
            int r0 = r2.facing     // Catch:{ Exception -> 0x0117 }
            if (r0 != r4) goto L_0x0111
            int r0 = 360 - r5
            int r0 = r0 % 360
            if (r0 != r6) goto L_0x010d
            goto L_0x010e
        L_0x010d:
            r4 = 0
        L_0x010e:
            r10.sameTakePictureOrientation = r4     // Catch:{ Exception -> 0x0117 }
            goto L_0x0117
        L_0x0111:
            if (r5 != r6) goto L_0x0114
            goto L_0x0115
        L_0x0114:
            r4 = 0
        L_0x0115:
            r10.sameTakePictureOrientation = r4     // Catch:{ Exception -> 0x0117 }
        L_0x0117:
            boolean r0 = r10.useTorch     // Catch:{ all -> 0x0128 }
            if (r0 == 0) goto L_0x011f
            java.lang.String r0 = "torch"
            goto L_0x0121
        L_0x011f:
            java.lang.String r0 = r10.currentFlashMode     // Catch:{ all -> 0x0128 }
        L_0x0121:
            r3.setFlashMode(r0)     // Catch:{ all -> 0x0128 }
            r1.setParameters(r3)     // Catch:{ Exception -> 0x012c }
            goto L_0x012c
        L_0x0128:
            r0 = move-exception
            org.telegram.messenger.FileLog.e((java.lang.Throwable) r0)
        L_0x012c:
            return
        */
        throw new UnsupportedOperationException("Method not decompiled: org.telegram.messenger.camera.CameraSession.configurePhotoCamera():void");
    }

    /* access modifiers changed from: protected */
    public void focusToRect(Rect rect, Rect rect2) {
        try {
            Camera camera = this.cameraInfo.camera;
            if (camera != null) {
                camera.cancelAutoFocus();
                Camera.Parameters parameters = null;
                try {
                    parameters = camera.getParameters();
                } catch (Exception e) {
                    FileLog.e((Throwable) e);
                }
                if (parameters != null) {
                    parameters.setFocusMode("auto");
                    ArrayList arrayList = new ArrayList();
                    arrayList.add(new Camera.Area(rect, 1000));
                    parameters.setFocusAreas(arrayList);
                    if (this.meteringAreaSupported) {
                        ArrayList arrayList2 = new ArrayList();
                        arrayList2.add(new Camera.Area(rect2, 1000));
                        parameters.setMeteringAreas(arrayList2);
                    }
                    try {
                        camera.setParameters(parameters);
                        camera.autoFocus(this.autoFocusCallback);
                    } catch (Exception e2) {
                        FileLog.e((Throwable) e2);
                    }
                }
            }
        } catch (Exception e3) {
            FileLog.e((Throwable) e3);
        }
    }

    /* access modifiers changed from: protected */
    public int getMaxZoom() {
        return this.maxZoom;
    }

    public void onStartRecord() {
        this.isVideo = true;
    }

    public void setZoom(float f) {
        this.currentZoom = f;
        if (this.isVideo && "on".equals(this.currentFlashMode)) {
            this.useTorch = true;
        }
        if (this.isRound) {
            configureRoundCamera(false);
        } else {
            configurePhotoCamera();
        }
    }

    /* access modifiers changed from: protected */
    public void configureRecorder(int i, MediaRecorder mediaRecorder) {
        int i2;
        Camera.CameraInfo cameraInfo2 = new Camera.CameraInfo();
        Camera.getCameraInfo(this.cameraInfo.cameraId, cameraInfo2);
        getDisplayOrientation(cameraInfo2, false);
        int i3 = this.jpegOrientation;
        if (i3 != -1) {
            i2 = cameraInfo2.facing == 1 ? ((cameraInfo2.orientation - i3) + 360) % 360 : (cameraInfo2.orientation + i3) % 360;
        } else {
            i2 = 0;
        }
        mediaRecorder.setOrientationHint(i2);
        int high = getHigh();
        boolean hasProfile = CamcorderProfile.hasProfile(this.cameraInfo.cameraId, high);
        boolean hasProfile2 = CamcorderProfile.hasProfile(this.cameraInfo.cameraId, 0);
        if (hasProfile && (i == 1 || !hasProfile2)) {
            mediaRecorder.setProfile(CamcorderProfile.get(this.cameraInfo.cameraId, high));
        } else if (hasProfile2) {
            mediaRecorder.setProfile(CamcorderProfile.get(this.cameraInfo.cameraId, 0));
        } else {
            throw new IllegalStateException("cannot find valid CamcorderProfile");
        }
        this.isVideo = true;
    }

    /* access modifiers changed from: protected */
    public void stopVideoRecording() {
        this.isVideo = false;
        this.useTorch = false;
        configurePhotoCamera();
    }

    private int getHigh() {
        return (!"LGE".equals(Build.MANUFACTURER) || !"g3_tmo_us".equals(Build.PRODUCT)) ? 1 : 4;
    }

    private int getDisplayOrientation(Camera.CameraInfo cameraInfo2, boolean z) {
        int rotation = ((WindowManager) ApplicationLoader.applicationContext.getSystemService("window")).getDefaultDisplay().getRotation();
        int i = 0;
        if (rotation != 0) {
            if (rotation == 1) {
                i = 90;
            } else if (rotation == 2) {
                i = 180;
            } else if (rotation == 3) {
                i = 270;
            }
        }
        if (cameraInfo2.facing != 1) {
            return ((cameraInfo2.orientation - i) + 360) % 360;
        }
        int i2 = (360 - ((cameraInfo2.orientation + i) % 360)) % 360;
        if (!z && i2 == 90) {
            i2 = 270;
        }
        if (z || !"Huawei".equals(Build.MANUFACTURER) || !"angler".equals(Build.PRODUCT) || i2 != 270) {
            return i2;
        }
        return 90;
    }

    public int getDisplayOrientation() {
        try {
            Camera.CameraInfo cameraInfo2 = new Camera.CameraInfo();
            Camera.getCameraInfo(this.cameraInfo.getCameraId(), cameraInfo2);
            return getDisplayOrientation(cameraInfo2, true);
        } catch (Exception e) {
            FileLog.e((Throwable) e);
            return 0;
        }
    }

    public void setPreviewCallback(Camera.PreviewCallback previewCallback) {
        this.cameraInfo.camera.setPreviewCallback(previewCallback);
    }

    public void setOneShotPreviewCallback(Camera.PreviewCallback previewCallback) {
        Camera camera;
        CameraInfo cameraInfo2 = this.cameraInfo;
        if (cameraInfo2 != null && (camera = cameraInfo2.camera) != null) {
            try {
                camera.setOneShotPreviewCallback(previewCallback);
            } catch (Exception unused) {
            }
        }
    }

    public void destroy() {
        this.initied = false;
        OrientationEventListener orientationEventListener2 = this.orientationEventListener;
        if (orientationEventListener2 != null) {
            orientationEventListener2.disable();
            this.orientationEventListener = null;
        }
    }
}
