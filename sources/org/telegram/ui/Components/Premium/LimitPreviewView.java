package org.telegram.ui.Components.Premium;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ValueAnimator;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.CornerPathEffect;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.PathEffect;
import android.text.Layout;
import android.text.SpannableStringBuilder;
import android.text.StaticLayout;
import android.text.TextPaint;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.OvershootInterpolator;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.TextView;
import java.util.ArrayList;
import org.telegram.messenger.AndroidUtilities;
import org.telegram.messenger.LocaleController;
import org.telegram.messenger.UserConfig;
import org.telegram.messenger.Utilities;
import org.telegram.ui.ActionBar.Theme;
import org.telegram.ui.Components.ColoredImageSpan;
import org.telegram.ui.Components.CubicBezierInterpolator;
import org.telegram.ui.Components.EmptyStubSpan;
import org.telegram.ui.Components.LayoutHelper;
import org.telegram.ui.Components.Premium.PremiumGradient;

public class LimitPreviewView extends LinearLayout {
    boolean animationCanPlay = true;
    TextView defaultCount;
    public int gradientTotalHeight;
    int gradientYOffset;
    int icon;
    boolean inc;
    CounterView limitIcon;
    LinearLayout limitsContainer;
    /* access modifiers changed from: private */
    public View parentVideForGradient;
    private float position;
    TextView premiumCount;
    /* access modifiers changed from: private */
    public boolean premiumLocked;
    float progress;
    PremiumGradient.GradientTools staticGradient;
    boolean wasAnimation;
    boolean wasHaptic;

    /* JADX INFO: super call moved to the top of the method (can break code semantics) */
    public LimitPreviewView(Context context, int icon2, int currentValue, int premiumLimit) {
        super(context);
        Context context2 = context;
        int i = icon2;
        this.icon = i;
        setOrientation(1);
        setClipChildren(false);
        setClipToPadding(false);
        if (i != 0) {
            setPadding(0, AndroidUtilities.dp(16.0f), 0, 0);
            this.limitIcon = new CounterView(context2);
            setIconValue(currentValue);
            this.limitIcon.setPadding(AndroidUtilities.dp(24.0f), AndroidUtilities.dp(6.0f), AndroidUtilities.dp(24.0f), AndroidUtilities.dp(14.0f));
            addView(this.limitIcon, LayoutHelper.createLinear(-2, -2, 0.0f, 3));
        } else {
            int i2 = currentValue;
        }
        AnonymousClass1 r5 = new LinearLayout(context2) {
            Paint grayPaint = new Paint();

            /* JADX WARNING: type inference failed for: r5v5, types: [android.view.ViewParent] */
            /* access modifiers changed from: protected */
            /* JADX WARNING: Multi-variable type inference failed */
            /* Code decompiled incorrectly, please refer to instructions dump. */
            public void dispatchDraw(android.graphics.Canvas r13) {
                /*
                    r12 = this;
                    android.graphics.Paint r0 = r12.grayPaint
                    java.lang.String r1 = "windowBackgroundGray"
                    int r1 = org.telegram.ui.ActionBar.Theme.getColor(r1)
                    r0.setColor(r1)
                    android.graphics.RectF r0 = org.telegram.messenger.AndroidUtilities.rectTmp
                    int r1 = r12.getMeasuredWidth()
                    float r1 = (float) r1
                    int r2 = r12.getMeasuredHeight()
                    float r2 = (float) r2
                    r3 = 0
                    r0.set(r3, r3, r1, r2)
                    android.graphics.RectF r0 = org.telegram.messenger.AndroidUtilities.rectTmp
                    r1 = 1086324736(0x40CLASSNAME, float:6.0)
                    int r2 = org.telegram.messenger.AndroidUtilities.dp(r1)
                    float r2 = (float) r2
                    int r4 = org.telegram.messenger.AndroidUtilities.dp(r1)
                    float r4 = (float) r4
                    android.graphics.Paint r5 = r12.grayPaint
                    r13.drawRoundRect(r0, r2, r4, r5)
                    r13.save()
                    int r0 = r12.getMeasuredWidth()
                    float r0 = (float) r0
                    r2 = 1073741824(0x40000000, float:2.0)
                    float r0 = r0 / r2
                    int r2 = r12.getMeasuredWidth()
                    float r2 = (float) r2
                    int r4 = r12.getMeasuredHeight()
                    float r4 = (float) r4
                    r13.clipRect(r0, r3, r2, r4)
                    org.telegram.ui.Components.Premium.PremiumGradient r0 = org.telegram.ui.Components.Premium.PremiumGradient.getInstance()
                    android.graphics.Paint r0 = r0.getMainGradientPaint()
                    org.telegram.ui.Components.Premium.LimitPreviewView r2 = org.telegram.ui.Components.Premium.LimitPreviewView.this
                    android.view.View r2 = r2.parentVideForGradient
                    if (r2 == 0) goto L_0x00ab
                    org.telegram.ui.Components.Premium.LimitPreviewView r2 = org.telegram.ui.Components.Premium.LimitPreviewView.this
                    android.view.View r2 = r2.parentVideForGradient
                    org.telegram.ui.Components.Premium.LimitPreviewView r3 = org.telegram.ui.Components.Premium.LimitPreviewView.this
                    org.telegram.ui.Components.Premium.PremiumGradient$GradientTools r3 = r3.staticGradient
                    if (r3 == 0) goto L_0x007b
                    org.telegram.ui.Components.Premium.LimitPreviewView r3 = org.telegram.ui.Components.Premium.LimitPreviewView.this
                    org.telegram.ui.Components.Premium.PremiumGradient$GradientTools r3 = r3.staticGradient
                    android.graphics.Paint r0 = r3.paint
                    org.telegram.ui.Components.Premium.LimitPreviewView r3 = org.telegram.ui.Components.Premium.LimitPreviewView.this
                    org.telegram.ui.Components.Premium.PremiumGradient$GradientTools r3 = r3.staticGradient
                    org.telegram.ui.Components.Premium.LimitPreviewView r4 = org.telegram.ui.Components.Premium.LimitPreviewView.this
                    int r4 = r4.gradientTotalHeight
                    float r4 = (float) r4
                    org.telegram.ui.Components.Premium.LimitPreviewView r5 = org.telegram.ui.Components.Premium.LimitPreviewView.this
                    int r5 = r5.gradientYOffset
                    int r5 = -r5
                    float r5 = (float) r5
                    r3.gradientMatrixLinear(r4, r5)
                    goto L_0x00aa
                L_0x007b:
                    r3 = 0
                    r4 = r12
                L_0x007d:
                    if (r4 == r2) goto L_0x008c
                    float r5 = r4.getY()
                    float r3 = r3 + r5
                    android.view.ViewParent r5 = r4.getParent()
                    r4 = r5
                    android.view.View r4 = (android.view.View) r4
                    goto L_0x007d
                L_0x008c:
                    org.telegram.ui.Components.Premium.PremiumGradient r5 = org.telegram.ui.Components.Premium.PremiumGradient.getInstance()
                    r6 = 0
                    r7 = 0
                    int r8 = r2.getMeasuredWidth()
                    int r9 = r2.getMeasuredHeight()
                    org.telegram.ui.Components.Premium.LimitPreviewView r10 = org.telegram.ui.Components.Premium.LimitPreviewView.this
                    float r10 = r10.getGlobalXOffset()
                    int r11 = r12.getLeft()
                    float r11 = (float) r11
                    float r10 = r10 - r11
                    float r11 = -r3
                    r5.updateMainGradientMatrix(r6, r7, r8, r9, r10, r11)
                L_0x00aa:
                    goto L_0x00d2
                L_0x00ab:
                    org.telegram.ui.Components.Premium.PremiumGradient r2 = org.telegram.ui.Components.Premium.PremiumGradient.getInstance()
                    r3 = 0
                    r4 = 0
                    org.telegram.ui.Components.Premium.LimitPreviewView r5 = org.telegram.ui.Components.Premium.LimitPreviewView.this
                    int r5 = r5.getMeasuredWidth()
                    org.telegram.ui.Components.Premium.LimitPreviewView r6 = org.telegram.ui.Components.Premium.LimitPreviewView.this
                    int r6 = r6.getMeasuredHeight()
                    org.telegram.ui.Components.Premium.LimitPreviewView r7 = org.telegram.ui.Components.Premium.LimitPreviewView.this
                    float r7 = r7.getGlobalXOffset()
                    int r8 = r12.getLeft()
                    float r8 = (float) r8
                    float r7 = r7 - r8
                    int r8 = r12.getTop()
                    int r8 = -r8
                    float r8 = (float) r8
                    r2.updateMainGradientMatrix(r3, r4, r5, r6, r7, r8)
                L_0x00d2:
                    android.graphics.RectF r2 = org.telegram.messenger.AndroidUtilities.rectTmp
                    int r3 = org.telegram.messenger.AndroidUtilities.dp(r1)
                    float r3 = (float) r3
                    int r1 = org.telegram.messenger.AndroidUtilities.dp(r1)
                    float r1 = (float) r1
                    r13.drawRoundRect(r2, r3, r1, r0)
                    r13.restore()
                    org.telegram.ui.Components.Premium.LimitPreviewView r1 = org.telegram.ui.Components.Premium.LimitPreviewView.this
                    org.telegram.ui.Components.Premium.PremiumGradient$GradientTools r1 = r1.staticGradient
                    if (r1 != 0) goto L_0x00ed
                    r12.invalidate()
                L_0x00ed:
                    super.dispatchDraw(r13)
                    return
                */
                throw new UnsupportedOperationException("Method not decompiled: org.telegram.ui.Components.Premium.LimitPreviewView.AnonymousClass1.dispatchDraw(android.graphics.Canvas):void");
            }
        };
        this.limitsContainer = r5;
        r5.setOrientation(0);
        FrameLayout limitLayout = new FrameLayout(context2);
        TextView freeTextView = new TextView(context2);
        freeTextView.setTypeface(AndroidUtilities.getTypeface("fonts/rmedium.ttf"));
        freeTextView.setText(LocaleController.getString("LimitFree", NUM));
        freeTextView.setGravity(16);
        freeTextView.setTextColor(Theme.getColor("windowBackgroundWhiteBlackText"));
        freeTextView.setPadding(AndroidUtilities.dp(12.0f), 0, 0, 0);
        TextView textView = new TextView(context2);
        this.defaultCount = textView;
        textView.setTypeface(AndroidUtilities.getTypeface("fonts/rmedium.ttf"));
        this.defaultCount.setText(Integer.toString(premiumLimit));
        this.defaultCount.setGravity(16);
        this.defaultCount.setTextColor(Theme.getColor("windowBackgroundWhiteBlackText"));
        limitLayout.addView(freeTextView, LayoutHelper.createFrame(-1, 30.0f, 3, 0.0f, 0.0f, 36.0f, 0.0f));
        limitLayout.addView(this.defaultCount, LayoutHelper.createFrame(-2, 30.0f, 5, 0.0f, 0.0f, 12.0f, 0.0f));
        this.limitsContainer.addView(limitLayout, LayoutHelper.createLinear(0, 30, 1.0f));
        FrameLayout limitLayout2 = new FrameLayout(context2);
        TextView limitTextView = new TextView(context2);
        limitTextView.setTypeface(AndroidUtilities.getTypeface("fonts/rmedium.ttf"));
        limitTextView.setText(LocaleController.getString("LimitPremium", NUM));
        limitTextView.setGravity(16);
        limitTextView.setTextColor(-1);
        limitTextView.setPadding(AndroidUtilities.dp(12.0f), 0, 0, 0);
        TextView textView2 = new TextView(context2);
        this.premiumCount = textView2;
        textView2.setTypeface(AndroidUtilities.getTypeface("fonts/rmedium.ttf"));
        this.premiumCount.setText(Integer.toString(premiumLimit));
        this.premiumCount.setGravity(16);
        this.premiumCount.setTextColor(-1);
        limitLayout2.addView(limitTextView, LayoutHelper.createFrame(-1, 30.0f, 3, 0.0f, 0.0f, 36.0f, 0.0f));
        limitLayout2.addView(this.premiumCount, LayoutHelper.createFrame(-2, 30.0f, 5, 0.0f, 0.0f, 12.0f, 0.0f));
        this.limitsContainer.addView(limitLayout2, LayoutHelper.createLinear(0, 30, 1.0f));
        addView(this.limitsContainer, LayoutHelper.createLinear(-1, -2, 0.0f, 0, 14, i == 0 ? 0 : 12, 14, 0));
    }

    public void setIconValue(int currentValue) {
        SpannableStringBuilder spannableStringBuilder = new SpannableStringBuilder();
        spannableStringBuilder.append("d ").setSpan(new ColoredImageSpan(this.icon), 0, 1, 0);
        spannableStringBuilder.append(Integer.toString(currentValue));
        this.limitIcon.setText(spannableStringBuilder);
    }

    /* access modifiers changed from: private */
    public float getGlobalXOffset() {
        return ((((float) (-getMeasuredWidth())) * 0.1f) * this.progress) - (((float) getMeasuredWidth()) * 0.2f);
    }

    /* access modifiers changed from: protected */
    public void dispatchDraw(Canvas canvas) {
        if (this.staticGradient == null) {
            if (this.inc) {
                float f = this.progress + 0.016f;
                this.progress = f;
                if (f > 3.0f) {
                    this.inc = false;
                }
            } else {
                float f2 = this.progress - 0.016f;
                this.progress = f2;
                if (f2 < 1.0f) {
                    this.inc = true;
                }
            }
            invalidate();
        }
        super.dispatchDraw(canvas);
    }

    /* access modifiers changed from: protected */
    public void onLayout(boolean changed, int l, int t, int r, int b) {
        float toProgressCenter;
        float toX;
        super.onLayout(changed, l, t, r, b);
        if (!this.wasAnimation && this.limitIcon != null && this.animationCanPlay && !this.premiumLocked) {
            int padding = AndroidUtilities.dp(14.0f);
            float toX2 = (((float) padding) + (((float) (getMeasuredWidth() - (padding * 2))) * this.position)) - (((float) this.limitIcon.getMeasuredWidth()) / 2.0f);
            if (toX2 > ((float) ((getMeasuredWidth() - padding) - this.limitIcon.getMeasuredWidth()))) {
                toX = (float) ((getMeasuredWidth() - padding) - this.limitIcon.getMeasuredWidth());
                toProgressCenter = 1.0f;
            } else {
                toX = toX2;
                toProgressCenter = 0.5f;
            }
            this.limitIcon.setAlpha(1.0f);
            this.limitIcon.setTranslationX(0.0f);
            CounterView counterView = this.limitIcon;
            counterView.setPivotX(((float) counterView.getMeasuredWidth()) / 2.0f);
            CounterView counterView2 = this.limitIcon;
            counterView2.setPivotY((float) counterView2.getMeasuredHeight());
            this.limitIcon.setScaleX(0.0f);
            this.limitIcon.setScaleY(0.0f);
            this.limitIcon.createAnimationLayouts();
            ValueAnimator valueAnimator = ValueAnimator.ofFloat(new float[]{0.0f, 1.0f});
            LimitPreviewView$$ExternalSyntheticLambda0 limitPreviewView$$ExternalSyntheticLambda0 = r0;
            LimitPreviewView$$ExternalSyntheticLambda0 limitPreviewView$$ExternalSyntheticLambda02 = new LimitPreviewView$$ExternalSyntheticLambda0(this, 0.0f, toX, 0.5f, toProgressCenter);
            valueAnimator.addUpdateListener(limitPreviewView$$ExternalSyntheticLambda0);
            valueAnimator.setInterpolator(new OvershootInterpolator());
            valueAnimator.setDuration(1000);
            valueAnimator.setStartDelay(200);
            valueAnimator.start();
            this.wasAnimation = true;
        } else if (this.premiumLocked) {
            int padding2 = AndroidUtilities.dp(14.0f);
            float toX3 = (((float) padding2) + (((float) (getMeasuredWidth() - (padding2 * 2))) * 0.5f)) - (((float) this.limitIcon.getMeasuredWidth()) / 2.0f);
            boolean z = this.wasAnimation;
            if (!z && this.animationCanPlay) {
                this.wasAnimation = true;
                this.limitIcon.animate().alpha(1.0f).scaleX(1.0f).scaleY(1.0f).setDuration(200).setInterpolator(new OvershootInterpolator()).start();
            } else if (!z) {
                this.limitIcon.setAlpha(0.0f);
                this.limitIcon.setScaleX(0.0f);
                this.limitIcon.setScaleY(0.0f);
            } else {
                this.limitIcon.setAlpha(1.0f);
                this.limitIcon.setScaleX(1.0f);
                this.limitIcon.setScaleY(1.0f);
            }
            this.limitIcon.setTranslationX(toX3);
        } else {
            CounterView counterView3 = this.limitIcon;
            if (counterView3 != null) {
                counterView3.setAlpha(0.0f);
            }
        }
    }

    /* renamed from: lambda$onLayout$0$org-telegram-ui-Components-Premium-LimitPreviewView  reason: not valid java name */
    public /* synthetic */ void m1237xfbb0c7b7(float fromX, float finalToX, float fromProgressCenter, float finalToProgressCenter, ValueAnimator animation) {
        float v = ((Float) animation.getAnimatedValue()).floatValue();
        float moveValue = Math.min(1.0f, v);
        if (v > 1.0f) {
            if (!this.wasHaptic) {
                this.wasHaptic = true;
                this.limitIcon.performHapticFeedback(3);
            }
            this.limitIcon.setRotation((v - 1.0f) * 60.0f);
        } else {
            this.limitIcon.setRotation(0.0f);
        }
        this.limitIcon.setTranslationX(((1.0f - moveValue) * fromX) + (finalToX * moveValue));
        float arrowCenter = ((1.0f - moveValue) * fromProgressCenter) + (finalToProgressCenter * moveValue);
        this.limitIcon.setArrowCenter(arrowCenter);
        float scale = Math.min(1.0f, 2.0f * moveValue);
        this.limitIcon.setScaleX(scale);
        this.limitIcon.setScaleY(scale);
        CounterView counterView = this.limitIcon;
        counterView.setPivotX(((float) counterView.getMeasuredWidth()) * arrowCenter);
    }

    public void setType(int type) {
        String str;
        if (type == 6) {
            if (this.limitIcon != null) {
                SpannableStringBuilder spannableStringBuilder = new SpannableStringBuilder();
                spannableStringBuilder.append("d ").setSpan(new ColoredImageSpan(this.icon), 0, 1, 0);
                if (UserConfig.getInstance(UserConfig.selectedAccount).isPremium()) {
                    str = "4 GB";
                } else {
                    str = "2 GB";
                }
                spannableStringBuilder.append(str);
                this.limitIcon.setText(spannableStringBuilder);
            }
            this.premiumCount.setText("4 GB");
        }
    }

    public void setBagePosition(float position2) {
        this.position = position2;
    }

    public void setParentViewForGradien(ViewGroup containerView) {
        this.parentVideForGradient = containerView;
    }

    public void setStaticGradinet(PremiumGradient.GradientTools gradientTools) {
        this.staticGradient = gradientTools;
    }

    public void setDelayedAnimation() {
        this.animationCanPlay = false;
    }

    public void startDelayedAnimation() {
        this.animationCanPlay = true;
        requestLayout();
    }

    public void setPremiumLocked() {
        this.limitsContainer.setVisibility(8);
        this.limitIcon.setPadding(AndroidUtilities.dp(24.0f), AndroidUtilities.dp(3.0f), AndroidUtilities.dp(24.0f), AndroidUtilities.dp(3.0f));
        this.premiumLocked = true;
    }

    private class LimitTextView extends LinearLayout {
        public LimitTextView(Context context) {
            super(context);
        }
    }

    private class CounterView extends View {
        ArrayList<AnimatedLayout> animatedLayouts = new ArrayList<>();
        StaticLayout animatedStableLayout;
        boolean animationInProgress;
        float arrowCenter;
        boolean invalidatePath;
        Path path = new Path();
        PathEffect pathEffect = new CornerPathEffect((float) AndroidUtilities.dp(6.0f));
        CharSequence text;
        StaticLayout textLayout;
        TextPaint textPaint = new TextPaint(1);
        float textWidth;

        public CounterView(Context context) {
            super(context);
            this.textPaint.setTypeface(AndroidUtilities.getTypeface("fonts/rmedium.ttf"));
            this.textPaint.setTextSize((float) AndroidUtilities.dp(22.0f));
            this.textPaint.setColor(-1);
        }

        /* access modifiers changed from: protected */
        public void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
            TextPaint textPaint2 = this.textPaint;
            CharSequence charSequence = this.text;
            this.textWidth = textPaint2.measureText(charSequence, 0, charSequence.length());
            this.textLayout = new StaticLayout(this.text, this.textPaint, AndroidUtilities.dp(12.0f) + ((int) this.textWidth), Layout.Alignment.ALIGN_NORMAL, 1.0f, 0.0f, false);
            setMeasuredDimension((int) (this.textWidth + ((float) getPaddingRight()) + ((float) getPaddingLeft())), AndroidUtilities.dp(44.0f) + AndroidUtilities.dp(8.0f));
            updatePath();
        }

        private void updatePath() {
            int h = getMeasuredHeight() - AndroidUtilities.dp(8.0f);
            float widthHalf = ((float) getMeasuredWidth()) * this.arrowCenter;
            float x2 = Utilities.clamp(((float) AndroidUtilities.dp(8.0f)) + widthHalf, (float) getMeasuredWidth(), 0.0f);
            float x3 = Utilities.clamp(((float) AndroidUtilities.dp(10.0f)) + widthHalf, (float) getMeasuredWidth(), 0.0f);
            this.path.rewind();
            this.path.moveTo(widthHalf - ((float) AndroidUtilities.dp(24.0f)), (((float) h) - (((float) h) / 2.0f)) - ((float) AndroidUtilities.dp(2.0f)));
            this.path.lineTo(widthHalf - ((float) AndroidUtilities.dp(24.0f)), (float) h);
            this.path.lineTo(widthHalf - ((float) AndroidUtilities.dp(8.0f)), (float) h);
            this.path.lineTo(widthHalf, (float) (AndroidUtilities.dp(8.0f) + h));
            if (this.arrowCenter < 0.7f) {
                this.path.lineTo(x2, (float) h);
            }
            this.path.lineTo(x3, (float) h);
            this.path.lineTo(x3, (((float) h) - (((float) h) / 2.0f)) - ((float) AndroidUtilities.dp(2.0f)));
            this.path.close();
        }

        /* access modifiers changed from: protected */
        public void onDraw(Canvas canvas) {
            int h = getMeasuredHeight() - AndroidUtilities.dp(8.0f);
            if (LimitPreviewView.this.premiumLocked) {
                h = getMeasuredHeight();
                PremiumGradient.getInstance().updateMainGradientMatrix(0, 0, LimitPreviewView.this.getMeasuredWidth(), LimitPreviewView.this.getMeasuredHeight(), LimitPreviewView.this.getGlobalXOffset() - getX(), (float) (-getTop()));
                AndroidUtilities.rectTmp.set(0.0f, (float) AndroidUtilities.dp(3.0f), (float) getMeasuredWidth(), (float) (h - AndroidUtilities.dp(3.0f)));
                canvas.drawRoundRect(AndroidUtilities.rectTmp, ((float) h) / 2.0f, ((float) h) / 2.0f, PremiumGradient.getInstance().getMainGradientPaint());
            } else {
                if (this.invalidatePath) {
                    this.invalidatePath = false;
                    updatePath();
                }
                PremiumGradient.getInstance().updateMainGradientMatrix(0, 0, LimitPreviewView.this.getMeasuredWidth(), LimitPreviewView.this.getMeasuredHeight(), LimitPreviewView.this.getGlobalXOffset() - getX(), (float) (-getTop()));
                AndroidUtilities.rectTmp.set(0.0f, 0.0f, (float) getMeasuredWidth(), (float) h);
                canvas.drawRoundRect(AndroidUtilities.rectTmp, ((float) h) / 2.0f, ((float) h) / 2.0f, PremiumGradient.getInstance().getMainGradientPaint());
                PremiumGradient.getInstance().getMainGradientPaint().setPathEffect(this.pathEffect);
                canvas.drawPath(this.path, PremiumGradient.getInstance().getMainGradientPaint());
                PremiumGradient.getInstance().getMainGradientPaint().setPathEffect((PathEffect) null);
                invalidate();
            }
            float x = ((float) (getMeasuredWidth() - this.textLayout.getWidth())) / 2.0f;
            float y = ((float) (h - this.textLayout.getHeight())) / 2.0f;
            if (this.animationInProgress) {
                canvas.save();
                canvas.clipRect(0, 0, getMeasuredWidth(), getMeasuredHeight() - AndroidUtilities.dp(8.0f));
                if (this.animatedStableLayout != null) {
                    canvas.save();
                    canvas.translate(x, y);
                    this.animatedStableLayout.draw(canvas);
                    canvas.restore();
                }
                for (int i = 0; i < this.animatedLayouts.size(); i++) {
                    AnimatedLayout animatedLayout = this.animatedLayouts.get(i);
                    canvas.save();
                    if (animatedLayout.direction) {
                        canvas.translate(animatedLayout.x + x, (y - (((float) (h * 10)) * animatedLayout.progress)) + ((float) ((10 - animatedLayout.staticLayouts.size()) * h)));
                        for (int j = 0; j < animatedLayout.staticLayouts.size(); j++) {
                            canvas.translate(0.0f, (float) h);
                            animatedLayout.staticLayouts.get(j).draw(canvas);
                        }
                    } else {
                        canvas.translate(animatedLayout.x + x, ((((float) (h * 10)) * animatedLayout.progress) + y) - ((float) ((10 - animatedLayout.staticLayouts.size()) * h)));
                        for (int j2 = 0; j2 < animatedLayout.staticLayouts.size(); j2++) {
                            canvas.translate(0.0f, (float) (-h));
                            animatedLayout.staticLayouts.get(j2).draw(canvas);
                        }
                    }
                    canvas.restore();
                }
                canvas.restore();
            } else if (this.textLayout != null) {
                canvas.save();
                canvas.translate(x, y);
                this.textLayout.draw(canvas);
                canvas.restore();
            }
        }

        public void setTranslationX(float translationX) {
            if (translationX != getTranslationX()) {
                super.setTranslationX(translationX);
                invalidate();
            }
        }

        /* access modifiers changed from: package-private */
        public void createAnimationLayouts() {
            this.animatedLayouts.clear();
            SpannableStringBuilder spannableStringBuilder = new SpannableStringBuilder(this.text);
            boolean direction = true;
            int directionCount = 0;
            for (int i = 0; i < this.text.length(); i++) {
                if (Character.isDigit(this.text.charAt(i))) {
                    AnimatedLayout animatedLayout = new AnimatedLayout();
                    this.animatedLayouts.add(animatedLayout);
                    animatedLayout.x = this.textLayout.getSecondaryHorizontal(i);
                    animatedLayout.direction = direction;
                    if (directionCount >= 1) {
                        direction = !direction;
                        directionCount = 0;
                    }
                    directionCount++;
                    int digit = this.text.charAt(i) - '0';
                    if (digit == 0) {
                        digit = 10;
                    }
                    for (int j = 1; j <= digit; j++) {
                        int k = j;
                        if (k == 10) {
                            k = 0;
                        }
                        animatedLayout.staticLayouts.add(new StaticLayout("" + k, this.textPaint, (int) this.textWidth, Layout.Alignment.ALIGN_NORMAL, 1.0f, 0.0f, false));
                    }
                    spannableStringBuilder.setSpan(new EmptyStubSpan(), i, i + 1, 0);
                }
            }
            this.animatedStableLayout = new StaticLayout(spannableStringBuilder, this.textPaint, ((int) this.textWidth) + AndroidUtilities.dp(12.0f), Layout.Alignment.ALIGN_NORMAL, 1.0f, 0.0f, false);
            for (int i2 = 0; i2 < this.animatedLayouts.size(); i2++) {
                this.animationInProgress = true;
                final AnimatedLayout layout = this.animatedLayouts.get(i2);
                layout.valueAnimator = ValueAnimator.ofFloat(new float[]{0.0f, 1.0f});
                layout.valueAnimator.addUpdateListener(new LimitPreviewView$CounterView$$ExternalSyntheticLambda0(this, layout));
                layout.valueAnimator.addListener(new AnimatorListenerAdapter() {
                    public void onAnimationEnd(Animator animation) {
                        layout.valueAnimator = null;
                        CounterView.this.checkAnimationComplete();
                    }
                });
                layout.valueAnimator.setInterpolator(CubicBezierInterpolator.EASE_OUT);
                layout.valueAnimator.setDuration(750);
                layout.valueAnimator.setStartDelay(((long) ((this.animatedLayouts.size() - 1) - i2)) * 60);
                layout.valueAnimator.start();
            }
        }

        /* renamed from: lambda$createAnimationLayouts$0$org-telegram-ui-Components-Premium-LimitPreviewView$CounterView  reason: not valid java name */
        public /* synthetic */ void m1238x1ef3fe0c(AnimatedLayout layout, ValueAnimator animation) {
            layout.progress = ((Float) animation.getAnimatedValue()).floatValue();
            invalidate();
        }

        /* access modifiers changed from: private */
        public void checkAnimationComplete() {
            int i = 0;
            while (i < this.animatedLayouts.size()) {
                if (this.animatedLayouts.get(i).valueAnimator == null) {
                    i++;
                } else {
                    return;
                }
            }
            this.animatedLayouts.clear();
            this.animationInProgress = false;
            invalidate();
        }

        public void setText(CharSequence text2) {
            this.text = text2;
        }

        public void setArrowCenter(float v) {
            if (this.arrowCenter != v) {
                this.arrowCenter = v;
                this.invalidatePath = true;
                invalidate();
            }
        }

        private class AnimatedLayout {
            public boolean direction;
            float progress;
            ArrayList<StaticLayout> staticLayouts;
            ValueAnimator valueAnimator;
            float x;

            private AnimatedLayout() {
                this.staticLayouts = new ArrayList<>();
            }
        }
    }
}
