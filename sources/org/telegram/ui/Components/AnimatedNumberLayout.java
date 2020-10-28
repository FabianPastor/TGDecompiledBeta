package org.telegram.ui.Components;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ObjectAnimator;
import android.graphics.Canvas;
import android.text.Layout;
import android.text.StaticLayout;
import android.text.TextPaint;
import android.util.Property;
import android.view.View;
import java.util.ArrayList;
import java.util.Locale;
import org.telegram.ui.Components.AnimationProperties;

public class AnimatedNumberLayout {
    public static final Property<AnimatedNumberLayout, Float> PROGRESS = new AnimationProperties.FloatProperty<AnimatedNumberLayout>("progress") {
        public void setValue(AnimatedNumberLayout animatedNumberLayout, float f) {
            animatedNumberLayout.setProgress(f);
        }

        public Float get(AnimatedNumberLayout animatedNumberLayout) {
            return Float.valueOf(animatedNumberLayout.progress);
        }
    };
    /* access modifiers changed from: private */
    public ObjectAnimator animator;
    private int currentNumber = 1;
    private ArrayList<StaticLayout> letters = new ArrayList<>();
    /* access modifiers changed from: private */
    public ArrayList<StaticLayout> oldLetters = new ArrayList<>();
    private final View parentView;
    /* access modifiers changed from: private */
    public float progress = 0.0f;
    private final TextPaint textPaint;

    public AnimatedNumberLayout(View view, TextPaint textPaint2) {
        this.textPaint = textPaint2;
        this.parentView = view;
    }

    /* access modifiers changed from: private */
    public void setProgress(float f) {
        if (this.progress != f) {
            this.progress = f;
            this.parentView.invalidate();
        }
    }

    public int getWidth() {
        int size = this.letters.size();
        float f = 0.0f;
        for (int i = 0; i < size; i++) {
            f += this.letters.get(i).getLineWidth(0);
        }
        return (int) Math.ceil((double) f);
    }

    public void setNumber(int i, boolean z) {
        int i2 = i;
        if (this.currentNumber != i2 || this.letters.isEmpty()) {
            ObjectAnimator objectAnimator = this.animator;
            if (objectAnimator != null) {
                objectAnimator.cancel();
                this.animator = null;
            }
            this.oldLetters.clear();
            this.oldLetters.addAll(this.letters);
            this.letters.clear();
            Locale locale = Locale.US;
            String format = String.format(locale, "%d", new Object[]{Integer.valueOf(this.currentNumber)});
            String format2 = String.format(locale, "%d", new Object[]{Integer.valueOf(i)});
            boolean z2 = i2 > this.currentNumber;
            this.currentNumber = i2;
            this.progress = 0.0f;
            int i3 = 0;
            while (i3 < format2.length()) {
                int i4 = i3 + 1;
                String substring = format2.substring(i3, i4);
                String substring2 = (this.oldLetters.isEmpty() || i3 >= format.length()) ? null : format.substring(i3, i4);
                if (substring2 == null || !substring2.equals(substring)) {
                    TextPaint textPaint2 = this.textPaint;
                    this.letters.add(new StaticLayout(substring, textPaint2, (int) Math.ceil((double) textPaint2.measureText(substring)), Layout.Alignment.ALIGN_NORMAL, 1.0f, 0.0f, false));
                } else {
                    this.letters.add(this.oldLetters.get(i3));
                    this.oldLetters.set(i3, (Object) null);
                }
                i3 = i4;
            }
            if (z && !this.oldLetters.isEmpty()) {
                Property<AnimatedNumberLayout, Float> property = PROGRESS;
                float[] fArr = new float[2];
                fArr[0] = z2 ? -1.0f : 1.0f;
                fArr[1] = 0.0f;
                ObjectAnimator ofFloat = ObjectAnimator.ofFloat(this, property, fArr);
                this.animator = ofFloat;
                ofFloat.setDuration(150);
                this.animator.addListener(new AnimatorListenerAdapter() {
                    public void onAnimationEnd(Animator animator) {
                        ObjectAnimator unused = AnimatedNumberLayout.this.animator = null;
                        AnimatedNumberLayout.this.oldLetters.clear();
                    }
                });
                this.animator.start();
            }
            this.parentView.invalidate();
        }
    }

    public void draw(Canvas canvas) {
        if (!this.letters.isEmpty()) {
            float height = (float) this.letters.get(0).getHeight();
            int max = Math.max(this.letters.size(), this.oldLetters.size());
            canvas.save();
            int alpha = this.textPaint.getAlpha();
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
                        float f2 = (float) alpha;
                        this.textPaint.setAlpha((int) (f * f2));
                        canvas.save();
                        canvas.translate(0.0f, (this.progress - 1.0f) * height);
                        staticLayout2.draw(canvas);
                        canvas.restore();
                        if (staticLayout != null) {
                            this.textPaint.setAlpha((int) (f2 * (1.0f - this.progress)));
                            canvas.translate(0.0f, this.progress * height);
                        }
                    } else {
                        this.textPaint.setAlpha(alpha);
                    }
                } else if (f < 0.0f) {
                    if (staticLayout2 != null) {
                        this.textPaint.setAlpha((int) (((float) alpha) * (-f)));
                        canvas.save();
                        canvas.translate(0.0f, (this.progress + 1.0f) * height);
                        staticLayout2.draw(canvas);
                        canvas.restore();
                    }
                    if (staticLayout != null) {
                        if (i == max - 1 || staticLayout2 != null) {
                            this.textPaint.setAlpha((int) (((float) alpha) * (this.progress + 1.0f)));
                            canvas.translate(0.0f, this.progress * height);
                        } else {
                            this.textPaint.setAlpha(alpha);
                        }
                    }
                } else if (staticLayout != null) {
                    this.textPaint.setAlpha(alpha);
                }
                if (staticLayout != null) {
                    staticLayout.draw(canvas);
                }
                canvas.restore();
                canvas.translate(staticLayout != null ? staticLayout.getLineWidth(0) : staticLayout2.getLineWidth(0), 0.0f);
                i++;
            }
            canvas.restore();
        }
    }
}
