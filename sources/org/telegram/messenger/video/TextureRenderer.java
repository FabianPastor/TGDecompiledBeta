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
import org.telegram.messenger.BuildVars;
import org.telegram.messenger.FileLog;
import org.telegram.messenger.MediaController;
import org.telegram.messenger.VideoEditedInfo;
import org.telegram.ui.Components.FilterShaders;
import org.telegram.ui.Components.Paint.Views.EditTextOutline;
import org.telegram.ui.Components.RLottieDrawable;

public class TextureRenderer {
    private static final String FRAGMENT_EXTERNAL_SHADER = "#extension GL_OES_EGL_image_external : require\nprecision highp float;\nvarying vec2 vTextureCoord;\nuniform samplerExternalOES sTexture;\nvoid main() {\n  gl_FragColor = texture2D(sTexture, vTextureCoord);\n}\n";
    private static final String FRAGMENT_SHADER = "precision highp float;\nvarying vec2 vTextureCoord;\nuniform sampler2D sTexture;\nvoid main() {\n  gl_FragColor = texture2D(sTexture, vTextureCoord);\n}\n";
    private static final String VERTEX_SHADER = "uniform mat4 uMVPMatrix;\nuniform mat4 uSTMatrix;\nattribute vec4 aPosition;\nattribute vec4 aTextureCoord;\nvarying vec2 vTextureCoord;\nvoid main() {\n  gl_Position = uMVPMatrix * aPosition;\n  vTextureCoord = (uSTMatrix * aTextureCoord).xy;\n}\n";
    float[] bitmapData = {-1.0f, 1.0f, 1.0f, 1.0f, -1.0f, -1.0f, 1.0f, -1.0f};
    private FloatBuffer bitmapVerticesBuffer;
    private boolean blendEnabled;
    private FilterShaders filterShaders;
    private boolean firstFrame = true;
    private int imageOrientation;
    private String imagePath;
    private boolean isPhoto;
    private float[] mMVPMatrix = new float[16];
    private int[] mProgram;
    private float[] mSTMatrix = new float[16];
    private float[] mSTMatrixIdentity = new float[16];
    private int mTextureID;
    private int[] maPositionHandle;
    private int[] maTextureHandle;
    private ArrayList<VideoEditedInfo.MediaEntity> mediaEntities;
    private int[] muMVPMatrixHandle;
    private int[] muSTMatrixHandle;
    private int originalHeight;
    private int originalWidth;
    private String paintPath;
    private int[] paintTexture;
    private FloatBuffer renderTextureBuffer;
    private int simpleInputTexCoordHandle;
    private int simplePositionHandle;
    private int simpleShaderProgram;
    private int simpleSourceImageHandle;
    private Bitmap stickerBitmap;
    private int[] stickerTexture;
    private FloatBuffer textureBuffer;
    private int transformedHeight;
    private int transformedWidth;
    private FloatBuffer verticesBuffer;
    private float videoFps;

    public TextureRenderer(MediaController.SavedFilterState savedFilterState, String str, String str2, ArrayList<VideoEditedInfo.MediaEntity> arrayList, MediaController.CropState cropState, int i, int i2, int i3, float f, boolean z) {
        int i4;
        float[] fArr;
        MediaController.CropState cropState2 = cropState;
        int i5 = i;
        int i6 = i2;
        float f2 = f;
        this.isPhoto = z;
        float[] fArr2 = {0.0f, 0.0f, 1.0f, 0.0f, 0.0f, 1.0f, 1.0f, 1.0f};
        if (BuildVars.LOGS_ENABLED) {
            FileLog.d("start textureRenderer w = " + i5 + " h = " + i6 + " r = " + i3 + " fps = " + f2);
            if (cropState2 != null) {
                FileLog.d("cropState px = " + cropState2.cropPx + " py = " + cropState2.cropPy + " cScale = " + cropState2.cropScale + " cropRotate = " + cropState2.cropRotate + " pw = " + cropState2.cropPw + " ph = " + cropState2.cropPh + " tw = " + cropState2.transformWidth + " th = " + cropState2.transformHeight + " tr = " + cropState2.transformRotation + " mirror = " + cropState2.mirrored);
            }
        }
        FloatBuffer asFloatBuffer = ByteBuffer.allocateDirect(32).order(ByteOrder.nativeOrder()).asFloatBuffer();
        this.textureBuffer = asFloatBuffer;
        asFloatBuffer.put(fArr2).position(0);
        FloatBuffer asFloatBuffer2 = ByteBuffer.allocateDirect(this.bitmapData.length * 4).order(ByteOrder.nativeOrder()).asFloatBuffer();
        this.bitmapVerticesBuffer = asFloatBuffer2;
        asFloatBuffer2.put(this.bitmapData).position(0);
        Matrix.setIdentityM(this.mSTMatrix, 0);
        Matrix.setIdentityM(this.mSTMatrixIdentity, 0);
        if (savedFilterState != null) {
            FilterShaders filterShaders2 = new FilterShaders(true);
            this.filterShaders = filterShaders2;
            filterShaders2.setDelegate(FilterShaders.getFilterShadersDelegate(savedFilterState));
        }
        this.originalWidth = i5;
        this.transformedWidth = i5;
        this.originalHeight = i6;
        this.transformedHeight = i6;
        this.imagePath = str;
        this.paintPath = str2;
        this.mediaEntities = arrayList;
        this.videoFps = f2 == 0.0f ? 30.0f : f2;
        int i7 = this.filterShaders != null ? 2 : 1;
        this.mProgram = new int[i7];
        this.muMVPMatrixHandle = new int[i7];
        this.muSTMatrixHandle = new int[i7];
        this.maPositionHandle = new int[i7];
        this.maTextureHandle = new int[i7];
        Matrix.setIdentityM(this.mMVPMatrix, 0);
        if (cropState2 != null) {
            float f3 = (float) i5;
            float f4 = (float) i6;
            float[] fArr3 = {0.0f, 0.0f, f3, 0.0f, 0.0f, f4, f3, f4};
            i4 = cropState2.transformRotation;
            if (i4 == 90 || i4 == 270) {
                int i8 = this.originalWidth;
                this.originalWidth = this.originalHeight;
                this.originalHeight = i8;
            }
            this.transformedWidth = (int) (((float) this.transformedWidth) * cropState2.cropPw);
            this.transformedHeight = (int) (((float) this.transformedHeight) * cropState2.cropPh);
            double d = (double) (-cropState2.cropRotate);
            Double.isNaN(d);
            float f5 = (float) (d * 0.017453292519943295d);
            int i9 = 0;
            for (int i10 = 4; i9 < i10; i10 = 4) {
                int i11 = i9 * 2;
                int i12 = i11 + 1;
                float f6 = f3;
                double d2 = (double) (fArr3[i11] - ((float) (i5 / 2)));
                double d3 = (double) f5;
                double cos = Math.cos(d3);
                Double.isNaN(d2);
                double d4 = (double) (fArr3[i12] - ((float) (i6 / 2)));
                double sin = Math.sin(d3);
                Double.isNaN(d4);
                double d5 = (cos * d2) - (sin * d4);
                int i13 = i12;
                double d6 = (double) (cropState2.cropPx * f6);
                Double.isNaN(d6);
                float f7 = ((float) (d5 + d6)) * cropState2.cropScale;
                double sin2 = Math.sin(d3);
                Double.isNaN(d2);
                double cos2 = Math.cos(d3);
                Double.isNaN(d4);
                double d7 = (d2 * sin2) + (d4 * cos2);
                double d8 = (double) (cropState2.cropPy * f4);
                Double.isNaN(d8);
                float f8 = ((float) (d7 - d8)) * cropState2.cropScale;
                fArr3[i11] = (f7 / ((float) this.transformedWidth)) * 2.0f;
                fArr3[i13] = (f8 / ((float) this.transformedHeight)) * 2.0f;
                i9++;
                f3 = f6;
                i5 = i;
                i6 = i2;
            }
            FloatBuffer asFloatBuffer3 = ByteBuffer.allocateDirect(32).order(ByteOrder.nativeOrder()).asFloatBuffer();
            this.verticesBuffer = asFloatBuffer3;
            asFloatBuffer3.put(fArr3).position(0);
        } else {
            FloatBuffer asFloatBuffer4 = ByteBuffer.allocateDirect(32).order(ByteOrder.nativeOrder()).asFloatBuffer();
            this.verticesBuffer = asFloatBuffer4;
            asFloatBuffer4.put(new float[]{-1.0f, -1.0f, 1.0f, -1.0f, -1.0f, 1.0f, 1.0f, 1.0f}).position(0);
            i4 = 0;
        }
        if (this.filterShaders != null) {
            if (i4 == 90) {
                fArr = new float[]{1.0f, 1.0f, 1.0f, 0.0f, 0.0f, 1.0f, 0.0f, 0.0f};
            } else if (i4 == 180) {
                fArr = new float[]{1.0f, 0.0f, 0.0f, 0.0f, 1.0f, 1.0f, 0.0f, 1.0f};
            } else if (i4 == 270) {
                fArr = new float[]{0.0f, 0.0f, 0.0f, 1.0f, 1.0f, 0.0f, 1.0f, 1.0f};
            } else {
                fArr = new float[]{0.0f, 1.0f, 1.0f, 1.0f, 0.0f, 0.0f, 1.0f, 0.0f};
            }
        } else if (i4 == 90) {
            fArr = new float[]{1.0f, 0.0f, 1.0f, 1.0f, 0.0f, 0.0f, 0.0f, 1.0f};
        } else if (i4 == 180) {
            fArr = new float[]{1.0f, 1.0f, 0.0f, 1.0f, 1.0f, 0.0f, 0.0f, 0.0f};
        } else if (i4 == 270) {
            fArr = new float[]{0.0f, 1.0f, 0.0f, 0.0f, 1.0f, 1.0f, 1.0f, 0.0f};
        } else {
            fArr = new float[]{0.0f, 0.0f, 1.0f, 0.0f, 0.0f, 1.0f, 1.0f, 1.0f};
        }
        if (cropState2 != null && cropState2.mirrored) {
            int i14 = 0;
            for (int i15 = 4; i14 < i15; i15 = 4) {
                int i16 = i14 * 2;
                if (fArr[i16] > 0.5f) {
                    fArr[i16] = 0.0f;
                } else {
                    fArr[i16] = 1.0f;
                }
                i14++;
            }
        }
        FloatBuffer asFloatBuffer5 = ByteBuffer.allocateDirect(fArr.length * 4).order(ByteOrder.nativeOrder()).asFloatBuffer();
        this.renderTextureBuffer = asFloatBuffer5;
        asFloatBuffer5.put(fArr).position(0);
    }

    public int getTextureId() {
        return this.mTextureID;
    }

    public void drawFrame(SurfaceTexture surfaceTexture) {
        char c;
        float[] fArr;
        int i;
        int i2;
        if (this.isPhoto) {
            GLES20.glUseProgram(this.simpleShaderProgram);
            GLES20.glActiveTexture(33984);
            GLES20.glUniform1i(this.simpleSourceImageHandle, 0);
            GLES20.glEnableVertexAttribArray(this.simpleInputTexCoordHandle);
            GLES20.glVertexAttribPointer(this.simpleInputTexCoordHandle, 2, 5126, false, 8, this.textureBuffer);
            GLES20.glEnableVertexAttribArray(this.simplePositionHandle);
        } else {
            surfaceTexture.getTransformMatrix(this.mSTMatrix);
            if (BuildVars.LOGS_ENABLED && this.firstFrame) {
                StringBuilder sb = new StringBuilder();
                int i3 = 0;
                while (true) {
                    float[] fArr2 = this.mSTMatrix;
                    if (i3 >= fArr2.length) {
                        break;
                    }
                    sb.append(fArr2[i3]);
                    sb.append(", ");
                    i3++;
                }
                FileLog.d("stMatrix = " + sb);
                this.firstFrame = false;
            }
            if (this.blendEnabled) {
                GLES20.glDisable(3042);
                this.blendEnabled = false;
            }
            FilterShaders filterShaders2 = this.filterShaders;
            if (filterShaders2 != null) {
                filterShaders2.onVideoFrameUpdate(this.mSTMatrix);
                GLES20.glViewport(0, 0, this.originalWidth, this.originalHeight);
                this.filterShaders.drawSkinSmoothPass();
                this.filterShaders.drawEnhancePass();
                this.filterShaders.drawSharpenPass();
                this.filterShaders.drawCustomParamsPass();
                boolean drawBlurPass = this.filterShaders.drawBlurPass();
                GLES20.glBindFramebuffer(36160, 0);
                int i4 = this.transformedWidth;
                if (!(i4 == this.originalWidth && this.transformedHeight == this.originalHeight)) {
                    GLES20.glViewport(0, 0, i4, this.transformedHeight);
                }
                i2 = this.filterShaders.getRenderTexture(drawBlurPass ^ true ? 1 : 0);
                fArr = this.mSTMatrixIdentity;
                i = 3553;
                c = 1;
            } else {
                i2 = this.mTextureID;
                i = 36197;
                fArr = this.mSTMatrix;
                c = 0;
            }
            GLES20.glUseProgram(this.mProgram[c]);
            GLES20.glActiveTexture(33984);
            GLES20.glBindTexture(i, i2);
            GLES20.glVertexAttribPointer(this.maPositionHandle[c], 2, 5126, false, 8, this.verticesBuffer);
            GLES20.glEnableVertexAttribArray(this.maPositionHandle[c]);
            GLES20.glVertexAttribPointer(this.maTextureHandle[c], 2, 5126, false, 8, this.renderTextureBuffer);
            GLES20.glEnableVertexAttribArray(this.maTextureHandle[c]);
            GLES20.glUniformMatrix4fv(this.muSTMatrixHandle[c], 1, false, fArr, 0);
            GLES20.glUniformMatrix4fv(this.muMVPMatrixHandle[c], 1, false, this.mMVPMatrix, 0);
            GLES20.glDrawArrays(5, 0, 4);
        }
        if (!(this.paintTexture == null && this.stickerTexture == null)) {
            GLES20.glUseProgram(this.simpleShaderProgram);
            GLES20.glActiveTexture(33984);
            GLES20.glUniform1i(this.simpleSourceImageHandle, 0);
            GLES20.glEnableVertexAttribArray(this.simpleInputTexCoordHandle);
            GLES20.glVertexAttribPointer(this.simpleInputTexCoordHandle, 2, 5126, false, 8, this.textureBuffer);
            GLES20.glEnableVertexAttribArray(this.simplePositionHandle);
        }
        if (this.paintTexture != null) {
            int i5 = 0;
            while (true) {
                int[] iArr = this.paintTexture;
                if (i5 >= iArr.length) {
                    break;
                }
                drawTexture(true, iArr[i5]);
                i5++;
            }
        }
        if (this.stickerTexture != null) {
            int size = this.mediaEntities.size();
            for (int i6 = 0; i6 < size; i6++) {
                VideoEditedInfo.MediaEntity mediaEntity = this.mediaEntities.get(i6);
                long j = mediaEntity.ptr;
                if (j != 0) {
                    Bitmap bitmap = this.stickerBitmap;
                    RLottieDrawable.getFrame(j, (int) mediaEntity.currentFrame, bitmap, 512, 512, bitmap.getRowBytes(), true);
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
            float var_ = ((float) this.transformedWidth) / ((float) this.transformedHeight);
            float var_ = (fArr3[5] + fArr3[1]) / 2.0f;
            int i2 = 0;
            for (int i3 = 4; i2 < i3; i3 = 4) {
                float[] fArr4 = this.bitmapData;
                int i4 = i2 * 2;
                int i5 = i4 + 1;
                double d = (double) (fArr4[i4] - var_);
                double d2 = (double) f6;
                double cos = Math.cos(d2);
                Double.isNaN(d);
                double d3 = (double) ((fArr4[i5] - var_) / var_);
                double sin = Math.sin(d2);
                Double.isNaN(d3);
                fArr4[i4] = ((float) ((cos * d) - (sin * d3))) + var_;
                float[] fArr5 = this.bitmapData;
                double sin2 = Math.sin(d2);
                Double.isNaN(d);
                double cos2 = Math.cos(d2);
                Double.isNaN(d3);
                fArr5[i5] = (((float) ((d * sin2) + (d3 * cos2))) * var_) + var_;
                i2++;
            }
        }
        this.bitmapVerticesBuffer.put(this.bitmapData).position(0);
        GLES20.glVertexAttribPointer(this.simplePositionHandle, 2, 5126, false, 8, this.bitmapVerticesBuffer);
        if (z) {
            GLES20.glBindTexture(3553, i);
        }
        GLES20.glDrawArrays(5, 0, 4);
    }

    public void setBreakStrategy(EditTextOutline editTextOutline) {
        editTextOutline.setBreakStrategy(0);
    }

    /* JADX WARNING: Removed duplicated region for block: B:120:0x0209 A[SYNTHETIC] */
    /* JADX WARNING: Removed duplicated region for block: B:60:0x0169 A[ADDED_TO_REGION, Catch:{ all -> 0x0219 }] */
    @android.annotation.SuppressLint({"WrongConstant"})
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public void surfaceCreated() {
        /*
            r25 = this;
            r1 = r25
            r2 = 0
            r0 = 0
        L_0x0004:
            int[] r3 = r1.mProgram
            int r4 = r3.length
            if (r0 >= r4) goto L_0x0056
            if (r0 != 0) goto L_0x000e
            java.lang.String r4 = "#extension GL_OES_EGL_image_external : require\nprecision highp float;\nvarying vec2 vTextureCoord;\nuniform samplerExternalOES sTexture;\nvoid main() {\n  gl_FragColor = texture2D(sTexture, vTextureCoord);\n}\n"
            goto L_0x0010
        L_0x000e:
            java.lang.String r4 = "precision highp float;\nvarying vec2 vTextureCoord;\nuniform sampler2D sTexture;\nvoid main() {\n  gl_FragColor = texture2D(sTexture, vTextureCoord);\n}\n"
        L_0x0010:
            java.lang.String r5 = "uniform mat4 uMVPMatrix;\nuniform mat4 uSTMatrix;\nattribute vec4 aPosition;\nattribute vec4 aTextureCoord;\nvarying vec2 vTextureCoord;\nvoid main() {\n  gl_Position = uMVPMatrix * aPosition;\n  vTextureCoord = (uSTMatrix * aTextureCoord).xy;\n}\n"
            int r4 = r1.createProgram(r5, r4)
            r3[r0] = r4
            int[] r3 = r1.maPositionHandle
            int[] r4 = r1.mProgram
            r4 = r4[r0]
            java.lang.String r5 = "aPosition"
            int r4 = android.opengl.GLES20.glGetAttribLocation(r4, r5)
            r3[r0] = r4
            int[] r3 = r1.maTextureHandle
            int[] r4 = r1.mProgram
            r4 = r4[r0]
            java.lang.String r5 = "aTextureCoord"
            int r4 = android.opengl.GLES20.glGetAttribLocation(r4, r5)
            r3[r0] = r4
            int[] r3 = r1.muMVPMatrixHandle
            int[] r4 = r1.mProgram
            r4 = r4[r0]
            java.lang.String r5 = "uMVPMatrix"
            int r4 = android.opengl.GLES20.glGetUniformLocation(r4, r5)
            r3[r0] = r4
            int[] r3 = r1.muSTMatrixHandle
            int[] r4 = r1.mProgram
            r4 = r4[r0]
            java.lang.String r5 = "uSTMatrix"
            int r4 = android.opengl.GLES20.glGetUniformLocation(r4, r5)
            r3[r0] = r4
            int r0 = r0 + 1
            goto L_0x0004
        L_0x0056:
            r3 = 1
            int[] r0 = new int[r3]
            android.opengl.GLES20.glGenTextures(r3, r0, r2)
            r0 = r0[r2]
            r1.mTextureID = r0
            r4 = 36197(0x8d65, float:5.0723E-41)
            android.opengl.GLES20.glBindTexture(r4, r0)
            r5 = 10241(0x2801, float:1.435E-41)
            r6 = 9729(0x2601, float:1.3633E-41)
            android.opengl.GLES20.glTexParameteri(r4, r5, r6)
            r7 = 10240(0x2800, float:1.4349E-41)
            android.opengl.GLES20.glTexParameteri(r4, r7, r6)
            r8 = 10242(0x2802, float:1.4352E-41)
            r9 = 33071(0x812f, float:4.6342E-41)
            android.opengl.GLES20.glTexParameteri(r4, r8, r9)
            r10 = 10243(0x2803, float:1.4354E-41)
            android.opengl.GLES20.glTexParameteri(r4, r10, r9)
            org.telegram.ui.Components.FilterShaders r0 = r1.filterShaders
            if (r0 != 0) goto L_0x008f
            java.lang.String r0 = r1.imagePath
            if (r0 != 0) goto L_0x008f
            java.lang.String r0 = r1.paintPath
            if (r0 != 0) goto L_0x008f
            java.util.ArrayList<org.telegram.messenger.VideoEditedInfo$MediaEntity> r0 = r1.mediaEntities
            if (r0 == 0) goto L_0x00f8
        L_0x008f:
            r0 = 35633(0x8b31, float:4.9932E-41)
            java.lang.String r4 = "attribute vec4 position;attribute vec2 inputTexCoord;varying vec2 texCoord;void main() {gl_Position = position;texCoord = inputTexCoord;}"
            int r0 = org.telegram.ui.Components.FilterShaders.loadShader(r0, r4)
            r4 = 35632(0x8b30, float:4.9931E-41)
            java.lang.String r11 = "varying highp vec2 texCoord;uniform sampler2D sourceImage;void main() {gl_FragColor = texture2D(sourceImage, texCoord);}"
            int r4 = org.telegram.ui.Components.FilterShaders.loadShader(r4, r11)
            if (r0 == 0) goto L_0x00f8
            if (r4 == 0) goto L_0x00f8
            int r11 = android.opengl.GLES20.glCreateProgram()
            r1.simpleShaderProgram = r11
            android.opengl.GLES20.glAttachShader(r11, r0)
            int r0 = r1.simpleShaderProgram
            android.opengl.GLES20.glAttachShader(r0, r4)
            int r0 = r1.simpleShaderProgram
            java.lang.String r4 = "position"
            android.opengl.GLES20.glBindAttribLocation(r0, r2, r4)
            int r0 = r1.simpleShaderProgram
            java.lang.String r11 = "inputTexCoord"
            android.opengl.GLES20.glBindAttribLocation(r0, r3, r11)
            int r0 = r1.simpleShaderProgram
            android.opengl.GLES20.glLinkProgram(r0)
            int[] r0 = new int[r3]
            int r12 = r1.simpleShaderProgram
            r13 = 35714(0x8b82, float:5.0046E-41)
            android.opengl.GLES20.glGetProgramiv(r12, r13, r0, r2)
            r0 = r0[r2]
            if (r0 != 0) goto L_0x00dd
            int r0 = r1.simpleShaderProgram
            android.opengl.GLES20.glDeleteProgram(r0)
            r1.simpleShaderProgram = r2
            goto L_0x00f8
        L_0x00dd:
            int r0 = r1.simpleShaderProgram
            int r0 = android.opengl.GLES20.glGetAttribLocation(r0, r4)
            r1.simplePositionHandle = r0
            int r0 = r1.simpleShaderProgram
            int r0 = android.opengl.GLES20.glGetAttribLocation(r0, r11)
            r1.simpleInputTexCoordHandle = r0
            int r0 = r1.simpleShaderProgram
            java.lang.String r4 = "sourceImage"
            int r0 = android.opengl.GLES20.glGetUniformLocation(r0, r4)
            r1.simpleSourceImageHandle = r0
        L_0x00f8:
            org.telegram.ui.Components.FilterShaders r0 = r1.filterShaders
            if (r0 == 0) goto L_0x010e
            r0.create()
            org.telegram.ui.Components.FilterShaders r11 = r1.filterShaders
            r12 = 0
            r13 = 0
            int r14 = r1.mTextureID
            int r15 = r1.originalWidth
            int r0 = r1.originalHeight
            r16 = r0
            r11.setRenderData(r12, r13, r14, r15, r16)
        L_0x010e:
            java.lang.String r0 = r1.imagePath
            r4 = -16777216(0xfffffffffvar_, float:-1.7014118E38)
            r11 = 3
            r12 = 1065353216(0x3var_, float:1.0)
            r13 = 3553(0xde1, float:4.979E-42)
            if (r0 != 0) goto L_0x011d
            java.lang.String r14 = r1.paintPath
            if (r14 == 0) goto L_0x021d
        L_0x011d:
            if (r0 == 0) goto L_0x0121
            r0 = 1
            goto L_0x0122
        L_0x0121:
            r0 = 0
        L_0x0122:
            java.lang.String r14 = r1.paintPath
            if (r14 == 0) goto L_0x0128
            r14 = 1
            goto L_0x0129
        L_0x0128:
            r14 = 0
        L_0x0129:
            int r0 = r0 + r14
            int[] r0 = new int[r0]
            r1.paintTexture = r0
            int r14 = r0.length
            android.opengl.GLES20.glGenTextures(r14, r0, r2)
            r0 = 0
        L_0x0133:
            int[] r14 = r1.paintTexture     // Catch:{ all -> 0x0219 }
            int r14 = r14.length     // Catch:{ all -> 0x0219 }
            if (r0 >= r14) goto L_0x021d
            r14 = 270(0x10e, float:3.78E-43)
            r15 = 90
            if (r0 != 0) goto L_0x0160
            java.lang.String r2 = r1.imagePath     // Catch:{ all -> 0x0219 }
            if (r2 == 0) goto L_0x0160
            androidx.exifinterface.media.ExifInterface r10 = new androidx.exifinterface.media.ExifInterface     // Catch:{ all -> 0x0162 }
            r10.<init>((java.lang.String) r2)     // Catch:{ all -> 0x0162 }
            java.lang.String r8 = "Orientation"
            int r8 = r10.getAttributeInt(r8, r3)     // Catch:{ all -> 0x0162 }
            if (r8 == r11) goto L_0x015d
            r10 = 6
            if (r8 == r10) goto L_0x015a
            r10 = 8
            if (r8 == r10) goto L_0x0157
            goto L_0x0162
        L_0x0157:
            r8 = 270(0x10e, float:3.78E-43)
            goto L_0x0163
        L_0x015a:
            r8 = 90
            goto L_0x0163
        L_0x015d:
            r8 = 180(0xb4, float:2.52E-43)
            goto L_0x0163
        L_0x0160:
            java.lang.String r2 = r1.paintPath     // Catch:{ all -> 0x0219 }
        L_0x0162:
            r8 = 0
        L_0x0163:
            android.graphics.Bitmap r2 = android.graphics.BitmapFactory.decodeFile(r2)     // Catch:{ all -> 0x0219 }
            if (r2 == 0) goto L_0x0209
            if (r0 != 0) goto L_0x01eb
            java.lang.String r10 = r1.imagePath     // Catch:{ all -> 0x0219 }
            if (r10 == 0) goto L_0x01eb
            int r10 = r1.transformedWidth     // Catch:{ all -> 0x0219 }
            int r11 = r1.transformedHeight     // Catch:{ all -> 0x0219 }
            android.graphics.Bitmap$Config r3 = android.graphics.Bitmap.Config.ARGB_8888     // Catch:{ all -> 0x0219 }
            android.graphics.Bitmap r3 = android.graphics.Bitmap.createBitmap(r10, r11, r3)     // Catch:{ all -> 0x0219 }
            r3.eraseColor(r4)     // Catch:{ all -> 0x0219 }
            android.graphics.Canvas r10 = new android.graphics.Canvas     // Catch:{ all -> 0x0219 }
            r10.<init>(r3)     // Catch:{ all -> 0x0219 }
            if (r8 == r15) goto L_0x019d
            if (r8 != r14) goto L_0x0186
            goto L_0x019d
        L_0x0186:
            int r11 = r2.getWidth()     // Catch:{ all -> 0x0219 }
            float r11 = (float) r11     // Catch:{ all -> 0x0219 }
            int r14 = r1.transformedWidth     // Catch:{ all -> 0x0219 }
            float r14 = (float) r14     // Catch:{ all -> 0x0219 }
            float r11 = r11 / r14
            int r14 = r2.getHeight()     // Catch:{ all -> 0x0219 }
            float r14 = (float) r14     // Catch:{ all -> 0x0219 }
            int r15 = r1.transformedHeight     // Catch:{ all -> 0x0219 }
            float r15 = (float) r15     // Catch:{ all -> 0x0219 }
            float r14 = r14 / r15
            float r11 = java.lang.Math.max(r11, r14)     // Catch:{ all -> 0x0219 }
            goto L_0x01b3
        L_0x019d:
            int r11 = r2.getHeight()     // Catch:{ all -> 0x0219 }
            float r11 = (float) r11     // Catch:{ all -> 0x0219 }
            int r14 = r1.transformedWidth     // Catch:{ all -> 0x0219 }
            float r14 = (float) r14     // Catch:{ all -> 0x0219 }
            float r11 = r11 / r14
            int r14 = r2.getWidth()     // Catch:{ all -> 0x0219 }
            float r14 = (float) r14     // Catch:{ all -> 0x0219 }
            int r15 = r1.transformedHeight     // Catch:{ all -> 0x0219 }
            float r15 = (float) r15     // Catch:{ all -> 0x0219 }
            float r14 = r14 / r15
            float r11 = java.lang.Math.max(r11, r14)     // Catch:{ all -> 0x0219 }
        L_0x01b3:
            android.graphics.Matrix r14 = new android.graphics.Matrix     // Catch:{ all -> 0x0219 }
            r14.<init>()     // Catch:{ all -> 0x0219 }
            int r15 = r2.getWidth()     // Catch:{ all -> 0x0219 }
            int r15 = -r15
            r4 = 2
            int r15 = r15 / r4
            float r15 = (float) r15     // Catch:{ all -> 0x0219 }
            int r9 = r2.getHeight()     // Catch:{ all -> 0x0219 }
            int r9 = -r9
            int r9 = r9 / r4
            float r9 = (float) r9     // Catch:{ all -> 0x0219 }
            r14.postTranslate(r15, r9)     // Catch:{ all -> 0x0219 }
            float r9 = r12 / r11
            r14.postScale(r9, r9)     // Catch:{ all -> 0x0219 }
            float r8 = (float) r8     // Catch:{ all -> 0x0219 }
            r14.postRotate(r8)     // Catch:{ all -> 0x0219 }
            int r8 = r3.getWidth()     // Catch:{ all -> 0x0219 }
            int r8 = r8 / r4
            float r8 = (float) r8     // Catch:{ all -> 0x0219 }
            int r9 = r3.getHeight()     // Catch:{ all -> 0x0219 }
            int r9 = r9 / r4
            float r9 = (float) r9     // Catch:{ all -> 0x0219 }
            r14.postTranslate(r8, r9)     // Catch:{ all -> 0x0219 }
            android.graphics.Paint r8 = new android.graphics.Paint     // Catch:{ all -> 0x0219 }
            r8.<init>(r4)     // Catch:{ all -> 0x0219 }
            r10.drawBitmap(r2, r14, r8)     // Catch:{ all -> 0x0219 }
            r2 = r3
        L_0x01eb:
            int[] r3 = r1.paintTexture     // Catch:{ all -> 0x0219 }
            r3 = r3[r0]     // Catch:{ all -> 0x0219 }
            android.opengl.GLES20.glBindTexture(r13, r3)     // Catch:{ all -> 0x0219 }
            android.opengl.GLES20.glTexParameteri(r13, r5, r6)     // Catch:{ all -> 0x0219 }
            android.opengl.GLES20.glTexParameteri(r13, r7, r6)     // Catch:{ all -> 0x0219 }
            r3 = 10242(0x2802, float:1.4352E-41)
            r4 = 33071(0x812f, float:4.6342E-41)
            android.opengl.GLES20.glTexParameteri(r13, r3, r4)     // Catch:{ all -> 0x0219 }
            r3 = 10243(0x2803, float:1.4354E-41)
            android.opengl.GLES20.glTexParameteri(r13, r3, r4)     // Catch:{ all -> 0x0219 }
            r3 = 0
            android.opengl.GLUtils.texImage2D(r13, r3, r2, r3)     // Catch:{ all -> 0x0219 }
        L_0x0209:
            int r0 = r0 + 1
            r2 = 0
            r3 = 1
            r4 = -16777216(0xfffffffffvar_, float:-1.7014118E38)
            r8 = 10242(0x2802, float:1.4352E-41)
            r9 = 33071(0x812f, float:4.6342E-41)
            r10 = 10243(0x2803, float:1.4354E-41)
            r11 = 3
            goto L_0x0133
        L_0x0219:
            r0 = move-exception
            org.telegram.messenger.FileLog.e((java.lang.Throwable) r0)
        L_0x021d:
            java.util.ArrayList<org.telegram.messenger.VideoEditedInfo$MediaEntity> r0 = r1.mediaEntities
            if (r0 == 0) goto L_0x0403
            android.graphics.Bitmap$Config r0 = android.graphics.Bitmap.Config.ARGB_8888     // Catch:{ all -> 0x03ff }
            r2 = 512(0x200, float:7.175E-43)
            android.graphics.Bitmap r0 = android.graphics.Bitmap.createBitmap(r2, r2, r0)     // Catch:{ all -> 0x03ff }
            r1.stickerBitmap = r0     // Catch:{ all -> 0x03ff }
            r2 = 1
            int[] r0 = new int[r2]     // Catch:{ all -> 0x03ff }
            r1.stickerTexture = r0     // Catch:{ all -> 0x03ff }
            r3 = 0
            android.opengl.GLES20.glGenTextures(r2, r0, r3)     // Catch:{ all -> 0x03ff }
            int[] r0 = r1.stickerTexture     // Catch:{ all -> 0x03ff }
            r0 = r0[r3]     // Catch:{ all -> 0x03ff }
            android.opengl.GLES20.glBindTexture(r13, r0)     // Catch:{ all -> 0x03ff }
            android.opengl.GLES20.glTexParameteri(r13, r5, r6)     // Catch:{ all -> 0x03ff }
            android.opengl.GLES20.glTexParameteri(r13, r7, r6)     // Catch:{ all -> 0x03ff }
            r2 = 10242(0x2802, float:1.4352E-41)
            r3 = 33071(0x812f, float:4.6342E-41)
            android.opengl.GLES20.glTexParameteri(r13, r2, r3)     // Catch:{ all -> 0x03ff }
            r2 = 10243(0x2803, float:1.4354E-41)
            android.opengl.GLES20.glTexParameteri(r13, r2, r3)     // Catch:{ all -> 0x03ff }
            java.util.ArrayList<org.telegram.messenger.VideoEditedInfo$MediaEntity> r0 = r1.mediaEntities     // Catch:{ all -> 0x03ff }
            int r0 = r0.size()     // Catch:{ all -> 0x03ff }
            r3 = 0
        L_0x0255:
            if (r3 >= r0) goto L_0x0403
            java.util.ArrayList<org.telegram.messenger.VideoEditedInfo$MediaEntity> r2 = r1.mediaEntities     // Catch:{ all -> 0x03ff }
            java.lang.Object r2 = r2.get(r3)     // Catch:{ all -> 0x03ff }
            org.telegram.messenger.VideoEditedInfo$MediaEntity r2 = (org.telegram.messenger.VideoEditedInfo.MediaEntity) r2     // Catch:{ all -> 0x03ff }
            byte r4 = r2.type     // Catch:{ all -> 0x03ff }
            r5 = 0
            if (r4 != 0) goto L_0x0325
            byte r4 = r2.subType     // Catch:{ all -> 0x03ff }
            r6 = 1
            r4 = r4 & r6
            if (r4 == 0) goto L_0x0294
            r6 = 3
            int[] r4 = new int[r6]     // Catch:{ all -> 0x03ff }
            r2.metadata = r4     // Catch:{ all -> 0x03ff }
            java.lang.String r5 = r2.text     // Catch:{ all -> 0x03ff }
            r18 = 0
            r19 = 512(0x200, float:7.175E-43)
            r20 = 512(0x200, float:7.175E-43)
            r22 = 0
            r23 = 0
            r24 = 0
            r17 = r5
            r21 = r4
            long r4 = org.telegram.ui.Components.RLottieDrawable.create(r17, r18, r19, r20, r21, r22, r23, r24)     // Catch:{ all -> 0x03ff }
            r2.ptr = r4     // Catch:{ all -> 0x03ff }
            int[] r4 = r2.metadata     // Catch:{ all -> 0x03ff }
            r5 = 1
            r4 = r4[r5]     // Catch:{ all -> 0x03ff }
            float r4 = (float) r4     // Catch:{ all -> 0x03ff }
            float r5 = r1.videoFps     // Catch:{ all -> 0x03ff }
            float r4 = r4 / r5
            r2.framesPerDraw = r4     // Catch:{ all -> 0x03ff }
            goto L_0x0320
        L_0x0294:
            r6 = 3
            int r4 = android.os.Build.VERSION.SDK_INT     // Catch:{ all -> 0x03ff }
            r7 = 19
            if (r4 < r7) goto L_0x02a4
            java.lang.String r4 = r2.text     // Catch:{ all -> 0x03ff }
            android.graphics.Bitmap r4 = android.graphics.BitmapFactory.decodeFile(r4)     // Catch:{ all -> 0x03ff }
            r2.bitmap = r4     // Catch:{ all -> 0x03ff }
            goto L_0x02e8
        L_0x02a4:
            java.io.File r4 = new java.io.File     // Catch:{ all -> 0x03ff }
            java.lang.String r7 = r2.text     // Catch:{ all -> 0x03ff }
            r4.<init>(r7)     // Catch:{ all -> 0x03ff }
            java.io.RandomAccessFile r7 = new java.io.RandomAccessFile     // Catch:{ all -> 0x03ff }
            java.lang.String r8 = "r"
            r7.<init>(r4, r8)     // Catch:{ all -> 0x03ff }
            java.nio.channels.FileChannel r17 = r7.getChannel()     // Catch:{ all -> 0x03ff }
            java.nio.channels.FileChannel$MapMode r18 = java.nio.channels.FileChannel.MapMode.READ_ONLY     // Catch:{ all -> 0x03ff }
            r19 = 0
            long r21 = r4.length()     // Catch:{ all -> 0x03ff }
            java.nio.MappedByteBuffer r4 = r17.map(r18, r19, r21)     // Catch:{ all -> 0x03ff }
            android.graphics.BitmapFactory$Options r8 = new android.graphics.BitmapFactory$Options     // Catch:{ all -> 0x03ff }
            r8.<init>()     // Catch:{ all -> 0x03ff }
            r9 = 1
            r8.inJustDecodeBounds = r9     // Catch:{ all -> 0x03ff }
            int r10 = r4.limit()     // Catch:{ all -> 0x03ff }
            org.telegram.messenger.Utilities.loadWebpImage(r5, r4, r10, r8, r9)     // Catch:{ all -> 0x03ff }
            int r9 = r8.outWidth     // Catch:{ all -> 0x03ff }
            int r8 = r8.outHeight     // Catch:{ all -> 0x03ff }
            android.graphics.Bitmap$Config r10 = android.graphics.Bitmap.Config.ARGB_8888     // Catch:{ all -> 0x03ff }
            android.graphics.Bitmap r8 = org.telegram.messenger.Bitmaps.createBitmap(r9, r8, r10)     // Catch:{ all -> 0x03ff }
            r2.bitmap = r8     // Catch:{ all -> 0x03ff }
            int r9 = r4.limit()     // Catch:{ all -> 0x03ff }
            r10 = 1
            org.telegram.messenger.Utilities.loadWebpImage(r8, r4, r9, r5, r10)     // Catch:{ all -> 0x03ff }
            r7.close()     // Catch:{ all -> 0x03ff }
        L_0x02e8:
            android.graphics.Bitmap r4 = r2.bitmap     // Catch:{ all -> 0x03ff }
            if (r4 == 0) goto L_0x0320
            int r4 = r4.getWidth()     // Catch:{ all -> 0x03ff }
            float r4 = (float) r4     // Catch:{ all -> 0x03ff }
            android.graphics.Bitmap r5 = r2.bitmap     // Catch:{ all -> 0x03ff }
            int r5 = r5.getHeight()     // Catch:{ all -> 0x03ff }
            float r5 = (float) r5     // Catch:{ all -> 0x03ff }
            float r4 = r4 / r5
            int r5 = (r4 > r12 ? 1 : (r4 == r12 ? 0 : -1))
            if (r5 <= 0) goto L_0x030d
            float r5 = r2.height     // Catch:{ all -> 0x03ff }
            float r4 = r5 / r4
            float r7 = r2.y     // Catch:{ all -> 0x03ff }
            float r5 = r5 - r4
            r8 = 1073741824(0x40000000, float:2.0)
            float r5 = r5 / r8
            float r7 = r7 + r5
            r2.y = r7     // Catch:{ all -> 0x03ff }
            r2.height = r4     // Catch:{ all -> 0x03ff }
            goto L_0x0320
        L_0x030d:
            int r5 = (r4 > r12 ? 1 : (r4 == r12 ? 0 : -1))
            if (r5 >= 0) goto L_0x0320
            float r5 = r2.width     // Catch:{ all -> 0x03ff }
            float r4 = r4 * r5
            float r7 = r2.x     // Catch:{ all -> 0x03ff }
            float r5 = r5 - r4
            r8 = 1073741824(0x40000000, float:2.0)
            float r5 = r5 / r8
            float r7 = r7 + r5
            r2.x = r7     // Catch:{ all -> 0x03ff }
            r2.width = r4     // Catch:{ all -> 0x03ff }
        L_0x0320:
            r5 = -16777216(0xfffffffffvar_, float:-1.7014118E38)
            r7 = 1
            goto L_0x03fa
        L_0x0325:
            r6 = 3
            r7 = 1
            if (r4 != r7) goto L_0x03f8
            org.telegram.ui.Components.Paint.Views.EditTextOutline r4 = new org.telegram.ui.Components.Paint.Views.EditTextOutline     // Catch:{ all -> 0x03ff }
            android.content.Context r7 = org.telegram.messenger.ApplicationLoader.applicationContext     // Catch:{ all -> 0x03ff }
            r4.<init>(r7)     // Catch:{ all -> 0x03ff }
            r7 = 0
            r4.setBackgroundColor(r7)     // Catch:{ all -> 0x03ff }
            r7 = 1088421888(0x40e00000, float:7.0)
            int r8 = org.telegram.messenger.AndroidUtilities.dp(r7)     // Catch:{ all -> 0x03ff }
            int r9 = org.telegram.messenger.AndroidUtilities.dp(r7)     // Catch:{ all -> 0x03ff }
            int r10 = org.telegram.messenger.AndroidUtilities.dp(r7)     // Catch:{ all -> 0x03ff }
            int r7 = org.telegram.messenger.AndroidUtilities.dp(r7)     // Catch:{ all -> 0x03ff }
            r4.setPadding(r8, r9, r10, r7)     // Catch:{ all -> 0x03ff }
            int r7 = r2.fontSize     // Catch:{ all -> 0x03ff }
            float r7 = (float) r7     // Catch:{ all -> 0x03ff }
            r8 = 0
            r4.setTextSize(r8, r7)     // Catch:{ all -> 0x03ff }
            java.lang.String r7 = r2.text     // Catch:{ all -> 0x03ff }
            r4.setText(r7)     // Catch:{ all -> 0x03ff }
            int r7 = r2.color     // Catch:{ all -> 0x03ff }
            r4.setTextColor(r7)     // Catch:{ all -> 0x03ff }
            r7 = 1
            r4.setTypeface(r5, r7)     // Catch:{ all -> 0x03ff }
            r5 = 17
            r4.setGravity(r5)     // Catch:{ all -> 0x03ff }
            r5 = 0
            r4.setHorizontallyScrolling(r5)     // Catch:{ all -> 0x03ff }
            r5 = 268435456(0x10000000, float:2.5243549E-29)
            r4.setImeOptions(r5)     // Catch:{ all -> 0x03ff }
            r4.setFocusableInTouchMode(r7)     // Catch:{ all -> 0x03ff }
            int r5 = r4.getInputType()     // Catch:{ all -> 0x03ff }
            r5 = r5 | 16384(0x4000, float:2.2959E-41)
            r4.setInputType(r5)     // Catch:{ all -> 0x03ff }
            int r5 = android.os.Build.VERSION.SDK_INT     // Catch:{ all -> 0x03ff }
            r8 = 23
            if (r5 < r8) goto L_0x0381
            r1.setBreakStrategy(r4)     // Catch:{ all -> 0x03ff }
        L_0x0381:
            byte r5 = r2.subType     // Catch:{ all -> 0x03ff }
            r8 = r5 & 1
            r9 = 0
            if (r8 == 0) goto L_0x039b
            r5 = -1
            r4.setTextColor(r5)     // Catch:{ all -> 0x03ff }
            int r5 = r2.color     // Catch:{ all -> 0x03ff }
            r4.setStrokeColor(r5)     // Catch:{ all -> 0x03ff }
            r5 = 0
            r4.setFrameColor(r5)     // Catch:{ all -> 0x03ff }
            r4.setShadowLayer(r9, r9, r9, r5)     // Catch:{ all -> 0x03ff }
            r5 = -16777216(0xfffffffffvar_, float:-1.7014118E38)
            goto L_0x03c6
        L_0x039b:
            r5 = r5 & 4
            if (r5 == 0) goto L_0x03b1
            r5 = -16777216(0xfffffffffvar_, float:-1.7014118E38)
            r4.setTextColor(r5)     // Catch:{ all -> 0x03ff }
            r8 = 0
            r4.setStrokeColor(r8)     // Catch:{ all -> 0x03ff }
            int r10 = r2.color     // Catch:{ all -> 0x03ff }
            r4.setFrameColor(r10)     // Catch:{ all -> 0x03ff }
            r4.setShadowLayer(r9, r9, r9, r8)     // Catch:{ all -> 0x03ff }
            goto L_0x03c6
        L_0x03b1:
            r5 = -16777216(0xfffffffffvar_, float:-1.7014118E38)
            int r8 = r2.color     // Catch:{ all -> 0x03ff }
            r4.setTextColor(r8)     // Catch:{ all -> 0x03ff }
            r8 = 0
            r4.setStrokeColor(r8)     // Catch:{ all -> 0x03ff }
            r4.setFrameColor(r8)     // Catch:{ all -> 0x03ff }
            r8 = 1084227584(0x40a00000, float:5.0)
            r10 = 1711276032(0x66000000, float:1.5111573E23)
            r4.setShadowLayer(r8, r9, r12, r10)     // Catch:{ all -> 0x03ff }
        L_0x03c6:
            int r8 = r2.viewWidth     // Catch:{ all -> 0x03ff }
            r9 = 1073741824(0x40000000, float:2.0)
            int r8 = android.view.View.MeasureSpec.makeMeasureSpec(r8, r9)     // Catch:{ all -> 0x03ff }
            int r9 = r2.viewHeight     // Catch:{ all -> 0x03ff }
            r10 = 1073741824(0x40000000, float:2.0)
            int r9 = android.view.View.MeasureSpec.makeMeasureSpec(r9, r10)     // Catch:{ all -> 0x03ff }
            r4.measure(r8, r9)     // Catch:{ all -> 0x03ff }
            int r8 = r2.viewWidth     // Catch:{ all -> 0x03ff }
            int r9 = r2.viewHeight     // Catch:{ all -> 0x03ff }
            r10 = 0
            r4.layout(r10, r10, r8, r9)     // Catch:{ all -> 0x03ff }
            int r8 = r2.viewWidth     // Catch:{ all -> 0x03ff }
            int r9 = r2.viewHeight     // Catch:{ all -> 0x03ff }
            android.graphics.Bitmap$Config r11 = android.graphics.Bitmap.Config.ARGB_8888     // Catch:{ all -> 0x03ff }
            android.graphics.Bitmap r8 = android.graphics.Bitmap.createBitmap(r8, r9, r11)     // Catch:{ all -> 0x03ff }
            r2.bitmap = r8     // Catch:{ all -> 0x03ff }
            android.graphics.Canvas r8 = new android.graphics.Canvas     // Catch:{ all -> 0x03ff }
            android.graphics.Bitmap r2 = r2.bitmap     // Catch:{ all -> 0x03ff }
            r8.<init>(r2)     // Catch:{ all -> 0x03ff }
            r4.draw(r8)     // Catch:{ all -> 0x03ff }
            goto L_0x03fb
        L_0x03f8:
            r5 = -16777216(0xfffffffffvar_, float:-1.7014118E38)
        L_0x03fa:
            r10 = 0
        L_0x03fb:
            int r3 = r3 + 1
            goto L_0x0255
        L_0x03ff:
            r0 = move-exception
            org.telegram.messenger.FileLog.e((java.lang.Throwable) r0)
        L_0x0403:
            return
        */
        throw new UnsupportedOperationException("Method not decompiled: org.telegram.messenger.video.TextureRenderer.surfaceCreated():void");
    }

    private int createProgram(String str, String str2) {
        int loadShader;
        int glCreateProgram;
        int loadShader2 = FilterShaders.loadShader(35633, str);
        if (loadShader2 == 0 || (loadShader = FilterShaders.loadShader(35632, str2)) == 0 || (glCreateProgram = GLES20.glCreateProgram()) == 0) {
            return 0;
        }
        GLES20.glAttachShader(glCreateProgram, loadShader2);
        GLES20.glAttachShader(glCreateProgram, loadShader);
        GLES20.glLinkProgram(glCreateProgram);
        int[] iArr = new int[1];
        GLES20.glGetProgramiv(glCreateProgram, 35714, iArr, 0);
        if (iArr[0] == 1) {
            return glCreateProgram;
        }
        GLES20.glDeleteProgram(glCreateProgram);
        return 0;
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
