package org.telegram.ui.Components;

import android.animation.ObjectAnimator;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Typeface;
import android.text.StaticLayout;
import android.text.TextPaint;
import android.view.View;
import androidx.annotation.Keep;
import java.util.ArrayList;
import org.telegram.messenger.AndroidUtilities;

public class NumberTextView extends View {
    private boolean addNumber;
    /* access modifiers changed from: private */
    public ObjectAnimator animator;
    private boolean center;
    private int currentNumber = 1;
    private ArrayList<StaticLayout> letters = new ArrayList<>();
    /* access modifiers changed from: private */
    public ArrayList<StaticLayout> oldLetters = new ArrayList<>();
    private float oldTextWidth;
    private float progress = 0.0f;
    private TextPaint textPaint = new TextPaint(1);
    private float textWidth;

    public NumberTextView(Context context) {
        super(context);
    }

    @Keep
    public void setProgress(float f) {
        if (this.progress != f) {
            this.progress = f;
            invalidate();
        }
    }

    @Keep
    public float getProgress() {
        return this.progress;
    }

    public void setAddNumber() {
        this.addNumber = true;
    }

    /* JADX WARNING: Code restructure failed: missing block: B:10:0x004c, code lost:
        if (r1 < r0.currentNumber) goto L_0x004e;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:12:0x0050, code lost:
        r7 = false;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:14:0x0072, code lost:
        if (r1 > r0.currentNumber) goto L_0x004e;
     */
    /* JADX WARNING: Removed duplicated region for block: B:24:0x009e  */
    /* JADX WARNING: Removed duplicated region for block: B:45:0x0120  */
    /* JADX WARNING: Removed duplicated region for block: B:46:0x0123  */
    /* JADX WARNING: Removed duplicated region for block: B:49:0x0135  */
    /* JADX WARNING: Removed duplicated region for block: B:50:0x0138  */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public void setNumber(int r22, boolean r23) {
        /*
            r21 = this;
            r0 = r21
            r1 = r22
            int r2 = r0.currentNumber
            if (r2 != r1) goto L_0x000b
            if (r23 == 0) goto L_0x000b
            return
        L_0x000b:
            android.animation.ObjectAnimator r2 = r0.animator
            r3 = 0
            if (r2 == 0) goto L_0x0015
            r2.cancel()
            r0.animator = r3
        L_0x0015:
            java.util.ArrayList<android.text.StaticLayout> r2 = r0.oldLetters
            r2.clear()
            java.util.ArrayList<android.text.StaticLayout> r2 = r0.oldLetters
            java.util.ArrayList<android.text.StaticLayout> r4 = r0.letters
            r2.addAll(r4)
            java.util.ArrayList<android.text.StaticLayout> r2 = r0.letters
            r2.clear()
            boolean r2 = r0.addNumber
            r4 = 0
            r5 = 1
            if (r2 == 0) goto L_0x0052
            java.util.Locale r2 = java.util.Locale.US
            java.lang.Object[] r6 = new java.lang.Object[r5]
            int r7 = r0.currentNumber
            java.lang.Integer r7 = java.lang.Integer.valueOf(r7)
            r6[r4] = r7
            java.lang.String r7 = "#%d"
            java.lang.String r6 = java.lang.String.format(r2, r7, r6)
            java.lang.Object[] r8 = new java.lang.Object[r5]
            java.lang.Integer r9 = java.lang.Integer.valueOf(r22)
            r8[r4] = r9
            java.lang.String r2 = java.lang.String.format(r2, r7, r8)
            int r7 = r0.currentNumber
            if (r1 >= r7) goto L_0x0050
        L_0x004e:
            r7 = 1
            goto L_0x0075
        L_0x0050:
            r7 = 0
            goto L_0x0075
        L_0x0052:
            java.util.Locale r2 = java.util.Locale.US
            java.lang.Object[] r6 = new java.lang.Object[r5]
            int r7 = r0.currentNumber
            java.lang.Integer r7 = java.lang.Integer.valueOf(r7)
            r6[r4] = r7
            java.lang.String r7 = "%d"
            java.lang.String r6 = java.lang.String.format(r2, r7, r6)
            java.lang.Object[] r8 = new java.lang.Object[r5]
            java.lang.Integer r9 = java.lang.Integer.valueOf(r22)
            r8[r4] = r9
            java.lang.String r2 = java.lang.String.format(r2, r7, r8)
            int r7 = r0.currentNumber
            if (r1 <= r7) goto L_0x0050
            goto L_0x004e
        L_0x0075:
            boolean r8 = r0.center
            if (r8 == 0) goto L_0x0091
            android.text.TextPaint r8 = r0.textPaint
            float r8 = r8.measureText(r2)
            r0.textWidth = r8
            android.text.TextPaint r8 = r0.textPaint
            float r8 = r8.measureText(r6)
            r0.oldTextWidth = r8
            float r9 = r0.textWidth
            int r8 = (r9 > r8 ? 1 : (r9 == r8 ? 0 : -1))
            if (r8 == 0) goto L_0x0091
            r8 = 1
            goto L_0x0092
        L_0x0091:
            r8 = 0
        L_0x0092:
            r0.currentNumber = r1
            r1 = 0
            r0.progress = r1
            r9 = 0
        L_0x0098:
            int r10 = r2.length()
            if (r9 >= r10) goto L_0x0111
            int r10 = r9 + 1
            java.lang.String r12 = r2.substring(r9, r10)
            java.util.ArrayList<android.text.StaticLayout> r11 = r0.oldLetters
            boolean r11 = r11.isEmpty()
            if (r11 != 0) goto L_0x00b7
            int r11 = r6.length()
            if (r9 >= r11) goto L_0x00b7
            java.lang.String r11 = r6.substring(r9, r10)
            goto L_0x00b8
        L_0x00b7:
            r11 = r3
        L_0x00b8:
            if (r8 != 0) goto L_0x00d3
            if (r11 == 0) goto L_0x00d3
            boolean r13 = r11.equals(r12)
            if (r13 == 0) goto L_0x00d3
            java.util.ArrayList<android.text.StaticLayout> r11 = r0.letters
            java.util.ArrayList<android.text.StaticLayout> r12 = r0.oldLetters
            java.lang.Object r12 = r12.get(r9)
            r11.add(r12)
            java.util.ArrayList<android.text.StaticLayout> r11 = r0.oldLetters
            r11.set(r9, r3)
            goto L_0x010f
        L_0x00d3:
            if (r8 == 0) goto L_0x00f0
            if (r11 != 0) goto L_0x00f0
            java.util.ArrayList<android.text.StaticLayout> r9 = r0.oldLetters
            android.text.StaticLayout r11 = new android.text.StaticLayout
            android.text.TextPaint r15 = r0.textPaint
            r16 = 0
            android.text.Layout$Alignment r17 = android.text.Layout.Alignment.ALIGN_NORMAL
            r18 = 1065353216(0x3var_, float:1.0)
            r19 = 0
            r20 = 0
            java.lang.String r14 = ""
            r13 = r11
            r13.<init>(r14, r15, r16, r17, r18, r19, r20)
            r9.add(r11)
        L_0x00f0:
            android.text.StaticLayout r9 = new android.text.StaticLayout
            android.text.TextPaint r13 = r0.textPaint
            float r11 = r13.measureText(r12)
            double r14 = (double) r11
            double r14 = java.lang.Math.ceil(r14)
            int r14 = (int) r14
            android.text.Layout$Alignment r15 = android.text.Layout.Alignment.ALIGN_NORMAL
            r16 = 1065353216(0x3var_, float:1.0)
            r17 = 0
            r18 = 0
            r11 = r9
            r11.<init>(r12, r13, r14, r15, r16, r17, r18)
            java.util.ArrayList<android.text.StaticLayout> r11 = r0.letters
            r11.add(r9)
        L_0x010f:
            r9 = r10
            goto L_0x0098
        L_0x0111:
            if (r23 == 0) goto L_0x014c
            java.util.ArrayList<android.text.StaticLayout> r2 = r0.oldLetters
            boolean r2 = r2.isEmpty()
            if (r2 != 0) goto L_0x014c
            r2 = 2
            float[] r2 = new float[r2]
            if (r7 == 0) goto L_0x0123
            r3 = -1082130432(0xffffffffbvar_, float:-1.0)
            goto L_0x0125
        L_0x0123:
            r3 = 1065353216(0x3var_, float:1.0)
        L_0x0125:
            r2[r4] = r3
            r2[r5] = r1
            java.lang.String r1 = "progress"
            android.animation.ObjectAnimator r1 = android.animation.ObjectAnimator.ofFloat(r0, r1, r2)
            r0.animator = r1
            boolean r2 = r0.addNumber
            if (r2 == 0) goto L_0x0138
            r2 = 180(0xb4, double:8.9E-322)
            goto L_0x013a
        L_0x0138:
            r2 = 150(0x96, double:7.4E-322)
        L_0x013a:
            r1.setDuration(r2)
            android.animation.ObjectAnimator r1 = r0.animator
            org.telegram.ui.Components.NumberTextView$1 r2 = new org.telegram.ui.Components.NumberTextView$1
            r2.<init>()
            r1.addListener(r2)
            android.animation.ObjectAnimator r1 = r0.animator
            r1.start()
        L_0x014c:
            r21.invalidate()
            return
        */
        throw new UnsupportedOperationException("Method not decompiled: org.telegram.ui.Components.NumberTextView.setNumber(int, boolean):void");
    }

    public void setTextSize(int i) {
        this.textPaint.setTextSize((float) AndroidUtilities.dp((float) i));
        this.oldLetters.clear();
        this.letters.clear();
        setNumber(this.currentNumber, false);
    }

    public void setTextColor(int i) {
        this.textPaint.setColor(i);
        invalidate();
    }

    public void setTypeface(Typeface typeface) {
        this.textPaint.setTypeface(typeface);
        this.oldLetters.clear();
        this.letters.clear();
        setNumber(this.currentNumber, false);
    }

    public void setCenterAlign(boolean z) {
        this.center = z;
    }

    /* access modifiers changed from: protected */
    public void onDraw(Canvas canvas) {
        float f;
        float f2;
        if (!this.letters.isEmpty()) {
            float height = (float) this.letters.get(0).getHeight();
            float dp = this.addNumber ? (float) AndroidUtilities.dp(4.0f) : height;
            if (this.center) {
                f2 = (((float) getMeasuredWidth()) - this.textWidth) / 2.0f;
                f = ((((float) getMeasuredWidth()) - this.oldTextWidth) / 2.0f) - f2;
            } else {
                f2 = 0.0f;
                f = 0.0f;
            }
            canvas.save();
            canvas.translate(((float) getPaddingLeft()) + f2, (((float) getMeasuredHeight()) - height) / 2.0f);
            int max = Math.max(this.letters.size(), this.oldLetters.size());
            int i = 0;
            while (i < max) {
                canvas.save();
                StaticLayout staticLayout = null;
                StaticLayout staticLayout2 = i < this.oldLetters.size() ? this.oldLetters.get(i) : null;
                if (i < this.letters.size()) {
                    staticLayout = this.letters.get(i);
                }
                float f3 = this.progress;
                if (f3 > 0.0f) {
                    if (staticLayout2 != null) {
                        this.textPaint.setAlpha((int) (f3 * 255.0f));
                        canvas.save();
                        canvas.translate(f, (this.progress - 1.0f) * dp);
                        staticLayout2.draw(canvas);
                        canvas.restore();
                        if (staticLayout != null) {
                            this.textPaint.setAlpha((int) ((1.0f - this.progress) * 255.0f));
                            canvas.translate(0.0f, this.progress * dp);
                        }
                    } else {
                        this.textPaint.setAlpha(255);
                    }
                } else if (f3 < 0.0f) {
                    if (staticLayout2 != null) {
                        this.textPaint.setAlpha((int) ((-f3) * 255.0f));
                        canvas.save();
                        canvas.translate(f, (this.progress + 1.0f) * dp);
                        staticLayout2.draw(canvas);
                        canvas.restore();
                    }
                    if (staticLayout != null) {
                        if (i == max - 1 || staticLayout2 != null) {
                            this.textPaint.setAlpha((int) ((this.progress + 1.0f) * 255.0f));
                            canvas.translate(0.0f, this.progress * dp);
                        } else {
                            this.textPaint.setAlpha(255);
                        }
                    }
                } else if (staticLayout != null) {
                    this.textPaint.setAlpha(255);
                }
                if (staticLayout != null) {
                    staticLayout.draw(canvas);
                }
                canvas.restore();
                canvas.translate(staticLayout != null ? staticLayout.getLineWidth(0) : staticLayout2.getLineWidth(0) + ((float) AndroidUtilities.dp(1.0f)), 0.0f);
                if (!(staticLayout == null || staticLayout2 == null)) {
                    f += staticLayout2.getLineWidth(0) - staticLayout.getLineWidth(0);
                }
                i++;
            }
            canvas.restore();
        }
    }
}
