package org.telegram.ui.Components;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.ColorFilter;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffColorFilter;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.view.MotionEvent;
import android.view.TextureView;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.recyclerview.widget.RecyclerView;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.util.ArrayList;
import org.telegram.messenger.AndroidUtilities;
import org.telegram.messenger.LocaleController;
import org.telegram.messenger.MediaController;
import org.telegram.ui.ActionBar.Theme;
import org.telegram.ui.Cells.PhotoEditRadioCell;
import org.telegram.ui.Cells.PhotoEditToolCell;
import org.telegram.ui.Components.FilterShaders;
import org.telegram.ui.Components.RecyclerListView;
import org.telegram.ui.Components.VideoEditTextureView;

@SuppressLint({"NewApi"})
public class PhotoFilterView extends FrameLayout implements FilterShaders.FilterShadersDelegate {
    /* access modifiers changed from: private */
    public Bitmap bitmapToEdit;
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
    /* access modifiers changed from: private */
    public int contrastTool;
    /* access modifiers changed from: private */
    public float contrastValue;
    private ImageView curveItem;
    private FrameLayout curveLayout;
    private RadioButton[] curveRadioButton = new RadioButton[4];
    private PhotoFilterCurvesControl curvesControl;
    private CurvesToolValue curvesToolValue;
    private TextView doneTextView;
    /* access modifiers changed from: private */
    public FilterGLThread eglThread;
    /* access modifiers changed from: private */
    public int enhanceTool;
    /* access modifiers changed from: private */
    public float enhanceValue;
    /* access modifiers changed from: private */
    public int exposureTool;
    /* access modifiers changed from: private */
    public float exposureValue;
    /* access modifiers changed from: private */
    public int fadeTool;
    /* access modifiers changed from: private */
    public float fadeValue;
    /* access modifiers changed from: private */
    public int grainTool;
    /* access modifiers changed from: private */
    public float grainValue;
    /* access modifiers changed from: private */
    public int highlightsTool;
    /* access modifiers changed from: private */
    public float highlightsValue;
    private boolean inBubbleMode;
    /* access modifiers changed from: private */
    public boolean isMirrored;
    private MediaController.SavedFilterState lastState;
    /* access modifiers changed from: private */
    public int orientation;
    private boolean ownsTextureView;
    private PaintingOverlay paintingOverlay;
    private RecyclerListView recyclerListView;
    /* access modifiers changed from: private */
    public int rowsCount;
    /* access modifiers changed from: private */
    public int saturationTool;
    /* access modifiers changed from: private */
    public float saturationValue;
    private int selectedTool;
    /* access modifiers changed from: private */
    public int shadowsTool;
    /* access modifiers changed from: private */
    public float shadowsValue;
    /* access modifiers changed from: private */
    public int sharpenTool;
    /* access modifiers changed from: private */
    public float sharpenValue;
    private boolean showOriginal;
    /* access modifiers changed from: private */
    public int softenSkinTool;
    /* access modifiers changed from: private */
    public float softenSkinValue;
    private TextureView textureView;
    /* access modifiers changed from: private */
    public int tintHighlightsColor;
    /* access modifiers changed from: private */
    public int tintHighlightsTool;
    /* access modifiers changed from: private */
    public int tintShadowsColor;
    /* access modifiers changed from: private */
    public int tintShadowsTool;
    private FrameLayout toolsView;
    private ImageView tuneItem;
    /* access modifiers changed from: private */
    public int vignetteTool;
    /* access modifiers changed from: private */
    public float vignetteValue;
    /* access modifiers changed from: private */
    public int warmthTool;
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
            float f2 = 0.0f;
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
                    float var_ = ((f6 * 2.0f) + ((f8 - f4) * var_) + (((((f4 * 2.0f) - (f6 * 5.0f)) + (f8 * 4.0f)) - var_) * var_) + (((((f6 * 3.0f) - f4) - (f8 * 3.0f)) + var_) * var_)) * 0.5f;
                    float max = Math.max(f2, Math.min(1.0f, ((f7 * 2.0f) + ((f9 - f5) * var_) + (((((2.0f * f5) - (5.0f * f7)) + (4.0f * f9)) - var_) * var_) + (((((f7 * 3.0f) - f5) - (3.0f * f9)) + var_) * var_)) * 0.5f));
                    if (var_ > f4) {
                        arrayList2.add(Float.valueOf(var_));
                        arrayList2.add(Float.valueOf(max));
                    }
                    if ((i8 - 1) % 2 == 0) {
                        arrayList.add(Float.valueOf(max));
                    }
                    i8++;
                    f2 = 0.0f;
                }
                arrayList2.add(Float.valueOf(f8));
                arrayList2.add(Float.valueOf(f9));
                i2 = i5;
                i = 1;
                f2 = 0.0f;
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

    /* JADX INFO: super call moved to the top of the method (can break code semantics) */
    /* JADX WARNING: Removed duplicated region for block: B:39:0x04d1  */
    /* JADX WARNING: Removed duplicated region for block: B:40:0x04d3  */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public PhotoFilterView(android.content.Context r24, org.telegram.ui.Components.VideoEditTextureView r25, android.graphics.Bitmap r26, int r27, org.telegram.messenger.MediaController.SavedFilterState r28, org.telegram.ui.Components.PaintingOverlay r29, int r30, boolean r31) {
        /*
            r23 = this;
            r0 = r23
            r1 = r24
            r2 = r25
            r3 = r28
            r4 = r30
            r23.<init>(r24)
            r5 = 4
            org.telegram.ui.Components.RadioButton[] r6 = new org.telegram.ui.Components.RadioButton[r5]
            r0.curveRadioButton = r6
            boolean r6 = r1 instanceof org.telegram.ui.BubbleActivity
            r0.inBubbleMode = r6
            r6 = r29
            r0.paintingOverlay = r6
            r6 = r31
            r0.isMirrored = r6
            r6 = 0
            r0.rowsCount = r6
            r7 = -1
            r8 = 1
            if (r4 != r8) goto L_0x002c
            r9 = 0
            int r9 = r9 + r8
            r0.rowsCount = r9
            r0.softenSkinTool = r6
            goto L_0x0030
        L_0x002c:
            if (r4 != 0) goto L_0x0030
            r0.softenSkinTool = r7
        L_0x0030:
            int r9 = r0.rowsCount
            int r10 = r9 + 1
            r0.rowsCount = r10
            r0.enhanceTool = r9
            int r9 = r10 + 1
            r0.rowsCount = r9
            r0.exposureTool = r10
            int r10 = r9 + 1
            r0.rowsCount = r10
            r0.contrastTool = r9
            int r9 = r10 + 1
            r0.rowsCount = r9
            r0.saturationTool = r10
            int r10 = r9 + 1
            r0.rowsCount = r10
            r0.warmthTool = r9
            int r9 = r10 + 1
            r0.rowsCount = r9
            r0.fadeTool = r10
            int r10 = r9 + 1
            r0.rowsCount = r10
            r0.highlightsTool = r9
            int r9 = r10 + 1
            r0.rowsCount = r9
            r0.shadowsTool = r10
            int r10 = r9 + 1
            r0.rowsCount = r10
            r0.vignetteTool = r9
            r9 = 2
            if (r4 != r9) goto L_0x0071
            int r4 = r10 + 1
            r0.rowsCount = r4
            r0.softenSkinTool = r10
        L_0x0071:
            if (r2 != 0) goto L_0x007c
            int r4 = r0.rowsCount
            int r10 = r4 + 1
            r0.rowsCount = r10
            r0.grainTool = r4
            goto L_0x007e
        L_0x007c:
            r0.grainTool = r7
        L_0x007e:
            int r4 = r0.rowsCount
            int r10 = r4 + 1
            r0.rowsCount = r10
            r0.sharpenTool = r4
            int r4 = r10 + 1
            r0.rowsCount = r4
            r0.tintShadowsTool = r10
            int r10 = r4 + 1
            r0.rowsCount = r10
            r0.tintHighlightsTool = r4
            if (r3 == 0) goto L_0x00e7
            float r4 = r3.enhanceValue
            r0.enhanceValue = r4
            float r4 = r3.softenSkinValue
            r0.softenSkinValue = r4
            float r4 = r3.exposureValue
            r0.exposureValue = r4
            float r4 = r3.contrastValue
            r0.contrastValue = r4
            float r4 = r3.warmthValue
            r0.warmthValue = r4
            float r4 = r3.saturationValue
            r0.saturationValue = r4
            float r4 = r3.fadeValue
            r0.fadeValue = r4
            int r4 = r3.tintShadowsColor
            r0.tintShadowsColor = r4
            int r4 = r3.tintHighlightsColor
            r0.tintHighlightsColor = r4
            float r4 = r3.highlightsValue
            r0.highlightsValue = r4
            float r4 = r3.shadowsValue
            r0.shadowsValue = r4
            float r4 = r3.vignetteValue
            r0.vignetteValue = r4
            float r4 = r3.grainValue
            r0.grainValue = r4
            int r4 = r3.blurType
            r0.blurType = r4
            float r4 = r3.sharpenValue
            r0.sharpenValue = r4
            org.telegram.ui.Components.PhotoFilterView$CurvesToolValue r4 = r3.curvesToolValue
            r0.curvesToolValue = r4
            float r4 = r3.blurExcludeSize
            r0.blurExcludeSize = r4
            org.telegram.ui.Components.Point r4 = r3.blurExcludePoint
            r0.blurExcludePoint = r4
            float r4 = r3.blurExcludeBlurSize
            r0.blurExcludeBlurSize = r4
            float r4 = r3.blurAngle
            r0.blurAngle = r4
            r0.lastState = r3
            goto L_0x0106
        L_0x00e7:
            org.telegram.ui.Components.PhotoFilterView$CurvesToolValue r3 = new org.telegram.ui.Components.PhotoFilterView$CurvesToolValue
            r3.<init>()
            r0.curvesToolValue = r3
            r3 = 1051931443(0x3eb33333, float:0.35)
            r0.blurExcludeSize = r3
            org.telegram.ui.Components.Point r3 = new org.telegram.ui.Components.Point
            r4 = 1056964608(0x3var_, float:0.5)
            r3.<init>(r4, r4)
            r0.blurExcludePoint = r3
            r3 = 1041865114(0x3e19999a, float:0.15)
            r0.blurExcludeBlurSize = r3
            r3 = 1070141403(0x3fCLASSNAMEfdb, float:1.5707964)
            r0.blurAngle = r3
        L_0x0106:
            r3 = r26
            r0.bitmapToEdit = r3
            r3 = r27
            r0.orientation = r3
            r3 = 51
            if (r2 == 0) goto L_0x011d
            r0.textureView = r2
            org.telegram.ui.Components.-$$Lambda$PhotoFilterView$q8bSXenPFKmAiLet4nYY0FG6QUI r4 = new org.telegram.ui.Components.-$$Lambda$PhotoFilterView$q8bSXenPFKmAiLet4nYY0FG6QUI
            r4.<init>()
            r2.setDelegate(r4)
            goto L_0x013c
        L_0x011d:
            r0.ownsTextureView = r8
            android.view.TextureView r4 = new android.view.TextureView
            r4.<init>(r1)
            r0.textureView = r4
            android.widget.FrameLayout$LayoutParams r10 = org.telegram.ui.Components.LayoutHelper.createFrame(r7, r7, r3)
            r0.addView(r4, r10)
            android.view.TextureView r4 = r0.textureView
            r4.setVisibility(r5)
            android.view.TextureView r4 = r0.textureView
            org.telegram.ui.Components.PhotoFilterView$1 r10 = new org.telegram.ui.Components.PhotoFilterView$1
            r10.<init>()
            r4.setSurfaceTextureListener(r10)
        L_0x013c:
            org.telegram.ui.Components.PhotoFilterBlurControl r4 = new org.telegram.ui.Components.PhotoFilterBlurControl
            r4.<init>(r1)
            r0.blurControl = r4
            r4.setVisibility(r5)
            org.telegram.ui.Components.PhotoFilterBlurControl r4 = r0.blurControl
            android.widget.FrameLayout$LayoutParams r10 = org.telegram.ui.Components.LayoutHelper.createFrame(r7, r7, r3)
            r0.addView(r4, r10)
            org.telegram.ui.Components.PhotoFilterBlurControl r4 = r0.blurControl
            org.telegram.ui.Components.-$$Lambda$PhotoFilterView$GCGCchbOZkYFd7FKnH3pWJ5bURo r10 = new org.telegram.ui.Components.-$$Lambda$PhotoFilterView$GCGCchbOZkYFd7FKnH3pWJ5bURo
            r10.<init>()
            r4.setDelegate(r10)
            org.telegram.ui.Components.PhotoFilterCurvesControl r4 = new org.telegram.ui.Components.PhotoFilterCurvesControl
            org.telegram.ui.Components.PhotoFilterView$CurvesToolValue r10 = r0.curvesToolValue
            r4.<init>(r1, r10)
            r0.curvesControl = r4
            org.telegram.ui.Components.-$$Lambda$PhotoFilterView$KvuUyjS3o6sxpmVQlMBz4wjgbsE r10 = new org.telegram.ui.Components.-$$Lambda$PhotoFilterView$KvuUyjS3o6sxpmVQlMBz4wjgbsE
            r10.<init>()
            r4.setDelegate(r10)
            org.telegram.ui.Components.PhotoFilterCurvesControl r4 = r0.curvesControl
            r4.setVisibility(r5)
            org.telegram.ui.Components.PhotoFilterCurvesControl r4 = r0.curvesControl
            android.widget.FrameLayout$LayoutParams r10 = org.telegram.ui.Components.LayoutHelper.createFrame(r7, r7, r3)
            r0.addView(r4, r10)
            android.widget.FrameLayout r4 = new android.widget.FrameLayout
            r4.<init>(r1)
            r0.toolsView = r4
            r10 = 186(0xba, float:2.6E-43)
            r11 = 83
            android.widget.FrameLayout$LayoutParams r10 = org.telegram.ui.Components.LayoutHelper.createFrame(r7, r10, r11)
            r0.addView(r4, r10)
            android.widget.FrameLayout r4 = new android.widget.FrameLayout
            r4.<init>(r1)
            r10 = -16777216(0xfffffffffvar_, float:-1.7014118E38)
            r4.setBackgroundColor(r10)
            android.widget.FrameLayout r10 = r0.toolsView
            r12 = 48
            android.widget.FrameLayout$LayoutParams r11 = org.telegram.ui.Components.LayoutHelper.createFrame(r7, r12, r11)
            r10.addView(r4, r11)
            android.widget.TextView r10 = new android.widget.TextView
            r10.<init>(r1)
            r0.cancelTextView = r10
            r11 = 1096810496(0x41600000, float:14.0)
            r10.setTextSize(r8, r11)
            android.widget.TextView r10 = r0.cancelTextView
            r10.setTextColor(r7)
            android.widget.TextView r10 = r0.cancelTextView
            r13 = 17
            r10.setGravity(r13)
            android.widget.TextView r10 = r0.cancelTextView
            r14 = -12763843(0xffffffffff3d3d3d, float:-2.5154206E38)
            android.graphics.drawable.Drawable r15 = org.telegram.ui.ActionBar.Theme.createSelectorDrawable(r14, r6)
            r10.setBackgroundDrawable(r15)
            android.widget.TextView r10 = r0.cancelTextView
            r15 = 1101004800(0x41a00000, float:20.0)
            int r5 = org.telegram.messenger.AndroidUtilities.dp(r15)
            int r9 = org.telegram.messenger.AndroidUtilities.dp(r15)
            r10.setPadding(r5, r6, r9, r6)
            android.widget.TextView r5 = r0.cancelTextView
            r9 = 2131624575(0x7f0e027f, float:1.8876334E38)
            java.lang.String r10 = "Cancel"
            java.lang.String r9 = org.telegram.messenger.LocaleController.getString(r10, r9)
            java.lang.String r9 = r9.toUpperCase()
            r5.setText(r9)
            android.widget.TextView r5 = r0.cancelTextView
            java.lang.String r9 = "fonts/rmedium.ttf"
            android.graphics.Typeface r10 = org.telegram.messenger.AndroidUtilities.getTypeface(r9)
            r5.setTypeface(r10)
            android.widget.TextView r5 = r0.cancelTextView
            r10 = -2
            android.widget.FrameLayout$LayoutParams r12 = org.telegram.ui.Components.LayoutHelper.createFrame(r10, r7, r3)
            r4.addView(r5, r12)
            android.widget.TextView r5 = new android.widget.TextView
            r5.<init>(r1)
            r0.doneTextView = r5
            r5.setTextSize(r8, r11)
            android.widget.TextView r5 = r0.doneTextView
            java.lang.String r11 = "dialogFloatingButton"
            int r11 = org.telegram.ui.ActionBar.Theme.getColor(r11)
            r5.setTextColor(r11)
            android.widget.TextView r5 = r0.doneTextView
            r5.setGravity(r13)
            android.widget.TextView r5 = r0.doneTextView
            android.graphics.drawable.Drawable r11 = org.telegram.ui.ActionBar.Theme.createSelectorDrawable(r14, r6)
            r5.setBackgroundDrawable(r11)
            android.widget.TextView r5 = r0.doneTextView
            int r11 = org.telegram.messenger.AndroidUtilities.dp(r15)
            int r12 = org.telegram.messenger.AndroidUtilities.dp(r15)
            r5.setPadding(r11, r6, r12, r6)
            android.widget.TextView r5 = r0.doneTextView
            r11 = 2131625115(0x7f0e049b, float:1.8877429E38)
            java.lang.String r12 = "Done"
            java.lang.String r11 = org.telegram.messenger.LocaleController.getString(r12, r11)
            java.lang.String r11 = r11.toUpperCase()
            r5.setText(r11)
            android.widget.TextView r5 = r0.doneTextView
            android.graphics.Typeface r9 = org.telegram.messenger.AndroidUtilities.getTypeface(r9)
            r5.setTypeface(r9)
            android.widget.TextView r5 = r0.doneTextView
            r9 = 53
            android.widget.FrameLayout$LayoutParams r9 = org.telegram.ui.Components.LayoutHelper.createFrame(r10, r7, r9)
            r4.addView(r5, r9)
            android.widget.LinearLayout r5 = new android.widget.LinearLayout
            r5.<init>(r1)
            android.widget.FrameLayout$LayoutParams r9 = org.telegram.ui.Components.LayoutHelper.createFrame(r10, r7, r8)
            r4.addView(r5, r9)
            android.widget.ImageView r4 = new android.widget.ImageView
            r4.<init>(r1)
            r0.tuneItem = r4
            android.widget.ImageView$ScaleType r9 = android.widget.ImageView.ScaleType.CENTER
            r4.setScaleType(r9)
            android.widget.ImageView r4 = r0.tuneItem
            r9 = 2131165868(0x7var_ac, float:1.7945965E38)
            r4.setImageResource(r9)
            android.widget.ImageView r4 = r0.tuneItem
            android.graphics.PorterDuffColorFilter r9 = new android.graphics.PorterDuffColorFilter
            java.lang.String r11 = "dialogFloatingButton"
            int r11 = org.telegram.ui.ActionBar.Theme.getColor(r11)
            android.graphics.PorterDuff$Mode r12 = android.graphics.PorterDuff.Mode.MULTIPLY
            r9.<init>(r11, r12)
            r4.setColorFilter(r9)
            android.widget.ImageView r4 = r0.tuneItem
            r9 = 1090519039(0x40ffffff, float:7.9999995)
            android.graphics.drawable.Drawable r11 = org.telegram.ui.ActionBar.Theme.createSelectorDrawable(r9)
            r4.setBackgroundDrawable(r11)
            android.widget.ImageView r4 = r0.tuneItem
            r11 = 56
            r12 = 48
            android.widget.LinearLayout$LayoutParams r13 = org.telegram.ui.Components.LayoutHelper.createLinear(r11, r12)
            r5.addView(r4, r13)
            android.widget.ImageView r4 = r0.tuneItem
            org.telegram.ui.Components.-$$Lambda$PhotoFilterView$AAjp9rbnsUw8tN4hNxe43Bd_GGk r12 = new org.telegram.ui.Components.-$$Lambda$PhotoFilterView$AAjp9rbnsUw8tN4hNxe43Bd_GGk
            r12.<init>()
            r4.setOnClickListener(r12)
            android.widget.ImageView r4 = new android.widget.ImageView
            r4.<init>(r1)
            r0.blurItem = r4
            android.widget.ImageView$ScaleType r12 = android.widget.ImageView.ScaleType.CENTER
            r4.setScaleType(r12)
            android.widget.ImageView r4 = r0.blurItem
            r12 = 2131166037(0x7var_, float:1.7946308E38)
            r4.setImageResource(r12)
            android.widget.ImageView r4 = r0.blurItem
            android.graphics.drawable.Drawable r12 = org.telegram.ui.ActionBar.Theme.createSelectorDrawable(r9)
            r4.setBackgroundDrawable(r12)
            android.widget.ImageView r4 = r0.blurItem
            r12 = 48
            android.widget.LinearLayout$LayoutParams r13 = org.telegram.ui.Components.LayoutHelper.createLinear(r11, r12)
            r5.addView(r4, r13)
            android.widget.ImageView r4 = r0.blurItem
            org.telegram.ui.Components.-$$Lambda$PhotoFilterView$MBKBy8Yjpk6c7p81zuBtyfNt1Ew r12 = new org.telegram.ui.Components.-$$Lambda$PhotoFilterView$MBKBy8Yjpk6c7p81zuBtyfNt1Ew
            r12.<init>()
            r4.setOnClickListener(r12)
            if (r2 == 0) goto L_0x02df
            android.widget.ImageView r2 = r0.blurItem
            r4 = 8
            r2.setVisibility(r4)
        L_0x02df:
            android.widget.ImageView r2 = new android.widget.ImageView
            r2.<init>(r1)
            r0.curveItem = r2
            android.widget.ImageView$ScaleType r4 = android.widget.ImageView.ScaleType.CENTER
            r2.setScaleType(r4)
            android.widget.ImageView r2 = r0.curveItem
            r4 = 2131166039(0x7var_, float:1.7946312E38)
            r2.setImageResource(r4)
            android.widget.ImageView r2 = r0.curveItem
            android.graphics.drawable.Drawable r4 = org.telegram.ui.ActionBar.Theme.createSelectorDrawable(r9)
            r2.setBackgroundDrawable(r4)
            android.widget.ImageView r2 = r0.curveItem
            r4 = 48
            android.widget.LinearLayout$LayoutParams r4 = org.telegram.ui.Components.LayoutHelper.createLinear(r11, r4)
            r5.addView(r2, r4)
            android.widget.ImageView r2 = r0.curveItem
            org.telegram.ui.Components.-$$Lambda$PhotoFilterView$v62bzBhXcMHOnVhW5Ioiq6WiHJw r4 = new org.telegram.ui.Components.-$$Lambda$PhotoFilterView$v62bzBhXcMHOnVhW5Ioiq6WiHJw
            r4.<init>()
            r2.setOnClickListener(r4)
            org.telegram.ui.Components.RecyclerListView r2 = new org.telegram.ui.Components.RecyclerListView
            r2.<init>(r1)
            r0.recyclerListView = r2
            androidx.recyclerview.widget.LinearLayoutManager r2 = new androidx.recyclerview.widget.LinearLayoutManager
            r2.<init>(r1)
            r2.setOrientation(r8)
            org.telegram.ui.Components.RecyclerListView r4 = r0.recyclerListView
            r4.setLayoutManager(r2)
            org.telegram.ui.Components.RecyclerListView r2 = r0.recyclerListView
            r2.setClipToPadding(r6)
            org.telegram.ui.Components.RecyclerListView r2 = r0.recyclerListView
            r4 = 2
            r2.setOverScrollMode(r4)
            org.telegram.ui.Components.RecyclerListView r2 = r0.recyclerListView
            org.telegram.ui.Components.PhotoFilterView$ToolsAdapter r4 = new org.telegram.ui.Components.PhotoFilterView$ToolsAdapter
            r4.<init>(r1)
            r2.setAdapter(r4)
            android.widget.FrameLayout r2 = r0.toolsView
            org.telegram.ui.Components.RecyclerListView r4 = r0.recyclerListView
            r5 = 120(0x78, float:1.68E-43)
            android.widget.FrameLayout$LayoutParams r3 = org.telegram.ui.Components.LayoutHelper.createFrame(r7, r5, r3)
            r2.addView(r4, r3)
            android.widget.FrameLayout r2 = new android.widget.FrameLayout
            r2.<init>(r1)
            r0.curveLayout = r2
            r3 = 4
            r2.setVisibility(r3)
            android.widget.FrameLayout r2 = r0.toolsView
            android.widget.FrameLayout r3 = r0.curveLayout
            r16 = -1
            r17 = 1117519872(0x429CLASSNAME, float:78.0)
            r18 = 1
            r19 = 0
            r20 = 1109393408(0x42200000, float:40.0)
            r21 = 0
            r22 = 0
            android.widget.FrameLayout$LayoutParams r4 = org.telegram.ui.Components.LayoutHelper.createFrame(r16, r17, r18, r19, r20, r21, r22)
            r2.addView(r3, r4)
            android.widget.LinearLayout r2 = new android.widget.LinearLayout
            r2.<init>(r1)
            r2.setOrientation(r6)
            android.widget.FrameLayout r3 = r0.curveLayout
            android.widget.FrameLayout$LayoutParams r4 = org.telegram.ui.Components.LayoutHelper.createFrame(r10, r10, r8)
            r3.addView(r2, r4)
            r3 = 0
        L_0x037d:
            r4 = 4
            if (r3 >= r4) goto L_0x04f7
            android.widget.FrameLayout r4 = new android.widget.FrameLayout
            r4.<init>(r1)
            java.lang.Integer r5 = java.lang.Integer.valueOf(r3)
            r4.setTag(r5)
            org.telegram.ui.Components.RadioButton[] r5 = r0.curveRadioButton
            org.telegram.ui.Components.RadioButton r9 = new org.telegram.ui.Components.RadioButton
            r9.<init>(r1)
            r5[r3] = r9
            org.telegram.ui.Components.RadioButton[] r5 = r0.curveRadioButton
            r5 = r5[r3]
            int r9 = org.telegram.messenger.AndroidUtilities.dp(r15)
            r5.setSize(r9)
            org.telegram.ui.Components.RadioButton[] r5 = r0.curveRadioButton
            r5 = r5[r3]
            r9 = 30
            r10 = 30
            r11 = 49
            android.widget.FrameLayout$LayoutParams r9 = org.telegram.ui.Components.LayoutHelper.createFrame(r9, r10, r11)
            r4.addView(r5, r9)
            android.widget.TextView r5 = new android.widget.TextView
            r5.<init>(r1)
            r9 = 1094713344(0x41400000, float:12.0)
            r5.setTextSize(r8, r9)
            r9 = 16
            r5.setGravity(r9)
            if (r3 != 0) goto L_0x03fa
            r9 = 2131624954(0x7f0e03fa, float:1.8877102E38)
            java.lang.String r10 = "CurvesAll"
            java.lang.String r9 = org.telegram.messenger.LocaleController.getString(r10, r9)
            java.lang.StringBuilder r10 = new java.lang.StringBuilder
            r10.<init>()
            java.lang.String r11 = r9.substring(r6, r8)
            java.lang.String r11 = r11.toUpperCase()
            r10.append(r11)
            java.lang.String r9 = r9.substring(r8)
            java.lang.String r9 = r9.toLowerCase()
            r10.append(r9)
            java.lang.String r9 = r10.toString()
            r5.setText(r9)
            r5.setTextColor(r7)
            org.telegram.ui.Components.RadioButton[] r9 = r0.curveRadioButton
            r9 = r9[r3]
            r9.setColor(r7, r7)
        L_0x03f7:
            r9 = 2
            goto L_0x04ac
        L_0x03fa:
            if (r3 != r8) goto L_0x0435
            r9 = 2131624957(0x7f0e03fd, float:1.8877108E38)
            java.lang.String r10 = "CurvesRed"
            java.lang.String r9 = org.telegram.messenger.LocaleController.getString(r10, r9)
            java.lang.StringBuilder r10 = new java.lang.StringBuilder
            r10.<init>()
            java.lang.String r11 = r9.substring(r6, r8)
            java.lang.String r11 = r11.toUpperCase()
            r10.append(r11)
            java.lang.String r9 = r9.substring(r8)
            java.lang.String r9 = r9.toLowerCase()
            r10.append(r9)
            java.lang.String r9 = r10.toString()
            r5.setText(r9)
            r9 = -1684147(0xffffffffffe64d4d, float:NaN)
            r5.setTextColor(r9)
            org.telegram.ui.Components.RadioButton[] r10 = r0.curveRadioButton
            r10 = r10[r3]
            r10.setColor(r9, r9)
            goto L_0x03f7
        L_0x0435:
            r9 = 2
            if (r3 != r9) goto L_0x0471
            r10 = 2131624956(0x7f0e03fc, float:1.8877106E38)
            java.lang.String r11 = "CurvesGreen"
            java.lang.String r10 = org.telegram.messenger.LocaleController.getString(r11, r10)
            java.lang.StringBuilder r11 = new java.lang.StringBuilder
            r11.<init>()
            java.lang.String r12 = r10.substring(r6, r8)
            java.lang.String r12 = r12.toUpperCase()
            r11.append(r12)
            java.lang.String r10 = r10.substring(r8)
            java.lang.String r10 = r10.toLowerCase()
            r11.append(r10)
            java.lang.String r10 = r11.toString()
            r5.setText(r10)
            r10 = -10831009(0xffffffffff5abb5f, float:-2.9074459E38)
            r5.setTextColor(r10)
            org.telegram.ui.Components.RadioButton[] r11 = r0.curveRadioButton
            r11 = r11[r3]
            r11.setColor(r10, r10)
            goto L_0x04ac
        L_0x0471:
            r10 = 3
            if (r3 != r10) goto L_0x04ac
            r10 = 2131624955(0x7f0e03fb, float:1.8877104E38)
            java.lang.String r11 = "CurvesBlue"
            java.lang.String r10 = org.telegram.messenger.LocaleController.getString(r11, r10)
            java.lang.StringBuilder r11 = new java.lang.StringBuilder
            r11.<init>()
            java.lang.String r12 = r10.substring(r6, r8)
            java.lang.String r12 = r12.toUpperCase()
            r11.append(r12)
            java.lang.String r10 = r10.substring(r8)
            java.lang.String r10 = r10.toLowerCase()
            r11.append(r10)
            java.lang.String r10 = r11.toString()
            r5.setText(r10)
            r10 = -12734994(0xffffffffff3dadee, float:-2.5212719E38)
            r5.setTextColor(r10)
            org.telegram.ui.Components.RadioButton[] r11 = r0.curveRadioButton
            r11 = r11[r3]
            r11.setColor(r10, r10)
        L_0x04ac:
            r10 = -2
            r11 = -1073741824(0xffffffffCLASSNAME, float:-2.0)
            r12 = 49
            r13 = 0
            r14 = 1108869120(0x42180000, float:38.0)
            r16 = 0
            r17 = 0
            r25 = r10
            r26 = r11
            r27 = r12
            r28 = r13
            r29 = r14
            r30 = r16
            r31 = r17
            android.widget.FrameLayout$LayoutParams r10 = org.telegram.ui.Components.LayoutHelper.createFrame(r25, r26, r27, r28, r29, r30, r31)
            r4.addView(r5, r10)
            r5 = -2
            r10 = -2
            if (r3 != 0) goto L_0x04d3
            r11 = 0
            goto L_0x04d5
        L_0x04d3:
            r11 = 1106247680(0x41var_, float:30.0)
        L_0x04d5:
            r12 = 0
            r13 = 0
            r14 = 0
            r25 = r5
            r26 = r10
            r27 = r11
            r28 = r12
            r29 = r13
            r30 = r14
            android.widget.LinearLayout$LayoutParams r5 = org.telegram.ui.Components.LayoutHelper.createLinear(r25, r26, r27, r28, r29, r30)
            r2.addView(r4, r5)
            org.telegram.ui.Components.-$$Lambda$PhotoFilterView$L1-qn-kj-rboPLqJmUeYGRY74A4 r5 = new org.telegram.ui.Components.-$$Lambda$PhotoFilterView$L1-qn-kj-rboPLqJmUeYGRY74A4
            r5.<init>()
            r4.setOnClickListener(r5)
            int r3 = r3 + 1
            goto L_0x037d
        L_0x04f7:
            android.widget.FrameLayout r2 = new android.widget.FrameLayout
            r2.<init>(r1)
            r0.blurLayout = r2
            r3 = 4
            r2.setVisibility(r3)
            android.widget.FrameLayout r2 = r0.toolsView
            android.widget.FrameLayout r3 = r0.blurLayout
            r4 = 280(0x118, float:3.92E-43)
            r5 = 1114636288(0x42700000, float:60.0)
            r6 = 1
            r7 = 0
            r9 = 1109393408(0x42200000, float:40.0)
            r10 = 0
            r11 = 0
            r25 = r4
            r26 = r5
            r27 = r6
            r28 = r7
            r29 = r9
            r30 = r10
            r31 = r11
            android.widget.FrameLayout$LayoutParams r4 = org.telegram.ui.Components.LayoutHelper.createFrame(r25, r26, r27, r28, r29, r30, r31)
            r2.addView(r3, r4)
            android.widget.TextView r2 = new android.widget.TextView
            r2.<init>(r1)
            r0.blurOffButton = r2
            r3 = 1073741824(0x40000000, float:2.0)
            int r4 = org.telegram.messenger.AndroidUtilities.dp(r3)
            r2.setCompoundDrawablePadding(r4)
            android.widget.TextView r2 = r0.blurOffButton
            r4 = 1095761920(0x41500000, float:13.0)
            r2.setTextSize(r8, r4)
            android.widget.TextView r2 = r0.blurOffButton
            r2.setGravity(r8)
            android.widget.TextView r2 = r0.blurOffButton
            r5 = 2131624515(0x7f0e0243, float:1.8876212E38)
            java.lang.String r6 = "BlurOff"
            java.lang.String r5 = org.telegram.messenger.LocaleController.getString(r6, r5)
            r2.setText(r5)
            android.widget.FrameLayout r2 = r0.blurLayout
            android.widget.TextView r5 = r0.blurOffButton
            r6 = 80
            r7 = 1114636288(0x42700000, float:60.0)
            android.widget.FrameLayout$LayoutParams r6 = org.telegram.ui.Components.LayoutHelper.createFrame(r6, r7)
            r2.addView(r5, r6)
            android.widget.TextView r2 = r0.blurOffButton
            org.telegram.ui.Components.-$$Lambda$PhotoFilterView$E33gxsLr1bFb2cfvLoCrPut1UWo r5 = new org.telegram.ui.Components.-$$Lambda$PhotoFilterView$E33gxsLr1bFb2cfvLoCrPut1UWo
            r5.<init>()
            r2.setOnClickListener(r5)
            android.widget.TextView r2 = new android.widget.TextView
            r2.<init>(r1)
            r0.blurRadialButton = r2
            int r5 = org.telegram.messenger.AndroidUtilities.dp(r3)
            r2.setCompoundDrawablePadding(r5)
            android.widget.TextView r2 = r0.blurRadialButton
            r2.setTextSize(r8, r4)
            android.widget.TextView r2 = r0.blurRadialButton
            r2.setGravity(r8)
            android.widget.TextView r2 = r0.blurRadialButton
            r5 = 2131624516(0x7f0e0244, float:1.8876214E38)
            java.lang.String r6 = "BlurRadial"
            java.lang.String r5 = org.telegram.messenger.LocaleController.getString(r6, r5)
            r2.setText(r5)
            android.widget.FrameLayout r2 = r0.blurLayout
            android.widget.TextView r5 = r0.blurRadialButton
            r6 = 80
            r7 = 1117782016(0x42a00000, float:80.0)
            r9 = 51
            r10 = 1120403456(0x42CLASSNAME, float:100.0)
            r12 = 0
            r13 = 0
            r25 = r6
            r26 = r7
            r27 = r9
            r28 = r10
            r29 = r11
            r30 = r12
            r31 = r13
            android.widget.FrameLayout$LayoutParams r6 = org.telegram.ui.Components.LayoutHelper.createFrame(r25, r26, r27, r28, r29, r30, r31)
            r2.addView(r5, r6)
            android.widget.TextView r2 = r0.blurRadialButton
            org.telegram.ui.Components.-$$Lambda$PhotoFilterView$zNRLY59SFYMJR9OCVQC_Z36k4Hw r5 = new org.telegram.ui.Components.-$$Lambda$PhotoFilterView$zNRLY59SFYMJR9OCVQC_Z36k4Hw
            r5.<init>()
            r2.setOnClickListener(r5)
            android.widget.TextView r2 = new android.widget.TextView
            r2.<init>(r1)
            r0.blurLinearButton = r2
            int r1 = org.telegram.messenger.AndroidUtilities.dp(r3)
            r2.setCompoundDrawablePadding(r1)
            android.widget.TextView r1 = r0.blurLinearButton
            r1.setTextSize(r8, r4)
            android.widget.TextView r1 = r0.blurLinearButton
            r1.setGravity(r8)
            android.widget.TextView r1 = r0.blurLinearButton
            r2 = 2131624514(0x7f0e0242, float:1.887621E38)
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
            r24 = r3
            r25 = r4
            r26 = r5
            r27 = r6
            r28 = r7
            r29 = r8
            r30 = r9
            android.widget.FrameLayout$LayoutParams r3 = org.telegram.ui.Components.LayoutHelper.createFrame(r24, r25, r26, r27, r28, r29, r30)
            r1.addView(r2, r3)
            android.widget.TextView r1 = r0.blurLinearButton
            org.telegram.ui.Components.-$$Lambda$PhotoFilterView$zH0YZYnMNX3qKUvaCWEop484aQo r2 = new org.telegram.ui.Components.-$$Lambda$PhotoFilterView$zH0YZYnMNX3qKUvaCWEop484aQo
            r2.<init>()
            r1.setOnClickListener(r2)
            r23.updateSelectedBlurType()
            int r1 = android.os.Build.VERSION.SDK_INT
            r2 = 21
            if (r1 < r2) goto L_0x0638
            boolean r1 = r0.inBubbleMode
            if (r1 != 0) goto L_0x0638
            boolean r1 = r0.ownsTextureView
            if (r1 == 0) goto L_0x062c
            android.view.TextureView r1 = r0.textureView
            android.view.ViewGroup$LayoutParams r1 = r1.getLayoutParams()
            android.widget.FrameLayout$LayoutParams r1 = (android.widget.FrameLayout.LayoutParams) r1
            int r2 = org.telegram.messenger.AndroidUtilities.statusBarHeight
            r1.topMargin = r2
        L_0x062c:
            org.telegram.ui.Components.PhotoFilterCurvesControl r1 = r0.curvesControl
            android.view.ViewGroup$LayoutParams r1 = r1.getLayoutParams()
            android.widget.FrameLayout$LayoutParams r1 = (android.widget.FrameLayout.LayoutParams) r1
            int r2 = org.telegram.messenger.AndroidUtilities.statusBarHeight
            r1.topMargin = r2
        L_0x0638:
            return
        */
        throw new UnsupportedOperationException("Method not decompiled: org.telegram.ui.Components.PhotoFilterView.<init>(android.content.Context, org.telegram.ui.Components.VideoEditTextureView, android.graphics.Bitmap, int, org.telegram.messenger.MediaController$SavedFilterState, org.telegram.ui.Components.PaintingOverlay, int, boolean):void");
    }

    /* access modifiers changed from: private */
    /* renamed from: lambda$new$0 */
    public /* synthetic */ void lambda$new$0$PhotoFilterView(FilterGLThread filterGLThread) {
        this.eglThread = filterGLThread;
        filterGLThread.setFilterGLThreadDelegate(this);
    }

    /* access modifiers changed from: private */
    /* renamed from: lambda$new$1 */
    public /* synthetic */ void lambda$new$1$PhotoFilterView(Point point, float f, float f2, float f3) {
        this.blurExcludeSize = f2;
        this.blurExcludePoint = point;
        this.blurExcludeBlurSize = f;
        this.blurAngle = f3;
        FilterGLThread filterGLThread = this.eglThread;
        if (filterGLThread != null) {
            filterGLThread.requestRender(false);
        }
    }

    /* access modifiers changed from: private */
    /* renamed from: lambda$new$2 */
    public /* synthetic */ void lambda$new$2$PhotoFilterView() {
        FilterGLThread filterGLThread = this.eglThread;
        if (filterGLThread != null) {
            filterGLThread.requestRender(false);
        }
    }

    /* access modifiers changed from: private */
    /* renamed from: lambda$new$3 */
    public /* synthetic */ void lambda$new$3$PhotoFilterView(View view) {
        this.selectedTool = 0;
        this.tuneItem.setColorFilter(new PorterDuffColorFilter(Theme.getColor("dialogFloatingButton"), PorterDuff.Mode.MULTIPLY));
        this.blurItem.setColorFilter((ColorFilter) null);
        this.curveItem.setColorFilter((ColorFilter) null);
        switchMode();
    }

    /* access modifiers changed from: private */
    /* renamed from: lambda$new$4 */
    public /* synthetic */ void lambda$new$4$PhotoFilterView(View view) {
        this.selectedTool = 1;
        this.tuneItem.setColorFilter((ColorFilter) null);
        this.blurItem.setColorFilter(new PorterDuffColorFilter(Theme.getColor("dialogFloatingButton"), PorterDuff.Mode.MULTIPLY));
        this.curveItem.setColorFilter((ColorFilter) null);
        switchMode();
    }

    /* access modifiers changed from: private */
    /* renamed from: lambda$new$5 */
    public /* synthetic */ void lambda$new$5$PhotoFilterView(View view) {
        this.selectedTool = 2;
        this.tuneItem.setColorFilter((ColorFilter) null);
        this.blurItem.setColorFilter((ColorFilter) null);
        this.curveItem.setColorFilter(new PorterDuffColorFilter(Theme.getColor("dialogFloatingButton"), PorterDuff.Mode.MULTIPLY));
        switchMode();
    }

    /* access modifiers changed from: private */
    /* renamed from: lambda$new$6 */
    public /* synthetic */ void lambda$new$6$PhotoFilterView(View view) {
        int intValue = ((Integer) view.getTag()).intValue();
        this.curvesToolValue.activeType = intValue;
        int i = 0;
        while (i < 4) {
            this.curveRadioButton[i].setChecked(i == intValue, true);
            i++;
        }
        this.curvesControl.invalidate();
    }

    /* access modifiers changed from: private */
    /* renamed from: lambda$new$7 */
    public /* synthetic */ void lambda$new$7$PhotoFilterView(View view) {
        this.blurType = 0;
        updateSelectedBlurType();
        this.blurControl.setVisibility(4);
        FilterGLThread filterGLThread = this.eglThread;
        if (filterGLThread != null) {
            filterGLThread.requestRender(false);
        }
    }

    /* access modifiers changed from: private */
    /* renamed from: lambda$new$8 */
    public /* synthetic */ void lambda$new$8$PhotoFilterView(View view) {
        this.blurType = 1;
        updateSelectedBlurType();
        this.blurControl.setVisibility(0);
        this.blurControl.setType(1);
        FilterGLThread filterGLThread = this.eglThread;
        if (filterGLThread != null) {
            filterGLThread.requestRender(false);
        }
    }

    /* access modifiers changed from: private */
    /* renamed from: lambda$new$9 */
    public /* synthetic */ void lambda$new$9$PhotoFilterView(View view) {
        this.blurType = 2;
        updateSelectedBlurType();
        this.blurControl.setVisibility(0);
        this.blurControl.setType(0);
        FilterGLThread filterGLThread = this.eglThread;
        if (filterGLThread != null) {
            filterGLThread.requestRender(false);
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
        savedFilterState.softenSkinValue = this.softenSkinValue;
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
        this.lastState = savedFilterState;
        return savedFilterState;
    }

    public boolean hasChanges() {
        MediaController.SavedFilterState savedFilterState = this.lastState;
        if (savedFilterState != null) {
            if (this.enhanceValue == savedFilterState.enhanceValue && this.contrastValue == savedFilterState.contrastValue && this.highlightsValue == savedFilterState.highlightsValue && this.exposureValue == savedFilterState.exposureValue && this.warmthValue == savedFilterState.warmthValue && this.saturationValue == savedFilterState.saturationValue && this.vignetteValue == savedFilterState.vignetteValue && this.shadowsValue == savedFilterState.shadowsValue && this.grainValue == savedFilterState.grainValue && this.sharpenValue == savedFilterState.sharpenValue && this.fadeValue == savedFilterState.fadeValue && this.softenSkinValue == savedFilterState.softenSkinValue && this.tintHighlightsColor == savedFilterState.tintHighlightsColor && this.tintShadowsColor == savedFilterState.tintShadowsColor && this.curvesToolValue.shouldBeSkipped()) {
                return false;
            }
            return true;
        } else if (this.enhanceValue == 0.0f && this.contrastValue == 0.0f && this.highlightsValue == 0.0f && this.exposureValue == 0.0f && this.warmthValue == 0.0f && this.saturationValue == 0.0f && this.vignetteValue == 0.0f && this.shadowsValue == 0.0f && this.grainValue == 0.0f && this.sharpenValue == 0.0f && this.fadeValue == 0.0f && this.softenSkinValue == 0.0f && this.tintHighlightsColor == 0 && this.tintShadowsColor == 0 && this.curvesToolValue.shouldBeSkipped()) {
            return false;
        } else {
            return true;
        }
    }

    public void onTouch(MotionEvent motionEvent) {
        if (motionEvent.getActionMasked() == 0 || motionEvent.getActionMasked() == 5) {
            TextureView textureView2 = this.textureView;
            if (textureView2 instanceof VideoEditTextureView) {
                if (((VideoEditTextureView) textureView2).containsPoint(motionEvent.getX(), motionEvent.getY())) {
                    setShowOriginal(true);
                }
            } else if (motionEvent.getX() >= this.textureView.getX() && motionEvent.getY() >= this.textureView.getY() && motionEvent.getX() <= this.textureView.getX() + ((float) this.textureView.getWidth()) && motionEvent.getY() <= this.textureView.getY() + ((float) this.textureView.getHeight())) {
                setShowOriginal(true);
            }
        } else if (motionEvent.getActionMasked() == 1 || motionEvent.getActionMasked() == 6) {
            setShowOriginal(false);
        }
    }

    private void setShowOriginal(boolean z) {
        if (this.showOriginal != z) {
            this.showOriginal = z;
            FilterGLThread filterGLThread = this.eglThread;
            if (filterGLThread != null) {
                filterGLThread.requestRender(false);
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
        if (this.ownsTextureView) {
            FilterGLThread filterGLThread = this.eglThread;
            if (filterGLThread != null) {
                filterGLThread.shutdown();
                this.eglThread = null;
            }
            this.textureView.setVisibility(8);
            return;
        }
        TextureView textureView2 = this.textureView;
        if (textureView2 instanceof VideoEditTextureView) {
            VideoEditTextureView videoEditTextureView = (VideoEditTextureView) textureView2;
            MediaController.SavedFilterState savedFilterState = this.lastState;
            if (savedFilterState == null) {
                videoEditTextureView.setDelegate((VideoEditTextureView.VideoEditTextureViewDelegate) null);
            } else {
                this.eglThread.setFilterGLThreadDelegate(FilterShaders.getFilterShadersDelegate(savedFilterState));
            }
        }
    }

    public void init() {
        if (this.ownsTextureView) {
            this.textureView.setVisibility(0);
        }
    }

    public Bitmap getBitmap() {
        FilterGLThread filterGLThread = this.eglThread;
        if (filterGLThread != null) {
            return filterGLThread.getTexture();
        }
        return null;
    }

    private void fixLayout(int i, int i2) {
        int i3;
        float f;
        float f2;
        float f3;
        int dp = i - AndroidUtilities.dp(28.0f);
        int dp2 = AndroidUtilities.dp(214.0f);
        int i4 = Build.VERSION.SDK_INT;
        int i5 = i2 - (dp2 + ((i4 < 21 || this.inBubbleMode) ? 0 : AndroidUtilities.statusBarHeight));
        Bitmap bitmap = this.bitmapToEdit;
        if (bitmap != null) {
            int i6 = this.orientation;
            if (i6 % 360 == 90 || i6 % 360 == 270) {
                f = (float) bitmap.getHeight();
                i3 = this.bitmapToEdit.getWidth();
            } else {
                f = (float) bitmap.getWidth();
                i3 = this.bitmapToEdit.getHeight();
            }
        } else {
            f = (float) this.textureView.getWidth();
            i3 = this.textureView.getHeight();
        }
        float f4 = (float) i3;
        float f5 = (float) dp;
        float f6 = f5 / f;
        float f7 = (float) i5;
        float f8 = f7 / f4;
        if (f6 > f8) {
            f3 = (float) ((int) Math.ceil((double) (f * f8)));
            f2 = f7;
        } else {
            f2 = (float) ((int) Math.ceil((double) (f4 * f6)));
            f3 = f5;
        }
        int ceil = (int) Math.ceil((double) (((f5 - f3) / 2.0f) + ((float) AndroidUtilities.dp(14.0f))));
        int ceil2 = (int) Math.ceil((double) (((f7 - f2) / 2.0f) + ((float) AndroidUtilities.dp(14.0f)) + ((float) ((i4 < 21 || this.inBubbleMode) ? 0 : AndroidUtilities.statusBarHeight))));
        int i7 = (int) f3;
        int i8 = (int) f2;
        if (this.ownsTextureView) {
            FrameLayout.LayoutParams layoutParams = (FrameLayout.LayoutParams) this.textureView.getLayoutParams();
            layoutParams.leftMargin = ceil;
            layoutParams.topMargin = ceil2;
            layoutParams.width = i7;
            layoutParams.height = i8;
        }
        float f9 = (float) i7;
        float var_ = (float) i8;
        this.curvesControl.setActualArea((float) ceil, (float) (ceil2 - ((i4 < 21 || this.inBubbleMode) ? 0 : AndroidUtilities.statusBarHeight)), f9, var_);
        this.blurControl.setActualAreaSize(f9, var_);
        ((FrameLayout.LayoutParams) this.blurControl.getLayoutParams()).height = AndroidUtilities.dp(38.0f) + i5;
        ((FrameLayout.LayoutParams) this.curvesControl.getLayoutParams()).height = i5 + AndroidUtilities.dp(28.0f);
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

    /* access modifiers changed from: protected */
    public boolean drawChild(Canvas canvas, View view, long j) {
        boolean drawChild = super.drawChild(canvas, view, j);
        if (this.paintingOverlay != null && view == this.textureView) {
            canvas.save();
            canvas.translate((float) this.textureView.getLeft(), (float) this.textureView.getTop());
            float measuredWidth = ((float) this.textureView.getMeasuredWidth()) / ((float) this.paintingOverlay.getMeasuredWidth());
            canvas.scale(measuredWidth, measuredWidth);
            this.paintingOverlay.draw(canvas);
            canvas.restore();
        }
        return drawChild;
    }

    /* access modifiers changed from: protected */
    public void onMeasure(int i, int i2) {
        fixLayout(View.MeasureSpec.getSize(i), View.MeasureSpec.getSize(i2));
        super.onMeasure(i, i2);
    }

    public float getShadowsValue() {
        return ((this.shadowsValue * 0.55f) + 100.0f) / 100.0f;
    }

    public float getHighlightsValue() {
        return ((this.highlightsValue * 0.75f) + 100.0f) / 100.0f;
    }

    public float getEnhanceValue() {
        return this.enhanceValue / 100.0f;
    }

    public float getExposureValue() {
        return this.exposureValue / 100.0f;
    }

    public float getContrastValue() {
        return ((this.contrastValue / 100.0f) * 0.3f) + 1.0f;
    }

    public float getWarmthValue() {
        return this.warmthValue / 100.0f;
    }

    public float getVignetteValue() {
        return this.vignetteValue / 100.0f;
    }

    public float getSharpenValue() {
        return ((this.sharpenValue / 100.0f) * 0.6f) + 0.11f;
    }

    public float getGrainValue() {
        return (this.grainValue / 100.0f) * 0.04f;
    }

    public float getFadeValue() {
        return this.fadeValue / 100.0f;
    }

    public float getSoftenSkinValue() {
        return this.softenSkinValue / 100.0f;
    }

    public float getTintHighlightsIntensityValue() {
        return this.tintHighlightsColor == 0 ? 0.0f : 0.5f;
    }

    public float getTintShadowsIntensityValue() {
        return this.tintShadowsColor == 0 ? 0.0f : 0.5f;
    }

    public float getSaturationValue() {
        float f = this.saturationValue / 100.0f;
        if (f > 0.0f) {
            f *= 1.05f;
        }
        return f + 1.0f;
    }

    public int getTintHighlightsColor() {
        return this.tintHighlightsColor;
    }

    public int getTintShadowsColor() {
        return this.tintShadowsColor;
    }

    public int getBlurType() {
        return this.blurType;
    }

    public float getBlurExcludeSize() {
        return this.blurExcludeSize;
    }

    public float getBlurExcludeBlurSize() {
        return this.blurExcludeBlurSize;
    }

    public float getBlurAngle() {
        return this.blurAngle;
    }

    public Point getBlurExcludePoint() {
        return this.blurExcludePoint;
    }

    public boolean shouldShowOriginal() {
        return this.showOriginal;
    }

    public boolean shouldDrawCurvesPass() {
        return !this.curvesToolValue.shouldBeSkipped();
    }

    public ByteBuffer fillAndGetCurveBuffer() {
        this.curvesToolValue.fillBuffer();
        return this.curvesToolValue.curveBuffer;
    }

    public FrameLayout getToolsView() {
        return this.toolsView;
    }

    public View getCurveControl() {
        return this.curvesControl;
    }

    public View getBlurControl() {
        return this.blurControl;
    }

    public TextView getDoneTextView() {
        return this.doneTextView;
    }

    public TextView getCancelTextView() {
        return this.cancelTextView;
    }

    public class ToolsAdapter extends RecyclerListView.SelectionAdapter {
        private Context mContext;

        public long getItemId(int i) {
            return (long) i;
        }

        public boolean isEnabled(RecyclerView.ViewHolder viewHolder) {
            return false;
        }

        public ToolsAdapter(Context context) {
            this.mContext = context;
        }

        public int getItemCount() {
            return PhotoFilterView.this.rowsCount;
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
                org.telegram.ui.Components.-$$Lambda$PhotoFilterView$ToolsAdapter$EOKeFucSDUYP_lu9dSpbDzFOUJQ r2 = new org.telegram.ui.Components.-$$Lambda$PhotoFilterView$ToolsAdapter$EOKeFucSDUYP_lu9dSpbDzFOUJQ
                r2.<init>()
                r1.setSeekBarDelegate(r2)
                goto L_0x0021
            L_0x0012:
                org.telegram.ui.Cells.PhotoEditRadioCell r1 = new org.telegram.ui.Cells.PhotoEditRadioCell
                android.content.Context r2 = r0.mContext
                r1.<init>(r2)
                org.telegram.ui.Components.-$$Lambda$PhotoFilterView$ToolsAdapter$0krgHb7WblSm-Pbun4oy6OZgkWI r2 = new org.telegram.ui.Components.-$$Lambda$PhotoFilterView$ToolsAdapter$0krgHb7WblSm-Pbun4oy6OZgkWI
                r2.<init>()
                r1.setOnClickListener(r2)
            L_0x0021:
                org.telegram.ui.Components.RecyclerListView$Holder r2 = new org.telegram.ui.Components.RecyclerListView$Holder
                r2.<init>(r1)
                return r2
            */
            throw new UnsupportedOperationException("Method not decompiled: org.telegram.ui.Components.PhotoFilterView.ToolsAdapter.onCreateViewHolder(android.view.ViewGroup, int):androidx.recyclerview.widget.RecyclerView$ViewHolder");
        }

        /* access modifiers changed from: private */
        /* renamed from: lambda$onCreateViewHolder$0 */
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
            } else if (i == PhotoFilterView.this.softenSkinTool) {
                float unused12 = PhotoFilterView.this.softenSkinValue = (float) i2;
            }
            if (PhotoFilterView.this.eglThread != null) {
                PhotoFilterView.this.eglThread.requestRender(true);
            }
        }

        /* access modifiers changed from: private */
        /* renamed from: lambda$onCreateViewHolder$1 */
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
                } else if (i == PhotoFilterView.this.softenSkinTool) {
                    photoEditToolCell.setIconAndTextAndValue(LocaleController.getString("SoftenSkin", NUM), PhotoFilterView.this.softenSkinValue, 0, 100);
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
