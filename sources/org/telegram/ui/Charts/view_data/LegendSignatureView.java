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
        textView.setTextSize(14.0f);
        this.time.setTypeface(AndroidUtilities.getTypeface("fonts/rmedium.ttf"));
        TextView textView2 = new TextView(context);
        this.hourTime = textView2;
        textView2.setTextSize(14.0f);
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
        CombinedDrawable combinedDrawable = new CombinedDrawable(this.shadowDrawable, this.backgroundDrawable, AndroidUtilities.dp(3.0f), AndroidUtilities.dp(3.0f));
        combinedDrawable.setFullsize(true);
        setBackground(combinedDrawable);
    }

    public void setSize(int i) {
        this.content.removeAllViews();
        this.holdes = new Holder[i];
        for (int i2 = 0; i2 < i; i2++) {
            this.holdes[i2] = new Holder(this);
            this.content.addView(this.holdes[i2].root);
        }
    }

    public void setData(int i, long j, ArrayList<LineViewData> arrayList, boolean z) {
        TextView textView;
        int length = this.holdes.length;
        boolean z2 = true;
        if (z && Build.VERSION.SDK_INT >= 19) {
            TransitionSet transitionSet = new TransitionSet();
            transitionSet.addTransition(new Fade(2).setDuration(100)).addTransition(new ChangeBounds().setDuration(150)).addTransition(new Fade(1).setDuration(100));
            transitionSet.setOrdering(1);
            TransitionManager.beginDelayedTransition(this, transitionSet);
        }
        int i2 = 0;
        if (this.isTopHourChart) {
            this.time.setText(String.format(Locale.ENGLISH, "%02d:00", new Object[]{Long.valueOf(j)}));
        } else {
            if (this.useWeek) {
                this.time.setText(String.format("%s — %s", new Object[]{this.format4.format(new Date(j)), this.format3.format(new Date(NUM + j))}));
            } else {
                this.time.setText(formatData(new Date(j)));
            }
            if (this.useHour) {
                this.hourTime.setText(this.hourFormat.format(Long.valueOf(j)));
            }
        }
        int i3 = 0;
        for (int i4 = 0; i4 < length; i4++) {
            if (arrayList.get(i4).enabled) {
                i3 += arrayList.get(i4).line.y[i];
            }
        }
        for (int i5 = 0; i5 < length; i5++) {
            Holder holder = this.holdes[i5];
            if (!arrayList.get(i5).enabled) {
                holder.root.setVisibility(8);
            } else {
                ChartData.Line line = arrayList.get(i5).line;
                if (holder.root.getMeasuredHeight() == 0) {
                    holder.root.requestLayout();
                }
                holder.root.setVisibility(0);
                holder.value.setText(formatWholeNumber(line.y[i]));
                holder.signature.setText(line.name);
                String str = line.colorKey;
                if (str == null || !Theme.hasThemeKey(str)) {
                    holder.value.setTextColor(Theme.getCurrentTheme().isDark() ? line.colorDark : line.color);
                } else {
                    holder.value.setTextColor(Theme.getColor(line.colorKey));
                }
                holder.signature.setTextColor(Theme.getColor("dialogTextBlack"));
                if (this.showPercentage && (textView = holder.percentage) != null) {
                    textView.setVisibility(0);
                    holder.percentage.setTextColor(Theme.getColor("dialogTextBlack"));
                    float f = ((float) arrayList.get(i5).line.y[i]) / ((float) i3);
                    if (f >= 0.1f || f == 0.0f) {
                        holder.percentage.setText(String.format(Locale.ENGLISH, "%d%s", new Object[]{Integer.valueOf(Math.round(f * 100.0f)), "%"}));
                    } else {
                        holder.percentage.setText(String.format(Locale.ENGLISH, "%.1f%s", new Object[]{Float.valueOf(f * 100.0f), "%"}));
                    }
                }
            }
        }
        if (this.zoomEnabled) {
            if (i3 <= 0) {
                z2 = false;
            }
            this.canGoZoom = z2;
            ImageView imageView = this.chevron;
            if (i3 <= 0) {
                i2 = 8;
            }
            imageView.setVisibility(i2);
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

    private String capitalize(String str) {
        if (str.length() <= 0) {
            return str;
        }
        return Character.toUpperCase(str.charAt(0)) + str.substring(1);
    }

    public String formatWholeNumber(int i) {
        float f = (float) i;
        if (i < 10000) {
            return String.format("%d", new Object[]{Integer.valueOf(i)});
        }
        int i2 = 0;
        while (f >= 10000.0f && i2 < AndroidUtilities.numbersSignatureArray.length - 1) {
            f /= 1000.0f;
            i2++;
        }
        return String.format("%.2f", new Object[]{Float.valueOf(f)}) + AndroidUtilities.numbersSignatureArray[i2];
    }

    public void showProgress(boolean z, boolean z2) {
        if (z) {
            AndroidUtilities.runOnUIThread(this.showProgressRunnable, 300);
            return;
        }
        AndroidUtilities.cancelRunOnUIThread(this.showProgressRunnable);
        if (z2) {
            this.progressView.setVisibility(8);
            return;
        }
        this.chevron.animate().setDuration(80).alpha(1.0f).start();
        if (this.progressView.getVisibility() == 0) {
            this.progressView.animate().setDuration(80).alpha(0.0f).setListener(new AnimatorListenerAdapter() {
                public void onAnimationEnd(Animator animator) {
                    LegendSignatureView.this.progressView.setVisibility(8);
                }
            }).start();
        }
    }

    public void setUseWeek(boolean z) {
        this.useWeek = z;
    }

    class Holder {
        TextView percentage;
        final LinearLayout root;
        final TextView signature;
        final TextView value;

        Holder(LegendSignatureView legendSignatureView) {
            LinearLayout linearLayout = new LinearLayout(legendSignatureView.getContext());
            this.root = linearLayout;
            linearLayout.setPadding(AndroidUtilities.dp(4.0f), AndroidUtilities.dp(2.0f), AndroidUtilities.dp(4.0f), AndroidUtilities.dp(2.0f));
            if (legendSignatureView.showPercentage) {
                LinearLayout linearLayout2 = this.root;
                TextView textView = new TextView(legendSignatureView.getContext());
                this.percentage = textView;
                linearLayout2.addView(textView);
                this.percentage.getLayoutParams().width = AndroidUtilities.dp(36.0f);
                this.percentage.setVisibility(8);
                this.percentage.setTypeface(AndroidUtilities.getTypeface("fonts/rmedium.ttf"));
                this.percentage.setTextSize(13.0f);
            }
            LinearLayout linearLayout3 = this.root;
            TextView textView2 = new TextView(legendSignatureView.getContext());
            this.signature = textView2;
            linearLayout3.addView(textView2);
            this.signature.getLayoutParams().width = AndroidUtilities.dp(legendSignatureView.showPercentage ? 80.0f : 96.0f);
            LinearLayout linearLayout4 = this.root;
            TextView textView3 = new TextView(legendSignatureView.getContext());
            this.value = textView3;
            linearLayout4.addView(textView3, LayoutHelper.createLinear(-1, -2));
            this.signature.setGravity(8388611);
            this.value.setGravity(8388613);
            this.value.setTypeface(AndroidUtilities.getTypeface("fonts/rmedium.ttf"));
            this.value.setTextSize(13.0f);
            this.value.setMinEms(4);
            this.value.setMaxEms(4);
            this.signature.setTextSize(13.0f);
        }
    }
}
