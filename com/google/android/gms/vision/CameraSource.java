package com.google.android.gms.vision;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.ImageFormat;
import android.graphics.SurfaceTexture;
import android.hardware.Camera;
import android.hardware.Camera.CameraInfo;
import android.hardware.Camera.Parameters;
import android.hardware.Camera.PreviewCallback;
import android.os.SystemClock;
import android.support.annotation.RequiresPermission;
import android.util.Log;
import android.view.SurfaceHolder;
import android.view.WindowManager;
import com.google.android.gms.common.images.Size;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import java.io.IOException;
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
    private int zzOa;
    private Map<byte[], ByteBuffer> zzbMA;
    private final Object zzbMo;
    private Camera zzbMp;
    private int zzbMq;
    private Size zzbMr;
    private float zzbMs;
    private int zzbMt;
    private int zzbMu;
    private boolean zzbMv;
    private SurfaceTexture zzbMw;
    private boolean zzbMx;
    private Thread zzbMy;
    private zzb zzbMz;

    public static class Builder {
        private final Detector<?> zzbMB;
        private CameraSource zzbMC = new CameraSource();

        public Builder(Context context, Detector<?> detector) {
            if (context == null) {
                throw new IllegalArgumentException("No context supplied.");
            } else if (detector == null) {
                throw new IllegalArgumentException("No detector supplied.");
            } else {
                this.zzbMB = detector;
                this.zzbMC.mContext = context;
            }
        }

        public CameraSource build() {
            CameraSource cameraSource = this.zzbMC;
            CameraSource cameraSource2 = this.zzbMC;
            cameraSource2.getClass();
            cameraSource.zzbMz = new zzb(cameraSource2, this.zzbMB);
            return this.zzbMC;
        }

        public Builder setAutoFocusEnabled(boolean z) {
            this.zzbMC.zzbMv = z;
            return this;
        }

        public Builder setFacing(int i) {
            if (i == 0 || i == 1) {
                this.zzbMC.zzbMq = i;
                return this;
            }
            throw new IllegalArgumentException("Invalid camera: " + i);
        }

        public Builder setRequestedFps(float f) {
            if (f <= 0.0f) {
                throw new IllegalArgumentException("Invalid fps: " + f);
            }
            this.zzbMC.zzbMs = f;
            return this;
        }

        public Builder setRequestedPreviewSize(int i, int i2) {
            if (i <= 0 || i > 1000000 || i2 <= 0 || i2 > 1000000) {
                throw new IllegalArgumentException("Invalid preview size: " + i + "x" + i2);
            }
            this.zzbMC.zzbMt = i;
            this.zzbMC.zzbMu = i2;
            return this;
        }
    }

    public interface PictureCallback {
        void onPictureTaken(byte[] bArr);
    }

    public interface ShutterCallback {
        void onShutter();
    }

    class zza implements PreviewCallback {
        private /* synthetic */ CameraSource zzbMD;

        private zza(CameraSource cameraSource) {
            this.zzbMD = cameraSource;
        }

        public final void onPreviewFrame(byte[] bArr, Camera camera) {
            this.zzbMD.zzbMz.zza(bArr, camera);
        }
    }

    class zzb implements Runnable {
        private boolean mActive = true;
        private final Object mLock = new Object();
        private long zzagZ = SystemClock.elapsedRealtime();
        private Detector<?> zzbMB;
        private /* synthetic */ CameraSource zzbMD;
        private long zzbME;
        private int zzbMF = 0;
        private ByteBuffer zzbMG;

        zzb(CameraSource cameraSource, Detector<?> detector) {
            this.zzbMD = cameraSource;
            this.zzbMB = detector;
        }

        @SuppressLint({"Assert"})
        final void release() {
            this.zzbMB.release();
            this.zzbMB = null;
        }

        /* JADX WARNING: inconsistent code. */
        /* Code decompiled incorrectly, please refer to instructions dump. */
        @SuppressLint({"InlinedApi"})
        public final void run() {
            while (true) {
                synchronized (this.mLock) {
                    while (this.mActive && this.zzbMG == null) {
                        try {
                            this.mLock.wait();
                        } catch (Throwable e) {
                            Log.d("CameraSource", "Frame processing loop terminated.", e);
                            return;
                        }
                    }
                    if (this.mActive) {
                        Frame build = new com.google.android.gms.vision.Frame.Builder().setImageData(this.zzbMG, this.zzbMD.zzbMr.getWidth(), this.zzbMD.zzbMr.getHeight(), 17).setId(this.zzbMF).setTimestampMillis(this.zzbME).setRotation(this.zzbMD.zzOa).build();
                        ByteBuffer byteBuffer = this.zzbMG;
                        this.zzbMG = null;
                    } else {
                        return;
                    }
                }
            }
        }

        final void setActive(boolean z) {
            synchronized (this.mLock) {
                this.mActive = z;
                this.mLock.notifyAll();
            }
        }

        final void zza(byte[] bArr, Camera camera) {
            synchronized (this.mLock) {
                if (this.zzbMG != null) {
                    camera.addCallbackBuffer(this.zzbMG.array());
                    this.zzbMG = null;
                }
                if (this.zzbMD.zzbMA.containsKey(bArr)) {
                    this.zzbME = SystemClock.elapsedRealtime() - this.zzagZ;
                    this.zzbMF++;
                    this.zzbMG = (ByteBuffer) this.zzbMD.zzbMA.get(bArr);
                    this.mLock.notifyAll();
                    return;
                }
                Log.d("CameraSource", "Skipping frame. Could not find ByteBuffer associated with the image data from the camera.");
            }
        }
    }

    class zzc implements android.hardware.Camera.PictureCallback {
        private /* synthetic */ CameraSource zzbMD;
        private PictureCallback zzbMH;

        private zzc(CameraSource cameraSource) {
            this.zzbMD = cameraSource;
        }

        public final void onPictureTaken(byte[] bArr, Camera camera) {
            if (this.zzbMH != null) {
                this.zzbMH.onPictureTaken(bArr);
            }
            synchronized (this.zzbMD.zzbMo) {
                if (this.zzbMD.zzbMp != null) {
                    this.zzbMD.zzbMp.startPreview();
                }
            }
        }
    }

    static class zzd implements android.hardware.Camera.ShutterCallback {
        private ShutterCallback zzbMI;

        private zzd() {
        }

        public final void onShutter() {
            if (this.zzbMI != null) {
                this.zzbMI.onShutter();
            }
        }
    }

    static class zze {
        private Size zzbMJ;
        private Size zzbMK;

        public zze(Camera.Size size, Camera.Size size2) {
            this.zzbMJ = new Size(size.width, size.height);
            if (size2 != null) {
                this.zzbMK = new Size(size2.width, size2.height);
            }
        }

        public final Size zzDL() {
            return this.zzbMJ;
        }

        public final Size zzDM() {
            return this.zzbMK;
        }
    }

    private CameraSource() {
        this.zzbMo = new Object();
        this.zzbMq = 0;
        this.zzbMs = BitmapDescriptorFactory.HUE_ORANGE;
        this.zzbMt = 1024;
        this.zzbMu = 768;
        this.zzbMv = false;
        this.zzbMA = new HashMap();
    }

    @SuppressLint({"InlinedApi"})
    private final Camera zzDK() throws IOException {
        int i;
        int i2 = 0;
        int i3 = this.zzbMq;
        CameraInfo cameraInfo = new CameraInfo();
        for (i = 0; i < Camera.getNumberOfCameras(); i++) {
            Camera.getCameraInfo(i, cameraInfo);
            if (cameraInfo.facing == i3) {
                i3 = i;
                break;
            }
        }
        i3 = -1;
        if (i3 == -1) {
            throw new IOException("Could not find requested camera.");
        }
        Camera open = Camera.open(i3);
        zze zza = zza(open, this.zzbMt, this.zzbMu);
        if (zza == null) {
            throw new IOException("Could not find suitable preview size.");
        }
        Size zzDM = zza.zzDM();
        this.zzbMr = zza.zzDL();
        int[] zza2 = zza(open, this.zzbMs);
        if (zza2 == null) {
            throw new IOException("Could not find suitable preview frames per second range.");
        }
        Parameters parameters = open.getParameters();
        if (zzDM != null) {
            parameters.setPictureSize(zzDM.getWidth(), zzDM.getHeight());
        }
        parameters.setPreviewSize(this.zzbMr.getWidth(), this.zzbMr.getHeight());
        parameters.setPreviewFpsRange(zza2[0], zza2[1]);
        parameters.setPreviewFormat(17);
        i = ((WindowManager) this.mContext.getSystemService("window")).getDefaultDisplay().getRotation();
        switch (i) {
            case 0:
                break;
            case 1:
                i2 = 90;
                break;
            case 2:
                i2 = 180;
                break;
            case 3:
                i2 = 270;
                break;
            default:
                Log.e("CameraSource", "Bad rotation value: " + i);
                break;
        }
        CameraInfo cameraInfo2 = new CameraInfo();
        Camera.getCameraInfo(i3, cameraInfo2);
        if (cameraInfo2.facing == 1) {
            i2 = (cameraInfo2.orientation + i2) % 360;
            i = (360 - i2) % 360;
        } else {
            i = ((cameraInfo2.orientation - i2) + 360) % 360;
            i2 = i;
        }
        this.zzOa = i2 / 90;
        open.setDisplayOrientation(i);
        parameters.setRotation(i2);
        if (this.zzbMv) {
            if (parameters.getSupportedFocusModes().contains("continuous-video")) {
                parameters.setFocusMode("continuous-video");
            } else {
                Log.i("CameraSource", "Camera auto focus is not supported on this device.");
            }
        }
        open.setParameters(parameters);
        open.setPreviewCallbackWithBuffer(new zza());
        open.addCallbackBuffer(zza(this.zzbMr));
        open.addCallbackBuffer(zza(this.zzbMr));
        open.addCallbackBuffer(zza(this.zzbMr));
        open.addCallbackBuffer(zza(this.zzbMr));
        return open;
    }

    private static zze zza(Camera camera, int i, int i2) {
        zze com_google_android_gms_vision_CameraSource_zze = null;
        Parameters parameters = camera.getParameters();
        List<Camera.Size> supportedPreviewSizes = parameters.getSupportedPreviewSizes();
        List<Camera.Size> supportedPictureSizes = parameters.getSupportedPictureSizes();
        List arrayList = new ArrayList();
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
        int i3 = ConnectionsManager.DEFAULT_DATACENTER_ID;
        ArrayList arrayList2 = (ArrayList) arrayList;
        int size4 = arrayList2.size();
        int i4 = 0;
        while (i4 < size4) {
            zze com_google_android_gms_vision_CameraSource_zze2;
            int i5;
            int i6 = i4 + 1;
            zze com_google_android_gms_vision_CameraSource_zze3 = (zze) arrayList2.get(i4);
            Size zzDL = com_google_android_gms_vision_CameraSource_zze3.zzDL();
            i4 = Math.abs(zzDL.getHeight() - i2) + Math.abs(zzDL.getWidth() - i);
            if (i4 < i3) {
                int i7 = i4;
                com_google_android_gms_vision_CameraSource_zze2 = com_google_android_gms_vision_CameraSource_zze3;
                i5 = i7;
            } else {
                i5 = i3;
                com_google_android_gms_vision_CameraSource_zze2 = com_google_android_gms_vision_CameraSource_zze;
            }
            i3 = i5;
            com_google_android_gms_vision_CameraSource_zze = com_google_android_gms_vision_CameraSource_zze2;
            i4 = i6;
        }
        return com_google_android_gms_vision_CameraSource_zze;
    }

    @SuppressLint({"InlinedApi"})
    private final byte[] zza(Size size) {
        Object obj = new byte[(((int) Math.ceil(((double) ((long) (ImageFormat.getBitsPerPixel(17) * (size.getHeight() * size.getWidth())))) / 8.0d)) + 1)];
        ByteBuffer wrap = ByteBuffer.wrap(obj);
        if (wrap.hasArray() && wrap.array() == obj) {
            this.zzbMA.put(obj, wrap);
            return obj;
        }
        throw new IllegalStateException("Failed to create valid buffer for camera source.");
    }

    @SuppressLint({"InlinedApi"})
    private static int[] zza(Camera camera, float f) {
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

    public int getCameraFacing() {
        return this.zzbMq;
    }

    public Size getPreviewSize() {
        return this.zzbMr;
    }

    public void release() {
        synchronized (this.zzbMo) {
            stop();
            this.zzbMz.release();
        }
    }

    @RequiresPermission("android.permission.CAMERA")
    public CameraSource start() throws IOException {
        synchronized (this.zzbMo) {
            if (this.zzbMp != null) {
            } else {
                this.zzbMp = zzDK();
                this.zzbMw = new SurfaceTexture(100);
                this.zzbMp.setPreviewTexture(this.zzbMw);
                this.zzbMx = true;
                this.zzbMp.startPreview();
                this.zzbMy = new Thread(this.zzbMz);
                this.zzbMz.setActive(true);
                this.zzbMy.start();
            }
        }
        return this;
    }

    @RequiresPermission("android.permission.CAMERA")
    public CameraSource start(SurfaceHolder surfaceHolder) throws IOException {
        synchronized (this.zzbMo) {
            if (this.zzbMp != null) {
            } else {
                this.zzbMp = zzDK();
                this.zzbMp.setPreviewDisplay(surfaceHolder);
                this.zzbMp.startPreview();
                this.zzbMy = new Thread(this.zzbMz);
                this.zzbMz.setActive(true);
                this.zzbMy.start();
                this.zzbMx = false;
            }
        }
        return this;
    }

    public void stop() {
        synchronized (this.zzbMo) {
            this.zzbMz.setActive(false);
            if (this.zzbMy != null) {
                try {
                    this.zzbMy.join();
                } catch (InterruptedException e) {
                    Log.d("CameraSource", "Frame processing thread interrupted on release.");
                }
                this.zzbMy = null;
            }
            if (this.zzbMp != null) {
                this.zzbMp.stopPreview();
                this.zzbMp.setPreviewCallbackWithBuffer(null);
                try {
                    if (this.zzbMx) {
                        this.zzbMp.setPreviewTexture(null);
                    } else {
                        this.zzbMp.setPreviewDisplay(null);
                    }
                } catch (Exception e2) {
                    String valueOf = String.valueOf(e2);
                    Log.e("CameraSource", new StringBuilder(String.valueOf(valueOf).length() + 32).append("Failed to clear camera preview: ").append(valueOf).toString());
                }
                this.zzbMp.release();
                this.zzbMp = null;
            }
            this.zzbMA.clear();
        }
    }

    public void takePicture(ShutterCallback shutterCallback, PictureCallback pictureCallback) {
        synchronized (this.zzbMo) {
            if (this.zzbMp != null) {
                android.hardware.Camera.ShutterCallback com_google_android_gms_vision_CameraSource_zzd = new zzd();
                com_google_android_gms_vision_CameraSource_zzd.zzbMI = shutterCallback;
                android.hardware.Camera.PictureCallback com_google_android_gms_vision_CameraSource_zzc = new zzc();
                com_google_android_gms_vision_CameraSource_zzc.zzbMH = pictureCallback;
                this.zzbMp.takePicture(com_google_android_gms_vision_CameraSource_zzd, null, null, com_google_android_gms_vision_CameraSource_zzc);
            }
        }
    }
}
