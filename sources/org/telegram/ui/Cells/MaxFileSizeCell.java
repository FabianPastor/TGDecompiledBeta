package org.telegram.ui.Cells;

import android.animation.Animator;
import android.animation.ObjectAnimator;
import android.content.Context;
import android.graphics.Canvas;
import android.text.TextUtils.TruncateAt;
import android.view.MotionEvent;
import android.view.View.MeasureSpec;
import android.widget.FrameLayout;
import android.widget.TextView;
import java.util.ArrayList;
import org.telegram.messenger.AndroidUtilities;
import org.telegram.messenger.LocaleController;
import org.telegram.ui.ActionBar.Theme;
import org.telegram.ui.Components.LayoutHelper;
import org.telegram.ui.Components.SeekBarView;

public class MaxFileSizeCell extends FrameLayout {
    private long currentSize;
    private SeekBarView seekBarView;
    private TextView sizeTextView;
    private TextView textView;

    /* Access modifiers changed, original: protected */
    public void didChangedSizeValue(int i) {
    }

    public MaxFileSizeCell(Context context) {
        super(context);
        setWillNotDraw(false);
        this.textView = new TextView(context);
        this.textView.setTextColor(Theme.getColor("dialogTextBlack"));
        this.textView.setTextSize(1, 16.0f);
        this.textView.setLines(1);
        this.textView.setMaxLines(1);
        this.textView.setSingleLine(true);
        int i = 5;
        this.textView.setGravity((LocaleController.isRTL ? 5 : 3) | 48);
        this.textView.setEllipsize(TruncateAt.END);
        addView(this.textView, LayoutHelper.createFrame(-1, -1.0f, (LocaleController.isRTL ? 5 : 3) | 48, 21.0f, 13.0f, 21.0f, 0.0f));
        this.sizeTextView = new TextView(context);
        this.sizeTextView.setTextColor(Theme.getColor("dialogTextBlue2"));
        this.sizeTextView.setTextSize(1, 16.0f);
        this.sizeTextView.setLines(1);
        this.sizeTextView.setMaxLines(1);
        this.sizeTextView.setSingleLine(true);
        this.sizeTextView.setGravity((LocaleController.isRTL ? 3 : 5) | 48);
        TextView textView = this.sizeTextView;
        if (LocaleController.isRTL) {
            i = 3;
        }
        addView(textView, LayoutHelper.createFrame(-2, -1.0f, i | 48, 21.0f, 13.0f, 21.0f, 0.0f));
        this.seekBarView = new SeekBarView(context) {
            public boolean onTouchEvent(MotionEvent motionEvent) {
                if (motionEvent.getAction() == 0) {
                    getParent().requestDisallowInterceptTouchEvent(true);
                }
                return super.onTouchEvent(motionEvent);
            }
        };
        this.seekBarView.setReportChanges(true);
        this.seekBarView.setDelegate(new -$$Lambda$MaxFileSizeCell$cPUnEl5DY5tp-A3IQaD5cETcr3k(this));
        addView(this.seekBarView, LayoutHelper.createFrame(-1, 30.0f, 51, 10.0f, 40.0f, 10.0f, 0.0f));
    }

    public /* synthetic */ void lambda$new$0$MaxFileSizeCell(float f) {
        float f2;
        float f3;
        if (f <= 0.25f) {
            f2 = (float) 512000;
            f3 = 536576.0f;
        } else {
            f -= 0.25f;
            if (f < 0.25f) {
                f2 = (float) 1048576;
                f3 = 9437184.0f;
            } else {
                f -= 0.25f;
                if (f <= 0.25f) {
                    f2 = (float) 10485760;
                    f3 = 9.437184E7f;
                } else {
                    f -= 0.25f;
                    f2 = (float) NUM;
                    f3 = 1.50575514E9f;
                }
            }
        }
        int i = (int) (f2 + ((f / 0.25f) * f3));
        TextView textView = this.sizeTextView;
        Object[] objArr = new Object[1];
        long j = (long) i;
        objArr[0] = AndroidUtilities.formatFileSize(j);
        textView.setText(LocaleController.formatString("AutodownloadSizeLimitUpTo", NUM, objArr));
        this.currentSize = j;
        didChangedSizeValue(i);
    }

    public void setText(String str) {
        this.textView.setText(str);
    }

    public long getSize() {
        return this.currentSize;
    }

    /* Access modifiers changed, original: protected */
    public void onMeasure(int i, int i2) {
        super.onMeasure(MeasureSpec.makeMeasureSpec(MeasureSpec.getSize(i), NUM), MeasureSpec.makeMeasureSpec(AndroidUtilities.dp(80.0f), NUM));
        setMeasuredDimension(MeasureSpec.getSize(i), AndroidUtilities.dp(80.0f));
        i = getMeasuredWidth() - AndroidUtilities.dp(42.0f);
        this.sizeTextView.measure(MeasureSpec.makeMeasureSpec(i, Integer.MIN_VALUE), MeasureSpec.makeMeasureSpec(AndroidUtilities.dp(30.0f), NUM));
        this.textView.measure(MeasureSpec.makeMeasureSpec(Math.max(AndroidUtilities.dp(10.0f), (i - this.sizeTextView.getMeasuredWidth()) - AndroidUtilities.dp(8.0f)), NUM), MeasureSpec.makeMeasureSpec(AndroidUtilities.dp(30.0f), NUM));
        this.seekBarView.measure(MeasureSpec.makeMeasureSpec(getMeasuredWidth() - AndroidUtilities.dp(20.0f), NUM), MeasureSpec.makeMeasureSpec(AndroidUtilities.dp(30.0f), NUM));
    }

    public boolean onInterceptTouchEvent(MotionEvent motionEvent) {
        if (isEnabled()) {
            return super.onInterceptTouchEvent(motionEvent);
        }
        return true;
    }

    public boolean dispatchTouchEvent(MotionEvent motionEvent) {
        if (isEnabled()) {
            return super.dispatchTouchEvent(motionEvent);
        }
        return true;
    }

    public boolean onTouchEvent(MotionEvent motionEvent) {
        if (isEnabled()) {
            return super.onTouchEvent(motionEvent);
        }
        return true;
    }

    public void setSize(long j) {
        float max;
        this.currentSize = j;
        this.sizeTextView.setText(LocaleController.formatString("AutodownloadSizeLimitUpTo", NUM, AndroidUtilities.formatFileSize(j)));
        j -= 512000;
        if (j < 536576) {
            max = Math.max(0.0f, ((float) j) / 536576.0f) * 0.25f;
        } else {
            j -= 536576;
            if (j < 9437184) {
                max = (Math.max(0.0f, ((float) j) / 9437184.0f) * 0.25f) + 0.25f;
            } else {
                float f = 0.5f;
                j -= 9437184;
                if (j < 94371840) {
                    max = Math.max(0.0f, ((float) j) / 9.437184E7f);
                } else {
                    f = 0.75f;
                    max = Math.max(0.0f, ((float) (j - 94371840)) / 1.50575514E9f);
                }
                max = (max * 0.25f) + f;
            }
        }
        this.seekBarView.setProgress(max);
    }

    public void setEnabled(boolean z, ArrayList<Animator> arrayList) {
        super.setEnabled(z);
        float f = 1.0f;
        if (arrayList != null) {
            TextView textView = this.textView;
            float[] fArr = new float[1];
            fArr[0] = z ? 1.0f : 0.5f;
            String str = "alpha";
            arrayList.add(ObjectAnimator.ofFloat(textView, str, fArr));
            SeekBarView seekBarView = this.seekBarView;
            fArr = new float[1];
            fArr[0] = z ? 1.0f : 0.5f;
            arrayList.add(ObjectAnimator.ofFloat(seekBarView, str, fArr));
            textView = this.sizeTextView;
            float[] fArr2 = new float[1];
            if (!z) {
                f = 0.5f;
            }
            fArr2[0] = f;
            arrayList.add(ObjectAnimator.ofFloat(textView, str, fArr2));
            return;
        }
        this.textView.setAlpha(z ? 1.0f : 0.5f);
        this.seekBarView.setAlpha(z ? 1.0f : 0.5f);
        TextView textView2 = this.sizeTextView;
        if (!z) {
            f = 0.5f;
        }
        textView2.setAlpha(f);
    }

    /* Access modifiers changed, original: protected */
    public void onDraw(Canvas canvas) {
        canvas.drawLine(LocaleController.isRTL ? 0.0f : (float) AndroidUtilities.dp(20.0f), (float) (getMeasuredHeight() - 1), (float) (getMeasuredWidth() - (LocaleController.isRTL ? AndroidUtilities.dp(20.0f) : 0)), (float) (getMeasuredHeight() - 1), Theme.dividerPaint);
    }
}
