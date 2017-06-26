package com.google.android.gms.vision.text;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.graphics.Rect;
import android.graphics.YuvImage;
import android.util.SparseArray;
import com.google.android.gms.internal.fb;
import com.google.android.gms.internal.fj;
import com.google.android.gms.internal.fl;
import com.google.android.gms.internal.fp;
import com.google.android.gms.internal.fq;
import com.google.android.gms.vision.Detector;
import com.google.android.gms.vision.Frame;
import com.google.android.gms.vision.Frame.Metadata;
import java.io.ByteArrayOutputStream;
import java.io.OutputStream;
import java.nio.ByteBuffer;

public final class TextRecognizer extends Detector<TextBlock> {
    private final fp zzbNS;

    public static class Builder {
        private Context mContext;
        private fq zzbNT = new fq();

        public Builder(Context context) {
            this.mContext = context;
        }

        public TextRecognizer build() {
            return new TextRecognizer(new fp(this.mContext, this.zzbNT));
        }
    }

    private TextRecognizer() {
        throw new IllegalStateException("Default constructor called");
    }

    private TextRecognizer(fp fpVar) {
        this.zzbNS = fpVar;
    }

    private static SparseArray<TextBlock> zza(fj[] fjVarArr) {
        int i = 0;
        SparseArray sparseArray = new SparseArray();
        for (fj fjVar : fjVarArr) {
            SparseArray sparseArray2 = (SparseArray) sparseArray.get(fjVar.zzbOd);
            if (sparseArray2 == null) {
                sparseArray2 = new SparseArray();
                sparseArray.append(fjVar.zzbOd, sparseArray2);
            }
            sparseArray2.append(fjVar.zzbOe, fjVar);
        }
        SparseArray<TextBlock> sparseArray3 = new SparseArray(sparseArray.size());
        while (i < sparseArray.size()) {
            sparseArray3.append(sparseArray.keyAt(i), new TextBlock((SparseArray) sparseArray.valueAt(i)));
            i++;
        }
        return sparseArray3;
    }

    public final SparseArray<TextBlock> detect(Frame frame) {
        fl flVar = new fl(new Rect());
        if (frame == null) {
            throw new IllegalArgumentException("No frame supplied.");
        }
        Bitmap bitmap;
        int i;
        int i2;
        fb zzc = fb.zzc(frame);
        if (frame.getBitmap() != null) {
            bitmap = frame.getBitmap();
        } else {
            byte[] array;
            Metadata metadata = frame.getMetadata();
            ByteBuffer grayscaleImageData = frame.getGrayscaleImageData();
            int format = metadata.getFormat();
            i = zzc.width;
            i2 = zzc.height;
            if (grayscaleImageData.hasArray() && grayscaleImageData.arrayOffset() == 0) {
                array = grayscaleImageData.array();
            } else {
                array = new byte[grayscaleImageData.capacity()];
                grayscaleImageData.get(array);
            }
            OutputStream byteArrayOutputStream = new ByteArrayOutputStream();
            new YuvImage(array, format, i, i2, null).compressToJpeg(new Rect(0, 0, i, i2), 100, byteArrayOutputStream);
            byte[] toByteArray = byteArrayOutputStream.toByteArray();
            bitmap = BitmapFactory.decodeByteArray(toByteArray, 0, toByteArray.length);
        }
        i = bitmap.getWidth();
        i2 = bitmap.getHeight();
        if (zzc.rotation != 0) {
            int i3;
            Matrix matrix = new Matrix();
            switch (zzc.rotation) {
                case 0:
                    i3 = 0;
                    break;
                case 1:
                    i3 = 90;
                    break;
                case 2:
                    i3 = 180;
                    break;
                case 3:
                    i3 = 270;
                    break;
                default:
                    throw new IllegalArgumentException("Unsupported rotation degree.");
            }
            matrix.postRotate((float) i3);
            bitmap = Bitmap.createBitmap(bitmap, 0, 0, i, i2, matrix, false);
        }
        if (zzc.rotation == 1 || zzc.rotation == 3) {
            zzc.width = i2;
            zzc.height = i;
        }
        if (!flVar.zzbOf.isEmpty()) {
            Rect rect;
            Rect rect2 = flVar.zzbOf;
            i = frame.getMetadata().getWidth();
            i2 = frame.getMetadata().getHeight();
            switch (zzc.rotation) {
                case 1:
                    rect = new Rect(i2 - rect2.bottom, rect2.left, i2 - rect2.top, rect2.right);
                    break;
                case 2:
                    rect = new Rect(i - rect2.right, i2 - rect2.bottom, i - rect2.left, i2 - rect2.top);
                    break;
                case 3:
                    rect = new Rect(rect2.top, i - rect2.right, rect2.bottom, i - rect2.left);
                    break;
                default:
                    rect = rect2;
                    break;
            }
            flVar.zzbOf.set(rect);
        }
        zzc.rotation = 0;
        return zza(this.zzbNS.zza(bitmap, zzc, flVar));
    }

    public final boolean isOperational() {
        return this.zzbNS.isOperational();
    }

    public final void release() {
        super.release();
        this.zzbNS.zzDP();
    }
}
