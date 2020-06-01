package org.telegram.ui.Components;

import android.annotation.SuppressLint;
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
import org.telegram.ui.Cells.PhotoEditRadioCell;
import org.telegram.ui.Cells.PhotoEditToolCell;
import org.telegram.ui.Components.FilterShaders;
import org.telegram.ui.Components.PhotoFilterBlurControl;
import org.telegram.ui.Components.PhotoFilterCurvesControl;
import org.telegram.ui.Components.PhotoFilterView;
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

    /* JADX INFO: super call moved to the top of the method (can break code semantics) */
    public PhotoFilterView(Context context, VideoEditTextureView videoEditTextureView, Bitmap bitmap, int i, MediaController.SavedFilterState savedFilterState, PaintingOverlay paintingOverlay2) {
        super(context);
        Context context2 = context;
        VideoEditTextureView videoEditTextureView2 = videoEditTextureView;
        MediaController.SavedFilterState savedFilterState2 = savedFilterState;
        this.paintingOverlay = paintingOverlay2;
        this.rowsCount = 0;
        int i2 = 0 + 1;
        this.rowsCount = i2;
        this.enhanceTool = 0;
        int i3 = i2 + 1;
        this.rowsCount = i3;
        this.exposureTool = i2;
        int i4 = i3 + 1;
        this.rowsCount = i4;
        this.contrastTool = i3;
        int i5 = i4 + 1;
        this.rowsCount = i5;
        this.saturationTool = i4;
        int i6 = i5 + 1;
        this.rowsCount = i6;
        this.warmthTool = i5;
        int i7 = i6 + 1;
        this.rowsCount = i7;
        this.fadeTool = i6;
        int i8 = i7 + 1;
        this.rowsCount = i8;
        this.highlightsTool = i7;
        int i9 = i8 + 1;
        this.rowsCount = i9;
        this.shadowsTool = i8;
        int i10 = i9 + 1;
        this.rowsCount = i10;
        this.vignetteTool = i9;
        if (videoEditTextureView2 == null) {
            int i11 = i10 + 1;
            this.rowsCount = i11;
            this.grainTool = i10;
            this.rowsCount = i11 + 1;
            this.sharpenTool = i11;
        } else {
            this.grainTool = -1;
            this.sharpenTool = -1;
        }
        int i12 = this.rowsCount;
        int i13 = i12 + 1;
        this.rowsCount = i13;
        this.tintShadowsTool = i12;
        this.rowsCount = i13 + 1;
        this.tintHighlightsTool = i13;
        if (savedFilterState2 != null) {
            this.enhanceValue = savedFilterState2.enhanceValue;
            this.exposureValue = savedFilterState2.exposureValue;
            this.contrastValue = savedFilterState2.contrastValue;
            this.warmthValue = savedFilterState2.warmthValue;
            this.saturationValue = savedFilterState2.saturationValue;
            this.fadeValue = savedFilterState2.fadeValue;
            this.tintShadowsColor = savedFilterState2.tintShadowsColor;
            this.tintHighlightsColor = savedFilterState2.tintHighlightsColor;
            this.highlightsValue = savedFilterState2.highlightsValue;
            this.shadowsValue = savedFilterState2.shadowsValue;
            this.vignetteValue = savedFilterState2.vignetteValue;
            this.grainValue = savedFilterState2.grainValue;
            this.blurType = savedFilterState2.blurType;
            this.sharpenValue = savedFilterState2.sharpenValue;
            this.curvesToolValue = savedFilterState2.curvesToolValue;
            this.blurExcludeSize = savedFilterState2.blurExcludeSize;
            this.blurExcludePoint = savedFilterState2.blurExcludePoint;
            this.blurExcludeBlurSize = savedFilterState2.blurExcludeBlurSize;
            this.blurAngle = savedFilterState2.blurAngle;
            this.lastState = savedFilterState2;
        } else {
            this.curvesToolValue = new CurvesToolValue();
            this.blurExcludeSize = 0.35f;
            this.blurExcludePoint = new Point(0.5f, 0.5f);
            this.blurExcludeBlurSize = 0.15f;
            this.blurAngle = 1.5707964f;
        }
        this.bitmapToEdit = bitmap;
        this.orientation = i;
        if (videoEditTextureView2 != null) {
            this.textureView = videoEditTextureView2;
            videoEditTextureView2.setDelegate(new VideoEditTextureView.VideoEditTextureViewDelegate() {
                public final void onEGLThreadAvailable(FilterGLThread filterGLThread) {
                    PhotoFilterView.this.lambda$new$0$PhotoFilterView(filterGLThread);
                }
            });
        } else {
            this.ownsTextureView = true;
            TextureView textureView2 = new TextureView(context2);
            this.textureView = textureView2;
            addView(textureView2, LayoutHelper.createFrame(-1, -1, 51));
            this.textureView.setVisibility(4);
            this.textureView.setSurfaceTextureListener(new TextureView.SurfaceTextureListener() {
                public void onSurfaceTextureUpdated(SurfaceTexture surfaceTexture) {
                }

                public void onSurfaceTextureAvailable(SurfaceTexture surfaceTexture, int i, int i2) {
                    if (PhotoFilterView.this.eglThread == null && surfaceTexture != null) {
                        FilterGLThread unused = PhotoFilterView.this.eglThread = new FilterGLThread(surfaceTexture, PhotoFilterView.this.bitmapToEdit, PhotoFilterView.this.orientation);
                        PhotoFilterView.this.eglThread.setFilterGLThreadDelegate(PhotoFilterView.this);
                        PhotoFilterView.this.eglThread.setSurfaceTextureSize(i, i2);
                        PhotoFilterView.this.eglThread.requestRender(true, true, false);
                    }
                }

                public void onSurfaceTextureSizeChanged(SurfaceTexture surfaceTexture, int i, int i2) {
                    if (PhotoFilterView.this.eglThread != null) {
                        PhotoFilterView.this.eglThread.setSurfaceTextureSize(i, i2);
                        PhotoFilterView.this.eglThread.requestRender(false, true, false);
                        PhotoFilterView.this.eglThread.postRunnable(new Runnable() {
                            public final void run() {
                                PhotoFilterView.AnonymousClass1.this.lambda$onSurfaceTextureSizeChanged$0$PhotoFilterView$1();
                            }
                        });
                    }
                }

                public /* synthetic */ void lambda$onSurfaceTextureSizeChanged$0$PhotoFilterView$1() {
                    if (PhotoFilterView.this.eglThread != null) {
                        PhotoFilterView.this.eglThread.requestRender(false, true, false);
                    }
                }

                public boolean onSurfaceTextureDestroyed(SurfaceTexture surfaceTexture) {
                    if (PhotoFilterView.this.eglThread == null) {
                        return true;
                    }
                    PhotoFilterView.this.eglThread.shutdown();
                    FilterGLThread unused = PhotoFilterView.this.eglThread = null;
                    return true;
                }
            });
        }
        PhotoFilterBlurControl photoFilterBlurControl = new PhotoFilterBlurControl(context2);
        this.blurControl = photoFilterBlurControl;
        photoFilterBlurControl.setVisibility(4);
        addView(this.blurControl, LayoutHelper.createFrame(-1, -1, 51));
        this.blurControl.setDelegate(new PhotoFilterBlurControl.PhotoFilterLinearBlurControlDelegate() {
            public final void valueChanged(Point point, float f, float f2, float f3) {
                PhotoFilterView.this.lambda$new$1$PhotoFilterView(point, f, f2, f3);
            }
        });
        PhotoFilterCurvesControl photoFilterCurvesControl = new PhotoFilterCurvesControl(context2, this.curvesToolValue);
        this.curvesControl = photoFilterCurvesControl;
        photoFilterCurvesControl.setDelegate(new PhotoFilterCurvesControl.PhotoFilterCurvesControlDelegate() {
            public final void valueChanged() {
                PhotoFilterView.this.lambda$new$2$PhotoFilterView();
            }
        });
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
        this.doneTextView.setTextColor(Theme.getColor("dialogFloatingButton"));
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
        this.tuneItem.setColorFilter(new PorterDuffColorFilter(Theme.getColor("dialogFloatingButton"), PorterDuff.Mode.MULTIPLY));
        this.tuneItem.setBackgroundDrawable(Theme.createSelectorDrawable(NUM));
        linearLayout.addView(this.tuneItem, LayoutHelper.createLinear(56, 48));
        this.tuneItem.setOnClickListener(new View.OnClickListener() {
            public final void onClick(View view) {
                PhotoFilterView.this.lambda$new$3$PhotoFilterView(view);
            }
        });
        ImageView imageView2 = new ImageView(context2);
        this.blurItem = imageView2;
        imageView2.setScaleType(ImageView.ScaleType.CENTER);
        this.blurItem.setImageResource(NUM);
        this.blurItem.setBackgroundDrawable(Theme.createSelectorDrawable(NUM));
        linearLayout.addView(this.blurItem, LayoutHelper.createLinear(56, 48));
        this.blurItem.setOnClickListener(new View.OnClickListener() {
            public final void onClick(View view) {
                PhotoFilterView.this.lambda$new$4$PhotoFilterView(view);
            }
        });
        if (videoEditTextureView2 != null) {
            this.blurItem.setVisibility(8);
        }
        ImageView imageView3 = new ImageView(context2);
        this.curveItem = imageView3;
        imageView3.setScaleType(ImageView.ScaleType.CENTER);
        this.curveItem.setImageResource(NUM);
        this.curveItem.setBackgroundDrawable(Theme.createSelectorDrawable(NUM));
        linearLayout.addView(this.curveItem, LayoutHelper.createLinear(56, 48));
        this.curveItem.setOnClickListener(new View.OnClickListener() {
            public final void onClick(View view) {
                PhotoFilterView.this.lambda$new$5$PhotoFilterView(view);
            }
        });
        this.recyclerListView = new RecyclerListView(context2);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(context2);
        linearLayoutManager.setOrientation(1);
        this.recyclerListView.setLayoutManager(linearLayoutManager);
        this.recyclerListView.setClipToPadding(false);
        this.recyclerListView.setOverScrollMode(2);
        this.recyclerListView.setAdapter(new ToolsAdapter(context2));
        this.toolsView.addView(this.recyclerListView, LayoutHelper.createFrame(-1, 120, 51));
        FrameLayout frameLayout3 = new FrameLayout(context2);
        this.curveLayout = frameLayout3;
        frameLayout3.setVisibility(4);
        this.toolsView.addView(this.curveLayout, LayoutHelper.createFrame(-1, 78.0f, 1, 0.0f, 40.0f, 0.0f, 0.0f));
        LinearLayout linearLayout2 = new LinearLayout(context2);
        linearLayout2.setOrientation(0);
        this.curveLayout.addView(linearLayout2, LayoutHelper.createFrame(-2, -2, 1));
        int i14 = 0;
        while (i14 < 4) {
            FrameLayout frameLayout4 = new FrameLayout(context2);
            frameLayout4.setTag(Integer.valueOf(i14));
            this.curveRadioButton[i14] = new RadioButton(context2);
            this.curveRadioButton[i14].setSize(AndroidUtilities.dp(20.0f));
            frameLayout4.addView(this.curveRadioButton[i14], LayoutHelper.createFrame(30, 30, 49));
            TextView textView3 = new TextView(context2);
            textView3.setTextSize(1, 12.0f);
            textView3.setGravity(16);
            if (i14 == 0) {
                String string = LocaleController.getString("CurvesAll", NUM);
                textView3.setText(string.substring(0, 1).toUpperCase() + string.substring(1).toLowerCase());
                textView3.setTextColor(-1);
                this.curveRadioButton[i14].setColor(-1, -1);
            } else if (i14 == 1) {
                String string2 = LocaleController.getString("CurvesRed", NUM);
                textView3.setText(string2.substring(0, 1).toUpperCase() + string2.substring(1).toLowerCase());
                textView3.setTextColor(-1684147);
                this.curveRadioButton[i14].setColor(-1684147, -1684147);
            } else if (i14 == 2) {
                String string3 = LocaleController.getString("CurvesGreen", NUM);
                textView3.setText(string3.substring(0, 1).toUpperCase() + string3.substring(1).toLowerCase());
                textView3.setTextColor(-10831009);
                this.curveRadioButton[i14].setColor(-10831009, -10831009);
            } else if (i14 == 3) {
                String string4 = LocaleController.getString("CurvesBlue", NUM);
                textView3.setText(string4.substring(0, 1).toUpperCase() + string4.substring(1).toLowerCase());
                textView3.setTextColor(-12734994);
                this.curveRadioButton[i14].setColor(-12734994, -12734994);
            }
            frameLayout4.addView(textView3, LayoutHelper.createFrame(-2, -2.0f, 49, 0.0f, 38.0f, 0.0f, 0.0f));
            linearLayout2.addView(frameLayout4, LayoutHelper.createLinear(-2, -2, i14 == 0 ? 0.0f : 30.0f, 0.0f, 0.0f, 0.0f));
            frameLayout4.setOnClickListener(new View.OnClickListener() {
                public final void onClick(View view) {
                    PhotoFilterView.this.lambda$new$6$PhotoFilterView(view);
                }
            });
            i14++;
        }
        FrameLayout frameLayout5 = new FrameLayout(context2);
        this.blurLayout = frameLayout5;
        frameLayout5.setVisibility(4);
        this.toolsView.addView(this.blurLayout, LayoutHelper.createFrame(280, 60.0f, 1, 0.0f, 40.0f, 0.0f, 0.0f));
        TextView textView4 = new TextView(context2);
        this.blurOffButton = textView4;
        textView4.setCompoundDrawablePadding(AndroidUtilities.dp(2.0f));
        this.blurOffButton.setTextSize(1, 13.0f);
        this.blurOffButton.setGravity(1);
        this.blurOffButton.setText(LocaleController.getString("BlurOff", NUM));
        this.blurLayout.addView(this.blurOffButton, LayoutHelper.createFrame(80, 60.0f));
        this.blurOffButton.setOnClickListener(new View.OnClickListener() {
            public final void onClick(View view) {
                PhotoFilterView.this.lambda$new$7$PhotoFilterView(view);
            }
        });
        TextView textView5 = new TextView(context2);
        this.blurRadialButton = textView5;
        textView5.setCompoundDrawablePadding(AndroidUtilities.dp(2.0f));
        this.blurRadialButton.setTextSize(1, 13.0f);
        this.blurRadialButton.setGravity(1);
        this.blurRadialButton.setText(LocaleController.getString("BlurRadial", NUM));
        this.blurLayout.addView(this.blurRadialButton, LayoutHelper.createFrame(80, 80.0f, 51, 100.0f, 0.0f, 0.0f, 0.0f));
        this.blurRadialButton.setOnClickListener(new View.OnClickListener() {
            public final void onClick(View view) {
                PhotoFilterView.this.lambda$new$8$PhotoFilterView(view);
            }
        });
        TextView textView6 = new TextView(context2);
        this.blurLinearButton = textView6;
        textView6.setCompoundDrawablePadding(AndroidUtilities.dp(2.0f));
        this.blurLinearButton.setTextSize(1, 13.0f);
        this.blurLinearButton.setGravity(1);
        this.blurLinearButton.setText(LocaleController.getString("BlurLinear", NUM));
        this.blurLayout.addView(this.blurLinearButton, LayoutHelper.createFrame(80, 80.0f, 51, 200.0f, 0.0f, 0.0f, 0.0f));
        this.blurLinearButton.setOnClickListener(new View.OnClickListener() {
            public final void onClick(View view) {
                PhotoFilterView.this.lambda$new$9$PhotoFilterView(view);
            }
        });
        updateSelectedBlurType();
        if (Build.VERSION.SDK_INT >= 21) {
            if (this.ownsTextureView) {
                ((FrameLayout.LayoutParams) this.textureView.getLayoutParams()).topMargin = AndroidUtilities.statusBarHeight;
            }
            ((FrameLayout.LayoutParams) this.curvesControl.getLayoutParams()).topMargin = AndroidUtilities.statusBarHeight;
        }
    }

    public /* synthetic */ void lambda$new$0$PhotoFilterView(FilterGLThread filterGLThread) {
        this.eglThread = filterGLThread;
        filterGLThread.setFilterGLThreadDelegate(this);
    }

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

    public /* synthetic */ void lambda$new$2$PhotoFilterView() {
        FilterGLThread filterGLThread = this.eglThread;
        if (filterGLThread != null) {
            filterGLThread.requestRender(false);
        }
    }

    public /* synthetic */ void lambda$new$3$PhotoFilterView(View view) {
        this.selectedTool = 0;
        this.tuneItem.setColorFilter(new PorterDuffColorFilter(Theme.getColor("dialogFloatingButton"), PorterDuff.Mode.MULTIPLY));
        this.blurItem.setColorFilter((ColorFilter) null);
        this.curveItem.setColorFilter((ColorFilter) null);
        switchMode();
    }

    public /* synthetic */ void lambda$new$4$PhotoFilterView(View view) {
        this.selectedTool = 1;
        this.tuneItem.setColorFilter((ColorFilter) null);
        this.blurItem.setColorFilter(new PorterDuffColorFilter(Theme.getColor("dialogFloatingButton"), PorterDuff.Mode.MULTIPLY));
        this.curveItem.setColorFilter((ColorFilter) null);
        switchMode();
    }

    public /* synthetic */ void lambda$new$5$PhotoFilterView(View view) {
        this.selectedTool = 2;
        this.tuneItem.setColorFilter((ColorFilter) null);
        this.blurItem.setColorFilter((ColorFilter) null);
        this.curveItem.setColorFilter(new PorterDuffColorFilter(Theme.getColor("dialogFloatingButton"), PorterDuff.Mode.MULTIPLY));
        switchMode();
    }

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

    public /* synthetic */ void lambda$new$7$PhotoFilterView(View view) {
        this.blurType = 0;
        updateSelectedBlurType();
        this.blurControl.setVisibility(4);
        FilterGLThread filterGLThread = this.eglThread;
        if (filterGLThread != null) {
            filterGLThread.requestRender(false);
        }
    }

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
        int dp2 = i2 - (AndroidUtilities.dp(214.0f) + (Build.VERSION.SDK_INT >= 21 ? AndroidUtilities.statusBarHeight : 0));
        Bitmap bitmap = this.bitmapToEdit;
        if (bitmap != null) {
            int i4 = this.orientation;
            if (i4 % 360 == 90 || i4 % 360 == 270) {
                f = (float) this.bitmapToEdit.getHeight();
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
        int i5 = (int) f3;
        int i6 = (int) f2;
        if (this.ownsTextureView) {
            FrameLayout.LayoutParams layoutParams = (FrameLayout.LayoutParams) this.textureView.getLayoutParams();
            layoutParams.leftMargin = ceil;
            layoutParams.topMargin = ceil2;
            layoutParams.width = i5;
            layoutParams.height = i6;
        }
        float f9 = (float) i5;
        float var_ = (float) i6;
        this.curvesControl.setActualArea((float) ceil, (float) (ceil2 - (Build.VERSION.SDK_INT >= 21 ? AndroidUtilities.statusBarHeight : 0)), f9, var_);
        this.blurControl.setActualAreaSize(f9, var_);
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
