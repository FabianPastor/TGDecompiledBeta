package org.telegram.ui.Components;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.ColorFilter;
import android.graphics.Matrix;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffColorFilter;
import android.graphics.SurfaceTexture;
import android.graphics.drawable.Drawable;
import android.opengl.GLES20;
import android.opengl.GLUtils;
import android.os.Build;
import android.os.Looper;
import android.view.MotionEvent;
import android.view.TextureView;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.recyclerview.widget.RecyclerView;
import java.nio.Buffer;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;
import java.util.ArrayList;
import java.util.concurrent.CountDownLatch;
import javax.microedition.khronos.egl.EGL10;
import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.egl.EGLContext;
import javax.microedition.khronos.egl.EGLDisplay;
import javax.microedition.khronos.egl.EGLSurface;
import org.telegram.messenger.AndroidUtilities;
import org.telegram.messenger.BuildVars;
import org.telegram.messenger.DispatchQueue;
import org.telegram.messenger.FileLog;
import org.telegram.messenger.LocaleController;
import org.telegram.messenger.MediaController;
import org.telegram.messenger.Utilities;
import org.telegram.ui.ActionBar.Theme;
import org.telegram.ui.Cells.PhotoEditRadioCell;
import org.telegram.ui.Cells.PhotoEditToolCell;
import org.telegram.ui.Components.PhotoFilterView;
import org.telegram.ui.Components.RecyclerListView;

@SuppressLint({"NewApi"})
public class PhotoFilterView extends FrameLayout {
    /* access modifiers changed from: private */
    public Bitmap bitmapToEdit;
    /* access modifiers changed from: private */
    public float blurAngle;
    private PhotoFilterBlurControl blurControl;
    /* access modifiers changed from: private */
    public float blurExcludeBlurSize;
    /* access modifiers changed from: private */
    public Point blurExcludePoint;
    /* access modifiers changed from: private */
    public float blurExcludeSize;
    private ImageView blurItem;
    private FrameLayout blurLayout;
    private TextView blurLinearButton;
    private TextView blurOffButton;
    private TextView blurRadialButton;
    /* access modifiers changed from: private */
    public int blurType;
    private TextView cancelTextView;
    /* access modifiers changed from: private */
    public int contrastTool = 2;
    /* access modifiers changed from: private */
    public float contrastValue;
    private ImageView curveItem;
    private FrameLayout curveLayout;
    private RadioButton[] curveRadioButton = new RadioButton[4];
    private PhotoFilterCurvesControl curvesControl;
    /* access modifiers changed from: private */
    public CurvesToolValue curvesToolValue;
    private TextView doneTextView;
    /* access modifiers changed from: private */
    public EGLThread eglThread;
    /* access modifiers changed from: private */
    public int enhanceTool = 0;
    /* access modifiers changed from: private */
    public float enhanceValue;
    /* access modifiers changed from: private */
    public int exposureTool = 1;
    /* access modifiers changed from: private */
    public float exposureValue;
    /* access modifiers changed from: private */
    public int fadeTool = 5;
    /* access modifiers changed from: private */
    public float fadeValue;
    /* access modifiers changed from: private */
    public int grainTool = 9;
    /* access modifiers changed from: private */
    public float grainValue;
    /* access modifiers changed from: private */
    public int highlightsTool = 6;
    /* access modifiers changed from: private */
    public float highlightsValue;
    private MediaController.SavedFilterState lastState;
    /* access modifiers changed from: private */
    public int orientation;
    private RecyclerListView recyclerListView;
    /* access modifiers changed from: private */
    public int saturationTool = 3;
    /* access modifiers changed from: private */
    public float saturationValue;
    private int selectedTool;
    /* access modifiers changed from: private */
    public int shadowsTool = 7;
    /* access modifiers changed from: private */
    public float shadowsValue;
    /* access modifiers changed from: private */
    public int sharpenTool = 10;
    /* access modifiers changed from: private */
    public float sharpenValue;
    /* access modifiers changed from: private */
    public boolean showOriginal;
    private TextureView textureView;
    /* access modifiers changed from: private */
    public int tintHighlightsColor;
    /* access modifiers changed from: private */
    public int tintHighlightsTool = 12;
    /* access modifiers changed from: private */
    public int tintShadowsColor;
    /* access modifiers changed from: private */
    public int tintShadowsTool = 11;
    private FrameLayout toolsView;
    private ImageView tuneItem;
    /* access modifiers changed from: private */
    public int vignetteTool = 8;
    /* access modifiers changed from: private */
    public float vignetteValue;
    /* access modifiers changed from: private */
    public int warmthTool = 4;
    /* access modifiers changed from: private */
    public float warmthValue;

    public static class CurvesValue {
        public float blacksLevel = 0.0f;
        public float[] cachedDataPoints;
        public float highlightsLevel = 75.0f;
        public float midtonesLevel = 50.0f;
        public float shadowsLevel = 25.0f;
        public float whitesLevel = 100.0f;

        public float[] getDataPoints() {
            if (this.cachedDataPoints == null) {
                interpolateCurve();
            }
            return this.cachedDataPoints;
        }

        public float[] interpolateCurve() {
            float f = this.blacksLevel;
            int i = 1;
            float f2 = 0.5f;
            float f3 = this.whitesLevel;
            float[] fArr = {-0.001f, f / 100.0f, 0.0f, f / 100.0f, 0.25f, this.shadowsLevel / 100.0f, 0.5f, this.midtonesLevel / 100.0f, 0.75f, this.highlightsLevel / 100.0f, 1.0f, f3 / 100.0f, 1.001f, f3 / 100.0f};
            ArrayList arrayList = new ArrayList(100);
            ArrayList arrayList2 = new ArrayList(100);
            arrayList2.add(Float.valueOf(fArr[0]));
            arrayList2.add(Float.valueOf(fArr[1]));
            int i2 = 1;
            while (i2 < 5) {
                int i3 = (i2 - 1) * 2;
                float f4 = fArr[i3];
                float f5 = fArr[i3 + i];
                int i4 = i2 * 2;
                float f6 = fArr[i4];
                float f7 = fArr[i4 + 1];
                int i5 = i2 + 1;
                int i6 = i5 * 2;
                float f8 = fArr[i6];
                float f9 = fArr[i6 + 1];
                int i7 = (i2 + 2) * 2;
                float var_ = fArr[i7];
                float var_ = fArr[i7 + i];
                int i8 = 1;
                while (i8 < 100) {
                    float var_ = ((float) i8) * 0.01f;
                    float var_ = var_ * var_;
                    float var_ = var_ * var_;
                    float var_ = ((f6 * 2.0f) + ((f8 - f4) * var_) + (((((f4 * 2.0f) - (f6 * 5.0f)) + (f8 * 4.0f)) - var_) * var_) + (((((f6 * 3.0f) - f4) - (f8 * 3.0f)) + var_) * var_)) * f2;
                    float max = Math.max(0.0f, Math.min(1.0f, ((f7 * 2.0f) + ((f9 - f5) * var_) + (((((2.0f * f5) - (5.0f * f7)) + (4.0f * f9)) - var_) * var_) + (((((f7 * 3.0f) - f5) - (3.0f * f9)) + var_) * var_)) * f2));
                    if (var_ > f4) {
                        arrayList2.add(Float.valueOf(var_));
                        arrayList2.add(Float.valueOf(max));
                    }
                    if ((i8 - 1) % 2 == 0) {
                        arrayList.add(Float.valueOf(max));
                    }
                    i8++;
                    f2 = 0.5f;
                }
                arrayList2.add(Float.valueOf(f8));
                arrayList2.add(Float.valueOf(f9));
                i2 = i5;
                f2 = 0.5f;
                i = 1;
            }
            arrayList2.add(Float.valueOf(fArr[12]));
            arrayList2.add(Float.valueOf(fArr[13]));
            this.cachedDataPoints = new float[arrayList.size()];
            int i9 = 0;
            while (true) {
                float[] fArr2 = this.cachedDataPoints;
                if (i9 >= fArr2.length) {
                    break;
                }
                fArr2[i9] = ((Float) arrayList.get(i9)).floatValue();
                i9++;
            }
            int size = arrayList2.size();
            float[] fArr3 = new float[size];
            for (int i10 = 0; i10 < size; i10++) {
                fArr3[i10] = ((Float) arrayList2.get(i10)).floatValue();
            }
            return fArr3;
        }

        public boolean isDefault() {
            return ((double) Math.abs(this.blacksLevel - 0.0f)) < 1.0E-5d && ((double) Math.abs(this.shadowsLevel - 25.0f)) < 1.0E-5d && ((double) Math.abs(this.midtonesLevel - 50.0f)) < 1.0E-5d && ((double) Math.abs(this.highlightsLevel - 75.0f)) < 1.0E-5d && ((double) Math.abs(this.whitesLevel - 100.0f)) < 1.0E-5d;
        }
    }

    public static class CurvesToolValue {
        public int activeType;
        public CurvesValue blueCurve = new CurvesValue();
        public ByteBuffer curveBuffer;
        public CurvesValue greenCurve = new CurvesValue();
        public CurvesValue luminanceCurve = new CurvesValue();
        public CurvesValue redCurve = new CurvesValue();

        public CurvesToolValue() {
            ByteBuffer allocateDirect = ByteBuffer.allocateDirect(800);
            this.curveBuffer = allocateDirect;
            allocateDirect.order(ByteOrder.LITTLE_ENDIAN);
        }

        public void fillBuffer() {
            this.curveBuffer.position(0);
            float[] dataPoints = this.luminanceCurve.getDataPoints();
            float[] dataPoints2 = this.redCurve.getDataPoints();
            float[] dataPoints3 = this.greenCurve.getDataPoints();
            float[] dataPoints4 = this.blueCurve.getDataPoints();
            for (int i = 0; i < 200; i++) {
                this.curveBuffer.put((byte) ((int) (dataPoints2[i] * 255.0f)));
                this.curveBuffer.put((byte) ((int) (dataPoints3[i] * 255.0f)));
                this.curveBuffer.put((byte) ((int) (dataPoints4[i] * 255.0f)));
                this.curveBuffer.put((byte) ((int) (dataPoints[i] * 255.0f)));
            }
            this.curveBuffer.position(0);
        }

        public boolean shouldBeSkipped() {
            return this.luminanceCurve.isDefault() && this.redCurve.isDefault() && this.greenCurve.isDefault() && this.blueCurve.isDefault();
        }
    }

    public class EGLThread extends DispatchQueue {
        private int blurHeightHandle;
        private int blurInputTexCoordHandle;
        private int blurPositionHandle;
        private int blurShaderProgram;
        private int blurSourceImageHandle;
        private int blurWidthHandle;
        /* access modifiers changed from: private */
        public boolean blured;
        private int contrastHandle;
        private Bitmap currentBitmap;
        private int[] curveTextures = new int[1];
        private int curvesImageHandle;
        private Runnable drawRunnable = new Runnable() {
            public void run() {
                if (EGLThread.this.initied) {
                    if ((EGLThread.this.eglContext.equals(EGLThread.this.egl10.eglGetCurrentContext()) && EGLThread.this.eglSurface.equals(EGLThread.this.egl10.eglGetCurrentSurface(12377))) || EGLThread.this.egl10.eglMakeCurrent(EGLThread.this.eglDisplay, EGLThread.this.eglSurface, EGLThread.this.eglSurface, EGLThread.this.eglContext)) {
                        GLES20.glViewport(0, 0, EGLThread.this.renderBufferWidth, EGLThread.this.renderBufferHeight);
                        EGLThread.this.drawEnhancePass();
                        EGLThread.this.drawSharpenPass();
                        EGLThread.this.drawCustomParamsPass();
                        EGLThread eGLThread = EGLThread.this;
                        boolean unused = eGLThread.blured = eGLThread.drawBlurPass();
                        GLES20.glViewport(0, 0, EGLThread.this.surfaceWidth, EGLThread.this.surfaceHeight);
                        GLES20.glBindFramebuffer(36160, 0);
                        GLES20.glClear(0);
                        GLES20.glUseProgram(EGLThread.this.simpleShaderProgram);
                        GLES20.glActiveTexture(33984);
                        GLES20.glBindTexture(3553, EGLThread.this.renderTexture[!EGLThread.this.blured]);
                        GLES20.glUniform1i(EGLThread.this.simpleSourceImageHandle, 0);
                        GLES20.glEnableVertexAttribArray(EGLThread.this.simpleInputTexCoordHandle);
                        GLES20.glVertexAttribPointer(EGLThread.this.simpleInputTexCoordHandle, 2, 5126, false, 8, EGLThread.this.textureBuffer);
                        GLES20.glEnableVertexAttribArray(EGLThread.this.simplePositionHandle);
                        GLES20.glVertexAttribPointer(EGLThread.this.simplePositionHandle, 2, 5126, false, 8, EGLThread.this.vertexBuffer);
                        GLES20.glDrawArrays(5, 0, 4);
                        EGLThread.this.egl10.eglSwapBuffers(EGLThread.this.eglDisplay, EGLThread.this.eglSurface);
                    } else if (BuildVars.LOGS_ENABLED) {
                        FileLog.e("eglMakeCurrent failed " + GLUtils.getEGLErrorString(EGLThread.this.egl10.eglGetError()));
                    }
                }
            }
        };
        /* access modifiers changed from: private */
        public EGL10 egl10;
        private EGLConfig eglConfig;
        /* access modifiers changed from: private */
        public EGLContext eglContext;
        /* access modifiers changed from: private */
        public EGLDisplay eglDisplay;
        /* access modifiers changed from: private */
        public EGLSurface eglSurface;
        private int enhanceInputImageTexture2Handle;
        private int enhanceInputTexCoordHandle;
        private int enhanceIntensityHandle;
        private int enhancePositionHandle;
        private int enhanceShaderProgram;
        private int enhanceSourceImageHandle;
        private int[] enhanceTextures = new int[2];
        private int exposureHandle;
        private int fadeAmountHandle;
        private int grainHandle;
        private int heightHandle;
        private int highlightsHandle;
        private int highlightsTintColorHandle;
        private int highlightsTintIntensityHandle;
        private boolean hsvGenerated;
        /* access modifiers changed from: private */
        public boolean initied;
        private int inputTexCoordHandle;
        private long lastRenderCallTime;
        private int linearBlurAngleHandle;
        private int linearBlurAspectRatioHandle;
        private int linearBlurExcludeBlurSizeHandle;
        private int linearBlurExcludePointHandle;
        private int linearBlurExcludeSizeHandle;
        private int linearBlurInputTexCoordHandle;
        private int linearBlurPositionHandle;
        private int linearBlurShaderProgram;
        private int linearBlurSourceImage2Handle;
        private int linearBlurSourceImageHandle;
        private boolean needUpdateBlurTexture = true;
        private int positionHandle;
        private int radialBlurAspectRatioHandle;
        private int radialBlurExcludeBlurSizeHandle;
        private int radialBlurExcludePointHandle;
        private int radialBlurExcludeSizeHandle;
        private int radialBlurInputTexCoordHandle;
        private int radialBlurPositionHandle;
        private int radialBlurShaderProgram;
        private int radialBlurSourceImage2Handle;
        private int radialBlurSourceImageHandle;
        /* access modifiers changed from: private */
        public int renderBufferHeight;
        /* access modifiers changed from: private */
        public int renderBufferWidth;
        private int[] renderFrameBuffer = new int[3];
        /* access modifiers changed from: private */
        public int[] renderTexture = new int[3];
        private int rgbToHsvInputTexCoordHandle;
        private int rgbToHsvPositionHandle;
        private int rgbToHsvShaderProgram;
        private int rgbToHsvSourceImageHandle;
        private int saturationHandle;
        private int shadowsHandle;
        private int shadowsTintColorHandle;
        private int shadowsTintIntensityHandle;
        private int sharpenHandle;
        private int sharpenHeightHandle;
        private int sharpenInputTexCoordHandle;
        private int sharpenPositionHandle;
        private int sharpenShaderProgram;
        private int sharpenSourceImageHandle;
        private int sharpenWidthHandle;
        /* access modifiers changed from: private */
        public int simpleInputTexCoordHandle;
        /* access modifiers changed from: private */
        public int simplePositionHandle;
        /* access modifiers changed from: private */
        public int simpleShaderProgram;
        /* access modifiers changed from: private */
        public int simpleSourceImageHandle;
        private int skipToneHandle;
        private int sourceImageHandle;
        /* access modifiers changed from: private */
        public volatile int surfaceHeight;
        private SurfaceTexture surfaceTexture;
        /* access modifiers changed from: private */
        public volatile int surfaceWidth;
        /* access modifiers changed from: private */
        public FloatBuffer textureBuffer;
        private int toolsShaderProgram;
        /* access modifiers changed from: private */
        public FloatBuffer vertexBuffer;
        private FloatBuffer vertexInvertBuffer;
        private int vignetteHandle;
        private int warmthHandle;
        private int widthHandle;

        public EGLThread(SurfaceTexture surfaceTexture2, Bitmap bitmap) {
            super("EGLThread");
            this.surfaceTexture = surfaceTexture2;
            this.currentBitmap = bitmap;
        }

        private int loadShader(int i, String str) {
            int glCreateShader = GLES20.glCreateShader(i);
            GLES20.glShaderSource(glCreateShader, str);
            GLES20.glCompileShader(glCreateShader);
            int[] iArr = new int[1];
            GLES20.glGetShaderiv(glCreateShader, 35713, iArr, 0);
            if (iArr[0] != 0) {
                return glCreateShader;
            }
            if (BuildVars.LOGS_ENABLED) {
                FileLog.e(GLES20.glGetShaderInfoLog(glCreateShader));
            }
            GLES20.glDeleteShader(glCreateShader);
            return 0;
        }

        private boolean initGL() {
            EGL10 egl102 = (EGL10) EGLContext.getEGL();
            this.egl10 = egl102;
            EGLDisplay eglGetDisplay = egl102.eglGetDisplay(EGL10.EGL_DEFAULT_DISPLAY);
            this.eglDisplay = eglGetDisplay;
            if (eglGetDisplay == EGL10.EGL_NO_DISPLAY) {
                if (BuildVars.LOGS_ENABLED) {
                    FileLog.e("eglGetDisplay failed " + GLUtils.getEGLErrorString(this.egl10.eglGetError()));
                }
                finish();
                return false;
            }
            if (!this.egl10.eglInitialize(eglGetDisplay, new int[2])) {
                if (BuildVars.LOGS_ENABLED) {
                    FileLog.e("eglInitialize failed " + GLUtils.getEGLErrorString(this.egl10.eglGetError()));
                }
                finish();
                return false;
            }
            int[] iArr = new int[1];
            EGLConfig[] eGLConfigArr = new EGLConfig[1];
            if (!this.egl10.eglChooseConfig(this.eglDisplay, new int[]{12352, 4, 12324, 8, 12323, 8, 12322, 8, 12321, 8, 12325, 0, 12326, 0, 12344}, eGLConfigArr, 1, iArr)) {
                if (BuildVars.LOGS_ENABLED) {
                    FileLog.e("eglChooseConfig failed " + GLUtils.getEGLErrorString(this.egl10.eglGetError()));
                }
                finish();
                return false;
            } else if (iArr[0] > 0) {
                EGLConfig eGLConfig = eGLConfigArr[0];
                this.eglConfig = eGLConfig;
                EGLContext eglCreateContext = this.egl10.eglCreateContext(this.eglDisplay, eGLConfig, EGL10.EGL_NO_CONTEXT, new int[]{12440, 2, 12344});
                this.eglContext = eglCreateContext;
                if (eglCreateContext == null) {
                    if (BuildVars.LOGS_ENABLED) {
                        FileLog.e("eglCreateContext failed " + GLUtils.getEGLErrorString(this.egl10.eglGetError()));
                    }
                    finish();
                    return false;
                }
                SurfaceTexture surfaceTexture2 = this.surfaceTexture;
                if (surfaceTexture2 instanceof SurfaceTexture) {
                    EGLSurface eglCreateWindowSurface = this.egl10.eglCreateWindowSurface(this.eglDisplay, this.eglConfig, surfaceTexture2, (int[]) null);
                    this.eglSurface = eglCreateWindowSurface;
                    if (eglCreateWindowSurface == null || eglCreateWindowSurface == EGL10.EGL_NO_SURFACE) {
                        if (BuildVars.LOGS_ENABLED) {
                            FileLog.e("createWindowSurface failed " + GLUtils.getEGLErrorString(this.egl10.eglGetError()));
                        }
                        finish();
                        return false;
                    } else if (!this.egl10.eglMakeCurrent(this.eglDisplay, eglCreateWindowSurface, eglCreateWindowSurface, this.eglContext)) {
                        if (BuildVars.LOGS_ENABLED) {
                            FileLog.e("eglMakeCurrent failed " + GLUtils.getEGLErrorString(this.egl10.eglGetError()));
                        }
                        finish();
                        return false;
                    } else {
                        this.eglContext.getGL();
                        ByteBuffer allocateDirect = ByteBuffer.allocateDirect(32);
                        allocateDirect.order(ByteOrder.nativeOrder());
                        FloatBuffer asFloatBuffer = allocateDirect.asFloatBuffer();
                        this.vertexBuffer = asFloatBuffer;
                        asFloatBuffer.put(new float[]{-1.0f, 1.0f, 1.0f, 1.0f, -1.0f, -1.0f, 1.0f, -1.0f});
                        this.vertexBuffer.position(0);
                        ByteBuffer allocateDirect2 = ByteBuffer.allocateDirect(32);
                        allocateDirect2.order(ByteOrder.nativeOrder());
                        FloatBuffer asFloatBuffer2 = allocateDirect2.asFloatBuffer();
                        this.vertexInvertBuffer = asFloatBuffer2;
                        asFloatBuffer2.put(new float[]{-1.0f, -1.0f, 1.0f, -1.0f, -1.0f, 1.0f, 1.0f, 1.0f});
                        this.vertexInvertBuffer.position(0);
                        ByteBuffer allocateDirect3 = ByteBuffer.allocateDirect(32);
                        allocateDirect3.order(ByteOrder.nativeOrder());
                        FloatBuffer asFloatBuffer3 = allocateDirect3.asFloatBuffer();
                        this.textureBuffer = asFloatBuffer3;
                        asFloatBuffer3.put(new float[]{0.0f, 0.0f, 1.0f, 0.0f, 0.0f, 1.0f, 1.0f, 1.0f});
                        this.textureBuffer.position(0);
                        GLES20.glGenTextures(1, this.curveTextures, 0);
                        GLES20.glGenTextures(2, this.enhanceTextures, 0);
                        int loadShader = loadShader(35633, "attribute vec4 position;attribute vec2 inputTexCoord;varying vec2 texCoord;void main() {gl_Position = position;texCoord = inputTexCoord;}");
                        int loadShader2 = loadShader(35632, "varying highp vec2 texCoord;uniform sampler2D sourceImage;uniform highp float width;uniform highp float height;uniform sampler2D curvesImage;uniform lowp float skipTone;uniform lowp float shadows;const mediump vec3 hsLuminanceWeighting = vec3(0.3, 0.3, 0.3);uniform lowp float highlights;uniform lowp float contrast;uniform lowp float fadeAmount;const mediump vec3 satLuminanceWeighting = vec3(0.2126, 0.7152, 0.0722);uniform lowp float saturation;uniform lowp float shadowsTintIntensity;uniform lowp float highlightsTintIntensity;uniform lowp vec3 shadowsTintColor;uniform lowp vec3 highlightsTintColor;uniform lowp float exposure;uniform lowp float warmth;uniform lowp float grain;const lowp float permTexUnit = 1.0 / 256.0;const lowp float permTexUnitHalf = 0.5 / 256.0;const lowp float grainsize = 2.3;uniform lowp float vignette;highp float getLuma(highp vec3 rgbP) {return (0.299 * rgbP.r) + (0.587 * rgbP.g) + (0.114 * rgbP.b);}lowp vec3 rgbToHsv(lowp vec3 c) {highp vec4 K = vec4(0.0, -1.0 / 3.0, 2.0 / 3.0, -1.0);highp vec4 p = c.g < c.b ? vec4(c.bg, K.wz) : vec4(c.gb, K.xy);highp vec4 q = c.r < p.x ? vec4(p.xyw, c.r) : vec4(c.r, p.yzx);highp float d = q.x - min(q.w, q.y);highp float e = 1.0e-10;return vec3(abs(q.z + (q.w - q.y) / (6.0 * d + e)), d / (q.x + e), q.x);}lowp vec3 hsvToRgb(lowp vec3 c) {highp vec4 K = vec4(1.0, 2.0 / 3.0, 1.0 / 3.0, 3.0);highp vec3 p = abs(fract(c.xxx + K.xyz) * 6.0 - K.www);return c.z * mix(K.xxx, clamp(p - K.xxx, 0.0, 1.0), c.y);}highp vec3 rgbToHsl(highp vec3 color) {highp vec3 hsl;highp float fmin = min(min(color.r, color.g), color.b);highp float fmax = max(max(color.r, color.g), color.b);highp float delta = fmax - fmin;hsl.z = (fmax + fmin) / 2.0;if (delta == 0.0) {hsl.x = 0.0;hsl.y = 0.0;} else {if (hsl.z < 0.5) {hsl.y = delta / (fmax + fmin);} else {hsl.y = delta / (2.0 - fmax - fmin);}highp float deltaR = (((fmax - color.r) / 6.0) + (delta / 2.0)) / delta;highp float deltaG = (((fmax - color.g) / 6.0) + (delta / 2.0)) / delta;highp float deltaB = (((fmax - color.b) / 6.0) + (delta / 2.0)) / delta;if (color.r == fmax) {hsl.x = deltaB - deltaG;} else if (color.g == fmax) {hsl.x = (1.0 / 3.0) + deltaR - deltaB;} else if (color.b == fmax) {hsl.x = (2.0 / 3.0) + deltaG - deltaR;}if (hsl.x < 0.0) {hsl.x += 1.0;} else if (hsl.x > 1.0) {hsl.x -= 1.0;}}return hsl;}highp float hueToRgb(highp float f1, highp float f2, highp float hue) {if (hue < 0.0) {hue += 1.0;} else if (hue > 1.0) {hue -= 1.0;}highp float res;if ((6.0 * hue) < 1.0) {res = f1 + (f2 - f1) * 6.0 * hue;} else if ((2.0 * hue) < 1.0) {res = f2;} else if ((3.0 * hue) < 2.0) {res = f1 + (f2 - f1) * ((2.0 / 3.0) - hue) * 6.0;} else {res = f1;} return res;}highp vec3 hslToRgb(highp vec3 hsl) {if (hsl.y == 0.0) {return vec3(hsl.z);} else {highp float f2;if (hsl.z < 0.5) {f2 = hsl.z * (1.0 + hsl.y);} else {f2 = (hsl.z + hsl.y) - (hsl.y * hsl.z);}highp float f1 = 2.0 * hsl.z - f2;return vec3(hueToRgb(f1, f2, hsl.x + (1.0/3.0)), hueToRgb(f1, f2, hsl.x), hueToRgb(f1, f2, hsl.x - (1.0/3.0)));}}highp vec3 rgbToYuv(highp vec3 inP) {highp float luma = getLuma(inP);return vec3(luma, (1.0 / 1.772) * (inP.b - luma), (1.0 / 1.402) * (inP.r - luma));}lowp vec3 yuvToRgb(highp vec3 inP) {return vec3(1.402 * inP.b + inP.r, (inP.r - (0.299 * 1.402 / 0.587) * inP.b - (0.114 * 1.772 / 0.587) * inP.g), 1.772 * inP.g + inP.r);}lowp float easeInOutSigmoid(lowp float value, lowp float strength) {if (value > 0.5) {return 1.0 - pow(2.0 - 2.0 * value, 1.0 / (1.0 - strength)) * 0.5;} else {return pow(2.0 * value, 1.0 / (1.0 - strength)) * 0.5;}}lowp vec3 applyLuminanceCurve(lowp vec3 pixel) {highp float index = floor(clamp(pixel.z / (1.0 / 200.0), 0.0, 199.0));pixel.y = mix(0.0, pixel.y, smoothstep(0.0, 0.1, pixel.z) * (1.0 - smoothstep(0.8, 1.0, pixel.z)));pixel.z = texture2D(curvesImage, vec2(1.0 / 200.0 * index, 0)).a;return pixel;}lowp vec3 applyRGBCurve(lowp vec3 pixel) {highp float index = floor(clamp(pixel.r / (1.0 / 200.0), 0.0, 199.0));pixel.r = texture2D(curvesImage, vec2(1.0 / 200.0 * index, 0)).r;index = floor(clamp(pixel.g / (1.0 / 200.0), 0.0, 199.0));pixel.g = clamp(texture2D(curvesImage, vec2(1.0 / 200.0 * index, 0)).g, 0.0, 1.0);index = floor(clamp(pixel.b / (1.0 / 200.0), 0.0, 199.0));pixel.b = clamp(texture2D(curvesImage, vec2(1.0 / 200.0 * index, 0)).b, 0.0, 1.0);return pixel;}highp vec3 fadeAdjust(highp vec3 color, highp float fadeVal) {return (color * (1.0 - fadeVal)) + ((color + (vec3(-0.9772) * pow(vec3(color), vec3(3.0)) + vec3(1.708) * pow(vec3(color), vec3(2.0)) + vec3(-0.1603) * vec3(color) + vec3(0.2878) - color * vec3(0.9))) * fadeVal);}lowp vec3 tintRaiseShadowsCurve(lowp vec3 color) {return vec3(-0.003671) * pow(color, vec3(3.0)) + vec3(0.3842) * pow(color, vec3(2.0)) + vec3(0.3764) * color + vec3(0.2515);}lowp vec3 tintShadows(lowp vec3 texel, lowp vec3 tintColor, lowp float tintAmount) {return clamp(mix(texel, mix(texel, tintRaiseShadowsCurve(texel), tintColor), tintAmount), 0.0, 1.0);} lowp vec3 tintHighlights(lowp vec3 texel, lowp vec3 tintColor, lowp float tintAmount) {return clamp(mix(texel, mix(texel, vec3(1.0) - tintRaiseShadowsCurve(vec3(1.0) - texel), (vec3(1.0) - tintColor)), tintAmount), 0.0, 1.0);}highp vec4 rnm(in highp vec2 tc) {highp float noise = sin(dot(tc, vec2(12.9898, 78.233))) * 43758.5453;return vec4(fract(noise), fract(noise * 1.2154), fract(noise * 1.3453), fract(noise * 1.3647)) * 2.0 - 1.0;}highp float fade(in highp float t) {return t * t * t * (t * (t * 6.0 - 15.0) + 10.0);}highp float pnoise3D(in highp vec3 p) {highp vec3 pi = permTexUnit * floor(p) + permTexUnitHalf;highp vec3 pf = fract(p);highp float perm = rnm(pi.xy).a;highp float n000 = dot(rnm(vec2(perm, pi.z)).rgb * 4.0 - 1.0, pf);highp float n001 = dot(rnm(vec2(perm, pi.z + permTexUnit)).rgb * 4.0 - 1.0, pf - vec3(0.0, 0.0, 1.0));perm = rnm(pi.xy + vec2(0.0, permTexUnit)).a;highp float n010 = dot(rnm(vec2(perm, pi.z)).rgb * 4.0 - 1.0, pf - vec3(0.0, 1.0, 0.0));highp float n011 = dot(rnm(vec2(perm, pi.z + permTexUnit)).rgb * 4.0 - 1.0, pf - vec3(0.0, 1.0, 1.0));perm = rnm(pi.xy + vec2(permTexUnit, 0.0)).a;highp float n100 = dot(rnm(vec2(perm, pi.z)).rgb * 4.0 - 1.0, pf - vec3(1.0, 0.0, 0.0));highp float n101 = dot(rnm(vec2(perm, pi.z + permTexUnit)).rgb * 4.0 - 1.0, pf - vec3(1.0, 0.0, 1.0));perm = rnm(pi.xy + vec2(permTexUnit, permTexUnit)).a;highp float n110 = dot(rnm(vec2(perm, pi.z)).rgb * 4.0 - 1.0, pf - vec3(1.0, 1.0, 0.0));highp float n111 = dot(rnm(vec2(perm, pi.z + permTexUnit)).rgb * 4.0 - 1.0, pf - vec3(1.0, 1.0, 1.0));highp vec4 n_x = mix(vec4(n000, n001, n010, n011), vec4(n100, n101, n110, n111), fade(pf.x));highp vec2 n_xy = mix(n_x.xy, n_x.zw, fade(pf.y));return mix(n_xy.x, n_xy.y, fade(pf.z));}lowp vec2 coordRot(in lowp vec2 tc, in lowp float angle) {return vec2(((tc.x * 2.0 - 1.0) * cos(angle) - (tc.y * 2.0 - 1.0) * sin(angle)) * 0.5 + 0.5, ((tc.y * 2.0 - 1.0) * cos(angle) + (tc.x * 2.0 - 1.0) * sin(angle)) * 0.5 + 0.5);}void main() {lowp vec4 source = texture2D(sourceImage, texCoord);lowp vec4 result = source;const lowp float toolEpsilon = 0.005;if (skipTone < toolEpsilon) {result = vec4(applyRGBCurve(hslToRgb(applyLuminanceCurve(rgbToHsl(result.rgb)))), result.a);}mediump float hsLuminance = dot(result.rgb, hsLuminanceWeighting);mediump float shadow = clamp((pow(hsLuminance, 1.0 / shadows) + (-0.76) * pow(hsLuminance, 2.0 / shadows)) - hsLuminance, 0.0, 1.0);mediump float highlight = clamp((1.0 - (pow(1.0 - hsLuminance, 1.0 / (2.0 - highlights)) + (-0.8) * pow(1.0 - hsLuminance, 2.0 / (2.0 - highlights)))) - hsLuminance, -1.0, 0.0);lowp vec3 hsresult = vec3(0.0, 0.0, 0.0) + ((hsLuminance + shadow + highlight) - 0.0) * ((result.rgb - vec3(0.0, 0.0, 0.0)) / (hsLuminance - 0.0));mediump float contrastedLuminance = ((hsLuminance - 0.5) * 1.5) + 0.5;mediump float whiteInterp = contrastedLuminance * contrastedLuminance * contrastedLuminance;mediump float whiteTarget = clamp(highlights, 1.0, 2.0) - 1.0;hsresult = mix(hsresult, vec3(1.0), whiteInterp * whiteTarget);mediump float invContrastedLuminance = 1.0 - contrastedLuminance;mediump float blackInterp = invContrastedLuminance * invContrastedLuminance * invContrastedLuminance;mediump float blackTarget = 1.0 - clamp(shadows, 0.0, 1.0);hsresult = mix(hsresult, vec3(0.0), blackInterp * blackTarget);result = vec4(hsresult.rgb, result.a);result = vec4(clamp(((result.rgb - vec3(0.5)) * contrast + vec3(0.5)), 0.0, 1.0), result.a);if (abs(fadeAmount) > toolEpsilon) {result.rgb = fadeAdjust(result.rgb, fadeAmount);}lowp float satLuminance = dot(result.rgb, satLuminanceWeighting);lowp vec3 greyScaleColor = vec3(satLuminance);result = vec4(clamp(mix(greyScaleColor, result.rgb, saturation), 0.0, 1.0), result.a);if (abs(shadowsTintIntensity) > toolEpsilon) {result.rgb = tintShadows(result.rgb, shadowsTintColor, shadowsTintIntensity * 2.0);}if (abs(highlightsTintIntensity) > toolEpsilon) {result.rgb = tintHighlights(result.rgb, highlightsTintColor, highlightsTintIntensity * 2.0);}if (abs(exposure) > toolEpsilon) {mediump float mag = exposure * 1.045;mediump float exppower = 1.0 + abs(mag);if (mag < 0.0) {exppower = 1.0 / exppower;}result.r = 1.0 - pow((1.0 - result.r), exppower);result.g = 1.0 - pow((1.0 - result.g), exppower);result.b = 1.0 - pow((1.0 - result.b), exppower);}if (abs(warmth) > toolEpsilon) {highp vec3 yuvVec;if (warmth > 0.0 ) {yuvVec = vec3(0.1765, -0.1255, 0.0902);} else {yuvVec = -vec3(0.0588, 0.1569, -0.1255);}highp vec3 yuvColor = rgbToYuv(result.rgb);highp float luma = yuvColor.r;highp float curveScale = sin(luma * 3.14159);yuvColor += 0.375 * warmth * curveScale * yuvVec;result.rgb = yuvToRgb(yuvColor);}if (abs(grain) > toolEpsilon) {highp vec3 rotOffset = vec3(1.425, 3.892, 5.835);highp vec2 rotCoordsR = coordRot(texCoord, rotOffset.x);highp vec3 noise = vec3(pnoise3D(vec3(rotCoordsR * vec2(width / grainsize, height / grainsize),0.0)));lowp vec3 lumcoeff = vec3(0.299,0.587,0.114);lowp float luminance = dot(result.rgb, lumcoeff);lowp float lum = smoothstep(0.2, 0.0, luminance);lum += luminance;noise = mix(noise,vec3(0.0),pow(lum,4.0));result.rgb = result.rgb + noise * grain;}if (abs(vignette) > toolEpsilon) {const lowp float midpoint = 0.7;const lowp float fuzziness = 0.62;lowp float radDist = length(texCoord - 0.5) / sqrt(0.5);lowp float mag = easeInOutSigmoid(radDist * midpoint, fuzziness) * vignette * 0.645;result.rgb = mix(pow(result.rgb, vec3(1.0 / (1.0 - mag))), vec3(0.0), mag * mag);}gl_FragColor = result;}");
                        if (loadShader == 0 || loadShader2 == 0) {
                            finish();
                            return false;
                        }
                        int glCreateProgram = GLES20.glCreateProgram();
                        this.toolsShaderProgram = glCreateProgram;
                        GLES20.glAttachShader(glCreateProgram, loadShader);
                        GLES20.glAttachShader(this.toolsShaderProgram, loadShader2);
                        GLES20.glBindAttribLocation(this.toolsShaderProgram, 0, "position");
                        GLES20.glBindAttribLocation(this.toolsShaderProgram, 1, "inputTexCoord");
                        GLES20.glLinkProgram(this.toolsShaderProgram);
                        int[] iArr2 = new int[1];
                        GLES20.glGetProgramiv(this.toolsShaderProgram, 35714, iArr2, 0);
                        if (iArr2[0] == 0) {
                            GLES20.glDeleteProgram(this.toolsShaderProgram);
                            this.toolsShaderProgram = 0;
                        } else {
                            this.positionHandle = GLES20.glGetAttribLocation(this.toolsShaderProgram, "position");
                            this.inputTexCoordHandle = GLES20.glGetAttribLocation(this.toolsShaderProgram, "inputTexCoord");
                            this.sourceImageHandle = GLES20.glGetUniformLocation(this.toolsShaderProgram, "sourceImage");
                            this.shadowsHandle = GLES20.glGetUniformLocation(this.toolsShaderProgram, "shadows");
                            this.highlightsHandle = GLES20.glGetUniformLocation(this.toolsShaderProgram, "highlights");
                            this.exposureHandle = GLES20.glGetUniformLocation(this.toolsShaderProgram, "exposure");
                            this.contrastHandle = GLES20.glGetUniformLocation(this.toolsShaderProgram, "contrast");
                            this.saturationHandle = GLES20.glGetUniformLocation(this.toolsShaderProgram, "saturation");
                            this.warmthHandle = GLES20.glGetUniformLocation(this.toolsShaderProgram, "warmth");
                            this.vignetteHandle = GLES20.glGetUniformLocation(this.toolsShaderProgram, "vignette");
                            this.grainHandle = GLES20.glGetUniformLocation(this.toolsShaderProgram, "grain");
                            this.widthHandle = GLES20.glGetUniformLocation(this.toolsShaderProgram, "width");
                            this.heightHandle = GLES20.glGetUniformLocation(this.toolsShaderProgram, "height");
                            this.curvesImageHandle = GLES20.glGetUniformLocation(this.toolsShaderProgram, "curvesImage");
                            this.skipToneHandle = GLES20.glGetUniformLocation(this.toolsShaderProgram, "skipTone");
                            this.fadeAmountHandle = GLES20.glGetUniformLocation(this.toolsShaderProgram, "fadeAmount");
                            this.shadowsTintIntensityHandle = GLES20.glGetUniformLocation(this.toolsShaderProgram, "shadowsTintIntensity");
                            this.highlightsTintIntensityHandle = GLES20.glGetUniformLocation(this.toolsShaderProgram, "highlightsTintIntensity");
                            this.shadowsTintColorHandle = GLES20.glGetUniformLocation(this.toolsShaderProgram, "shadowsTintColor");
                            this.highlightsTintColorHandle = GLES20.glGetUniformLocation(this.toolsShaderProgram, "highlightsTintColor");
                        }
                        int loadShader3 = loadShader(35633, "attribute vec4 position;attribute vec2 inputTexCoord;varying vec2 texCoord;uniform highp float inputWidth;uniform highp float inputHeight;varying vec2 leftTexCoord;varying vec2 rightTexCoord;varying vec2 topTexCoord;varying vec2 bottomTexCoord;void main() {gl_Position = position;texCoord = inputTexCoord;highp vec2 widthStep = vec2(1.0 / inputWidth, 0.0);highp vec2 heightStep = vec2(0.0, 1.0 / inputHeight);leftTexCoord = inputTexCoord - widthStep;rightTexCoord = inputTexCoord + widthStep;topTexCoord = inputTexCoord + heightStep;bottomTexCoord = inputTexCoord - heightStep;}");
                        int loadShader4 = loadShader(35632, "precision highp float;varying vec2 texCoord;varying vec2 leftTexCoord;varying vec2 rightTexCoord;varying vec2 topTexCoord;varying vec2 bottomTexCoord;uniform sampler2D sourceImage;uniform float sharpen;void main() {vec4 result = texture2D(sourceImage, texCoord);vec3 leftTextureColor = texture2D(sourceImage, leftTexCoord).rgb;vec3 rightTextureColor = texture2D(sourceImage, rightTexCoord).rgb;vec3 topTextureColor = texture2D(sourceImage, topTexCoord).rgb;vec3 bottomTextureColor = texture2D(sourceImage, bottomTexCoord).rgb;result.rgb = result.rgb * (1.0 + 4.0 * sharpen) - (leftTextureColor + rightTextureColor + topTextureColor + bottomTextureColor) * sharpen;gl_FragColor = result;}");
                        if (loadShader3 == 0 || loadShader4 == 0) {
                            finish();
                            return false;
                        }
                        int glCreateProgram2 = GLES20.glCreateProgram();
                        this.sharpenShaderProgram = glCreateProgram2;
                        GLES20.glAttachShader(glCreateProgram2, loadShader3);
                        GLES20.glAttachShader(this.sharpenShaderProgram, loadShader4);
                        GLES20.glBindAttribLocation(this.sharpenShaderProgram, 0, "position");
                        GLES20.glBindAttribLocation(this.sharpenShaderProgram, 1, "inputTexCoord");
                        GLES20.glLinkProgram(this.sharpenShaderProgram);
                        int[] iArr3 = new int[1];
                        GLES20.glGetProgramiv(this.sharpenShaderProgram, 35714, iArr3, 0);
                        if (iArr3[0] == 0) {
                            GLES20.glDeleteProgram(this.sharpenShaderProgram);
                            this.sharpenShaderProgram = 0;
                        } else {
                            this.sharpenPositionHandle = GLES20.glGetAttribLocation(this.sharpenShaderProgram, "position");
                            this.sharpenInputTexCoordHandle = GLES20.glGetAttribLocation(this.sharpenShaderProgram, "inputTexCoord");
                            this.sharpenSourceImageHandle = GLES20.glGetUniformLocation(this.sharpenShaderProgram, "sourceImage");
                            this.sharpenWidthHandle = GLES20.glGetUniformLocation(this.sharpenShaderProgram, "inputWidth");
                            this.sharpenHeightHandle = GLES20.glGetUniformLocation(this.sharpenShaderProgram, "inputHeight");
                            this.sharpenHandle = GLES20.glGetUniformLocation(this.sharpenShaderProgram, "sharpen");
                        }
                        int loadShader5 = loadShader(35633, "attribute vec4 position;attribute vec4 inputTexCoord;uniform highp float texelWidthOffset;uniform highp float texelHeightOffset;varying vec2 blurCoordinates[9];void main() {gl_Position = position;vec2 singleStepOffset = vec2(texelWidthOffset, texelHeightOffset);blurCoordinates[0] = inputTexCoord.xy;blurCoordinates[1] = inputTexCoord.xy + singleStepOffset * 1.458430;blurCoordinates[2] = inputTexCoord.xy - singleStepOffset * 1.458430;blurCoordinates[3] = inputTexCoord.xy + singleStepOffset * 3.403985;blurCoordinates[4] = inputTexCoord.xy - singleStepOffset * 3.403985;blurCoordinates[5] = inputTexCoord.xy + singleStepOffset * 5.351806;blurCoordinates[6] = inputTexCoord.xy - singleStepOffset * 5.351806;blurCoordinates[7] = inputTexCoord.xy + singleStepOffset * 7.302940;blurCoordinates[8] = inputTexCoord.xy - singleStepOffset * 7.302940;}");
                        int loadShader6 = loadShader(35632, "uniform sampler2D sourceImage;varying highp vec2 blurCoordinates[9];void main() {lowp vec4 sum = vec4(0.0);sum += texture2D(sourceImage, blurCoordinates[0]) * 0.133571;sum += texture2D(sourceImage, blurCoordinates[1]) * 0.233308;sum += texture2D(sourceImage, blurCoordinates[2]) * 0.233308;sum += texture2D(sourceImage, blurCoordinates[3]) * 0.135928;sum += texture2D(sourceImage, blurCoordinates[4]) * 0.135928;sum += texture2D(sourceImage, blurCoordinates[5]) * 0.051383;sum += texture2D(sourceImage, blurCoordinates[6]) * 0.051383;sum += texture2D(sourceImage, blurCoordinates[7]) * 0.012595;sum += texture2D(sourceImage, blurCoordinates[8]) * 0.012595;gl_FragColor = sum;}");
                        if (loadShader5 == 0 || loadShader6 == 0) {
                            finish();
                            return false;
                        }
                        int glCreateProgram3 = GLES20.glCreateProgram();
                        this.blurShaderProgram = glCreateProgram3;
                        GLES20.glAttachShader(glCreateProgram3, loadShader5);
                        GLES20.glAttachShader(this.blurShaderProgram, loadShader6);
                        GLES20.glBindAttribLocation(this.blurShaderProgram, 0, "position");
                        GLES20.glBindAttribLocation(this.blurShaderProgram, 1, "inputTexCoord");
                        GLES20.glLinkProgram(this.blurShaderProgram);
                        int[] iArr4 = new int[1];
                        GLES20.glGetProgramiv(this.blurShaderProgram, 35714, iArr4, 0);
                        if (iArr4[0] == 0) {
                            GLES20.glDeleteProgram(this.blurShaderProgram);
                            this.blurShaderProgram = 0;
                        } else {
                            this.blurPositionHandle = GLES20.glGetAttribLocation(this.blurShaderProgram, "position");
                            this.blurInputTexCoordHandle = GLES20.glGetAttribLocation(this.blurShaderProgram, "inputTexCoord");
                            this.blurSourceImageHandle = GLES20.glGetUniformLocation(this.blurShaderProgram, "sourceImage");
                            this.blurWidthHandle = GLES20.glGetUniformLocation(this.blurShaderProgram, "texelWidthOffset");
                            this.blurHeightHandle = GLES20.glGetUniformLocation(this.blurShaderProgram, "texelHeightOffset");
                        }
                        int loadShader7 = loadShader(35633, "attribute vec4 position;attribute vec2 inputTexCoord;varying vec2 texCoord;void main() {gl_Position = position;texCoord = inputTexCoord;}");
                        int loadShader8 = loadShader(35632, "varying highp vec2 texCoord;uniform sampler2D sourceImage;uniform sampler2D inputImageTexture2;uniform lowp float excludeSize;uniform lowp vec2 excludePoint;uniform lowp float excludeBlurSize;uniform highp float angle;uniform highp float aspectRatio;void main() {lowp vec4 sharpImageColor = texture2D(sourceImage, texCoord);lowp vec4 blurredImageColor = texture2D(inputImageTexture2, texCoord);highp vec2 texCoordToUse = vec2(texCoord.x, (texCoord.y * aspectRatio + 0.5 - 0.5 * aspectRatio));highp float distanceFromCenter = abs((texCoordToUse.x - excludePoint.x) * aspectRatio * cos(angle) + (texCoordToUse.y - excludePoint.y) * sin(angle));gl_FragColor = mix(sharpImageColor, blurredImageColor, smoothstep(excludeSize - excludeBlurSize, excludeSize, distanceFromCenter));}");
                        if (loadShader7 == 0 || loadShader8 == 0) {
                            finish();
                            return false;
                        }
                        int glCreateProgram4 = GLES20.glCreateProgram();
                        this.linearBlurShaderProgram = glCreateProgram4;
                        GLES20.glAttachShader(glCreateProgram4, loadShader7);
                        GLES20.glAttachShader(this.linearBlurShaderProgram, loadShader8);
                        GLES20.glBindAttribLocation(this.linearBlurShaderProgram, 0, "position");
                        GLES20.glBindAttribLocation(this.linearBlurShaderProgram, 1, "inputTexCoord");
                        GLES20.glLinkProgram(this.linearBlurShaderProgram);
                        int[] iArr5 = new int[1];
                        GLES20.glGetProgramiv(this.linearBlurShaderProgram, 35714, iArr5, 0);
                        if (iArr5[0] == 0) {
                            GLES20.glDeleteProgram(this.linearBlurShaderProgram);
                            this.linearBlurShaderProgram = 0;
                        } else {
                            this.linearBlurPositionHandle = GLES20.glGetAttribLocation(this.linearBlurShaderProgram, "position");
                            this.linearBlurInputTexCoordHandle = GLES20.glGetAttribLocation(this.linearBlurShaderProgram, "inputTexCoord");
                            this.linearBlurSourceImageHandle = GLES20.glGetUniformLocation(this.linearBlurShaderProgram, "sourceImage");
                            this.linearBlurSourceImage2Handle = GLES20.glGetUniformLocation(this.linearBlurShaderProgram, "inputImageTexture2");
                            this.linearBlurExcludeSizeHandle = GLES20.glGetUniformLocation(this.linearBlurShaderProgram, "excludeSize");
                            this.linearBlurExcludePointHandle = GLES20.glGetUniformLocation(this.linearBlurShaderProgram, "excludePoint");
                            this.linearBlurExcludeBlurSizeHandle = GLES20.glGetUniformLocation(this.linearBlurShaderProgram, "excludeBlurSize");
                            this.linearBlurAngleHandle = GLES20.glGetUniformLocation(this.linearBlurShaderProgram, "angle");
                            this.linearBlurAspectRatioHandle = GLES20.glGetUniformLocation(this.linearBlurShaderProgram, "aspectRatio");
                        }
                        int loadShader9 = loadShader(35633, "attribute vec4 position;attribute vec2 inputTexCoord;varying vec2 texCoord;void main() {gl_Position = position;texCoord = inputTexCoord;}");
                        int loadShader10 = loadShader(35632, "varying highp vec2 texCoord;uniform sampler2D sourceImage;uniform sampler2D inputImageTexture2;uniform lowp float excludeSize;uniform lowp vec2 excludePoint;uniform lowp float excludeBlurSize;uniform highp float aspectRatio;void main() {lowp vec4 sharpImageColor = texture2D(sourceImage, texCoord);lowp vec4 blurredImageColor = texture2D(inputImageTexture2, texCoord);highp vec2 texCoordToUse = vec2(texCoord.x, (texCoord.y * aspectRatio + 0.5 - 0.5 * aspectRatio));highp float distanceFromCenter = distance(excludePoint, texCoordToUse);gl_FragColor = mix(sharpImageColor, blurredImageColor, smoothstep(excludeSize - excludeBlurSize, excludeSize, distanceFromCenter));}");
                        if (loadShader9 == 0 || loadShader10 == 0) {
                            finish();
                            return false;
                        }
                        int glCreateProgram5 = GLES20.glCreateProgram();
                        this.radialBlurShaderProgram = glCreateProgram5;
                        GLES20.glAttachShader(glCreateProgram5, loadShader9);
                        GLES20.glAttachShader(this.radialBlurShaderProgram, loadShader10);
                        GLES20.glBindAttribLocation(this.radialBlurShaderProgram, 0, "position");
                        GLES20.glBindAttribLocation(this.radialBlurShaderProgram, 1, "inputTexCoord");
                        GLES20.glLinkProgram(this.radialBlurShaderProgram);
                        int[] iArr6 = new int[1];
                        GLES20.glGetProgramiv(this.radialBlurShaderProgram, 35714, iArr6, 0);
                        if (iArr6[0] == 0) {
                            GLES20.glDeleteProgram(this.radialBlurShaderProgram);
                            this.radialBlurShaderProgram = 0;
                        } else {
                            this.radialBlurPositionHandle = GLES20.glGetAttribLocation(this.radialBlurShaderProgram, "position");
                            this.radialBlurInputTexCoordHandle = GLES20.glGetAttribLocation(this.radialBlurShaderProgram, "inputTexCoord");
                            this.radialBlurSourceImageHandle = GLES20.glGetUniformLocation(this.radialBlurShaderProgram, "sourceImage");
                            this.radialBlurSourceImage2Handle = GLES20.glGetUniformLocation(this.radialBlurShaderProgram, "inputImageTexture2");
                            this.radialBlurExcludeSizeHandle = GLES20.glGetUniformLocation(this.radialBlurShaderProgram, "excludeSize");
                            this.radialBlurExcludePointHandle = GLES20.glGetUniformLocation(this.radialBlurShaderProgram, "excludePoint");
                            this.radialBlurExcludeBlurSizeHandle = GLES20.glGetUniformLocation(this.radialBlurShaderProgram, "excludeBlurSize");
                            this.radialBlurAspectRatioHandle = GLES20.glGetUniformLocation(this.radialBlurShaderProgram, "aspectRatio");
                        }
                        int loadShader11 = loadShader(35633, "attribute vec4 position;attribute vec2 inputTexCoord;varying vec2 texCoord;void main() {gl_Position = position;texCoord = inputTexCoord;}");
                        int loadShader12 = loadShader(35632, "precision highp float;varying vec2 texCoord;uniform sampler2D sourceImage;vec3 rgb_to_hsv(vec3 c) {vec4 K = vec4(0.0, -1.0 / 3.0, 2.0 / 3.0, -1.0);vec4 p = c.g < c.b ? vec4(c.bg, K.wz) : vec4(c.gb, K.xy);vec4 q = c.r < p.x ? vec4(p.xyw, c.r) : vec4(c.r, p.yzx);float d = q.x - min(q.w, q.y);float e = 1.0e-10;return vec3(abs(q.z + (q.w - q.y) / (6.0 * d + e)), d / (q.x + e), q.x);}void main() {vec4 texel = texture2D(sourceImage, texCoord);gl_FragColor = vec4(rgb_to_hsv(texel.rgb), texel.a);}");
                        if (loadShader11 == 0 || loadShader12 == 0) {
                            finish();
                            return false;
                        }
                        int glCreateProgram6 = GLES20.glCreateProgram();
                        this.rgbToHsvShaderProgram = glCreateProgram6;
                        GLES20.glAttachShader(glCreateProgram6, loadShader11);
                        GLES20.glAttachShader(this.rgbToHsvShaderProgram, loadShader12);
                        GLES20.glBindAttribLocation(this.rgbToHsvShaderProgram, 0, "position");
                        GLES20.glBindAttribLocation(this.rgbToHsvShaderProgram, 1, "inputTexCoord");
                        GLES20.glLinkProgram(this.rgbToHsvShaderProgram);
                        int[] iArr7 = new int[1];
                        GLES20.glGetProgramiv(this.rgbToHsvShaderProgram, 35714, iArr7, 0);
                        if (iArr7[0] == 0) {
                            GLES20.glDeleteProgram(this.rgbToHsvShaderProgram);
                            this.rgbToHsvShaderProgram = 0;
                        } else {
                            this.rgbToHsvPositionHandle = GLES20.glGetAttribLocation(this.rgbToHsvShaderProgram, "position");
                            this.rgbToHsvInputTexCoordHandle = GLES20.glGetAttribLocation(this.rgbToHsvShaderProgram, "inputTexCoord");
                            this.rgbToHsvSourceImageHandle = GLES20.glGetUniformLocation(this.rgbToHsvShaderProgram, "sourceImage");
                        }
                        int loadShader13 = loadShader(35633, "attribute vec4 position;attribute vec2 inputTexCoord;varying vec2 texCoord;void main() {gl_Position = position;texCoord = inputTexCoord;}");
                        int loadShader14 = loadShader(35632, "precision highp float;varying vec2 texCoord;uniform sampler2D sourceImage;uniform sampler2D inputImageTexture2;uniform float intensity;float enhance(float value) {const vec2 offset = vec2(0.NUM, 0.03125);value = value + offset.x;vec2 coord = (clamp(texCoord, 0.125, 1.0 - 0.125001) - 0.125) * 4.0;vec2 frac = fract(coord);coord = floor(coord);float p00 = float(coord.y * 4.0 + coord.x) * 0.0625 + offset.y;float p01 = float(coord.y * 4.0 + coord.x + 1.0) * 0.0625 + offset.y;float p10 = float((coord.y + 1.0) * 4.0 + coord.x) * 0.0625 + offset.y;float p11 = float((coord.y + 1.0) * 4.0 + coord.x + 1.0) * 0.0625 + offset.y;vec3 CLASSNAME = texture2D(inputImageTexture2, vec2(value, p00)).rgb;vec3 CLASSNAME = texture2D(inputImageTexture2, vec2(value, p01)).rgb;vec3 CLASSNAME = texture2D(inputImageTexture2, vec2(value, p10)).rgb;vec3 CLASSNAME = texture2D(inputImageTexture2, vec2(value, p11)).rgb;float c1 = ((CLASSNAME.r - CLASSNAME.g) / (CLASSNAME.b - CLASSNAME.g));float c2 = ((CLASSNAME.r - CLASSNAME.g) / (CLASSNAME.b - CLASSNAME.g));float c3 = ((CLASSNAME.r - CLASSNAME.g) / (CLASSNAME.b - CLASSNAME.g));float c4 = ((CLASSNAME.r - CLASSNAME.g) / (CLASSNAME.b - CLASSNAME.g));float c1_2 = mix(c1, c2, frac.x);float c3_4 = mix(c3, c4, frac.x);return mix(c1_2, c3_4, frac.y);}vec3 hsv_to_rgb(vec3 c) {vec4 K = vec4(1.0, 2.0 / 3.0, 1.0 / 3.0, 3.0);vec3 p = abs(fract(c.xxx + K.xyz) * 6.0 - K.www);return c.z * mix(K.xxx, clamp(p - K.xxx, 0.0, 1.0), c.y);}void main() {vec4 texel = texture2D(sourceImage, texCoord);vec4 hsv = texel;hsv.y = min(1.0, hsv.y * 1.2);hsv.z = min(1.0, enhance(hsv.z) * 1.1);gl_FragColor = vec4(hsv_to_rgb(mix(texel.xyz, hsv.xyz, intensity)), texel.w);}");
                        if (loadShader13 == 0 || loadShader14 == 0) {
                            finish();
                            return false;
                        }
                        int glCreateProgram7 = GLES20.glCreateProgram();
                        this.enhanceShaderProgram = glCreateProgram7;
                        GLES20.glAttachShader(glCreateProgram7, loadShader13);
                        GLES20.glAttachShader(this.enhanceShaderProgram, loadShader14);
                        GLES20.glBindAttribLocation(this.enhanceShaderProgram, 0, "position");
                        GLES20.glBindAttribLocation(this.enhanceShaderProgram, 1, "inputTexCoord");
                        GLES20.glLinkProgram(this.enhanceShaderProgram);
                        int[] iArr8 = new int[1];
                        GLES20.glGetProgramiv(this.enhanceShaderProgram, 35714, iArr8, 0);
                        if (iArr8[0] == 0) {
                            GLES20.glDeleteProgram(this.enhanceShaderProgram);
                            this.enhanceShaderProgram = 0;
                        } else {
                            this.enhancePositionHandle = GLES20.glGetAttribLocation(this.enhanceShaderProgram, "position");
                            this.enhanceInputTexCoordHandle = GLES20.glGetAttribLocation(this.enhanceShaderProgram, "inputTexCoord");
                            this.enhanceSourceImageHandle = GLES20.glGetUniformLocation(this.enhanceShaderProgram, "sourceImage");
                            this.enhanceIntensityHandle = GLES20.glGetUniformLocation(this.enhanceShaderProgram, "intensity");
                            this.enhanceInputImageTexture2Handle = GLES20.glGetUniformLocation(this.enhanceShaderProgram, "inputImageTexture2");
                        }
                        int loadShader15 = loadShader(35633, "attribute vec4 position;attribute vec2 inputTexCoord;varying vec2 texCoord;void main() {gl_Position = position;texCoord = inputTexCoord;}");
                        int loadShader16 = loadShader(35632, "varying highp vec2 texCoord;uniform sampler2D sourceImage;void main() {gl_FragColor = texture2D(sourceImage, texCoord);}");
                        if (loadShader15 == 0 || loadShader16 == 0) {
                            finish();
                            return false;
                        }
                        int glCreateProgram8 = GLES20.glCreateProgram();
                        this.simpleShaderProgram = glCreateProgram8;
                        GLES20.glAttachShader(glCreateProgram8, loadShader15);
                        GLES20.glAttachShader(this.simpleShaderProgram, loadShader16);
                        GLES20.glBindAttribLocation(this.simpleShaderProgram, 0, "position");
                        GLES20.glBindAttribLocation(this.simpleShaderProgram, 1, "inputTexCoord");
                        GLES20.glLinkProgram(this.simpleShaderProgram);
                        int[] iArr9 = new int[1];
                        GLES20.glGetProgramiv(this.simpleShaderProgram, 35714, iArr9, 0);
                        if (iArr9[0] == 0) {
                            GLES20.glDeleteProgram(this.simpleShaderProgram);
                            this.simpleShaderProgram = 0;
                        } else {
                            this.simplePositionHandle = GLES20.glGetAttribLocation(this.simpleShaderProgram, "position");
                            this.simpleInputTexCoordHandle = GLES20.glGetAttribLocation(this.simpleShaderProgram, "inputTexCoord");
                            this.simpleSourceImageHandle = GLES20.glGetUniformLocation(this.simpleShaderProgram, "sourceImage");
                        }
                        Bitmap bitmap = this.currentBitmap;
                        if (bitmap != null && !bitmap.isRecycled()) {
                            loadTexture(this.currentBitmap);
                        }
                        return true;
                    }
                } else {
                    finish();
                    return false;
                }
            } else {
                if (BuildVars.LOGS_ENABLED) {
                    FileLog.e("eglConfig not initialized");
                }
                finish();
                return false;
            }
        }

        public void finish() {
            if (this.eglSurface != null) {
                EGL10 egl102 = this.egl10;
                EGLDisplay eGLDisplay = this.eglDisplay;
                EGLSurface eGLSurface = EGL10.EGL_NO_SURFACE;
                egl102.eglMakeCurrent(eGLDisplay, eGLSurface, eGLSurface, EGL10.EGL_NO_CONTEXT);
                this.egl10.eglDestroySurface(this.eglDisplay, this.eglSurface);
                this.eglSurface = null;
            }
            EGLContext eGLContext = this.eglContext;
            if (eGLContext != null) {
                this.egl10.eglDestroyContext(this.eglDisplay, eGLContext);
                this.eglContext = null;
            }
            EGLDisplay eGLDisplay2 = this.eglDisplay;
            if (eGLDisplay2 != null) {
                this.egl10.eglTerminate(eGLDisplay2);
                this.eglDisplay = null;
            }
        }

        /* access modifiers changed from: private */
        public void drawEnhancePass() {
            if (!this.hsvGenerated) {
                GLES20.glBindFramebuffer(36160, this.renderFrameBuffer[0]);
                GLES20.glFramebufferTexture2D(36160, 36064, 3553, this.renderTexture[0], 0);
                GLES20.glClear(0);
                GLES20.glUseProgram(this.rgbToHsvShaderProgram);
                GLES20.glActiveTexture(33984);
                GLES20.glBindTexture(3553, this.renderTexture[1]);
                GLES20.glUniform1i(this.rgbToHsvSourceImageHandle, 0);
                GLES20.glEnableVertexAttribArray(this.rgbToHsvInputTexCoordHandle);
                GLES20.glVertexAttribPointer(this.rgbToHsvInputTexCoordHandle, 2, 5126, false, 8, this.textureBuffer);
                GLES20.glEnableVertexAttribArray(this.rgbToHsvPositionHandle);
                GLES20.glVertexAttribPointer(this.rgbToHsvPositionHandle, 2, 5126, false, 8, this.vertexBuffer);
                GLES20.glDrawArrays(5, 0, 4);
                ByteBuffer allocateDirect = ByteBuffer.allocateDirect(this.renderBufferWidth * this.renderBufferHeight * 4);
                GLES20.glReadPixels(0, 0, this.renderBufferWidth, this.renderBufferHeight, 6408, 5121, allocateDirect);
                GLES20.glBindTexture(3553, this.enhanceTextures[0]);
                GLES20.glTexParameteri(3553, 10241, 9729);
                GLES20.glTexParameteri(3553, 10240, 9729);
                GLES20.glTexParameteri(3553, 10242, 33071);
                GLES20.glTexParameteri(3553, 10243, 33071);
                GLES20.glTexImage2D(3553, 0, 6408, this.renderBufferWidth, this.renderBufferHeight, 0, 6408, 5121, allocateDirect);
                ByteBuffer byteBuffer = null;
                try {
                    byteBuffer = ByteBuffer.allocateDirect(16384);
                    Utilities.calcCDT(allocateDirect, this.renderBufferWidth, this.renderBufferHeight, byteBuffer);
                } catch (Exception e) {
                    FileLog.e((Throwable) e);
                }
                GLES20.glBindTexture(3553, this.enhanceTextures[1]);
                GLES20.glTexParameteri(3553, 10241, 9729);
                GLES20.glTexParameteri(3553, 10240, 9729);
                GLES20.glTexParameteri(3553, 10242, 33071);
                GLES20.glTexParameteri(3553, 10243, 33071);
                GLES20.glTexImage2D(3553, 0, 6408, 256, 16, 0, 6408, 5121, byteBuffer);
                this.hsvGenerated = true;
            }
            GLES20.glBindFramebuffer(36160, this.renderFrameBuffer[1]);
            GLES20.glFramebufferTexture2D(36160, 36064, 3553, this.renderTexture[1], 0);
            GLES20.glClear(0);
            GLES20.glUseProgram(this.enhanceShaderProgram);
            GLES20.glActiveTexture(33984);
            GLES20.glBindTexture(3553, this.enhanceTextures[0]);
            GLES20.glUniform1i(this.enhanceSourceImageHandle, 0);
            GLES20.glActiveTexture(33985);
            GLES20.glBindTexture(3553, this.enhanceTextures[1]);
            GLES20.glUniform1i(this.enhanceInputImageTexture2Handle, 1);
            if (PhotoFilterView.this.showOriginal) {
                GLES20.glUniform1f(this.enhanceIntensityHandle, 0.0f);
            } else {
                GLES20.glUniform1f(this.enhanceIntensityHandle, PhotoFilterView.this.getEnhanceValue());
            }
            GLES20.glEnableVertexAttribArray(this.enhanceInputTexCoordHandle);
            GLES20.glVertexAttribPointer(this.enhanceInputTexCoordHandle, 2, 5126, false, 8, this.textureBuffer);
            GLES20.glEnableVertexAttribArray(this.enhancePositionHandle);
            GLES20.glVertexAttribPointer(this.enhancePositionHandle, 2, 5126, false, 8, this.vertexBuffer);
            GLES20.glDrawArrays(5, 0, 4);
        }

        /* access modifiers changed from: private */
        public void drawSharpenPass() {
            GLES20.glBindFramebuffer(36160, this.renderFrameBuffer[0]);
            GLES20.glFramebufferTexture2D(36160, 36064, 3553, this.renderTexture[0], 0);
            GLES20.glClear(0);
            GLES20.glUseProgram(this.sharpenShaderProgram);
            GLES20.glActiveTexture(33984);
            GLES20.glBindTexture(3553, this.renderTexture[1]);
            GLES20.glUniform1i(this.sharpenSourceImageHandle, 0);
            if (PhotoFilterView.this.showOriginal) {
                GLES20.glUniform1f(this.sharpenHandle, 0.0f);
            } else {
                GLES20.glUniform1f(this.sharpenHandle, PhotoFilterView.this.getSharpenValue());
            }
            GLES20.glUniform1f(this.sharpenWidthHandle, (float) this.renderBufferWidth);
            GLES20.glUniform1f(this.sharpenHeightHandle, (float) this.renderBufferHeight);
            GLES20.glEnableVertexAttribArray(this.sharpenInputTexCoordHandle);
            GLES20.glVertexAttribPointer(this.sharpenInputTexCoordHandle, 2, 5126, false, 8, this.textureBuffer);
            GLES20.glEnableVertexAttribArray(this.sharpenPositionHandle);
            GLES20.glVertexAttribPointer(this.sharpenPositionHandle, 2, 5126, false, 8, this.vertexInvertBuffer);
            GLES20.glDrawArrays(5, 0, 4);
        }

        /* access modifiers changed from: private */
        public void drawCustomParamsPass() {
            GLES20.glBindFramebuffer(36160, this.renderFrameBuffer[1]);
            GLES20.glFramebufferTexture2D(36160, 36064, 3553, this.renderTexture[1], 0);
            GLES20.glClear(0);
            GLES20.glUseProgram(this.toolsShaderProgram);
            GLES20.glActiveTexture(33984);
            GLES20.glBindTexture(3553, this.renderTexture[0]);
            GLES20.glUniform1i(this.sourceImageHandle, 0);
            float f = 1.0f;
            if (PhotoFilterView.this.showOriginal) {
                GLES20.glUniform1f(this.shadowsHandle, 1.0f);
                GLES20.glUniform1f(this.highlightsHandle, 1.0f);
                GLES20.glUniform1f(this.exposureHandle, 0.0f);
                GLES20.glUniform1f(this.contrastHandle, 1.0f);
                GLES20.glUniform1f(this.saturationHandle, 1.0f);
                GLES20.glUniform1f(this.warmthHandle, 0.0f);
                GLES20.glUniform1f(this.vignetteHandle, 0.0f);
                GLES20.glUniform1f(this.grainHandle, 0.0f);
                GLES20.glUniform1f(this.fadeAmountHandle, 0.0f);
                GLES20.glUniform3f(this.highlightsTintColorHandle, 0.0f, 0.0f, 0.0f);
                GLES20.glUniform1f(this.highlightsTintIntensityHandle, 0.0f);
                GLES20.glUniform3f(this.shadowsTintColorHandle, 0.0f, 0.0f, 0.0f);
                GLES20.glUniform1f(this.shadowsTintIntensityHandle, 0.0f);
                GLES20.glUniform1f(this.skipToneHandle, 1.0f);
            } else {
                GLES20.glUniform1f(this.shadowsHandle, PhotoFilterView.this.getShadowsValue());
                GLES20.glUniform1f(this.highlightsHandle, PhotoFilterView.this.getHighlightsValue());
                GLES20.glUniform1f(this.exposureHandle, PhotoFilterView.this.getExposureValue());
                GLES20.glUniform1f(this.contrastHandle, PhotoFilterView.this.getContrastValue());
                GLES20.glUniform1f(this.saturationHandle, PhotoFilterView.this.getSaturationValue());
                GLES20.glUniform1f(this.warmthHandle, PhotoFilterView.this.getWarmthValue());
                GLES20.glUniform1f(this.vignetteHandle, PhotoFilterView.this.getVignetteValue());
                GLES20.glUniform1f(this.grainHandle, PhotoFilterView.this.getGrainValue());
                GLES20.glUniform1f(this.fadeAmountHandle, PhotoFilterView.this.getFadeValue());
                GLES20.glUniform3f(this.highlightsTintColorHandle, ((float) ((PhotoFilterView.this.tintHighlightsColor >> 16) & 255)) / 255.0f, ((float) ((PhotoFilterView.this.tintHighlightsColor >> 8) & 255)) / 255.0f, ((float) (PhotoFilterView.this.tintHighlightsColor & 255)) / 255.0f);
                GLES20.glUniform1f(this.highlightsTintIntensityHandle, PhotoFilterView.this.getTintHighlightsIntensityValue());
                GLES20.glUniform3f(this.shadowsTintColorHandle, ((float) ((PhotoFilterView.this.tintShadowsColor >> 16) & 255)) / 255.0f, ((float) ((PhotoFilterView.this.tintShadowsColor >> 8) & 255)) / 255.0f, ((float) (PhotoFilterView.this.tintShadowsColor & 255)) / 255.0f);
                GLES20.glUniform1f(this.shadowsTintIntensityHandle, PhotoFilterView.this.getTintShadowsIntensityValue());
                boolean shouldBeSkipped = PhotoFilterView.this.curvesToolValue.shouldBeSkipped();
                int i = this.skipToneHandle;
                if (!shouldBeSkipped) {
                    f = 0.0f;
                }
                GLES20.glUniform1f(i, f);
                if (!shouldBeSkipped) {
                    PhotoFilterView.this.curvesToolValue.fillBuffer();
                    GLES20.glActiveTexture(33985);
                    GLES20.glBindTexture(3553, this.curveTextures[0]);
                    GLES20.glTexParameteri(3553, 10241, 9729);
                    GLES20.glTexParameteri(3553, 10240, 9729);
                    GLES20.glTexParameteri(3553, 10242, 33071);
                    GLES20.glTexParameteri(3553, 10243, 33071);
                    GLES20.glTexImage2D(3553, 0, 6408, 200, 1, 0, 6408, 5121, PhotoFilterView.this.curvesToolValue.curveBuffer);
                    GLES20.glUniform1i(this.curvesImageHandle, 1);
                }
            }
            GLES20.glUniform1f(this.widthHandle, (float) this.renderBufferWidth);
            GLES20.glUniform1f(this.heightHandle, (float) this.renderBufferHeight);
            GLES20.glEnableVertexAttribArray(this.inputTexCoordHandle);
            GLES20.glVertexAttribPointer(this.inputTexCoordHandle, 2, 5126, false, 8, this.textureBuffer);
            GLES20.glEnableVertexAttribArray(this.positionHandle);
            GLES20.glVertexAttribPointer(this.positionHandle, 2, 5126, false, 8, this.vertexInvertBuffer);
            GLES20.glDrawArrays(5, 0, 4);
        }

        /* access modifiers changed from: private */
        public boolean drawBlurPass() {
            if (PhotoFilterView.this.showOriginal || PhotoFilterView.this.blurType == 0) {
                return false;
            }
            if (this.needUpdateBlurTexture) {
                GLES20.glUseProgram(this.blurShaderProgram);
                GLES20.glUniform1i(this.blurSourceImageHandle, 0);
                GLES20.glEnableVertexAttribArray(this.blurInputTexCoordHandle);
                GLES20.glVertexAttribPointer(this.blurInputTexCoordHandle, 2, 5126, false, 8, this.textureBuffer);
                GLES20.glEnableVertexAttribArray(this.blurPositionHandle);
                GLES20.glVertexAttribPointer(this.blurPositionHandle, 2, 5126, false, 8, this.vertexInvertBuffer);
                GLES20.glBindFramebuffer(36160, this.renderFrameBuffer[0]);
                GLES20.glFramebufferTexture2D(36160, 36064, 3553, this.renderTexture[0], 0);
                GLES20.glClear(0);
                GLES20.glActiveTexture(33984);
                GLES20.glBindTexture(3553, this.renderTexture[1]);
                GLES20.glUniform1f(this.blurWidthHandle, 0.0f);
                GLES20.glUniform1f(this.blurHeightHandle, 1.0f / ((float) this.renderBufferHeight));
                GLES20.glDrawArrays(5, 0, 4);
                GLES20.glBindFramebuffer(36160, this.renderFrameBuffer[2]);
                GLES20.glFramebufferTexture2D(36160, 36064, 3553, this.renderTexture[2], 0);
                GLES20.glClear(0);
                GLES20.glActiveTexture(33984);
                GLES20.glBindTexture(3553, this.renderTexture[0]);
                GLES20.glUniform1f(this.blurWidthHandle, 1.0f / ((float) this.renderBufferWidth));
                GLES20.glUniform1f(this.blurHeightHandle, 0.0f);
                GLES20.glDrawArrays(5, 0, 4);
                this.needUpdateBlurTexture = false;
            }
            GLES20.glBindFramebuffer(36160, this.renderFrameBuffer[0]);
            GLES20.glFramebufferTexture2D(36160, 36064, 3553, this.renderTexture[0], 0);
            GLES20.glClear(0);
            if (PhotoFilterView.this.blurType == 1) {
                GLES20.glUseProgram(this.radialBlurShaderProgram);
                GLES20.glUniform1i(this.radialBlurSourceImageHandle, 0);
                GLES20.glUniform1i(this.radialBlurSourceImage2Handle, 1);
                GLES20.glUniform1f(this.radialBlurExcludeSizeHandle, PhotoFilterView.this.blurExcludeSize);
                GLES20.glUniform1f(this.radialBlurExcludeBlurSizeHandle, PhotoFilterView.this.blurExcludeBlurSize);
                GLES20.glUniform2f(this.radialBlurExcludePointHandle, PhotoFilterView.this.blurExcludePoint.x, PhotoFilterView.this.blurExcludePoint.y);
                GLES20.glUniform1f(this.radialBlurAspectRatioHandle, ((float) this.renderBufferHeight) / ((float) this.renderBufferWidth));
                GLES20.glEnableVertexAttribArray(this.radialBlurInputTexCoordHandle);
                GLES20.glVertexAttribPointer(this.radialBlurInputTexCoordHandle, 2, 5126, false, 8, this.textureBuffer);
                GLES20.glEnableVertexAttribArray(this.radialBlurPositionHandle);
                GLES20.glVertexAttribPointer(this.radialBlurPositionHandle, 2, 5126, false, 8, this.vertexInvertBuffer);
            } else if (PhotoFilterView.this.blurType == 2) {
                GLES20.glUseProgram(this.linearBlurShaderProgram);
                GLES20.glUniform1i(this.linearBlurSourceImageHandle, 0);
                GLES20.glUniform1i(this.linearBlurSourceImage2Handle, 1);
                GLES20.glUniform1f(this.linearBlurExcludeSizeHandle, PhotoFilterView.this.blurExcludeSize);
                GLES20.glUniform1f(this.linearBlurExcludeBlurSizeHandle, PhotoFilterView.this.blurExcludeBlurSize);
                GLES20.glUniform1f(this.linearBlurAngleHandle, PhotoFilterView.this.blurAngle);
                GLES20.glUniform2f(this.linearBlurExcludePointHandle, PhotoFilterView.this.blurExcludePoint.x, PhotoFilterView.this.blurExcludePoint.y);
                GLES20.glUniform1f(this.linearBlurAspectRatioHandle, ((float) this.renderBufferHeight) / ((float) this.renderBufferWidth));
                GLES20.glEnableVertexAttribArray(this.linearBlurInputTexCoordHandle);
                GLES20.glVertexAttribPointer(this.linearBlurInputTexCoordHandle, 2, 5126, false, 8, this.textureBuffer);
                GLES20.glEnableVertexAttribArray(this.linearBlurPositionHandle);
                GLES20.glVertexAttribPointer(this.linearBlurPositionHandle, 2, 5126, false, 8, this.vertexInvertBuffer);
            }
            GLES20.glActiveTexture(33984);
            GLES20.glBindTexture(3553, this.renderTexture[1]);
            GLES20.glActiveTexture(33985);
            GLES20.glBindTexture(3553, this.renderTexture[2]);
            GLES20.glDrawArrays(5, 0, 4);
            return true;
        }

        private Bitmap getRenderBufferBitmap() {
            ByteBuffer allocateDirect = ByteBuffer.allocateDirect(this.renderBufferWidth * this.renderBufferHeight * 4);
            GLES20.glReadPixels(0, 0, this.renderBufferWidth, this.renderBufferHeight, 6408, 5121, allocateDirect);
            Bitmap createBitmap = Bitmap.createBitmap(this.renderBufferWidth, this.renderBufferHeight, Bitmap.Config.ARGB_8888);
            createBitmap.copyPixelsFromBuffer(allocateDirect);
            return createBitmap;
        }

        public Bitmap getTexture() {
            if (!this.initied) {
                return null;
            }
            CountDownLatch countDownLatch = new CountDownLatch(1);
            Bitmap[] bitmapArr = new Bitmap[1];
            try {
                postRunnable(new Runnable(bitmapArr, countDownLatch) {
                    private final /* synthetic */ Bitmap[] f$1;
                    private final /* synthetic */ CountDownLatch f$2;

                    {
                        this.f$1 = r2;
                        this.f$2 = r3;
                    }

                    public final void run() {
                        PhotoFilterView.EGLThread.this.lambda$getTexture$0$PhotoFilterView$EGLThread(this.f$1, this.f$2);
                    }
                });
                countDownLatch.await();
            } catch (Exception e) {
                FileLog.e((Throwable) e);
            }
            return bitmapArr[0];
        }

        public /* synthetic */ void lambda$getTexture$0$PhotoFilterView$EGLThread(Bitmap[] bitmapArr, CountDownLatch countDownLatch) {
            GLES20.glBindFramebuffer(36160, this.renderFrameBuffer[1]);
            GLES20.glFramebufferTexture2D(36160, 36064, 3553, this.renderTexture[true ^ this.blured], 0);
            GLES20.glClear(0);
            bitmapArr[0] = getRenderBufferBitmap();
            countDownLatch.countDown();
            GLES20.glBindFramebuffer(36160, 0);
            GLES20.glClear(0);
        }

        private Bitmap createBitmap(Bitmap bitmap, int i, int i2, float f) {
            Matrix matrix = new Matrix();
            matrix.setScale(f, f);
            matrix.postRotate((float) PhotoFilterView.this.orientation);
            return Bitmap.createBitmap(bitmap, 0, 0, bitmap.getWidth(), bitmap.getHeight(), matrix, true);
        }

        private void loadTexture(Bitmap bitmap) {
            this.renderBufferWidth = bitmap.getWidth();
            this.renderBufferHeight = bitmap.getHeight();
            float photoSize = (float) AndroidUtilities.getPhotoSize();
            if (((float) this.renderBufferWidth) > photoSize || ((float) this.renderBufferHeight) > photoSize || PhotoFilterView.this.orientation % 360 != 0) {
                float f = 1.0f;
                if (((float) this.renderBufferWidth) > photoSize || ((float) this.renderBufferHeight) > photoSize) {
                    f = photoSize / ((float) bitmap.getWidth());
                    float height = photoSize / ((float) bitmap.getHeight());
                    if (f < height) {
                        this.renderBufferWidth = (int) photoSize;
                        this.renderBufferHeight = (int) (((float) bitmap.getHeight()) * f);
                    } else {
                        this.renderBufferHeight = (int) photoSize;
                        this.renderBufferWidth = (int) (((float) bitmap.getWidth()) * height);
                        f = height;
                    }
                }
                if (PhotoFilterView.this.orientation % 360 == 90 || PhotoFilterView.this.orientation % 360 == 270) {
                    int i = this.renderBufferWidth;
                    this.renderBufferWidth = this.renderBufferHeight;
                    this.renderBufferHeight = i;
                }
                this.currentBitmap = createBitmap(bitmap, this.renderBufferWidth, this.renderBufferHeight, f);
            }
            GLES20.glGenFramebuffers(3, this.renderFrameBuffer, 0);
            GLES20.glGenTextures(3, this.renderTexture, 0);
            GLES20.glBindTexture(3553, this.renderTexture[0]);
            GLES20.glTexParameteri(3553, 10241, 9729);
            GLES20.glTexParameteri(3553, 10240, 9729);
            GLES20.glTexParameteri(3553, 10242, 33071);
            GLES20.glTexParameteri(3553, 10243, 33071);
            GLES20.glTexImage2D(3553, 0, 6408, this.renderBufferWidth, this.renderBufferHeight, 0, 6408, 5121, (Buffer) null);
            GLES20.glBindTexture(3553, this.renderTexture[1]);
            GLES20.glTexParameteri(3553, 10241, 9729);
            GLES20.glTexParameteri(3553, 10240, 9729);
            GLES20.glTexParameteri(3553, 10242, 33071);
            GLES20.glTexParameteri(3553, 10243, 33071);
            GLUtils.texImage2D(3553, 0, this.currentBitmap, 0);
            GLES20.glBindTexture(3553, this.renderTexture[2]);
            GLES20.glTexParameteri(3553, 10241, 9729);
            GLES20.glTexParameteri(3553, 10240, 9729);
            GLES20.glTexParameteri(3553, 10242, 33071);
            GLES20.glTexParameteri(3553, 10243, 33071);
            GLES20.glTexImage2D(3553, 0, 6408, this.renderBufferWidth, this.renderBufferHeight, 0, 6408, 5121, (Buffer) null);
        }

        public void shutdown() {
            postRunnable(new Runnable() {
                public final void run() {
                    PhotoFilterView.EGLThread.this.lambda$shutdown$1$PhotoFilterView$EGLThread();
                }
            });
        }

        public /* synthetic */ void lambda$shutdown$1$PhotoFilterView$EGLThread() {
            finish();
            this.currentBitmap = null;
            Looper myLooper = Looper.myLooper();
            if (myLooper != null) {
                myLooper.quit();
            }
        }

        public void setSurfaceTextureSize(int i, int i2) {
            this.surfaceWidth = i;
            this.surfaceHeight = i2;
        }

        public void run() {
            this.initied = initGL();
            super.run();
        }

        public void requestRender(boolean z) {
            requestRender(z, false);
        }

        public void requestRender(boolean z, boolean z2) {
            postRunnable(new Runnable(z, z2) {
                private final /* synthetic */ boolean f$1;
                private final /* synthetic */ boolean f$2;

                {
                    this.f$1 = r2;
                    this.f$2 = r3;
                }

                public final void run() {
                    PhotoFilterView.EGLThread.this.lambda$requestRender$2$PhotoFilterView$EGLThread(this.f$1, this.f$2);
                }
            });
        }

        public /* synthetic */ void lambda$requestRender$2$PhotoFilterView$EGLThread(boolean z, boolean z2) {
            if (!this.needUpdateBlurTexture) {
                this.needUpdateBlurTexture = z;
            }
            long currentTimeMillis = System.currentTimeMillis();
            if (z2 || Math.abs(this.lastRenderCallTime - currentTimeMillis) > 30) {
                this.lastRenderCallTime = currentTimeMillis;
                this.drawRunnable.run();
            }
        }
    }

    /* JADX INFO: super call moved to the top of the method (can break code semantics) */
    /* JADX WARNING: Removed duplicated region for block: B:20:0x044b  */
    /* JADX WARNING: Removed duplicated region for block: B:21:0x044f  */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public PhotoFilterView(android.content.Context r24, android.graphics.Bitmap r25, int r26, org.telegram.messenger.MediaController.SavedFilterState r27) {
        /*
            r23 = this;
            r0 = r23
            r1 = r24
            r2 = r27
            r23.<init>(r24)
            r3 = 0
            r0.enhanceTool = r3
            r4 = 1
            r0.exposureTool = r4
            r5 = 2
            r0.contrastTool = r5
            r6 = 3
            r0.saturationTool = r6
            r7 = 4
            r0.warmthTool = r7
            r8 = 5
            r0.fadeTool = r8
            r8 = 6
            r0.highlightsTool = r8
            r8 = 7
            r0.shadowsTool = r8
            r8 = 8
            r0.vignetteTool = r8
            r8 = 9
            r0.grainTool = r8
            r8 = 10
            r0.sharpenTool = r8
            r8 = 11
            r0.tintShadowsTool = r8
            r8 = 12
            r0.tintHighlightsTool = r8
            org.telegram.ui.Components.RadioButton[] r8 = new org.telegram.ui.Components.RadioButton[r7]
            r0.curveRadioButton = r8
            if (r2 == 0) goto L_0x008a
            float r8 = r2.enhanceValue
            r0.enhanceValue = r8
            float r8 = r2.exposureValue
            r0.exposureValue = r8
            float r8 = r2.contrastValue
            r0.contrastValue = r8
            float r8 = r2.warmthValue
            r0.warmthValue = r8
            float r8 = r2.saturationValue
            r0.saturationValue = r8
            float r8 = r2.fadeValue
            r0.fadeValue = r8
            int r8 = r2.tintShadowsColor
            r0.tintShadowsColor = r8
            int r8 = r2.tintHighlightsColor
            r0.tintHighlightsColor = r8
            float r8 = r2.highlightsValue
            r0.highlightsValue = r8
            float r8 = r2.shadowsValue
            r0.shadowsValue = r8
            float r8 = r2.vignetteValue
            r0.vignetteValue = r8
            float r8 = r2.grainValue
            r0.grainValue = r8
            int r8 = r2.blurType
            r0.blurType = r8
            float r8 = r2.sharpenValue
            r0.sharpenValue = r8
            org.telegram.ui.Components.PhotoFilterView$CurvesToolValue r8 = r2.curvesToolValue
            r0.curvesToolValue = r8
            float r8 = r2.blurExcludeSize
            r0.blurExcludeSize = r8
            org.telegram.ui.Components.Point r8 = r2.blurExcludePoint
            r0.blurExcludePoint = r8
            float r8 = r2.blurExcludeBlurSize
            r0.blurExcludeBlurSize = r8
            float r8 = r2.blurAngle
            r0.blurAngle = r8
            r0.lastState = r2
            goto L_0x00a9
        L_0x008a:
            org.telegram.ui.Components.PhotoFilterView$CurvesToolValue r2 = new org.telegram.ui.Components.PhotoFilterView$CurvesToolValue
            r2.<init>()
            r0.curvesToolValue = r2
            r2 = 1051931443(0x3eb33333, float:0.35)
            r0.blurExcludeSize = r2
            org.telegram.ui.Components.Point r2 = new org.telegram.ui.Components.Point
            r8 = 1056964608(0x3var_, float:0.5)
            r2.<init>(r8, r8)
            r0.blurExcludePoint = r2
            r2 = 1041865114(0x3e19999a, float:0.15)
            r0.blurExcludeBlurSize = r2
            r2 = 1070141403(0x3fCLASSNAMEfdb, float:1.5707964)
            r0.blurAngle = r2
        L_0x00a9:
            r2 = r25
            r0.bitmapToEdit = r2
            r2 = r26
            r0.orientation = r2
            android.view.TextureView r2 = new android.view.TextureView
            r2.<init>(r1)
            r0.textureView = r2
            r8 = 51
            r9 = -1
            android.widget.FrameLayout$LayoutParams r10 = org.telegram.ui.Components.LayoutHelper.createFrame(r9, r9, r8)
            r0.addView(r2, r10)
            android.view.TextureView r2 = r0.textureView
            r2.setVisibility(r7)
            android.view.TextureView r2 = r0.textureView
            org.telegram.ui.Components.PhotoFilterView$1 r10 = new org.telegram.ui.Components.PhotoFilterView$1
            r10.<init>()
            r2.setSurfaceTextureListener(r10)
            org.telegram.ui.Components.PhotoFilterBlurControl r2 = new org.telegram.ui.Components.PhotoFilterBlurControl
            r2.<init>(r1)
            r0.blurControl = r2
            r2.setVisibility(r7)
            org.telegram.ui.Components.PhotoFilterBlurControl r2 = r0.blurControl
            android.widget.FrameLayout$LayoutParams r10 = org.telegram.ui.Components.LayoutHelper.createFrame(r9, r9, r8)
            r0.addView(r2, r10)
            org.telegram.ui.Components.PhotoFilterBlurControl r2 = r0.blurControl
            org.telegram.ui.Components.-$$Lambda$PhotoFilterView$wsvuAfZFAJpyt9V1ZYZMPETINLA r10 = new org.telegram.ui.Components.-$$Lambda$PhotoFilterView$wsvuAfZFAJpyt9V1ZYZMPETINLA
            r10.<init>()
            r2.setDelegate(r10)
            org.telegram.ui.Components.PhotoFilterCurvesControl r2 = new org.telegram.ui.Components.PhotoFilterCurvesControl
            org.telegram.ui.Components.PhotoFilterView$CurvesToolValue r10 = r0.curvesToolValue
            r2.<init>(r1, r10)
            r0.curvesControl = r2
            org.telegram.ui.Components.-$$Lambda$PhotoFilterView$Q8Q0QxhBgkn0x_QyOvZIPgaDQJM r10 = new org.telegram.ui.Components.-$$Lambda$PhotoFilterView$Q8Q0QxhBgkn0x_QyOvZIPgaDQJM
            r10.<init>()
            r2.setDelegate(r10)
            org.telegram.ui.Components.PhotoFilterCurvesControl r2 = r0.curvesControl
            r2.setVisibility(r7)
            org.telegram.ui.Components.PhotoFilterCurvesControl r2 = r0.curvesControl
            android.widget.FrameLayout$LayoutParams r10 = org.telegram.ui.Components.LayoutHelper.createFrame(r9, r9, r8)
            r0.addView(r2, r10)
            android.widget.FrameLayout r2 = new android.widget.FrameLayout
            r2.<init>(r1)
            r0.toolsView = r2
            r10 = 186(0xba, float:2.6E-43)
            r11 = 83
            android.widget.FrameLayout$LayoutParams r10 = org.telegram.ui.Components.LayoutHelper.createFrame(r9, r10, r11)
            r0.addView(r2, r10)
            android.widget.FrameLayout r2 = new android.widget.FrameLayout
            r2.<init>(r1)
            r10 = -16777216(0xfffffffffvar_, float:-1.7014118E38)
            r2.setBackgroundColor(r10)
            android.widget.FrameLayout r10 = r0.toolsView
            r12 = 48
            android.widget.FrameLayout$LayoutParams r11 = org.telegram.ui.Components.LayoutHelper.createFrame(r9, r12, r11)
            r10.addView(r2, r11)
            android.widget.TextView r10 = new android.widget.TextView
            r10.<init>(r1)
            r0.cancelTextView = r10
            r11 = 1096810496(0x41600000, float:14.0)
            r10.setTextSize(r4, r11)
            android.widget.TextView r10 = r0.cancelTextView
            r10.setTextColor(r9)
            android.widget.TextView r10 = r0.cancelTextView
            r13 = 17
            r10.setGravity(r13)
            android.widget.TextView r10 = r0.cancelTextView
            r14 = -12763843(0xffffffffff3d3d3d, float:-2.5154206E38)
            android.graphics.drawable.Drawable r15 = org.telegram.ui.ActionBar.Theme.createSelectorDrawable(r14, r3)
            r10.setBackgroundDrawable(r15)
            android.widget.TextView r10 = r0.cancelTextView
            r15 = 1101004800(0x41a00000, float:20.0)
            int r6 = org.telegram.messenger.AndroidUtilities.dp(r15)
            int r7 = org.telegram.messenger.AndroidUtilities.dp(r15)
            r10.setPadding(r6, r3, r7, r3)
            android.widget.TextView r6 = r0.cancelTextView
            r7 = 2131624483(0x7f0e0223, float:1.8876147E38)
            java.lang.String r10 = "Cancel"
            java.lang.String r7 = org.telegram.messenger.LocaleController.getString(r10, r7)
            java.lang.String r7 = r7.toUpperCase()
            r6.setText(r7)
            android.widget.TextView r6 = r0.cancelTextView
            java.lang.String r7 = "fonts/rmedium.ttf"
            android.graphics.Typeface r7 = org.telegram.messenger.AndroidUtilities.getTypeface(r7)
            r6.setTypeface(r7)
            android.widget.TextView r6 = r0.cancelTextView
            r7 = -2
            android.widget.FrameLayout$LayoutParams r10 = org.telegram.ui.Components.LayoutHelper.createFrame(r7, r9, r8)
            r2.addView(r6, r10)
            android.widget.TextView r6 = new android.widget.TextView
            r6.<init>(r1)
            r0.doneTextView = r6
            r6.setTextSize(r4, r11)
            android.widget.TextView r6 = r0.doneTextView
            java.lang.String r10 = "dialogFloatingButton"
            int r10 = org.telegram.ui.ActionBar.Theme.getColor(r10)
            r6.setTextColor(r10)
            android.widget.TextView r6 = r0.doneTextView
            r6.setGravity(r13)
            android.widget.TextView r6 = r0.doneTextView
            android.graphics.drawable.Drawable r10 = org.telegram.ui.ActionBar.Theme.createSelectorDrawable(r14, r3)
            r6.setBackgroundDrawable(r10)
            android.widget.TextView r6 = r0.doneTextView
            int r10 = org.telegram.messenger.AndroidUtilities.dp(r15)
            int r11 = org.telegram.messenger.AndroidUtilities.dp(r15)
            r6.setPadding(r10, r3, r11, r3)
            android.widget.TextView r6 = r0.doneTextView
            r10 = 2131624972(0x7f0e040c, float:1.8877139E38)
            java.lang.String r11 = "Done"
            java.lang.String r10 = org.telegram.messenger.LocaleController.getString(r11, r10)
            java.lang.String r10 = r10.toUpperCase()
            r6.setText(r10)
            android.widget.TextView r6 = r0.doneTextView
            java.lang.String r10 = "fonts/rmedium.ttf"
            android.graphics.Typeface r10 = org.telegram.messenger.AndroidUtilities.getTypeface(r10)
            r6.setTypeface(r10)
            android.widget.TextView r6 = r0.doneTextView
            r10 = 53
            android.widget.FrameLayout$LayoutParams r10 = org.telegram.ui.Components.LayoutHelper.createFrame(r7, r9, r10)
            r2.addView(r6, r10)
            android.widget.LinearLayout r6 = new android.widget.LinearLayout
            r6.<init>(r1)
            android.widget.FrameLayout$LayoutParams r10 = org.telegram.ui.Components.LayoutHelper.createFrame(r7, r9, r4)
            r2.addView(r6, r10)
            android.widget.ImageView r2 = new android.widget.ImageView
            r2.<init>(r1)
            r0.tuneItem = r2
            android.widget.ImageView$ScaleType r10 = android.widget.ImageView.ScaleType.CENTER
            r2.setScaleType(r10)
            android.widget.ImageView r2 = r0.tuneItem
            r10 = 2131165799(0x7var_, float:1.7945825E38)
            r2.setImageResource(r10)
            android.widget.ImageView r2 = r0.tuneItem
            android.graphics.PorterDuffColorFilter r10 = new android.graphics.PorterDuffColorFilter
            java.lang.String r11 = "dialogFloatingButton"
            int r11 = org.telegram.ui.ActionBar.Theme.getColor(r11)
            android.graphics.PorterDuff$Mode r13 = android.graphics.PorterDuff.Mode.MULTIPLY
            r10.<init>(r11, r13)
            r2.setColorFilter(r10)
            android.widget.ImageView r2 = r0.tuneItem
            r10 = 1090519039(0x40ffffff, float:7.9999995)
            android.graphics.drawable.Drawable r11 = org.telegram.ui.ActionBar.Theme.createSelectorDrawable(r10)
            r2.setBackgroundDrawable(r11)
            android.widget.ImageView r2 = r0.tuneItem
            r11 = 56
            android.widget.LinearLayout$LayoutParams r13 = org.telegram.ui.Components.LayoutHelper.createLinear(r11, r12)
            r6.addView(r2, r13)
            android.widget.ImageView r2 = r0.tuneItem
            org.telegram.ui.Components.-$$Lambda$PhotoFilterView$4hvar_H88dpzJN2DxB_cny05r_QI r13 = new org.telegram.ui.Components.-$$Lambda$PhotoFilterView$4hvar_H88dpzJN2DxB_cny05r_QI
            r13.<init>()
            r2.setOnClickListener(r13)
            android.widget.ImageView r2 = new android.widget.ImageView
            r2.<init>(r1)
            r0.blurItem = r2
            android.widget.ImageView$ScaleType r13 = android.widget.ImageView.ScaleType.CENTER
            r2.setScaleType(r13)
            android.widget.ImageView r2 = r0.blurItem
            r13 = 2131165940(0x7var_f4, float:1.7946111E38)
            r2.setImageResource(r13)
            android.widget.ImageView r2 = r0.blurItem
            android.graphics.drawable.Drawable r13 = org.telegram.ui.ActionBar.Theme.createSelectorDrawable(r10)
            r2.setBackgroundDrawable(r13)
            android.widget.ImageView r2 = r0.blurItem
            android.widget.LinearLayout$LayoutParams r13 = org.telegram.ui.Components.LayoutHelper.createLinear(r11, r12)
            r6.addView(r2, r13)
            android.widget.ImageView r2 = r0.blurItem
            org.telegram.ui.Components.-$$Lambda$PhotoFilterView$DKhAjcvz5psQCw7kBhsHKlWIYQM r13 = new org.telegram.ui.Components.-$$Lambda$PhotoFilterView$DKhAjcvz5psQCw7kBhsHKlWIYQM
            r13.<init>()
            r2.setOnClickListener(r13)
            android.widget.ImageView r2 = new android.widget.ImageView
            r2.<init>(r1)
            r0.curveItem = r2
            android.widget.ImageView$ScaleType r13 = android.widget.ImageView.ScaleType.CENTER
            r2.setScaleType(r13)
            android.widget.ImageView r2 = r0.curveItem
            r13 = 2131165942(0x7var_f6, float:1.7946115E38)
            r2.setImageResource(r13)
            android.widget.ImageView r2 = r0.curveItem
            android.graphics.drawable.Drawable r10 = org.telegram.ui.ActionBar.Theme.createSelectorDrawable(r10)
            r2.setBackgroundDrawable(r10)
            android.widget.ImageView r2 = r0.curveItem
            android.widget.LinearLayout$LayoutParams r10 = org.telegram.ui.Components.LayoutHelper.createLinear(r11, r12)
            r6.addView(r2, r10)
            android.widget.ImageView r2 = r0.curveItem
            org.telegram.ui.Components.-$$Lambda$PhotoFilterView$F_KOwGrCZ7lau2u49edpZx3V5ug r6 = new org.telegram.ui.Components.-$$Lambda$PhotoFilterView$F_KOwGrCZ7lau2u49edpZx3V5ug
            r6.<init>()
            r2.setOnClickListener(r6)
            org.telegram.ui.Components.RecyclerListView r2 = new org.telegram.ui.Components.RecyclerListView
            r2.<init>(r1)
            r0.recyclerListView = r2
            androidx.recyclerview.widget.LinearLayoutManager r2 = new androidx.recyclerview.widget.LinearLayoutManager
            r2.<init>(r1)
            r2.setOrientation(r4)
            org.telegram.ui.Components.RecyclerListView r6 = r0.recyclerListView
            r6.setLayoutManager(r2)
            org.telegram.ui.Components.RecyclerListView r2 = r0.recyclerListView
            r2.setClipToPadding(r3)
            org.telegram.ui.Components.RecyclerListView r2 = r0.recyclerListView
            r2.setOverScrollMode(r5)
            org.telegram.ui.Components.RecyclerListView r2 = r0.recyclerListView
            org.telegram.ui.Components.PhotoFilterView$ToolsAdapter r6 = new org.telegram.ui.Components.PhotoFilterView$ToolsAdapter
            r6.<init>(r1)
            r2.setAdapter(r6)
            android.widget.FrameLayout r2 = r0.toolsView
            org.telegram.ui.Components.RecyclerListView r6 = r0.recyclerListView
            r10 = 120(0x78, float:1.68E-43)
            android.widget.FrameLayout$LayoutParams r8 = org.telegram.ui.Components.LayoutHelper.createFrame(r9, r10, r8)
            r2.addView(r6, r8)
            android.widget.FrameLayout r2 = new android.widget.FrameLayout
            r2.<init>(r1)
            r0.curveLayout = r2
            r6 = 4
            r2.setVisibility(r6)
            android.widget.FrameLayout r2 = r0.toolsView
            android.widget.FrameLayout r6 = r0.curveLayout
            r16 = -1
            r17 = 1117519872(0x429CLASSNAME, float:78.0)
            r18 = 1
            r19 = 0
            r20 = 1109393408(0x42200000, float:40.0)
            r21 = 0
            r22 = 0
            android.widget.FrameLayout$LayoutParams r8 = org.telegram.ui.Components.LayoutHelper.createFrame(r16, r17, r18, r19, r20, r21, r22)
            r2.addView(r6, r8)
            android.widget.LinearLayout r2 = new android.widget.LinearLayout
            r2.<init>(r1)
            r2.setOrientation(r3)
            android.widget.FrameLayout r6 = r0.curveLayout
            android.widget.FrameLayout$LayoutParams r7 = org.telegram.ui.Components.LayoutHelper.createFrame(r7, r7, r4)
            r6.addView(r2, r7)
            r6 = 0
        L_0x0304:
            r7 = 4
            if (r6 >= r7) goto L_0x046c
            android.widget.FrameLayout r7 = new android.widget.FrameLayout
            r7.<init>(r1)
            java.lang.Integer r8 = java.lang.Integer.valueOf(r6)
            r7.setTag(r8)
            org.telegram.ui.Components.RadioButton[] r8 = r0.curveRadioButton
            org.telegram.ui.Components.RadioButton r10 = new org.telegram.ui.Components.RadioButton
            r10.<init>(r1)
            r8[r6] = r10
            org.telegram.ui.Components.RadioButton[] r8 = r0.curveRadioButton
            r8 = r8[r6]
            int r10 = org.telegram.messenger.AndroidUtilities.dp(r15)
            r8.setSize(r10)
            org.telegram.ui.Components.RadioButton[] r8 = r0.curveRadioButton
            r8 = r8[r6]
            r10 = 30
            r11 = 30
            r12 = 49
            android.widget.FrameLayout$LayoutParams r10 = org.telegram.ui.Components.LayoutHelper.createFrame(r10, r11, r12)
            r7.addView(r8, r10)
            android.widget.TextView r8 = new android.widget.TextView
            r8.<init>(r1)
            r10 = 1094713344(0x41400000, float:12.0)
            r8.setTextSize(r4, r10)
            r10 = 16
            r8.setGravity(r10)
            if (r6 != 0) goto L_0x0381
            r10 = 2131624820(0x7f0e0374, float:1.887683E38)
            java.lang.String r11 = "CurvesAll"
            java.lang.String r10 = org.telegram.messenger.LocaleController.getString(r11, r10)
            java.lang.StringBuilder r11 = new java.lang.StringBuilder
            r11.<init>()
            java.lang.String r12 = r10.substring(r3, r4)
            java.lang.String r12 = r12.toUpperCase()
            r11.append(r12)
            java.lang.String r10 = r10.substring(r4)
            java.lang.String r10 = r10.toLowerCase()
            r11.append(r10)
            java.lang.String r10 = r11.toString()
            r8.setText(r10)
            r8.setTextColor(r9)
            org.telegram.ui.Components.RadioButton[] r10 = r0.curveRadioButton
            r10 = r10[r6]
            r10.setColor(r9, r9)
        L_0x037e:
            r10 = 3
            goto L_0x0432
        L_0x0381:
            if (r6 != r4) goto L_0x03bc
            r10 = 2131624823(0x7f0e0377, float:1.8876837E38)
            java.lang.String r11 = "CurvesRed"
            java.lang.String r10 = org.telegram.messenger.LocaleController.getString(r11, r10)
            java.lang.StringBuilder r11 = new java.lang.StringBuilder
            r11.<init>()
            java.lang.String r12 = r10.substring(r3, r4)
            java.lang.String r12 = r12.toUpperCase()
            r11.append(r12)
            java.lang.String r10 = r10.substring(r4)
            java.lang.String r10 = r10.toLowerCase()
            r11.append(r10)
            java.lang.String r10 = r11.toString()
            r8.setText(r10)
            r10 = -1684147(0xffffffffffe64d4d, float:NaN)
            r8.setTextColor(r10)
            org.telegram.ui.Components.RadioButton[] r11 = r0.curveRadioButton
            r11 = r11[r6]
            r11.setColor(r10, r10)
            goto L_0x037e
        L_0x03bc:
            if (r6 != r5) goto L_0x03f7
            r10 = 2131624822(0x7f0e0376, float:1.8876835E38)
            java.lang.String r11 = "CurvesGreen"
            java.lang.String r10 = org.telegram.messenger.LocaleController.getString(r11, r10)
            java.lang.StringBuilder r11 = new java.lang.StringBuilder
            r11.<init>()
            java.lang.String r12 = r10.substring(r3, r4)
            java.lang.String r12 = r12.toUpperCase()
            r11.append(r12)
            java.lang.String r10 = r10.substring(r4)
            java.lang.String r10 = r10.toLowerCase()
            r11.append(r10)
            java.lang.String r10 = r11.toString()
            r8.setText(r10)
            r10 = -10831009(0xffffffffff5abb5f, float:-2.9074459E38)
            r8.setTextColor(r10)
            org.telegram.ui.Components.RadioButton[] r11 = r0.curveRadioButton
            r11 = r11[r6]
            r11.setColor(r10, r10)
            goto L_0x037e
        L_0x03f7:
            r10 = 3
            if (r6 != r10) goto L_0x0432
            r11 = 2131624821(0x7f0e0375, float:1.8876833E38)
            java.lang.String r12 = "CurvesBlue"
            java.lang.String r11 = org.telegram.messenger.LocaleController.getString(r12, r11)
            java.lang.StringBuilder r12 = new java.lang.StringBuilder
            r12.<init>()
            java.lang.String r13 = r11.substring(r3, r4)
            java.lang.String r13 = r13.toUpperCase()
            r12.append(r13)
            java.lang.String r11 = r11.substring(r4)
            java.lang.String r11 = r11.toLowerCase()
            r12.append(r11)
            java.lang.String r11 = r12.toString()
            r8.setText(r11)
            r11 = -12734994(0xffffffffff3dadee, float:-2.5212719E38)
            r8.setTextColor(r11)
            org.telegram.ui.Components.RadioButton[] r12 = r0.curveRadioButton
            r12 = r12[r6]
            r12.setColor(r11, r11)
        L_0x0432:
            r16 = -2
            r17 = -1073741824(0xffffffffCLASSNAME, float:-2.0)
            r18 = 49
            r19 = 0
            r20 = 1108869120(0x42180000, float:38.0)
            r21 = 0
            r22 = 0
            android.widget.FrameLayout$LayoutParams r11 = org.telegram.ui.Components.LayoutHelper.createFrame(r16, r17, r18, r19, r20, r21, r22)
            r7.addView(r8, r11)
            r17 = -2
            if (r6 != 0) goto L_0x044f
            r8 = 0
            r18 = 0
            goto L_0x0453
        L_0x044f:
            r8 = 1106247680(0x41var_, float:30.0)
            r18 = 1106247680(0x41var_, float:30.0)
        L_0x0453:
            r19 = 0
            r20 = 0
            r21 = 0
            android.widget.LinearLayout$LayoutParams r8 = org.telegram.ui.Components.LayoutHelper.createLinear(r16, r17, r18, r19, r20, r21)
            r2.addView(r7, r8)
            org.telegram.ui.Components.-$$Lambda$PhotoFilterView$L20A01o2AsZwohbyfeeuMPbGEQI r8 = new org.telegram.ui.Components.-$$Lambda$PhotoFilterView$L20A01o2AsZwohbyfeeuMPbGEQI
            r8.<init>()
            r7.setOnClickListener(r8)
            int r6 = r6 + 1
            goto L_0x0304
        L_0x046c:
            android.widget.FrameLayout r2 = new android.widget.FrameLayout
            r2.<init>(r1)
            r0.blurLayout = r2
            r3 = 4
            r2.setVisibility(r3)
            android.widget.FrameLayout r2 = r0.toolsView
            android.widget.FrameLayout r3 = r0.blurLayout
            r5 = 280(0x118, float:3.92E-43)
            r6 = 1114636288(0x42700000, float:60.0)
            r7 = 1
            r8 = 0
            r9 = 1109393408(0x42200000, float:40.0)
            r10 = 0
            r11 = 0
            android.widget.FrameLayout$LayoutParams r5 = org.telegram.ui.Components.LayoutHelper.createFrame(r5, r6, r7, r8, r9, r10, r11)
            r2.addView(r3, r5)
            android.widget.TextView r2 = new android.widget.TextView
            r2.<init>(r1)
            r0.blurOffButton = r2
            r3 = 1073741824(0x40000000, float:2.0)
            int r5 = org.telegram.messenger.AndroidUtilities.dp(r3)
            r2.setCompoundDrawablePadding(r5)
            android.widget.TextView r2 = r0.blurOffButton
            r5 = 1095761920(0x41500000, float:13.0)
            r2.setTextSize(r4, r5)
            android.widget.TextView r2 = r0.blurOffButton
            r2.setGravity(r4)
            android.widget.TextView r2 = r0.blurOffButton
            r6 = 2131624435(0x7f0e01f3, float:1.887605E38)
            java.lang.String r7 = "BlurOff"
            java.lang.String r6 = org.telegram.messenger.LocaleController.getString(r7, r6)
            r2.setText(r6)
            android.widget.FrameLayout r2 = r0.blurLayout
            android.widget.TextView r6 = r0.blurOffButton
            r7 = 80
            r8 = 1114636288(0x42700000, float:60.0)
            android.widget.FrameLayout$LayoutParams r7 = org.telegram.ui.Components.LayoutHelper.createFrame(r7, r8)
            r2.addView(r6, r7)
            android.widget.TextView r2 = r0.blurOffButton
            org.telegram.ui.Components.-$$Lambda$PhotoFilterView$EHBGA4T-Wcgx03trAGzstG0gXqg r6 = new org.telegram.ui.Components.-$$Lambda$PhotoFilterView$EHBGA4T-Wcgx03trAGzstG0gXqg
            r6.<init>()
            r2.setOnClickListener(r6)
            android.widget.TextView r2 = new android.widget.TextView
            r2.<init>(r1)
            r0.blurRadialButton = r2
            int r6 = org.telegram.messenger.AndroidUtilities.dp(r3)
            r2.setCompoundDrawablePadding(r6)
            android.widget.TextView r2 = r0.blurRadialButton
            r2.setTextSize(r4, r5)
            android.widget.TextView r2 = r0.blurRadialButton
            r2.setGravity(r4)
            android.widget.TextView r2 = r0.blurRadialButton
            r6 = 2131624436(0x7f0e01f4, float:1.8876052E38)
            java.lang.String r7 = "BlurRadial"
            java.lang.String r6 = org.telegram.messenger.LocaleController.getString(r7, r6)
            r2.setText(r6)
            android.widget.FrameLayout r2 = r0.blurLayout
            android.widget.TextView r6 = r0.blurRadialButton
            r7 = 80
            r8 = 1117782016(0x42a00000, float:80.0)
            r9 = 51
            r10 = 1120403456(0x42CLASSNAME, float:100.0)
            r12 = 0
            r13 = 0
            android.widget.FrameLayout$LayoutParams r7 = org.telegram.ui.Components.LayoutHelper.createFrame(r7, r8, r9, r10, r11, r12, r13)
            r2.addView(r6, r7)
            android.widget.TextView r2 = r0.blurRadialButton
            org.telegram.ui.Components.-$$Lambda$PhotoFilterView$SHP9jNXWESNEp5KZC_qRcZYRL1Y r6 = new org.telegram.ui.Components.-$$Lambda$PhotoFilterView$SHP9jNXWESNEp5KZC_qRcZYRL1Y
            r6.<init>()
            r2.setOnClickListener(r6)
            android.widget.TextView r2 = new android.widget.TextView
            r2.<init>(r1)
            r0.blurLinearButton = r2
            int r1 = org.telegram.messenger.AndroidUtilities.dp(r3)
            r2.setCompoundDrawablePadding(r1)
            android.widget.TextView r1 = r0.blurLinearButton
            r1.setTextSize(r4, r5)
            android.widget.TextView r1 = r0.blurLinearButton
            r1.setGravity(r4)
            android.widget.TextView r1 = r0.blurLinearButton
            r2 = 2131624434(0x7f0e01f2, float:1.8876048E38)
            java.lang.String r3 = "BlurLinear"
            java.lang.String r2 = org.telegram.messenger.LocaleController.getString(r3, r2)
            r1.setText(r2)
            android.widget.FrameLayout r1 = r0.blurLayout
            android.widget.TextView r2 = r0.blurLinearButton
            r3 = 80
            r4 = 1117782016(0x42a00000, float:80.0)
            r5 = 51
            r6 = 1128792064(0x43480000, float:200.0)
            r7 = 0
            r8 = 0
            r9 = 0
            android.widget.FrameLayout$LayoutParams r3 = org.telegram.ui.Components.LayoutHelper.createFrame(r3, r4, r5, r6, r7, r8, r9)
            r1.addView(r2, r3)
            android.widget.TextView r1 = r0.blurLinearButton
            org.telegram.ui.Components.-$$Lambda$PhotoFilterView$TdTpIDy0jwl-mIhu0bpchkPiKg8 r2 = new org.telegram.ui.Components.-$$Lambda$PhotoFilterView$TdTpIDy0jwl-mIhu0bpchkPiKg8
            r2.<init>()
            r1.setOnClickListener(r2)
            r23.updateSelectedBlurType()
            int r1 = android.os.Build.VERSION.SDK_INT
            r2 = 21
            if (r1 < r2) goto L_0x057b
            android.view.TextureView r1 = r0.textureView
            android.view.ViewGroup$LayoutParams r1 = r1.getLayoutParams()
            android.widget.FrameLayout$LayoutParams r1 = (android.widget.FrameLayout.LayoutParams) r1
            int r2 = org.telegram.messenger.AndroidUtilities.statusBarHeight
            r1.topMargin = r2
            org.telegram.ui.Components.PhotoFilterCurvesControl r1 = r0.curvesControl
            android.view.ViewGroup$LayoutParams r1 = r1.getLayoutParams()
            android.widget.FrameLayout$LayoutParams r1 = (android.widget.FrameLayout.LayoutParams) r1
            int r2 = org.telegram.messenger.AndroidUtilities.statusBarHeight
            r1.topMargin = r2
        L_0x057b:
            return
        */
        throw new UnsupportedOperationException("Method not decompiled: org.telegram.ui.Components.PhotoFilterView.<init>(android.content.Context, android.graphics.Bitmap, int, org.telegram.messenger.MediaController$SavedFilterState):void");
    }

    public /* synthetic */ void lambda$new$0$PhotoFilterView(Point point, float f, float f2, float f3) {
        this.blurExcludeSize = f2;
        this.blurExcludePoint = point;
        this.blurExcludeBlurSize = f;
        this.blurAngle = f3;
        EGLThread eGLThread = this.eglThread;
        if (eGLThread != null) {
            eGLThread.requestRender(false);
        }
    }

    public /* synthetic */ void lambda$new$1$PhotoFilterView() {
        EGLThread eGLThread = this.eglThread;
        if (eGLThread != null) {
            eGLThread.requestRender(false);
        }
    }

    public /* synthetic */ void lambda$new$2$PhotoFilterView(View view) {
        this.selectedTool = 0;
        this.tuneItem.setColorFilter(new PorterDuffColorFilter(Theme.getColor("dialogFloatingButton"), PorterDuff.Mode.MULTIPLY));
        this.blurItem.setColorFilter((ColorFilter) null);
        this.curveItem.setColorFilter((ColorFilter) null);
        switchMode();
    }

    public /* synthetic */ void lambda$new$3$PhotoFilterView(View view) {
        this.selectedTool = 1;
        this.tuneItem.setColorFilter((ColorFilter) null);
        this.blurItem.setColorFilter(new PorterDuffColorFilter(Theme.getColor("dialogFloatingButton"), PorterDuff.Mode.MULTIPLY));
        this.curveItem.setColorFilter((ColorFilter) null);
        switchMode();
    }

    public /* synthetic */ void lambda$new$4$PhotoFilterView(View view) {
        this.selectedTool = 2;
        this.tuneItem.setColorFilter((ColorFilter) null);
        this.blurItem.setColorFilter((ColorFilter) null);
        this.curveItem.setColorFilter(new PorterDuffColorFilter(Theme.getColor("dialogFloatingButton"), PorterDuff.Mode.MULTIPLY));
        switchMode();
    }

    public /* synthetic */ void lambda$new$5$PhotoFilterView(View view) {
        int intValue = ((Integer) view.getTag()).intValue();
        this.curvesToolValue.activeType = intValue;
        int i = 0;
        while (i < 4) {
            this.curveRadioButton[i].setChecked(i == intValue, true);
            i++;
        }
        this.curvesControl.invalidate();
    }

    public /* synthetic */ void lambda$new$6$PhotoFilterView(View view) {
        this.blurType = 0;
        updateSelectedBlurType();
        this.blurControl.setVisibility(4);
        EGLThread eGLThread = this.eglThread;
        if (eGLThread != null) {
            eGLThread.requestRender(false);
        }
    }

    public /* synthetic */ void lambda$new$7$PhotoFilterView(View view) {
        this.blurType = 1;
        updateSelectedBlurType();
        this.blurControl.setVisibility(0);
        this.blurControl.setType(1);
        EGLThread eGLThread = this.eglThread;
        if (eGLThread != null) {
            eGLThread.requestRender(false);
        }
    }

    public /* synthetic */ void lambda$new$8$PhotoFilterView(View view) {
        this.blurType = 2;
        updateSelectedBlurType();
        this.blurControl.setVisibility(0);
        this.blurControl.setType(0);
        EGLThread eGLThread = this.eglThread;
        if (eGLThread != null) {
            eGLThread.requestRender(false);
        }
    }

    public void updateColors() {
        TextView textView = this.doneTextView;
        if (textView != null) {
            textView.setTextColor(Theme.getColor("dialogFloatingButton"));
        }
        ImageView imageView = this.tuneItem;
        if (!(imageView == null || imageView.getColorFilter() == null)) {
            this.tuneItem.setColorFilter(new PorterDuffColorFilter(Theme.getColor("dialogFloatingButton"), PorterDuff.Mode.MULTIPLY));
        }
        ImageView imageView2 = this.blurItem;
        if (!(imageView2 == null || imageView2.getColorFilter() == null)) {
            this.blurItem.setColorFilter(new PorterDuffColorFilter(Theme.getColor("dialogFloatingButton"), PorterDuff.Mode.MULTIPLY));
        }
        ImageView imageView3 = this.curveItem;
        if (!(imageView3 == null || imageView3.getColorFilter() == null)) {
            this.curveItem.setColorFilter(new PorterDuffColorFilter(Theme.getColor("dialogFloatingButton"), PorterDuff.Mode.MULTIPLY));
        }
        updateSelectedBlurType();
    }

    private void updateSelectedBlurType() {
        int i = this.blurType;
        if (i == 0) {
            Drawable mutate = this.blurOffButton.getContext().getResources().getDrawable(NUM).mutate();
            mutate.setColorFilter(new PorterDuffColorFilter(Theme.getColor("dialogFloatingButton"), PorterDuff.Mode.MULTIPLY));
            this.blurOffButton.setCompoundDrawablesWithIntrinsicBounds((Drawable) null, mutate, (Drawable) null, (Drawable) null);
            this.blurOffButton.setTextColor(Theme.getColor("dialogFloatingButton"));
            this.blurRadialButton.setCompoundDrawablesWithIntrinsicBounds(0, NUM, 0, 0);
            this.blurRadialButton.setTextColor(-1);
            this.blurLinearButton.setCompoundDrawablesWithIntrinsicBounds(0, NUM, 0, 0);
            this.blurLinearButton.setTextColor(-1);
        } else if (i == 1) {
            this.blurOffButton.setCompoundDrawablesWithIntrinsicBounds(0, NUM, 0, 0);
            this.blurOffButton.setTextColor(-1);
            Drawable mutate2 = this.blurOffButton.getContext().getResources().getDrawable(NUM).mutate();
            mutate2.setColorFilter(new PorterDuffColorFilter(Theme.getColor("dialogFloatingButton"), PorterDuff.Mode.MULTIPLY));
            this.blurRadialButton.setCompoundDrawablesWithIntrinsicBounds((Drawable) null, mutate2, (Drawable) null, (Drawable) null);
            this.blurRadialButton.setTextColor(Theme.getColor("dialogFloatingButton"));
            this.blurLinearButton.setCompoundDrawablesWithIntrinsicBounds(0, NUM, 0, 0);
            this.blurLinearButton.setTextColor(-1);
        } else if (i == 2) {
            this.blurOffButton.setCompoundDrawablesWithIntrinsicBounds(0, NUM, 0, 0);
            this.blurOffButton.setTextColor(-1);
            this.blurRadialButton.setCompoundDrawablesWithIntrinsicBounds(0, NUM, 0, 0);
            this.blurRadialButton.setTextColor(-1);
            Drawable mutate3 = this.blurOffButton.getContext().getResources().getDrawable(NUM).mutate();
            mutate3.setColorFilter(new PorterDuffColorFilter(Theme.getColor("dialogFloatingButton"), PorterDuff.Mode.MULTIPLY));
            this.blurLinearButton.setCompoundDrawablesWithIntrinsicBounds((Drawable) null, mutate3, (Drawable) null, (Drawable) null);
            this.blurLinearButton.setTextColor(Theme.getColor("dialogFloatingButton"));
        }
    }

    public MediaController.SavedFilterState getSavedFilterState() {
        MediaController.SavedFilterState savedFilterState = new MediaController.SavedFilterState();
        savedFilterState.enhanceValue = this.enhanceValue;
        savedFilterState.exposureValue = this.exposureValue;
        savedFilterState.contrastValue = this.contrastValue;
        savedFilterState.warmthValue = this.warmthValue;
        savedFilterState.saturationValue = this.saturationValue;
        savedFilterState.fadeValue = this.fadeValue;
        savedFilterState.tintShadowsColor = this.tintShadowsColor;
        savedFilterState.tintHighlightsColor = this.tintHighlightsColor;
        savedFilterState.highlightsValue = this.highlightsValue;
        savedFilterState.shadowsValue = this.shadowsValue;
        savedFilterState.vignetteValue = this.vignetteValue;
        savedFilterState.grainValue = this.grainValue;
        savedFilterState.blurType = this.blurType;
        savedFilterState.sharpenValue = this.sharpenValue;
        savedFilterState.curvesToolValue = this.curvesToolValue;
        savedFilterState.blurExcludeSize = this.blurExcludeSize;
        savedFilterState.blurExcludePoint = this.blurExcludePoint;
        savedFilterState.blurExcludeBlurSize = this.blurExcludeBlurSize;
        savedFilterState.blurAngle = this.blurAngle;
        return savedFilterState;
    }

    public boolean hasChanges() {
        MediaController.SavedFilterState savedFilterState = this.lastState;
        if (savedFilterState != null) {
            if (this.enhanceValue == savedFilterState.enhanceValue && this.contrastValue == savedFilterState.contrastValue && this.highlightsValue == savedFilterState.highlightsValue && this.exposureValue == savedFilterState.exposureValue && this.warmthValue == savedFilterState.warmthValue && this.saturationValue == savedFilterState.saturationValue && this.vignetteValue == savedFilterState.vignetteValue && this.shadowsValue == savedFilterState.shadowsValue && this.grainValue == savedFilterState.grainValue && this.sharpenValue == savedFilterState.sharpenValue && this.fadeValue == savedFilterState.fadeValue && this.tintHighlightsColor == savedFilterState.tintHighlightsColor && this.tintShadowsColor == savedFilterState.tintShadowsColor && this.curvesToolValue.shouldBeSkipped()) {
                return false;
            }
            return true;
        } else if (this.enhanceValue == 0.0f && this.contrastValue == 0.0f && this.highlightsValue == 0.0f && this.exposureValue == 0.0f && this.warmthValue == 0.0f && this.saturationValue == 0.0f && this.vignetteValue == 0.0f && this.shadowsValue == 0.0f && this.grainValue == 0.0f && this.sharpenValue == 0.0f && this.fadeValue == 0.0f && this.tintHighlightsColor == 0 && this.tintShadowsColor == 0 && this.curvesToolValue.shouldBeSkipped()) {
            return false;
        } else {
            return true;
        }
    }

    public void onTouch(MotionEvent motionEvent) {
        if (motionEvent.getActionMasked() == 0 || motionEvent.getActionMasked() == 5) {
            FrameLayout.LayoutParams layoutParams = (FrameLayout.LayoutParams) this.textureView.getLayoutParams();
            if (layoutParams != null && motionEvent.getX() >= ((float) layoutParams.leftMargin) && motionEvent.getY() >= ((float) layoutParams.topMargin) && motionEvent.getX() <= ((float) (layoutParams.leftMargin + layoutParams.width)) && motionEvent.getY() <= ((float) (layoutParams.topMargin + layoutParams.height))) {
                setShowOriginal(true);
            }
        } else if (motionEvent.getActionMasked() == 1 || motionEvent.getActionMasked() == 6) {
            setShowOriginal(false);
        }
    }

    private void setShowOriginal(boolean z) {
        if (this.showOriginal != z) {
            this.showOriginal = z;
            EGLThread eGLThread = this.eglThread;
            if (eGLThread != null) {
                eGLThread.requestRender(false);
            }
        }
    }

    public void switchMode() {
        int i = this.selectedTool;
        if (i == 0) {
            this.blurControl.setVisibility(4);
            this.blurLayout.setVisibility(4);
            this.curveLayout.setVisibility(4);
            this.curvesControl.setVisibility(4);
            this.recyclerListView.setVisibility(0);
        } else if (i == 1) {
            this.recyclerListView.setVisibility(4);
            this.curveLayout.setVisibility(4);
            this.curvesControl.setVisibility(4);
            this.blurLayout.setVisibility(0);
            if (this.blurType != 0) {
                this.blurControl.setVisibility(0);
            }
            updateSelectedBlurType();
        } else if (i == 2) {
            this.recyclerListView.setVisibility(4);
            this.blurLayout.setVisibility(4);
            this.blurControl.setVisibility(4);
            this.curveLayout.setVisibility(0);
            this.curvesControl.setVisibility(0);
            this.curvesToolValue.activeType = 0;
            int i2 = 0;
            while (i2 < 4) {
                this.curveRadioButton[i2].setChecked(i2 == 0, false);
                i2++;
            }
        }
    }

    public void shutdown() {
        EGLThread eGLThread = this.eglThread;
        if (eGLThread != null) {
            eGLThread.shutdown();
            this.eglThread = null;
        }
        this.textureView.setVisibility(8);
    }

    public void init() {
        this.textureView.setVisibility(0);
    }

    public Bitmap getBitmap() {
        EGLThread eGLThread = this.eglThread;
        if (eGLThread != null) {
            return eGLThread.getTexture();
        }
        return null;
    }

    private void fixLayout(int i, int i2) {
        int i3;
        float f;
        float f2;
        float f3;
        if (this.bitmapToEdit != null) {
            int dp = i - AndroidUtilities.dp(28.0f);
            int dp2 = i2 - (AndroidUtilities.dp(214.0f) + (Build.VERSION.SDK_INT >= 21 ? AndroidUtilities.statusBarHeight : 0));
            int i4 = this.orientation;
            if (i4 % 360 == 90 || i4 % 360 == 270) {
                f = (float) this.bitmapToEdit.getHeight();
                i3 = this.bitmapToEdit.getWidth();
            } else {
                f = (float) this.bitmapToEdit.getWidth();
                i3 = this.bitmapToEdit.getHeight();
            }
            float f4 = (float) i3;
            float f5 = (float) dp;
            float f6 = f5 / f;
            float f7 = (float) dp2;
            float f8 = f7 / f4;
            if (f6 > f8) {
                f3 = (float) ((int) Math.ceil((double) (f * f8)));
                f2 = f7;
            } else {
                f2 = (float) ((int) Math.ceil((double) (f4 * f6)));
                f3 = f5;
            }
            int ceil = (int) Math.ceil((double) (((f5 - f3) / 2.0f) + ((float) AndroidUtilities.dp(14.0f))));
            int ceil2 = (int) Math.ceil((double) (((f7 - f2) / 2.0f) + ((float) AndroidUtilities.dp(14.0f)) + ((float) (Build.VERSION.SDK_INT >= 21 ? AndroidUtilities.statusBarHeight : 0))));
            FrameLayout.LayoutParams layoutParams = (FrameLayout.LayoutParams) this.textureView.getLayoutParams();
            layoutParams.leftMargin = ceil;
            layoutParams.topMargin = ceil2;
            layoutParams.width = (int) f3;
            layoutParams.height = (int) f2;
            this.curvesControl.setActualArea((float) ceil, (float) (ceil2 - (Build.VERSION.SDK_INT >= 21 ? AndroidUtilities.statusBarHeight : 0)), (float) layoutParams.width, (float) layoutParams.height);
            this.blurControl.setActualAreaSize((float) layoutParams.width, (float) layoutParams.height);
            ((FrameLayout.LayoutParams) this.blurControl.getLayoutParams()).height = AndroidUtilities.dp(38.0f) + dp2;
            ((FrameLayout.LayoutParams) this.curvesControl.getLayoutParams()).height = dp2 + AndroidUtilities.dp(28.0f);
            if (AndroidUtilities.isTablet()) {
                int dp3 = AndroidUtilities.dp(86.0f) * 10;
                FrameLayout.LayoutParams layoutParams2 = (FrameLayout.LayoutParams) this.recyclerListView.getLayoutParams();
                if (dp3 < dp) {
                    layoutParams2.width = dp3;
                    layoutParams2.leftMargin = (dp - dp3) / 2;
                    return;
                }
                layoutParams2.width = -1;
                layoutParams2.leftMargin = 0;
            }
        }
    }

    /* access modifiers changed from: protected */
    public void onMeasure(int i, int i2) {
        fixLayout(View.MeasureSpec.getSize(i), View.MeasureSpec.getSize(i2));
        super.onMeasure(i, i2);
    }

    /* access modifiers changed from: private */
    public float getShadowsValue() {
        return ((this.shadowsValue * 0.55f) + 100.0f) / 100.0f;
    }

    /* access modifiers changed from: private */
    public float getHighlightsValue() {
        return ((this.highlightsValue * 0.75f) + 100.0f) / 100.0f;
    }

    /* access modifiers changed from: private */
    public float getEnhanceValue() {
        return this.enhanceValue / 100.0f;
    }

    /* access modifiers changed from: private */
    public float getExposureValue() {
        return this.exposureValue / 100.0f;
    }

    /* access modifiers changed from: private */
    public float getContrastValue() {
        return ((this.contrastValue / 100.0f) * 0.3f) + 1.0f;
    }

    /* access modifiers changed from: private */
    public float getWarmthValue() {
        return this.warmthValue / 100.0f;
    }

    /* access modifiers changed from: private */
    public float getVignetteValue() {
        return this.vignetteValue / 100.0f;
    }

    /* access modifiers changed from: private */
    public float getSharpenValue() {
        return ((this.sharpenValue / 100.0f) * 0.6f) + 0.11f;
    }

    /* access modifiers changed from: private */
    public float getGrainValue() {
        return (this.grainValue / 100.0f) * 0.04f;
    }

    /* access modifiers changed from: private */
    public float getFadeValue() {
        return this.fadeValue / 100.0f;
    }

    /* access modifiers changed from: private */
    public float getTintHighlightsIntensityValue() {
        return this.tintHighlightsColor == 0 ? 0.0f : 0.5f;
    }

    /* access modifiers changed from: private */
    public float getTintShadowsIntensityValue() {
        return this.tintShadowsColor == 0 ? 0.0f : 0.5f;
    }

    /* access modifiers changed from: private */
    public float getSaturationValue() {
        float f = this.saturationValue / 100.0f;
        if (f > 0.0f) {
            f *= 1.05f;
        }
        return f + 1.0f;
    }

    public FrameLayout getToolsView() {
        return this.toolsView;
    }

    public TextView getDoneTextView() {
        return this.doneTextView;
    }

    public TextView getCancelTextView() {
        return this.cancelTextView;
    }

    public class ToolsAdapter extends RecyclerListView.SelectionAdapter {
        private Context mContext;

        public int getItemCount() {
            return 13;
        }

        public long getItemId(int i) {
            return (long) i;
        }

        public boolean isEnabled(RecyclerView.ViewHolder viewHolder) {
            return false;
        }

        public ToolsAdapter(Context context) {
            this.mContext = context;
        }

        /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r1v1, resolved type: org.telegram.ui.Cells.PhotoEditRadioCell} */
        /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r1v3, resolved type: org.telegram.ui.Cells.PhotoEditToolCell} */
        /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r1v4, resolved type: org.telegram.ui.Cells.PhotoEditRadioCell} */
        /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r1v5, resolved type: org.telegram.ui.Cells.PhotoEditRadioCell} */
        /* JADX WARNING: Multi-variable type inference failed */
        /* Code decompiled incorrectly, please refer to instructions dump. */
        public androidx.recyclerview.widget.RecyclerView.ViewHolder onCreateViewHolder(android.view.ViewGroup r1, int r2) {
            /*
                r0 = this;
                if (r2 != 0) goto L_0x0012
                org.telegram.ui.Cells.PhotoEditToolCell r1 = new org.telegram.ui.Cells.PhotoEditToolCell
                android.content.Context r2 = r0.mContext
                r1.<init>(r2)
                org.telegram.ui.Components.-$$Lambda$PhotoFilterView$ToolsAdapter$y8L3fL76Xdh9BIlSQihYImp1GRU r2 = new org.telegram.ui.Components.-$$Lambda$PhotoFilterView$ToolsAdapter$y8L3fL76Xdh9BIlSQihYImp1GRU
                r2.<init>()
                r1.setSeekBarDelegate(r2)
                goto L_0x0021
            L_0x0012:
                org.telegram.ui.Cells.PhotoEditRadioCell r1 = new org.telegram.ui.Cells.PhotoEditRadioCell
                android.content.Context r2 = r0.mContext
                r1.<init>(r2)
                org.telegram.ui.Components.-$$Lambda$PhotoFilterView$ToolsAdapter$l6jhbYVDCcFuEfXfCcHJenobMmo r2 = new org.telegram.ui.Components.-$$Lambda$PhotoFilterView$ToolsAdapter$l6jhbYVDCcFuEfXfCcHJenobMmo
                r2.<init>()
                r1.setOnClickListener(r2)
            L_0x0021:
                org.telegram.ui.Components.RecyclerListView$Holder r2 = new org.telegram.ui.Components.RecyclerListView$Holder
                r2.<init>(r1)
                return r2
            */
            throw new UnsupportedOperationException("Method not decompiled: org.telegram.ui.Components.PhotoFilterView.ToolsAdapter.onCreateViewHolder(android.view.ViewGroup, int):androidx.recyclerview.widget.RecyclerView$ViewHolder");
        }

        public /* synthetic */ void lambda$onCreateViewHolder$0$PhotoFilterView$ToolsAdapter(int i, int i2) {
            if (i == PhotoFilterView.this.enhanceTool) {
                float unused = PhotoFilterView.this.enhanceValue = (float) i2;
            } else if (i == PhotoFilterView.this.highlightsTool) {
                float unused2 = PhotoFilterView.this.highlightsValue = (float) i2;
            } else if (i == PhotoFilterView.this.contrastTool) {
                float unused3 = PhotoFilterView.this.contrastValue = (float) i2;
            } else if (i == PhotoFilterView.this.exposureTool) {
                float unused4 = PhotoFilterView.this.exposureValue = (float) i2;
            } else if (i == PhotoFilterView.this.warmthTool) {
                float unused5 = PhotoFilterView.this.warmthValue = (float) i2;
            } else if (i == PhotoFilterView.this.saturationTool) {
                float unused6 = PhotoFilterView.this.saturationValue = (float) i2;
            } else if (i == PhotoFilterView.this.vignetteTool) {
                float unused7 = PhotoFilterView.this.vignetteValue = (float) i2;
            } else if (i == PhotoFilterView.this.shadowsTool) {
                float unused8 = PhotoFilterView.this.shadowsValue = (float) i2;
            } else if (i == PhotoFilterView.this.grainTool) {
                float unused9 = PhotoFilterView.this.grainValue = (float) i2;
            } else if (i == PhotoFilterView.this.sharpenTool) {
                float unused10 = PhotoFilterView.this.sharpenValue = (float) i2;
            } else if (i == PhotoFilterView.this.fadeTool) {
                float unused11 = PhotoFilterView.this.fadeValue = (float) i2;
            }
            if (PhotoFilterView.this.eglThread != null) {
                PhotoFilterView.this.eglThread.requestRender(true);
            }
        }

        public /* synthetic */ void lambda$onCreateViewHolder$1$PhotoFilterView$ToolsAdapter(View view) {
            PhotoEditRadioCell photoEditRadioCell = (PhotoEditRadioCell) view;
            if (((Integer) photoEditRadioCell.getTag()).intValue() == PhotoFilterView.this.tintShadowsTool) {
                int unused = PhotoFilterView.this.tintShadowsColor = photoEditRadioCell.getCurrentColor();
            } else {
                int unused2 = PhotoFilterView.this.tintHighlightsColor = photoEditRadioCell.getCurrentColor();
            }
            if (PhotoFilterView.this.eglThread != null) {
                PhotoFilterView.this.eglThread.requestRender(false);
            }
        }

        public void onBindViewHolder(RecyclerView.ViewHolder viewHolder, int i) {
            int itemViewType = viewHolder.getItemViewType();
            if (itemViewType == 0) {
                PhotoEditToolCell photoEditToolCell = (PhotoEditToolCell) viewHolder.itemView;
                photoEditToolCell.setTag(Integer.valueOf(i));
                if (i == PhotoFilterView.this.enhanceTool) {
                    photoEditToolCell.setIconAndTextAndValue(LocaleController.getString("Enhance", NUM), PhotoFilterView.this.enhanceValue, 0, 100);
                } else if (i == PhotoFilterView.this.highlightsTool) {
                    photoEditToolCell.setIconAndTextAndValue(LocaleController.getString("Highlights", NUM), PhotoFilterView.this.highlightsValue, -100, 100);
                } else if (i == PhotoFilterView.this.contrastTool) {
                    photoEditToolCell.setIconAndTextAndValue(LocaleController.getString("Contrast", NUM), PhotoFilterView.this.contrastValue, -100, 100);
                } else if (i == PhotoFilterView.this.exposureTool) {
                    photoEditToolCell.setIconAndTextAndValue(LocaleController.getString("Exposure", NUM), PhotoFilterView.this.exposureValue, -100, 100);
                } else if (i == PhotoFilterView.this.warmthTool) {
                    photoEditToolCell.setIconAndTextAndValue(LocaleController.getString("Warmth", NUM), PhotoFilterView.this.warmthValue, -100, 100);
                } else if (i == PhotoFilterView.this.saturationTool) {
                    photoEditToolCell.setIconAndTextAndValue(LocaleController.getString("Saturation", NUM), PhotoFilterView.this.saturationValue, -100, 100);
                } else if (i == PhotoFilterView.this.vignetteTool) {
                    photoEditToolCell.setIconAndTextAndValue(LocaleController.getString("Vignette", NUM), PhotoFilterView.this.vignetteValue, 0, 100);
                } else if (i == PhotoFilterView.this.shadowsTool) {
                    photoEditToolCell.setIconAndTextAndValue(LocaleController.getString("Shadows", NUM), PhotoFilterView.this.shadowsValue, -100, 100);
                } else if (i == PhotoFilterView.this.grainTool) {
                    photoEditToolCell.setIconAndTextAndValue(LocaleController.getString("Grain", NUM), PhotoFilterView.this.grainValue, 0, 100);
                } else if (i == PhotoFilterView.this.sharpenTool) {
                    photoEditToolCell.setIconAndTextAndValue(LocaleController.getString("Sharpen", NUM), PhotoFilterView.this.sharpenValue, 0, 100);
                } else if (i == PhotoFilterView.this.fadeTool) {
                    photoEditToolCell.setIconAndTextAndValue(LocaleController.getString("Fade", NUM), PhotoFilterView.this.fadeValue, 0, 100);
                }
            } else if (itemViewType == 1) {
                PhotoEditRadioCell photoEditRadioCell = (PhotoEditRadioCell) viewHolder.itemView;
                photoEditRadioCell.setTag(Integer.valueOf(i));
                if (i == PhotoFilterView.this.tintShadowsTool) {
                    photoEditRadioCell.setIconAndTextAndValue(LocaleController.getString("TintShadows", NUM), 0, PhotoFilterView.this.tintShadowsColor);
                } else if (i == PhotoFilterView.this.tintHighlightsTool) {
                    photoEditRadioCell.setIconAndTextAndValue(LocaleController.getString("TintHighlights", NUM), 0, PhotoFilterView.this.tintHighlightsColor);
                }
            }
        }

        public int getItemViewType(int i) {
            return (i == PhotoFilterView.this.tintShadowsTool || i == PhotoFilterView.this.tintHighlightsTool) ? 1 : 0;
        }
    }
}
