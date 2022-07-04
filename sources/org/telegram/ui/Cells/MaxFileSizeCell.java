package org.telegram.ui.Cells;

import android.animation.Animator;
import android.animation.ObjectAnimator;
import android.content.Context;
import android.graphics.Canvas;
import android.text.TextUtils;
import android.view.MotionEvent;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.TextView;
import java.util.ArrayList;
import org.telegram.messenger.AndroidUtilities;
import org.telegram.messenger.LocaleController;
import org.telegram.ui.ActionBar.Theme;
import org.telegram.ui.Components.LayoutHelper;
import org.telegram.ui.Components.SeekBarView;

public class MaxFileSizeCell extends FrameLayout {
    /* access modifiers changed from: private */
    public long currentSize;
    private SeekBarView seekBarView;
    /* access modifiers changed from: private */
    public TextView sizeTextView;
    /* access modifiers changed from: private */
    public TextView textView;

    public MaxFileSizeCell(Context context) {
        super(context);
        setWillNotDraw(false);
        TextView textView2 = new TextView(context);
        this.textView = textView2;
        textView2.setTextColor(Theme.getColor("dialogTextBlack"));
        this.textView.setTextSize(1, 16.0f);
        this.textView.setLines(1);
        this.textView.setMaxLines(1);
        this.textView.setSingleLine(true);
        int i = 5;
        this.textView.setGravity((LocaleController.isRTL ? 5 : 3) | 48);
        this.textView.setEllipsize(TextUtils.TruncateAt.END);
        this.textView.setImportantForAccessibility(2);
        addView(this.textView, LayoutHelper.createFrame(-1, -1.0f, (LocaleController.isRTL ? 5 : 3) | 48, 21.0f, 13.0f, 21.0f, 0.0f));
        TextView textView3 = new TextView(context);
        this.sizeTextView = textView3;
        textView3.setTextColor(Theme.getColor("dialogTextBlue2"));
        this.sizeTextView.setTextSize(1, 16.0f);
        this.sizeTextView.setLines(1);
        this.sizeTextView.setMaxLines(1);
        this.sizeTextView.setSingleLine(true);
        this.sizeTextView.setGravity((LocaleController.isRTL ? 3 : 5) | 48);
        this.sizeTextView.setImportantForAccessibility(2);
        addView(this.sizeTextView, LayoutHelper.createFrame(-2, -1.0f, (LocaleController.isRTL ? 3 : i) | 48, 21.0f, 13.0f, 21.0f, 0.0f));
        AnonymousClass1 r0 = new SeekBarView(context) {
            public boolean onTouchEvent(MotionEvent event) {
                if (event.getAction() == 0) {
                    getParent().requestDisallowInterceptTouchEvent(true);
                }
                return super.onTouchEvent(event);
            }
        };
        this.seekBarView = r0;
        r0.setReportChanges(true);
        this.seekBarView.setDelegate(new SeekBarView.SeekBarViewDelegate() {
            public /* synthetic */ int getStepsCount() {
                return SeekBarView.SeekBarViewDelegate.CC.$default$getStepsCount(this);
            }

            public void onSeekBarDrag(boolean stop, float progress) {
                int size;
                if (progress <= 0.25f) {
                    size = (int) (((float) 512000) + ((progress / 0.25f) * 536576.0f));
                } else {
                    float progress2 = progress - 0.25f;
                    int size2 = 512000 + 536576;
                    if (progress2 < 0.25f) {
                        size = (int) (((float) size2) + ((progress2 / 0.25f) * 9437184.0f));
                    } else {
                        float progress3 = progress2 - 0.25f;
                        int size3 = size2 + 9437184;
                        if (progress3 <= 0.25f) {
                            size = (int) (((float) size3) + ((progress3 / 0.25f) * 9.437184E7f));
                        } else {
                            int size4 = size3 + 94371840;
                            size = (int) (((float) size4) + (((float) (NUM - ((long) size4))) * ((progress3 - 0.25f) / 0.25f)));
                        }
                    }
                }
                MaxFileSizeCell.this.sizeTextView.setText(LocaleController.formatString("AutodownloadSizeLimitUpTo", NUM, AndroidUtilities.formatFileSize((long) size)));
                long unused = MaxFileSizeCell.this.currentSize = (long) size;
                MaxFileSizeCell.this.didChangedSizeValue(size);
            }

            public void onSeekBarPressed(boolean pressed) {
            }

            public CharSequence getContentDescription() {
                return MaxFileSizeCell.this.textView.getText() + " " + MaxFileSizeCell.this.sizeTextView.getText();
            }
        });
        this.seekBarView.setImportantForAccessibility(2);
        addView(this.seekBarView, LayoutHelper.createFrame(-1, 38.0f, 51, 6.0f, 36.0f, 6.0f, 0.0f));
        setImportantForAccessibility(1);
        setAccessibilityDelegate(this.seekBarView.getSeekBarAccessibilityDelegate());
    }

    /* access modifiers changed from: protected */
    public void didChangedSizeValue(int value) {
    }

    public void setText(String text) {
        this.textView.setText(text);
    }

    public long getSize() {
        return this.currentSize;
    }

    /* access modifiers changed from: protected */
    public void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(View.MeasureSpec.makeMeasureSpec(View.MeasureSpec.getSize(widthMeasureSpec), NUM), View.MeasureSpec.makeMeasureSpec(AndroidUtilities.dp(80.0f), NUM));
        setMeasuredDimension(View.MeasureSpec.getSize(widthMeasureSpec), AndroidUtilities.dp(80.0f));
        int availableWidth = getMeasuredWidth() - AndroidUtilities.dp(42.0f);
        this.sizeTextView.measure(View.MeasureSpec.makeMeasureSpec(availableWidth, Integer.MIN_VALUE), View.MeasureSpec.makeMeasureSpec(AndroidUtilities.dp(30.0f), NUM));
        this.textView.measure(View.MeasureSpec.makeMeasureSpec(Math.max(AndroidUtilities.dp(10.0f), (availableWidth - this.sizeTextView.getMeasuredWidth()) - AndroidUtilities.dp(8.0f)), NUM), View.MeasureSpec.makeMeasureSpec(AndroidUtilities.dp(30.0f), NUM));
        this.seekBarView.measure(View.MeasureSpec.makeMeasureSpec(getMeasuredWidth() - AndroidUtilities.dp(20.0f), NUM), View.MeasureSpec.makeMeasureSpec(AndroidUtilities.dp(30.0f), NUM));
    }

    public boolean onInterceptTouchEvent(MotionEvent ev) {
        if (!isEnabled()) {
            return true;
        }
        return super.onInterceptTouchEvent(ev);
    }

    public boolean dispatchTouchEvent(MotionEvent ev) {
        if (!isEnabled()) {
            return true;
        }
        return super.dispatchTouchEvent(ev);
    }

    public boolean onTouchEvent(MotionEvent event) {
        if (!isEnabled()) {
            return true;
        }
        return super.onTouchEvent(event);
    }

    public void setSize(long size) {
        float progress;
        this.currentSize = size;
        this.sizeTextView.setText(LocaleController.formatString("AutodownloadSizeLimitUpTo", NUM, AndroidUtilities.formatFileSize(size)));
        long size2 = size - 512000;
        if (size2 < 536576) {
            progress = Math.max(0.0f, ((float) size2) / 536576.0f) * 0.25f;
        } else {
            float progress2 = 0.0f + 0.25f;
            long size3 = size2 - 536576;
            if (size3 < 9437184) {
                progress = (Math.max(0.0f, ((float) size3) / 9437184.0f) * 0.25f) + progress2;
            } else {
                float progress3 = progress2 + 0.25f;
                long size4 = size3 - 9437184;
                if (size4 < 94371840) {
                    progress = (Math.max(0.0f, ((float) size4) / 9.437184E7f) * 0.25f) + progress3;
                } else {
                    progress = (Math.max(0.0f, ((float) (size4 - 94371840)) / 1.9922944E9f) * 0.25f) + progress3 + 0.25f;
                }
            }
        }
        this.seekBarView.setProgress(Math.min(1.0f, progress));
    }

    public void setEnabled(boolean value, ArrayList<Animator> animators) {
        super.setEnabled(value);
        float f = 1.0f;
        if (animators != null) {
            TextView textView2 = this.textView;
            float[] fArr = new float[1];
            fArr[0] = value ? 1.0f : 0.5f;
            animators.add(ObjectAnimator.ofFloat(textView2, "alpha", fArr));
            SeekBarView seekBarView2 = this.seekBarView;
            float[] fArr2 = new float[1];
            fArr2[0] = value ? 1.0f : 0.5f;
            animators.add(ObjectAnimator.ofFloat(seekBarView2, "alpha", fArr2));
            TextView textView3 = this.sizeTextView;
            float[] fArr3 = new float[1];
            if (!value) {
                f = 0.5f;
            }
            fArr3[0] = f;
            animators.add(ObjectAnimator.ofFloat(textView3, "alpha", fArr3));
            return;
        }
        this.textView.setAlpha(value ? 1.0f : 0.5f);
        this.seekBarView.setAlpha(value ? 1.0f : 0.5f);
        TextView textView4 = this.sizeTextView;
        if (!value) {
            f = 0.5f;
        }
        textView4.setAlpha(f);
    }

    /* access modifiers changed from: protected */
    public void onDraw(Canvas canvas) {
        canvas.drawLine(LocaleController.isRTL ? 0.0f : (float) AndroidUtilities.dp(20.0f), (float) (getMeasuredHeight() - 1), (float) (getMeasuredWidth() - (LocaleController.isRTL ? AndroidUtilities.dp(20.0f) : 0)), (float) (getMeasuredHeight() - 1), Theme.dividerPaint);
    }
}
