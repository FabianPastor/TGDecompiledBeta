package org.telegram.ui.Components;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.TimeInterpolator;
import android.animation.ValueAnimator;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.ColorFilter;
import android.graphics.Point;
import android.graphics.Rect;
import android.graphics.Typeface;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.text.Layout;
import android.text.StaticLayout;
import android.text.TextPaint;
import android.text.TextUtils;
import android.view.View;
import android.view.accessibility.AccessibilityNodeInfo;
import j$.util.stream.IntStream;
import j$.wrappers.C$r8$wrapper$java$util$stream$IntStream$VWRP;
import java.util.ArrayList;
import java.util.Arrays;
import org.telegram.messenger.AndroidUtilities;

public class AnimatedTextView extends View {
    private AnimatedTextDrawable drawable;
    private boolean first = true;
    private int lastMaxWidth;
    private boolean toSetMoveDown;
    private CharSequence toSetText;

    public static class AnimatedTextDrawable extends Drawable {
        private int alpha;
        private long animateDelay;
        private long animateDuration;
        private TimeInterpolator animateInterpolator;
        /* access modifiers changed from: private */
        public ValueAnimator animator;
        private Rect bounds;
        private int currentHeight;
        private StaticLayout[] currentLayout;
        private Integer[] currentLayoutOffsets;
        private Integer[] currentLayoutToOldIndex;
        private CharSequence currentText;
        private int currentWidth;
        private int gravity;
        private boolean isRTL;
        private float moveAmplitude;
        private boolean moveDown;
        private int oldHeight;
        /* access modifiers changed from: private */
        public StaticLayout[] oldLayout;
        /* access modifiers changed from: private */
        public Integer[] oldLayoutOffsets;
        /* access modifiers changed from: private */
        public Integer[] oldLayoutToCurrentIndex;
        /* access modifiers changed from: private */
        public CharSequence oldText;
        /* access modifiers changed from: private */
        public int oldWidth;
        /* access modifiers changed from: private */
        public Runnable onAnimationFinishListener;
        private boolean preserveIndex;
        private boolean splitByWords;
        private boolean startFromEnd;
        /* access modifiers changed from: private */
        public float t;
        private TextPaint textPaint;
        /* access modifiers changed from: private */
        public CharSequence toSetText;
        /* access modifiers changed from: private */
        public boolean toSetTextMoveDown;

        private interface RegionCallback {
            void run(CharSequence charSequence, int i, int i2);
        }

        @Deprecated
        public int getOpacity() {
            return -2;
        }

        public AnimatedTextDrawable() {
            this(false, false, false);
        }

        public AnimatedTextDrawable(boolean z, boolean z2, boolean z3) {
            this.textPaint = new TextPaint(1);
            this.gravity = 0;
            this.isRTL = false;
            this.t = 0.0f;
            this.moveDown = true;
            this.animateDelay = 0;
            this.animateDuration = 450;
            this.animateInterpolator = CubicBezierInterpolator.EASE_OUT_QUINT;
            this.moveAmplitude = 1.0f;
            this.alpha = 255;
            this.bounds = new Rect();
            this.splitByWords = z;
            this.preserveIndex = z2;
            this.startFromEnd = z3;
        }

        public void setOnAnimationFinishListener(Runnable runnable) {
            this.onAnimationFinishListener = runnable;
        }

        public void draw(Canvas canvas) {
            float f;
            float f2;
            float f3;
            int i;
            float f4;
            canvas.save();
            Rect rect = this.bounds;
            canvas.translate((float) rect.left, (float) rect.top);
            int width = this.bounds.width();
            int height = this.bounds.height();
            int i2 = 0;
            if (this.currentLayout == null || this.oldLayout == null) {
                canvas.translate(0.0f, ((float) (height - this.currentHeight)) / 2.0f);
                if (this.currentLayout != null) {
                    while (i2 < this.currentLayout.length) {
                        this.textPaint.setAlpha(this.alpha);
                        canvas.save();
                        float intValue = (float) this.currentLayoutOffsets[i2].intValue();
                        if (this.isRTL) {
                            intValue = (((-intValue) + ((float) (this.currentWidth * 2))) - ((float) this.currentLayout[i2].getWidth())) - ((float) width);
                        }
                        int i3 = this.gravity;
                        if ((i3 & 1) > 0) {
                            f = ((float) (width - this.currentWidth)) / 2.0f;
                        } else if ((i3 & 5) > 0 || this.isRTL) {
                            f = (float) (width - this.currentWidth);
                        } else {
                            canvas.translate(intValue, 0.0f);
                            this.currentLayout[i2].draw(canvas);
                            canvas.restore();
                            i2++;
                        }
                        intValue += f;
                        canvas.translate(intValue, 0.0f);
                        this.currentLayout[i2].draw(canvas);
                        canvas.restore();
                        i2++;
                    }
                }
            } else {
                int lerp = AndroidUtilities.lerp(this.oldWidth, this.currentWidth, this.t);
                canvas.translate(0.0f, ((float) (height - AndroidUtilities.lerp(this.oldHeight, this.currentHeight, this.t))) / 2.0f);
                int i4 = 0;
                while (true) {
                    float f5 = -1.0f;
                    if (i4 >= this.currentLayout.length) {
                        break;
                    }
                    int intValue2 = this.currentLayoutToOldIndex[i4].intValue();
                    float intValue3 = (float) this.currentLayoutOffsets[i4].intValue();
                    if (intValue2 >= 0) {
                        intValue3 = AndroidUtilities.lerp((float) this.oldLayoutOffsets[intValue2].intValue(), intValue3, this.t);
                        this.textPaint.setAlpha(this.alpha);
                        f3 = 0.0f;
                    } else {
                        float f6 = (-this.textPaint.getTextSize()) * this.moveAmplitude;
                        float f7 = this.t;
                        float f8 = f6 * (1.0f - f7);
                        if (this.moveDown) {
                            f5 = 1.0f;
                        }
                        f3 = f8 * f5;
                        this.textPaint.setAlpha((int) (((float) this.alpha) * f7));
                    }
                    canvas.save();
                    if (intValue2 >= 0) {
                        i = lerp;
                    } else {
                        i = this.currentWidth;
                    }
                    if (this.isRTL) {
                        intValue3 = (((-intValue3) + ((float) (i * 2))) - ((float) this.currentLayout[i4].getWidth())) - ((float) width);
                    }
                    int i5 = this.gravity;
                    if ((i5 & 1) > 0) {
                        f4 = ((float) (width - i)) / 2.0f;
                    } else if ((i5 & 5) > 0 || this.isRTL) {
                        f4 = (float) (width - i);
                    } else {
                        canvas.translate(intValue3, f3);
                        this.currentLayout[i4].draw(canvas);
                        canvas.restore();
                        i4++;
                    }
                    intValue3 += f4;
                    canvas.translate(intValue3, f3);
                    this.currentLayout[i4].draw(canvas);
                    canvas.restore();
                    i4++;
                }
                while (i2 < this.oldLayout.length) {
                    if (this.oldLayoutToCurrentIndex[i2].intValue() < 0) {
                        float intValue4 = (float) this.oldLayoutOffsets[i2].intValue();
                        float textSize = this.textPaint.getTextSize() * this.moveAmplitude;
                        float f9 = this.t;
                        float var_ = textSize * f9 * (this.moveDown ? 1.0f : -1.0f);
                        this.textPaint.setAlpha((int) (((float) this.alpha) * (1.0f - f9)));
                        canvas.save();
                        if (this.isRTL) {
                            intValue4 = (((-intValue4) + ((float) (this.oldWidth * 2))) - ((float) this.oldLayout[i2].getWidth())) - ((float) width);
                        }
                        int i6 = this.gravity;
                        if ((i6 & 1) > 0) {
                            f2 = ((float) (width - this.oldWidth)) / 2.0f;
                        } else {
                            if ((i6 & 5) > 0 || this.isRTL) {
                                f2 = (float) (width - this.oldWidth);
                            }
                            canvas.translate(intValue4, var_);
                            this.oldLayout[i2].draw(canvas);
                            canvas.restore();
                        }
                        intValue4 += f2;
                        canvas.translate(intValue4, var_);
                        this.oldLayout[i2].draw(canvas);
                        canvas.restore();
                    }
                    i2++;
                }
            }
            canvas.restore();
        }

        public void cancelAnimation() {
            ValueAnimator valueAnimator = this.animator;
            if (valueAnimator != null) {
                valueAnimator.cancel();
            }
        }

        public boolean isAnimating() {
            ValueAnimator valueAnimator = this.animator;
            return valueAnimator != null && valueAnimator.isRunning();
        }

        public void setText(CharSequence charSequence, boolean z, boolean z2) {
            boolean z3 = z2;
            boolean z4 = (this.currentText == null || charSequence == null) ? false : z;
            String str = charSequence == null ? "" : charSequence;
            if (!z4) {
                ValueAnimator valueAnimator = this.animator;
                if (valueAnimator != null) {
                    valueAnimator.cancel();
                }
                this.animator = null;
                this.toSetText = null;
                this.toSetTextMoveDown = false;
                this.t = 0.0f;
                StaticLayout[] staticLayoutArr = new StaticLayout[1];
                this.currentLayout = staticLayoutArr;
                this.currentText = str;
                staticLayoutArr[0] = makeLayout(str, this.bounds.width());
                this.currentWidth = (int) this.currentLayout[0].getLineWidth(0);
                this.currentHeight = this.currentLayout[0].getHeight();
                Integer[] numArr = new Integer[1];
                this.currentLayoutOffsets = numArr;
                numArr[0] = 0;
                Integer[] numArr2 = new Integer[1];
                this.currentLayoutToOldIndex = numArr2;
                numArr2[0] = -1;
                StaticLayout[] staticLayoutArr2 = this.currentLayout;
                if (staticLayoutArr2.length > 0) {
                    this.isRTL = staticLayoutArr2[0].isRtlCharAt(0);
                }
                this.oldLayout = null;
                this.oldLayoutOffsets = null;
                this.oldLayoutToCurrentIndex = null;
                this.oldText = null;
                this.oldWidth = 0;
                this.oldHeight = 0;
                invalidateSelf();
            } else if (isAnimating()) {
                this.toSetText = str;
                this.toSetTextMoveDown = z3;
            } else if (!str.equals(this.currentText)) {
                this.oldText = this.currentText;
                this.currentText = str;
                this.currentLayout = null;
                this.oldLayout = null;
                ArrayList arrayList = new ArrayList();
                ArrayList arrayList2 = new ArrayList();
                ArrayList arrayList3 = new ArrayList();
                ArrayList arrayList4 = new ArrayList();
                ArrayList arrayList5 = new ArrayList();
                ArrayList arrayList6 = new ArrayList();
                this.currentHeight = 0;
                this.currentWidth = 0;
                this.oldHeight = 0;
                this.oldWidth = 0;
                ArrayList arrayList7 = arrayList6;
                ArrayList arrayList8 = arrayList5;
                diff(this.splitByWords ? new WordSequence(this.oldText) : this.oldText, this.splitByWords ? new WordSequence(this.currentText) : this.currentText, new AnimatedTextView$AnimatedTextDrawable$$ExternalSyntheticLambda3(this, arrayList5, arrayList3, arrayList2, arrayList6, arrayList, arrayList4), new AnimatedTextView$AnimatedTextDrawable$$ExternalSyntheticLambda2(this, arrayList, arrayList3, arrayList2), new AnimatedTextView$AnimatedTextDrawable$$ExternalSyntheticLambda1(this, arrayList4, arrayList7, arrayList8));
                StaticLayout[] staticLayoutArr3 = this.currentLayout;
                if (staticLayoutArr3 == null || staticLayoutArr3.length != arrayList3.size()) {
                    this.currentLayout = new StaticLayout[arrayList3.size()];
                }
                arrayList3.toArray(this.currentLayout);
                Integer[] numArr3 = this.currentLayoutOffsets;
                if (numArr3 == null || numArr3.length != arrayList.size()) {
                    this.currentLayoutOffsets = new Integer[arrayList.size()];
                }
                arrayList.toArray(this.currentLayoutOffsets);
                Integer[] numArr4 = this.currentLayoutToOldIndex;
                if (numArr4 == null || numArr4.length != arrayList2.size()) {
                    this.currentLayoutToOldIndex = new Integer[arrayList2.size()];
                }
                arrayList2.toArray(this.currentLayoutToOldIndex);
                StaticLayout[] staticLayoutArr4 = this.oldLayout;
                if (staticLayoutArr4 == null || staticLayoutArr4.length != arrayList7.size()) {
                    this.oldLayout = new StaticLayout[arrayList7.size()];
                }
                arrayList7.toArray(this.oldLayout);
                Integer[] numArr5 = this.oldLayoutOffsets;
                if (numArr5 == null || numArr5.length != arrayList4.size()) {
                    this.oldLayoutOffsets = new Integer[arrayList4.size()];
                }
                arrayList4.toArray(this.oldLayoutOffsets);
                Integer[] numArr6 = this.oldLayoutToCurrentIndex;
                if (numArr6 == null || numArr6.length != arrayList8.size()) {
                    this.oldLayoutToCurrentIndex = new Integer[arrayList8.size()];
                }
                arrayList8.toArray(this.oldLayoutToCurrentIndex);
                StaticLayout[] staticLayoutArr5 = this.currentLayout;
                if (staticLayoutArr5.length > 0) {
                    this.isRTL = staticLayoutArr5[0].isRtlCharAt(0);
                } else {
                    StaticLayout[] staticLayoutArr6 = this.oldLayout;
                    if (staticLayoutArr6.length > 0) {
                        this.isRTL = staticLayoutArr6[0].isRtlCharAt(0);
                    }
                }
                this.moveDown = z3;
                this.t = 0.0f;
                ValueAnimator ofFloat = ValueAnimator.ofFloat(new float[]{0.0f, 1.0f});
                this.animator = ofFloat;
                ofFloat.addUpdateListener(new AnimatedTextView$AnimatedTextDrawable$$ExternalSyntheticLambda0(this));
                this.animator.addListener(new AnimatorListenerAdapter() {
                    public void onAnimationEnd(Animator animator) {
                        super.onAnimationEnd(animator);
                        StaticLayout[] unused = AnimatedTextDrawable.this.oldLayout = null;
                        Integer[] unused2 = AnimatedTextDrawable.this.oldLayoutOffsets = null;
                        Integer[] unused3 = AnimatedTextDrawable.this.oldLayoutToCurrentIndex = null;
                        CharSequence unused4 = AnimatedTextDrawable.this.oldText = null;
                        int unused5 = AnimatedTextDrawable.this.oldWidth = 0;
                        float unused6 = AnimatedTextDrawable.this.t = 0.0f;
                        AnimatedTextDrawable.this.invalidateSelf();
                        ValueAnimator unused7 = AnimatedTextDrawable.this.animator = null;
                        if (AnimatedTextDrawable.this.toSetText != null) {
                            AnimatedTextDrawable animatedTextDrawable = AnimatedTextDrawable.this;
                            animatedTextDrawable.setText(animatedTextDrawable.toSetText, true, AnimatedTextDrawable.this.toSetTextMoveDown);
                            CharSequence unused8 = AnimatedTextDrawable.this.toSetText = null;
                            boolean unused9 = AnimatedTextDrawable.this.toSetTextMoveDown = false;
                        } else if (AnimatedTextDrawable.this.onAnimationFinishListener != null) {
                            AnimatedTextDrawable.this.onAnimationFinishListener.run();
                        }
                    }
                });
                this.animator.setStartDelay(this.animateDelay);
                this.animator.setDuration(this.animateDuration);
                this.animator.setInterpolator(this.animateInterpolator);
                this.animator.start();
            }
        }

        /* access modifiers changed from: private */
        public /* synthetic */ void lambda$setText$0(ArrayList arrayList, ArrayList arrayList2, ArrayList arrayList3, ArrayList arrayList4, ArrayList arrayList5, ArrayList arrayList6, CharSequence charSequence, int i, int i2) {
            StaticLayout makeLayout = makeLayout(charSequence, this.bounds.width() - Math.min(this.currentWidth, this.oldWidth));
            arrayList.add(Integer.valueOf(arrayList2.size()));
            arrayList3.add(Integer.valueOf(arrayList4.size()));
            arrayList5.add(Integer.valueOf(this.currentWidth));
            arrayList2.add(makeLayout);
            arrayList6.add(Integer.valueOf(this.oldWidth));
            arrayList4.add(makeLayout);
            float lineWidth = makeLayout.getLineWidth(0);
            this.currentWidth = (int) (((float) this.currentWidth) + lineWidth);
            this.oldWidth = (int) (((float) this.oldWidth) + lineWidth);
            this.currentHeight = Math.max(this.currentHeight, makeLayout.getHeight());
            this.oldHeight = Math.max(this.oldHeight, makeLayout.getHeight());
        }

        /* access modifiers changed from: private */
        public /* synthetic */ void lambda$setText$1(ArrayList arrayList, ArrayList arrayList2, ArrayList arrayList3, CharSequence charSequence, int i, int i2) {
            StaticLayout makeLayout = makeLayout(charSequence, this.bounds.width() - this.currentWidth);
            arrayList.add(Integer.valueOf(this.currentWidth));
            arrayList2.add(makeLayout);
            arrayList3.add(-1);
            this.currentWidth = (int) (((float) this.currentWidth) + makeLayout.getLineWidth(0));
            this.currentHeight = Math.max(this.currentHeight, makeLayout.getHeight());
        }

        /* access modifiers changed from: private */
        public /* synthetic */ void lambda$setText$2(ArrayList arrayList, ArrayList arrayList2, ArrayList arrayList3, CharSequence charSequence, int i, int i2) {
            StaticLayout makeLayout = makeLayout(charSequence, this.bounds.width() - this.oldWidth);
            arrayList.add(Integer.valueOf(this.oldWidth));
            arrayList2.add(makeLayout);
            arrayList3.add(-1);
            this.oldWidth = (int) (((float) this.oldWidth) + makeLayout.getLineWidth(0));
            this.oldHeight = Math.max(this.oldHeight, makeLayout.getHeight());
        }

        /* access modifiers changed from: private */
        public /* synthetic */ void lambda$setText$3(ValueAnimator valueAnimator) {
            this.t = ((Float) valueAnimator.getAnimatedValue()).floatValue();
            invalidateSelf();
        }

        public CharSequence getText() {
            return this.currentText;
        }

        public int getWidth() {
            return Math.max(this.currentWidth, this.oldWidth);
        }

        private StaticLayout makeLayout(CharSequence charSequence, int i) {
            if (i <= 0) {
                Point point = AndroidUtilities.displaySize;
                i = Math.min(point.x, point.y);
            }
            int i2 = i;
            if (Build.VERSION.SDK_INT >= 23) {
                return StaticLayout.Builder.obtain(charSequence, 0, charSequence.length(), this.textPaint, i2).setMaxLines(1).setLineSpacing(0.0f, 1.0f).setAlignment(Layout.Alignment.ALIGN_NORMAL).setEllipsize(TextUtils.TruncateAt.END).setEllipsizedWidth(i2).build();
            }
            return new StaticLayout(charSequence, 0, charSequence.length(), this.textPaint, i2, Layout.Alignment.ALIGN_NORMAL, 1.0f, 0.0f, false, TextUtils.TruncateAt.END, i2);
        }

        private static class WordSequence implements CharSequence {
            private final int length;
            private CharSequence[] words;

            public WordSequence(CharSequence charSequence) {
                if (charSequence == null) {
                    this.words = new CharSequence[0];
                    this.length = 0;
                    return;
                }
                this.length = charSequence.length();
                int i = 0;
                for (int i2 = 0; i2 < this.length; i2++) {
                    if (charSequence.charAt(i2) == ' ') {
                        i++;
                    }
                }
                this.words = new CharSequence[(i + 1)];
                int i3 = 0;
                int i4 = 0;
                int i5 = 0;
                while (true) {
                    int i6 = this.length;
                    if (i3 <= i6) {
                        if (i3 == i6 || charSequence.charAt(i3) == ' ') {
                            int i7 = i4 + 1;
                            this.words[i4] = charSequence.subSequence(i5, (i3 < this.length ? 1 : 0) + i3);
                            i5 = i3 + 1;
                            i4 = i7;
                        }
                        i3++;
                    } else {
                        return;
                    }
                }
            }

            public CharSequence wordAt(int i) {
                if (i < 0) {
                    return null;
                }
                CharSequence[] charSequenceArr = this.words;
                if (i >= charSequenceArr.length) {
                    return null;
                }
                return charSequenceArr[i];
            }

            public int length() {
                return this.words.length;
            }

            public char charAt(int i) {
                int i2 = 0;
                while (true) {
                    CharSequence[] charSequenceArr = this.words;
                    if (i2 >= charSequenceArr.length) {
                        return 0;
                    }
                    if (i < charSequenceArr[i2].length()) {
                        return this.words[i2].charAt(i);
                    }
                    i -= this.words[i2].length();
                    i2++;
                }
            }

            public CharSequence subSequence(int i, int i2) {
                return TextUtils.concat((CharSequence[]) Arrays.copyOfRange(this.words, i, i2));
            }

            public String toString() {
                StringBuilder sb = new StringBuilder();
                int i = 0;
                while (true) {
                    CharSequence[] charSequenceArr = this.words;
                    if (i >= charSequenceArr.length) {
                        return sb.toString();
                    }
                    sb.append(charSequenceArr[i]);
                    i++;
                }
            }

            public CharSequence toCharSequence() {
                return TextUtils.concat(this.words);
            }

            public IntStream chars() {
                if (Build.VERSION.SDK_INT >= 24) {
                    return C$r8$wrapper$java$util$stream$IntStream$VWRP.convert(toCharSequence().chars());
                }
                return null;
            }

            public IntStream codePoints() {
                if (Build.VERSION.SDK_INT >= 24) {
                    return C$r8$wrapper$java$util$stream$IntStream$VWRP.convert(toCharSequence().codePoints());
                }
                return null;
            }
        }

        public static boolean partEquals(CharSequence charSequence, CharSequence charSequence2, int i, int i2) {
            if ((charSequence instanceof WordSequence) && (charSequence2 instanceof WordSequence)) {
                CharSequence wordAt = ((WordSequence) charSequence).wordAt(i);
                CharSequence wordAt2 = ((WordSequence) charSequence2).wordAt(i2);
                if (wordAt == null && wordAt2 == null) {
                    return true;
                }
                if (wordAt == null || !wordAt.equals(wordAt2)) {
                    return false;
                }
                return true;
            } else if (charSequence == null && charSequence2 == null) {
                return true;
            } else {
                if (charSequence == null || charSequence2 == null || charSequence.charAt(i) != charSequence2.charAt(i2)) {
                    return false;
                }
                return true;
            }
        }

        private void diff(CharSequence charSequence, CharSequence charSequence2, RegionCallback regionCallback, RegionCallback regionCallback2, RegionCallback regionCallback3) {
            CharSequence charSequence3 = charSequence;
            CharSequence charSequence4 = charSequence2;
            RegionCallback regionCallback4 = regionCallback;
            RegionCallback regionCallback5 = regionCallback2;
            RegionCallback regionCallback6 = regionCallback3;
            if (this.preserveIndex) {
                int min = Math.min(charSequence2.length(), charSequence.length());
                if (this.startFromEnd) {
                    ArrayList arrayList = new ArrayList();
                    boolean z = true;
                    int i = 0;
                    boolean z2 = true;
                    for (int i2 = 0; i2 <= min; i2++) {
                        int length = (charSequence2.length() - i2) - 1;
                        int length2 = (charSequence.length() - i2) - 1;
                        boolean z3 = length >= 0 && length2 >= 0 && partEquals(charSequence4, charSequence3, length, length2);
                        if (z != z3 || i2 == min) {
                            int i3 = i2 - i;
                            if (i3 > 0) {
                                if (arrayList.size() != 0) {
                                    z = z2;
                                }
                                arrayList.add(Integer.valueOf(i3));
                                z2 = z;
                            }
                            i = i2;
                            z = z3;
                        }
                    }
                    int length3 = charSequence2.length() - min;
                    int length4 = charSequence.length() - min;
                    if (length3 > 0) {
                        regionCallback5.run(charSequence4.subSequence(0, length3), 0, length3);
                    }
                    if (length4 > 0) {
                        regionCallback6.run(charSequence3.subSequence(0, length4), 0, length4);
                    }
                    for (int size = arrayList.size() - 1; size >= 0; size--) {
                        int intValue = ((Integer) arrayList.get(size)).intValue();
                        if (size % 2 != 0 ? z2 : !z2) {
                            int i4 = length3 + intValue;
                            regionCallback5.run(charSequence4.subSequence(length3, i4), length3, i4);
                            int i5 = length4 + intValue;
                            regionCallback6.run(charSequence3.subSequence(length4, i5), length4, i5);
                        } else if (charSequence2.length() > charSequence.length()) {
                            int i6 = length3 + intValue;
                            regionCallback4.run(charSequence4.subSequence(length3, i6), length3, i6);
                        } else {
                            int i7 = length4 + intValue;
                            regionCallback4.run(charSequence3.subSequence(length4, i7), length4, i7);
                        }
                        length3 += intValue;
                        length4 += intValue;
                    }
                    return;
                }
                int i8 = 0;
                boolean z4 = true;
                int i9 = 0;
                while (i8 <= min) {
                    boolean z5 = i8 < min && partEquals(charSequence4, charSequence3, i8, i8);
                    if (z4 != z5 || i8 == min) {
                        if (i8 - i9 > 0) {
                            if (z4) {
                                regionCallback4.run(charSequence4.subSequence(i9, i8), i9, i8);
                            } else {
                                regionCallback5.run(charSequence4.subSequence(i9, i8), i9, i8);
                                regionCallback6.run(charSequence3.subSequence(i9, i8), i9, i8);
                            }
                        }
                        i9 = i8;
                        z4 = z5;
                    }
                    i8++;
                }
                if (charSequence2.length() - min > 0) {
                    regionCallback5.run(charSequence4.subSequence(min, charSequence2.length()), min, charSequence2.length());
                }
                if (charSequence.length() - min > 0) {
                    regionCallback6.run(charSequence3.subSequence(min, charSequence.length()), min, charSequence.length());
                    return;
                }
                return;
            }
            int min2 = Math.min(charSequence2.length(), charSequence.length());
            int i10 = 0;
            int i11 = 0;
            boolean z6 = true;
            int i12 = 0;
            int i13 = 0;
            while (i10 <= min2) {
                boolean z7 = i10 < min2 && partEquals(charSequence4, charSequence3, i10, i11);
                if (z6 != z7 || i10 == min2) {
                    if (i10 == min2) {
                        i10 = charSequence2.length();
                        i11 = charSequence.length();
                    }
                    int i14 = i10 - i12;
                    int i15 = i11 - i13;
                    if (i14 > 0 || i15 > 0) {
                        if (i14 != i15 || !z6) {
                            if (i14 > 0) {
                                regionCallback5.run(charSequence4.subSequence(i12, i10), i12, i10);
                            }
                            if (i15 > 0) {
                                regionCallback6.run(charSequence3.subSequence(i13, i11), i13, i11);
                            }
                        } else {
                            regionCallback4.run(charSequence4.subSequence(i12, i10), i12, i10);
                        }
                    }
                    i12 = i10;
                    i13 = i11;
                    z6 = z7;
                }
                if (z7) {
                    i11++;
                }
                i10++;
            }
        }

        public void setTextSize(float f) {
            this.textPaint.setTextSize(f);
        }

        public void setTextColor(int i) {
            this.textPaint.setColor(i);
        }

        public void setTypeface(Typeface typeface) {
            this.textPaint.setTypeface(typeface);
        }

        public void setGravity(int i) {
            this.gravity = i;
        }

        public void setAnimationProperties(float f, long j, long j2, TimeInterpolator timeInterpolator) {
            this.moveAmplitude = f;
            this.animateDelay = j;
            this.animateDuration = j2;
            this.animateInterpolator = timeInterpolator;
        }

        public TextPaint getPaint() {
            return this.textPaint;
        }

        public void setAlpha(int i) {
            this.alpha = i;
        }

        public void setColorFilter(ColorFilter colorFilter) {
            this.textPaint.setColorFilter(colorFilter);
        }

        public void setBounds(Rect rect) {
            super.setBounds(rect);
            this.bounds.set(rect);
        }

        public void setBounds(int i, int i2, int i3, int i4) {
            super.setBounds(i, i2, i3, i4);
            this.bounds.set(i, i2, i3, i4);
        }
    }

    public AnimatedTextView(Context context) {
        super(context);
        AnimatedTextDrawable animatedTextDrawable = new AnimatedTextDrawable();
        this.drawable = animatedTextDrawable;
        animatedTextDrawable.setCallback(this);
    }

    public AnimatedTextView(Context context, boolean z, boolean z2, boolean z3) {
        super(context);
        AnimatedTextDrawable animatedTextDrawable = new AnimatedTextDrawable(z, z2, z3);
        this.drawable = animatedTextDrawable;
        animatedTextDrawable.setCallback(this);
        this.drawable.setOnAnimationFinishListener(new AnimatedTextView$$ExternalSyntheticLambda0(this));
    }

    /* access modifiers changed from: private */
    public /* synthetic */ void lambda$new$0() {
        CharSequence charSequence = this.toSetText;
        if (charSequence != null) {
            setText(charSequence, this.toSetMoveDown, true);
            this.toSetText = null;
            this.toSetMoveDown = false;
        }
    }

    /* access modifiers changed from: protected */
    public void onMeasure(int i, int i2) {
        int size = View.MeasureSpec.getSize(i);
        int size2 = View.MeasureSpec.getSize(i2);
        if (this.lastMaxWidth != size) {
            this.drawable.setBounds(getPaddingLeft(), getPaddingTop(), size - getPaddingRight(), size2 - getPaddingBottom());
            setText(this.drawable.getText(), false);
        }
        this.lastMaxWidth = size;
        if (View.MeasureSpec.getMode(i) == Integer.MIN_VALUE) {
            size = getPaddingRight() + getPaddingLeft() + this.drawable.getWidth();
        }
        setMeasuredDimension(size, size2);
    }

    /* access modifiers changed from: protected */
    public void onDraw(Canvas canvas) {
        this.drawable.setBounds(getPaddingLeft(), getPaddingTop(), getMeasuredWidth() - getPaddingRight(), getMeasuredHeight() - getPaddingBottom());
        this.drawable.draw(canvas);
    }

    public void setText(CharSequence charSequence) {
        setText(charSequence, true, true);
    }

    public void setText(CharSequence charSequence, boolean z) {
        setText(charSequence, z, true);
    }

    public void cancelAnimation() {
        this.drawable.cancelAnimation();
    }

    public boolean isAnimating() {
        return this.drawable.isAnimating();
    }

    public void setText(CharSequence charSequence, boolean z, boolean z2) {
        boolean z3 = !this.first && z;
        this.first = false;
        if (!z3 || !this.drawable.isAnimating()) {
            int width = this.drawable.getWidth();
            this.drawable.setBounds(getPaddingLeft(), getPaddingTop(), this.lastMaxWidth - getPaddingRight(), getMeasuredHeight() - getPaddingBottom());
            this.drawable.setText(charSequence, z3, z2);
            if (width < this.drawable.getWidth() || (!z3 && width != this.drawable.getWidth())) {
                requestLayout();
                return;
            }
            return;
        }
        this.toSetText = charSequence;
        this.toSetMoveDown = z2;
    }

    public CharSequence getText() {
        return this.drawable.getText();
    }

    public void setTextSize(float f) {
        this.drawable.setTextSize(f);
    }

    public void setTextColor(int i) {
        this.drawable.setTextColor(i);
    }

    public void setTypeface(Typeface typeface) {
        this.drawable.setTypeface(typeface);
    }

    public void setGravity(int i) {
        this.drawable.setGravity(i);
    }

    public void setAnimationProperties(float f, long j, long j2, TimeInterpolator timeInterpolator) {
        this.drawable.setAnimationProperties(f, j, j2, timeInterpolator);
    }

    public AnimatedTextDrawable getDrawable() {
        return this.drawable;
    }

    public TextPaint getPaint() {
        return this.drawable.getPaint();
    }

    public void invalidateDrawable(Drawable drawable2) {
        super.invalidateDrawable(drawable2);
        invalidate();
    }

    public void onInitializeAccessibilityNodeInfo(AccessibilityNodeInfo accessibilityNodeInfo) {
        super.onInitializeAccessibilityNodeInfo(accessibilityNodeInfo);
        accessibilityNodeInfo.setClassName("android.widget.TextView");
        accessibilityNodeInfo.setText(getText());
    }
}
