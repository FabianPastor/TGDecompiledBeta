package org.webrtc;

import android.graphics.Bitmap;
import android.opengl.GLES20;
import android.opengl.Matrix;
import java.lang.reflect.Array;
import java.nio.Buffer;
import java.nio.ByteBuffer;
import java.nio.FloatBuffer;
import org.telegram.messenger.FileLog;
import org.webrtc.RendererCommon;

public class GlGenericDrawer implements RendererCommon.GlDrawer {
    private static final String DEFAULT_VERTEX_SHADER_STRING = "varying vec2 tc;\nattribute vec4 in_pos;\nattribute vec4 in_tc;\nuniform mat4 tex_mat;\nvoid main() {\n  gl_Position = in_pos;\n  tc = (tex_mat * in_tc).xy;\n}\n";
    private static final FloatBuffer FULL_RECTANGLE_BUFFER = GlUtil.createFloatBuffer(new float[]{-1.0f, -1.0f, 1.0f, -1.0f, -1.0f, 1.0f, 1.0f, 1.0f});
    private static final FloatBuffer FULL_RECTANGLE_TEXTURE_BUFFER = GlUtil.createFloatBuffer(new float[]{0.0f, 0.0f, 1.0f, 0.0f, 0.0f, 1.0f, 1.0f, 1.0f});
    private static final String INPUT_TEXTURE_COORDINATE_NAME = "in_tc";
    private static final String INPUT_VERTEX_COORDINATE_NAME = "in_pos";
    private static final int OES = 0;
    private static final int RGB = 1;
    private static final String TEXTURE_MATRIX_NAME = "tex_mat";
    private static final int YUV = 2;
    private GlShader[][] currentShader;
    private final String genericFragmentSource;
    private int[][] inPosLocation;
    private int[][] inTcLocation;
    private int[] renderFrameBuffer;
    private float[] renderMatrix;
    private int[] renderTexture;
    private float renderTextureDownscale;
    private int[] renderTextureHeight;
    private int[] renderTextureWidth;
    private final ShaderCallbacks shaderCallbacks;
    private int[][] texMatrixLocation;
    private int[][] texelLocation;
    private float[] textureMatrix;
    private final String vertexShader;

    public interface ShaderCallbacks {
        void onNewShader(GlShader glShader);

        void onPrepareShader(GlShader glShader, float[] fArr, int i, int i2, int i3, int i4);
    }

    public interface TextureCallback {
        void run(Bitmap bitmap, int i);
    }

    static String createFragmentShaderString(String genericFragmentSource2, int shaderType, boolean blur) {
        StringBuilder stringBuilder = new StringBuilder();
        if (shaderType == 0) {
            stringBuilder.append("#extension GL_OES_EGL_image_external : require\n");
        }
        stringBuilder.append("precision highp float;\n");
        if (!blur) {
            stringBuilder.append("varying vec2 tc;\n");
        }
        if (shaderType == 2) {
            stringBuilder.append("uniform sampler2D y_tex;\n");
            stringBuilder.append("uniform sampler2D u_tex;\n");
            stringBuilder.append("uniform sampler2D v_tex;\n");
            stringBuilder.append("vec4 sample(vec2 p) {\n");
            stringBuilder.append("  float y = texture2D(y_tex, p).r * 1.16438;\n");
            stringBuilder.append("  float u = texture2D(u_tex, p).r;\n");
            stringBuilder.append("  float v = texture2D(v_tex, p).r;\n");
            stringBuilder.append("  return vec4(y + 1.59603 * v - 0.874202,\n");
            stringBuilder.append("    y - 0.391762 * u - 0.812968 * v + 0.531668,\n");
            stringBuilder.append("    y + 2.01723 * u - 1.08563, 1);\n");
            stringBuilder.append("}\n");
            stringBuilder.append(genericFragmentSource2);
        } else {
            String samplerName = shaderType == 0 ? "samplerExternalOES" : "sampler2D";
            stringBuilder.append("uniform ");
            stringBuilder.append(samplerName);
            stringBuilder.append(" tex;\n");
            if (blur) {
                stringBuilder.append("precision mediump float;\n");
                stringBuilder.append("varying vec2 tc;\n");
                stringBuilder.append("const mediump vec3 satLuminanceWeighting = vec3(0.2126, 0.7152, 0.0722);\n");
                stringBuilder.append("uniform float texelWidthOffset;\n");
                stringBuilder.append("uniform float texelHeightOffset;\n");
                stringBuilder.append("void main(){\n");
                stringBuilder.append("int rad = 3;\n");
                stringBuilder.append("int diameter = 2 * rad + 1;\n");
                stringBuilder.append("vec4 sampleTex = vec4(0, 0, 0, 0);\n");
                stringBuilder.append("vec3 col = vec3(0, 0, 0);\n");
                stringBuilder.append("float weightSum = 0.0;\n");
                stringBuilder.append("for(int i = 0; i < diameter; i++) {\n");
                stringBuilder.append("vec2 offset = vec2(float(i - rad) * texelWidthOffset, float(i - rad) * texelHeightOffset);\n");
                stringBuilder.append("sampleTex = vec4(texture2D(tex, tc.st+offset));\n");
                stringBuilder.append("float index = float(i);\n");
                stringBuilder.append("float boxWeight = float(rad) + 1.0 - abs(index - float(rad));\n");
                stringBuilder.append("col += sampleTex.rgb * boxWeight;\n");
                stringBuilder.append("weightSum += boxWeight;\n");
                stringBuilder.append("}\n");
                stringBuilder.append("vec3 result = col / weightSum;\n");
                stringBuilder.append("lowp float satLuminance = dot(result.rgb, satLuminanceWeighting);\n");
                stringBuilder.append("lowp vec3 greyScaleColor = vec3(satLuminance);\n");
                stringBuilder.append("gl_FragColor = vec4(clamp(mix(greyScaleColor, result.rgb, 1.1), 0.0, 1.0), 1.0);\n");
                stringBuilder.append("}\n");
            } else {
                stringBuilder.append(genericFragmentSource2.replace("sample(", "texture2D(tex, "));
            }
        }
        return stringBuilder.toString();
    }

    public GlGenericDrawer(String genericFragmentSource2, ShaderCallbacks shaderCallbacks2) {
        this("varying vec2 tc;\nattribute vec4 in_pos;\nattribute vec4 in_tc;\nuniform mat4 tex_mat;\nvoid main() {\n  gl_Position = in_pos;\n  tc = (tex_mat * in_tc).xy;\n}\n", genericFragmentSource2, shaderCallbacks2);
    }

    public GlGenericDrawer(String vertexShader2, String genericFragmentSource2, ShaderCallbacks shaderCallbacks2) {
        Class<int> cls = int.class;
        this.currentShader = (GlShader[][]) Array.newInstance(GlShader.class, new int[]{3, 3});
        this.inPosLocation = (int[][]) Array.newInstance(cls, new int[]{3, 3});
        this.inTcLocation = (int[][]) Array.newInstance(cls, new int[]{3, 3});
        this.texMatrixLocation = (int[][]) Array.newInstance(cls, new int[]{3, 3});
        this.texelLocation = (int[][]) Array.newInstance(cls, new int[]{3, 3});
        this.renderTexture = new int[2];
        this.renderTextureWidth = new int[2];
        this.renderTextureHeight = new int[2];
        this.vertexShader = vertexShader2;
        this.genericFragmentSource = genericFragmentSource2;
        this.shaderCallbacks = shaderCallbacks2;
    }

    /* access modifiers changed from: package-private */
    public GlShader createShader(int shaderType, boolean blur) {
        return new GlShader(this.vertexShader, createFragmentShaderString(this.genericFragmentSource, shaderType, blur));
    }

    private void ensureRenderTargetCreated(int originalWidth, int originalHeight, int texIndex) {
        if (this.renderFrameBuffer == null) {
            int[] iArr = new int[2];
            this.renderFrameBuffer = iArr;
            GLES20.glGenFramebuffers(2, iArr, 0);
            GLES20.glGenTextures(2, this.renderTexture, 0);
            int a = 0;
            while (true) {
                int[] iArr2 = this.renderTexture;
                if (a >= iArr2.length) {
                    break;
                }
                GLES20.glBindTexture(3553, iArr2[a]);
                GLES20.glTexParameteri(3553, 10241, 9729);
                GLES20.glTexParameteri(3553, 10240, 9729);
                GLES20.glTexParameteri(3553, 10242, 33071);
                GLES20.glTexParameteri(3553, 10243, 33071);
                a++;
            }
            float[] fArr = new float[16];
            this.renderMatrix = fArr;
            Matrix.setIdentityM(fArr, 0);
        }
        if (this.renderTextureWidth[texIndex] != originalWidth) {
            this.renderTextureDownscale = Math.max(1.0f, ((float) Math.max(originalWidth, originalHeight)) / 50.0f);
            GLES20.glBindTexture(3553, this.renderTexture[texIndex]);
            float f = this.renderTextureDownscale;
            GLES20.glTexImage2D(3553, 0, 6408, (int) (((float) originalWidth) / f), (int) (((float) originalHeight) / f), 0, 6408, 5121, (Buffer) null);
            this.renderTextureWidth[texIndex] = originalWidth;
            this.renderTextureHeight[texIndex] = originalHeight;
        }
    }

    public void getRenderBufferBitmap(int baseRotation, TextureCallback callback) {
        float[] fArr;
        int rotation;
        TextureCallback textureCallback = callback;
        if (this.renderFrameBuffer == null || (fArr = this.textureMatrix) == null) {
            textureCallback.run((Bitmap) null, 0);
            return;
        }
        double Ry = Math.asin((double) fArr[2]);
        if (Ry >= 1.5707963267948966d || Ry <= -1.5707963267948966d) {
            rotation = baseRotation;
        } else {
            float[] fArr2 = this.textureMatrix;
            rotation = (int) ((-Math.atan((double) ((-fArr2[1]) / fArr2[0]))) / 0.017453292519943295d);
        }
        float f = this.renderTextureDownscale;
        int viewportW = (int) (((float) this.renderTextureWidth[0]) / f);
        int viewportH = (int) (((float) this.renderTextureHeight[0]) / f);
        GLES20.glBindFramebuffer(36160, this.renderFrameBuffer[0]);
        GLES20.glFramebufferTexture2D(36160, 36064, 3553, this.renderTexture[0], 0);
        ByteBuffer buffer = ByteBuffer.allocateDirect(viewportW * viewportH * 4);
        GLES20.glReadPixels(0, 0, viewportW, viewportH, 6408, 5121, buffer);
        Bitmap bitmap = Bitmap.createBitmap(viewportW, viewportH, Bitmap.Config.ARGB_8888);
        bitmap.copyPixelsFromBuffer(buffer);
        GLES20.glBindFramebuffer(36160, 0);
        textureCallback.run(bitmap, rotation);
    }

    public void drawOes(int oesTextureId, int originalWidth, int originalHeight, int rotatedWidth, int rotatedHeight, float[] texMatrix, int frameWidth, int frameHeight, int viewportX, int viewportY, int viewportWidth, int viewportHeight, boolean blur) {
        int i = oesTextureId;
        int i2 = originalWidth;
        int i3 = originalHeight;
        int i4 = rotatedWidth;
        if (blur) {
            ensureRenderTargetCreated(i2, i3, 1);
            this.textureMatrix = texMatrix;
            float f = this.renderTextureDownscale;
            int viewportW = (int) (((float) i2) / f);
            int viewportH = (int) (((float) i3) / f);
            GLES20.glViewport(0, 0, viewportW, viewportH);
            int viewportH2 = viewportH;
            int viewportW2 = viewportW;
            prepareShader(0, this.renderMatrix, rotatedWidth, rotatedHeight, frameWidth, frameHeight, viewportWidth, viewportHeight, 0);
            GLES20.glActiveTexture(33984);
            GLES20.glBindTexture(36197, i);
            GLES20.glBindFramebuffer(36160, this.renderFrameBuffer[1]);
            GLES20.glFramebufferTexture2D(36160, 36064, 3553, this.renderTexture[1], 0);
            GLES20.glDrawArrays(5, 0, 4);
            GLES20.glBindTexture(36197, 0);
            GLES20.glBindFramebuffer(36160, 0);
            if (i4 != i2) {
                int viewportW3 = viewportH2;
                viewportH2 = viewportW2;
                viewportW2 = viewportW3;
            }
            ensureRenderTargetCreated(i2, i3, 0);
            prepareShader(1, this.renderMatrix, i4 != i2 ? viewportH2 : viewportW2, i4 != i2 ? viewportW2 : viewportH2, frameWidth, frameHeight, viewportWidth, viewportHeight, 1);
            GLES20.glActiveTexture(33984);
            GLES20.glBindTexture(3553, this.renderTexture[1]);
            GLES20.glBindFramebuffer(36160, this.renderFrameBuffer[0]);
            GLES20.glFramebufferTexture2D(36160, 36064, 3553, this.renderTexture[0], 0);
            GLES20.glDrawArrays(5, 0, 4);
            GLES20.glBindFramebuffer(36160, 0);
            GLES20.glViewport(viewportX, viewportY, viewportWidth, viewportHeight);
            prepareShader(1, texMatrix, i4 != i2 ? viewportH2 : viewportW2, i4 != i2 ? viewportW2 : viewportH2, frameWidth, frameHeight, viewportWidth, viewportHeight, 2);
            GLES20.glActiveTexture(33984);
            GLES20.glBindTexture(3553, this.renderTexture[0]);
            GLES20.glDrawArrays(5, 0, 4);
            return;
        }
        prepareShader(0, texMatrix, rotatedWidth, rotatedHeight, frameWidth, frameHeight, viewportWidth, viewportHeight, 0);
        GLES20.glActiveTexture(33984);
        GLES20.glBindTexture(36197, i);
        GLES20.glViewport(viewportX, viewportY, viewportWidth, viewportHeight);
        GLES20.glDrawArrays(5, 0, 4);
        GLES20.glBindTexture(36197, 0);
    }

    public void drawRgb(int textureId, int originalWidth, int originalHeight, int rotatedWidth, int rotatedHeight, float[] texMatrix, int frameWidth, int frameHeight, int viewportX, int viewportY, int viewportWidth, int viewportHeight, boolean blur) {
        prepareShader(1, texMatrix, rotatedWidth, rotatedHeight, frameWidth, frameHeight, viewportWidth, viewportHeight, 0);
        GLES20.glActiveTexture(33984);
        int i = textureId;
        GLES20.glBindTexture(3553, textureId);
        GLES20.glViewport(viewportX, viewportY, viewportWidth, viewportHeight);
        GLES20.glDrawArrays(5, 0, 4);
        GLES20.glBindTexture(3553, 0);
    }

    public void drawYuv(int[] yuvTextures, int originalWidth, int originalHeight, int rotatedWidth, int rotatedHeight, float[] texMatrix, int frameWidth, int frameHeight, int viewportX, int viewportY, int viewportWidth, int viewportHeight, boolean blur) {
        int i = originalWidth;
        int i2 = originalHeight;
        int i3 = rotatedWidth;
        if (!blur || i <= 0 || i2 <= 0) {
            prepareShader(2, texMatrix, rotatedWidth, rotatedHeight, frameWidth, frameHeight, viewportWidth, viewportHeight, 0);
            for (int i4 = 0; i4 < 3; i4++) {
                GLES20.glActiveTexture(i4 + 33984);
                GLES20.glBindTexture(3553, yuvTextures[i4]);
            }
            GLES20.glViewport(viewportX, viewportY, viewportWidth, viewportHeight);
            GLES20.glDrawArrays(5, 0, 4);
            for (int i5 = 0; i5 < 3; i5++) {
                GLES20.glActiveTexture(i5 + 33984);
                GLES20.glBindTexture(3553, 0);
            }
            return;
        }
        this.textureMatrix = texMatrix;
        ensureRenderTargetCreated(i, i2, 1);
        float f = this.renderTextureDownscale;
        int viewportW = (int) (((float) i) / f);
        int viewportH = (int) (((float) i2) / f);
        GLES20.glViewport(0, 0, viewportW, viewportH);
        int viewportH2 = viewportH;
        int viewportW2 = viewportW;
        prepareShader(2, this.renderMatrix, rotatedWidth, rotatedHeight, frameWidth, frameHeight, viewportWidth, viewportHeight, 0);
        for (int i6 = 0; i6 < 3; i6++) {
            GLES20.glActiveTexture(i6 + 33984);
            GLES20.glBindTexture(3553, yuvTextures[i6]);
        }
        GLES20.glBindFramebuffer(36160, this.renderFrameBuffer[1]);
        GLES20.glFramebufferTexture2D(36160, 36064, 3553, this.renderTexture[1], 0);
        GLES20.glDrawArrays(5, 0, 4);
        for (int i7 = 0; i7 < 3; i7++) {
            GLES20.glActiveTexture(i7 + 33984);
            GLES20.glBindTexture(3553, 0);
        }
        GLES20.glBindFramebuffer(36160, 0);
        if (i3 != i) {
            int viewportW3 = viewportH2;
            viewportH2 = viewportW2;
            viewportW2 = viewportW3;
        }
        ensureRenderTargetCreated(i, i2, 0);
        prepareShader(1, this.renderMatrix, i3 != i ? viewportH2 : viewportW2, i3 != i ? viewportW2 : viewportH2, frameWidth, frameHeight, viewportWidth, viewportHeight, 1);
        GLES20.glActiveTexture(33984);
        GLES20.glBindTexture(3553, this.renderTexture[1]);
        GLES20.glBindFramebuffer(36160, this.renderFrameBuffer[0]);
        GLES20.glFramebufferTexture2D(36160, 36064, 3553, this.renderTexture[0], 0);
        GLES20.glDrawArrays(5, 0, 4);
        GLES20.glBindFramebuffer(36160, 0);
        GLES20.glViewport(viewportX, viewportY, viewportWidth, viewportHeight);
        prepareShader(1, texMatrix, i3 != i ? viewportH2 : viewportW2, i3 != i ? viewportW2 : viewportH2, frameWidth, frameHeight, viewportWidth, viewportHeight, 2);
        GLES20.glActiveTexture(33984);
        GLES20.glBindTexture(3553, this.renderTexture[0]);
        GLES20.glDrawArrays(5, 0, 4);
    }

    private void prepareShader(int shaderType, float[] texMatrix, int texWidth, int texHeight, int frameWidth, int frameHeight, int viewportWidth, int viewportHeight, int blurPass) {
        GlShader shader;
        float f;
        int i = shaderType;
        int i2 = blurPass;
        boolean blur = i2 != 0;
        GlShader[][] glShaderArr = this.currentShader;
        if (glShaderArr[i][i2] != null) {
            shader = glShaderArr[i][i2];
        } else {
            try {
                shader = createShader(i, blur);
                this.currentShader[i][i2] = shader;
                shader.useProgram();
                if (i == 2) {
                    GLES20.glUniform1i(shader.getUniformLocation("y_tex"), 0);
                    GLES20.glUniform1i(shader.getUniformLocation("u_tex"), 1);
                    GLES20.glUniform1i(shader.getUniformLocation("v_tex"), 2);
                } else {
                    GLES20.glUniform1i(shader.getUniformLocation("tex"), 0);
                }
                GlUtil.checkNoGLES2Error("Create shader");
                this.shaderCallbacks.onNewShader(shader);
                if (blur) {
                    this.texelLocation[i][0] = shader.getUniformLocation("texelWidthOffset");
                    this.texelLocation[i][1] = shader.getUniformLocation("texelHeightOffset");
                }
                this.texMatrixLocation[i][i2] = shader.getUniformLocation("tex_mat");
                this.inPosLocation[i][i2] = shader.getAttribLocation("in_pos");
                this.inTcLocation[i][i2] = shader.getAttribLocation("in_tc");
            } catch (Exception e) {
                float[] fArr = texMatrix;
                int i3 = texHeight;
                FileLog.e((Throwable) e);
                return;
            }
        }
        shader.useProgram();
        if (blur) {
            int i4 = this.texelLocation[i][0];
            float f2 = 0.0f;
            if (i2 == 1) {
                f = 1.0f / ((float) texWidth);
            } else {
                int i5 = texWidth;
                f = 0.0f;
            }
            GLES20.glUniform1f(i4, f);
            int i6 = this.texelLocation[i][1];
            if (i2 == 2) {
                f2 = 1.0f / ((float) texHeight);
            } else {
                int i7 = texHeight;
            }
            GLES20.glUniform1f(i6, f2);
        } else {
            int i8 = texWidth;
            int i9 = texHeight;
        }
        GLES20.glEnableVertexAttribArray(this.inPosLocation[i][i2]);
        GLES20.glVertexAttribPointer(this.inPosLocation[i][i2], 2, 5126, false, 0, FULL_RECTANGLE_BUFFER);
        GLES20.glEnableVertexAttribArray(this.inTcLocation[i][i2]);
        GLES20.glVertexAttribPointer(this.inTcLocation[i][i2], 2, 5126, false, 0, FULL_RECTANGLE_TEXTURE_BUFFER);
        GLES20.glUniformMatrix4fv(this.texMatrixLocation[i][i2], 1, false, texMatrix, 0);
        this.shaderCallbacks.onPrepareShader(shader, texMatrix, frameWidth, frameHeight, viewportWidth, viewportHeight);
        GlUtil.checkNoGLES2Error("Prepare shader");
    }

    public void release() {
        for (int a = 0; a < this.currentShader.length; a++) {
            int b = 0;
            while (true) {
                GlShader[][] glShaderArr = this.currentShader;
                if (b >= glShaderArr[a].length) {
                    break;
                }
                if (glShaderArr[a][b] != null) {
                    glShaderArr[a][b].release();
                    this.currentShader[a][b] = null;
                }
                b++;
            }
        }
        int[] iArr = this.renderFrameBuffer;
        if (iArr != null) {
            GLES20.glDeleteFramebuffers(2, iArr, 0);
            GLES20.glDeleteTextures(2, this.renderTexture, 0);
        }
    }
}
