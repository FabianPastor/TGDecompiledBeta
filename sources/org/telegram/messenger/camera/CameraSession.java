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
    private AutoFocusCallback autoFocusCallback = new AutoFocusCallback() {
        public void onAutoFocus(boolean success, Camera camera) {
            if (success) {
            }
        }
    };
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
    private boolean meteringAreaSupported;
    private OrientationEventListener orientationEventListener;
    private final int pictureFormat;
    private final Size pictureSize;
    private final Size previewSize;
    private boolean sameTakePictureOrientation;

    public CameraSession(CameraInfo info, Size preview, Size picture, int format) {
        this.previewSize = preview;
        this.pictureSize = picture;
        this.pictureFormat = format;
        this.cameraInfo = info;
        this.currentFlashMode = ApplicationLoader.applicationContext.getSharedPreferences("camera", 0).getString(this.cameraInfo.frontCamera != 0 ? "flashMode_front" : "flashMode", "off");
        this.orientationEventListener = new OrientationEventListener(ApplicationLoader.applicationContext) {
            public void onOrientationChanged(int orientation) {
                if (CameraSession.this.orientationEventListener != null && CameraSession.this.initied && orientation != -1) {
                    CameraSession.this.jpegOrientation = CameraSession.this.roundOrientation(orientation, CameraSession.this.jpegOrientation);
                    int rotation = ((WindowManager) ApplicationLoader.applicationContext.getSystemService("window")).getDefaultDisplay().getRotation();
                    if (CameraSession.this.lastOrientation != CameraSession.this.jpegOrientation || rotation != CameraSession.this.lastDisplayOrientation) {
                        if (!CameraSession.this.isVideo) {
                            CameraSession.this.configurePhotoCamera();
                        }
                        CameraSession.this.lastDisplayOrientation = rotation;
                        CameraSession.this.lastOrientation = CameraSession.this.jpegOrientation;
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

    private int roundOrientation(int orientation, int orientationHistory) {
        boolean changeOrientation;
        if (orientationHistory == -1) {
            changeOrientation = true;
        } else {
            int dist = Math.abs(orientation - orientationHistory);
            changeOrientation = Math.min(dist, 360 - dist) >= 50;
        }
        if (changeOrientation) {
            return (((orientation + 45) / 90) * 90) % 360;
        }
        return orientationHistory;
    }

    public void checkFlashMode(String mode) {
        if (!CameraController.getInstance().availableFlashModes.contains(this.currentFlashMode)) {
            this.currentFlashMode = mode;
            configurePhotoCamera();
            ApplicationLoader.applicationContext.getSharedPreferences("camera", 0).edit().putString(this.cameraInfo.frontCamera != 0 ? "flashMode_front" : "flashMode", mode).commit();
        }
    }

    public void setCurrentFlashMode(String mode) {
        this.currentFlashMode = mode;
        configurePhotoCamera();
        ApplicationLoader.applicationContext.getSharedPreferences("camera", 0).edit().putString(this.cameraInfo.frontCamera != 0 ? "flashMode_front" : "flashMode", mode).commit();
    }

    public String getCurrentFlashMode() {
        return this.currentFlashMode;
    }

    public String getNextFlashMode() {
        ArrayList<String> modes = CameraController.getInstance().availableFlashModes;
        int a = 0;
        while (a < modes.size()) {
            if (!((String) modes.get(a)).equals(this.currentFlashMode)) {
                a++;
            } else if (a < modes.size() - 1) {
                return (String) modes.get(a + 1);
            } else {
                return (String) modes.get(0);
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

    public void setFlipFront(boolean value) {
        this.flipFront = value;
    }

    public int getWorldAngle() {
        return this.diffOrientation;
    }

    public boolean isSameTakePictureOrientation() {
        return this.sameTakePictureOrientation;
    }

    /* Access modifiers changed, original: protected */
    /* JADX WARNING: No exception handlers in catch block: Catch:{  } */
    public void configureRoundCamera() {
        /*
        r14 = this;
        r11 = 0;
        r10 = 1;
        r12 = 1;
        r14.isVideo = r12;	 Catch:{ Throwable -> 0x011c }
        r12 = r14.cameraInfo;	 Catch:{ Throwable -> 0x011c }
        r0 = r12.camera;	 Catch:{ Throwable -> 0x011c }
        if (r0 == 0) goto L_0x0115;
    L_0x000b:
        r6 = new android.hardware.Camera$CameraInfo;	 Catch:{ Throwable -> 0x011c }
        r6.<init>();	 Catch:{ Throwable -> 0x011c }
        r8 = 0;
        r8 = r0.getParameters();	 Catch:{ Exception -> 0x0116 }
    L_0x0015:
        r12 = r14.cameraInfo;	 Catch:{ Throwable -> 0x011c }
        r12 = r12.getCameraId();	 Catch:{ Throwable -> 0x011c }
        android.hardware.Camera.getCameraInfo(r12, r6);	 Catch:{ Throwable -> 0x011c }
        r12 = 1;
        r4 = r14.getDisplayOrientation(r6, r12);	 Catch:{ Throwable -> 0x011c }
        r12 = "samsung";
        r13 = android.os.Build.MANUFACTURER;	 Catch:{ Throwable -> 0x011c }
        r12 = r12.equals(r13);	 Catch:{ Throwable -> 0x011c }
        if (r12 == 0) goto L_0x0121;
    L_0x002e:
        r12 = "sf2wifixx";
        r13 = android.os.Build.PRODUCT;	 Catch:{ Throwable -> 0x011c }
        r12 = r12.equals(r13);	 Catch:{ Throwable -> 0x011c }
        if (r12 == 0) goto L_0x0121;
    L_0x0039:
        r1 = 0;
    L_0x003a:
        r14.currentOrientation = r1;	 Catch:{ Throwable -> 0x011c }
        r0.setDisplayOrientation(r1);	 Catch:{ Throwable -> 0x011c }
        r12 = r14.currentOrientation;	 Catch:{ Throwable -> 0x011c }
        r12 = r12 - r4;
        r14.diffOrientation = r12;	 Catch:{ Throwable -> 0x011c }
        if (r8 == 0) goto L_0x0115;
    L_0x0046:
        r12 = org.telegram.messenger.BuildVars.LOGS_ENABLED;	 Catch:{ Throwable -> 0x011c }
        if (r12 == 0) goto L_0x0078;
    L_0x004a:
        r12 = new java.lang.StringBuilder;	 Catch:{ Throwable -> 0x011c }
        r12.<init>();	 Catch:{ Throwable -> 0x011c }
        r13 = "set preview size = ";
        r12 = r12.append(r13);	 Catch:{ Throwable -> 0x011c }
        r13 = r14.previewSize;	 Catch:{ Throwable -> 0x011c }
        r13 = r13.getWidth();	 Catch:{ Throwable -> 0x011c }
        r12 = r12.append(r13);	 Catch:{ Throwable -> 0x011c }
        r13 = " ";
        r12 = r12.append(r13);	 Catch:{ Throwable -> 0x011c }
        r13 = r14.previewSize;	 Catch:{ Throwable -> 0x011c }
        r13 = r13.getHeight();	 Catch:{ Throwable -> 0x011c }
        r12 = r12.append(r13);	 Catch:{ Throwable -> 0x011c }
        r12 = r12.toString();	 Catch:{ Throwable -> 0x011c }
        org.telegram.messenger.FileLog.d(r12);	 Catch:{ Throwable -> 0x011c }
    L_0x0078:
        r12 = r14.previewSize;	 Catch:{ Throwable -> 0x011c }
        r12 = r12.getWidth();	 Catch:{ Throwable -> 0x011c }
        r13 = r14.previewSize;	 Catch:{ Throwable -> 0x011c }
        r13 = r13.getHeight();	 Catch:{ Throwable -> 0x011c }
        r8.setPreviewSize(r12, r13);	 Catch:{ Throwable -> 0x011c }
        r12 = org.telegram.messenger.BuildVars.LOGS_ENABLED;	 Catch:{ Throwable -> 0x011c }
        if (r12 == 0) goto L_0x00b9;
    L_0x008b:
        r12 = new java.lang.StringBuilder;	 Catch:{ Throwable -> 0x011c }
        r12.<init>();	 Catch:{ Throwable -> 0x011c }
        r13 = "set picture size = ";
        r12 = r12.append(r13);	 Catch:{ Throwable -> 0x011c }
        r13 = r14.pictureSize;	 Catch:{ Throwable -> 0x011c }
        r13 = r13.getWidth();	 Catch:{ Throwable -> 0x011c }
        r12 = r12.append(r13);	 Catch:{ Throwable -> 0x011c }
        r13 = " ";
        r12 = r12.append(r13);	 Catch:{ Throwable -> 0x011c }
        r13 = r14.pictureSize;	 Catch:{ Throwable -> 0x011c }
        r13 = r13.getHeight();	 Catch:{ Throwable -> 0x011c }
        r12 = r12.append(r13);	 Catch:{ Throwable -> 0x011c }
        r12 = r12.toString();	 Catch:{ Throwable -> 0x011c }
        org.telegram.messenger.FileLog.d(r12);	 Catch:{ Throwable -> 0x011c }
    L_0x00b9:
        r12 = r14.pictureSize;	 Catch:{ Throwable -> 0x011c }
        r12 = r12.getWidth();	 Catch:{ Throwable -> 0x011c }
        r13 = r14.pictureSize;	 Catch:{ Throwable -> 0x011c }
        r13 = r13.getHeight();	 Catch:{ Throwable -> 0x011c }
        r8.setPictureSize(r12, r13);	 Catch:{ Throwable -> 0x011c }
        r12 = r14.pictureFormat;	 Catch:{ Throwable -> 0x011c }
        r8.setPictureFormat(r12);	 Catch:{ Throwable -> 0x011c }
        r12 = 1;
        r8.setRecordingHint(r12);	 Catch:{ Throwable -> 0x011c }
        r3 = "continuous-video";
        r12 = r8.getSupportedFocusModes();	 Catch:{ Throwable -> 0x011c }
        r12 = r12.contains(r3);	 Catch:{ Throwable -> 0x011c }
        if (r12 == 0) goto L_0x0152;
    L_0x00de:
        r8.setFocusMode(r3);	 Catch:{ Throwable -> 0x011c }
    L_0x00e1:
        r7 = 0;
        r12 = r14.jpegOrientation;	 Catch:{ Throwable -> 0x011c }
        r13 = -1;
        if (r12 == r13) goto L_0x00f4;
    L_0x00e7:
        r12 = r6.facing;	 Catch:{ Throwable -> 0x011c }
        if (r12 != r10) goto L_0x0164;
    L_0x00eb:
        r12 = r6.orientation;	 Catch:{ Throwable -> 0x011c }
        r13 = r14.jpegOrientation;	 Catch:{ Throwable -> 0x011c }
        r12 = r12 - r13;
        r12 = r12 + 360;
        r7 = r12 % 360;
    L_0x00f4:
        r8.setRotation(r7);	 Catch:{ Exception -> 0x0173 }
        r12 = r6.facing;	 Catch:{ Exception -> 0x0173 }
        if (r12 != r10) goto L_0x016e;
    L_0x00fb:
        r12 = 360 - r4;
        r12 = r12 % 360;
        if (r12 != r7) goto L_0x016c;
    L_0x0101:
        r14.sameTakePictureOrientation = r10;	 Catch:{ Exception -> 0x0173 }
    L_0x0103:
        r10 = "off";
        r8.setFlashMode(r10);	 Catch:{ Throwable -> 0x011c }
        r0.setParameters(r8);	 Catch:{ Exception -> 0x0177 }
    L_0x010c:
        r10 = r8.getMaxNumMeteringAreas();	 Catch:{ Throwable -> 0x011c }
        if (r10 <= 0) goto L_0x0115;
    L_0x0112:
        r10 = 1;
        r14.meteringAreaSupported = r10;	 Catch:{ Throwable -> 0x011c }
    L_0x0115:
        return;
    L_0x0116:
        r5 = move-exception;
        org.telegram.messenger.FileLog.e(r5);	 Catch:{ Throwable -> 0x011c }
        goto L_0x0015;
    L_0x011c:
        r5 = move-exception;
        org.telegram.messenger.FileLog.e(r5);
        goto L_0x0115;
    L_0x0121:
        r2 = 0;
        r9 = r4;
        switch(r9) {
            case 0: goto L_0x013f;
            case 1: goto L_0x0141;
            case 2: goto L_0x0144;
            case 3: goto L_0x0147;
            default: goto L_0x0126;
        };
    L_0x0126:
        r12 = r6.orientation;	 Catch:{ Throwable -> 0x011c }
        r12 = r12 % 90;
        if (r12 == 0) goto L_0x012f;
    L_0x012c:
        r12 = 0;
        r6.orientation = r12;	 Catch:{ Throwable -> 0x011c }
    L_0x012f:
        r12 = r6.facing;	 Catch:{ Throwable -> 0x011c }
        if (r12 != r10) goto L_0x014a;
    L_0x0133:
        r12 = r6.orientation;	 Catch:{ Throwable -> 0x011c }
        r12 = r12 + r2;
        r9 = r12 % 360;
        r12 = 360 - r9;
        r9 = r12 % 360;
    L_0x013c:
        r1 = r9;
        goto L_0x003a;
    L_0x013f:
        r2 = 0;
        goto L_0x0126;
    L_0x0141:
        r2 = 90;
        goto L_0x0126;
    L_0x0144:
        r2 = 180; // 0xb4 float:2.52E-43 double:8.9E-322;
        goto L_0x0126;
    L_0x0147:
        r2 = 270; // 0x10e float:3.78E-43 double:1.334E-321;
        goto L_0x0126;
    L_0x014a:
        r12 = r6.orientation;	 Catch:{ Throwable -> 0x011c }
        r12 = r12 - r2;
        r12 = r12 + 360;
        r9 = r12 % 360;
        goto L_0x013c;
    L_0x0152:
        r3 = "auto";
        r12 = r8.getSupportedFocusModes();	 Catch:{ Throwable -> 0x011c }
        r12 = r12.contains(r3);	 Catch:{ Throwable -> 0x011c }
        if (r12 == 0) goto L_0x00e1;
    L_0x015f:
        r8.setFocusMode(r3);	 Catch:{ Throwable -> 0x011c }
        goto L_0x00e1;
    L_0x0164:
        r12 = r6.orientation;	 Catch:{ Throwable -> 0x011c }
        r13 = r14.jpegOrientation;	 Catch:{ Throwable -> 0x011c }
        r12 = r12 + r13;
        r7 = r12 % 360;
        goto L_0x00f4;
    L_0x016c:
        r10 = r11;
        goto L_0x0101;
    L_0x016e:
        if (r4 != r7) goto L_0x0175;
    L_0x0170:
        r14.sameTakePictureOrientation = r10;	 Catch:{ Exception -> 0x0173 }
        goto L_0x0103;
    L_0x0173:
        r10 = move-exception;
        goto L_0x0103;
    L_0x0175:
        r10 = r11;
        goto L_0x0170;
    L_0x0177:
        r10 = move-exception;
        goto L_0x010c;
        */
        throw new UnsupportedOperationException("Method not decompiled: org.telegram.messenger.camera.CameraSession.configureRoundCamera():void");
    }

    /* Access modifiers changed, original: protected */
    /* JADX WARNING: No exception handlers in catch block: Catch:{  } */
    public void configurePhotoCamera() {
        /*
        r14 = this;
        r11 = 0;
        r10 = 1;
        r12 = r14.cameraInfo;	 Catch:{ Throwable -> 0x00ab }
        r0 = r12.camera;	 Catch:{ Throwable -> 0x00ab }
        if (r0 == 0) goto L_0x00a4;
    L_0x0008:
        r6 = new android.hardware.Camera$CameraInfo;	 Catch:{ Throwable -> 0x00ab }
        r6.<init>();	 Catch:{ Throwable -> 0x00ab }
        r8 = 0;
        r8 = r0.getParameters();	 Catch:{ Exception -> 0x00a5 }
    L_0x0012:
        r12 = r14.cameraInfo;	 Catch:{ Throwable -> 0x00ab }
        r12 = r12.getCameraId();	 Catch:{ Throwable -> 0x00ab }
        android.hardware.Camera.getCameraInfo(r12, r6);	 Catch:{ Throwable -> 0x00ab }
        r12 = 1;
        r4 = r14.getDisplayOrientation(r6, r12);	 Catch:{ Throwable -> 0x00ab }
        r12 = "samsung";
        r13 = android.os.Build.MANUFACTURER;	 Catch:{ Throwable -> 0x00ab }
        r12 = r12.equals(r13);	 Catch:{ Throwable -> 0x00ab }
        if (r12 == 0) goto L_0x00b0;
    L_0x002b:
        r12 = "sf2wifixx";
        r13 = android.os.Build.PRODUCT;	 Catch:{ Throwable -> 0x00ab }
        r12 = r12.equals(r13);	 Catch:{ Throwable -> 0x00ab }
        if (r12 == 0) goto L_0x00b0;
    L_0x0036:
        r1 = 0;
    L_0x0037:
        r14.currentOrientation = r1;	 Catch:{ Throwable -> 0x00ab }
        r0.setDisplayOrientation(r1);	 Catch:{ Throwable -> 0x00ab }
        if (r8 == 0) goto L_0x00a4;
    L_0x003e:
        r12 = r14.previewSize;	 Catch:{ Throwable -> 0x00ab }
        r12 = r12.getWidth();	 Catch:{ Throwable -> 0x00ab }
        r13 = r14.previewSize;	 Catch:{ Throwable -> 0x00ab }
        r13 = r13.getHeight();	 Catch:{ Throwable -> 0x00ab }
        r8.setPreviewSize(r12, r13);	 Catch:{ Throwable -> 0x00ab }
        r12 = r14.pictureSize;	 Catch:{ Throwable -> 0x00ab }
        r12 = r12.getWidth();	 Catch:{ Throwable -> 0x00ab }
        r13 = r14.pictureSize;	 Catch:{ Throwable -> 0x00ab }
        r13 = r13.getHeight();	 Catch:{ Throwable -> 0x00ab }
        r8.setPictureSize(r12, r13);	 Catch:{ Throwable -> 0x00ab }
        r12 = r14.pictureFormat;	 Catch:{ Throwable -> 0x00ab }
        r8.setPictureFormat(r12);	 Catch:{ Throwable -> 0x00ab }
        r3 = "continuous-picture";
        r12 = r8.getSupportedFocusModes();	 Catch:{ Throwable -> 0x00ab }
        r12 = r12.contains(r3);	 Catch:{ Throwable -> 0x00ab }
        if (r12 == 0) goto L_0x0071;
    L_0x006e:
        r8.setFocusMode(r3);	 Catch:{ Throwable -> 0x00ab }
    L_0x0071:
        r7 = 0;
        r12 = r14.jpegOrientation;	 Catch:{ Throwable -> 0x00ab }
        r13 = -1;
        if (r12 == r13) goto L_0x0084;
    L_0x0077:
        r12 = r6.facing;	 Catch:{ Throwable -> 0x00ab }
        if (r12 != r10) goto L_0x00e1;
    L_0x007b:
        r12 = r6.orientation;	 Catch:{ Throwable -> 0x00ab }
        r13 = r14.jpegOrientation;	 Catch:{ Throwable -> 0x00ab }
        r12 = r12 - r13;
        r12 = r12 + 360;
        r7 = r12 % 360;
    L_0x0084:
        r8.setRotation(r7);	 Catch:{ Exception -> 0x00f0 }
        r12 = r6.facing;	 Catch:{ Exception -> 0x00f0 }
        if (r12 != r10) goto L_0x00eb;
    L_0x008b:
        r12 = 360 - r4;
        r12 = r12 % 360;
        if (r12 != r7) goto L_0x00e9;
    L_0x0091:
        r14.sameTakePictureOrientation = r10;	 Catch:{ Exception -> 0x00f0 }
    L_0x0093:
        r10 = r14.currentFlashMode;	 Catch:{ Throwable -> 0x00ab }
        r8.setFlashMode(r10);	 Catch:{ Throwable -> 0x00ab }
        r0.setParameters(r8);	 Catch:{ Exception -> 0x00f4 }
    L_0x009b:
        r10 = r8.getMaxNumMeteringAreas();	 Catch:{ Throwable -> 0x00ab }
        if (r10 <= 0) goto L_0x00a4;
    L_0x00a1:
        r10 = 1;
        r14.meteringAreaSupported = r10;	 Catch:{ Throwable -> 0x00ab }
    L_0x00a4:
        return;
    L_0x00a5:
        r5 = move-exception;
        org.telegram.messenger.FileLog.e(r5);	 Catch:{ Throwable -> 0x00ab }
        goto L_0x0012;
    L_0x00ab:
        r5 = move-exception;
        org.telegram.messenger.FileLog.e(r5);
        goto L_0x00a4;
    L_0x00b0:
        r2 = 0;
        r9 = r4;
        switch(r9) {
            case 0: goto L_0x00ce;
            case 1: goto L_0x00d0;
            case 2: goto L_0x00d3;
            case 3: goto L_0x00d6;
            default: goto L_0x00b5;
        };
    L_0x00b5:
        r12 = r6.orientation;	 Catch:{ Throwable -> 0x00ab }
        r12 = r12 % 90;
        if (r12 == 0) goto L_0x00be;
    L_0x00bb:
        r12 = 0;
        r6.orientation = r12;	 Catch:{ Throwable -> 0x00ab }
    L_0x00be:
        r12 = r6.facing;	 Catch:{ Throwable -> 0x00ab }
        if (r12 != r10) goto L_0x00d9;
    L_0x00c2:
        r12 = r6.orientation;	 Catch:{ Throwable -> 0x00ab }
        r12 = r12 + r2;
        r9 = r12 % 360;
        r12 = 360 - r9;
        r9 = r12 % 360;
    L_0x00cb:
        r1 = r9;
        goto L_0x0037;
    L_0x00ce:
        r2 = 0;
        goto L_0x00b5;
    L_0x00d0:
        r2 = 90;
        goto L_0x00b5;
    L_0x00d3:
        r2 = 180; // 0xb4 float:2.52E-43 double:8.9E-322;
        goto L_0x00b5;
    L_0x00d6:
        r2 = 270; // 0x10e float:3.78E-43 double:1.334E-321;
        goto L_0x00b5;
    L_0x00d9:
        r12 = r6.orientation;	 Catch:{ Throwable -> 0x00ab }
        r12 = r12 - r2;
        r12 = r12 + 360;
        r9 = r12 % 360;
        goto L_0x00cb;
    L_0x00e1:
        r12 = r6.orientation;	 Catch:{ Throwable -> 0x00ab }
        r13 = r14.jpegOrientation;	 Catch:{ Throwable -> 0x00ab }
        r12 = r12 + r13;
        r7 = r12 % 360;
        goto L_0x0084;
    L_0x00e9:
        r10 = r11;
        goto L_0x0091;
    L_0x00eb:
        if (r4 != r7) goto L_0x00f2;
    L_0x00ed:
        r14.sameTakePictureOrientation = r10;	 Catch:{ Exception -> 0x00f0 }
        goto L_0x0093;
    L_0x00f0:
        r10 = move-exception;
        goto L_0x0093;
    L_0x00f2:
        r10 = r11;
        goto L_0x00ed;
    L_0x00f4:
        r10 = move-exception;
        goto L_0x009b;
        */
        throw new UnsupportedOperationException("Method not decompiled: org.telegram.messenger.camera.CameraSession.configurePhotoCamera():void");
    }

    /* Access modifiers changed, original: protected */
    public void focusToRect(Rect focusRect, Rect meteringRect) {
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
                    ArrayList<Area> meteringAreas = new ArrayList();
                    meteringAreas.add(new Area(focusRect, 1000));
                    parameters.setFocusAreas(meteringAreas);
                    if (this.meteringAreaSupported) {
                        meteringAreas = new ArrayList();
                        meteringAreas.add(new Area(meteringRect, 1000));
                        parameters.setMeteringAreas(meteringAreas);
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
    public void configureRecorder(int quality, MediaRecorder recorder) {
        CameraInfo info = new CameraInfo();
        Camera.getCameraInfo(this.cameraInfo.cameraId, info);
        int displayOrientation = getDisplayOrientation(info, false);
        int outputOrientation = 0;
        if (this.jpegOrientation != -1) {
            if (info.facing == 1) {
                outputOrientation = ((info.orientation - this.jpegOrientation) + 360) % 360;
            } else {
                outputOrientation = (info.orientation + this.jpegOrientation) % 360;
            }
        }
        recorder.setOrientationHint(outputOrientation);
        int highProfile = getHigh();
        boolean canGoHigh = CamcorderProfile.hasProfile(this.cameraInfo.cameraId, highProfile);
        boolean canGoLow = CamcorderProfile.hasProfile(this.cameraInfo.cameraId, 0);
        if (canGoHigh && (quality == 1 || !canGoLow)) {
            recorder.setProfile(CamcorderProfile.get(this.cameraInfo.cameraId, highProfile));
        } else if (canGoLow) {
            recorder.setProfile(CamcorderProfile.get(this.cameraInfo.cameraId, 0));
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
        if ("LGE".equals(Build.MANUFACTURER) && "g3_tmo_us".equals(Build.PRODUCT)) {
            return 4;
        }
        return 1;
    }

    private int getDisplayOrientation(CameraInfo info, boolean isStillCapture) {
        int degrees = 0;
        switch (((WindowManager) ApplicationLoader.applicationContext.getSystemService("window")).getDefaultDisplay().getRotation()) {
            case 0:
                degrees = 0;
                break;
            case 1:
                degrees = 90;
                break;
            case 2:
                degrees = 180;
                break;
            case 3:
                degrees = 270;
                break;
        }
        if (info.facing != 1) {
            return ((info.orientation - degrees) + 360) % 360;
        }
        int displayOrientation = (360 - ((info.orientation + degrees) % 360)) % 360;
        if (!isStillCapture && displayOrientation == 90) {
            displayOrientation = 270;
        }
        if (!isStillCapture && "Huawei".equals(Build.MANUFACTURER) && "angler".equals(Build.PRODUCT) && displayOrientation == 270) {
            return 90;
        }
        return displayOrientation;
    }

    public int getDisplayOrientation() {
        try {
            CameraInfo info = new CameraInfo();
            Camera.getCameraInfo(this.cameraInfo.getCameraId(), info);
            return getDisplayOrientation(info, true);
        } catch (Exception e) {
            FileLog.e(e);
            return 0;
        }
    }

    public void setPreviewCallback(PreviewCallback callback) {
        this.cameraInfo.camera.setPreviewCallback(callback);
    }

    public void setOneShotPreviewCallback(PreviewCallback callback) {
        if (this.cameraInfo != null && this.cameraInfo.camera != null) {
            this.cameraInfo.camera.setOneShotPreviewCallback(callback);
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
