package org.telegram.ui.Components;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.ColorFilter;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffColorFilter;
import android.graphics.SurfaceTexture;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.view.MotionEvent;
import android.view.TextureView;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.util.ArrayList;
import org.telegram.messenger.AndroidUtilities;
import org.telegram.messenger.LocaleController;
import org.telegram.messenger.MediaController;
import org.telegram.ui.ActionBar.Theme;
import org.telegram.ui.BubbleActivity;
import org.telegram.ui.Cells.PhotoEditRadioCell;
import org.telegram.ui.Cells.PhotoEditToolCell;
import org.telegram.ui.Components.FilterShaders;
import org.telegram.ui.Components.RecyclerListView;
import org.telegram.ui.Components.VideoEditTextureView;

public class PhotoFilterView extends FrameLayout implements FilterShaders.FilterShadersDelegate {
    private static final int curveDataStep = 2;
    private static final int curveGranularity = 100;
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
    public final Theme.ResourcesProvider resourcesProvider;
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
            float f = this.blacksLevel;
            int i = 1;
            float f2 = 0.5f;
            float f3 = this.whitesLevel;
            float[] points = {-0.001f, f / 100.0f, 0.0f, f / 100.0f, 0.25f, this.shadowsLevel / 100.0f, 0.5f, this.midtonesLevel / 100.0f, 0.75f, this.highlightsLevel / 100.0f, 1.0f, f3 / 100.0f, 1.001f, f3 / 100.0f};
            int i2 = 100;
            ArrayList<Float> dataPoints = new ArrayList<>(100);
            ArrayList<Float> interpolatedPoints = new ArrayList<>(100);
            interpolatedPoints.add(Float.valueOf(points[0]));
            interpolatedPoints.add(Float.valueOf(points[1]));
            int index = 1;
            while (index < (points.length / 2) - 2) {
                float point0x = points[(index - 1) * 2];
                float point0y = points[((index - 1) * 2) + i];
                float point1x = points[index * 2];
                float point1y = points[(index * 2) + 1];
                float point2x = points[(index + 1) * 2];
                float point2y = points[((index + 1) * 2) + 1];
                float point3x = points[(index + 2) * 2];
                float point3y = points[((index + 2) * 2) + 1];
                int i3 = 1;
                while (i3 < i2) {
                    float t = ((float) i3) * 0.01f;
                    float tt = t * t;
                    float ttt = tt * t;
                    float pix = ((point1x * 2.0f) + ((point2x - point0x) * t) + (((((point0x * 2.0f) - (point1x * 5.0f)) + (point2x * 4.0f)) - point3x) * tt) + (((((point1x * 3.0f) - point0x) - (point2x * 3.0f)) + point3x) * ttt)) * f2;
                    float piy = Math.max(0.0f, Math.min(1.0f, ((point1y * 2.0f) + ((point2y - point0y) * t) + (((((2.0f * point0y) - (5.0f * point1y)) + (4.0f * point2y)) - point3y) * tt) + (((((point1y * 3.0f) - point0y) - (3.0f * point2y)) + point3y) * ttt)) * f2));
                    if (pix > point0x) {
                        interpolatedPoints.add(Float.valueOf(pix));
                        interpolatedPoints.add(Float.valueOf(piy));
                    }
                    if ((i3 - 1) % 2 == 0) {
                        dataPoints.add(Float.valueOf(piy));
                    }
                    i3++;
                    f2 = 0.5f;
                    i2 = 100;
                }
                interpolatedPoints.add(Float.valueOf(point2x));
                interpolatedPoints.add(Float.valueOf(point2y));
                index++;
                i = 1;
                f2 = 0.5f;
                i2 = 100;
            }
            interpolatedPoints.add(Float.valueOf(points[12]));
            interpolatedPoints.add(Float.valueOf(points[13]));
            this.cachedDataPoints = new float[dataPoints.size()];
            int a = 0;
            while (true) {
                float[] fArr = this.cachedDataPoints;
                if (a >= fArr.length) {
                    break;
                }
                fArr[a] = dataPoints.get(a).floatValue();
                a++;
            }
            float[] retValue = new float[interpolatedPoints.size()];
            for (int a2 = 0; a2 < retValue.length; a2++) {
                retValue[a2] = interpolatedPoints.get(a2).floatValue();
            }
            return retValue;
        }

        public boolean isDefault() {
            return ((double) Math.abs(this.blacksLevel - 0.0f)) < 1.0E-5d && ((double) Math.abs(this.shadowsLevel - 25.0f)) < 1.0E-5d && ((double) Math.abs(this.midtonesLevel - 50.0f)) < 1.0E-5d && ((double) Math.abs(this.highlightsLevel - 75.0f)) < 1.0E-5d && ((double) Math.abs(this.whitesLevel - 100.0f)) < 1.0E-5d;
        }
    }

    public static class CurvesToolValue {
        public static final int CurvesTypeBlue = 3;
        public static final int CurvesTypeGreen = 2;
        public static final int CurvesTypeLuminance = 0;
        public static final int CurvesTypeRed = 1;
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
            float[] luminanceCurveData = this.luminanceCurve.getDataPoints();
            float[] redCurveData = this.redCurve.getDataPoints();
            float[] greenCurveData = this.greenCurve.getDataPoints();
            float[] blueCurveData = this.blueCurve.getDataPoints();
            for (int a = 0; a < 200; a++) {
                this.curveBuffer.put((byte) ((int) (redCurveData[a] * 255.0f)));
                this.curveBuffer.put((byte) ((int) (greenCurveData[a] * 255.0f)));
                this.curveBuffer.put((byte) ((int) (blueCurveData[a] * 255.0f)));
                this.curveBuffer.put((byte) ((int) (luminanceCurveData[a] * 255.0f)));
            }
            this.curveBuffer.position(0);
        }

        public boolean shouldBeSkipped() {
            return this.luminanceCurve.isDefault() && this.redCurve.isDefault() && this.greenCurve.isDefault() && this.blueCurve.isDefault();
        }
    }

    /* JADX INFO: super call moved to the top of the method (can break code semantics) */
    public PhotoFilterView(Context context, VideoEditTextureView videoTextureView, Bitmap bitmap, int rotation, MediaController.SavedFilterState state, PaintingOverlay overlay, int hasFaces, boolean mirror, Theme.ResourcesProvider resourcesProvider2) {
        super(context);
        LinearLayoutManager layoutManager;
        Context context2 = context;
        VideoEditTextureView videoEditTextureView = videoTextureView;
        MediaController.SavedFilterState savedFilterState = state;
        int i = hasFaces;
        this.resourcesProvider = resourcesProvider2;
        this.inBubbleMode = context2 instanceof BubbleActivity;
        this.paintingOverlay = overlay;
        this.isMirrored = mirror;
        this.rowsCount = 0;
        if (i == 1) {
            this.rowsCount = 0 + 1;
            this.softenSkinTool = 0;
        } else if (i == 0) {
            this.softenSkinTool = -1;
        }
        int i2 = this.rowsCount;
        int i3 = i2 + 1;
        this.rowsCount = i3;
        this.enhanceTool = i2;
        int i4 = i3 + 1;
        this.rowsCount = i4;
        this.exposureTool = i3;
        int i5 = i4 + 1;
        this.rowsCount = i5;
        this.contrastTool = i4;
        int i6 = i5 + 1;
        this.rowsCount = i6;
        this.saturationTool = i5;
        int i7 = i6 + 1;
        this.rowsCount = i7;
        this.warmthTool = i6;
        int i8 = i7 + 1;
        this.rowsCount = i8;
        this.fadeTool = i7;
        int i9 = i8 + 1;
        this.rowsCount = i9;
        this.highlightsTool = i8;
        int i10 = i9 + 1;
        this.rowsCount = i10;
        this.shadowsTool = i9;
        int i11 = i10 + 1;
        this.rowsCount = i11;
        this.vignetteTool = i10;
        if (i == 2) {
            this.rowsCount = i11 + 1;
            this.softenSkinTool = i11;
        }
        if (videoEditTextureView == null) {
            int i12 = this.rowsCount;
            this.rowsCount = i12 + 1;
            this.grainTool = i12;
        } else {
            this.grainTool = -1;
        }
        int i13 = this.rowsCount;
        int i14 = i13 + 1;
        this.rowsCount = i14;
        this.sharpenTool = i13;
        int i15 = i14 + 1;
        this.rowsCount = i15;
        this.tintShadowsTool = i14;
        this.rowsCount = i15 + 1;
        this.tintHighlightsTool = i15;
        if (savedFilterState != null) {
            this.enhanceValue = savedFilterState.enhanceValue;
            this.softenSkinValue = savedFilterState.softenSkinValue;
            this.exposureValue = savedFilterState.exposureValue;
            this.contrastValue = savedFilterState.contrastValue;
            this.warmthValue = savedFilterState.warmthValue;
            this.saturationValue = savedFilterState.saturationValue;
            this.fadeValue = savedFilterState.fadeValue;
            this.tintShadowsColor = savedFilterState.tintShadowsColor;
            this.tintHighlightsColor = savedFilterState.tintHighlightsColor;
            this.highlightsValue = savedFilterState.highlightsValue;
            this.shadowsValue = savedFilterState.shadowsValue;
            this.vignetteValue = savedFilterState.vignetteValue;
            this.grainValue = savedFilterState.grainValue;
            this.blurType = savedFilterState.blurType;
            this.sharpenValue = savedFilterState.sharpenValue;
            this.curvesToolValue = savedFilterState.curvesToolValue;
            this.blurExcludeSize = savedFilterState.blurExcludeSize;
            this.blurExcludePoint = savedFilterState.blurExcludePoint;
            this.blurExcludeBlurSize = savedFilterState.blurExcludeBlurSize;
            this.blurAngle = savedFilterState.blurAngle;
            this.lastState = savedFilterState;
        } else {
            this.curvesToolValue = new CurvesToolValue();
            this.blurExcludeSize = 0.35f;
            this.blurExcludePoint = new Point(0.5f, 0.5f);
            this.blurExcludeBlurSize = 0.15f;
            this.blurAngle = 1.5707964f;
        }
        this.bitmapToEdit = bitmap;
        this.orientation = rotation;
        if (videoEditTextureView != null) {
            this.textureView = videoEditTextureView;
            videoEditTextureView.setDelegate(new PhotoFilterView$$ExternalSyntheticLambda9(this));
        } else {
            this.ownsTextureView = true;
            TextureView textureView2 = new TextureView(context2);
            this.textureView = textureView2;
            addView(textureView2, LayoutHelper.createFrame(-1, -1, 51));
            this.textureView.setVisibility(4);
            this.textureView.setSurfaceTextureListener(new TextureView.SurfaceTextureListener() {
                public void onSurfaceTextureAvailable(SurfaceTexture surface, int width, int height) {
                    if (PhotoFilterView.this.eglThread == null && surface != null) {
                        FilterGLThread unused = PhotoFilterView.this.eglThread = new FilterGLThread(surface, PhotoFilterView.this.bitmapToEdit, PhotoFilterView.this.orientation, PhotoFilterView.this.isMirrored);
                        PhotoFilterView.this.eglThread.setFilterGLThreadDelegate(PhotoFilterView.this);
                        PhotoFilterView.this.eglThread.setSurfaceTextureSize(width, height);
                        PhotoFilterView.this.eglThread.requestRender(true, true, false);
                    }
                }

                public void onSurfaceTextureSizeChanged(SurfaceTexture surface, int width, int height) {
                    if (PhotoFilterView.this.eglThread != null) {
                        PhotoFilterView.this.eglThread.setSurfaceTextureSize(width, height);
                        PhotoFilterView.this.eglThread.requestRender(false, true, false);
                        PhotoFilterView.this.eglThread.postRunnable(new PhotoFilterView$1$$ExternalSyntheticLambda0(this));
                    }
                }

                /* renamed from: lambda$onSurfaceTextureSizeChanged$0$org-telegram-ui-Components-PhotoFilterView$1  reason: not valid java name */
                public /* synthetic */ void m1174x9475d40a() {
                    if (PhotoFilterView.this.eglThread != null) {
                        PhotoFilterView.this.eglThread.requestRender(false, true, false);
                    }
                }

                public boolean onSurfaceTextureDestroyed(SurfaceTexture surface) {
                    if (PhotoFilterView.this.eglThread == null) {
                        return true;
                    }
                    PhotoFilterView.this.eglThread.shutdown();
                    FilterGLThread unused = PhotoFilterView.this.eglThread = null;
                    return true;
                }

                public void onSurfaceTextureUpdated(SurfaceTexture surface) {
                }
            });
        }
        PhotoFilterBlurControl photoFilterBlurControl = new PhotoFilterBlurControl(context2);
        this.blurControl = photoFilterBlurControl;
        photoFilterBlurControl.setVisibility(4);
        addView(this.blurControl, LayoutHelper.createFrame(-1, -1, 51));
        this.blurControl.setDelegate(new PhotoFilterView$$ExternalSyntheticLambda7(this));
        PhotoFilterCurvesControl photoFilterCurvesControl = new PhotoFilterCurvesControl(context2, this.curvesToolValue);
        this.curvesControl = photoFilterCurvesControl;
        photoFilterCurvesControl.setDelegate(new PhotoFilterView$$ExternalSyntheticLambda8(this));
        this.curvesControl.setVisibility(4);
        addView(this.curvesControl, LayoutHelper.createFrame(-1, -1, 51));
        FrameLayout frameLayout = new FrameLayout(context2);
        this.toolsView = frameLayout;
        addView(frameLayout, LayoutHelper.createFrame(-1, 186, 83));
        FrameLayout frameLayout2 = new FrameLayout(context2);
        frameLayout2.setBackgroundColor(-16777216);
        this.toolsView.addView(frameLayout2, LayoutHelper.createFrame(-1, 48, 83));
        TextView textView = new TextView(context2);
        this.cancelTextView = textView;
        textView.setTextSize(1, 14.0f);
        this.cancelTextView.setTextColor(-1);
        this.cancelTextView.setGravity(17);
        this.cancelTextView.setBackgroundDrawable(Theme.createSelectorDrawable(-12763843, 0));
        this.cancelTextView.setPadding(AndroidUtilities.dp(20.0f), 0, AndroidUtilities.dp(20.0f), 0);
        this.cancelTextView.setText(LocaleController.getString("Cancel", NUM).toUpperCase());
        this.cancelTextView.setTypeface(AndroidUtilities.getTypeface("fonts/rmedium.ttf"));
        frameLayout2.addView(this.cancelTextView, LayoutHelper.createFrame(-2, -1, 51));
        TextView textView2 = new TextView(context2);
        this.doneTextView = textView2;
        textView2.setTextSize(1, 14.0f);
        this.doneTextView.setTextColor(getThemedColor("dialogFloatingButton"));
        this.doneTextView.setGravity(17);
        this.doneTextView.setBackgroundDrawable(Theme.createSelectorDrawable(-12763843, 0));
        this.doneTextView.setPadding(AndroidUtilities.dp(20.0f), 0, AndroidUtilities.dp(20.0f), 0);
        this.doneTextView.setText(LocaleController.getString("Done", NUM).toUpperCase());
        this.doneTextView.setTypeface(AndroidUtilities.getTypeface("fonts/rmedium.ttf"));
        frameLayout2.addView(this.doneTextView, LayoutHelper.createFrame(-2, -1, 53));
        LinearLayout linearLayout = new LinearLayout(context2);
        frameLayout2.addView(linearLayout, LayoutHelper.createFrame(-2, -1, 1));
        ImageView imageView = new ImageView(context2);
        this.tuneItem = imageView;
        imageView.setScaleType(ImageView.ScaleType.CENTER);
        this.tuneItem.setImageResource(NUM);
        this.tuneItem.setColorFilter(new PorterDuffColorFilter(getThemedColor("dialogFloatingButton"), PorterDuff.Mode.MULTIPLY));
        this.tuneItem.setBackgroundDrawable(Theme.createSelectorDrawable(NUM));
        linearLayout.addView(this.tuneItem, LayoutHelper.createLinear(56, 48));
        this.tuneItem.setOnClickListener(new PhotoFilterView$$ExternalSyntheticLambda0(this));
        ImageView imageView2 = new ImageView(context2);
        this.blurItem = imageView2;
        imageView2.setScaleType(ImageView.ScaleType.CENTER);
        this.blurItem.setImageResource(NUM);
        this.blurItem.setBackgroundDrawable(Theme.createSelectorDrawable(NUM));
        linearLayout.addView(this.blurItem, LayoutHelper.createLinear(56, 48));
        this.blurItem.setOnClickListener(new PhotoFilterView$$ExternalSyntheticLambda1(this));
        if (videoEditTextureView != null) {
            this.blurItem.setVisibility(8);
        }
        ImageView imageView3 = new ImageView(context2);
        this.curveItem = imageView3;
        imageView3.setScaleType(ImageView.ScaleType.CENTER);
        this.curveItem.setImageResource(NUM);
        this.curveItem.setBackgroundDrawable(Theme.createSelectorDrawable(NUM));
        linearLayout.addView(this.curveItem, LayoutHelper.createLinear(56, 48));
        this.curveItem.setOnClickListener(new PhotoFilterView$$ExternalSyntheticLambda2(this));
        this.recyclerListView = new RecyclerListView(context2);
        LinearLayoutManager layoutManager2 = new LinearLayoutManager(context2);
        layoutManager2.setOrientation(1);
        this.recyclerListView.setLayoutManager(layoutManager2);
        this.recyclerListView.setClipToPadding(false);
        this.recyclerListView.setOverScrollMode(2);
        this.recyclerListView.setAdapter(new ToolsAdapter(context2));
        this.toolsView.addView(this.recyclerListView, LayoutHelper.createFrame(-1, 120, 51));
        FrameLayout frameLayout3 = new FrameLayout(context2);
        this.curveLayout = frameLayout3;
        frameLayout3.setVisibility(4);
        this.toolsView.addView(this.curveLayout, LayoutHelper.createFrame(-1, 78.0f, 1, 0.0f, 40.0f, 0.0f, 0.0f));
        LinearLayout curveTextViewContainer = new LinearLayout(context2);
        curveTextViewContainer.setOrientation(0);
        this.curveLayout.addView(curveTextViewContainer, LayoutHelper.createFrame(-2, -2, 1));
        int a = 0;
        while (a < 4) {
            FrameLayout frameLayout1 = new FrameLayout(context2);
            frameLayout1.setTag(Integer.valueOf(a));
            this.curveRadioButton[a] = new RadioButton(context2);
            LinearLayout linearLayout2 = linearLayout;
            this.curveRadioButton[a].setSize(AndroidUtilities.dp(20.0f));
            frameLayout1.addView(this.curveRadioButton[a], LayoutHelper.createFrame(30, 30, 49));
            TextView curveTextView = new TextView(context2);
            curveTextView.setTextSize(1, 12.0f);
            curveTextView.setGravity(16);
            if (a == 0) {
                String str = LocaleController.getString("CurvesAll", NUM);
                StringBuilder sb = new StringBuilder();
                layoutManager = layoutManager2;
                sb.append(str.substring(0, 1).toUpperCase());
                sb.append(str.substring(1).toLowerCase());
                curveTextView.setText(sb.toString());
                curveTextView.setTextColor(-1);
                this.curveRadioButton[a].setColor(-1, -1);
            } else {
                layoutManager = layoutManager2;
                if (a == 1) {
                    String str2 = LocaleController.getString("CurvesRed", NUM);
                    curveTextView.setText(str2.substring(0, 1).toUpperCase() + str2.substring(1).toLowerCase());
                    curveTextView.setTextColor(-1684147);
                    this.curveRadioButton[a].setColor(-1684147, -1684147);
                } else if (a == 2) {
                    String str3 = LocaleController.getString("CurvesGreen", NUM);
                    curveTextView.setText(str3.substring(0, 1).toUpperCase() + str3.substring(1).toLowerCase());
                    curveTextView.setTextColor(-10831009);
                    this.curveRadioButton[a].setColor(-10831009, -10831009);
                } else if (a == 3) {
                    String str4 = LocaleController.getString("CurvesBlue", NUM);
                    curveTextView.setText(str4.substring(0, 1).toUpperCase() + str4.substring(1).toLowerCase());
                    curveTextView.setTextColor(-12734994);
                    this.curveRadioButton[a].setColor(-12734994, -12734994);
                }
            }
            frameLayout1.addView(curveTextView, LayoutHelper.createFrame(-2, -2.0f, 49, 0.0f, 38.0f, 0.0f, 0.0f));
            curveTextViewContainer.addView(frameLayout1, LayoutHelper.createLinear(-2, -2, a == 0 ? 0.0f : 30.0f, 0.0f, 0.0f, 0.0f));
            frameLayout1.setOnClickListener(new PhotoFilterView$$ExternalSyntheticLambda3(this));
            a++;
            int i16 = hasFaces;
            linearLayout = linearLayout2;
            layoutManager2 = layoutManager;
        }
        LinearLayoutManager linearLayoutManager = layoutManager2;
        FrameLayout frameLayout4 = new FrameLayout(context2);
        this.blurLayout = frameLayout4;
        frameLayout4.setVisibility(4);
        this.toolsView.addView(this.blurLayout, LayoutHelper.createFrame(280, 60.0f, 1, 0.0f, 40.0f, 0.0f, 0.0f));
        TextView textView3 = new TextView(context2);
        this.blurOffButton = textView3;
        textView3.setCompoundDrawablePadding(AndroidUtilities.dp(2.0f));
        this.blurOffButton.setTextSize(1, 13.0f);
        this.blurOffButton.setGravity(1);
        this.blurOffButton.setText(LocaleController.getString("BlurOff", NUM));
        this.blurLayout.addView(this.blurOffButton, LayoutHelper.createFrame(80, 60.0f));
        this.blurOffButton.setOnClickListener(new PhotoFilterView$$ExternalSyntheticLambda4(this));
        TextView textView4 = new TextView(context2);
        this.blurRadialButton = textView4;
        textView4.setCompoundDrawablePadding(AndroidUtilities.dp(2.0f));
        this.blurRadialButton.setTextSize(1, 13.0f);
        this.blurRadialButton.setGravity(1);
        this.blurRadialButton.setText(LocaleController.getString("BlurRadial", NUM));
        this.blurLayout.addView(this.blurRadialButton, LayoutHelper.createFrame(80, 80.0f, 51, 100.0f, 0.0f, 0.0f, 0.0f));
        this.blurRadialButton.setOnClickListener(new PhotoFilterView$$ExternalSyntheticLambda5(this));
        TextView textView5 = new TextView(context2);
        this.blurLinearButton = textView5;
        textView5.setCompoundDrawablePadding(AndroidUtilities.dp(2.0f));
        this.blurLinearButton.setTextSize(1, 13.0f);
        this.blurLinearButton.setGravity(1);
        this.blurLinearButton.setText(LocaleController.getString("BlurLinear", NUM));
        this.blurLayout.addView(this.blurLinearButton, LayoutHelper.createFrame(80, 80.0f, 51, 200.0f, 0.0f, 0.0f, 0.0f));
        this.blurLinearButton.setOnClickListener(new PhotoFilterView$$ExternalSyntheticLambda6(this));
        updateSelectedBlurType();
        if (Build.VERSION.SDK_INT >= 21 && !this.inBubbleMode) {
            if (this.ownsTextureView) {
                ((FrameLayout.LayoutParams) this.textureView.getLayoutParams()).topMargin = AndroidUtilities.statusBarHeight;
            }
            ((FrameLayout.LayoutParams) this.curvesControl.getLayoutParams()).topMargin = AndroidUtilities.statusBarHeight;
        }
    }

    /* renamed from: lambda$new$0$org-telegram-ui-Components-PhotoFilterView  reason: not valid java name */
    public /* synthetic */ void m1164lambda$new$0$orgtelegramuiComponentsPhotoFilterView(FilterGLThread thread) {
        this.eglThread = thread;
        thread.setFilterGLThreadDelegate(this);
    }

    /* renamed from: lambda$new$1$org-telegram-ui-Components-PhotoFilterView  reason: not valid java name */
    public /* synthetic */ void m1165lambda$new$1$orgtelegramuiComponentsPhotoFilterView(Point centerPoint, float falloff, float size, float angle) {
        this.blurExcludeSize = size;
        this.blurExcludePoint = centerPoint;
        this.blurExcludeBlurSize = falloff;
        this.blurAngle = angle;
        FilterGLThread filterGLThread = this.eglThread;
        if (filterGLThread != null) {
            filterGLThread.requestRender(false);
        }
    }

    /* renamed from: lambda$new$2$org-telegram-ui-Components-PhotoFilterView  reason: not valid java name */
    public /* synthetic */ void m1166lambda$new$2$orgtelegramuiComponentsPhotoFilterView() {
        FilterGLThread filterGLThread = this.eglThread;
        if (filterGLThread != null) {
            filterGLThread.requestRender(false);
        }
    }

    /* renamed from: lambda$new$3$org-telegram-ui-Components-PhotoFilterView  reason: not valid java name */
    public /* synthetic */ void m1167lambda$new$3$orgtelegramuiComponentsPhotoFilterView(View v) {
        this.selectedTool = 0;
        this.tuneItem.setColorFilter(new PorterDuffColorFilter(getThemedColor("dialogFloatingButton"), PorterDuff.Mode.MULTIPLY));
        this.blurItem.setColorFilter((ColorFilter) null);
        this.curveItem.setColorFilter((ColorFilter) null);
        switchMode();
    }

    /* renamed from: lambda$new$4$org-telegram-ui-Components-PhotoFilterView  reason: not valid java name */
    public /* synthetic */ void m1168lambda$new$4$orgtelegramuiComponentsPhotoFilterView(View v) {
        this.selectedTool = 1;
        this.tuneItem.setColorFilter((ColorFilter) null);
        this.blurItem.setColorFilter(new PorterDuffColorFilter(getThemedColor("dialogFloatingButton"), PorterDuff.Mode.MULTIPLY));
        this.curveItem.setColorFilter((ColorFilter) null);
        switchMode();
    }

    /* renamed from: lambda$new$5$org-telegram-ui-Components-PhotoFilterView  reason: not valid java name */
    public /* synthetic */ void m1169lambda$new$5$orgtelegramuiComponentsPhotoFilterView(View v) {
        this.selectedTool = 2;
        this.tuneItem.setColorFilter((ColorFilter) null);
        this.blurItem.setColorFilter((ColorFilter) null);
        this.curveItem.setColorFilter(new PorterDuffColorFilter(getThemedColor("dialogFloatingButton"), PorterDuff.Mode.MULTIPLY));
        switchMode();
    }

    /* renamed from: lambda$new$6$org-telegram-ui-Components-PhotoFilterView  reason: not valid java name */
    public /* synthetic */ void m1170lambda$new$6$orgtelegramuiComponentsPhotoFilterView(View v) {
        int num = ((Integer) v.getTag()).intValue();
        this.curvesToolValue.activeType = num;
        int a1 = 0;
        while (a1 < 4) {
            this.curveRadioButton[a1].setChecked(a1 == num, true);
            a1++;
        }
        this.curvesControl.invalidate();
    }

    /* renamed from: lambda$new$7$org-telegram-ui-Components-PhotoFilterView  reason: not valid java name */
    public /* synthetic */ void m1171lambda$new$7$orgtelegramuiComponentsPhotoFilterView(View v) {
        this.blurType = 0;
        updateSelectedBlurType();
        this.blurControl.setVisibility(4);
        FilterGLThread filterGLThread = this.eglThread;
        if (filterGLThread != null) {
            filterGLThread.requestRender(false);
        }
    }

    /* renamed from: lambda$new$8$org-telegram-ui-Components-PhotoFilterView  reason: not valid java name */
    public /* synthetic */ void m1172lambda$new$8$orgtelegramuiComponentsPhotoFilterView(View v) {
        this.blurType = 1;
        updateSelectedBlurType();
        this.blurControl.setVisibility(0);
        this.blurControl.setType(1);
        FilterGLThread filterGLThread = this.eglThread;
        if (filterGLThread != null) {
            filterGLThread.requestRender(false);
        }
    }

    /* renamed from: lambda$new$9$org-telegram-ui-Components-PhotoFilterView  reason: not valid java name */
    public /* synthetic */ void m1173lambda$new$9$orgtelegramuiComponentsPhotoFilterView(View v) {
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
            textView.setTextColor(getThemedColor("dialogFloatingButton"));
        }
        ImageView imageView = this.tuneItem;
        if (!(imageView == null || imageView.getColorFilter() == null)) {
            this.tuneItem.setColorFilter(new PorterDuffColorFilter(getThemedColor("dialogFloatingButton"), PorterDuff.Mode.MULTIPLY));
        }
        ImageView imageView2 = this.blurItem;
        if (!(imageView2 == null || imageView2.getColorFilter() == null)) {
            this.blurItem.setColorFilter(new PorterDuffColorFilter(getThemedColor("dialogFloatingButton"), PorterDuff.Mode.MULTIPLY));
        }
        ImageView imageView3 = this.curveItem;
        if (!(imageView3 == null || imageView3.getColorFilter() == null)) {
            this.curveItem.setColorFilter(new PorterDuffColorFilter(getThemedColor("dialogFloatingButton"), PorterDuff.Mode.MULTIPLY));
        }
        updateSelectedBlurType();
    }

    private void updateSelectedBlurType() {
        int i = this.blurType;
        if (i == 0) {
            Drawable drawable = this.blurOffButton.getContext().getResources().getDrawable(NUM).mutate();
            drawable.setColorFilter(new PorterDuffColorFilter(getThemedColor("dialogFloatingButton"), PorterDuff.Mode.MULTIPLY));
            this.blurOffButton.setCompoundDrawablesWithIntrinsicBounds((Drawable) null, drawable, (Drawable) null, (Drawable) null);
            this.blurOffButton.setTextColor(getThemedColor("dialogFloatingButton"));
            this.blurRadialButton.setCompoundDrawablesWithIntrinsicBounds(0, NUM, 0, 0);
            this.blurRadialButton.setTextColor(-1);
            this.blurLinearButton.setCompoundDrawablesWithIntrinsicBounds(0, NUM, 0, 0);
            this.blurLinearButton.setTextColor(-1);
        } else if (i == 1) {
            this.blurOffButton.setCompoundDrawablesWithIntrinsicBounds(0, NUM, 0, 0);
            this.blurOffButton.setTextColor(-1);
            Drawable drawable2 = this.blurOffButton.getContext().getResources().getDrawable(NUM).mutate();
            drawable2.setColorFilter(new PorterDuffColorFilter(getThemedColor("dialogFloatingButton"), PorterDuff.Mode.MULTIPLY));
            this.blurRadialButton.setCompoundDrawablesWithIntrinsicBounds((Drawable) null, drawable2, (Drawable) null, (Drawable) null);
            this.blurRadialButton.setTextColor(getThemedColor("dialogFloatingButton"));
            this.blurLinearButton.setCompoundDrawablesWithIntrinsicBounds(0, NUM, 0, 0);
            this.blurLinearButton.setTextColor(-1);
        } else if (i == 2) {
            this.blurOffButton.setCompoundDrawablesWithIntrinsicBounds(0, NUM, 0, 0);
            this.blurOffButton.setTextColor(-1);
            this.blurRadialButton.setCompoundDrawablesWithIntrinsicBounds(0, NUM, 0, 0);
            this.blurRadialButton.setTextColor(-1);
            Drawable drawable3 = this.blurOffButton.getContext().getResources().getDrawable(NUM).mutate();
            drawable3.setColorFilter(new PorterDuffColorFilter(getThemedColor("dialogFloatingButton"), PorterDuff.Mode.MULTIPLY));
            this.blurLinearButton.setCompoundDrawablesWithIntrinsicBounds((Drawable) null, drawable3, (Drawable) null, (Drawable) null);
            this.blurLinearButton.setTextColor(getThemedColor("dialogFloatingButton"));
        }
    }

    public MediaController.SavedFilterState getSavedFilterState() {
        MediaController.SavedFilterState state = new MediaController.SavedFilterState();
        state.enhanceValue = this.enhanceValue;
        state.exposureValue = this.exposureValue;
        state.contrastValue = this.contrastValue;
        state.warmthValue = this.warmthValue;
        state.saturationValue = this.saturationValue;
        state.fadeValue = this.fadeValue;
        state.softenSkinValue = this.softenSkinValue;
        state.tintShadowsColor = this.tintShadowsColor;
        state.tintHighlightsColor = this.tintHighlightsColor;
        state.highlightsValue = this.highlightsValue;
        state.shadowsValue = this.shadowsValue;
        state.vignetteValue = this.vignetteValue;
        state.grainValue = this.grainValue;
        state.blurType = this.blurType;
        state.sharpenValue = this.sharpenValue;
        state.curvesToolValue = this.curvesToolValue;
        state.blurExcludeSize = this.blurExcludeSize;
        state.blurExcludePoint = this.blurExcludePoint;
        state.blurExcludeBlurSize = this.blurExcludeBlurSize;
        state.blurAngle = this.blurAngle;
        this.lastState = state;
        return state;
    }

    public boolean hasChanges() {
        MediaController.SavedFilterState savedFilterState = this.lastState;
        if (savedFilterState != null) {
            if (this.enhanceValue == savedFilterState.enhanceValue && this.contrastValue == this.lastState.contrastValue && this.highlightsValue == this.lastState.highlightsValue && this.exposureValue == this.lastState.exposureValue && this.warmthValue == this.lastState.warmthValue && this.saturationValue == this.lastState.saturationValue && this.vignetteValue == this.lastState.vignetteValue && this.shadowsValue == this.lastState.shadowsValue && this.grainValue == this.lastState.grainValue && this.sharpenValue == this.lastState.sharpenValue && this.fadeValue == this.lastState.fadeValue && this.softenSkinValue == this.lastState.softenSkinValue && this.tintHighlightsColor == this.lastState.tintHighlightsColor && this.tintShadowsColor == this.lastState.tintShadowsColor && this.curvesToolValue.shouldBeSkipped()) {
                return false;
            }
            return true;
        } else if (this.enhanceValue == 0.0f && this.contrastValue == 0.0f && this.highlightsValue == 0.0f && this.exposureValue == 0.0f && this.warmthValue == 0.0f && this.saturationValue == 0.0f && this.vignetteValue == 0.0f && this.shadowsValue == 0.0f && this.grainValue == 0.0f && this.sharpenValue == 0.0f && this.fadeValue == 0.0f && this.softenSkinValue == 0.0f && this.tintHighlightsColor == 0 && this.tintShadowsColor == 0 && this.curvesToolValue.shouldBeSkipped()) {
            return false;
        } else {
            return true;
        }
    }

    public void onTouch(MotionEvent event) {
        if (event.getActionMasked() == 0 || event.getActionMasked() == 5) {
            TextureView textureView2 = this.textureView;
            if (textureView2 instanceof VideoEditTextureView) {
                if (((VideoEditTextureView) textureView2).containsPoint(event.getX(), event.getY())) {
                    setShowOriginal(true);
                }
            } else if (event.getX() >= this.textureView.getX() && event.getY() >= this.textureView.getY() && event.getX() <= this.textureView.getX() + ((float) this.textureView.getWidth()) && event.getY() <= this.textureView.getY() + ((float) this.textureView.getHeight())) {
                setShowOriginal(true);
            }
        } else if (event.getActionMasked() == 1 || event.getActionMasked() == 6) {
            setShowOriginal(false);
        }
    }

    private void setShowOriginal(boolean value) {
        if (this.showOriginal != value) {
            this.showOriginal = value;
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
            int a = 0;
            while (a < 4) {
                this.curveRadioButton[a].setChecked(a == 0, false);
                a++;
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

    private void fixLayout(int viewWidth, int viewHeight) {
        float bitmapH;
        float bitmapW;
        float bitmapH2;
        float bitmapW2;
        int viewWidth2 = viewWidth - AndroidUtilities.dp(28.0f);
        int viewHeight2 = viewHeight - (AndroidUtilities.dp(214.0f) + ((Build.VERSION.SDK_INT < 21 || this.inBubbleMode) ? 0 : AndroidUtilities.statusBarHeight));
        Bitmap bitmap = this.bitmapToEdit;
        if (bitmap != null) {
            int i = this.orientation;
            if (i % 360 == 90 || i % 360 == 270) {
                bitmapW = (float) bitmap.getHeight();
                bitmapH = (float) this.bitmapToEdit.getWidth();
            } else {
                bitmapW = (float) bitmap.getWidth();
                bitmapH = (float) this.bitmapToEdit.getHeight();
            }
        } else {
            bitmapW = (float) this.textureView.getWidth();
            bitmapH = (float) this.textureView.getHeight();
        }
        float scaleX = ((float) viewWidth2) / bitmapW;
        float scaleY = ((float) viewHeight2) / bitmapH;
        if (scaleX > scaleY) {
            bitmapH2 = (float) viewHeight2;
            bitmapW2 = (float) ((int) Math.ceil((double) (bitmapW * scaleY)));
        } else {
            bitmapW2 = (float) viewWidth2;
            bitmapH2 = (float) ((int) Math.ceil((double) (bitmapH * scaleX)));
        }
        int bitmapX = (int) Math.ceil((double) (((((float) viewWidth2) - bitmapW2) / 2.0f) + ((float) AndroidUtilities.dp(14.0f))));
        int bitmapY = (int) Math.ceil((double) (((((float) viewHeight2) - bitmapH2) / 2.0f) + ((float) AndroidUtilities.dp(14.0f)) + ((float) ((Build.VERSION.SDK_INT < 21 || this.inBubbleMode) ? 0 : AndroidUtilities.statusBarHeight))));
        int width = (int) bitmapW2;
        int height = (int) bitmapH2;
        if (this.ownsTextureView) {
            FrameLayout.LayoutParams layoutParams = (FrameLayout.LayoutParams) this.textureView.getLayoutParams();
            layoutParams.leftMargin = bitmapX;
            layoutParams.topMargin = bitmapY;
            layoutParams.width = width;
            layoutParams.height = height;
        }
        this.curvesControl.setActualArea((float) bitmapX, (float) (bitmapY - ((Build.VERSION.SDK_INT < 21 || this.inBubbleMode) ? 0 : AndroidUtilities.statusBarHeight)), (float) width, (float) height);
        this.blurControl.setActualAreaSize((float) width, (float) height);
        ((FrameLayout.LayoutParams) this.blurControl.getLayoutParams()).height = AndroidUtilities.dp(38.0f) + viewHeight2;
        ((FrameLayout.LayoutParams) this.curvesControl.getLayoutParams()).height = AndroidUtilities.dp(28.0f) + viewHeight2;
        if (AndroidUtilities.isTablet()) {
            int total = AndroidUtilities.dp(86.0f) * 10;
            FrameLayout.LayoutParams layoutParams2 = (FrameLayout.LayoutParams) this.recyclerListView.getLayoutParams();
            if (total < viewWidth2) {
                layoutParams2.width = total;
                layoutParams2.leftMargin = (viewWidth2 - total) / 2;
                return;
            }
            layoutParams2.width = -1;
            layoutParams2.leftMargin = 0;
        }
    }

    /* access modifiers changed from: protected */
    public boolean drawChild(Canvas canvas, View child, long drawingTime) {
        boolean result = super.drawChild(canvas, child, drawingTime);
        if (this.paintingOverlay != null && child == this.textureView) {
            canvas.save();
            canvas.translate((float) this.textureView.getLeft(), (float) this.textureView.getTop());
            float scale = ((float) this.textureView.getMeasuredWidth()) / ((float) this.paintingOverlay.getMeasuredWidth());
            canvas.scale(scale, scale);
            this.paintingOverlay.draw(canvas);
            canvas.restore();
        }
        return result;
    }

    /* access modifiers changed from: protected */
    public void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        fixLayout(View.MeasureSpec.getSize(widthMeasureSpec), View.MeasureSpec.getSize(heightMeasureSpec));
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
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
        if (this.tintHighlightsColor == 0) {
            return 0.0f;
        }
        return 50.0f / 100.0f;
    }

    public float getTintShadowsIntensityValue() {
        if (this.tintShadowsColor == 0) {
            return 0.0f;
        }
        return 50.0f / 100.0f;
    }

    public float getSaturationValue() {
        float parameterValue = this.saturationValue / 100.0f;
        if (parameterValue > 0.0f) {
            parameterValue *= 1.05f;
        }
        return 1.0f + parameterValue;
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

    private int getThemedColor(String key) {
        Theme.ResourcesProvider resourcesProvider2 = this.resourcesProvider;
        Integer color = resourcesProvider2 != null ? resourcesProvider2.getColor(key) : null;
        return color != null ? color.intValue() : Theme.getColor(key);
    }

    public class ToolsAdapter extends RecyclerListView.SelectionAdapter {
        private Context mContext;

        public ToolsAdapter(Context context) {
            this.mContext = context;
        }

        public int getItemCount() {
            return PhotoFilterView.this.rowsCount;
        }

        public long getItemId(int i) {
            return (long) i;
        }

        /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r1v0, resolved type: org.telegram.ui.Cells.PhotoEditRadioCell} */
        /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r1v2, resolved type: org.telegram.ui.Cells.PhotoEditRadioCell} */
        /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r0v3, resolved type: org.telegram.ui.Cells.PhotoEditToolCell} */
        /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r1v4, resolved type: org.telegram.ui.Cells.PhotoEditRadioCell} */
        /* JADX WARNING: Multi-variable type inference failed */
        /* Code decompiled incorrectly, please refer to instructions dump. */
        public androidx.recyclerview.widget.RecyclerView.ViewHolder onCreateViewHolder(android.view.ViewGroup r4, int r5) {
            /*
                r3 = this;
                if (r5 != 0) goto L_0x0019
                org.telegram.ui.Cells.PhotoEditToolCell r0 = new org.telegram.ui.Cells.PhotoEditToolCell
                android.content.Context r1 = r3.mContext
                org.telegram.ui.Components.PhotoFilterView r2 = org.telegram.ui.Components.PhotoFilterView.this
                org.telegram.ui.ActionBar.Theme$ResourcesProvider r2 = r2.resourcesProvider
                r0.<init>(r1, r2)
                r1 = r0
                org.telegram.ui.Components.PhotoFilterView$ToolsAdapter$$ExternalSyntheticLambda1 r2 = new org.telegram.ui.Components.PhotoFilterView$ToolsAdapter$$ExternalSyntheticLambda1
                r2.<init>(r3)
                r0.setSeekBarDelegate(r2)
                goto L_0x0029
            L_0x0019:
                org.telegram.ui.Cells.PhotoEditRadioCell r0 = new org.telegram.ui.Cells.PhotoEditRadioCell
                android.content.Context r1 = r3.mContext
                r0.<init>(r1)
                r1 = r0
                org.telegram.ui.Components.PhotoFilterView$ToolsAdapter$$ExternalSyntheticLambda0 r0 = new org.telegram.ui.Components.PhotoFilterView$ToolsAdapter$$ExternalSyntheticLambda0
                r0.<init>(r3)
                r1.setOnClickListener(r0)
            L_0x0029:
                org.telegram.ui.Components.RecyclerListView$Holder r0 = new org.telegram.ui.Components.RecyclerListView$Holder
                r0.<init>(r1)
                return r0
            */
            throw new UnsupportedOperationException("Method not decompiled: org.telegram.ui.Components.PhotoFilterView.ToolsAdapter.onCreateViewHolder(android.view.ViewGroup, int):androidx.recyclerview.widget.RecyclerView$ViewHolder");
        }

        /* renamed from: lambda$onCreateViewHolder$0$org-telegram-ui-Components-PhotoFilterView$ToolsAdapter  reason: not valid java name */
        public /* synthetic */ void m1175x4dda4bfb(int i1, int progress) {
            if (i1 == PhotoFilterView.this.enhanceTool) {
                float unused = PhotoFilterView.this.enhanceValue = (float) progress;
            } else if (i1 == PhotoFilterView.this.highlightsTool) {
                float unused2 = PhotoFilterView.this.highlightsValue = (float) progress;
            } else if (i1 == PhotoFilterView.this.contrastTool) {
                float unused3 = PhotoFilterView.this.contrastValue = (float) progress;
            } else if (i1 == PhotoFilterView.this.exposureTool) {
                float unused4 = PhotoFilterView.this.exposureValue = (float) progress;
            } else if (i1 == PhotoFilterView.this.warmthTool) {
                float unused5 = PhotoFilterView.this.warmthValue = (float) progress;
            } else if (i1 == PhotoFilterView.this.saturationTool) {
                float unused6 = PhotoFilterView.this.saturationValue = (float) progress;
            } else if (i1 == PhotoFilterView.this.vignetteTool) {
                float unused7 = PhotoFilterView.this.vignetteValue = (float) progress;
            } else if (i1 == PhotoFilterView.this.shadowsTool) {
                float unused8 = PhotoFilterView.this.shadowsValue = (float) progress;
            } else if (i1 == PhotoFilterView.this.grainTool) {
                float unused9 = PhotoFilterView.this.grainValue = (float) progress;
            } else if (i1 == PhotoFilterView.this.sharpenTool) {
                float unused10 = PhotoFilterView.this.sharpenValue = (float) progress;
            } else if (i1 == PhotoFilterView.this.fadeTool) {
                float unused11 = PhotoFilterView.this.fadeValue = (float) progress;
            } else if (i1 == PhotoFilterView.this.softenSkinTool) {
                float unused12 = PhotoFilterView.this.softenSkinValue = (float) progress;
            }
            if (PhotoFilterView.this.eglThread != null) {
                PhotoFilterView.this.eglThread.requestRender(true);
            }
        }

        /* renamed from: lambda$onCreateViewHolder$1$org-telegram-ui-Components-PhotoFilterView$ToolsAdapter  reason: not valid java name */
        public /* synthetic */ void m1176x736e54fc(View v) {
            PhotoEditRadioCell cell = (PhotoEditRadioCell) v;
            if (((Integer) cell.getTag()).intValue() == PhotoFilterView.this.tintShadowsTool) {
                int unused = PhotoFilterView.this.tintShadowsColor = cell.getCurrentColor();
            } else {
                int unused2 = PhotoFilterView.this.tintHighlightsColor = cell.getCurrentColor();
            }
            if (PhotoFilterView.this.eglThread != null) {
                PhotoFilterView.this.eglThread.requestRender(false);
            }
        }

        public boolean isEnabled(RecyclerView.ViewHolder holder) {
            return false;
        }

        public void onBindViewHolder(RecyclerView.ViewHolder holder, int i) {
            switch (holder.getItemViewType()) {
                case 0:
                    PhotoEditToolCell cell = (PhotoEditToolCell) holder.itemView;
                    cell.setTag(Integer.valueOf(i));
                    if (i == PhotoFilterView.this.enhanceTool) {
                        cell.setIconAndTextAndValue(LocaleController.getString("Enhance", NUM), PhotoFilterView.this.enhanceValue, 0, 100);
                        return;
                    } else if (i == PhotoFilterView.this.highlightsTool) {
                        cell.setIconAndTextAndValue(LocaleController.getString("Highlights", NUM), PhotoFilterView.this.highlightsValue, -100, 100);
                        return;
                    } else if (i == PhotoFilterView.this.contrastTool) {
                        cell.setIconAndTextAndValue(LocaleController.getString("Contrast", NUM), PhotoFilterView.this.contrastValue, -100, 100);
                        return;
                    } else if (i == PhotoFilterView.this.exposureTool) {
                        cell.setIconAndTextAndValue(LocaleController.getString("Exposure", NUM), PhotoFilterView.this.exposureValue, -100, 100);
                        return;
                    } else if (i == PhotoFilterView.this.warmthTool) {
                        cell.setIconAndTextAndValue(LocaleController.getString("Warmth", NUM), PhotoFilterView.this.warmthValue, -100, 100);
                        return;
                    } else if (i == PhotoFilterView.this.saturationTool) {
                        cell.setIconAndTextAndValue(LocaleController.getString("Saturation", NUM), PhotoFilterView.this.saturationValue, -100, 100);
                        return;
                    } else if (i == PhotoFilterView.this.vignetteTool) {
                        cell.setIconAndTextAndValue(LocaleController.getString("Vignette", NUM), PhotoFilterView.this.vignetteValue, 0, 100);
                        return;
                    } else if (i == PhotoFilterView.this.shadowsTool) {
                        cell.setIconAndTextAndValue(LocaleController.getString("Shadows", NUM), PhotoFilterView.this.shadowsValue, -100, 100);
                        return;
                    } else if (i == PhotoFilterView.this.grainTool) {
                        cell.setIconAndTextAndValue(LocaleController.getString("Grain", NUM), PhotoFilterView.this.grainValue, 0, 100);
                        return;
                    } else if (i == PhotoFilterView.this.sharpenTool) {
                        cell.setIconAndTextAndValue(LocaleController.getString("Sharpen", NUM), PhotoFilterView.this.sharpenValue, 0, 100);
                        return;
                    } else if (i == PhotoFilterView.this.fadeTool) {
                        cell.setIconAndTextAndValue(LocaleController.getString("Fade", NUM), PhotoFilterView.this.fadeValue, 0, 100);
                        return;
                    } else if (i == PhotoFilterView.this.softenSkinTool) {
                        cell.setIconAndTextAndValue(LocaleController.getString("SoftenSkin", NUM), PhotoFilterView.this.softenSkinValue, 0, 100);
                        return;
                    } else {
                        return;
                    }
                case 1:
                    PhotoEditRadioCell cell2 = (PhotoEditRadioCell) holder.itemView;
                    cell2.setTag(Integer.valueOf(i));
                    if (i == PhotoFilterView.this.tintShadowsTool) {
                        cell2.setIconAndTextAndValue(LocaleController.getString("TintShadows", NUM), 0, PhotoFilterView.this.tintShadowsColor);
                        return;
                    } else if (i == PhotoFilterView.this.tintHighlightsTool) {
                        cell2.setIconAndTextAndValue(LocaleController.getString("TintHighlights", NUM), 0, PhotoFilterView.this.tintHighlightsColor);
                        return;
                    } else {
                        return;
                    }
                default:
                    return;
            }
        }

        public int getItemViewType(int position) {
            if (position == PhotoFilterView.this.tintShadowsTool || position == PhotoFilterView.this.tintHighlightsTool) {
                return 1;
            }
            return 0;
        }
    }
}
