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
import androidx.annotation.Keep;
import androidx.core.util.ObjectsCompat$$ExternalSyntheticBackport0;
import androidx.dynamicanimation.animation.FloatPropertyCompat;
import androidx.dynamicanimation.animation.SpringAnimation;
import androidx.dynamicanimation.animation.SpringForce;
import java.util.ArrayList;
import java.util.List;

public class AnimatedPhoneNumberEditText extends HintEditText {
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

    public void setHintText(String str) {
        String str2;
        boolean z = !TextUtils.isEmpty(str);
        boolean z2 = false;
        Boolean bool = this.wasHintVisible;
        if (bool == null || bool.booleanValue() != z) {
            this.hintAnimationValues.clear();
            for (SpringAnimation cancel : this.hintAnimations) {
                cancel.cancel();
            }
            this.hintAnimations.clear();
            this.wasHintVisible = Boolean.valueOf(z);
            z2 = TextUtils.isEmpty(getText());
        }
        if (z) {
            str2 = str;
        } else {
            str2 = this.wasHint;
        }
        if (str2 == null) {
            str2 = "";
        }
        this.wasHint = str;
        if (z || !z2) {
            super.setHintText(str);
        }
        if (z2) {
            runHintAnimation(str2.length(), z, new AnimatedPhoneNumberEditText$$ExternalSyntheticLambda1(this, z, str));
        }
    }

    /* access modifiers changed from: private */
    public /* synthetic */ void lambda$setHintText$0(boolean z, String str) {
        this.hintAnimationValues.clear();
        for (SpringAnimation cancel : this.hintAnimations) {
            cancel.cancel();
        }
        if (!z) {
            super.setHintText(str);
        }
    }

    public String getHintText() {
        return this.wasHint;
    }

    private void runHintAnimation(int i, boolean z, Runnable runnable) {
        Runnable runnable2 = this.hintAnimationCallback;
        if (runnable2 != null) {
            removeCallbacks(runnable2);
        }
        for (int i2 = 0; i2 < i; i2++) {
            float f = 0.0f;
            float f2 = z ? 0.0f : 1.0f;
            if (z) {
                f = 1.0f;
            }
            float f3 = f * 100.0f;
            SpringAnimation springAnimation = (SpringAnimation) new SpringAnimation(Integer.valueOf(i2), this.hintFadeProperty).setSpring(new SpringForce(f3).setStiffness(500.0f).setDampingRatio(1.0f).setFinalPosition(f3)).setStartValue(100.0f * f2);
            this.hintAnimations.add(springAnimation);
            this.hintAnimationValues.add(Float.valueOf(f2));
            springAnimation.getClass();
            postDelayed(new AnimatedPhoneNumberEditText$$ExternalSyntheticLambda0(springAnimation), ((long) i2) * 5);
        }
        this.hintAnimationCallback = runnable;
        postDelayed(runnable, (((long) i) * 5) + 150);
    }

    public void setTextSize(int i, float f) {
        super.setTextSize(i, f);
        this.textPaint.setTextSize(TypedValue.applyDimension(i, f, getResources().getDisplayMetrics()));
    }

    public void setTextColor(int i) {
        super.setTextColor(i);
        this.textPaint.setColor(i);
    }

    /* access modifiers changed from: protected */
    public void onTextChanged(CharSequence charSequence, int i, int i2, int i3) {
        super.onTextChanged(charSequence, i, i2, i3);
    }

    /* access modifiers changed from: protected */
    public void onDraw(Canvas canvas) {
        super.onDraw(canvas);
    }

    public void setNewText(String str) {
        if (this.oldLetters != null && this.letters != null && !ObjectsCompat$$ExternalSyntheticBackport0.m(this.oldText, str)) {
            ObjectAnimator objectAnimator = this.animator;
            if (objectAnimator != null) {
                objectAnimator.cancel();
                this.animator = null;
            }
            this.oldLetters.clear();
            this.oldLetters.addAll(this.letters);
            this.letters.clear();
            int i = 0;
            boolean z = TextUtils.isEmpty(this.oldText) && !TextUtils.isEmpty(str);
            this.progress = 0.0f;
            while (i < str.length()) {
                int i2 = i + 1;
                String substring = str.substring(i, i2);
                String substring2 = (this.oldLetters.isEmpty() || i >= this.oldText.length()) ? null : this.oldText.substring(i, i2);
                if (z || substring2 == null || !substring2.equals(substring)) {
                    if (z && substring2 == null) {
                        this.oldLetters.add(new StaticLayout("", this.textPaint, 0, Layout.Alignment.ALIGN_NORMAL, 1.0f, 0.0f, false));
                    }
                    TextPaint textPaint2 = this.textPaint;
                    this.letters.add(new StaticLayout(substring, textPaint2, (int) Math.ceil((double) textPaint2.measureText(substring)), Layout.Alignment.ALIGN_NORMAL, 1.0f, 0.0f, false));
                } else {
                    this.letters.add(this.oldLetters.get(i));
                    this.oldLetters.set(i, (Object) null);
                }
                i = i2;
            }
            if (!this.oldLetters.isEmpty()) {
                ObjectAnimator ofFloat = ObjectAnimator.ofFloat(this, "progress", new float[]{-1.0f, 0.0f});
                this.animator = ofFloat;
                ofFloat.setDuration(150);
                this.animator.addListener(new AnimatorListenerAdapter() {
                    public void onAnimationEnd(Animator animator) {
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
    public void onPreDrawHintCharacter(int i, Canvas canvas, float f, float f2) {
        if (i < this.hintAnimationValues.size()) {
            this.hintPaint.setAlpha((int) (this.hintAnimationValues.get(i).floatValue() * 255.0f));
        }
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

    private final class HintFadeProperty extends FloatPropertyCompat<Integer> {
        public HintFadeProperty() {
            super("hint_fade");
        }

        public float getValue(Integer num) {
            if (num.intValue() < AnimatedPhoneNumberEditText.this.hintAnimationValues.size()) {
                return ((Float) AnimatedPhoneNumberEditText.this.hintAnimationValues.get(num.intValue())).floatValue() * 100.0f;
            }
            return 0.0f;
        }

        public void setValue(Integer num, float f) {
            if (num.intValue() < AnimatedPhoneNumberEditText.this.hintAnimationValues.size()) {
                AnimatedPhoneNumberEditText.this.hintAnimationValues.set(num.intValue(), Float.valueOf(f / 100.0f));
                AnimatedPhoneNumberEditText.this.invalidate();
            }
        }
    }
}
