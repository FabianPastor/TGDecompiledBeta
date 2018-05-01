package org.telegram.ui.Cells;

import android.content.Context;
import android.text.TextUtils.TruncateAt;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.MeasureSpec;
import android.widget.FrameLayout;
import android.widget.TextView;
import org.telegram.messenger.AndroidUtilities;
import org.telegram.messenger.C0446R;
import org.telegram.messenger.LocaleController;
import org.telegram.ui.ActionBar.SimpleTextView;
import org.telegram.ui.ActionBar.Theme;
import org.telegram.ui.Components.LayoutHelper;
import org.telegram.ui.Components.SeekBarView;
import org.telegram.ui.Components.SeekBarView.SeekBarViewDelegate;

public class MaxFileSizeCell extends FrameLayout {
    private long maxSize;
    private SeekBarView seekBarView;
    private SimpleTextView sizeTextView;
    private TextView textView;

    /* renamed from: org.telegram.ui.Cells.MaxFileSizeCell$2 */
    class C19442 implements SeekBarViewDelegate {
        C19442() {
        }

        public void onSeekBarDrag(float f) {
            if (MaxFileSizeCell.this.maxSize <= 10485760) {
                f = (int) (((float) MaxFileSizeCell.this.maxSize) * f);
            } else if (f <= 0.8f) {
                f = (int) (((float) 104857600) * (f / 0.8f));
            } else {
                f = (int) (((float) 104857600) + ((((float) (MaxFileSizeCell.this.maxSize - ((long) 104857600))) * (f - 0.8f)) / 0.2f));
            }
            MaxFileSizeCell.this.sizeTextView.setText(LocaleController.formatString("AutodownloadSizeLimitUpTo", C0446R.string.AutodownloadSizeLimitUpTo, AndroidUtilities.formatFileSize((long) f)));
            MaxFileSizeCell.this.didChangedSizeValue(f);
        }
    }

    protected void didChangedSizeValue(int i) {
    }

    public MaxFileSizeCell(Context context) {
        super(context);
        this.textView = new TextView(context);
        this.textView.setTextColor(Theme.getColor(Theme.key_windowBackgroundWhiteBlackText));
        this.textView.setTextSize(1, 16.0f);
        this.textView.setLines(1);
        this.textView.setMaxLines(1);
        this.textView.setSingleLine(true);
        this.textView.setText(LocaleController.getString("AutodownloadSizeLimit", C0446R.string.AutodownloadSizeLimit));
        int i = 3;
        this.textView.setGravity((LocaleController.isRTL ? 5 : 3) | 48);
        this.textView.setEllipsize(TruncateAt.END);
        addView(this.textView, LayoutHelper.createFrame(-1, -1.0f, (LocaleController.isRTL ? 5 : 3) | 48, LocaleController.isRTL ? 64.0f : 17.0f, 13.0f, LocaleController.isRTL ? 17.0f : 64.0f, 0.0f));
        this.sizeTextView = new SimpleTextView(context);
        this.sizeTextView.setTextColor(Theme.getColor(Theme.key_windowBackgroundWhiteBlueText6));
        this.sizeTextView.setTextSize(16);
        this.sizeTextView.setGravity((LocaleController.isRTL ? 3 : 5) | 48);
        View view = this.sizeTextView;
        if (LocaleController.isRTL) {
            i = 5;
        }
        addView(view, LayoutHelper.createFrame(-1, -1.0f, i | 48, LocaleController.isRTL ? 17.0f : 64.0f, 13.0f, LocaleController.isRTL ? 64.0f : 17.0f, 0.0f));
        this.seekBarView = new SeekBarView(context) {
            public boolean onTouchEvent(MotionEvent motionEvent) {
                if (motionEvent.getAction() == 0) {
                    getParent().requestDisallowInterceptTouchEvent(true);
                }
                return super.onTouchEvent(motionEvent);
            }
        };
        this.seekBarView.setReportChanges(true);
        this.seekBarView.setDelegate(new C19442());
        addView(this.seekBarView, LayoutHelper.createFrame(-1, 30.0f, 51, 4.0f, 40.0f, 4.0f, 0.0f));
    }

    protected void onMeasure(int i, int i2) {
        super.onMeasure(MeasureSpec.makeMeasureSpec(MeasureSpec.getSize(i), NUM), MeasureSpec.makeMeasureSpec(AndroidUtilities.dp(80.0f), NUM));
    }

    public void setSize(long j, long j2) {
        float f;
        this.maxSize = j2;
        if (this.maxSize > 10485760) {
            long j3 = (long) 104857600;
            if (j <= j3) {
                f = (((float) j) / ((float) 104857600)) * 0.8f;
            } else {
                f = 0.8f + ((((float) (j - j3)) / ((float) (this.maxSize - j3))) * 0.2f);
            }
        } else {
            f = ((float) j) / ((float) this.maxSize);
        }
        this.seekBarView.setProgress(f);
        this.sizeTextView.setText(LocaleController.formatString("AutodownloadSizeLimitUpTo", C0446R.string.AutodownloadSizeLimitUpTo, AndroidUtilities.formatFileSize(j)));
    }
}
