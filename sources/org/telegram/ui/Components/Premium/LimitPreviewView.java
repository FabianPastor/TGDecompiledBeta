package org.telegram.ui.Components.Premium;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ValueAnimator;
import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.CornerPathEffect;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.PathEffect;
import android.graphics.RectF;
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
    @SuppressLint({"SetTextI18n"})
    public LimitPreviewView(Context context, int i, int i2, int i3) {
        super(context);
        Context context2 = context;
        int i4 = i;
        this.icon = i4;
        setOrientation(1);
        setClipChildren(false);
        setClipToPadding(false);
        if (i4 != 0) {
            setPadding(0, AndroidUtilities.dp(16.0f), 0, 0);
            this.limitIcon = new CounterView(context2);
            setIconValue(i2);
            this.limitIcon.setPadding(AndroidUtilities.dp(24.0f), AndroidUtilities.dp(6.0f), AndroidUtilities.dp(24.0f), AndroidUtilities.dp(14.0f));
            addView(this.limitIcon, LayoutHelper.createLinear(-2, -2, 0.0f, 3));
        }
        AnonymousClass1 r4 = new LinearLayout(context2) {
            Paint grayPaint = new Paint();

            /* access modifiers changed from: protected */
            public void dispatchDraw(Canvas canvas) {
                this.grayPaint.setColor(Theme.getColor("windowBackgroundGray"));
                RectF rectF = AndroidUtilities.rectTmp;
                float f = 0.0f;
                rectF.set(0.0f, 0.0f, (float) getMeasuredWidth(), (float) getMeasuredHeight());
                canvas.drawRoundRect(rectF, (float) AndroidUtilities.dp(6.0f), (float) AndroidUtilities.dp(6.0f), this.grayPaint);
                canvas.save();
                canvas.clipRect(((float) getMeasuredWidth()) / 2.0f, 0.0f, (float) getMeasuredWidth(), (float) getMeasuredHeight());
                Paint mainGradientPaint = PremiumGradient.getInstance().getMainGradientPaint();
                if (LimitPreviewView.this.parentVideForGradient != null) {
                    View access$000 = LimitPreviewView.this.parentVideForGradient;
                    LimitPreviewView limitPreviewView = LimitPreviewView.this;
                    PremiumGradient.GradientTools gradientTools = limitPreviewView.staticGradient;
                    if (gradientTools != null) {
                        mainGradientPaint = gradientTools.paint;
                        gradientTools.gradientMatrixLinear((float) limitPreviewView.gradientTotalHeight, (float) (-limitPreviewView.gradientYOffset));
                    } else {
                        for (View view = this; view != access$000; view = (View) view.getParent()) {
                            f += view.getY();
                        }
                        PremiumGradient.getInstance().updateMainGradientMatrix(0, 0, access$000.getMeasuredWidth(), access$000.getMeasuredHeight(), LimitPreviewView.this.getGlobalXOffset() - ((float) getLeft()), -f);
                    }
                } else {
                    PremiumGradient.getInstance().updateMainGradientMatrix(0, 0, LimitPreviewView.this.getMeasuredWidth(), LimitPreviewView.this.getMeasuredHeight(), LimitPreviewView.this.getGlobalXOffset() - ((float) getLeft()), (float) (-getTop()));
                }
                canvas.drawRoundRect(AndroidUtilities.rectTmp, (float) AndroidUtilities.dp(6.0f), (float) AndroidUtilities.dp(6.0f), mainGradientPaint);
                canvas.restore();
                if (LimitPreviewView.this.staticGradient == null) {
                    invalidate();
                }
                super.dispatchDraw(canvas);
            }
        };
        this.limitsContainer = r4;
        r4.setOrientation(0);
        FrameLayout frameLayout = new FrameLayout(context2);
        TextView textView = new TextView(context2);
        textView.setTypeface(AndroidUtilities.getTypeface("fonts/rmedium.ttf"));
        textView.setText(LocaleController.getString("LimitFree", NUM));
        textView.setGravity(16);
        textView.setTextColor(Theme.getColor("windowBackgroundWhiteBlackText"));
        textView.setPadding(AndroidUtilities.dp(12.0f), 0, 0, 0);
        TextView textView2 = new TextView(context2);
        this.defaultCount = textView2;
        textView2.setTypeface(AndroidUtilities.getTypeface("fonts/rmedium.ttf"));
        this.defaultCount.setText(Integer.toString(i3));
        this.defaultCount.setGravity(16);
        this.defaultCount.setTextColor(Theme.getColor("windowBackgroundWhiteBlackText"));
        frameLayout.addView(textView, LayoutHelper.createFrame(-1, 30.0f, 3, 0.0f, 0.0f, 36.0f, 0.0f));
        frameLayout.addView(this.defaultCount, LayoutHelper.createFrame(-2, 30.0f, 5, 0.0f, 0.0f, 12.0f, 0.0f));
        this.limitsContainer.addView(frameLayout, LayoutHelper.createLinear(0, 30, 1.0f));
        FrameLayout frameLayout2 = new FrameLayout(context2);
        TextView textView3 = new TextView(context2);
        textView3.setTypeface(AndroidUtilities.getTypeface("fonts/rmedium.ttf"));
        textView3.setText(LocaleController.getString("LimitPremium", NUM));
        textView3.setGravity(16);
        textView3.setTextColor(-1);
        textView3.setPadding(AndroidUtilities.dp(12.0f), 0, 0, 0);
        TextView textView4 = new TextView(context2);
        this.premiumCount = textView4;
        textView4.setTypeface(AndroidUtilities.getTypeface("fonts/rmedium.ttf"));
        this.premiumCount.setText(Integer.toString(i3));
        this.premiumCount.setGravity(16);
        this.premiumCount.setTextColor(-1);
        frameLayout2.addView(textView3, LayoutHelper.createFrame(-1, 30.0f, 3, 0.0f, 0.0f, 36.0f, 0.0f));
        frameLayout2.addView(this.premiumCount, LayoutHelper.createFrame(-2, 30.0f, 5, 0.0f, 0.0f, 12.0f, 0.0f));
        this.limitsContainer.addView(frameLayout2, LayoutHelper.createLinear(0, 30, 1.0f));
        addView(this.limitsContainer, LayoutHelper.createLinear(-1, -2, 0.0f, 0, 14, i4 == 0 ? 0 : 12, 14, 0));
    }

    public void setIconValue(int i) {
        SpannableStringBuilder spannableStringBuilder = new SpannableStringBuilder();
        spannableStringBuilder.append("d ").setSpan(new ColoredImageSpan(this.icon), 0, 1, 0);
        spannableStringBuilder.append(Integer.toString(i));
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
    public void onLayout(boolean z, int i, int i2, int i3, int i4) {
        float f;
        float f2;
        super.onLayout(z, i, i2, i3, i4);
        if (!this.wasAnimation && this.limitIcon != null && this.animationCanPlay && !this.premiumLocked) {
            int dp = AndroidUtilities.dp(14.0f);
            float measuredWidth = (((float) dp) + (((float) (getMeasuredWidth() - (dp * 2))) * this.position)) - (((float) this.limitIcon.getMeasuredWidth()) / 2.0f);
            if (measuredWidth > ((float) ((getMeasuredWidth() - dp) - this.limitIcon.getMeasuredWidth()))) {
                f2 = (float) ((getMeasuredWidth() - dp) - this.limitIcon.getMeasuredWidth());
                f = 1.0f;
            } else {
                f2 = measuredWidth;
                f = 0.5f;
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
            ValueAnimator ofFloat = ValueAnimator.ofFloat(new float[]{0.0f, 1.0f});
            ofFloat.addUpdateListener(new LimitPreviewView$$ExternalSyntheticLambda0(this, 0.0f, f2, 0.5f, f));
            ofFloat.setInterpolator(new OvershootInterpolator());
            ofFloat.setDuration(1000);
            ofFloat.setStartDelay(200);
            ofFloat.start();
            this.wasAnimation = true;
        } else if (this.premiumLocked) {
            int dp2 = AndroidUtilities.dp(14.0f);
            float measuredWidth2 = (((float) dp2) + (((float) (getMeasuredWidth() - (dp2 * 2))) * 0.5f)) - (((float) this.limitIcon.getMeasuredWidth()) / 2.0f);
            boolean z2 = this.wasAnimation;
            if (!z2 && this.animationCanPlay) {
                this.wasAnimation = true;
                this.limitIcon.animate().alpha(1.0f).scaleX(1.0f).scaleY(1.0f).setDuration(200).setInterpolator(new OvershootInterpolator()).start();
            } else if (!z2) {
                this.limitIcon.setAlpha(0.0f);
                this.limitIcon.setScaleX(0.0f);
                this.limitIcon.setScaleY(0.0f);
            } else {
                this.limitIcon.setAlpha(1.0f);
                this.limitIcon.setScaleX(1.0f);
                this.limitIcon.setScaleY(1.0f);
            }
            this.limitIcon.setTranslationX(measuredWidth2);
        } else {
            CounterView counterView3 = this.limitIcon;
            if (counterView3 != null) {
                counterView3.setAlpha(0.0f);
            }
        }
    }

    /* access modifiers changed from: private */
    public /* synthetic */ void lambda$onLayout$0(float f, float f2, float f3, float f4, ValueAnimator valueAnimator) {
        float floatValue = ((Float) valueAnimator.getAnimatedValue()).floatValue();
        float min = Math.min(1.0f, floatValue);
        if (floatValue > 1.0f) {
            if (!this.wasHaptic) {
                this.wasHaptic = true;
                this.limitIcon.performHapticFeedback(3);
            }
            this.limitIcon.setRotation((floatValue - 1.0f) * 60.0f);
        } else {
            this.limitIcon.setRotation(0.0f);
        }
        float f5 = 1.0f - min;
        this.limitIcon.setTranslationX((f * f5) + (f2 * min));
        float f6 = (f3 * f5) + (f4 * min);
        this.limitIcon.setArrowCenter(f6);
        float min2 = Math.min(1.0f, min * 2.0f);
        this.limitIcon.setScaleX(min2);
        this.limitIcon.setScaleY(min2);
        CounterView counterView = this.limitIcon;
        counterView.setPivotX(((float) counterView.getMeasuredWidth()) * f6);
    }

    public void setType(int i) {
        String str;
        if (i == 6) {
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

    public void setBagePosition(float f) {
        this.position = f;
    }

    public void setParentViewForGradien(ViewGroup viewGroup) {
        this.parentVideForGradient = viewGroup;
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
        public void onMeasure(int i, int i2) {
            TextPaint textPaint2 = this.textPaint;
            CharSequence charSequence = this.text;
            this.textWidth = textPaint2.measureText(charSequence, 0, charSequence.length());
            this.textLayout = new StaticLayout(this.text, this.textPaint, ((int) this.textWidth) + AndroidUtilities.dp(12.0f), Layout.Alignment.ALIGN_NORMAL, 1.0f, 0.0f, false);
            setMeasuredDimension((int) (this.textWidth + ((float) getPaddingRight()) + ((float) getPaddingLeft())), AndroidUtilities.dp(44.0f) + AndroidUtilities.dp(8.0f));
            updatePath();
        }

        private void updatePath() {
            int measuredHeight = getMeasuredHeight() - AndroidUtilities.dp(8.0f);
            float measuredWidth = ((float) getMeasuredWidth()) * this.arrowCenter;
            float clamp = Utilities.clamp(((float) AndroidUtilities.dp(8.0f)) + measuredWidth, (float) getMeasuredWidth(), 0.0f);
            float clamp2 = Utilities.clamp(((float) AndroidUtilities.dp(10.0f)) + measuredWidth, (float) getMeasuredWidth(), 0.0f);
            this.path.rewind();
            float f = (float) measuredHeight;
            float f2 = f - (f / 2.0f);
            this.path.moveTo(measuredWidth - ((float) AndroidUtilities.dp(24.0f)), f2 - ((float) AndroidUtilities.dp(2.0f)));
            this.path.lineTo(measuredWidth - ((float) AndroidUtilities.dp(24.0f)), f);
            this.path.lineTo(measuredWidth - ((float) AndroidUtilities.dp(8.0f)), f);
            this.path.lineTo(measuredWidth, (float) (measuredHeight + AndroidUtilities.dp(8.0f)));
            if (this.arrowCenter < 0.7f) {
                this.path.lineTo(clamp, f);
            }
            this.path.lineTo(clamp2, f);
            this.path.lineTo(clamp2, f2 - ((float) AndroidUtilities.dp(2.0f)));
            this.path.close();
        }

        /* access modifiers changed from: protected */
        public void onDraw(Canvas canvas) {
            int measuredHeight = getMeasuredHeight() - AndroidUtilities.dp(8.0f);
            if (LimitPreviewView.this.premiumLocked) {
                measuredHeight = getMeasuredHeight();
                PremiumGradient.getInstance().updateMainGradientMatrix(0, 0, LimitPreviewView.this.getMeasuredWidth(), LimitPreviewView.this.getMeasuredHeight(), LimitPreviewView.this.getGlobalXOffset() - getX(), (float) (-getTop()));
                RectF rectF = AndroidUtilities.rectTmp;
                rectF.set(0.0f, (float) AndroidUtilities.dp(3.0f), (float) getMeasuredWidth(), (float) (measuredHeight - AndroidUtilities.dp(3.0f)));
                float f = ((float) measuredHeight) / 2.0f;
                canvas.drawRoundRect(rectF, f, f, PremiumGradient.getInstance().getMainGradientPaint());
            } else {
                if (this.invalidatePath) {
                    this.invalidatePath = false;
                    updatePath();
                }
                PremiumGradient.getInstance().updateMainGradientMatrix(0, 0, LimitPreviewView.this.getMeasuredWidth(), LimitPreviewView.this.getMeasuredHeight(), LimitPreviewView.this.getGlobalXOffset() - getX(), (float) (-getTop()));
                RectF rectF2 = AndroidUtilities.rectTmp;
                float f2 = (float) measuredHeight;
                rectF2.set(0.0f, 0.0f, (float) getMeasuredWidth(), f2);
                float f3 = f2 / 2.0f;
                canvas.drawRoundRect(rectF2, f3, f3, PremiumGradient.getInstance().getMainGradientPaint());
                PremiumGradient.getInstance().getMainGradientPaint().setPathEffect(this.pathEffect);
                canvas.drawPath(this.path, PremiumGradient.getInstance().getMainGradientPaint());
                PremiumGradient.getInstance().getMainGradientPaint().setPathEffect((PathEffect) null);
                invalidate();
            }
            float measuredWidth = ((float) (getMeasuredWidth() - this.textLayout.getWidth())) / 2.0f;
            float height = ((float) (measuredHeight - this.textLayout.getHeight())) / 2.0f;
            if (this.animationInProgress) {
                canvas.save();
                canvas.clipRect(0, 0, getMeasuredWidth(), getMeasuredHeight() - AndroidUtilities.dp(8.0f));
                if (this.animatedStableLayout != null) {
                    canvas.save();
                    canvas.translate(measuredWidth, height);
                    this.animatedStableLayout.draw(canvas);
                    canvas.restore();
                }
                for (int i = 0; i < this.animatedLayouts.size(); i++) {
                    AnimatedLayout animatedLayout = this.animatedLayouts.get(i);
                    canvas.save();
                    if (animatedLayout.direction) {
                        canvas.translate(animatedLayout.x + measuredWidth, (height - (((float) (measuredHeight * 10)) * animatedLayout.progress)) + ((float) ((10 - animatedLayout.staticLayouts.size()) * measuredHeight)));
                        for (int i2 = 0; i2 < animatedLayout.staticLayouts.size(); i2++) {
                            canvas.translate(0.0f, (float) measuredHeight);
                            animatedLayout.staticLayouts.get(i2).draw(canvas);
                        }
                    } else {
                        canvas.translate(animatedLayout.x + measuredWidth, ((((float) (measuredHeight * 10)) * animatedLayout.progress) + height) - ((float) ((10 - animatedLayout.staticLayouts.size()) * measuredHeight)));
                        for (int i3 = 0; i3 < animatedLayout.staticLayouts.size(); i3++) {
                            canvas.translate(0.0f, (float) (-measuredHeight));
                            animatedLayout.staticLayouts.get(i3).draw(canvas);
                        }
                    }
                    canvas.restore();
                }
                canvas.restore();
            } else if (this.textLayout != null) {
                canvas.save();
                canvas.translate(measuredWidth, height);
                this.textLayout.draw(canvas);
                canvas.restore();
            }
        }

        public void setTranslationX(float f) {
            if (f != getTranslationX()) {
                super.setTranslationX(f);
                invalidate();
            }
        }

        /* access modifiers changed from: package-private */
        public void createAnimationLayouts() {
            this.animatedLayouts.clear();
            SpannableStringBuilder spannableStringBuilder = new SpannableStringBuilder(this.text);
            boolean z = true;
            int i = 0;
            for (int i2 = 0; i2 < this.text.length(); i2++) {
                if (Character.isDigit(this.text.charAt(i2))) {
                    AnimatedLayout animatedLayout = new AnimatedLayout();
                    this.animatedLayouts.add(animatedLayout);
                    animatedLayout.x = this.textLayout.getSecondaryHorizontal(i2);
                    animatedLayout.direction = z;
                    if (i >= 1) {
                        z = !z;
                        i = 0;
                    }
                    i++;
                    int charAt = this.text.charAt(i2) - '0';
                    if (charAt == 0) {
                        charAt = 10;
                    }
                    int i3 = 1;
                    while (i3 <= charAt) {
                        animatedLayout.staticLayouts.add(new StaticLayout("" + (i3 == 10 ? 0 : i3), this.textPaint, (int) this.textWidth, Layout.Alignment.ALIGN_NORMAL, 1.0f, 0.0f, false));
                        i3++;
                    }
                    spannableStringBuilder.setSpan(new EmptyStubSpan(), i2, i2 + 1, 0);
                }
            }
            this.animatedStableLayout = new StaticLayout(spannableStringBuilder, this.textPaint, AndroidUtilities.dp(12.0f) + ((int) this.textWidth), Layout.Alignment.ALIGN_NORMAL, 1.0f, 0.0f, false);
            for (int i4 = 0; i4 < this.animatedLayouts.size(); i4++) {
                this.animationInProgress = true;
                final AnimatedLayout animatedLayout2 = this.animatedLayouts.get(i4);
                ValueAnimator ofFloat = ValueAnimator.ofFloat(new float[]{0.0f, 1.0f});
                animatedLayout2.valueAnimator = ofFloat;
                ofFloat.addUpdateListener(new LimitPreviewView$CounterView$$ExternalSyntheticLambda0(this, animatedLayout2));
                animatedLayout2.valueAnimator.addListener(new AnimatorListenerAdapter() {
                    public void onAnimationEnd(Animator animator) {
                        animatedLayout2.valueAnimator = null;
                        CounterView.this.checkAnimationComplete();
                    }
                });
                animatedLayout2.valueAnimator.setInterpolator(CubicBezierInterpolator.EASE_OUT);
                animatedLayout2.valueAnimator.setDuration(750);
                animatedLayout2.valueAnimator.setStartDelay(((long) ((this.animatedLayouts.size() - 1) - i4)) * 60);
                animatedLayout2.valueAnimator.start();
            }
        }

        /* access modifiers changed from: private */
        public /* synthetic */ void lambda$createAnimationLayouts$0(AnimatedLayout animatedLayout, ValueAnimator valueAnimator) {
            animatedLayout.progress = ((Float) valueAnimator.getAnimatedValue()).floatValue();
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

        public void setText(CharSequence charSequence) {
            this.text = charSequence;
        }

        public void setArrowCenter(float f) {
            if (this.arrowCenter != f) {
                this.arrowCenter = f;
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

            private AnimatedLayout(CounterView counterView) {
                this.staticLayouts = new ArrayList<>();
            }
        }
    }
}
