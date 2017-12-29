package com.google.android.gms.vision;

import android.graphics.Bitmap;
import android.graphics.Color;
import java.nio.ByteBuffer;

public class Frame {
    private Bitmap mBitmap;
    private Metadata zzkwd;
    private ByteBuffer zzkwe;

    public static class Builder {
        private Frame zzkwf = new Frame();

        public Frame build() {
            if (this.zzkwf.zzkwe != null || this.zzkwf.mBitmap != null) {
                return this.zzkwf;
            }
            throw new IllegalStateException("Missing image data.  Call either setBitmap or setImageData to specify the image");
        }

        public Builder setBitmap(Bitmap bitmap) {
            int width = bitmap.getWidth();
            int height = bitmap.getHeight();
            this.zzkwf.mBitmap = bitmap;
            Metadata metadata = this.zzkwf.getMetadata();
            metadata.zzalv = width;
            metadata.zzalw = height;
            return this;
        }

        public Builder setRotation(int i) {
            this.zzkwf.getMetadata().zzchn = i;
            return this;
        }
    }

    public static class Metadata {
        private int format = -1;
        private int mId;
        private int zzalv;
        private int zzalw;
        private int zzchn;
        private long zzhwo;

        public int getHeight() {
            return this.zzalw;
        }

        public int getId() {
            return this.mId;
        }

        public int getRotation() {
            return this.zzchn;
        }

        public long getTimestampMillis() {
            return this.zzhwo;
        }

        public int getWidth() {
            return this.zzalv;
        }
    }

    private Frame() {
        this.zzkwd = new Metadata();
        this.zzkwe = null;
        this.mBitmap = null;
    }

    public ByteBuffer getGrayscaleImageData() {
        int i = 0;
        if (this.mBitmap == null) {
            return this.zzkwe;
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
        return this.zzkwd;
    }
}
