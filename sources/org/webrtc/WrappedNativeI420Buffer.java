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

    WrappedNativeI420Buffer(int width2, int height2, ByteBuffer dataY2, int strideY2, ByteBuffer dataU2, int strideU2, ByteBuffer dataV2, int strideV2, long nativeBuffer2) {
        this.width = width2;
        this.height = height2;
        this.dataY = dataY2;
        this.strideY = strideY2;
        this.dataU = dataU2;
        this.strideU = strideU2;
        this.dataV = dataV2;
        this.strideV = strideV2;
        this.nativeBuffer = nativeBuffer2;
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

    public VideoFrame.Buffer cropAndScale(int cropX, int cropY, int cropWidth, int cropHeight, int scaleWidth, int scaleHeight) {
        return JavaI420Buffer.cropAndScaleI420(this, cropX, cropY, cropWidth, cropHeight, scaleWidth, scaleHeight);
    }
}
