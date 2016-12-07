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
    private Metadata zzbMy;
    private ByteBuffer zzbMz;

    public static class Builder {
        private Frame zzbMA = new Frame();

        public Frame build() {
            if (this.zzbMA.zzbMz != null || this.zzbMA.mBitmap != null) {
                return this.zzbMA;
            }
            throw new IllegalStateException("Missing image data.  Call either setBitmap or setImageData to specify the image");
        }

        public Builder setBitmap(Bitmap bitmap) {
            int width = bitmap.getWidth();
            int height = bitmap.getHeight();
            this.zzbMA.mBitmap = bitmap;
            Metadata metadata = this.zzbMA.getMetadata();
            metadata.zzrG = width;
            metadata.zzrH = height;
            return this;
        }

        public Builder setId(int i) {
            this.zzbMA.getMetadata().mId = i;
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
                        this.zzbMA.zzbMz = byteBuffer;
                        Metadata metadata = this.zzbMA.getMetadata();
                        metadata.zzrG = i;
                        metadata.zzrH = i2;
                        metadata.format = i3;
                        return this;
                    default:
                        throw new IllegalArgumentException("Unsupported image format: " + i3);
                }
            }
        }

        public Builder setRotation(int i) {
            this.zzbMA.getMetadata().zzLS = i;
            return this;
        }

        public Builder setTimestampMillis(long j) {
            this.zzbMA.getMetadata().zzbde = j;
            return this;
        }
    }

    public static class Metadata {
        private int format = -1;
        private int mId;
        private int zzLS;
        private long zzbde;
        private int zzrG;
        private int zzrH;

        public Metadata(Metadata metadata) {
            this.zzrG = metadata.getWidth();
            this.zzrH = metadata.getHeight();
            this.mId = metadata.getId();
            this.zzbde = metadata.getTimestampMillis();
            this.zzLS = metadata.getRotation();
        }

        public int getFormat() {
            return this.format;
        }

        public int getHeight() {
            return this.zzrH;
        }

        public int getId() {
            return this.mId;
        }

        public int getRotation() {
            return this.zzLS;
        }

        public long getTimestampMillis() {
            return this.zzbde;
        }

        public int getWidth() {
            return this.zzrG;
        }

        public void zzSm() {
            if (this.zzLS % 2 != 0) {
                int i = this.zzrG;
                this.zzrG = this.zzrH;
                this.zzrH = i;
            }
            this.zzLS = 0;
        }
    }

    private Frame() {
        this.zzbMy = new Metadata();
        this.zzbMz = null;
        this.mBitmap = null;
    }

    private ByteBuffer zzSl() {
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
        return this.mBitmap != null ? zzSl() : this.zzbMz;
    }

    public Metadata getMetadata() {
        return this.zzbMy;
    }
}
