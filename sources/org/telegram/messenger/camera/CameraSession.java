package org.telegram.messenger.camera;

import android.graphics.Rect;
import android.hardware.Camera;
import android.hardware.Camera.Area;
import android.hardware.Camera.AutoFocusCallback;
import android.hardware.Camera.CameraInfo;
import android.hardware.Camera.Parameters;
import android.hardware.Camera.PreviewCallback;
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
    private AutoFocusCallback autoFocusCallback = -$$Lambda$CameraSession$XMt9_OOlnCLbx3LVWHZ1eqpLkD4.INSTANCE;
    protected CameraInfo cameraInfo;
    private String currentFlashMode;
    private int currentOrientation;
    private int diffOrientation;
    private boolean flipFront = true;
    private boolean initied;
    private boolean isVideo;
    private int jpegOrientation;
    private int lastDisplayOrientation = -1;
    private int lastOrientation = -1;
    private int maxZoom;
    private boolean meteringAreaSupported;
    private OrientationEventListener orientationEventListener;
    private final int pictureFormat;
    private final Size pictureSize;
    private final Size previewSize;
    private boolean sameTakePictureOrientation;

    static /* synthetic */ void lambda$new$0(boolean z, Camera camera) {
    }

    public CameraSession(CameraInfo cameraInfo, Size size, Size size2, int i) {
        this.previewSize = size;
        this.pictureSize = size2;
        this.pictureFormat = i;
        this.cameraInfo = cameraInfo;
        this.currentFlashMode = ApplicationLoader.applicationContext.getSharedPreferences("camera", 0).getString(this.cameraInfo.frontCamera != 0 ? "flashMode_front" : "flashMode", "off");
        this.orientationEventListener = new OrientationEventListener(ApplicationLoader.applicationContext) {
            public void onOrientationChanged(int i) {
                if (CameraSession.this.orientationEventListener != null && CameraSession.this.initied && i != -1) {
                    CameraSession cameraSession = CameraSession.this;
                    cameraSession.jpegOrientation = cameraSession.roundOrientation(i, cameraSession.jpegOrientation);
                    i = ((WindowManager) ApplicationLoader.applicationContext.getSystemService("window")).getDefaultDisplay().getRotation();
                    if (CameraSession.this.lastOrientation != CameraSession.this.jpegOrientation || i != CameraSession.this.lastDisplayOrientation) {
                        if (!CameraSession.this.isVideo) {
                            CameraSession.this.configurePhotoCamera();
                        }
                        CameraSession.this.lastDisplayOrientation = i;
                        CameraSession cameraSession2 = CameraSession.this;
                        cameraSession2.lastOrientation = cameraSession2.jpegOrientation;
                    }
                }
            }
        };
        if (this.orientationEventListener.canDetectOrientation()) {
            this.orientationEventListener.enable();
            return;
        }
        this.orientationEventListener.disable();
        this.orientationEventListener = null;
    }

    private int roundOrientation(int i, int i2) {
        Object obj = 1;
        if (i2 != -1) {
            int abs = Math.abs(i - i2);
            if (Math.min(abs, 360 - abs) < 50) {
                obj = null;
            }
        }
        return obj != null ? (((i + 45) / 90) * 90) % 360 : i2;
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

    public String getCurrentFlashMode() {
        return this.currentFlashMode;
    }

    public String getNextFlashMode() {
        ArrayList arrayList = CameraController.getInstance().availableFlashModes;
        int i = 0;
        while (i < arrayList.size()) {
            if (!((String) arrayList.get(i)).equals(this.currentFlashMode)) {
                i++;
            } else if (i < arrayList.size() - 1) {
                return (String) arrayList.get(i + 1);
            } else {
                return (String) arrayList.get(0);
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

    /* Access modifiers changed, original: protected */
    /* JADX WARNING: Removed duplicated region for block: B:29:0x0057 A:{Catch:{ Exception -> 0x0014, Throwable -> 0x0158 }} */
    /* JADX WARNING: Removed duplicated region for block: B:33:0x0067 A:{Catch:{ Exception -> 0x0014, Throwable -> 0x0158 }} */
    /* JADX WARNING: Removed duplicated region for block: B:32:0x005d A:{Catch:{ Exception -> 0x0014, Throwable -> 0x0158 }} */
    /* JADX WARNING: Removed duplicated region for block: B:80:? A:{SYNTHETIC, RETURN} */
    /* JADX WARNING: Removed duplicated region for block: B:74:0x0155 A:{Catch:{ Exception -> 0x0014, Throwable -> 0x0158 }} */
    /* JADX WARNING: Missing exception handler attribute for start block: B:71:0x014f */
    /* JADX WARNING: Removed duplicated region for block: B:74:0x0155 A:{Catch:{ Exception -> 0x0014, Throwable -> 0x0158 }} */
    /* JADX WARNING: Removed duplicated region for block: B:80:? A:{SYNTHETIC, RETURN} */
    /* JADX WARNING: Missing exception handler attribute for start block: B:67:0x0147 */
    /* JADX WARNING: Exception block dominator not found, dom blocks: [] */
    /* JADX WARNING: Can't wrap try/catch for region: R(19:36|37|(2:39|40)|41|(1:43)|44|(1:46)(2:47|(1:49))|50|(2:52|(1:54)(1:55))(1:56)|57|58|(3:60|(1:62)|63)(2:(1:65)|66)|67|68|69|70|71|72|(2:74|81)(1:80)) */
    public void configureRoundCamera() {
        /*
        r9 = this;
        r0 = 1;
        r9.isVideo = r0;	 Catch:{ Throwable -> 0x0158 }
        r1 = r9.cameraInfo;	 Catch:{ Throwable -> 0x0158 }
        r1 = r1.camera;	 Catch:{ Throwable -> 0x0158 }
        if (r1 == 0) goto L_0x015c;
    L_0x0009:
        r2 = new android.hardware.Camera$CameraInfo;	 Catch:{ Throwable -> 0x0158 }
        r2.<init>();	 Catch:{ Throwable -> 0x0158 }
        r3 = 0;
        r3 = r1.getParameters();	 Catch:{ Exception -> 0x0014 }
        goto L_0x0018;
    L_0x0014:
        r4 = move-exception;
        org.telegram.messenger.FileLog.e(r4);	 Catch:{ Throwable -> 0x0158 }
    L_0x0018:
        r4 = r9.cameraInfo;	 Catch:{ Throwable -> 0x0158 }
        r4 = r4.getCameraId();	 Catch:{ Throwable -> 0x0158 }
        android.hardware.Camera.getCameraInfo(r4, r2);	 Catch:{ Throwable -> 0x0158 }
        r4 = r9.getDisplayOrientation(r2, r0);	 Catch:{ Throwable -> 0x0158 }
        r5 = "samsung";
        r6 = android.os.Build.MANUFACTURER;	 Catch:{ Throwable -> 0x0158 }
        r5 = r5.equals(r6);	 Catch:{ Throwable -> 0x0158 }
        r6 = 0;
        if (r5 == 0) goto L_0x003c;
    L_0x0030:
        r5 = "sf2wifixx";
        r7 = android.os.Build.PRODUCT;	 Catch:{ Throwable -> 0x0158 }
        r5 = r5.equals(r7);	 Catch:{ Throwable -> 0x0158 }
        if (r5 == 0) goto L_0x003c;
    L_0x003a:
        r5 = 0;
        goto L_0x006e;
    L_0x003c:
        r5 = 90;
        if (r4 == 0) goto L_0x0048;
    L_0x0040:
        if (r4 == r0) goto L_0x0050;
    L_0x0042:
        r7 = 2;
        if (r4 == r7) goto L_0x004d;
    L_0x0045:
        r7 = 3;
        if (r4 == r7) goto L_0x004a;
    L_0x0048:
        r7 = 0;
        goto L_0x0052;
    L_0x004a:
        r7 = 270; // 0x10e float:3.78E-43 double:1.334E-321;
        goto L_0x0052;
    L_0x004d:
        r7 = 180; // 0xb4 float:2.52E-43 double:8.9E-322;
        goto L_0x0052;
    L_0x0050:
        r7 = 90;
    L_0x0052:
        r8 = r2.orientation;	 Catch:{ Throwable -> 0x0158 }
        r8 = r8 % r5;
        if (r8 == 0) goto L_0x0059;
    L_0x0057:
        r2.orientation = r6;	 Catch:{ Throwable -> 0x0158 }
    L_0x0059:
        r5 = r2.facing;	 Catch:{ Throwable -> 0x0158 }
        if (r5 != r0) goto L_0x0067;
    L_0x005d:
        r5 = r2.orientation;	 Catch:{ Throwable -> 0x0158 }
        r5 = r5 + r7;
        r5 = r5 % 360;
        r5 = 360 - r5;
        r5 = r5 % 360;
        goto L_0x006e;
    L_0x0067:
        r5 = r2.orientation;	 Catch:{ Throwable -> 0x0158 }
        r5 = r5 - r7;
        r5 = r5 + 360;
        r5 = r5 % 360;
    L_0x006e:
        r9.currentOrientation = r5;	 Catch:{ Throwable -> 0x0158 }
        r1.setDisplayOrientation(r5);	 Catch:{ Throwable -> 0x0158 }
        r5 = r9.currentOrientation;	 Catch:{ Throwable -> 0x0158 }
        r5 = r5 - r4;
        r9.diffOrientation = r5;	 Catch:{ Throwable -> 0x0158 }
        if (r3 == 0) goto L_0x015c;
    L_0x007a:
        r5 = org.telegram.messenger.BuildVars.LOGS_ENABLED;	 Catch:{ Throwable -> 0x0158 }
        r7 = " ";
        if (r5 == 0) goto L_0x00a6;
    L_0x0080:
        r5 = new java.lang.StringBuilder;	 Catch:{ Throwable -> 0x0158 }
        r5.<init>();	 Catch:{ Throwable -> 0x0158 }
        r8 = "set preview size = ";
        r5.append(r8);	 Catch:{ Throwable -> 0x0158 }
        r8 = r9.previewSize;	 Catch:{ Throwable -> 0x0158 }
        r8 = r8.getWidth();	 Catch:{ Throwable -> 0x0158 }
        r5.append(r8);	 Catch:{ Throwable -> 0x0158 }
        r5.append(r7);	 Catch:{ Throwable -> 0x0158 }
        r8 = r9.previewSize;	 Catch:{ Throwable -> 0x0158 }
        r8 = r8.getHeight();	 Catch:{ Throwable -> 0x0158 }
        r5.append(r8);	 Catch:{ Throwable -> 0x0158 }
        r5 = r5.toString();	 Catch:{ Throwable -> 0x0158 }
        org.telegram.messenger.FileLog.d(r5);	 Catch:{ Throwable -> 0x0158 }
    L_0x00a6:
        r5 = r9.previewSize;	 Catch:{ Throwable -> 0x0158 }
        r5 = r5.getWidth();	 Catch:{ Throwable -> 0x0158 }
        r8 = r9.previewSize;	 Catch:{ Throwable -> 0x0158 }
        r8 = r8.getHeight();	 Catch:{ Throwable -> 0x0158 }
        r3.setPreviewSize(r5, r8);	 Catch:{ Throwable -> 0x0158 }
        r5 = org.telegram.messenger.BuildVars.LOGS_ENABLED;	 Catch:{ Throwable -> 0x0158 }
        if (r5 == 0) goto L_0x00df;
    L_0x00b9:
        r5 = new java.lang.StringBuilder;	 Catch:{ Throwable -> 0x0158 }
        r5.<init>();	 Catch:{ Throwable -> 0x0158 }
        r8 = "set picture size = ";
        r5.append(r8);	 Catch:{ Throwable -> 0x0158 }
        r8 = r9.pictureSize;	 Catch:{ Throwable -> 0x0158 }
        r8 = r8.getWidth();	 Catch:{ Throwable -> 0x0158 }
        r5.append(r8);	 Catch:{ Throwable -> 0x0158 }
        r5.append(r7);	 Catch:{ Throwable -> 0x0158 }
        r7 = r9.pictureSize;	 Catch:{ Throwable -> 0x0158 }
        r7 = r7.getHeight();	 Catch:{ Throwable -> 0x0158 }
        r5.append(r7);	 Catch:{ Throwable -> 0x0158 }
        r5 = r5.toString();	 Catch:{ Throwable -> 0x0158 }
        org.telegram.messenger.FileLog.d(r5);	 Catch:{ Throwable -> 0x0158 }
    L_0x00df:
        r5 = r9.pictureSize;	 Catch:{ Throwable -> 0x0158 }
        r5 = r5.getWidth();	 Catch:{ Throwable -> 0x0158 }
        r7 = r9.pictureSize;	 Catch:{ Throwable -> 0x0158 }
        r7 = r7.getHeight();	 Catch:{ Throwable -> 0x0158 }
        r3.setPictureSize(r5, r7);	 Catch:{ Throwable -> 0x0158 }
        r5 = r9.pictureFormat;	 Catch:{ Throwable -> 0x0158 }
        r3.setPictureFormat(r5);	 Catch:{ Throwable -> 0x0158 }
        r3.setRecordingHint(r0);	 Catch:{ Throwable -> 0x0158 }
        r5 = "continuous-video";
        r7 = r3.getSupportedFocusModes();	 Catch:{ Throwable -> 0x0158 }
        r7 = r7.contains(r5);	 Catch:{ Throwable -> 0x0158 }
        if (r7 == 0) goto L_0x0106;
    L_0x0102:
        r3.setFocusMode(r5);	 Catch:{ Throwable -> 0x0158 }
        goto L_0x0115;
    L_0x0106:
        r5 = "auto";
        r7 = r3.getSupportedFocusModes();	 Catch:{ Throwable -> 0x0158 }
        r7 = r7.contains(r5);	 Catch:{ Throwable -> 0x0158 }
        if (r7 == 0) goto L_0x0115;
    L_0x0112:
        r3.setFocusMode(r5);	 Catch:{ Throwable -> 0x0158 }
    L_0x0115:
        r5 = r9.jpegOrientation;	 Catch:{ Throwable -> 0x0158 }
        r7 = -1;
        if (r5 == r7) goto L_0x0130;
    L_0x011a:
        r5 = r2.facing;	 Catch:{ Throwable -> 0x0158 }
        if (r5 != r0) goto L_0x0128;
    L_0x011e:
        r5 = r2.orientation;	 Catch:{ Throwable -> 0x0158 }
        r7 = r9.jpegOrientation;	 Catch:{ Throwable -> 0x0158 }
        r5 = r5 - r7;
        r5 = r5 + 360;
        r5 = r5 % 360;
        goto L_0x0131;
    L_0x0128:
        r5 = r2.orientation;	 Catch:{ Throwable -> 0x0158 }
        r7 = r9.jpegOrientation;	 Catch:{ Throwable -> 0x0158 }
        r5 = r5 + r7;
        r5 = r5 % 360;
        goto L_0x0131;
    L_0x0130:
        r5 = 0;
    L_0x0131:
        r3.setRotation(r5);	 Catch:{ Exception -> 0x0147 }
        r2 = r2.facing;	 Catch:{ Exception -> 0x0147 }
        if (r2 != r0) goto L_0x0142;
    L_0x0138:
        r2 = 360 - r4;
        r2 = r2 % 360;
        if (r2 != r5) goto L_0x013f;
    L_0x013e:
        r6 = 1;
    L_0x013f:
        r9.sameTakePictureOrientation = r6;	 Catch:{ Exception -> 0x0147 }
        goto L_0x0147;
    L_0x0142:
        if (r4 != r5) goto L_0x0145;
    L_0x0144:
        r6 = 1;
    L_0x0145:
        r9.sameTakePictureOrientation = r6;	 Catch:{ Exception -> 0x0147 }
    L_0x0147:
        r2 = "off";
        r3.setFlashMode(r2);	 Catch:{ Throwable -> 0x0158 }
        r1.setParameters(r3);	 Catch:{ Exception -> 0x014f }
    L_0x014f:
        r1 = r3.getMaxNumMeteringAreas();	 Catch:{ Throwable -> 0x0158 }
        if (r1 <= 0) goto L_0x015c;
    L_0x0155:
        r9.meteringAreaSupported = r0;	 Catch:{ Throwable -> 0x0158 }
        goto L_0x015c;
    L_0x0158:
        r0 = move-exception;
        org.telegram.messenger.FileLog.e(r0);
    L_0x015c:
        return;
        */
        throw new UnsupportedOperationException("Method not decompiled: org.telegram.messenger.camera.CameraSession.configureRoundCamera():void");
    }

    /* Access modifiers changed, original: protected */
    /* JADX WARNING: Removed duplicated region for block: B:28:0x0055 A:{Catch:{ Exception -> 0x0011, Throwable -> 0x00f1 }} */
    /* JADX WARNING: Removed duplicated region for block: B:32:0x0065 A:{Catch:{ Exception -> 0x0011, Throwable -> 0x00f1 }} */
    /* JADX WARNING: Removed duplicated region for block: B:31:0x005b A:{Catch:{ Exception -> 0x0011, Throwable -> 0x00f1 }} */
    /* JADX WARNING: Removed duplicated region for block: B:68:? A:{SYNTHETIC, RETURN} */
    /* JADX WARNING: Removed duplicated region for block: B:62:0x00ee A:{Catch:{ Exception -> 0x0011, Throwable -> 0x00f1 }} */
    /* JADX WARNING: Missing exception handler attribute for start block: B:55:0x00e0 */
    /* JADX WARNING: Removed duplicated region for block: B:62:0x00ee A:{Catch:{ Exception -> 0x0011, Throwable -> 0x00f1 }} */
    /* JADX WARNING: Removed duplicated region for block: B:68:? A:{SYNTHETIC, RETURN} */
    /* JADX WARNING: Missing exception handler attribute for start block: B:59:0x00e8 */
    /* JADX WARNING: Exception block dominator not found, dom blocks: [] */
    /* JADX WARNING: Exception block dominator not found, dom blocks: [] */
    public void configurePhotoCamera() {
        /*
        r9 = this;
        r0 = r9.cameraInfo;	 Catch:{ Throwable -> 0x00f1 }
        r0 = r0.camera;	 Catch:{ Throwable -> 0x00f1 }
        if (r0 == 0) goto L_0x00f5;
    L_0x0006:
        r1 = new android.hardware.Camera$CameraInfo;	 Catch:{ Throwable -> 0x00f1 }
        r1.<init>();	 Catch:{ Throwable -> 0x00f1 }
        r2 = 0;
        r2 = r0.getParameters();	 Catch:{ Exception -> 0x0011 }
        goto L_0x0015;
    L_0x0011:
        r3 = move-exception;
        org.telegram.messenger.FileLog.e(r3);	 Catch:{ Throwable -> 0x00f1 }
    L_0x0015:
        r3 = r9.cameraInfo;	 Catch:{ Throwable -> 0x00f1 }
        r3 = r3.getCameraId();	 Catch:{ Throwable -> 0x00f1 }
        android.hardware.Camera.getCameraInfo(r3, r1);	 Catch:{ Throwable -> 0x00f1 }
        r3 = 1;
        r4 = r9.getDisplayOrientation(r1, r3);	 Catch:{ Throwable -> 0x00f1 }
        r5 = "samsung";
        r6 = android.os.Build.MANUFACTURER;	 Catch:{ Throwable -> 0x00f1 }
        r5 = r5.equals(r6);	 Catch:{ Throwable -> 0x00f1 }
        r6 = 0;
        if (r5 == 0) goto L_0x003a;
    L_0x002e:
        r5 = "sf2wifixx";
        r7 = android.os.Build.PRODUCT;	 Catch:{ Throwable -> 0x00f1 }
        r5 = r5.equals(r7);	 Catch:{ Throwable -> 0x00f1 }
        if (r5 == 0) goto L_0x003a;
    L_0x0038:
        r5 = 0;
        goto L_0x006c;
    L_0x003a:
        r5 = 90;
        if (r4 == 0) goto L_0x0046;
    L_0x003e:
        if (r4 == r3) goto L_0x004e;
    L_0x0040:
        r7 = 2;
        if (r4 == r7) goto L_0x004b;
    L_0x0043:
        r7 = 3;
        if (r4 == r7) goto L_0x0048;
    L_0x0046:
        r7 = 0;
        goto L_0x0050;
    L_0x0048:
        r7 = 270; // 0x10e float:3.78E-43 double:1.334E-321;
        goto L_0x0050;
    L_0x004b:
        r7 = 180; // 0xb4 float:2.52E-43 double:8.9E-322;
        goto L_0x0050;
    L_0x004e:
        r7 = 90;
    L_0x0050:
        r8 = r1.orientation;	 Catch:{ Throwable -> 0x00f1 }
        r8 = r8 % r5;
        if (r8 == 0) goto L_0x0057;
    L_0x0055:
        r1.orientation = r6;	 Catch:{ Throwable -> 0x00f1 }
    L_0x0057:
        r5 = r1.facing;	 Catch:{ Throwable -> 0x00f1 }
        if (r5 != r3) goto L_0x0065;
    L_0x005b:
        r5 = r1.orientation;	 Catch:{ Throwable -> 0x00f1 }
        r5 = r5 + r7;
        r5 = r5 % 360;
        r5 = 360 - r5;
        r5 = r5 % 360;
        goto L_0x006c;
    L_0x0065:
        r5 = r1.orientation;	 Catch:{ Throwable -> 0x00f1 }
        r5 = r5 - r7;
        r5 = r5 + 360;
        r5 = r5 % 360;
    L_0x006c:
        r9.currentOrientation = r5;	 Catch:{ Throwable -> 0x00f1 }
        r0.setDisplayOrientation(r5);	 Catch:{ Throwable -> 0x00f1 }
        if (r2 == 0) goto L_0x00f5;
    L_0x0073:
        r5 = r9.previewSize;	 Catch:{ Throwable -> 0x00f1 }
        r5 = r5.getWidth();	 Catch:{ Throwable -> 0x00f1 }
        r7 = r9.previewSize;	 Catch:{ Throwable -> 0x00f1 }
        r7 = r7.getHeight();	 Catch:{ Throwable -> 0x00f1 }
        r2.setPreviewSize(r5, r7);	 Catch:{ Throwable -> 0x00f1 }
        r5 = r9.pictureSize;	 Catch:{ Throwable -> 0x00f1 }
        r5 = r5.getWidth();	 Catch:{ Throwable -> 0x00f1 }
        r7 = r9.pictureSize;	 Catch:{ Throwable -> 0x00f1 }
        r7 = r7.getHeight();	 Catch:{ Throwable -> 0x00f1 }
        r2.setPictureSize(r5, r7);	 Catch:{ Throwable -> 0x00f1 }
        r5 = r9.pictureFormat;	 Catch:{ Throwable -> 0x00f1 }
        r2.setPictureFormat(r5);	 Catch:{ Throwable -> 0x00f1 }
        r5 = r2.getMaxZoom();	 Catch:{ Throwable -> 0x00f1 }
        r9.maxZoom = r5;	 Catch:{ Throwable -> 0x00f1 }
        r2.getZoom();	 Catch:{ Throwable -> 0x00f1 }
        r5 = "continuous-picture";
        r7 = r2.getSupportedFocusModes();	 Catch:{ Throwable -> 0x00f1 }
        r7 = r7.contains(r5);	 Catch:{ Throwable -> 0x00f1 }
        if (r7 == 0) goto L_0x00ae;
    L_0x00ab:
        r2.setFocusMode(r5);	 Catch:{ Throwable -> 0x00f1 }
    L_0x00ae:
        r5 = r9.jpegOrientation;	 Catch:{ Throwable -> 0x00f1 }
        r7 = -1;
        if (r5 == r7) goto L_0x00c9;
    L_0x00b3:
        r5 = r1.facing;	 Catch:{ Throwable -> 0x00f1 }
        if (r5 != r3) goto L_0x00c1;
    L_0x00b7:
        r5 = r1.orientation;	 Catch:{ Throwable -> 0x00f1 }
        r7 = r9.jpegOrientation;	 Catch:{ Throwable -> 0x00f1 }
        r5 = r5 - r7;
        r5 = r5 + 360;
        r5 = r5 % 360;
        goto L_0x00ca;
    L_0x00c1:
        r5 = r1.orientation;	 Catch:{ Throwable -> 0x00f1 }
        r7 = r9.jpegOrientation;	 Catch:{ Throwable -> 0x00f1 }
        r5 = r5 + r7;
        r5 = r5 % 360;
        goto L_0x00ca;
    L_0x00c9:
        r5 = 0;
    L_0x00ca:
        r2.setRotation(r5);	 Catch:{ Exception -> 0x00e0 }
        r1 = r1.facing;	 Catch:{ Exception -> 0x00e0 }
        if (r1 != r3) goto L_0x00db;
    L_0x00d1:
        r1 = 360 - r4;
        r1 = r1 % 360;
        if (r1 != r5) goto L_0x00d8;
    L_0x00d7:
        r6 = 1;
    L_0x00d8:
        r9.sameTakePictureOrientation = r6;	 Catch:{ Exception -> 0x00e0 }
        goto L_0x00e0;
    L_0x00db:
        if (r4 != r5) goto L_0x00de;
    L_0x00dd:
        r6 = 1;
    L_0x00de:
        r9.sameTakePictureOrientation = r6;	 Catch:{ Exception -> 0x00e0 }
    L_0x00e0:
        r1 = r9.currentFlashMode;	 Catch:{ Throwable -> 0x00f1 }
        r2.setFlashMode(r1);	 Catch:{ Throwable -> 0x00f1 }
        r0.setParameters(r2);	 Catch:{ Exception -> 0x00e8 }
    L_0x00e8:
        r0 = r2.getMaxNumMeteringAreas();	 Catch:{ Throwable -> 0x00f1 }
        if (r0 <= 0) goto L_0x00f5;
    L_0x00ee:
        r9.meteringAreaSupported = r3;	 Catch:{ Throwable -> 0x00f1 }
        goto L_0x00f5;
    L_0x00f1:
        r0 = move-exception;
        org.telegram.messenger.FileLog.e(r0);
    L_0x00f5:
        return;
        */
        throw new UnsupportedOperationException("Method not decompiled: org.telegram.messenger.camera.CameraSession.configurePhotoCamera():void");
    }

    /* Access modifiers changed, original: protected */
    public void focusToRect(Rect rect, Rect rect2) {
        try {
            Camera camera = this.cameraInfo.camera;
            if (camera != null) {
                camera.cancelAutoFocus();
                Parameters parameters = null;
                try {
                    parameters = camera.getParameters();
                } catch (Exception e) {
                    FileLog.e(e);
                }
                if (parameters != null) {
                    parameters.setFocusMode("auto");
                    ArrayList arrayList = new ArrayList();
                    arrayList.add(new Area(rect, 1000));
                    parameters.setFocusAreas(arrayList);
                    if (this.meteringAreaSupported) {
                        ArrayList arrayList2 = new ArrayList();
                        arrayList2.add(new Area(rect2, 1000));
                        parameters.setMeteringAreas(arrayList2);
                    }
                    try {
                        camera.setParameters(parameters);
                        camera.autoFocus(this.autoFocusCallback);
                    } catch (Exception e2) {
                        FileLog.e(e2);
                    }
                }
            }
        } catch (Exception e22) {
            FileLog.e(e22);
        }
    }

    /* Access modifiers changed, original: protected */
    public int getMaxZoom() {
        return this.maxZoom;
    }

    /* Access modifiers changed, original: protected */
    public void setZoom(float f) {
        try {
            Camera camera = this.cameraInfo.camera;
            if (camera != null) {
                Parameters parameters = null;
                try {
                    parameters = camera.getParameters();
                } catch (Exception e) {
                    FileLog.e(e);
                }
                if (parameters != null) {
                    parameters.setZoom((int) (f * ((float) this.maxZoom)));
                    try {
                        camera.setParameters(parameters);
                    } catch (Exception e2) {
                        FileLog.e(e2);
                    }
                }
            }
        } catch (Exception e22) {
            FileLog.e(e22);
        }
    }

    /* Access modifiers changed, original: protected */
    public void configureRecorder(int i, MediaRecorder mediaRecorder) {
        CameraInfo cameraInfo = new CameraInfo();
        Camera.getCameraInfo(this.cameraInfo.cameraId, cameraInfo);
        getDisplayOrientation(cameraInfo, false);
        int i2 = this.jpegOrientation;
        int i3 = i2 != -1 ? cameraInfo.facing == 1 ? ((cameraInfo.orientation - i2) + 360) % 360 : (cameraInfo.orientation + i2) % 360 : 0;
        mediaRecorder.setOrientationHint(i3);
        i3 = getHigh();
        boolean hasProfile = CamcorderProfile.hasProfile(this.cameraInfo.cameraId, i3);
        boolean hasProfile2 = CamcorderProfile.hasProfile(this.cameraInfo.cameraId, 0);
        if (hasProfile && (i == 1 || !hasProfile2)) {
            mediaRecorder.setProfile(CamcorderProfile.get(this.cameraInfo.cameraId, i3));
        } else if (hasProfile2) {
            mediaRecorder.setProfile(CamcorderProfile.get(this.cameraInfo.cameraId, 0));
        } else {
            throw new IllegalStateException("cannot find valid CamcorderProfile");
        }
        this.isVideo = true;
    }

    /* Access modifiers changed, original: protected */
    public void stopVideoRecording() {
        this.isVideo = false;
        configurePhotoCamera();
    }

    private int getHigh() {
        if ("LGE".equals(Build.MANUFACTURER)) {
            if ("g3_tmo_us".equals(Build.PRODUCT)) {
                return 4;
            }
        }
        return 1;
    }

    private int getDisplayOrientation(CameraInfo cameraInfo, boolean z) {
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
        if (cameraInfo.facing != 1) {
            return ((cameraInfo.orientation - i) + 360) % 360;
        }
        int i2 = (360 - ((cameraInfo.orientation + i) % 360)) % 360;
        if (!z && i2 == 90) {
            i2 = 270;
        }
        if (!z) {
            if ("Huawei".equals(Build.MANUFACTURER)) {
                if ("angler".equals(Build.PRODUCT) && i2 == 270) {
                    return 90;
                }
            }
        }
        return i2;
    }

    public int getDisplayOrientation() {
        try {
            CameraInfo cameraInfo = new CameraInfo();
            Camera.getCameraInfo(this.cameraInfo.getCameraId(), cameraInfo);
            return getDisplayOrientation(cameraInfo, true);
        } catch (Exception e) {
            FileLog.e(e);
            return 0;
        }
    }

    public void setPreviewCallback(PreviewCallback previewCallback) {
        this.cameraInfo.camera.setPreviewCallback(previewCallback);
    }

    public void setOneShotPreviewCallback(PreviewCallback previewCallback) {
        CameraInfo cameraInfo = this.cameraInfo;
        if (cameraInfo != null) {
            Camera camera = cameraInfo.camera;
            if (camera != null) {
                camera.setOneShotPreviewCallback(previewCallback);
            }
        }
    }

    public void destroy() {
        this.initied = false;
        OrientationEventListener orientationEventListener = this.orientationEventListener;
        if (orientationEventListener != null) {
            orientationEventListener.disable();
            this.orientationEventListener = null;
        }
    }
}
