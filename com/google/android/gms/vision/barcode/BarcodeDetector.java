package com.google.android.gms.vision.barcode;

import android.content.Context;
import android.util.SparseArray;
import com.google.android.gms.internal.zzbjl;
import com.google.android.gms.internal.zzbjn;
import com.google.android.gms.internal.zzbka;
import com.google.android.gms.vision.Detector;
import com.google.android.gms.vision.Frame;

public final class BarcodeDetector extends Detector<Barcode> {
    private final zzbjn zzbOE;

    public static class Builder {
        private Context mContext;
        private zzbjl zzbOF = new zzbjl();

        public Builder(Context context) {
            this.mContext = context;
        }

        public BarcodeDetector build() {
            return new BarcodeDetector(new zzbjn(this.mContext, this.zzbOF));
        }

        public Builder setBarcodeFormats(int i) {
            this.zzbOF.zzbOG = i;
            return this;
        }
    }

    private BarcodeDetector() {
        throw new IllegalStateException("Default constructor called");
    }

    private BarcodeDetector(zzbjn com_google_android_gms_internal_zzbjn) {
        this.zzbOE = com_google_android_gms_internal_zzbjn;
    }

    public SparseArray<Barcode> detect(Frame frame) {
        if (frame == null) {
            throw new IllegalArgumentException("No frame supplied.");
        }
        Barcode[] zza;
        zzbka zzc = zzbka.zzc(frame);
        if (frame.getBitmap() != null) {
            zza = this.zzbOE.zza(frame.getBitmap(), zzc);
            if (zza == null) {
                throw new IllegalArgumentException("Internal barcode detector error; check logcat output.");
            }
        }
        zza = this.zzbOE.zza(frame.getGrayscaleImageData(), zzc);
        SparseArray<Barcode> sparseArray = new SparseArray(zza.length);
        for (Barcode barcode : zza) {
            sparseArray.append(barcode.rawValue.hashCode(), barcode);
        }
        return sparseArray;
    }

    public boolean isOperational() {
        return this.zzbOE.isOperational();
    }

    public void release() {
        super.release();
        this.zzbOE.zzTR();
    }
}
