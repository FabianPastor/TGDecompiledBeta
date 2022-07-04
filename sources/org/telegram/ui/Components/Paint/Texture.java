package org.telegram.ui.Components.Paint;

import android.graphics.Bitmap;
import android.opengl.GLES20;
import java.nio.Buffer;
import java.nio.ByteBuffer;
import java.nio.IntBuffer;
import org.telegram.ui.Components.Size;

public class Texture {
    private Bitmap bitmap;
    private int texture;

    public Texture(Bitmap bitmap2) {
        this.bitmap = bitmap2;
    }

    public void cleanResources(boolean recycleBitmap) {
        int i = this.texture;
        if (i != 0) {
            GLES20.glDeleteTextures(1, new int[]{i}, 0);
            this.texture = 0;
            if (recycleBitmap) {
                this.bitmap.recycle();
            }
        }
    }

    private boolean isPOT(int x) {
        return ((x + -1) & x) == 0;
    }

    public int texture() {
        int i = this.texture;
        if (i != 0) {
            return i;
        }
        if (this.bitmap.isRecycled()) {
            return 0;
        }
        int[] textures = new int[1];
        GLES20.glGenTextures(1, textures, 0);
        int i2 = textures[0];
        this.texture = i2;
        GLES20.glBindTexture(3553, i2);
        GLES20.glTexParameteri(3553, 10242, 33071);
        GLES20.glTexParameteri(3553, 10243, 33071);
        GLES20.glTexParameteri(3553, 10240, 9729);
        GLES20.glTexParameteri(3553, 10241, 9729);
        int width = this.bitmap.getWidth();
        int height = this.bitmap.getHeight();
        int[] pixels = new int[(width * height)];
        this.bitmap.getPixels(pixels, 0, width, 0, 0, width, height);
        for (int i3 = 0; i3 < pixels.length; i3++) {
            int argb = pixels[i3];
            pixels[i3] = (-16711936 & argb) | ((argb & 255) << 16) | ((argb >> 16) & 255);
        }
        GLES20.glTexImage2D(3553, 0, 6408, width, height, 0, 6408, 5121, IntBuffer.wrap(pixels));
        int px = this.bitmap.getPixel(0, 0);
        ByteBuffer buffer = ByteBuffer.allocateDirect(4);
        buffer.putInt(px).position(0);
        GLES20.glTexSubImage2D(3553, 0, 0, 0, 1, 1, 6408, 5121, buffer);
        Utils.HasGLError();
        return this.texture;
    }

    public static int generateTexture(Size size) {
        int[] textures = new int[1];
        GLES20.glGenTextures(1, textures, 0);
        int texture2 = textures[0];
        GLES20.glBindTexture(3553, texture2);
        GLES20.glTexParameteri(3553, 10242, 33071);
        GLES20.glTexParameteri(3553, 10243, 33071);
        GLES20.glTexParameteri(3553, 10240, 9729);
        GLES20.glTexParameteri(3553, 10241, 9729);
        GLES20.glTexImage2D(3553, 0, 6408, (int) size.width, (int) size.height, 0, 6408, 5121, (Buffer) null);
        return texture2;
    }
}
