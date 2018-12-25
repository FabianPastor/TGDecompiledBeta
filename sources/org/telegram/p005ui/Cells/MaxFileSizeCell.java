package org.telegram.p005ui.Cells;

import android.content.Context;
import android.text.TextUtils.TruncateAt;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.MeasureSpec;
import android.widget.FrameLayout;
import android.widget.TextView;
import org.telegram.messenger.AndroidUtilities;
import org.telegram.messenger.CLASSNAMER;
import org.telegram.messenger.LocaleController;
import org.telegram.p005ui.ActionBar.Theme;
import org.telegram.p005ui.Components.LayoutHelper;
import org.telegram.p005ui.Components.SeekBarView;

/* renamed from: org.telegram.ui.Cells.MaxFileSizeCell */
public class MaxFileSizeCell extends FrameLayout {
    private long maxSize;
    private SeekBarView seekBarView;
    private TextView sizeTextView;
    private TextView textView;

    public MaxFileSizeCell(Context context) {
        int i;
        int i2;
        int i3 = 3;
        super(context);
        this.textView = new TextView(context);
        this.textView.setTextColor(Theme.getColor(Theme.key_windowBackgroundWhiteBlackText));
        this.textView.setTextSize(1, 16.0f);
        this.textView.setLines(1);
        this.textView.setMaxLines(1);
        this.textView.setSingleLine(true);
        this.textView.setText(LocaleController.getString("AutodownloadSizeLimit", CLASSNAMER.string.AutodownloadSizeLimit));
        this.textView.setGravity((LocaleController.isRTL ? 5 : 3) | 48);
        this.textView.setEllipsize(TruncateAt.END);
        View view = this.textView;
        if (LocaleController.isRTL) {
            i = 5;
        } else {
            i = 3;
        }
        addView(view, LayoutHelper.createFrame(-1, -1.0f, i | 48, 21.0f, 13.0f, 21.0f, 0.0f));
        this.sizeTextView = new TextView(context);
        this.sizeTextView.setTextColor(Theme.getColor(Theme.key_windowBackgroundWhiteBlueText6));
        this.sizeTextView.setTextSize(1, 16.0f);
        this.sizeTextView.setLines(1);
        this.sizeTextView.setMaxLines(1);
        this.sizeTextView.setSingleLine(true);
        TextView textView = this.sizeTextView;
        if (LocaleController.isRTL) {
            i2 = 3;
        } else {
            i2 = 5;
        }
        textView.setGravity(i2 | 48);
        view = this.sizeTextView;
        if (!LocaleController.isRTL) {
            i3 = 5;
        }
        addView(view, LayoutHelper.createFrame(-2, -1.0f, i3 | 48, 21.0f, 13.0f, 21.0f, 0.0f));
        this.seekBarView = new SeekBarView(context) {
            public boolean onTouchEvent(MotionEvent event) {
                if (event.getAction() == 0) {
                    getParent().requestDisallowInterceptTouchEvent(true);
                }
                return super.onTouchEvent(event);
            }
        };
        this.seekBarView.setReportChanges(true);
        this.seekBarView.setDelegate(new MaxFileSizeCell$$Lambda$0(this));
        addView(this.seekBarView, LayoutHelper.createFrame(-1, 30.0f, 51, 4.0f, 40.0f, 4.0f, 0.0f));
    }

    final /* synthetic */ void lambda$new$0$MaxFileSizeCell(float progress) {
        int size;
        if (this.maxSize <= 10485760) {
            size = (int) (((float) this.maxSize) * progress);
        } else if (progress <= 0.8f) {
            size = (int) (((float) NUM) * (progress / 0.8f));
        } else {
            size = (int) (((float) NUM) + ((((float) (this.maxSize - ((long) NUM))) * (progress - 0.8f)) / 0.2f));
        }
        this.sizeTextView.setText(LocaleController.formatString("AutodownloadSizeLimitUpTo", CLASSNAMER.string.AutodownloadSizeLimitUpTo, AndroidUtilities.formatFileSize((long) size)));
        didChangedSizeValue(size);
    }

    protected void didChangedSizeValue(int value) {
    }

    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(MeasureSpec.makeMeasureSpec(MeasureSpec.getSize(widthMeasureSpec), NUM), MeasureSpec.makeMeasureSpec(AndroidUtilities.m9dp(80.0f), NUM));
        setMeasuredDimension(MeasureSpec.getSize(widthMeasureSpec), AndroidUtilities.m9dp(80.0f));
        int availableWidth = getMeasuredWidth() - AndroidUtilities.m9dp(42.0f);
        this.sizeTextView.measure(MeasureSpec.makeMeasureSpec(availableWidth, Integer.MIN_VALUE), MeasureSpec.makeMeasureSpec(AndroidUtilities.m9dp(30.0f), NUM));
        this.textView.measure(MeasureSpec.makeMeasureSpec(Math.max(AndroidUtilities.m9dp(10.0f), (availableWidth - this.sizeTextView.getMeasuredWidth()) - AndroidUtilities.m9dp(8.0f)), NUM), MeasureSpec.makeMeasureSpec(AndroidUtilities.m9dp(30.0f), NUM));
        this.seekBarView.measure(MeasureSpec.makeMeasureSpec(getMeasuredWidth() - AndroidUtilities.m9dp(8.0f), NUM), MeasureSpec.makeMeasureSpec(AndroidUtilities.m9dp(30.0f), NUM));
    }

    public void setSize(long size, long max) {
        float progress;
        this.maxSize = max;
        if (this.maxSize <= 10485760) {
            progress = ((float) size) / ((float) this.maxSize);
        } else if (size <= ((long) NUM)) {
            progress = (((float) size) / ((float) NUM)) * 0.8f;
        } else {
            progress = 0.8f + ((((float) (size - ((long) NUM))) / ((float) (this.maxSize - ((long) NUM)))) * 0.2f);
        }
        this.seekBarView.setProgress(progress);
        this.sizeTextView.setText(LocaleController.formatString("AutodownloadSizeLimitUpTo", CLASSNAMER.string.AutodownloadSizeLimitUpTo, AndroidUtilities.formatFileSize(size)));
    }
}
