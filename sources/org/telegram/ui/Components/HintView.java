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
import android.view.ViewGroup;
import android.view.ViewGroup.MarginLayoutParams;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.ImageView.ScaleType;
import android.widget.TextView;
import org.telegram.messenger.AndroidUtilities;
import org.telegram.messenger.ImageReceiver;
import org.telegram.messenger.LocaleController;
import org.telegram.messenger.MessageObject;
import org.telegram.ui.ActionBar.SimpleTextView;
import org.telegram.ui.ActionBar.Theme;
import org.telegram.ui.Cells.ChatMessageCell;

public class HintView extends FrameLayout {
    public static final int TYPE_POLL_VOTE = 5;
    public static final int TYPE_SEARCH_AS_LIST = 3;
    private AnimatorSet animatorSet;
    private ImageView arrowImageView;
    private int currentType;
    private View currentView;
    private Runnable hideRunnable;
    private ImageView imageView;
    private boolean isTopArrow;
    private ChatMessageCell messageCell;
    private String overrideText;
    private long showingDuration;
    private int shownY;
    private TextView textView;

    public HintView(Context context, int i) {
        this(context, i, false);
    }

    public HintView(Context context, int i, boolean z) {
        Context context2 = context;
        int i2 = i;
        boolean z2 = z;
        super(context);
        this.showingDuration = 2000;
        this.currentType = i2;
        this.isTopArrow = z2;
        this.textView = new CorrectlyMeasuringTextView(context2);
        String str = "chat_gifSaveHintText";
        this.textView.setTextColor(Theme.getColor(str));
        this.textView.setTextSize(1, 14.0f);
        this.textView.setMaxLines(2);
        this.textView.setMaxWidth(AndroidUtilities.dp(i2 == 4 ? 280.0f : 250.0f));
        String str2 = "chat_gifSaveHintBackground";
        if (this.currentType == 3) {
            this.textView.setGravity(19);
            this.textView.setBackground(Theme.createRoundRectDrawable(AndroidUtilities.dp(5.0f), Theme.getColor(str2)));
            this.textView.setPadding(AndroidUtilities.dp(10.0f), 0, AndroidUtilities.dp(10.0f), 0);
            addView(this.textView, LayoutHelper.createFrame(-2, 30.0f, 51, 0.0f, z2 ? 6.0f : 0.0f, 0.0f, z2 ? 0.0f : 6.0f));
        } else {
            this.textView.setGravity(51);
            this.textView.setBackground(Theme.createRoundRectDrawable(AndroidUtilities.dp(3.0f), Theme.getColor(str2)));
            int i3 = this.currentType;
            if (i3 == 5 || i3 == 4) {
                this.textView.setPadding(AndroidUtilities.dp(9.0f), AndroidUtilities.dp(6.0f), AndroidUtilities.dp(9.0f), AndroidUtilities.dp(7.0f));
            } else if (i3 == 2) {
                this.textView.setPadding(AndroidUtilities.dp(7.0f), AndroidUtilities.dp(6.0f), AndroidUtilities.dp(7.0f), AndroidUtilities.dp(7.0f));
            } else {
                this.textView.setPadding(AndroidUtilities.dp(i3 == 0 ? 54.0f : 5.0f), AndroidUtilities.dp(6.0f), AndroidUtilities.dp(5.0f), AndroidUtilities.dp(7.0f));
            }
            addView(this.textView, LayoutHelper.createFrame(-2, -2.0f, 51, 0.0f, z2 ? 6.0f : 0.0f, 0.0f, z2 ? 0.0f : 6.0f));
        }
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
        return showForMessageCell(chatMessageCell, null, 0, 0, z);
    }

    public boolean showForMessageCell(ChatMessageCell chatMessageCell, Object obj, int i, int i2, boolean z) {
        ViewGroup viewGroup = chatMessageCell;
        int i3 = i2;
        if (!(this.currentType == 5 && i3 == this.shownY && this.messageCell == viewGroup)) {
            int i4 = this.currentType;
            if (i4 == 5 || ((i4 != 0 || getTag() == null) && this.messageCell != viewGroup)) {
                int measuredHeight;
                Runnable runnable = this.hideRunnable;
                if (runnable != null) {
                    AndroidUtilities.cancelRunOnUIThread(runnable);
                    this.hideRunnable = null;
                }
                int[] iArr = new int[2];
                viewGroup.getLocationInWindow(iArr);
                int i5 = iArr[1];
                ((View) getParent()).getLocationInWindow(iArr);
                i5 -= iArr[1];
                View view = (View) chatMessageCell.getParent();
                int i6 = this.currentType;
                if (i6 == 0) {
                    ImageReceiver photoImage = chatMessageCell.getPhotoImage();
                    i5 += photoImage.getImageY();
                    i3 = photoImage.getImageHeight();
                    i6 = i5 + i3;
                    measuredHeight = view.getMeasuredHeight();
                    if (i5 <= getMeasuredHeight() + AndroidUtilities.dp(10.0f) || i6 > measuredHeight + (i3 / 4)) {
                        return false;
                    }
                    i3 = chatMessageCell.getNoSoundIconCenterX();
                } else if (i6 == 5) {
                    i5 += i3;
                    this.shownY = i3;
                    Integer num = (Integer) obj;
                    if (chatMessageCell.getMessageObject().isQuiz()) {
                        if (num.intValue() == 0) {
                            this.textView.setText(LocaleController.getString("NoVotesQuiz", NUM));
                        } else {
                            this.textView.setText(LocaleController.formatPluralString("Answer", num.intValue()));
                        }
                    } else if (num.intValue() == 0) {
                        this.textView.setText(LocaleController.getString("NoVotes", NUM));
                    } else {
                        this.textView.setText(LocaleController.formatPluralString("Vote", num.intValue()));
                    }
                    measure(MeasureSpec.makeMeasureSpec(1000, Integer.MIN_VALUE), MeasureSpec.makeMeasureSpec(1000, Integer.MIN_VALUE));
                    i3 = i;
                } else {
                    MessageObject messageObject = chatMessageCell.getMessageObject();
                    String str = this.overrideText;
                    if (str == null) {
                        this.textView.setText(LocaleController.getString("HidAccount", NUM));
                    } else {
                        this.textView.setText(str);
                    }
                    measure(MeasureSpec.makeMeasureSpec(1000, Integer.MIN_VALUE), MeasureSpec.makeMeasureSpec(1000, Integer.MIN_VALUE));
                    i5 += AndroidUtilities.dp(22.0f);
                    if (!messageObject.isOutOwner() && chatMessageCell.isDrawNameLayout()) {
                        i5 += AndroidUtilities.dp(20.0f);
                    }
                    if (!this.isTopArrow && r9 <= getMeasuredHeight() + AndroidUtilities.dp(10.0f)) {
                        return false;
                    }
                    i3 = chatMessageCell.getForwardNameCenterX();
                }
                i6 = view.getMeasuredWidth();
                if (this.isTopArrow) {
                    setTranslationY((float) AndroidUtilities.dp(44.0f));
                } else {
                    setTranslationY((float) (i5 - getMeasuredHeight()));
                }
                i5 = chatMessageCell.getLeft() + i3;
                measuredHeight = AndroidUtilities.dp(19.0f);
                if (this.currentType == 5) {
                    int measuredWidth = (i3 - (getMeasuredWidth() / 2)) - AndroidUtilities.dp(19.1f);
                    setTranslationX((float) measuredWidth);
                    measuredHeight += measuredWidth;
                } else if (i5 > view.getMeasuredWidth() / 2) {
                    i6 = (i6 - getMeasuredWidth()) - AndroidUtilities.dp(38.0f);
                    setTranslationX((float) i6);
                    measuredHeight += i6;
                } else {
                    setTranslationX(0.0f);
                }
                float left = (float) (((chatMessageCell.getLeft() + i3) - measuredHeight) - (this.arrowImageView.getMeasuredWidth() / 2));
                this.arrowImageView.setTranslationX(left);
                float dp;
                if (i5 > view.getMeasuredWidth() / 2) {
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
                this.messageCell = viewGroup;
                AnimatorSet animatorSet = this.animatorSet;
                if (animatorSet != null) {
                    animatorSet.cancel();
                    this.animatorSet = null;
                }
                setTag(Integer.valueOf(1));
                setVisibility(0);
                if (z) {
                    this.animatorSet = new AnimatorSet();
                    animatorSet = this.animatorSet;
                    Animator[] animatorArr = new Animator[1];
                    animatorArr[0] = ObjectAnimator.ofFloat(this, "alpha", new float[]{0.0f, 1.0f});
                    animatorSet.playTogether(animatorArr);
                    this.animatorSet.addListener(new AnimatorListenerAdapter() {
                        public void onAnimationEnd(Animator animator) {
                            HintView.this.animatorSet = null;
                            AndroidUtilities.runOnUIThread(HintView.this.hideRunnable = new -$$Lambda$HintView$1$Oo-YArBkq6553J0682j2MQqGlbY(this), HintView.this.currentType == 0 ? 10000 : 2000);
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
        }
        return false;
    }

    public boolean showForView(View view, boolean z) {
        if (this.currentView == view || getTag() != null) {
            return false;
        }
        int i;
        int measuredWidth;
        int i2;
        Runnable runnable = this.hideRunnable;
        if (runnable != null) {
            AndroidUtilities.cancelRunOnUIThread(runnable);
            this.hideRunnable = null;
        }
        measure(MeasureSpec.makeMeasureSpec(AndroidUtilities.displaySize.x, Integer.MIN_VALUE), MeasureSpec.makeMeasureSpec(AndroidUtilities.displaySize.x, Integer.MIN_VALUE));
        int[] iArr = new int[2];
        view.getLocationInWindow(iArr);
        int dp = iArr[1] - AndroidUtilities.dp(4.0f);
        if (this.currentType == 4) {
            dp += AndroidUtilities.dp(4.0f);
        }
        if (this.currentType != 3) {
            i = iArr[0];
            measuredWidth = view.getMeasuredWidth() / 2;
        } else if (view instanceof SimpleTextView) {
            i = iArr[0];
            measuredWidth = ((SimpleTextView) view).getTextWidth() / 2;
        } else {
            throw new IllegalArgumentException();
        }
        i += measuredWidth;
        View view2 = (View) getParent();
        view2.getLocationInWindow(iArr);
        i -= iArr[0];
        dp -= iArr[1];
        int measuredWidth2 = view2.getMeasuredWidth();
        if (this.isTopArrow) {
            setTranslationY((float) AndroidUtilities.dp(44.0f));
        } else {
            setTranslationY((float) (dp - getMeasuredHeight()));
        }
        if (getLayoutParams() instanceof MarginLayoutParams) {
            dp = ((MarginLayoutParams) getLayoutParams()).leftMargin;
            i2 = ((MarginLayoutParams) getLayoutParams()).rightMargin;
        } else {
            dp = 0;
            i2 = 0;
        }
        measuredWidth2 = i > view2.getMeasuredWidth() / 2 ? this.currentType == 3 ? (int) (((float) measuredWidth2) - (((float) getMeasuredWidth()) * 1.5f)) : (measuredWidth2 - getMeasuredWidth()) - (i2 + dp) : this.currentType == 3 ? (i - (getMeasuredWidth() / 2)) - this.arrowImageView.getMeasuredWidth() : 0;
        setTranslationX((float) measuredWidth2);
        float measuredWidth3 = (float) ((i - (dp + measuredWidth2)) - (this.arrowImageView.getMeasuredWidth() / 2));
        this.arrowImageView.setTranslationX(measuredWidth3);
        float dp2;
        if (i > view2.getMeasuredWidth() / 2) {
            if (measuredWidth3 < ((float) AndroidUtilities.dp(10.0f))) {
                dp2 = measuredWidth3 - ((float) AndroidUtilities.dp(10.0f));
                setTranslationX(getTranslationX() + dp2);
                this.arrowImageView.setTranslationX(measuredWidth3 - dp2);
            }
        } else if (measuredWidth3 > ((float) (getMeasuredWidth() - AndroidUtilities.dp(24.0f)))) {
            dp2 = (measuredWidth3 - ((float) getMeasuredWidth())) + ((float) AndroidUtilities.dp(24.0f));
            setTranslationX(dp2);
            this.arrowImageView.setTranslationX(measuredWidth3 - dp2);
        } else if (measuredWidth3 < ((float) AndroidUtilities.dp(10.0f))) {
            dp2 = measuredWidth3 - ((float) AndroidUtilities.dp(10.0f));
            setTranslationX(getTranslationX() + dp2);
            this.arrowImageView.setTranslationX(measuredWidth3 - dp2);
        }
        this.currentView = view;
        AnimatorSet animatorSet = this.animatorSet;
        if (animatorSet != null) {
            animatorSet.cancel();
            this.animatorSet = null;
        }
        setTag(Integer.valueOf(1));
        setVisibility(0);
        if (z) {
            this.animatorSet = new AnimatorSet();
            animatorSet = this.animatorSet;
            Animator[] animatorArr = new Animator[1];
            animatorArr[0] = ObjectAnimator.ofFloat(this, "alpha", new float[]{0.0f, 1.0f});
            animatorSet.playTogether(animatorArr);
            this.animatorSet.addListener(new AnimatorListenerAdapter() {
                public void onAnimationEnd(Animator animator) {
                    HintView.this.animatorSet = null;
                    AndroidUtilities.runOnUIThread(HintView.this.hideRunnable = new -$$Lambda$HintView$2$jvm1hL0MTRZE1LibhgmMBpjU0UA(this), HintView.this.showingDuration);
                }

                public /* synthetic */ void lambda$onAnimationEnd$0$HintView$2() {
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
                    HintView.this.currentView = null;
                    HintView.this.messageCell = null;
                    HintView.this.animatorSet = null;
                }
            });
            this.animatorSet.setDuration(300);
            this.animatorSet.start();
        }
    }

    public void setText(CharSequence charSequence) {
        this.textView.setText(charSequence);
    }

    public ChatMessageCell getMessageCell() {
        return this.messageCell;
    }

    public void setShowingDuration(long j) {
        this.showingDuration = j;
    }
}
