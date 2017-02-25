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
    private final Object zzbNQ;
    private Camera zzbNR;
    private int zzbNS;
    private Size zzbNT;
    private float zzbNU;
    private int zzbNV;
    private int zzbNW;
    private boolean zzbNX;
    private SurfaceTexture zzbNY;
    private boolean zzbNZ;
    private Thread zzbOa;
    private zzb zzbOb;
    private Map<byte[], ByteBuffer> zzbOc;

    public static class Builder {
        private final Detector<?> zzbOd;
        private CameraSource zzbOe = new CameraSource();

        public Builder(Context context, Detector<?> detector) {
            if (context == null) {
                throw new IllegalArgumentException("No context supplied.");
            } else if (detector == null) {
                throw new IllegalArgumentException("No detector supplied.");
            } else {
                this.zzbOd = detector;
                this.zzbOe.mContext = context;
            }
        }

        public CameraSource build() {
            CameraSource cameraSource = this.zzbOe;
            CameraSource cameraSource2 = this.zzbOe;
            cameraSource2.getClass();
            cameraSource.zzbOb = new zzb(cameraSource2, this.zzbOd);
            return this.zzbOe;
        }

        public Builder setAutoFocusEnabled(boolean z) {
            this.zzbOe.zzbNX = z;
            return this;
        }

        public Builder setFacing(int i) {
            if (i == 0 || i == 1) {
                this.zzbOe.zzbNS = i;
                return this;
            }
            throw new IllegalArgumentException("Invalid camera: " + i);
        }

        public Builder setRequestedFps(float f) {
            if (f <= 0.0f) {
                throw new IllegalArgumentException("Invalid fps: " + f);
            }
            this.zzbOe.zzbNU = f;
            return this;
        }

        public Builder setRequestedPreviewSize(int i, int i2) {
            if (i <= 0 || i > 1000000 || i2 <= 0 || i2 > 1000000) {
                throw new IllegalArgumentException("Invalid preview size: " + i + "x" + i2);
            }
            this.zzbOe.zzbNV = i;
            this.zzbOe.zzbNW = i2;
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
        final /* synthetic */ CameraSource zzbOf;

        private zza(CameraSource cameraSource) {
            this.zzbOf = cameraSource;
        }

        public void onPreviewFrame(byte[] bArr, Camera camera) {
            this.zzbOf.zzbOb.zza(bArr, camera);
        }
    }

    private class zzb implements Runnable {
        static final /* synthetic */ boolean $assertionsDisabled = (!CameraSource.class.desiredAssertionStatus());
        private boolean mActive = true;
        private long zzafe = SystemClock.elapsedRealtime();
        private Detector<?> zzbOd;
        final /* synthetic */ CameraSource zzbOf;
        private long zzbOg;
        private int zzbOh = 0;
        private ByteBuffer zzbOi;
        private final Object zzrJ = new Object();

        zzb(CameraSource cameraSource, Detector<?> detector) {
            this.zzbOf = cameraSource;
            this.zzbOd = detector;
        }

        @SuppressLint({"Assert"})
        void release() {
            if ($assertionsDisabled || this.zzbOf.zzbOa.getState() == State.TERMINATED) {
                this.zzbOd.release();
                this.zzbOd = null;
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
                    while (this.mActive && this.zzbOi == null) {
                        try {
                            this.zzrJ.wait();
                        } catch (Throwable e) {
                            Log.d("CameraSource", "Frame processing loop terminated.", e);
                            return;
                        }
                    }
                    if (this.mActive) {
                        Frame build = new com.google.android.gms.vision.Frame.Builder().setImageData(this.zzbOi, this.zzbOf.zzbNT.getWidth(), this.zzbOf.zzbNT.getHeight(), 17).setId(this.zzbOh).setTimestampMillis(this.zzbOg).setRotation(this.zzbOf.zzMA).build();
                        ByteBuffer byteBuffer = this.zzbOi;
                        this.zzbOi = null;
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
                if (this.zzbOi != null) {
                    camera.addCallbackBuffer(this.zzbOi.array());
                    this.zzbOi = null;
                }
                if (this.zzbOf.zzbOc.containsKey(bArr)) {
                    this.zzbOg = SystemClock.elapsedRealtime() - this.zzafe;
                    this.zzbOh++;
                    this.zzbOi = (ByteBuffer) this.zzbOf.zzbOc.get(bArr);
                    this.zzrJ.notifyAll();
                    return;
                }
                Log.d("CameraSource", "Skipping frame. Could not find ByteBuffer associated with the image data from the camera.");
            }
        }
    }

    private class zzc implements android.hardware.Camera.PictureCallback {
        final /* synthetic */ CameraSource zzbOf;
        private PictureCallback zzbOj;

        private zzc(CameraSource cameraSource) {
            this.zzbOf = cameraSource;
        }

        public void onPictureTaken(byte[] bArr, Camera camera) {
            if (this.zzbOj != null) {
                this.zzbOj.onPictureTaken(bArr);
            }
            synchronized (this.zzbOf.zzbNQ) {
                if (this.zzbOf.zzbNR != null) {
                    this.zzbOf.zzbNR.startPreview();
                }
            }
        }
    }

    private class zzd implements android.hardware.Camera.ShutterCallback {
        private ShutterCallback zzbOk;

        private zzd(CameraSource cameraSource) {
        }

        public void onShutter() {
            if (this.zzbOk != null) {
                this.zzbOk.onShutter();
            }
        }
    }

    static class zze {
        private Size zzbOl;
        private Size zzbOm;

        public zze(Camera.Size size, Camera.Size size2) {
            this.zzbOl = new Size(size.width, size.height);
            if (size2 != null) {
                this.zzbOm = new Size(size2.width, size2.height);
            }
        }

        public Size zzTK() {
            return this.zzbOl;
        }

        public Size zzTL() {
            return this.zzbOm;
        }
    }

    private CameraSource() {
        this.zzbNQ = new Object();
        this.zzbNS = 0;
        this.zzbNU = BitmapDescriptorFactory.HUE_ORANGE;
        this.zzbNV = 1024;
        this.zzbNW = 768;
        this.zzbNX = false;
        this.zzbOc = new HashMap();
    }

    @SuppressLint({"InlinedApi"})
    private Camera zzTJ() {
        int zznP = zznP(this.zzbNS);
        if (zznP == -1) {
            throw new RuntimeException("Could not find requested camera.");
        }
        Camera open = Camera.open(zznP);
        zze zza = zza(open, this.zzbNV, this.zzbNW);
        if (zza == null) {
            throw new RuntimeException("Could not find suitable preview size.");
        }
        Size zzTL = zza.zzTL();
        this.zzbNT = zza.zzTK();
        int[] zza2 = zza(open, this.zzbNU);
        if (zza2 == null) {
            throw new RuntimeException("Could not find suitable preview frames per second range.");
        }
        Parameters parameters = open.getParameters();
        if (zzTL != null) {
            parameters.setPictureSize(zzTL.getWidth(), zzTL.getHeight());
        }
        parameters.setPreviewSize(this.zzbNT.getWidth(), this.zzbNT.getHeight());
        parameters.setPreviewFpsRange(zza2[0], zza2[1]);
        parameters.setPreviewFormat(17);
        zza(open, parameters, zznP);
        if (this.zzbNX) {
            if (parameters.getSupportedFocusModes().contains("continuous-video")) {
                parameters.setFocusMode("continuous-video");
            } else {
                Log.i("CameraSource", "Camera auto focus is not supported on this device.");
            }
        }
        open.setParameters(parameters);
        open.setPreviewCallbackWithBuffer(new zza());
        open.addCallbackBuffer(zza(this.zzbNT));
        open.addCallbackBuffer(zza(this.zzbNT));
        open.addCallbackBuffer(zza(this.zzbNT));
        open.addCallbackBuffer(zza(this.zzbNT));
        return open;
    }

    static zze zza(Camera camera, int i, int i2) {
        List<zze> zza = zza(camera);
        zze com_google_android_gms_vision_CameraSource_zze = null;
        int i3 = ConnectionsManager.DEFAULT_DATACENTER_ID;
        for (zze com_google_android_gms_vision_CameraSource_zze2 : zza) {
            zze com_google_android_gms_vision_CameraSource_zze3;
            int i4;
            Size zzTK = com_google_android_gms_vision_CameraSource_zze2.zzTK();
            int abs = Math.abs(zzTK.getHeight() - i2) + Math.abs(zzTK.getWidth() - i);
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
            this.zzbOc.put(obj, wrap);
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
        return this.zzbNS;
    }

    public Size getPreviewSize() {
        return this.zzbNT;
    }

    public void release() {
        synchronized (this.zzbNQ) {
            stop();
            this.zzbOb.release();
        }
    }

    @RequiresPermission("android.permission.CAMERA")
    public CameraSource start() throws IOException {
        synchronized (this.zzbNQ) {
            if (this.zzbNR != null) {
            } else {
                this.zzbNR = zzTJ();
                int i = VERSION.SDK_INT;
                this.zzbNY = new SurfaceTexture(100);
                this.zzbNR.setPreviewTexture(this.zzbNY);
                this.zzbNZ = true;
                this.zzbNR.startPreview();
                this.zzbOa = new Thread(this.zzbOb);
                this.zzbOb.setActive(true);
                this.zzbOa.start();
            }
        }
        return this;
    }

    @RequiresPermission("android.permission.CAMERA")
    public CameraSource start(SurfaceHolder surfaceHolder) throws IOException {
        synchronized (this.zzbNQ) {
            if (this.zzbNR != null) {
            } else {
                this.zzbNR = zzTJ();
                this.zzbNR.setPreviewDisplay(surfaceHolder);
                this.zzbNR.startPreview();
                this.zzbOa = new Thread(this.zzbOb);
                this.zzbOb.setActive(true);
                this.zzbOa.start();
                this.zzbNZ = false;
            }
        }
        return this;
    }

    public void stop() {
        synchronized (this.zzbNQ) {
            this.zzbOb.setActive(false);
            if (this.zzbOa != null) {
                try {
                    this.zzbOa.join();
                } catch (InterruptedException e) {
                    Log.d("CameraSource", "Frame processing thread interrupted on release.");
                }
                this.zzbOa = null;
            }
            if (this.zzbNR != null) {
                this.zzbNR.stopPreview();
                this.zzbNR.setPreviewCallbackWithBuffer(null);
                try {
                    if (this.zzbNZ) {
                        this.zzbNR.setPreviewTexture(null);
                    } else {
                        this.zzbNR.setPreviewDisplay(null);
                    }
                } catch (Exception e2) {
                    String valueOf = String.valueOf(e2);
                    Log.e("CameraSource", new StringBuilder(String.valueOf(valueOf).length() + 32).append("Failed to clear camera preview: ").append(valueOf).toString());
                }
                this.zzbNR.release();
                this.zzbNR = null;
            }
            this.zzbOc.clear();
        }
    }

    public void takePicture(ShutterCallback shutterCallback, PictureCallback pictureCallback) {
        synchronized (this.zzbNQ) {
            if (this.zzbNR != null) {
                android.hardware.Camera.ShutterCallback com_google_android_gms_vision_CameraSource_zzd = new zzd();
                com_google_android_gms_vision_CameraSource_zzd.zzbOk = shutterCallback;
                android.hardware.Camera.PictureCallback com_google_android_gms_vision_CameraSource_zzc = new zzc();
                com_google_android_gms_vision_CameraSource_zzc.zzbOj = pictureCallback;
                this.zzbNR.takePicture(com_google_android_gms_vision_CameraSource_zzd, null, null, com_google_android_gms_vision_CameraSource_zzc);
            }
        }
    }
}
