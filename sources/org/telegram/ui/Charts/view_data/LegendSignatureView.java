package org.telegram.ui.Charts.view_data;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.content.Context;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.transition.ChangeBounds;
import android.transition.Fade;
import android.transition.TransitionManager;
import android.transition.TransitionSet;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;
import org.telegram.messenger.AndroidUtilities;
import org.telegram.ui.ActionBar.Theme;
import org.telegram.ui.Charts.data.ChartData;
import org.telegram.ui.Components.CombinedDrawable;
import org.telegram.ui.Components.LayoutHelper;
import org.telegram.ui.Components.RadialProgressView;

public class LegendSignatureView extends FrameLayout {
    Drawable background;
    Drawable backgroundDrawable;
    public boolean canGoZoom = true;
    public ImageView chevron;
    LinearLayout content;
    SimpleDateFormat format = new SimpleDateFormat("E, ");
    SimpleDateFormat format2 = new SimpleDateFormat("MMM dd");
    SimpleDateFormat format3 = new SimpleDateFormat("d MMM yyyy");
    SimpleDateFormat format4 = new SimpleDateFormat("d MMM");
    Holder[] holdes;
    SimpleDateFormat hourFormat = new SimpleDateFormat(" HH:mm");
    TextView hourTime;
    public boolean isTopHourChart;
    /* access modifiers changed from: private */
    public RadialProgressView progressView;
    Drawable shadowDrawable;
    public boolean showPercentage;
    Runnable showProgressRunnable = new Runnable() {
        public void run() {
            LegendSignatureView.this.chevron.animate().setDuration(120).alpha(0.0f);
            LegendSignatureView.this.progressView.animate().setListener((Animator.AnimatorListener) null).start();
            if (LegendSignatureView.this.progressView.getVisibility() != 0) {
                LegendSignatureView.this.progressView.setVisibility(0);
                LegendSignatureView.this.progressView.setAlpha(0.0f);
            }
            LegendSignatureView.this.progressView.animate().setDuration(120).alpha(1.0f).start();
        }
    };
    TextView time;
    public boolean useHour;
    public boolean useWeek;
    public boolean zoomEnabled;

    public LegendSignatureView(Context context) {
        super(context);
        setPadding(AndroidUtilities.dp(8.0f), AndroidUtilities.dp(8.0f), AndroidUtilities.dp(8.0f), AndroidUtilities.dp(8.0f));
        LinearLayout linearLayout = new LinearLayout(getContext());
        this.content = linearLayout;
        linearLayout.setOrientation(1);
        TextView textView = new TextView(context);
        this.time = textView;
        textView.setTextSize(1, 14.0f);
        this.time.setTypeface(AndroidUtilities.getTypeface("fonts/rmedium.ttf"));
        TextView textView2 = new TextView(context);
        this.hourTime = textView2;
        textView2.setTextSize(1, 14.0f);
        this.hourTime.setTypeface(AndroidUtilities.getTypeface("fonts/rmedium.ttf"));
        ImageView imageView = new ImageView(context);
        this.chevron = imageView;
        imageView.setImageResource(NUM);
        RadialProgressView radialProgressView = new RadialProgressView(context);
        this.progressView = radialProgressView;
        radialProgressView.setSize(AndroidUtilities.dp(12.0f));
        this.progressView.setStrokeWidth((float) AndroidUtilities.dp(0.5f));
        this.progressView.setVisibility(8);
        addView(this.content, LayoutHelper.createFrame(-2, -2.0f, 0, 0.0f, 22.0f, 0.0f, 0.0f));
        addView(this.time, LayoutHelper.createFrame(-2, -2.0f, 8388611, 4.0f, 0.0f, 4.0f, 0.0f));
        addView(this.hourTime, LayoutHelper.createFrame(-2, -2.0f, 8388613, 4.0f, 0.0f, 4.0f, 0.0f));
        addView(this.chevron, LayoutHelper.createFrame(18, 18.0f, 8388661, 0.0f, 2.0f, 0.0f, 0.0f));
        addView(this.progressView, LayoutHelper.createFrame(18, 18.0f, 8388661, 0.0f, 2.0f, 0.0f, 0.0f));
        recolor();
    }

    public void recolor() {
        this.time.setTextColor(Theme.getColor("dialogTextBlack"));
        this.hourTime.setTextColor(Theme.getColor("dialogTextBlack"));
        this.chevron.setColorFilter(Theme.getColor("statisticChartChevronColor"));
        this.progressView.setProgressColor(Theme.getColor("statisticChartChevronColor"));
        this.shadowDrawable = getContext().getResources().getDrawable(NUM).mutate();
        this.backgroundDrawable = Theme.createSimpleSelectorRoundRectDrawable(AndroidUtilities.dp(4.0f), Theme.getColor("dialogBackground"), Theme.getColor("listSelectorSDK21"), -16777216);
        CombinedDrawable drawable = new CombinedDrawable(this.shadowDrawable, this.backgroundDrawable, AndroidUtilities.dp(3.0f), AndroidUtilities.dp(3.0f));
        drawable.setFullsize(true);
        setBackground(drawable);
    }

    public void setSize(int n) {
        this.content.removeAllViews();
        this.holdes = new Holder[n];
        for (int i = 0; i < n; i++) {
            this.holdes[i] = new Holder();
            this.content.addView(this.holdes[i].root);
        }
    }

    public void setData(int index, long date, ArrayList<LineViewData> lines, boolean animateChanges) {
        long j = date;
        ArrayList<LineViewData> arrayList = lines;
        int n = this.holdes.length;
        int i = 0;
        if (animateChanges && Build.VERSION.SDK_INT >= 19) {
            TransitionSet transition = new TransitionSet();
            transition.addTransition(new Fade(2).setDuration(150)).addTransition(new ChangeBounds().setDuration(150)).addTransition(new Fade(1).setDuration(150));
            transition.setOrdering(0);
            TransitionManager.beginDelayedTransition(this, transition);
        }
        if (this.isTopHourChart) {
            this.time.setText(String.format(Locale.ENGLISH, "%02d:00", new Object[]{Long.valueOf(date)}));
        } else {
            if (this.useWeek) {
                this.time.setText(String.format("%s — %s", new Object[]{this.format4.format(new Date(j)), this.format3.format(new Date(NUM + j))}));
            } else {
                this.time.setText(formatData(new Date(j)));
            }
            if (this.useHour) {
                this.hourTime.setText(this.hourFormat.format(Long.valueOf(date)));
            }
        }
        int sum = 0;
        for (int i2 = 0; i2 < n; i2++) {
            if (arrayList.get(i2).enabled) {
                sum += arrayList.get(i2).line.y[index];
            }
        }
        int i3 = 0;
        while (i3 < n) {
            Holder h = this.holdes[i3];
            if (!arrayList.get(i3).enabled) {
                h.root.setVisibility(8);
            } else {
                ChartData.Line l = arrayList.get(i3).line;
                if (h.root.getMeasuredHeight() == 0) {
                    h.root.requestLayout();
                }
                h.root.setVisibility(i);
                h.value.setText(formatWholeNumber(l.y[index]));
                h.signature.setText(l.name);
                if (l.colorKey == null || !Theme.hasThemeKey(l.colorKey)) {
                    h.value.setTextColor(Theme.getCurrentTheme().isDark() ? l.colorDark : l.color);
                } else {
                    h.value.setTextColor(Theme.getColor(l.colorKey));
                }
                h.signature.setTextColor(Theme.getColor("dialogTextBlack"));
                if (this.showPercentage && h.percentage != null) {
                    h.percentage.setVisibility(i);
                    h.percentage.setTextColor(Theme.getColor("dialogTextBlack"));
                    float v = ((float) arrayList.get(i3).line.y[index]) / ((float) sum);
                    if (v >= 0.1f || v == 0.0f) {
                        h.percentage.setText(String.format(Locale.ENGLISH, "%d%s", new Object[]{Integer.valueOf(Math.round(100.0f * v)), "%"}));
                    } else {
                        h.percentage.setText(String.format(Locale.ENGLISH, "%.1f%s", new Object[]{Float.valueOf(100.0f * v), "%"}));
                    }
                }
            }
            i3++;
            i = 0;
        }
        if (this.zoomEnabled) {
            this.canGoZoom = sum > 0;
            this.chevron.setVisibility(sum > 0 ? 0 : 8);
            return;
        }
        this.canGoZoom = false;
        this.chevron.setVisibility(8);
    }

    private String formatData(Date date) {
        if (this.useHour) {
            return capitalize(this.format2.format(date));
        }
        return capitalize(this.format.format(date)) + capitalize(this.format2.format(date));
    }

    private String capitalize(String s) {
        if (s.length() <= 0) {
            return s;
        }
        return Character.toUpperCase(s.charAt(0)) + s.substring(1);
    }

    public String formatWholeNumber(int v) {
        float num_ = (float) v;
        int count = 0;
        if (v < 10000) {
            return String.format("%d", new Object[]{Integer.valueOf(v)});
        }
        while (num_ >= 10000.0f && count < AndroidUtilities.numbersSignatureArray.length - 1) {
            num_ /= 1000.0f;
            count++;
        }
        return String.format("%.2f", new Object[]{Float.valueOf(num_)}) + AndroidUtilities.numbersSignatureArray[count];
    }

    public void showProgress(boolean show, boolean force) {
        if (show) {
            AndroidUtilities.runOnUIThread(this.showProgressRunnable, 300);
            return;
        }
        AndroidUtilities.cancelRunOnUIThread(this.showProgressRunnable);
        if (force) {
            this.progressView.setVisibility(8);
            return;
        }
        this.chevron.animate().setDuration(80).alpha(1.0f).start();
        if (this.progressView.getVisibility() == 0) {
            this.progressView.animate().setDuration(80).alpha(0.0f).setListener(new AnimatorListenerAdapter() {
                public void onAnimationEnd(Animator animation) {
                    LegendSignatureView.this.progressView.setVisibility(8);
                }
            }).start();
        }
    }

    public void setUseWeek(boolean useWeek2) {
        this.useWeek = useWeek2;
    }

    class Holder {
        TextView percentage;
        final LinearLayout root;
        final TextView signature;
        final TextView value;

        Holder() {
            LinearLayout linearLayout = new LinearLayout(LegendSignatureView.this.getContext());
            this.root = linearLayout;
            linearLayout.setPadding(AndroidUtilities.dp(4.0f), AndroidUtilities.dp(2.0f), AndroidUtilities.dp(4.0f), AndroidUtilities.dp(2.0f));
            if (LegendSignatureView.this.showPercentage) {
                TextView textView = new TextView(LegendSignatureView.this.getContext());
                this.percentage = textView;
                linearLayout.addView(textView);
                this.percentage.getLayoutParams().width = AndroidUtilities.dp(36.0f);
                this.percentage.setVisibility(8);
                this.percentage.setTypeface(AndroidUtilities.getTypeface("fonts/rmedium.ttf"));
                this.percentage.setTextSize(1, 13.0f);
            }
            TextView textView2 = new TextView(LegendSignatureView.this.getContext());
            this.signature = textView2;
            linearLayout.addView(textView2);
            textView2.getLayoutParams().width = AndroidUtilities.dp(LegendSignatureView.this.showPercentage ? 80.0f : 96.0f);
            TextView textView3 = new TextView(LegendSignatureView.this.getContext());
            this.value = textView3;
            linearLayout.addView(textView3, LayoutHelper.createLinear(-1, -2));
            textView2.setGravity(8388611);
            textView3.setGravity(8388613);
            textView3.setTypeface(AndroidUtilities.getTypeface("fonts/rmedium.ttf"));
            textView3.setTextSize(1, 13.0f);
            textView3.setMinEms(4);
            textView3.setMaxEms(4);
            textView2.setTextSize(1, 13.0f);
        }
    }
}
