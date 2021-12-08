package org.telegram.ui.Components;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ObjectAnimator;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Typeface;
import android.text.Layout;
import android.text.StaticLayout;
import android.text.TextPaint;
import android.view.View;
import java.util.ArrayList;
import java.util.Locale;
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
    private OnTextWidthProgressChangedListener onTextWidthProgressChangedListener;
    private float progress = 0.0f;
    private TextPaint textPaint = new TextPaint(1);
    private float textWidth;

    public interface OnTextWidthProgressChangedListener {
        void onTextWidthProgress(float f, float f2, float f3);
    }

    public NumberTextView(Context context) {
        super(context);
    }

    public void setOnTextWidthProgressChangedListener(OnTextWidthProgressChangedListener onTextWidthProgressChangedListener2) {
        this.onTextWidthProgressChangedListener = onTextWidthProgressChangedListener2;
    }

    public void setProgress(float value) {
        if (this.progress != value) {
            this.progress = value;
            OnTextWidthProgressChangedListener onTextWidthProgressChangedListener2 = this.onTextWidthProgressChangedListener;
            if (onTextWidthProgressChangedListener2 != null) {
                onTextWidthProgressChangedListener2.onTextWidthProgress(this.oldTextWidth, this.textWidth, value);
            }
            invalidate();
        }
    }

    public float getProgress() {
        return this.progress;
    }

    public void setAddNumber() {
        this.addNumber = true;
    }

    public void setNumber(int number, boolean animated) {
        boolean forwardAnimation;
        String text;
        String oldText;
        int i = number;
        if (this.currentNumber != i || !animated) {
            ObjectAnimator objectAnimator = this.animator;
            if (objectAnimator != null) {
                objectAnimator.cancel();
                this.animator = null;
            }
            this.oldLetters.clear();
            this.oldLetters.addAll(this.letters);
            this.letters.clear();
            if (this.addNumber) {
                oldText = String.format(Locale.US, "#%d", new Object[]{Integer.valueOf(this.currentNumber)});
                text = String.format(Locale.US, "#%d", new Object[]{Integer.valueOf(number)});
                forwardAnimation = i < this.currentNumber;
            } else {
                oldText = String.format(Locale.US, "%d", new Object[]{Integer.valueOf(this.currentNumber)});
                text = String.format(Locale.US, "%d", new Object[]{Integer.valueOf(number)});
                forwardAnimation = i > this.currentNumber;
            }
            boolean replace = false;
            this.textWidth = this.textPaint.measureText(text);
            float measureText = this.textPaint.measureText(oldText);
            this.oldTextWidth = measureText;
            if (this.center && this.textWidth != measureText) {
                replace = true;
            }
            this.currentNumber = i;
            this.progress = 0.0f;
            int a = 0;
            while (a < text.length()) {
                String ch = text.substring(a, a + 1);
                String oldCh = (this.oldLetters.isEmpty() || a >= oldText.length()) ? null : oldText.substring(a, a + 1);
                if (replace || oldCh == null || !oldCh.equals(ch)) {
                    if (replace && oldCh == null) {
                        this.oldLetters.add(new StaticLayout("", this.textPaint, 0, Layout.Alignment.ALIGN_NORMAL, 1.0f, 0.0f, false));
                    }
                    TextPaint textPaint2 = this.textPaint;
                    String str = oldCh;
                    this.letters.add(new StaticLayout(ch, textPaint2, (int) Math.ceil((double) textPaint2.measureText(ch)), Layout.Alignment.ALIGN_NORMAL, 1.0f, 0.0f, false));
                } else {
                    this.letters.add(this.oldLetters.get(a));
                    this.oldLetters.set(a, (Object) null);
                }
                a++;
            }
            if (!animated || this.oldLetters.isEmpty()) {
                OnTextWidthProgressChangedListener onTextWidthProgressChangedListener2 = this.onTextWidthProgressChangedListener;
                if (onTextWidthProgressChangedListener2 != null) {
                    onTextWidthProgressChangedListener2.onTextWidthProgress(this.oldTextWidth, this.textWidth, this.progress);
                }
            } else {
                float[] fArr = new float[2];
                fArr[0] = forwardAnimation ? -1.0f : 1.0f;
                fArr[1] = 0.0f;
                ObjectAnimator ofFloat = ObjectAnimator.ofFloat(this, "progress", fArr);
                this.animator = ofFloat;
                ofFloat.setDuration(this.addNumber ? 180 : 150);
                this.animator.addListener(new AnimatorListenerAdapter() {
                    public void onAnimationEnd(Animator animation) {
                        ObjectAnimator unused = NumberTextView.this.animator = null;
                        NumberTextView.this.oldLetters.clear();
                    }
                });
                this.animator.start();
            }
            invalidate();
        }
    }

    public void setTextSize(int size) {
        this.textPaint.setTextSize((float) AndroidUtilities.dp((float) size));
        this.oldLetters.clear();
        this.letters.clear();
        setNumber(this.currentNumber, false);
    }

    public void setTextColor(int value) {
        this.textPaint.setColor(value);
        invalidate();
    }

    public void setTypeface(Typeface typeface) {
        this.textPaint.setTypeface(typeface);
        this.oldLetters.clear();
        this.letters.clear();
        setNumber(this.currentNumber, false);
    }

    public void setCenterAlign(boolean center2) {
        this.center = center2;
    }

    /* access modifiers changed from: protected */
    public void onDraw(Canvas canvas) {
        Canvas canvas2 = canvas;
        if (!this.letters.isEmpty()) {
            float height = (float) this.letters.get(0).getHeight();
            float translationHeight = this.addNumber ? (float) AndroidUtilities.dp(4.0f) : height;
            float x = 0.0f;
            float oldDx = 0.0f;
            if (this.center) {
                x = (((float) getMeasuredWidth()) - this.textWidth) / 2.0f;
                oldDx = ((((float) getMeasuredWidth()) - this.oldTextWidth) / 2.0f) - x;
            }
            canvas.save();
            canvas2.translate(((float) getPaddingLeft()) + x, (((float) getMeasuredHeight()) - height) / 2.0f);
            int count = Math.max(this.letters.size(), this.oldLetters.size());
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
                        this.textPaint.setAlpha((int) (f * 255.0f));
                        canvas.save();
                        canvas2.translate(oldDx, (this.progress - 1.0f) * translationHeight);
                        old.draw(canvas2);
                        canvas.restore();
                        if (layout != null) {
                            this.textPaint.setAlpha((int) ((1.0f - this.progress) * 255.0f));
                            canvas2.translate(0.0f, this.progress * translationHeight);
                        }
                    } else {
                        this.textPaint.setAlpha(255);
                    }
                } else if (f < 0.0f) {
                    if (old != null) {
                        this.textPaint.setAlpha((int) ((-f) * 255.0f));
                        canvas.save();
                        canvas2.translate(oldDx, (this.progress + 1.0f) * translationHeight);
                        old.draw(canvas2);
                        canvas.restore();
                    }
                    if (layout != null) {
                        if (a == count - 1 || old != null) {
                            this.textPaint.setAlpha((int) ((this.progress + 1.0f) * 255.0f));
                            canvas2.translate(0.0f, this.progress * translationHeight);
                        } else {
                            this.textPaint.setAlpha(255);
                        }
                    }
                } else if (layout != null) {
                    this.textPaint.setAlpha(255);
                }
                if (layout != null) {
                    layout.draw(canvas2);
                }
                canvas.restore();
                canvas2.translate(layout != null ? layout.getLineWidth(0) : old.getLineWidth(0) + ((float) AndroidUtilities.dp(1.0f)), 0.0f);
                if (layout != null && old != null) {
                    oldDx += old.getLineWidth(0) - layout.getLineWidth(0);
                }
                a++;
            }
            canvas.restore();
        }
    }

    public float getOldTextWidth() {
        return this.oldTextWidth;
    }

    public float getTextWidth() {
        return this.textWidth;
    }
}
