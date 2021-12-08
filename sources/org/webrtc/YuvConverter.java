package org.webrtc;

import android.graphics.Matrix;
import android.opengl.GLES20;
import java.nio.ByteBuffer;
import org.telegram.messenger.FileLog;
import org.webrtc.GlGenericDrawer;
import org.webrtc.ThreadUtils;
import org.webrtc.VideoFrame;

public class YuvConverter {
    private static final String FRAGMENT_SHADER = "uniform vec2 xUnit;\nuniform vec4 coeffs;\n\nvoid main() {\n  gl_FragColor.r = coeffs.a + dot(coeffs.rgb,\n      sample(tc - 1.5 * xUnit).rgb);\n  gl_FragColor.g = coeffs.a + dot(coeffs.rgb,\n      sample(tc - 0.5 * xUnit).rgb);\n  gl_FragColor.b = coeffs.a + dot(coeffs.rgb,\n      sample(tc + 0.5 * xUnit).rgb);\n  gl_FragColor.a = coeffs.a + dot(coeffs.rgb,\n      sample(tc + 1.5 * xUnit).rgb);\n}\n";
    private final GlGenericDrawer drawer;
    private final GlTextureFrameBuffer i420TextureFrameBuffer;
    private final ShaderCallbacks shaderCallbacks;
    private final ThreadUtils.ThreadChecker threadChecker;
    private final VideoFrameDrawer videoFrameDrawer;

    private static class ShaderCallbacks implements GlGenericDrawer.ShaderCallbacks {
        private static final float[] uCoeffs = {-0.148223f, -0.290993f, 0.439216f, 0.501961f};
        private static final float[] vCoeffs = {0.439216f, -0.367788f, -0.0714274f, 0.501961f};
        private static final float[] yCoeffs = {0.256788f, 0.504129f, 0.0979059f, 0.0627451f};
        private float[] coeffs;
        private int coeffsLoc;
        private float stepSize;
        private int xUnitLoc;

        private ShaderCallbacks() {
        }

        public void setPlaneY() {
            this.coeffs = yCoeffs;
            this.stepSize = 1.0f;
        }

        public void setPlaneU() {
            this.coeffs = uCoeffs;
            this.stepSize = 2.0f;
        }

        public void setPlaneV() {
            this.coeffs = vCoeffs;
            this.stepSize = 2.0f;
        }

        public void onNewShader(GlShader shader) {
            this.xUnitLoc = shader.getUniformLocation("xUnit");
            this.coeffsLoc = shader.getUniformLocation("coeffs");
        }

        public void onPrepareShader(GlShader shader, float[] texMatrix, int frameWidth, int frameHeight, int viewportWidth, int viewportHeight) {
            GLES20.glUniform4fv(this.coeffsLoc, 1, this.coeffs, 0);
            int i = this.xUnitLoc;
            float f = this.stepSize;
            GLES20.glUniform2f(i, (texMatrix[0] * f) / ((float) frameWidth), (f * texMatrix[1]) / ((float) frameWidth));
        }
    }

    public YuvConverter() {
        this(new VideoFrameDrawer());
    }

    public YuvConverter(VideoFrameDrawer videoFrameDrawer2) {
        ThreadUtils.ThreadChecker threadChecker2 = new ThreadUtils.ThreadChecker();
        this.threadChecker = threadChecker2;
        this.i420TextureFrameBuffer = new GlTextureFrameBuffer(6408);
        ShaderCallbacks shaderCallbacks2 = new ShaderCallbacks();
        this.shaderCallbacks = shaderCallbacks2;
        this.drawer = new GlGenericDrawer("uniform vec2 xUnit;\nuniform vec4 coeffs;\n\nvoid main() {\n  gl_FragColor.r = coeffs.a + dot(coeffs.rgb,\n      sample(tc - 1.5 * xUnit).rgb);\n  gl_FragColor.g = coeffs.a + dot(coeffs.rgb,\n      sample(tc - 0.5 * xUnit).rgb);\n  gl_FragColor.b = coeffs.a + dot(coeffs.rgb,\n      sample(tc + 0.5 * xUnit).rgb);\n  gl_FragColor.a = coeffs.a + dot(coeffs.rgb,\n      sample(tc + 1.5 * xUnit).rgb);\n}\n", shaderCallbacks2);
        this.videoFrameDrawer = videoFrameDrawer2;
        threadChecker2.detachThread();
    }

    public VideoFrame.I420Buffer convert(VideoFrame.TextureBuffer inputTextureBuffer) {
        ByteBuffer i420ByteBuffer;
        int i;
        this.threadChecker.checkIsOnValidThread();
        VideoFrame.TextureBuffer preparedBuffer = (VideoFrame.TextureBuffer) this.videoFrameDrawer.prepareBufferForViewportSize(inputTextureBuffer, inputTextureBuffer.getWidth(), inputTextureBuffer.getHeight());
        int frameWidth = preparedBuffer.getWidth();
        int frameHeight = preparedBuffer.getHeight();
        int stride = ((frameWidth + 7) / 8) * 8;
        int uvHeight = (frameHeight + 1) / 2;
        int totalHeight = frameHeight + uvHeight;
        ByteBuffer i420ByteBuffer2 = JniCommon.nativeAllocateByteBuffer(stride * totalHeight);
        int viewportWidth = stride / 4;
        Matrix renderMatrix = new Matrix();
        renderMatrix.preTranslate(0.5f, 0.5f);
        renderMatrix.preScale(1.0f, -1.0f);
        renderMatrix.preTranslate(-0.5f, -0.5f);
        try {
            this.i420TextureFrameBuffer.setSize(viewportWidth, totalHeight);
            GLES20.glBindFramebuffer(36160, this.i420TextureFrameBuffer.getFrameBufferId());
            GlUtil.checkNoGLES2Error("glBindFramebuffer");
            this.shaderCallbacks.setPlaneY();
            Matrix renderMatrix2 = renderMatrix;
            int viewportWidth2 = viewportWidth;
            i420ByteBuffer = i420ByteBuffer2;
            int i2 = totalHeight;
            try {
                VideoFrameDrawer.drawTexture(this.drawer, preparedBuffer, renderMatrix, frameWidth, frameHeight, frameWidth, frameHeight, 0, 0, viewportWidth2, frameHeight, false);
                this.shaderCallbacks.setPlaneU();
                VideoFrameDrawer.drawTexture(this.drawer, preparedBuffer, renderMatrix2, frameWidth, frameHeight, frameWidth, frameHeight, 0, frameHeight, viewportWidth2 / 2, uvHeight, false);
                this.shaderCallbacks.setPlaneV();
                VideoFrameDrawer.drawTexture(this.drawer, preparedBuffer, renderMatrix2, frameWidth, frameHeight, frameWidth, frameHeight, viewportWidth2 / 2, frameHeight, viewportWidth2 / 2, uvHeight, false);
                GLES20.glReadPixels(0, 0, this.i420TextureFrameBuffer.getWidth(), this.i420TextureFrameBuffer.getHeight(), 6408, 5121, i420ByteBuffer);
                GlUtil.checkNoGLES2Error("YuvConverter.convert");
                i = 0;
                try {
                    GLES20.glBindFramebuffer(36160, 0);
                } catch (Exception e) {
                    e = e;
                }
            } catch (Exception e2) {
                e = e2;
                i = 0;
                FileLog.e((Throwable) e);
                int uPos = (stride * frameHeight) + 0;
                int vPos = uPos + (stride / 2);
                ByteBuffer i420ByteBuffer3 = i420ByteBuffer;
                i420ByteBuffer3.position(i);
                i420ByteBuffer3.limit((stride * frameHeight) + i);
                ByteBuffer dataY = i420ByteBuffer3.slice();
                i420ByteBuffer3.position(uPos);
                int uvSize = ((uvHeight - 1) * stride) + (stride / 2);
                i420ByteBuffer3.limit(uPos + uvSize);
                ByteBuffer dataU = i420ByteBuffer3.slice();
                i420ByteBuffer3.position(vPos);
                i420ByteBuffer3.limit(vPos + uvSize);
                ByteBuffer dataV = i420ByteBuffer3.slice();
                preparedBuffer.release();
                ByteBuffer byteBuffer = i420ByteBuffer3;
                return JavaI420Buffer.wrap(frameWidth, frameHeight, dataY, stride, dataU, stride, dataV, stride, new YuvConverter$$ExternalSyntheticLambda0(i420ByteBuffer3));
            }
        } catch (Exception e3) {
            e = e3;
            Matrix matrix = renderMatrix;
            int i3 = viewportWidth;
            i420ByteBuffer = i420ByteBuffer2;
            int i4 = totalHeight;
            i = 0;
            FileLog.e((Throwable) e);
            int uPos2 = (stride * frameHeight) + 0;
            int vPos2 = uPos2 + (stride / 2);
            ByteBuffer i420ByteBuffer32 = i420ByteBuffer;
            i420ByteBuffer32.position(i);
            i420ByteBuffer32.limit((stride * frameHeight) + i);
            ByteBuffer dataY2 = i420ByteBuffer32.slice();
            i420ByteBuffer32.position(uPos2);
            int uvSize2 = ((uvHeight - 1) * stride) + (stride / 2);
            i420ByteBuffer32.limit(uPos2 + uvSize2);
            ByteBuffer dataU2 = i420ByteBuffer32.slice();
            i420ByteBuffer32.position(vPos2);
            i420ByteBuffer32.limit(vPos2 + uvSize2);
            ByteBuffer dataV2 = i420ByteBuffer32.slice();
            preparedBuffer.release();
            ByteBuffer byteBuffer2 = i420ByteBuffer32;
            return JavaI420Buffer.wrap(frameWidth, frameHeight, dataY2, stride, dataU2, stride, dataV2, stride, new YuvConverter$$ExternalSyntheticLambda0(i420ByteBuffer32));
        }
        int uPos22 = (stride * frameHeight) + 0;
        int vPos22 = uPos22 + (stride / 2);
        ByteBuffer i420ByteBuffer322 = i420ByteBuffer;
        i420ByteBuffer322.position(i);
        i420ByteBuffer322.limit((stride * frameHeight) + i);
        ByteBuffer dataY22 = i420ByteBuffer322.slice();
        i420ByteBuffer322.position(uPos22);
        int uvSize22 = ((uvHeight - 1) * stride) + (stride / 2);
        i420ByteBuffer322.limit(uPos22 + uvSize22);
        ByteBuffer dataU22 = i420ByteBuffer322.slice();
        i420ByteBuffer322.position(vPos22);
        i420ByteBuffer322.limit(vPos22 + uvSize22);
        ByteBuffer dataV22 = i420ByteBuffer322.slice();
        preparedBuffer.release();
        ByteBuffer byteBuffer22 = i420ByteBuffer322;
        return JavaI420Buffer.wrap(frameWidth, frameHeight, dataY22, stride, dataU22, stride, dataV22, stride, new YuvConverter$$ExternalSyntheticLambda0(i420ByteBuffer322));
    }

    public void release() {
        this.threadChecker.checkIsOnValidThread();
        this.drawer.release();
        this.i420TextureFrameBuffer.release();
        this.videoFrameDrawer.release();
        this.threadChecker.detachThread();
    }
}
