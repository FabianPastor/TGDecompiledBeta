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

    public HintView(Context context, int i) {
        this(context, i, false);
    }

    public HintView(Context context, int i, boolean z) {
        Context context2 = context;
        int i2 = i;
        boolean z2 = z;
        super(context);
        this.currentType = i2;
        this.isTopArrow = z2;
        this.textView = new CorrectlyMeasuringTextView(context2);
        String str = "chat_gifSaveHintText";
        this.textView.setTextColor(Theme.getColor(str));
        this.textView.setTextSize(1, 14.0f);
        this.textView.setMaxLines(2);
        this.textView.setMaxWidth(AndroidUtilities.dp(250.0f));
        this.textView.setGravity(51);
        String str2 = "chat_gifSaveHintBackground";
        this.textView.setBackgroundDrawable(Theme.createRoundRectDrawable(AndroidUtilities.dp(3.0f), Theme.getColor(str2)));
        this.textView.setPadding(AndroidUtilities.dp(this.currentType == 0 ? 54.0f : 5.0f), AndroidUtilities.dp(6.0f), AndroidUtilities.dp(5.0f), AndroidUtilities.dp(7.0f));
        addView(this.textView, LayoutHelper.createFrame(-2, -2.0f, 51, 0.0f, z2 ? 6.0f : 0.0f, 0.0f, z2 ? 0.0f : 6.0f));
        if (i2 == 0) {
            this.textView.setText(LocaleController.getString("AutoplayVideoInfo", NUM));
            this.imageView = new ImageView(context2);
            this.imageView.setImageResource(NUM);
            this.imageView.setScaleType(ScaleType.CENTER);
            this.imageView.setColorFilter(new PorterDuffColorFilter(Theme.getColor(str), Mode.MULTIPLY));
            addView(this.imageView, LayoutHelper.createFrame(38, 34.0f, 51, 7.0f, 7.0f, 0.0f, 0.0f));
        }
        this.arrowImageView = new ImageView(context2);
        this.arrowImageView.setImageResource(z2 ? NUM : NUM);
        this.arrowImageView.setColorFilter(new PorterDuffColorFilter(Theme.getColor(str2), Mode.MULTIPLY));
        addView(this.arrowImageView, LayoutHelper.createFrame(14, 6.0f, (z2 ? 48 : 80) | 3, 0.0f, 0.0f, 0.0f, 0.0f));
    }

    public void setOverrideText(String str) {
        this.overrideText = str;
        this.textView.setText(str);
        ChatMessageCell chatMessageCell = this.messageCell;
        if (chatMessageCell != null) {
            this.messageCell = null;
            showForMessageCell(chatMessageCell, false);
        }
    }

    public boolean showForMessageCell(ChatMessageCell chatMessageCell, boolean z) {
        if ((this.currentType == 0 && getTag() != null) || this.messageCell == chatMessageCell) {
            return false;
        }
        int imageHeight;
        int i;
        int measuredHeight;
        Runnable runnable = this.hideRunnable;
        if (runnable != null) {
            AndroidUtilities.cancelRunOnUIThread(runnable);
            this.hideRunnable = null;
        }
        int top = chatMessageCell.getTop();
        View view = (View) chatMessageCell.getParent();
        if (this.currentType == 0) {
            ImageReceiver photoImage = chatMessageCell.getPhotoImage();
            top += photoImage.getImageY();
            imageHeight = photoImage.getImageHeight();
            i = top + imageHeight;
            measuredHeight = view.getMeasuredHeight();
            if (top <= getMeasuredHeight() + AndroidUtilities.dp(10.0f) || i > measuredHeight + (imageHeight / 4)) {
                return false;
            }
            imageHeight = chatMessageCell.getNoSoundIconCenterX();
        } else {
            MessageObject messageObject = chatMessageCell.getMessageObject();
            String str = this.overrideText;
            if (str == null) {
                this.textView.setText(LocaleController.getString("HidAccount", NUM));
            } else {
                this.textView.setText(str);
            }
            measure(MeasureSpec.makeMeasureSpec(1000, Integer.MIN_VALUE), MeasureSpec.makeMeasureSpec(1000, Integer.MIN_VALUE));
            top += AndroidUtilities.dp(22.0f);
            if (!messageObject.isOutOwner() && chatMessageCell.isDrawNameLayout()) {
                top += AndroidUtilities.dp(20.0f);
            }
            if (!this.isTopArrow && r0 <= getMeasuredHeight() + AndroidUtilities.dp(10.0f)) {
                return false;
            }
            imageHeight = chatMessageCell.getForwardNameCenterX();
        }
        i = view.getMeasuredWidth();
        if (this.isTopArrow) {
            setTranslationY((float) AndroidUtilities.dp(44.0f));
        } else {
            setTranslationY((float) (top - getMeasuredHeight()));
        }
        top = chatMessageCell.getLeft() + imageHeight;
        measuredHeight = AndroidUtilities.dp(19.0f);
        if (top > view.getMeasuredWidth() / 2) {
            i = (i - getMeasuredWidth()) - AndroidUtilities.dp(38.0f);
            setTranslationX((float) i);
            measuredHeight += i;
        } else {
            setTranslationX(0.0f);
        }
        float left = (float) (((chatMessageCell.getLeft() + imageHeight) - measuredHeight) - (this.arrowImageView.getMeasuredWidth() / 2));
        this.arrowImageView.setTranslationX(left);
        float dp;
        if (top > view.getMeasuredWidth() / 2) {
            if (left < ((float) AndroidUtilities.dp(10.0f))) {
                dp = left - ((float) AndroidUtilities.dp(10.0f));
                setTranslationX(getTranslationX() + dp);
                this.arrowImageView.setTranslationX(left - dp);
            }
        } else if (left > ((float) (getMeasuredWidth() - AndroidUtilities.dp(24.0f)))) {
            dp = (left - ((float) getMeasuredWidth())) + ((float) AndroidUtilities.dp(24.0f));
            setTranslationX(dp);
            this.arrowImageView.setTranslationX(left - dp);
        } else if (left < ((float) AndroidUtilities.dp(10.0f))) {
            dp = left - ((float) AndroidUtilities.dp(10.0f));
            setTranslationX(getTranslationX() + dp);
            this.arrowImageView.setTranslationX(left - dp);
        }
        this.messageCell = chatMessageCell;
        AnimatorSet animatorSet = this.animatorSet;
        if (animatorSet != null) {
            animatorSet.cancel();
            this.animatorSet = null;
        }
        setTag(Integer.valueOf(1));
        setVisibility(0);
        if (z) {
            this.animatorSet = new AnimatorSet();
            AnimatorSet animatorSet2 = this.animatorSet;
            Animator[] animatorArr = new Animator[1];
            animatorArr[0] = ObjectAnimator.ofFloat(this, "alpha", new float[]{0.0f, 1.0f});
            animatorSet2.playTogether(animatorArr);
            this.animatorSet.addListener(new AnimatorListenerAdapter() {
                public void onAnimationEnd(Animator animator) {
                    HintView.this.animatorSet = null;
                    HintView hintView = HintView.this;
                    -$$Lambda$HintView$1$Oo-YArBkq6553J0682j2MQqGlbY -__lambda_hintview_1_oo-yarbkq6553j0682j2mqqglby = new -$$Lambda$HintView$1$Oo-YArBkq6553J0682j2MQqGlbY(this);
                    hintView.hideRunnable = -__lambda_hintview_1_oo-yarbkq6553j0682j2mqqglby;
                    AndroidUtilities.runOnUIThread(-__lambda_hintview_1_oo-yarbkq6553j0682j2mqqglby, HintView.this.currentType == 0 ? 10000 : 2000);
                }

                public /* synthetic */ void lambda$onAnimationEnd$0$HintView$1() {
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
            Runnable runnable = this.hideRunnable;
            if (runnable != null) {
                AndroidUtilities.cancelRunOnUIThread(runnable);
                this.hideRunnable = null;
            }
            AnimatorSet animatorSet = this.animatorSet;
            if (animatorSet != null) {
                animatorSet.cancel();
                this.animatorSet = null;
            }
            this.animatorSet = new AnimatorSet();
            AnimatorSet animatorSet2 = this.animatorSet;
            Animator[] animatorArr = new Animator[1];
            animatorArr[0] = ObjectAnimator.ofFloat(this, "alpha", new float[]{0.0f});
            animatorSet2.playTogether(animatorArr);
            this.animatorSet.addListener(new AnimatorListenerAdapter() {
                public void onAnimationEnd(Animator animator) {
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
