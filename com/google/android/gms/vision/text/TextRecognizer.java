package com.google.android.gms.vision.text;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.graphics.Rect;
import android.graphics.YuvImage;
import android.util.SparseArray;
import com.google.android.gms.vision.Detector;
import com.google.android.gms.vision.Frame;
import com.google.android.gms.vision.internal.client.FrameMetadataParcel;
import com.google.android.gms.vision.text.internal.client.LineBoxParcel;
import com.google.android.gms.vision.text.internal.client.RecognitionOptions;
import com.google.android.gms.vision.text.internal.client.TextRecognizerOptions;
import com.google.android.gms.vision.text.internal.client.zzg;
import java.io.ByteArrayOutputStream;
import java.io.OutputStream;
import java.nio.ByteBuffer;

public final class TextRecognizer extends Detector<TextBlock> {
    private final zzg aON;

    public static class Builder {
        private TextRecognizerOptions aOO = new TextRecognizerOptions();
        private Context mContext;

        public Builder(Context context) {
            this.mContext = context;
        }

        public TextRecognizer build() {
            return new TextRecognizer(new zzg(this.mContext, this.aOO));
        }
    }

    private TextRecognizer() {
        throw new IllegalStateException("Default constructor called");
    }

    private TextRecognizer(zzg com_google_android_gms_vision_text_internal_client_zzg) {
        this.aON = com_google_android_gms_vision_text_internal_client_zzg;
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

    private Rect zza(Rect rect, int i, int i2, FrameMetadataParcel frameMetadataParcel) {
        switch (frameMetadataParcel.rotation) {
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

    private SparseArray<TextBlock> zza(LineBoxParcel[] lineBoxParcelArr) {
        int i = 0;
        SparseArray sparseArray = new SparseArray();
        for (LineBoxParcel lineBoxParcel : lineBoxParcelArr) {
            SparseArray sparseArray2 = (SparseArray) sparseArray.get(lineBoxParcel.aOY);
            if (sparseArray2 == null) {
                sparseArray2 = new SparseArray();
                sparseArray.append(lineBoxParcel.aOY, sparseArray2);
            }
            sparseArray2.append(lineBoxParcel.aOZ, lineBoxParcel);
        }
        SparseArray<TextBlock> sparseArray3 = new SparseArray(sparseArray.size());
        while (i < sparseArray.size()) {
            sparseArray3.append(sparseArray.keyAt(i), new TextBlock((SparseArray) sparseArray.valueAt(i)));
            i++;
        }
        return sparseArray3;
    }

    private int zzabm(int i) {
        switch (i) {
            case 0:
                return 0;
            case 1:
                return 90;
            case 2:
                return 180;
            case 3:
                return 270;
            default:
                throw new IllegalArgumentException("Unsupported rotation degree.");
        }
    }

    private Bitmap zzb(Bitmap bitmap, FrameMetadataParcel frameMetadataParcel) {
        int width = bitmap.getWidth();
        int height = bitmap.getHeight();
        if (frameMetadataParcel.rotation != 0) {
            Matrix matrix = new Matrix();
            matrix.postRotate((float) zzabm(frameMetadataParcel.rotation));
            bitmap = Bitmap.createBitmap(bitmap, 0, 0, width, height, matrix, false);
        }
        if (frameMetadataParcel.rotation == 1 || frameMetadataParcel.rotation == 3) {
            frameMetadataParcel.width = height;
            frameMetadataParcel.height = width;
        }
        return bitmap;
    }

    public SparseArray<TextBlock> detect(Frame frame) {
        return zza(frame, new RecognitionOptions(1, new Rect()));
    }

    public boolean isOperational() {
        return this.aON.isOperational();
    }

    public void release() {
        super.release();
        this.aON.zzclr();
    }

    public SparseArray<TextBlock> zza(Frame frame, RecognitionOptions recognitionOptions) {
        if (frame == null) {
            throw new IllegalArgumentException("No frame supplied.");
        }
        Bitmap bitmap;
        FrameMetadataParcel zzc = FrameMetadataParcel.zzc(frame);
        if (frame.getBitmap() != null) {
            bitmap = frame.getBitmap();
        } else {
            bitmap = zza(frame.getGrayscaleImageData(), frame.getMetadata().getFormat(), zzc.width, zzc.height);
        }
        bitmap = zzb(bitmap, zzc);
        if (!recognitionOptions.aPa.isEmpty()) {
            recognitionOptions.aPa.set(zza(recognitionOptions.aPa, frame.getMetadata().getWidth(), frame.getMetadata().getHeight(), zzc));
        }
        zzc.rotation = 0;
        return zza(this.aON.zza(bitmap, zzc, recognitionOptions));
    }
}
