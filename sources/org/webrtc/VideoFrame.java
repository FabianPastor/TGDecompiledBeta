package org.webrtc;

import android.graphics.Matrix;
import java.nio.ByteBuffer;

public class VideoFrame implements RefCounted {
    private final Buffer buffer;
    private final int rotation;
    private final long timestampNs;

    public interface Buffer extends RefCounted {
        Buffer cropAndScale(int i, int i2, int i3, int i4, int i5, int i6);

        int getBufferType();

        int getHeight();

        int getWidth();

        void release();

        void retain();

        I420Buffer toI420();

        /* renamed from: org.webrtc.VideoFrame$Buffer$-CC  reason: invalid class name */
        public final /* synthetic */ class CC {
            public static int $default$getBufferType(Buffer _this) {
                return 0;
            }
        }
    }

    public interface I420Buffer extends Buffer {
        int getBufferType();

        ByteBuffer getDataU();

        ByteBuffer getDataV();

        ByteBuffer getDataY();

        int getStrideU();

        int getStrideV();

        int getStrideY();

        /* renamed from: org.webrtc.VideoFrame$I420Buffer$-CC  reason: invalid class name */
        public final /* synthetic */ class CC {
            public static int $default$getBufferType(I420Buffer _this) {
                return 1;
            }
        }
    }

    public interface TextureBuffer extends Buffer {
        int getTextureId();

        Matrix getTransformMatrix();

        Type getType();

        public enum Type {
            OES(36197),
            RGB(3553);
            
            private final int glTarget;

            private Type(int glTarget2) {
                this.glTarget = glTarget2;
            }

            public int getGlTarget() {
                return this.glTarget;
            }
        }
    }

    public VideoFrame(Buffer buffer2, int rotation2, long timestampNs2) {
        if (buffer2 == null) {
            throw new IllegalArgumentException("buffer not allowed to be null");
        } else if (rotation2 % 90 == 0) {
            this.buffer = buffer2;
            this.rotation = rotation2;
            this.timestampNs = timestampNs2;
        } else {
            throw new IllegalArgumentException("rotation must be a multiple of 90");
        }
    }

    public Buffer getBuffer() {
        return this.buffer;
    }

    public int getRotation() {
        return this.rotation;
    }

    public long getTimestampNs() {
        return this.timestampNs;
    }

    public int getRotatedWidth() {
        if (this.rotation % 180 == 0) {
            return this.buffer.getWidth();
        }
        return this.buffer.getHeight();
    }

    public int getRotatedHeight() {
        if (this.rotation % 180 == 0) {
            return this.buffer.getHeight();
        }
        return this.buffer.getWidth();
    }

    public void retain() {
        this.buffer.retain();
    }

    public void release() {
        this.buffer.release();
    }
}
