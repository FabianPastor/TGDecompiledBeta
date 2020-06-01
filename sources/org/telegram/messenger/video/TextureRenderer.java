package org.telegram.messenger.video;

import android.annotation.SuppressLint;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.SurfaceTexture;
import android.graphics.Typeface;
import android.opengl.GLES20;
import android.opengl.GLUtils;
import android.opengl.Matrix;
import android.os.Build;
import android.view.View;
import androidx.exifinterface.media.ExifInterface;
import java.io.File;
import java.io.RandomAccessFile;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;
import java.nio.MappedByteBuffer;
import java.nio.channels.FileChannel;
import java.util.ArrayList;
import org.telegram.messenger.AndroidUtilities;
import org.telegram.messenger.ApplicationLoader;
import org.telegram.messenger.Bitmaps;
import org.telegram.messenger.FileLog;
import org.telegram.messenger.MediaController;
import org.telegram.messenger.Utilities;
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

    @SuppressLint({"WrongConstant"})
    public void surfaceCreated() {
        android.graphics.Matrix matrix;
        String str;
        Bitmap createBitmap;
        android.graphics.Matrix matrix2;
        int createProgram = createProgram("uniform mat4 uMVPMatrix;\nuniform mat4 uSTMatrix;\nattribute vec4 aPosition;\nattribute vec4 aTextureCoord;\nvarying vec2 vTextureCoord;\nvoid main() {\n  gl_Position = uMVPMatrix * aPosition;\n  vTextureCoord = (uSTMatrix * aTextureCoord).xy;\n}\n", "#extension GL_OES_EGL_image_external : require\nprecision highp float;\nvarying vec2 vTextureCoord;\nuniform samplerExternalOES sTexture;\nvoid main() {\n  gl_FragColor = texture2D(sTexture, vTextureCoord);\n}\n");
        this.mProgram = createProgram;
        if (createProgram != 0) {
            this.maPositionHandle = GLES20.glGetAttribLocation(createProgram, "aPosition");
            checkGlError("glGetAttribLocation aPosition");
            if (this.maPositionHandle != -1) {
                this.maTextureHandle = GLES20.glGetAttribLocation(this.mProgram, "aTextureCoord");
                checkGlError("glGetAttribLocation aTextureCoord");
                if (this.maTextureHandle != -1) {
                    this.muMVPMatrixHandle = GLES20.glGetUniformLocation(this.mProgram, "uMVPMatrix");
                    checkGlError("glGetUniformLocation uMVPMatrix");
                    if (this.muMVPMatrixHandle != -1) {
                        this.muSTMatrixHandle = GLES20.glGetUniformLocation(this.mProgram, "uSTMatrix");
                        checkGlError("glGetUniformLocation uSTMatrix");
                        if (this.muSTMatrixHandle != -1) {
                            int[] iArr = new int[1];
                            GLES20.glGenTextures(1, iArr, 0);
                            int i = iArr[0];
                            this.mTextureID = i;
                            GLES20.glBindTexture(36197, i);
                            checkGlError("glBindTexture mTextureID");
                            GLES20.glTexParameteri(36197, 10241, 9729);
                            GLES20.glTexParameteri(36197, 10240, 9729);
                            GLES20.glTexParameteri(36197, 10242, 33071);
                            GLES20.glTexParameteri(36197, 10243, 33071);
                            checkGlError("glTexParameter");
                            Matrix.setIdentityM(this.mMVPMatrix, 0);
                            int i2 = this.rotationAngle;
                            if (i2 != 0) {
                                Matrix.rotateM(this.mMVPMatrix, 0, (float) i2, 0.0f, 0.0f, 1.0f);
                            }
                            if (!(this.filterShaders == null && this.imagePath == null && this.paintPath == null && this.mediaEntities == null)) {
                                int loadShader = FilterShaders.loadShader(35633, "attribute vec4 position;attribute vec2 inputTexCoord;varying vec2 texCoord;void main() {gl_Position = position;texCoord = inputTexCoord;}");
                                int loadShader2 = FilterShaders.loadShader(35632, "varying highp vec2 texCoord;uniform sampler2D sourceImage;void main() {gl_FragColor = texture2D(sourceImage, texCoord);}");
                                if (!(loadShader == 0 || loadShader2 == 0)) {
                                    int glCreateProgram = GLES20.glCreateProgram();
                                    this.simpleShaderProgram = glCreateProgram;
                                    GLES20.glAttachShader(glCreateProgram, loadShader);
                                    GLES20.glAttachShader(this.simpleShaderProgram, loadShader2);
                                    GLES20.glBindAttribLocation(this.simpleShaderProgram, 0, "position");
                                    GLES20.glBindAttribLocation(this.simpleShaderProgram, 1, "inputTexCoord");
                                    GLES20.glLinkProgram(this.simpleShaderProgram);
                                    int[] iArr2 = new int[1];
                                    GLES20.glGetProgramiv(this.simpleShaderProgram, 35714, iArr2, 0);
                                    if (iArr2[0] == 0) {
                                        GLES20.glDeleteProgram(this.simpleShaderProgram);
                                        this.simpleShaderProgram = 0;
                                    } else {
                                        this.simplePositionHandle = GLES20.glGetAttribLocation(this.simpleShaderProgram, "position");
                                        this.simpleInputTexCoordHandle = GLES20.glGetAttribLocation(this.simpleShaderProgram, "inputTexCoord");
                                        this.simpleSourceImageHandle = GLES20.glGetUniformLocation(this.simpleShaderProgram, "sourceImage");
                                    }
                                }
                            }
                            FilterShaders filterShaders2 = this.filterShaders;
                            if (filterShaders2 != null) {
                                filterShaders2.create();
                                this.filterShaders.setRenderData((Bitmap) null, 0, this.mTextureID, this.videoWidth, this.videoHeight);
                            }
                            if (!(this.imagePath == null && this.paintPath == null)) {
                                int[] iArr3 = new int[((this.imagePath != null ? 1 : 0) + (this.paintPath != null ? 1 : 0))];
                                this.paintTexture = iArr3;
                                GLES20.glGenTextures(iArr3.length, iArr3, 0);
                                for (int i3 = 0; i3 < this.paintTexture.length; i3++) {
                                    if (i3 != 0 || this.imagePath == null) {
                                        try {
                                            str = this.paintPath;
                                            matrix = null;
                                        } catch (Throwable th) {
                                            FileLog.e(th);
                                        }
                                    } else {
                                        str = this.imagePath;
                                        try {
                                            int attributeInt = new ExifInterface(str).getAttributeInt("Orientation", 1);
                                            matrix2 = new android.graphics.Matrix();
                                            if (attributeInt == 3) {
                                                matrix2.postRotate(180.0f);
                                            } else if (attributeInt == 6) {
                                                matrix2.postRotate(90.0f);
                                            } else if (attributeInt == 8) {
                                                try {
                                                    matrix2.postRotate(270.0f);
                                                } catch (Throwable unused) {
                                                }
                                            }
                                        } catch (Throwable unused2) {
                                            matrix2 = null;
                                        }
                                        matrix = matrix2;
                                    }
                                    Bitmap decodeFile = BitmapFactory.decodeFile(str);
                                    if (decodeFile != null) {
                                        if (!(matrix == null || (createBitmap = Bitmaps.createBitmap(decodeFile, 0, 0, decodeFile.getWidth(), decodeFile.getHeight(), matrix, true)) == decodeFile)) {
                                            decodeFile.recycle();
                                            decodeFile = createBitmap;
                                        }
                                        GLES20.glBindTexture(3553, this.paintTexture[i3]);
                                        GLES20.glTexParameteri(3553, 10241, 9729);
                                        GLES20.glTexParameteri(3553, 10240, 9729);
                                        GLES20.glTexParameteri(3553, 10242, 33071);
                                        GLES20.glTexParameteri(3553, 10243, 33071);
                                        GLUtils.texImage2D(3553, 0, decodeFile, 0);
                                    }
                                }
                            }
                            if (this.mediaEntities != null) {
                                try {
                                    this.stickerBitmap = Bitmap.createBitmap(512, 512, Bitmap.Config.ARGB_8888);
                                    int[] iArr4 = new int[1];
                                    this.stickerTexture = iArr4;
                                    GLES20.glGenTextures(1, iArr4, 0);
                                    GLES20.glBindTexture(3553, this.stickerTexture[0]);
                                    GLES20.glTexParameteri(3553, 10241, 9729);
                                    GLES20.glTexParameteri(3553, 10240, 9729);
                                    GLES20.glTexParameteri(3553, 10242, 33071);
                                    GLES20.glTexParameteri(3553, 10243, 33071);
                                    int size = this.mediaEntities.size();
                                    for (int i4 = 0; i4 < size; i4++) {
                                        VideoEditedInfo.MediaEntity mediaEntity = this.mediaEntities.get(i4);
                                        if (mediaEntity.type == 0) {
                                            if ((mediaEntity.subType & 1) != 0) {
                                                int[] iArr5 = new int[3];
                                                mediaEntity.metadata = iArr5;
                                                mediaEntity.ptr = RLottieDrawable.create(mediaEntity.text, 512, 512, iArr5, false, (int[]) null, false);
                                                mediaEntity.framesPerDraw = ((float) mediaEntity.metadata[1]) / this.videoFps;
                                            } else {
                                                if (Build.VERSION.SDK_INT >= 19) {
                                                    mediaEntity.bitmap = BitmapFactory.decodeFile(mediaEntity.text);
                                                } else {
                                                    File file = new File(mediaEntity.text);
                                                    RandomAccessFile randomAccessFile = new RandomAccessFile(file, "r");
                                                    MappedByteBuffer map = randomAccessFile.getChannel().map(FileChannel.MapMode.READ_ONLY, 0, file.length());
                                                    BitmapFactory.Options options = new BitmapFactory.Options();
                                                    options.inJustDecodeBounds = true;
                                                    Utilities.loadWebpImage((Bitmap) null, map, map.limit(), options, true);
                                                    Bitmap createBitmap2 = Bitmaps.createBitmap(options.outWidth, options.outHeight, Bitmap.Config.ARGB_8888);
                                                    mediaEntity.bitmap = createBitmap2;
                                                    Utilities.loadWebpImage(createBitmap2, map, map.limit(), (BitmapFactory.Options) null, true);
                                                    randomAccessFile.close();
                                                }
                                                if (mediaEntity.bitmap != null) {
                                                    float width = ((float) mediaEntity.bitmap.getWidth()) / ((float) mediaEntity.bitmap.getHeight());
                                                    if (width > 1.0f) {
                                                        float f = mediaEntity.height / width;
                                                        mediaEntity.y += (mediaEntity.height - f) / 2.0f;
                                                        mediaEntity.height = f;
                                                    } else if (width < 1.0f) {
                                                        float f2 = mediaEntity.width * width;
                                                        mediaEntity.x += (mediaEntity.width - f2) / 2.0f;
                                                        mediaEntity.width = f2;
                                                    }
                                                }
                                            }
                                        } else if (mediaEntity.type == 1) {
                                            EditTextOutline editTextOutline = new EditTextOutline(ApplicationLoader.applicationContext);
                                            editTextOutline.setBackgroundColor(0);
                                            editTextOutline.setPadding(AndroidUtilities.dp(7.0f), AndroidUtilities.dp(7.0f), AndroidUtilities.dp(7.0f), AndroidUtilities.dp(7.0f));
                                            editTextOutline.setTextSize(0, (float) mediaEntity.fontSize);
                                            editTextOutline.setText(mediaEntity.text);
                                            editTextOutline.setTextColor(mediaEntity.color);
                                            editTextOutline.setTypeface((Typeface) null, 1);
                                            editTextOutline.setGravity(17);
                                            editTextOutline.setHorizontallyScrolling(false);
                                            editTextOutline.setImeOptions(NUM);
                                            editTextOutline.setFocusableInTouchMode(true);
                                            editTextOutline.setInputType(editTextOutline.getInputType() | 16384);
                                            if (Build.VERSION.SDK_INT >= 23) {
                                                setBreakStrategy(editTextOutline);
                                            }
                                            if ((mediaEntity.subType & 1) != 0) {
                                                editTextOutline.setTextColor(-1);
                                                editTextOutline.setStrokeColor(mediaEntity.color);
                                                editTextOutline.setFrameColor(0);
                                                editTextOutline.setShadowLayer(0.0f, 0.0f, 0.0f, 0);
                                            } else if ((mediaEntity.subType & 4) != 0) {
                                                editTextOutline.setTextColor(-16777216);
                                                editTextOutline.setStrokeColor(0);
                                                editTextOutline.setFrameColor(mediaEntity.color);
                                                editTextOutline.setShadowLayer(0.0f, 0.0f, 0.0f, 0);
                                            } else {
                                                editTextOutline.setTextColor(mediaEntity.color);
                                                editTextOutline.setStrokeColor(0);
                                                editTextOutline.setFrameColor(0);
                                                editTextOutline.setShadowLayer(5.0f, 0.0f, 1.0f, NUM);
                                            }
                                            editTextOutline.measure(View.MeasureSpec.makeMeasureSpec(mediaEntity.viewWidth, NUM), View.MeasureSpec.makeMeasureSpec(mediaEntity.viewHeight, NUM));
                                            editTextOutline.layout(0, 0, mediaEntity.viewWidth, mediaEntity.viewHeight);
                                            mediaEntity.bitmap = Bitmap.createBitmap(mediaEntity.viewWidth, mediaEntity.viewHeight, Bitmap.Config.ARGB_8888);
                                            editTextOutline.draw(new Canvas(mediaEntity.bitmap));
                                        }
                                    }
                                } catch (Throwable th2) {
                                    FileLog.e(th2);
                                }
                            }
                        } else {
                            throw new RuntimeException("Could not get attrib location for uSTMatrix");
                        }
                    } else {
                        throw new RuntimeException("Could not get attrib location for uMVPMatrix");
                    }
                } else {
                    throw new RuntimeException("Could not get attrib location for aTextureCoord");
                }
            } else {
                throw new RuntimeException("Could not get attrib location for aPosition");
            }
        } else {
            throw new RuntimeException("failed creating program");
        }
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
