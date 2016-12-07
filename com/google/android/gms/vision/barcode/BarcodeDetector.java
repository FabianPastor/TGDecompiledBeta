package com.google.android.gms.vision.barcode;

import android.content.Context;
import android.util.SparseArray;
import com.google.android.gms.internal.zzbgo;
import com.google.android.gms.internal.zzbgq;
import com.google.android.gms.internal.zzbhd;
import com.google.android.gms.vision.Detector;
import com.google.android.gms.vision.Frame;

public final class BarcodeDetector extends Detector<Barcode> {
    private final zzbgq zzbMJ;

    public static class Builder {
        private Context mContext;
        private zzbgo zzbMK = new zzbgo();

        public Builder(Context context) {
            this.mContext = context;
        }

        public BarcodeDetector build() {
            return new BarcodeDetector(new zzbgq(this.mContext, this.zzbMK));
        }

        public Builder setBarcodeFormats(int i) {
            this.zzbMK.zzbML = i;
            return this;
        }
    }

    private BarcodeDetector() {
        throw new IllegalStateException("Default constructor called");
    }

    private BarcodeDetector(zzbgq com_google_android_gms_internal_zzbgq) {
        this.zzbMJ = com_google_android_gms_internal_zzbgq;
    }

    public SparseArray<Barcode> detect(Frame frame) {
        if (frame == null) {
            throw new IllegalArgumentException("No frame supplied.");
        }
        Barcode[] zza;
        zzbhd zzc = zzbhd.zzc(frame);
        if (frame.getBitmap() != null) {
            zza = this.zzbMJ.zza(frame.getBitmap(), zzc);
            if (zza == null) {
                throw new IllegalArgumentException("Internal barcode detector error; check logcat output.");
            }
        }
        zza = this.zzbMJ.zza(frame.getGrayscaleImageData(), zzc);
        SparseArray<Barcode> sparseArray = new SparseArray(zza.length);
        for (Barcode barcode : zza) {
            sparseArray.append(barcode.rawValue.hashCode(), barcode);
        }
        return sparseArray;
    }

    public boolean isOperational() {
        return this.zzbMJ.isOperational();
    }

    public void release() {
        super.release();
        this.zzbMJ.zzSp();
    }
}
