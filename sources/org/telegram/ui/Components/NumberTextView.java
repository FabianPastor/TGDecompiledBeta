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
    private float progress = null;
    private TextPaint textPaint = new TextPaint(1);

    /* renamed from: org.telegram.ui.Components.NumberTextView$1 */
    class C11951 extends AnimatorListenerAdapter {
        C11951() {
        }

        public void onAnimationEnd(Animator animator) {
            NumberTextView.this.animator = null;
            NumberTextView.this.oldLetters.clear();
        }
    }

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
            if (r0.animator != null) {
                r0.animator.cancel();
                r0.animator = null;
            }
            r0.oldLetters.clear();
            r0.oldLetters.addAll(r0.letters);
            r0.letters.clear();
            String format = String.format(Locale.US, "%d", new Object[]{Integer.valueOf(r0.currentNumber)});
            String format2 = String.format(Locale.US, "%d", new Object[]{Integer.valueOf(i)});
            int i3 = i2 > r0.currentNumber ? 1 : 0;
            r0.currentNumber = i2;
            r0.progress = 0.0f;
            int i4 = 0;
            while (i4 < format2.length()) {
                int i5 = i4 + 1;
                CharSequence substring = format2.substring(i4, i5);
                String substring2 = (r0.oldLetters.isEmpty() || i4 >= format.length()) ? null : format.substring(i4, i5);
                if (substring2 == null || !substring2.equals(substring)) {
                    r0.letters.add(new StaticLayout(substring, r0.textPaint, (int) Math.ceil((double) r0.textPaint.measureText(substring)), Alignment.ALIGN_NORMAL, 1.0f, 0.0f, false));
                } else {
                    r0.letters.add(r0.oldLetters.get(i4));
                    r0.oldLetters.set(i4, null);
                }
                i4 = i5;
            }
            if (z && !r0.oldLetters.isEmpty()) {
                String str = "progress";
                float[] fArr = new float[2];
                fArr[0] = i3 != 0 ? -1.0f : 1.0f;
                fArr[1] = 0.0f;
                r0.animator = ObjectAnimator.ofFloat(r0, str, fArr);
                r0.animator.setDuration(150);
                r0.animator.addListener(new C11951());
                r0.animator.start();
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

    protected void onDraw(Canvas canvas) {
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
                if (this.progress > 0.0f) {
                    if (staticLayout2 != null) {
                        this.textPaint.setAlpha((int) (this.progress * 255.0f));
                        canvas.save();
                        canvas.translate(0.0f, (this.progress - 1.0f) * height);
                        staticLayout2.draw(canvas);
                        canvas.restore();
                        if (staticLayout != null) {
                            this.textPaint.setAlpha((int) (255.0f * (1.0f - this.progress)));
                            canvas.translate(0.0f, this.progress * height);
                        }
                    } else {
                        this.textPaint.setAlpha(255);
                    }
                } else if (this.progress < 0.0f) {
                    if (staticLayout2 != null) {
                        this.textPaint.setAlpha((int) ((-this.progress) * 255.0f));
                        canvas.save();
                        canvas.translate(0.0f, (this.progress + 1.0f) * height);
                        staticLayout2.draw(canvas);
                        canvas.restore();
                    }
                    if (staticLayout != null) {
                        if (i != max - 1) {
                            if (staticLayout2 == null) {
                                this.textPaint.setAlpha(255);
                            }
                        }
                        this.textPaint.setAlpha((int) (255.0f * (this.progress + 1.0f)));
                        canvas.translate(0.0f, this.progress * height);
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
