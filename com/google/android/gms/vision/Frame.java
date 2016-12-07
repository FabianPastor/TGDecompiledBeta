package com.google.android.gms.vision;

import android.graphics.Bitmap;
import android.graphics.Color;
import java.nio.ByteBuffer;

public class Frame {
    public static final int ROTATION_0 = 0;
    public static final int ROTATION_180 = 2;
    public static final int ROTATION_270 = 3;
    public static final int ROTATION_90 = 1;
    private Metadata aNL;
    private ByteBuffer aNM;
    private Bitmap mBitmap;

    public static class Builder {
        private Frame aNN = new Frame();

        public Frame build() {
            if (this.aNN.aNM != null || this.aNN.mBitmap != null) {
                return this.aNN;
            }
            throw new IllegalStateException("Missing image data.  Call either setBitmap or setImageData to specify the image");
        }

        public Builder setBitmap(Bitmap bitmap) {
            int width = bitmap.getWidth();
            int height = bitmap.getHeight();
            this.aNN.mBitmap = bitmap;
            Metadata metadata = this.aNN.getMetadata();
            metadata.zzakh = width;
            metadata.zzaki = height;
            return this;
        }

        public Builder setId(int i) {
            this.aNN.getMetadata().mId = i;
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
                        this.aNN.aNM = byteBuffer;
                        Metadata metadata = this.aNN.getMetadata();
                        metadata.zzakh = i;
                        metadata.zzaki = i2;
                        metadata.format = i3;
                        return this;
                    default:
                        throw new IllegalArgumentException("Unsupported image format: " + i3);
                }
            }
        }

        public Builder setRotation(int i) {
            this.aNN.getMetadata().zzbzf = i;
            return this;
        }

        public Builder setTimestampMillis(long j) {
            this.aNN.getMetadata().aeh = j;
            return this;
        }
    }

    public static class Metadata {
        private long aeh;
        private int format = -1;
        private int mId;
        private int zzakh;
        private int zzaki;
        private int zzbzf;

        public Metadata(Metadata metadata) {
            this.zzakh = metadata.getWidth();
            this.zzaki = metadata.getHeight();
            this.mId = metadata.getId();
            this.aeh = metadata.getTimestampMillis();
            this.zzbzf = metadata.getRotation();
        }

        public int getFormat() {
            return this.format;
        }

        public int getHeight() {
            return this.zzaki;
        }

        public int getId() {
            return this.mId;
        }

        public int getRotation() {
            return this.zzbzf;
        }

        public long getTimestampMillis() {
            return this.aeh;
        }

        public int getWidth() {
            return this.zzakh;
        }

        public void zzclo() {
            if (this.zzbzf % 2 != 0) {
                int i = this.zzakh;
                this.zzakh = this.zzaki;
                this.zzaki = i;
            }
            this.zzbzf = 0;
        }
    }

    private Frame() {
        this.aNL = new Metadata();
        this.aNM = null;
        this.mBitmap = null;
    }

    private ByteBuffer zzcln() {
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
        return this.mBitmap != null ? zzcln() : this.aNM;
    }

    public Metadata getMetadata() {
        return this.aNL;
    }
}
