package org.telegram.ui.Charts;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.AnimatorSet;
import android.animation.ValueAnimator;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.Rect;
import android.graphics.RectF;
import android.os.Build;
import android.os.VibrationEffect;
import android.os.Vibrator;
import android.text.TextPaint;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewConfiguration;
import androidx.interpolator.view.animation.FastOutSlowInInterpolator;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import org.telegram.messenger.AndroidUtilities;
import org.telegram.ui.ActionBar.Theme;
import org.telegram.ui.Charts.ChartPickerDelegate;
import org.telegram.ui.Charts.data.ChartData;
import org.telegram.ui.Charts.view_data.ChartBottomSignatureData;
import org.telegram.ui.Charts.view_data.ChartHeaderView;
import org.telegram.ui.Charts.view_data.ChartHorizontalLinesData;
import org.telegram.ui.Charts.view_data.LegendSignatureView;
import org.telegram.ui.Charts.view_data.LineViewData;
import org.telegram.ui.Charts.view_data.TransitionParams;
import org.telegram.ui.Components.CubicBezierInterpolator;
/* loaded from: classes3.dex */
public abstract class BaseChartView<T extends ChartData, L extends LineViewData> extends View implements ChartPickerDelegate.Listener {
    protected static final boolean ANIMATE_PICKER_SIZES;
    public static FastOutSlowInInterpolator INTERPOLATOR;
    public static final boolean USE_LINES;
    ValueAnimator alphaAnimator;
    ValueAnimator alphaBottomAnimator;
    public boolean animateLegentTo;
    float animateToMaxHeight;
    float animateToMinHeight;
    protected float animatedToPickerMaxHeight;
    protected float animatedToPickerMinHeight;
    private Bitmap bottomChartBitmap;
    private Canvas bottomChartCanvas;
    ArrayList<ChartBottomSignatureData> bottomSignatureDate;
    protected int bottomSignatureOffset;
    Paint bottomSignaturePaint;
    float bottomSignaturePaintAlpha;
    protected boolean canCaptureChartSelection;
    long capturedTime;
    int capturedX;
    int capturedY;
    int chartActiveLineAlpha;
    public RectF chartArea;
    int chartBottom;
    protected boolean chartCaptured;
    T chartData;
    public float chartEnd;
    public float chartFullWidth;
    ChartHeaderView chartHeaderView;
    public float chartStart;
    public float chartWidth;
    ChartBottomSignatureData currentBottomSignatures;
    public float currentMaxHeight;
    public float currentMinHeight;
    protected DateSelectionListener dateSelectionListener;
    protected boolean drawPointOnSelection;
    Paint emptyPaint;
    public boolean enabled;
    int endXIndex;
    private ValueAnimator.AnimatorUpdateListener heightUpdateListener;
    int hintLinePaintAlpha;
    ArrayList<ChartHorizontalLinesData> horizontalLines;
    boolean invalidatePickerChart;
    boolean landscape;
    int lastH;
    long lastTime;
    int lastW;
    int lastX;
    int lastY;
    public boolean legendShowing;
    public LegendSignatureView legendSignatureView;
    Paint linePaint;
    public ArrayList<L> lines;
    Animator maxValueAnimator;
    private ValueAnimator.AnimatorUpdateListener minHeightUpdateListener;
    private float minMaxUpdateStep;
    Path pathTmp;
    Animator pickerAnimator;
    public ChartPickerDelegate pickerDelegate;
    private ValueAnimator.AnimatorUpdateListener pickerHeightUpdateListener;
    protected float pickerMaxHeight;
    protected float pickerMinHeight;
    private ValueAnimator.AnimatorUpdateListener pickerMinHeightUpdateListener;
    Rect pickerRect;
    Paint pickerSelectorPaint;
    public float pickerWidth;
    public int pikerHeight;
    boolean postTransition;
    Paint ripplePaint;
    protected int selectedIndex;
    Paint selectedLinePaint;
    public float selectionA;
    ValueAnimator selectionAnimator;
    private ValueAnimator.AnimatorUpdateListener selectionAnimatorListener;
    Paint selectionBackgroundPaint;
    private Animator.AnimatorListener selectorAnimatorEndListener;
    public SharedUiComponents sharedUiComponents;
    Paint signaturePaint;
    Paint signaturePaint2;
    float signaturePaintAlpha;
    private float startFromMax;
    private float startFromMaxH;
    private float startFromMin;
    private float startFromMinH;
    int startXIndex;
    boolean superDraw;
    float thresholdMaxHeight;
    protected int tmpI;
    protected int tmpN;
    private final int touchSlop;
    public int transitionMode;
    public TransitionParams transitionParams;
    Paint unactiveBottomChartPaint;
    boolean useAlphaSignature;
    protected boolean useMinHeight;
    VibrationEffect vibrationEffect;
    Paint whiteLinePaint;
    public static final float HORIZONTAL_PADDING = AndroidUtilities.dpf2(16.0f);
    private static final float SELECTED_LINE_WIDTH = AndroidUtilities.dpf2(1.5f);
    private static final float SIGNATURE_TEXT_SIZE = AndroidUtilities.dpf2(12.0f);
    public static final int SIGNATURE_TEXT_HEIGHT = AndroidUtilities.dp(18.0f);
    private static final int BOTTOM_SIGNATURE_TEXT_HEIGHT = AndroidUtilities.dp(14.0f);
    public static final int BOTTOM_SIGNATURE_START_ALPHA = AndroidUtilities.dp(10.0f);
    protected static final int PICKER_PADDING = AndroidUtilities.dp(16.0f);
    private static final int PICKER_CAPTURE_WIDTH = AndroidUtilities.dp(24.0f);
    private static final int LANDSCAPE_END_PADDING = AndroidUtilities.dp(16.0f);
    private static final int BOTTOM_SIGNATURE_OFFSET = AndroidUtilities.dp(10.0f);
    private static final int DP_12 = AndroidUtilities.dp(12.0f);
    private static final int DP_6 = AndroidUtilities.dp(6.0f);
    private static final int DP_5 = AndroidUtilities.dp(5.0f);
    private static final int DP_2 = AndroidUtilities.dp(2.0f);
    private static final int DP_1 = AndroidUtilities.dp(1.0f);

    /* loaded from: classes3.dex */
    public interface DateSelectionListener {
        void onDateSelected(long j);
    }

    /* renamed from: createLineViewData */
    public abstract L mo1036createLineViewData(ChartData.Line line);

    protected void drawChart(Canvas canvas) {
    }

    protected void drawPickerChart(Canvas canvas) {
    }

    public void fillTransitionParams(TransitionParams transitionParams) {
    }

    protected void onActionUp() {
    }

    static {
        int i = Build.VERSION.SDK_INT;
        boolean z = true;
        USE_LINES = i < 28;
        if (i <= 21) {
            z = false;
        }
        ANIMATE_PICKER_SIZES = z;
        INTERPOLATOR = new FastOutSlowInInterpolator();
    }

    /* JADX INFO: Access modifiers changed from: private */
    public /* synthetic */ void lambda$new$0(ValueAnimator valueAnimator) {
        this.currentMaxHeight = ((Float) valueAnimator.getAnimatedValue()).floatValue();
        invalidate();
    }

    /* JADX INFO: Access modifiers changed from: private */
    public /* synthetic */ void lambda$new$1(ValueAnimator valueAnimator) {
        this.currentMinHeight = ((Float) valueAnimator.getAnimatedValue()).floatValue();
        invalidate();
    }

    public BaseChartView(Context context) {
        super(context);
        this.horizontalLines = new ArrayList<>(10);
        this.bottomSignatureDate = new ArrayList<>(25);
        this.lines = new ArrayList<>();
        this.drawPointOnSelection = true;
        this.currentMaxHeight = 250.0f;
        this.currentMinHeight = 0.0f;
        this.animateToMaxHeight = 0.0f;
        this.animateToMinHeight = 0.0f;
        this.thresholdMaxHeight = 0.0f;
        this.invalidatePickerChart = true;
        this.landscape = false;
        this.enabled = true;
        this.emptyPaint = new Paint();
        this.linePaint = new Paint();
        this.selectedLinePaint = new Paint();
        this.signaturePaint = new TextPaint(1);
        this.signaturePaint2 = new TextPaint(1);
        this.bottomSignaturePaint = new TextPaint(1);
        this.pickerSelectorPaint = new Paint(1);
        this.unactiveBottomChartPaint = new Paint();
        this.selectionBackgroundPaint = new Paint(1);
        this.ripplePaint = new Paint(1);
        this.whiteLinePaint = new Paint(1);
        this.pickerRect = new Rect();
        this.pathTmp = new Path();
        this.postTransition = false;
        this.pickerDelegate = new ChartPickerDelegate(this);
        this.chartCaptured = false;
        this.selectedIndex = -1;
        this.legendShowing = false;
        this.selectionA = 0.0f;
        this.superDraw = false;
        this.useAlphaSignature = false;
        this.transitionMode = 0;
        this.pikerHeight = AndroidUtilities.dp(46.0f);
        this.chartArea = new RectF();
        this.pickerHeightUpdateListener = new ValueAnimator.AnimatorUpdateListener() { // from class: org.telegram.ui.Charts.BaseChartView.1
            @Override // android.animation.ValueAnimator.AnimatorUpdateListener
            public void onAnimationUpdate(ValueAnimator valueAnimator) {
                BaseChartView.this.pickerMaxHeight = ((Float) valueAnimator.getAnimatedValue()).floatValue();
                BaseChartView baseChartView = BaseChartView.this;
                baseChartView.invalidatePickerChart = true;
                baseChartView.invalidate();
            }
        };
        this.pickerMinHeightUpdateListener = new ValueAnimator.AnimatorUpdateListener() { // from class: org.telegram.ui.Charts.BaseChartView.2
            @Override // android.animation.ValueAnimator.AnimatorUpdateListener
            public void onAnimationUpdate(ValueAnimator valueAnimator) {
                BaseChartView.this.pickerMinHeight = ((Float) valueAnimator.getAnimatedValue()).floatValue();
                BaseChartView baseChartView = BaseChartView.this;
                baseChartView.invalidatePickerChart = true;
                baseChartView.invalidate();
            }
        };
        this.heightUpdateListener = new ValueAnimator.AnimatorUpdateListener() { // from class: org.telegram.ui.Charts.BaseChartView$$ExternalSyntheticLambda1
            @Override // android.animation.ValueAnimator.AnimatorUpdateListener
            public final void onAnimationUpdate(ValueAnimator valueAnimator) {
                BaseChartView.this.lambda$new$0(valueAnimator);
            }
        };
        this.minHeightUpdateListener = new ValueAnimator.AnimatorUpdateListener() { // from class: org.telegram.ui.Charts.BaseChartView$$ExternalSyntheticLambda0
            @Override // android.animation.ValueAnimator.AnimatorUpdateListener
            public final void onAnimationUpdate(ValueAnimator valueAnimator) {
                BaseChartView.this.lambda$new$1(valueAnimator);
            }
        };
        this.selectionAnimatorListener = new ValueAnimator.AnimatorUpdateListener() { // from class: org.telegram.ui.Charts.BaseChartView.3
            @Override // android.animation.ValueAnimator.AnimatorUpdateListener
            public void onAnimationUpdate(ValueAnimator valueAnimator) {
                BaseChartView.this.selectionA = ((Float) valueAnimator.getAnimatedValue()).floatValue();
                BaseChartView baseChartView = BaseChartView.this;
                baseChartView.legendSignatureView.setAlpha(baseChartView.selectionA);
                BaseChartView.this.invalidate();
            }
        };
        this.selectorAnimatorEndListener = new AnimatorListenerAdapter() { // from class: org.telegram.ui.Charts.BaseChartView.4
            @Override // android.animation.AnimatorListenerAdapter, android.animation.Animator.AnimatorListener
            public void onAnimationEnd(Animator animator) {
                super.onAnimationEnd(animator);
                BaseChartView baseChartView = BaseChartView.this;
                if (!baseChartView.animateLegentTo) {
                    baseChartView.legendShowing = false;
                    baseChartView.legendSignatureView.setVisibility(8);
                    BaseChartView.this.invalidate();
                }
                BaseChartView.this.postTransition = false;
            }
        };
        this.useMinHeight = false;
        this.lastW = 0;
        this.lastH = 0;
        this.lastTime = 0L;
        this.animateLegentTo = false;
        init();
        this.touchSlop = ViewConfiguration.get(context).getScaledTouchSlop();
    }

    /* JADX INFO: Access modifiers changed from: protected */
    public void init() {
        this.linePaint.setStrokeWidth(1.0f);
        this.selectedLinePaint.setStrokeWidth(SELECTED_LINE_WIDTH);
        Paint paint = this.signaturePaint;
        float f = SIGNATURE_TEXT_SIZE;
        paint.setTextSize(f);
        this.signaturePaint2.setTextSize(f);
        this.signaturePaint2.setTextAlign(Paint.Align.RIGHT);
        this.bottomSignaturePaint.setTextSize(f);
        this.bottomSignaturePaint.setTextAlign(Paint.Align.CENTER);
        this.selectionBackgroundPaint.setStrokeWidth(AndroidUtilities.dpf2(6.0f));
        this.selectionBackgroundPaint.setStrokeCap(Paint.Cap.ROUND);
        setLayerType(2, null);
        setWillNotDraw(false);
        LegendSignatureView createLegendView = createLegendView();
        this.legendSignatureView = createLegendView;
        createLegendView.setVisibility(8);
        this.whiteLinePaint.setColor(-1);
        this.whiteLinePaint.setStrokeWidth(AndroidUtilities.dpf2(3.0f));
        this.whiteLinePaint.setStrokeCap(Paint.Cap.ROUND);
        updateColors();
    }

    protected LegendSignatureView createLegendView() {
        return new LegendSignatureView(getContext());
    }

    public void updateColors() {
        if (this.useAlphaSignature) {
            this.signaturePaint.setColor(Theme.getColor("statisticChartSignatureAlpha"));
        } else {
            this.signaturePaint.setColor(Theme.getColor("statisticChartSignature"));
        }
        this.bottomSignaturePaint.setColor(Theme.getColor("statisticChartSignature"));
        this.linePaint.setColor(Theme.getColor("statisticChartHintLine"));
        this.selectedLinePaint.setColor(Theme.getColor("statisticChartActiveLine"));
        this.pickerSelectorPaint.setColor(Theme.getColor("statisticChartActivePickerChart"));
        this.unactiveBottomChartPaint.setColor(Theme.getColor("statisticChartInactivePickerChart"));
        this.selectionBackgroundPaint.setColor(Theme.getColor("windowBackgroundWhite"));
        this.ripplePaint.setColor(Theme.getColor("statisticChartRipple"));
        this.legendSignatureView.recolor();
        this.hintLinePaintAlpha = this.linePaint.getAlpha();
        this.chartActiveLineAlpha = this.selectedLinePaint.getAlpha();
        this.signaturePaintAlpha = this.signaturePaint.getAlpha() / 255.0f;
        this.bottomSignaturePaintAlpha = this.bottomSignaturePaint.getAlpha() / 255.0f;
        Iterator<L> it = this.lines.iterator();
        while (it.hasNext()) {
            it.next().updateColors();
        }
        if (this.legendShowing) {
            int i = this.selectedIndex;
            long[] jArr = this.chartData.x;
            if (i < jArr.length) {
                this.legendSignatureView.setData(i, jArr[i], this.lines, false);
            }
        }
        this.invalidatePickerChart = true;
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // android.view.View
    public void onMeasure(int i, int i2) {
        super.onMeasure(i, i2);
        if (!this.landscape) {
            setMeasuredDimension(View.MeasureSpec.getSize(i), View.MeasureSpec.getSize(i));
        } else {
            setMeasuredDimension(View.MeasureSpec.getSize(i), AndroidUtilities.displaySize.y - AndroidUtilities.dp(56.0f));
        }
        if (getMeasuredWidth() == this.lastW && getMeasuredHeight() == this.lastH) {
            return;
        }
        this.lastW = getMeasuredWidth();
        this.lastH = getMeasuredHeight();
        float f = HORIZONTAL_PADDING;
        this.bottomChartBitmap = Bitmap.createBitmap((int) (getMeasuredWidth() - (f * 2.0f)), this.pikerHeight, Bitmap.Config.ARGB_4444);
        this.bottomChartCanvas = new Canvas(this.bottomChartBitmap);
        this.sharedUiComponents.getPickerMaskBitmap(this.pikerHeight, (int) (getMeasuredWidth() - (2.0f * f)));
        measureSizes();
        if (this.legendShowing) {
            moveLegend((this.chartFullWidth * this.pickerDelegate.pickerStart) - f);
        }
        onPickerDataChanged(false, true, false);
    }

    private void measureSizes() {
        if (getMeasuredHeight() <= 0 || getMeasuredWidth() <= 0) {
            return;
        }
        float f = HORIZONTAL_PADDING;
        this.pickerWidth = getMeasuredWidth() - (2.0f * f);
        this.chartStart = f;
        float measuredWidth = getMeasuredWidth() - (this.landscape ? LANDSCAPE_END_PADDING : f);
        this.chartEnd = measuredWidth;
        float f2 = measuredWidth - this.chartStart;
        this.chartWidth = f2;
        ChartPickerDelegate chartPickerDelegate = this.pickerDelegate;
        this.chartFullWidth = f2 / (chartPickerDelegate.pickerEnd - chartPickerDelegate.pickerStart);
        updateLineSignature();
        this.chartBottom = AndroidUtilities.dp(100.0f);
        this.chartArea.set(this.chartStart - f, 0.0f, this.chartEnd + f, getMeasuredHeight() - this.chartBottom);
        if (this.chartData != null) {
            this.bottomSignatureOffset = (int) (AndroidUtilities.dp(20.0f) / (this.pickerWidth / this.chartData.x.length));
        }
        measureHeightThreshold();
    }

    private void measureHeightThreshold() {
        int measuredHeight = getMeasuredHeight() - this.chartBottom;
        float f = this.animateToMaxHeight;
        if (f == 0.0f || measuredHeight == 0) {
            return;
        }
        this.thresholdMaxHeight = (f / measuredHeight) * SIGNATURE_TEXT_SIZE;
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // android.view.View
    public void onDraw(Canvas canvas) {
        if (this.superDraw) {
            super.onDraw(canvas);
            return;
        }
        tick();
        int save = canvas.save();
        canvas.clipRect(0.0f, this.chartArea.top, getMeasuredWidth(), this.chartArea.bottom);
        drawBottomLine(canvas);
        this.tmpN = this.horizontalLines.size();
        int i = 0;
        this.tmpI = 0;
        while (true) {
            int i2 = this.tmpI;
            if (i2 >= this.tmpN) {
                break;
            }
            drawHorizontalLines(canvas, this.horizontalLines.get(i2));
            this.tmpI++;
        }
        drawChart(canvas);
        while (true) {
            this.tmpI = i;
            int i3 = this.tmpI;
            if (i3 < this.tmpN) {
                drawSignaturesToHorizontalLines(canvas, this.horizontalLines.get(i3));
                i = this.tmpI + 1;
            } else {
                canvas.restoreToCount(save);
                drawBottomSignature(canvas);
                drawPicker(canvas);
                drawSelection(canvas);
                super.onDraw(canvas);
                return;
            }
        }
    }

    /* JADX INFO: Access modifiers changed from: protected */
    public void tick() {
        float f = this.minMaxUpdateStep;
        if (f == 0.0f) {
            return;
        }
        float f2 = this.currentMaxHeight;
        float f3 = this.animateToMaxHeight;
        if (f2 != f3) {
            float f4 = this.startFromMax + f;
            this.startFromMax = f4;
            if (f4 > 1.0f) {
                this.startFromMax = 1.0f;
                this.currentMaxHeight = f3;
            } else {
                float f5 = this.startFromMaxH;
                this.currentMaxHeight = f5 + ((f3 - f5) * CubicBezierInterpolator.EASE_OUT.getInterpolation(f4));
            }
            invalidate();
        }
        if (!this.useMinHeight) {
            return;
        }
        float f6 = this.currentMinHeight;
        float f7 = this.animateToMinHeight;
        if (f6 == f7) {
            return;
        }
        float f8 = this.startFromMin + this.minMaxUpdateStep;
        this.startFromMin = f8;
        if (f8 > 1.0f) {
            this.startFromMin = 1.0f;
            this.currentMinHeight = f7;
        } else {
            float f9 = this.startFromMinH;
            this.currentMinHeight = f9 + ((f7 - f9) * CubicBezierInterpolator.EASE_OUT.getInterpolation(f8));
        }
        invalidate();
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public void drawBottomSignature(Canvas canvas) {
        float f;
        if (this.chartData == null) {
            return;
        }
        this.tmpN = this.bottomSignatureDate.size();
        int i = this.transitionMode;
        if (i == 2) {
            f = 1.0f - this.transitionParams.progress;
        } else if (i == 1) {
            f = this.transitionParams.progress;
        } else {
            f = i == 3 ? this.transitionParams.progress : 1.0f;
        }
        this.tmpI = 0;
        while (true) {
            int i2 = this.tmpI;
            if (i2 >= this.tmpN) {
                return;
            }
            int i3 = this.bottomSignatureDate.get(i2).alpha;
            int i4 = this.bottomSignatureDate.get(this.tmpI).step;
            if (i4 == 0) {
                i4 = 1;
            }
            int i5 = this.startXIndex - this.bottomSignatureOffset;
            while (i5 % i4 != 0) {
                i5--;
            }
            int i6 = this.endXIndex - this.bottomSignatureOffset;
            while (true) {
                if (i6 % i4 == 0 && i6 >= this.chartData.x.length - 1) {
                    break;
                }
                i6++;
            }
            int i7 = this.bottomSignatureOffset;
            int i8 = i6 + i7;
            float f2 = (this.chartFullWidth * this.pickerDelegate.pickerStart) - HORIZONTAL_PADDING;
            for (int i9 = i5 + i7; i9 < i8; i9 += i4) {
                if (i9 >= 0) {
                    long[] jArr = this.chartData.x;
                    if (i9 < jArr.length - 1) {
                        float f3 = ((((float) (jArr[i9] - jArr[0])) / ((float) (jArr[jArr.length - 1] - jArr[0]))) * this.chartFullWidth) - f2;
                        float f4 = f3 - BOTTOM_SIGNATURE_OFFSET;
                        if (f4 > 0.0f) {
                            float f5 = this.chartWidth;
                            float f6 = HORIZONTAL_PADDING;
                            if (f4 <= f5 + f6) {
                                int i10 = BOTTOM_SIGNATURE_START_ALPHA;
                                if (f4 < i10) {
                                    this.bottomSignaturePaint.setAlpha((int) (i3 * (1.0f - ((i10 - f4) / i10)) * this.bottomSignaturePaintAlpha * f));
                                } else if (f4 > f5) {
                                    this.bottomSignaturePaint.setAlpha((int) (i3 * (1.0f - ((f4 - f5) / f6)) * this.bottomSignaturePaintAlpha * f));
                                } else {
                                    this.bottomSignaturePaint.setAlpha((int) (i3 * this.bottomSignaturePaintAlpha * f));
                                }
                                canvas.drawText(this.chartData.getDayString(i9), f3, (getMeasuredHeight() - this.chartBottom) + BOTTOM_SIGNATURE_TEXT_HEIGHT + AndroidUtilities.dp(3.0f), this.bottomSignaturePaint);
                            }
                        }
                    }
                }
            }
            this.tmpI++;
        }
    }

    /* JADX INFO: Access modifiers changed from: protected */
    public void drawBottomLine(Canvas canvas) {
        int measuredHeight;
        if (this.chartData == null) {
            return;
        }
        int i = this.transitionMode;
        float f = 1.0f;
        if (i == 2) {
            f = 1.0f - this.transitionParams.progress;
        } else if (i == 1) {
            f = this.transitionParams.progress;
        } else if (i == 3) {
            f = this.transitionParams.progress;
        }
        this.linePaint.setAlpha((int) (this.hintLinePaintAlpha * f));
        this.signaturePaint.setAlpha((int) (this.signaturePaintAlpha * 255.0f * f));
        int textSize = (int) (SIGNATURE_TEXT_HEIGHT - this.signaturePaint.getTextSize());
        float measuredHeight2 = (getMeasuredHeight() - this.chartBottom) - 1;
        canvas.drawLine(this.chartStart, measuredHeight2, this.chartEnd, measuredHeight2, this.linePaint);
        if (this.useMinHeight) {
            return;
        }
        canvas.drawText("0", HORIZONTAL_PADDING, measuredHeight - textSize, this.signaturePaint);
    }

    /* JADX INFO: Access modifiers changed from: protected */
    public void drawSelection(Canvas canvas) {
        T t;
        int i = this.selectedIndex;
        if (i < 0 || !this.legendShowing || (t = this.chartData) == null) {
            return;
        }
        int i2 = (int) (this.chartActiveLineAlpha * this.selectionA);
        float f = this.chartWidth;
        ChartPickerDelegate chartPickerDelegate = this.pickerDelegate;
        float f2 = chartPickerDelegate.pickerEnd;
        float f3 = chartPickerDelegate.pickerStart;
        float f4 = f / (f2 - f3);
        float f5 = (f3 * f4) - HORIZONTAL_PADDING;
        float[] fArr = t.xPercentage;
        if (i >= fArr.length) {
            return;
        }
        float f6 = (fArr[i] * f4) - f5;
        this.selectedLinePaint.setAlpha(i2);
        canvas.drawLine(f6, 0.0f, f6, this.chartArea.bottom, this.selectedLinePaint);
        if (!this.drawPointOnSelection) {
            return;
        }
        this.tmpN = this.lines.size();
        int i3 = 0;
        while (true) {
            this.tmpI = i3;
            int i4 = this.tmpI;
            if (i4 >= this.tmpN) {
                return;
            }
            L l = this.lines.get(i4);
            if (l.enabled || l.alpha != 0.0f) {
                float f7 = this.currentMinHeight;
                float measuredHeight = (getMeasuredHeight() - this.chartBottom) - (((l.line.y[this.selectedIndex] - f7) / (this.currentMaxHeight - f7)) * ((getMeasuredHeight() - this.chartBottom) - SIGNATURE_TEXT_HEIGHT));
                l.selectionPaint.setAlpha((int) (l.alpha * 255.0f * this.selectionA));
                this.selectionBackgroundPaint.setAlpha((int) (l.alpha * 255.0f * this.selectionA));
                canvas.drawPoint(f6, measuredHeight, l.selectionPaint);
                canvas.drawPoint(f6, measuredHeight, this.selectionBackgroundPaint);
            }
            i3 = this.tmpI + 1;
        }
    }

    /* JADX INFO: Access modifiers changed from: protected */
    /* JADX WARN: Removed duplicated region for block: B:10:0x002b  */
    /* JADX WARN: Removed duplicated region for block: B:11:0x0031  */
    /* JADX WARN: Removed duplicated region for block: B:18:0x0074 A[LOOP:0: B:17:0x0072->B:18:0x0074, LOOP_END] */
    /*
        Code decompiled incorrectly, please refer to instructions dump.
        To view partially-correct add '--show-bad-code' argument
    */
    public void drawHorizontalLines(android.graphics.Canvas r12, org.telegram.ui.Charts.view_data.ChartHorizontalLinesData r13) {
        /*
            r11 = this;
            int[] r0 = r13.values
            int r1 = r0.length
            r2 = 2
            r3 = 1065353216(0x3var_, float:1.0)
            r4 = 1
            if (r1 <= r2) goto L25
            r5 = r0[r4]
            r6 = 0
            r0 = r0[r6]
            int r5 = r5 - r0
            float r0 = (float) r5
            float r5 = r11.currentMaxHeight
            float r6 = r11.currentMinHeight
            float r5 = r5 - r6
            float r0 = r0 / r5
            double r5 = (double) r0
            r7 = 4591870180066957722(0x3fb999999999999a, double:0.1)
            int r9 = (r5 > r7 ? 1 : (r5 == r7 ? 0 : -1))
            if (r9 >= 0) goto L25
            r5 = 1036831949(0x3dcccccd, float:0.1)
            float r0 = r0 / r5
            goto L27
        L25:
            r0 = 1065353216(0x3var_, float:1.0)
        L27:
            int r5 = r11.transitionMode
            if (r5 != r2) goto L31
            org.telegram.ui.Charts.view_data.TransitionParams r2 = r11.transitionParams
            float r2 = r2.progress
            float r3 = r3 - r2
            goto L3f
        L31:
            if (r5 != r4) goto L38
            org.telegram.ui.Charts.view_data.TransitionParams r2 = r11.transitionParams
            float r3 = r2.progress
            goto L3f
        L38:
            r2 = 3
            if (r5 != r2) goto L3f
            org.telegram.ui.Charts.view_data.TransitionParams r2 = r11.transitionParams
            float r3 = r2.progress
        L3f:
            android.graphics.Paint r2 = r11.linePaint
            int r5 = r13.alpha
            float r5 = (float) r5
            int r6 = r11.hintLinePaintAlpha
            float r6 = (float) r6
            r7 = 1132396544(0x437var_, float:255.0)
            float r6 = r6 / r7
            float r5 = r5 * r6
            float r5 = r5 * r3
            float r5 = r5 * r0
            int r5 = (int) r5
            r2.setAlpha(r5)
            android.graphics.Paint r2 = r11.signaturePaint
            int r5 = r13.alpha
            float r5 = (float) r5
            float r6 = r11.signaturePaintAlpha
            float r5 = r5 * r6
            float r5 = r5 * r3
            float r5 = r5 * r0
            int r0 = (int) r5
            r2.setAlpha(r0)
            int r0 = r11.getMeasuredHeight()
            int r2 = r11.chartBottom
            int r0 = r0 - r2
            int r2 = org.telegram.ui.Charts.BaseChartView.SIGNATURE_TEXT_HEIGHT
            int r0 = r0 - r2
            boolean r2 = r11.useMinHeight
            r2 = r2 ^ r4
        L72:
            if (r2 >= r1) goto L9d
            int r3 = r11.getMeasuredHeight()
            int r5 = r11.chartBottom
            int r3 = r3 - r5
            float r3 = (float) r3
            float r5 = (float) r0
            int[] r6 = r13.values
            r6 = r6[r2]
            float r6 = (float) r6
            float r7 = r11.currentMinHeight
            float r6 = r6 - r7
            float r8 = r11.currentMaxHeight
            float r8 = r8 - r7
            float r6 = r6 / r8
            float r5 = r5 * r6
            float r3 = r3 - r5
            int r3 = (int) r3
            float r6 = r11.chartStart
            float r7 = (float) r3
            float r8 = r11.chartEnd
            int r3 = r3 + r4
            float r9 = (float) r3
            android.graphics.Paint r10 = r11.linePaint
            r5 = r12
            r5.drawRect(r6, r7, r8, r9, r10)
            int r2 = r2 + 1
            goto L72
        L9d:
            return
        */
        throw new UnsupportedOperationException("Method not decompiled: org.telegram.ui.Charts.BaseChartView.drawHorizontalLines(android.graphics.Canvas, org.telegram.ui.Charts.view_data.ChartHorizontalLinesData):void");
    }

    /* JADX INFO: Access modifiers changed from: protected */
    /* JADX WARN: Removed duplicated region for block: B:10:0x002b  */
    /* JADX WARN: Removed duplicated region for block: B:11:0x0031  */
    /* JADX WARN: Removed duplicated region for block: B:18:0x007d A[LOOP:0: B:17:0x007b->B:18:0x007d, LOOP_END] */
    /*
        Code decompiled incorrectly, please refer to instructions dump.
        To view partially-correct add '--show-bad-code' argument
    */
    public void drawSignaturesToHorizontalLines(android.graphics.Canvas r11, org.telegram.ui.Charts.view_data.ChartHorizontalLinesData r12) {
        /*
            r10 = this;
            int[] r0 = r12.values
            int r1 = r0.length
            r2 = 2
            r3 = 1065353216(0x3var_, float:1.0)
            r4 = 1
            if (r1 <= r2) goto L25
            r5 = r0[r4]
            r6 = 0
            r0 = r0[r6]
            int r5 = r5 - r0
            float r0 = (float) r5
            float r5 = r10.currentMaxHeight
            float r6 = r10.currentMinHeight
            float r5 = r5 - r6
            float r0 = r0 / r5
            double r5 = (double) r0
            r7 = 4591870180066957722(0x3fb999999999999a, double:0.1)
            int r9 = (r5 > r7 ? 1 : (r5 == r7 ? 0 : -1))
            if (r9 >= 0) goto L25
            r5 = 1036831949(0x3dcccccd, float:0.1)
            float r0 = r0 / r5
            goto L27
        L25:
            r0 = 1065353216(0x3var_, float:1.0)
        L27:
            int r5 = r10.transitionMode
            if (r5 != r2) goto L31
            org.telegram.ui.Charts.view_data.TransitionParams r2 = r10.transitionParams
            float r2 = r2.progress
            float r3 = r3 - r2
            goto L3f
        L31:
            if (r5 != r4) goto L38
            org.telegram.ui.Charts.view_data.TransitionParams r2 = r10.transitionParams
            float r3 = r2.progress
            goto L3f
        L38:
            r2 = 3
            if (r5 != r2) goto L3f
            org.telegram.ui.Charts.view_data.TransitionParams r2 = r10.transitionParams
            float r3 = r2.progress
        L3f:
            android.graphics.Paint r2 = r10.linePaint
            int r5 = r12.alpha
            float r5 = (float) r5
            int r6 = r10.hintLinePaintAlpha
            float r6 = (float) r6
            r7 = 1132396544(0x437var_, float:255.0)
            float r6 = r6 / r7
            float r5 = r5 * r6
            float r5 = r5 * r3
            float r5 = r5 * r0
            int r5 = (int) r5
            r2.setAlpha(r5)
            android.graphics.Paint r2 = r10.signaturePaint
            int r5 = r12.alpha
            float r5 = (float) r5
            float r6 = r10.signaturePaintAlpha
            float r5 = r5 * r6
            float r5 = r5 * r3
            float r5 = r5 * r0
            int r0 = (int) r5
            r2.setAlpha(r0)
            int r0 = r10.getMeasuredHeight()
            int r2 = r10.chartBottom
            int r0 = r0 - r2
            int r2 = org.telegram.ui.Charts.BaseChartView.SIGNATURE_TEXT_HEIGHT
            int r0 = r0 - r2
            float r2 = (float) r2
            android.graphics.Paint r3 = r10.signaturePaint
            float r3 = r3.getTextSize()
            float r2 = r2 - r3
            int r2 = (int) r2
            boolean r3 = r10.useMinHeight
            r3 = r3 ^ r4
        L7b:
            if (r3 >= r1) goto La6
            int r4 = r10.getMeasuredHeight()
            int r5 = r10.chartBottom
            int r4 = r4 - r5
            float r4 = (float) r4
            float r5 = (float) r0
            int[] r6 = r12.values
            r6 = r6[r3]
            float r6 = (float) r6
            float r7 = r10.currentMinHeight
            float r6 = r6 - r7
            float r8 = r10.currentMaxHeight
            float r8 = r8 - r7
            float r6 = r6 / r8
            float r5 = r5 * r6
            float r4 = r4 - r5
            int r4 = (int) r4
            java.lang.String[] r5 = r12.valuesStr
            r5 = r5[r3]
            float r6 = org.telegram.ui.Charts.BaseChartView.HORIZONTAL_PADDING
            int r4 = r4 - r2
            float r4 = (float) r4
            android.graphics.Paint r7 = r10.signaturePaint
            r11.drawText(r5, r6, r4, r7)
            int r3 = r3 + 1
            goto L7b
        La6:
            return
        */
        throw new UnsupportedOperationException("Method not decompiled: org.telegram.ui.Charts.BaseChartView.drawSignaturesToHorizontalLines(android.graphics.Canvas, org.telegram.ui.Charts.view_data.ChartHorizontalLinesData):void");
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    /* JADX WARN: Removed duplicated region for block: B:14:0x0070  */
    /* JADX WARN: Removed duplicated region for block: B:51:0x01d1  */
    /* JADX WARN: Removed duplicated region for block: B:54:0x020a  */
    /* JADX WARN: Removed duplicated region for block: B:69:? A[RETURN, SYNTHETIC] */
    /*
        Code decompiled incorrectly, please refer to instructions dump.
        To view partially-correct add '--show-bad-code' argument
    */
    public void drawPicker(android.graphics.Canvas r29) {
        /*
            Method dump skipped, instructions count: 866
            To view this dump add '--comments-level debug' option
        */
        throw new UnsupportedOperationException("Method not decompiled: org.telegram.ui.Charts.BaseChartView.drawPicker(android.graphics.Canvas):void");
    }

    private void setMaxMinValue(int i, int i2, boolean z) {
        setMaxMinValue(i, i2, z, false, false);
    }

    protected void setMaxMinValue(int i, int i2, boolean z, boolean z2, boolean z3) {
        if ((Math.abs(((float) ChartHorizontalLinesData.lookupHeight(i)) - this.animateToMaxHeight) >= this.thresholdMaxHeight && i != 0) || i != this.animateToMinHeight) {
            final ChartHorizontalLinesData createHorizontalLinesData = createHorizontalLinesData(i, i2);
            int[] iArr = createHorizontalLinesData.values;
            int i3 = iArr[iArr.length - 1];
            int i4 = iArr[0];
            if (!z3) {
                float f = this.currentMaxHeight;
                float f2 = this.currentMinHeight;
                float f3 = i3 - i4;
                float f4 = (f - f2) / f3;
                if (f4 > 1.0f) {
                    f4 = f3 / (f - f2);
                }
                float f5 = 0.045f;
                double d = f4;
                if (d > 0.7d) {
                    f5 = 0.1f;
                } else if (d < 0.1d) {
                    f5 = 0.03f;
                }
                boolean z4 = ((float) i3) != this.animateToMaxHeight;
                if (this.useMinHeight && i4 != this.animateToMinHeight) {
                    z4 = true;
                }
                if (z4) {
                    Animator animator = this.maxValueAnimator;
                    if (animator != null) {
                        animator.removeAllListeners();
                        this.maxValueAnimator.cancel();
                    }
                    this.startFromMaxH = this.currentMaxHeight;
                    this.startFromMinH = this.currentMinHeight;
                    this.startFromMax = 0.0f;
                    this.startFromMin = 0.0f;
                    this.minMaxUpdateStep = f5;
                }
            }
            float f6 = i3;
            this.animateToMaxHeight = f6;
            float f7 = i4;
            this.animateToMinHeight = f7;
            measureHeightThreshold();
            long currentTimeMillis = System.currentTimeMillis();
            if (currentTimeMillis - this.lastTime < 320 && !z2) {
                return;
            }
            this.lastTime = currentTimeMillis;
            ValueAnimator valueAnimator = this.alphaAnimator;
            if (valueAnimator != null) {
                valueAnimator.removeAllListeners();
                this.alphaAnimator.cancel();
            }
            if (!z) {
                this.currentMaxHeight = f6;
                this.currentMinHeight = f7;
                this.horizontalLines.clear();
                this.horizontalLines.add(createHorizontalLinesData);
                createHorizontalLinesData.alpha = 255;
                return;
            }
            this.horizontalLines.add(createHorizontalLinesData);
            if (z3) {
                Animator animator2 = this.maxValueAnimator;
                if (animator2 != null) {
                    animator2.removeAllListeners();
                    this.maxValueAnimator.cancel();
                }
                this.minMaxUpdateStep = 0.0f;
                AnimatorSet animatorSet = new AnimatorSet();
                animatorSet.playTogether(createAnimator(this.currentMaxHeight, f6, this.heightUpdateListener));
                if (this.useMinHeight) {
                    animatorSet.playTogether(createAnimator(this.currentMinHeight, f7, this.minHeightUpdateListener));
                }
                this.maxValueAnimator = animatorSet;
                animatorSet.start();
            }
            int size = this.horizontalLines.size();
            for (int i5 = 0; i5 < size; i5++) {
                ChartHorizontalLinesData chartHorizontalLinesData = this.horizontalLines.get(i5);
                if (chartHorizontalLinesData != createHorizontalLinesData) {
                    chartHorizontalLinesData.fixedAlpha = chartHorizontalLinesData.alpha;
                }
            }
            ValueAnimator createAnimator = createAnimator(0.0f, 255.0f, new ValueAnimator.AnimatorUpdateListener() { // from class: org.telegram.ui.Charts.BaseChartView$$ExternalSyntheticLambda3
                @Override // android.animation.ValueAnimator.AnimatorUpdateListener
                public final void onAnimationUpdate(ValueAnimator valueAnimator2) {
                    BaseChartView.this.lambda$setMaxMinValue$2(createHorizontalLinesData, valueAnimator2);
                }
            });
            this.alphaAnimator = createAnimator;
            createAnimator.addListener(new AnimatorListenerAdapter() { // from class: org.telegram.ui.Charts.BaseChartView.5
                @Override // android.animation.AnimatorListenerAdapter, android.animation.Animator.AnimatorListener
                public void onAnimationEnd(Animator animator3) {
                    BaseChartView.this.horizontalLines.clear();
                    BaseChartView.this.horizontalLines.add(createHorizontalLinesData);
                }
            });
            this.alphaAnimator.start();
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public /* synthetic */ void lambda$setMaxMinValue$2(ChartHorizontalLinesData chartHorizontalLinesData, ValueAnimator valueAnimator) {
        chartHorizontalLinesData.alpha = (int) ((Float) valueAnimator.getAnimatedValue()).floatValue();
        Iterator<ChartHorizontalLinesData> it = this.horizontalLines.iterator();
        while (it.hasNext()) {
            ChartHorizontalLinesData next = it.next();
            if (next != chartHorizontalLinesData) {
                next.alpha = (int) ((next.fixedAlpha / 255.0f) * (255 - chartHorizontalLinesData.alpha));
            }
        }
        invalidate();
    }

    protected ChartHorizontalLinesData createHorizontalLinesData(int i, int i2) {
        return new ChartHorizontalLinesData(i, i2, this.useMinHeight);
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public ValueAnimator createAnimator(float f, float f2, ValueAnimator.AnimatorUpdateListener animatorUpdateListener) {
        ValueAnimator ofFloat = ValueAnimator.ofFloat(f, f2);
        ofFloat.setDuration(400L);
        ofFloat.setInterpolator(INTERPOLATOR);
        ofFloat.addUpdateListener(animatorUpdateListener);
        return ofFloat;
    }

    @Override // android.view.View
    public boolean onTouchEvent(MotionEvent motionEvent) {
        boolean z = false;
        if (this.chartData == null) {
            return false;
        }
        if (!this.enabled) {
            this.pickerDelegate.uncapture(motionEvent, motionEvent.getActionIndex());
            getParent().requestDisallowInterceptTouchEvent(false);
            this.chartCaptured = false;
            return false;
        }
        int x = (int) motionEvent.getX(motionEvent.getActionIndex());
        int y = (int) motionEvent.getY(motionEvent.getActionIndex());
        int actionMasked = motionEvent.getActionMasked();
        if (actionMasked == 0) {
            this.capturedTime = System.currentTimeMillis();
            getParent().requestDisallowInterceptTouchEvent(true);
            if (this.pickerDelegate.capture(x, y, motionEvent.getActionIndex())) {
                return true;
            }
            this.lastX = x;
            this.capturedX = x;
            this.lastY = y;
            this.capturedY = y;
            if (!this.chartArea.contains(x, y)) {
                return false;
            }
            if (this.selectedIndex < 0 || !this.animateLegentTo) {
                this.chartCaptured = true;
                selectXOnChart(x, y);
            }
            return true;
        }
        if (actionMasked != 1) {
            if (actionMasked == 2) {
                int i = x - this.lastX;
                int i2 = y - this.lastY;
                if (this.pickerDelegate.captured()) {
                    boolean move = this.pickerDelegate.move(x, y, motionEvent.getActionIndex());
                    if (motionEvent.getPointerCount() > 1) {
                        this.pickerDelegate.move((int) motionEvent.getX(1), (int) motionEvent.getY(1), 1);
                    }
                    getParent().requestDisallowInterceptTouchEvent(move);
                    return true;
                }
                if (this.chartCaptured) {
                    if ((this.canCaptureChartSelection && System.currentTimeMillis() - this.capturedTime > 200) || Math.abs(i) > Math.abs(i2) || Math.abs(i2) < this.touchSlop) {
                        z = true;
                    }
                    this.lastX = x;
                    this.lastY = y;
                    getParent().requestDisallowInterceptTouchEvent(z);
                    selectXOnChart(x, y);
                } else if (this.chartArea.contains(this.capturedX, this.capturedY)) {
                    int i3 = this.capturedX - x;
                    int i4 = this.capturedY - y;
                    if (Math.sqrt((i3 * i3) + (i4 * i4)) > this.touchSlop || System.currentTimeMillis() - this.capturedTime > 200) {
                        this.chartCaptured = true;
                        selectXOnChart(x, y);
                    }
                }
                return true;
            } else if (actionMasked != 3) {
                if (actionMasked == 5) {
                    return this.pickerDelegate.capture(x, y, motionEvent.getActionIndex());
                }
                if (actionMasked != 6) {
                    return false;
                }
                this.pickerDelegate.uncapture(motionEvent, motionEvent.getActionIndex());
                return true;
            }
        }
        if (this.pickerDelegate.uncapture(motionEvent, motionEvent.getActionIndex())) {
            return true;
        }
        if (this.chartArea.contains(this.capturedX, this.capturedY) && !this.chartCaptured) {
            animateLegend(false);
        }
        this.pickerDelegate.uncapture();
        updateLineSignature();
        getParent().requestDisallowInterceptTouchEvent(false);
        this.chartCaptured = false;
        onActionUp();
        invalidate();
        setMaxMinValue(findMaxValue(this.startXIndex, this.endXIndex), this.useMinHeight ? findMinValue(this.startXIndex, this.endXIndex) : 0, true, true, false);
        return true;
    }

    protected void selectXOnChart(int i, int i2) {
        int i3 = this.selectedIndex;
        T t = this.chartData;
        if (t == null) {
            return;
        }
        float f = this.chartFullWidth;
        float f2 = (this.pickerDelegate.pickerStart * f) - HORIZONTAL_PADDING;
        float f3 = (i + f2) / f;
        if (f3 < 0.0f) {
            this.selectedIndex = 0;
        } else if (f3 > 1.0f) {
            this.selectedIndex = t.x.length - 1;
        } else {
            int findIndex = t.findIndex(this.startXIndex, this.endXIndex, f3);
            this.selectedIndex = findIndex;
            int i4 = findIndex + 1;
            float[] fArr = this.chartData.xPercentage;
            if (i4 < fArr.length) {
                if (Math.abs(this.chartData.xPercentage[this.selectedIndex + 1] - f3) < Math.abs(fArr[findIndex] - f3)) {
                    this.selectedIndex++;
                }
            }
        }
        int i5 = this.selectedIndex;
        int i6 = this.endXIndex;
        if (i5 > i6) {
            this.selectedIndex = i6;
        }
        int i7 = this.selectedIndex;
        int i8 = this.startXIndex;
        if (i7 < i8) {
            this.selectedIndex = i8;
        }
        if (i3 == this.selectedIndex) {
            return;
        }
        this.legendShowing = true;
        animateLegend(true);
        moveLegend(f2);
        DateSelectionListener dateSelectionListener = this.dateSelectionListener;
        if (dateSelectionListener != null) {
            dateSelectionListener.onDateSelected(getSelectedDate());
        }
        runSmoothHaptic();
        invalidate();
    }

    /* JADX INFO: Access modifiers changed from: protected */
    public void runSmoothHaptic() {
        if (Build.VERSION.SDK_INT >= 26) {
            Vibrator vibrator = (Vibrator) getContext().getSystemService("vibrator");
            if (this.vibrationEffect == null) {
                this.vibrationEffect = VibrationEffect.createWaveform(new long[]{0, 2}, -1);
            }
            vibrator.cancel();
            vibrator.vibrate(this.vibrationEffect);
        }
    }

    public void animateLegend(boolean z) {
        moveLegend();
        if (this.animateLegentTo == z) {
            return;
        }
        this.animateLegentTo = z;
        ValueAnimator valueAnimator = this.selectionAnimator;
        if (valueAnimator != null) {
            valueAnimator.removeAllListeners();
            this.selectionAnimator.cancel();
        }
        ValueAnimator duration = createAnimator(this.selectionA, z ? 1.0f : 0.0f, this.selectionAnimatorListener).setDuration(200L);
        this.selectionAnimator = duration;
        duration.addListener(this.selectorAnimatorEndListener);
        this.selectionAnimator.start();
    }

    public void moveLegend(float f) {
        int i;
        float f2;
        T t = this.chartData;
        if (t == null || (i = this.selectedIndex) == -1 || !this.legendShowing) {
            return;
        }
        this.legendSignatureView.setData(i, t.x[i], this.lines, false);
        this.legendSignatureView.setVisibility(0);
        this.legendSignatureView.measure(View.MeasureSpec.makeMeasureSpec(getMeasuredWidth(), Integer.MIN_VALUE), View.MeasureSpec.makeMeasureSpec(getMeasuredHeight(), Integer.MIN_VALUE));
        float f3 = (this.chartData.xPercentage[this.selectedIndex] * this.chartFullWidth) - f;
        if (f3 > (this.chartStart + this.chartWidth) / 2.0f) {
            f2 = f3 - (this.legendSignatureView.getWidth() + DP_5);
        } else {
            f2 = f3 + DP_5;
        }
        if (f2 < 0.0f) {
            f2 = 0.0f;
        } else if (this.legendSignatureView.getMeasuredWidth() + f2 > getMeasuredWidth()) {
            f2 = getMeasuredWidth() - this.legendSignatureView.getMeasuredWidth();
        }
        this.legendSignatureView.setTranslationX(f2);
    }

    public int findMaxValue(int i, int i2) {
        int rMaxQ;
        int size = this.lines.size();
        int i3 = 0;
        for (int i4 = 0; i4 < size; i4++) {
            if (this.lines.get(i4).enabled && (rMaxQ = this.lines.get(i4).line.segmentTree.rMaxQ(i, i2)) > i3) {
                i3 = rMaxQ;
            }
        }
        return i3;
    }

    public int findMinValue(int i, int i2) {
        int rMinQ;
        int size = this.lines.size();
        int i3 = Integer.MAX_VALUE;
        for (int i4 = 0; i4 < size; i4++) {
            if (this.lines.get(i4).enabled && (rMinQ = this.lines.get(i4).line.segmentTree.rMinQ(i, i2)) < i3) {
                i3 = rMinQ;
            }
        }
        return i3;
    }

    public void setData(T t) {
        if (this.chartData != t) {
            invalidate();
            this.lines.clear();
            if (t != null && t.lines != null) {
                for (int i = 0; i < t.lines.size(); i++) {
                    this.lines.add(mo1036createLineViewData(t.lines.get(i)));
                }
            }
            clearSelection();
            this.chartData = t;
            if (t != null) {
                if (t.x[0] == 0) {
                    ChartPickerDelegate chartPickerDelegate = this.pickerDelegate;
                    chartPickerDelegate.pickerStart = 0.0f;
                    chartPickerDelegate.pickerEnd = 1.0f;
                } else {
                    this.pickerDelegate.minDistance = getMinDistance();
                    ChartPickerDelegate chartPickerDelegate2 = this.pickerDelegate;
                    float f = chartPickerDelegate2.pickerEnd;
                    float f2 = chartPickerDelegate2.minDistance;
                    if (f - chartPickerDelegate2.pickerStart < f2) {
                        float f3 = f - f2;
                        chartPickerDelegate2.pickerStart = f3;
                        if (f3 < 0.0f) {
                            chartPickerDelegate2.pickerStart = 0.0f;
                            chartPickerDelegate2.pickerEnd = 1.0f;
                        }
                    }
                }
            }
        }
        measureSizes();
        if (t != null) {
            updateIndexes();
            setMaxMinValue(findMaxValue(this.startXIndex, this.endXIndex), this.useMinHeight ? findMinValue(this.startXIndex, this.endXIndex) : 0, false);
            this.pickerMaxHeight = 0.0f;
            this.pickerMinHeight = 2.14748365E9f;
            initPickerMaxHeight();
            this.legendSignatureView.setSize(this.lines.size());
            this.invalidatePickerChart = true;
            updateLineSignature();
            return;
        }
        ChartPickerDelegate chartPickerDelegate3 = this.pickerDelegate;
        chartPickerDelegate3.pickerStart = 0.7f;
        chartPickerDelegate3.pickerEnd = 1.0f;
        this.pickerMinHeight = 0.0f;
        this.pickerMaxHeight = 0.0f;
        this.horizontalLines.clear();
        Animator animator = this.maxValueAnimator;
        if (animator != null) {
            animator.cancel();
        }
        ValueAnimator valueAnimator = this.alphaAnimator;
        if (valueAnimator == null) {
            return;
        }
        valueAnimator.removeAllListeners();
        this.alphaAnimator.cancel();
    }

    protected float getMinDistance() {
        T t = this.chartData;
        if (t == null) {
            return 0.1f;
        }
        int length = t.x.length;
        if (length < 5) {
            return 1.0f;
        }
        float f = 5.0f / length;
        if (f >= 0.1f) {
            return f;
        }
        return 0.1f;
    }

    /* JADX INFO: Access modifiers changed from: protected */
    public void initPickerMaxHeight() {
        Iterator<L> it = this.lines.iterator();
        while (it.hasNext()) {
            L next = it.next();
            boolean z = next.enabled;
            if (z) {
                int i = next.line.maxValue;
                if (i > this.pickerMaxHeight) {
                    this.pickerMaxHeight = i;
                }
            }
            if (z) {
                int i2 = next.line.minValue;
                if (i2 < this.pickerMinHeight) {
                    this.pickerMinHeight = i2;
                }
            }
            float f = this.pickerMaxHeight;
            float f2 = this.pickerMinHeight;
            if (f == f2) {
                this.pickerMaxHeight = f + 1.0f;
                this.pickerMinHeight = f2 - 1.0f;
            }
        }
    }

    @Override // org.telegram.ui.Charts.ChartPickerDelegate.Listener
    public void onPickerDataChanged() {
        onPickerDataChanged(true, false, false);
    }

    public void onPickerDataChanged(boolean z, boolean z2, boolean z3) {
        if (this.chartData == null) {
            return;
        }
        float f = this.chartWidth;
        ChartPickerDelegate chartPickerDelegate = this.pickerDelegate;
        this.chartFullWidth = f / (chartPickerDelegate.pickerEnd - chartPickerDelegate.pickerStart);
        updateIndexes();
        setMaxMinValue(findMaxValue(this.startXIndex, this.endXIndex), this.useMinHeight ? findMinValue(this.startXIndex, this.endXIndex) : 0, z, z2, z3);
        if (this.legendShowing && !z2) {
            animateLegend(false);
            moveLegend((this.chartFullWidth * this.pickerDelegate.pickerStart) - HORIZONTAL_PADDING);
        }
        invalidate();
    }

    @Override // org.telegram.ui.Charts.ChartPickerDelegate.Listener
    public void onPickerJumpTo(float f, float f2, boolean z) {
        T t = this.chartData;
        if (t == null) {
            return;
        }
        if (z) {
            int findStartIndex = t.findStartIndex(Math.max(f, 0.0f));
            int findEndIndex = this.chartData.findEndIndex(findStartIndex, Math.min(f2, 1.0f));
            setMaxMinValue(findMaxValue(findStartIndex, findEndIndex), findMinValue(findStartIndex, findEndIndex), true, true, false);
            animateLegend(false);
            return;
        }
        updateIndexes();
        invalidate();
    }

    /* JADX INFO: Access modifiers changed from: protected */
    public void updateIndexes() {
        T t = this.chartData;
        if (t == null) {
            return;
        }
        int findStartIndex = t.findStartIndex(Math.max(this.pickerDelegate.pickerStart, 0.0f));
        this.startXIndex = findStartIndex;
        int findEndIndex = this.chartData.findEndIndex(findStartIndex, Math.min(this.pickerDelegate.pickerEnd, 1.0f));
        this.endXIndex = findEndIndex;
        int i = this.startXIndex;
        if (findEndIndex < i) {
            this.endXIndex = i;
        }
        ChartHeaderView chartHeaderView = this.chartHeaderView;
        if (chartHeaderView != null) {
            long[] jArr = this.chartData.x;
            chartHeaderView.setDates(jArr[i], jArr[this.endXIndex]);
        }
        updateLineSignature();
    }

    private void updateLineSignature() {
        T t = this.chartData;
        if (t != null) {
            float f = this.chartWidth;
            if (f == 0.0f) {
                return;
            }
            updateDates((int) ((f / (this.chartFullWidth * t.oneDayPercentage)) / 6.0f));
        }
    }

    private void updateDates(int i) {
        ChartBottomSignatureData chartBottomSignatureData = this.currentBottomSignatures;
        if (chartBottomSignatureData == null || i >= chartBottomSignatureData.stepMax || i <= chartBottomSignatureData.stepMin) {
            int highestOneBit = Integer.highestOneBit(i) << 1;
            ChartBottomSignatureData chartBottomSignatureData2 = this.currentBottomSignatures;
            if (chartBottomSignatureData2 != null && chartBottomSignatureData2.step == highestOneBit) {
                return;
            }
            ValueAnimator valueAnimator = this.alphaBottomAnimator;
            if (valueAnimator != null) {
                valueAnimator.removeAllListeners();
                this.alphaBottomAnimator.cancel();
            }
            double d = highestOneBit;
            Double.isNaN(d);
            double d2 = 0.2d * d;
            Double.isNaN(d);
            Double.isNaN(d);
            final ChartBottomSignatureData chartBottomSignatureData3 = new ChartBottomSignatureData(highestOneBit, (int) (d + d2), (int) (d - d2));
            chartBottomSignatureData3.alpha = 255;
            if (this.currentBottomSignatures == null) {
                this.currentBottomSignatures = chartBottomSignatureData3;
                chartBottomSignatureData3.alpha = 255;
                this.bottomSignatureDate.add(chartBottomSignatureData3);
                return;
            }
            this.currentBottomSignatures = chartBottomSignatureData3;
            this.tmpN = this.bottomSignatureDate.size();
            for (int i2 = 0; i2 < this.tmpN; i2++) {
                ChartBottomSignatureData chartBottomSignatureData4 = this.bottomSignatureDate.get(i2);
                chartBottomSignatureData4.fixedAlpha = chartBottomSignatureData4.alpha;
            }
            this.bottomSignatureDate.add(chartBottomSignatureData3);
            if (this.bottomSignatureDate.size() > 2) {
                this.bottomSignatureDate.remove(0);
            }
            ValueAnimator duration = createAnimator(0.0f, 1.0f, new ValueAnimator.AnimatorUpdateListener() { // from class: org.telegram.ui.Charts.BaseChartView$$ExternalSyntheticLambda2
                @Override // android.animation.ValueAnimator.AnimatorUpdateListener
                public final void onAnimationUpdate(ValueAnimator valueAnimator2) {
                    BaseChartView.this.lambda$updateDates$3(chartBottomSignatureData3, valueAnimator2);
                }
            }).setDuration(200L);
            this.alphaBottomAnimator = duration;
            duration.addListener(new AnimatorListenerAdapter() { // from class: org.telegram.ui.Charts.BaseChartView.6
                @Override // android.animation.AnimatorListenerAdapter, android.animation.Animator.AnimatorListener
                public void onAnimationEnd(Animator animator) {
                    super.onAnimationEnd(animator);
                    BaseChartView.this.bottomSignatureDate.clear();
                    BaseChartView.this.bottomSignatureDate.add(chartBottomSignatureData3);
                }
            });
            this.alphaBottomAnimator.start();
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public /* synthetic */ void lambda$updateDates$3(ChartBottomSignatureData chartBottomSignatureData, ValueAnimator valueAnimator) {
        float floatValue = ((Float) valueAnimator.getAnimatedValue()).floatValue();
        Iterator<ChartBottomSignatureData> it = this.bottomSignatureDate.iterator();
        while (it.hasNext()) {
            ChartBottomSignatureData next = it.next();
            if (next == chartBottomSignatureData) {
                chartBottomSignatureData.alpha = (int) (255.0f * floatValue);
            } else {
                next.alpha = (int) ((1.0f - floatValue) * next.fixedAlpha);
            }
        }
        invalidate();
    }

    public void onCheckChanged() {
        ValueAnimator valueAnimator;
        ValueAnimator valueAnimator2;
        ValueAnimator valueAnimator3;
        onPickerDataChanged(true, true, true);
        this.tmpN = this.lines.size();
        int i = 0;
        while (true) {
            this.tmpI = i;
            int i2 = this.tmpI;
            if (i2 >= this.tmpN) {
                break;
            }
            final L l = this.lines.get(i2);
            if (l.enabled && (valueAnimator3 = l.animatorOut) != null) {
                valueAnimator3.cancel();
            }
            if (!l.enabled && (valueAnimator2 = l.animatorIn) != null) {
                valueAnimator2.cancel();
            }
            if (l.enabled && l.alpha != 1.0f) {
                ValueAnimator valueAnimator4 = l.animatorIn;
                if (valueAnimator4 != null && valueAnimator4.isRunning()) {
                    i = this.tmpI + 1;
                } else {
                    ValueAnimator createAnimator = createAnimator(l.alpha, 1.0f, new ValueAnimator.AnimatorUpdateListener() { // from class: org.telegram.ui.Charts.BaseChartView$$ExternalSyntheticLambda4
                        @Override // android.animation.ValueAnimator.AnimatorUpdateListener
                        public final void onAnimationUpdate(ValueAnimator valueAnimator5) {
                            BaseChartView.this.lambda$onCheckChanged$4(l, valueAnimator5);
                        }
                    });
                    l.animatorIn = createAnimator;
                    createAnimator.start();
                }
            }
            if (!l.enabled && l.alpha != 0.0f && ((valueAnimator = l.animatorOut) == null || !valueAnimator.isRunning())) {
                ValueAnimator createAnimator2 = createAnimator(l.alpha, 0.0f, new ValueAnimator.AnimatorUpdateListener() { // from class: org.telegram.ui.Charts.BaseChartView$$ExternalSyntheticLambda5
                    @Override // android.animation.ValueAnimator.AnimatorUpdateListener
                    public final void onAnimationUpdate(ValueAnimator valueAnimator5) {
                        BaseChartView.this.lambda$onCheckChanged$5(l, valueAnimator5);
                    }
                });
                l.animatorOut = createAnimator2;
                createAnimator2.start();
            }
            i = this.tmpI + 1;
        }
        updatePickerMinMaxHeight();
        if (this.legendShowing) {
            LegendSignatureView legendSignatureView = this.legendSignatureView;
            int i3 = this.selectedIndex;
            legendSignatureView.setData(i3, this.chartData.x[i3], this.lines, true);
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public /* synthetic */ void lambda$onCheckChanged$4(LineViewData lineViewData, ValueAnimator valueAnimator) {
        lineViewData.alpha = ((Float) valueAnimator.getAnimatedValue()).floatValue();
        this.invalidatePickerChart = true;
        invalidate();
    }

    /* JADX INFO: Access modifiers changed from: private */
    public /* synthetic */ void lambda$onCheckChanged$5(LineViewData lineViewData, ValueAnimator valueAnimator) {
        lineViewData.alpha = ((Float) valueAnimator.getAnimatedValue()).floatValue();
        this.invalidatePickerChart = true;
        invalidate();
    }

    /* JADX INFO: Access modifiers changed from: protected */
    public void updatePickerMinMaxHeight() {
        int i;
        int i2;
        if (!ANIMATE_PICKER_SIZES) {
            return;
        }
        Iterator<L> it = this.lines.iterator();
        int i3 = Integer.MAX_VALUE;
        int i4 = 0;
        while (it.hasNext()) {
            L next = it.next();
            boolean z = next.enabled;
            if (z && (i2 = next.line.maxValue) > i4) {
                i4 = i2;
            }
            if (z && (i = next.line.minValue) < i3) {
                i3 = i;
            }
        }
        if ((i3 == Integer.MAX_VALUE || i3 == this.animatedToPickerMinHeight) && (i4 <= 0 || i4 == this.animatedToPickerMaxHeight)) {
            return;
        }
        this.animatedToPickerMaxHeight = i4;
        Animator animator = this.pickerAnimator;
        if (animator != null) {
            animator.cancel();
        }
        AnimatorSet animatorSet = new AnimatorSet();
        animatorSet.playTogether(createAnimator(this.pickerMaxHeight, this.animatedToPickerMaxHeight, this.pickerHeightUpdateListener), createAnimator(this.pickerMinHeight, this.animatedToPickerMinHeight, this.pickerMinHeightUpdateListener));
        this.pickerAnimator = animatorSet;
        animatorSet.start();
    }

    public void setLandscape(boolean z) {
        this.landscape = z;
    }

    public void setHeader(ChartHeaderView chartHeaderView) {
        this.chartHeaderView = chartHeaderView;
    }

    public long getSelectedDate() {
        int i = this.selectedIndex;
        if (i < 0) {
            return -1L;
        }
        return this.chartData.x[i];
    }

    public void clearSelection() {
        this.selectedIndex = -1;
        this.legendShowing = false;
        this.animateLegentTo = false;
        this.legendSignatureView.setVisibility(8);
        this.selectionA = 0.0f;
    }

    public void selectDate(long j) {
        this.selectedIndex = Arrays.binarySearch(this.chartData.x, j);
        this.legendShowing = true;
        this.legendSignatureView.setVisibility(0);
        this.selectionA = 1.0f;
        moveLegend((this.chartFullWidth * this.pickerDelegate.pickerStart) - HORIZONTAL_PADDING);
        performHapticFeedback(3, 2);
    }

    public long getStartDate() {
        return this.chartData.x[this.startXIndex];
    }

    public long getEndDate() {
        return this.chartData.x[this.endXIndex];
    }

    public void updatePicker(ChartData chartData, long j) {
        int length = chartData.x.length;
        long j2 = j - (j % 86400000);
        long j3 = (86400000 + j2) - 1;
        int i = 0;
        int i2 = 0;
        for (int i3 = 0; i3 < length; i3++) {
            long[] jArr = chartData.x;
            if (j2 > jArr[i3]) {
                i = i3;
            }
            if (j3 > jArr[i3]) {
                i2 = i3;
            }
        }
        ChartPickerDelegate chartPickerDelegate = this.pickerDelegate;
        float[] fArr = chartData.xPercentage;
        chartPickerDelegate.pickerStart = fArr[i];
        chartPickerDelegate.pickerEnd = fArr[i2];
    }

    public void moveLegend() {
        moveLegend((this.chartFullWidth * this.pickerDelegate.pickerStart) - HORIZONTAL_PADDING);
    }

    @Override // android.view.View
    public void requestLayout() {
        super.requestLayout();
    }

    public static Path RoundedRect(Path path, float f, float f2, float f3, float f4, float f5, float f6, boolean z, boolean z2, boolean z3, boolean z4) {
        path.reset();
        if (f5 < 0.0f) {
            f5 = 0.0f;
        }
        if (f6 < 0.0f) {
            f6 = 0.0f;
        }
        float f7 = f3 - f;
        float f8 = f4 - f2;
        float f9 = f7 / 2.0f;
        if (f5 > f9) {
            f5 = f9;
        }
        float var_ = f8 / 2.0f;
        if (f6 > var_) {
            f6 = var_;
        }
        float var_ = f7 - (f5 * 2.0f);
        float var_ = f8 - (2.0f * f6);
        path.moveTo(f3, f2 + f6);
        if (z2) {
            float var_ = -f6;
            path.rQuadTo(0.0f, var_, -f5, var_);
        } else {
            path.rLineTo(0.0f, -f6);
            path.rLineTo(-f5, 0.0f);
        }
        path.rLineTo(-var_, 0.0f);
        if (z) {
            float var_ = -f5;
            path.rQuadTo(var_, 0.0f, var_, f6);
        } else {
            path.rLineTo(-f5, 0.0f);
            path.rLineTo(0.0f, f6);
        }
        path.rLineTo(0.0f, var_);
        if (z4) {
            path.rQuadTo(0.0f, f6, f5, f6);
        } else {
            path.rLineTo(0.0f, f6);
            path.rLineTo(f5, 0.0f);
        }
        path.rLineTo(var_, 0.0f);
        if (z3) {
            path.rQuadTo(f5, 0.0f, f5, -f6);
        } else {
            path.rLineTo(f5, 0.0f);
            path.rLineTo(0.0f, -f6);
        }
        path.rLineTo(0.0f, -var_);
        path.close();
        return path;
    }

    public void setDateSelectionListener(DateSelectionListener dateSelectionListener) {
        this.dateSelectionListener = dateSelectionListener;
    }

    /* loaded from: classes3.dex */
    public static class SharedUiComponents {
        private Canvas canvas;
        private boolean invalidate;
        int k;
        private Bitmap pickerRoundBitmap;
        private RectF rectF = new RectF();
        private Paint xRefP;

        public SharedUiComponents() {
            Paint paint = new Paint(1);
            this.xRefP = paint;
            this.k = 0;
            this.invalidate = true;
            paint.setColor(0);
            this.xRefP.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.CLEAR));
        }

        Bitmap getPickerMaskBitmap(int i, int i2) {
            int i3 = (i + i2) << 10;
            if (i3 != this.k || this.invalidate) {
                this.invalidate = false;
                this.k = i3;
                this.pickerRoundBitmap = Bitmap.createBitmap(i2, i, Bitmap.Config.ARGB_8888);
                this.canvas = new Canvas(this.pickerRoundBitmap);
                this.rectF.set(0.0f, 0.0f, i2, i);
                this.canvas.drawColor(Theme.getColor("windowBackgroundWhite"));
                this.canvas.drawRoundRect(this.rectF, AndroidUtilities.dp(4.0f), AndroidUtilities.dp(4.0f), this.xRefP);
            }
            return this.pickerRoundBitmap;
        }

        public void invalidate() {
            this.invalidate = true;
        }
    }
}
