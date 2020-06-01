package org.telegram.ui.Components.Paint;

import android.graphics.Bitmap;
import android.opengl.GLES20;
import android.opengl.GLUtils;
import java.nio.Buffer;
import java.nio.ByteBuffer;
import org.telegram.ui.Components.Size;

public class Texture {
    private Bitmap bitmap;
    private int texture;

    public Texture(Bitmap bitmap2) {
        this.bitmap = bitmap2;
    }

    public void cleanResources(boolean z) {
        int i = this.texture;
        if (i != 0) {
            GLES20.glDeleteTextures(1, new int[]{i}, 0);
            this.texture = 0;
            if (z) {
                this.bitmap.recycle();
            }
        }
    }

    public int texture() {
        int i = this.texture;
        if (i != 0) {
            return i;
        }
        if (this.bitmap.isRecycled()) {
            return 0;
        }
        int[] iArr = new int[1];
        GLES20.glGenTextures(1, iArr, 0);
        int i2 = iArr[0];
        this.texture = i2;
        GLES20.glBindTexture(3553, i2);
        GLES20.glTexParameteri(3553, 10242, 33071);
        GLES20.glTexParameteri(3553, 10243, 33071);
        GLES20.glTexParameteri(3553, 10240, 9729);
        GLES20.glTexParameteri(3553, 10241, 9729);
        GLUtils.texImage2D(3553, 0, this.bitmap, 0);
        int pixel = this.bitmap.getPixel(0, 0);
        ByteBuffer allocateDirect = ByteBuffer.allocateDirect(4);
        allocateDirect.putInt(pixel).position(0);
        GLES20.glTexSubImage2D(3553, 0, 0, 0, 1, 1, 6408, 5121, allocateDirect);
        Utils.HasGLError();
        return this.texture;
    }

    public static int generateTexture(Size size) {
        int[] iArr = new int[1];
        GLES20.glGenTextures(1, iArr, 0);
        int i = iArr[0];
        GLES20.glBindTexture(3553, i);
        GLES20.glTexParameteri(3553, 10242, 33071);
        GLES20.glTexParameteri(3553, 10243, 33071);
        GLES20.glTexParameteri(3553, 10240, 9729);
        GLES20.glTexParameteri(3553, 10241, 9729);
        GLES20.glTexImage2D(3553, 0, 6408, (int) size.width, (int) size.height, 0, 6408, 5121, (Buffer) null);
        return i;
    }
}
