package org.telegram.ui.Components;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.content.Context;
import android.graphics.PorterDuff.Mode;
import android.graphics.PorterDuffColorFilter;
import android.view.View;
import android.view.View.MeasureSpec;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.ImageView.ScaleType;
import android.widget.TextView;
import org.telegram.messenger.AndroidUtilities;
import org.telegram.messenger.ImageReceiver;
import org.telegram.messenger.LocaleController;
import org.telegram.messenger.MessageObject;
import org.telegram.ui.ActionBar.Theme;
import org.telegram.ui.Cells.ChatMessageCell;

public class HintView extends FrameLayout {
    private AnimatorSet animatorSet;
    private ImageView arrowImageView;
    private int currentType;
    private Runnable hideRunnable;
    private ImageView imageView;
    private boolean isTopArrow;
    private ChatMessageCell messageCell;
    private String overrideText;
    private TextView textView;

    public HintView(Context context, int type) {
        this(context, type, false);
    }

    public HintView(Context context, int type, boolean topArrow) {
        float f;
        float f2;
        super(context);
        this.currentType = type;
        this.isTopArrow = topArrow;
        this.textView = new CorrectlyMeasuringTextView(context);
        this.textView.setTextColor(Theme.getColor("chat_gifSaveHintText"));
        this.textView.setTextSize(1, 14.0f);
        this.textView.setMaxLines(2);
        this.textView.setMaxWidth(AndroidUtilities.dp(250.0f));
        this.textView.setGravity(51);
        this.textView.setBackgroundDrawable(Theme.createRoundRectDrawable(AndroidUtilities.dp(3.0f), Theme.getColor("chat_gifSaveHintBackground")));
        this.textView.setPadding(AndroidUtilities.dp(this.currentType == 0 ? 54.0f : 5.0f), AndroidUtilities.dp(6.0f), AndroidUtilities.dp(5.0f), AndroidUtilities.dp(7.0f));
        TextView textView = this.textView;
        if (topArrow) {
            f = 6.0f;
        } else {
            f = 0.0f;
        }
        if (topArrow) {
            f2 = 0.0f;
        } else {
            f2 = 6.0f;
        }
        addView(textView, LayoutHelper.createFrame(-2, -2.0f, 51, 0.0f, f, 0.0f, f2));
        if (type == 0) {
            this.textView.setText(LocaleController.getString("AutoplayVideoInfo", NUM));
            this.imageView = new ImageView(context);
            this.imageView.setImageResource(NUM);
            this.imageView.setScaleType(ScaleType.CENTER);
            this.imageView.setColorFilter(new PorterDuffColorFilter(Theme.getColor("chat_gifSaveHintText"), Mode.MULTIPLY));
            addView(this.imageView, LayoutHelper.createFrame(38, 34.0f, 51, 7.0f, 7.0f, 0.0f, 0.0f));
        }
        this.arrowImageView = new ImageView(context);
        this.arrowImageView.setImageResource(topArrow ? NUM : NUM);
        this.arrowImageView.setColorFilter(new PorterDuffColorFilter(Theme.getColor("chat_gifSaveHintBackground"), Mode.MULTIPLY));
        addView(this.arrowImageView, LayoutHelper.createFrame(14, 6.0f, (topArrow ? 48 : 80) | 3, 0.0f, 0.0f, 0.0f, 0.0f));
    }

    public void setOverrideText(String text) {
        this.overrideText = text;
        this.textView.setText(text);
        if (this.messageCell != null) {
            ChatMessageCell cell = this.messageCell;
            this.messageCell = null;
            showForMessageCell(cell, false);
        }
    }

    public boolean showForMessageCell(ChatMessageCell cell, boolean animated) {
        if ((this.currentType == 0 && getTag() != null) || this.messageCell == cell) {
            return false;
        }
        int centerX;
        if (this.hideRunnable != null) {
            AndroidUtilities.cancelRunOnUIThread(this.hideRunnable);
            this.hideRunnable = null;
        }
        int top = cell.getTop();
        View parentView = (View) cell.getParent();
        if (this.currentType == 0) {
            ImageReceiver imageReceiver = cell.getPhotoImage();
            top += imageReceiver.getImageY();
            int height = imageReceiver.getImageHeight();
            int bottom = top + height;
            int parentHeight = parentView.getMeasuredHeight();
            if (top <= getMeasuredHeight() + AndroidUtilities.dp(10.0f) || bottom > (height / 4) + parentHeight) {
                return false;
            }
            centerX = cell.getNoSoundIconCenterX();
        } else {
            MessageObject messageObject = cell.getMessageObject();
            if (this.overrideText == null) {
                this.textView.setText(LocaleController.getString("HidAccount", NUM));
            } else {
                this.textView.setText(this.overrideText);
            }
            measure(MeasureSpec.makeMeasureSpec(1000, Integer.MIN_VALUE), MeasureSpec.makeMeasureSpec(1000, Integer.MIN_VALUE));
            top += AndroidUtilities.dp(22.0f);
            if (!this.isTopArrow && top <= getMeasuredHeight() + AndroidUtilities.dp(10.0f)) {
                return false;
            }
            centerX = cell.getForwardNameCenterX();
        }
        int parentWidth = parentView.getMeasuredWidth();
        if (this.isTopArrow) {
            setTranslationY((float) AndroidUtilities.dp(44.0f));
        } else {
            setTranslationY((float) (top - getMeasuredHeight()));
        }
        int iconX = cell.getLeft() + centerX;
        int left = AndroidUtilities.dp(19.0f);
        if (iconX > parentView.getMeasuredWidth() / 2) {
            int offset = (parentWidth - getMeasuredWidth()) - AndroidUtilities.dp(38.0f);
            setTranslationX((float) offset);
            left += offset;
        } else {
            setTranslationX(0.0f);
        }
        float arrowX = (float) (((cell.getLeft() + centerX) - left) - (this.arrowImageView.getMeasuredWidth() / 2));
        this.arrowImageView.setTranslationX(arrowX);
        float diff;
        if (iconX > parentView.getMeasuredWidth() / 2) {
            if (arrowX < ((float) AndroidUtilities.dp(10.0f))) {
                diff = arrowX - ((float) AndroidUtilities.dp(10.0f));
                setTranslationX(getTranslationX() + diff);
                this.arrowImageView.setTranslationX(arrowX - diff);
            }
        } else if (arrowX > ((float) (getMeasuredWidth() - AndroidUtilities.dp(24.0f)))) {
            diff = (arrowX - ((float) getMeasuredWidth())) + ((float) AndroidUtilities.dp(24.0f));
            setTranslationX(diff);
            this.arrowImageView.setTranslationX(arrowX - diff);
        } else if (arrowX < ((float) AndroidUtilities.dp(10.0f))) {
            diff = arrowX - ((float) AndroidUtilities.dp(10.0f));
            setTranslationX(getTranslationX() + diff);
            this.arrowImageView.setTranslationX(arrowX - diff);
        }
        this.messageCell = cell;
        if (this.animatorSet != null) {
            this.animatorSet.cancel();
            this.animatorSet = null;
        }
        setTag(Integer.valueOf(1));
        setVisibility(0);
        if (animated) {
            this.animatorSet = new AnimatorSet();
            AnimatorSet animatorSet = this.animatorSet;
            Animator[] animatorArr = new Animator[1];
            float[] fArr = new float[2];
            animatorArr[0] = ObjectAnimator.ofFloat(this, "alpha", new float[]{0.0f, 1.0f});
            animatorSet.playTogether(animatorArr);
            this.animatorSet.addListener(new AnimatorListenerAdapter() {
                public void onAnimationEnd(Animator animation) {
                    HintView.this.animatorSet = null;
                    AndroidUtilities.runOnUIThread(HintView.this.hideRunnable = new HintView$1$$Lambda$0(this), HintView.this.currentType == 0 ? 10000 : 2000);
                }

                /* Access modifiers changed, original: final|synthetic */
                public final /* synthetic */ void lambda$onAnimationEnd$0$HintView$1() {
                    HintView.this.hide();
                }
            });
            this.animatorSet.setDuration(300);
            this.animatorSet.start();
        } else {
            setAlpha(1.0f);
        }
        return true;
    }

    public void hide() {
        if (getTag() != null) {
            setTag(null);
            if (this.hideRunnable != null) {
                AndroidUtilities.cancelRunOnUIThread(this.hideRunnable);
                this.hideRunnable = null;
            }
            if (this.animatorSet != null) {
                this.animatorSet.cancel();
                this.animatorSet = null;
            }
            this.animatorSet = new AnimatorSet();
            AnimatorSet animatorSet = this.animatorSet;
            Animator[] animatorArr = new Animator[1];
            animatorArr[0] = ObjectAnimator.ofFloat(this, "alpha", new float[]{0.0f});
            animatorSet.playTogether(animatorArr);
            this.animatorSet.addListener(new AnimatorListenerAdapter() {
                public void onAnimationEnd(Animator animation) {
                    HintView.this.setVisibility(4);
                    HintView.this.messageCell = null;
                    HintView.this.animatorSet = null;
                }
            });
            this.animatorSet.setDuration(300);
            this.animatorSet.start();
        }
    }

    public ChatMessageCell getMessageCell() {
        return this.messageCell;
    }
}
