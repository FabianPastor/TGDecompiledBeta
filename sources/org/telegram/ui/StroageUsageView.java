package org.telegram.ui;

import android.animation.ValueAnimator;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.drawable.Drawable;
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
import org.telegram.ui.Components.LayoutHelper;

class StroageUsageView extends FrameLayout {
    /* access modifiers changed from: private */
    public Paint bgPaint = new Paint();
    /* access modifiers changed from: private */
    public boolean calculating;
    TextView calculatingTextView;
    View divider;
    TextView freeSizeTextView;
    int lastProgressColor;
    ViewGroup legendLayout;
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
    TextView totlaSizeTextView;
    ValueAnimator valueAnimator;
    ValueAnimator valueAnimator2;

    public StroageUsageView(Context context) {
        super(context);
        setWillNotDraw(false);
        this.paintFill.setStrokeWidth((float) AndroidUtilities.dp(6.0f));
        this.paintProgress.setStrokeWidth((float) AndroidUtilities.dp(6.0f));
        this.paintProgress2.setStrokeWidth((float) AndroidUtilities.dp(6.0f));
        this.paintFill.setStrokeCap(Paint.Cap.ROUND);
        this.paintProgress.setStrokeCap(Paint.Cap.ROUND);
        this.paintProgress2.setStrokeCap(Paint.Cap.ROUND);
        ProgressView progressView2 = new ProgressView(context);
        this.progressView = progressView2;
        addView(progressView2, LayoutHelper.createFrame(-1, -2.0f));
        LinearLayout linearLayout = new LinearLayout(context);
        linearLayout.setOrientation(1);
        addView(linearLayout, LayoutHelper.createFrame(-1, -2.0f));
        AnonymousClass1 r5 = new FrameLayout(this, context) {
            /* access modifiers changed from: protected */
            public void onMeasure(int i, int i2) {
                super.onMeasure(View.MeasureSpec.makeMeasureSpec(View.MeasureSpec.getSize(i), NUM), i2);
                int childCount = getChildCount();
                int i3 = 0;
                int i4 = 0;
                int i5 = 0;
                for (int i6 = 0; i6 < childCount; i6++) {
                    if (getChildAt(i6).getVisibility() != 8) {
                        if (getChildAt(i6).getMeasuredWidth() + i4 > View.MeasureSpec.getSize(i)) {
                            i5 += getChildAt(i6).getMeasuredHeight() + AndroidUtilities.dp(8.0f);
                            i4 = 0;
                        }
                        i4 += getChildAt(i6).getMeasuredWidth() + AndroidUtilities.dp(16.0f);
                        i3 = getChildAt(i6).getMeasuredHeight() + i5;
                    }
                }
                setMeasuredDimension(getMeasuredWidth(), i3);
            }

            /* access modifiers changed from: protected */
            public void onLayout(boolean z, int i, int i2, int i3, int i4) {
                int childCount = getChildCount();
                int i5 = 0;
                int i6 = 0;
                for (int i7 = 0; i7 < childCount; i7++) {
                    if (getChildAt(i7).getVisibility() != 8) {
                        if (getChildAt(i7).getMeasuredWidth() + i5 > getMeasuredWidth()) {
                            i6 += getChildAt(i7).getMeasuredHeight() + AndroidUtilities.dp(8.0f);
                            i5 = 0;
                        }
                        getChildAt(i7).layout(i5, i6, getChildAt(i7).getMeasuredWidth() + i5, getChildAt(i7).getMeasuredHeight() + i6);
                        i5 += getChildAt(i7).getMeasuredWidth() + AndroidUtilities.dp(16.0f);
                    }
                }
            }
        };
        this.legendLayout = r5;
        linearLayout.addView(r5, LayoutHelper.createLinear(-1, -2, 21.0f, 40.0f, 21.0f, 16.0f));
        TextView textView = new TextView(context);
        this.calculatingTextView = textView;
        textView.setTextColor(Theme.getColor("windowBackgroundWhiteGrayText"));
        this.calculatingTextView.setText(LocaleController.getString("CalculatingSize", NUM));
        TextView textView2 = new TextView(context);
        this.telegramCacheTextView = textView2;
        textView2.setCompoundDrawablePadding(AndroidUtilities.dp(6.0f));
        this.telegramCacheTextView.setTextColor(Theme.getColor("windowBackgroundWhiteGrayText"));
        TextView textView3 = new TextView(context);
        this.telegramDatabaseTextView = textView3;
        textView3.setCompoundDrawablePadding(AndroidUtilities.dp(6.0f));
        this.telegramDatabaseTextView.setTextColor(Theme.getColor("windowBackgroundWhiteGrayText"));
        TextView textView4 = new TextView(context);
        this.freeSizeTextView = textView4;
        textView4.setCompoundDrawablePadding(AndroidUtilities.dp(6.0f));
        this.freeSizeTextView.setTextColor(Theme.getColor("windowBackgroundWhiteGrayText"));
        TextView textView5 = new TextView(context);
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

    public void setStorageUsage(boolean z, long j, long j2, long j3, long j4) {
        boolean z2 = z;
        long j5 = j4;
        this.calculating = z2;
        this.freeSizeTextView.setText(LocaleController.formatString("TotalDeviceFreeSize", NUM, AndroidUtilities.formatFileSize(j3)));
        long j6 = j5 - j3;
        this.totlaSizeTextView.setText(LocaleController.formatString("TotalDeviceSize", NUM, AndroidUtilities.formatFileSize(j6)));
        if (z2) {
            this.calculatingTextView.setVisibility(0);
            this.telegramCacheTextView.setVisibility(8);
            this.freeSizeTextView.setVisibility(8);
            this.totlaSizeTextView.setVisibility(8);
            this.telegramDatabaseTextView.setVisibility(8);
            this.divider.setVisibility(8);
            this.textSettingsCell.setVisibility(8);
            this.progress = 0.0f;
            this.progress2 = 0.0f;
        } else {
            this.calculatingTextView.setVisibility(8);
            if (j2 > 0) {
                this.divider.setVisibility(0);
                this.textSettingsCell.setVisibility(0);
                this.telegramCacheTextView.setVisibility(0);
                this.telegramDatabaseTextView.setVisibility(8);
                this.textSettingsCell.setText(LocaleController.getString("ClearTelegramCache", NUM), false);
                this.telegramCacheTextView.setText(LocaleController.formatString("TelegramCacheSize", NUM, AndroidUtilities.formatFileSize(j2 + j)));
            } else {
                this.telegramCacheTextView.setVisibility(8);
                this.telegramDatabaseTextView.setVisibility(0);
                this.telegramDatabaseTextView.setText(LocaleController.formatString("LocalDatabaseSize", NUM, AndroidUtilities.formatFileSize(j)));
                this.divider.setVisibility(8);
                this.textSettingsCell.setVisibility(8);
            }
            this.freeSizeTextView.setVisibility(0);
            this.totlaSizeTextView.setVisibility(0);
            float f = (float) j5;
            float f2 = ((float) (j2 + j)) / f;
            float f3 = ((float) j6) / f;
            if (this.progress != f2) {
                ValueAnimator valueAnimator3 = this.valueAnimator;
                if (valueAnimator3 != null) {
                    valueAnimator3.cancel();
                }
                ValueAnimator ofFloat = ValueAnimator.ofFloat(new float[]{this.progress, f2});
                this.valueAnimator = ofFloat;
                ofFloat.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
                    public final void onAnimationUpdate(ValueAnimator valueAnimator) {
                        StroageUsageView.this.lambda$setStorageUsage$0$StroageUsageView(valueAnimator);
                    }
                });
                this.valueAnimator.start();
            }
            if (this.progress2 != f3) {
                ValueAnimator valueAnimator4 = this.valueAnimator2;
                if (valueAnimator4 != null) {
                    valueAnimator4.cancel();
                }
                ValueAnimator ofFloat2 = ValueAnimator.ofFloat(new float[]{this.progress2, f3});
                this.valueAnimator2 = ofFloat2;
                ofFloat2.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
                    public final void onAnimationUpdate(ValueAnimator valueAnimator) {
                        StroageUsageView.this.lambda$setStorageUsage$1$StroageUsageView(valueAnimator);
                    }
                });
                this.valueAnimator2.start();
            }
        }
        this.textSettingsCell.setTextColor(Theme.getColor("windowBackgroundWhiteBlackText"));
        requestLayout();
    }

    /* access modifiers changed from: private */
    /* renamed from: lambda$setStorageUsage$0 */
    public /* synthetic */ void lambda$setStorageUsage$0$StroageUsageView(ValueAnimator valueAnimator3) {
        this.progress = ((Float) valueAnimator3.getAnimatedValue()).floatValue();
        invalidate();
    }

    /* access modifiers changed from: private */
    /* renamed from: lambda$setStorageUsage$1 */
    public /* synthetic */ void lambda$setStorageUsage$1$StroageUsageView(ValueAnimator valueAnimator3) {
        this.progress2 = ((Float) valueAnimator3.getAnimatedValue()).floatValue();
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
        public void onMeasure(int i, int i2) {
            super.onMeasure(i, View.MeasureSpec.makeMeasureSpec(AndroidUtilities.dp(40.0f), NUM));
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
            Canvas canvas2 = canvas;
            canvas2.drawLine((float) AndroidUtilities.dp(24.0f), (float) AndroidUtilities.dp(20.0f), (float) (getMeasuredWidth() - AndroidUtilities.dp(24.0f)), (float) AndroidUtilities.dp(20.0f), StroageUsageView.this.paintFill);
            int dp = AndroidUtilities.dp(24.0f);
            if (!StroageUsageView.this.calculating) {
                int measuredWidth = (int) (((float) (getMeasuredWidth() - (AndroidUtilities.dp(24.0f) * 2))) * StroageUsageView.this.progress2);
                int dp2 = AndroidUtilities.dp(24.0f) + measuredWidth;
                canvas.drawLine((float) dp, (float) AndroidUtilities.dp(20.0f), (float) (AndroidUtilities.dp(24.0f) + measuredWidth), (float) AndroidUtilities.dp(20.0f), StroageUsageView.this.paintProgress2);
                Canvas canvas3 = canvas;
                canvas3.drawRect((float) dp2, (float) (AndroidUtilities.dp(20.0f) - AndroidUtilities.dp(3.0f)), (float) (dp2 + AndroidUtilities.dp(3.0f)), (float) (AndroidUtilities.dp(20.0f) + AndroidUtilities.dp(3.0f)), StroageUsageView.this.bgPaint);
            }
            if (!StroageUsageView.this.calculating) {
                int measuredWidth2 = (int) (((float) (getMeasuredWidth() - (AndroidUtilities.dp(24.0f) * 2))) * StroageUsageView.this.progress);
                if (measuredWidth2 < AndroidUtilities.dp(1.0f)) {
                    measuredWidth2 = AndroidUtilities.dp(1.0f);
                }
                int dp3 = AndroidUtilities.dp(24.0f) + measuredWidth2;
                canvas.drawLine((float) dp, (float) AndroidUtilities.dp(20.0f), (float) (AndroidUtilities.dp(24.0f) + measuredWidth2), (float) AndroidUtilities.dp(20.0f), StroageUsageView.this.paintProgress);
                Canvas canvas4 = canvas;
                canvas4.drawRect((float) dp3, (float) (AndroidUtilities.dp(20.0f) - AndroidUtilities.dp(3.0f)), (float) (dp3 + AndroidUtilities.dp(3.0f)), (float) (AndroidUtilities.dp(20.0f) + AndroidUtilities.dp(3.0f)), StroageUsageView.this.bgPaint);
            }
        }
    }
}
