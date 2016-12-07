package com.google.android.gms.vision.face;

import android.content.Context;
import android.util.Log;
import android.util.SparseArray;
import com.google.android.gms.vision.Detector;
import com.google.android.gms.vision.Frame;
import com.google.android.gms.vision.face.internal.client.FaceSettingsParcel;
import com.google.android.gms.vision.internal.client.FrameMetadataParcel;
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
    private final zza aOh;
    private final com.google.android.gms.vision.face.internal.client.zza aOi;
    private boolean aOj;
    private final Object zzako;

    public static class Builder {
        private int IT = 0;
        private int aOk = 0;
        private boolean aOl = false;
        private int aOm = 0;
        private boolean aOn = true;
        private float aOo = -1.0f;
        private final Context mContext;

        public Builder(Context context) {
            this.mContext = context;
        }

        public FaceDetector build() {
            FaceSettingsParcel faceSettingsParcel = new FaceSettingsParcel();
            faceSettingsParcel.mode = this.IT;
            faceSettingsParcel.aOx = this.aOk;
            faceSettingsParcel.aOy = this.aOm;
            faceSettingsParcel.aOz = this.aOl;
            faceSettingsParcel.aOA = this.aOn;
            faceSettingsParcel.aOB = this.aOo;
            return new FaceDetector(new com.google.android.gms.vision.face.internal.client.zza(this.mContext, faceSettingsParcel));
        }

        public Builder setClassificationType(int i) {
            if (i == 0 || i == 1) {
                this.aOm = i;
                return this;
            }
            throw new IllegalArgumentException("Invalid classification type: " + i);
        }

        public Builder setLandmarkType(int i) {
            if (i == 0 || i == 1) {
                this.aOk = i;
                return this;
            }
            throw new IllegalArgumentException("Invalid landmark type: " + i);
        }

        public Builder setMinFaceSize(float f) {
            if (f < 0.0f || f > DefaultRetryPolicy.DEFAULT_BACKOFF_MULT) {
                throw new IllegalArgumentException("Invalid proportional face size: " + f);
            }
            this.aOo = f;
            return this;
        }

        public Builder setMode(int i) {
            switch (i) {
                case 0:
                case 1:
                    this.IT = i;
                    return this;
                default:
                    throw new IllegalArgumentException("Invalid mode: " + i);
            }
        }

        public Builder setProminentFaceOnly(boolean z) {
            this.aOl = z;
            return this;
        }

        public Builder setTrackingEnabled(boolean z) {
            this.aOn = z;
            return this;
        }
    }

    private FaceDetector() {
        this.aOh = new zza();
        this.zzako = new Object();
        this.aOj = true;
        throw new IllegalStateException("Default constructor called");
    }

    private FaceDetector(com.google.android.gms.vision.face.internal.client.zza com_google_android_gms_vision_face_internal_client_zza) {
        this.aOh = new zza();
        this.zzako = new Object();
        this.aOj = true;
        this.aOi = com_google_android_gms_vision_face_internal_client_zza;
    }

    public SparseArray<Face> detect(Frame frame) {
        if (frame == null) {
            throw new IllegalArgumentException("No frame supplied.");
        }
        Face[] zzb;
        ByteBuffer grayscaleImageData = frame.getGrayscaleImageData();
        synchronized (this.zzako) {
            if (this.aOj) {
                zzb = this.aOi.zzb(grayscaleImageData, FrameMetadataParcel.zzc(frame));
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
            sparseArray.append(this.aOh.zzaar(id), face);
        }
        return sparseArray;
    }

    protected void finalize() throws Throwable {
        try {
            synchronized (this.zzako) {
                if (this.aOj) {
                    Log.w("FaceDetector", "FaceDetector was not released with FaceDetector.release()");
                    release();
                }
            }
        } finally {
            super.finalize();
        }
    }

    public boolean isOperational() {
        return this.aOi.isOperational();
    }

    public void release() {
        super.release();
        synchronized (this.zzako) {
            if (this.aOj) {
                this.aOi.zzclr();
                this.aOj = false;
                return;
            }
        }
    }

    public boolean setFocus(int i) {
        boolean zzabh;
        int zzaas = this.aOh.zzaas(i);
        synchronized (this.zzako) {
            if (this.aOj) {
                zzabh = this.aOi.zzabh(zzaas);
            } else {
                throw new RuntimeException("Cannot use detector after release()");
            }
        }
        return zzabh;
    }
}
