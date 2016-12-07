package com.google.android.gms.vision;

import android.graphics.Bitmap;
import android.graphics.Color;
import java.nio.ByteBuffer;

public class Frame {
    public static final int ROTATION_0 = 0;
    public static final int ROTATION_180 = 2;
    public static final int ROTATION_270 = 3;
    public static final int ROTATION_90 = 1;
    private Metadata aKA;
    private ByteBuffer aKB;
    private Bitmap mBitmap;

    public static class Builder {
        private Frame aKC = new Frame();

        public Frame build() {
            if (this.aKC.aKB != null || this.aKC.mBitmap != null) {
                return this.aKC;
            }
            throw new IllegalStateException("Missing image data.  Call either setBitmap or setImageData to specify the image");
        }

        public Builder setBitmap(Bitmap bitmap) {
            int width = bitmap.getWidth();
            int height = bitmap.getHeight();
            this.aKC.mBitmap = bitmap;
            Metadata metadata = this.aKC.getMetadata();
            metadata.zzajw = width;
            metadata.zzajx = height;
            return this;
        }

        public Builder setId(int i) {
            this.aKC.getMetadata().mId = i;
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
                        this.aKC.aKB = byteBuffer;
                        Metadata metadata = this.aKC.getMetadata();
                        metadata.zzajw = i;
                        metadata.zzajx = i2;
                        metadata.format = i3;
                        return this;
                    default:
                        throw new IllegalArgumentException("Unsupported image format: " + i3);
                }
            }
        }

        public Builder setRotation(int i) {
            this.aKC.getMetadata().zzbvy = i;
            return this;
        }

        public Builder setTimestampMillis(long j) {
            this.aKC.getMetadata().abY = j;
            return this;
        }
    }

    public static class Metadata {
        private long abY;
        private int format = -1;
        private int mId;
        private int zzajw;
        private int zzajx;
        private int zzbvy;

        public Metadata(Metadata metadata) {
            this.zzajw = metadata.getWidth();
            this.zzajx = metadata.getHeight();
            this.mId = metadata.getId();
            this.abY = metadata.getTimestampMillis();
            this.zzbvy = metadata.getRotation();
        }

        public int getFormat() {
            return this.format;
        }

        public int getHeight() {
            return this.zzajx;
        }

        public int getId() {
            return this.mId;
        }

        public int getRotation() {
            return this.zzbvy;
        }

        public long getTimestampMillis() {
            return this.abY;
        }

        public int getWidth() {
            return this.zzajw;
        }

        public void zzclp() {
            if (this.zzbvy % 2 != 0) {
                int i = this.zzajw;
                this.zzajw = this.zzajx;
                this.zzajx = i;
            }
            this.zzbvy = 0;
        }
    }

    private Frame() {
        this.aKA = new Metadata();
        this.aKB = null;
        this.mBitmap = null;
    }

    private ByteBuffer zzclo() {
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
        return this.mBitmap != null ? zzclo() : this.aKB;
    }

    public Metadata getMetadata() {
        return this.aKA;
    }
}
