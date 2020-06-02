package org.telegram.messenger.video;

import android.graphics.Bitmap;
import android.graphics.SurfaceTexture;
import android.opengl.GLES20;
import android.opengl.GLUtils;
import android.opengl.Matrix;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;
import java.util.ArrayList;
import org.telegram.messenger.MediaController;
import org.telegram.messenger.VideoEditedInfo;
import org.telegram.ui.Components.FilterShaders;
import org.telegram.ui.Components.Paint.Views.EditTextOutline;
import org.telegram.ui.Components.RLottieDrawable;

public class TextureRenderer {
    private static final String FRAGMENT_SHADER = "#extension GL_OES_EGL_image_external : require\nprecision highp float;\nvarying vec2 vTextureCoord;\nuniform samplerExternalOES sTexture;\nvoid main() {\n  gl_FragColor = texture2D(sTexture, vTextureCoord);\n}\n";
    private static final String VERTEX_SHADER = "uniform mat4 uMVPMatrix;\nuniform mat4 uSTMatrix;\nattribute vec4 aPosition;\nattribute vec4 aTextureCoord;\nvarying vec2 vTextureCoord;\nvoid main() {\n  gl_Position = uMVPMatrix * aPosition;\n  vTextureCoord = (uSTMatrix * aTextureCoord).xy;\n}\n";
    float[] bitmapData = {-1.0f, 1.0f, 1.0f, 1.0f, -1.0f, -1.0f, 1.0f, -1.0f};
    private FloatBuffer bitmapVerticesBuffer;
    private boolean blendEnabled;
    private FilterShaders filterShaders;
    private int imageOrientation;
    private String imagePath;
    private boolean isPhoto;
    private float[] mMVPMatrix = new float[16];
    private int mProgram;
    private float[] mSTMatrix = new float[16];
    private int mTextureID = -12345;
    private int maPositionHandle;
    private int maTextureHandle;
    private ArrayList<VideoEditedInfo.MediaEntity> mediaEntities;
    private int muMVPMatrixHandle;
    private int muSTMatrixHandle;
    private String paintPath;
    private int[] paintTexture;
    private int rotationAngle;
    private int simpleInputTexCoordHandle;
    private int simplePositionHandle;
    private int simpleShaderProgram;
    private int simpleSourceImageHandle;
    private Bitmap stickerBitmap;
    private int[] stickerTexture;
    private FloatBuffer textureBuffer;
    private FloatBuffer verticesBuffer;
    private float videoFps;
    private int videoHeight;
    private int videoWidth;

    public TextureRenderer(int i, MediaController.SavedFilterState savedFilterState, String str, String str2, ArrayList<VideoEditedInfo.MediaEntity> arrayList, int i2, int i3, float f, boolean z) {
        this.rotationAngle = i;
        this.isPhoto = z;
        float[] fArr = {-1.0f, -1.0f, 1.0f, -1.0f, -1.0f, 1.0f, 1.0f, 1.0f};
        FloatBuffer asFloatBuffer = ByteBuffer.allocateDirect(32).order(ByteOrder.nativeOrder()).asFloatBuffer();
        this.verticesBuffer = asFloatBuffer;
        asFloatBuffer.put(fArr).position(0);
        FloatBuffer asFloatBuffer2 = ByteBuffer.allocateDirect(32).order(ByteOrder.nativeOrder()).asFloatBuffer();
        this.textureBuffer = asFloatBuffer2;
        asFloatBuffer2.put(new float[]{0.0f, 0.0f, 1.0f, 0.0f, 0.0f, 1.0f, 1.0f, 1.0f}).position(0);
        FloatBuffer asFloatBuffer3 = ByteBuffer.allocateDirect(32).order(ByteOrder.nativeOrder()).asFloatBuffer();
        this.verticesBuffer = asFloatBuffer3;
        asFloatBuffer3.put(fArr).position(0);
        FloatBuffer asFloatBuffer4 = ByteBuffer.allocateDirect(this.bitmapData.length * 4).order(ByteOrder.nativeOrder()).asFloatBuffer();
        this.bitmapVerticesBuffer = asFloatBuffer4;
        asFloatBuffer4.put(this.bitmapData).position(0);
        Matrix.setIdentityM(this.mSTMatrix, 0);
        if (savedFilterState != null) {
            FilterShaders filterShaders2 = new FilterShaders(true);
            this.filterShaders = filterShaders2;
            filterShaders2.setDelegate(FilterShaders.getFilterShadersDelegate(savedFilterState));
        }
        this.videoWidth = i2;
        this.videoHeight = i3;
        this.imagePath = str;
        this.paintPath = str2;
        this.mediaEntities = arrayList;
        this.videoFps = f == 0.0f ? 30.0f : f;
    }

    public int getTextureId() {
        return this.mTextureID;
    }

    public void drawFrame(SurfaceTexture surfaceTexture, boolean z) {
        checkGlError("onDrawFrame start");
        if (this.isPhoto) {
            GLES20.glUseProgram(this.simpleShaderProgram);
            GLES20.glActiveTexture(33984);
            GLES20.glUniform1i(this.simpleSourceImageHandle, 0);
            GLES20.glEnableVertexAttribArray(this.simpleInputTexCoordHandle);
            GLES20.glVertexAttribPointer(this.maTextureHandle, 2, 5126, false, 8, this.textureBuffer);
            GLES20.glEnableVertexAttribArray(this.simplePositionHandle);
        } else {
            surfaceTexture.getTransformMatrix(this.mSTMatrix);
            if (this.blendEnabled) {
                GLES20.glDisable(3042);
                this.blendEnabled = false;
            }
            FilterShaders filterShaders2 = this.filterShaders;
            if (filterShaders2 != null) {
                filterShaders2.onVideoFrameUpdate(this.mSTMatrix);
                GLES20.glViewport(0, 0, this.videoWidth, this.videoHeight);
                this.filterShaders.drawEnhancePass();
                this.filterShaders.drawSharpenPass();
                this.filterShaders.drawCustomParamsPass();
                boolean drawBlurPass = this.filterShaders.drawBlurPass();
                GLES20.glBindFramebuffer(36160, 0);
                GLES20.glUseProgram(this.simpleShaderProgram);
                GLES20.glActiveTexture(33984);
                GLES20.glBindTexture(3553, this.filterShaders.getRenderTexture(drawBlurPass ^ true ? 1 : 0));
                GLES20.glUniform1i(this.simpleSourceImageHandle, 0);
                GLES20.glEnableVertexAttribArray(this.simpleInputTexCoordHandle);
                GLES20.glVertexAttribPointer(this.simpleInputTexCoordHandle, 2, 5126, false, 8, this.filterShaders.getTextureBuffer());
                GLES20.glEnableVertexAttribArray(this.simplePositionHandle);
                GLES20.glVertexAttribPointer(this.simplePositionHandle, 2, 5126, false, 8, this.filterShaders.getVertexBuffer());
                GLES20.glDrawArrays(5, 0, 4);
            } else {
                if (z) {
                    float[] fArr = this.mSTMatrix;
                    fArr[5] = -fArr[5];
                    fArr[13] = 1.0f - fArr[13];
                }
                GLES20.glUseProgram(this.mProgram);
                GLES20.glActiveTexture(33984);
                GLES20.glBindTexture(36197, this.mTextureID);
                GLES20.glVertexAttribPointer(this.maPositionHandle, 2, 5126, false, 8, this.verticesBuffer);
                GLES20.glEnableVertexAttribArray(this.maPositionHandle);
                GLES20.glVertexAttribPointer(this.maTextureHandle, 2, 5126, false, 8, this.textureBuffer);
                GLES20.glEnableVertexAttribArray(this.maTextureHandle);
                GLES20.glUniformMatrix4fv(this.muSTMatrixHandle, 1, false, this.mSTMatrix, 0);
                GLES20.glUniformMatrix4fv(this.muMVPMatrixHandle, 1, false, this.mMVPMatrix, 0);
                GLES20.glDrawArrays(5, 0, 4);
                if (!(this.paintTexture == null && this.stickerTexture == null)) {
                    GLES20.glUseProgram(this.simpleShaderProgram);
                    GLES20.glActiveTexture(33984);
                    GLES20.glUniform1i(this.simpleSourceImageHandle, 0);
                    GLES20.glEnableVertexAttribArray(this.simpleInputTexCoordHandle);
                    GLES20.glVertexAttribPointer(this.maTextureHandle, 2, 5126, false, 8, this.textureBuffer);
                    GLES20.glEnableVertexAttribArray(this.simplePositionHandle);
                }
            }
        }
        if (this.paintTexture != null) {
            int i = 0;
            while (true) {
                int[] iArr = this.paintTexture;
                if (i >= iArr.length) {
                    break;
                }
                drawTexture(true, iArr[i]);
                i++;
            }
        }
        if (this.stickerTexture != null) {
            int size = this.mediaEntities.size();
            for (int i2 = 0; i2 < size; i2++) {
                VideoEditedInfo.MediaEntity mediaEntity = this.mediaEntities.get(i2);
                long j = mediaEntity.ptr;
                if (j != 0) {
                    Bitmap bitmap = this.stickerBitmap;
                    RLottieDrawable.getFrame(j, (int) mediaEntity.currentFrame, bitmap, 512, 512, bitmap.getRowBytes());
                    GLES20.glBindTexture(3553, this.stickerTexture[0]);
                    GLUtils.texImage2D(3553, 0, this.stickerBitmap, 0);
                    float f = mediaEntity.currentFrame + mediaEntity.framesPerDraw;
                    mediaEntity.currentFrame = f;
                    if (f >= ((float) mediaEntity.metadata[0])) {
                        mediaEntity.currentFrame = 0.0f;
                    }
                    drawTexture(false, this.stickerTexture[0], mediaEntity.x, mediaEntity.y, mediaEntity.width, mediaEntity.height, mediaEntity.rotation, (mediaEntity.subType & 2) != 0);
                } else if (mediaEntity.bitmap != null) {
                    GLES20.glBindTexture(3553, this.stickerTexture[0]);
                    GLUtils.texImage2D(3553, 0, mediaEntity.bitmap, 0);
                    drawTexture(false, this.stickerTexture[0], mediaEntity.x, mediaEntity.y, mediaEntity.width, mediaEntity.height, mediaEntity.rotation, (mediaEntity.subType & 2) != 0);
                }
            }
        }
        GLES20.glFinish();
    }

    private void drawTexture(boolean z, int i) {
        drawTexture(z, i, -10000.0f, -10000.0f, -10000.0f, -10000.0f, 0.0f, false);
    }

    private void drawTexture(boolean z, int i, float f, float f2, float f3, float f4, float f5, boolean z2) {
        float f6 = f5;
        if (!this.blendEnabled) {
            GLES20.glEnable(3042);
            GLES20.glBlendFunc(1, 771);
            this.blendEnabled = true;
        }
        if (f <= -10000.0f) {
            float[] fArr = this.bitmapData;
            fArr[0] = -1.0f;
            fArr[1] = 1.0f;
            fArr[2] = 1.0f;
            fArr[3] = 1.0f;
            fArr[4] = -1.0f;
            fArr[5] = -1.0f;
            fArr[6] = 1.0f;
            fArr[7] = -1.0f;
        } else {
            float f7 = (f * 2.0f) - 1.0f;
            float f8 = ((1.0f - f2) * 2.0f) - 1.0f;
            float[] fArr2 = this.bitmapData;
            fArr2[0] = f7;
            fArr2[1] = f8;
            float f9 = (f3 * 2.0f) + f7;
            fArr2[2] = f9;
            fArr2[3] = f8;
            fArr2[4] = f7;
            float var_ = f8 - (f4 * 2.0f);
            fArr2[5] = var_;
            fArr2[6] = f9;
            fArr2[7] = var_;
        }
        float[] fArr3 = this.bitmapData;
        float var_ = (fArr3[0] + fArr3[2]) / 2.0f;
        if (z2) {
            float var_ = fArr3[2];
            fArr3[2] = fArr3[0];
            fArr3[0] = var_;
            float var_ = fArr3[6];
            fArr3[6] = fArr3[4];
            fArr3[4] = var_;
        }
        if (f6 != 0.0f) {
            float var_ = ((float) this.videoWidth) / ((float) this.videoHeight);
            float[] fArr4 = this.bitmapData;
            float var_ = (fArr4[5] + fArr4[1]) / 2.0f;
            int i2 = 0;
            for (int i3 = 4; i2 < i3; i3 = 4) {
                float[] fArr5 = this.bitmapData;
                int i4 = i2 * 2;
                int i5 = i4 + 1;
                double d = (double) (fArr5[i4] - var_);
                double d2 = (double) f6;
                double cos = Math.cos(d2);
                Double.isNaN(d);
                double d3 = (double) ((fArr5[i5] - var_) / var_);
                double sin = Math.sin(d2);
                Double.isNaN(d3);
                fArr5[i4] = ((float) ((cos * d) - (sin * d3))) + var_;
                float[] fArr6 = this.bitmapData;
                double sin2 = Math.sin(d2);
                Double.isNaN(d);
                double cos2 = Math.cos(d2);
                Double.isNaN(d3);
                fArr6[i5] = (((float) ((d * sin2) + (d3 * cos2))) * var_) + var_;
                i2++;
            }
        }
        this.bitmapVerticesBuffer.put(this.bitmapData).position(0);
        GLES20.glVertexAttribPointer(this.maPositionHandle, 2, 5126, false, 8, this.bitmapVerticesBuffer);
        if (z) {
            GLES20.glBindTexture(3553, i);
        }
        GLES20.glDrawArrays(5, 0, 4);
    }

    public void setBreakStrategy(EditTextOutline editTextOutline) {
        editTextOutline.setBreakStrategy(0);
    }

    /* JADX WARNING: Removed duplicated region for block: B:135:0x0230 A[SYNTHETIC] */
    /* JADX WARNING: Removed duplicated region for block: B:68:0x018e A[ADDED_TO_REGION, Catch:{ all -> 0x023f }] */
    @android.annotation.SuppressLint({"WrongConstant"})
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public void surfaceCreated() {
        /*
            r25 = this;
            r1 = r25
            java.lang.String r0 = "uniform mat4 uMVPMatrix;\nuniform mat4 uSTMatrix;\nattribute vec4 aPosition;\nattribute vec4 aTextureCoord;\nvarying vec2 vTextureCoord;\nvoid main() {\n  gl_Position = uMVPMatrix * aPosition;\n  vTextureCoord = (uSTMatrix * aTextureCoord).xy;\n}\n"
            java.lang.String r2 = "#extension GL_OES_EGL_image_external : require\nprecision highp float;\nvarying vec2 vTextureCoord;\nuniform samplerExternalOES sTexture;\nvoid main() {\n  gl_FragColor = texture2D(sTexture, vTextureCoord);\n}\n"
            int r0 = r1.createProgram(r0, r2)
            r1.mProgram = r0
            if (r0 == 0) goto L_0x044c
            java.lang.String r2 = "aPosition"
            int r0 = android.opengl.GLES20.glGetAttribLocation(r0, r2)
            r1.maPositionHandle = r0
            java.lang.String r0 = "glGetAttribLocation aPosition"
            r1.checkGlError(r0)
            int r0 = r1.maPositionHandle
            r2 = -1
            if (r0 == r2) goto L_0x0444
            int r0 = r1.mProgram
            java.lang.String r3 = "aTextureCoord"
            int r0 = android.opengl.GLES20.glGetAttribLocation(r0, r3)
            r1.maTextureHandle = r0
            java.lang.String r0 = "glGetAttribLocation aTextureCoord"
            r1.checkGlError(r0)
            int r0 = r1.maTextureHandle
            if (r0 == r2) goto L_0x043c
            int r0 = r1.mProgram
            java.lang.String r3 = "uMVPMatrix"
            int r0 = android.opengl.GLES20.glGetUniformLocation(r0, r3)
            r1.muMVPMatrixHandle = r0
            java.lang.String r0 = "glGetUniformLocation uMVPMatrix"
            r1.checkGlError(r0)
            int r0 = r1.muMVPMatrixHandle
            if (r0 == r2) goto L_0x0434
            int r0 = r1.mProgram
            java.lang.String r3 = "uSTMatrix"
            int r0 = android.opengl.GLES20.glGetUniformLocation(r0, r3)
            r1.muSTMatrixHandle = r0
            java.lang.String r0 = "glGetUniformLocation uSTMatrix"
            r1.checkGlError(r0)
            int r0 = r1.muSTMatrixHandle
            if (r0 == r2) goto L_0x042c
            r3 = 1
            int[] r0 = new int[r3]
            r4 = 0
            android.opengl.GLES20.glGenTextures(r3, r0, r4)
            r0 = r0[r4]
            r1.mTextureID = r0
            r5 = 36197(0x8d65, float:5.0723E-41)
            android.opengl.GLES20.glBindTexture(r5, r0)
            java.lang.String r0 = "glBindTexture mTextureID"
            r1.checkGlError(r0)
            r6 = 10241(0x2801, float:1.435E-41)
            r7 = 9729(0x2601, float:1.3633E-41)
            android.opengl.GLES20.glTexParameteri(r5, r6, r7)
            r8 = 10240(0x2800, float:1.4349E-41)
            android.opengl.GLES20.glTexParameteri(r5, r8, r7)
            r9 = 10242(0x2802, float:1.4352E-41)
            r10 = 33071(0x812f, float:4.6342E-41)
            android.opengl.GLES20.glTexParameteri(r5, r9, r10)
            r11 = 10243(0x2803, float:1.4354E-41)
            android.opengl.GLES20.glTexParameteri(r5, r11, r10)
            java.lang.String r0 = "glTexParameter"
            r1.checkGlError(r0)
            float[] r0 = r1.mMVPMatrix
            android.opengl.Matrix.setIdentityM(r0, r4)
            int r0 = r1.rotationAngle
            if (r0 == 0) goto L_0x00a2
            float[] r12 = r1.mMVPMatrix
            r13 = 0
            float r14 = (float) r0
            r15 = 0
            r16 = 0
            r17 = 1065353216(0x3var_, float:1.0)
            android.opengl.Matrix.rotateM(r12, r13, r14, r15, r16, r17)
        L_0x00a2:
            org.telegram.ui.Components.FilterShaders r0 = r1.filterShaders
            if (r0 != 0) goto L_0x00b2
            java.lang.String r0 = r1.imagePath
            if (r0 != 0) goto L_0x00b2
            java.lang.String r0 = r1.paintPath
            if (r0 != 0) goto L_0x00b2
            java.util.ArrayList<org.telegram.messenger.VideoEditedInfo$MediaEntity> r0 = r1.mediaEntities
            if (r0 == 0) goto L_0x0119
        L_0x00b2:
            r0 = 35633(0x8b31, float:4.9932E-41)
            java.lang.String r5 = "attribute vec4 position;attribute vec2 inputTexCoord;varying vec2 texCoord;void main() {gl_Position = position;texCoord = inputTexCoord;}"
            int r0 = org.telegram.ui.Components.FilterShaders.loadShader(r0, r5)
            r5 = 35632(0x8b30, float:4.9931E-41)
            java.lang.String r12 = "varying highp vec2 texCoord;uniform sampler2D sourceImage;void main() {gl_FragColor = texture2D(sourceImage, texCoord);}"
            int r5 = org.telegram.ui.Components.FilterShaders.loadShader(r5, r12)
            if (r0 == 0) goto L_0x0119
            if (r5 == 0) goto L_0x0119
            int r12 = android.opengl.GLES20.glCreateProgram()
            r1.simpleShaderProgram = r12
            android.opengl.GLES20.glAttachShader(r12, r0)
            int r0 = r1.simpleShaderProgram
            android.opengl.GLES20.glAttachShader(r0, r5)
            int r0 = r1.simpleShaderProgram
            java.lang.String r5 = "position"
            android.opengl.GLES20.glBindAttribLocation(r0, r4, r5)
            int r0 = r1.simpleShaderProgram
            java.lang.String r12 = "inputTexCoord"
            android.opengl.GLES20.glBindAttribLocation(r0, r3, r12)
            int r0 = r1.simpleShaderProgram
            android.opengl.GLES20.glLinkProgram(r0)
            int[] r0 = new int[r3]
            int r13 = r1.simpleShaderProgram
            r14 = 35714(0x8b82, float:5.0046E-41)
            android.opengl.GLES20.glGetProgramiv(r13, r14, r0, r4)
            r0 = r0[r4]
            if (r0 != 0) goto L_0x00ff
            int r0 = r1.simpleShaderProgram
            android.opengl.GLES20.glDeleteProgram(r0)
            r1.simpleShaderProgram = r4
            goto L_0x0119
        L_0x00ff:
            int r0 = r1.simpleShaderProgram
            int r0 = android.opengl.GLES20.glGetAttribLocation(r0, r5)
            r1.simplePositionHandle = r0
            int r0 = r1.simpleShaderProgram
            int r0 = android.opengl.GLES20.glGetAttribLocation(r0, r12)
            r1.simpleInputTexCoordHandle = r0
            int r0 = r1.simpleShaderProgram
            java.lang.String r5 = "sourceImage"
            int r0 = android.opengl.GLES20.glGetUniformLocation(r0, r5)
            r1.simpleSourceImageHandle = r0
        L_0x0119:
            org.telegram.ui.Components.FilterShaders r0 = r1.filterShaders
            if (r0 == 0) goto L_0x0131
            r0.create()
            org.telegram.ui.Components.FilterShaders r12 = r1.filterShaders
            r13 = 0
            r14 = 0
            int r15 = r1.mTextureID
            int r0 = r1.videoWidth
            int r5 = r1.videoHeight
            r16 = r0
            r17 = r5
            r12.setRenderData(r13, r14, r15, r16, r17)
        L_0x0131:
            java.lang.String r0 = r1.imagePath
            r5 = 3
            r12 = 1065353216(0x3var_, float:1.0)
            r13 = 3553(0xde1, float:4.979E-42)
            if (r0 != 0) goto L_0x013e
            java.lang.String r0 = r1.paintPath
            if (r0 == 0) goto L_0x0243
        L_0x013e:
            java.lang.String r0 = r1.imagePath
            if (r0 == 0) goto L_0x0144
            r0 = 1
            goto L_0x0145
        L_0x0144:
            r0 = 0
        L_0x0145:
            java.lang.String r14 = r1.paintPath
            if (r14 == 0) goto L_0x014b
            r14 = 1
            goto L_0x014c
        L_0x014b:
            r14 = 0
        L_0x014c:
            int r0 = r0 + r14
            int[] r0 = new int[r0]
            r1.paintTexture = r0
            int r14 = r0.length
            android.opengl.GLES20.glGenTextures(r14, r0, r4)
            r0 = 0
        L_0x0156:
            int[] r14 = r1.paintTexture     // Catch:{ all -> 0x023f }
            int r14 = r14.length     // Catch:{ all -> 0x023f }
            if (r0 >= r14) goto L_0x0243
            r14 = 270(0x10e, float:3.78E-43)
            r15 = 90
            if (r0 != 0) goto L_0x0185
            java.lang.String r2 = r1.imagePath     // Catch:{ all -> 0x023f }
            if (r2 == 0) goto L_0x0185
            java.lang.String r2 = r1.imagePath     // Catch:{ all -> 0x023f }
            androidx.exifinterface.media.ExifInterface r4 = new androidx.exifinterface.media.ExifInterface     // Catch:{ all -> 0x0187 }
            r4.<init>((java.lang.String) r2)     // Catch:{ all -> 0x0187 }
            java.lang.String r11 = "Orientation"
            int r4 = r4.getAttributeInt(r11, r3)     // Catch:{ all -> 0x0187 }
            if (r4 == r5) goto L_0x0182
            r11 = 6
            if (r4 == r11) goto L_0x017f
            r11 = 8
            if (r4 == r11) goto L_0x017c
            goto L_0x0187
        L_0x017c:
            r4 = 270(0x10e, float:3.78E-43)
            goto L_0x0188
        L_0x017f:
            r4 = 90
            goto L_0x0188
        L_0x0182:
            r4 = 180(0xb4, float:2.52E-43)
            goto L_0x0188
        L_0x0185:
            java.lang.String r2 = r1.paintPath     // Catch:{ all -> 0x023f }
        L_0x0187:
            r4 = 0
        L_0x0188:
            android.graphics.Bitmap r2 = android.graphics.BitmapFactory.decodeFile(r2)     // Catch:{ all -> 0x023f }
            if (r2 == 0) goto L_0x0230
            if (r0 != 0) goto L_0x0212
            java.lang.String r11 = r1.imagePath     // Catch:{ all -> 0x023f }
            if (r11 == 0) goto L_0x0212
            int r11 = r1.videoWidth     // Catch:{ all -> 0x023f }
            int r5 = r1.videoHeight     // Catch:{ all -> 0x023f }
            android.graphics.Bitmap$Config r3 = android.graphics.Bitmap.Config.ARGB_8888     // Catch:{ all -> 0x023f }
            android.graphics.Bitmap r3 = android.graphics.Bitmap.createBitmap(r11, r5, r3)     // Catch:{ all -> 0x023f }
            r5 = -16777216(0xfffffffffvar_, float:-1.7014118E38)
            r3.eraseColor(r5)     // Catch:{ all -> 0x023f }
            android.graphics.Canvas r5 = new android.graphics.Canvas     // Catch:{ all -> 0x023f }
            r5.<init>(r3)     // Catch:{ all -> 0x023f }
            if (r4 == r15) goto L_0x01c4
            if (r4 != r14) goto L_0x01ad
            goto L_0x01c4
        L_0x01ad:
            int r11 = r2.getWidth()     // Catch:{ all -> 0x023f }
            float r11 = (float) r11     // Catch:{ all -> 0x023f }
            int r14 = r1.videoWidth     // Catch:{ all -> 0x023f }
            float r14 = (float) r14     // Catch:{ all -> 0x023f }
            float r11 = r11 / r14
            int r14 = r2.getHeight()     // Catch:{ all -> 0x023f }
            float r14 = (float) r14     // Catch:{ all -> 0x023f }
            int r15 = r1.videoHeight     // Catch:{ all -> 0x023f }
            float r15 = (float) r15     // Catch:{ all -> 0x023f }
            float r14 = r14 / r15
            float r11 = java.lang.Math.max(r11, r14)     // Catch:{ all -> 0x023f }
            goto L_0x01da
        L_0x01c4:
            int r11 = r2.getHeight()     // Catch:{ all -> 0x023f }
            float r11 = (float) r11     // Catch:{ all -> 0x023f }
            int r14 = r1.videoWidth     // Catch:{ all -> 0x023f }
            float r14 = (float) r14     // Catch:{ all -> 0x023f }
            float r11 = r11 / r14
            int r14 = r2.getWidth()     // Catch:{ all -> 0x023f }
            float r14 = (float) r14     // Catch:{ all -> 0x023f }
            int r15 = r1.videoHeight     // Catch:{ all -> 0x023f }
            float r15 = (float) r15     // Catch:{ all -> 0x023f }
            float r14 = r14 / r15
            float r11 = java.lang.Math.max(r11, r14)     // Catch:{ all -> 0x023f }
        L_0x01da:
            android.graphics.Matrix r14 = new android.graphics.Matrix     // Catch:{ all -> 0x023f }
            r14.<init>()     // Catch:{ all -> 0x023f }
            int r15 = r2.getWidth()     // Catch:{ all -> 0x023f }
            int r15 = -r15
            r9 = 2
            int r15 = r15 / r9
            float r15 = (float) r15     // Catch:{ all -> 0x023f }
            int r10 = r2.getHeight()     // Catch:{ all -> 0x023f }
            int r10 = -r10
            int r10 = r10 / r9
            float r10 = (float) r10     // Catch:{ all -> 0x023f }
            r14.postTranslate(r15, r10)     // Catch:{ all -> 0x023f }
            float r10 = r12 / r11
            r14.postScale(r10, r10)     // Catch:{ all -> 0x023f }
            float r4 = (float) r4     // Catch:{ all -> 0x023f }
            r14.postRotate(r4)     // Catch:{ all -> 0x023f }
            int r4 = r3.getWidth()     // Catch:{ all -> 0x023f }
            int r4 = r4 / r9
            float r4 = (float) r4     // Catch:{ all -> 0x023f }
            int r10 = r3.getHeight()     // Catch:{ all -> 0x023f }
            int r10 = r10 / r9
            float r10 = (float) r10     // Catch:{ all -> 0x023f }
            r14.postTranslate(r4, r10)     // Catch:{ all -> 0x023f }
            android.graphics.Paint r4 = new android.graphics.Paint     // Catch:{ all -> 0x023f }
            r4.<init>(r9)     // Catch:{ all -> 0x023f }
            r5.drawBitmap(r2, r14, r4)     // Catch:{ all -> 0x023f }
            r2 = r3
        L_0x0212:
            int[] r3 = r1.paintTexture     // Catch:{ all -> 0x023f }
            r3 = r3[r0]     // Catch:{ all -> 0x023f }
            android.opengl.GLES20.glBindTexture(r13, r3)     // Catch:{ all -> 0x023f }
            android.opengl.GLES20.glTexParameteri(r13, r6, r7)     // Catch:{ all -> 0x023f }
            android.opengl.GLES20.glTexParameteri(r13, r8, r7)     // Catch:{ all -> 0x023f }
            r3 = 10242(0x2802, float:1.4352E-41)
            r4 = 33071(0x812f, float:4.6342E-41)
            android.opengl.GLES20.glTexParameteri(r13, r3, r4)     // Catch:{ all -> 0x023f }
            r3 = 10243(0x2803, float:1.4354E-41)
            android.opengl.GLES20.glTexParameteri(r13, r3, r4)     // Catch:{ all -> 0x023f }
            r3 = 0
            android.opengl.GLUtils.texImage2D(r13, r3, r2, r3)     // Catch:{ all -> 0x023f }
        L_0x0230:
            int r0 = r0 + 1
            r2 = -1
            r3 = 1
            r4 = 0
            r5 = 3
            r9 = 10242(0x2802, float:1.4352E-41)
            r10 = 33071(0x812f, float:4.6342E-41)
            r11 = 10243(0x2803, float:1.4354E-41)
            goto L_0x0156
        L_0x023f:
            r0 = move-exception
            org.telegram.messenger.FileLog.e((java.lang.Throwable) r0)
        L_0x0243:
            java.util.ArrayList<org.telegram.messenger.VideoEditedInfo$MediaEntity> r0 = r1.mediaEntities
            if (r0 == 0) goto L_0x042b
            android.graphics.Bitmap$Config r0 = android.graphics.Bitmap.Config.ARGB_8888     // Catch:{ all -> 0x0427 }
            r2 = 512(0x200, float:7.175E-43)
            android.graphics.Bitmap r0 = android.graphics.Bitmap.createBitmap(r2, r2, r0)     // Catch:{ all -> 0x0427 }
            r1.stickerBitmap = r0     // Catch:{ all -> 0x0427 }
            r2 = 1
            int[] r0 = new int[r2]     // Catch:{ all -> 0x0427 }
            r1.stickerTexture = r0     // Catch:{ all -> 0x0427 }
            r3 = 0
            android.opengl.GLES20.glGenTextures(r2, r0, r3)     // Catch:{ all -> 0x0427 }
            int[] r0 = r1.stickerTexture     // Catch:{ all -> 0x0427 }
            r0 = r0[r3]     // Catch:{ all -> 0x0427 }
            android.opengl.GLES20.glBindTexture(r13, r0)     // Catch:{ all -> 0x0427 }
            android.opengl.GLES20.glTexParameteri(r13, r6, r7)     // Catch:{ all -> 0x0427 }
            android.opengl.GLES20.glTexParameteri(r13, r8, r7)     // Catch:{ all -> 0x0427 }
            r2 = 10242(0x2802, float:1.4352E-41)
            r3 = 33071(0x812f, float:4.6342E-41)
            android.opengl.GLES20.glTexParameteri(r13, r2, r3)     // Catch:{ all -> 0x0427 }
            r2 = 10243(0x2803, float:1.4354E-41)
            android.opengl.GLES20.glTexParameteri(r13, r2, r3)     // Catch:{ all -> 0x0427 }
            java.util.ArrayList<org.telegram.messenger.VideoEditedInfo$MediaEntity> r0 = r1.mediaEntities     // Catch:{ all -> 0x0427 }
            int r0 = r0.size()     // Catch:{ all -> 0x0427 }
            r3 = 0
        L_0x027b:
            if (r3 >= r0) goto L_0x042b
            java.util.ArrayList<org.telegram.messenger.VideoEditedInfo$MediaEntity> r2 = r1.mediaEntities     // Catch:{ all -> 0x0427 }
            java.lang.Object r2 = r2.get(r3)     // Catch:{ all -> 0x0427 }
            org.telegram.messenger.VideoEditedInfo$MediaEntity r2 = (org.telegram.messenger.VideoEditedInfo.MediaEntity) r2     // Catch:{ all -> 0x0427 }
            byte r4 = r2.type     // Catch:{ all -> 0x0427 }
            r5 = 0
            if (r4 != 0) goto L_0x034d
            byte r4 = r2.subType     // Catch:{ all -> 0x0427 }
            r6 = 1
            r4 = r4 & r6
            if (r4 == 0) goto L_0x02b8
            r4 = 3
            int[] r5 = new int[r4]     // Catch:{ all -> 0x0427 }
            r2.metadata = r5     // Catch:{ all -> 0x0427 }
            java.lang.String r6 = r2.text     // Catch:{ all -> 0x0427 }
            r19 = 512(0x200, float:7.175E-43)
            r20 = 512(0x200, float:7.175E-43)
            r22 = 0
            r23 = 0
            r24 = 0
            r18 = r6
            r21 = r5
            long r5 = org.telegram.ui.Components.RLottieDrawable.create(r18, r19, r20, r21, r22, r23, r24)     // Catch:{ all -> 0x0427 }
            r2.ptr = r5     // Catch:{ all -> 0x0427 }
            int[] r5 = r2.metadata     // Catch:{ all -> 0x0427 }
            r6 = 1
            r5 = r5[r6]     // Catch:{ all -> 0x0427 }
            float r5 = (float) r5     // Catch:{ all -> 0x0427 }
            float r6 = r1.videoFps     // Catch:{ all -> 0x0427 }
            float r5 = r5 / r6
            r2.framesPerDraw = r5     // Catch:{ all -> 0x0427 }
            goto L_0x0349
        L_0x02b8:
            r4 = 3
            int r6 = android.os.Build.VERSION.SDK_INT     // Catch:{ all -> 0x0427 }
            r7 = 19
            if (r6 < r7) goto L_0x02c8
            java.lang.String r5 = r2.text     // Catch:{ all -> 0x0427 }
            android.graphics.Bitmap r5 = android.graphics.BitmapFactory.decodeFile(r5)     // Catch:{ all -> 0x0427 }
            r2.bitmap = r5     // Catch:{ all -> 0x0427 }
            goto L_0x030c
        L_0x02c8:
            java.io.File r6 = new java.io.File     // Catch:{ all -> 0x0427 }
            java.lang.String r7 = r2.text     // Catch:{ all -> 0x0427 }
            r6.<init>(r7)     // Catch:{ all -> 0x0427 }
            java.io.RandomAccessFile r7 = new java.io.RandomAccessFile     // Catch:{ all -> 0x0427 }
            java.lang.String r8 = "r"
            r7.<init>(r6, r8)     // Catch:{ all -> 0x0427 }
            java.nio.channels.FileChannel r18 = r7.getChannel()     // Catch:{ all -> 0x0427 }
            java.nio.channels.FileChannel$MapMode r19 = java.nio.channels.FileChannel.MapMode.READ_ONLY     // Catch:{ all -> 0x0427 }
            r20 = 0
            long r22 = r6.length()     // Catch:{ all -> 0x0427 }
            java.nio.MappedByteBuffer r6 = r18.map(r19, r20, r22)     // Catch:{ all -> 0x0427 }
            android.graphics.BitmapFactory$Options r8 = new android.graphics.BitmapFactory$Options     // Catch:{ all -> 0x0427 }
            r8.<init>()     // Catch:{ all -> 0x0427 }
            r9 = 1
            r8.inJustDecodeBounds = r9     // Catch:{ all -> 0x0427 }
            int r10 = r6.limit()     // Catch:{ all -> 0x0427 }
            org.telegram.messenger.Utilities.loadWebpImage(r5, r6, r10, r8, r9)     // Catch:{ all -> 0x0427 }
            int r9 = r8.outWidth     // Catch:{ all -> 0x0427 }
            int r8 = r8.outHeight     // Catch:{ all -> 0x0427 }
            android.graphics.Bitmap$Config r10 = android.graphics.Bitmap.Config.ARGB_8888     // Catch:{ all -> 0x0427 }
            android.graphics.Bitmap r8 = org.telegram.messenger.Bitmaps.createBitmap(r9, r8, r10)     // Catch:{ all -> 0x0427 }
            r2.bitmap = r8     // Catch:{ all -> 0x0427 }
            int r9 = r6.limit()     // Catch:{ all -> 0x0427 }
            r10 = 1
            org.telegram.messenger.Utilities.loadWebpImage(r8, r6, r9, r5, r10)     // Catch:{ all -> 0x0427 }
            r7.close()     // Catch:{ all -> 0x0427 }
        L_0x030c:
            android.graphics.Bitmap r5 = r2.bitmap     // Catch:{ all -> 0x0427 }
            if (r5 == 0) goto L_0x0349
            android.graphics.Bitmap r5 = r2.bitmap     // Catch:{ all -> 0x0427 }
            int r5 = r5.getWidth()     // Catch:{ all -> 0x0427 }
            float r5 = (float) r5     // Catch:{ all -> 0x0427 }
            android.graphics.Bitmap r6 = r2.bitmap     // Catch:{ all -> 0x0427 }
            int r6 = r6.getHeight()     // Catch:{ all -> 0x0427 }
            float r6 = (float) r6     // Catch:{ all -> 0x0427 }
            float r5 = r5 / r6
            int r6 = (r5 > r12 ? 1 : (r5 == r12 ? 0 : -1))
            if (r6 <= 0) goto L_0x0334
            float r6 = r2.height     // Catch:{ all -> 0x0427 }
            float r6 = r6 / r5
            float r5 = r2.y     // Catch:{ all -> 0x0427 }
            float r7 = r2.height     // Catch:{ all -> 0x0427 }
            float r7 = r7 - r6
            r8 = 1073741824(0x40000000, float:2.0)
            float r7 = r7 / r8
            float r5 = r5 + r7
            r2.y = r5     // Catch:{ all -> 0x0427 }
            r2.height = r6     // Catch:{ all -> 0x0427 }
            goto L_0x0349
        L_0x0334:
            int r6 = (r5 > r12 ? 1 : (r5 == r12 ? 0 : -1))
            if (r6 >= 0) goto L_0x0349
            float r6 = r2.width     // Catch:{ all -> 0x0427 }
            float r6 = r6 * r5
            float r5 = r2.x     // Catch:{ all -> 0x0427 }
            float r7 = r2.width     // Catch:{ all -> 0x0427 }
            float r7 = r7 - r6
            r8 = 1073741824(0x40000000, float:2.0)
            float r7 = r7 / r8
            float r5 = r5 + r7
            r2.x = r5     // Catch:{ all -> 0x0427 }
            r2.width = r6     // Catch:{ all -> 0x0427 }
        L_0x0349:
            r5 = -1
            r7 = 1
            goto L_0x0422
        L_0x034d:
            r4 = 3
            byte r6 = r2.type     // Catch:{ all -> 0x0427 }
            r7 = 1
            if (r6 != r7) goto L_0x0421
            org.telegram.ui.Components.Paint.Views.EditTextOutline r6 = new org.telegram.ui.Components.Paint.Views.EditTextOutline     // Catch:{ all -> 0x0427 }
            android.content.Context r7 = org.telegram.messenger.ApplicationLoader.applicationContext     // Catch:{ all -> 0x0427 }
            r6.<init>(r7)     // Catch:{ all -> 0x0427 }
            r7 = 0
            r6.setBackgroundColor(r7)     // Catch:{ all -> 0x0427 }
            r7 = 1088421888(0x40e00000, float:7.0)
            int r8 = org.telegram.messenger.AndroidUtilities.dp(r7)     // Catch:{ all -> 0x0427 }
            int r9 = org.telegram.messenger.AndroidUtilities.dp(r7)     // Catch:{ all -> 0x0427 }
            int r10 = org.telegram.messenger.AndroidUtilities.dp(r7)     // Catch:{ all -> 0x0427 }
            int r7 = org.telegram.messenger.AndroidUtilities.dp(r7)     // Catch:{ all -> 0x0427 }
            r6.setPadding(r8, r9, r10, r7)     // Catch:{ all -> 0x0427 }
            int r7 = r2.fontSize     // Catch:{ all -> 0x0427 }
            float r7 = (float) r7     // Catch:{ all -> 0x0427 }
            r8 = 0
            r6.setTextSize(r8, r7)     // Catch:{ all -> 0x0427 }
            java.lang.String r7 = r2.text     // Catch:{ all -> 0x0427 }
            r6.setText(r7)     // Catch:{ all -> 0x0427 }
            int r7 = r2.color     // Catch:{ all -> 0x0427 }
            r6.setTextColor(r7)     // Catch:{ all -> 0x0427 }
            r7 = 1
            r6.setTypeface(r5, r7)     // Catch:{ all -> 0x0427 }
            r5 = 17
            r6.setGravity(r5)     // Catch:{ all -> 0x0427 }
            r5 = 0
            r6.setHorizontallyScrolling(r5)     // Catch:{ all -> 0x0427 }
            r5 = 268435456(0x10000000, float:2.5243549E-29)
            r6.setImeOptions(r5)     // Catch:{ all -> 0x0427 }
            r6.setFocusableInTouchMode(r7)     // Catch:{ all -> 0x0427 }
            int r5 = r6.getInputType()     // Catch:{ all -> 0x0427 }
            r5 = r5 | 16384(0x4000, float:2.2959E-41)
            r6.setInputType(r5)     // Catch:{ all -> 0x0427 }
            int r5 = android.os.Build.VERSION.SDK_INT     // Catch:{ all -> 0x0427 }
            r7 = 23
            if (r5 < r7) goto L_0x03ab
            r1.setBreakStrategy(r6)     // Catch:{ all -> 0x0427 }
        L_0x03ab:
            byte r5 = r2.subType     // Catch:{ all -> 0x0427 }
            r7 = 1
            r5 = r5 & r7
            r8 = 0
            if (r5 == 0) goto L_0x03c3
            r5 = -1
            r6.setTextColor(r5)     // Catch:{ all -> 0x0427 }
            int r9 = r2.color     // Catch:{ all -> 0x0427 }
            r6.setStrokeColor(r9)     // Catch:{ all -> 0x0427 }
            r9 = 0
            r6.setFrameColor(r9)     // Catch:{ all -> 0x0427 }
            r6.setShadowLayer(r8, r8, r8, r9)     // Catch:{ all -> 0x0427 }
            goto L_0x03ef
        L_0x03c3:
            r5 = -1
            byte r9 = r2.subType     // Catch:{ all -> 0x0427 }
            r9 = r9 & 4
            if (r9 == 0) goto L_0x03dc
            r9 = -16777216(0xfffffffffvar_, float:-1.7014118E38)
            r6.setTextColor(r9)     // Catch:{ all -> 0x0427 }
            r9 = 0
            r6.setStrokeColor(r9)     // Catch:{ all -> 0x0427 }
            int r10 = r2.color     // Catch:{ all -> 0x0427 }
            r6.setFrameColor(r10)     // Catch:{ all -> 0x0427 }
            r6.setShadowLayer(r8, r8, r8, r9)     // Catch:{ all -> 0x0427 }
            goto L_0x03ef
        L_0x03dc:
            int r9 = r2.color     // Catch:{ all -> 0x0427 }
            r6.setTextColor(r9)     // Catch:{ all -> 0x0427 }
            r9 = 0
            r6.setStrokeColor(r9)     // Catch:{ all -> 0x0427 }
            r6.setFrameColor(r9)     // Catch:{ all -> 0x0427 }
            r9 = 1084227584(0x40a00000, float:5.0)
            r10 = 1711276032(0x66000000, float:1.5111573E23)
            r6.setShadowLayer(r9, r8, r12, r10)     // Catch:{ all -> 0x0427 }
        L_0x03ef:
            int r8 = r2.viewWidth     // Catch:{ all -> 0x0427 }
            r9 = 1073741824(0x40000000, float:2.0)
            int r8 = android.view.View.MeasureSpec.makeMeasureSpec(r8, r9)     // Catch:{ all -> 0x0427 }
            int r9 = r2.viewHeight     // Catch:{ all -> 0x0427 }
            r10 = 1073741824(0x40000000, float:2.0)
            int r9 = android.view.View.MeasureSpec.makeMeasureSpec(r9, r10)     // Catch:{ all -> 0x0427 }
            r6.measure(r8, r9)     // Catch:{ all -> 0x0427 }
            int r8 = r2.viewWidth     // Catch:{ all -> 0x0427 }
            int r9 = r2.viewHeight     // Catch:{ all -> 0x0427 }
            r10 = 0
            r6.layout(r10, r10, r8, r9)     // Catch:{ all -> 0x0427 }
            int r8 = r2.viewWidth     // Catch:{ all -> 0x0427 }
            int r9 = r2.viewHeight     // Catch:{ all -> 0x0427 }
            android.graphics.Bitmap$Config r11 = android.graphics.Bitmap.Config.ARGB_8888     // Catch:{ all -> 0x0427 }
            android.graphics.Bitmap r8 = android.graphics.Bitmap.createBitmap(r8, r9, r11)     // Catch:{ all -> 0x0427 }
            r2.bitmap = r8     // Catch:{ all -> 0x0427 }
            android.graphics.Canvas r8 = new android.graphics.Canvas     // Catch:{ all -> 0x0427 }
            android.graphics.Bitmap r2 = r2.bitmap     // Catch:{ all -> 0x0427 }
            r8.<init>(r2)     // Catch:{ all -> 0x0427 }
            r6.draw(r8)     // Catch:{ all -> 0x0427 }
            goto L_0x0423
        L_0x0421:
            r5 = -1
        L_0x0422:
            r10 = 0
        L_0x0423:
            int r3 = r3 + 1
            goto L_0x027b
        L_0x0427:
            r0 = move-exception
            org.telegram.messenger.FileLog.e((java.lang.Throwable) r0)
        L_0x042b:
            return
        L_0x042c:
            java.lang.RuntimeException r0 = new java.lang.RuntimeException
            java.lang.String r2 = "Could not get attrib location for uSTMatrix"
            r0.<init>(r2)
            throw r0
        L_0x0434:
            java.lang.RuntimeException r0 = new java.lang.RuntimeException
            java.lang.String r2 = "Could not get attrib location for uMVPMatrix"
            r0.<init>(r2)
            throw r0
        L_0x043c:
            java.lang.RuntimeException r0 = new java.lang.RuntimeException
            java.lang.String r2 = "Could not get attrib location for aTextureCoord"
            r0.<init>(r2)
            throw r0
        L_0x0444:
            java.lang.RuntimeException r0 = new java.lang.RuntimeException
            java.lang.String r2 = "Could not get attrib location for aPosition"
            r0.<init>(r2)
            throw r0
        L_0x044c:
            java.lang.RuntimeException r0 = new java.lang.RuntimeException
            java.lang.String r2 = "failed creating program"
            r0.<init>(r2)
            goto L_0x0455
        L_0x0454:
            throw r0
        L_0x0455:
            goto L_0x0454
        */
        throw new UnsupportedOperationException("Method not decompiled: org.telegram.messenger.video.TextureRenderer.surfaceCreated():void");
    }

    private int createProgram(String str, String str2) {
        int loadShader;
        int loadShader2 = FilterShaders.loadShader(35633, str);
        if (loadShader2 == 0 || (loadShader = FilterShaders.loadShader(35632, str2)) == 0) {
            return 0;
        }
        int glCreateProgram = GLES20.glCreateProgram();
        checkGlError("glCreateProgram");
        if (glCreateProgram == 0) {
            return 0;
        }
        GLES20.glAttachShader(glCreateProgram, loadShader2);
        checkGlError("glAttachShader");
        GLES20.glAttachShader(glCreateProgram, loadShader);
        checkGlError("glAttachShader");
        GLES20.glLinkProgram(glCreateProgram);
        int[] iArr = new int[1];
        GLES20.glGetProgramiv(glCreateProgram, 35714, iArr, 0);
        if (iArr[0] == 1) {
            return glCreateProgram;
        }
        GLES20.glDeleteProgram(glCreateProgram);
        return 0;
    }

    public void checkGlError(String str) {
        int glGetError = GLES20.glGetError();
        if (glGetError != 0) {
            throw new RuntimeException(str + ": glError " + glGetError);
        }
    }

    public void release() {
        ArrayList<VideoEditedInfo.MediaEntity> arrayList = this.mediaEntities;
        if (arrayList != null) {
            int size = arrayList.size();
            for (int i = 0; i < size; i++) {
                long j = this.mediaEntities.get(i).ptr;
                if (j != 0) {
                    RLottieDrawable.destroy(j);
                }
            }
        }
    }
}
