package com.google.android.gms.vision.text;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Rect;
import android.graphics.YuvImage;
import android.util.SparseArray;
import com.google.android.gms.internal.zzbhd;
import com.google.android.gms.internal.zzbhf;
import com.google.android.gms.internal.zzbhk;
import com.google.android.gms.internal.zzbhm;
import com.google.android.gms.internal.zzbhq;
import com.google.android.gms.internal.zzbhr;
import com.google.android.gms.vision.Detector;
import com.google.android.gms.vision.Frame;
import java.io.ByteArrayOutputStream;
import java.io.OutputStream;
import java.nio.ByteBuffer;

public final class TextRecognizer extends Detector<TextBlock> {
    private final zzbhq zzbNy;

    public static class Builder {
        private Context mContext;
        private zzbhr zzbNz = new zzbhr();

        public Builder(Context context) {
            this.mContext = context;
        }

        public TextRecognizer build() {
            return new TextRecognizer(new zzbhq(this.mContext, this.zzbNz));
        }
    }

    private TextRecognizer() {
        throw new IllegalStateException("Default constructor called");
    }

    private TextRecognizer(zzbhq com_google_android_gms_internal_zzbhq) {
        this.zzbNy = com_google_android_gms_internal_zzbhq;
    }

    private Bitmap zza(ByteBuffer byteBuffer, int i, int i2, int i3) {
        byte[] array;
        if (byteBuffer.hasArray() && byteBuffer.arrayOffset() == 0) {
            array = byteBuffer.array();
        } else {
            array = new byte[byteBuffer.capacity()];
            byteBuffer.get(array);
        }
        OutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        new YuvImage(array, i, i2, i3, null).compressToJpeg(new Rect(0, 0, i2, i3), 100, byteArrayOutputStream);
        byte[] toByteArray = byteArrayOutputStream.toByteArray();
        return BitmapFactory.decodeByteArray(toByteArray, 0, toByteArray.length);
    }

    private Rect zza(Rect rect, int i, int i2, zzbhd com_google_android_gms_internal_zzbhd) {
        switch (com_google_android_gms_internal_zzbhd.rotation) {
            case 1:
                return new Rect(i2 - rect.bottom, rect.left, i2 - rect.top, rect.right);
            case 2:
                return new Rect(i - rect.right, i2 - rect.bottom, i - rect.left, i2 - rect.top);
            case 3:
                return new Rect(rect.top, i - rect.right, rect.bottom, i - rect.left);
            default:
                return rect;
        }
    }

    private SparseArray<TextBlock> zza(zzbhk[] com_google_android_gms_internal_zzbhkArr) {
        int i = 0;
        SparseArray sparseArray = new SparseArray();
        for (zzbhk com_google_android_gms_internal_zzbhk : com_google_android_gms_internal_zzbhkArr) {
            SparseArray sparseArray2 = (SparseArray) sparseArray.get(com_google_android_gms_internal_zzbhk.zzbNJ);
            if (sparseArray2 == null) {
                sparseArray2 = new SparseArray();
                sparseArray.append(com_google_android_gms_internal_zzbhk.zzbNJ, sparseArray2);
            }
            sparseArray2.append(com_google_android_gms_internal_zzbhk.zzbNK, com_google_android_gms_internal_zzbhk);
        }
        SparseArray<TextBlock> sparseArray3 = new SparseArray(sparseArray.size());
        while (i < sparseArray.size()) {
            sparseArray3.append(sparseArray.keyAt(i), new TextBlock((SparseArray) sparseArray.valueAt(i)));
            i++;
        }
        return sparseArray3;
    }

    public SparseArray<TextBlock> detect(Frame frame) {
        return zza(frame, new zzbhm(1, new Rect()));
    }

    public boolean isOperational() {
        return this.zzbNy.isOperational();
    }

    public void release() {
        super.release();
        this.zzbNy.zzSp();
    }

    public SparseArray<TextBlock> zza(Frame frame, zzbhm com_google_android_gms_internal_zzbhm) {
        if (frame == null) {
            throw new IllegalArgumentException("No frame supplied.");
        }
        Bitmap bitmap;
        zzbhd zzc = zzbhd.zzc(frame);
        if (frame.getBitmap() != null) {
            bitmap = frame.getBitmap();
        } else {
            bitmap = zza(frame.getGrayscaleImageData(), frame.getMetadata().getFormat(), zzc.width, zzc.height);
        }
        bitmap = zzbhf.zzb(bitmap, zzc);
        if (!com_google_android_gms_internal_zzbhm.zzbNL.isEmpty()) {
            com_google_android_gms_internal_zzbhm.zzbNL.set(zza(com_google_android_gms_internal_zzbhm.zzbNL, frame.getMetadata().getWidth(), frame.getMetadata().getHeight(), zzc));
        }
        zzc.rotation = 0;
        return zza(this.zzbNy.zza(bitmap, zzc, com_google_android_gms_internal_zzbhm));
    }
}
