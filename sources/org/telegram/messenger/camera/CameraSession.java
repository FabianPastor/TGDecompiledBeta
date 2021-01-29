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

    static /* synthetic */ void lambda$new$0(boolean z, Camera camera) {
    }

    public CameraSession(CameraInfo cameraInfo2, Size size, Size size2, int i) {
        this.previewSize = size;
        this.pictureSize = size2;
        this.pictureFormat = i;
        this.cameraInfo = cameraInfo2;
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
    /* JADX WARNING: Can't wrap try/catch for region: R(18:36|37|(2:39|40)|41|(1:43)|44|(1:46)(2:47|(1:49))|50|(2:52|(1:54)(1:55))(1:56)|(3:57|58|(3:60|(1:62)|63)(2:(1:65)|66))|67|69|70|71|72|73|74|(2:76|83)(1:82)) */
    /* JADX WARNING: Missing exception handler attribute for start block: B:73:0x014b */
    /* JADX WARNING: Removed duplicated region for block: B:29:0x0057 A[Catch:{ Exception -> 0x0014, all -> 0x0154 }] */
    /* JADX WARNING: Removed duplicated region for block: B:32:0x005d A[Catch:{ Exception -> 0x0014, all -> 0x0154 }] */
    /* JADX WARNING: Removed duplicated region for block: B:33:0x0067 A[Catch:{ Exception -> 0x0014, all -> 0x0154 }] */
    /* JADX WARNING: Removed duplicated region for block: B:76:0x0151 A[Catch:{ Exception -> 0x0014, all -> 0x0154 }] */
    /* JADX WARNING: Removed duplicated region for block: B:82:? A[RETURN, SYNTHETIC] */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public void configureRoundCamera() {
        /*
            r9 = this;
            r0 = 1
            r9.isVideo = r0     // Catch:{ all -> 0x0154 }
            org.telegram.messenger.camera.CameraInfo r1 = r9.cameraInfo     // Catch:{ all -> 0x0154 }
            android.hardware.Camera r1 = r1.camera     // Catch:{ all -> 0x0154 }
            if (r1 == 0) goto L_0x0158
            android.hardware.Camera$CameraInfo r2 = new android.hardware.Camera$CameraInfo     // Catch:{ all -> 0x0154 }
            r2.<init>()     // Catch:{ all -> 0x0154 }
            r3 = 0
            android.hardware.Camera$Parameters r3 = r1.getParameters()     // Catch:{ Exception -> 0x0014 }
            goto L_0x0018
        L_0x0014:
            r4 = move-exception
            org.telegram.messenger.FileLog.e((java.lang.Throwable) r4)     // Catch:{ all -> 0x0154 }
        L_0x0018:
            org.telegram.messenger.camera.CameraInfo r4 = r9.cameraInfo     // Catch:{ all -> 0x0154 }
            int r4 = r4.getCameraId()     // Catch:{ all -> 0x0154 }
            android.hardware.Camera.getCameraInfo(r4, r2)     // Catch:{ all -> 0x0154 }
            int r4 = r9.getDisplayOrientation(r2, r0)     // Catch:{ all -> 0x0154 }
            java.lang.String r5 = "samsung"
            java.lang.String r6 = android.os.Build.MANUFACTURER     // Catch:{ all -> 0x0154 }
            boolean r5 = r5.equals(r6)     // Catch:{ all -> 0x0154 }
            r6 = 0
            if (r5 == 0) goto L_0x003c
            java.lang.String r5 = "sf2wifixx"
            java.lang.String r7 = android.os.Build.PRODUCT     // Catch:{ all -> 0x0154 }
            boolean r5 = r5.equals(r7)     // Catch:{ all -> 0x0154 }
            if (r5 == 0) goto L_0x003c
            r5 = 0
            goto L_0x006e
        L_0x003c:
            r5 = 90
            if (r4 == 0) goto L_0x0048
            if (r4 == r0) goto L_0x0050
            r7 = 2
            if (r4 == r7) goto L_0x004d
            r7 = 3
            if (r4 == r7) goto L_0x004a
        L_0x0048:
            r7 = 0
            goto L_0x0052
        L_0x004a:
            r7 = 270(0x10e, float:3.78E-43)
            goto L_0x0052
        L_0x004d:
            r7 = 180(0xb4, float:2.52E-43)
            goto L_0x0052
        L_0x0050:
            r7 = 90
        L_0x0052:
            int r8 = r2.orientation     // Catch:{ all -> 0x0154 }
            int r8 = r8 % r5
            if (r8 == 0) goto L_0x0059
            r2.orientation = r6     // Catch:{ all -> 0x0154 }
        L_0x0059:
            int r5 = r2.facing     // Catch:{ all -> 0x0154 }
            if (r5 != r0) goto L_0x0067
            int r5 = r2.orientation     // Catch:{ all -> 0x0154 }
            int r5 = r5 + r7
            int r5 = r5 % 360
            int r5 = 360 - r5
            int r5 = r5 % 360
            goto L_0x006e
        L_0x0067:
            int r5 = r2.orientation     // Catch:{ all -> 0x0154 }
            int r5 = r5 - r7
            int r5 = r5 + 360
            int r5 = r5 % 360
        L_0x006e:
            r9.currentOrientation = r5     // Catch:{ all -> 0x0154 }
            r1.setDisplayOrientation(r5)     // Catch:{ all -> 0x0154 }
            int r5 = r9.currentOrientation     // Catch:{ all -> 0x0154 }
            int r5 = r5 - r4
            r9.diffOrientation = r5     // Catch:{ all -> 0x0154 }
            if (r3 == 0) goto L_0x0158
            boolean r5 = org.telegram.messenger.BuildVars.LOGS_ENABLED     // Catch:{ all -> 0x0154 }
            java.lang.String r7 = " "
            if (r5 == 0) goto L_0x00a6
            java.lang.StringBuilder r5 = new java.lang.StringBuilder     // Catch:{ all -> 0x0154 }
            r5.<init>()     // Catch:{ all -> 0x0154 }
            java.lang.String r8 = "set preview size = "
            r5.append(r8)     // Catch:{ all -> 0x0154 }
            org.telegram.messenger.camera.Size r8 = r9.previewSize     // Catch:{ all -> 0x0154 }
            int r8 = r8.getWidth()     // Catch:{ all -> 0x0154 }
            r5.append(r8)     // Catch:{ all -> 0x0154 }
            r5.append(r7)     // Catch:{ all -> 0x0154 }
            org.telegram.messenger.camera.Size r8 = r9.previewSize     // Catch:{ all -> 0x0154 }
            int r8 = r8.getHeight()     // Catch:{ all -> 0x0154 }
            r5.append(r8)     // Catch:{ all -> 0x0154 }
            java.lang.String r5 = r5.toString()     // Catch:{ all -> 0x0154 }
            org.telegram.messenger.FileLog.d(r5)     // Catch:{ all -> 0x0154 }
        L_0x00a6:
            org.telegram.messenger.camera.Size r5 = r9.previewSize     // Catch:{ all -> 0x0154 }
            int r5 = r5.getWidth()     // Catch:{ all -> 0x0154 }
            org.telegram.messenger.camera.Size r8 = r9.previewSize     // Catch:{ all -> 0x0154 }
            int r8 = r8.getHeight()     // Catch:{ all -> 0x0154 }
            r3.setPreviewSize(r5, r8)     // Catch:{ all -> 0x0154 }
            boolean r5 = org.telegram.messenger.BuildVars.LOGS_ENABLED     // Catch:{ all -> 0x0154 }
            if (r5 == 0) goto L_0x00df
            java.lang.StringBuilder r5 = new java.lang.StringBuilder     // Catch:{ all -> 0x0154 }
            r5.<init>()     // Catch:{ all -> 0x0154 }
            java.lang.String r8 = "set picture size = "
            r5.append(r8)     // Catch:{ all -> 0x0154 }
            org.telegram.messenger.camera.Size r8 = r9.pictureSize     // Catch:{ all -> 0x0154 }
            int r8 = r8.getWidth()     // Catch:{ all -> 0x0154 }
            r5.append(r8)     // Catch:{ all -> 0x0154 }
            r5.append(r7)     // Catch:{ all -> 0x0154 }
            org.telegram.messenger.camera.Size r7 = r9.pictureSize     // Catch:{ all -> 0x0154 }
            int r7 = r7.getHeight()     // Catch:{ all -> 0x0154 }
            r5.append(r7)     // Catch:{ all -> 0x0154 }
            java.lang.String r5 = r5.toString()     // Catch:{ all -> 0x0154 }
            org.telegram.messenger.FileLog.d(r5)     // Catch:{ all -> 0x0154 }
        L_0x00df:
            org.telegram.messenger.camera.Size r5 = r9.pictureSize     // Catch:{ all -> 0x0154 }
            int r5 = r5.getWidth()     // Catch:{ all -> 0x0154 }
            org.telegram.messenger.camera.Size r7 = r9.pictureSize     // Catch:{ all -> 0x0154 }
            int r7 = r7.getHeight()     // Catch:{ all -> 0x0154 }
            r3.setPictureSize(r5, r7)     // Catch:{ all -> 0x0154 }
            int r5 = r9.pictureFormat     // Catch:{ all -> 0x0154 }
            r3.setPictureFormat(r5)     // Catch:{ all -> 0x0154 }
            r3.setRecordingHint(r0)     // Catch:{ all -> 0x0154 }
            java.lang.String r5 = "continuous-video"
            java.util.List r7 = r3.getSupportedFocusModes()     // Catch:{ all -> 0x0154 }
            boolean r7 = r7.contains(r5)     // Catch:{ all -> 0x0154 }
            if (r7 == 0) goto L_0x0106
            r3.setFocusMode(r5)     // Catch:{ all -> 0x0154 }
            goto L_0x0115
        L_0x0106:
            java.lang.String r5 = "auto"
            java.util.List r7 = r3.getSupportedFocusModes()     // Catch:{ all -> 0x0154 }
            boolean r7 = r7.contains(r5)     // Catch:{ all -> 0x0154 }
            if (r7 == 0) goto L_0x0115
            r3.setFocusMode(r5)     // Catch:{ all -> 0x0154 }
        L_0x0115:
            int r5 = r9.jpegOrientation     // Catch:{ all -> 0x0154 }
            r7 = -1
            if (r5 == r7) goto L_0x012c
            int r7 = r2.facing     // Catch:{ all -> 0x0154 }
            if (r7 != r0) goto L_0x0126
            int r7 = r2.orientation     // Catch:{ all -> 0x0154 }
            int r7 = r7 - r5
            int r7 = r7 + 360
            int r7 = r7 % 360
            goto L_0x012d
        L_0x0126:
            int r7 = r2.orientation     // Catch:{ all -> 0x0154 }
            int r7 = r7 + r5
            int r7 = r7 % 360
            goto L_0x012d
        L_0x012c:
            r7 = 0
        L_0x012d:
            r3.setRotation(r7)     // Catch:{ Exception -> 0x0143 }
            int r2 = r2.facing     // Catch:{ Exception -> 0x0143 }
            if (r2 != r0) goto L_0x013e
            int r2 = 360 - r4
            int r2 = r2 % 360
            if (r2 != r7) goto L_0x013b
            r6 = 1
        L_0x013b:
            r9.sameTakePictureOrientation = r6     // Catch:{ Exception -> 0x0143 }
            goto L_0x0143
        L_0x013e:
            if (r4 != r7) goto L_0x0141
            r6 = 1
        L_0x0141:
            r9.sameTakePictureOrientation = r6     // Catch:{ Exception -> 0x0143 }
        L_0x0143:
            java.lang.String r2 = "off"
            r3.setFlashMode(r2)     // Catch:{ all -> 0x0154 }
            r1.setParameters(r3)     // Catch:{ Exception -> 0x014b }
        L_0x014b:
            int r1 = r3.getMaxNumMeteringAreas()     // Catch:{ all -> 0x0154 }
            if (r1 <= 0) goto L_0x0158
            r9.meteringAreaSupported = r0     // Catch:{ all -> 0x0154 }
            goto L_0x0158
        L_0x0154:
            r0 = move-exception
            org.telegram.messenger.FileLog.e((java.lang.Throwable) r0)
        L_0x0158:
            return
        */
        throw new UnsupportedOperationException("Method not decompiled: org.telegram.messenger.camera.CameraSession.configureRoundCamera():void");
    }

    /* access modifiers changed from: protected */
    /* JADX WARNING: Can't wrap try/catch for region: R(14:36|(4:38|(1:42)|43|(1:45))(2:46|(1:48))|49|(2:51|(1:53)(1:54))(1:55)|56|57|(3:59|(1:61)|62)(2:(1:64)|65)|66|67|68|69|70|71|(2:73|80)(1:79)) */
    /* JADX WARNING: Exception block dominator not found, dom blocks: [] */
    /* JADX WARNING: Missing exception handler attribute for start block: B:66:0x010f */
    /* JADX WARNING: Missing exception handler attribute for start block: B:70:0x0117 */
    /* JADX WARNING: Removed duplicated region for block: B:29:0x0057 A[Catch:{ Exception -> 0x0013, all -> 0x0120 }] */
    /* JADX WARNING: Removed duplicated region for block: B:32:0x005d A[Catch:{ Exception -> 0x0013, all -> 0x0120 }] */
    /* JADX WARNING: Removed duplicated region for block: B:33:0x0067 A[Catch:{ Exception -> 0x0013, all -> 0x0120 }] */
    /* JADX WARNING: Removed duplicated region for block: B:73:0x011d A[Catch:{ Exception -> 0x0013, all -> 0x0120 }] */
    /* JADX WARNING: Removed duplicated region for block: B:79:? A[RETURN, SYNTHETIC] */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public void configurePhotoCamera() {
        /*
            r10 = this;
            java.lang.String r0 = "barcode"
            org.telegram.messenger.camera.CameraInfo r1 = r10.cameraInfo     // Catch:{ all -> 0x0120 }
            android.hardware.Camera r1 = r1.camera     // Catch:{ all -> 0x0120 }
            if (r1 == 0) goto L_0x0124
            android.hardware.Camera$CameraInfo r2 = new android.hardware.Camera$CameraInfo     // Catch:{ all -> 0x0120 }
            r2.<init>()     // Catch:{ all -> 0x0120 }
            r3 = 0
            android.hardware.Camera$Parameters r3 = r1.getParameters()     // Catch:{ Exception -> 0x0013 }
            goto L_0x0017
        L_0x0013:
            r4 = move-exception
            org.telegram.messenger.FileLog.e((java.lang.Throwable) r4)     // Catch:{ all -> 0x0120 }
        L_0x0017:
            org.telegram.messenger.camera.CameraInfo r4 = r10.cameraInfo     // Catch:{ all -> 0x0120 }
            int r4 = r4.getCameraId()     // Catch:{ all -> 0x0120 }
            android.hardware.Camera.getCameraInfo(r4, r2)     // Catch:{ all -> 0x0120 }
            r4 = 1
            int r5 = r10.getDisplayOrientation(r2, r4)     // Catch:{ all -> 0x0120 }
            java.lang.String r6 = "samsung"
            java.lang.String r7 = android.os.Build.MANUFACTURER     // Catch:{ all -> 0x0120 }
            boolean r6 = r6.equals(r7)     // Catch:{ all -> 0x0120 }
            r7 = 0
            if (r6 == 0) goto L_0x003c
            java.lang.String r6 = "sf2wifixx"
            java.lang.String r8 = android.os.Build.PRODUCT     // Catch:{ all -> 0x0120 }
            boolean r6 = r6.equals(r8)     // Catch:{ all -> 0x0120 }
            if (r6 == 0) goto L_0x003c
            r6 = 0
            goto L_0x006e
        L_0x003c:
            r6 = 90
            if (r5 == 0) goto L_0x0048
            if (r5 == r4) goto L_0x0050
            r8 = 2
            if (r5 == r8) goto L_0x004d
            r8 = 3
            if (r5 == r8) goto L_0x004a
        L_0x0048:
            r8 = 0
            goto L_0x0052
        L_0x004a:
            r8 = 270(0x10e, float:3.78E-43)
            goto L_0x0052
        L_0x004d:
            r8 = 180(0xb4, float:2.52E-43)
            goto L_0x0052
        L_0x0050:
            r8 = 90
        L_0x0052:
            int r9 = r2.orientation     // Catch:{ all -> 0x0120 }
            int r9 = r9 % r6
            if (r9 == 0) goto L_0x0059
            r2.orientation = r7     // Catch:{ all -> 0x0120 }
        L_0x0059:
            int r6 = r2.facing     // Catch:{ all -> 0x0120 }
            if (r6 != r4) goto L_0x0067
            int r6 = r2.orientation     // Catch:{ all -> 0x0120 }
            int r6 = r6 + r8
            int r6 = r6 % 360
            int r6 = 360 - r6
            int r6 = r6 % 360
            goto L_0x006e
        L_0x0067:
            int r6 = r2.orientation     // Catch:{ all -> 0x0120 }
            int r6 = r6 - r8
            int r6 = r6 + 360
            int r6 = r6 % 360
        L_0x006e:
            r10.currentOrientation = r6     // Catch:{ all -> 0x0120 }
            r1.setDisplayOrientation(r6)     // Catch:{ all -> 0x0120 }
            if (r3 == 0) goto L_0x0124
            org.telegram.messenger.camera.Size r6 = r10.previewSize     // Catch:{ all -> 0x0120 }
            int r6 = r6.getWidth()     // Catch:{ all -> 0x0120 }
            org.telegram.messenger.camera.Size r8 = r10.previewSize     // Catch:{ all -> 0x0120 }
            int r8 = r8.getHeight()     // Catch:{ all -> 0x0120 }
            r3.setPreviewSize(r6, r8)     // Catch:{ all -> 0x0120 }
            org.telegram.messenger.camera.Size r6 = r10.pictureSize     // Catch:{ all -> 0x0120 }
            int r6 = r6.getWidth()     // Catch:{ all -> 0x0120 }
            org.telegram.messenger.camera.Size r8 = r10.pictureSize     // Catch:{ all -> 0x0120 }
            int r8 = r8.getHeight()     // Catch:{ all -> 0x0120 }
            r3.setPictureSize(r6, r8)     // Catch:{ all -> 0x0120 }
            int r6 = r10.pictureFormat     // Catch:{ all -> 0x0120 }
            r3.setPictureFormat(r6)     // Catch:{ all -> 0x0120 }
            r6 = 100
            r3.setJpegQuality(r6)     // Catch:{ all -> 0x0120 }
            r3.setJpegThumbnailQuality(r6)     // Catch:{ all -> 0x0120 }
            int r6 = r3.getMaxZoom()     // Catch:{ all -> 0x0120 }
            r10.maxZoom = r6     // Catch:{ all -> 0x0120 }
            float r8 = r10.currentZoom     // Catch:{ all -> 0x0120 }
            float r6 = (float) r6     // Catch:{ all -> 0x0120 }
            float r8 = r8 * r6
            int r6 = (int) r8     // Catch:{ all -> 0x0120 }
            r3.setZoom(r6)     // Catch:{ all -> 0x0120 }
            boolean r6 = r10.optimizeForBarcode     // Catch:{ all -> 0x0120 }
            if (r6 == 0) goto L_0x00d2
            java.util.List r6 = r3.getSupportedSceneModes()     // Catch:{ all -> 0x0120 }
            if (r6 == 0) goto L_0x00c2
            boolean r6 = r6.contains(r0)     // Catch:{ all -> 0x0120 }
            if (r6 == 0) goto L_0x00c2
            r3.setSceneMode(r0)     // Catch:{ all -> 0x0120 }
        L_0x00c2:
            java.lang.String r0 = "continuous-video"
            java.util.List r6 = r3.getSupportedFocusModes()     // Catch:{ all -> 0x0120 }
            boolean r6 = r6.contains(r0)     // Catch:{ all -> 0x0120 }
            if (r6 == 0) goto L_0x00e1
            r3.setFocusMode(r0)     // Catch:{ all -> 0x0120 }
            goto L_0x00e1
        L_0x00d2:
            java.lang.String r0 = "continuous-picture"
            java.util.List r6 = r3.getSupportedFocusModes()     // Catch:{ all -> 0x0120 }
            boolean r6 = r6.contains(r0)     // Catch:{ all -> 0x0120 }
            if (r6 == 0) goto L_0x00e1
            r3.setFocusMode(r0)     // Catch:{ all -> 0x0120 }
        L_0x00e1:
            int r0 = r10.jpegOrientation     // Catch:{ all -> 0x0120 }
            r6 = -1
            if (r0 == r6) goto L_0x00f8
            int r6 = r2.facing     // Catch:{ all -> 0x0120 }
            if (r6 != r4) goto L_0x00f2
            int r6 = r2.orientation     // Catch:{ all -> 0x0120 }
            int r6 = r6 - r0
            int r6 = r6 + 360
            int r6 = r6 % 360
            goto L_0x00f9
        L_0x00f2:
            int r6 = r2.orientation     // Catch:{ all -> 0x0120 }
            int r6 = r6 + r0
            int r6 = r6 % 360
            goto L_0x00f9
        L_0x00f8:
            r6 = 0
        L_0x00f9:
            r3.setRotation(r6)     // Catch:{ Exception -> 0x010f }
            int r0 = r2.facing     // Catch:{ Exception -> 0x010f }
            if (r0 != r4) goto L_0x010a
            int r0 = 360 - r5
            int r0 = r0 % 360
            if (r0 != r6) goto L_0x0107
            r7 = 1
        L_0x0107:
            r10.sameTakePictureOrientation = r7     // Catch:{ Exception -> 0x010f }
            goto L_0x010f
        L_0x010a:
            if (r5 != r6) goto L_0x010d
            r7 = 1
        L_0x010d:
            r10.sameTakePictureOrientation = r7     // Catch:{ Exception -> 0x010f }
        L_0x010f:
            java.lang.String r0 = r10.currentFlashMode     // Catch:{ all -> 0x0120 }
            r3.setFlashMode(r0)     // Catch:{ all -> 0x0120 }
            r1.setParameters(r3)     // Catch:{ Exception -> 0x0117 }
        L_0x0117:
            int r0 = r3.getMaxNumMeteringAreas()     // Catch:{ all -> 0x0120 }
            if (r0 <= 0) goto L_0x0124
            r10.meteringAreaSupported = r4     // Catch:{ all -> 0x0120 }
            goto L_0x0124
        L_0x0120:
            r0 = move-exception
            org.telegram.messenger.FileLog.e((java.lang.Throwable) r0)
        L_0x0124:
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

    /* access modifiers changed from: protected */
    public void setZoom(float f) {
        this.currentZoom = f;
        configurePhotoCamera();
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
