package com.google.android.gms.vision.face;

import android.content.Context;
import android.util.Log;
import android.util.SparseArray;
import com.google.android.gms.internal.zzdjw;
import com.google.android.gms.vision.Detector;
import com.google.android.gms.vision.Frame;
import com.google.android.gms.vision.face.internal.client.zza;
import com.google.android.gms.vision.zzc;
import java.nio.ByteBuffer;
import java.util.HashSet;
import java.util.Set;

public final class FaceDetector extends Detector<Face> {
    private boolean mIsActive;
    private final Object mLock;
    private final zzc zzkwy;
    private final zza zzkwz;

    public static class Builder {
        private final Context mContext;
        private int zzgir = 0;
        private int zzkxa = 0;
        private boolean zzkxb = false;
        private int zzkxc = 0;
        private boolean zzkxd = true;
        private float zzkxe = -1.0f;

        public Builder(Context context) {
            this.mContext = context;
        }

        public FaceDetector build() {
            com.google.android.gms.vision.face.internal.client.zzc com_google_android_gms_vision_face_internal_client_zzc = new com.google.android.gms.vision.face.internal.client.zzc();
            com_google_android_gms_vision_face_internal_client_zzc.mode = this.zzgir;
            com_google_android_gms_vision_face_internal_client_zzc.zzkxn = this.zzkxa;
            com_google_android_gms_vision_face_internal_client_zzc.zzkxo = this.zzkxc;
            com_google_android_gms_vision_face_internal_client_zzc.zzkxp = this.zzkxb;
            com_google_android_gms_vision_face_internal_client_zzc.zzkxq = this.zzkxd;
            com_google_android_gms_vision_face_internal_client_zzc.zzkxr = this.zzkxe;
            return new FaceDetector(new zza(this.mContext, com_google_android_gms_vision_face_internal_client_zzc));
        }

        public Builder setLandmarkType(int i) {
            if (i == 0 || i == 1) {
                this.zzkxa = i;
                return this;
            }
            throw new IllegalArgumentException("Invalid landmark type: " + i);
        }

        public Builder setMode(int i) {
            switch (i) {
                case 0:
                case 1:
                    this.zzgir = i;
                    return this;
                default:
                    throw new IllegalArgumentException("Invalid mode: " + i);
            }
        }

        public Builder setTrackingEnabled(boolean z) {
            this.zzkxd = z;
            return this;
        }
    }

    private FaceDetector() {
        this.zzkwy = new zzc();
        this.mLock = new Object();
        this.mIsActive = true;
        throw new IllegalStateException("Default constructor called");
    }

    private FaceDetector(zza com_google_android_gms_vision_face_internal_client_zza) {
        this.zzkwy = new zzc();
        this.mLock = new Object();
        this.mIsActive = true;
        this.zzkwz = com_google_android_gms_vision_face_internal_client_zza;
    }

    public final SparseArray<Face> detect(Frame frame) {
        if (frame == null) {
            throw new IllegalArgumentException("No frame supplied.");
        }
        Face[] zzb;
        ByteBuffer grayscaleImageData = frame.getGrayscaleImageData();
        synchronized (this.mLock) {
            if (this.mIsActive) {
                zzb = this.zzkwz.zzb(grayscaleImageData, zzdjw.zzc(frame));
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
            sparseArray.append(this.zzkwy.zzex(id), face);
        }
        return sparseArray;
    }

    protected final void finalize() throws Throwable {
        try {
            synchronized (this.mLock) {
                if (this.mIsActive) {
                    Log.w("FaceDetector", "FaceDetector was not released with FaceDetector.release()");
                    release();
                }
            }
        } finally {
            super.finalize();
        }
    }

    public final boolean isOperational() {
        return this.zzkwz.isOperational();
    }

    public final void release() {
        super.release();
        synchronized (this.mLock) {
            if (this.mIsActive) {
                this.zzkwz.zzbju();
                this.mIsActive = false;
                return;
            }
        }
    }
}
