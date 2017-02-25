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
    private Metadata zzbOx;
    private ByteBuffer zzbOy;

    public static class Builder {
        private Frame zzbOz = new Frame();

        public Frame build() {
            if (this.zzbOz.zzbOy != null || this.zzbOz.mBitmap != null) {
                return this.zzbOz;
            }
            throw new IllegalStateException("Missing image data.  Call either setBitmap or setImageData to specify the image");
        }

        public Builder setBitmap(Bitmap bitmap) {
            int width = bitmap.getWidth();
            int height = bitmap.getHeight();
            this.zzbOz.mBitmap = bitmap;
            Metadata metadata = this.zzbOz.getMetadata();
            metadata.zzrC = width;
            metadata.zzrD = height;
            return this;
        }

        public Builder setId(int i) {
            this.zzbOz.getMetadata().mId = i;
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
                        this.zzbOz.zzbOy = byteBuffer;
                        Metadata metadata = this.zzbOz.getMetadata();
                        metadata.zzrC = i;
                        metadata.zzrD = i2;
                        metadata.format = i3;
                        return this;
                    default:
                        throw new IllegalArgumentException("Unsupported image format: " + i3);
                }
            }
        }

        public Builder setRotation(int i) {
            this.zzbOz.getMetadata().zzMA = i;
            return this;
        }

        public Builder setTimestampMillis(long j) {
            this.zzbOz.getMetadata().zzbdJ = j;
            return this;
        }
    }

    public static class Metadata {
        private int format = -1;
        private int mId;
        private int zzMA;
        private long zzbdJ;
        private int zzrC;
        private int zzrD;

        public Metadata(Metadata metadata) {
            this.zzrC = metadata.getWidth();
            this.zzrD = metadata.getHeight();
            this.mId = metadata.getId();
            this.zzbdJ = metadata.getTimestampMillis();
            this.zzMA = metadata.getRotation();
        }

        public int getFormat() {
            return this.format;
        }

        public int getHeight() {
            return this.zzrD;
        }

        public int getId() {
            return this.mId;
        }

        public int getRotation() {
            return this.zzMA;
        }

        public long getTimestampMillis() {
            return this.zzbdJ;
        }

        public int getWidth() {
            return this.zzrC;
        }

        public void zzTN() {
            if (this.zzMA % 2 != 0) {
                int i = this.zzrC;
                this.zzrC = this.zzrD;
                this.zzrD = i;
            }
            this.zzMA = 0;
        }
    }

    private Frame() {
        this.zzbOx = new Metadata();
        this.zzbOy = null;
        this.mBitmap = null;
    }

    private ByteBuffer zzTM() {
        int i = 0;
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

    public Bitmap getBitmap() {
        return this.mBitmap;
    }

    public ByteBuffer getGrayscaleImageData() {
        return this.mBitmap != null ? zzTM() : this.zzbOy;
    }

    public Metadata getMetadata() {
        return this.zzbOx;
    }
}
