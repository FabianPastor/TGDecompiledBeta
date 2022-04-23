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
import org.telegram.messenger.BuildVars;
import org.telegram.messenger.FileLog;

public class CameraSession {
    public static final int ORIENTATION_HYSTERESIS = 5;
    private Camera.AutoFocusCallback autoFocusCallback = CameraSession$$ExternalSyntheticLambda0.INSTANCE;
    protected CameraInfo cameraInfo;
    private String currentFlashMode;
    private int currentOrientation;
    private float currentZoom;
    private boolean destroyed;
    private int diffOrientation;
    private int displayOrientation;
    private boolean flipFront = true;
    Camera.CameraInfo info = new Camera.CameraInfo();
    private int infoCameraId = -1;
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

    /* access modifiers changed from: private */
    public static /* synthetic */ void lambda$new$0(boolean z, Camera camera) {
    }

    public CameraSession(CameraInfo cameraInfo2, Size size, Size size2, int i, boolean z) {
        this.previewSize = size;
        this.pictureSize = size2;
        this.pictureFormat = i;
        this.cameraInfo = cameraInfo2;
        this.isRound = z;
        this.currentFlashMode = ApplicationLoader.applicationContext.getSharedPreferences("camera", 0).getString(this.cameraInfo.frontCamera != 0 ? "flashMode_front" : "flashMode", "off");
        AnonymousClass1 r3 = new OrientationEventListener(ApplicationLoader.applicationContext) {
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
        this.orientationEventListener = r3;
        if (r3.canDetectOrientation()) {
            this.orientationEventListener.enable();
            return;
        }
        this.orientationEventListener.disable();
        this.orientationEventListener = null;
    }

    private void updateCameraInfo() {
        if (this.infoCameraId != this.cameraInfo.getCameraId()) {
            int cameraId = this.cameraInfo.getCameraId();
            this.infoCameraId = cameraId;
            Camera.getCameraInfo(cameraId, this.info);
        }
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
    public void configureRoundCamera(boolean z) {
        int i;
        try {
            this.isVideo = true;
            Camera camera = this.cameraInfo.camera;
            if (camera != null) {
                Camera.Parameters parameters = null;
                parameters = camera.getParameters();
                updateCameraInfo();
                updateRotation();
                if (parameters != null) {
                    if (z) {
                        if (BuildVars.LOGS_ENABLED) {
                            FileLog.d("set preview size = " + this.previewSize.getWidth() + " " + this.previewSize.getHeight());
                        }
                    }
                    parameters.setPreviewSize(this.previewSize.getWidth(), this.previewSize.getHeight());
                    if (z && BuildVars.LOGS_ENABLED) {
                        FileLog.d("set picture size = " + this.pictureSize.getWidth() + " " + this.pictureSize.getHeight());
                    }
                    parameters.setPictureSize(this.pictureSize.getWidth(), this.pictureSize.getHeight());
                    parameters.setPictureFormat(this.pictureFormat);
                    parameters.setRecordingHint(true);
                    this.maxZoom = parameters.getMaxZoom();
                    if (parameters.getSupportedFocusModes().contains("continuous-video")) {
                        parameters.setFocusMode("continuous-video");
                    } else if (parameters.getSupportedFocusModes().contains("auto")) {
                        parameters.setFocusMode("auto");
                    }
                    int i2 = this.jpegOrientation;
                    boolean z2 = false;
                    if (i2 != -1) {
                        Camera.CameraInfo cameraInfo2 = this.info;
                        i = cameraInfo2.facing == 1 ? ((cameraInfo2.orientation - i2) + 360) % 360 : (cameraInfo2.orientation + i2) % 360;
                    } else {
                        i = 0;
                    }
                    try {
                        parameters.setRotation(i);
                        if (this.info.facing == 1) {
                            if ((360 - this.displayOrientation) % 360 == i) {
                                z2 = true;
                            }
                            this.sameTakePictureOrientation = z2;
                        } else {
                            if (this.displayOrientation == i) {
                                z2 = true;
                            }
                            this.sameTakePictureOrientation = z2;
                        }
                    } catch (Exception unused) {
                    }
                    parameters.setFlashMode("off");
                    parameters.setZoom((int) (this.currentZoom * ((float) this.maxZoom)));
                    camera.setParameters(parameters);
                    if (parameters.getMaxNumMeteringAreas() > 0) {
                        this.meteringAreaSupported = true;
                    }
                }
            }
        } catch (Exception e) {
            throw new RuntimeException(e);
        } catch (Exception e2) {
            FileLog.e((Throwable) e2);
        } catch (Throwable th) {
            FileLog.e(th);
        }
    }

    /* JADX WARNING: Removed duplicated region for block: B:26:0x0050  */
    /* JADX WARNING: Removed duplicated region for block: B:29:0x0056  */
    /* JADX WARNING: Removed duplicated region for block: B:30:0x0061  */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public void updateRotation() {
        /*
            r7 = this;
            org.telegram.messenger.camera.CameraInfo r0 = r7.cameraInfo
            if (r0 != 0) goto L_0x0005
            return
        L_0x0005:
            r7.updateCameraInfo()     // Catch:{ all -> 0x0080 }
            boolean r0 = r7.destroyed
            if (r0 == 0) goto L_0x000e
            r0 = 0
            goto L_0x0012
        L_0x000e:
            org.telegram.messenger.camera.CameraInfo r0 = r7.cameraInfo
            android.hardware.Camera r0 = r0.camera
        L_0x0012:
            android.hardware.Camera$CameraInfo r1 = r7.info
            r2 = 1
            int r1 = r7.getDisplayOrientation(r1, r2)
            r7.displayOrientation = r1
            java.lang.String r1 = android.os.Build.MANUFACTURER
            java.lang.String r3 = "samsung"
            boolean r1 = r3.equals(r1)
            r3 = 0
            if (r1 == 0) goto L_0x0031
            java.lang.String r1 = android.os.Build.PRODUCT
            java.lang.String r4 = "sf2wifixx"
            boolean r1 = r4.equals(r1)
            if (r1 == 0) goto L_0x0031
            goto L_0x0069
        L_0x0031:
            int r1 = r7.displayOrientation
            r4 = 90
            if (r1 == 0) goto L_0x003f
            if (r1 == r2) goto L_0x0047
            r5 = 2
            if (r1 == r5) goto L_0x0044
            r5 = 3
            if (r1 == r5) goto L_0x0041
        L_0x003f:
            r1 = 0
            goto L_0x0049
        L_0x0041:
            r1 = 270(0x10e, float:3.78E-43)
            goto L_0x0049
        L_0x0044:
            r1 = 180(0xb4, float:2.52E-43)
            goto L_0x0049
        L_0x0047:
            r1 = 90
        L_0x0049:
            android.hardware.Camera$CameraInfo r5 = r7.info
            int r6 = r5.orientation
            int r6 = r6 % r4
            if (r6 == 0) goto L_0x0052
            r5.orientation = r3
        L_0x0052:
            int r3 = r5.facing
            if (r3 != r2) goto L_0x0061
            int r2 = r5.orientation
            int r2 = r2 + r1
            int r2 = r2 % 360
            int r1 = 360 - r2
            int r1 = r1 % 360
            r3 = r1
            goto L_0x0069
        L_0x0061:
            int r2 = r5.orientation
            int r2 = r2 - r1
            int r2 = r2 + 360
            int r2 = r2 % 360
            r3 = r2
        L_0x0069:
            r7.currentOrientation = r3
            if (r0 == 0) goto L_0x0072
            r0.setDisplayOrientation(r3)     // Catch:{ all -> 0x0071 }
            goto L_0x0072
        L_0x0071:
        L_0x0072:
            int r0 = r7.currentOrientation
            int r1 = r7.displayOrientation
            int r0 = r0 - r1
            r7.diffOrientation = r0
            if (r0 >= 0) goto L_0x007f
            int r0 = r0 + 360
            r7.diffOrientation = r0
        L_0x007f:
            return
        L_0x0080:
            r0 = move-exception
            org.telegram.messenger.FileLog.e((java.lang.Throwable) r0)
            return
        */
        throw new UnsupportedOperationException("Method not decompiled: org.telegram.messenger.camera.CameraSession.updateRotation():void");
    }

    /* access modifiers changed from: protected */
    /* JADX WARNING: Can't wrap try/catch for region: R(12:14|(4:16|(1:20)|21|(1:23))(2:24|(1:26))|27|(2:29|(1:31)(1:32))(1:33)|34|35|(3:37|(1:39)|40)(3:41|(1:43)|44)|45|46|(1:48)(1:49)|50|(3:51|52|60)) */
    /* JADX WARNING: Missing exception handler attribute for start block: B:45:0x00cb */
    /* JADX WARNING: Removed duplicated region for block: B:48:0x00cf A[Catch:{ Exception -> 0x000e, all -> 0x00db }] */
    /* JADX WARNING: Removed duplicated region for block: B:49:0x00d2 A[Catch:{ Exception -> 0x000e, all -> 0x00db }] */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public void configurePhotoCamera() {
        /*
            r7 = this;
            java.lang.String r0 = "barcode"
            org.telegram.messenger.camera.CameraInfo r1 = r7.cameraInfo     // Catch:{ all -> 0x00db }
            android.hardware.Camera r1 = r1.camera     // Catch:{ all -> 0x00db }
            if (r1 == 0) goto L_0x00df
            r2 = 0
            android.hardware.Camera$Parameters r2 = r1.getParameters()     // Catch:{ Exception -> 0x000e }
            goto L_0x0012
        L_0x000e:
            r3 = move-exception
            org.telegram.messenger.FileLog.e((java.lang.Throwable) r3)     // Catch:{ all -> 0x00db }
        L_0x0012:
            r7.updateCameraInfo()     // Catch:{ all -> 0x00db }
            r7.updateRotation()     // Catch:{ all -> 0x00db }
            int r3 = r7.currentOrientation     // Catch:{ all -> 0x00db }
            int r4 = r7.displayOrientation     // Catch:{ all -> 0x00db }
            int r3 = r3 - r4
            r7.diffOrientation = r3     // Catch:{ all -> 0x00db }
            if (r3 >= 0) goto L_0x0025
            int r3 = r3 + 360
            r7.diffOrientation = r3     // Catch:{ all -> 0x00db }
        L_0x0025:
            if (r2 == 0) goto L_0x00df
            org.telegram.messenger.camera.Size r3 = r7.previewSize     // Catch:{ all -> 0x00db }
            int r3 = r3.getWidth()     // Catch:{ all -> 0x00db }
            org.telegram.messenger.camera.Size r4 = r7.previewSize     // Catch:{ all -> 0x00db }
            int r4 = r4.getHeight()     // Catch:{ all -> 0x00db }
            r2.setPreviewSize(r3, r4)     // Catch:{ all -> 0x00db }
            org.telegram.messenger.camera.Size r3 = r7.pictureSize     // Catch:{ all -> 0x00db }
            int r3 = r3.getWidth()     // Catch:{ all -> 0x00db }
            org.telegram.messenger.camera.Size r4 = r7.pictureSize     // Catch:{ all -> 0x00db }
            int r4 = r4.getHeight()     // Catch:{ all -> 0x00db }
            r2.setPictureSize(r3, r4)     // Catch:{ all -> 0x00db }
            int r3 = r7.pictureFormat     // Catch:{ all -> 0x00db }
            r2.setPictureFormat(r3)     // Catch:{ all -> 0x00db }
            r3 = 100
            r2.setJpegQuality(r3)     // Catch:{ all -> 0x00db }
            r2.setJpegThumbnailQuality(r3)     // Catch:{ all -> 0x00db }
            int r3 = r2.getMaxZoom()     // Catch:{ all -> 0x00db }
            r7.maxZoom = r3     // Catch:{ all -> 0x00db }
            float r4 = r7.currentZoom     // Catch:{ all -> 0x00db }
            float r3 = (float) r3     // Catch:{ all -> 0x00db }
            float r4 = r4 * r3
            int r3 = (int) r4     // Catch:{ all -> 0x00db }
            r2.setZoom(r3)     // Catch:{ all -> 0x00db }
            boolean r3 = r7.optimizeForBarcode     // Catch:{ all -> 0x00db }
            if (r3 == 0) goto L_0x0084
            java.util.List r3 = r2.getSupportedSceneModes()     // Catch:{ all -> 0x00db }
            if (r3 == 0) goto L_0x0074
            boolean r3 = r3.contains(r0)     // Catch:{ all -> 0x00db }
            if (r3 == 0) goto L_0x0074
            r2.setSceneMode(r0)     // Catch:{ all -> 0x00db }
        L_0x0074:
            java.lang.String r0 = "continuous-video"
            java.util.List r3 = r2.getSupportedFocusModes()     // Catch:{ all -> 0x00db }
            boolean r3 = r3.contains(r0)     // Catch:{ all -> 0x00db }
            if (r3 == 0) goto L_0x0093
            r2.setFocusMode(r0)     // Catch:{ all -> 0x00db }
            goto L_0x0093
        L_0x0084:
            java.lang.String r0 = "continuous-picture"
            java.util.List r3 = r2.getSupportedFocusModes()     // Catch:{ all -> 0x00db }
            boolean r3 = r3.contains(r0)     // Catch:{ all -> 0x00db }
            if (r3 == 0) goto L_0x0093
            r2.setFocusMode(r0)     // Catch:{ all -> 0x00db }
        L_0x0093:
            int r0 = r7.jpegOrientation     // Catch:{ all -> 0x00db }
            r3 = -1
            r4 = 0
            r5 = 1
            if (r0 == r3) goto L_0x00ae
            android.hardware.Camera$CameraInfo r3 = r7.info     // Catch:{ all -> 0x00db }
            int r6 = r3.facing     // Catch:{ all -> 0x00db }
            if (r6 != r5) goto L_0x00a8
            int r3 = r3.orientation     // Catch:{ all -> 0x00db }
            int r3 = r3 - r0
            int r3 = r3 + 360
            int r3 = r3 % 360
            goto L_0x00af
        L_0x00a8:
            int r3 = r3.orientation     // Catch:{ all -> 0x00db }
            int r3 = r3 + r0
            int r3 = r3 % 360
            goto L_0x00af
        L_0x00ae:
            r3 = 0
        L_0x00af:
            r2.setRotation(r3)     // Catch:{ Exception -> 0x00cb }
            android.hardware.Camera$CameraInfo r0 = r7.info     // Catch:{ Exception -> 0x00cb }
            int r0 = r0.facing     // Catch:{ Exception -> 0x00cb }
            if (r0 != r5) goto L_0x00c4
            int r0 = r7.displayOrientation     // Catch:{ Exception -> 0x00cb }
            int r0 = 360 - r0
            int r0 = r0 % 360
            if (r0 != r3) goto L_0x00c1
            r4 = 1
        L_0x00c1:
            r7.sameTakePictureOrientation = r4     // Catch:{ Exception -> 0x00cb }
            goto L_0x00cb
        L_0x00c4:
            int r0 = r7.displayOrientation     // Catch:{ Exception -> 0x00cb }
            if (r0 != r3) goto L_0x00c9
            r4 = 1
        L_0x00c9:
            r7.sameTakePictureOrientation = r4     // Catch:{ Exception -> 0x00cb }
        L_0x00cb:
            boolean r0 = r7.useTorch     // Catch:{ all -> 0x00db }
            if (r0 == 0) goto L_0x00d2
            java.lang.String r0 = "torch"
            goto L_0x00d4
        L_0x00d2:
            java.lang.String r0 = r7.currentFlashMode     // Catch:{ all -> 0x00db }
        L_0x00d4:
            r2.setFlashMode(r0)     // Catch:{ all -> 0x00db }
            r1.setParameters(r2)     // Catch:{ Exception -> 0x00df }
            goto L_0x00df
        L_0x00db:
            r0 = move-exception
            org.telegram.messenger.FileLog.e((java.lang.Throwable) r0)
        L_0x00df:
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
        updateCameraInfo();
        int i3 = this.jpegOrientation;
        if (i3 != -1) {
            Camera.CameraInfo cameraInfo2 = this.info;
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
            updateCameraInfo();
            return getDisplayOrientation(this.info, true);
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
        this.destroyed = true;
        OrientationEventListener orientationEventListener2 = this.orientationEventListener;
        if (orientationEventListener2 != null) {
            orientationEventListener2.disable();
            this.orientationEventListener = null;
        }
    }
}
