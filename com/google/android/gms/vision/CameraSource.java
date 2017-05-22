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
    private final Object zzbNP;
    private Camera zzbNQ;
    private int zzbNR;
    private Size zzbNS;
    private float zzbNT;
    private int zzbNU;
    private int zzbNV;
    private boolean zzbNW;
    private SurfaceTexture zzbNX;
    private boolean zzbNY;
    private Thread zzbNZ;
    private zzb zzbOa;
    private Map<byte[], ByteBuffer> zzbOb;

    public static class Builder {
        private final Detector<?> zzbOc;
        private CameraSource zzbOd = new CameraSource();

        public Builder(Context context, Detector<?> detector) {
            if (context == null) {
                throw new IllegalArgumentException("No context supplied.");
            } else if (detector == null) {
                throw new IllegalArgumentException("No detector supplied.");
            } else {
                this.zzbOc = detector;
                this.zzbOd.mContext = context;
            }
        }

        public CameraSource build() {
            CameraSource cameraSource = this.zzbOd;
            CameraSource cameraSource2 = this.zzbOd;
            cameraSource2.getClass();
            cameraSource.zzbOa = new zzb(cameraSource2, this.zzbOc);
            return this.zzbOd;
        }

        public Builder setAutoFocusEnabled(boolean z) {
            this.zzbOd.zzbNW = z;
            return this;
        }

        public Builder setFacing(int i) {
            if (i == 0 || i == 1) {
                this.zzbOd.zzbNR = i;
                return this;
            }
            throw new IllegalArgumentException("Invalid camera: " + i);
        }

        public Builder setRequestedFps(float f) {
            if (f <= 0.0f) {
                throw new IllegalArgumentException("Invalid fps: " + f);
            }
            this.zzbOd.zzbNT = f;
            return this;
        }

        public Builder setRequestedPreviewSize(int i, int i2) {
            if (i <= 0 || i > 1000000 || i2 <= 0 || i2 > 1000000) {
                throw new IllegalArgumentException("Invalid preview size: " + i + "x" + i2);
            }
            this.zzbOd.zzbNU = i;
            this.zzbOd.zzbNV = i2;
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
        final /* synthetic */ CameraSource zzbOe;

        private zza(CameraSource cameraSource) {
            this.zzbOe = cameraSource;
        }

        public void onPreviewFrame(byte[] bArr, Camera camera) {
            this.zzbOe.zzbOa.zza(bArr, camera);
        }
    }

    private class zzb implements Runnable {
        static final /* synthetic */ boolean $assertionsDisabled = (!CameraSource.class.desiredAssertionStatus());
        private boolean mActive = true;
        private long zzafe = SystemClock.elapsedRealtime();
        private Detector<?> zzbOc;
        final /* synthetic */ CameraSource zzbOe;
        private long zzbOf;
        private int zzbOg = 0;
        private ByteBuffer zzbOh;
        private final Object zzrJ = new Object();

        zzb(CameraSource cameraSource, Detector<?> detector) {
            this.zzbOe = cameraSource;
            this.zzbOc = detector;
        }

        @SuppressLint({"Assert"})
        void release() {
            if ($assertionsDisabled || this.zzbOe.zzbNZ.getState() == State.TERMINATED) {
                this.zzbOc.release();
                this.zzbOc = null;
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
                    while (this.mActive && this.zzbOh == null) {
                        try {
                            this.zzrJ.wait();
                        } catch (Throwable e) {
                            Log.d("CameraSource", "Frame processing loop terminated.", e);
                            return;
                        }
                    }
                    if (this.mActive) {
                        Frame build = new com.google.android.gms.vision.Frame.Builder().setImageData(this.zzbOh, this.zzbOe.zzbNS.getWidth(), this.zzbOe.zzbNS.getHeight(), 17).setId(this.zzbOg).setTimestampMillis(this.zzbOf).setRotation(this.zzbOe.zzMA).build();
                        ByteBuffer byteBuffer = this.zzbOh;
                        this.zzbOh = null;
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
                if (this.zzbOh != null) {
                    camera.addCallbackBuffer(this.zzbOh.array());
                    this.zzbOh = null;
                }
                if (this.zzbOe.zzbOb.containsKey(bArr)) {
                    this.zzbOf = SystemClock.elapsedRealtime() - this.zzafe;
                    this.zzbOg++;
                    this.zzbOh = (ByteBuffer) this.zzbOe.zzbOb.get(bArr);
                    this.zzrJ.notifyAll();
                    return;
                }
                Log.d("CameraSource", "Skipping frame. Could not find ByteBuffer associated with the image data from the camera.");
            }
        }
    }

    private class zzc implements android.hardware.Camera.PictureCallback {
        final /* synthetic */ CameraSource zzbOe;
        private PictureCallback zzbOi;

        private zzc(CameraSource cameraSource) {
            this.zzbOe = cameraSource;
        }

        public void onPictureTaken(byte[] bArr, Camera camera) {
            if (this.zzbOi != null) {
                this.zzbOi.onPictureTaken(bArr);
            }
            synchronized (this.zzbOe.zzbNP) {
                if (this.zzbOe.zzbNQ != null) {
                    this.zzbOe.zzbNQ.startPreview();
                }
            }
        }
    }

    private class zzd implements android.hardware.Camera.ShutterCallback {
        private ShutterCallback zzbOj;

        private zzd(CameraSource cameraSource) {
        }

        public void onShutter() {
            if (this.zzbOj != null) {
                this.zzbOj.onShutter();
            }
        }
    }

    static class zze {
        private Size zzbOk;
        private Size zzbOl;

        public zze(Camera.Size size, Camera.Size size2) {
            this.zzbOk = new Size(size.width, size.height);
            if (size2 != null) {
                this.zzbOl = new Size(size2.width, size2.height);
            }
        }

        public Size zzTN() {
            return this.zzbOk;
        }

        public Size zzTO() {
            return this.zzbOl;
        }
    }

    private CameraSource() {
        this.zzbNP = new Object();
        this.zzbNR = 0;
        this.zzbNT = BitmapDescriptorFactory.HUE_ORANGE;
        this.zzbNU = 1024;
        this.zzbNV = 768;
        this.zzbNW = false;
        this.zzbOb = new HashMap();
    }

    @SuppressLint({"InlinedApi"})
    private Camera zzTM() {
        int zznP = zznP(this.zzbNR);
        if (zznP == -1) {
            throw new RuntimeException("Could not find requested camera.");
        }
        Camera open = Camera.open(zznP);
        zze zza = zza(open, this.zzbNU, this.zzbNV);
        if (zza == null) {
            throw new RuntimeException("Could not find suitable preview size.");
        }
        Size zzTO = zza.zzTO();
        this.zzbNS = zza.zzTN();
        int[] zza2 = zza(open, this.zzbNT);
        if (zza2 == null) {
            throw new RuntimeException("Could not find suitable preview frames per second range.");
        }
        Parameters parameters = open.getParameters();
        if (zzTO != null) {
            parameters.setPictureSize(zzTO.getWidth(), zzTO.getHeight());
        }
        parameters.setPreviewSize(this.zzbNS.getWidth(), this.zzbNS.getHeight());
        parameters.setPreviewFpsRange(zza2[0], zza2[1]);
        parameters.setPreviewFormat(17);
        zza(open, parameters, zznP);
        if (this.zzbNW) {
            if (parameters.getSupportedFocusModes().contains("continuous-video")) {
                parameters.setFocusMode("continuous-video");
            } else {
                Log.i("CameraSource", "Camera auto focus is not supported on this device.");
            }
        }
        open.setParameters(parameters);
        open.setPreviewCallbackWithBuffer(new zza());
        open.addCallbackBuffer(zza(this.zzbNS));
        open.addCallbackBuffer(zza(this.zzbNS));
        open.addCallbackBuffer(zza(this.zzbNS));
        open.addCallbackBuffer(zza(this.zzbNS));
        return open;
    }

    static zze zza(Camera camera, int i, int i2) {
        List<zze> zza = zza(camera);
        zze com_google_android_gms_vision_CameraSource_zze = null;
        int i3 = ConnectionsManager.DEFAULT_DATACENTER_ID;
        for (zze com_google_android_gms_vision_CameraSource_zze2 : zza) {
            zze com_google_android_gms_vision_CameraSource_zze3;
            int i4;
            Size zzTN = com_google_android_gms_vision_CameraSource_zze2.zzTN();
            int abs = Math.abs(zzTN.getHeight() - i2) + Math.abs(zzTN.getWidth() - i);
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
            this.zzbOb.put(obj, wrap);
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
        return this.zzbNR;
    }

    public Size getPreviewSize() {
        return this.zzbNS;
    }

    public void release() {
        synchronized (this.zzbNP) {
            stop();
            this.zzbOa.release();
        }
    }

    @RequiresPermission("android.permission.CAMERA")
    public CameraSource start() throws IOException {
        synchronized (this.zzbNP) {
            if (this.zzbNQ != null) {
            } else {
                this.zzbNQ = zzTM();
                int i = VERSION.SDK_INT;
                this.zzbNX = new SurfaceTexture(100);
                this.zzbNQ.setPreviewTexture(this.zzbNX);
                this.zzbNY = true;
                this.zzbNQ.startPreview();
                this.zzbNZ = new Thread(this.zzbOa);
                this.zzbOa.setActive(true);
                this.zzbNZ.start();
            }
        }
        return this;
    }

    @RequiresPermission("android.permission.CAMERA")
    public CameraSource start(SurfaceHolder surfaceHolder) throws IOException {
        synchronized (this.zzbNP) {
            if (this.zzbNQ != null) {
            } else {
                this.zzbNQ = zzTM();
                this.zzbNQ.setPreviewDisplay(surfaceHolder);
                this.zzbNQ.startPreview();
                this.zzbNZ = new Thread(this.zzbOa);
                this.zzbOa.setActive(true);
                this.zzbNZ.start();
                this.zzbNY = false;
            }
        }
        return this;
    }

    public void stop() {
        synchronized (this.zzbNP) {
            this.zzbOa.setActive(false);
            if (this.zzbNZ != null) {
                try {
                    this.zzbNZ.join();
                } catch (InterruptedException e) {
                    Log.d("CameraSource", "Frame processing thread interrupted on release.");
                }
                this.zzbNZ = null;
            }
            if (this.zzbNQ != null) {
                this.zzbNQ.stopPreview();
                this.zzbNQ.setPreviewCallbackWithBuffer(null);
                try {
                    if (this.zzbNY) {
                        this.zzbNQ.setPreviewTexture(null);
                    } else {
                        this.zzbNQ.setPreviewDisplay(null);
                    }
                } catch (Exception e2) {
                    String valueOf = String.valueOf(e2);
                    Log.e("CameraSource", new StringBuilder(String.valueOf(valueOf).length() + 32).append("Failed to clear camera preview: ").append(valueOf).toString());
                }
                this.zzbNQ.release();
                this.zzbNQ = null;
            }
            this.zzbOb.clear();
        }
    }

    public void takePicture(ShutterCallback shutterCallback, PictureCallback pictureCallback) {
        synchronized (this.zzbNP) {
            if (this.zzbNQ != null) {
                android.hardware.Camera.ShutterCallback com_google_android_gms_vision_CameraSource_zzd = new zzd();
                com_google_android_gms_vision_CameraSource_zzd.zzbOj = shutterCallback;
                android.hardware.Camera.PictureCallback com_google_android_gms_vision_CameraSource_zzc = new zzc();
                com_google_android_gms_vision_CameraSource_zzc.zzbOi = pictureCallback;
                this.zzbNQ.takePicture(com_google_android_gms_vision_CameraSource_zzd, null, null, com_google_android_gms_vision_CameraSource_zzc);
            }
        }
    }
}
