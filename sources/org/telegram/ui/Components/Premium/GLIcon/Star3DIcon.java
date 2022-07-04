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
    public float spec1 = 0.0f;
    public float spec2 = 0.0f;
    public int specColor = -1;
    int specColorHandle;
    int specHandleBottom;
    int specHandleTop;
    Bitmap texture;
    int trianglesCount;
    float xOffset;
    private int xOffsetHandle;

    public Star3DIcon(Context context) {
        ObjLoader starObj = new ObjLoader(context, "models/star.binobj");
        FloatBuffer asFloatBuffer = ByteBuffer.allocateDirect(starObj.positions.length * 4).order(ByteOrder.nativeOrder()).asFloatBuffer();
        this.mVertices = asFloatBuffer;
        asFloatBuffer.put(starObj.positions).position(0);
        FloatBuffer asFloatBuffer2 = ByteBuffer.allocateDirect(starObj.textureCoordinates.length * 4).order(ByteOrder.nativeOrder()).asFloatBuffer();
        this.mTextures = asFloatBuffer2;
        asFloatBuffer2.put(starObj.textureCoordinates).position(0);
        FloatBuffer asFloatBuffer3 = ByteBuffer.allocateDirect(starObj.normals.length * 4).order(ByteOrder.nativeOrder()).asFloatBuffer();
        this.mNormals = asFloatBuffer3;
        asFloatBuffer3.put(starObj.normals).position(0);
        this.trianglesCount = starObj.positions.length;
        generateTexture();
        int vertexShader = GLIconRenderer.loadShader(35633, loadFromAsset(context, "shaders/vertex2.glsl"));
        int fragmentShader = GLIconRenderer.loadShader(35632, loadFromAsset(context, "shaders/fragment2.glsl"));
        int programObject = GLES20.glCreateProgram();
        GLES20.glAttachShader(programObject, vertexShader);
        GLES20.glAttachShader(programObject, fragmentShader);
        GLES20.glBindAttribLocation(programObject, 0, "vPosition");
        GLES20.glLinkProgram(programObject);
        GLES20.glGetProgramiv(programObject, 35714, new int[1], 0);
        this.mProgramObject = programObject;
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
        Bitmap bitmap = SvgHelper.getBitmap(NUM, 80, 80, -1);
        Utilities.stackBlurBitmap(bitmap, 3);
        int[] texture2 = new int[1];
        GLES20.glGenTextures(1, texture2, 0);
        GLES20.glBindTexture(3553, texture2[0]);
        GLES20.glTexParameteri(3553, 10241, 9729);
        GLES20.glTexParameteri(3553, 10240, 9729);
        GLUtils.texImage2D(3553, 0, bitmap, 0);
        bitmap.recycle();
        int[] textureDatHandle = new int[1];
        GLES20.glGenTextures(1, textureDatHandle, 0);
        this.mTextureDataHandle = textureDatHandle[0];
        GLES20.glBindTexture(3553, textureDatHandle[0]);
        GLES20.glTexParameteri(3553, 10241, 9729);
        GLES20.glTexParameteri(3553, 10240, 9729);
        GLES20.glBindTexture(3553, this.mTextureDataHandle);
        Bitmap bitmap1 = getBitmapFromAsset(context, "flecks.png");
        int[] normalMap = new int[1];
        GLES20.glGenTextures(1, normalMap, 0);
        GLES20.glBindTexture(3553, normalMap[0]);
        GLES20.glTexParameteri(3553, 10241, 9729);
        GLES20.glTexParameteri(3553, 10240, 9729);
        GLUtils.texImage2D(3553, 0, bitmap1, 0);
        bitmap1.recycle();
        int[] backgroundBitmapHandel = new int[1];
        GLES20.glGenTextures(1, backgroundBitmapHandel, 0);
        this.mBackgroundTextureHandle = backgroundBitmapHandel[0];
        GLES20.glBindTexture(3553, backgroundBitmapHandel[0]);
        GLES20.glTexParameteri(3553, 10241, 9729);
        GLES20.glTexParameteri(3553, 10240, 9729);
        GLES20.glBindTexture(3553, this.mBackgroundTextureHandle);
        GLES20.glActiveTexture(33984);
        GLES20.glBindTexture(3553, texture2[0]);
        GLES20.glUniform1i(this.mTextureUniformHandle, 0);
        GLES20.glActiveTexture(33985);
        GLES20.glBindTexture(3553, normalMap[0]);
        GLES20.glUniform1i(this.mNormalMapUniformHandle, 1);
        GLES20.glActiveTexture(33986);
        GLES20.glBindTexture(3553, backgroundBitmapHandel[0]);
        GLES20.glUniform1i(this.mBackgroundTextureUniformHandle, 2);
    }

    private void generateTexture() {
        this.texture = Bitmap.createBitmap(100, 100, Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(this.texture);
        Paint paint = new Paint();
        paint.setShader(new LinearGradient(0.0f, 100.0f, 150.0f, 0.0f, new int[]{Theme.getColor("premiumGradient1"), Theme.getColor("premiumGradient2"), Theme.getColor("premiumGradient3"), Theme.getColor("premiumGradient4")}, new float[]{0.0f, 0.5f, 0.78f, 1.0f}, Shader.TileMode.CLAMP));
        canvas.drawRect(0.0f, 0.0f, 100.0f, 100.0f, paint);
        int[] textureHandle = new int[1];
        GLES20.glGenTextures(1, textureHandle, 0);
        GLES20.glBindTexture(3553, textureHandle[0]);
        GLES20.glTexParameteri(3553, 10241, 9728);
        GLES20.glTexParameteri(3553, 10240, 9728);
        GLUtils.texImage2D(3553, 0, this.texture, 0);
        this.mTextureDataHandle = textureHandle[0];
    }

    public void draw(float[] mvpMatrix, float[] worldMatrix, int width, int height, float gradientStartX, float gradientScaleX, float gradientStartY, float gradientScaleY) {
        if (this.backgroundBitmap != null) {
            GLES20.glBindTexture(3553, this.mBackgroundTextureHandle);
            GLUtils.texImage2D(3553, 0, this.backgroundBitmap, 0);
            this.backgroundBitmap = null;
        }
        GLES20.glUniform1i(this.mTextureUniformHandle, 0);
        GLES20.glUniform1f(this.xOffsetHandle, this.xOffset);
        GLES20.glUniform1f(this.alphaHandle, this.enterAlpha);
        GLES20.glUniformMatrix4fv(this.mMVPMatrixHandle, 1, false, mvpMatrix, 0);
        GLES20.glUniformMatrix4fv(this.mWorldMatrixHandle, 1, false, worldMatrix, 0);
        GLES20.glDrawArrays(4, 0, this.trianglesCount / 3);
        GLES20.glUniform1f(this.specHandleTop, this.spec1);
        GLES20.glUniform1f(this.specHandleBottom, this.spec2);
        GLES20.glUniform1f(this.diffuseHandle, this.diffuse);
        GLES20.glUniform1f(this.normalSpecHandle, this.normalSpec);
        GLES20.glUniform3f(this.gradientColor1Handle, ((float) Color.red(this.gradientColor1)) / 255.0f, ((float) Color.green(this.gradientColor1)) / 255.0f, ((float) Color.blue(this.gradientColor1)) / 255.0f);
        GLES20.glUniform3f(this.gradientColor2Handle, ((float) Color.red(this.gradientColor2)) / 255.0f, ((float) Color.green(this.gradientColor2)) / 255.0f, ((float) Color.blue(this.gradientColor2)) / 255.0f);
        GLES20.glUniform3f(this.normalSpecColorHandle, ((float) Color.red(this.normalSpecColor)) / 255.0f, ((float) Color.green(this.normalSpecColor)) / 255.0f, ((float) Color.blue(this.normalSpecColor)) / 255.0f);
        GLES20.glUniform3f(this.specColorHandle, ((float) Color.red(this.specColor)) / 255.0f, ((float) Color.green(this.specColor)) / 255.0f, ((float) Color.blue(this.specColor)) / 255.0f);
        GLES20.glUniform2f(this.resolutionHandle, (float) width, (float) height);
        GLES20.glUniform4f(this.gradientPositionHandle, gradientStartX, gradientScaleX, gradientStartY, gradientScaleY);
        float f = this.enterAlpha;
        if (f < 1.0f) {
            float f2 = f + 0.07272727f;
            this.enterAlpha = f2;
            if (f2 > 1.0f) {
                this.enterAlpha = 1.0f;
            }
        }
        float f3 = this.xOffset + 5.0E-4f;
        this.xOffset = f3;
        if (f3 > 1.0f) {
            this.xOffset = f3 - 1.0f;
        }
    }

    public String loadFromAsset(Context context, String name) {
        StringBuilder sb = new StringBuilder();
        try {
            InputStream is = context.getAssets().open(name);
            BufferedReader br = new BufferedReader(new InputStreamReader(is, StandardCharsets.UTF_8));
            while (true) {
                String readLine = br.readLine();
                String str = readLine;
                if (readLine == null) {
                    break;
                } else if (!str.startsWith("//")) {
                    sb.append(str);
                }
            }
            br.close();
            is.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return sb.toString();
    }

    public static Bitmap getBitmapFromAsset(Context context, String filePath) {
        try {
            return BitmapFactory.decodeStream(context.getAssets().open(filePath));
        } catch (IOException e) {
            return null;
        }
    }

    public void setBackground(Bitmap gradientTextureBitmap) {
        this.backgroundBitmap = gradientTextureBitmap;
    }

    public void destroy() {
        GLES20.glDeleteProgram(this.mProgramObject);
    }
}
