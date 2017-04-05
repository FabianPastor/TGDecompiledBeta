package com.google.android.gms.vision;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.ImageFormat;
import android.graphics.SurfaceTexture;
import android.hardware.Camera;
import android.hardware.Camera.CameraInfo;
import android.hardware.Camera.Parameters;
import android.hardware.Camera.PreviewCallback;
import android.os.Build.VERSION;
import android.os.SystemClock;
import android.support.annotation.RequiresPermission;
import android.util.Log;
import android.view.SurfaceHolder;
import android.view.WindowManager;
import com.google.android.gms.common.images.Size;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import java.io.IOException;
import java.lang.Thread.State;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.telegram.tgnet.ConnectionsManager;

public class CameraSource {
    @SuppressLint({"InlinedApi"})
    public static final int CAMERA_FACING_BACK = 0;
    @SuppressLint({"InlinedApi"})
    public static final int CAMERA_FACING_FRONT = 1;
    private Context mContext;
    private int zzMA;
    private final Object zzbNM;
    private Camera zzbNN;
    private int zzbNO;
    private Size zzbNP;
    private float zzbNQ;
    private int zzbNR;
    private int zzbNS;
    private boolean zzbNT;
    private SurfaceTexture zzbNU;
    private boolean zzbNV;
    private Thread zzbNW;
    private zzb zzbNX;
    private Map<byte[], ByteBuffer> zzbNY;

    public static class Builder {
        private final Detector<?> zzbNZ;
        private CameraSource zzbOa = new CameraSource();

        public Builder(Context context, Detector<?> detector) {
            if (context == null) {
                throw new IllegalArgumentException("No context supplied.");
            } else if (detector == null) {
                throw new IllegalArgumentException("No detector supplied.");
            } else {
                this.zzbNZ = detector;
                this.zzbOa.mContext = context;
            }
        }

        public CameraSource build() {
            CameraSource cameraSource = this.zzbOa;
            CameraSource cameraSource2 = this.zzbOa;
            cameraSource2.getClass();
            cameraSource.zzbNX = new zzb(cameraSource2, this.zzbNZ);
            return this.zzbOa;
        }

        public Builder setAutoFocusEnabled(boolean z) {
            this.zzbOa.zzbNT = z;
            return this;
        }

        public Builder setFacing(int i) {
            if (i == 0 || i == 1) {
                this.zzbOa.zzbNO = i;
                return this;
            }
            throw new IllegalArgumentException("Invalid camera: " + i);
        }

        public Builder setRequestedFps(float f) {
            if (f <= 0.0f) {
                throw new IllegalArgumentException("Invalid fps: " + f);
            }
            this.zzbOa.zzbNQ = f;
            return this;
        }

        public Builder setRequestedPreviewSize(int i, int i2) {
            if (i <= 0 || i > 1000000 || i2 <= 0 || i2 > 1000000) {
                throw new IllegalArgumentException("Invalid preview size: " + i + "x" + i2);
            }
            this.zzbOa.zzbNR = i;
            this.zzbOa.zzbNS = i2;
            return this;
        }
    }

    public interface PictureCallback {
        void onPictureTaken(byte[] bArr);
    }

    public interface ShutterCallback {
        void onShutter();
    }

    private class zza implements PreviewCallback {
        final /* synthetic */ CameraSource zzbOb;

        private zza(CameraSource cameraSource) {
            this.zzbOb = cameraSource;
        }

        public void onPreviewFrame(byte[] bArr, Camera camera) {
            this.zzbOb.zzbNX.zza(bArr, camera);
        }
    }

    private class zzb implements Runnable {
        static final /* synthetic */ boolean $assertionsDisabled = (!CameraSource.class.desiredAssertionStatus());
        private boolean mActive = true;
        private long zzafe = SystemClock.elapsedRealtime();
        private Detector<?> zzbNZ;
        final /* synthetic */ CameraSource zzbOb;
        private long zzbOc;
        private int zzbOd = 0;
        private ByteBuffer zzbOe;
        private final Object zzrJ = new Object();

        zzb(CameraSource cameraSource, Detector<?> detector) {
            this.zzbOb = cameraSource;
            this.zzbNZ = detector;
        }

        @SuppressLint({"Assert"})
        void release() {
            if ($assertionsDisabled || this.zzbOb.zzbNW.getState() == State.TERMINATED) {
                this.zzbNZ.release();
                this.zzbNZ = null;
                return;
            }
            throw new AssertionError();
        }

        /* JADX WARNING: inconsistent code. */
        /* Code decompiled incorrectly, please refer to instructions dump. */
        @SuppressLint({"InlinedApi"})
        public void run() {
            while (true) {
                synchronized (this.zzrJ) {
                    while (this.mActive && this.zzbOe == null) {
                        try {
                            this.zzrJ.wait();
                        } catch (Throwable e) {
                            Log.d("CameraSource", "Frame processing loop terminated.", e);
                            return;
                        }
                    }
                    if (this.mActive) {
                        Frame build = new com.google.android.gms.vision.Frame.Builder().setImageData(this.zzbOe, this.zzbOb.zzbNP.getWidth(), this.zzbOb.zzbNP.getHeight(), 17).setId(this.zzbOd).setTimestampMillis(this.zzbOc).setRotation(this.zzbOb.zzMA).build();
                        ByteBuffer byteBuffer = this.zzbOe;
                        this.zzbOe = null;
                    } else {
                        return;
                    }
                }
            }
        }

        void setActive(boolean z) {
            synchronized (this.zzrJ) {
                this.mActive = z;
                this.zzrJ.notifyAll();
            }
        }

        void zza(byte[] bArr, Camera camera) {
            synchronized (this.zzrJ) {
                if (this.zzbOe != null) {
                    camera.addCallbackBuffer(this.zzbOe.array());
                    this.zzbOe = null;
                }
                if (this.zzbOb.zzbNY.containsKey(bArr)) {
                    this.zzbOc = SystemClock.elapsedRealtime() - this.zzafe;
                    this.zzbOd++;
                    this.zzbOe = (ByteBuffer) this.zzbOb.zzbNY.get(bArr);
                    this.zzrJ.notifyAll();
                    return;
                }
                Log.d("CameraSource", "Skipping frame. Could not find ByteBuffer associated with the image data from the camera.");
            }
        }
    }

    private class zzc implements android.hardware.Camera.PictureCallback {
        final /* synthetic */ CameraSource zzbOb;
        private PictureCallback zzbOf;

        private zzc(CameraSource cameraSource) {
            this.zzbOb = cameraSource;
        }

        public void onPictureTaken(byte[] bArr, Camera camera) {
            if (this.zzbOf != null) {
                this.zzbOf.onPictureTaken(bArr);
            }
            synchronized (this.zzbOb.zzbNM) {
                if (this.zzbOb.zzbNN != null) {
                    this.zzbOb.zzbNN.startPreview();
                }
            }
        }
    }

    private class zzd implements android.hardware.Camera.ShutterCallback {
        private ShutterCallback zzbOg;

        private zzd(CameraSource cameraSource) {
        }

        public void onShutter() {
            if (this.zzbOg != null) {
                this.zzbOg.onShutter();
            }
        }
    }

    static class zze {
        private Size zzbOh;
        private Size zzbOi;

        public zze(Camera.Size size, Camera.Size size2) {
            this.zzbOh = new Size(size.width, size.height);
            if (size2 != null) {
                this.zzbOi = new Size(size2.width, size2.height);
            }
        }

        public Size zzTL() {
            return this.zzbOh;
        }

        public Size zzTM() {
            return this.zzbOi;
        }
    }

    private CameraSource() {
        this.zzbNM = new Object();
        this.zzbNO = 0;
        this.zzbNQ = BitmapDescriptorFactory.HUE_ORANGE;
        this.zzbNR = 1024;
        this.zzbNS = 768;
        this.zzbNT = false;
        this.zzbNY = new HashMap();
    }

    @SuppressLint({"InlinedApi"})
    private Camera zzTK() {
        int zznP = zznP(this.zzbNO);
        if (zznP == -1) {
            throw new RuntimeException("Could not find requested camera.");
        }
        Camera open = Camera.open(zznP);
        zze zza = zza(open, this.zzbNR, this.zzbNS);
        if (zza == null) {
            throw new RuntimeException("Could not find suitable preview size.");
        }
        Size zzTM = zza.zzTM();
        this.zzbNP = zza.zzTL();
        int[] zza2 = zza(open, this.zzbNQ);
        if (zza2 == null) {
            throw new RuntimeException("Could not find suitable preview frames per second range.");
        }
        Parameters parameters = open.getParameters();
        if (zzTM != null) {
            parameters.setPictureSize(zzTM.getWidth(), zzTM.getHeight());
        }
        parameters.setPreviewSize(this.zzbNP.getWidth(), this.zzbNP.getHeight());
        parameters.setPreviewFpsRange(zza2[0], zza2[1]);
        parameters.setPreviewFormat(17);
        zza(open, parameters, zznP);
        if (this.zzbNT) {
            if (parameters.getSupportedFocusModes().contains("continuous-video")) {
                parameters.setFocusMode("continuous-video");
            } else {
                Log.i("CameraSource", "Camera auto focus is not supported on this device.");
            }
        }
        open.setParameters(parameters);
        open.setPreviewCallbackWithBuffer(new zza());
        open.addCallbackBuffer(zza(this.zzbNP));
        open.addCallbackBuffer(zza(this.zzbNP));
        open.addCallbackBuffer(zza(this.zzbNP));
        open.addCallbackBuffer(zza(this.zzbNP));
        return open;
    }

    static zze zza(Camera camera, int i, int i2) {
        List<zze> zza = zza(camera);
        zze com_google_android_gms_vision_CameraSource_zze = null;
        int i3 = ConnectionsManager.DEFAULT_DATACENTER_ID;
        for (zze com_google_android_gms_vision_CameraSource_zze2 : zza) {
            zze com_google_android_gms_vision_CameraSource_zze3;
            int i4;
            Size zzTL = com_google_android_gms_vision_CameraSource_zze2.zzTL();
            int abs = Math.abs(zzTL.getHeight() - i2) + Math.abs(zzTL.getWidth() - i);
            if (abs < i3) {
                int i5 = abs;
                com_google_android_gms_vision_CameraSource_zze3 = com_google_android_gms_vision_CameraSource_zze2;
                i4 = i5;
            } else {
                i4 = i3;
                com_google_android_gms_vision_CameraSource_zze3 = com_google_android_gms_vision_CameraSource_zze;
            }
            i3 = i4;
            com_google_android_gms_vision_CameraSource_zze = com_google_android_gms_vision_CameraSource_zze3;
        }
        return com_google_android_gms_vision_CameraSource_zze;
    }

    static List<zze> zza(Camera camera) {
        Parameters parameters = camera.getParameters();
        List<Camera.Size> supportedPreviewSizes = parameters.getSupportedPreviewSizes();
        List<Camera.Size> supportedPictureSizes = parameters.getSupportedPictureSizes();
        List<zze> arrayList = new ArrayList();
        for (Camera.Size size : supportedPreviewSizes) {
            float f = ((float) size.width) / ((float) size.height);
            for (Camera.Size size2 : supportedPictureSizes) {
                if (Math.abs(f - (((float) size2.width) / ((float) size2.height))) < 0.01f) {
                    arrayList.add(new zze(size, size2));
                    break;
                }
            }
        }
        if (arrayList.size() == 0) {
            Log.w("CameraSource", "No preview sizes have a corresponding same-aspect-ratio picture size");
            for (Camera.Size size3 : supportedPreviewSizes) {
                arrayList.add(new zze(size3, null));
            }
        }
        return arrayList;
    }

    private void zza(Camera camera, Parameters parameters, int i) {
        int i2;
        int rotation = ((WindowManager) this.mContext.getSystemService("window")).getDefaultDisplay().getRotation();
        switch (rotation) {
            case 0:
                rotation = 0;
                break;
            case 1:
                rotation = 90;
                break;
            case 2:
                rotation = 180;
                break;
            case 3:
                rotation = 270;
                break;
            default:
                Log.e("CameraSource", "Bad rotation value: " + rotation);
                rotation = 0;
                break;
        }
        CameraInfo cameraInfo = new CameraInfo();
        Camera.getCameraInfo(i, cameraInfo);
        if (cameraInfo.facing == 1) {
            i2 = (rotation + cameraInfo.orientation) % 360;
            rotation = (360 - i2) % 360;
        } else {
            rotation = ((cameraInfo.orientation - rotation) + 360) % 360;
            i2 = rotation;
        }
        this.zzMA = i2 / 90;
        camera.setDisplayOrientation(rotation);
        parameters.setRotation(i2);
    }

    @SuppressLint({"InlinedApi"})
    private byte[] zza(Size size) {
        Object obj = new byte[(((int) Math.ceil(((double) ((long) (ImageFormat.getBitsPerPixel(17) * (size.getHeight() * size.getWidth())))) / 8.0d)) + 1)];
        ByteBuffer wrap = ByteBuffer.wrap(obj);
        if (wrap.hasArray() && wrap.array() == obj) {
            this.zzbNY.put(obj, wrap);
            return obj;
        }
        throw new IllegalStateException("Failed to create valid buffer for camera source.");
    }

    @SuppressLint({"InlinedApi"})
    static int[] zza(Camera camera, float f) {
        int i = (int) (1000.0f * f);
        int[] iArr = null;
        int i2 = ConnectionsManager.DEFAULT_DATACENTER_ID;
        for (int[] iArr2 : camera.getParameters().getSupportedPreviewFpsRange()) {
            int[] iArr3;
            int i3;
            int abs = Math.abs(i - iArr2[0]) + Math.abs(i - iArr2[1]);
            if (abs < i2) {
                int i4 = abs;
                iArr3 = iArr2;
                i3 = i4;
            } else {
                i3 = i2;
                iArr3 = iArr;
            }
            i2 = i3;
            iArr = iArr3;
        }
        return iArr;
    }

    private static int zznP(int i) {
        CameraInfo cameraInfo = new CameraInfo();
        for (int i2 = 0; i2 < Camera.getNumberOfCameras(); i2++) {
            Camera.getCameraInfo(i2, cameraInfo);
            if (cameraInfo.facing == i) {
                return i2;
            }
        }
        return -1;
    }

    public int getCameraFacing() {
        return this.zzbNO;
    }

    public Size getPreviewSize() {
        return this.zzbNP;
    }

    public void release() {
        synchronized (this.zzbNM) {
            stop();
            this.zzbNX.release();
        }
    }

    @RequiresPermission("android.permission.CAMERA")
    public CameraSource start() throws IOException {
        synchronized (this.zzbNM) {
            if (this.zzbNN != null) {
            } else {
                this.zzbNN = zzTK();
                int i = VERSION.SDK_INT;
                this.zzbNU = new SurfaceTexture(100);
                this.zzbNN.setPreviewTexture(this.zzbNU);
                this.zzbNV = true;
                this.zzbNN.startPreview();
                this.zzbNW = new Thread(this.zzbNX);
                this.zzbNX.setActive(true);
                this.zzbNW.start();
            }
        }
        return this;
    }

    @RequiresPermission("android.permission.CAMERA")
    public CameraSource start(SurfaceHolder surfaceHolder) throws IOException {
        synchronized (this.zzbNM) {
            if (this.zzbNN != null) {
            } else {
                this.zzbNN = zzTK();
                this.zzbNN.setPreviewDisplay(surfaceHolder);
                this.zzbNN.startPreview();
                this.zzbNW = new Thread(this.zzbNX);
                this.zzbNX.setActive(true);
                this.zzbNW.start();
                this.zzbNV = false;
            }
        }
        return this;
    }

    public void stop() {
        synchronized (this.zzbNM) {
            this.zzbNX.setActive(false);
            if (this.zzbNW != null) {
                try {
                    this.zzbNW.join();
                } catch (InterruptedException e) {
                    Log.d("CameraSource", "Frame processing thread interrupted on release.");
                }
                this.zzbNW = null;
            }
            if (this.zzbNN != null) {
                this.zzbNN.stopPreview();
                this.zzbNN.setPreviewCallbackWithBuffer(null);
                try {
                    if (this.zzbNV) {
                        this.zzbNN.setPreviewTexture(null);
                    } else {
                        this.zzbNN.setPreviewDisplay(null);
                    }
                } catch (Exception e2) {
                    String valueOf = String.valueOf(e2);
                    Log.e("CameraSource", new StringBuilder(String.valueOf(valueOf).length() + 32).append("Failed to clear camera preview: ").append(valueOf).toString());
                }
                this.zzbNN.release();
                this.zzbNN = null;
            }
            this.zzbNY.clear();
        }
    }

    public void takePicture(ShutterCallback shutterCallback, PictureCallback pictureCallback) {
        synchronized (this.zzbNM) {
            if (this.zzbNN != null) {
                android.hardware.Camera.ShutterCallback com_google_android_gms_vision_CameraSource_zzd = new zzd();
                com_google_android_gms_vision_CameraSource_zzd.zzbOg = shutterCallback;
                android.hardware.Camera.PictureCallback com_google_android_gms_vision_CameraSource_zzc = new zzc();
                com_google_android_gms_vision_CameraSource_zzc.zzbOf = pictureCallback;
                this.zzbNN.takePicture(com_google_android_gms_vision_CameraSource_zzd, null, null, com_google_android_gms_vision_CameraSource_zzc);
            }
        }
    }
}
