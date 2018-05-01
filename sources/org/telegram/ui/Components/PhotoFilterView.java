package org.telegram.ui.Components;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Bitmap.Config;
import android.graphics.Matrix;
import android.graphics.PorterDuff.Mode;
import android.graphics.PorterDuffColorFilter;
import android.graphics.SurfaceTexture;
import android.graphics.drawable.Drawable;
import android.opengl.GLES20;
import android.opengl.GLUtils;
import android.os.Build.VERSION;
import android.os.Looper;
import android.view.MotionEvent;
import android.view.TextureView;
import android.view.TextureView.SurfaceTextureListener;
import android.view.View;
import android.view.View.MeasureSpec;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.FrameLayout.LayoutParams;
import android.widget.ImageView;
import android.widget.ImageView.ScaleType;
import android.widget.LinearLayout;
import android.widget.TextView;
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
import javax.microedition.khronos.opengles.GL;
import org.telegram.messenger.AndroidUtilities;
import org.telegram.messenger.BuildVars;
import org.telegram.messenger.C0446R;
import org.telegram.messenger.DispatchQueue;
import org.telegram.messenger.FileLog;
import org.telegram.messenger.LocaleController;
import org.telegram.messenger.MediaController.SavedFilterState;
import org.telegram.messenger.MessagesController;
import org.telegram.messenger.Utilities;
import org.telegram.messenger.support.widget.LinearLayoutManager;
import org.telegram.messenger.support.widget.RecyclerView.LayoutManager;
import org.telegram.messenger.support.widget.RecyclerView.ViewHolder;
import org.telegram.messenger.support.widget.helper.ItemTouchHelper.Callback;
import org.telegram.ui.ActionBar.Theme;
import org.telegram.ui.Cells.PhotoEditRadioCell;
import org.telegram.ui.Cells.PhotoEditToolCell;
import org.telegram.ui.Components.PhotoEditorSeekBar.PhotoEditorSeekBarDelegate;
import org.telegram.ui.Components.PhotoFilterBlurControl.PhotoFilterLinearBlurControlDelegate;
import org.telegram.ui.Components.PhotoFilterCurvesControl.PhotoFilterCurvesControlDelegate;
import org.telegram.ui.Components.RecyclerListView.Holder;
import org.telegram.ui.Components.RecyclerListView.SelectionAdapter;

@SuppressLint({"NewApi"})
public class PhotoFilterView extends FrameLayout {
    private static final int curveDataStep = 2;
    private static final int curveGranularity = 100;
    private Bitmap bitmapToEdit;
    private float blurAngle;
    private PhotoFilterBlurControl blurControl;
    private float blurExcludeBlurSize;
    private Point blurExcludePoint;
    private float blurExcludeSize;
    private ImageView blurItem;
    private FrameLayout blurLayout;
    private TextView blurLinearButton;
    private TextView blurOffButton;
    private TextView blurRadialButton;
    private int blurType;
    private TextView cancelTextView;
    private int contrastTool = 2;
    private float contrastValue;
    private ImageView curveItem;
    private FrameLayout curveLayout;
    private RadioButton[] curveRadioButton = new RadioButton[4];
    private PhotoFilterCurvesControl curvesControl;
    private CurvesToolValue curvesToolValue;
    private TextView doneTextView;
    private EGLThread eglThread;
    private int enhanceTool = 0;
    private float enhanceValue;
    private int exposureTool = 1;
    private float exposureValue;
    private int fadeTool = 5;
    private float fadeValue;
    private int grainTool = 9;
    private float grainValue;
    private int highlightsTool = 6;
    private float highlightsValue;
    private SavedFilterState lastState;
    private int orientation;
    private RecyclerListView recyclerListView;
    private int saturationTool = 3;
    private float saturationValue;
    private int selectedTool;
    private int shadowsTool = 7;
    private float shadowsValue;
    private int sharpenTool = 10;
    private float sharpenValue;
    private boolean showOriginal;
    private TextureView textureView;
    private int tintHighlightsColor;
    private int tintHighlightsTool = 12;
    private int tintShadowsColor;
    private int tintShadowsTool = 11;
    private FrameLayout toolsView;
    private ImageView tuneItem;
    private int vignetteTool = 8;
    private float vignetteValue;
    private int warmthTool = 4;
    private float warmthValue;

    /* renamed from: org.telegram.ui.Components.PhotoFilterView$1 */
    class C12411 implements SurfaceTextureListener {

        /* renamed from: org.telegram.ui.Components.PhotoFilterView$1$1 */
        class C12401 implements Runnable {
            C12401() {
            }

            public void run() {
                if (PhotoFilterView.this.eglThread != null) {
                    PhotoFilterView.this.eglThread.requestRender(false, true);
                }
            }
        }

        public void onSurfaceTextureUpdated(SurfaceTexture surfaceTexture) {
        }

        C12411() {
        }

        public void onSurfaceTextureAvailable(SurfaceTexture surfaceTexture, int i, int i2) {
            if (PhotoFilterView.this.eglThread == null && surfaceTexture != null) {
                PhotoFilterView.this.eglThread = new EGLThread(surfaceTexture, PhotoFilterView.this.bitmapToEdit);
                PhotoFilterView.this.eglThread.setSurfaceTextureSize(i, i2);
                PhotoFilterView.this.eglThread.requestRender(true, true);
            }
        }

        public void onSurfaceTextureSizeChanged(SurfaceTexture surfaceTexture, int i, int i2) {
            if (PhotoFilterView.this.eglThread != null) {
                PhotoFilterView.this.eglThread.setSurfaceTextureSize(i, i2);
                PhotoFilterView.this.eglThread.requestRender(0, 1);
                PhotoFilterView.this.eglThread.postRunnable(new C12401());
            }
        }

        public boolean onSurfaceTextureDestroyed(SurfaceTexture surfaceTexture) {
            if (PhotoFilterView.this.eglThread != null) {
                PhotoFilterView.this.eglThread.shutdown();
                PhotoFilterView.this.eglThread = null;
            }
            return true;
        }
    }

    /* renamed from: org.telegram.ui.Components.PhotoFilterView$4 */
    class C12424 implements OnClickListener {
        C12424() {
        }

        public void onClick(View view) {
            PhotoFilterView.this.selectedTool = 0;
            PhotoFilterView.this.tuneItem.setColorFilter(new PorterDuffColorFilter(-9649153, Mode.MULTIPLY));
            PhotoFilterView.this.blurItem.setColorFilter(null);
            PhotoFilterView.this.curveItem.setColorFilter(null);
            PhotoFilterView.this.switchMode();
        }
    }

    /* renamed from: org.telegram.ui.Components.PhotoFilterView$5 */
    class C12435 implements OnClickListener {
        C12435() {
        }

        public void onClick(View view) {
            PhotoFilterView.this.selectedTool = 1;
            PhotoFilterView.this.tuneItem.setColorFilter(null);
            PhotoFilterView.this.blurItem.setColorFilter(new PorterDuffColorFilter(-9649153, Mode.MULTIPLY));
            PhotoFilterView.this.curveItem.setColorFilter(null);
            PhotoFilterView.this.switchMode();
        }
    }

    /* renamed from: org.telegram.ui.Components.PhotoFilterView$6 */
    class C12446 implements OnClickListener {
        C12446() {
        }

        public void onClick(View view) {
            PhotoFilterView.this.selectedTool = 2;
            PhotoFilterView.this.tuneItem.setColorFilter(null);
            PhotoFilterView.this.blurItem.setColorFilter(null);
            PhotoFilterView.this.curveItem.setColorFilter(new PorterDuffColorFilter(-9649153, Mode.MULTIPLY));
            PhotoFilterView.this.switchMode();
        }
    }

    /* renamed from: org.telegram.ui.Components.PhotoFilterView$7 */
    class C12457 implements OnClickListener {
        C12457() {
        }

        public void onClick(View view) {
            view = ((Integer) view.getTag()).intValue();
            PhotoFilterView.this.curvesToolValue.activeType = view;
            int i = 0;
            while (i < 4) {
                PhotoFilterView.this.curveRadioButton[i].setChecked(i == view, true);
                i++;
            }
            PhotoFilterView.this.curvesControl.invalidate();
        }
    }

    /* renamed from: org.telegram.ui.Components.PhotoFilterView$8 */
    class C12468 implements OnClickListener {
        C12468() {
        }

        public void onClick(View view) {
            PhotoFilterView.this.blurType = 0;
            PhotoFilterView.this.updateSelectedBlurType();
            PhotoFilterView.this.blurControl.setVisibility(4);
            if (PhotoFilterView.this.eglThread != null) {
                PhotoFilterView.this.eglThread.requestRender(false);
            }
        }
    }

    /* renamed from: org.telegram.ui.Components.PhotoFilterView$9 */
    class C12479 implements OnClickListener {
        C12479() {
        }

        public void onClick(View view) {
            PhotoFilterView.this.blurType = 1;
            PhotoFilterView.this.updateSelectedBlurType();
            PhotoFilterView.this.blurControl.setVisibility(0);
            PhotoFilterView.this.blurControl.setType(1);
            if (PhotoFilterView.this.eglThread != null) {
                PhotoFilterView.this.eglThread.requestRender(false);
            }
        }
    }

    public static class CurvesToolValue {
        public static final int CurvesTypeBlue = 3;
        public static final int CurvesTypeGreen = 2;
        public static final int CurvesTypeLuminance = 0;
        public static final int CurvesTypeRed = 1;
        public int activeType;
        public CurvesValue blueCurve;
        public ByteBuffer curveBuffer;
        public CurvesValue greenCurve;
        public CurvesValue luminanceCurve;
        public CurvesValue redCurve;

        public CurvesToolValue() {
            this.luminanceCurve = new CurvesValue();
            this.redCurve = new CurvesValue();
            this.greenCurve = new CurvesValue();
            this.blueCurve = new CurvesValue();
            this.curveBuffer = null;
            this.curveBuffer = ByteBuffer.allocateDirect(800);
            this.curveBuffer.order(ByteOrder.LITTLE_ENDIAN);
        }

        public void fillBuffer() {
            this.curveBuffer.position(0);
            float[] dataPoints = this.luminanceCurve.getDataPoints();
            float[] dataPoints2 = this.redCurve.getDataPoints();
            float[] dataPoints3 = this.greenCurve.getDataPoints();
            float[] dataPoints4 = this.blueCurve.getDataPoints();
            for (int i = 0; i < Callback.DEFAULT_DRAG_ANIMATION_DURATION; i++) {
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

    public static class CurvesValue {
        public float blacksLevel = 0.0f;
        public float[] cachedDataPoints;
        public float highlightsLevel = 75.0f;
        public float midtonesLevel = 50.0f;
        public float previousBlacksLevel = 0.0f;
        public float previousHighlightsLevel = 75.0f;
        public float previousMidtonesLevel = 50.0f;
        public float previousShadowsLevel = 25.0f;
        public float previousWhitesLevel = 100.0f;
        public float shadowsLevel = 25.0f;
        public float whitesLevel = 100.0f;

        public float[] getDataPoints() {
            if (this.cachedDataPoints == null) {
                interpolateCurve();
            }
            return this.cachedDataPoints;
        }

        public void saveValues() {
            this.previousBlacksLevel = this.blacksLevel;
            this.previousShadowsLevel = this.shadowsLevel;
            this.previousMidtonesLevel = this.midtonesLevel;
            this.previousHighlightsLevel = this.highlightsLevel;
            this.previousWhitesLevel = this.whitesLevel;
        }

        public void restoreValues() {
            this.blacksLevel = this.previousBlacksLevel;
            this.shadowsLevel = this.previousShadowsLevel;
            this.midtonesLevel = this.previousMidtonesLevel;
            this.highlightsLevel = this.previousHighlightsLevel;
            this.whitesLevel = this.previousWhitesLevel;
            interpolateCurve();
        }

        public float[] interpolateCurve() {
            float[] fArr = new float[14];
            fArr[0] = -0.001f;
            int i = 1;
            fArr[1] = this.blacksLevel / 100.0f;
            fArr[2] = 0.0f;
            fArr[3] = this.blacksLevel / 100.0f;
            fArr[4] = 0.25f;
            fArr[5] = this.shadowsLevel / 100.0f;
            fArr[6] = 0.5f;
            fArr[7] = this.midtonesLevel / 100.0f;
            fArr[8] = 0.75f;
            fArr[9] = this.highlightsLevel / 100.0f;
            fArr[10] = 1.0f;
            fArr[11] = this.whitesLevel / 100.0f;
            int i2 = 12;
            fArr[12] = 1.001f;
            fArr[13] = this.whitesLevel / 100.0f;
            int i3 = PhotoFilterView.curveGranularity;
            ArrayList arrayList = new ArrayList(PhotoFilterView.curveGranularity);
            ArrayList arrayList2 = new ArrayList(PhotoFilterView.curveGranularity);
            arrayList2.add(Float.valueOf(fArr[0]));
            arrayList2.add(Float.valueOf(fArr[1]));
            int i4 = 1;
            while (i4 < (fArr.length / 2) - 2) {
                int i5 = (i4 - 1) * 2;
                float f = fArr[i5];
                float f2 = fArr[i5 + i];
                int i6 = i4 * 2;
                float f3 = fArr[i6];
                float f4 = fArr[i6 + 1];
                int i7 = i4 + 1;
                int i8 = i7 * 2;
                float f5 = fArr[i8];
                float f6 = fArr[i8 + 1];
                i4 = (i4 + 2) * 2;
                float f7 = fArr[i4];
                float f8 = fArr[i4 + i];
                for (i3 = 
/*
Method generation error in method: org.telegram.ui.Components.PhotoFilterView.CurvesValue.interpolateCurve():float[], dex: classes.dex
jadx.core.utils.exceptions.CodegenException: Error generate insn: PHI: (r11_1 'i3' int) = (r11_0 'i3' int), (r11_14 'i3' int) binds: {(r11_0 'i3' int)=B:0:0x0000, (r11_14 'i3' int)=B:12:0x0132} in method: org.telegram.ui.Components.PhotoFilterView.CurvesValue.interpolateCurve():float[], dex: classes.dex
	at jadx.core.codegen.InsnGen.makeInsn(InsnGen.java:226)
	at jadx.core.codegen.RegionGen.makeLoop(RegionGen.java:184)
	at jadx.core.codegen.RegionGen.makeRegion(RegionGen.java:61)
	at jadx.core.codegen.RegionGen.makeSimpleRegion(RegionGen.java:87)
	at jadx.core.codegen.RegionGen.makeRegion(RegionGen.java:53)
	at jadx.core.codegen.RegionGen.makeRegionIndent(RegionGen.java:93)
	at jadx.core.codegen.RegionGen.makeLoop(RegionGen.java:219)
	at jadx.core.codegen.RegionGen.makeRegion(RegionGen.java:61)
	at jadx.core.codegen.RegionGen.makeSimpleRegion(RegionGen.java:87)
	at jadx.core.codegen.RegionGen.makeRegion(RegionGen.java:53)
	at jadx.core.codegen.MethodGen.addInstructions(MethodGen.java:187)
	at jadx.core.codegen.ClassGen.addMethod(ClassGen.java:320)
	at jadx.core.codegen.ClassGen.addMethods(ClassGen.java:257)
	at jadx.core.codegen.ClassGen.addClassBody(ClassGen.java:220)
	at jadx.core.codegen.ClassGen.addClassCode(ClassGen.java:110)
	at jadx.core.codegen.ClassGen.addInnerClasses(ClassGen.java:233)
	at jadx.core.codegen.ClassGen.addClassBody(ClassGen.java:219)
	at jadx.core.codegen.ClassGen.addClassCode(ClassGen.java:110)
	at jadx.core.codegen.ClassGen.makeClass(ClassGen.java:75)
	at jadx.core.codegen.CodeGen.visit(CodeGen.java:12)
	at jadx.core.ProcessClass.process(ProcessClass.java:40)
	at jadx.api.JadxDecompiler.processClass(JadxDecompiler.java:282)
	at jadx.api.JavaClass.decompile(JavaClass.java:62)
	at jadx.api.JadxDecompiler.lambda$appendSourcesSave$0(JadxDecompiler.java:200)
Caused by: jadx.core.utils.exceptions.CodegenException: PHI can be used only in fallback mode
	at jadx.core.codegen.InsnGen.fallbackOnlyInsn(InsnGen.java:537)
	at jadx.core.codegen.InsnGen.makeInsnBody(InsnGen.java:509)
	at jadx.core.codegen.InsnGen.makeInsn(InsnGen.java:220)
	... 23 more

*/

                public boolean isDefault() {
                    return ((double) Math.abs(this.blacksLevel - 0.0f)) < 1.0E-5d && ((double) Math.abs(this.shadowsLevel - 25.0f)) < 1.0E-5d && ((double) Math.abs(this.midtonesLevel - 50.0f)) < 1.0E-5d && ((double) Math.abs(this.highlightsLevel - 75.0f)) < 1.0E-5d && ((double) Math.abs(this.whitesLevel - 100.0f)) < 1.0E-5d;
                }
            }

            /* renamed from: org.telegram.ui.Components.PhotoFilterView$2 */
            class C20682 implements PhotoFilterLinearBlurControlDelegate {
                C20682() {
                }

                public void valueChanged(Point point, float f, float f2, float f3) {
                    PhotoFilterView.this.blurExcludeSize = f2;
                    PhotoFilterView.this.blurExcludePoint = point;
                    PhotoFilterView.this.blurExcludeBlurSize = f;
                    PhotoFilterView.this.blurAngle = f3;
                    if (PhotoFilterView.this.eglThread != null) {
                        PhotoFilterView.this.eglThread.requestRender(0.0f);
                    }
                }
            }

            /* renamed from: org.telegram.ui.Components.PhotoFilterView$3 */
            class C20693 implements PhotoFilterCurvesControlDelegate {
                C20693() {
                }

                public void valueChanged() {
                    if (PhotoFilterView.this.eglThread != null) {
                        PhotoFilterView.this.eglThread.requestRender(false);
                    }
                }
            }

            public class EGLThread extends DispatchQueue {
                private static final int PGPhotoEnhanceHistogramBins = 256;
                private static final int PGPhotoEnhanceSegments = 4;
                private static final String blurFragmentShaderCode = "uniform sampler2D sourceImage;varying highp vec2 blurCoordinates[9];void main() {lowp vec4 sum = vec4(0.0);sum += texture2D(sourceImage, blurCoordinates[0]) * 0.133571;sum += texture2D(sourceImage, blurCoordinates[1]) * 0.233308;sum += texture2D(sourceImage, blurCoordinates[2]) * 0.233308;sum += texture2D(sourceImage, blurCoordinates[3]) * 0.135928;sum += texture2D(sourceImage, blurCoordinates[4]) * 0.135928;sum += texture2D(sourceImage, blurCoordinates[5]) * 0.051383;sum += texture2D(sourceImage, blurCoordinates[6]) * 0.051383;sum += texture2D(sourceImage, blurCoordinates[7]) * 0.012595;sum += texture2D(sourceImage, blurCoordinates[8]) * 0.012595;gl_FragColor = sum;}";
                private static final String blurVertexShaderCode = "attribute vec4 position;attribute vec4 inputTexCoord;uniform highp float texelWidthOffset;uniform highp float texelHeightOffset;varying vec2 blurCoordinates[9];void main() {gl_Position = position;vec2 singleStepOffset = vec2(texelWidthOffset, texelHeightOffset);blurCoordinates[0] = inputTexCoord.xy;blurCoordinates[1] = inputTexCoord.xy + singleStepOffset * 1.458430;blurCoordinates[2] = inputTexCoord.xy - singleStepOffset * 1.458430;blurCoordinates[3] = inputTexCoord.xy + singleStepOffset * 3.403985;blurCoordinates[4] = inputTexCoord.xy - singleStepOffset * 3.403985;blurCoordinates[5] = inputTexCoord.xy + singleStepOffset * 5.351806;blurCoordinates[6] = inputTexCoord.xy - singleStepOffset * 5.351806;blurCoordinates[7] = inputTexCoord.xy + singleStepOffset * 7.302940;blurCoordinates[8] = inputTexCoord.xy - singleStepOffset * 7.302940;}";
                private static final String enhanceFragmentShaderCode = "precision highp float;varying vec2 texCoord;uniform sampler2D sourceImage;uniform sampler2D inputImageTexture2;uniform float intensity;float enhance(float value) {const vec2 offset = vec2(0.001953125, 0.03125);value = value + offset.x;vec2 coord = (clamp(texCoord, 0.125, 1.0 - 0.125001) - 0.125) * 4.0;vec2 frac = fract(coord);coord = floor(coord);float p00 = float(coord.y * 4.0 + coord.x) * 0.0625 + offset.y;float p01 = float(coord.y * 4.0 + coord.x + 1.0) * 0.0625 + offset.y;float p10 = float((coord.y + 1.0) * 4.0 + coord.x) * 0.0625 + offset.y;float p11 = float((coord.y + 1.0) * 4.0 + coord.x + 1.0) * 0.0625 + offset.y;vec3 c00 = texture2D(inputImageTexture2, vec2(value, p00)).rgb;vec3 c01 = texture2D(inputImageTexture2, vec2(value, p01)).rgb;vec3 c10 = texture2D(inputImageTexture2, vec2(value, p10)).rgb;vec3 c11 = texture2D(inputImageTexture2, vec2(value, p11)).rgb;float c1 = ((c00.r - c00.g) / (c00.b - c00.g));float c2 = ((c01.r - c01.g) / (c01.b - c01.g));float c3 = ((c10.r - c10.g) / (c10.b - c10.g));float c4 = ((c11.r - c11.g) / (c11.b - c11.g));float c1_2 = mix(c1, c2, frac.x);float c3_4 = mix(c3, c4, frac.x);return mix(c1_2, c3_4, frac.y);}vec3 hsv_to_rgb(vec3 c) {vec4 K = vec4(1.0, 2.0 / 3.0, 1.0 / 3.0, 3.0);vec3 p = abs(fract(c.xxx + K.xyz) * 6.0 - K.www);return c.z * mix(K.xxx, clamp(p - K.xxx, 0.0, 1.0), c.y);}void main() {vec4 texel = texture2D(sourceImage, texCoord);vec4 hsv = texel;hsv.y = min(1.0, hsv.y * 1.2);hsv.z = min(1.0, enhance(hsv.z) * 1.1);gl_FragColor = vec4(hsv_to_rgb(mix(texel.xyz, hsv.xyz, intensity)), texel.w);}";
                private static final String linearBlurFragmentShaderCode = "varying highp vec2 texCoord;uniform sampler2D sourceImage;uniform sampler2D inputImageTexture2;uniform lowp float excludeSize;uniform lowp vec2 excludePoint;uniform lowp float excludeBlurSize;uniform highp float angle;uniform highp float aspectRatio;void main() {lowp vec4 sharpImageColor = texture2D(sourceImage, texCoord);lowp vec4 blurredImageColor = texture2D(inputImageTexture2, texCoord);highp vec2 texCoordToUse = vec2(texCoord.x, (texCoord.y * aspectRatio + 0.5 - 0.5 * aspectRatio));highp float distanceFromCenter = abs((texCoordToUse.x - excludePoint.x) * aspectRatio * cos(angle) + (texCoordToUse.y - excludePoint.y) * sin(angle));gl_FragColor = mix(sharpImageColor, blurredImageColor, smoothstep(excludeSize - excludeBlurSize, excludeSize, distanceFromCenter));}";
                private static final String radialBlurFragmentShaderCode = "varying highp vec2 texCoord;uniform sampler2D sourceImage;uniform sampler2D inputImageTexture2;uniform lowp float excludeSize;uniform lowp vec2 excludePoint;uniform lowp float excludeBlurSize;uniform highp float aspectRatio;void main() {lowp vec4 sharpImageColor = texture2D(sourceImage, texCoord);lowp vec4 blurredImageColor = texture2D(inputImageTexture2, texCoord);highp vec2 texCoordToUse = vec2(texCoord.x, (texCoord.y * aspectRatio + 0.5 - 0.5 * aspectRatio));highp float distanceFromCenter = distance(excludePoint, texCoordToUse);gl_FragColor = mix(sharpImageColor, blurredImageColor, smoothstep(excludeSize - excludeBlurSize, excludeSize, distanceFromCenter));}";
                private static final String rgbToHsvFragmentShaderCode = "precision highp float;varying vec2 texCoord;uniform sampler2D sourceImage;vec3 rgb_to_hsv(vec3 c) {vec4 K = vec4(0.0, -1.0 / 3.0, 2.0 / 3.0, -1.0);vec4 p = c.g < c.b ? vec4(c.bg, K.wz) : vec4(c.gb, K.xy);vec4 q = c.r < p.x ? vec4(p.xyw, c.r) : vec4(c.r, p.yzx);float d = q.x - min(q.w, q.y);float e = 1.0e-10;return vec3(abs(q.z + (q.w - q.y) / (6.0 * d + e)), d / (q.x + e), q.x);}void main() {vec4 texel = texture2D(sourceImage, texCoord);gl_FragColor = vec4(rgb_to_hsv(texel.rgb), texel.a);}";
                private static final String sharpenFragmentShaderCode = "precision highp float;varying vec2 texCoord;varying vec2 leftTexCoord;varying vec2 rightTexCoord;varying vec2 topTexCoord;varying vec2 bottomTexCoord;uniform sampler2D sourceImage;uniform float sharpen;void main() {vec4 result = texture2D(sourceImage, texCoord);vec3 leftTextureColor = texture2D(sourceImage, leftTexCoord).rgb;vec3 rightTextureColor = texture2D(sourceImage, rightTexCoord).rgb;vec3 topTextureColor = texture2D(sourceImage, topTexCoord).rgb;vec3 bottomTextureColor = texture2D(sourceImage, bottomTexCoord).rgb;result.rgb = result.rgb * (1.0 + 4.0 * sharpen) - (leftTextureColor + rightTextureColor + topTextureColor + bottomTextureColor) * sharpen;gl_FragColor = result;}";
                private static final String sharpenVertexShaderCode = "attribute vec4 position;attribute vec2 inputTexCoord;varying vec2 texCoord;uniform highp float inputWidth;uniform highp float inputHeight;varying vec2 leftTexCoord;varying vec2 rightTexCoord;varying vec2 topTexCoord;varying vec2 bottomTexCoord;void main() {gl_Position = position;texCoord = inputTexCoord;highp vec2 widthStep = vec2(1.0 / inputWidth, 0.0);highp vec2 heightStep = vec2(0.0, 1.0 / inputHeight);leftTexCoord = inputTexCoord - widthStep;rightTexCoord = inputTexCoord + widthStep;topTexCoord = inputTexCoord + heightStep;bottomTexCoord = inputTexCoord - heightStep;}";
                private static final String simpleFragmentShaderCode = "varying highp vec2 texCoord;uniform sampler2D sourceImage;void main() {gl_FragColor = texture2D(sourceImage, texCoord);}";
                private static final String simpleVertexShaderCode = "attribute vec4 position;attribute vec2 inputTexCoord;varying vec2 texCoord;void main() {gl_Position = position;texCoord = inputTexCoord;}";
                private static final String toolsFragmentShaderCode = "varying highp vec2 texCoord;uniform sampler2D sourceImage;uniform highp float width;uniform highp float height;uniform sampler2D curvesImage;uniform lowp float skipTone;uniform lowp float shadows;const mediump vec3 hsLuminanceWeighting = vec3(0.3, 0.3, 0.3);uniform lowp float highlights;uniform lowp float contrast;uniform lowp float fadeAmount;const mediump vec3 satLuminanceWeighting = vec3(0.2126, 0.7152, 0.0722);uniform lowp float saturation;uniform lowp float shadowsTintIntensity;uniform lowp float highlightsTintIntensity;uniform lowp vec3 shadowsTintColor;uniform lowp vec3 highlightsTintColor;uniform lowp float exposure;uniform lowp float warmth;uniform lowp float grain;const lowp float permTexUnit = 1.0 / 256.0;const lowp float permTexUnitHalf = 0.5 / 256.0;const lowp float grainsize = 2.3;uniform lowp float vignette;highp float getLuma(highp vec3 rgbP) {return (0.299 * rgbP.r) + (0.587 * rgbP.g) + (0.114 * rgbP.b);}lowp vec3 rgbToHsv(lowp vec3 c) {highp vec4 K = vec4(0.0, -1.0 / 3.0, 2.0 / 3.0, -1.0);highp vec4 p = c.g < c.b ? vec4(c.bg, K.wz) : vec4(c.gb, K.xy);highp vec4 q = c.r < p.x ? vec4(p.xyw, c.r) : vec4(c.r, p.yzx);highp float d = q.x - min(q.w, q.y);highp float e = 1.0e-10;return vec3(abs(q.z + (q.w - q.y) / (6.0 * d + e)), d / (q.x + e), q.x);}lowp vec3 hsvToRgb(lowp vec3 c) {highp vec4 K = vec4(1.0, 2.0 / 3.0, 1.0 / 3.0, 3.0);highp vec3 p = abs(fract(c.xxx + K.xyz) * 6.0 - K.www);return c.z * mix(K.xxx, clamp(p - K.xxx, 0.0, 1.0), c.y);}highp vec3 rgbToHsl(highp vec3 color) {highp vec3 hsl;highp float fmin = min(min(color.r, color.g), color.b);highp float fmax = max(max(color.r, color.g), color.b);highp float delta = fmax - fmin;hsl.z = (fmax + fmin) / 2.0;if (delta == 0.0) {hsl.x = 0.0;hsl.y = 0.0;} else {if (hsl.z < 0.5) {hsl.y = delta / (fmax + fmin);} else {hsl.y = delta / (2.0 - fmax - fmin);}highp float deltaR = (((fmax - color.r) / 6.0) + (delta / 2.0)) / delta;highp float deltaG = (((fmax - color.g) / 6.0) + (delta / 2.0)) / delta;highp float deltaB = (((fmax - color.b) / 6.0) + (delta / 2.0)) / delta;if (color.r == fmax) {hsl.x = deltaB - deltaG;} else if (color.g == fmax) {hsl.x = (1.0 / 3.0) + deltaR - deltaB;} else if (color.b == fmax) {hsl.x = (2.0 / 3.0) + deltaG - deltaR;}if (hsl.x < 0.0) {hsl.x += 1.0;} else if (hsl.x > 1.0) {hsl.x -= 1.0;}}return hsl;}highp float hueToRgb(highp float f1, highp float f2, highp float hue) {if (hue < 0.0) {hue += 1.0;} else if (hue > 1.0) {hue -= 1.0;}highp float res;if ((6.0 * hue) < 1.0) {res = f1 + (f2 - f1) * 6.0 * hue;} else if ((2.0 * hue) < 1.0) {res = f2;} else if ((3.0 * hue) < 2.0) {res = f1 + (f2 - f1) * ((2.0 / 3.0) - hue) * 6.0;} else {res = f1;} return res;}highp vec3 hslToRgb(highp vec3 hsl) {if (hsl.y == 0.0) {return vec3(hsl.z);} else {highp float f2;if (hsl.z < 0.5) {f2 = hsl.z * (1.0 + hsl.y);} else {f2 = (hsl.z + hsl.y) - (hsl.y * hsl.z);}highp float f1 = 2.0 * hsl.z - f2;return vec3(hueToRgb(f1, f2, hsl.x + (1.0/3.0)), hueToRgb(f1, f2, hsl.x), hueToRgb(f1, f2, hsl.x - (1.0/3.0)));}}highp vec3 rgbToYuv(highp vec3 inP) {highp float luma = getLuma(inP);return vec3(luma, (1.0 / 1.772) * (inP.b - luma), (1.0 / 1.402) * (inP.r - luma));}lowp vec3 yuvToRgb(highp vec3 inP) {return vec3(1.402 * inP.b + inP.r, (inP.r - (0.299 * 1.402 / 0.587) * inP.b - (0.114 * 1.772 / 0.587) * inP.g), 1.772 * inP.g + inP.r);}lowp float easeInOutSigmoid(lowp float value, lowp float strength) {if (value > 0.5) {return 1.0 - pow(2.0 - 2.0 * value, 1.0 / (1.0 - strength)) * 0.5;} else {return pow(2.0 * value, 1.0 / (1.0 - strength)) * 0.5;}}lowp vec3 applyLuminanceCurve(lowp vec3 pixel) {highp float index = floor(clamp(pixel.z / (1.0 / 200.0), 0.0, 199.0));pixel.y = mix(0.0, pixel.y, smoothstep(0.0, 0.1, pixel.z) * (1.0 - smoothstep(0.8, 1.0, pixel.z)));pixel.z = texture2D(curvesImage, vec2(1.0 / 200.0 * index, 0)).a;return pixel;}lowp vec3 applyRGBCurve(lowp vec3 pixel) {highp float index = floor(clamp(pixel.r / (1.0 / 200.0), 0.0, 199.0));pixel.r = texture2D(curvesImage, vec2(1.0 / 200.0 * index, 0)).r;index = floor(clamp(pixel.g / (1.0 / 200.0), 0.0, 199.0));pixel.g = clamp(texture2D(curvesImage, vec2(1.0 / 200.0 * index, 0)).g, 0.0, 1.0);index = floor(clamp(pixel.b / (1.0 / 200.0), 0.0, 199.0));pixel.b = clamp(texture2D(curvesImage, vec2(1.0 / 200.0 * index, 0)).b, 0.0, 1.0);return pixel;}highp vec3 fadeAdjust(highp vec3 color, highp float fadeVal) {return (color * (1.0 - fadeVal)) + ((color + (vec3(-0.9772) * pow(vec3(color), vec3(3.0)) + vec3(1.708) * pow(vec3(color), vec3(2.0)) + vec3(-0.1603) * vec3(color) + vec3(0.2878) - color * vec3(0.9))) * fadeVal);}lowp vec3 tintRaiseShadowsCurve(lowp vec3 color) {return vec3(-0.003671) * pow(color, vec3(3.0)) + vec3(0.3842) * pow(color, vec3(2.0)) + vec3(0.3764) * color + vec3(0.2515);}lowp vec3 tintShadows(lowp vec3 texel, lowp vec3 tintColor, lowp float tintAmount) {return clamp(mix(texel, mix(texel, tintRaiseShadowsCurve(texel), tintColor), tintAmount), 0.0, 1.0);} lowp vec3 tintHighlights(lowp vec3 texel, lowp vec3 tintColor, lowp float tintAmount) {return clamp(mix(texel, mix(texel, vec3(1.0) - tintRaiseShadowsCurve(vec3(1.0) - texel), (vec3(1.0) - tintColor)), tintAmount), 0.0, 1.0);}highp vec4 rnm(in highp vec2 tc) {highp float noise = sin(dot(tc, vec2(12.9898, 78.233))) * 43758.5453;return vec4(fract(noise), fract(noise * 1.2154), fract(noise * 1.3453), fract(noise * 1.3647)) * 2.0 - 1.0;}highp float fade(in highp float t) {return t * t * t * (t * (t * 6.0 - 15.0) + 10.0);}highp float pnoise3D(in highp vec3 p) {highp vec3 pi = permTexUnit * floor(p) + permTexUnitHalf;highp vec3 pf = fract(p);highp float perm = rnm(pi.xy).a;highp float n000 = dot(rnm(vec2(perm, pi.z)).rgb * 4.0 - 1.0, pf);highp float n001 = dot(rnm(vec2(perm, pi.z + permTexUnit)).rgb * 4.0 - 1.0, pf - vec3(0.0, 0.0, 1.0));perm = rnm(pi.xy + vec2(0.0, permTexUnit)).a;highp float n010 = dot(rnm(vec2(perm, pi.z)).rgb * 4.0 - 1.0, pf - vec3(0.0, 1.0, 0.0));highp float n011 = dot(rnm(vec2(perm, pi.z + permTexUnit)).rgb * 4.0 - 1.0, pf - vec3(0.0, 1.0, 1.0));perm = rnm(pi.xy + vec2(permTexUnit, 0.0)).a;highp float n100 = dot(rnm(vec2(perm, pi.z)).rgb * 4.0 - 1.0, pf - vec3(1.0, 0.0, 0.0));highp float n101 = dot(rnm(vec2(perm, pi.z + permTexUnit)).rgb * 4.0 - 1.0, pf - vec3(1.0, 0.0, 1.0));perm = rnm(pi.xy + vec2(permTexUnit, permTexUnit)).a;highp float n110 = dot(rnm(vec2(perm, pi.z)).rgb * 4.0 - 1.0, pf - vec3(1.0, 1.0, 0.0));highp float n111 = dot(rnm(vec2(perm, pi.z + permTexUnit)).rgb * 4.0 - 1.0, pf - vec3(1.0, 1.0, 1.0));highp vec4 n_x = mix(vec4(n000, n001, n010, n011), vec4(n100, n101, n110, n111), fade(pf.x));highp vec2 n_xy = mix(n_x.xy, n_x.zw, fade(pf.y));return mix(n_xy.x, n_xy.y, fade(pf.z));}lowp vec2 coordRot(in lowp vec2 tc, in lowp float angle) {return vec2(((tc.x * 2.0 - 1.0) * cos(angle) - (tc.y * 2.0 - 1.0) * sin(angle)) * 0.5 + 0.5, ((tc.y * 2.0 - 1.0) * cos(angle) + (tc.x * 2.0 - 1.0) * sin(angle)) * 0.5 + 0.5);}void main() {lowp vec4 source = texture2D(sourceImage, texCoord);lowp vec4 result = source;const lowp float toolEpsilon = 0.005;if (skipTone < toolEpsilon) {result = vec4(applyRGBCurve(hslToRgb(applyLuminanceCurve(rgbToHsl(result.rgb)))), result.a);}mediump float hsLuminance = dot(result.rgb, hsLuminanceWeighting);mediump float shadow = clamp((pow(hsLuminance, 1.0 / shadows) + (-0.76) * pow(hsLuminance, 2.0 / shadows)) - hsLuminance, 0.0, 1.0);mediump float highlight = clamp((1.0 - (pow(1.0 - hsLuminance, 1.0 / (2.0 - highlights)) + (-0.8) * pow(1.0 - hsLuminance, 2.0 / (2.0 - highlights)))) - hsLuminance, -1.0, 0.0);lowp vec3 hsresult = vec3(0.0, 0.0, 0.0) + ((hsLuminance + shadow + highlight) - 0.0) * ((result.rgb - vec3(0.0, 0.0, 0.0)) / (hsLuminance - 0.0));mediump float contrastedLuminance = ((hsLuminance - 0.5) * 1.5) + 0.5;mediump float whiteInterp = contrastedLuminance * contrastedLuminance * contrastedLuminance;mediump float whiteTarget = clamp(highlights, 1.0, 2.0) - 1.0;hsresult = mix(hsresult, vec3(1.0), whiteInterp * whiteTarget);mediump float invContrastedLuminance = 1.0 - contrastedLuminance;mediump float blackInterp = invContrastedLuminance * invContrastedLuminance * invContrastedLuminance;mediump float blackTarget = 1.0 - clamp(shadows, 0.0, 1.0);hsresult = mix(hsresult, vec3(0.0), blackInterp * blackTarget);result = vec4(hsresult.rgb, result.a);result = vec4(clamp(((result.rgb - vec3(0.5)) * contrast + vec3(0.5)), 0.0, 1.0), result.a);if (abs(fadeAmount) > toolEpsilon) {result.rgb = fadeAdjust(result.rgb, fadeAmount);}lowp float satLuminance = dot(result.rgb, satLuminanceWeighting);lowp vec3 greyScaleColor = vec3(satLuminance);result = vec4(clamp(mix(greyScaleColor, result.rgb, saturation), 0.0, 1.0), result.a);if (abs(shadowsTintIntensity) > toolEpsilon) {result.rgb = tintShadows(result.rgb, shadowsTintColor, shadowsTintIntensity * 2.0);}if (abs(highlightsTintIntensity) > toolEpsilon) {result.rgb = tintHighlights(result.rgb, highlightsTintColor, highlightsTintIntensity * 2.0);}if (abs(exposure) > toolEpsilon) {mediump float mag = exposure * 1.045;mediump float exppower = 1.0 + abs(mag);if (mag < 0.0) {exppower = 1.0 / exppower;}result.r = 1.0 - pow((1.0 - result.r), exppower);result.g = 1.0 - pow((1.0 - result.g), exppower);result.b = 1.0 - pow((1.0 - result.b), exppower);}if (abs(warmth) > toolEpsilon) {highp vec3 yuvVec;if (warmth > 0.0 ) {yuvVec = vec3(0.1765, -0.1255, 0.0902);} else {yuvVec = -vec3(0.0588, 0.1569, -0.1255);}highp vec3 yuvColor = rgbToYuv(result.rgb);highp float luma = yuvColor.r;highp float curveScale = sin(luma * 3.14159);yuvColor += 0.375 * warmth * curveScale * yuvVec;result.rgb = yuvToRgb(yuvColor);}if (abs(grain) > toolEpsilon) {highp vec3 rotOffset = vec3(1.425, 3.892, 5.835);highp vec2 rotCoordsR = coordRot(texCoord, rotOffset.x);highp vec3 noise = vec3(pnoise3D(vec3(rotCoordsR * vec2(width / grainsize, height / grainsize),0.0)));lowp vec3 lumcoeff = vec3(0.299,0.587,0.114);lowp float luminance = dot(result.rgb, lumcoeff);lowp float lum = smoothstep(0.2, 0.0, luminance);lum += luminance;noise = mix(noise,vec3(0.0),pow(lum,4.0));result.rgb = result.rgb + noise * grain;}if (abs(vignette) > toolEpsilon) {const lowp float midpoint = 0.7;const lowp float fuzziness = 0.62;lowp float radDist = length(texCoord - 0.5) / sqrt(0.5);lowp float mag = easeInOutSigmoid(radDist * midpoint, fuzziness) * vignette * 0.645;result.rgb = mix(pow(result.rgb, vec3(1.0 / (1.0 - mag))), vec3(0.0), mag * mag);}gl_FragColor = result;}";
                private final int EGL_CONTEXT_CLIENT_VERSION = 12440;
                private final int EGL_OPENGL_ES2_BIT = 4;
                private int blurHeightHandle;
                private int blurInputTexCoordHandle;
                private int blurPositionHandle;
                private int blurShaderProgram;
                private int blurSourceImageHandle;
                private int blurWidthHandle;
                private boolean blured;
                private int contrastHandle;
                private Bitmap currentBitmap;
                private int[] curveTextures = new int[1];
                private int curvesImageHandle;
                private Runnable drawRunnable = new C12481();
                private EGL10 egl10;
                private EGLConfig eglConfig;
                private EGLContext eglContext;
                private EGLDisplay eglDisplay;
                private EGLSurface eglSurface;
                private int enhanceInputImageTexture2Handle;
                private int enhanceInputTexCoordHandle;
                private int enhanceIntensityHandle;
                private int enhancePositionHandle;
                private int enhanceShaderProgram;
                private int enhanceSourceImageHandle;
                private int[] enhanceTextures = new int[2];
                private int exposureHandle;
                private int fadeAmountHandle;
                private GL gl;
                private int grainHandle;
                private int heightHandle;
                private int highlightsHandle;
                private int highlightsTintColorHandle;
                private int highlightsTintIntensityHandle;
                private boolean hsvGenerated;
                private boolean initied;
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
                private int renderBufferHeight;
                private int renderBufferWidth;
                private int[] renderFrameBuffer = new int[3];
                private int[] renderTexture = new int[3];
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
                private int simpleInputTexCoordHandle;
                private int simplePositionHandle;
                private int simpleShaderProgram;
                private int simpleSourceImageHandle;
                private int skipToneHandle;
                private int sourceImageHandle;
                private volatile int surfaceHeight;
                private SurfaceTexture surfaceTexture;
                private volatile int surfaceWidth;
                private FloatBuffer textureBuffer;
                private int toolsShaderProgram;
                private FloatBuffer vertexBuffer;
                private FloatBuffer vertexInvertBuffer;
                private int vignetteHandle;
                private int warmthHandle;
                private int widthHandle;

                /* renamed from: org.telegram.ui.Components.PhotoFilterView$EGLThread$1 */
                class C12481 implements Runnable {
                    C12481() {
                    }

                    public void run() {
                        if (!EGLThread.this.initied) {
                            return;
                        }
                        if ((EGLThread.this.eglContext.equals(EGLThread.this.egl10.eglGetCurrentContext()) && EGLThread.this.eglSurface.equals(EGLThread.this.egl10.eglGetCurrentSurface(12377))) || EGLThread.this.egl10.eglMakeCurrent(EGLThread.this.eglDisplay, EGLThread.this.eglSurface, EGLThread.this.eglSurface, EGLThread.this.eglContext)) {
                            GLES20.glViewport(0, 0, EGLThread.this.renderBufferWidth, EGLThread.this.renderBufferHeight);
                            EGLThread.this.drawEnhancePass();
                            EGLThread.this.drawSharpenPass();
                            EGLThread.this.drawCustomParamsPass();
                            EGLThread.this.blured = EGLThread.this.drawBlurPass();
                            GLES20.glViewport(0, 0, EGLThread.this.surfaceWidth, EGLThread.this.surfaceHeight);
                            GLES20.glBindFramebuffer(36160, 0);
                            GLES20.glClear(0);
                            GLES20.glUseProgram(EGLThread.this.simpleShaderProgram);
                            GLES20.glActiveTexture(33984);
                            GLES20.glBindTexture(3553, EGLThread.this.renderTexture[EGLThread.this.blured ^ 1]);
                            GLES20.glUniform1i(EGLThread.this.simpleSourceImageHandle, 0);
                            GLES20.glEnableVertexAttribArray(EGLThread.this.simpleInputTexCoordHandle);
                            GLES20.glVertexAttribPointer(EGLThread.this.simpleInputTexCoordHandle, 2, 5126, false, 8, EGLThread.this.textureBuffer);
                            GLES20.glEnableVertexAttribArray(EGLThread.this.simplePositionHandle);
                            GLES20.glVertexAttribPointer(EGLThread.this.simplePositionHandle, 2, 5126, false, 8, EGLThread.this.vertexBuffer);
                            GLES20.glDrawArrays(5, 0, 4);
                            EGLThread.this.egl10.eglSwapBuffers(EGLThread.this.eglDisplay, EGLThread.this.eglSurface);
                            return;
                        }
                        if (BuildVars.LOGS_ENABLED) {
                            StringBuilder stringBuilder = new StringBuilder();
                            stringBuilder.append("eglMakeCurrent failed ");
                            stringBuilder.append(GLUtils.getEGLErrorString(EGLThread.this.egl10.eglGetError()));
                            FileLog.m1e(stringBuilder.toString());
                        }
                    }
                }

                /* renamed from: org.telegram.ui.Components.PhotoFilterView$EGLThread$3 */
                class C12503 implements Runnable {
                    C12503() {
                    }

                    public void run() {
                        EGLThread.this.finish();
                        EGLThread.this.currentBitmap = null;
                        Looper myLooper = Looper.myLooper();
                        if (myLooper != null) {
                            myLooper.quit();
                        }
                    }
                }

                public EGLThread(SurfaceTexture surfaceTexture, Bitmap bitmap) {
                    super("EGLThread");
                    this.surfaceTexture = surfaceTexture;
                    this.currentBitmap = bitmap;
                }

                private int loadShader(int i, String str) {
                    i = GLES20.glCreateShader(i);
                    GLES20.glShaderSource(i, str);
                    GLES20.glCompileShader(i);
                    str = new int[1];
                    GLES20.glGetShaderiv(i, 35713, str, 0);
                    if (str[0] != null) {
                        return i;
                    }
                    if (BuildVars.LOGS_ENABLED != null) {
                        FileLog.m1e(GLES20.glGetShaderInfoLog(i));
                    }
                    GLES20.glDeleteShader(i);
                    return 0;
                }

                private boolean initGL() {
                    this.egl10 = (EGL10) EGLContext.getEGL();
                    this.eglDisplay = this.egl10.eglGetDisplay(EGL10.EGL_DEFAULT_DISPLAY);
                    if (this.eglDisplay == EGL10.EGL_NO_DISPLAY) {
                        if (BuildVars.LOGS_ENABLED) {
                            StringBuilder stringBuilder = new StringBuilder();
                            stringBuilder.append("eglGetDisplay failed ");
                            stringBuilder.append(GLUtils.getEGLErrorString(this.egl10.eglGetError()));
                            FileLog.m1e(stringBuilder.toString());
                        }
                        finish();
                        return false;
                    }
                    if (this.egl10.eglInitialize(this.eglDisplay, new int[2])) {
                        int[] iArr = new int[1];
                        EGLConfig[] eGLConfigArr = new EGLConfig[1];
                        if (!this.egl10.eglChooseConfig(this.eglDisplay, new int[]{12352, 4, 12324, 8, 12323, 8, 12322, 8, 12321, 8, 12325, 0, 12326, 0, 12344}, eGLConfigArr, 1, iArr)) {
                            if (BuildVars.LOGS_ENABLED) {
                                stringBuilder = new StringBuilder();
                                stringBuilder.append("eglChooseConfig failed ");
                                stringBuilder.append(GLUtils.getEGLErrorString(this.egl10.eglGetError()));
                                FileLog.m1e(stringBuilder.toString());
                            }
                            finish();
                            return false;
                        } else if (iArr[0] > 0) {
                            this.eglConfig = eGLConfigArr[0];
                            this.eglContext = this.egl10.eglCreateContext(this.eglDisplay, this.eglConfig, EGL10.EGL_NO_CONTEXT, new int[]{12440, 2, 12344});
                            if (this.eglContext == null) {
                                if (BuildVars.LOGS_ENABLED) {
                                    stringBuilder = new StringBuilder();
                                    stringBuilder.append("eglCreateContext failed ");
                                    stringBuilder.append(GLUtils.getEGLErrorString(this.egl10.eglGetError()));
                                    FileLog.m1e(stringBuilder.toString());
                                }
                                finish();
                                return false;
                            } else if (this.surfaceTexture instanceof SurfaceTexture) {
                                this.eglSurface = this.egl10.eglCreateWindowSurface(this.eglDisplay, this.eglConfig, this.surfaceTexture, null);
                                if (this.eglSurface != null) {
                                    if (this.eglSurface != EGL10.EGL_NO_SURFACE) {
                                        if (this.egl10.eglMakeCurrent(this.eglDisplay, this.eglSurface, this.eglSurface, this.eglContext)) {
                                            this.gl = this.eglContext.getGL();
                                            float[] fArr = new float[]{-1.0f, 1.0f, 1.0f, 1.0f, -1.0f, -1.0f, 1.0f, -1.0f};
                                            ByteBuffer allocateDirect = ByteBuffer.allocateDirect(fArr.length * 4);
                                            allocateDirect.order(ByteOrder.nativeOrder());
                                            this.vertexBuffer = allocateDirect.asFloatBuffer();
                                            this.vertexBuffer.put(fArr);
                                            this.vertexBuffer.position(0);
                                            fArr = new float[]{-1.0f, -1.0f, 1.0f, -1.0f, -1.0f, 1.0f, 1.0f, 1.0f};
                                            allocateDirect = ByteBuffer.allocateDirect(fArr.length * 4);
                                            allocateDirect.order(ByteOrder.nativeOrder());
                                            this.vertexInvertBuffer = allocateDirect.asFloatBuffer();
                                            this.vertexInvertBuffer.put(fArr);
                                            this.vertexInvertBuffer.position(0);
                                            float[] fArr2 = new float[]{0.0f, 0.0f, 1.0f, 0.0f, 0.0f, 1.0f, 1.0f, 1.0f};
                                            ByteBuffer allocateDirect2 = ByteBuffer.allocateDirect(fArr2.length * 4);
                                            allocateDirect2.order(ByteOrder.nativeOrder());
                                            this.textureBuffer = allocateDirect2.asFloatBuffer();
                                            this.textureBuffer.put(fArr2);
                                            this.textureBuffer.position(0);
                                            GLES20.glGenTextures(1, this.curveTextures, 0);
                                            GLES20.glGenTextures(2, this.enhanceTextures, 0);
                                            int loadShader = loadShader(35633, simpleVertexShaderCode);
                                            int loadShader2 = loadShader(35632, toolsFragmentShaderCode);
                                            if (loadShader == 0 || loadShader2 == 0) {
                                                finish();
                                                return false;
                                            }
                                            this.toolsShaderProgram = GLES20.glCreateProgram();
                                            GLES20.glAttachShader(this.toolsShaderProgram, loadShader);
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
                                            loadShader = loadShader(35633, sharpenVertexShaderCode);
                                            loadShader2 = loadShader(35632, sharpenFragmentShaderCode);
                                            if (loadShader == 0 || loadShader2 == 0) {
                                                finish();
                                                return false;
                                            }
                                            this.sharpenShaderProgram = GLES20.glCreateProgram();
                                            GLES20.glAttachShader(this.sharpenShaderProgram, loadShader);
                                            GLES20.glAttachShader(this.sharpenShaderProgram, loadShader2);
                                            GLES20.glBindAttribLocation(this.sharpenShaderProgram, 0, "position");
                                            GLES20.glBindAttribLocation(this.sharpenShaderProgram, 1, "inputTexCoord");
                                            GLES20.glLinkProgram(this.sharpenShaderProgram);
                                            iArr2 = new int[1];
                                            GLES20.glGetProgramiv(this.sharpenShaderProgram, 35714, iArr2, 0);
                                            if (iArr2[0] == 0) {
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
                                            loadShader = loadShader(35633, blurVertexShaderCode);
                                            loadShader2 = loadShader(35632, blurFragmentShaderCode);
                                            if (loadShader == 0 || loadShader2 == 0) {
                                                finish();
                                                return false;
                                            }
                                            this.blurShaderProgram = GLES20.glCreateProgram();
                                            GLES20.glAttachShader(this.blurShaderProgram, loadShader);
                                            GLES20.glAttachShader(this.blurShaderProgram, loadShader2);
                                            GLES20.glBindAttribLocation(this.blurShaderProgram, 0, "position");
                                            GLES20.glBindAttribLocation(this.blurShaderProgram, 1, "inputTexCoord");
                                            GLES20.glLinkProgram(this.blurShaderProgram);
                                            iArr2 = new int[1];
                                            GLES20.glGetProgramiv(this.blurShaderProgram, 35714, iArr2, 0);
                                            if (iArr2[0] == 0) {
                                                GLES20.glDeleteProgram(this.blurShaderProgram);
                                                this.blurShaderProgram = 0;
                                            } else {
                                                this.blurPositionHandle = GLES20.glGetAttribLocation(this.blurShaderProgram, "position");
                                                this.blurInputTexCoordHandle = GLES20.glGetAttribLocation(this.blurShaderProgram, "inputTexCoord");
                                                this.blurSourceImageHandle = GLES20.glGetUniformLocation(this.blurShaderProgram, "sourceImage");
                                                this.blurWidthHandle = GLES20.glGetUniformLocation(this.blurShaderProgram, "texelWidthOffset");
                                                this.blurHeightHandle = GLES20.glGetUniformLocation(this.blurShaderProgram, "texelHeightOffset");
                                            }
                                            loadShader = loadShader(35633, simpleVertexShaderCode);
                                            loadShader2 = loadShader(35632, linearBlurFragmentShaderCode);
                                            if (loadShader == 0 || loadShader2 == 0) {
                                                finish();
                                                return false;
                                            }
                                            this.linearBlurShaderProgram = GLES20.glCreateProgram();
                                            GLES20.glAttachShader(this.linearBlurShaderProgram, loadShader);
                                            GLES20.glAttachShader(this.linearBlurShaderProgram, loadShader2);
                                            GLES20.glBindAttribLocation(this.linearBlurShaderProgram, 0, "position");
                                            GLES20.glBindAttribLocation(this.linearBlurShaderProgram, 1, "inputTexCoord");
                                            GLES20.glLinkProgram(this.linearBlurShaderProgram);
                                            iArr2 = new int[1];
                                            GLES20.glGetProgramiv(this.linearBlurShaderProgram, 35714, iArr2, 0);
                                            if (iArr2[0] == 0) {
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
                                            loadShader = loadShader(35633, simpleVertexShaderCode);
                                            loadShader2 = loadShader(35632, radialBlurFragmentShaderCode);
                                            if (loadShader == 0 || loadShader2 == 0) {
                                                finish();
                                                return false;
                                            }
                                            this.radialBlurShaderProgram = GLES20.glCreateProgram();
                                            GLES20.glAttachShader(this.radialBlurShaderProgram, loadShader);
                                            GLES20.glAttachShader(this.radialBlurShaderProgram, loadShader2);
                                            GLES20.glBindAttribLocation(this.radialBlurShaderProgram, 0, "position");
                                            GLES20.glBindAttribLocation(this.radialBlurShaderProgram, 1, "inputTexCoord");
                                            GLES20.glLinkProgram(this.radialBlurShaderProgram);
                                            iArr2 = new int[1];
                                            GLES20.glGetProgramiv(this.radialBlurShaderProgram, 35714, iArr2, 0);
                                            if (iArr2[0] == 0) {
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
                                            loadShader = loadShader(35633, simpleVertexShaderCode);
                                            loadShader2 = loadShader(35632, rgbToHsvFragmentShaderCode);
                                            if (loadShader == 0 || loadShader2 == 0) {
                                                finish();
                                                return false;
                                            }
                                            this.rgbToHsvShaderProgram = GLES20.glCreateProgram();
                                            GLES20.glAttachShader(this.rgbToHsvShaderProgram, loadShader);
                                            GLES20.glAttachShader(this.rgbToHsvShaderProgram, loadShader2);
                                            GLES20.glBindAttribLocation(this.rgbToHsvShaderProgram, 0, "position");
                                            GLES20.glBindAttribLocation(this.rgbToHsvShaderProgram, 1, "inputTexCoord");
                                            GLES20.glLinkProgram(this.rgbToHsvShaderProgram);
                                            iArr2 = new int[1];
                                            GLES20.glGetProgramiv(this.rgbToHsvShaderProgram, 35714, iArr2, 0);
                                            if (iArr2[0] == 0) {
                                                GLES20.glDeleteProgram(this.rgbToHsvShaderProgram);
                                                this.rgbToHsvShaderProgram = 0;
                                            } else {
                                                this.rgbToHsvPositionHandle = GLES20.glGetAttribLocation(this.rgbToHsvShaderProgram, "position");
                                                this.rgbToHsvInputTexCoordHandle = GLES20.glGetAttribLocation(this.rgbToHsvShaderProgram, "inputTexCoord");
                                                this.rgbToHsvSourceImageHandle = GLES20.glGetUniformLocation(this.rgbToHsvShaderProgram, "sourceImage");
                                            }
                                            loadShader = loadShader(35633, simpleVertexShaderCode);
                                            loadShader2 = loadShader(35632, enhanceFragmentShaderCode);
                                            if (loadShader == 0 || loadShader2 == 0) {
                                                finish();
                                                return false;
                                            }
                                            this.enhanceShaderProgram = GLES20.glCreateProgram();
                                            GLES20.glAttachShader(this.enhanceShaderProgram, loadShader);
                                            GLES20.glAttachShader(this.enhanceShaderProgram, loadShader2);
                                            GLES20.glBindAttribLocation(this.enhanceShaderProgram, 0, "position");
                                            GLES20.glBindAttribLocation(this.enhanceShaderProgram, 1, "inputTexCoord");
                                            GLES20.glLinkProgram(this.enhanceShaderProgram);
                                            iArr2 = new int[1];
                                            GLES20.glGetProgramiv(this.enhanceShaderProgram, 35714, iArr2, 0);
                                            if (iArr2[0] == 0) {
                                                GLES20.glDeleteProgram(this.enhanceShaderProgram);
                                                this.enhanceShaderProgram = 0;
                                            } else {
                                                this.enhancePositionHandle = GLES20.glGetAttribLocation(this.enhanceShaderProgram, "position");
                                                this.enhanceInputTexCoordHandle = GLES20.glGetAttribLocation(this.enhanceShaderProgram, "inputTexCoord");
                                                this.enhanceSourceImageHandle = GLES20.glGetUniformLocation(this.enhanceShaderProgram, "sourceImage");
                                                this.enhanceIntensityHandle = GLES20.glGetUniformLocation(this.enhanceShaderProgram, "intensity");
                                                this.enhanceInputImageTexture2Handle = GLES20.glGetUniformLocation(this.enhanceShaderProgram, "inputImageTexture2");
                                            }
                                            loadShader = loadShader(35633, simpleVertexShaderCode);
                                            loadShader2 = loadShader(35632, simpleFragmentShaderCode);
                                            if (loadShader == 0 || loadShader2 == 0) {
                                                finish();
                                                return false;
                                            }
                                            this.simpleShaderProgram = GLES20.glCreateProgram();
                                            GLES20.glAttachShader(this.simpleShaderProgram, loadShader);
                                            GLES20.glAttachShader(this.simpleShaderProgram, loadShader2);
                                            GLES20.glBindAttribLocation(this.simpleShaderProgram, 0, "position");
                                            GLES20.glBindAttribLocation(this.simpleShaderProgram, 1, "inputTexCoord");
                                            GLES20.glLinkProgram(this.simpleShaderProgram);
                                            iArr2 = new int[1];
                                            GLES20.glGetProgramiv(this.simpleShaderProgram, 35714, iArr2, 0);
                                            if (iArr2[0] == 0) {
                                                GLES20.glDeleteProgram(this.simpleShaderProgram);
                                                this.simpleShaderProgram = 0;
                                            } else {
                                                this.simplePositionHandle = GLES20.glGetAttribLocation(this.simpleShaderProgram, "position");
                                                this.simpleInputTexCoordHandle = GLES20.glGetAttribLocation(this.simpleShaderProgram, "inputTexCoord");
                                                this.simpleSourceImageHandle = GLES20.glGetUniformLocation(this.simpleShaderProgram, "sourceImage");
                                            }
                                            if (this.currentBitmap != null) {
                                                loadTexture(this.currentBitmap);
                                            }
                                            return true;
                                        }
                                        if (BuildVars.LOGS_ENABLED) {
                                            stringBuilder = new StringBuilder();
                                            stringBuilder.append("eglMakeCurrent failed ");
                                            stringBuilder.append(GLUtils.getEGLErrorString(this.egl10.eglGetError()));
                                            FileLog.m1e(stringBuilder.toString());
                                        }
                                        finish();
                                        return false;
                                    }
                                }
                                if (BuildVars.LOGS_ENABLED) {
                                    stringBuilder = new StringBuilder();
                                    stringBuilder.append("createWindowSurface failed ");
                                    stringBuilder.append(GLUtils.getEGLErrorString(this.egl10.eglGetError()));
                                    FileLog.m1e(stringBuilder.toString());
                                }
                                finish();
                                return false;
                            } else {
                                finish();
                                return false;
                            }
                        } else {
                            if (BuildVars.LOGS_ENABLED) {
                                FileLog.m1e("eglConfig not initialized");
                            }
                            finish();
                            return false;
                        }
                    }
                    if (BuildVars.LOGS_ENABLED) {
                        stringBuilder = new StringBuilder();
                        stringBuilder.append("eglInitialize failed ");
                        stringBuilder.append(GLUtils.getEGLErrorString(this.egl10.eglGetError()));
                        FileLog.m1e(stringBuilder.toString());
                    }
                    finish();
                    return false;
                }

                public void finish() {
                    if (this.eglSurface != null) {
                        this.egl10.eglMakeCurrent(this.eglDisplay, EGL10.EGL_NO_SURFACE, EGL10.EGL_NO_SURFACE, EGL10.EGL_NO_CONTEXT);
                        this.egl10.eglDestroySurface(this.eglDisplay, this.eglSurface);
                        this.eglSurface = null;
                    }
                    if (this.eglContext != null) {
                        this.egl10.eglDestroyContext(this.eglDisplay, this.eglContext);
                        this.eglContext = null;
                    }
                    if (this.eglDisplay != null) {
                        this.egl10.eglTerminate(this.eglDisplay);
                        this.eglDisplay = null;
                    }
                }

                private void drawEnhancePass() {
                    Throwable th;
                    Buffer buffer;
                    if (!this.hsvGenerated) {
                        Buffer allocateDirect;
                        GLES20.glBindFramebuffer(36160, r1.renderFrameBuffer[0]);
                        GLES20.glFramebufferTexture2D(36160, 36064, 3553, r1.renderTexture[0], 0);
                        GLES20.glClear(0);
                        GLES20.glUseProgram(r1.rgbToHsvShaderProgram);
                        GLES20.glActiveTexture(33984);
                        GLES20.glBindTexture(3553, r1.renderTexture[1]);
                        GLES20.glUniform1i(r1.rgbToHsvSourceImageHandle, 0);
                        GLES20.glEnableVertexAttribArray(r1.rgbToHsvInputTexCoordHandle);
                        GLES20.glVertexAttribPointer(r1.rgbToHsvInputTexCoordHandle, 2, 5126, false, 8, r1.textureBuffer);
                        GLES20.glEnableVertexAttribArray(r1.rgbToHsvPositionHandle);
                        GLES20.glVertexAttribPointer(r1.rgbToHsvPositionHandle, 2, 5126, false, 8, r1.vertexBuffer);
                        GLES20.glDrawArrays(5, 0, 4);
                        Buffer allocateDirect2 = ByteBuffer.allocateDirect((r1.renderBufferWidth * r1.renderBufferHeight) * 4);
                        GLES20.glReadPixels(0, 0, r1.renderBufferWidth, r1.renderBufferHeight, 6408, 5121, allocateDirect2);
                        GLES20.glBindTexture(3553, r1.enhanceTextures[0]);
                        GLES20.glTexParameteri(3553, 10241, 9729);
                        GLES20.glTexParameteri(3553, 10240, 9729);
                        GLES20.glTexParameteri(3553, 10242, 33071);
                        GLES20.glTexParameteri(3553, 10243, 33071);
                        int i = 33071;
                        int i2 = 10242;
                        int i3 = 10240;
                        GLES20.glTexImage2D(3553, 0, 6408, r1.renderBufferWidth, r1.renderBufferHeight, 0, 6408, 5121, allocateDirect2);
                        try {
                            allocateDirect = ByteBuffer.allocateDirect(MessagesController.UPDATE_MASK_CHAT_ADMINS);
                            try {
                                Utilities.calcCDT(allocateDirect2, r1.renderBufferWidth, r1.renderBufferHeight, allocateDirect);
                            } catch (Throwable e) {
                                th = e;
                                FileLog.m3e(th);
                                buffer = allocateDirect;
                                GLES20.glBindTexture(3553, r1.enhanceTextures[1]);
                                GLES20.glTexParameteri(3553, 10241, 9729);
                                GLES20.glTexParameteri(3553, i3, 9729);
                                GLES20.glTexParameteri(3553, i2, i);
                                GLES20.glTexParameteri(3553, 10243, i);
                                GLES20.glTexImage2D(3553, 0, 6408, 256, 16, 0, 6408, 5121, buffer);
                                r1.hsvGenerated = true;
                                GLES20.glBindFramebuffer(36160, r1.renderFrameBuffer[1]);
                                GLES20.glFramebufferTexture2D(36160, 36064, 3553, r1.renderTexture[1], 0);
                                GLES20.glClear(0);
                                GLES20.glUseProgram(r1.enhanceShaderProgram);
                                GLES20.glActiveTexture(33984);
                                GLES20.glBindTexture(3553, r1.enhanceTextures[0]);
                                GLES20.glUniform1i(r1.enhanceSourceImageHandle, 0);
                                GLES20.glActiveTexture(33985);
                                GLES20.glBindTexture(3553, r1.enhanceTextures[1]);
                                GLES20.glUniform1i(r1.enhanceInputImageTexture2Handle, 1);
                                if (PhotoFilterView.this.showOriginal) {
                                    GLES20.glUniform1f(r1.enhanceIntensityHandle, 0.0f);
                                } else {
                                    GLES20.glUniform1f(r1.enhanceIntensityHandle, PhotoFilterView.this.getEnhanceValue());
                                }
                                GLES20.glEnableVertexAttribArray(r1.enhanceInputTexCoordHandle);
                                GLES20.glVertexAttribPointer(r1.enhanceInputTexCoordHandle, 2, 5126, false, 8, r1.textureBuffer);
                                GLES20.glEnableVertexAttribArray(r1.enhancePositionHandle);
                                GLES20.glVertexAttribPointer(r1.enhancePositionHandle, 2, 5126, false, 8, r1.vertexBuffer);
                                GLES20.glDrawArrays(5, 0, 4);
                            }
                        } catch (Throwable e2) {
                            th = e2;
                            allocateDirect = null;
                            FileLog.m3e(th);
                            buffer = allocateDirect;
                            GLES20.glBindTexture(3553, r1.enhanceTextures[1]);
                            GLES20.glTexParameteri(3553, 10241, 9729);
                            GLES20.glTexParameteri(3553, i3, 9729);
                            GLES20.glTexParameteri(3553, i2, i);
                            GLES20.glTexParameteri(3553, 10243, i);
                            GLES20.glTexImage2D(3553, 0, 6408, 256, 16, 0, 6408, 5121, buffer);
                            r1.hsvGenerated = true;
                            GLES20.glBindFramebuffer(36160, r1.renderFrameBuffer[1]);
                            GLES20.glFramebufferTexture2D(36160, 36064, 3553, r1.renderTexture[1], 0);
                            GLES20.glClear(0);
                            GLES20.glUseProgram(r1.enhanceShaderProgram);
                            GLES20.glActiveTexture(33984);
                            GLES20.glBindTexture(3553, r1.enhanceTextures[0]);
                            GLES20.glUniform1i(r1.enhanceSourceImageHandle, 0);
                            GLES20.glActiveTexture(33985);
                            GLES20.glBindTexture(3553, r1.enhanceTextures[1]);
                            GLES20.glUniform1i(r1.enhanceInputImageTexture2Handle, 1);
                            if (PhotoFilterView.this.showOriginal) {
                                GLES20.glUniform1f(r1.enhanceIntensityHandle, PhotoFilterView.this.getEnhanceValue());
                            } else {
                                GLES20.glUniform1f(r1.enhanceIntensityHandle, 0.0f);
                            }
                            GLES20.glEnableVertexAttribArray(r1.enhanceInputTexCoordHandle);
                            GLES20.glVertexAttribPointer(r1.enhanceInputTexCoordHandle, 2, 5126, false, 8, r1.textureBuffer);
                            GLES20.glEnableVertexAttribArray(r1.enhancePositionHandle);
                            GLES20.glVertexAttribPointer(r1.enhancePositionHandle, 2, 5126, false, 8, r1.vertexBuffer);
                            GLES20.glDrawArrays(5, 0, 4);
                        }
                        buffer = allocateDirect;
                        GLES20.glBindTexture(3553, r1.enhanceTextures[1]);
                        GLES20.glTexParameteri(3553, 10241, 9729);
                        GLES20.glTexParameteri(3553, i3, 9729);
                        GLES20.glTexParameteri(3553, i2, i);
                        GLES20.glTexParameteri(3553, 10243, i);
                        GLES20.glTexImage2D(3553, 0, 6408, 256, 16, 0, 6408, 5121, buffer);
                        r1.hsvGenerated = true;
                    }
                    GLES20.glBindFramebuffer(36160, r1.renderFrameBuffer[1]);
                    GLES20.glFramebufferTexture2D(36160, 36064, 3553, r1.renderTexture[1], 0);
                    GLES20.glClear(0);
                    GLES20.glUseProgram(r1.enhanceShaderProgram);
                    GLES20.glActiveTexture(33984);
                    GLES20.glBindTexture(3553, r1.enhanceTextures[0]);
                    GLES20.glUniform1i(r1.enhanceSourceImageHandle, 0);
                    GLES20.glActiveTexture(33985);
                    GLES20.glBindTexture(3553, r1.enhanceTextures[1]);
                    GLES20.glUniform1i(r1.enhanceInputImageTexture2Handle, 1);
                    if (PhotoFilterView.this.showOriginal) {
                        GLES20.glUniform1f(r1.enhanceIntensityHandle, 0.0f);
                    } else {
                        GLES20.glUniform1f(r1.enhanceIntensityHandle, PhotoFilterView.this.getEnhanceValue());
                    }
                    GLES20.glEnableVertexAttribArray(r1.enhanceInputTexCoordHandle);
                    GLES20.glVertexAttribPointer(r1.enhanceInputTexCoordHandle, 2, 5126, false, 8, r1.textureBuffer);
                    GLES20.glEnableVertexAttribArray(r1.enhancePositionHandle);
                    GLES20.glVertexAttribPointer(r1.enhancePositionHandle, 2, 5126, false, 8, r1.vertexBuffer);
                    GLES20.glDrawArrays(5, 0, 4);
                }

                private void drawSharpenPass() {
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

                private void drawCustomParamsPass() {
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
                            GLES20.glTexImage2D(3553, 0, 6408, Callback.DEFAULT_DRAG_ANIMATION_DURATION, 1, 0, 6408, 5121, PhotoFilterView.this.curvesToolValue.curveBuffer);
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

                private boolean drawBlurPass() {
                    if (!PhotoFilterView.this.showOriginal) {
                        if (PhotoFilterView.this.blurType != 0) {
                            if (r0.needUpdateBlurTexture) {
                                GLES20.glUseProgram(r0.blurShaderProgram);
                                GLES20.glUniform1i(r0.blurSourceImageHandle, 0);
                                GLES20.glEnableVertexAttribArray(r0.blurInputTexCoordHandle);
                                GLES20.glVertexAttribPointer(r0.blurInputTexCoordHandle, 2, 5126, false, 8, r0.textureBuffer);
                                GLES20.glEnableVertexAttribArray(r0.blurPositionHandle);
                                GLES20.glVertexAttribPointer(r0.blurPositionHandle, 2, 5126, false, 8, r0.vertexInvertBuffer);
                                GLES20.glBindFramebuffer(36160, r0.renderFrameBuffer[0]);
                                GLES20.glFramebufferTexture2D(36160, 36064, 3553, r0.renderTexture[0], 0);
                                GLES20.glClear(0);
                                GLES20.glActiveTexture(33984);
                                GLES20.glBindTexture(3553, r0.renderTexture[1]);
                                GLES20.glUniform1f(r0.blurWidthHandle, 0.0f);
                                GLES20.glUniform1f(r0.blurHeightHandle, 1.0f / ((float) r0.renderBufferHeight));
                                GLES20.glDrawArrays(5, 0, 4);
                                GLES20.glBindFramebuffer(36160, r0.renderFrameBuffer[2]);
                                GLES20.glFramebufferTexture2D(36160, 36064, 3553, r0.renderTexture[2], 0);
                                GLES20.glClear(0);
                                GLES20.glActiveTexture(33984);
                                GLES20.glBindTexture(3553, r0.renderTexture[0]);
                                GLES20.glUniform1f(r0.blurWidthHandle, 1.0f / ((float) r0.renderBufferWidth));
                                GLES20.glUniform1f(r0.blurHeightHandle, 0.0f);
                                GLES20.glDrawArrays(5, 0, 4);
                                r0.needUpdateBlurTexture = false;
                            }
                            GLES20.glBindFramebuffer(36160, r0.renderFrameBuffer[0]);
                            GLES20.glFramebufferTexture2D(36160, 36064, 3553, r0.renderTexture[0], 0);
                            GLES20.glClear(0);
                            if (PhotoFilterView.this.blurType == 1) {
                                GLES20.glUseProgram(r0.radialBlurShaderProgram);
                                GLES20.glUniform1i(r0.radialBlurSourceImageHandle, 0);
                                GLES20.glUniform1i(r0.radialBlurSourceImage2Handle, 1);
                                GLES20.glUniform1f(r0.radialBlurExcludeSizeHandle, PhotoFilterView.this.blurExcludeSize);
                                GLES20.glUniform1f(r0.radialBlurExcludeBlurSizeHandle, PhotoFilterView.this.blurExcludeBlurSize);
                                GLES20.glUniform2f(r0.radialBlurExcludePointHandle, PhotoFilterView.this.blurExcludePoint.f24x, PhotoFilterView.this.blurExcludePoint.f25y);
                                GLES20.glUniform1f(r0.radialBlurAspectRatioHandle, ((float) r0.renderBufferHeight) / ((float) r0.renderBufferWidth));
                                GLES20.glEnableVertexAttribArray(r0.radialBlurInputTexCoordHandle);
                                GLES20.glVertexAttribPointer(r0.radialBlurInputTexCoordHandle, 2, 5126, false, 8, r0.textureBuffer);
                                GLES20.glEnableVertexAttribArray(r0.radialBlurPositionHandle);
                                GLES20.glVertexAttribPointer(r0.radialBlurPositionHandle, 2, 5126, false, 8, r0.vertexInvertBuffer);
                            } else if (PhotoFilterView.this.blurType == 2) {
                                GLES20.glUseProgram(r0.linearBlurShaderProgram);
                                GLES20.glUniform1i(r0.linearBlurSourceImageHandle, 0);
                                GLES20.glUniform1i(r0.linearBlurSourceImage2Handle, 1);
                                GLES20.glUniform1f(r0.linearBlurExcludeSizeHandle, PhotoFilterView.this.blurExcludeSize);
                                GLES20.glUniform1f(r0.linearBlurExcludeBlurSizeHandle, PhotoFilterView.this.blurExcludeBlurSize);
                                GLES20.glUniform1f(r0.linearBlurAngleHandle, PhotoFilterView.this.blurAngle);
                                GLES20.glUniform2f(r0.linearBlurExcludePointHandle, PhotoFilterView.this.blurExcludePoint.f24x, PhotoFilterView.this.blurExcludePoint.f25y);
                                GLES20.glUniform1f(r0.linearBlurAspectRatioHandle, ((float) r0.renderBufferHeight) / ((float) r0.renderBufferWidth));
                                GLES20.glEnableVertexAttribArray(r0.linearBlurInputTexCoordHandle);
                                GLES20.glVertexAttribPointer(r0.linearBlurInputTexCoordHandle, 2, 5126, false, 8, r0.textureBuffer);
                                GLES20.glEnableVertexAttribArray(r0.linearBlurPositionHandle);
                                GLES20.glVertexAttribPointer(r0.linearBlurPositionHandle, 2, 5126, false, 8, r0.vertexInvertBuffer);
                            }
                            GLES20.glActiveTexture(33984);
                            GLES20.glBindTexture(3553, r0.renderTexture[1]);
                            GLES20.glActiveTexture(33985);
                            GLES20.glBindTexture(3553, r0.renderTexture[2]);
                            GLES20.glDrawArrays(5, 0, 4);
                            return true;
                        }
                    }
                    return false;
                }

                private Bitmap getRenderBufferBitmap() {
                    Buffer allocateDirect = ByteBuffer.allocateDirect((this.renderBufferWidth * this.renderBufferHeight) * 4);
                    GLES20.glReadPixels(0, 0, this.renderBufferWidth, this.renderBufferHeight, 6408, 5121, allocateDirect);
                    Bitmap createBitmap = Bitmap.createBitmap(this.renderBufferWidth, this.renderBufferHeight, Config.ARGB_8888);
                    createBitmap.copyPixelsFromBuffer(allocateDirect);
                    return createBitmap;
                }

                public Bitmap getTexture() {
                    if (!this.initied) {
                        return null;
                    }
                    final CountDownLatch countDownLatch = new CountDownLatch(1);
                    final Bitmap[] bitmapArr = new Bitmap[1];
                    try {
                        postRunnable(new Runnable() {
                            public void run() {
                                GLES20.glBindFramebuffer(36160, EGLThread.this.renderFrameBuffer[1]);
                                GLES20.glFramebufferTexture2D(36160, 36064, 3553, EGLThread.this.renderTexture[1 ^ EGLThread.this.blured], 0);
                                GLES20.glClear(0);
                                bitmapArr[0] = EGLThread.this.getRenderBufferBitmap();
                                countDownLatch.countDown();
                                GLES20.glBindFramebuffer(36160, 0);
                                GLES20.glClear(0);
                            }
                        });
                        countDownLatch.await();
                    } catch (Throwable e) {
                        FileLog.m3e(e);
                    }
                    return bitmapArr[0];
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
                    if (((float) this.renderBufferWidth) > photoSize || ((float) r0.renderBufferHeight) > photoSize || PhotoFilterView.this.orientation % 360 != 0) {
                        float f = 1.0f;
                        if (((float) r0.renderBufferWidth) > photoSize || ((float) r0.renderBufferHeight) > photoSize) {
                            f = photoSize / ((float) bitmap.getWidth());
                            float height = photoSize / ((float) bitmap.getHeight());
                            if (f < height) {
                                r0.renderBufferWidth = (int) photoSize;
                                r0.renderBufferHeight = (int) (((float) bitmap.getHeight()) * f);
                            } else {
                                r0.renderBufferHeight = (int) photoSize;
                                r0.renderBufferWidth = (int) (((float) bitmap.getWidth()) * height);
                                f = height;
                            }
                        }
                        if (PhotoFilterView.this.orientation % 360 == 90 || PhotoFilterView.this.orientation % 360 == 270) {
                            int i = r0.renderBufferWidth;
                            r0.renderBufferWidth = r0.renderBufferHeight;
                            r0.renderBufferHeight = i;
                        }
                        r0.currentBitmap = createBitmap(bitmap, r0.renderBufferWidth, r0.renderBufferHeight, f);
                    }
                    GLES20.glGenFramebuffers(3, r0.renderFrameBuffer, 0);
                    GLES20.glGenTextures(3, r0.renderTexture, 0);
                    GLES20.glBindTexture(3553, r0.renderTexture[0]);
                    GLES20.glTexParameteri(3553, 10241, 9729);
                    GLES20.glTexParameteri(3553, 10240, 9729);
                    GLES20.glTexParameteri(3553, 10242, 33071);
                    GLES20.glTexParameteri(3553, 10243, 33071);
                    GLES20.glTexImage2D(3553, 0, 6408, r0.renderBufferWidth, r0.renderBufferHeight, 0, 6408, 5121, null);
                    GLES20.glBindTexture(3553, r0.renderTexture[1]);
                    GLES20.glTexParameteri(3553, 10241, 9729);
                    GLES20.glTexParameteri(3553, 10240, 9729);
                    GLES20.glTexParameteri(3553, 10242, 33071);
                    GLES20.glTexParameteri(3553, 10243, 33071);
                    GLUtils.texImage2D(3553, 0, r0.currentBitmap, 0);
                    GLES20.glBindTexture(3553, r0.renderTexture[2]);
                    GLES20.glTexParameteri(3553, 10241, 9729);
                    GLES20.glTexParameteri(3553, 10240, 9729);
                    GLES20.glTexParameteri(3553, 10242, 33071);
                    GLES20.glTexParameteri(3553, 10243, 33071);
                    GLES20.glTexImage2D(3553, 0, 6408, r0.renderBufferWidth, r0.renderBufferHeight, 0, 6408, 5121, null);
                }

                public void shutdown() {
                    postRunnable(new C12503());
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

                public void requestRender(final boolean z, final boolean z2) {
                    postRunnable(new Runnable() {
                        public void run() {
                            if (!EGLThread.this.needUpdateBlurTexture) {
                                EGLThread.this.needUpdateBlurTexture = z;
                            }
                            long currentTimeMillis = System.currentTimeMillis();
                            if (z2 || Math.abs(EGLThread.this.lastRenderCallTime - currentTimeMillis) > 30) {
                                EGLThread.this.lastRenderCallTime = currentTimeMillis;
                                EGLThread.this.drawRunnable.run();
                            }
                        }
                    });
                }
            }

            public class ToolsAdapter extends SelectionAdapter {
                private Context mContext;

                /* renamed from: org.telegram.ui.Components.PhotoFilterView$ToolsAdapter$2 */
                class C12522 implements OnClickListener {
                    C12522() {
                    }

                    public void onClick(View view) {
                        PhotoEditRadioCell photoEditRadioCell = (PhotoEditRadioCell) view;
                        if (((Integer) photoEditRadioCell.getTag()).intValue() == PhotoFilterView.this.tintShadowsTool) {
                            PhotoFilterView.this.tintShadowsColor = photoEditRadioCell.getCurrentColor();
                        } else {
                            PhotoFilterView.this.tintHighlightsColor = photoEditRadioCell.getCurrentColor();
                        }
                        if (PhotoFilterView.this.eglThread != null) {
                            PhotoFilterView.this.eglThread.requestRender(false);
                        }
                    }
                }

                /* renamed from: org.telegram.ui.Components.PhotoFilterView$ToolsAdapter$1 */
                class C20701 implements PhotoEditorSeekBarDelegate {
                    C20701() {
                    }

                    public void onProgressChanged(int i, int i2) {
                        if (i == PhotoFilterView.this.enhanceTool) {
                            PhotoFilterView.this.enhanceValue = (float) i2;
                        } else if (i == PhotoFilterView.this.highlightsTool) {
                            PhotoFilterView.this.highlightsValue = (float) i2;
                        } else if (i == PhotoFilterView.this.contrastTool) {
                            PhotoFilterView.this.contrastValue = (float) i2;
                        } else if (i == PhotoFilterView.this.exposureTool) {
                            PhotoFilterView.this.exposureValue = (float) i2;
                        } else if (i == PhotoFilterView.this.warmthTool) {
                            PhotoFilterView.this.warmthValue = (float) i2;
                        } else if (i == PhotoFilterView.this.saturationTool) {
                            PhotoFilterView.this.saturationValue = (float) i2;
                        } else if (i == PhotoFilterView.this.vignetteTool) {
                            PhotoFilterView.this.vignetteValue = (float) i2;
                        } else if (i == PhotoFilterView.this.shadowsTool) {
                            PhotoFilterView.this.shadowsValue = (float) i2;
                        } else if (i == PhotoFilterView.this.grainTool) {
                            PhotoFilterView.this.grainValue = (float) i2;
                        } else if (i == PhotoFilterView.this.sharpenTool) {
                            PhotoFilterView.this.sharpenValue = (float) i2;
                        } else if (i == PhotoFilterView.this.fadeTool) {
                            PhotoFilterView.this.fadeValue = (float) i2;
                        }
                        if (PhotoFilterView.this.eglThread != 0) {
                            PhotoFilterView.this.eglThread.requestRender(1);
                        }
                    }
                }

                public int getItemCount() {
                    return 13;
                }

                public long getItemId(int i) {
                    return (long) i;
                }

                public boolean isEnabled(ViewHolder viewHolder) {
                    return false;
                }

                public ToolsAdapter(Context context) {
                    this.mContext = context;
                }

                public ViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
                    if (i == 0) {
                        viewGroup = new PhotoEditToolCell(this.mContext);
                        viewGroup.setSeekBarDelegate(new C20701());
                    } else {
                        viewGroup = new PhotoEditRadioCell(this.mContext);
                        viewGroup.setOnClickListener(new C12522());
                    }
                    return new Holder(viewGroup);
                }

                public void onBindViewHolder(ViewHolder viewHolder, int i) {
                    switch (viewHolder.getItemViewType()) {
                        case 0:
                            PhotoEditToolCell photoEditToolCell = (PhotoEditToolCell) viewHolder.itemView;
                            photoEditToolCell.setTag(Integer.valueOf(i));
                            if (i == PhotoFilterView.this.enhanceTool) {
                                photoEditToolCell.setIconAndTextAndValue(LocaleController.getString("Enhance", C0446R.string.Enhance), PhotoFilterView.this.enhanceValue, 0, PhotoFilterView.curveGranularity);
                                return;
                            } else if (i == PhotoFilterView.this.highlightsTool) {
                                photoEditToolCell.setIconAndTextAndValue(LocaleController.getString("Highlights", C0446R.string.Highlights), PhotoFilterView.this.highlightsValue, -100, PhotoFilterView.curveGranularity);
                                return;
                            } else if (i == PhotoFilterView.this.contrastTool) {
                                photoEditToolCell.setIconAndTextAndValue(LocaleController.getString("Contrast", C0446R.string.Contrast), PhotoFilterView.this.contrastValue, -100, PhotoFilterView.curveGranularity);
                                return;
                            } else if (i == PhotoFilterView.this.exposureTool) {
                                photoEditToolCell.setIconAndTextAndValue(LocaleController.getString("Exposure", C0446R.string.Exposure), PhotoFilterView.this.exposureValue, -100, PhotoFilterView.curveGranularity);
                                return;
                            } else if (i == PhotoFilterView.this.warmthTool) {
                                photoEditToolCell.setIconAndTextAndValue(LocaleController.getString("Warmth", C0446R.string.Warmth), PhotoFilterView.this.warmthValue, -100, PhotoFilterView.curveGranularity);
                                return;
                            } else if (i == PhotoFilterView.this.saturationTool) {
                                photoEditToolCell.setIconAndTextAndValue(LocaleController.getString("Saturation", C0446R.string.Saturation), PhotoFilterView.this.saturationValue, -100, PhotoFilterView.curveGranularity);
                                return;
                            } else if (i == PhotoFilterView.this.vignetteTool) {
                                photoEditToolCell.setIconAndTextAndValue(LocaleController.getString("Vignette", C0446R.string.Vignette), PhotoFilterView.this.vignetteValue, 0, PhotoFilterView.curveGranularity);
                                return;
                            } else if (i == PhotoFilterView.this.shadowsTool) {
                                photoEditToolCell.setIconAndTextAndValue(LocaleController.getString("Shadows", C0446R.string.Shadows), PhotoFilterView.this.shadowsValue, -100, PhotoFilterView.curveGranularity);
                                return;
                            } else if (i == PhotoFilterView.this.grainTool) {
                                photoEditToolCell.setIconAndTextAndValue(LocaleController.getString("Grain", C0446R.string.Grain), PhotoFilterView.this.grainValue, 0, PhotoFilterView.curveGranularity);
                                return;
                            } else if (i == PhotoFilterView.this.sharpenTool) {
                                photoEditToolCell.setIconAndTextAndValue(LocaleController.getString("Sharpen", C0446R.string.Sharpen), PhotoFilterView.this.sharpenValue, 0, PhotoFilterView.curveGranularity);
                                return;
                            } else if (i == PhotoFilterView.this.fadeTool) {
                                photoEditToolCell.setIconAndTextAndValue(LocaleController.getString("Fade", C0446R.string.Fade), PhotoFilterView.this.fadeValue, 0, PhotoFilterView.curveGranularity);
                                return;
                            } else {
                                return;
                            }
                        case 1:
                            PhotoEditRadioCell photoEditRadioCell = (PhotoEditRadioCell) viewHolder.itemView;
                            photoEditRadioCell.setTag(Integer.valueOf(i));
                            if (i == PhotoFilterView.this.tintShadowsTool) {
                                photoEditRadioCell.setIconAndTextAndValue(LocaleController.getString("TintShadows", C0446R.string.TintShadows), 0, PhotoFilterView.this.tintShadowsColor);
                                return;
                            } else if (i == PhotoFilterView.this.tintHighlightsTool) {
                                photoEditRadioCell.setIconAndTextAndValue(LocaleController.getString("TintHighlights", C0446R.string.TintHighlights), 0, PhotoFilterView.this.tintHighlightsColor);
                                return;
                            } else {
                                return;
                            }
                        default:
                            return;
                    }
                }

                public int getItemViewType(int i) {
                    if (i != PhotoFilterView.this.tintShadowsTool) {
                        if (i != PhotoFilterView.this.tintHighlightsTool) {
                            return 0;
                        }
                    }
                    return 1;
                }
            }

            public PhotoFilterView(Context context, Bitmap bitmap, int i, SavedFilterState savedFilterState) {
                Context context2 = context;
                SavedFilterState savedFilterState2 = savedFilterState;
                super(context);
                if (savedFilterState2 != null) {
                    r0.enhanceValue = savedFilterState2.enhanceValue;
                    r0.exposureValue = savedFilterState2.exposureValue;
                    r0.contrastValue = savedFilterState2.contrastValue;
                    r0.warmthValue = savedFilterState2.warmthValue;
                    r0.saturationValue = savedFilterState2.saturationValue;
                    r0.fadeValue = savedFilterState2.fadeValue;
                    r0.tintShadowsColor = savedFilterState2.tintShadowsColor;
                    r0.tintHighlightsColor = savedFilterState2.tintHighlightsColor;
                    r0.highlightsValue = savedFilterState2.highlightsValue;
                    r0.shadowsValue = savedFilterState2.shadowsValue;
                    r0.vignetteValue = savedFilterState2.vignetteValue;
                    r0.grainValue = savedFilterState2.grainValue;
                    r0.blurType = savedFilterState2.blurType;
                    r0.sharpenValue = savedFilterState2.sharpenValue;
                    r0.curvesToolValue = savedFilterState2.curvesToolValue;
                    r0.blurExcludeSize = savedFilterState2.blurExcludeSize;
                    r0.blurExcludePoint = savedFilterState2.blurExcludePoint;
                    r0.blurExcludeBlurSize = savedFilterState2.blurExcludeBlurSize;
                    r0.blurAngle = savedFilterState2.blurAngle;
                    r0.lastState = savedFilterState2;
                } else {
                    r0.curvesToolValue = new CurvesToolValue();
                    r0.blurExcludeSize = 0.35f;
                    r0.blurExcludePoint = new Point(0.5f, 0.5f);
                    r0.blurExcludeBlurSize = 0.15f;
                    r0.blurAngle = 1.5707964f;
                }
                r0.bitmapToEdit = bitmap;
                r0.orientation = i;
                r0.textureView = new TextureView(context2);
                addView(r0.textureView, LayoutHelper.createFrame(-1, -1, 51));
                r0.textureView.setVisibility(4);
                r0.textureView.setSurfaceTextureListener(new C12411());
                r0.blurControl = new PhotoFilterBlurControl(context2);
                r0.blurControl.setVisibility(4);
                addView(r0.blurControl, LayoutHelper.createFrame(-1, -1, 51));
                r0.blurControl.setDelegate(new C20682());
                r0.curvesControl = new PhotoFilterCurvesControl(context2, r0.curvesToolValue);
                r0.curvesControl.setDelegate(new C20693());
                r0.curvesControl.setVisibility(4);
                addView(r0.curvesControl, LayoutHelper.createFrame(-1, -1, 51));
                r0.toolsView = new FrameLayout(context2);
                addView(r0.toolsView, LayoutHelper.createFrame(-1, 186, 83));
                View frameLayout = new FrameLayout(context2);
                frameLayout.setBackgroundColor(Theme.ACTION_BAR_VIDEO_EDIT_COLOR);
                r0.toolsView.addView(frameLayout, LayoutHelper.createFrame(-1, 48, 83));
                r0.cancelTextView = new TextView(context2);
                r0.cancelTextView.setTextSize(1, 14.0f);
                r0.cancelTextView.setTextColor(-1);
                r0.cancelTextView.setGravity(17);
                r0.cancelTextView.setBackgroundDrawable(Theme.createSelectorDrawable(Theme.ACTION_BAR_PICKER_SELECTOR_COLOR, 0));
                r0.cancelTextView.setPadding(AndroidUtilities.dp(20.0f), 0, AndroidUtilities.dp(20.0f), 0);
                r0.cancelTextView.setText(LocaleController.getString("Cancel", C0446R.string.Cancel).toUpperCase());
                r0.cancelTextView.setTypeface(AndroidUtilities.getTypeface("fonts/rmedium.ttf"));
                frameLayout.addView(r0.cancelTextView, LayoutHelper.createFrame(-2, -1, 51));
                r0.doneTextView = new TextView(context2);
                r0.doneTextView.setTextSize(1, 14.0f);
                r0.doneTextView.setTextColor(-11420173);
                r0.doneTextView.setGravity(17);
                r0.doneTextView.setBackgroundDrawable(Theme.createSelectorDrawable(Theme.ACTION_BAR_PICKER_SELECTOR_COLOR, 0));
                r0.doneTextView.setPadding(AndroidUtilities.dp(20.0f), 0, AndroidUtilities.dp(20.0f), 0);
                r0.doneTextView.setText(LocaleController.getString("Done", C0446R.string.Done).toUpperCase());
                r0.doneTextView.setTypeface(AndroidUtilities.getTypeface("fonts/rmedium.ttf"));
                frameLayout.addView(r0.doneTextView, LayoutHelper.createFrame(-2, -1, 53));
                View linearLayout = new LinearLayout(context2);
                frameLayout.addView(linearLayout, LayoutHelper.createFrame(-2, -1, 1));
                r0.tuneItem = new ImageView(context2);
                r0.tuneItem.setScaleType(ScaleType.CENTER);
                r0.tuneItem.setImageResource(C0446R.drawable.photo_tools);
                r0.tuneItem.setColorFilter(new PorterDuffColorFilter(-9649153, Mode.MULTIPLY));
                r0.tuneItem.setBackgroundDrawable(Theme.createSelectorDrawable(Theme.ACTION_BAR_WHITE_SELECTOR_COLOR));
                linearLayout.addView(r0.tuneItem, LayoutHelper.createLinear(56, 48));
                r0.tuneItem.setOnClickListener(new C12424());
                r0.blurItem = new ImageView(context2);
                r0.blurItem.setScaleType(ScaleType.CENTER);
                r0.blurItem.setImageResource(C0446R.drawable.tool_blur);
                r0.blurItem.setBackgroundDrawable(Theme.createSelectorDrawable(Theme.ACTION_BAR_WHITE_SELECTOR_COLOR));
                linearLayout.addView(r0.blurItem, LayoutHelper.createLinear(56, 48));
                r0.blurItem.setOnClickListener(new C12435());
                r0.curveItem = new ImageView(context2);
                r0.curveItem.setScaleType(ScaleType.CENTER);
                r0.curveItem.setImageResource(C0446R.drawable.tool_curve);
                r0.curveItem.setBackgroundDrawable(Theme.createSelectorDrawable(Theme.ACTION_BAR_WHITE_SELECTOR_COLOR));
                linearLayout.addView(r0.curveItem, LayoutHelper.createLinear(56, 48));
                r0.curveItem.setOnClickListener(new C12446());
                r0.recyclerListView = new RecyclerListView(context2);
                LayoutManager linearLayoutManager = new LinearLayoutManager(context2);
                linearLayoutManager.setOrientation(1);
                r0.recyclerListView.setLayoutManager(linearLayoutManager);
                r0.recyclerListView.setClipToPadding(false);
                r0.recyclerListView.setOverScrollMode(2);
                r0.recyclerListView.setAdapter(new ToolsAdapter(context2));
                r0.toolsView.addView(r0.recyclerListView, LayoutHelper.createFrame(-1, 120, 51));
                r0.curveLayout = new FrameLayout(context2);
                r0.curveLayout.setVisibility(4);
                r0.toolsView.addView(r0.curveLayout, LayoutHelper.createFrame(-1, 78.0f, 1, 0.0f, 40.0f, 0.0f, 0.0f));
                frameLayout = new LinearLayout(context2);
                frameLayout.setOrientation(0);
                r0.curveLayout.addView(frameLayout, LayoutHelper.createFrame(-2, -2, 1));
                int i2 = 0;
                while (i2 < 4) {
                    linearLayout = new FrameLayout(context2);
                    linearLayout.setTag(Integer.valueOf(i2));
                    r0.curveRadioButton[i2] = new RadioButton(context2);
                    r0.curveRadioButton[i2].setSize(AndroidUtilities.dp(20.0f));
                    linearLayout.addView(r0.curveRadioButton[i2], LayoutHelper.createFrame(30, 30, 49));
                    View textView = new TextView(context2);
                    textView.setTextSize(1, 12.0f);
                    textView.setGravity(16);
                    String string;
                    StringBuilder stringBuilder;
                    if (i2 == 0) {
                        string = LocaleController.getString("CurvesAll", C0446R.string.CurvesAll);
                        stringBuilder = new StringBuilder();
                        stringBuilder.append(string.substring(0, 1).toUpperCase());
                        stringBuilder.append(string.substring(1).toLowerCase());
                        textView.setText(stringBuilder.toString());
                        textView.setTextColor(-1);
                        r0.curveRadioButton[i2].setColor(-1, -1);
                    } else if (i2 == 1) {
                        string = LocaleController.getString("CurvesRed", C0446R.string.CurvesRed);
                        stringBuilder = new StringBuilder();
                        stringBuilder.append(string.substring(0, 1).toUpperCase());
                        stringBuilder.append(string.substring(1).toLowerCase());
                        textView.setText(stringBuilder.toString());
                        textView.setTextColor(-1684147);
                        r0.curveRadioButton[i2].setColor(-1684147, -1684147);
                    } else if (i2 == 2) {
                        string = LocaleController.getString("CurvesGreen", C0446R.string.CurvesGreen);
                        stringBuilder = new StringBuilder();
                        stringBuilder.append(string.substring(0, 1).toUpperCase());
                        stringBuilder.append(string.substring(1).toLowerCase());
                        textView.setText(stringBuilder.toString());
                        textView.setTextColor(-10831009);
                        r0.curveRadioButton[i2].setColor(-10831009, -10831009);
                    } else if (i2 == 3) {
                        string = LocaleController.getString("CurvesBlue", C0446R.string.CurvesBlue);
                        stringBuilder = new StringBuilder();
                        stringBuilder.append(string.substring(0, 1).toUpperCase());
                        stringBuilder.append(string.substring(1).toLowerCase());
                        textView.setText(stringBuilder.toString());
                        textView.setTextColor(-12734994);
                        r0.curveRadioButton[i2].setColor(-12734994, -12734994);
                    }
                    linearLayout.addView(textView, LayoutHelper.createFrame(-2, -2.0f, 49, 0.0f, 38.0f, 0.0f, 0.0f));
                    frameLayout.addView(linearLayout, LayoutHelper.createLinear(-2, -2, i2 == 0 ? 0.0f : 30.0f, 0.0f, 0.0f, 0.0f));
                    linearLayout.setOnClickListener(new C12457());
                    i2++;
                }
                r0.blurLayout = new FrameLayout(context2);
                r0.blurLayout.setVisibility(4);
                r0.toolsView.addView(r0.blurLayout, LayoutHelper.createFrame(280, 60.0f, 1, 0.0f, 40.0f, 0.0f, 0.0f));
                r0.blurOffButton = new TextView(context2);
                r0.blurOffButton.setCompoundDrawablePadding(AndroidUtilities.dp(2.0f));
                r0.blurOffButton.setTextSize(1, 13.0f);
                r0.blurOffButton.setGravity(1);
                r0.blurOffButton.setText(LocaleController.getString("BlurOff", C0446R.string.BlurOff));
                r0.blurLayout.addView(r0.blurOffButton, LayoutHelper.createFrame(80, 60.0f));
                r0.blurOffButton.setOnClickListener(new C12468());
                r0.blurRadialButton = new TextView(context2);
                r0.blurRadialButton.setCompoundDrawablePadding(AndroidUtilities.dp(2.0f));
                r0.blurRadialButton.setTextSize(1, 13.0f);
                r0.blurRadialButton.setGravity(1);
                r0.blurRadialButton.setText(LocaleController.getString("BlurRadial", C0446R.string.BlurRadial));
                r0.blurLayout.addView(r0.blurRadialButton, LayoutHelper.createFrame(80, 80.0f, 51, 100.0f, 0.0f, 0.0f, 0.0f));
                r0.blurRadialButton.setOnClickListener(new C12479());
                r0.blurLinearButton = new TextView(context2);
                r0.blurLinearButton.setCompoundDrawablePadding(AndroidUtilities.dp(2.0f));
                r0.blurLinearButton.setTextSize(1, 13.0f);
                r0.blurLinearButton.setGravity(1);
                r0.blurLinearButton.setText(LocaleController.getString("BlurLinear", C0446R.string.BlurLinear));
                r0.blurLayout.addView(r0.blurLinearButton, LayoutHelper.createFrame(80, 80.0f, 51, 200.0f, 0.0f, 0.0f, 0.0f));
                r0.blurLinearButton.setOnClickListener(new OnClickListener() {
                    public void onClick(View view) {
                        PhotoFilterView.this.blurType = 2;
                        PhotoFilterView.this.updateSelectedBlurType();
                        PhotoFilterView.this.blurControl.setVisibility(0);
                        PhotoFilterView.this.blurControl.setType(0);
                        if (PhotoFilterView.this.eglThread != null) {
                            PhotoFilterView.this.eglThread.requestRender(false);
                        }
                    }
                });
                updateSelectedBlurType();
                if (VERSION.SDK_INT >= 21) {
                    ((LayoutParams) r0.textureView.getLayoutParams()).topMargin = AndroidUtilities.statusBarHeight;
                    ((LayoutParams) r0.curvesControl.getLayoutParams()).topMargin = AndroidUtilities.statusBarHeight;
                }
            }

            private void updateSelectedBlurType() {
                Drawable mutate;
                if (this.blurType == 0) {
                    mutate = this.blurOffButton.getContext().getResources().getDrawable(C0446R.drawable.blur_off).mutate();
                    mutate.setColorFilter(new PorterDuffColorFilter(-11420173, Mode.MULTIPLY));
                    this.blurOffButton.setCompoundDrawablesWithIntrinsicBounds(null, mutate, null, null);
                    this.blurOffButton.setTextColor(-11420173);
                    this.blurRadialButton.setCompoundDrawablesWithIntrinsicBounds(0, C0446R.drawable.blur_radial, 0, 0);
                    this.blurRadialButton.setTextColor(-1);
                    this.blurLinearButton.setCompoundDrawablesWithIntrinsicBounds(0, C0446R.drawable.blur_linear, 0, 0);
                    this.blurLinearButton.setTextColor(-1);
                } else if (this.blurType == 1) {
                    this.blurOffButton.setCompoundDrawablesWithIntrinsicBounds(0, C0446R.drawable.blur_off, 0, 0);
                    this.blurOffButton.setTextColor(-1);
                    mutate = this.blurOffButton.getContext().getResources().getDrawable(C0446R.drawable.blur_radial).mutate();
                    mutate.setColorFilter(new PorterDuffColorFilter(-11420173, Mode.MULTIPLY));
                    this.blurRadialButton.setCompoundDrawablesWithIntrinsicBounds(null, mutate, null, null);
                    this.blurRadialButton.setTextColor(-11420173);
                    this.blurLinearButton.setCompoundDrawablesWithIntrinsicBounds(0, C0446R.drawable.blur_linear, 0, 0);
                    this.blurLinearButton.setTextColor(-1);
                } else if (this.blurType == 2) {
                    this.blurOffButton.setCompoundDrawablesWithIntrinsicBounds(0, C0446R.drawable.blur_off, 0, 0);
                    this.blurOffButton.setTextColor(-1);
                    this.blurRadialButton.setCompoundDrawablesWithIntrinsicBounds(0, C0446R.drawable.blur_radial, 0, 0);
                    this.blurRadialButton.setTextColor(-1);
                    mutate = this.blurOffButton.getContext().getResources().getDrawable(C0446R.drawable.blur_linear).mutate();
                    mutate.setColorFilter(new PorterDuffColorFilter(-11420173, Mode.MULTIPLY));
                    this.blurLinearButton.setCompoundDrawablesWithIntrinsicBounds(null, mutate, null, null);
                    this.blurLinearButton.setTextColor(-11420173);
                }
            }

            public SavedFilterState getSavedFilterState() {
                SavedFilterState savedFilterState = new SavedFilterState();
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
                boolean z = true;
                if (this.lastState != null) {
                    if (this.enhanceValue == this.lastState.enhanceValue && this.contrastValue == this.lastState.contrastValue && this.highlightsValue == this.lastState.highlightsValue && this.exposureValue == this.lastState.exposureValue && this.warmthValue == this.lastState.warmthValue && this.saturationValue == this.lastState.saturationValue && this.vignetteValue == this.lastState.vignetteValue && this.shadowsValue == this.lastState.shadowsValue && this.grainValue == this.lastState.grainValue && this.sharpenValue == this.lastState.sharpenValue && this.fadeValue == this.lastState.fadeValue && this.tintHighlightsColor == this.lastState.tintHighlightsColor && this.tintShadowsColor == this.lastState.tintShadowsColor) {
                        if (this.curvesToolValue.shouldBeSkipped()) {
                            z = false;
                        }
                    }
                    return z;
                }
                if (this.enhanceValue == 0.0f && this.contrastValue == 0.0f && this.highlightsValue == 0.0f && this.exposureValue == 0.0f && this.warmthValue == 0.0f && this.saturationValue == 0.0f && this.vignetteValue == 0.0f && this.shadowsValue == 0.0f && this.grainValue == 0.0f && this.sharpenValue == 0.0f && this.fadeValue == 0.0f && this.tintHighlightsColor == 0 && this.tintShadowsColor == 0) {
                    if (this.curvesToolValue.shouldBeSkipped()) {
                        z = false;
                    }
                }
                return z;
            }

            public void onTouch(MotionEvent motionEvent) {
                if (motionEvent.getActionMasked() != 0) {
                    if (motionEvent.getActionMasked() != 5) {
                        if (motionEvent.getActionMasked() == 1 || motionEvent.getActionMasked() == 6) {
                            setShowOriginal(null);
                            return;
                        }
                        return;
                    }
                }
                LayoutParams layoutParams = (LayoutParams) this.textureView.getLayoutParams();
                if (layoutParams != null && motionEvent.getX() >= ((float) layoutParams.leftMargin) && motionEvent.getY() >= ((float) layoutParams.topMargin) && motionEvent.getX() <= ((float) (layoutParams.leftMargin + layoutParams.width)) && motionEvent.getY() <= ((float) (layoutParams.topMargin + layoutParams.height))) {
                    setShowOriginal(true);
                }
            }

            private void setShowOriginal(boolean z) {
                if (this.showOriginal != z) {
                    this.showOriginal = z;
                    if (this.eglThread) {
                        this.eglThread.requestRender(false);
                    }
                }
            }

            public void switchMode() {
                if (this.selectedTool == 0) {
                    this.blurControl.setVisibility(4);
                    this.blurLayout.setVisibility(4);
                    this.curveLayout.setVisibility(4);
                    this.curvesControl.setVisibility(4);
                    this.recyclerListView.setVisibility(0);
                } else if (this.selectedTool == 1) {
                    this.recyclerListView.setVisibility(4);
                    this.curveLayout.setVisibility(4);
                    this.curvesControl.setVisibility(4);
                    this.blurLayout.setVisibility(0);
                    if (this.blurType != 0) {
                        this.blurControl.setVisibility(0);
                    }
                    updateSelectedBlurType();
                } else if (this.selectedTool == 2) {
                    this.recyclerListView.setVisibility(4);
                    this.blurLayout.setVisibility(4);
                    this.blurControl.setVisibility(4);
                    this.curveLayout.setVisibility(0);
                    this.curvesControl.setVisibility(0);
                    this.curvesToolValue.activeType = 0;
                    int i = 0;
                    while (i < 4) {
                        this.curveRadioButton[i].setChecked(i == 0, false);
                        i++;
                    }
                }
            }

            public void shutdown() {
                if (this.eglThread != null) {
                    this.eglThread.shutdown();
                    this.eglThread = null;
                }
                this.textureView.setVisibility(8);
            }

            public void init() {
                this.textureView.setVisibility(0);
            }

            public Bitmap getBitmap() {
                return this.eglThread != null ? this.eglThread.getTexture() : null;
            }

            private void fixLayout(int i, int i2) {
                if (this.bitmapToEdit != null) {
                    float width;
                    float height;
                    float f;
                    float f2;
                    float f3;
                    float f4;
                    int ceil;
                    int ceil2;
                    LayoutParams layoutParams;
                    LayoutParams layoutParams2;
                    i -= AndroidUtilities.dp(28.0f);
                    i2 -= AndroidUtilities.dp(214.0f) + (VERSION.SDK_INT >= 21 ? AndroidUtilities.statusBarHeight : 0);
                    if (this.orientation % 360 != 90) {
                        if (this.orientation % 360 != 270) {
                            width = (float) this.bitmapToEdit.getWidth();
                            height = (float) this.bitmapToEdit.getHeight();
                            f = (float) i;
                            f2 = f / width;
                            f3 = (float) i2;
                            f4 = f3 / height;
                            if (f2 <= f4) {
                                width = (float) ((int) Math.ceil((double) (width * f4)));
                                height = f3;
                            } else {
                                height = (float) ((int) Math.ceil((double) (height * f2)));
                                width = f;
                            }
                            ceil = (int) Math.ceil((double) (((f - width) / 2.0f) + ((float) AndroidUtilities.dp(14.0f))));
                            ceil2 = (int) Math.ceil((double) ((((f3 - height) / 2.0f) + ((float) AndroidUtilities.dp(14.0f))) + ((float) (VERSION.SDK_INT < 21 ? AndroidUtilities.statusBarHeight : 0))));
                            layoutParams = (LayoutParams) this.textureView.getLayoutParams();
                            layoutParams.leftMargin = ceil;
                            layoutParams.topMargin = ceil2;
                            layoutParams.width = (int) width;
                            layoutParams.height = (int) height;
                            this.curvesControl.setActualArea((float) ceil, (float) (ceil2 - (VERSION.SDK_INT < 21 ? AndroidUtilities.statusBarHeight : 0)), (float) layoutParams.width, (float) layoutParams.height);
                            this.blurControl.setActualAreaSize((float) layoutParams.width, (float) layoutParams.height);
                            ((LayoutParams) this.blurControl.getLayoutParams()).height = AndroidUtilities.dp(38.0f) + i2;
                            ((LayoutParams) this.curvesControl.getLayoutParams()).height = i2 + AndroidUtilities.dp(28.0f);
                            if (AndroidUtilities.isTablet() != 0) {
                                i2 = AndroidUtilities.dp(NUM) * 10;
                                layoutParams2 = (LayoutParams) this.recyclerListView.getLayoutParams();
                                if (i2 >= i) {
                                    layoutParams2.width = i2;
                                    layoutParams2.leftMargin = (i - i2) / 2;
                                } else {
                                    layoutParams2.width = -1;
                                    layoutParams2.leftMargin = 0;
                                }
                            }
                        }
                    }
                    width = (float) this.bitmapToEdit.getHeight();
                    height = (float) this.bitmapToEdit.getWidth();
                    f = (float) i;
                    f2 = f / width;
                    f3 = (float) i2;
                    f4 = f3 / height;
                    if (f2 <= f4) {
                        height = (float) ((int) Math.ceil((double) (height * f2)));
                        width = f;
                    } else {
                        width = (float) ((int) Math.ceil((double) (width * f4)));
                        height = f3;
                    }
                    ceil = (int) Math.ceil((double) (((f - width) / 2.0f) + ((float) AndroidUtilities.dp(14.0f))));
                    if (VERSION.SDK_INT < 21) {
                    }
                    ceil2 = (int) Math.ceil((double) ((((f3 - height) / 2.0f) + ((float) AndroidUtilities.dp(14.0f))) + ((float) (VERSION.SDK_INT < 21 ? AndroidUtilities.statusBarHeight : 0))));
                    layoutParams = (LayoutParams) this.textureView.getLayoutParams();
                    layoutParams.leftMargin = ceil;
                    layoutParams.topMargin = ceil2;
                    layoutParams.width = (int) width;
                    layoutParams.height = (int) height;
                    if (VERSION.SDK_INT < 21) {
                    }
                    this.curvesControl.setActualArea((float) ceil, (float) (ceil2 - (VERSION.SDK_INT < 21 ? AndroidUtilities.statusBarHeight : 0)), (float) layoutParams.width, (float) layoutParams.height);
                    this.blurControl.setActualAreaSize((float) layoutParams.width, (float) layoutParams.height);
                    ((LayoutParams) this.blurControl.getLayoutParams()).height = AndroidUtilities.dp(38.0f) + i2;
                    ((LayoutParams) this.curvesControl.getLayoutParams()).height = i2 + AndroidUtilities.dp(28.0f);
                    if (AndroidUtilities.isTablet() != 0) {
                        i2 = AndroidUtilities.dp(NUM) * 10;
                        layoutParams2 = (LayoutParams) this.recyclerListView.getLayoutParams();
                        if (i2 >= i) {
                            layoutParams2.width = -1;
                            layoutParams2.leftMargin = 0;
                        } else {
                            layoutParams2.width = i2;
                            layoutParams2.leftMargin = (i - i2) / 2;
                        }
                    }
                }
            }

            protected void onMeasure(int i, int i2) {
                fixLayout(MeasureSpec.getSize(i), MeasureSpec.getSize(i2));
                super.onMeasure(i, i2);
            }

            private float getShadowsValue() {
                return ((this.shadowsValue * 0.55f) + 100.0f) / 100.0f;
            }

            private float getHighlightsValue() {
                return ((this.highlightsValue * 0.75f) + 100.0f) / 100.0f;
            }

            private float getEnhanceValue() {
                return this.enhanceValue / 100.0f;
            }

            private float getExposureValue() {
                return this.exposureValue / 100.0f;
            }

            private float getContrastValue() {
                return ((this.contrastValue / 100.0f) * 0.3f) + 1.0f;
            }

            private float getWarmthValue() {
                return this.warmthValue / 100.0f;
            }

            private float getVignetteValue() {
                return this.vignetteValue / 100.0f;
            }

            private float getSharpenValue() {
                return 0.11f + ((this.sharpenValue / 100.0f) * 0.6f);
            }

            private float getGrainValue() {
                return (this.grainValue / 100.0f) * 0.04f;
            }

            private float getFadeValue() {
                return this.fadeValue / 100.0f;
            }

            private float getTintHighlightsIntensityValue() {
                return this.tintHighlightsColor == 0 ? 0.0f : 0.5f;
            }

            private float getTintShadowsIntensityValue() {
                return this.tintShadowsColor == 0 ? 0.0f : 0.5f;
            }

            private float getSaturationValue() {
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
        }
