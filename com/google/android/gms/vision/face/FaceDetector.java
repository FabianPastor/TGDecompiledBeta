package com.google.android.gms.vision.face;

import android.content.Context;
import android.util.Log;
import android.util.SparseArray;
import com.google.android.gms.internal.fb;
import com.google.android.gms.vision.Detector;
import com.google.android.gms.vision.Frame;
import com.google.android.gms.vision.face.internal.client.zza;
import com.google.android.gms.vision.zzc;
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
    private final Object mLock;
    private final zzc zzbNo;
    private final zza zzbNp;
    private boolean zzbNq;

    public static class Builder {
        private final Context mContext;
        private int zzaLU = 0;
        private int zzbNr = 0;
        private boolean zzbNs = false;
        private int zzbNt = 0;
        private boolean zzbNu = true;
        private float zzbNv = -1.0f;

        public Builder(Context context) {
            this.mContext = context;
        }

        public FaceDetector build() {
            com.google.android.gms.vision.face.internal.client.zzc com_google_android_gms_vision_face_internal_client_zzc = new com.google.android.gms.vision.face.internal.client.zzc();
            com_google_android_gms_vision_face_internal_client_zzc.mode = this.zzaLU;
            com_google_android_gms_vision_face_internal_client_zzc.zzbNE = this.zzbNr;
            com_google_android_gms_vision_face_internal_client_zzc.zzbNF = this.zzbNt;
            com_google_android_gms_vision_face_internal_client_zzc.zzbNG = this.zzbNs;
            com_google_android_gms_vision_face_internal_client_zzc.zzbNH = this.zzbNu;
            com_google_android_gms_vision_face_internal_client_zzc.zzbNI = this.zzbNv;
            return new FaceDetector(new zza(this.mContext, com_google_android_gms_vision_face_internal_client_zzc));
        }

        public Builder setClassificationType(int i) {
            if (i == 0 || i == 1) {
                this.zzbNt = i;
                return this;
            }
            throw new IllegalArgumentException("Invalid classification type: " + i);
        }

        public Builder setLandmarkType(int i) {
            if (i == 0 || i == 1) {
                this.zzbNr = i;
                return this;
            }
            throw new IllegalArgumentException("Invalid landmark type: " + i);
        }

        public Builder setMinFaceSize(float f) {
            if (f < 0.0f || f > 1.0f) {
                throw new IllegalArgumentException("Invalid proportional face size: " + f);
            }
            this.zzbNv = f;
            return this;
        }

        public Builder setMode(int i) {
            switch (i) {
                case 0:
                case 1:
                    this.zzaLU = i;
                    return this;
                default:
                    throw new IllegalArgumentException("Invalid mode: " + i);
            }
        }

        public Builder setProminentFaceOnly(boolean z) {
            this.zzbNs = z;
            return this;
        }

        public Builder setTrackingEnabled(boolean z) {
            this.zzbNu = z;
            return this;
        }
    }

    private FaceDetector() {
        this.zzbNo = new zzc();
        this.mLock = new Object();
        this.zzbNq = true;
        throw new IllegalStateException("Default constructor called");
    }

    private FaceDetector(zza com_google_android_gms_vision_face_internal_client_zza) {
        this.zzbNo = new zzc();
        this.mLock = new Object();
        this.zzbNq = true;
        this.zzbNp = com_google_android_gms_vision_face_internal_client_zza;
    }

    public final SparseArray<Face> detect(Frame frame) {
        if (frame == null) {
            throw new IllegalArgumentException("No frame supplied.");
        }
        Face[] zzb;
        ByteBuffer grayscaleImageData = frame.getGrayscaleImageData();
        synchronized (this.mLock) {
            if (this.zzbNq) {
                zzb = this.zzbNp.zzb(grayscaleImageData, fb.zzc(frame));
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
            sparseArray.append(this.zzbNo.zzbL(id), face);
        }
        return sparseArray;
    }

    protected final void finalize() throws Throwable {
        try {
            synchronized (this.mLock) {
                if (this.zzbNq) {
                    Log.w("FaceDetector", "FaceDetector was not released with FaceDetector.release()");
                    release();
                }
            }
        } finally {
            super.finalize();
        }
    }

    public final boolean isOperational() {
        return this.zzbNp.isOperational();
    }

    public final void release() {
        super.release();
        synchronized (this.mLock) {
            if (this.zzbNq) {
                this.zzbNp.zzDP();
                this.zzbNq = false;
                return;
            }
        }
    }

    public final boolean setFocus(int i) {
        boolean zzbN;
        int zzbM = this.zzbNo.zzbM(i);
        synchronized (this.mLock) {
            if (this.zzbNq) {
                zzbN = this.zzbNp.zzbN(zzbM);
            } else {
                throw new RuntimeException("Cannot use detector after release()");
            }
        }
        return zzbN;
    }
}
