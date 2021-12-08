package org.webrtc;

import android.opengl.GLES20;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;

public class GlUtil {
    private GlUtil() {
    }

    public static class GlOutOfMemoryException extends RuntimeException {
        public GlOutOfMemoryException(String msg) {
            super(msg);
        }
    }

    public static void checkNoGLES2Error(String msg) {
    }

    public static FloatBuffer createFloatBuffer(float[] coords) {
        ByteBuffer bb = ByteBuffer.allocateDirect(coords.length * 4);
        bb.order(ByteOrder.nativeOrder());
        FloatBuffer fb = bb.asFloatBuffer();
        fb.put(coords);
        fb.position(0);
        return fb;
    }

    public static int generateTexture(int target) {
        int[] textureArray = new int[1];
        GLES20.glGenTextures(1, textureArray, 0);
        int textureId = textureArray[0];
        GLES20.glBindTexture(target, textureId);
        GLES20.glTexParameterf(target, 10241, 9729.0f);
        GLES20.glTexParameterf(target, 10240, 9729.0f);
        GLES20.glTexParameterf(target, 10242, 33071.0f);
        GLES20.glTexParameterf(target, 10243, 33071.0f);
        checkNoGLES2Error("generateTexture");
        return textureId;
    }
}
