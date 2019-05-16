package org.telegram.ui.Components;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ObjectAnimator;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Typeface;
import android.text.Layout.Alignment;
import android.text.StaticLayout;
import android.text.TextPaint;
import android.view.View;
import java.util.ArrayList;
import java.util.Locale;
import org.telegram.messenger.AndroidUtilities;

public class NumberTextView extends View {
    private ObjectAnimator animator;
    private int currentNumber = 1;
    private ArrayList<StaticLayout> letters = new ArrayList();
    private ArrayList<StaticLayout> oldLetters = new ArrayList();
    private float progress = 0.0f;
    private TextPaint textPaint = new TextPaint(1);

    public NumberTextView(Context context) {
        super(context);
    }

    public void setProgress(float f) {
        if (this.progress != f) {
            this.progress = f;
            invalidate();
        }
    }

    public float getProgress() {
        return this.progress;
    }

    public void setNumber(int i, boolean z) {
        int i2 = i;
        if (this.currentNumber != i2 || !z) {
            ObjectAnimator objectAnimator = this.animator;
            if (objectAnimator != null) {
                objectAnimator.cancel();
                this.animator = null;
            }
            this.oldLetters.clear();
            this.oldLetters.addAll(this.letters);
            this.letters.clear();
            String str = "%d";
            String format = String.format(Locale.US, str, new Object[]{Integer.valueOf(this.currentNumber)});
            String format2 = String.format(Locale.US, str, new Object[]{Integer.valueOf(i)});
            Object obj = i2 > this.currentNumber ? 1 : null;
            this.currentNumber = i2;
            this.progress = 0.0f;
            int i3 = 0;
            while (i3 < format2.length()) {
                int i4 = i3 + 1;
                String substring = format2.substring(i3, i4);
                String substring2 = (this.oldLetters.isEmpty() || i3 >= format.length()) ? null : format.substring(i3, i4);
                if (substring2 == null || !substring2.equals(substring)) {
                    TextPaint textPaint = this.textPaint;
                    this.letters.add(new StaticLayout(substring, textPaint, (int) Math.ceil((double) textPaint.measureText(substring)), Alignment.ALIGN_NORMAL, 1.0f, 0.0f, false));
                } else {
                    this.letters.add(this.oldLetters.get(i3));
                    this.oldLetters.set(i3, null);
                }
                i3 = i4;
            }
            if (z && !this.oldLetters.isEmpty()) {
                float[] fArr = new float[2];
                fArr[0] = obj != null ? -1.0f : 1.0f;
                fArr[1] = 0.0f;
                this.animator = ObjectAnimator.ofFloat(this, "progress", fArr);
                this.animator.setDuration(150);
                this.animator.addListener(new AnimatorListenerAdapter() {
                    public void onAnimationEnd(Animator animator) {
                        NumberTextView.this.animator = null;
                        NumberTextView.this.oldLetters.clear();
                    }
                });
                this.animator.start();
            }
            invalidate();
        }
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

    /* Access modifiers changed, original: protected */
    public void onDraw(Canvas canvas) {
        if (!this.letters.isEmpty()) {
            float height = (float) ((StaticLayout) this.letters.get(0)).getHeight();
            canvas.save();
            canvas.translate((float) getPaddingLeft(), (((float) getMeasuredHeight()) - height) / 2.0f);
            int max = Math.max(this.letters.size(), this.oldLetters.size());
            int i = 0;
            while (i < max) {
                canvas.save();
                StaticLayout staticLayout = null;
                StaticLayout staticLayout2 = i < this.oldLetters.size() ? (StaticLayout) this.oldLetters.get(i) : null;
                if (i < this.letters.size()) {
                    staticLayout = (StaticLayout) this.letters.get(i);
                }
                float f = this.progress;
                if (f > 0.0f) {
                    if (staticLayout2 != null) {
                        this.textPaint.setAlpha((int) (f * 255.0f));
                        canvas.save();
                        canvas.translate(0.0f, (this.progress - 1.0f) * height);
                        staticLayout2.draw(canvas);
                        canvas.restore();
                        if (staticLayout != null) {
                            this.textPaint.setAlpha((int) ((1.0f - this.progress) * 255.0f));
                            canvas.translate(0.0f, this.progress * height);
                        }
                    } else {
                        this.textPaint.setAlpha(255);
                    }
                } else if (f < 0.0f) {
                    if (staticLayout2 != null) {
                        this.textPaint.setAlpha((int) ((-f) * 255.0f));
                        canvas.save();
                        canvas.translate(0.0f, (this.progress + 1.0f) * height);
                        staticLayout2.draw(canvas);
                        canvas.restore();
                    }
                    if (staticLayout != null) {
                        if (i == max - 1 || staticLayout2 != null) {
                            this.textPaint.setAlpha((int) ((this.progress + 1.0f) * 255.0f));
                            canvas.translate(0.0f, this.progress * height);
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
