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
import org.telegram.ui.Components.ChecksHintView;

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
    private long showingDuration = 2000;
    /* access modifiers changed from: private */
    public TextView[] textView = new TextView[2];
    private float translationY;

    public ChecksHintView(Context context) {
        super(context);
        FrameLayout frameLayout = new FrameLayout(context);
        frameLayout.setBackground(Theme.createRoundRectDrawable(AndroidUtilities.dp(6.0f), Theme.getColor("chat_gifSaveHintBackground")));
        int i = 0;
        frameLayout.setPadding(AndroidUtilities.dp(8.0f), AndroidUtilities.dp(8.0f), 0, AndroidUtilities.dp(8.0f));
        addView(frameLayout, LayoutHelper.createFrame(-2, -2.0f, 51, 0.0f, 0.0f, 0.0f, 6.0f));
        while (i < 2) {
            this.imageView[i] = new RLottieImageView(context);
            this.imageView[i].setScaleType(ImageView.ScaleType.CENTER);
            frameLayout.addView(this.imageView[i], LayoutHelper.createFrame(24, 24.0f, 51, 0.0f, i == 0 ? 0.0f : 24.0f, 0.0f, 0.0f));
            this.textView[i] = new TextView(context);
            this.textView[i].setTextColor(Theme.getColor("chat_gifSaveHintText"));
            this.textView[i].setTextSize(1, 14.0f);
            this.textView[i].setMaxLines(1);
            this.textView[i].setSingleLine(true);
            this.textView[i].setMaxWidth(AndroidUtilities.dp(250.0f));
            this.textView[i].setGravity(51);
            this.textView[i].setPivotX(0.0f);
            frameLayout.addView(this.textView[i], LayoutHelper.createFrame(-2, -2.0f, 51, 32.0f, i == 0 ? 2.0f : 26.0f, 10.0f, 0.0f));
            if (i == 0) {
                this.imageView[i].setAnimation(NUM, 24, 24);
                this.textView[i].setText(LocaleController.getString("HintSent", NUM));
            } else {
                this.imageView[i].setAnimation(NUM, 24, 24);
                this.textView[i].setText(LocaleController.getString("HintRead", NUM));
            }
            this.imageView[i].playAnimation();
            i++;
        }
        ImageView imageView2 = new ImageView(context);
        this.arrowImageView = imageView2;
        imageView2.setImageResource(NUM);
        this.arrowImageView.setColorFilter(new PorterDuffColorFilter(Theme.getColor("chat_gifSaveHintBackground"), PorterDuff.Mode.MULTIPLY));
        addView(this.arrowImageView, LayoutHelper.createFrame(14, 6.0f, 83, 0.0f, 0.0f, 0.0f, 0.0f));
    }

    public float getBaseTranslationY() {
        return this.translationY;
    }

    public boolean showForMessageCell(ChatMessageCell chatMessageCell, boolean z) {
        Runnable runnable = this.hideRunnable;
        if (runnable != null) {
            AndroidUtilities.cancelRunOnUIThread(runnable);
            this.hideRunnable = null;
        }
        int[] iArr = new int[2];
        chatMessageCell.getLocationInWindow(iArr);
        int i = iArr[1];
        ((View) getParent()).getLocationInWindow(iArr);
        int i2 = i - iArr[1];
        View view = (View) chatMessageCell.getParent();
        measure(View.MeasureSpec.makeMeasureSpec(1000, Integer.MIN_VALUE), View.MeasureSpec.makeMeasureSpec(1000, Integer.MIN_VALUE));
        final int i3 = 0;
        if (i2 <= getMeasuredHeight() + AndroidUtilities.dp(10.0f)) {
            return false;
        }
        int checksY = i2 + chatMessageCell.getChecksY() + AndroidUtilities.dp(6.0f);
        int checksX = chatMessageCell.getChecksX() + AndroidUtilities.dp(5.0f);
        int measuredWidth = view.getMeasuredWidth();
        float measuredHeight = (float) (checksY - getMeasuredHeight());
        this.translationY = measuredHeight;
        setTranslationY(measuredHeight);
        int left = chatMessageCell.getLeft() + checksX;
        int dp = AndroidUtilities.dp(15.0f);
        if (left > view.getMeasuredWidth() / 2) {
            int measuredWidth2 = (measuredWidth - getMeasuredWidth()) - AndroidUtilities.dp(20.0f);
            setTranslationX((float) measuredWidth2);
            dp += measuredWidth2;
        } else {
            setTranslationX(0.0f);
        }
        float left2 = (float) (((chatMessageCell.getLeft() + checksX) - dp) - (this.arrowImageView.getMeasuredWidth() / 2));
        this.arrowImageView.setTranslationX(left2);
        if (left > view.getMeasuredWidth() / 2) {
            if (left2 < ((float) AndroidUtilities.dp(10.0f))) {
                float dp2 = left2 - ((float) AndroidUtilities.dp(10.0f));
                setTranslationX(getTranslationX() + dp2);
                this.arrowImageView.setTranslationX(left2 - dp2);
            }
        } else if (left2 > ((float) (getMeasuredWidth() - AndroidUtilities.dp(24.0f)))) {
            float measuredWidth3 = (left2 - ((float) getMeasuredWidth())) + ((float) AndroidUtilities.dp(24.0f));
            setTranslationX(measuredWidth3);
            this.arrowImageView.setTranslationX(left2 - measuredWidth3);
        } else if (left2 < ((float) AndroidUtilities.dp(10.0f))) {
            float dp3 = left2 - ((float) AndroidUtilities.dp(10.0f));
            setTranslationX(getTranslationX() + dp3);
            this.arrowImageView.setTranslationX(left2 - dp3);
        }
        setPivotX(left2);
        setPivotY((float) getMeasuredHeight());
        this.messageCell = chatMessageCell;
        AnimatorSet animatorSet2 = this.animatorSet;
        if (animatorSet2 != null) {
            animatorSet2.cancel();
            this.animatorSet = null;
        }
        setTag(1);
        setVisibility(0);
        if (z) {
            AnimatorSet animatorSet3 = new AnimatorSet();
            this.animatorSet = animatorSet3;
            animatorSet3.playTogether(new Animator[]{ObjectAnimator.ofFloat(this, View.ALPHA, new float[]{0.0f, 1.0f}), ObjectAnimator.ofFloat(this, View.SCALE_X, new float[]{0.0f, 1.0f}), ObjectAnimator.ofFloat(this, View.SCALE_Y, new float[]{0.0f, 1.0f})});
            this.animatorSet.addListener(new AnimatorListenerAdapter() {
                public void onAnimationEnd(Animator animator) {
                    AnimatorSet unused = ChecksHintView.this.animatorSet = null;
                    AndroidUtilities.runOnUIThread(ChecksHintView.this.hideRunnable = new Runnable() {
                        public final void run() {
                            ChecksHintView.AnonymousClass1.this.lambda$onAnimationEnd$0$ChecksHintView$1();
                        }
                    }, 3000);
                }

                /* access modifiers changed from: private */
                /* renamed from: lambda$onAnimationEnd$0 */
                public /* synthetic */ void lambda$onAnimationEnd$0$ChecksHintView$1() {
                    ChecksHintView.this.hide();
                }
            });
            this.animatorSet.setDuration(180);
            this.animatorSet.start();
            while (i3 < 2) {
                this.textView[i3].animate().scaleX(1.04f).scaleY(1.04f).setInterpolator(CubicBezierInterpolator.EASE_IN).setStartDelay((long) ((i3 == 0 ? 132 : 500) + 140)).setDuration(100).setListener(new AnimatorListenerAdapter() {
                    public void onAnimationEnd(Animator animator) {
                        ChecksHintView.this.textView[i3].animate().scaleX(1.0f).scaleY(1.0f).setInterpolator(CubicBezierInterpolator.EASE_OUT).setStartDelay(0).setDuration(100).start();
                    }
                }).start();
                i3++;
            }
        } else {
            setAlpha(1.0f);
        }
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
                public void onAnimationEnd(Animator animator) {
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
}
