package com.google.android.gms.vision.barcode;

import android.content.Context;
import android.util.SparseArray;
import com.google.android.gms.vision.Detector;
import com.google.android.gms.vision.Frame;
import com.google.android.gms.vision.barcode.internal.client.BarcodeDetectorOptions;
import com.google.android.gms.vision.barcode.internal.client.zzb;
import com.google.android.gms.vision.internal.client.FrameMetadataParcel;

public final class BarcodeDetector extends Detector<Barcode> {
    private final zzb aNX;

    public static class Builder {
        private BarcodeDetectorOptions aNY = new BarcodeDetectorOptions();
        private Context mContext;

        public Builder(Context context) {
            this.mContext = context;
        }

        public BarcodeDetector build() {
            return new BarcodeDetector(new zzb(this.mContext, this.aNY));
        }

        public Builder setBarcodeFormats(int i) {
            this.aNY.aNZ = i;
            return this;
        }
    }

    private BarcodeDetector() {
        throw new IllegalStateException("Default constructor called");
    }

    private BarcodeDetector(zzb com_google_android_gms_vision_barcode_internal_client_zzb) {
        this.aNX = com_google_android_gms_vision_barcode_internal_client_zzb;
    }

    public SparseArray<Barcode> detect(Frame frame) {
        if (frame == null) {
            throw new IllegalArgumentException("No frame supplied.");
        }
        Barcode[] zza;
        FrameMetadataParcel zzc = FrameMetadataParcel.zzc(frame);
        if (frame.getBitmap() != null) {
            zza = this.aNX.zza(frame.getBitmap(), zzc);
            if (zza == null) {
                throw new IllegalArgumentException("Internal barcode detector error; check logcat output.");
            }
        }
        zza = this.aNX.zza(frame.getGrayscaleImageData(), zzc);
        SparseArray<Barcode> sparseArray = new SparseArray(zza.length);
        for (Barcode barcode : zza) {
            sparseArray.append(barcode.rawValue.hashCode(), barcode);
        }
        return sparseArray;
    }

    public boolean isOperational() {
        return this.aNX.isOperational();
    }

    public void release() {
        super.release();
        this.aNX.zzclr();
    }
}
