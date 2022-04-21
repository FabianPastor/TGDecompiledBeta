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
        public void setValue(AnimatedNumberLayout object, float value) {
            object.setProgress(value);
        }

        public Float get(AnimatedNumberLayout object) {
            return Float.valueOf(object.progress);
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

    public AnimatedNumberLayout(View parent, TextPaint paint) {
        this.textPaint = paint;
        this.parentView = parent;
    }

    /* access modifiers changed from: private */
    public void setProgress(float value) {
        if (this.progress != value) {
            this.progress = value;
            this.parentView.invalidate();
        }
    }

    private float getProgress() {
        return this.progress;
    }

    public int getWidth() {
        float width = 0.0f;
        int count = this.letters.size();
        for (int a = 0; a < count; a++) {
            width += this.letters.get(a).getLineWidth(0);
        }
        return (int) Math.ceil((double) width);
    }

    public void setNumber(int number, boolean animated) {
        int i = number;
        if (this.currentNumber != i || this.letters.isEmpty()) {
            ObjectAnimator objectAnimator = this.animator;
            if (objectAnimator != null) {
                objectAnimator.cancel();
                this.animator = null;
            }
            this.oldLetters.clear();
            this.oldLetters.addAll(this.letters);
            this.letters.clear();
            String oldText = String.format(Locale.US, "%d", new Object[]{Integer.valueOf(this.currentNumber)});
            String text = String.format(Locale.US, "%d", new Object[]{Integer.valueOf(number)});
            boolean forwardAnimation = i > this.currentNumber;
            this.currentNumber = i;
            this.progress = 0.0f;
            int a = 0;
            while (a < text.length()) {
                String ch = text.substring(a, a + 1);
                String oldCh = (this.oldLetters.isEmpty() || a >= oldText.length()) ? null : oldText.substring(a, a + 1);
                if (oldCh == null || !oldCh.equals(ch)) {
                    TextPaint textPaint2 = this.textPaint;
                    String str = oldCh;
                    this.letters.add(new StaticLayout(ch, textPaint2, (int) Math.ceil((double) textPaint2.measureText(ch)), Layout.Alignment.ALIGN_NORMAL, 1.0f, 0.0f, false));
                } else {
                    this.letters.add(this.oldLetters.get(a));
                    this.oldLetters.set(a, (Object) null);
                }
                a++;
            }
            if (animated && !this.oldLetters.isEmpty()) {
                Property<AnimatedNumberLayout, Float> property = PROGRESS;
                float[] fArr = new float[2];
                fArr[0] = forwardAnimation ? -1.0f : 1.0f;
                fArr[1] = 0.0f;
                ObjectAnimator ofFloat = ObjectAnimator.ofFloat(this, property, fArr);
                this.animator = ofFloat;
                ofFloat.setDuration(150);
                this.animator.addListener(new AnimatorListenerAdapter() {
                    public void onAnimationEnd(Animator animation) {
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
            int count = Math.max(this.letters.size(), this.oldLetters.size());
            canvas.save();
            int currentAlpha = this.textPaint.getAlpha();
            int a = 0;
            while (a < count) {
                canvas.save();
                StaticLayout layout = null;
                StaticLayout old = a < this.oldLetters.size() ? this.oldLetters.get(a) : null;
                if (a < this.letters.size()) {
                    layout = this.letters.get(a);
                }
                float f = this.progress;
                if (f > 0.0f) {
                    if (old != null) {
                        this.textPaint.setAlpha((int) (((float) currentAlpha) * f));
                        canvas.save();
                        canvas.translate(0.0f, (this.progress - 1.0f) * height);
                        old.draw(canvas);
                        canvas.restore();
                        if (layout != null) {
                            this.textPaint.setAlpha((int) (((float) currentAlpha) * (1.0f - this.progress)));
                            canvas.translate(0.0f, this.progress * height);
                        }
                    } else {
                        this.textPaint.setAlpha(currentAlpha);
                    }
                } else if (f < 0.0f) {
                    if (old != null) {
                        this.textPaint.setAlpha((int) (((float) currentAlpha) * (-f)));
                        canvas.save();
                        canvas.translate(0.0f, (this.progress + 1.0f) * height);
                        old.draw(canvas);
                        canvas.restore();
                    }
                    if (layout != null) {
                        if (a == count - 1 || old != null) {
                            this.textPaint.setAlpha((int) (((float) currentAlpha) * (this.progress + 1.0f)));
                            canvas.translate(0.0f, this.progress * height);
                        } else {
                            this.textPaint.setAlpha(currentAlpha);
                        }
                    }
                } else if (layout != null) {
                    this.textPaint.setAlpha(currentAlpha);
                }
                if (layout != null) {
                    layout.draw(canvas);
                }
                canvas.restore();
                canvas.translate(layout != null ? layout.getLineWidth(0) : old.getLineWidth(0), 0.0f);
                a++;
            }
            canvas.restore();
        }
    }
}
