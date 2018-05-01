package org.telegram.messenger.camera;

import android.graphics.Rect;
import android.hardware.Camera;
import android.hardware.Camera.Area;
import android.hardware.Camera.AutoFocusCallback;
import android.hardware.Camera.CameraInfo;
import android.hardware.Camera.Parameters;
import android.media.CamcorderProfile;
import android.media.MediaRecorder;
import android.os.Build;
import android.view.OrientationEventListener;
import android.view.WindowManager;
import java.util.ArrayList;
import java.util.List;
import org.telegram.messenger.ApplicationLoader;
import org.telegram.messenger.FileLog;

public class CameraSession {
    public static final int ORIENTATION_HYSTERESIS = 5;
    private AutoFocusCallback autoFocusCallback = new C05381();
    protected CameraInfo cameraInfo;
    private String currentFlashMode = "off";
    private int currentOrientation;
    private int diffOrientation;
    private boolean initied;
    private boolean isVideo;
    private int jpegOrientation;
    private int lastDisplayOrientation = -1;
    private int lastOrientation = -1;
    private boolean meteringAreaSupported;
    private OrientationEventListener orientationEventListener;
    private final int pictureFormat;
    private final Size pictureSize;
    private final Size previewSize;
    private boolean sameTakePictureOrientation;

    /* renamed from: org.telegram.messenger.camera.CameraSession$1 */
    class C05381 implements AutoFocusCallback {
        public void onAutoFocus(boolean z, Camera camera) {
        }

        C05381() {
        }
    }

    public CameraSession(CameraInfo cameraInfo, Size size, Size size2, int i) {
        this.previewSize = size;
        this.pictureSize = size2;
        this.pictureFormat = i;
        this.cameraInfo = cameraInfo;
        this.currentFlashMode = ApplicationLoader.applicationContext.getSharedPreferences("camera", null).getString(this.cameraInfo.frontCamera != null ? "flashMode_front" : "flashMode", "off");
        this.orientationEventListener = new OrientationEventListener(ApplicationLoader.applicationContext) {
            public void onOrientationChanged(int i) {
                if (CameraSession.this.orientationEventListener != null && CameraSession.this.initied) {
                    if (i != -1) {
                        CameraSession.this.jpegOrientation = CameraSession.this.roundOrientation(i, CameraSession.this.jpegOrientation);
                        i = ((WindowManager) ApplicationLoader.applicationContext.getSystemService("window")).getDefaultDisplay().getRotation();
                        if (!(CameraSession.this.lastOrientation == CameraSession.this.jpegOrientation && i == CameraSession.this.lastDisplayOrientation)) {
                            if (!CameraSession.this.isVideo) {
                                CameraSession.this.configurePhotoCamera();
                            }
                            CameraSession.this.lastDisplayOrientation = i;
                            CameraSession.this.lastOrientation = CameraSession.this.jpegOrientation;
                        }
                    }
                }
            }
        };
        if (this.orientationEventListener.canDetectOrientation() != null) {
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

    public int getWorldAngle() {
        return this.diffOrientation;
    }

    public boolean isSameTakePictureOrientation() {
        return this.sameTakePictureOrientation;
    }

    protected void configureRoundCamera() {
        /* JADX: method processing error */
/*
Error: java.lang.NullPointerException
	at jadx.core.dex.visitors.regions.ProcessTryCatchRegions.searchTryCatchDominators(ProcessTryCatchRegions.java:75)
	at jadx.core.dex.visitors.regions.ProcessTryCatchRegions.process(ProcessTryCatchRegions.java:45)
	at jadx.core.dex.visitors.regions.RegionMakerVisitor.postProcessRegions(RegionMakerVisitor.java:63)
	at jadx.core.dex.visitors.regions.RegionMakerVisitor.visit(RegionMakerVisitor.java:58)
	at jadx.core.dex.visitors.DepthTraversal.visit(DepthTraversal.java:31)
	at jadx.core.dex.visitors.DepthTraversal.visit(DepthTraversal.java:17)
	at jadx.core.ProcessClass.process(ProcessClass.java:34)
	at jadx.core.ProcessClass.processDependencies(ProcessClass.java:60)
	at jadx.core.ProcessClass.process(ProcessClass.java:39)
	at jadx.api.JadxDecompiler.processClass(JadxDecompiler.java:282)
	at jadx.api.JavaClass.decompile(JavaClass.java:62)
	at jadx.api.JadxDecompiler.lambda$appendSourcesSave$0(JadxDecompiler.java:200)
*/
        /*
        r9 = this;
        r0 = 1;
        r9.isVideo = r0;	 Catch:{ Throwable -> 0x0143 }
        r1 = r9.cameraInfo;	 Catch:{ Throwable -> 0x0143 }
        r1 = r1.camera;	 Catch:{ Throwable -> 0x0143 }
        if (r1 == 0) goto L_0x0147;	 Catch:{ Throwable -> 0x0143 }
    L_0x0009:
        r2 = new android.hardware.Camera$CameraInfo;	 Catch:{ Throwable -> 0x0143 }
        r2.<init>();	 Catch:{ Throwable -> 0x0143 }
        r3 = 0;
        r4 = r1.getParameters();	 Catch:{ Exception -> 0x0015 }
        r3 = r4;
        goto L_0x0019;
    L_0x0015:
        r4 = move-exception;
        org.telegram.messenger.FileLog.m3e(r4);	 Catch:{ Throwable -> 0x0143 }
    L_0x0019:
        r4 = r9.cameraInfo;	 Catch:{ Throwable -> 0x0143 }
        r4 = r4.getCameraId();	 Catch:{ Throwable -> 0x0143 }
        android.hardware.Camera.getCameraInfo(r4, r2);	 Catch:{ Throwable -> 0x0143 }
        r4 = r9.getDisplayOrientation(r2, r0);	 Catch:{ Throwable -> 0x0143 }
        r5 = "samsung";	 Catch:{ Throwable -> 0x0143 }
        r6 = android.os.Build.MANUFACTURER;	 Catch:{ Throwable -> 0x0143 }
        r5 = r5.equals(r6);	 Catch:{ Throwable -> 0x0143 }
        r6 = 0;	 Catch:{ Throwable -> 0x0143 }
        if (r5 == 0) goto L_0x003d;	 Catch:{ Throwable -> 0x0143 }
    L_0x0031:
        r5 = "sf2wifixx";	 Catch:{ Throwable -> 0x0143 }
        r7 = android.os.Build.PRODUCT;	 Catch:{ Throwable -> 0x0143 }
        r5 = r5.equals(r7);	 Catch:{ Throwable -> 0x0143 }
        if (r5 == 0) goto L_0x003d;	 Catch:{ Throwable -> 0x0143 }
    L_0x003b:
        r5 = r6;	 Catch:{ Throwable -> 0x0143 }
        goto L_0x0067;	 Catch:{ Throwable -> 0x0143 }
    L_0x003d:
        r5 = 90;	 Catch:{ Throwable -> 0x0143 }
        switch(r4) {
            case 0: goto L_0x0042;
            case 1: goto L_0x004a;
            case 2: goto L_0x0047;
            case 3: goto L_0x0044;
            default: goto L_0x0042;
        };	 Catch:{ Throwable -> 0x0143 }
    L_0x0042:
        r7 = r6;	 Catch:{ Throwable -> 0x0143 }
        goto L_0x004b;	 Catch:{ Throwable -> 0x0143 }
    L_0x0044:
        r7 = 270; // 0x10e float:3.78E-43 double:1.334E-321;	 Catch:{ Throwable -> 0x0143 }
        goto L_0x004b;	 Catch:{ Throwable -> 0x0143 }
    L_0x0047:
        r7 = 180; // 0xb4 float:2.52E-43 double:8.9E-322;	 Catch:{ Throwable -> 0x0143 }
        goto L_0x004b;	 Catch:{ Throwable -> 0x0143 }
    L_0x004a:
        r7 = r5;	 Catch:{ Throwable -> 0x0143 }
    L_0x004b:
        r8 = r2.orientation;	 Catch:{ Throwable -> 0x0143 }
        r8 = r8 % r5;	 Catch:{ Throwable -> 0x0143 }
        if (r8 == 0) goto L_0x0052;	 Catch:{ Throwable -> 0x0143 }
    L_0x0050:
        r2.orientation = r6;	 Catch:{ Throwable -> 0x0143 }
    L_0x0052:
        r5 = r2.facing;	 Catch:{ Throwable -> 0x0143 }
        if (r5 != r0) goto L_0x0060;	 Catch:{ Throwable -> 0x0143 }
    L_0x0056:
        r5 = r2.orientation;	 Catch:{ Throwable -> 0x0143 }
        r5 = r5 + r7;	 Catch:{ Throwable -> 0x0143 }
        r5 = r5 % 360;	 Catch:{ Throwable -> 0x0143 }
        r5 = 360 - r5;	 Catch:{ Throwable -> 0x0143 }
        r5 = r5 % 360;	 Catch:{ Throwable -> 0x0143 }
        goto L_0x0067;	 Catch:{ Throwable -> 0x0143 }
    L_0x0060:
        r5 = r2.orientation;	 Catch:{ Throwable -> 0x0143 }
        r5 = r5 - r7;	 Catch:{ Throwable -> 0x0143 }
        r5 = r5 + 360;	 Catch:{ Throwable -> 0x0143 }
        r5 = r5 % 360;	 Catch:{ Throwable -> 0x0143 }
    L_0x0067:
        r9.currentOrientation = r5;	 Catch:{ Throwable -> 0x0143 }
        r1.setDisplayOrientation(r5);	 Catch:{ Throwable -> 0x0143 }
        r5 = r9.currentOrientation;	 Catch:{ Throwable -> 0x0143 }
        r5 = r5 - r4;	 Catch:{ Throwable -> 0x0143 }
        r9.diffOrientation = r5;	 Catch:{ Throwable -> 0x0143 }
        if (r3 == 0) goto L_0x0147;	 Catch:{ Throwable -> 0x0143 }
    L_0x0073:
        r5 = org.telegram.messenger.BuildVars.LOGS_ENABLED;	 Catch:{ Throwable -> 0x0143 }
        if (r5 == 0) goto L_0x009f;	 Catch:{ Throwable -> 0x0143 }
    L_0x0077:
        r5 = new java.lang.StringBuilder;	 Catch:{ Throwable -> 0x0143 }
        r5.<init>();	 Catch:{ Throwable -> 0x0143 }
        r7 = "set preview size = ";	 Catch:{ Throwable -> 0x0143 }
        r5.append(r7);	 Catch:{ Throwable -> 0x0143 }
        r7 = r9.previewSize;	 Catch:{ Throwable -> 0x0143 }
        r7 = r7.getWidth();	 Catch:{ Throwable -> 0x0143 }
        r5.append(r7);	 Catch:{ Throwable -> 0x0143 }
        r7 = " ";	 Catch:{ Throwable -> 0x0143 }
        r5.append(r7);	 Catch:{ Throwable -> 0x0143 }
        r7 = r9.previewSize;	 Catch:{ Throwable -> 0x0143 }
        r7 = r7.getHeight();	 Catch:{ Throwable -> 0x0143 }
        r5.append(r7);	 Catch:{ Throwable -> 0x0143 }
        r5 = r5.toString();	 Catch:{ Throwable -> 0x0143 }
        org.telegram.messenger.FileLog.m0d(r5);	 Catch:{ Throwable -> 0x0143 }
    L_0x009f:
        r5 = r9.previewSize;	 Catch:{ Throwable -> 0x0143 }
        r5 = r5.getWidth();	 Catch:{ Throwable -> 0x0143 }
        r7 = r9.previewSize;	 Catch:{ Throwable -> 0x0143 }
        r7 = r7.getHeight();	 Catch:{ Throwable -> 0x0143 }
        r3.setPreviewSize(r5, r7);	 Catch:{ Throwable -> 0x0143 }
        r5 = org.telegram.messenger.BuildVars.LOGS_ENABLED;	 Catch:{ Throwable -> 0x0143 }
        if (r5 == 0) goto L_0x00da;	 Catch:{ Throwable -> 0x0143 }
    L_0x00b2:
        r5 = new java.lang.StringBuilder;	 Catch:{ Throwable -> 0x0143 }
        r5.<init>();	 Catch:{ Throwable -> 0x0143 }
        r7 = "set picture size = ";	 Catch:{ Throwable -> 0x0143 }
        r5.append(r7);	 Catch:{ Throwable -> 0x0143 }
        r7 = r9.pictureSize;	 Catch:{ Throwable -> 0x0143 }
        r7 = r7.getWidth();	 Catch:{ Throwable -> 0x0143 }
        r5.append(r7);	 Catch:{ Throwable -> 0x0143 }
        r7 = " ";	 Catch:{ Throwable -> 0x0143 }
        r5.append(r7);	 Catch:{ Throwable -> 0x0143 }
        r7 = r9.pictureSize;	 Catch:{ Throwable -> 0x0143 }
        r7 = r7.getHeight();	 Catch:{ Throwable -> 0x0143 }
        r5.append(r7);	 Catch:{ Throwable -> 0x0143 }
        r5 = r5.toString();	 Catch:{ Throwable -> 0x0143 }
        org.telegram.messenger.FileLog.m0d(r5);	 Catch:{ Throwable -> 0x0143 }
    L_0x00da:
        r5 = r9.pictureSize;	 Catch:{ Throwable -> 0x0143 }
        r5 = r5.getWidth();	 Catch:{ Throwable -> 0x0143 }
        r7 = r9.pictureSize;	 Catch:{ Throwable -> 0x0143 }
        r7 = r7.getHeight();	 Catch:{ Throwable -> 0x0143 }
        r3.setPictureSize(r5, r7);	 Catch:{ Throwable -> 0x0143 }
        r5 = r9.pictureFormat;	 Catch:{ Throwable -> 0x0143 }
        r3.setPictureFormat(r5);	 Catch:{ Throwable -> 0x0143 }
        r3.setRecordingHint(r0);	 Catch:{ Throwable -> 0x0143 }
        r5 = "auto";	 Catch:{ Throwable -> 0x0143 }
        r7 = r3.getSupportedFocusModes();	 Catch:{ Throwable -> 0x0143 }
        r7 = r7.contains(r5);	 Catch:{ Throwable -> 0x0143 }
        if (r7 == 0) goto L_0x0100;	 Catch:{ Throwable -> 0x0143 }
    L_0x00fd:
        r3.setFocusMode(r5);	 Catch:{ Throwable -> 0x0143 }
    L_0x0100:
        r5 = r9.jpegOrientation;	 Catch:{ Throwable -> 0x0143 }
        r7 = -1;	 Catch:{ Throwable -> 0x0143 }
        if (r5 == r7) goto L_0x011b;	 Catch:{ Throwable -> 0x0143 }
    L_0x0105:
        r5 = r2.facing;	 Catch:{ Throwable -> 0x0143 }
        if (r5 != r0) goto L_0x0113;	 Catch:{ Throwable -> 0x0143 }
    L_0x0109:
        r5 = r2.orientation;	 Catch:{ Throwable -> 0x0143 }
        r7 = r9.jpegOrientation;	 Catch:{ Throwable -> 0x0143 }
        r5 = r5 - r7;	 Catch:{ Throwable -> 0x0143 }
        r5 = r5 + 360;	 Catch:{ Throwable -> 0x0143 }
        r5 = r5 % 360;	 Catch:{ Throwable -> 0x0143 }
        goto L_0x011c;	 Catch:{ Throwable -> 0x0143 }
    L_0x0113:
        r5 = r2.orientation;	 Catch:{ Throwable -> 0x0143 }
        r7 = r9.jpegOrientation;	 Catch:{ Throwable -> 0x0143 }
        r5 = r5 + r7;	 Catch:{ Throwable -> 0x0143 }
        r5 = r5 % 360;	 Catch:{ Throwable -> 0x0143 }
        goto L_0x011c;
    L_0x011b:
        r5 = r6;
    L_0x011c:
        r3.setRotation(r5);	 Catch:{ Exception -> 0x0132 }
        r2 = r2.facing;	 Catch:{ Exception -> 0x0132 }
        if (r2 != r0) goto L_0x012d;	 Catch:{ Exception -> 0x0132 }
    L_0x0123:
        r2 = 360 - r4;	 Catch:{ Exception -> 0x0132 }
        r2 = r2 % 360;	 Catch:{ Exception -> 0x0132 }
        if (r2 != r5) goto L_0x012a;	 Catch:{ Exception -> 0x0132 }
    L_0x0129:
        r6 = r0;	 Catch:{ Exception -> 0x0132 }
    L_0x012a:
        r9.sameTakePictureOrientation = r6;	 Catch:{ Exception -> 0x0132 }
        goto L_0x0132;	 Catch:{ Exception -> 0x0132 }
    L_0x012d:
        if (r4 != r5) goto L_0x0130;	 Catch:{ Exception -> 0x0132 }
    L_0x012f:
        r6 = r0;	 Catch:{ Exception -> 0x0132 }
    L_0x0130:
        r9.sameTakePictureOrientation = r6;	 Catch:{ Exception -> 0x0132 }
    L_0x0132:
        r2 = "off";	 Catch:{ Throwable -> 0x0143 }
        r3.setFlashMode(r2);	 Catch:{ Throwable -> 0x0143 }
        r1.setParameters(r3);	 Catch:{ Exception -> 0x013a }
    L_0x013a:
        r1 = r3.getMaxNumMeteringAreas();	 Catch:{ Throwable -> 0x0143 }
        if (r1 <= 0) goto L_0x0147;	 Catch:{ Throwable -> 0x0143 }
    L_0x0140:
        r9.meteringAreaSupported = r0;	 Catch:{ Throwable -> 0x0143 }
        goto L_0x0147;
    L_0x0143:
        r0 = move-exception;
        org.telegram.messenger.FileLog.m3e(r0);
    L_0x0147:
        return;
        */
        throw new UnsupportedOperationException("Method not decompiled: org.telegram.messenger.camera.CameraSession.configureRoundCamera():void");
    }

    protected void configurePhotoCamera() {
        /* JADX: method processing error */
/*
Error: java.lang.NullPointerException
	at jadx.core.dex.visitors.regions.ProcessTryCatchRegions.searchTryCatchDominators(ProcessTryCatchRegions.java:75)
	at jadx.core.dex.visitors.regions.ProcessTryCatchRegions.process(ProcessTryCatchRegions.java:45)
	at jadx.core.dex.visitors.regions.RegionMakerVisitor.postProcessRegions(RegionMakerVisitor.java:63)
	at jadx.core.dex.visitors.regions.RegionMakerVisitor.visit(RegionMakerVisitor.java:58)
	at jadx.core.dex.visitors.DepthTraversal.visit(DepthTraversal.java:31)
	at jadx.core.dex.visitors.DepthTraversal.visit(DepthTraversal.java:17)
	at jadx.core.ProcessClass.process(ProcessClass.java:34)
	at jadx.core.ProcessClass.processDependencies(ProcessClass.java:60)
	at jadx.core.ProcessClass.process(ProcessClass.java:39)
	at jadx.api.JadxDecompiler.processClass(JadxDecompiler.java:282)
	at jadx.api.JavaClass.decompile(JavaClass.java:62)
	at jadx.api.JadxDecompiler.lambda$appendSourcesSave$0(JadxDecompiler.java:200)
*/
        /*
        r9 = this;
        r0 = r9.cameraInfo;	 Catch:{ Throwable -> 0x00e1 }
        r0 = r0.camera;	 Catch:{ Throwable -> 0x00e1 }
        if (r0 == 0) goto L_0x00e5;	 Catch:{ Throwable -> 0x00e1 }
    L_0x0006:
        r1 = new android.hardware.Camera$CameraInfo;	 Catch:{ Throwable -> 0x00e1 }
        r1.<init>();	 Catch:{ Throwable -> 0x00e1 }
        r2 = 0;
        r3 = r0.getParameters();	 Catch:{ Exception -> 0x0012 }
        r2 = r3;
        goto L_0x0016;
    L_0x0012:
        r3 = move-exception;
        org.telegram.messenger.FileLog.m3e(r3);	 Catch:{ Throwable -> 0x00e1 }
    L_0x0016:
        r3 = r9.cameraInfo;	 Catch:{ Throwable -> 0x00e1 }
        r3 = r3.getCameraId();	 Catch:{ Throwable -> 0x00e1 }
        android.hardware.Camera.getCameraInfo(r3, r1);	 Catch:{ Throwable -> 0x00e1 }
        r3 = 1;	 Catch:{ Throwable -> 0x00e1 }
        r4 = r9.getDisplayOrientation(r1, r3);	 Catch:{ Throwable -> 0x00e1 }
        r5 = "samsung";	 Catch:{ Throwable -> 0x00e1 }
        r6 = android.os.Build.MANUFACTURER;	 Catch:{ Throwable -> 0x00e1 }
        r5 = r5.equals(r6);	 Catch:{ Throwable -> 0x00e1 }
        r6 = 0;	 Catch:{ Throwable -> 0x00e1 }
        if (r5 == 0) goto L_0x003b;	 Catch:{ Throwable -> 0x00e1 }
    L_0x002f:
        r5 = "sf2wifixx";	 Catch:{ Throwable -> 0x00e1 }
        r7 = android.os.Build.PRODUCT;	 Catch:{ Throwable -> 0x00e1 }
        r5 = r5.equals(r7);	 Catch:{ Throwable -> 0x00e1 }
        if (r5 == 0) goto L_0x003b;	 Catch:{ Throwable -> 0x00e1 }
    L_0x0039:
        r5 = r6;	 Catch:{ Throwable -> 0x00e1 }
        goto L_0x0065;	 Catch:{ Throwable -> 0x00e1 }
    L_0x003b:
        r5 = 90;	 Catch:{ Throwable -> 0x00e1 }
        switch(r4) {
            case 0: goto L_0x0040;
            case 1: goto L_0x0048;
            case 2: goto L_0x0045;
            case 3: goto L_0x0042;
            default: goto L_0x0040;
        };	 Catch:{ Throwable -> 0x00e1 }
    L_0x0040:
        r7 = r6;	 Catch:{ Throwable -> 0x00e1 }
        goto L_0x0049;	 Catch:{ Throwable -> 0x00e1 }
    L_0x0042:
        r7 = 270; // 0x10e float:3.78E-43 double:1.334E-321;	 Catch:{ Throwable -> 0x00e1 }
        goto L_0x0049;	 Catch:{ Throwable -> 0x00e1 }
    L_0x0045:
        r7 = 180; // 0xb4 float:2.52E-43 double:8.9E-322;	 Catch:{ Throwable -> 0x00e1 }
        goto L_0x0049;	 Catch:{ Throwable -> 0x00e1 }
    L_0x0048:
        r7 = r5;	 Catch:{ Throwable -> 0x00e1 }
    L_0x0049:
        r8 = r1.orientation;	 Catch:{ Throwable -> 0x00e1 }
        r8 = r8 % r5;	 Catch:{ Throwable -> 0x00e1 }
        if (r8 == 0) goto L_0x0050;	 Catch:{ Throwable -> 0x00e1 }
    L_0x004e:
        r1.orientation = r6;	 Catch:{ Throwable -> 0x00e1 }
    L_0x0050:
        r5 = r1.facing;	 Catch:{ Throwable -> 0x00e1 }
        if (r5 != r3) goto L_0x005e;	 Catch:{ Throwable -> 0x00e1 }
    L_0x0054:
        r5 = r1.orientation;	 Catch:{ Throwable -> 0x00e1 }
        r5 = r5 + r7;	 Catch:{ Throwable -> 0x00e1 }
        r5 = r5 % 360;	 Catch:{ Throwable -> 0x00e1 }
        r5 = 360 - r5;	 Catch:{ Throwable -> 0x00e1 }
        r5 = r5 % 360;	 Catch:{ Throwable -> 0x00e1 }
        goto L_0x0065;	 Catch:{ Throwable -> 0x00e1 }
    L_0x005e:
        r5 = r1.orientation;	 Catch:{ Throwable -> 0x00e1 }
        r5 = r5 - r7;	 Catch:{ Throwable -> 0x00e1 }
        r5 = r5 + 360;	 Catch:{ Throwable -> 0x00e1 }
        r5 = r5 % 360;	 Catch:{ Throwable -> 0x00e1 }
    L_0x0065:
        r9.currentOrientation = r5;	 Catch:{ Throwable -> 0x00e1 }
        r0.setDisplayOrientation(r5);	 Catch:{ Throwable -> 0x00e1 }
        if (r2 == 0) goto L_0x00e5;	 Catch:{ Throwable -> 0x00e1 }
    L_0x006c:
        r5 = r9.previewSize;	 Catch:{ Throwable -> 0x00e1 }
        r5 = r5.getWidth();	 Catch:{ Throwable -> 0x00e1 }
        r7 = r9.previewSize;	 Catch:{ Throwable -> 0x00e1 }
        r7 = r7.getHeight();	 Catch:{ Throwable -> 0x00e1 }
        r2.setPreviewSize(r5, r7);	 Catch:{ Throwable -> 0x00e1 }
        r5 = r9.pictureSize;	 Catch:{ Throwable -> 0x00e1 }
        r5 = r5.getWidth();	 Catch:{ Throwable -> 0x00e1 }
        r7 = r9.pictureSize;	 Catch:{ Throwable -> 0x00e1 }
        r7 = r7.getHeight();	 Catch:{ Throwable -> 0x00e1 }
        r2.setPictureSize(r5, r7);	 Catch:{ Throwable -> 0x00e1 }
        r5 = r9.pictureFormat;	 Catch:{ Throwable -> 0x00e1 }
        r2.setPictureFormat(r5);	 Catch:{ Throwable -> 0x00e1 }
        r5 = "continuous-picture";	 Catch:{ Throwable -> 0x00e1 }
        r7 = r2.getSupportedFocusModes();	 Catch:{ Throwable -> 0x00e1 }
        r7 = r7.contains(r5);	 Catch:{ Throwable -> 0x00e1 }
        if (r7 == 0) goto L_0x009e;	 Catch:{ Throwable -> 0x00e1 }
    L_0x009b:
        r2.setFocusMode(r5);	 Catch:{ Throwable -> 0x00e1 }
    L_0x009e:
        r5 = r9.jpegOrientation;	 Catch:{ Throwable -> 0x00e1 }
        r7 = -1;	 Catch:{ Throwable -> 0x00e1 }
        if (r5 == r7) goto L_0x00b9;	 Catch:{ Throwable -> 0x00e1 }
    L_0x00a3:
        r5 = r1.facing;	 Catch:{ Throwable -> 0x00e1 }
        if (r5 != r3) goto L_0x00b1;	 Catch:{ Throwable -> 0x00e1 }
    L_0x00a7:
        r5 = r1.orientation;	 Catch:{ Throwable -> 0x00e1 }
        r7 = r9.jpegOrientation;	 Catch:{ Throwable -> 0x00e1 }
        r5 = r5 - r7;	 Catch:{ Throwable -> 0x00e1 }
        r5 = r5 + 360;	 Catch:{ Throwable -> 0x00e1 }
        r5 = r5 % 360;	 Catch:{ Throwable -> 0x00e1 }
        goto L_0x00ba;	 Catch:{ Throwable -> 0x00e1 }
    L_0x00b1:
        r5 = r1.orientation;	 Catch:{ Throwable -> 0x00e1 }
        r7 = r9.jpegOrientation;	 Catch:{ Throwable -> 0x00e1 }
        r5 = r5 + r7;	 Catch:{ Throwable -> 0x00e1 }
        r5 = r5 % 360;	 Catch:{ Throwable -> 0x00e1 }
        goto L_0x00ba;
    L_0x00b9:
        r5 = r6;
    L_0x00ba:
        r2.setRotation(r5);	 Catch:{ Exception -> 0x00d0 }
        r1 = r1.facing;	 Catch:{ Exception -> 0x00d0 }
        if (r1 != r3) goto L_0x00cb;	 Catch:{ Exception -> 0x00d0 }
    L_0x00c1:
        r1 = 360 - r4;	 Catch:{ Exception -> 0x00d0 }
        r1 = r1 % 360;	 Catch:{ Exception -> 0x00d0 }
        if (r1 != r5) goto L_0x00c8;	 Catch:{ Exception -> 0x00d0 }
    L_0x00c7:
        r6 = r3;	 Catch:{ Exception -> 0x00d0 }
    L_0x00c8:
        r9.sameTakePictureOrientation = r6;	 Catch:{ Exception -> 0x00d0 }
        goto L_0x00d0;	 Catch:{ Exception -> 0x00d0 }
    L_0x00cb:
        if (r4 != r5) goto L_0x00ce;	 Catch:{ Exception -> 0x00d0 }
    L_0x00cd:
        r6 = r3;	 Catch:{ Exception -> 0x00d0 }
    L_0x00ce:
        r9.sameTakePictureOrientation = r6;	 Catch:{ Exception -> 0x00d0 }
    L_0x00d0:
        r1 = r9.currentFlashMode;	 Catch:{ Throwable -> 0x00e1 }
        r2.setFlashMode(r1);	 Catch:{ Throwable -> 0x00e1 }
        r0.setParameters(r2);	 Catch:{ Exception -> 0x00d8 }
    L_0x00d8:
        r0 = r2.getMaxNumMeteringAreas();	 Catch:{ Throwable -> 0x00e1 }
        if (r0 <= 0) goto L_0x00e5;	 Catch:{ Throwable -> 0x00e1 }
    L_0x00de:
        r9.meteringAreaSupported = r3;	 Catch:{ Throwable -> 0x00e1 }
        goto L_0x00e5;
    L_0x00e1:
        r0 = move-exception;
        org.telegram.messenger.FileLog.m3e(r0);
    L_0x00e5:
        return;
        */
        throw new UnsupportedOperationException("Method not decompiled: org.telegram.messenger.camera.CameraSession.configurePhotoCamera():void");
    }

    protected void focusToRect(Rect rect, Rect rect2) {
        try {
            Camera camera = this.cameraInfo.camera;
            if (camera != null) {
                camera.cancelAutoFocus();
                Parameters parameters = null;
                try {
                    parameters = camera.getParameters();
                } catch (Throwable e) {
                    FileLog.m3e(e);
                }
                if (parameters != null) {
                    parameters.setFocusMode("auto");
                    List arrayList = new ArrayList();
                    arrayList.add(new Area(rect, 1000));
                    parameters.setFocusAreas(arrayList);
                    if (this.meteringAreaSupported != null) {
                        rect = new ArrayList();
                        rect.add(new Area(rect2, 1000));
                        parameters.setMeteringAreas(rect);
                    }
                    try {
                        camera.setParameters(parameters);
                        camera.autoFocus(this.autoFocusCallback);
                    } catch (Throwable e2) {
                        FileLog.m3e(e2);
                    }
                }
            }
        } catch (Throwable e22) {
            FileLog.m3e(e22);
        }
    }

    protected void configureRecorder(int i, MediaRecorder mediaRecorder) {
        CameraInfo cameraInfo = new CameraInfo();
        Camera.getCameraInfo(this.cameraInfo.cameraId, cameraInfo);
        getDisplayOrientation(cameraInfo, false);
        int i2 = this.jpegOrientation != -1 ? cameraInfo.facing == 1 ? ((cameraInfo.orientation - this.jpegOrientation) + 360) % 360 : (cameraInfo.orientation + this.jpegOrientation) % 360 : 0;
        mediaRecorder.setOrientationHint(i2);
        i2 = getHigh();
        boolean hasProfile = CamcorderProfile.hasProfile(this.cameraInfo.cameraId, i2);
        boolean hasProfile2 = CamcorderProfile.hasProfile(this.cameraInfo.cameraId, 0);
        if (hasProfile && (i == 1 || !hasProfile2)) {
            mediaRecorder.setProfile(CamcorderProfile.get(this.cameraInfo.cameraId, i2));
        } else if (hasProfile2) {
            mediaRecorder.setProfile(CamcorderProfile.get(this.cameraInfo.cameraId, 0));
        } else {
            throw new IllegalStateException("cannot find valid CamcorderProfile");
        }
        this.isVideo = true;
    }

    protected void stopVideoRecording() {
        this.isVideo = false;
        configurePhotoCamera();
    }

    private int getHigh() {
        return ("LGE".equals(Build.MANUFACTURER) && "g3_tmo_us".equals(Build.PRODUCT)) ? 4 : 1;
    }

    private int getDisplayOrientation(CameraInfo cameraInfo, boolean z) {
        int i = 0;
        switch (((WindowManager) ApplicationLoader.applicationContext.getSystemService("window")).getDefaultDisplay().getRotation()) {
            case 0:
                break;
            case 1:
                i = 90;
                break;
            case 2:
                i = 180;
                break;
            case 3:
                i = 270;
                break;
            default:
                break;
        }
        if (cameraInfo.facing != 1) {
            return ((cameraInfo.orientation - i) + 360) % 360;
        }
        cameraInfo = (360 - ((cameraInfo.orientation + i) % 360)) % 360;
        if (!z && cameraInfo == 90) {
            cameraInfo = 270;
        }
        if (!z && "Huawei".equals(Build.MANUFACTURER) && "angler".equals(Build.PRODUCT) && cameraInfo == 270) {
            return 90;
        }
        return cameraInfo;
    }

    public int getDisplayOrientation() {
        try {
            CameraInfo cameraInfo = new CameraInfo();
            Camera.getCameraInfo(this.cameraInfo.getCameraId(), cameraInfo);
            return getDisplayOrientation(cameraInfo, true);
        } catch (Throwable e) {
            FileLog.m3e(e);
            return 0;
        }
    }

    public void destroy() {
        this.initied = false;
        if (this.orientationEventListener != null) {
            this.orientationEventListener.disable();
            this.orientationEventListener = null;
        }
    }
}
