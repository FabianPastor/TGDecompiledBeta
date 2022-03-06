package org.webrtc;

import java.nio.ByteBuffer;
import org.webrtc.VideoFrame;

class WrappedNativeI420Buffer implements VideoFrame.I420Buffer {
    private final ByteBuffer dataU;
    private final ByteBuffer dataV;
    private final ByteBuffer dataY;
    private final int height;
    private final long nativeBuffer;
    private final int strideU;
    private final int strideV;
    private final int strideY;
    private final int width;

    public /* synthetic */ int getBufferType() {
        return VideoFrame.I420Buffer.CC.$default$getBufferType(this);
    }

    @CalledByNative
    WrappedNativeI420Buffer(int i, int i2, ByteBuffer byteBuffer, int i3, ByteBuffer byteBuffer2, int i4, ByteBuffer byteBuffer3, int i5, long j) {
        this.width = i;
        this.height = i2;
        this.dataY = byteBuffer;
        this.strideY = i3;
        this.dataU = byteBuffer2;
        this.strideU = i4;
        this.dataV = byteBuffer3;
        this.strideV = i5;
        this.nativeBuffer = j;
        retain();
    }

    public int getWidth() {
        return this.width;
    }

    public int getHeight() {
        return this.height;
    }

    public ByteBuffer getDataY() {
        return this.dataY.slice();
    }

    public ByteBuffer getDataU() {
        return this.dataU.slice();
    }

    public ByteBuffer getDataV() {
        return this.dataV.slice();
    }

    public int getStrideY() {
        return this.strideY;
    }

    public int getStrideU() {
        return this.strideU;
    }

    public int getStrideV() {
        return this.strideV;
    }

    public VideoFrame.I420Buffer toI420() {
        retain();
        return this;
    }

    public void retain() {
        JniCommon.nativeAddRef(this.nativeBuffer);
    }

    public void release() {
        JniCommon.nativeReleaseRef(this.nativeBuffer);
    }

    public VideoFrame.Buffer cropAndScale(int i, int i2, int i3, int i4, int i5, int i6) {
        return JavaI420Buffer.cropAndScaleI420(this, i, i2, i3, i4, i5, i6);
    }
}
