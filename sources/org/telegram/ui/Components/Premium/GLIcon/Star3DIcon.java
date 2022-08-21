package org.telegram.ui.Components.Premium.GLIcon;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.LinearGradient;
import android.graphics.Paint;
import android.graphics.Shader;
import android.opengl.GLES20;
import android.opengl.GLUtils;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;
import java.nio.charset.StandardCharsets;
import org.telegram.messenger.R;
import org.telegram.messenger.SvgHelper;
import org.telegram.messenger.Utilities;
import org.telegram.ui.ActionBar.Theme;

public class Star3DIcon {
    private int alphaHandle;
    Bitmap backgroundBitmap;
    public float diffuse = 1.0f;
    int diffuseHandle;
    float enterAlpha = 0.0f;
    public int gradientColor1;
    int gradientColor1Handle;
    public int gradientColor2;
    int gradientColor2Handle;
    int gradientPositionHandle;
    private int mBackgroundTextureHandle;
    private int mBackgroundTextureUniformHandle;
    private int mMVPMatrixHandle;
    private int mNormalCoordinateHandle;
    private int mNormalMapUniformHandle;
    private FloatBuffer mNormals;
    private int mProgramObject;
    private int mTextureCoordinateHandle;
    private int mTextureDataHandle;
    private int mTextureUniformHandle;
    private FloatBuffer mTextures;
    private FloatBuffer mVertices;
    private int mWorldMatrixHandle;
    public float normalSpec = 0.2f;
    public int normalSpecColor = -1;
    int normalSpecColorHandle;
    int normalSpecHandle;
    int resolutionHandle;
    public float spec1 = 2.0f;
    public float spec2 = 0.13f;
    public int specColor = -1;
    int specColorHandle;
    int specHandleBottom;
    int specHandleTop;
    Bitmap texture;
    int trianglesCount;
    float xOffset;
    private int xOffsetHandle;

    public Star3DIcon(Context context) {
        ObjLoader objLoader = new ObjLoader(context, "models/star.binobj");
        FloatBuffer asFloatBuffer = ByteBuffer.allocateDirect(objLoader.positions.length * 4).order(ByteOrder.nativeOrder()).asFloatBuffer();
        this.mVertices = asFloatBuffer;
        asFloatBuffer.put(objLoader.positions).position(0);
        FloatBuffer asFloatBuffer2 = ByteBuffer.allocateDirect(objLoader.textureCoordinates.length * 4).order(ByteOrder.nativeOrder()).asFloatBuffer();
        this.mTextures = asFloatBuffer2;
        asFloatBuffer2.put(objLoader.textureCoordinates).position(0);
        FloatBuffer asFloatBuffer3 = ByteBuffer.allocateDirect(objLoader.normals.length * 4).order(ByteOrder.nativeOrder()).asFloatBuffer();
        this.mNormals = asFloatBuffer3;
        asFloatBuffer3.put(objLoader.normals).position(0);
        this.trianglesCount = objLoader.positions.length;
        generateTexture();
        int loadShader = GLIconRenderer.loadShader(35633, loadFromAsset(context, "shaders/vertex2.glsl"));
        int loadShader2 = GLIconRenderer.loadShader(35632, loadFromAsset(context, "shaders/fragment2.glsl"));
        int glCreateProgram = GLES20.glCreateProgram();
        GLES20.glAttachShader(glCreateProgram, loadShader);
        GLES20.glAttachShader(glCreateProgram, loadShader2);
        GLES20.glBindAttribLocation(glCreateProgram, 0, "vPosition");
        GLES20.glLinkProgram(glCreateProgram);
        GLES20.glGetProgramiv(glCreateProgram, 35714, new int[1], 0);
        this.mProgramObject = glCreateProgram;
        init(context);
    }

    private void init(Context context) {
        GLES20.glUseProgram(this.mProgramObject);
        this.mTextureCoordinateHandle = GLES20.glGetAttribLocation(this.mProgramObject, "a_TexCoordinate");
        this.mNormalCoordinateHandle = GLES20.glGetAttribLocation(this.mProgramObject, "a_Normal");
        this.mTextureUniformHandle = GLES20.glGetUniformLocation(this.mProgramObject, "u_Texture");
        this.mNormalMapUniformHandle = GLES20.glGetUniformLocation(this.mProgramObject, "u_NormalMap");
        this.mBackgroundTextureUniformHandle = GLES20.glGetUniformLocation(this.mProgramObject, "u_BackgroundTexture");
        this.xOffsetHandle = GLES20.glGetUniformLocation(this.mProgramObject, "f_xOffset");
        this.alphaHandle = GLES20.glGetUniformLocation(this.mProgramObject, "f_alpha");
        this.mMVPMatrixHandle = GLES20.glGetUniformLocation(this.mProgramObject, "uMVPMatrix");
        this.mWorldMatrixHandle = GLES20.glGetUniformLocation(this.mProgramObject, "world");
        this.specHandleTop = GLES20.glGetUniformLocation(this.mProgramObject, "spec1");
        this.specHandleBottom = GLES20.glGetUniformLocation(this.mProgramObject, "spec2");
        this.diffuseHandle = GLES20.glGetUniformLocation(this.mProgramObject, "u_diffuse");
        this.gradientColor1Handle = GLES20.glGetUniformLocation(this.mProgramObject, "gradientColor1");
        this.gradientColor2Handle = GLES20.glGetUniformLocation(this.mProgramObject, "gradientColor2");
        this.normalSpecColorHandle = GLES20.glGetUniformLocation(this.mProgramObject, "normalSpecColor");
        this.normalSpecHandle = GLES20.glGetUniformLocation(this.mProgramObject, "normalSpec");
        this.specColorHandle = GLES20.glGetUniformLocation(this.mProgramObject, "specColor");
        this.resolutionHandle = GLES20.glGetUniformLocation(this.mProgramObject, "resolution");
        this.gradientPositionHandle = GLES20.glGetUniformLocation(this.mProgramObject, "gradientPosition");
        this.mTextures.position(0);
        GLES20.glVertexAttribPointer(this.mTextureCoordinateHandle, 2, 5126, false, 0, this.mTextures);
        GLES20.glEnableVertexAttribArray(this.mTextureCoordinateHandle);
        this.mNormals.position(0);
        GLES20.glVertexAttribPointer(this.mNormalCoordinateHandle, 3, 5126, false, 0, this.mNormals);
        GLES20.glEnableVertexAttribArray(this.mNormalCoordinateHandle);
        this.mVertices.position(0);
        GLES20.glVertexAttribPointer(0, 3, 5126, false, 0, this.mVertices);
        GLES20.glEnableVertexAttribArray(0);
        Bitmap bitmap = SvgHelper.getBitmap(R.raw.start_texture, 80, 80, -1);
        Utilities.stackBlurBitmap(bitmap, 3);
        int[] iArr = new int[1];
        GLES20.glGenTextures(1, iArr, 0);
        GLES20.glBindTexture(3553, iArr[0]);
        GLES20.glTexParameteri(3553, 10241, 9729);
        GLES20.glTexParameteri(3553, 10240, 9729);
        GLUtils.texImage2D(3553, 0, bitmap, 0);
        bitmap.recycle();
        int[] iArr2 = new int[1];
        GLES20.glGenTextures(1, iArr2, 0);
        this.mTextureDataHandle = iArr2[0];
        GLES20.glBindTexture(3553, iArr2[0]);
        GLES20.glTexParameteri(3553, 10241, 9729);
        GLES20.glTexParameteri(3553, 10240, 9729);
        GLES20.glBindTexture(3553, this.mTextureDataHandle);
        Bitmap bitmapFromAsset = getBitmapFromAsset(context, "flecks.png");
        int[] iArr3 = new int[1];
        GLES20.glGenTextures(1, iArr3, 0);
        GLES20.glBindTexture(3553, iArr3[0]);
        GLES20.glTexParameteri(3553, 10241, 9729);
        GLES20.glTexParameteri(3553, 10240, 9729);
        GLUtils.texImage2D(3553, 0, bitmapFromAsset, 0);
        bitmapFromAsset.recycle();
        int[] iArr4 = new int[1];
        GLES20.glGenTextures(1, iArr4, 0);
        this.mBackgroundTextureHandle = iArr4[0];
        GLES20.glBindTexture(3553, iArr4[0]);
        GLES20.glTexParameteri(3553, 10241, 9729);
        GLES20.glTexParameteri(3553, 10240, 9729);
        GLES20.glBindTexture(3553, this.mBackgroundTextureHandle);
        GLES20.glActiveTexture(33984);
        GLES20.glBindTexture(3553, iArr[0]);
        GLES20.glUniform1i(this.mTextureUniformHandle, 0);
        GLES20.glActiveTexture(33985);
        GLES20.glBindTexture(3553, iArr3[0]);
        GLES20.glUniform1i(this.mNormalMapUniformHandle, 1);
        GLES20.glActiveTexture(33986);
        GLES20.glBindTexture(3553, iArr4[0]);
        GLES20.glUniform1i(this.mBackgroundTextureUniformHandle, 2);
    }

    private void generateTexture() {
        this.texture = Bitmap.createBitmap(100, 100, Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(this.texture);
        Paint paint = new Paint();
        paint.setShader(new LinearGradient(0.0f, 100.0f, 150.0f, 0.0f, new int[]{Theme.getColor("premiumGradient1"), Theme.getColor("premiumGradient2"), Theme.getColor("premiumGradient3"), Theme.getColor("premiumGradient4")}, new float[]{0.0f, 0.5f, 0.78f, 1.0f}, Shader.TileMode.CLAMP));
        canvas.drawRect(0.0f, 0.0f, 100.0f, 100.0f, paint);
        int[] iArr = new int[1];
        GLES20.glGenTextures(1, iArr, 0);
        GLES20.glBindTexture(3553, iArr[0]);
        GLES20.glTexParameteri(3553, 10241, 9728);
        GLES20.glTexParameteri(3553, 10240, 9728);
        GLUtils.texImage2D(3553, 0, this.texture, 0);
        this.mTextureDataHandle = iArr[0];
    }

    public void draw(float[] fArr, float[] fArr2, int i, int i2, float f, float f2, float f3, float f4) {
        if (this.backgroundBitmap != null) {
            GLES20.glBindTexture(3553, this.mBackgroundTextureHandle);
            GLUtils.texImage2D(3553, 0, this.backgroundBitmap, 0);
            this.backgroundBitmap = null;
        }
        GLES20.glUniform1i(this.mTextureUniformHandle, 0);
        GLES20.glUniform1f(this.xOffsetHandle, this.xOffset);
        GLES20.glUniform1f(this.alphaHandle, this.enterAlpha);
        GLES20.glUniformMatrix4fv(this.mMVPMatrixHandle, 1, false, fArr, 0);
        GLES20.glUniformMatrix4fv(this.mWorldMatrixHandle, 1, false, fArr2, 0);
        GLES20.glDrawArrays(4, 0, this.trianglesCount / 3);
        GLES20.glUniform1f(this.specHandleTop, this.spec1);
        GLES20.glUniform1f(this.specHandleBottom, this.spec2);
        GLES20.glUniform1f(this.diffuseHandle, this.diffuse);
        GLES20.glUniform1f(this.normalSpecHandle, this.normalSpec);
        GLES20.glUniform3f(this.gradientColor1Handle, ((float) Color.red(this.gradientColor1)) / 255.0f, ((float) Color.green(this.gradientColor1)) / 255.0f, ((float) Color.blue(this.gradientColor1)) / 255.0f);
        GLES20.glUniform3f(this.gradientColor2Handle, ((float) Color.red(this.gradientColor2)) / 255.0f, ((float) Color.green(this.gradientColor2)) / 255.0f, ((float) Color.blue(this.gradientColor2)) / 255.0f);
        GLES20.glUniform3f(this.normalSpecColorHandle, ((float) Color.red(this.normalSpecColor)) / 255.0f, ((float) Color.green(this.normalSpecColor)) / 255.0f, ((float) Color.blue(this.normalSpecColor)) / 255.0f);
        GLES20.glUniform3f(this.specColorHandle, ((float) Color.red(this.specColor)) / 255.0f, ((float) Color.green(this.specColor)) / 255.0f, ((float) Color.blue(this.specColor)) / 255.0f);
        GLES20.glUniform2f(this.resolutionHandle, (float) i, (float) i2);
        GLES20.glUniform4f(this.gradientPositionHandle, f, f2, f3, f4);
        float f5 = this.enterAlpha;
        if (f5 < 1.0f) {
            float f6 = f5 + 0.07272727f;
            this.enterAlpha = f6;
            if (f6 > 1.0f) {
                this.enterAlpha = 1.0f;
            }
        }
        float f7 = this.xOffset + 5.0E-4f;
        this.xOffset = f7;
        if (f7 > 1.0f) {
            this.xOffset = f7 - 1.0f;
        }
    }

    public String loadFromAsset(Context context, String str) {
        StringBuilder sb = new StringBuilder();
        try {
            InputStream open = context.getAssets().open(str);
            BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(open, StandardCharsets.UTF_8));
            while (true) {
                String readLine = bufferedReader.readLine();
                if (readLine == null) {
                    break;
                } else if (!readLine.startsWith("//")) {
                    sb.append(readLine);
                }
            }
            bufferedReader.close();
            open.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return sb.toString();
    }

    public static Bitmap getBitmapFromAsset(Context context, String str) {
        try {
            return BitmapFactory.decodeStream(context.getAssets().open(str));
        } catch (IOException unused) {
            return null;
        }
    }

    public void setBackground(Bitmap bitmap) {
        this.backgroundBitmap = bitmap;
    }

    public void destroy() {
        GLES20.glDeleteProgram(this.mProgramObject);
    }
}
