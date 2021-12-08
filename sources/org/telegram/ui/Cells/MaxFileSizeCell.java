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

    /* access modifiers changed from: protected */
    public void didChangedSizeValue(int i) {
    }

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
        AnonymousClass1 r0 = new SeekBarView(this, context) {
            public boolean onTouchEvent(MotionEvent motionEvent) {
                if (motionEvent.getAction() == 0) {
                    getParent().requestDisallowInterceptTouchEvent(true);
                }
                return super.onTouchEvent(motionEvent);
            }
        };
        this.seekBarView = r0;
        r0.setReportChanges(true);
        this.seekBarView.setDelegate(new SeekBarView.SeekBarViewDelegate() {
            public /* synthetic */ int getStepsCount() {
                return SeekBarView.SeekBarViewDelegate.CC.$default$getStepsCount(this);
            }

            public void onSeekBarPressed(boolean z) {
            }

            public void onSeekBarDrag(boolean z, float f) {
                int i;
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
                            i = (int) (((float) NUM) + (((float) (NUM - ((long) NUM))) * ((f - 0.25f) / 0.25f)));
                            long j = (long) i;
                            MaxFileSizeCell.this.sizeTextView.setText(LocaleController.formatString("AutodownloadSizeLimitUpTo", NUM, AndroidUtilities.formatFileSize(j)));
                            long unused = MaxFileSizeCell.this.currentSize = j;
                            MaxFileSizeCell.this.didChangedSizeValue(i);
                        }
                    }
                }
                i = (int) (f2 + ((f / 0.25f) * f3));
                long j2 = (long) i;
                MaxFileSizeCell.this.sizeTextView.setText(LocaleController.formatString("AutodownloadSizeLimitUpTo", NUM, AndroidUtilities.formatFileSize(j2)));
                long unused2 = MaxFileSizeCell.this.currentSize = j2;
                MaxFileSizeCell.this.didChangedSizeValue(i);
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

    public void setText(String str) {
        this.textView.setText(str);
    }

    public long getSize() {
        return this.currentSize;
    }

    /* access modifiers changed from: protected */
    public void onMeasure(int i, int i2) {
        super.onMeasure(View.MeasureSpec.makeMeasureSpec(View.MeasureSpec.getSize(i), NUM), View.MeasureSpec.makeMeasureSpec(AndroidUtilities.dp(80.0f), NUM));
        setMeasuredDimension(View.MeasureSpec.getSize(i), AndroidUtilities.dp(80.0f));
        int measuredWidth = getMeasuredWidth() - AndroidUtilities.dp(42.0f);
        this.sizeTextView.measure(View.MeasureSpec.makeMeasureSpec(measuredWidth, Integer.MIN_VALUE), View.MeasureSpec.makeMeasureSpec(AndroidUtilities.dp(30.0f), NUM));
        this.textView.measure(View.MeasureSpec.makeMeasureSpec(Math.max(AndroidUtilities.dp(10.0f), (measuredWidth - this.sizeTextView.getMeasuredWidth()) - AndroidUtilities.dp(8.0f)), NUM), View.MeasureSpec.makeMeasureSpec(AndroidUtilities.dp(30.0f), NUM));
        this.seekBarView.measure(View.MeasureSpec.makeMeasureSpec(getMeasuredWidth() - AndroidUtilities.dp(20.0f), NUM), View.MeasureSpec.makeMeasureSpec(AndroidUtilities.dp(30.0f), NUM));
    }

    public boolean onInterceptTouchEvent(MotionEvent motionEvent) {
        if (!isEnabled()) {
            return true;
        }
        return super.onInterceptTouchEvent(motionEvent);
    }

    public boolean dispatchTouchEvent(MotionEvent motionEvent) {
        if (!isEnabled()) {
            return true;
        }
        return super.dispatchTouchEvent(motionEvent);
    }

    public boolean onTouchEvent(MotionEvent motionEvent) {
        if (!isEnabled()) {
            return true;
        }
        return super.onTouchEvent(motionEvent);
    }

    public void setSize(long j) {
        float f;
        float f2;
        this.currentSize = j;
        this.sizeTextView.setText(LocaleController.formatString("AutodownloadSizeLimitUpTo", NUM, AndroidUtilities.formatFileSize(j)));
        long j2 = j - 512000;
        if (j2 < 536576) {
            f = Math.max(0.0f, ((float) j2) / 536576.0f) * 0.25f;
        } else {
            long j3 = j2 - 536576;
            if (j3 < 9437184) {
                f = (Math.max(0.0f, ((float) j3) / 9437184.0f) * 0.25f) + 0.25f;
            } else {
                float f3 = 0.5f;
                long j4 = j3 - 9437184;
                if (j4 < 94371840) {
                    f2 = Math.max(0.0f, ((float) j4) / 9.437184E7f);
                } else {
                    f3 = 0.75f;
                    f2 = Math.max(0.0f, ((float) (j4 - 94371840)) / 1.9922944E9f);
                }
                f = (f2 * 0.25f) + f3;
            }
        }
        this.seekBarView.setProgress(Math.min(1.0f, f));
    }

    public void setEnabled(boolean z, ArrayList<Animator> arrayList) {
        super.setEnabled(z);
        float f = 1.0f;
        if (arrayList != null) {
            TextView textView2 = this.textView;
            float[] fArr = new float[1];
            fArr[0] = z ? 1.0f : 0.5f;
            arrayList.add(ObjectAnimator.ofFloat(textView2, "alpha", fArr));
            SeekBarView seekBarView2 = this.seekBarView;
            float[] fArr2 = new float[1];
            fArr2[0] = z ? 1.0f : 0.5f;
            arrayList.add(ObjectAnimator.ofFloat(seekBarView2, "alpha", fArr2));
            TextView textView3 = this.sizeTextView;
            float[] fArr3 = new float[1];
            if (!z) {
                f = 0.5f;
            }
            fArr3[0] = f;
            arrayList.add(ObjectAnimator.ofFloat(textView3, "alpha", fArr3));
            return;
        }
        this.textView.setAlpha(z ? 1.0f : 0.5f);
        this.seekBarView.setAlpha(z ? 1.0f : 0.5f);
        TextView textView4 = this.sizeTextView;
        if (!z) {
            f = 0.5f;
        }
        textView4.setAlpha(f);
    }

    /* access modifiers changed from: protected */
    public void onDraw(Canvas canvas) {
        canvas.drawLine(LocaleController.isRTL ? 0.0f : (float) AndroidUtilities.dp(20.0f), (float) (getMeasuredHeight() - 1), (float) (getMeasuredWidth() - (LocaleController.isRTL ? AndroidUtilities.dp(20.0f) : 0)), (float) (getMeasuredHeight() - 1), Theme.dividerPaint);
    }
}
