package org.telegram.ui.Components;

import android.animation.TimeInterpolator;
import android.animation.ValueAnimator;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.ColorFilter;
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

        public AnimatedTextDrawable() {
            this(false, false, false);
        }

        public AnimatedTextDrawable(boolean splitByWords2, boolean preserveIndex2, boolean startFromEnd2) {
            this.textPaint = new TextPaint();
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
            this.splitByWords = splitByWords2;
            this.preserveIndex = preserveIndex2;
            this.startFromEnd = startFromEnd2;
        }

        public void setOnAnimationFinishListener(Runnable listener) {
            this.onAnimationFinishListener = listener;
        }

        public void draw(Canvas canvas) {
            canvas.save();
            canvas.translate((float) this.bounds.left, (float) this.bounds.top);
            int fullWidth = this.bounds.width();
            int fullHeight = this.bounds.height();
            if (this.currentLayout == null || this.oldLayout == null) {
                canvas.translate(0.0f, ((float) (fullHeight - this.currentHeight)) / 2.0f);
                if (this.currentLayout != null) {
                    for (int i = 0; i < this.currentLayout.length; i++) {
                        this.textPaint.setAlpha(this.alpha);
                        canvas.save();
                        float x = (float) this.currentLayoutOffsets[i].intValue();
                        if (this.isRTL) {
                            x = ((((float) this.currentWidth) - x) - ((float) this.currentLayout[i].getWidth())) - ((float) (fullWidth - this.currentWidth));
                        }
                        int i2 = this.gravity;
                        if ((i2 & 5) > 0) {
                            x += (float) (fullWidth - this.currentWidth);
                        } else if ((i2 & 1) > 0) {
                            x += ((float) (fullWidth - this.currentWidth)) / 2.0f;
                        }
                        canvas.translate(x, 0.0f);
                        this.currentLayout[i].draw(canvas);
                        canvas.restore();
                    }
                }
            } else {
                int width = AndroidUtilities.lerp(this.oldWidth, this.currentWidth, this.t);
                canvas.translate(0.0f, ((float) (fullHeight - AndroidUtilities.lerp(this.oldHeight, this.currentHeight, this.t))) / 2.0f);
                int i3 = 0;
                while (true) {
                    float f = -1.0f;
                    if (i3 >= this.currentLayout.length) {
                        break;
                    }
                    int j = this.currentLayoutToOldIndex[i3].intValue();
                    float x2 = (float) this.currentLayoutOffsets[i3].intValue();
                    float y = 0.0f;
                    if (j >= 0) {
                        x2 = AndroidUtilities.lerp((float) this.oldLayoutOffsets[j].intValue(), x2, this.t);
                        this.textPaint.setAlpha(this.alpha);
                    } else {
                        float f2 = (-this.textPaint.getTextSize()) * this.moveAmplitude;
                        float f3 = this.t;
                        float f4 = f2 * (1.0f - f3);
                        if (this.moveDown) {
                            f = 1.0f;
                        }
                        y = f4 * f;
                        this.textPaint.setAlpha((int) (((float) this.alpha) * f3));
                    }
                    canvas.save();
                    int lwidth = j >= 0 ? width : this.currentWidth;
                    if (this.isRTL) {
                        x2 = ((((float) lwidth) - x2) - ((float) this.currentLayout[i3].getWidth())) - ((float) (fullWidth - lwidth));
                    }
                    int i4 = this.gravity;
                    if ((i4 & 5) > 0) {
                        x2 += (float) (fullWidth - lwidth);
                    } else if ((i4 & 1) > 0) {
                        x2 += ((float) (fullWidth - lwidth)) / 2.0f;
                    }
                    canvas.translate(x2, y);
                    this.currentLayout[i3].draw(canvas);
                    canvas.restore();
                    i3++;
                }
                for (int i5 = 0; i5 < this.oldLayout.length; i5++) {
                    if (this.oldLayoutToCurrentIndex[i5].intValue() < 0) {
                        float x3 = (float) this.oldLayoutOffsets[i5].intValue();
                        float textSize = this.textPaint.getTextSize() * this.moveAmplitude;
                        float f5 = this.t;
                        float y2 = textSize * f5 * (this.moveDown ? 1.0f : -1.0f);
                        this.textPaint.setAlpha((int) (((float) this.alpha) * (1.0f - f5)));
                        canvas.save();
                        if (this.isRTL) {
                            x3 = ((((float) this.oldWidth) - x3) - ((float) this.oldLayout[i5].getWidth())) - ((float) (fullWidth - this.oldWidth));
                        }
                        int i6 = this.gravity;
                        if ((i6 & 5) > 0) {
                            x3 += (float) (fullWidth - this.oldWidth);
                        } else if ((i6 & 1) > 0) {
                            x3 += ((float) (fullWidth - this.oldWidth)) / 2.0f;
                        }
                        canvas.translate(x3, y2);
                        this.oldLayout[i5].draw(canvas);
                        canvas.restore();
                    }
                }
            }
            canvas.restore();
        }

        public boolean isAnimating() {
            ValueAnimator valueAnimator = this.animator;
            return valueAnimator != null && valueAnimator.isRunning();
        }

        public void setText(CharSequence text) {
            setText(text, true);
        }

        public void setText(CharSequence text, boolean animated) {
            setText(text, animated, true);
        }

        /* JADX WARNING: Code restructure failed: missing block: B:28:0x00bb, code lost:
            if (r0.length != r13.size()) goto L_0x00c0;
         */
        /* Code decompiled incorrectly, please refer to instructions dump. */
        public void setText(java.lang.CharSequence r19, boolean r20, boolean r21) {
            /*
                r18 = this;
                r8 = r18
                r9 = r21
                java.lang.CharSequence r0 = r8.currentText
                if (r0 == 0) goto L_0x000e
                if (r19 != 0) goto L_0x000b
                goto L_0x000e
            L_0x000b:
                r10 = r20
                goto L_0x0010
            L_0x000e:
                r0 = 0
                r10 = r0
            L_0x0010:
                if (r19 != 0) goto L_0x0016
                java.lang.String r0 = ""
                r11 = r0
                goto L_0x0018
            L_0x0016:
                r11 = r19
            L_0x0018:
                r0 = 0
                r13 = 0
                if (r10 == 0) goto L_0x01a3
                boolean r1 = r18.isAnimating()
                if (r1 == 0) goto L_0x0027
                r8.toSetText = r11
                r8.toSetTextMoveDown = r9
                return
            L_0x0027:
                java.lang.CharSequence r1 = r8.currentText
                boolean r1 = r11.equals(r1)
                if (r1 == 0) goto L_0x0030
                return
            L_0x0030:
                java.lang.CharSequence r1 = r8.currentText
                r8.oldText = r1
                r8.currentText = r11
                r8.currentLayout = r0
                r8.oldLayout = r0
                java.util.ArrayList r0 = new java.util.ArrayList
                r0.<init>()
                r14 = r0
                java.util.ArrayList r0 = new java.util.ArrayList
                r0.<init>()
                r15 = r0
                java.util.ArrayList r0 = new java.util.ArrayList
                r0.<init>()
                r7 = r0
                java.util.ArrayList r0 = new java.util.ArrayList
                r0.<init>()
                r6 = r0
                java.util.ArrayList r0 = new java.util.ArrayList
                r0.<init>()
                r5 = r0
                java.util.ArrayList r0 = new java.util.ArrayList
                r0.<init>()
                r4 = r0
                r8.currentHeight = r13
                r8.currentWidth = r13
                r8.oldHeight = r13
                r8.oldWidth = r13
                org.telegram.ui.Components.AnimatedTextView$AnimatedTextDrawable$$ExternalSyntheticLambda3 r16 = new org.telegram.ui.Components.AnimatedTextView$AnimatedTextDrawable$$ExternalSyntheticLambda3
                r0 = r16
                r1 = r18
                r2 = r5
                r3 = r7
                r19 = r4
                r4 = r15
                r12 = r5
                r5 = r19
                r17 = r6
                r6 = r14
                r13 = r7
                r7 = r17
                r0.<init>(r1, r2, r3, r4, r5, r6, r7)
                r3 = r16
                org.telegram.ui.Components.AnimatedTextView$AnimatedTextDrawable$$ExternalSyntheticLambda1 r4 = new org.telegram.ui.Components.AnimatedTextView$AnimatedTextDrawable$$ExternalSyntheticLambda1
                r4.<init>(r8, r14, r13, r15)
                org.telegram.ui.Components.AnimatedTextView$AnimatedTextDrawable$$ExternalSyntheticLambda2 r5 = new org.telegram.ui.Components.AnimatedTextView$AnimatedTextDrawable$$ExternalSyntheticLambda2
                r7 = r19
                r6 = r17
                r5.<init>(r8, r6, r7, r12)
                boolean r0 = r8.splitByWords
                if (r0 == 0) goto L_0x0099
                org.telegram.ui.Components.AnimatedTextView$AnimatedTextDrawable$WordSequence r0 = new org.telegram.ui.Components.AnimatedTextView$AnimatedTextDrawable$WordSequence
                java.lang.CharSequence r1 = r8.oldText
                r0.<init>((java.lang.CharSequence) r1)
                goto L_0x009b
            L_0x0099:
                java.lang.CharSequence r0 = r8.oldText
            L_0x009b:
                r1 = r0
                boolean r0 = r8.splitByWords
                if (r0 == 0) goto L_0x00a8
                org.telegram.ui.Components.AnimatedTextView$AnimatedTextDrawable$WordSequence r0 = new org.telegram.ui.Components.AnimatedTextView$AnimatedTextDrawable$WordSequence
                java.lang.CharSequence r2 = r8.currentText
                r0.<init>((java.lang.CharSequence) r2)
                goto L_0x00aa
            L_0x00a8:
                java.lang.CharSequence r0 = r8.currentText
            L_0x00aa:
                r2 = r0
                r0 = r18
                r0.diff(r1, r2, r3, r4, r5)
                android.text.StaticLayout[] r0 = r8.currentLayout
                if (r0 == 0) goto L_0x00be
                int r0 = r0.length
                r19 = r1
                int r1 = r13.size()
                if (r0 == r1) goto L_0x00c8
                goto L_0x00c0
            L_0x00be:
                r19 = r1
            L_0x00c0:
                int r0 = r13.size()
                android.text.StaticLayout[] r0 = new android.text.StaticLayout[r0]
                r8.currentLayout = r0
            L_0x00c8:
                android.text.StaticLayout[] r0 = r8.currentLayout
                r13.toArray(r0)
                java.lang.Integer[] r0 = r8.currentLayoutOffsets
                if (r0 == 0) goto L_0x00d8
                int r0 = r0.length
                int r1 = r14.size()
                if (r0 == r1) goto L_0x00e0
            L_0x00d8:
                int r0 = r14.size()
                java.lang.Integer[] r0 = new java.lang.Integer[r0]
                r8.currentLayoutOffsets = r0
            L_0x00e0:
                java.lang.Integer[] r0 = r8.currentLayoutOffsets
                r14.toArray(r0)
                java.lang.Integer[] r0 = r8.currentLayoutToOldIndex
                if (r0 == 0) goto L_0x00f0
                int r0 = r0.length
                int r1 = r15.size()
                if (r0 == r1) goto L_0x00f8
            L_0x00f0:
                int r0 = r15.size()
                java.lang.Integer[] r0 = new java.lang.Integer[r0]
                r8.currentLayoutToOldIndex = r0
            L_0x00f8:
                java.lang.Integer[] r0 = r8.currentLayoutToOldIndex
                r15.toArray(r0)
                android.text.StaticLayout[] r0 = r8.oldLayout
                if (r0 == 0) goto L_0x0108
                int r0 = r0.length
                int r1 = r7.size()
                if (r0 == r1) goto L_0x0110
            L_0x0108:
                int r0 = r7.size()
                android.text.StaticLayout[] r0 = new android.text.StaticLayout[r0]
                r8.oldLayout = r0
            L_0x0110:
                android.text.StaticLayout[] r0 = r8.oldLayout
                r7.toArray(r0)
                java.lang.Integer[] r0 = r8.oldLayoutOffsets
                if (r0 == 0) goto L_0x0120
                int r0 = r0.length
                int r1 = r6.size()
                if (r0 == r1) goto L_0x0128
            L_0x0120:
                int r0 = r6.size()
                java.lang.Integer[] r0 = new java.lang.Integer[r0]
                r8.oldLayoutOffsets = r0
            L_0x0128:
                java.lang.Integer[] r0 = r8.oldLayoutOffsets
                r6.toArray(r0)
                java.lang.Integer[] r0 = r8.oldLayoutToCurrentIndex
                if (r0 == 0) goto L_0x0138
                int r0 = r0.length
                int r1 = r12.size()
                if (r0 == r1) goto L_0x0140
            L_0x0138:
                int r0 = r12.size()
                java.lang.Integer[] r0 = new java.lang.Integer[r0]
                r8.oldLayoutToCurrentIndex = r0
            L_0x0140:
                java.lang.Integer[] r0 = r8.oldLayoutToCurrentIndex
                r12.toArray(r0)
                android.text.StaticLayout[] r0 = r8.currentLayout
                int r1 = r0.length
                if (r1 <= 0) goto L_0x0154
                r1 = 0
                r0 = r0[r1]
                boolean r0 = r0.isRtlCharAt(r1)
                r8.isRTL = r0
                goto L_0x0163
            L_0x0154:
                r1 = 0
                android.text.StaticLayout[] r0 = r8.oldLayout
                int r1 = r0.length
                if (r1 <= 0) goto L_0x0163
                r1 = 0
                r0 = r0[r1]
                boolean r0 = r0.isRtlCharAt(r1)
                r8.isRTL = r0
            L_0x0163:
                r8.moveDown = r9
                r0 = 2
                float[] r0 = new float[r0]
                r0 = {0, NUM} // fill-array
                r1 = 0
                r8.t = r1
                android.animation.ValueAnimator r0 = android.animation.ValueAnimator.ofFloat(r0)
                r8.animator = r0
                org.telegram.ui.Components.AnimatedTextView$AnimatedTextDrawable$$ExternalSyntheticLambda0 r1 = new org.telegram.ui.Components.AnimatedTextView$AnimatedTextDrawable$$ExternalSyntheticLambda0
                r1.<init>(r8)
                r0.addUpdateListener(r1)
                android.animation.ValueAnimator r0 = r8.animator
                org.telegram.ui.Components.AnimatedTextView$AnimatedTextDrawable$1 r1 = new org.telegram.ui.Components.AnimatedTextView$AnimatedTextDrawable$1
                r1.<init>()
                r0.addListener(r1)
                android.animation.ValueAnimator r0 = r8.animator
                r20 = r2
                long r1 = r8.animateDelay
                r0.setStartDelay(r1)
                android.animation.ValueAnimator r0 = r8.animator
                long r1 = r8.animateDuration
                r0.setDuration(r1)
                android.animation.ValueAnimator r0 = r8.animator
                android.animation.TimeInterpolator r1 = r8.animateInterpolator
                r0.setInterpolator(r1)
                android.animation.ValueAnimator r0 = r8.animator
                r0.start()
                goto L_0x020f
            L_0x01a3:
                android.animation.ValueAnimator r1 = r8.animator
                if (r1 == 0) goto L_0x01aa
                r1.cancel()
            L_0x01aa:
                r8.animator = r0
                r8.toSetText = r0
                r1 = 0
                r8.toSetTextMoveDown = r1
                r1 = 0
                r8.t = r1
                r1 = 1
                android.text.StaticLayout[] r2 = new android.text.StaticLayout[r1]
                r8.currentLayout = r2
                r8.currentText = r11
                android.graphics.Rect r3 = r8.bounds
                int r3 = r3.width()
                android.text.StaticLayout r3 = r8.makeLayout(r11, r3)
                r4 = 0
                r2[r4] = r3
                android.text.StaticLayout[] r2 = r8.currentLayout
                r2 = r2[r4]
                float r2 = r2.getLineWidth(r4)
                int r2 = (int) r2
                r8.currentWidth = r2
                android.text.StaticLayout[] r2 = r8.currentLayout
                r2 = r2[r4]
                int r2 = r2.getHeight()
                r8.currentHeight = r2
                java.lang.Integer[] r2 = new java.lang.Integer[r1]
                r8.currentLayoutOffsets = r2
                java.lang.Integer r3 = java.lang.Integer.valueOf(r4)
                r2[r4] = r3
                java.lang.Integer[] r1 = new java.lang.Integer[r1]
                r8.currentLayoutToOldIndex = r1
                r2 = -1
                java.lang.Integer r2 = java.lang.Integer.valueOf(r2)
                r1[r4] = r2
                android.text.StaticLayout[] r1 = r8.currentLayout
                int r2 = r1.length
                if (r2 <= 0) goto L_0x01ff
                r1 = r1[r4]
                boolean r1 = r1.isRtlCharAt(r4)
                r8.isRTL = r1
            L_0x01ff:
                r8.oldLayout = r0
                r8.oldLayoutOffsets = r0
                r8.oldLayoutToCurrentIndex = r0
                r8.oldText = r0
                r0 = 0
                r8.oldWidth = r0
                r8.oldHeight = r0
                r18.invalidateSelf()
            L_0x020f:
                return
            */
            throw new UnsupportedOperationException("Method not decompiled: org.telegram.ui.Components.AnimatedTextView.AnimatedTextDrawable.setText(java.lang.CharSequence, boolean, boolean):void");
        }

        /* renamed from: lambda$setText$0$org-telegram-ui-Components-AnimatedTextView$AnimatedTextDrawable  reason: not valid java name */
        public /* synthetic */ void m534x604cea82(ArrayList oldLayoutToCurrentIndex2, ArrayList currentLayoutList, ArrayList currentLayoutToOldIndex2, ArrayList oldLayoutList, ArrayList currentLayoutOffsets2, ArrayList oldLayoutOffsets2, CharSequence part, int from, int to) {
            StaticLayout layout = makeLayout(part, this.bounds.width() - Math.min(this.currentWidth, this.oldWidth));
            oldLayoutToCurrentIndex2.add(Integer.valueOf(currentLayoutList.size()));
            currentLayoutToOldIndex2.add(Integer.valueOf(oldLayoutList.size()));
            currentLayoutOffsets2.add(Integer.valueOf(this.currentWidth));
            currentLayoutList.add(layout);
            oldLayoutOffsets2.add(Integer.valueOf(this.oldWidth));
            oldLayoutList.add(layout);
            float partWidth = layout.getLineWidth(0);
            this.currentWidth = (int) (((float) this.currentWidth) + partWidth);
            this.oldWidth = (int) (((float) this.oldWidth) + partWidth);
            this.currentHeight = Math.max(this.currentHeight, layout.getHeight());
            this.oldHeight = Math.max(this.oldHeight, layout.getHeight());
        }

        /* renamed from: lambda$setText$1$org-telegram-ui-Components-AnimatedTextView$AnimatedTextDrawable  reason: not valid java name */
        public /* synthetic */ void m535xca7CLASSNAMEa1(ArrayList currentLayoutOffsets2, ArrayList currentLayoutList, ArrayList currentLayoutToOldIndex2, CharSequence part, int from, int to) {
            StaticLayout layout = makeLayout(part, this.bounds.width() - this.currentWidth);
            currentLayoutOffsets2.add(Integer.valueOf(this.currentWidth));
            currentLayoutList.add(layout);
            currentLayoutToOldIndex2.add(-1);
            this.currentWidth = (int) (((float) this.currentWidth) + layout.getLineWidth(0));
            this.currentHeight = Math.max(this.currentHeight, layout.getHeight());
        }

        /* renamed from: lambda$setText$2$org-telegram-ui-Components-AnimatedTextView$AnimatedTextDrawable  reason: not valid java name */
        public /* synthetic */ void m536x34abfac0(ArrayList oldLayoutOffsets2, ArrayList oldLayoutList, ArrayList oldLayoutToCurrentIndex2, CharSequence part, int from, int to) {
            StaticLayout layout = makeLayout(part, this.bounds.width() - this.oldWidth);
            oldLayoutOffsets2.add(Integer.valueOf(this.oldWidth));
            oldLayoutList.add(layout);
            oldLayoutToCurrentIndex2.add(-1);
            this.oldWidth = (int) (((float) this.oldWidth) + layout.getLineWidth(0));
            this.oldHeight = Math.max(this.oldHeight, layout.getHeight());
        }

        /* renamed from: lambda$setText$3$org-telegram-ui-Components-AnimatedTextView$AnimatedTextDrawable  reason: not valid java name */
        public /* synthetic */ void m537x9edb82df(ValueAnimator anm) {
            this.t = ((Float) anm.getAnimatedValue()).floatValue();
            invalidateSelf();
        }

        public CharSequence getText() {
            return this.currentText;
        }

        public int getWidth() {
            return Math.max(this.currentWidth, this.oldWidth);
        }

        public int getCurrentWidth() {
            if (this.currentLayout == null || this.oldLayout == null) {
                return this.currentWidth;
            }
            return AndroidUtilities.lerp(this.oldWidth, this.currentWidth, this.t);
        }

        private StaticLayout makeLayout(CharSequence textPart, int width) {
            if (width <= 0) {
                width = Math.min(AndroidUtilities.displaySize.x, AndroidUtilities.displaySize.y);
            }
            if (Build.VERSION.SDK_INT >= 23) {
                return StaticLayout.Builder.obtain(textPart, 0, textPart.length(), this.textPaint, width).setMaxLines(1).setLineSpacing(0.0f, 1.0f).setAlignment(Layout.Alignment.ALIGN_NORMAL).setEllipsize(TextUtils.TruncateAt.END).setEllipsizedWidth(width).build();
            }
            return new StaticLayout(textPart, 0, textPart.length(), this.textPaint, width, Layout.Alignment.ALIGN_NORMAL, 1.0f, 0.0f, false, TextUtils.TruncateAt.END, width);
        }

        private static class WordSequence implements CharSequence {
            private static final char SPACE = ' ';
            private final int length;
            private CharSequence[] words;

            public WordSequence(CharSequence text) {
                if (text == null) {
                    this.words = new CharSequence[0];
                    this.length = 0;
                    return;
                }
                this.length = text.length();
                int spacesCount = 0;
                for (int i = 0; i < this.length; i++) {
                    if (text.charAt(i) == ' ') {
                        spacesCount++;
                    }
                }
                int j = 0;
                this.words = new CharSequence[(spacesCount + 1)];
                int start = 0;
                int i2 = 0;
                while (true) {
                    int i3 = this.length;
                    if (i2 <= i3) {
                        if (i2 == i3 || text.charAt(i2) == ' ') {
                            int j2 = j + 1;
                            this.words[j] = text.subSequence(start, (i2 < this.length ? 1 : 0) + i2);
                            start = i2 + 1;
                            j = j2;
                        }
                        i2++;
                    } else {
                        return;
                    }
                }
            }

            public WordSequence(CharSequence[] words2) {
                if (words2 == null) {
                    this.words = new CharSequence[0];
                    this.length = 0;
                    return;
                }
                this.words = words2;
                int length2 = 0;
                int i = 0;
                while (true) {
                    CharSequence[] charSequenceArr = this.words;
                    if (i < charSequenceArr.length) {
                        if (charSequenceArr[i] != null) {
                            length2 += charSequenceArr[i].length();
                        }
                        i++;
                    } else {
                        this.length = length2;
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
                int j = 0;
                while (true) {
                    CharSequence[] charSequenceArr = this.words;
                    if (j >= charSequenceArr.length) {
                        return 0;
                    }
                    if (i < charSequenceArr[j].length()) {
                        return this.words[j].charAt(i);
                    }
                    i -= this.words[j].length();
                    j++;
                }
            }

            public CharSequence subSequence(int from, int to) {
                return TextUtils.concat((CharSequence[]) Arrays.copyOfRange(this.words, from, to));
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

        public static boolean partEquals(CharSequence a, CharSequence b, int aIndex, int bIndex) {
            if ((a instanceof WordSequence) && (b instanceof WordSequence)) {
                CharSequence wordA = ((WordSequence) a).wordAt(aIndex);
                CharSequence wordB = ((WordSequence) b).wordAt(bIndex);
                if (wordA == null && wordB == null) {
                    return true;
                }
                if (wordA == null || !wordA.equals(wordB)) {
                    return false;
                }
                return true;
            } else if (a == null && b == null) {
                return true;
            } else {
                if (a == null || b == null || a.charAt(aIndex) != b.charAt(bIndex)) {
                    return false;
                }
                return true;
            }
        }

        private void diff(CharSequence oldText2, CharSequence newText, RegionCallback onEqualPart, RegionCallback onNewPart, RegionCallback onOldPart) {
            int i;
            CharSequence charSequence = oldText2;
            CharSequence charSequence2 = newText;
            RegionCallback regionCallback = onEqualPart;
            RegionCallback regionCallback2 = onNewPart;
            RegionCallback regionCallback3 = onOldPart;
            int i2 = 1;
            if (this.preserveIndex) {
                boolean equal = true;
                int start = 0;
                int minLength = Math.min(newText.length(), oldText2.length());
                if (this.startFromEnd) {
                    ArrayList<Integer> indexes = new ArrayList<>();
                    boolean eq = true;
                    int i3 = 0;
                    while (i3 <= minLength) {
                        int a = (newText.length() - i3) - i2;
                        int b = (oldText2.length() - i3) - i2;
                        boolean thisEqual = a >= 0 && b >= 0 && partEquals(charSequence2, charSequence, a, b);
                        if (equal != thisEqual || i3 == minLength) {
                            if (i3 - start > 0) {
                                if (indexes.size() == 0) {
                                    eq = equal;
                                }
                                indexes.add(Integer.valueOf(i3 - start));
                            }
                            equal = thisEqual;
                            start = i3;
                        }
                        i3++;
                        i2 = 1;
                    }
                    int a2 = newText.length() - minLength;
                    int b2 = oldText2.length() - minLength;
                    if (a2 > 0) {
                        i = 0;
                        regionCallback2.run(charSequence2.subSequence(0, a2), 0, a2);
                    } else {
                        i = 0;
                    }
                    if (b2 > 0) {
                        regionCallback3.run(charSequence.subSequence(i, b2), i, b2);
                    }
                    int i4 = indexes.size() - 1;
                    while (i4 >= 0) {
                        int count = indexes.get(i4).intValue();
                        if (i4 % 2 != 0 ? eq : !eq) {
                            regionCallback2.run(charSequence2.subSequence(a2, a2 + count), a2, a2 + count);
                            regionCallback3.run(charSequence.subSequence(b2, b2 + count), b2, b2 + count);
                        } else if (newText.length() > oldText2.length()) {
                            regionCallback.run(charSequence2.subSequence(a2, a2 + count), a2, a2 + count);
                        } else {
                            regionCallback.run(charSequence.subSequence(b2, b2 + count), b2, b2 + count);
                        }
                        a2 += count;
                        b2 += count;
                        i4--;
                    }
                    return;
                }
                int i5 = 0;
                while (i5 <= minLength) {
                    boolean thisEqual2 = i5 < minLength && partEquals(charSequence2, charSequence, i5, i5);
                    if (equal != thisEqual2 || i5 == minLength) {
                        if (i5 - start > 0) {
                            if (equal) {
                                regionCallback.run(charSequence2.subSequence(start, i5), start, i5);
                            } else {
                                regionCallback2.run(charSequence2.subSequence(start, i5), start, i5);
                                regionCallback3.run(charSequence.subSequence(start, i5), start, i5);
                            }
                        }
                        equal = thisEqual2;
                        start = i5;
                    }
                    i5++;
                }
                if (newText.length() - minLength > 0) {
                    regionCallback2.run(charSequence2.subSequence(minLength, newText.length()), minLength, newText.length());
                }
                if (oldText2.length() - minLength > 0) {
                    regionCallback3.run(charSequence.subSequence(minLength, oldText2.length()), minLength, oldText2.length());
                    return;
                }
                return;
            }
            int astart = 0;
            int bstart = 0;
            boolean equal2 = true;
            int a3 = 0;
            int b3 = 0;
            int minLength2 = Math.min(newText.length(), oldText2.length());
            while (a3 <= minLength2) {
                boolean thisEqual3 = a3 < minLength2 && partEquals(charSequence2, charSequence, a3, b3);
                if (equal2 != thisEqual3 || a3 == minLength2) {
                    if (a3 == minLength2) {
                        a3 = newText.length();
                        b3 = oldText2.length();
                    }
                    int alen = a3 - astart;
                    int blen = b3 - bstart;
                    if (alen > 0 || blen > 0) {
                        if (alen == blen && equal2) {
                            regionCallback.run(charSequence2.subSequence(astart, a3), astart, a3);
                        } else if (!equal2) {
                            if (alen > 0) {
                                regionCallback2.run(charSequence2.subSequence(astart, a3), astart, a3);
                            }
                            if (blen > 0) {
                                regionCallback3.run(charSequence.subSequence(bstart, b3), bstart, b3);
                            }
                        }
                    }
                    equal2 = thisEqual3;
                    astart = a3;
                    bstart = b3;
                }
                if (thisEqual3) {
                    b3++;
                }
                a3++;
            }
        }

        public void setTextSize(float textSizePx) {
            this.textPaint.setTextSize(textSizePx);
        }

        public void setTextColor(int color) {
            this.textPaint.setColor(color);
        }

        public void setTypeface(Typeface typeface) {
            this.textPaint.setTypeface(typeface);
        }

        public void setGravity(int gravity2) {
            this.gravity = gravity2;
        }

        public void setAnimationProperties(float moveAmplitude2, long startDelay, long duration, TimeInterpolator interpolator) {
            this.moveAmplitude = moveAmplitude2;
            this.animateDelay = startDelay;
            this.animateDuration = duration;
            this.animateInterpolator = interpolator;
        }

        public void copyStylesFrom(TextPaint paint) {
            setTextColor(paint.getColor());
            setTextSize(paint.getTextSize());
            setTypeface(paint.getTypeface());
        }

        public TextPaint getPaint() {
            return this.textPaint;
        }

        public void setAlpha(int alpha2) {
            this.alpha = alpha2;
        }

        public void setColorFilter(ColorFilter colorFilter) {
            this.textPaint.setColorFilter(colorFilter);
        }

        @Deprecated
        public int getOpacity() {
            return -2;
        }

        public void setBounds(Rect bounds2) {
            super.setBounds(bounds2);
            this.bounds.set(bounds2);
        }

        public void setBounds(int left, int top, int right, int bottom) {
            super.setBounds(left, top, right, bottom);
            this.bounds.set(left, top, right, bottom);
        }
    }

    public AnimatedTextView(Context context) {
        super(context);
        AnimatedTextDrawable animatedTextDrawable = new AnimatedTextDrawable();
        this.drawable = animatedTextDrawable;
        animatedTextDrawable.setCallback(this);
    }

    public AnimatedTextView(Context context, boolean splitByWords, boolean preserveIndex, boolean startFromEnd) {
        super(context);
        AnimatedTextDrawable animatedTextDrawable = new AnimatedTextDrawable(splitByWords, preserveIndex, startFromEnd);
        this.drawable = animatedTextDrawable;
        animatedTextDrawable.setCallback(this);
        this.drawable.setOnAnimationFinishListener(new AnimatedTextView$$ExternalSyntheticLambda0(this));
    }

    /* renamed from: lambda$new$0$org-telegram-ui-Components-AnimatedTextView  reason: not valid java name */
    public /* synthetic */ void m533lambda$new$0$orgtelegramuiComponentsAnimatedTextView() {
        CharSequence charSequence = this.toSetText;
        if (charSequence != null) {
            setText(charSequence, this.toSetMoveDown, true);
            this.toSetText = null;
            this.toSetMoveDown = false;
        }
    }

    /* access modifiers changed from: protected */
    public void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        int width = View.MeasureSpec.getSize(widthMeasureSpec);
        int height = View.MeasureSpec.getSize(heightMeasureSpec);
        if (this.lastMaxWidth != width) {
            this.drawable.setBounds(getPaddingLeft(), getPaddingTop(), width - getPaddingRight(), height - getPaddingBottom());
            setText(this.drawable.getText(), false);
        }
        this.lastMaxWidth = width;
        if (View.MeasureSpec.getMode(widthMeasureSpec) == Integer.MIN_VALUE) {
            width = getPaddingLeft() + this.drawable.getWidth() + getPaddingRight();
        }
        setMeasuredDimension(width, height);
    }

    /* access modifiers changed from: protected */
    public void onDraw(Canvas canvas) {
        this.drawable.setBounds(getPaddingLeft(), getPaddingTop(), getMeasuredWidth() - getPaddingRight(), getMeasuredHeight() - getPaddingBottom());
        this.drawable.draw(canvas);
    }

    public void setText(CharSequence text) {
        setText(text, true, true);
    }

    public void setText(CharSequence text, boolean animated) {
        setText(text, animated, true);
    }

    public void setText(CharSequence text, boolean animated, boolean moveDown) {
        boolean animated2 = !this.first && animated;
        this.first = false;
        if (!animated2 || !this.drawable.isAnimating()) {
            int wasWidth = this.drawable.getWidth();
            this.drawable.setBounds(getPaddingLeft(), getPaddingTop(), this.lastMaxWidth - getPaddingRight(), getMeasuredHeight() - getPaddingBottom());
            this.drawable.setText(text, animated2, moveDown);
            if (wasWidth < this.drawable.getWidth()) {
                requestLayout();
                return;
            }
            return;
        }
        this.toSetText = text;
        this.toSetMoveDown = moveDown;
    }

    public CharSequence getText() {
        return this.drawable.getText();
    }

    public void setTextSize(float textSizePx) {
        this.drawable.setTextSize(textSizePx);
    }

    public void setTextColor(int color) {
        this.drawable.setTextColor(color);
    }

    public void setTypeface(Typeface typeface) {
        this.drawable.setTypeface(typeface);
    }

    public void setGravity(int gravity) {
        this.drawable.setGravity(gravity);
    }

    public void setAnimationProperties(float moveAmplitude, long startDelay, long duration, TimeInterpolator interpolator) {
        this.drawable.setAnimationProperties(moveAmplitude, startDelay, duration, interpolator);
    }

    public TextPaint getPaint() {
        return this.drawable.getPaint();
    }

    public void invalidateDrawable(Drawable drawable2) {
        super.invalidateDrawable(drawable2);
        invalidate();
    }

    public void onInitializeAccessibilityNodeInfo(AccessibilityNodeInfo info) {
        super.onInitializeAccessibilityNodeInfo(info);
        info.setClassName("android.widget.TextView");
        info.setText(getText());
    }
}
