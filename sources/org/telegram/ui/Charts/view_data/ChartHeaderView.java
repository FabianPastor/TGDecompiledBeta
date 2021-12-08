package org.telegram.ui.Charts.view_data;

import android.content.Context;
import android.graphics.PorterDuff;
import android.graphics.Typeface;
import android.graphics.drawable.Drawable;
import android.text.TextPaint;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.TextView;
import androidx.core.content.ContextCompat;
import java.text.SimpleDateFormat;
import java.util.Date;
import org.telegram.messenger.AndroidUtilities;
import org.telegram.messenger.LocaleController;
import org.telegram.ui.ActionBar.Theme;
import org.telegram.ui.Charts.BaseChartView;
import org.telegram.ui.Components.LayoutHelper;

public class ChartHeaderView extends FrameLayout {
    public TextView back;
    private TextView dates;
    private TextView datesTmp;
    SimpleDateFormat formatter = new SimpleDateFormat("d MMM yyyy");
    private boolean showDate = true;
    int textMargin;
    private TextView title;
    private boolean useWeekInterval;
    private Drawable zoomIcon;

    public ChartHeaderView(Context context) {
        super(context);
        TextPaint textPaint = new TextPaint();
        textPaint.setTextSize(14.0f);
        textPaint.setTypeface(AndroidUtilities.getTypeface("fonts/rmedium.ttf"));
        this.textMargin = (int) textPaint.measureText("00 MMM 0000 - 00 MMM 000");
        TextView textView = new TextView(context);
        this.title = textView;
        textView.setTextSize(1, 15.0f);
        this.title.setTypeface(AndroidUtilities.getTypeface("fonts/rmedium.ttf"));
        addView(this.title, LayoutHelper.createFrame(-2, -2.0f, 8388627, 16.0f, 0.0f, (float) this.textMargin, 0.0f));
        TextView textView2 = new TextView(context);
        this.back = textView2;
        textView2.setTextSize(1, 15.0f);
        this.back.setTypeface(Typeface.DEFAULT_BOLD);
        this.back.setGravity(8388627);
        addView(this.back, LayoutHelper.createFrame(-2, -2.0f, 8388627, 8.0f, 0.0f, 8.0f, 0.0f));
        TextView textView3 = new TextView(context);
        this.dates = textView3;
        textView3.setTextSize(1, 13.0f);
        this.dates.setTypeface(AndroidUtilities.getTypeface("fonts/rmedium.ttf"));
        this.dates.setGravity(8388629);
        addView(this.dates, LayoutHelper.createFrame(-2, -2.0f, 8388629, 16.0f, 0.0f, 16.0f, 0.0f));
        TextView textView4 = new TextView(context);
        this.datesTmp = textView4;
        textView4.setTextSize(1, 13.0f);
        this.datesTmp.setTypeface(AndroidUtilities.getTypeface("fonts/rmedium.ttf"));
        this.datesTmp.setGravity(8388629);
        addView(this.datesTmp, LayoutHelper.createFrame(-2, -2.0f, 8388629, 16.0f, 0.0f, 16.0f, 0.0f));
        this.datesTmp.setVisibility(8);
        this.back.setVisibility(8);
        this.back.setText(LocaleController.getString("ZoomOut", NUM));
        Drawable drawable = ContextCompat.getDrawable(getContext(), NUM);
        this.zoomIcon = drawable;
        this.back.setCompoundDrawablesWithIntrinsicBounds(drawable, (Drawable) null, (Drawable) null, (Drawable) null);
        this.back.setCompoundDrawablePadding(AndroidUtilities.dp(4.0f));
        this.back.setPadding(AndroidUtilities.dp(8.0f), AndroidUtilities.dp(4.0f), AndroidUtilities.dp(8.0f), AndroidUtilities.dp(4.0f));
        this.back.setBackground(Theme.getRoundRectSelectorDrawable(Theme.getColor("featuredStickers_removeButtonText")));
        this.datesTmp.addOnLayoutChangeListener(new ChartHeaderView$$ExternalSyntheticLambda0(this));
        recolor();
    }

    /* renamed from: lambda$new$0$org-telegram-ui-Charts-view_data-ChartHeaderView  reason: not valid java name */
    public /* synthetic */ void m1658lambda$new$0$orgtelegramuiChartsview_dataChartHeaderView(View v, int left, int top, int right, int bottom, int oldLeft, int oldTop, int oldRight, int oldBottom) {
        TextView textView = this.datesTmp;
        textView.setPivotX(((float) textView.getMeasuredWidth()) * 0.7f);
        TextView textView2 = this.dates;
        textView2.setPivotX(((float) textView2.getMeasuredWidth()) * 0.7f);
    }

    public void recolor() {
        this.title.setTextColor(Theme.getColor("dialogTextBlack"));
        this.dates.setTextColor(Theme.getColor("dialogTextBlack"));
        this.datesTmp.setTextColor(Theme.getColor("dialogTextBlack"));
        this.back.setTextColor(Theme.getColor("statisticChartBackZoomColor"));
        this.zoomIcon.setColorFilter(Theme.getColor("statisticChartBackZoomColor"), PorterDuff.Mode.SRC_IN);
    }

    public void setDates(long start, long end) {
        String newText;
        if (!this.showDate) {
            this.dates.setVisibility(8);
            this.datesTmp.setVisibility(8);
            return;
        }
        if (this.useWeekInterval) {
            end += NUM;
        }
        if (end - start >= 86400000) {
            newText = this.formatter.format(new Date(start)) + " — " + this.formatter.format(new Date(end));
        } else {
            newText = this.formatter.format(new Date(start));
        }
        this.dates.setText(newText);
        this.dates.setVisibility(0);
    }

    public void setTitle(String s) {
        this.title.setText(s);
    }

    public void zoomTo(BaseChartView chartView, long d, boolean animate) {
        setDates(d, d);
        this.back.setVisibility(0);
        if (animate) {
            this.back.setAlpha(0.0f);
            this.back.setScaleX(0.3f);
            this.back.setScaleY(0.3f);
            this.back.setPivotX(0.0f);
            this.back.setPivotY((float) AndroidUtilities.dp(40.0f));
            this.back.animate().alpha(1.0f).scaleY(1.0f).scaleX(1.0f).setDuration(200).start();
            this.title.setAlpha(1.0f);
            this.title.setTranslationX(0.0f);
            this.title.setTranslationY(0.0f);
            this.title.setScaleX(1.0f);
            this.title.setScaleY(1.0f);
            this.title.setPivotX(0.0f);
            this.title.setPivotY(0.0f);
            this.title.animate().alpha(0.0f).scaleY(0.3f).scaleX(0.3f).setDuration(200).start();
            return;
        }
        this.back.setAlpha(1.0f);
        this.back.setTranslationX(0.0f);
        this.back.setTranslationY(0.0f);
        this.back.setScaleX(1.0f);
        this.back.setScaleY(1.0f);
        this.title.setAlpha(0.0f);
    }

    public void zoomOut(BaseChartView chartView, boolean animated) {
        setDates(chartView.getStartDate(), chartView.getEndDate());
        if (animated) {
            this.title.setAlpha(0.0f);
            this.title.setScaleX(0.3f);
            this.title.setScaleY(0.3f);
            this.title.setPivotX(0.0f);
            this.title.setPivotY(0.0f);
            this.title.animate().alpha(1.0f).scaleY(1.0f).scaleX(1.0f).setDuration(200).start();
            this.back.setAlpha(1.0f);
            this.back.setTranslationX(0.0f);
            this.back.setTranslationY(0.0f);
            this.back.setScaleX(1.0f);
            this.back.setScaleY(1.0f);
            this.back.setPivotY((float) AndroidUtilities.dp(40.0f));
            this.back.animate().alpha(0.0f).scaleY(0.3f).scaleX(0.3f).setDuration(200).start();
            return;
        }
        this.title.setAlpha(1.0f);
        this.title.setScaleX(1.0f);
        this.title.setScaleY(1.0f);
        this.back.setAlpha(0.0f);
    }

    public void setUseWeekInterval(boolean useWeekInterval2) {
        this.useWeekInterval = useWeekInterval2;
    }

    public void showDate(boolean b) {
        this.showDate = b;
        if (!b) {
            this.datesTmp.setVisibility(8);
            this.dates.setVisibility(8);
            this.title.setLayoutParams(LayoutHelper.createFrame(-2, -2.0f, 8388627, 16.0f, 0.0f, 16.0f, 0.0f));
            this.title.requestLayout();
            return;
        }
        this.title.setLayoutParams(LayoutHelper.createFrame(-2, -2.0f, 8388627, 16.0f, 0.0f, (float) this.textMargin, 0.0f));
    }
}
