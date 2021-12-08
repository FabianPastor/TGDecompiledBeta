package org.telegram.messenger.video;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Paint;
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
import java.nio.channels.FileChannel;
import java.util.ArrayList;
import org.telegram.messenger.AndroidUtilities;
import org.telegram.messenger.ApplicationLoader;
import org.telegram.messenger.Bitmaps;
import org.telegram.messenger.BuildVars;
import org.telegram.messenger.FileLog;
import org.telegram.messenger.MediaController;
import org.telegram.messenger.Utilities;
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

    public TextureRenderer(MediaController.SavedFilterState savedFilterState, String image, String paint, ArrayList<VideoEditedInfo.MediaEntity> entities, MediaController.CropState cropState, int w, int h, int rotation, float fps, boolean photo) {
        int count;
        float[] textureData;
        MediaController.CropState cropState2 = cropState;
        int i = w;
        int i2 = h;
        float f = fps;
        this.isPhoto = photo;
        float[] texData = {0.0f, 0.0f, 1.0f, 0.0f, 0.0f, 1.0f, 1.0f, 1.0f};
        if (BuildVars.LOGS_ENABLED) {
            FileLog.d("start textureRenderer w = " + i + " h = " + i2 + " r = " + rotation + " fps = " + f);
            if (cropState2 != null) {
                FileLog.d("cropState px = " + cropState2.cropPx + " py = " + cropState2.cropPy + " cScale = " + cropState2.cropScale + " cropRotate = " + cropState2.cropRotate + " pw = " + cropState2.cropPw + " ph = " + cropState2.cropPh + " tw = " + cropState2.transformWidth + " th = " + cropState2.transformHeight + " tr = " + cropState2.transformRotation + " mirror = " + cropState2.mirrored);
            }
        } else {
            int i3 = rotation;
        }
        FloatBuffer asFloatBuffer = ByteBuffer.allocateDirect(texData.length * 4).order(ByteOrder.nativeOrder()).asFloatBuffer();
        this.textureBuffer = asFloatBuffer;
        asFloatBuffer.put(texData).position(0);
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
        this.originalWidth = i;
        this.transformedWidth = i;
        this.originalHeight = i2;
        this.transformedHeight = i2;
        this.imagePath = image;
        this.paintPath = paint;
        this.mediaEntities = entities;
        this.videoFps = f == 0.0f ? 30.0f : f;
        if (this.filterShaders != null) {
            count = 2;
        } else {
            count = 1;
        }
        this.mProgram = new int[count];
        this.muMVPMatrixHandle = new int[count];
        this.muSTMatrixHandle = new int[count];
        this.maPositionHandle = new int[count];
        this.maTextureHandle = new int[count];
        Matrix.setIdentityM(this.mMVPMatrix, 0);
        int textureRotation = 0;
        if (cropState2 != null) {
            float[] verticesData = {0.0f, 0.0f, (float) i, 0.0f, 0.0f, (float) i2, (float) i, (float) i2};
            int textureRotation2 = cropState2.transformRotation;
            if (textureRotation2 == 90 || textureRotation2 == 270) {
                int temp = this.originalWidth;
                this.originalWidth = this.originalHeight;
                this.originalHeight = temp;
            }
            this.transformedWidth = (int) (((float) this.transformedWidth) * cropState2.cropPw);
            this.transformedHeight = (int) (((float) this.transformedHeight) * cropState2.cropPh);
            int textureRotation3 = textureRotation2;
            double d = (double) (-cropState2.cropRotate);
            Double.isNaN(d);
            float angle = (float) (d * 0.017453292519943295d);
            int a = 0;
            while (a < 4) {
                float x1 = verticesData[a * 2] - ((float) (i / 2));
                float[] texData2 = texData;
                float y1 = verticesData[(a * 2) + 1] - ((float) (i2 / 2));
                double d2 = (double) x1;
                int count2 = count;
                double cos = Math.cos((double) angle);
                Double.isNaN(d2);
                double d3 = d2 * cos;
                double d4 = (double) y1;
                double sin = Math.sin((double) angle);
                Double.isNaN(d4);
                double d5 = d3 - (d4 * sin);
                double d6 = (double) (cropState2.cropPx * ((float) i));
                Double.isNaN(d6);
                float x2 = ((float) (d5 + d6)) * cropState2.cropScale;
                double d7 = (double) x1;
                double sin2 = Math.sin((double) angle);
                Double.isNaN(d7);
                double d8 = d7 * sin2;
                double d9 = (double) y1;
                float f2 = x1;
                double cos2 = Math.cos((double) angle);
                Double.isNaN(d9);
                double d10 = (double) (cropState2.cropPy * ((float) i2));
                Double.isNaN(d10);
                float y2 = ((float) ((d8 + (d9 * cos2)) - d10)) * cropState2.cropScale;
                verticesData[a * 2] = (x2 / ((float) this.transformedWidth)) * 2.0f;
                verticesData[(a * 2) + 1] = (y2 / ((float) this.transformedHeight)) * 2.0f;
                a++;
                String str = image;
                String str2 = paint;
                ArrayList<VideoEditedInfo.MediaEntity> arrayList = entities;
                int i4 = rotation;
                float f3 = fps;
                texData = texData2;
                count = count2;
            }
            int i5 = count;
            FloatBuffer asFloatBuffer3 = ByteBuffer.allocateDirect(verticesData.length * 4).order(ByteOrder.nativeOrder()).asFloatBuffer();
            this.verticesBuffer = asFloatBuffer3;
            asFloatBuffer3.put(verticesData).position(0);
            textureRotation = textureRotation3;
        } else {
            int i6 = count;
            float[] verticesData2 = {-1.0f, -1.0f, 1.0f, -1.0f, -1.0f, 1.0f, 1.0f, 1.0f};
            FloatBuffer asFloatBuffer4 = ByteBuffer.allocateDirect(verticesData2.length * 4).order(ByteOrder.nativeOrder()).asFloatBuffer();
            this.verticesBuffer = asFloatBuffer4;
            asFloatBuffer4.put(verticesData2).position(0);
        }
        if (this.filterShaders != null) {
            if (textureRotation == 90) {
                textureData = new float[]{1.0f, 1.0f, 1.0f, 0.0f, 0.0f, 1.0f, 0.0f, 0.0f};
            } else if (textureRotation == 180) {
                textureData = new float[]{1.0f, 0.0f, 0.0f, 0.0f, 1.0f, 1.0f, 0.0f, 1.0f};
            } else if (textureRotation == 270) {
                textureData = new float[]{0.0f, 0.0f, 0.0f, 1.0f, 1.0f, 0.0f, 1.0f, 1.0f};
            } else {
                textureData = new float[]{0.0f, 1.0f, 1.0f, 1.0f, 0.0f, 0.0f, 1.0f, 0.0f};
            }
        } else if (textureRotation == 90) {
            textureData = new float[]{1.0f, 0.0f, 1.0f, 1.0f, 0.0f, 0.0f, 0.0f, 1.0f};
        } else if (textureRotation == 180) {
            textureData = new float[]{1.0f, 1.0f, 0.0f, 1.0f, 1.0f, 0.0f, 0.0f, 0.0f};
        } else if (textureRotation == 270) {
            textureData = new float[]{0.0f, 1.0f, 0.0f, 0.0f, 1.0f, 1.0f, 1.0f, 0.0f};
        } else {
            textureData = new float[]{0.0f, 0.0f, 1.0f, 0.0f, 0.0f, 1.0f, 1.0f, 1.0f};
        }
        if (cropState2 != null && cropState2.mirrored) {
            for (int a2 = 0; a2 < 4; a2++) {
                if (textureData[a2 * 2] > 0.5f) {
                    textureData[a2 * 2] = 0.0f;
                } else {
                    textureData[a2 * 2] = 1.0f;
                }
            }
        }
        FloatBuffer asFloatBuffer5 = ByteBuffer.allocateDirect(textureData.length * 4).order(ByteOrder.nativeOrder()).asFloatBuffer();
        this.renderTextureBuffer = asFloatBuffer5;
        asFloatBuffer5.put(textureData).position(0);
    }

    public int getTextureId() {
        return this.mTextureID;
    }

    public void drawFrame(SurfaceTexture st) {
        int target;
        int index;
        int texture;
        float[] stMatrix;
        if (this.isPhoto) {
            GLES20.glUseProgram(this.simpleShaderProgram);
            GLES20.glActiveTexture(33984);
            GLES20.glUniform1i(this.simpleSourceImageHandle, 0);
            GLES20.glEnableVertexAttribArray(this.simpleInputTexCoordHandle);
            GLES20.glVertexAttribPointer(this.simpleInputTexCoordHandle, 2, 5126, false, 8, this.textureBuffer);
            GLES20.glEnableVertexAttribArray(this.simplePositionHandle);
            SurfaceTexture surfaceTexture = st;
        } else {
            st.getTransformMatrix(this.mSTMatrix);
            if (BuildVars.LOGS_ENABLED && this.firstFrame) {
                StringBuilder builder = new StringBuilder();
                int a = 0;
                while (true) {
                    float[] fArr = this.mSTMatrix;
                    if (a >= fArr.length) {
                        break;
                    }
                    builder.append(fArr[a]);
                    builder.append(", ");
                    a++;
                }
                FileLog.d("stMatrix = " + builder);
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
                boolean blurred = this.filterShaders.drawBlurPass();
                GLES20.glBindFramebuffer(36160, 0);
                int i = this.transformedWidth;
                if (!(i == this.originalWidth && this.transformedHeight == this.originalHeight)) {
                    GLES20.glViewport(0, 0, i, this.transformedHeight);
                }
                texture = this.filterShaders.getRenderTexture(blurred ^ true ? 1 : 0);
                index = 1;
                target = 3553;
                stMatrix = this.mSTMatrixIdentity;
            } else {
                texture = this.mTextureID;
                index = 0;
                target = 36197;
                stMatrix = this.mSTMatrix;
            }
            GLES20.glUseProgram(this.mProgram[index]);
            GLES20.glActiveTexture(33984);
            GLES20.glBindTexture(target, texture);
            GLES20.glVertexAttribPointer(this.maPositionHandle[index], 2, 5126, false, 8, this.verticesBuffer);
            GLES20.glEnableVertexAttribArray(this.maPositionHandle[index]);
            GLES20.glVertexAttribPointer(this.maTextureHandle[index], 2, 5126, false, 8, this.renderTextureBuffer);
            GLES20.glEnableVertexAttribArray(this.maTextureHandle[index]);
            GLES20.glUniformMatrix4fv(this.muSTMatrixHandle[index], 1, false, stMatrix, 0);
            GLES20.glUniformMatrix4fv(this.muMVPMatrixHandle[index], 1, false, this.mMVPMatrix, 0);
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
            int a2 = 0;
            while (true) {
                int[] iArr = this.paintTexture;
                if (a2 >= iArr.length) {
                    break;
                }
                drawTexture(true, iArr[a2]);
                a2++;
            }
        }
        if (this.stickerTexture != null) {
            int N = this.mediaEntities.size();
            for (int a3 = 0; a3 < N; a3++) {
                VideoEditedInfo.MediaEntity entity = this.mediaEntities.get(a3);
                if (entity.ptr != 0) {
                    long j = entity.ptr;
                    Bitmap bitmap = this.stickerBitmap;
                    RLottieDrawable.getFrame(j, (int) entity.currentFrame, bitmap, 512, 512, bitmap.getRowBytes(), true);
                    GLES20.glBindTexture(3553, this.stickerTexture[0]);
                    GLUtils.texImage2D(3553, 0, this.stickerBitmap, 0);
                    entity.currentFrame += entity.framesPerDraw;
                    if (entity.currentFrame >= ((float) entity.metadata[0])) {
                        entity.currentFrame = 0.0f;
                    }
                    drawTexture(false, this.stickerTexture[0], entity.x, entity.y, entity.width, entity.height, entity.rotation, (entity.subType & 2) != 0);
                } else if (entity.bitmap != null) {
                    GLES20.glBindTexture(3553, this.stickerTexture[0]);
                    GLUtils.texImage2D(3553, 0, entity.bitmap, 0);
                    drawTexture(false, this.stickerTexture[0], entity.x, entity.y, entity.width, entity.height, entity.rotation, (entity.subType & 2) != 0);
                }
            }
        }
        GLES20.glFinish();
    }

    private void drawTexture(boolean bind, int texture) {
        drawTexture(bind, texture, -10000.0f, -10000.0f, -10000.0f, -10000.0f, 0.0f, false);
    }

    private void drawTexture(boolean bind, int texture, float x, float y, float w, float h, float rotation, boolean mirror) {
        float h2;
        float w2;
        float y2;
        float f = rotation;
        if (!this.blendEnabled) {
            GLES20.glEnable(3042);
            GLES20.glBlendFunc(1, 771);
            this.blendEnabled = true;
        }
        if (x <= -10000.0f) {
            float[] fArr = this.bitmapData;
            fArr[0] = -1.0f;
            fArr[1] = 1.0f;
            fArr[2] = 1.0f;
            fArr[3] = 1.0f;
            fArr[4] = -1.0f;
            fArr[5] = -1.0f;
            fArr[6] = 1.0f;
            fArr[7] = -1.0f;
            float f2 = x;
            y2 = y;
            w2 = w;
            h2 = h;
        } else {
            float x2 = (x * 2.0f) - 1.0f;
            y2 = ((1.0f - y) * 2.0f) - 1.0f;
            w2 = w * 2.0f;
            h2 = h * 2.0f;
            float[] fArr2 = this.bitmapData;
            fArr2[0] = x2;
            fArr2[1] = y2;
            fArr2[2] = x2 + w2;
            fArr2[3] = y2;
            fArr2[4] = x2;
            fArr2[5] = y2 - h2;
            fArr2[6] = x2 + w2;
            fArr2[7] = y2 - h2;
        }
        float[] fArr3 = this.bitmapData;
        float mx = (fArr3[0] + fArr3[2]) / 2.0f;
        if (mirror) {
            float temp = fArr3[2];
            fArr3[2] = fArr3[0];
            fArr3[0] = temp;
            float temp2 = fArr3[6];
            fArr3[6] = fArr3[4];
            fArr3[4] = temp2;
        }
        if (f != 0.0f) {
            float ratio = ((float) this.transformedWidth) / ((float) this.transformedHeight);
            float my = (fArr3[5] + fArr3[1]) / 2.0f;
            int a = 0;
            for (int i = 4; a < i; i = 4) {
                float[] fArr4 = this.bitmapData;
                float x1 = fArr4[a * 2] - mx;
                float y1 = (fArr4[(a * 2) + 1] - my) / ratio;
                double d = (double) x1;
                int a2 = a;
                double cos = Math.cos((double) f);
                Double.isNaN(d);
                double d2 = d * cos;
                double d3 = (double) y1;
                double sin = Math.sin((double) f);
                Double.isNaN(d3);
                fArr4[a * 2] = ((float) (d2 - (d3 * sin))) + mx;
                double d4 = (double) x1;
                float w3 = w2;
                double sin2 = Math.sin((double) f);
                Double.isNaN(d4);
                double d5 = d4 * sin2;
                double d6 = (double) y1;
                double cos2 = Math.cos((double) f);
                Double.isNaN(d6);
                this.bitmapData[(a2 * 2) + 1] = (((float) (d5 + (d6 * cos2))) * ratio) + my;
                a = a2 + 1;
                y2 = y2;
                w2 = w3;
                h2 = h2;
            }
            int i2 = a;
            float f3 = y2;
            float f4 = w2;
            float f5 = h2;
        } else {
            float f6 = w2;
            float f7 = h2;
        }
        this.bitmapVerticesBuffer.put(this.bitmapData).position(0);
        GLES20.glVertexAttribPointer(this.simplePositionHandle, 2, 5126, false, 8, this.bitmapVerticesBuffer);
        if (bind) {
            GLES20.glBindTexture(3553, texture);
        } else {
            int i3 = texture;
        }
        GLES20.glDrawArrays(5, 0, 4);
    }

    public void setBreakStrategy(EditTextOutline editText) {
        editText.setBreakStrategy(0);
    }

    public void surfaceCreated() {
        String path;
        float scale;
        String str;
        int a = 0;
        while (true) {
            int[] iArr = this.mProgram;
            if (a >= iArr.length) {
                break;
            }
            iArr[a] = createProgram("uniform mat4 uMVPMatrix;\nuniform mat4 uSTMatrix;\nattribute vec4 aPosition;\nattribute vec4 aTextureCoord;\nvarying vec2 vTextureCoord;\nvoid main() {\n  gl_Position = uMVPMatrix * aPosition;\n  vTextureCoord = (uSTMatrix * aTextureCoord).xy;\n}\n", a == 0 ? "#extension GL_OES_EGL_image_external : require\nprecision highp float;\nvarying vec2 vTextureCoord;\nuniform samplerExternalOES sTexture;\nvoid main() {\n  gl_FragColor = texture2D(sTexture, vTextureCoord);\n}\n" : "precision highp float;\nvarying vec2 vTextureCoord;\nuniform sampler2D sTexture;\nvoid main() {\n  gl_FragColor = texture2D(sTexture, vTextureCoord);\n}\n");
            this.maPositionHandle[a] = GLES20.glGetAttribLocation(this.mProgram[a], "aPosition");
            this.maTextureHandle[a] = GLES20.glGetAttribLocation(this.mProgram[a], "aTextureCoord");
            this.muMVPMatrixHandle[a] = GLES20.glGetUniformLocation(this.mProgram[a], "uMVPMatrix");
            this.muSTMatrixHandle[a] = GLES20.glGetUniformLocation(this.mProgram[a], "uSTMatrix");
            a++;
        }
        int i = 1;
        int[] textures = new int[1];
        GLES20.glGenTextures(1, textures, 0);
        int i2 = textures[0];
        this.mTextureID = i2;
        GLES20.glBindTexture(36197, i2);
        GLES20.glTexParameteri(36197, 10241, 9729);
        GLES20.glTexParameteri(36197, 10240, 9729);
        GLES20.glTexParameteri(36197, 10242, 33071);
        GLES20.glTexParameteri(36197, 10243, 33071);
        if (!(this.filterShaders == null && this.imagePath == null && this.paintPath == null && this.mediaEntities == null)) {
            int vertexShader = FilterShaders.loadShader(35633, "attribute vec4 position;attribute vec2 inputTexCoord;varying vec2 texCoord;void main() {gl_Position = position;texCoord = inputTexCoord;}");
            int fragmentShader = FilterShaders.loadShader(35632, "varying highp vec2 texCoord;uniform sampler2D sourceImage;void main() {gl_FragColor = texture2D(sourceImage, texCoord);}");
            if (!(vertexShader == 0 || fragmentShader == 0)) {
                int glCreateProgram = GLES20.glCreateProgram();
                this.simpleShaderProgram = glCreateProgram;
                GLES20.glAttachShader(glCreateProgram, vertexShader);
                GLES20.glAttachShader(this.simpleShaderProgram, fragmentShader);
                GLES20.glBindAttribLocation(this.simpleShaderProgram, 0, "position");
                GLES20.glBindAttribLocation(this.simpleShaderProgram, 1, "inputTexCoord");
                GLES20.glLinkProgram(this.simpleShaderProgram);
                int[] linkStatus = new int[1];
                GLES20.glGetProgramiv(this.simpleShaderProgram, 35714, linkStatus, 0);
                if (linkStatus[0] == 0) {
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
            this.filterShaders.setRenderData((Bitmap) null, 0, this.mTextureID, this.originalWidth, this.originalHeight);
        }
        String str2 = this.imagePath;
        int i3 = -16777216;
        if (!(str2 == null && this.paintPath == null)) {
            int[] iArr2 = new int[((str2 != null ? 1 : 0) + (this.paintPath != null ? 1 : 0))];
            this.paintTexture = iArr2;
            GLES20.glGenTextures(iArr2.length, iArr2, 0);
            int a2 = 0;
            while (a2 < this.paintTexture.length) {
                int angle = 0;
                if (a2 != 0 || (str = this.imagePath) == null) {
                    try {
                        path = this.paintPath;
                    } catch (Throwable e) {
                        FileLog.e(e);
                    }
                } else {
                    path = str;
                    try {
                        switch (new ExifInterface(path).getAttributeInt("Orientation", i)) {
                            case 3:
                                angle = 180;
                                break;
                            case 6:
                                angle = 90;
                                break;
                            case 8:
                                angle = 270;
                                break;
                        }
                    } catch (Throwable th) {
                    }
                }
                Bitmap bitmap = BitmapFactory.decodeFile(path);
                if (bitmap != null) {
                    if (a2 == 0 && this.imagePath != null) {
                        Bitmap newBitmap = Bitmap.createBitmap(this.transformedWidth, this.transformedHeight, Bitmap.Config.ARGB_8888);
                        newBitmap.eraseColor(i3);
                        Canvas canvas = new Canvas(newBitmap);
                        if (angle != 90) {
                            if (angle != 270) {
                                scale = Math.max(((float) bitmap.getWidth()) / ((float) this.transformedWidth), ((float) bitmap.getHeight()) / ((float) this.transformedHeight));
                                android.graphics.Matrix matrix = new android.graphics.Matrix();
                                matrix.postTranslate((float) ((-bitmap.getWidth()) / 2), (float) ((-bitmap.getHeight()) / 2));
                                matrix.postScale(1.0f / scale, 1.0f / scale);
                                matrix.postRotate((float) angle);
                                matrix.postTranslate((float) (newBitmap.getWidth() / 2), (float) (newBitmap.getHeight() / 2));
                                canvas.drawBitmap(bitmap, matrix, new Paint(2));
                                bitmap = newBitmap;
                            }
                        }
                        scale = Math.max(((float) bitmap.getHeight()) / ((float) this.transformedWidth), ((float) bitmap.getWidth()) / ((float) this.transformedHeight));
                        android.graphics.Matrix matrix2 = new android.graphics.Matrix();
                        matrix2.postTranslate((float) ((-bitmap.getWidth()) / 2), (float) ((-bitmap.getHeight()) / 2));
                        matrix2.postScale(1.0f / scale, 1.0f / scale);
                        matrix2.postRotate((float) angle);
                        matrix2.postTranslate((float) (newBitmap.getWidth() / 2), (float) (newBitmap.getHeight() / 2));
                        canvas.drawBitmap(bitmap, matrix2, new Paint(2));
                        bitmap = newBitmap;
                    }
                    GLES20.glBindTexture(3553, this.paintTexture[a2]);
                    GLES20.glTexParameteri(3553, 10241, 9729);
                    GLES20.glTexParameteri(3553, 10240, 9729);
                    GLES20.glTexParameteri(3553, 10242, 33071);
                    GLES20.glTexParameteri(3553, 10243, 33071);
                    GLUtils.texImage2D(3553, 0, bitmap, 0);
                }
                a2++;
                i = 1;
                i3 = -16777216;
            }
        }
        if (this.mediaEntities != null) {
            try {
                this.stickerBitmap = Bitmap.createBitmap(512, 512, Bitmap.Config.ARGB_8888);
                int[] iArr3 = new int[1];
                this.stickerTexture = iArr3;
                GLES20.glGenTextures(1, iArr3, 0);
                GLES20.glBindTexture(3553, this.stickerTexture[0]);
                GLES20.glTexParameteri(3553, 10241, 9729);
                GLES20.glTexParameteri(3553, 10240, 9729);
                GLES20.glTexParameteri(3553, 10242, 33071);
                GLES20.glTexParameteri(3553, 10243, 33071);
                int N = this.mediaEntities.size();
                for (int a3 = 0; a3 < N; a3++) {
                    VideoEditedInfo.MediaEntity entity = this.mediaEntities.get(a3);
                    if (entity.type == 0) {
                        if ((entity.subType & 1) != 0) {
                            entity.metadata = new int[3];
                            entity.ptr = RLottieDrawable.create(entity.text, (String) null, 512, 512, entity.metadata, false, (int[]) null, false, 0);
                            entity.framesPerDraw = ((float) entity.metadata[1]) / this.videoFps;
                        } else {
                            if (Build.VERSION.SDK_INT >= 19) {
                                entity.bitmap = BitmapFactory.decodeFile(entity.text);
                            } else {
                                File path2 = new File(entity.text);
                                RandomAccessFile file = new RandomAccessFile(path2, "r");
                                ByteBuffer buffer = file.getChannel().map(FileChannel.MapMode.READ_ONLY, 0, path2.length());
                                BitmapFactory.Options bmOptions = new BitmapFactory.Options();
                                bmOptions.inJustDecodeBounds = true;
                                Utilities.loadWebpImage((Bitmap) null, buffer, buffer.limit(), bmOptions, true);
                                entity.bitmap = Bitmaps.createBitmap(bmOptions.outWidth, bmOptions.outHeight, Bitmap.Config.ARGB_8888);
                                Utilities.loadWebpImage(entity.bitmap, buffer, buffer.limit(), (BitmapFactory.Options) null, true);
                                file.close();
                            }
                            if (entity.bitmap != null) {
                                float aspect = ((float) entity.bitmap.getWidth()) / ((float) entity.bitmap.getHeight());
                                if (aspect > 1.0f) {
                                    float h = entity.height / aspect;
                                    entity.y += (entity.height - h) / 2.0f;
                                    entity.height = h;
                                } else if (aspect < 1.0f) {
                                    float w = entity.width * aspect;
                                    entity.x += (entity.width - w) / 2.0f;
                                    entity.width = w;
                                }
                            }
                        }
                    } else if (entity.type == 1) {
                        EditTextOutline editText = new EditTextOutline(ApplicationLoader.applicationContext);
                        editText.setBackgroundColor(0);
                        editText.setPadding(AndroidUtilities.dp(7.0f), AndroidUtilities.dp(7.0f), AndroidUtilities.dp(7.0f), AndroidUtilities.dp(7.0f));
                        editText.setTextSize(0, (float) entity.fontSize);
                        editText.setText(entity.text);
                        editText.setTextColor(entity.color);
                        editText.setTypeface((Typeface) null, 1);
                        editText.setGravity(17);
                        editText.setHorizontallyScrolling(false);
                        editText.setImeOptions(NUM);
                        editText.setFocusableInTouchMode(true);
                        editText.setInputType(editText.getInputType() | 16384);
                        if (Build.VERSION.SDK_INT >= 23) {
                            setBreakStrategy(editText);
                        }
                        if ((entity.subType & 1) != 0) {
                            editText.setTextColor(-1);
                            editText.setStrokeColor(entity.color);
                            editText.setFrameColor(0);
                            editText.setShadowLayer(0.0f, 0.0f, 0.0f, 0);
                        } else if ((entity.subType & 4) != 0) {
                            editText.setTextColor(-16777216);
                            editText.setStrokeColor(0);
                            editText.setFrameColor(entity.color);
                            editText.setShadowLayer(0.0f, 0.0f, 0.0f, 0);
                        } else {
                            editText.setTextColor(entity.color);
                            editText.setStrokeColor(0);
                            editText.setFrameColor(0);
                            editText.setShadowLayer(5.0f, 0.0f, 1.0f, NUM);
                        }
                        editText.measure(View.MeasureSpec.makeMeasureSpec(entity.viewWidth, NUM), View.MeasureSpec.makeMeasureSpec(entity.viewHeight, NUM));
                        editText.layout(0, 0, entity.viewWidth, entity.viewHeight);
                        entity.bitmap = Bitmap.createBitmap(entity.viewWidth, entity.viewHeight, Bitmap.Config.ARGB_8888);
                        editText.draw(new Canvas(entity.bitmap));
                    }
                }
            } catch (Throwable e2) {
                FileLog.e(e2);
            }
        }
    }

    private int createProgram(String vertexSource, String fragmentSource) {
        int pixelShader;
        int program;
        int vertexShader = FilterShaders.loadShader(35633, vertexSource);
        if (vertexShader == 0 || (pixelShader = FilterShaders.loadShader(35632, fragmentSource)) == 0 || (program = GLES20.glCreateProgram()) == 0) {
            return 0;
        }
        GLES20.glAttachShader(program, vertexShader);
        GLES20.glAttachShader(program, pixelShader);
        GLES20.glLinkProgram(program);
        int[] linkStatus = new int[1];
        GLES20.glGetProgramiv(program, 35714, linkStatus, 0);
        if (linkStatus[0] == 1) {
            return program;
        }
        GLES20.glDeleteProgram(program);
        return 0;
    }

    public void release() {
        ArrayList<VideoEditedInfo.MediaEntity> arrayList = this.mediaEntities;
        if (arrayList != null) {
            int N = arrayList.size();
            for (int a = 0; a < N; a++) {
                VideoEditedInfo.MediaEntity entity = this.mediaEntities.get(a);
                if (entity.ptr != 0) {
                    RLottieDrawable.destroy(entity.ptr);
                }
            }
        }
    }

    public void changeFragmentShader(String fragmentShader) {
        GLES20.glDeleteProgram(this.mProgram[0]);
        this.mProgram[0] = createProgram("uniform mat4 uMVPMatrix;\nuniform mat4 uSTMatrix;\nattribute vec4 aPosition;\nattribute vec4 aTextureCoord;\nvarying vec2 vTextureCoord;\nvoid main() {\n  gl_Position = uMVPMatrix * aPosition;\n  vTextureCoord = (uSTMatrix * aTextureCoord).xy;\n}\n", fragmentShader);
    }
}
