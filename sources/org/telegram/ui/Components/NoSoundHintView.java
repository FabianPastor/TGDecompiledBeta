package org.telegram.ui.Components;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.content.Context;
import android.graphics.PorterDuff.Mode;
import android.graphics.PorterDuffColorFilter;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.ImageView.ScaleType;
import android.widget.TextView;
import org.telegram.messenger.AndroidUtilities;
import org.telegram.messenger.ImageReceiver;
import org.telegram.messenger.LocaleController;
import org.telegram.messenger.beta.R;
import org.telegram.ui.ActionBar.Theme;
import org.telegram.ui.Cells.ChatMessageCell;

public class NoSoundHintView extends FrameLayout {
    private AnimatorSet animatorSet;
    private ImageView arrowImageView;
    private Runnable hideRunnable;
    private ImageView imageView;
    private ChatMessageCell messageCell;
    private TextView textView;

    public NoSoundHintView(Context context) {
        super(context);
        this.textView = new CorrectlyMeasuringTextView(context);
        this.textView.setTextColor(Theme.getColor("chat_gifSaveHintText"));
        this.textView.setTextSize(1, 14.0f);
        this.textView.setMaxLines(2);
        this.textView.setMaxWidth(AndroidUtilities.dp(250.0f));
        this.textView.setGravity(51);
        this.textView.setText(LocaleController.getString("AutoplayVideoInfo", R.string.AutoplayVideoInfo));
        this.textView.setBackgroundDrawable(Theme.createRoundRectDrawable(AndroidUtilities.dp(3.0f), Theme.getColor("chat_gifSaveHintBackground")));
        this.textView.setPadding(AndroidUtilities.dp(54.0f), AndroidUtilities.dp(6.0f), AndroidUtilities.dp(5.0f), AndroidUtilities.dp(7.0f));
        addView(this.textView, LayoutHelper.createFrame(-2, -2.0f, 51, 0.0f, 0.0f, 0.0f, 6.0f));
        this.imageView = new ImageView(context);
        this.imageView.setImageResource(R.drawable.tooltip_sound);
        this.imageView.setScaleType(ScaleType.CENTER);
        this.imageView.setColorFilter(new PorterDuffColorFilter(Theme.getColor("chat_gifSaveHintText"), Mode.MULTIPLY));
        addView(this.imageView, LayoutHelper.createFrame(38, 34.0f, 51, 7.0f, 7.0f, 0.0f, 0.0f));
        this.arrowImageView = new ImageView(context);
        this.arrowImageView.setImageResource(R.drawable.tooltip_arrow);
        this.arrowImageView.setColorFilter(new PorterDuffColorFilter(Theme.getColor("chat_gifSaveHintBackground"), Mode.MULTIPLY));
        addView(this.arrowImageView, LayoutHelper.createFrame(14, 6.0f, 83, 0.0f, 0.0f, 0.0f, 0.0f));
    }

    public boolean showForMessageCell(ChatMessageCell cell) {
        if (getTag() != null) {
            return false;
        }
        ImageReceiver imageReceiver = cell.getPhotoImage();
        int top = cell.getTop() + imageReceiver.getImageY();
        int height = imageReceiver.getImageHeight();
        int bottom = top + height;
        View parentView = (View) cell.getParent();
        int parentHeight = parentView.getMeasuredHeight();
        if (top <= getMeasuredHeight() + AndroidUtilities.dp(10.0f) || bottom > (height / 4) + parentHeight) {
            return false;
        }
        int parentWidth = parentView.getMeasuredWidth();
        setTranslationY((float) (top - getMeasuredHeight()));
        int iconX = cell.getLeft() + cell.getNoSoundIconCenterX();
        int left = getLeft();
        if (iconX > parentView.getMeasuredWidth() / 2) {
            int offset = (parentWidth - getMeasuredWidth()) - AndroidUtilities.dp(38.0f);
            setTranslationX((float) offset);
            left += offset;
        } else {
            setTranslationX(0.0f);
        }
        this.arrowImageView.setTranslationX((float) (((cell.getLeft() + cell.getNoSoundIconCenterX()) - left) - (this.arrowImageView.getMeasuredWidth() / 2)));
        this.messageCell = cell;
        if (this.animatorSet != null) {
            this.animatorSet.cancel();
            this.animatorSet = null;
        }
        setTag(Integer.valueOf(1));
        setVisibility(0);
        this.animatorSet = new AnimatorSet();
        AnimatorSet animatorSet = this.animatorSet;
        Animator[] animatorArr = new Animator[1];
        r16 = new float[2];
        animatorArr[0] = ObjectAnimator.ofFloat(this, "alpha", new float[]{0.0f, 1.0f});
        animatorSet.playTogether(animatorArr);
        this.animatorSet.addListener(new AnimatorListenerAdapter() {
            public void onAnimationEnd(Animator animation) {
                NoSoundHintView.this.animatorSet = null;
                AndroidUtilities.runOnUIThread(NoSoundHintView.this.hideRunnable = new NoSoundHintView$1$$Lambda$0(this), 10000);
            }

            final /* synthetic */ void lambda$onAnimationEnd$0$NoSoundHintView$1() {
                NoSoundHintView.this.hide();
            }
        });
        this.animatorSet.setDuration(300);
        this.animatorSet.start();
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
                    NoSoundHintView.this.setVisibility(4);
                    NoSoundHintView.this.animatorSet = null;
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
