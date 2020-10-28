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
    private int currentNumber = 1;
    private ArrayList<StaticLayout> letters = new ArrayList<>();
    /* access modifiers changed from: private */
    public ArrayList<StaticLayout> oldLetters = new ArrayList<>();
    private float progress = 0.0f;
    private TextPaint textPaint = new TextPaint(1);

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
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public void setNumber(int r19, boolean r20) {
        /*
            r18 = this;
            r0 = r18
            r1 = r19
            int r2 = r0.currentNumber
            if (r2 != r1) goto L_0x000b
            if (r20 == 0) goto L_0x000b
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
            java.lang.Integer r9 = java.lang.Integer.valueOf(r19)
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
            java.lang.Integer r9 = java.lang.Integer.valueOf(r19)
            r8[r4] = r9
            java.lang.String r2 = java.lang.String.format(r2, r7, r8)
            int r7 = r0.currentNumber
            if (r1 <= r7) goto L_0x0050
            goto L_0x004e
        L_0x0075:
            r0.currentNumber = r1
            r1 = 0
            r0.progress = r1
            r8 = 0
        L_0x007b:
            int r9 = r2.length()
            if (r8 >= r9) goto L_0x00d5
            int r9 = r8 + 1
            java.lang.String r11 = r2.substring(r8, r9)
            java.util.ArrayList<android.text.StaticLayout> r10 = r0.oldLetters
            boolean r10 = r10.isEmpty()
            if (r10 != 0) goto L_0x009a
            int r10 = r6.length()
            if (r8 >= r10) goto L_0x009a
            java.lang.String r10 = r6.substring(r8, r9)
            goto L_0x009b
        L_0x009a:
            r10 = r3
        L_0x009b:
            if (r10 == 0) goto L_0x00b4
            boolean r10 = r10.equals(r11)
            if (r10 == 0) goto L_0x00b4
            java.util.ArrayList<android.text.StaticLayout> r10 = r0.letters
            java.util.ArrayList<android.text.StaticLayout> r11 = r0.oldLetters
            java.lang.Object r11 = r11.get(r8)
            r10.add(r11)
            java.util.ArrayList<android.text.StaticLayout> r10 = r0.oldLetters
            r10.set(r8, r3)
            goto L_0x00d3
        L_0x00b4:
            android.text.StaticLayout r8 = new android.text.StaticLayout
            android.text.TextPaint r12 = r0.textPaint
            float r10 = r12.measureText(r11)
            double r13 = (double) r10
            double r13 = java.lang.Math.ceil(r13)
            int r13 = (int) r13
            android.text.Layout$Alignment r14 = android.text.Layout.Alignment.ALIGN_NORMAL
            r15 = 1065353216(0x3var_, float:1.0)
            r16 = 0
            r17 = 0
            r10 = r8
            r10.<init>(r11, r12, r13, r14, r15, r16, r17)
            java.util.ArrayList<android.text.StaticLayout> r10 = r0.letters
            r10.add(r8)
        L_0x00d3:
            r8 = r9
            goto L_0x007b
        L_0x00d5:
            if (r20 == 0) goto L_0x0110
            java.util.ArrayList<android.text.StaticLayout> r2 = r0.oldLetters
            boolean r2 = r2.isEmpty()
            if (r2 != 0) goto L_0x0110
            r2 = 2
            float[] r2 = new float[r2]
            if (r7 == 0) goto L_0x00e7
            r3 = -1082130432(0xffffffffbvar_, float:-1.0)
            goto L_0x00e9
        L_0x00e7:
            r3 = 1065353216(0x3var_, float:1.0)
        L_0x00e9:
            r2[r4] = r3
            r2[r5] = r1
            java.lang.String r1 = "progress"
            android.animation.ObjectAnimator r1 = android.animation.ObjectAnimator.ofFloat(r0, r1, r2)
            r0.animator = r1
            boolean r2 = r0.addNumber
            if (r2 == 0) goto L_0x00fc
            r2 = 180(0xb4, double:8.9E-322)
            goto L_0x00fe
        L_0x00fc:
            r2 = 150(0x96, double:7.4E-322)
        L_0x00fe:
            r1.setDuration(r2)
            android.animation.ObjectAnimator r1 = r0.animator
            org.telegram.ui.Components.NumberTextView$1 r2 = new org.telegram.ui.Components.NumberTextView$1
            r2.<init>()
            r1.addListener(r2)
            android.animation.ObjectAnimator r1 = r0.animator
            r1.start()
        L_0x0110:
            r18.invalidate()
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

    /* access modifiers changed from: protected */
    public void onDraw(Canvas canvas) {
        if (!this.letters.isEmpty()) {
            float height = (float) this.letters.get(0).getHeight();
            float dp = this.addNumber ? (float) AndroidUtilities.dp(4.0f) : height;
            canvas.save();
            canvas.translate((float) getPaddingLeft(), (((float) getMeasuredHeight()) - height) / 2.0f);
            int max = Math.max(this.letters.size(), this.oldLetters.size());
            int i = 0;
            while (i < max) {
                canvas.save();
                StaticLayout staticLayout = null;
                StaticLayout staticLayout2 = i < this.oldLetters.size() ? this.oldLetters.get(i) : null;
                if (i < this.letters.size()) {
                    staticLayout = this.letters.get(i);
                }
                float f = this.progress;
                if (f > 0.0f) {
                    if (staticLayout2 != null) {
                        this.textPaint.setAlpha((int) (f * 255.0f));
                        canvas.save();
                        canvas.translate(0.0f, (this.progress - 1.0f) * dp);
                        staticLayout2.draw(canvas);
                        canvas.restore();
                        if (staticLayout != null) {
                            this.textPaint.setAlpha((int) ((1.0f - this.progress) * 255.0f));
                            canvas.translate(0.0f, this.progress * dp);
                        }
                    } else {
                        this.textPaint.setAlpha(255);
                    }
                } else if (f < 0.0f) {
                    if (staticLayout2 != null) {
                        this.textPaint.setAlpha((int) ((-f) * 255.0f));
                        canvas.save();
                        canvas.translate(0.0f, (this.progress + 1.0f) * dp);
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
                i++;
            }
            canvas.restore();
        }
    }
}
