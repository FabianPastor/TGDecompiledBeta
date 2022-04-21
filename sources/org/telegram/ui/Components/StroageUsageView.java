package org.telegram.ui.Components;

import android.animation.ValueAnimator;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.drawable.Drawable;
import android.text.SpannableString;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.TextView;
import androidx.core.graphics.ColorUtils;
import org.telegram.messenger.AndroidUtilities;
import org.telegram.messenger.LocaleController;
import org.telegram.ui.ActionBar.Theme;
import org.telegram.ui.Cells.TextSettingsCell;
import org.telegram.ui.Components.voip.CellFlickerDrawable;

public class StroageUsageView extends FrameLayout {
    /* access modifiers changed from: private */
    public Paint bgPaint = new Paint();
    /* access modifiers changed from: private */
    public boolean calculating;
    float calculatingProgress;
    boolean calculatingProgressIncrement;
    TextView calculatingTextView;
    CellFlickerDrawable cellFlickerDrawable = new CellFlickerDrawable(220, 255);
    View divider;
    EllipsizeSpanAnimator ellipsizeSpanAnimator;
    TextView freeSizeTextView;
    int lastProgressColor;
    public ViewGroup legendLayout;
    private Paint paintCalculcating = new Paint(1);
    /* access modifiers changed from: private */
    public Paint paintFill = new Paint(1);
    /* access modifiers changed from: private */
    public Paint paintProgress = new Paint(1);
    /* access modifiers changed from: private */
    public Paint paintProgress2 = new Paint(1);
    float progress;
    float progress2;
    ProgressView progressView;
    TextView telegramCacheTextView;
    TextView telegramDatabaseTextView;
    TextSettingsCell textSettingsCell;
    private long totalDeviceFreeSize;
    private long totalDeviceSize;
    private long totalSize;
    TextView totlaSizeTextView;
    ValueAnimator valueAnimator;
    ValueAnimator valueAnimator2;

    /* JADX INFO: super call moved to the top of the method (can break code semantics) */
    public StroageUsageView(Context context) {
        super(context);
        Context context2 = context;
        setWillNotDraw(false);
        this.cellFlickerDrawable.drawFrame = false;
        this.paintFill.setStrokeWidth((float) AndroidUtilities.dp(6.0f));
        this.paintCalculcating.setStrokeWidth((float) AndroidUtilities.dp(6.0f));
        this.paintProgress.setStrokeWidth((float) AndroidUtilities.dp(6.0f));
        this.paintProgress2.setStrokeWidth((float) AndroidUtilities.dp(6.0f));
        this.paintFill.setStrokeCap(Paint.Cap.ROUND);
        this.paintCalculcating.setStrokeCap(Paint.Cap.ROUND);
        this.paintProgress.setStrokeCap(Paint.Cap.ROUND);
        this.paintProgress2.setStrokeCap(Paint.Cap.ROUND);
        ProgressView progressView2 = new ProgressView(context2);
        this.progressView = progressView2;
        addView(progressView2, LayoutHelper.createFrame(-1, -2.0f));
        LinearLayout linearLayout = new LinearLayout(context2);
        linearLayout.setOrientation(1);
        addView(linearLayout, LayoutHelper.createFrame(-1, -2.0f));
        AnonymousClass1 r7 = new FrameLayout(context2) {
            /* access modifiers changed from: protected */
            public void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
                super.onMeasure(View.MeasureSpec.makeMeasureSpec(View.MeasureSpec.getSize(widthMeasureSpec), NUM), heightMeasureSpec);
                int currentW = 0;
                int currentH = 0;
                int n = getChildCount();
                int lastChildH = 0;
                for (int i = 0; i < n; i++) {
                    if (getChildAt(i).getVisibility() != 8) {
                        if (getChildAt(i).getMeasuredWidth() + currentW > View.MeasureSpec.getSize(widthMeasureSpec)) {
                            currentW = 0;
                            currentH += getChildAt(i).getMeasuredHeight() + AndroidUtilities.dp(8.0f);
                        }
                        currentW += getChildAt(i).getMeasuredWidth() + AndroidUtilities.dp(16.0f);
                        lastChildH = getChildAt(i).getMeasuredHeight() + currentH;
                    }
                }
                setMeasuredDimension(getMeasuredWidth(), lastChildH);
            }

            /* access modifiers changed from: protected */
            public void onLayout(boolean changed, int left, int top, int right, int bottom) {
                int currentW = 0;
                int currentH = 0;
                int n = getChildCount();
                for (int i = 0; i < n; i++) {
                    if (getChildAt(i).getVisibility() != 8) {
                        if (getChildAt(i).getMeasuredWidth() + currentW > getMeasuredWidth()) {
                            currentW = 0;
                            currentH += getChildAt(i).getMeasuredHeight() + AndroidUtilities.dp(8.0f);
                        }
                        getChildAt(i).layout(currentW, currentH, getChildAt(i).getMeasuredWidth() + currentW, getChildAt(i).getMeasuredHeight() + currentH);
                        currentW += getChildAt(i).getMeasuredWidth() + AndroidUtilities.dp(16.0f);
                    }
                }
            }
        };
        this.legendLayout = r7;
        linearLayout.addView(r7, LayoutHelper.createLinear(-1, -2, 21.0f, 40.0f, 21.0f, 16.0f));
        TextView textView = new TextView(context2);
        this.calculatingTextView = textView;
        textView.setTextColor(Theme.getColor("windowBackgroundWhiteGrayText"));
        String calculatingString = LocaleController.getString("CalculatingSize", NUM);
        int indexOfDots = calculatingString.indexOf("...");
        if (indexOfDots >= 0) {
            SpannableString spannableString = new SpannableString(calculatingString);
            EllipsizeSpanAnimator ellipsizeSpanAnimator2 = new EllipsizeSpanAnimator(this.calculatingTextView);
            this.ellipsizeSpanAnimator = ellipsizeSpanAnimator2;
            ellipsizeSpanAnimator2.wrap(spannableString, indexOfDots);
            this.calculatingTextView.setText(spannableString);
        } else {
            this.calculatingTextView.setText(calculatingString);
        }
        TextView textView2 = new TextView(context2);
        this.telegramCacheTextView = textView2;
        textView2.setCompoundDrawablePadding(AndroidUtilities.dp(6.0f));
        this.telegramCacheTextView.setTextColor(Theme.getColor("windowBackgroundWhiteGrayText"));
        TextView textView3 = new TextView(context2);
        this.telegramDatabaseTextView = textView3;
        textView3.setCompoundDrawablePadding(AndroidUtilities.dp(6.0f));
        this.telegramDatabaseTextView.setTextColor(Theme.getColor("windowBackgroundWhiteGrayText"));
        TextView textView4 = new TextView(context2);
        this.freeSizeTextView = textView4;
        textView4.setCompoundDrawablePadding(AndroidUtilities.dp(6.0f));
        this.freeSizeTextView.setTextColor(Theme.getColor("windowBackgroundWhiteGrayText"));
        TextView textView5 = new TextView(context2);
        this.totlaSizeTextView = textView5;
        textView5.setCompoundDrawablePadding(AndroidUtilities.dp(6.0f));
        this.totlaSizeTextView.setTextColor(Theme.getColor("windowBackgroundWhiteGrayText"));
        this.lastProgressColor = Theme.getColor("player_progress");
        this.telegramCacheTextView.setCompoundDrawablesWithIntrinsicBounds(Theme.createCircleDrawable(AndroidUtilities.dp(10.0f), this.lastProgressColor), (Drawable) null, (Drawable) null, (Drawable) null);
        this.telegramCacheTextView.setCompoundDrawablePadding(AndroidUtilities.dp(6.0f));
        this.freeSizeTextView.setCompoundDrawablesWithIntrinsicBounds(Theme.createCircleDrawable(AndroidUtilities.dp(10.0f), ColorUtils.setAlphaComponent(this.lastProgressColor, 64)), (Drawable) null, (Drawable) null, (Drawable) null);
        this.freeSizeTextView.setCompoundDrawablePadding(AndroidUtilities.dp(6.0f));
        this.totlaSizeTextView.setCompoundDrawablesWithIntrinsicBounds(Theme.createCircleDrawable(AndroidUtilities.dp(10.0f), ColorUtils.setAlphaComponent(this.lastProgressColor, 127)), (Drawable) null, (Drawable) null, (Drawable) null);
        this.totlaSizeTextView.setCompoundDrawablePadding(AndroidUtilities.dp(6.0f));
        this.telegramDatabaseTextView.setCompoundDrawablesWithIntrinsicBounds(Theme.createCircleDrawable(AndroidUtilities.dp(10.0f), this.lastProgressColor), (Drawable) null, (Drawable) null, (Drawable) null);
        this.telegramDatabaseTextView.setCompoundDrawablePadding(AndroidUtilities.dp(6.0f));
        this.legendLayout.addView(this.calculatingTextView, LayoutHelper.createFrame(-2, -2.0f));
        this.legendLayout.addView(this.telegramDatabaseTextView, LayoutHelper.createFrame(-2, -2.0f));
        this.legendLayout.addView(this.telegramCacheTextView, LayoutHelper.createFrame(-2, -2.0f));
        this.legendLayout.addView(this.totlaSizeTextView, LayoutHelper.createFrame(-2, -2.0f));
        this.legendLayout.addView(this.freeSizeTextView, LayoutHelper.createFrame(-2, -2.0f));
        View view = new View(getContext());
        this.divider = view;
        linearLayout.addView(view, LayoutHelper.createLinear(-1, -2, 0, 21, 0, 0, 0));
        this.divider.getLayoutParams().height = 1;
        this.divider.setBackgroundColor(Theme.getColor("divider"));
        TextSettingsCell textSettingsCell2 = new TextSettingsCell(getContext());
        this.textSettingsCell = textSettingsCell2;
        linearLayout.addView(textSettingsCell2, LayoutHelper.createLinear(-1, -2));
    }

    public void setStorageUsage(boolean calculating2, long database, long totalSize2, long totalDeviceFreeSize2, long totalDeviceSize2) {
        boolean z = calculating2;
        long j = totalSize2;
        long j2 = totalDeviceFreeSize2;
        long j3 = totalDeviceSize2;
        this.calculating = z;
        this.totalSize = j;
        this.totalDeviceFreeSize = j2;
        this.totalDeviceSize = j3;
        this.freeSizeTextView.setText(LocaleController.formatString("TotalDeviceFreeSize", NUM, AndroidUtilities.formatFileSize(totalDeviceFreeSize2)));
        this.totlaSizeTextView.setText(LocaleController.formatString("TotalDeviceSize", NUM, AndroidUtilities.formatFileSize(j3 - j2)));
        if (z) {
            this.calculatingTextView.setVisibility(0);
            this.telegramCacheTextView.setVisibility(8);
            this.freeSizeTextView.setVisibility(8);
            this.totlaSizeTextView.setVisibility(8);
            this.telegramDatabaseTextView.setVisibility(8);
            this.divider.setVisibility(8);
            this.textSettingsCell.setVisibility(8);
            this.progress = 0.0f;
            this.progress2 = 0.0f;
            EllipsizeSpanAnimator ellipsizeSpanAnimator2 = this.ellipsizeSpanAnimator;
            if (ellipsizeSpanAnimator2 != null) {
                ellipsizeSpanAnimator2.addView(this.calculatingTextView);
            }
        } else {
            EllipsizeSpanAnimator ellipsizeSpanAnimator3 = this.ellipsizeSpanAnimator;
            if (ellipsizeSpanAnimator3 != null) {
                ellipsizeSpanAnimator3.removeView(this.calculatingTextView);
            }
            this.calculatingTextView.setVisibility(8);
            if (j > 0) {
                this.divider.setVisibility(0);
                this.textSettingsCell.setVisibility(0);
                this.telegramCacheTextView.setVisibility(0);
                this.telegramDatabaseTextView.setVisibility(8);
                this.textSettingsCell.setText(LocaleController.getString("ClearTelegramCache", NUM), false);
                this.telegramCacheTextView.setText(LocaleController.formatString("TelegramCacheSize", NUM, AndroidUtilities.formatFileSize(j + database)));
            } else {
                this.telegramCacheTextView.setVisibility(8);
                this.telegramDatabaseTextView.setVisibility(0);
                this.telegramDatabaseTextView.setText(LocaleController.formatString("LocalDatabaseSize", NUM, AndroidUtilities.formatFileSize(database)));
                this.divider.setVisibility(8);
                this.textSettingsCell.setVisibility(8);
            }
            this.freeSizeTextView.setVisibility(0);
            this.totlaSizeTextView.setVisibility(0);
            float p = ((float) (j + database)) / ((float) j3);
            float p2 = ((float) (j3 - j2)) / ((float) j3);
            if (this.progress != p) {
                ValueAnimator valueAnimator3 = this.valueAnimator;
                if (valueAnimator3 != null) {
                    valueAnimator3.cancel();
                }
                ValueAnimator ofFloat = ValueAnimator.ofFloat(new float[]{this.progress, p});
                this.valueAnimator = ofFloat;
                ofFloat.addUpdateListener(new StroageUsageView$$ExternalSyntheticLambda0(this));
                this.valueAnimator.start();
            }
            if (this.progress2 != p2) {
                ValueAnimator valueAnimator4 = this.valueAnimator2;
                if (valueAnimator4 != null) {
                    valueAnimator4.cancel();
                }
                ValueAnimator ofFloat2 = ValueAnimator.ofFloat(new float[]{this.progress2, p2});
                this.valueAnimator2 = ofFloat2;
                ofFloat2.addUpdateListener(new StroageUsageView$$ExternalSyntheticLambda1(this));
                this.valueAnimator2.start();
            }
        }
        this.textSettingsCell.setTextColor(Theme.getColor("windowBackgroundWhiteBlackText"));
        requestLayout();
    }

    /* renamed from: lambda$setStorageUsage$0$org-telegram-ui-Components-StroageUsageView  reason: not valid java name */
    public /* synthetic */ void m4447xb842a2b9(ValueAnimator animation) {
        this.progress = ((Float) animation.getAnimatedValue()).floatValue();
        invalidate();
    }

    /* renamed from: lambda$setStorageUsage$1$org-telegram-ui-Components-StroageUsageView  reason: not valid java name */
    public /* synthetic */ void m4448x52e3653a(ValueAnimator animation) {
        this.progress2 = ((Float) animation.getAnimatedValue()).floatValue();
        invalidate();
    }

    public void invalidate() {
        super.invalidate();
        this.progressView.invalidate();
        if (this.lastProgressColor != Theme.getColor("player_progress")) {
            this.lastProgressColor = Theme.getColor("player_progress");
            this.telegramCacheTextView.setCompoundDrawablesWithIntrinsicBounds(Theme.createCircleDrawable(AndroidUtilities.dp(10.0f), this.lastProgressColor), (Drawable) null, (Drawable) null, (Drawable) null);
            this.telegramCacheTextView.setCompoundDrawablePadding(AndroidUtilities.dp(6.0f));
            this.telegramDatabaseTextView.setCompoundDrawablesWithIntrinsicBounds(Theme.createCircleDrawable(AndroidUtilities.dp(10.0f), this.lastProgressColor), (Drawable) null, (Drawable) null, (Drawable) null);
            this.telegramDatabaseTextView.setCompoundDrawablePadding(AndroidUtilities.dp(6.0f));
            this.freeSizeTextView.setCompoundDrawablesWithIntrinsicBounds(Theme.createCircleDrawable(AndroidUtilities.dp(10.0f), ColorUtils.setAlphaComponent(this.lastProgressColor, 64)), (Drawable) null, (Drawable) null, (Drawable) null);
            this.freeSizeTextView.setCompoundDrawablePadding(AndroidUtilities.dp(6.0f));
            this.totlaSizeTextView.setCompoundDrawablesWithIntrinsicBounds(Theme.createCircleDrawable(AndroidUtilities.dp(10.0f), ColorUtils.setAlphaComponent(this.lastProgressColor, 127)), (Drawable) null, (Drawable) null, (Drawable) null);
            this.totlaSizeTextView.setCompoundDrawablePadding(AndroidUtilities.dp(6.0f));
        }
        this.textSettingsCell.setTextColor(Theme.getColor("windowBackgroundWhiteBlackText"));
        this.divider.setBackgroundColor(Theme.getColor("divider"));
    }

    private class ProgressView extends View {
        public ProgressView(Context context) {
            super(context);
        }

        /* access modifiers changed from: protected */
        public void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
            super.onMeasure(widthMeasureSpec, View.MeasureSpec.makeMeasureSpec(AndroidUtilities.dp(40.0f), NUM));
        }

        /* access modifiers changed from: protected */
        public void onDraw(Canvas canvas) {
            super.onDraw(canvas);
            int color = Theme.getColor("player_progress");
            StroageUsageView.this.paintFill.setColor(color);
            StroageUsageView.this.paintProgress.setColor(color);
            StroageUsageView.this.paintProgress2.setColor(color);
            StroageUsageView.this.paintProgress.setAlpha(255);
            StroageUsageView.this.paintProgress2.setAlpha(82);
            StroageUsageView.this.paintFill.setAlpha(46);
            StroageUsageView.this.bgPaint.setColor(Theme.getColor("windowBackgroundWhite"));
            canvas.drawLine((float) AndroidUtilities.dp(24.0f), (float) AndroidUtilities.dp(20.0f), (float) (getMeasuredWidth() - AndroidUtilities.dp(24.0f)), (float) AndroidUtilities.dp(20.0f), StroageUsageView.this.paintFill);
            if (StroageUsageView.this.calculating || StroageUsageView.this.calculatingProgress != 0.0f) {
                if (!StroageUsageView.this.calculating) {
                    StroageUsageView.this.calculatingProgress -= 0.10666667f;
                    if (StroageUsageView.this.calculatingProgress < 0.0f) {
                        StroageUsageView.this.calculatingProgress = 0.0f;
                    }
                } else if (StroageUsageView.this.calculatingProgressIncrement) {
                    StroageUsageView.this.calculatingProgress += 0.024615385f;
                    if (StroageUsageView.this.calculatingProgress > 1.0f) {
                        StroageUsageView.this.calculatingProgress = 1.0f;
                        StroageUsageView.this.calculatingProgressIncrement = false;
                    }
                } else {
                    StroageUsageView.this.calculatingProgress -= 0.024615385f;
                    if (StroageUsageView.this.calculatingProgress < 0.0f) {
                        StroageUsageView.this.calculatingProgress = 0.0f;
                        StroageUsageView.this.calculatingProgressIncrement = true;
                    }
                }
                invalidate();
                AndroidUtilities.rectTmp.set((float) AndroidUtilities.dp(24.0f), (float) AndroidUtilities.dp(17.0f), (float) (getMeasuredWidth() - AndroidUtilities.dp(24.0f)), (float) AndroidUtilities.dp(23.0f));
                StroageUsageView.this.cellFlickerDrawable.setParentWidth(getMeasuredWidth());
                StroageUsageView.this.cellFlickerDrawable.draw(canvas, AndroidUtilities.rectTmp, (float) AndroidUtilities.dp(3.0f));
            } else {
                Canvas canvas2 = canvas;
            }
            int currentP = AndroidUtilities.dp(24.0f);
            if (!StroageUsageView.this.calculating) {
                int progressWidth = (int) (((float) (getMeasuredWidth() - (AndroidUtilities.dp(24.0f) * 2))) * StroageUsageView.this.progress2);
                int left = AndroidUtilities.dp(24.0f) + progressWidth;
                canvas.drawLine((float) currentP, (float) AndroidUtilities.dp(20.0f), (float) (AndroidUtilities.dp(24.0f) + progressWidth), (float) AndroidUtilities.dp(20.0f), StroageUsageView.this.paintProgress2);
                canvas.drawRect((float) left, (float) (AndroidUtilities.dp(20.0f) - AndroidUtilities.dp(3.0f)), (float) (AndroidUtilities.dp(3.0f) + left), (float) (AndroidUtilities.dp(20.0f) + AndroidUtilities.dp(3.0f)), StroageUsageView.this.bgPaint);
            }
            if (!StroageUsageView.this.calculating) {
                int progressWidth2 = (int) (((float) (getMeasuredWidth() - (AndroidUtilities.dp(24.0f) * 2))) * StroageUsageView.this.progress);
                if (progressWidth2 < AndroidUtilities.dp(1.0f)) {
                    progressWidth2 = AndroidUtilities.dp(1.0f);
                }
                int left2 = AndroidUtilities.dp(24.0f) + progressWidth2;
                Canvas canvas3 = canvas;
                canvas3.drawLine((float) currentP, (float) AndroidUtilities.dp(20.0f), (float) (AndroidUtilities.dp(24.0f) + progressWidth2), (float) AndroidUtilities.dp(20.0f), StroageUsageView.this.paintProgress);
                canvas3.drawRect((float) left2, (float) (AndroidUtilities.dp(20.0f) - AndroidUtilities.dp(3.0f)), (float) (AndroidUtilities.dp(3.0f) + left2), (float) (AndroidUtilities.dp(20.0f) + AndroidUtilities.dp(3.0f)), StroageUsageView.this.bgPaint);
            }
        }
    }

    /* access modifiers changed from: protected */
    public void onAttachedToWindow() {
        super.onAttachedToWindow();
        EllipsizeSpanAnimator ellipsizeSpanAnimator2 = this.ellipsizeSpanAnimator;
        if (ellipsizeSpanAnimator2 != null) {
            ellipsizeSpanAnimator2.onAttachedToWindow();
        }
    }

    /* access modifiers changed from: protected */
    public void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        EllipsizeSpanAnimator ellipsizeSpanAnimator2 = this.ellipsizeSpanAnimator;
        if (ellipsizeSpanAnimator2 != null) {
            ellipsizeSpanAnimator2.onDetachedFromWindow();
        }
    }
}
