package com.google.android.gms.vision;

import android.graphics.Bitmap;
import android.graphics.Color;
import java.nio.ByteBuffer;

public class Frame {
    public static final int ROTATION_0 = 0;
    public static final int ROTATION_180 = 2;
    public static final int ROTATION_270 = 3;
    public static final int ROTATION_90 = 1;
    private Bitmap mBitmap;
    private Metadata zzbMT;
    private ByteBuffer zzbMU;

    public static class Builder {
        private Frame zzbMV = new Frame();

        public Frame build() {
            if (this.zzbMV.zzbMU != null || this.zzbMV.mBitmap != null) {
                return this.zzbMV;
            }
            throw new IllegalStateException("Missing image data.  Call either setBitmap or setImageData to specify the image");
        }

        public Builder setBitmap(Bitmap bitmap) {
            int width = bitmap.getWidth();
            int height = bitmap.getHeight();
            this.zzbMV.mBitmap = bitmap;
            Metadata metadata = this.zzbMV.getMetadata();
            metadata.zzrY = width;
            metadata.zzrZ = height;
            return this;
        }

        public Builder setId(int i) {
            this.zzbMV.getMetadata().mId = i;
            return this;
        }

        public Builder setImageData(ByteBuffer byteBuffer, int i, int i2, int i3) {
            if (byteBuffer == null) {
                throw new IllegalArgumentException("Null image data supplied.");
            } else if (byteBuffer.capacity() < i * i2) {
                throw new IllegalArgumentException("Invalid image data size.");
            } else {
                switch (i3) {
                    case 16:
                    case 17:
                    case 842094169:
                        this.zzbMV.zzbMU = byteBuffer;
                        Metadata metadata = this.zzbMV.getMetadata();
                        metadata.zzrY = i;
                        metadata.zzrZ = i2;
                        metadata.format = i3;
                        return this;
                    default:
                        throw new IllegalArgumentException("Unsupported image format: " + i3);
                }
            }
        }

        public Builder setRotation(int i) {
            this.zzbMV.getMetadata().zzOa = i;
            return this;
        }

        public Builder setTimestampMillis(long j) {
            this.zzbMV.getMetadata().zzbcV = j;
            return this;
        }
    }

    public static class Metadata {
        private int format = -1;
        private int mId;
        private int zzOa;
        private long zzbcV;
        private int zzrY;
        private int zzrZ;

        public Metadata(Metadata metadata) {
            this.zzrY = metadata.getWidth();
            this.zzrZ = metadata.getHeight();
            this.mId = metadata.getId();
            this.zzbcV = metadata.getTimestampMillis();
            this.zzOa = metadata.getRotation();
        }

        public int getFormat() {
            return this.format;
        }

        public int getHeight() {
            return this.zzrZ;
        }

        public int getId() {
            return this.mId;
        }

        public int getRotation() {
            return this.zzOa;
        }

        public long getTimestampMillis() {
            return this.zzbcV;
        }

        public int getWidth() {
            return this.zzrY;
        }

        public final void zzDM() {
            if (this.zzOa % 2 != 0) {
                int i = this.zzrY;
                this.zzrY = this.zzrZ;
                this.zzrZ = i;
            }
            this.zzOa = 0;
        }
    }

    private Frame() {
        this.zzbMT = new Metadata();
        this.zzbMU = null;
        this.mBitmap = null;
    }

    public Bitmap getBitmap() {
        return this.mBitmap;
    }

    public ByteBuffer getGrayscaleImageData() {
        int i = 0;
        if (this.mBitmap == null) {
            return this.zzbMU;
        }
        int width = this.mBitmap.getWidth();
        int height = this.mBitmap.getHeight();
        int[] iArr = new int[(width * height)];
        this.mBitmap.getPixels(iArr, 0, width, 0, 0, width, height);
        byte[] bArr = new byte[(width * height)];
        while (i < iArr.length) {
            bArr[i] = (byte) ((int) (((((float) Color.red(iArr[i])) * 0.299f) + (((float) Color.green(iArr[i])) * 0.587f)) + (((float) Color.blue(iArr[i])) * 0.114f)));
            i++;
        }
        return ByteBuffer.wrap(bArr);
    }

    public Metadata getMetadata() {
        return this.zzbMT;
    }
}
