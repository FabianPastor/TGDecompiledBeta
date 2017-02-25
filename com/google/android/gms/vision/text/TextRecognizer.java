package com.google.android.gms.vision.text;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Rect;
import android.graphics.YuvImage;
import android.util.SparseArray;
import com.google.android.gms.internal.zzbka;
import com.google.android.gms.internal.zzbkc;
import com.google.android.gms.internal.zzbkh;
import com.google.android.gms.internal.zzbkj;
import com.google.android.gms.internal.zzbkn;
import com.google.android.gms.internal.zzbko;
import com.google.android.gms.vision.Detector;
import com.google.android.gms.vision.Frame;
import java.io.ByteArrayOutputStream;
import java.io.OutputStream;
import java.nio.ByteBuffer;

public final class TextRecognizer extends Detector<TextBlock> {
    private final zzbkn zzbPx;

    public static class Builder {
        private Context mContext;
        private zzbko zzbPy = new zzbko();

        public Builder(Context context) {
            this.mContext = context;
        }

        public TextRecognizer build() {
            return new TextRecognizer(new zzbkn(this.mContext, this.zzbPy));
        }
    }

    private TextRecognizer() {
        throw new IllegalStateException("Default constructor called");
    }

    private TextRecognizer(zzbkn com_google_android_gms_internal_zzbkn) {
        this.zzbPx = com_google_android_gms_internal_zzbkn;
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

    private Rect zza(Rect rect, int i, int i2, zzbka com_google_android_gms_internal_zzbka) {
        switch (com_google_android_gms_internal_zzbka.rotation) {
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

    private SparseArray<TextBlock> zza(zzbkh[] com_google_android_gms_internal_zzbkhArr) {
        int i = 0;
        SparseArray sparseArray = new SparseArray();
        for (zzbkh com_google_android_gms_internal_zzbkh : com_google_android_gms_internal_zzbkhArr) {
            SparseArray sparseArray2 = (SparseArray) sparseArray.get(com_google_android_gms_internal_zzbkh.zzbPI);
            if (sparseArray2 == null) {
                sparseArray2 = new SparseArray();
                sparseArray.append(com_google_android_gms_internal_zzbkh.zzbPI, sparseArray2);
            }
            sparseArray2.append(com_google_android_gms_internal_zzbkh.zzbPJ, com_google_android_gms_internal_zzbkh);
        }
        SparseArray<TextBlock> sparseArray3 = new SparseArray(sparseArray.size());
        while (i < sparseArray.size()) {
            sparseArray3.append(sparseArray.keyAt(i), new TextBlock((SparseArray) sparseArray.valueAt(i)));
            i++;
        }
        return sparseArray3;
    }

    public SparseArray<TextBlock> detect(Frame frame) {
        return zza(frame, new zzbkj(1, new Rect()));
    }

    public boolean isOperational() {
        return this.zzbPx.isOperational();
    }

    public void release() {
        super.release();
        this.zzbPx.zzTQ();
    }

    public SparseArray<TextBlock> zza(Frame frame, zzbkj com_google_android_gms_internal_zzbkj) {
        if (frame == null) {
            throw new IllegalArgumentException("No frame supplied.");
        }
        Bitmap bitmap;
        zzbka zzc = zzbka.zzc(frame);
        if (frame.getBitmap() != null) {
            bitmap = frame.getBitmap();
        } else {
            bitmap = zza(frame.getGrayscaleImageData(), frame.getMetadata().getFormat(), zzc.width, zzc.height);
        }
        bitmap = zzbkc.zzb(bitmap, zzc);
        if (!com_google_android_gms_internal_zzbkj.zzbPK.isEmpty()) {
            com_google_android_gms_internal_zzbkj.zzbPK.set(zza(com_google_android_gms_internal_zzbkj.zzbPK, frame.getMetadata().getWidth(), frame.getMetadata().getHeight(), zzc));
        }
        zzc.rotation = 0;
        return zza(this.zzbPx.zza(bitmap, zzc, com_google_android_gms_internal_zzbkj));
    }
}
