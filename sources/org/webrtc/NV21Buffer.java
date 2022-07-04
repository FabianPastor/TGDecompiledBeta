package org.webrtc;

import java.nio.ByteBuffer;
import org.webrtc.VideoFrame;

public class NV21Buffer implements VideoFrame.Buffer {
    private final byte[] data;
    private final int height;
    private final RefCountDelegate refCountDelegate;
    private final int width;

    private static native void nativeCropAndScale(int i, int i2, int i3, int i4, int i5, int i6, byte[] bArr, int i7, int i8, ByteBuffer byteBuffer, int i9, ByteBuffer byteBuffer2, int i10, ByteBuffer byteBuffer3, int i11);

    public /* synthetic */ int getBufferType() {
        return VideoFrame.Buffer.CC.$default$getBufferType(this);
    }

    public NV21Buffer(byte[] data2, int width2, int height2, Runnable releaseCallback) {
        this.data = data2;
        this.width = width2;
        this.height = height2;
        this.refCountDelegate = new RefCountDelegate(releaseCallback);
    }

    public int getWidth() {
        return this.width;
    }

    public int getHeight() {
        return this.height;
    }

    public VideoFrame.I420Buffer toI420() {
        int i = this.width;
        int i2 = this.height;
        return (VideoFrame.I420Buffer) cropAndScale(0, 0, i, i2, i, i2);
    }

    public void retain() {
        this.refCountDelegate.retain();
    }

    public void release() {
        this.refCountDelegate.release();
    }

    public VideoFrame.Buffer cropAndScale(int cropX, int cropY, int cropWidth, int cropHeight, int scaleWidth, int scaleHeight) {
        JavaI420Buffer newBuffer = JavaI420Buffer.allocate(scaleWidth, scaleHeight);
        nativeCropAndScale(cropX, cropY, cropWidth, cropHeight, scaleWidth, scaleHeight, this.data, this.width, this.height, newBuffer.getDataY(), newBuffer.getStrideY(), newBuffer.getDataU(), newBuffer.getStrideU(), newBuffer.getDataV(), newBuffer.getStrideV());
        return newBuffer;
    }
}
