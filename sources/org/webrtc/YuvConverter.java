package org.webrtc;

import android.graphics.Matrix;
import android.opengl.GLES20;
import java.nio.ByteBuffer;
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

        public void onNewShader(GlShader glShader) {
            this.xUnitLoc = glShader.getUniformLocation("xUnit");
            this.coeffsLoc = glShader.getUniformLocation("coeffs");
        }

        public void onPrepareShader(GlShader glShader, float[] fArr, int i, int i2, int i3, int i4) {
            GLES20.glUniform4fv(this.coeffsLoc, 1, this.coeffs, 0);
            int i5 = this.xUnitLoc;
            float f = this.stepSize;
            float f2 = (float) i;
            GLES20.glUniform2f(i5, (fArr[0] * f) / f2, (f * fArr[1]) / f2);
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

    public VideoFrame.I420Buffer convert(VideoFrame.TextureBuffer textureBuffer) {
        this.threadChecker.checkIsOnValidThread();
        VideoFrame.TextureBuffer textureBuffer2 = (VideoFrame.TextureBuffer) this.videoFrameDrawer.prepareBufferForViewportSize(textureBuffer, textureBuffer.getWidth(), textureBuffer.getHeight());
        int width = textureBuffer2.getWidth();
        int height = textureBuffer2.getHeight();
        int i = ((width + 7) / 8) * 8;
        int i2 = (height + 1) / 2;
        int i3 = height + i2;
        ByteBuffer nativeAllocateByteBuffer = JniCommon.nativeAllocateByteBuffer(i * i3);
        int i4 = i / 4;
        Matrix matrix = new Matrix();
        matrix.preTranslate(0.5f, 0.5f);
        matrix.preScale(1.0f, -1.0f);
        matrix.preTranslate(-0.5f, -0.5f);
        this.i420TextureFrameBuffer.setSize(i4, i3);
        GLES20.glBindFramebuffer(36160, this.i420TextureFrameBuffer.getFrameBufferId());
        GlUtil.checkNoGLES2Error("glBindFramebuffer");
        this.shaderCallbacks.setPlaneY();
        VideoFrame.TextureBuffer textureBuffer3 = textureBuffer2;
        int i5 = width;
        int i6 = height;
        VideoFrameDrawer.drawTexture(this.drawer, textureBuffer3, matrix, i5, i6, 0, 0, i4, height);
        this.shaderCallbacks.setPlaneU();
        int i7 = i4 / 2;
        Matrix matrix2 = matrix;
        int i8 = height;
        int i9 = i7;
        int i10 = i2;
        VideoFrameDrawer.drawTexture(this.drawer, textureBuffer3, matrix2, i5, i6, 0, i8, i9, i10);
        this.shaderCallbacks.setPlaneV();
        VideoFrameDrawer.drawTexture(this.drawer, textureBuffer3, matrix2, i5, i6, i7, i8, i9, i10);
        GLES20.glReadPixels(0, 0, this.i420TextureFrameBuffer.getWidth(), this.i420TextureFrameBuffer.getHeight(), 6408, 5121, nativeAllocateByteBuffer);
        GlUtil.checkNoGLES2Error("YuvConverter.convert");
        GLES20.glBindFramebuffer(36160, 0);
        int i11 = (i * height) + 0;
        int i12 = i / 2;
        int i13 = i11 + i12;
        nativeAllocateByteBuffer.position(0);
        nativeAllocateByteBuffer.limit(i11);
        ByteBuffer slice = nativeAllocateByteBuffer.slice();
        nativeAllocateByteBuffer.position(i11);
        int i14 = ((i2 - 1) * i) + i12;
        nativeAllocateByteBuffer.limit(i11 + i14);
        ByteBuffer slice2 = nativeAllocateByteBuffer.slice();
        nativeAllocateByteBuffer.position(i13);
        nativeAllocateByteBuffer.limit(i13 + i14);
        ByteBuffer slice3 = nativeAllocateByteBuffer.slice();
        textureBuffer2.release();
        return JavaI420Buffer.wrap(width, height, slice, i, slice2, i, slice3, i, new Runnable(nativeAllocateByteBuffer) {
            public final /* synthetic */ ByteBuffer f$0;

            {
                this.f$0 = r1;
            }

            public final void run() {
                JniCommon.nativeFreeByteBuffer(this.f$0);
            }
        });
    }

    public void release() {
        this.threadChecker.checkIsOnValidThread();
        this.drawer.release();
        this.i420TextureFrameBuffer.release();
        this.videoFrameDrawer.release();
        this.threadChecker.detachThread();
    }
}
