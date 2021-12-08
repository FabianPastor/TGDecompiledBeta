package org.webrtc;

import java.nio.ByteBuffer;
import org.webrtc.VideoFrame;

public class JavaI420Buffer implements VideoFrame.I420Buffer {
    private final ByteBuffer dataU;
    private final ByteBuffer dataV;
    private final ByteBuffer dataY;
    private final int height;
    private final RefCountDelegate refCountDelegate;
    private final int strideU;
    private final int strideV;
    private final int strideY;
    private final int width;

    private static native void nativeCropAndScaleI420(ByteBuffer byteBuffer, int i, ByteBuffer byteBuffer2, int i2, ByteBuffer byteBuffer3, int i3, int i4, int i5, int i6, int i7, ByteBuffer byteBuffer4, int i8, ByteBuffer byteBuffer5, int i9, ByteBuffer byteBuffer6, int i10, int i11, int i12);

    private JavaI420Buffer(int width2, int height2, ByteBuffer dataY2, int strideY2, ByteBuffer dataU2, int strideU2, ByteBuffer dataV2, int strideV2, Runnable releaseCallback) {
        this.width = width2;
        this.height = height2;
        this.dataY = dataY2;
        this.dataU = dataU2;
        this.dataV = dataV2;
        this.strideY = strideY2;
        this.strideU = strideU2;
        this.strideV = strideV2;
        this.refCountDelegate = new RefCountDelegate(releaseCallback);
    }

    private static void checkCapacity(ByteBuffer data, int width2, int height2, int stride) {
        int minCapacity = ((height2 - 1) * stride) + width2;
        if (data.capacity() < minCapacity) {
            throw new IllegalArgumentException("Buffer must be at least " + minCapacity + " bytes, but was " + data.capacity());
        }
    }

    public static JavaI420Buffer wrap(int width2, int height2, ByteBuffer dataY2, int strideY2, ByteBuffer dataU2, int strideU2, ByteBuffer dataV2, int strideV2, Runnable releaseCallback) {
        int i = width2;
        int i2 = height2;
        if (dataY2 == null || dataU2 == null || dataV2 == null) {
            throw new IllegalArgumentException("Data buffers cannot be null.");
        } else if (!dataY2.isDirect() || !dataU2.isDirect() || !dataV2.isDirect()) {
            throw new IllegalArgumentException("Data buffers must be direct byte buffers.");
        } else {
            ByteBuffer dataY3 = dataY2.slice();
            ByteBuffer dataU3 = dataU2.slice();
            ByteBuffer dataV3 = dataV2.slice();
            int chromaWidth = (i + 1) / 2;
            int chromaHeight = (i2 + 1) / 2;
            checkCapacity(dataY3, i, i2, strideY2);
            checkCapacity(dataU3, chromaWidth, chromaHeight, strideU2);
            checkCapacity(dataV3, chromaWidth, chromaHeight, strideV2);
            int i3 = chromaHeight;
            return new JavaI420Buffer(width2, height2, dataY3, strideY2, dataU3, strideU2, dataV3, strideV2, releaseCallback);
        }
    }

    public static JavaI420Buffer allocate(int width2, int height2) {
        int chromaHeight = (height2 + 1) / 2;
        int strideUV = (width2 + 1) / 2;
        int uPos = 0 + (width2 * height2);
        int vPos = uPos + (strideUV * chromaHeight);
        ByteBuffer buffer = JniCommon.nativeAllocateByteBuffer((width2 * height2) + (strideUV * 2 * chromaHeight));
        buffer.position(0);
        buffer.limit(uPos);
        ByteBuffer dataY2 = buffer.slice();
        buffer.position(uPos);
        buffer.limit(vPos);
        ByteBuffer dataU2 = buffer.slice();
        buffer.position(vPos);
        buffer.limit((strideUV * chromaHeight) + vPos);
        return new JavaI420Buffer(width2, height2, dataY2, width2, dataU2, strideUV, buffer.slice(), strideUV, new JavaI420Buffer$$ExternalSyntheticLambda0(buffer));
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
        this.refCountDelegate.retain();
    }

    public void release() {
        this.refCountDelegate.release();
    }

    public VideoFrame.Buffer cropAndScale(int cropX, int cropY, int cropWidth, int cropHeight, int scaleWidth, int scaleHeight) {
        return cropAndScaleI420(this, cropX, cropY, cropWidth, cropHeight, scaleWidth, scaleHeight);
    }

    public static VideoFrame.Buffer cropAndScaleI420(VideoFrame.I420Buffer buffer, int cropX, int cropY, int cropWidth, int cropHeight, int scaleWidth, int scaleHeight) {
        if (cropWidth != scaleWidth) {
            int i = cropHeight;
            int i2 = scaleHeight;
        } else if (cropHeight == scaleHeight) {
            ByteBuffer dataY2 = buffer.getDataY();
            ByteBuffer dataU2 = buffer.getDataU();
            ByteBuffer dataV2 = buffer.getDataV();
            dataY2.position(cropX + (buffer.getStrideY() * cropY));
            dataU2.position((cropX / 2) + ((cropY / 2) * buffer.getStrideU()));
            dataV2.position((cropX / 2) + ((cropY / 2) * buffer.getStrideV()));
            buffer.retain();
            ByteBuffer slice = dataY2.slice();
            int strideY2 = buffer.getStrideY();
            ByteBuffer slice2 = dataU2.slice();
            int strideU2 = buffer.getStrideU();
            ByteBuffer slice3 = dataV2.slice();
            int strideV2 = buffer.getStrideV();
            buffer.getClass();
            return wrap(scaleWidth, scaleHeight, slice, strideY2, slice2, strideU2, slice3, strideV2, new JavaI420Buffer$$ExternalSyntheticLambda1(buffer));
        }
        JavaI420Buffer newBuffer = allocate(scaleWidth, scaleHeight);
        nativeCropAndScaleI420(buffer.getDataY(), buffer.getStrideY(), buffer.getDataU(), buffer.getStrideU(), buffer.getDataV(), buffer.getStrideV(), cropX, cropY, cropWidth, cropHeight, newBuffer.getDataY(), newBuffer.getStrideY(), newBuffer.getDataU(), newBuffer.getStrideU(), newBuffer.getDataV(), newBuffer.getStrideV(), scaleWidth, scaleHeight);
        return newBuffer;
    }
}
