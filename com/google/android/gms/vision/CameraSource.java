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
    private final Object aNc;
    private Camera aNd;
    private int aNe;
    private Size aNf;
    private float aNg;
    private int aNh;
    private int aNi;
    private boolean aNj;
    private SurfaceView aNk;
    private SurfaceTexture aNl;
    private boolean aNm;
    private Thread aNn;
    private zzb aNo;
    private Map<byte[], ByteBuffer> aNp;
    private Context mContext;
    private int zzbzf;

    public static class Builder {
        private final Detector<?> aNq;
        private CameraSource aNr = new CameraSource();

        public Builder(Context context, Detector<?> detector) {
            if (context == null) {
                throw new IllegalArgumentException("No context supplied.");
            } else if (detector == null) {
                throw new IllegalArgumentException("No detector supplied.");
            } else {
                this.aNq = detector;
                this.aNr.mContext = context;
            }
        }

        public CameraSource build() {
            CameraSource cameraSource = this.aNr;
            CameraSource cameraSource2 = this.aNr;
            cameraSource2.getClass();
            cameraSource.aNo = new zzb(cameraSource2, this.aNq);
            return this.aNr;
        }

        public Builder setAutoFocusEnabled(boolean z) {
            this.aNr.aNj = z;
            return this;
        }

        public Builder setFacing(int i) {
            if (i == 0 || i == 1) {
                this.aNr.aNe = i;
                return this;
            }
            throw new IllegalArgumentException("Invalid camera: " + i);
        }

        public Builder setRequestedFps(float f) {
            if (f <= 0.0f) {
                throw new IllegalArgumentException("Invalid fps: " + f);
            }
            this.aNr.aNg = f;
            return this;
        }

        public Builder setRequestedPreviewSize(int i, int i2) {
            if (i <= 0 || i > 1000000 || i2 <= 0 || i2 > 1000000) {
                throw new IllegalArgumentException("Invalid preview size: " + i + "x" + i2);
            }
            this.aNr.aNh = i;
            this.aNr.aNi = i2;
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
        final /* synthetic */ CameraSource aNs;

        private zza(CameraSource cameraSource) {
            this.aNs = cameraSource;
        }

        public void onPreviewFrame(byte[] bArr, Camera camera) {
            this.aNs.aNo.zza(bArr, camera);
        }
    }

    private class zzb implements Runnable {
        static final /* synthetic */ boolean $assertionsDisabled = (!CameraSource.class.desiredAssertionStatus());
        private Detector<?> aNq;
        final /* synthetic */ CameraSource aNs;
        private boolean aNt = true;
        private long aNu;
        private int aNv = 0;
        private ByteBuffer aNw;
        private long eg = SystemClock.elapsedRealtime();
        private final Object zzako = new Object();

        zzb(CameraSource cameraSource, Detector<?> detector) {
            this.aNs = cameraSource;
            this.aNq = detector;
        }

        @SuppressLint({"Assert"})
        void release() {
            if ($assertionsDisabled || this.aNs.aNn.getState() == State.TERMINATED) {
                this.aNq.release();
                this.aNq = null;
                return;
            }
            throw new AssertionError();
        }

        /* JADX WARNING: inconsistent code. */
        /* Code decompiled incorrectly, please refer to instructions dump. */
        @SuppressLint({"InlinedApi"})
        public void run() {
            while (true) {
                synchronized (this.zzako) {
                    while (this.aNt && this.aNw == null) {
                        try {
                            this.zzako.wait();
                        } catch (Throwable e) {
                            Log.d("CameraSource", "Frame processing loop terminated.", e);
                            return;
                        }
                    }
                    if (this.aNt) {
                        Frame build = new com.google.android.gms.vision.Frame.Builder().setImageData(this.aNw, this.aNs.aNf.getWidth(), this.aNs.aNf.getHeight(), 17).setId(this.aNv).setTimestampMillis(this.aNu).setRotation(this.aNs.zzbzf).build();
                        ByteBuffer byteBuffer = this.aNw;
                        this.aNw = null;
                    } else {
                        return;
                    }
                }
            }
        }

        void setActive(boolean z) {
            synchronized (this.zzako) {
                this.aNt = z;
                this.zzako.notifyAll();
            }
        }

        void zza(byte[] bArr, Camera camera) {
            synchronized (this.zzako) {
                if (this.aNw != null) {
                    camera.addCallbackBuffer(this.aNw.array());
                    this.aNw = null;
                }
                if (this.aNs.aNp.containsKey(bArr)) {
                    this.aNu = SystemClock.elapsedRealtime() - this.eg;
                    this.aNv++;
                    this.aNw = (ByteBuffer) this.aNs.aNp.get(bArr);
                    this.zzako.notifyAll();
                    return;
                }
                Log.d("CameraSource", "Skipping frame. Could not find ByteBuffer associated with the image data from the camera.");
            }
        }
    }

    private class zzc implements android.hardware.Camera.PictureCallback {
        final /* synthetic */ CameraSource aNs;
        private PictureCallback aNx;

        private zzc(CameraSource cameraSource) {
            this.aNs = cameraSource;
        }

        public void onPictureTaken(byte[] bArr, Camera camera) {
            if (this.aNx != null) {
                this.aNx.onPictureTaken(bArr);
            }
            synchronized (this.aNs.aNc) {
                if (this.aNs.aNd != null) {
                    this.aNs.aNd.startPreview();
                }
            }
        }
    }

    private class zzd implements android.hardware.Camera.ShutterCallback {
        final /* synthetic */ CameraSource aNs;
        private ShutterCallback aNy;

        private zzd(CameraSource cameraSource) {
            this.aNs = cameraSource;
        }

        public void onShutter() {
            if (this.aNy != null) {
                this.aNy.onShutter();
            }
        }
    }

    static class zze {
        private Size aNA;
        private Size aNz;

        public zze(Camera.Size size, Camera.Size size2) {
            this.aNz = new Size(size.width, size.height);
            if (size2 != null) {
                this.aNA = new Size(size2.width, size2.height);
            }
        }

        public Size zzcll() {
            return this.aNz;
        }

        public Size zzclm() {
            return this.aNA;
        }
    }

    private CameraSource() {
        this.aNc = new Object();
        this.aNe = 0;
        this.aNg = BitmapDescriptorFactory.HUE_ORANGE;
        this.aNh = 1024;
        this.aNi = 768;
        this.aNj = false;
        this.aNp = new HashMap();
    }

    static zze zza(Camera camera, int i, int i2) {
        List<zze> zza = zza(camera);
        zze com_google_android_gms_vision_CameraSource_zze = null;
        int i3 = ConnectionsManager.DEFAULT_DATACENTER_ID;
        for (zze com_google_android_gms_vision_CameraSource_zze2 : zza) {
            zze com_google_android_gms_vision_CameraSource_zze3;
            int i4;
            Size zzcll = com_google_android_gms_vision_CameraSource_zze2.zzcll();
            int abs = Math.abs(zzcll.getHeight() - i2) + Math.abs(zzcll.getWidth() - i);
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
        this.zzbzf = i2 / 90;
        camera.setDisplayOrientation(rotation);
        parameters.setRotation(i2);
    }

    @SuppressLint({"InlinedApi"})
    private byte[] zza(Size size) {
        Object obj = new byte[(((int) Math.ceil(((double) ((long) (ImageFormat.getBitsPerPixel(17) * (size.getHeight() * size.getWidth())))) / 8.0d)) + 1)];
        ByteBuffer wrap = ByteBuffer.wrap(obj);
        if (wrap.hasArray() && wrap.array() == obj) {
            this.aNp.put(obj, wrap);
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

    private static int zzaap(int i) {
        CameraInfo cameraInfo = new CameraInfo();
        for (int i2 = 0; i2 < Camera.getNumberOfCameras(); i2++) {
            Camera.getCameraInfo(i2, cameraInfo);
            if (cameraInfo.facing == i) {
                return i2;
            }
        }
        return -1;
    }

    @SuppressLint({"InlinedApi"})
    private Camera zzclk() {
        int zzaap = zzaap(this.aNe);
        if (zzaap == -1) {
            throw new RuntimeException("Could not find requested camera.");
        }
        Camera open = Camera.open(zzaap);
        zze zza = zza(open, this.aNh, this.aNi);
        if (zza == null) {
            throw new RuntimeException("Could not find suitable preview size.");
        }
        Size zzclm = zza.zzclm();
        this.aNf = zza.zzcll();
        int[] zza2 = zza(open, this.aNg);
        if (zza2 == null) {
            throw new RuntimeException("Could not find suitable preview frames per second range.");
        }
        Parameters parameters = open.getParameters();
        if (zzclm != null) {
            parameters.setPictureSize(zzclm.getWidth(), zzclm.getHeight());
        }
        parameters.setPreviewSize(this.aNf.getWidth(), this.aNf.getHeight());
        parameters.setPreviewFpsRange(zza2[0], zza2[1]);
        parameters.setPreviewFormat(17);
        zza(open, parameters, zzaap);
        if (this.aNj) {
            if (parameters.getSupportedFocusModes().contains("continuous-video")) {
                parameters.setFocusMode("continuous-video");
            } else {
                Log.i("CameraSource", "Camera auto focus is not supported on this device.");
            }
        }
        open.setParameters(parameters);
        open.setPreviewCallbackWithBuffer(new zza());
        open.addCallbackBuffer(zza(this.aNf));
        open.addCallbackBuffer(zza(this.aNf));
        open.addCallbackBuffer(zza(this.aNf));
        open.addCallbackBuffer(zza(this.aNf));
        return open;
    }

    public int getCameraFacing() {
        return this.aNe;
    }

    public Size getPreviewSize() {
        return this.aNf;
    }

    public void release() {
        synchronized (this.aNc) {
            stop();
            this.aNo.release();
        }
    }

    @RequiresPermission("android.permission.CAMERA")
    public CameraSource start() throws IOException {
        synchronized (this.aNc) {
            if (this.aNd != null) {
            } else {
                this.aNd = zzclk();
                if (VERSION.SDK_INT >= 11) {
                    this.aNl = new SurfaceTexture(100);
                    this.aNd.setPreviewTexture(this.aNl);
                    this.aNm = true;
                } else {
                    this.aNk = new SurfaceView(this.mContext);
                    this.aNd.setPreviewDisplay(this.aNk.getHolder());
                    this.aNm = false;
                }
                this.aNd.startPreview();
                this.aNn = new Thread(this.aNo);
                this.aNo.setActive(true);
                this.aNn.start();
            }
        }
        return this;
    }

    @RequiresPermission("android.permission.CAMERA")
    public CameraSource start(SurfaceHolder surfaceHolder) throws IOException {
        synchronized (this.aNc) {
            if (this.aNd != null) {
            } else {
                this.aNd = zzclk();
                this.aNd.setPreviewDisplay(surfaceHolder);
                this.aNd.startPreview();
                this.aNn = new Thread(this.aNo);
                this.aNo.setActive(true);
                this.aNn.start();
                this.aNm = false;
            }
        }
        return this;
    }

    public void stop() {
        synchronized (this.aNc) {
            this.aNo.setActive(false);
            if (this.aNn != null) {
                try {
                    this.aNn.join();
                } catch (InterruptedException e) {
                    Log.d("CameraSource", "Frame processing thread interrupted on release.");
                }
                this.aNn = null;
            }
            if (this.aNd != null) {
                this.aNd.stopPreview();
                this.aNd.setPreviewCallbackWithBuffer(null);
                try {
                    if (this.aNm) {
                        this.aNd.setPreviewTexture(null);
                    } else {
                        this.aNd.setPreviewDisplay(null);
                    }
                } catch (Exception e2) {
                    String valueOf = String.valueOf(e2);
                    Log.e("CameraSource", new StringBuilder(String.valueOf(valueOf).length() + 32).append("Failed to clear camera preview: ").append(valueOf).toString());
                }
                this.aNd.release();
                this.aNd = null;
            }
            this.aNp.clear();
        }
    }

    public void takePicture(ShutterCallback shutterCallback, PictureCallback pictureCallback) {
        synchronized (this.aNc) {
            if (this.aNd != null) {
                android.hardware.Camera.ShutterCallback com_google_android_gms_vision_CameraSource_zzd = new zzd();
                com_google_android_gms_vision_CameraSource_zzd.aNy = shutterCallback;
                android.hardware.Camera.PictureCallback com_google_android_gms_vision_CameraSource_zzc = new zzc();
                com_google_android_gms_vision_CameraSource_zzc.aNx = pictureCallback;
                this.aNd.takePicture(com_google_android_gms_vision_CameraSource_zzd, null, null, com_google_android_gms_vision_CameraSource_zzc);
            }
        }
    }
}
