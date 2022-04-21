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
import android.os.Bundle;
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

public abstract class BaseChartView<T extends ChartData, L extends LineViewData> extends View implements ChartPickerDelegate.Listener {
    protected static final boolean ANIMATE_PICKER_SIZES;
    private static final int BOTTOM_SIGNATURE_COUNT = 6;
    private static final int BOTTOM_SIGNATURE_OFFSET = AndroidUtilities.dp(10.0f);
    public static final int BOTTOM_SIGNATURE_START_ALPHA = AndroidUtilities.dp(10.0f);
    private static final int BOTTOM_SIGNATURE_TEXT_HEIGHT = AndroidUtilities.dp(14.0f);
    private static final int DP_1 = AndroidUtilities.dp(1.0f);
    private static final int DP_12 = AndroidUtilities.dp(12.0f);
    private static final int DP_2 = AndroidUtilities.dp(2.0f);
    private static final int DP_5 = AndroidUtilities.dp(5.0f);
    private static final int DP_6 = AndroidUtilities.dp(6.0f);
    public static final float HORIZONTAL_PADDING = AndroidUtilities.dpf2(16.0f);
    public static FastOutSlowInInterpolator INTERPOLATOR = new FastOutSlowInInterpolator();
    private static final int LANDSCAPE_END_PADDING = AndroidUtilities.dp(16.0f);
    private static final float LINE_WIDTH = 1.0f;
    private static final int PICKER_CAPTURE_WIDTH = AndroidUtilities.dp(24.0f);
    protected static final int PICKER_PADDING = AndroidUtilities.dp(16.0f);
    private static final float SELECTED_LINE_WIDTH = AndroidUtilities.dpf2(1.5f);
    public static final int SIGNATURE_TEXT_HEIGHT = AndroidUtilities.dp(18.0f);
    private static final float SIGNATURE_TEXT_SIZE = AndroidUtilities.dpf2(12.0f);
    public static final int TRANSITION_MODE_ALPHA_ENTER = 3;
    public static final int TRANSITION_MODE_CHILD = 1;
    public static final int TRANSITION_MODE_NONE = 0;
    public static final int TRANSITION_MODE_PARENT = 2;
    public static final boolean USE_LINES = (Build.VERSION.SDK_INT < 28);
    private final int ANIM_DURATION = 400;
    ValueAnimator alphaAnimator;
    ValueAnimator alphaBottomAnimator;
    public boolean animateLegentTo = false;
    float animateToMaxHeight = 0.0f;
    float animateToMinHeight = 0.0f;
    protected float animatedToPickerMaxHeight;
    protected float animatedToPickerMinHeight;
    private Bitmap bottomChartBitmap;
    private Canvas bottomChartCanvas;
    ArrayList<ChartBottomSignatureData> bottomSignatureDate = new ArrayList<>(25);
    protected int bottomSignatureOffset;
    Paint bottomSignaturePaint = new TextPaint(1);
    float bottomSignaturePaintAlpha;
    protected boolean canCaptureChartSelection;
    long capturedTime;
    int capturedX;
    int capturedY;
    int chartActiveLineAlpha;
    public RectF chartArea = new RectF();
    int chartBottom;
    protected boolean chartCaptured = false;
    T chartData;
    public float chartEnd;
    public float chartFullWidth;
    ChartHeaderView chartHeaderView;
    public float chartStart;
    public float chartWidth;
    ChartBottomSignatureData currentBottomSignatures;
    public float currentMaxHeight = 250.0f;
    public float currentMinHeight = 0.0f;
    protected DateSelectionListener dateSelectionListener;
    protected boolean drawPointOnSelection = true;
    Paint emptyPaint = new Paint();
    public boolean enabled = true;
    int endXIndex;
    private ValueAnimator.AnimatorUpdateListener heightUpdateListener = new BaseChartView$$ExternalSyntheticLambda0(this);
    int hintLinePaintAlpha;
    ArrayList<ChartHorizontalLinesData> horizontalLines = new ArrayList<>(10);
    boolean invalidatePickerChart = true;
    boolean landscape = false;
    int lastH = 0;
    long lastTime = 0;
    int lastW = 0;
    int lastX;
    int lastY;
    public boolean legendShowing = false;
    public LegendSignatureView legendSignatureView;
    Paint linePaint = new Paint();
    public ArrayList<L> lines = new ArrayList<>();
    Animator maxValueAnimator;
    private ValueAnimator.AnimatorUpdateListener minHeightUpdateListener = new BaseChartView$$ExternalSyntheticLambda1(this);
    private float minMaxUpdateStep;
    Path pathTmp = new Path();
    Animator pickerAnimator;
    public ChartPickerDelegate pickerDelegate = new ChartPickerDelegate(this);
    private ValueAnimator.AnimatorUpdateListener pickerHeightUpdateListener = new ValueAnimator.AnimatorUpdateListener() {
        public void onAnimationUpdate(ValueAnimator animation) {
            BaseChartView.this.pickerMaxHeight = ((Float) animation.getAnimatedValue()).floatValue();
            BaseChartView.this.invalidatePickerChart = true;
            BaseChartView.this.invalidate();
        }
    };
    protected float pickerMaxHeight;
    protected float pickerMinHeight;
    private ValueAnimator.AnimatorUpdateListener pickerMinHeightUpdateListener = new ValueAnimator.AnimatorUpdateListener() {
        public void onAnimationUpdate(ValueAnimator animation) {
            BaseChartView.this.pickerMinHeight = ((Float) animation.getAnimatedValue()).floatValue();
            BaseChartView.this.invalidatePickerChart = true;
            BaseChartView.this.invalidate();
        }
    };
    Rect pickerRect = new Rect();
    Paint pickerSelectorPaint = new Paint(1);
    public float pickerWidth;
    public int pikerHeight = AndroidUtilities.dp(46.0f);
    boolean postTransition = false;
    Paint ripplePaint = new Paint(1);
    protected float selectedCoordinate = -1.0f;
    protected int selectedIndex = -1;
    Paint selectedLinePaint = new Paint();
    public float selectionA = 0.0f;
    ValueAnimator selectionAnimator;
    private ValueAnimator.AnimatorUpdateListener selectionAnimatorListener = new ValueAnimator.AnimatorUpdateListener() {
        public void onAnimationUpdate(ValueAnimator animation) {
            BaseChartView.this.selectionA = ((Float) animation.getAnimatedValue()).floatValue();
            BaseChartView.this.legendSignatureView.setAlpha(BaseChartView.this.selectionA);
            BaseChartView.this.invalidate();
        }
    };
    Paint selectionBackgroundPaint = new Paint(1);
    private Animator.AnimatorListener selectorAnimatorEndListener = new AnimatorListenerAdapter() {
        public void onAnimationEnd(Animator animation) {
            super.onAnimationEnd(animation);
            if (!BaseChartView.this.animateLegentTo) {
                BaseChartView.this.legendShowing = false;
                BaseChartView.this.legendSignatureView.setVisibility(8);
                BaseChartView.this.invalidate();
            }
            BaseChartView.this.postTransition = false;
        }
    };
    public SharedUiComponents sharedUiComponents;
    Paint signaturePaint = new TextPaint(1);
    Paint signaturePaint2 = new TextPaint(1);
    float signaturePaintAlpha;
    private float startFromMax;
    private float startFromMaxH;
    private float startFromMin;
    private float startFromMinH;
    int startXIndex;
    boolean superDraw = false;
    float thresholdMaxHeight = 0.0f;
    protected int tmpI;
    protected int tmpN;
    private final int touchSlop;
    public int transitionMode = 0;
    public TransitionParams transitionParams;
    Paint unactiveBottomChartPaint = new Paint();
    boolean useAlphaSignature = false;
    protected boolean useMinHeight = false;
    VibrationEffect vibrationEffect;
    Paint whiteLinePaint = new Paint(1);

    public interface DateSelectionListener {
        void onDateSelected(long j);
    }

    public abstract L createLineViewData(ChartData.Line line);

    static {
        boolean z = true;
        if (Build.VERSION.SDK_INT <= 21) {
            z = false;
        }
        ANIMATE_PICKER_SIZES = z;
    }

    /* renamed from: lambda$new$0$org-telegram-ui-Charts-BaseChartView  reason: not valid java name */
    public /* synthetic */ void m1622lambda$new$0$orgtelegramuiChartsBaseChartView(ValueAnimator animation) {
        this.currentMaxHeight = ((Float) animation.getAnimatedValue()).floatValue();
        invalidate();
    }

    /* renamed from: lambda$new$1$org-telegram-ui-Charts-BaseChartView  reason: not valid java name */
    public /* synthetic */ void m1623lambda$new$1$orgtelegramuiChartsBaseChartView(ValueAnimator animation) {
        this.currentMinHeight = ((Float) animation.getAnimatedValue()).floatValue();
        invalidate();
    }

    public BaseChartView(Context context) {
        super(context);
        init();
        this.touchSlop = ViewConfiguration.get(context).getScaledTouchSlop();
    }

    /* access modifiers changed from: protected */
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
        setLayerType(2, (Paint) null);
        setWillNotDraw(false);
        LegendSignatureView createLegendView = createLegendView();
        this.legendSignatureView = createLegendView;
        createLegendView.setVisibility(8);
        this.whiteLinePaint.setColor(-1);
        this.whiteLinePaint.setStrokeWidth(AndroidUtilities.dpf2(3.0f));
        this.whiteLinePaint.setStrokeCap(Paint.Cap.ROUND);
        updateColors();
    }

    /* access modifiers changed from: protected */
    public LegendSignatureView createLegendView() {
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
        this.signaturePaintAlpha = ((float) this.signaturePaint.getAlpha()) / 255.0f;
        this.bottomSignaturePaintAlpha = ((float) this.bottomSignaturePaint.getAlpha()) / 255.0f;
        Iterator<L> it = this.lines.iterator();
        while (it.hasNext()) {
            ((LineViewData) it.next()).updateColors();
        }
        if (this.legendShowing && this.selectedIndex < this.chartData.x.length) {
            this.legendSignatureView.setData(this.selectedIndex, this.chartData.x[this.selectedIndex], this.lines, false);
        }
        this.invalidatePickerChart = true;
    }

    /* access modifiers changed from: protected */
    public void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        if (!this.landscape) {
            setMeasuredDimension(View.MeasureSpec.getSize(widthMeasureSpec), View.MeasureSpec.getSize(widthMeasureSpec));
        } else {
            setMeasuredDimension(View.MeasureSpec.getSize(widthMeasureSpec), AndroidUtilities.displaySize.y - AndroidUtilities.dp(56.0f));
        }
        if (getMeasuredWidth() != this.lastW || getMeasuredHeight() != this.lastH) {
            this.lastW = getMeasuredWidth();
            this.lastH = getMeasuredHeight();
            float f = HORIZONTAL_PADDING;
            this.bottomChartBitmap = Bitmap.createBitmap((int) (((float) getMeasuredWidth()) - (f * 2.0f)), this.pikerHeight, Bitmap.Config.ARGB_4444);
            this.bottomChartCanvas = new Canvas(this.bottomChartBitmap);
            this.sharedUiComponents.getPickerMaskBitmap(this.pikerHeight, (int) (((float) getMeasuredWidth()) - (2.0f * f)));
            measureSizes();
            if (this.legendShowing) {
                moveLegend((this.chartFullWidth * this.pickerDelegate.pickerStart) - f);
            }
            onPickerDataChanged(false, true, false);
        }
    }

    private void measureSizes() {
        if (getMeasuredHeight() > 0 && getMeasuredWidth() > 0) {
            float f = HORIZONTAL_PADDING;
            this.pickerWidth = ((float) getMeasuredWidth()) - (2.0f * f);
            this.chartStart = f;
            float measuredWidth = ((float) getMeasuredWidth()) - (this.landscape ? (float) LANDSCAPE_END_PADDING : f);
            this.chartEnd = measuredWidth;
            float f2 = measuredWidth - this.chartStart;
            this.chartWidth = f2;
            this.chartFullWidth = f2 / (this.pickerDelegate.pickerEnd - this.pickerDelegate.pickerStart);
            updateLineSignature();
            this.chartBottom = AndroidUtilities.dp(100.0f);
            this.chartArea.set(this.chartStart - f, 0.0f, this.chartEnd + f, (float) (getMeasuredHeight() - this.chartBottom));
            if (this.chartData != null) {
                this.bottomSignatureOffset = (int) (((float) AndroidUtilities.dp(20.0f)) / (this.pickerWidth / ((float) this.chartData.x.length)));
            }
            measureHeightThreshold();
        }
    }

    private void measureHeightThreshold() {
        int chartHeight = getMeasuredHeight() - this.chartBottom;
        float f = this.animateToMaxHeight;
        if (f != 0.0f && chartHeight != 0) {
            this.thresholdMaxHeight = (f / ((float) chartHeight)) * SIGNATURE_TEXT_SIZE;
        }
    }

    /* access modifiers changed from: protected */
    public void drawPickerChart(Canvas canvas) {
    }

    /* access modifiers changed from: protected */
    public void onDraw(Canvas canvas) {
        if (this.superDraw) {
            super.onDraw(canvas);
            return;
        }
        tick();
        int count = canvas.save();
        canvas.clipRect(0.0f, this.chartArea.top, (float) getMeasuredWidth(), this.chartArea.bottom);
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
                canvas.restoreToCount(count);
                drawBottomSignature(canvas);
                drawPicker(canvas);
                drawSelection(canvas);
                super.onDraw(canvas);
                return;
            }
        }
    }

    /* access modifiers changed from: protected */
    public void tick() {
        float f = this.minMaxUpdateStep;
        if (f != 0.0f) {
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
                    this.currentMaxHeight = f5 + ((f3 - f5) * CubicBezierInterpolator.EASE_OUT.getInterpolation(this.startFromMax));
                }
                invalidate();
            }
            if (this.useMinHeight) {
                float f6 = this.currentMinHeight;
                float f7 = this.animateToMinHeight;
                if (f6 != f7) {
                    float f8 = this.startFromMin + this.minMaxUpdateStep;
                    this.startFromMin = f8;
                    if (f8 > 1.0f) {
                        this.startFromMin = 1.0f;
                        this.currentMinHeight = f7;
                    } else {
                        float f9 = this.startFromMinH;
                        this.currentMinHeight = f9 + ((f7 - f9) * CubicBezierInterpolator.EASE_OUT.getInterpolation(this.startFromMin));
                    }
                    invalidate();
                }
            }
        }
    }

    /* access modifiers changed from: package-private */
    public void drawBottomSignature(Canvas canvas) {
        if (this.chartData != null) {
            this.tmpN = this.bottomSignatureDate.size();
            float transitionAlpha = 1.0f;
            int i = this.transitionMode;
            int i2 = 1;
            if (i == 2) {
                transitionAlpha = 1.0f - this.transitionParams.progress;
            } else if (i == 1) {
                transitionAlpha = this.transitionParams.progress;
            } else if (i == 3) {
                transitionAlpha = this.transitionParams.progress;
            }
            char c = 0;
            this.tmpI = 0;
            while (true) {
                int i3 = this.tmpI;
                if (i3 < this.tmpN) {
                    int resultAlpha = this.bottomSignatureDate.get(i3).alpha;
                    int step = this.bottomSignatureDate.get(this.tmpI).step;
                    if (step == 0) {
                        step = 1;
                    }
                    int start = this.startXIndex - this.bottomSignatureOffset;
                    while (start % step != 0) {
                        start--;
                    }
                    int end = this.endXIndex - this.bottomSignatureOffset;
                    while (true) {
                        if (end % step == 0 && end >= this.chartData.x.length - i2) {
                            break;
                        }
                        Canvas canvas2 = canvas;
                        end++;
                        c = 0;
                    }
                    int i4 = this.bottomSignatureOffset;
                    int start2 = start + i4;
                    int end2 = end + i4;
                    float offset = (this.chartFullWidth * this.pickerDelegate.pickerStart) - HORIZONTAL_PADDING;
                    int i5 = start2;
                    while (i5 < end2) {
                        if (i5 < 0) {
                            Canvas canvas3 = canvas;
                        } else if (i5 >= this.chartData.x.length - i2) {
                            Canvas canvas4 = canvas;
                        } else {
                            float xPoint = (this.chartFullWidth * (((float) (this.chartData.x[i5] - this.chartData.x[c])) / ((float) (this.chartData.x[this.chartData.x.length - i2] - this.chartData.x[c])))) - offset;
                            float xPointOffset = xPoint - ((float) BOTTOM_SIGNATURE_OFFSET);
                            if (xPointOffset > 0.0f) {
                                float f = this.chartWidth;
                                float f2 = HORIZONTAL_PADDING;
                                if (xPointOffset <= f + f2) {
                                    int i6 = BOTTOM_SIGNATURE_START_ALPHA;
                                    if (xPointOffset < ((float) i6)) {
                                        this.bottomSignaturePaint.setAlpha((int) (((float) resultAlpha) * (1.0f - ((((float) i6) - xPointOffset) / ((float) i6))) * this.bottomSignaturePaintAlpha * transitionAlpha));
                                    } else if (xPointOffset > f) {
                                        this.bottomSignaturePaint.setAlpha((int) (((float) resultAlpha) * (1.0f - ((xPointOffset - f) / f2)) * this.bottomSignaturePaintAlpha * transitionAlpha));
                                    } else {
                                        this.bottomSignaturePaint.setAlpha((int) (((float) resultAlpha) * this.bottomSignaturePaintAlpha * transitionAlpha));
                                    }
                                    canvas.drawText(this.chartData.getDayString(i5), xPoint, (float) ((getMeasuredHeight() - this.chartBottom) + BOTTOM_SIGNATURE_TEXT_HEIGHT + AndroidUtilities.dp(3.0f)), this.bottomSignaturePaint);
                                }
                            }
                            Canvas canvas5 = canvas;
                        }
                        i5 += step;
                        c = 0;
                        i2 = 1;
                    }
                    Canvas canvas6 = canvas;
                    i2 = 1;
                    this.tmpI++;
                    c = 0;
                } else {
                    Canvas canvas7 = canvas;
                    return;
                }
            }
        }
    }

    /* access modifiers changed from: protected */
    public void drawBottomLine(Canvas canvas) {
        if (this.chartData != null) {
            float transitionAlpha = 1.0f;
            int i = this.transitionMode;
            if (i == 2) {
                transitionAlpha = 1.0f - this.transitionParams.progress;
            } else if (i == 1) {
                transitionAlpha = this.transitionParams.progress;
            } else if (i == 3) {
                transitionAlpha = this.transitionParams.progress;
            }
            this.linePaint.setAlpha((int) (((float) this.hintLinePaintAlpha) * transitionAlpha));
            this.signaturePaint.setAlpha((int) (this.signaturePaintAlpha * 255.0f * transitionAlpha));
            int textOffset = (int) (((float) SIGNATURE_TEXT_HEIGHT) - this.signaturePaint.getTextSize());
            int y = (getMeasuredHeight() - this.chartBottom) - 1;
            canvas.drawLine(this.chartStart, (float) y, this.chartEnd, (float) y, this.linePaint);
            if (!this.useMinHeight) {
                canvas.drawText("0", HORIZONTAL_PADDING, (float) (y - textOffset), this.signaturePaint);
            }
        }
    }

    /* access modifiers changed from: protected */
    public void drawSelection(Canvas canvas) {
        if (this.selectedIndex >= 0 && this.legendShowing && this.chartData != null) {
            int alpha = (int) (((float) this.chartActiveLineAlpha) * this.selectionA);
            float fullWidth = this.chartWidth / (this.pickerDelegate.pickerEnd - this.pickerDelegate.pickerStart);
            float offset = (this.pickerDelegate.pickerStart * fullWidth) - HORIZONTAL_PADDING;
            if (this.selectedIndex < this.chartData.xPercentage.length) {
                float xPoint = (this.chartData.xPercentage[this.selectedIndex] * fullWidth) - offset;
                this.selectedLinePaint.setAlpha(alpha);
                canvas.drawLine(xPoint, 0.0f, xPoint, this.chartArea.bottom, this.selectedLinePaint);
                if (this.drawPointOnSelection) {
                    this.tmpN = this.lines.size();
                    int i = 0;
                    while (true) {
                        this.tmpI = i;
                        int i2 = this.tmpI;
                        if (i2 < this.tmpN) {
                            LineViewData line = (LineViewData) this.lines.get(i2);
                            if (line.enabled || line.alpha != 0.0f) {
                                float f = this.currentMinHeight;
                                float yPoint = ((float) (getMeasuredHeight() - this.chartBottom)) - (((float) ((getMeasuredHeight() - this.chartBottom) - SIGNATURE_TEXT_HEIGHT)) * ((((float) line.line.y[this.selectedIndex]) - f) / (this.currentMaxHeight - f)));
                                line.selectionPaint.setAlpha((int) (line.alpha * 255.0f * this.selectionA));
                                this.selectionBackgroundPaint.setAlpha((int) (line.alpha * 255.0f * this.selectionA));
                                canvas.drawPoint(xPoint, yPoint, line.selectionPaint);
                                canvas.drawPoint(xPoint, yPoint, this.selectionBackgroundPaint);
                            }
                            i = this.tmpI + 1;
                        } else {
                            return;
                        }
                    }
                }
            }
        }
    }

    /* access modifiers changed from: protected */
    public void drawChart(Canvas canvas) {
    }

    /* access modifiers changed from: protected */
    public void drawHorizontalLines(Canvas canvas, ChartHorizontalLinesData a) {
        int n = a.values.length;
        float additionalOutAlpha = 1.0f;
        if (n > 2) {
            float v = ((float) (a.values[1] - a.values[0])) / (this.currentMaxHeight - this.currentMinHeight);
            if (((double) v) < 0.1d) {
                additionalOutAlpha = v / 0.1f;
            }
        }
        float transitionAlpha = 1.0f;
        int i = this.transitionMode;
        if (i == 2) {
            transitionAlpha = 1.0f - this.transitionParams.progress;
        } else if (i == 1) {
            transitionAlpha = this.transitionParams.progress;
        } else if (i == 3) {
            transitionAlpha = this.transitionParams.progress;
        }
        this.linePaint.setAlpha((int) (((float) a.alpha) * (((float) this.hintLinePaintAlpha) / 255.0f) * transitionAlpha * additionalOutAlpha));
        this.signaturePaint.setAlpha((int) (((float) a.alpha) * this.signaturePaintAlpha * transitionAlpha * additionalOutAlpha));
        int chartHeight = (getMeasuredHeight() - this.chartBottom) - SIGNATURE_TEXT_HEIGHT;
        for (int i2 = true ^ this.useMinHeight; i2 < n; i2++) {
            float f = this.currentMinHeight;
            int y = (int) (((float) (getMeasuredHeight() - this.chartBottom)) - (((float) chartHeight) * ((((float) a.values[i2]) - f) / (this.currentMaxHeight - f))));
            canvas.drawRect(this.chartStart, (float) y, this.chartEnd, (float) (y + 1), this.linePaint);
        }
    }

    /* access modifiers changed from: protected */
    public void drawSignaturesToHorizontalLines(Canvas canvas, ChartHorizontalLinesData a) {
        int n = a.values.length;
        float additionalOutAlpha = 1.0f;
        if (n > 2) {
            float v = ((float) (a.values[1] - a.values[0])) / (this.currentMaxHeight - this.currentMinHeight);
            if (((double) v) < 0.1d) {
                additionalOutAlpha = v / 0.1f;
            }
        }
        float transitionAlpha = 1.0f;
        int i = this.transitionMode;
        if (i == 2) {
            transitionAlpha = 1.0f - this.transitionParams.progress;
        } else if (i == 1) {
            transitionAlpha = this.transitionParams.progress;
        } else if (i == 3) {
            transitionAlpha = this.transitionParams.progress;
        }
        this.linePaint.setAlpha((int) (((float) a.alpha) * (((float) this.hintLinePaintAlpha) / 255.0f) * transitionAlpha * additionalOutAlpha));
        this.signaturePaint.setAlpha((int) (((float) a.alpha) * this.signaturePaintAlpha * transitionAlpha * additionalOutAlpha));
        int measuredHeight = getMeasuredHeight() - this.chartBottom;
        int i2 = SIGNATURE_TEXT_HEIGHT;
        int chartHeight = measuredHeight - i2;
        int textOffset = (int) (((float) i2) - this.signaturePaint.getTextSize());
        for (int i3 = true ^ this.useMinHeight; i3 < n; i3++) {
            float f = this.currentMinHeight;
            canvas.drawText(a.valuesStr[i3], HORIZONTAL_PADDING, (float) (((int) (((float) (getMeasuredHeight() - this.chartBottom)) - (((float) chartHeight) * ((((float) a.values[i3]) - f) / (this.currentMaxHeight - f))))) - textOffset), this.signaturePaint);
        }
    }

    /* access modifiers changed from: package-private */
    /* JADX WARNING: Removed duplicated region for block: B:12:0x0084  */
    /* JADX WARNING: Removed duplicated region for block: B:29:0x00bb  */
    /* JADX WARNING: Removed duplicated region for block: B:30:0x00ed  */
    /* JADX WARNING: Removed duplicated region for block: B:35:0x0101  */
    /* JADX WARNING: Removed duplicated region for block: B:48:0x01e4 A[RETURN] */
    /* JADX WARNING: Removed duplicated region for block: B:49:0x01e5  */
    /* JADX WARNING: Removed duplicated region for block: B:50:0x020a  */
    /* JADX WARNING: Removed duplicated region for block: B:53:0x0243  */
    /* JADX WARNING: Removed duplicated region for block: B:61:0x03c3  */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public void drawPicker(android.graphics.Canvas r29) {
        /*
            r28 = this;
            r0 = r28
            r7 = r29
            T r1 = r0.chartData
            if (r1 != 0) goto L_0x0009
            return
        L_0x0009:
            org.telegram.ui.Charts.ChartPickerDelegate r1 = r0.pickerDelegate
            float r2 = r0.pickerWidth
            r1.pickerWidth = r2
            int r1 = r28.getMeasuredHeight()
            int r2 = PICKER_PADDING
            int r8 = r1 - r2
            int r1 = r28.getMeasuredHeight()
            int r3 = r0.pikerHeight
            int r1 = r1 - r3
            int r9 = r1 - r2
            float r2 = HORIZONTAL_PADDING
            float r1 = r0.pickerWidth
            org.telegram.ui.Charts.ChartPickerDelegate r3 = r0.pickerDelegate
            float r3 = r3.pickerStart
            float r1 = r1 * r3
            float r1 = r1 + r2
            int r1 = (int) r1
            float r3 = r0.pickerWidth
            org.telegram.ui.Charts.ChartPickerDelegate r4 = r0.pickerDelegate
            float r4 = r4.pickerEnd
            float r3 = r3 * r4
            float r3 = r3 + r2
            int r3 = (int) r3
            r4 = 1065353216(0x3var_, float:1.0)
            int r5 = r0.transitionMode
            r6 = 1065353216(0x3var_, float:1.0)
            r10 = 1
            if (r5 != r10) goto L_0x0070
            float r5 = r0.pickerWidth
            org.telegram.ui.Charts.view_data.TransitionParams r11 = r0.transitionParams
            float r11 = r11.pickerStartOut
            float r5 = r5 * r11
            float r5 = r5 + r2
            int r5 = (int) r5
            float r11 = r0.pickerWidth
            org.telegram.ui.Charts.view_data.TransitionParams r12 = r0.transitionParams
            float r12 = r12.pickerEndOut
            float r11 = r11 * r12
            float r11 = r11 + r2
            int r11 = (int) r11
            float r12 = (float) r1
            int r13 = r5 - r1
            float r13 = (float) r13
            org.telegram.ui.Charts.view_data.TransitionParams r14 = r0.transitionParams
            float r14 = r14.progress
            float r14 = r6 - r14
            float r13 = r13 * r14
            float r12 = r12 + r13
            int r1 = (int) r12
            float r12 = (float) r3
            int r13 = r11 - r3
            float r13 = (float) r13
            org.telegram.ui.Charts.view_data.TransitionParams r14 = r0.transitionParams
            float r14 = r14.progress
            float r14 = r6 - r14
            float r13 = r13 * r14
            float r12 = r12 + r13
            int r3 = (int) r12
            goto L_0x007b
        L_0x0070:
            r11 = 3
            if (r5 != r11) goto L_0x007b
            org.telegram.ui.Charts.view_data.TransitionParams r5 = r0.transitionParams
            float r4 = r5.progress
            r11 = r1
            r12 = r3
            r13 = r4
            goto L_0x007e
        L_0x007b:
            r11 = r1
            r12 = r3
            r13 = r4
        L_0x007e:
            T r1 = r0.chartData
            r14 = 1073741824(0x40000000, float:2.0)
            if (r1 == 0) goto L_0x020a
            r1 = 0
            int r2 = r0.transitionMode
            if (r2 != 0) goto L_0x00b8
            r2 = 0
        L_0x008a:
            java.util.ArrayList<L> r3 = r0.lines
            int r3 = r3.size()
            if (r2 >= r3) goto L_0x00b8
            java.util.ArrayList<L> r3 = r0.lines
            java.lang.Object r3 = r3.get(r2)
            org.telegram.ui.Charts.view_data.LineViewData r3 = (org.telegram.ui.Charts.view_data.LineViewData) r3
            android.animation.ValueAnimator r4 = r3.animatorIn
            if (r4 == 0) goto L_0x00a6
            android.animation.ValueAnimator r4 = r3.animatorIn
            boolean r4 = r4.isRunning()
            if (r4 != 0) goto L_0x00b2
        L_0x00a6:
            android.animation.ValueAnimator r4 = r3.animatorOut
            if (r4 == 0) goto L_0x00b5
            android.animation.ValueAnimator r4 = r3.animatorOut
            boolean r4 = r4.isRunning()
            if (r4 == 0) goto L_0x00b5
        L_0x00b2:
            r1 = 1
            r15 = r1
            goto L_0x00b9
        L_0x00b5:
            int r2 = r2 + 1
            goto L_0x008a
        L_0x00b8:
            r15 = r1
        L_0x00b9:
            if (r15 == 0) goto L_0x00ed
            r29.save()
            float r1 = HORIZONTAL_PADDING
            int r2 = r28.getMeasuredHeight()
            int r3 = PICKER_PADDING
            int r2 = r2 - r3
            int r4 = r0.pikerHeight
            int r2 = r2 - r4
            float r2 = (float) r2
            int r4 = r28.getMeasuredWidth()
            float r4 = (float) r4
            float r4 = r4 - r1
            int r5 = r28.getMeasuredHeight()
            int r5 = r5 - r3
            float r5 = (float) r5
            r7.clipRect(r1, r2, r4, r5)
            int r2 = r28.getMeasuredHeight()
            int r2 = r2 - r3
            int r3 = r0.pikerHeight
            int r2 = r2 - r3
            float r2 = (float) r2
            r7.translate(r1, r2)
            r28.drawPickerChart(r29)
            r29.restore()
            goto L_0x00fe
        L_0x00ed:
            boolean r1 = r0.invalidatePickerChart
            if (r1 == 0) goto L_0x00fe
            android.graphics.Bitmap r1 = r0.bottomChartBitmap
            r2 = 0
            r1.eraseColor(r2)
            android.graphics.Canvas r1 = r0.bottomChartCanvas
            r0.drawPickerChart(r1)
            r0.invalidatePickerChart = r2
        L_0x00fe:
            r1 = 2
            if (r15 != 0) goto L_0x01df
            int r2 = r0.transitionMode
            r3 = 1132396544(0x437var_, float:255.0)
            if (r2 != r1) goto L_0x0154
            int r2 = r8 - r9
            int r2 = r2 + r9
            int r2 = r2 >> r10
            float r2 = (float) r2
            float r4 = HORIZONTAL_PADDING
            float r5 = r0.pickerWidth
            org.telegram.ui.Charts.view_data.TransitionParams r1 = r0.transitionParams
            float r1 = r1.xPercentage
            float r5 = r5 * r1
            float r5 = r5 + r4
            android.graphics.Paint r1 = r0.emptyPaint
            org.telegram.ui.Charts.view_data.TransitionParams r10 = r0.transitionParams
            float r10 = r10.progress
            float r10 = r6 - r10
            float r10 = r10 * r3
            int r3 = (int) r10
            r1.setAlpha(r3)
            r29.save()
            float r1 = (float) r9
            int r3 = r28.getMeasuredWidth()
            float r3 = (float) r3
            float r3 = r3 - r4
            float r10 = (float) r8
            r7.clipRect(r4, r1, r3, r10)
            org.telegram.ui.Charts.view_data.TransitionParams r1 = r0.transitionParams
            float r1 = r1.progress
            float r1 = r1 * r14
            float r1 = r1 + r6
            r7.scale(r1, r6, r5, r2)
            android.graphics.Bitmap r1 = r0.bottomChartBitmap
            int r3 = r28.getMeasuredHeight()
            int r6 = PICKER_PADDING
            int r3 = r3 - r6
            int r6 = r0.pikerHeight
            int r3 = r3 - r6
            float r3 = (float) r3
            android.graphics.Paint r6 = r0.emptyPaint
            r7.drawBitmap(r1, r4, r3, r6)
            r29.restore()
            goto L_0x01df
        L_0x0154:
            r1 = 1
            if (r2 != r1) goto L_0x01c1
            int r2 = r8 - r9
            int r2 = r2 + r9
            int r2 = r2 >> r1
            float r1 = (float) r2
            float r2 = HORIZONTAL_PADDING
            float r4 = r0.pickerWidth
            org.telegram.ui.Charts.view_data.TransitionParams r5 = r0.transitionParams
            float r5 = r5.xPercentage
            float r4 = r4 * r5
            float r4 = r4 + r2
            org.telegram.ui.Charts.view_data.TransitionParams r5 = r0.transitionParams
            float r5 = r5.xPercentage
            r10 = 1056964608(0x3var_, float:0.5)
            int r5 = (r5 > r10 ? 1 : (r5 == r10 ? 0 : -1))
            if (r5 <= 0) goto L_0x0178
            float r5 = r0.pickerWidth
            org.telegram.ui.Charts.view_data.TransitionParams r10 = r0.transitionParams
            float r10 = r10.xPercentage
            goto L_0x0180
        L_0x0178:
            float r5 = r0.pickerWidth
            org.telegram.ui.Charts.view_data.TransitionParams r10 = r0.transitionParams
            float r10 = r10.xPercentage
            float r10 = r6 - r10
        L_0x0180:
            float r5 = r5 * r10
            org.telegram.ui.Charts.view_data.TransitionParams r10 = r0.transitionParams
            float r10 = r10.progress
            float r5 = r5 * r10
            r29.save()
            float r10 = r4 - r5
            float r14 = (float) r9
            float r6 = r4 + r5
            float r3 = (float) r8
            r7.clipRect(r10, r14, r6, r3)
            android.graphics.Paint r3 = r0.emptyPaint
            org.telegram.ui.Charts.view_data.TransitionParams r6 = r0.transitionParams
            float r6 = r6.progress
            r10 = 1132396544(0x437var_, float:255.0)
            float r6 = r6 * r10
            int r6 = (int) r6
            r3.setAlpha(r6)
            org.telegram.ui.Charts.view_data.TransitionParams r3 = r0.transitionParams
            float r3 = r3.progress
            r6 = 1065353216(0x3var_, float:1.0)
            r7.scale(r3, r6, r4, r1)
            android.graphics.Bitmap r3 = r0.bottomChartBitmap
            int r6 = r28.getMeasuredHeight()
            int r10 = PICKER_PADDING
            int r6 = r6 - r10
            int r10 = r0.pikerHeight
            int r6 = r6 - r10
            float r6 = (float) r6
            android.graphics.Paint r10 = r0.emptyPaint
            r7.drawBitmap(r3, r2, r6, r10)
            r29.restore()
            goto L_0x01df
        L_0x01c1:
            android.graphics.Paint r1 = r0.emptyPaint
            r2 = 1132396544(0x437var_, float:255.0)
            float r3 = r13 * r2
            int r2 = (int) r3
            r1.setAlpha(r2)
            android.graphics.Bitmap r1 = r0.bottomChartBitmap
            float r2 = HORIZONTAL_PADDING
            int r3 = r28.getMeasuredHeight()
            int r4 = PICKER_PADDING
            int r3 = r3 - r4
            int r4 = r0.pikerHeight
            int r3 = r3 - r4
            float r3 = (float) r3
            android.graphics.Paint r4 = r0.emptyPaint
            r7.drawBitmap(r1, r2, r3, r4)
        L_0x01df:
            int r1 = r0.transitionMode
            r2 = 2
            if (r1 != r2) goto L_0x01e5
            return
        L_0x01e5:
            float r10 = HORIZONTAL_PADDING
            float r3 = (float) r9
            int r14 = DP_12
            int r1 = r11 + r14
            float r4 = (float) r1
            float r5 = (float) r8
            android.graphics.Paint r6 = r0.unactiveBottomChartPaint
            r1 = r29
            r2 = r10
            r1.drawRect(r2, r3, r4, r5, r6)
            int r1 = r12 - r14
            float r2 = (float) r1
            float r3 = (float) r9
            int r1 = r28.getMeasuredWidth()
            float r1 = (float) r1
            float r4 = r1 - r10
            float r5 = (float) r8
            android.graphics.Paint r6 = r0.unactiveBottomChartPaint
            r1 = r29
            r1.drawRect(r2, r3, r4, r5, r6)
            goto L_0x021a
        L_0x020a:
            float r3 = (float) r9
            int r1 = r28.getMeasuredWidth()
            float r1 = (float) r1
            float r4 = r1 - r2
            float r5 = (float) r8
            android.graphics.Paint r6 = r0.unactiveBottomChartPaint
            r1 = r29
            r1.drawRect(r2, r3, r4, r5, r6)
        L_0x021a:
            org.telegram.ui.Charts.BaseChartView$SharedUiComponents r1 = r0.sharedUiComponents
            int r2 = r0.pikerHeight
            int r3 = r28.getMeasuredWidth()
            float r3 = (float) r3
            float r4 = HORIZONTAL_PADDING
            r5 = 1073741824(0x40000000, float:2.0)
            float r14 = r4 * r5
            float r3 = r3 - r14
            int r3 = (int) r3
            android.graphics.Bitmap r1 = r1.getPickerMaskBitmap(r2, r3)
            int r2 = r28.getMeasuredHeight()
            int r3 = PICKER_PADDING
            int r2 = r2 - r3
            int r3 = r0.pikerHeight
            int r2 = r2 - r3
            float r2 = (float) r2
            android.graphics.Paint r3 = r0.emptyPaint
            r7.drawBitmap(r1, r4, r2, r3)
            T r1 = r0.chartData
            if (r1 == 0) goto L_0x03c3
            android.graphics.Rect r1 = r0.pickerRect
            r1.set(r11, r9, r12, r8)
            org.telegram.ui.Charts.ChartPickerDelegate r1 = r0.pickerDelegate
            android.graphics.Rect r1 = r1.middlePickerArea
            android.graphics.Rect r2 = r0.pickerRect
            r1.set(r2)
            android.graphics.Path r1 = r0.pathTmp
            android.graphics.Rect r2 = r0.pickerRect
            int r2 = r2.left
            float r2 = (float) r2
            android.graphics.Rect r3 = r0.pickerRect
            int r3 = r3.top
            int r10 = DP_1
            int r3 = r3 - r10
            float r3 = (float) r3
            android.graphics.Rect r4 = r0.pickerRect
            int r4 = r4.left
            int r14 = DP_12
            int r4 = r4 + r14
            float r4 = (float) r4
            android.graphics.Rect r5 = r0.pickerRect
            int r5 = r5.bottom
            int r5 = r5 + r10
            float r5 = (float) r5
            int r15 = DP_6
            float r6 = (float) r15
            r16 = r13
            float r13 = (float) r15
            r24 = 1
            r25 = 0
            r26 = 0
            r27 = 1
            r17 = r1
            r18 = r2
            r19 = r3
            r20 = r4
            r21 = r5
            r22 = r6
            r23 = r13
            android.graphics.Path r1 = RoundedRect(r17, r18, r19, r20, r21, r22, r23, r24, r25, r26, r27)
            android.graphics.Paint r2 = r0.pickerSelectorPaint
            r7.drawPath(r1, r2)
            android.graphics.Path r1 = r0.pathTmp
            android.graphics.Rect r2 = r0.pickerRect
            int r2 = r2.right
            int r2 = r2 - r14
            float r2 = (float) r2
            android.graphics.Rect r3 = r0.pickerRect
            int r3 = r3.top
            int r3 = r3 - r10
            float r3 = (float) r3
            android.graphics.Rect r4 = r0.pickerRect
            int r4 = r4.right
            float r4 = (float) r4
            android.graphics.Rect r5 = r0.pickerRect
            int r5 = r5.bottom
            int r5 = r5 + r10
            float r5 = (float) r5
            float r6 = (float) r15
            float r13 = (float) r15
            r24 = 0
            r25 = 1
            r26 = 1
            r27 = 0
            r17 = r1
            r18 = r2
            r19 = r3
            r20 = r4
            r21 = r5
            r22 = r6
            r23 = r13
            android.graphics.Path r1 = RoundedRect(r17, r18, r19, r20, r21, r22, r23, r24, r25, r26, r27)
            android.graphics.Paint r2 = r0.pickerSelectorPaint
            r7.drawPath(r1, r2)
            android.graphics.Rect r1 = r0.pickerRect
            int r1 = r1.left
            int r1 = r1 + r14
            float r2 = (float) r1
            android.graphics.Rect r1 = r0.pickerRect
            int r1 = r1.bottom
            float r3 = (float) r1
            android.graphics.Rect r1 = r0.pickerRect
            int r1 = r1.right
            int r1 = r1 - r14
            float r4 = (float) r1
            android.graphics.Rect r1 = r0.pickerRect
            int r1 = r1.bottom
            int r1 = r1 + r10
            float r5 = (float) r1
            android.graphics.Paint r6 = r0.pickerSelectorPaint
            r1 = r29
            r1.drawRect(r2, r3, r4, r5, r6)
            android.graphics.Rect r1 = r0.pickerRect
            int r1 = r1.left
            int r1 = r1 + r14
            float r2 = (float) r1
            android.graphics.Rect r1 = r0.pickerRect
            int r1 = r1.top
            int r1 = r1 - r10
            float r3 = (float) r1
            android.graphics.Rect r1 = r0.pickerRect
            int r1 = r1.right
            int r1 = r1 - r14
            float r4 = (float) r1
            android.graphics.Rect r1 = r0.pickerRect
            int r1 = r1.top
            float r5 = (float) r1
            android.graphics.Paint r6 = r0.pickerSelectorPaint
            r1 = r29
            r1.drawRect(r2, r3, r4, r5, r6)
            android.graphics.Rect r1 = r0.pickerRect
            int r1 = r1.left
            int r1 = r1 + r15
            float r2 = (float) r1
            android.graphics.Rect r1 = r0.pickerRect
            int r1 = r1.centerY()
            int r1 = r1 - r15
            float r3 = (float) r1
            android.graphics.Rect r1 = r0.pickerRect
            int r1 = r1.left
            int r1 = r1 + r15
            float r4 = (float) r1
            android.graphics.Rect r1 = r0.pickerRect
            int r1 = r1.centerY()
            int r1 = r1 + r15
            float r5 = (float) r1
            android.graphics.Paint r6 = r0.whiteLinePaint
            r1 = r29
            r1.drawLine(r2, r3, r4, r5, r6)
            android.graphics.Rect r1 = r0.pickerRect
            int r1 = r1.right
            int r1 = r1 - r15
            float r2 = (float) r1
            android.graphics.Rect r1 = r0.pickerRect
            int r1 = r1.centerY()
            int r1 = r1 - r15
            float r3 = (float) r1
            android.graphics.Rect r1 = r0.pickerRect
            int r1 = r1.right
            int r1 = r1 - r15
            float r4 = (float) r1
            android.graphics.Rect r1 = r0.pickerRect
            int r1 = r1.centerY()
            int r1 = r1 + r15
            float r5 = (float) r1
            android.graphics.Paint r6 = r0.whiteLinePaint
            r1 = r29
            r1.drawLine(r2, r3, r4, r5, r6)
            org.telegram.ui.Charts.ChartPickerDelegate r1 = r0.pickerDelegate
            org.telegram.ui.Charts.ChartPickerDelegate$CapturesData r1 = r1.getMiddleCaptured()
            android.graphics.Rect r2 = r0.pickerRect
            int r2 = r2.bottom
            android.graphics.Rect r3 = r0.pickerRect
            int r3 = r3.top
            int r2 = r2 - r3
            r3 = 1
            int r2 = r2 >> r3
            android.graphics.Rect r3 = r0.pickerRect
            int r3 = r3.top
            int r3 = r3 + r2
            if (r1 == 0) goto L_0x0368
            goto L_0x03a6
        L_0x0368:
            org.telegram.ui.Charts.ChartPickerDelegate r4 = r0.pickerDelegate
            org.telegram.ui.Charts.ChartPickerDelegate$CapturesData r4 = r4.getLeftCaptured()
            org.telegram.ui.Charts.ChartPickerDelegate r5 = r0.pickerDelegate
            org.telegram.ui.Charts.ChartPickerDelegate$CapturesData r5 = r5.getRightCaptured()
            if (r4 == 0) goto L_0x038d
            android.graphics.Rect r6 = r0.pickerRect
            int r6 = r6.left
            int r10 = DP_5
            int r6 = r6 + r10
            float r6 = (float) r6
            float r10 = (float) r3
            float r13 = (float) r2
            float r14 = r4.aValue
            float r13 = r13 * r14
            int r14 = DP_2
            float r14 = (float) r14
            float r13 = r13 - r14
            android.graphics.Paint r14 = r0.ripplePaint
            r7.drawCircle(r6, r10, r13, r14)
        L_0x038d:
            if (r5 == 0) goto L_0x03a6
            android.graphics.Rect r6 = r0.pickerRect
            int r6 = r6.right
            int r10 = DP_5
            int r6 = r6 - r10
            float r6 = (float) r6
            float r10 = (float) r3
            float r13 = (float) r2
            float r14 = r5.aValue
            float r13 = r13 * r14
            int r14 = DP_2
            float r14 = (float) r14
            float r13 = r13 - r14
            android.graphics.Paint r14 = r0.ripplePaint
            r7.drawCircle(r6, r10, r13, r14)
        L_0x03a6:
            r4 = r11
            org.telegram.ui.Charts.ChartPickerDelegate r5 = r0.pickerDelegate
            android.graphics.Rect r5 = r5.leftPickerArea
            int r6 = PICKER_CAPTURE_WIDTH
            int r10 = r4 - r6
            int r13 = r6 >> 1
            int r13 = r13 + r4
            r5.set(r10, r9, r13, r8)
            r4 = r12
            org.telegram.ui.Charts.ChartPickerDelegate r5 = r0.pickerDelegate
            android.graphics.Rect r5 = r5.rightPickerArea
            int r10 = r6 >> 1
            int r10 = r4 - r10
            int r6 = r6 + r4
            r5.set(r10, r9, r6, r8)
            goto L_0x03c5
        L_0x03c3:
            r16 = r13
        L_0x03c5:
            return
        */
        throw new UnsupportedOperationException("Method not decompiled: org.telegram.ui.Charts.BaseChartView.drawPicker(android.graphics.Canvas):void");
    }

    private void setMaxMinValue(int newMaxHeight, int newMinHeight, boolean animated) {
        setMaxMinValue(newMaxHeight, newMinHeight, animated, false, false);
    }

    /* access modifiers changed from: protected */
    public void setMaxMinValue(int newMaxHeight, int newMinHeight, boolean animated, boolean force, boolean useAnimator) {
        int i = newMaxHeight;
        boolean heightChanged = true;
        if (Math.abs(((float) ChartHorizontalLinesData.lookupHeight(newMaxHeight)) - this.animateToMaxHeight) < this.thresholdMaxHeight || i == 0) {
            heightChanged = false;
        }
        if (heightChanged || ((float) i) != this.animateToMinHeight) {
            final ChartHorizontalLinesData newData = createHorizontalLinesData(newMaxHeight, newMinHeight);
            int newMaxHeight2 = newData.values[newData.values.length - 1];
            int newMinHeight2 = newData.values[0];
            if (!useAnimator) {
                float f = this.currentMaxHeight;
                float f2 = this.currentMinHeight;
                float k = (f - f2) / ((float) (newMaxHeight2 - newMinHeight2));
                if (k > 1.0f) {
                    k = ((float) (newMaxHeight2 - newMinHeight2)) / (f - f2);
                }
                float s = 0.045f;
                if (((double) k) > 0.7d) {
                    s = 0.1f;
                } else if (((double) k) < 0.1d) {
                    s = 0.03f;
                }
                boolean update = false;
                if (((float) newMaxHeight2) != this.animateToMaxHeight) {
                    update = true;
                }
                if (this.useMinHeight && ((float) newMinHeight2) != this.animateToMinHeight) {
                    update = true;
                }
                if (update) {
                    Animator animator = this.maxValueAnimator;
                    if (animator != null) {
                        animator.removeAllListeners();
                        this.maxValueAnimator.cancel();
                    }
                    this.startFromMaxH = this.currentMaxHeight;
                    this.startFromMinH = this.currentMinHeight;
                    this.startFromMax = 0.0f;
                    this.startFromMin = 0.0f;
                    this.minMaxUpdateStep = s;
                }
            }
            this.animateToMaxHeight = (float) newMaxHeight2;
            this.animateToMinHeight = (float) newMinHeight2;
            measureHeightThreshold();
            long t = System.currentTimeMillis();
            if (t - this.lastTime >= 320 || force) {
                this.lastTime = t;
                ValueAnimator valueAnimator = this.alphaAnimator;
                if (valueAnimator != null) {
                    valueAnimator.removeAllListeners();
                    this.alphaAnimator.cancel();
                }
                if (!animated) {
                    this.currentMaxHeight = (float) newMaxHeight2;
                    this.currentMinHeight = (float) newMinHeight2;
                    this.horizontalLines.clear();
                    this.horizontalLines.add(newData);
                    newData.alpha = 255;
                    return;
                }
                this.horizontalLines.add(newData);
                if (useAnimator) {
                    Animator animator2 = this.maxValueAnimator;
                    if (animator2 != null) {
                        animator2.removeAllListeners();
                        this.maxValueAnimator.cancel();
                    }
                    this.minMaxUpdateStep = 0.0f;
                    AnimatorSet animatorSet = new AnimatorSet();
                    animatorSet.playTogether(new Animator[]{createAnimator(this.currentMaxHeight, (float) newMaxHeight2, this.heightUpdateListener)});
                    if (this.useMinHeight) {
                        animatorSet.playTogether(new Animator[]{createAnimator(this.currentMinHeight, (float) newMinHeight2, this.minHeightUpdateListener)});
                    }
                    this.maxValueAnimator = animatorSet;
                    animatorSet.start();
                }
                int n = this.horizontalLines.size();
                for (int i2 = 0; i2 < n; i2++) {
                    ChartHorizontalLinesData a = this.horizontalLines.get(i2);
                    if (a != newData) {
                        a.fixedAlpha = a.alpha;
                    }
                }
                ValueAnimator createAnimator = createAnimator(0.0f, 255.0f, new BaseChartView$$ExternalSyntheticLambda3(this, newData));
                this.alphaAnimator = createAnimator;
                createAnimator.addListener(new AnimatorListenerAdapter() {
                    public void onAnimationEnd(Animator animation) {
                        BaseChartView.this.horizontalLines.clear();
                        BaseChartView.this.horizontalLines.add(newData);
                    }
                });
                this.alphaAnimator.start();
            }
        }
    }

    /* renamed from: lambda$setMaxMinValue$2$org-telegram-ui-Charts-BaseChartView  reason: not valid java name */
    public /* synthetic */ void m1626lambda$setMaxMinValue$2$orgtelegramuiChartsBaseChartView(ChartHorizontalLinesData newData, ValueAnimator animation) {
        newData.alpha = (int) ((Float) animation.getAnimatedValue()).floatValue();
        Iterator<ChartHorizontalLinesData> it = this.horizontalLines.iterator();
        while (it.hasNext()) {
            ChartHorizontalLinesData a = it.next();
            if (a != newData) {
                a.alpha = (int) ((((float) a.fixedAlpha) / 255.0f) * ((float) (255 - newData.alpha)));
            }
        }
        invalidate();
    }

    /* access modifiers changed from: protected */
    public ChartHorizontalLinesData createHorizontalLinesData(int newMaxHeight, int newMinHeight) {
        return new ChartHorizontalLinesData(newMaxHeight, newMinHeight, this.useMinHeight);
    }

    /* access modifiers changed from: package-private */
    public ValueAnimator createAnimator(float f1, float f2, ValueAnimator.AnimatorUpdateListener l) {
        ValueAnimator a = ValueAnimator.ofFloat(new float[]{f1, f2});
        a.setDuration(400);
        a.setInterpolator(INTERPOLATOR);
        a.addUpdateListener(l);
        return a;
    }

    public boolean onTouchEvent(MotionEvent event) {
        boolean disable = false;
        if (this.chartData == null) {
            return false;
        }
        if (!this.enabled) {
            this.pickerDelegate.uncapture(event, event.getActionIndex());
            getParent().requestDisallowInterceptTouchEvent(false);
            this.chartCaptured = false;
            return false;
        }
        int x = (int) event.getX(event.getActionIndex());
        int y = (int) event.getY(event.getActionIndex());
        switch (event.getActionMasked()) {
            case 0:
                this.capturedTime = System.currentTimeMillis();
                getParent().requestDisallowInterceptTouchEvent(true);
                if (this.pickerDelegate.capture(x, y, event.getActionIndex())) {
                    return true;
                }
                this.lastX = x;
                this.capturedX = x;
                this.lastY = y;
                this.capturedY = y;
                if (!this.chartArea.contains((float) x, (float) y)) {
                    return false;
                }
                if (this.selectedIndex < 0 || !this.animateLegentTo) {
                    this.chartCaptured = true;
                    selectXOnChart(x, y);
                }
                return true;
            case 1:
            case 3:
                if (this.pickerDelegate.uncapture(event, event.getActionIndex())) {
                    return true;
                }
                if (this.chartArea.contains((float) this.capturedX, (float) this.capturedY) && !this.chartCaptured) {
                    animateLegend(false);
                }
                this.pickerDelegate.uncapture();
                updateLineSignature();
                getParent().requestDisallowInterceptTouchEvent(false);
                this.chartCaptured = false;
                onActionUp();
                invalidate();
                int min = 0;
                if (this.useMinHeight) {
                    min = findMinValue(this.startXIndex, this.endXIndex);
                }
                setMaxMinValue(findMaxValue(this.startXIndex, this.endXIndex), min, true, true, false);
                return true;
            case 2:
                int dx = x - this.lastX;
                int dy = y - this.lastY;
                if (this.pickerDelegate.captured()) {
                    boolean rez = this.pickerDelegate.move(x, y, event.getActionIndex());
                    if (event.getPointerCount() > 1) {
                        this.pickerDelegate.move((int) event.getX(1), (int) event.getY(1), 1);
                    }
                    getParent().requestDisallowInterceptTouchEvent(rez);
                    return true;
                }
                if (this.chartCaptured) {
                    if (this.canCaptureChartSelection && System.currentTimeMillis() - this.capturedTime > 200) {
                        disable = true;
                    } else if (Math.abs(dx) > Math.abs(dy) || Math.abs(dy) < this.touchSlop) {
                        disable = true;
                    }
                    this.lastX = x;
                    this.lastY = y;
                    getParent().requestDisallowInterceptTouchEvent(disable);
                    selectXOnChart(x, y);
                } else if (this.chartArea.contains((float) this.capturedX, (float) this.capturedY)) {
                    int dxCaptured = this.capturedX - x;
                    int dyCaptured = this.capturedY - y;
                    if (Math.sqrt((double) ((dxCaptured * dxCaptured) + (dyCaptured * dyCaptured))) > ((double) this.touchSlop) || System.currentTimeMillis() - this.capturedTime > 200) {
                        this.chartCaptured = true;
                        selectXOnChart(x, y);
                    }
                }
                return true;
            case 5:
                return this.pickerDelegate.capture(x, y, event.getActionIndex());
            case 6:
                this.pickerDelegate.uncapture(event, event.getActionIndex());
                return true;
            default:
                return false;
        }
    }

    /* access modifiers changed from: protected */
    public void onActionUp() {
    }

    /* access modifiers changed from: protected */
    public void selectXOnChart(int x, int y) {
        int oldSelectedX = this.selectedIndex;
        if (this.chartData != null) {
            float offset = (this.chartFullWidth * this.pickerDelegate.pickerStart) - HORIZONTAL_PADDING;
            float xP = (((float) x) + offset) / this.chartFullWidth;
            this.selectedCoordinate = xP;
            if (xP < 0.0f) {
                this.selectedIndex = 0;
                this.selectedCoordinate = 0.0f;
            } else if (xP > 1.0f) {
                this.selectedIndex = this.chartData.x.length - 1;
                this.selectedCoordinate = 1.0f;
            } else {
                int findIndex = this.chartData.findIndex(this.startXIndex, this.endXIndex, xP);
                this.selectedIndex = findIndex;
                if (findIndex + 1 < this.chartData.xPercentage.length) {
                    if (Math.abs(this.chartData.xPercentage[this.selectedIndex + 1] - xP) < Math.abs(this.chartData.xPercentage[this.selectedIndex] - xP)) {
                        this.selectedIndex++;
                    }
                }
            }
            int i = this.selectedIndex;
            int i2 = this.endXIndex;
            if (i > i2) {
                this.selectedIndex = i2;
            }
            int i3 = this.selectedIndex;
            int i4 = this.startXIndex;
            if (i3 < i4) {
                this.selectedIndex = i4;
            }
            if (oldSelectedX != this.selectedIndex) {
                this.legendShowing = true;
                animateLegend(true);
                moveLegend(offset);
                DateSelectionListener dateSelectionListener2 = this.dateSelectionListener;
                if (dateSelectionListener2 != null) {
                    dateSelectionListener2.onDateSelected(getSelectedDate());
                }
                runSmoothHaptic();
                invalidate();
            }
        }
    }

    /* access modifiers changed from: protected */
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

    public void animateLegend(boolean show) {
        moveLegend();
        if (this.animateLegentTo != show) {
            this.animateLegentTo = show;
            ValueAnimator valueAnimator = this.selectionAnimator;
            if (valueAnimator != null) {
                valueAnimator.removeAllListeners();
                this.selectionAnimator.cancel();
            }
            ValueAnimator duration = createAnimator(this.selectionA, show ? 1.0f : 0.0f, this.selectionAnimatorListener).setDuration(200);
            this.selectionAnimator = duration;
            duration.addListener(this.selectorAnimatorEndListener);
            this.selectionAnimator.start();
        }
    }

    public void moveLegend(float offset) {
        int i;
        float lXPoint;
        T t = this.chartData;
        if (t != null && (i = this.selectedIndex) != -1 && this.legendShowing) {
            this.legendSignatureView.setData(i, t.x[this.selectedIndex], this.lines, false);
            this.legendSignatureView.setVisibility(0);
            this.legendSignatureView.measure(View.MeasureSpec.makeMeasureSpec(getMeasuredWidth(), Integer.MIN_VALUE), View.MeasureSpec.makeMeasureSpec(getMeasuredHeight(), Integer.MIN_VALUE));
            float lXPoint2 = (this.chartData.xPercentage[this.selectedIndex] * this.chartFullWidth) - offset;
            if (lXPoint2 > (this.chartStart + this.chartWidth) / 2.0f) {
                lXPoint = lXPoint2 - ((float) (this.legendSignatureView.getWidth() + DP_5));
            } else {
                lXPoint = lXPoint2 + ((float) DP_5);
            }
            if (lXPoint < 0.0f) {
                lXPoint = 0.0f;
            } else if (((float) this.legendSignatureView.getMeasuredWidth()) + lXPoint > ((float) getMeasuredWidth())) {
                lXPoint = (float) (getMeasuredWidth() - this.legendSignatureView.getMeasuredWidth());
            }
            this.legendSignatureView.setTranslationX(lXPoint);
        }
    }

    public int findMaxValue(int startXIndex2, int endXIndex2) {
        int lineMax;
        int linesSize = this.lines.size();
        int maxValue = 0;
        for (int j = 0; j < linesSize; j++) {
            if (((LineViewData) this.lines.get(j)).enabled && (lineMax = ((LineViewData) this.lines.get(j)).line.segmentTree.rMaxQ(startXIndex2, endXIndex2)) > maxValue) {
                maxValue = lineMax;
            }
        }
        return maxValue;
    }

    public int findMinValue(int startXIndex2, int endXIndex2) {
        int lineMin;
        int linesSize = this.lines.size();
        int minValue = Integer.MAX_VALUE;
        for (int j = 0; j < linesSize; j++) {
            if (((LineViewData) this.lines.get(j)).enabled && (lineMin = ((LineViewData) this.lines.get(j)).line.segmentTree.rMinQ(startXIndex2, endXIndex2)) < minValue) {
                minValue = lineMin;
            }
        }
        return minValue;
    }

    public void setData(T chartData2) {
        if (this.chartData != chartData2) {
            invalidate();
            this.lines.clear();
            if (!(chartData2 == null || chartData2.lines == null)) {
                for (int i = 0; i < chartData2.lines.size(); i++) {
                    this.lines.add(createLineViewData(chartData2.lines.get(i)));
                }
            }
            clearSelection();
            this.chartData = chartData2;
            if (chartData2 != null) {
                if (chartData2.x[0] == 0) {
                    this.pickerDelegate.pickerStart = 0.0f;
                    this.pickerDelegate.pickerEnd = 1.0f;
                } else {
                    this.pickerDelegate.minDistance = getMinDistance();
                    if (this.pickerDelegate.pickerEnd - this.pickerDelegate.pickerStart < this.pickerDelegate.minDistance) {
                        ChartPickerDelegate chartPickerDelegate = this.pickerDelegate;
                        chartPickerDelegate.pickerStart = chartPickerDelegate.pickerEnd - this.pickerDelegate.minDistance;
                        if (this.pickerDelegate.pickerStart < 0.0f) {
                            this.pickerDelegate.pickerStart = 0.0f;
                            this.pickerDelegate.pickerEnd = 1.0f;
                        }
                    }
                }
            }
        }
        measureSizes();
        if (chartData2 != null) {
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
        this.pickerDelegate.pickerStart = 0.7f;
        this.pickerDelegate.pickerEnd = 1.0f;
        this.pickerMinHeight = 0.0f;
        this.pickerMaxHeight = 0.0f;
        this.horizontalLines.clear();
        Animator animator = this.maxValueAnimator;
        if (animator != null) {
            animator.cancel();
        }
        ValueAnimator valueAnimator = this.alphaAnimator;
        if (valueAnimator != null) {
            valueAnimator.removeAllListeners();
            this.alphaAnimator.cancel();
        }
    }

    /* access modifiers changed from: protected */
    public float getMinDistance() {
        T t = this.chartData;
        if (t == null) {
            return 0.1f;
        }
        int n = t.x.length;
        if (n < 5) {
            return 1.0f;
        }
        float r = 5.0f / ((float) n);
        if (r < 0.1f) {
            return 0.1f;
        }
        return r;
    }

    /* access modifiers changed from: protected */
    public void initPickerMaxHeight() {
        Iterator<L> it = this.lines.iterator();
        while (it.hasNext()) {
            LineViewData l = (LineViewData) it.next();
            if (l.enabled && ((float) l.line.maxValue) > this.pickerMaxHeight) {
                this.pickerMaxHeight = (float) l.line.maxValue;
            }
            if (l.enabled && ((float) l.line.minValue) < this.pickerMinHeight) {
                this.pickerMinHeight = (float) l.line.minValue;
            }
            float f = this.pickerMaxHeight;
            float f2 = this.pickerMinHeight;
            if (f == f2) {
                this.pickerMaxHeight = f + 1.0f;
                this.pickerMinHeight = f2 - 1.0f;
            }
        }
    }

    public void onPickerDataChanged() {
        onPickerDataChanged(true, false, false);
    }

    public void onPickerDataChanged(boolean animated, boolean force, boolean useAniamtor) {
        if (this.chartData != null) {
            this.chartFullWidth = this.chartWidth / (this.pickerDelegate.pickerEnd - this.pickerDelegate.pickerStart);
            updateIndexes();
            setMaxMinValue(findMaxValue(this.startXIndex, this.endXIndex), this.useMinHeight ? findMinValue(this.startXIndex, this.endXIndex) : 0, animated, force, useAniamtor);
            if (this.legendShowing && !force) {
                animateLegend(false);
                moveLegend((this.chartFullWidth * this.pickerDelegate.pickerStart) - HORIZONTAL_PADDING);
            }
            invalidate();
        }
    }

    public void onPickerJumpTo(float start, float end, boolean force) {
        T t = this.chartData;
        if (t != null) {
            if (force) {
                int startXIndex2 = t.findStartIndex(Math.max(start, 0.0f));
                int endXIndex2 = this.chartData.findEndIndex(startXIndex2, Math.min(end, 1.0f));
                setMaxMinValue(findMaxValue(startXIndex2, endXIndex2), findMinValue(startXIndex2, endXIndex2), true, true, false);
                animateLegend(false);
                return;
            }
            updateIndexes();
            invalidate();
        }
    }

    /* access modifiers changed from: protected */
    public void updateIndexes() {
        T t = this.chartData;
        if (t != null) {
            int findStartIndex = t.findStartIndex(Math.max(this.pickerDelegate.pickerStart, 0.0f));
            this.startXIndex = findStartIndex;
            int findEndIndex = this.chartData.findEndIndex(findStartIndex, Math.min(this.pickerDelegate.pickerEnd, 1.0f));
            this.endXIndex = findEndIndex;
            int i = this.startXIndex;
            if (findEndIndex < i) {
                this.endXIndex = i;
            }
            ChartHeaderView chartHeaderView2 = this.chartHeaderView;
            if (chartHeaderView2 != null) {
                chartHeaderView2.setDates(this.chartData.x[this.startXIndex], this.chartData.x[this.endXIndex]);
            }
            updateLineSignature();
        }
    }

    private void updateLineSignature() {
        T t = this.chartData;
        if (t != null && this.chartWidth != 0.0f) {
            updateDates((int) ((this.chartWidth / (this.chartFullWidth * t.oneDayPercentage)) / 6.0f));
        }
    }

    private void updateDates(int step) {
        ChartBottomSignatureData chartBottomSignatureData = this.currentBottomSignatures;
        if (chartBottomSignatureData == null || step >= chartBottomSignatureData.stepMax || step <= this.currentBottomSignatures.stepMin) {
            int step2 = Integer.highestOneBit(step) << 1;
            ChartBottomSignatureData chartBottomSignatureData2 = this.currentBottomSignatures;
            if (chartBottomSignatureData2 == null || chartBottomSignatureData2.step != step2) {
                ValueAnimator valueAnimator = this.alphaBottomAnimator;
                if (valueAnimator != null) {
                    valueAnimator.removeAllListeners();
                    this.alphaBottomAnimator.cancel();
                }
                double d = (double) step2;
                double d2 = (double) step2;
                Double.isNaN(d2);
                Double.isNaN(d);
                double d3 = (double) step2;
                double d4 = (double) step2;
                Double.isNaN(d4);
                Double.isNaN(d3);
                final ChartBottomSignatureData data = new ChartBottomSignatureData(step2, (int) (d + (d2 * 0.2d)), (int) (d3 - (d4 * 0.2d)));
                data.alpha = 255;
                if (this.currentBottomSignatures == null) {
                    this.currentBottomSignatures = data;
                    data.alpha = 255;
                    this.bottomSignatureDate.add(data);
                    return;
                }
                this.currentBottomSignatures = data;
                this.tmpN = this.bottomSignatureDate.size();
                for (int i = 0; i < this.tmpN; i++) {
                    ChartBottomSignatureData a = this.bottomSignatureDate.get(i);
                    a.fixedAlpha = a.alpha;
                }
                this.bottomSignatureDate.add(data);
                if (this.bottomSignatureDate.size() > 2) {
                    this.bottomSignatureDate.remove(0);
                }
                ValueAnimator duration = createAnimator(0.0f, 1.0f, new BaseChartView$$ExternalSyntheticLambda2(this, data)).setDuration(200);
                this.alphaBottomAnimator = duration;
                duration.addListener(new AnimatorListenerAdapter() {
                    public void onAnimationEnd(Animator animation) {
                        super.onAnimationEnd(animation);
                        BaseChartView.this.bottomSignatureDate.clear();
                        BaseChartView.this.bottomSignatureDate.add(data);
                    }
                });
                this.alphaBottomAnimator.start();
            }
        }
    }

    /* renamed from: lambda$updateDates$3$org-telegram-ui-Charts-BaseChartView  reason: not valid java name */
    public /* synthetic */ void m1627lambda$updateDates$3$orgtelegramuiChartsBaseChartView(ChartBottomSignatureData data, ValueAnimator animation) {
        float alpha = ((Float) animation.getAnimatedValue()).floatValue();
        Iterator<ChartBottomSignatureData> it = this.bottomSignatureDate.iterator();
        while (it.hasNext()) {
            ChartBottomSignatureData a = it.next();
            if (a == data) {
                data.alpha = (int) (255.0f * alpha);
            } else {
                a.alpha = (int) ((1.0f - alpha) * ((float) a.fixedAlpha));
            }
        }
        invalidate();
    }

    public void onCheckChanged() {
        onPickerDataChanged(true, true, true);
        this.tmpN = this.lines.size();
        int i = 0;
        while (true) {
            this.tmpI = i;
            int i2 = this.tmpI;
            if (i2 >= this.tmpN) {
                break;
            }
            LineViewData lineViewData = (LineViewData) this.lines.get(i2);
            if (lineViewData.enabled && lineViewData.animatorOut != null) {
                lineViewData.animatorOut.cancel();
            }
            if (!lineViewData.enabled && lineViewData.animatorIn != null) {
                lineViewData.animatorIn.cancel();
            }
            if (lineViewData.enabled && lineViewData.alpha != 1.0f) {
                if (lineViewData.animatorIn == null || !lineViewData.animatorIn.isRunning()) {
                    lineViewData.animatorIn = createAnimator(lineViewData.alpha, 1.0f, new BaseChartView$$ExternalSyntheticLambda4(this, lineViewData));
                    lineViewData.animatorIn.start();
                } else {
                    i = this.tmpI + 1;
                }
            }
            if (!lineViewData.enabled && lineViewData.alpha != 0.0f && (lineViewData.animatorOut == null || !lineViewData.animatorOut.isRunning())) {
                lineViewData.animatorOut = createAnimator(lineViewData.alpha, 0.0f, new BaseChartView$$ExternalSyntheticLambda5(this, lineViewData));
                lineViewData.animatorOut.start();
            }
            i = this.tmpI + 1;
        }
        updatePickerMinMaxHeight();
        if (this.legendShowing) {
            this.legendSignatureView.setData(this.selectedIndex, this.chartData.x[this.selectedIndex], this.lines, true);
        }
    }

    /* renamed from: lambda$onCheckChanged$4$org-telegram-ui-Charts-BaseChartView  reason: not valid java name */
    public /* synthetic */ void m1624lambda$onCheckChanged$4$orgtelegramuiChartsBaseChartView(LineViewData lineViewData, ValueAnimator animation) {
        lineViewData.alpha = ((Float) animation.getAnimatedValue()).floatValue();
        this.invalidatePickerChart = true;
        invalidate();
    }

    /* renamed from: lambda$onCheckChanged$5$org-telegram-ui-Charts-BaseChartView  reason: not valid java name */
    public /* synthetic */ void m1625lambda$onCheckChanged$5$orgtelegramuiChartsBaseChartView(LineViewData lineViewData, ValueAnimator animation) {
        lineViewData.alpha = ((Float) animation.getAnimatedValue()).floatValue();
        this.invalidatePickerChart = true;
        invalidate();
    }

    /* access modifiers changed from: protected */
    public void updatePickerMinMaxHeight() {
        if (ANIMATE_PICKER_SIZES) {
            int max = 0;
            int min = Integer.MAX_VALUE;
            Iterator<L> it = this.lines.iterator();
            while (it.hasNext()) {
                LineViewData l = (LineViewData) it.next();
                if (l.enabled && l.line.maxValue > max) {
                    max = l.line.maxValue;
                }
                if (l.enabled && l.line.minValue < min) {
                    min = l.line.minValue;
                }
            }
            if ((min != Integer.MAX_VALUE && ((float) min) != this.animatedToPickerMinHeight) || (max > 0 && ((float) max) != this.animatedToPickerMaxHeight)) {
                this.animatedToPickerMaxHeight = (float) max;
                Animator animator = this.pickerAnimator;
                if (animator != null) {
                    animator.cancel();
                }
                AnimatorSet animatorSet = new AnimatorSet();
                animatorSet.playTogether(new Animator[]{createAnimator(this.pickerMaxHeight, this.animatedToPickerMaxHeight, this.pickerHeightUpdateListener), createAnimator(this.pickerMinHeight, this.animatedToPickerMinHeight, this.pickerMinHeightUpdateListener)});
                this.pickerAnimator = animatorSet;
                animatorSet.start();
            }
        }
    }

    public void setLandscape(boolean b) {
        this.landscape = b;
    }

    public void saveState(Bundle outState) {
        if (outState != null) {
            outState.putFloat("chart_start", this.pickerDelegate.pickerStart);
            outState.putFloat("chart_end", this.pickerDelegate.pickerEnd);
            ArrayList<L> arrayList = this.lines;
            if (arrayList != null) {
                int n = arrayList.size();
                boolean[] bArray = new boolean[n];
                for (int i = 0; i < n; i++) {
                    bArray[i] = ((LineViewData) this.lines.get(i)).enabled;
                }
                outState.putBooleanArray("chart_line_enabled", bArray);
            }
        }
    }

    public void setHeader(ChartHeaderView chartHeaderView2) {
        this.chartHeaderView = chartHeaderView2;
    }

    public long getSelectedDate() {
        if (this.selectedIndex < 0) {
            return -1;
        }
        return this.chartData.x[this.selectedIndex];
    }

    public void clearSelection() {
        this.selectedIndex = -1;
        this.legendShowing = false;
        this.animateLegentTo = false;
        this.legendSignatureView.setVisibility(8);
        this.selectionA = 0.0f;
    }

    public void selectDate(long activeZoom) {
        this.selectedIndex = Arrays.binarySearch(this.chartData.x, activeZoom);
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

    public void updatePicker(ChartData chartData2, long d) {
        int n = chartData2.x.length;
        long startOfDay = d - (d % 86400000);
        long endOfDay = (86400000 + startOfDay) - 1;
        int startIndex = 0;
        int endIndex = 0;
        for (int i = 0; i < n; i++) {
            if (startOfDay > chartData2.x[i]) {
                startIndex = i;
            }
            if (endOfDay > chartData2.x[i]) {
                endIndex = i;
            }
        }
        this.pickerDelegate.pickerStart = chartData2.xPercentage[startIndex];
        this.pickerDelegate.pickerEnd = chartData2.xPercentage[endIndex];
    }

    public void moveLegend() {
        moveLegend((this.chartFullWidth * this.pickerDelegate.pickerStart) - HORIZONTAL_PADDING);
    }

    public void requestLayout() {
        super.requestLayout();
    }

    public static Path RoundedRect(Path path, float left, float top, float right, float bottom, float rx, float ry, boolean tl, boolean tr, boolean br, boolean bl) {
        Path path2 = path;
        float f = right;
        path.reset();
        float rx2 = rx < 0.0f ? 0.0f : rx;
        float ry2 = ry < 0.0f ? 0.0f : ry;
        float width = f - left;
        float height = bottom - top;
        if (rx2 > width / 2.0f) {
            rx2 = width / 2.0f;
        }
        if (ry2 > height / 2.0f) {
            ry2 = height / 2.0f;
        }
        float widthMinusCorners = width - (rx2 * 2.0f);
        float heightMinusCorners = height - (2.0f * ry2);
        path.moveTo(right, top + ry2);
        if (tr) {
            path.rQuadTo(0.0f, -ry2, -rx2, -ry2);
        } else {
            path.rLineTo(0.0f, -ry2);
            path.rLineTo(-rx2, 0.0f);
        }
        path.rLineTo(-widthMinusCorners, 0.0f);
        if (tl) {
            path.rQuadTo(-rx2, 0.0f, -rx2, ry2);
        } else {
            path.rLineTo(-rx2, 0.0f);
            path.rLineTo(0.0f, ry2);
        }
        path.rLineTo(0.0f, heightMinusCorners);
        if (bl) {
            path.rQuadTo(0.0f, ry2, rx2, ry2);
        } else {
            path.rLineTo(0.0f, ry2);
            path.rLineTo(rx2, 0.0f);
        }
        path.rLineTo(widthMinusCorners, 0.0f);
        if (br) {
            path.rQuadTo(rx2, 0.0f, rx2, -ry2);
        } else {
            path.rLineTo(rx2, 0.0f);
            path.rLineTo(0.0f, -ry2);
        }
        path.rLineTo(0.0f, -heightMinusCorners);
        path.close();
        return path2;
    }

    public void setDateSelectionListener(DateSelectionListener dateSelectionListener2) {
        this.dateSelectionListener = dateSelectionListener2;
    }

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

        /* access modifiers changed from: package-private */
        public Bitmap getPickerMaskBitmap(int h, int w) {
            if (((h + w) << 10) != this.k || this.invalidate) {
                this.invalidate = false;
                this.k = (h + w) << 10;
                this.pickerRoundBitmap = Bitmap.createBitmap(w, h, Bitmap.Config.ARGB_8888);
                this.canvas = new Canvas(this.pickerRoundBitmap);
                this.rectF.set(0.0f, 0.0f, (float) w, (float) h);
                this.canvas.drawColor(Theme.getColor("windowBackgroundWhite"));
                this.canvas.drawRoundRect(this.rectF, (float) AndroidUtilities.dp(4.0f), (float) AndroidUtilities.dp(4.0f), this.xRefP);
            }
            return this.pickerRoundBitmap;
        }

        public void invalidate() {
            this.invalidate = true;
        }
    }

    public void fillTransitionParams(TransitionParams params) {
    }
}
