package org.webrtc;

import android.opengl.GLES20;
import java.nio.Buffer;

public class GlTextureFrameBuffer {
    private int frameBufferId;
    private int height;
    private final int pixelFormat;
    private int textureId;
    private int width;

    public GlTextureFrameBuffer(int pixelFormat2) {
        switch (pixelFormat2) {
            case 6407:
            case 6408:
            case 6409:
                this.pixelFormat = pixelFormat2;
                this.width = 0;
                this.height = 0;
                return;
            default:
                throw new IllegalArgumentException("Invalid pixel format: " + pixelFormat2);
        }
    }

    public void setSize(int width2, int height2) {
        if (width2 <= 0 || height2 <= 0) {
            throw new IllegalArgumentException("Invalid size: " + width2 + "x" + height2);
        } else if (width2 != this.width || height2 != this.height) {
            this.width = width2;
            this.height = height2;
            if (this.textureId == 0) {
                this.textureId = GlUtil.generateTexture(3553);
            }
            if (this.frameBufferId == 0) {
                int[] frameBuffers = new int[1];
                GLES20.glGenFramebuffers(1, frameBuffers, 0);
                this.frameBufferId = frameBuffers[0];
            }
            GLES20.glActiveTexture(33984);
            GLES20.glBindTexture(3553, this.textureId);
            int i = this.pixelFormat;
            GLES20.glTexImage2D(3553, 0, i, width2, height2, 0, i, 5121, (Buffer) null);
            GLES20.glBindTexture(3553, 0);
            GlUtil.checkNoGLES2Error("GlTextureFrameBuffer setSize");
            GLES20.glBindFramebuffer(36160, this.frameBufferId);
            GLES20.glFramebufferTexture2D(36160, 36064, 3553, this.textureId, 0);
            int status = GLES20.glCheckFramebufferStatus(36160);
            if (status == 36053) {
                GLES20.glBindFramebuffer(36160, 0);
                return;
            }
            throw new IllegalStateException("Framebuffer not complete, status: " + status);
        }
    }

    public int getWidth() {
        return this.width;
    }

    public int getHeight() {
        return this.height;
    }

    public int getFrameBufferId() {
        return this.frameBufferId;
    }

    public int getTextureId() {
        return this.textureId;
    }

    public void release() {
        GLES20.glDeleteTextures(1, new int[]{this.textureId}, 0);
        this.textureId = 0;
        GLES20.glDeleteFramebuffers(1, new int[]{this.frameBufferId}, 0);
        this.frameBufferId = 0;
        this.width = 0;
        this.height = 0;
    }
}
