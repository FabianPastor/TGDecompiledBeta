package org.telegram.ui.Components;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ObjectAnimator;
import android.content.Context;
import android.graphics.Canvas;
import android.text.Layout;
import android.text.StaticLayout;
import android.text.TextPaint;
import android.text.TextUtils;
import android.util.TypedValue;
import androidx.core.graphics.ColorUtils$$ExternalSyntheticBackport0;
import androidx.dynamicanimation.animation.FloatPropertyCompat;
import androidx.dynamicanimation.animation.SpringAnimation;
import androidx.dynamicanimation.animation.SpringForce;
import java.util.ArrayList;
import java.util.List;

public class AnimatedPhoneNumberEditText extends HintEditText {
    private static final float SPRING_MULTIPLIER = 100.0f;
    private static final boolean USE_NUMBERS_ANIMATION = false;
    /* access modifiers changed from: private */
    public ObjectAnimator animator;
    private Runnable hintAnimationCallback;
    /* access modifiers changed from: private */
    public List<Float> hintAnimationValues = new ArrayList();
    private List<SpringAnimation> hintAnimations = new ArrayList();
    private HintFadeProperty hintFadeProperty = new HintFadeProperty();
    private ArrayList<StaticLayout> letters = new ArrayList<>();
    /* access modifiers changed from: private */
    public ArrayList<StaticLayout> oldLetters = new ArrayList<>();
    private String oldText = "";
    private float progress;
    private TextPaint textPaint = new TextPaint(1);
    private String wasHint;
    private Boolean wasHintVisible;

    public AnimatedPhoneNumberEditText(Context context) {
        super(context);
    }

    public void setHintText(String value) {
        boolean show = !TextUtils.isEmpty(value);
        boolean runAnimation = false;
        Boolean bool = this.wasHintVisible;
        if (bool == null || bool.booleanValue() != show) {
            this.hintAnimationValues.clear();
            for (SpringAnimation a : this.hintAnimations) {
                a.cancel();
            }
            this.hintAnimations.clear();
            this.wasHintVisible = Boolean.valueOf(show);
            runAnimation = TextUtils.isEmpty(getText());
        }
        String str = show ? value : this.wasHint;
        if (str == null) {
            str = "";
        }
        this.wasHint = value;
        if (show || !runAnimation) {
            super.setHintText(value);
        }
        if (runAnimation) {
            runHintAnimation(str.length(), show, new AnimatedPhoneNumberEditText$$ExternalSyntheticLambda1(this, show, value));
        }
    }

    /* renamed from: lambda$setHintText$0$org-telegram-ui-Components-AnimatedPhoneNumberEditText  reason: not valid java name */
    public /* synthetic */ void m532xbfd6e9c9(boolean show, String value) {
        this.hintAnimationValues.clear();
        for (SpringAnimation a : this.hintAnimations) {
            a.cancel();
        }
        if (!show) {
            super.setHintText(value);
        }
    }

    public String getHintText() {
        return this.wasHint;
    }

    private void runHintAnimation(int length, boolean show, Runnable callback) {
        Runnable runnable = this.hintAnimationCallback;
        if (runnable != null) {
            removeCallbacks(runnable);
        }
        for (int i = 0; i < length; i++) {
            float finalValue = 0.0f;
            float startValue = show ? 0.0f : 1.0f;
            if (show) {
                finalValue = 1.0f;
            }
            SpringAnimation springAnimation = (SpringAnimation) new SpringAnimation(Integer.valueOf(i), this.hintFadeProperty).setSpring(new SpringForce(finalValue * 100.0f).setStiffness(500.0f).setDampingRatio(1.0f).setFinalPosition(finalValue * 100.0f)).setStartValue(100.0f * startValue);
            this.hintAnimations.add(springAnimation);
            this.hintAnimationValues.add(Float.valueOf(startValue));
            springAnimation.getClass();
            postDelayed(new AnimatedPhoneNumberEditText$$ExternalSyntheticLambda0(springAnimation), ((long) i) * 5);
        }
        this.hintAnimationCallback = callback;
        postDelayed(callback, (((long) length) * 5) + 150);
    }

    public void setTextSize(int unit, float size) {
        super.setTextSize(unit, size);
        this.textPaint.setTextSize(TypedValue.applyDimension(unit, size, getResources().getDisplayMetrics()));
    }

    public void setTextColor(int color) {
        super.setTextColor(color);
        this.textPaint.setColor(color);
    }

    /* access modifiers changed from: protected */
    public void onTextChanged(CharSequence text, int start, int lengthBefore, int lengthAfter) {
        super.onTextChanged(text, start, lengthBefore, lengthAfter);
    }

    /* access modifiers changed from: protected */
    public void onDraw(Canvas canvas) {
        super.onDraw(canvas);
    }

    public void setNewText(String text) {
        String str = text;
        if (this.oldLetters != null && this.letters != null && !ColorUtils$$ExternalSyntheticBackport0.m(this.oldText, str)) {
            ObjectAnimator objectAnimator = this.animator;
            if (objectAnimator != null) {
                objectAnimator.cancel();
                this.animator = null;
            }
            this.oldLetters.clear();
            this.oldLetters.addAll(this.letters);
            this.letters.clear();
            boolean replace = TextUtils.isEmpty(this.oldText) && !TextUtils.isEmpty(text);
            this.progress = 0.0f;
            int a = 0;
            while (a < text.length()) {
                String ch = str.substring(a, a + 1);
                String oldCh = (this.oldLetters.isEmpty() || a >= this.oldText.length()) ? null : this.oldText.substring(a, a + 1);
                if (replace || oldCh == null || !oldCh.equals(ch)) {
                    if (replace && oldCh == null) {
                        this.oldLetters.add(new StaticLayout("", this.textPaint, 0, Layout.Alignment.ALIGN_NORMAL, 1.0f, 0.0f, false));
                    }
                    TextPaint textPaint2 = this.textPaint;
                    this.letters.add(new StaticLayout(ch, textPaint2, (int) Math.ceil((double) textPaint2.measureText(ch)), Layout.Alignment.ALIGN_NORMAL, 1.0f, 0.0f, false));
                } else {
                    this.letters.add(this.oldLetters.get(a));
                    this.oldLetters.set(a, (Object) null);
                }
                a++;
            }
            if (!this.oldLetters.isEmpty()) {
                ObjectAnimator ofFloat = ObjectAnimator.ofFloat(this, "progress", new float[]{-1.0f, 0.0f});
                this.animator = ofFloat;
                ofFloat.setDuration(150);
                this.animator.addListener(new AnimatorListenerAdapter() {
                    public void onAnimationEnd(Animator animation) {
                        ObjectAnimator unused = AnimatedPhoneNumberEditText.this.animator = null;
                        AnimatedPhoneNumberEditText.this.oldLetters.clear();
                    }
                });
                this.animator.start();
            }
            this.oldText = str;
            invalidate();
        }
    }

    /* access modifiers changed from: protected */
    public void onPreDrawHintCharacter(int index, Canvas canvas, float pivotX, float pivotY) {
        if (index < this.hintAnimationValues.size()) {
            this.hintPaint.setAlpha((int) (this.hintAnimationValues.get(index).floatValue() * 255.0f));
        }
    }

    public void setProgress(float value) {
        if (this.progress != value) {
            this.progress = value;
            invalidate();
        }
    }

    public float getProgress() {
        return this.progress;
    }

    private final class HintFadeProperty extends FloatPropertyCompat<Integer> {
        public HintFadeProperty() {
            super("hint_fade");
        }

        public float getValue(Integer object) {
            if (object.intValue() < AnimatedPhoneNumberEditText.this.hintAnimationValues.size()) {
                return ((Float) AnimatedPhoneNumberEditText.this.hintAnimationValues.get(object.intValue())).floatValue() * 100.0f;
            }
            return 0.0f;
        }

        public void setValue(Integer object, float value) {
            if (object.intValue() < AnimatedPhoneNumberEditText.this.hintAnimationValues.size()) {
                AnimatedPhoneNumberEditText.this.hintAnimationValues.set(object.intValue(), Float.valueOf(value / 100.0f));
                AnimatedPhoneNumberEditText.this.invalidate();
            }
        }
    }
}
