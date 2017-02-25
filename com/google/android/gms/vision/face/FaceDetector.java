package com.google.android.gms.vision.face;

import android.content.Context;
import android.util.Log;
import android.util.SparseArray;
import com.google.android.gms.internal.zzbjq;
import com.google.android.gms.internal.zzbjt;
import com.google.android.gms.internal.zzbka;
import com.google.android.gms.vision.Detector;
import com.google.android.gms.vision.Frame;
import com.google.android.gms.vision.zza;
import java.nio.ByteBuffer;
import java.util.HashSet;
import java.util.Set;

public final class FaceDetector extends Detector<Face> {
    public static final int ACCURATE_MODE = 1;
    public static final int ALL_CLASSIFICATIONS = 1;
    public static final int ALL_LANDMARKS = 1;
    public static final int FAST_MODE = 0;
    public static final int NO_CLASSIFICATIONS = 0;
    public static final int NO_LANDMARKS = 0;
    private final zza zzbOS;
    private final zzbjq zzbOT;
    private boolean zzbOU;
    private final Object zzrJ;

    public static class Builder {
        private final Context mContext;
        private int zzaKF = 0;
        private int zzbOV = 0;
        private boolean zzbOW = false;
        private int zzbOX = 0;
        private boolean zzbOY = true;
        private float zzbOZ = -1.0f;

        public Builder(Context context) {
            this.mContext = context;
        }

        public FaceDetector build() {
            zzbjt com_google_android_gms_internal_zzbjt = new zzbjt();
            com_google_android_gms_internal_zzbjt.mode = this.zzaKF;
            com_google_android_gms_internal_zzbjt.zzbPi = this.zzbOV;
            com_google_android_gms_internal_zzbjt.zzbPj = this.zzbOX;
            com_google_android_gms_internal_zzbjt.zzbPk = this.zzbOW;
            com_google_android_gms_internal_zzbjt.zzbPl = this.zzbOY;
            com_google_android_gms_internal_zzbjt.zzbPm = this.zzbOZ;
            return new FaceDetector(new zzbjq(this.mContext, com_google_android_gms_internal_zzbjt));
        }

        public Builder setClassificationType(int i) {
            if (i == 0 || i == 1) {
                this.zzbOX = i;
                return this;
            }
            throw new IllegalArgumentException("Invalid classification type: " + i);
        }

        public Builder setLandmarkType(int i) {
            if (i == 0 || i == 1) {
                this.zzbOV = i;
                return this;
            }
            throw new IllegalArgumentException("Invalid landmark type: " + i);
        }

        public Builder setMinFaceSize(float f) {
            if (f < 0.0f || f > 1.0f) {
                throw new IllegalArgumentException("Invalid proportional face size: " + f);
            }
            this.zzbOZ = f;
            return this;
        }

        public Builder setMode(int i) {
            switch (i) {
                case 0:
                case 1:
                    this.zzaKF = i;
                    return this;
                default:
                    throw new IllegalArgumentException("Invalid mode: " + i);
            }
        }

        public Builder setProminentFaceOnly(boolean z) {
            this.zzbOW = z;
            return this;
        }

        public Builder setTrackingEnabled(boolean z) {
            this.zzbOY = z;
            return this;
        }
    }

    private FaceDetector() {
        this.zzbOS = new zza();
        this.zzrJ = new Object();
        this.zzbOU = true;
        throw new IllegalStateException("Default constructor called");
    }

    private FaceDetector(zzbjq com_google_android_gms_internal_zzbjq) {
        this.zzbOS = new zza();
        this.zzrJ = new Object();
        this.zzbOU = true;
        this.zzbOT = com_google_android_gms_internal_zzbjq;
    }

    public SparseArray<Face> detect(Frame frame) {
        if (frame == null) {
            throw new IllegalArgumentException("No frame supplied.");
        }
        Face[] zzb;
        ByteBuffer grayscaleImageData = frame.getGrayscaleImageData();
        synchronized (this.zzrJ) {
            if (this.zzbOU) {
                zzb = this.zzbOT.zzb(grayscaleImageData, zzbka.zzc(frame));
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
            sparseArray.append(this.zzbOS.zznR(id), face);
        }
        return sparseArray;
    }

    protected void finalize() throws Throwable {
        try {
            synchronized (this.zzrJ) {
                if (this.zzbOU) {
                    Log.w("FaceDetector", "FaceDetector was not released with FaceDetector.release()");
                    release();
                }
            }
        } finally {
            super.finalize();
        }
    }

    public boolean isOperational() {
        return this.zzbOT.isOperational();
    }

    public void release() {
        super.release();
        synchronized (this.zzrJ) {
            if (this.zzbOU) {
                this.zzbOT.zzTQ();
                this.zzbOU = false;
                return;
            }
        }
    }

    public boolean setFocus(int i) {
        boolean zzoh;
        int zznS = this.zzbOS.zznS(i);
        synchronized (this.zzrJ) {
            if (this.zzbOU) {
                zzoh = this.zzbOT.zzoh(zznS);
            } else {
                throw new RuntimeException("Cannot use detector after release()");
            }
        }
        return zzoh;
    }
}
