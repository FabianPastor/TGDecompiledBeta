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

    public MaxFileSizeCell(Context context) {
        int i;
        int i2;
        int i3 = 3;
        super(context);
        setWillNotDraw(false);
        this.textView = new TextView(context);
        this.textView.setTextColor(Theme.getColor("dialogTextBlack"));
        this.textView.setTextSize(1, 16.0f);
        this.textView.setLines(1);
        this.textView.setMaxLines(1);
        this.textView.setSingleLine(true);
        this.textView.setGravity((LocaleController.isRTL ? 5 : 3) | 48);
        this.textView.setEllipsize(TruncateAt.END);
        TextView textView = this.textView;
        if (LocaleController.isRTL) {
            i = 5;
        } else {
            i = 3;
        }
        addView(textView, LayoutHelper.createFrame(-1, -1.0f, i | 48, 21.0f, 13.0f, 21.0f, 0.0f));
        this.sizeTextView = new TextView(context);
        this.sizeTextView.setTextColor(Theme.getColor("dialogTextBlue2"));
        this.sizeTextView.setTextSize(1, 16.0f);
        this.sizeTextView.setLines(1);
        this.sizeTextView.setMaxLines(1);
        this.sizeTextView.setSingleLine(true);
        TextView textView2 = this.sizeTextView;
        if (LocaleController.isRTL) {
            i2 = 3;
        } else {
            i2 = 5;
        }
        textView2.setGravity(i2 | 48);
        textView = this.sizeTextView;
        if (!LocaleController.isRTL) {
            i3 = 5;
        }
        addView(textView, LayoutHelper.createFrame(-2, -1.0f, i3 | 48, 21.0f, 13.0f, 21.0f, 0.0f));
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
        addView(this.seekBarView, LayoutHelper.createFrame(-1, 30.0f, 51, 10.0f, 40.0f, 10.0f, 0.0f));
    }

    /* Access modifiers changed, original: final|synthetic */
    public final /* synthetic */ void lambda$new$0$MaxFileSizeCell(float progress) {
        int size;
        if (progress <= 0.25f) {
            size = (int) (((float) 512000) + (536576.0f * (progress / 0.25f)));
        } else {
            progress -= 0.25f;
            size = 512000 + 536576;
            if (progress < 0.25f) {
                size = (int) (((float) size) + (9437184.0f * (progress / 0.25f)));
            } else {
                progress -= 0.25f;
                size += 9437184;
                if (progress <= 0.25f) {
                    size = (int) (((float) size) + (9.437184E7f * (progress / 0.25f)));
                } else {
                    size = (int) (((float) (size + 94371840)) + (1.50575514E9f * ((progress - 0.25f) / 0.25f)));
                }
            }
        }
        this.sizeTextView.setText(LocaleController.formatString("AutodownloadSizeLimitUpTo", NUM, AndroidUtilities.formatFileSize((long) size)));
        this.currentSize = (long) size;
        didChangedSizeValue(size);
    }

    /* Access modifiers changed, original: protected */
    public void didChangedSizeValue(int value) {
    }

    public void setText(String text) {
        this.textView.setText(text);
    }

    public long getSize() {
        return this.currentSize;
    }

    /* Access modifiers changed, original: protected */
    public void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(MeasureSpec.makeMeasureSpec(MeasureSpec.getSize(widthMeasureSpec), NUM), MeasureSpec.makeMeasureSpec(AndroidUtilities.dp(80.0f), NUM));
        setMeasuredDimension(MeasureSpec.getSize(widthMeasureSpec), AndroidUtilities.dp(80.0f));
        int availableWidth = getMeasuredWidth() - AndroidUtilities.dp(42.0f);
        this.sizeTextView.measure(MeasureSpec.makeMeasureSpec(availableWidth, Integer.MIN_VALUE), MeasureSpec.makeMeasureSpec(AndroidUtilities.dp(30.0f), NUM));
        this.textView.measure(MeasureSpec.makeMeasureSpec(Math.max(AndroidUtilities.dp(10.0f), (availableWidth - this.sizeTextView.getMeasuredWidth()) - AndroidUtilities.dp(8.0f)), NUM), MeasureSpec.makeMeasureSpec(AndroidUtilities.dp(30.0f), NUM));
        this.seekBarView.measure(MeasureSpec.makeMeasureSpec(getMeasuredWidth() - AndroidUtilities.dp(20.0f), NUM), MeasureSpec.makeMeasureSpec(AndroidUtilities.dp(30.0f), NUM));
    }

    public boolean onInterceptTouchEvent(MotionEvent ev) {
        if (isEnabled()) {
            return super.onInterceptTouchEvent(ev);
        }
        return true;
    }

    public boolean dispatchTouchEvent(MotionEvent ev) {
        if (isEnabled()) {
            return super.dispatchTouchEvent(ev);
        }
        return true;
    }

    public boolean onTouchEvent(MotionEvent event) {
        if (isEnabled()) {
            return super.onTouchEvent(event);
        }
        return true;
    }

    public void setSize(long size) {
        float progress;
        this.currentSize = size;
        this.sizeTextView.setText(LocaleController.formatString("AutodownloadSizeLimitUpTo", NUM, AndroidUtilities.formatFileSize(size)));
        size -= 512000;
        if (size < 536576) {
            progress = Math.max(0.0f, ((float) size) / 536576.0f) * 0.25f;
        } else {
            progress = 0.0f + 0.25f;
            size -= 536576;
            if (size < 9437184) {
                progress += Math.max(0.0f, ((float) size) / 9437184.0f) * 0.25f;
            } else {
                progress += 0.25f;
                size -= 9437184;
                if (size < 94371840) {
                    progress += Math.max(0.0f, ((float) size) / 9.437184E7f) * 0.25f;
                } else {
                    progress = (progress + 0.25f) + (Math.max(0.0f, ((float) (size - 94371840)) / 1.50575514E9f) * 0.25f);
                }
            }
        }
        this.seekBarView.setProgress(progress);
    }

    public void setEnabled(boolean value, ArrayList<Animator> animators) {
        float f = 1.0f;
        super.setEnabled(value);
        TextView textView;
        SeekBarView seekBarView;
        float f2;
        TextView textView2;
        if (animators != null) {
            textView = this.textView;
            String str = "alpha";
            float[] fArr = new float[1];
            fArr[0] = value ? 1.0f : 0.5f;
            animators.add(ObjectAnimator.ofFloat(textView, str, fArr));
            seekBarView = this.seekBarView;
            str = "alpha";
            fArr = new float[1];
            if (value) {
                f2 = 1.0f;
            } else {
                f2 = 0.5f;
            }
            fArr[0] = f2;
            animators.add(ObjectAnimator.ofFloat(seekBarView, str, fArr));
            textView2 = this.sizeTextView;
            String str2 = "alpha";
            float[] fArr2 = new float[1];
            if (!value) {
                f = 0.5f;
            }
            fArr2[0] = f;
            animators.add(ObjectAnimator.ofFloat(textView2, str2, fArr2));
            return;
        }
        textView = this.textView;
        if (value) {
            f2 = 1.0f;
        } else {
            f2 = 0.5f;
        }
        textView.setAlpha(f2);
        seekBarView = this.seekBarView;
        if (value) {
            f2 = 1.0f;
        } else {
            f2 = 0.5f;
        }
        seekBarView.setAlpha(f2);
        textView2 = this.sizeTextView;
        if (!value) {
            f = 0.5f;
        }
        textView2.setAlpha(f);
    }

    /* Access modifiers changed, original: protected */
    public void onDraw(Canvas canvas) {
        canvas.drawLine(LocaleController.isRTL ? 0.0f : (float) AndroidUtilities.dp(20.0f), (float) (getMeasuredHeight() - 1), (float) (getMeasuredWidth() - (LocaleController.isRTL ? AndroidUtilities.dp(20.0f) : 0)), (float) (getMeasuredHeight() - 1), Theme.dividerPaint);
    }
}
