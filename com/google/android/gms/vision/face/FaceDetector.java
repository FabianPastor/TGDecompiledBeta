package com.google.android.gms.vision.face;

import android.content.Context;
import android.util.Log;
import android.util.SparseArray;
import com.google.android.gms.internal.zzbgt;
import com.google.android.gms.internal.zzbgw;
import com.google.android.gms.internal.zzbhd;
import com.google.android.gms.vision.Detector;
import com.google.android.gms.vision.Frame;
import com.google.android.gms.vision.zza;
import java.nio.ByteBuffer;
import java.util.HashSet;
import java.util.Set;
import org.telegram.messenger.volley.DefaultRetryPolicy;

public final class FaceDetector extends Detector<Face> {
    public static final int ACCURATE_MODE = 1;
    public static final int ALL_CLASSIFICATIONS = 1;
    public static final int ALL_LANDMARKS = 1;
    public static final int FAST_MODE = 0;
    public static final int NO_CLASSIFICATIONS = 0;
    public static final int NO_LANDMARKS = 0;
    private final zza zzbMT;
    private final zzbgt zzbMU;
    private boolean zzbMV;
    private final Object zzrN;

    public static class Builder {
        private final Context mContext;
        private int zzaJi = 0;
        private int zzbMW = 0;
        private boolean zzbMX = false;
        private int zzbMY = 0;
        private boolean zzbMZ = true;
        private float zzbNa = -1.0f;

        public Builder(Context context) {
            this.mContext = context;
        }

        public FaceDetector build() {
            zzbgw com_google_android_gms_internal_zzbgw = new zzbgw();
            com_google_android_gms_internal_zzbgw.mode = this.zzaJi;
            com_google_android_gms_internal_zzbgw.zzbNj = this.zzbMW;
            com_google_android_gms_internal_zzbgw.zzbNk = this.zzbMY;
            com_google_android_gms_internal_zzbgw.zzbNl = this.zzbMX;
            com_google_android_gms_internal_zzbgw.zzbNm = this.zzbMZ;
            com_google_android_gms_internal_zzbgw.zzbNn = this.zzbNa;
            return new FaceDetector(new zzbgt(this.mContext, com_google_android_gms_internal_zzbgw));
        }

        public Builder setClassificationType(int i) {
            if (i == 0 || i == 1) {
                this.zzbMY = i;
                return this;
            }
            throw new IllegalArgumentException("Invalid classification type: " + i);
        }

        public Builder setLandmarkType(int i) {
            if (i == 0 || i == 1) {
                this.zzbMW = i;
                return this;
            }
            throw new IllegalArgumentException("Invalid landmark type: " + i);
        }

        public Builder setMinFaceSize(float f) {
            if (f < 0.0f || f > DefaultRetryPolicy.DEFAULT_BACKOFF_MULT) {
                throw new IllegalArgumentException("Invalid proportional face size: " + f);
            }
            this.zzbNa = f;
            return this;
        }

        public Builder setMode(int i) {
            switch (i) {
                case 0:
                case 1:
                    this.zzaJi = i;
                    return this;
                default:
                    throw new IllegalArgumentException("Invalid mode: " + i);
            }
        }

        public Builder setProminentFaceOnly(boolean z) {
            this.zzbMX = z;
            return this;
        }

        public Builder setTrackingEnabled(boolean z) {
            this.zzbMZ = z;
            return this;
        }
    }

    private FaceDetector() {
        this.zzbMT = new zza();
        this.zzrN = new Object();
        this.zzbMV = true;
        throw new IllegalStateException("Default constructor called");
    }

    private FaceDetector(zzbgt com_google_android_gms_internal_zzbgt) {
        this.zzbMT = new zza();
        this.zzrN = new Object();
        this.zzbMV = true;
        this.zzbMU = com_google_android_gms_internal_zzbgt;
    }

    public SparseArray<Face> detect(Frame frame) {
        if (frame == null) {
            throw new IllegalArgumentException("No frame supplied.");
        }
        Face[] zzb;
        ByteBuffer grayscaleImageData = frame.getGrayscaleImageData();
        synchronized (this.zzrN) {
            if (this.zzbMV) {
                zzb = this.zzbMU.zzb(grayscaleImageData, zzbhd.zzc(frame));
            } else {
                throw new RuntimeException("Cannot use detector after release()");
            }
        }
        Set hashSet = new HashSet();
        SparseArray<Face> sparseArray = new SparseArray(zzb.length);
        int i = 0;
        for (Face face : zzb) {
            int id = face.getId();
            i = Math.max(i, id);
            if (hashSet.contains(Integer.valueOf(id))) {
                id = i + 1;
                i = id;
            }
            hashSet.add(Integer.valueOf(id));
            sparseArray.append(this.zzbMT.zzng(id), face);
        }
        return sparseArray;
    }

    protected void finalize() throws Throwable {
        try {
            synchronized (this.zzrN) {
                if (this.zzbMV) {
                    Log.w("FaceDetector", "FaceDetector was not released with FaceDetector.release()");
                    release();
                }
            }
        } finally {
            super.finalize();
        }
    }

    public boolean isOperational() {
        return this.zzbMU.isOperational();
    }

    public void release() {
        super.release();
        synchronized (this.zzrN) {
            if (this.zzbMV) {
                this.zzbMU.zzSp();
                this.zzbMV = false;
                return;
            }
        }
    }

    public boolean setFocus(int i) {
        boolean zznw;
        int zznh = this.zzbMT.zznh(i);
        synchronized (this.zzrN) {
            if (this.zzbMV) {
                zznw = this.zzbMU.zznw(zznh);
            } else {
                throw new RuntimeException("Cannot use detector after release()");
            }
        }
        return zznw;
    }
}
