package org.telegram.ui.Components;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.content.Context;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffColorFilter;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;
import org.telegram.messenger.AndroidUtilities;
import org.telegram.messenger.LocaleController;
import org.telegram.ui.ActionBar.Theme;
import org.telegram.ui.Cells.ChatMessageCell;

public class ChecksHintView extends FrameLayout {
    /* access modifiers changed from: private */
    public AnimatorSet animatorSet;
    private ImageView arrowImageView;
    /* access modifiers changed from: private */
    public View currentView;
    /* access modifiers changed from: private */
    public Runnable hideRunnable;
    private RLottieImageView[] imageView = new RLottieImageView[2];
    /* access modifiers changed from: private */
    public ChatMessageCell messageCell;
    private final Theme.ResourcesProvider resourcesProvider;
    private long showingDuration = 2000;
    /* access modifiers changed from: private */
    public TextView[] textView = new TextView[2];
    private float translationY;

    /* JADX INFO: super call moved to the top of the method (can break code semantics) */
    public ChecksHintView(Context context, Theme.ResourcesProvider resourcesProvider2) {
        super(context);
        Context context2 = context;
        this.resourcesProvider = resourcesProvider2;
        FrameLayout backgroundView = new FrameLayout(context2);
        backgroundView.setBackground(Theme.createRoundRectDrawable(AndroidUtilities.dp(6.0f), getThemedColor("chat_gifSaveHintBackground")));
        backgroundView.setPadding(AndroidUtilities.dp(8.0f), AndroidUtilities.dp(8.0f), 0, AndroidUtilities.dp(8.0f));
        addView(backgroundView, LayoutHelper.createFrame(-2, -2.0f, 51, 0.0f, 0.0f, 0.0f, 6.0f));
        int a = 0;
        while (a < 2) {
            this.imageView[a] = new RLottieImageView(context2);
            this.imageView[a].setScaleType(ImageView.ScaleType.CENTER);
            backgroundView.addView(this.imageView[a], LayoutHelper.createFrame(24, 24.0f, 51, 0.0f, a == 0 ? 0.0f : 24.0f, 0.0f, 0.0f));
            this.textView[a] = new TextView(context2);
            this.textView[a].setTextColor(getThemedColor("chat_gifSaveHintText"));
            this.textView[a].setTextSize(1, 14.0f);
            this.textView[a].setMaxLines(1);
            this.textView[a].setSingleLine(true);
            this.textView[a].setMaxWidth(AndroidUtilities.dp(250.0f));
            this.textView[a].setGravity(51);
            this.textView[a].setPivotX(0.0f);
            backgroundView.addView(this.textView[a], LayoutHelper.createFrame(-2, -2.0f, 51, 32.0f, a == 0 ? 2.0f : 26.0f, 10.0f, 0.0f));
            if (a == 0) {
                this.imageView[a].setAnimation(NUM, 24, 24);
                this.textView[a].setText(LocaleController.getString("HintSent", NUM));
            } else {
                this.imageView[a].setAnimation(NUM, 24, 24);
                this.textView[a].setText(LocaleController.getString("HintRead", NUM));
            }
            this.imageView[a].playAnimation();
            a++;
        }
        ImageView imageView2 = new ImageView(context2);
        this.arrowImageView = imageView2;
        imageView2.setImageResource(NUM);
        this.arrowImageView.setColorFilter(new PorterDuffColorFilter(getThemedColor("chat_gifSaveHintBackground"), PorterDuff.Mode.MULTIPLY));
        addView(this.arrowImageView, LayoutHelper.createFrame(14, 6.0f, 83, 0.0f, 0.0f, 0.0f, 0.0f));
    }

    public float getBaseTranslationY() {
        return this.translationY;
    }

    public boolean showForMessageCell(ChatMessageCell cell, boolean animated) {
        ChatMessageCell chatMessageCell = cell;
        Runnable runnable = this.hideRunnable;
        if (runnable != null) {
            AndroidUtilities.cancelRunOnUIThread(runnable);
            this.hideRunnable = null;
        }
        int[] position = new int[2];
        chatMessageCell.getLocationInWindow(position);
        int top = position[1];
        ((View) getParent()).getLocationInWindow(position);
        int top2 = top - position[1];
        View parentView = (View) cell.getParent();
        measure(View.MeasureSpec.makeMeasureSpec(1000, Integer.MIN_VALUE), View.MeasureSpec.makeMeasureSpec(1000, Integer.MIN_VALUE));
        if (top2 <= getMeasuredHeight() + AndroidUtilities.dp(10.0f)) {
            return false;
        }
        int top3 = top2 + cell.getChecksY() + AndroidUtilities.dp(6.0f);
        int centerX = cell.getChecksX() + AndroidUtilities.dp(5.0f);
        int parentWidth = parentView.getMeasuredWidth();
        float measuredHeight = (float) (top3 - getMeasuredHeight());
        this.translationY = measuredHeight;
        setTranslationY(measuredHeight);
        int iconX = cell.getLeft() + centerX;
        int left = AndroidUtilities.dp(15.0f);
        if (iconX > parentView.getMeasuredWidth() / 2) {
            int offset = (parentWidth - getMeasuredWidth()) - AndroidUtilities.dp(20.0f);
            setTranslationX((float) offset);
            left += offset;
        } else {
            setTranslationX(0.0f);
        }
        float arrowX = (float) (((cell.getLeft() + centerX) - left) - (this.arrowImageView.getMeasuredWidth() / 2));
        this.arrowImageView.setTranslationX(arrowX);
        if (iconX > parentView.getMeasuredWidth() / 2) {
            if (arrowX < ((float) AndroidUtilities.dp(10.0f))) {
                float diff = arrowX - ((float) AndroidUtilities.dp(10.0f));
                setTranslationX(getTranslationX() + diff);
                this.arrowImageView.setTranslationX(arrowX - diff);
            }
        } else if (arrowX > ((float) (getMeasuredWidth() - AndroidUtilities.dp(24.0f)))) {
            float diff2 = (arrowX - ((float) getMeasuredWidth())) + ((float) AndroidUtilities.dp(24.0f));
            setTranslationX(diff2);
            this.arrowImageView.setTranslationX(arrowX - diff2);
        } else if (arrowX < ((float) AndroidUtilities.dp(10.0f))) {
            float diff3 = arrowX - ((float) AndroidUtilities.dp(10.0f));
            setTranslationX(getTranslationX() + diff3);
            this.arrowImageView.setTranslationX(arrowX - diff3);
        }
        setPivotX(arrowX);
        setPivotY((float) getMeasuredHeight());
        this.messageCell = chatMessageCell;
        AnimatorSet animatorSet2 = this.animatorSet;
        if (animatorSet2 != null) {
            animatorSet2.cancel();
            this.animatorSet = null;
        }
        setTag(1);
        setVisibility(0);
        if (animated) {
            AnimatorSet animatorSet3 = new AnimatorSet();
            this.animatorSet = animatorSet3;
            animatorSet3.playTogether(new Animator[]{ObjectAnimator.ofFloat(this, View.ALPHA, new float[]{0.0f, 1.0f}), ObjectAnimator.ofFloat(this, View.SCALE_X, new float[]{0.0f, 1.0f}), ObjectAnimator.ofFloat(this, View.SCALE_Y, new float[]{0.0f, 1.0f})});
            this.animatorSet.addListener(new AnimatorListenerAdapter() {
                public void onAnimationEnd(Animator animation) {
                    AnimatorSet unused = ChecksHintView.this.animatorSet = null;
                    AndroidUtilities.runOnUIThread(ChecksHintView.this.hideRunnable = new ChecksHintView$1$$ExternalSyntheticLambda0(this), 3000);
                }

                /* renamed from: lambda$onAnimationEnd$0$org-telegram-ui-Components-ChecksHintView$1  reason: not valid java name */
                public /* synthetic */ void m3920x9d80fCLASSNAME() {
                    ChecksHintView.this.hide();
                }
            });
            int[] position2 = position;
            this.animatorSet.setDuration(180);
            this.animatorSet.start();
            int a = 0;
            while (a < 2) {
                int num = a;
                int[] position3 = position2;
                final int num2 = num;
                this.textView[a].animate().scaleX(1.04f).scaleY(1.04f).setInterpolator(CubicBezierInterpolator.EASE_IN).setStartDelay((long) ((a == 0 ? 132 : 500) + 140)).setDuration(100).setListener(new AnimatorListenerAdapter() {
                    public void onAnimationEnd(Animator animation) {
                        ChecksHintView.this.textView[num2].animate().scaleX(1.0f).scaleY(1.0f).setInterpolator(CubicBezierInterpolator.EASE_OUT).setStartDelay(0).setDuration(100).start();
                    }
                }).start();
                a++;
                position2 = position3;
            }
            return true;
        }
        setAlpha(1.0f);
        return true;
    }

    public void hide() {
        if (getTag() != null) {
            setTag((Object) null);
            Runnable runnable = this.hideRunnable;
            if (runnable != null) {
                AndroidUtilities.cancelRunOnUIThread(runnable);
                this.hideRunnable = null;
            }
            AnimatorSet animatorSet2 = this.animatorSet;
            if (animatorSet2 != null) {
                animatorSet2.cancel();
                this.animatorSet = null;
            }
            AnimatorSet animatorSet3 = new AnimatorSet();
            this.animatorSet = animatorSet3;
            animatorSet3.playTogether(new Animator[]{ObjectAnimator.ofFloat(this, View.ALPHA, new float[]{0.0f}), ObjectAnimator.ofFloat(this, View.SCALE_X, new float[]{0.0f}), ObjectAnimator.ofFloat(this, View.SCALE_Y, new float[]{0.0f})});
            this.animatorSet.addListener(new AnimatorListenerAdapter() {
                public void onAnimationEnd(Animator animation) {
                    ChecksHintView.this.setVisibility(4);
                    View unused = ChecksHintView.this.currentView = null;
                    ChatMessageCell unused2 = ChecksHintView.this.messageCell = null;
                    AnimatorSet unused3 = ChecksHintView.this.animatorSet = null;
                }
            });
            this.animatorSet.setDuration(180);
            this.animatorSet.start();
        }
    }

    private int getThemedColor(String key) {
        Theme.ResourcesProvider resourcesProvider2 = this.resourcesProvider;
        Integer color = resourcesProvider2 != null ? resourcesProvider2.getColor(key) : null;
        return color != null ? color.intValue() : Theme.getColor(key);
    }
}
