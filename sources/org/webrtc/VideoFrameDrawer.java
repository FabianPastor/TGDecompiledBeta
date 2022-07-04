package org.webrtc;

import android.graphics.Matrix;
import android.graphics.Point;
import android.opengl.GLES20;
import java.nio.ByteBuffer;
import org.webrtc.GlGenericDrawer;
import org.webrtc.RendererCommon;
import org.webrtc.VideoFrame;

public class VideoFrameDrawer {
    public static final String TAG = "VideoFrameDrawer";
    static final float[] srcPoints = {0.0f, 0.0f, 1.0f, 0.0f, 0.0f, 1.0f};
    private final float[] dstPoints = new float[6];
    private VideoFrame lastI420Frame;
    private int renderHeight;
    private final Matrix renderMatrix = new Matrix();
    private final Matrix renderRotateMatrix = new Matrix();
    private final Point renderSize = new Point();
    private int renderWidth;
    private final YuvUploader yuvUploader = new YuvUploader((AnonymousClass1) null);

    public static void drawTexture(RendererCommon.GlDrawer drawer, VideoFrame.TextureBuffer buffer, Matrix renderMatrix2, int rotatedWidth, int rotatedHeight, int frameWidth, int frameHeight, int viewportX, int viewportY, int viewportWidth, int viewportHeight, boolean blur) {
        Matrix finalMatrix = new Matrix(buffer.getTransformMatrix());
        finalMatrix.preConcat(renderMatrix2);
        float[] finalGlMatrix = RendererCommon.convertMatrixFromAndroidGraphicsMatrix(finalMatrix);
        switch (AnonymousClass1.$SwitchMap$org$webrtc$VideoFrame$TextureBuffer$Type[buffer.getType().ordinal()]) {
            case 1:
                drawer.drawOes(buffer.getTextureId(), buffer.getWidth(), buffer.getHeight(), rotatedWidth, rotatedHeight, finalGlMatrix, frameWidth, frameHeight, viewportX, viewportY, viewportWidth, viewportHeight, blur);
                return;
            case 2:
                drawer.drawRgb(buffer.getTextureId(), buffer.getWidth(), buffer.getHeight(), rotatedWidth, rotatedHeight, finalGlMatrix, frameWidth, frameHeight, viewportX, viewportY, viewportWidth, viewportHeight, blur);
                return;
            default:
                throw new RuntimeException("Unknown texture type.");
        }
    }

    /* renamed from: org.webrtc.VideoFrameDrawer$1  reason: invalid class name */
    static /* synthetic */ class AnonymousClass1 {
        static final /* synthetic */ int[] $SwitchMap$org$webrtc$VideoFrame$TextureBuffer$Type;

        static {
            int[] iArr = new int[VideoFrame.TextureBuffer.Type.values().length];
            $SwitchMap$org$webrtc$VideoFrame$TextureBuffer$Type = iArr;
            try {
                iArr[VideoFrame.TextureBuffer.Type.OES.ordinal()] = 1;
            } catch (NoSuchFieldError e) {
            }
            try {
                $SwitchMap$org$webrtc$VideoFrame$TextureBuffer$Type[VideoFrame.TextureBuffer.Type.RGB.ordinal()] = 2;
            } catch (NoSuchFieldError e2) {
            }
        }
    }

    public void getRenderBufferBitmap(RendererCommon.GlDrawer drawer, int rotation, GlGenericDrawer.TextureCallback callback) {
        if (drawer instanceof GlGenericDrawer) {
            ((GlGenericDrawer) drawer).getRenderBufferBitmap(rotation, callback);
        }
    }

    private static class YuvUploader {
        private ByteBuffer copyBuffer;
        private int[] yuvTextures;

        private YuvUploader() {
        }

        /* synthetic */ YuvUploader(AnonymousClass1 x0) {
            this();
        }

        public int[] uploadYuvData(int width, int height, int[] strides, ByteBuffer[] planes) {
            ByteBuffer packedByteBuffer;
            ByteBuffer byteBuffer;
            int[] planeWidths = {width, width / 2, width / 2};
            int[] planeHeights = {height, height / 2, height / 2};
            int copyCapacityNeeded = 0;
            for (int i = 0; i < 3; i++) {
                if (strides[i] > planeWidths[i]) {
                    copyCapacityNeeded = Math.max(copyCapacityNeeded, planeWidths[i] * planeHeights[i]);
                }
            }
            if (copyCapacityNeeded > 0 && ((byteBuffer = this.copyBuffer) == null || byteBuffer.capacity() < copyCapacityNeeded)) {
                this.copyBuffer = ByteBuffer.allocateDirect(copyCapacityNeeded);
            }
            if (this.yuvTextures == null) {
                this.yuvTextures = new int[3];
                for (int i2 = 0; i2 < 3; i2++) {
                    this.yuvTextures[i2] = GlUtil.generateTexture(3553);
                }
            }
            for (int i3 = 0; i3 < 3; i3++) {
                GLES20.glActiveTexture(33984 + i3);
                GLES20.glBindTexture(3553, this.yuvTextures[i3]);
                if (strides[i3] == planeWidths[i3]) {
                    packedByteBuffer = planes[i3];
                } else {
                    YuvHelper.copyPlane(planes[i3], strides[i3], this.copyBuffer, planeWidths[i3], planeWidths[i3], planeHeights[i3]);
                    packedByteBuffer = this.copyBuffer;
                }
                GLES20.glTexImage2D(3553, 0, 6409, planeWidths[i3], planeHeights[i3], 0, 6409, 5121, packedByteBuffer);
            }
            return this.yuvTextures;
        }

        public int[] uploadFromBuffer(VideoFrame.I420Buffer buffer) {
            return uploadYuvData(buffer.getWidth(), buffer.getHeight(), new int[]{buffer.getStrideY(), buffer.getStrideU(), buffer.getStrideV()}, new ByteBuffer[]{buffer.getDataY(), buffer.getDataU(), buffer.getDataV()});
        }

        public int[] getYuvTextures() {
            return this.yuvTextures;
        }

        public void release() {
            this.copyBuffer = null;
            int[] iArr = this.yuvTextures;
            if (iArr != null) {
                GLES20.glDeleteTextures(3, iArr, 0);
                this.yuvTextures = null;
            }
        }
    }

    private static int distance(float x0, float y0, float x1, float y1) {
        return (int) Math.round(Math.hypot((double) (x1 - x0), (double) (y1 - y0)));
    }

    private void calculateTransformedRenderSize(int frameWidth, int frameHeight, Matrix renderMatrix2) {
        if (renderMatrix2 == null) {
            this.renderWidth = frameWidth;
            this.renderHeight = frameHeight;
            return;
        }
        renderMatrix2.mapPoints(this.dstPoints, srcPoints);
        for (int i = 0; i < 3; i++) {
            float[] fArr = this.dstPoints;
            int i2 = i * 2;
            fArr[i2] = fArr[i2] * ((float) frameWidth);
            int i3 = (i * 2) + 1;
            fArr[i3] = fArr[i3] * ((float) frameHeight);
        }
        float[] fArr2 = this.dstPoints;
        this.renderWidth = distance(fArr2[0], fArr2[1], fArr2[2], fArr2[3]);
        float[] fArr3 = this.dstPoints;
        this.renderHeight = distance(fArr3[0], fArr3[1], fArr3[4], fArr3[5]);
    }

    public void drawFrame(VideoFrame frame, RendererCommon.GlDrawer drawer) {
        drawFrame(frame, drawer, (Matrix) null);
    }

    public void drawFrame(VideoFrame frame, RendererCommon.GlDrawer drawer, Matrix additionalRenderMatrix) {
        drawFrame(frame, drawer, additionalRenderMatrix, 0, 0, frame.getRotatedWidth(), frame.getRotatedHeight(), false, false);
    }

    public void drawFrame(VideoFrame frame, RendererCommon.GlDrawer drawer, Matrix additionalRenderMatrix, int viewportX, int viewportY, int viewportWidth, int viewportHeight, boolean rotate, boolean blur) {
        VideoFrame videoFrame = frame;
        Matrix matrix = additionalRenderMatrix;
        calculateTransformedRenderSize(rotate ? frame.getRotatedHeight() : frame.getRotatedWidth(), rotate ? frame.getRotatedWidth() : frame.getRotatedHeight(), matrix);
        if (this.renderWidth <= 0 || this.renderHeight <= 0) {
            Logging.w("VideoFrameDrawer", "Illegal frame size: " + this.renderWidth + "x" + this.renderHeight);
            return;
        }
        boolean isTextureFrame = frame.getBuffer() instanceof VideoFrame.TextureBuffer;
        this.renderMatrix.reset();
        this.renderMatrix.preTranslate(0.5f, 0.5f);
        if (!isTextureFrame) {
            this.renderMatrix.preScale(1.0f, -1.0f);
        }
        this.renderMatrix.preRotate((float) frame.getRotation());
        this.renderMatrix.preTranslate(-0.5f, -0.5f);
        this.renderRotateMatrix.set(this.renderMatrix);
        if (matrix != null) {
            this.renderMatrix.preConcat(matrix);
        }
        if (isTextureFrame) {
            this.lastI420Frame = null;
            drawTexture(drawer, (VideoFrame.TextureBuffer) frame.getBuffer(), this.renderMatrix, frame.getRotatedWidth(), frame.getRotatedHeight(), this.renderWidth, this.renderHeight, viewportX, viewportY, viewportWidth, viewportHeight, blur);
            return;
        }
        if (videoFrame != this.lastI420Frame) {
            this.lastI420Frame = videoFrame;
            VideoFrame.I420Buffer i420Buffer = frame.getBuffer().toI420();
            this.yuvUploader.uploadFromBuffer(i420Buffer);
            i420Buffer.release();
        }
        drawer.drawYuv(this.yuvUploader.getYuvTextures(), frame.getBuffer().getWidth(), frame.getBuffer().getHeight(), frame.getRotatedWidth(), frame.getRotatedHeight(), RendererCommon.convertMatrixFromAndroidGraphicsMatrix(this.renderMatrix), this.renderWidth, this.renderHeight, viewportX, viewportY, viewportWidth, viewportHeight, blur);
    }

    public VideoFrame.Buffer prepareBufferForViewportSize(VideoFrame.Buffer buffer, int width, int height) {
        buffer.retain();
        return buffer;
    }

    public void release() {
        this.yuvUploader.release();
        this.lastI420Frame = null;
    }
}
