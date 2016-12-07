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
import android.view.SurfaceView;
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
    private int zzLS;
    private final Object zzbLP;
    private Camera zzbLQ;
    private int zzbLR;
    private Size zzbLS;
    private float zzbLT;
    private int zzbLU;
    private int zzbLV;
    private boolean zzbLW;
    private SurfaceView zzbLX;
    private SurfaceTexture zzbLY;
    private boolean zzbLZ;
    private Thread zzbMa;
    private zzb zzbMb;
    private Map<byte[], ByteBuffer> zzbMc;

    public static class Builder {
        private final Detector<?> zzbMd;
        private CameraSource zzbMe = new CameraSource();

        public Builder(Context context, Detector<?> detector) {
            if (context == null) {
                throw new IllegalArgumentException("No context supplied.");
            } else if (detector == null) {
                throw new IllegalArgumentException("No detector supplied.");
            } else {
                this.zzbMd = detector;
                this.zzbMe.mContext = context;
            }
        }

        public CameraSource build() {
            CameraSource cameraSource = this.zzbMe;
            CameraSource cameraSource2 = this.zzbMe;
            cameraSource2.getClass();
            cameraSource.zzbMb = new zzb(cameraSource2, this.zzbMd);
            return this.zzbMe;
        }

        public Builder setAutoFocusEnabled(boolean z) {
            this.zzbMe.zzbLW = z;
            return this;
        }

        public Builder setFacing(int i) {
            if (i == 0 || i == 1) {
                this.zzbMe.zzbLR = i;
                return this;
            }
            throw new IllegalArgumentException("Invalid camera: " + i);
        }

        public Builder setRequestedFps(float f) {
            if (f <= 0.0f) {
                throw new IllegalArgumentException("Invalid fps: " + f);
            }
            this.zzbMe.zzbLT = f;
            return this;
        }

        public Builder setRequestedPreviewSize(int i, int i2) {
            if (i <= 0 || i > 1000000 || i2 <= 0 || i2 > 1000000) {
                throw new IllegalArgumentException("Invalid preview size: " + i + "x" + i2);
            }
            this.zzbMe.zzbLU = i;
            this.zzbMe.zzbLV = i2;
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
        final /* synthetic */ CameraSource zzbMf;

        private zza(CameraSource cameraSource) {
            this.zzbMf = cameraSource;
        }

        public void onPreviewFrame(byte[] bArr, Camera camera) {
            this.zzbMf.zzbMb.zza(bArr, camera);
        }
    }

    private class zzb implements Runnable {
        static final /* synthetic */ boolean $assertionsDisabled = (!CameraSource.class.desiredAssertionStatus());
        private long zzaed = SystemClock.elapsedRealtime();
        private Detector<?> zzbMd;
        final /* synthetic */ CameraSource zzbMf;
        private boolean zzbMg = true;
        private long zzbMh;
        private int zzbMi = 0;
        private ByteBuffer zzbMj;
        private final Object zzrN = new Object();

        zzb(CameraSource cameraSource, Detector<?> detector) {
            this.zzbMf = cameraSource;
            this.zzbMd = detector;
        }

        @SuppressLint({"Assert"})
        void release() {
            if ($assertionsDisabled || this.zzbMf.zzbMa.getState() == State.TERMINATED) {
                this.zzbMd.release();
                this.zzbMd = null;
                return;
            }
            throw new AssertionError();
        }

        /* JADX WARNING: inconsistent code. */
        /* Code decompiled incorrectly, please refer to instructions dump. */
        @SuppressLint({"InlinedApi"})
        public void run() {
            while (true) {
                synchronized (this.zzrN) {
                    while (this.zzbMg && this.zzbMj == null) {
                        try {
                            this.zzrN.wait();
                        } catch (Throwable e) {
                            Log.d("CameraSource", "Frame processing loop terminated.", e);
                            return;
                        }
                    }
                    if (this.zzbMg) {
                        Frame build = new com.google.android.gms.vision.Frame.Builder().setImageData(this.zzbMj, this.zzbMf.zzbLS.getWidth(), this.zzbMf.zzbLS.getHeight(), 17).setId(this.zzbMi).setTimestampMillis(this.zzbMh).setRotation(this.zzbMf.zzLS).build();
                        ByteBuffer byteBuffer = this.zzbMj;
                        this.zzbMj = null;
                    } else {
                        return;
                    }
                }
            }
        }

        void setActive(boolean z) {
            synchronized (this.zzrN) {
                this.zzbMg = z;
                this.zzrN.notifyAll();
            }
        }

        void zza(byte[] bArr, Camera camera) {
            synchronized (this.zzrN) {
                if (this.zzbMj != null) {
                    camera.addCallbackBuffer(this.zzbMj.array());
                    this.zzbMj = null;
                }
                if (this.zzbMf.zzbMc.containsKey(bArr)) {
                    this.zzbMh = SystemClock.elapsedRealtime() - this.zzaed;
                    this.zzbMi++;
                    this.zzbMj = (ByteBuffer) this.zzbMf.zzbMc.get(bArr);
                    this.zzrN.notifyAll();
                    return;
                }
                Log.d("CameraSource", "Skipping frame. Could not find ByteBuffer associated with the image data from the camera.");
            }
        }
    }

    private class zzc implements android.hardware.Camera.PictureCallback {
        final /* synthetic */ CameraSource zzbMf;
        private PictureCallback zzbMk;

        private zzc(CameraSource cameraSource) {
            this.zzbMf = cameraSource;
        }

        public void onPictureTaken(byte[] bArr, Camera camera) {
            if (this.zzbMk != null) {
                this.zzbMk.onPictureTaken(bArr);
            }
            synchronized (this.zzbMf.zzbLP) {
                if (this.zzbMf.zzbLQ != null) {
                    this.zzbMf.zzbLQ.startPreview();
                }
            }
        }
    }

    private class zzd implements android.hardware.Camera.ShutterCallback {
        private ShutterCallback zzbMl;

        private zzd(CameraSource cameraSource) {
        }

        public void onShutter() {
            if (this.zzbMl != null) {
                this.zzbMl.onShutter();
            }
        }
    }

    static class zze {
        private Size zzbMm;
        private Size zzbMn;

        public zze(Camera.Size size, Camera.Size size2) {
            this.zzbMm = new Size(size.width, size.height);
            if (size2 != null) {
                this.zzbMn = new Size(size2.width, size2.height);
            }
        }

        public Size zzSj() {
            return this.zzbMm;
        }

        public Size zzSk() {
            return this.zzbMn;
        }
    }

    private CameraSource() {
        this.zzbLP = new Object();
        this.zzbLR = 0;
        this.zzbLT = BitmapDescriptorFactory.HUE_ORANGE;
        this.zzbLU = 1024;
        this.zzbLV = 768;
        this.zzbLW = false;
        this.zzbMc = new HashMap();
    }

    @SuppressLint({"InlinedApi"})
    private Camera zzSi() {
        int zzne = zzne(this.zzbLR);
        if (zzne == -1) {
            throw new RuntimeException("Could not find requested camera.");
        }
        Camera open = Camera.open(zzne);
        zze zza = zza(open, this.zzbLU, this.zzbLV);
        if (zza == null) {
            throw new RuntimeException("Could not find suitable preview size.");
        }
        Size zzSk = zza.zzSk();
        this.zzbLS = zza.zzSj();
        int[] zza2 = zza(open, this.zzbLT);
        if (zza2 == null) {
            throw new RuntimeException("Could not find suitable preview frames per second range.");
        }
        Parameters parameters = open.getParameters();
        if (zzSk != null) {
            parameters.setPictureSize(zzSk.getWidth(), zzSk.getHeight());
        }
        parameters.setPreviewSize(this.zzbLS.getWidth(), this.zzbLS.getHeight());
        parameters.setPreviewFpsRange(zza2[0], zza2[1]);
        parameters.setPreviewFormat(17);
        zza(open, parameters, zzne);
        if (this.zzbLW) {
            if (parameters.getSupportedFocusModes().contains("continuous-video")) {
                parameters.setFocusMode("continuous-video");
            } else {
                Log.i("CameraSource", "Camera auto focus is not supported on this device.");
            }
        }
        open.setParameters(parameters);
        open.setPreviewCallbackWithBuffer(new zza());
        open.addCallbackBuffer(zza(this.zzbLS));
        open.addCallbackBuffer(zza(this.zzbLS));
        open.addCallbackBuffer(zza(this.zzbLS));
        open.addCallbackBuffer(zza(this.zzbLS));
        return open;
    }

    static zze zza(Camera camera, int i, int i2) {
        List<zze> zza = zza(camera);
        zze com_google_android_gms_vision_CameraSource_zze = null;
        int i3 = ConnectionsManager.DEFAULT_DATACENTER_ID;
        for (zze com_google_android_gms_vision_CameraSource_zze2 : zza) {
            zze com_google_android_gms_vision_CameraSource_zze3;
            int i4;
            Size zzSj = com_google_android_gms_vision_CameraSource_zze2.zzSj();
            int abs = Math.abs(zzSj.getHeight() - i2) + Math.abs(zzSj.getWidth() - i);
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
        this.zzLS = i2 / 90;
        camera.setDisplayOrientation(rotation);
        parameters.setRotation(i2);
    }

    @SuppressLint({"InlinedApi"})
    private byte[] zza(Size size) {
        Object obj = new byte[(((int) Math.ceil(((double) ((long) (ImageFormat.getBitsPerPixel(17) * (size.getHeight() * size.getWidth())))) / 8.0d)) + 1)];
        ByteBuffer wrap = ByteBuffer.wrap(obj);
        if (wrap.hasArray() && wrap.array() == obj) {
            this.zzbMc.put(obj, wrap);
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

    private static int zzne(int i) {
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
        return this.zzbLR;
    }

    public Size getPreviewSize() {
        return this.zzbLS;
    }

    public void release() {
        synchronized (this.zzbLP) {
            stop();
            this.zzbMb.release();
        }
    }

    @RequiresPermission("android.permission.CAMERA")
    public CameraSource start() throws IOException {
        synchronized (this.zzbLP) {
            if (this.zzbLQ != null) {
            } else {
                this.zzbLQ = zzSi();
                if (VERSION.SDK_INT >= 11) {
                    this.zzbLY = new SurfaceTexture(100);
                    this.zzbLQ.setPreviewTexture(this.zzbLY);
                    this.zzbLZ = true;
                } else {
                    this.zzbLX = new SurfaceView(this.mContext);
                    this.zzbLQ.setPreviewDisplay(this.zzbLX.getHolder());
                    this.zzbLZ = false;
                }
                this.zzbLQ.startPreview();
                this.zzbMa = new Thread(this.zzbMb);
                this.zzbMb.setActive(true);
                this.zzbMa.start();
            }
        }
        return this;
    }

    @RequiresPermission("android.permission.CAMERA")
    public CameraSource start(SurfaceHolder surfaceHolder) throws IOException {
        synchronized (this.zzbLP) {
            if (this.zzbLQ != null) {
            } else {
                this.zzbLQ = zzSi();
                this.zzbLQ.setPreviewDisplay(surfaceHolder);
                this.zzbLQ.startPreview();
                this.zzbMa = new Thread(this.zzbMb);
                this.zzbMb.setActive(true);
                this.zzbMa.start();
                this.zzbLZ = false;
            }
        }
        return this;
    }

    public void stop() {
        synchronized (this.zzbLP) {
            this.zzbMb.setActive(false);
            if (this.zzbMa != null) {
                try {
                    this.zzbMa.join();
                } catch (InterruptedException e) {
                    Log.d("CameraSource", "Frame processing thread interrupted on release.");
                }
                this.zzbMa = null;
            }
            if (this.zzbLQ != null) {
                this.zzbLQ.stopPreview();
                this.zzbLQ.setPreviewCallbackWithBuffer(null);
                try {
                    if (this.zzbLZ) {
                        this.zzbLQ.setPreviewTexture(null);
                    } else {
                        this.zzbLQ.setPreviewDisplay(null);
                    }
                } catch (Exception e2) {
                    String valueOf = String.valueOf(e2);
                    Log.e("CameraSource", new StringBuilder(String.valueOf(valueOf).length() + 32).append("Failed to clear camera preview: ").append(valueOf).toString());
                }
                this.zzbLQ.release();
                this.zzbLQ = null;
            }
            this.zzbMc.clear();
        }
    }

    public void takePicture(ShutterCallback shutterCallback, PictureCallback pictureCallback) {
        synchronized (this.zzbLP) {
            if (this.zzbLQ != null) {
                android.hardware.Camera.ShutterCallback com_google_android_gms_vision_CameraSource_zzd = new zzd();
                com_google_android_gms_vision_CameraSource_zzd.zzbMl = shutterCallback;
                android.hardware.Camera.PictureCallback com_google_android_gms_vision_CameraSource_zzc = new zzc();
                com_google_android_gms_vision_CameraSource_zzc.zzbMk = pictureCallback;
                this.zzbLQ.takePicture(com_google_android_gms_vision_CameraSource_zzd, null, null, com_google_android_gms_vision_CameraSource_zzc);
            }
        }
    }
}
