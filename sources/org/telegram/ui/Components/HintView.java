package org.telegram.ui.Components;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.content.Context;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffColorFilter;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;
import org.telegram.messenger.AndroidUtilities;
import org.telegram.messenger.ImageReceiver;
import org.telegram.messenger.LocaleController;
import org.telegram.messenger.MessageObject;
import org.telegram.ui.ActionBar.SimpleTextView;
import org.telegram.ui.ActionBar.Theme;
import org.telegram.ui.Cells.ChatMessageCell;
import org.telegram.ui.Components.HintView;

public class HintView extends FrameLayout {
    public static final int TYPE_POLL_VOTE = 5;
    public static final int TYPE_SEARCH_AS_LIST = 3;
    /* access modifiers changed from: private */
    public AnimatorSet animatorSet;
    private ImageView arrowImageView;
    /* access modifiers changed from: private */
    public int currentType;
    /* access modifiers changed from: private */
    public View currentView;
    /* access modifiers changed from: private */
    public Runnable hideRunnable;
    private ImageView imageView;
    private boolean isTopArrow;
    /* access modifiers changed from: private */
    public ChatMessageCell messageCell;
    private String overrideText;
    /* access modifiers changed from: private */
    public long showingDuration;
    private int shownY;
    private TextView textView;

    public HintView(Context context, int i) {
        this(context, i, false);
    }

    /* JADX INFO: super call moved to the top of the method (can break code semantics) */
    public HintView(Context context, int i, boolean z) {
        super(context);
        Context context2 = context;
        int i2 = i;
        boolean z2 = z;
        this.showingDuration = 2000;
        this.currentType = i2;
        this.isTopArrow = z2;
        this.textView = new CorrectlyMeasuringTextView(context2);
        this.textView.setTextColor(Theme.getColor("chat_gifSaveHintText"));
        this.textView.setTextSize(1, 14.0f);
        this.textView.setMaxLines(2);
        this.textView.setMaxWidth(AndroidUtilities.dp(i2 == 4 ? 280.0f : 250.0f));
        if (this.currentType == 3) {
            this.textView.setGravity(19);
            this.textView.setBackground(Theme.createRoundRectDrawable(AndroidUtilities.dp(5.0f), Theme.getColor("chat_gifSaveHintBackground")));
            this.textView.setPadding(AndroidUtilities.dp(10.0f), 0, AndroidUtilities.dp(10.0f), 0);
            addView(this.textView, LayoutHelper.createFrame(-2, 30.0f, 51, 0.0f, z2 ? 6.0f : 0.0f, 0.0f, z2 ? 0.0f : 6.0f));
        } else {
            this.textView.setGravity(51);
            this.textView.setBackground(Theme.createRoundRectDrawable(AndroidUtilities.dp(3.0f), Theme.getColor("chat_gifSaveHintBackground")));
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
            this.imageView.setScaleType(ImageView.ScaleType.CENTER);
            this.imageView.setColorFilter(new PorterDuffColorFilter(Theme.getColor("chat_gifSaveHintText"), PorterDuff.Mode.MULTIPLY));
            addView(this.imageView, LayoutHelper.createFrame(38, 34.0f, 51, 7.0f, 7.0f, 0.0f, 0.0f));
        }
        this.arrowImageView = new ImageView(context2);
        this.arrowImageView.setImageResource(z2 ? NUM : NUM);
        this.arrowImageView.setColorFilter(new PorterDuffColorFilter(Theme.getColor("chat_gifSaveHintBackground"), PorterDuff.Mode.MULTIPLY));
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
        return showForMessageCell(chatMessageCell, (Object) null, 0, 0, z);
    }

    public boolean showForMessageCell(ChatMessageCell chatMessageCell, Object obj, int i, int i2, boolean z) {
        int i3;
        int i4;
        int i5;
        ChatMessageCell chatMessageCell2 = chatMessageCell;
        int i6 = i2;
        if ((this.currentType == 5 && i6 == this.shownY && this.messageCell == chatMessageCell2) || ((i3 = this.currentType) != 5 && ((i3 == 0 && getTag() != null) || this.messageCell == chatMessageCell2))) {
            return false;
        }
        Runnable runnable = this.hideRunnable;
        if (runnable != null) {
            AndroidUtilities.cancelRunOnUIThread(runnable);
            this.hideRunnable = null;
        }
        int[] iArr = new int[2];
        chatMessageCell2.getLocationInWindow(iArr);
        int i7 = iArr[1];
        ((View) getParent()).getLocationInWindow(iArr);
        int i8 = i7 - iArr[1];
        View view = (View) chatMessageCell.getParent();
        int i9 = this.currentType;
        if (i9 == 0) {
            ImageReceiver photoImage = chatMessageCell.getPhotoImage();
            i4 = i8 + photoImage.getImageY();
            int imageHeight = photoImage.getImageHeight();
            int i10 = i4 + imageHeight;
            int measuredHeight = view.getMeasuredHeight();
            if (i4 <= getMeasuredHeight() + AndroidUtilities.dp(10.0f) || i10 > measuredHeight + (imageHeight / 4)) {
                return false;
            }
            i5 = chatMessageCell.getNoSoundIconCenterX();
        } else if (i9 == 5) {
            Integer num = (Integer) obj;
            i4 = i8 + i6;
            this.shownY = i6;
            if (num.intValue() == -1) {
                this.textView.setText(LocaleController.getString("PollSelectOption", NUM));
            } else if (chatMessageCell.getMessageObject().isQuiz()) {
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
            measure(View.MeasureSpec.makeMeasureSpec(1000, Integer.MIN_VALUE), View.MeasureSpec.makeMeasureSpec(1000, Integer.MIN_VALUE));
            i5 = i;
        } else {
            MessageObject messageObject = chatMessageCell.getMessageObject();
            String str = this.overrideText;
            if (str == null) {
                this.textView.setText(LocaleController.getString("HidAccount", NUM));
            } else {
                this.textView.setText(str);
            }
            measure(View.MeasureSpec.makeMeasureSpec(1000, Integer.MIN_VALUE), View.MeasureSpec.makeMeasureSpec(1000, Integer.MIN_VALUE));
            i4 = i8 + AndroidUtilities.dp(22.0f);
            if (!messageObject.isOutOwner() && chatMessageCell.isDrawNameLayout()) {
                i4 += AndroidUtilities.dp(20.0f);
            }
            if (!this.isTopArrow && i4 <= getMeasuredHeight() + AndroidUtilities.dp(10.0f)) {
                return false;
            }
            i5 = chatMessageCell.getForwardNameCenterX();
        }
        int measuredWidth = view.getMeasuredWidth();
        if (this.isTopArrow) {
            setTranslationY((float) AndroidUtilities.dp(44.0f));
        } else {
            setTranslationY((float) (i4 - getMeasuredHeight()));
        }
        int left = chatMessageCell.getLeft() + i5;
        int dp = AndroidUtilities.dp(19.0f);
        if (this.currentType == 5) {
            int max = Math.max(0, (i5 - (getMeasuredWidth() / 2)) - AndroidUtilities.dp(19.1f));
            setTranslationX((float) max);
            dp += max;
        } else if (left > view.getMeasuredWidth() / 2) {
            int measuredWidth2 = (measuredWidth - getMeasuredWidth()) - AndroidUtilities.dp(38.0f);
            setTranslationX((float) measuredWidth2);
            dp += measuredWidth2;
        } else {
            setTranslationX(0.0f);
        }
        float left2 = (float) (((chatMessageCell.getLeft() + i5) - dp) - (this.arrowImageView.getMeasuredWidth() / 2));
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
        this.messageCell = chatMessageCell2;
        AnimatorSet animatorSet2 = this.animatorSet;
        if (animatorSet2 != null) {
            animatorSet2.cancel();
            this.animatorSet = null;
        }
        setTag(1);
        setVisibility(0);
        if (z) {
            this.animatorSet = new AnimatorSet();
            this.animatorSet.playTogether(new Animator[]{ObjectAnimator.ofFloat(this, View.ALPHA, new float[]{0.0f, 1.0f})});
            this.animatorSet.addListener(new AnimatorListenerAdapter() {
                public void onAnimationEnd(Animator animator) {
                    AnimatorSet unused = HintView.this.animatorSet = null;
                    AndroidUtilities.runOnUIThread(HintView.this.hideRunnable = new Runnable() {
                        public final void run() {
                            HintView.AnonymousClass1.this.lambda$onAnimationEnd$0$HintView$1();
                        }
                    }, HintView.this.currentType == 0 ? 10000 : 2000);
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

    public boolean showForView(View view, boolean z) {
        int i;
        int i2;
        int i3;
        int i4;
        int i5;
        if (this.currentView == view || getTag() != null) {
            return false;
        }
        Runnable runnable = this.hideRunnable;
        if (runnable != null) {
            AndroidUtilities.cancelRunOnUIThread(runnable);
            this.hideRunnable = null;
        }
        measure(View.MeasureSpec.makeMeasureSpec(AndroidUtilities.displaySize.x, Integer.MIN_VALUE), View.MeasureSpec.makeMeasureSpec(AndroidUtilities.displaySize.x, Integer.MIN_VALUE));
        int[] iArr = new int[2];
        view.getLocationInWindow(iArr);
        int dp = iArr[1] - AndroidUtilities.dp(4.0f);
        if (this.currentType == 4) {
            dp += AndroidUtilities.dp(4.0f);
        }
        if (this.currentType != 3) {
            i2 = iArr[0];
            i = view.getMeasuredWidth() / 2;
        } else if (view instanceof SimpleTextView) {
            i2 = iArr[0];
            i = ((SimpleTextView) view).getTextWidth() / 2;
        } else {
            throw new IllegalArgumentException();
        }
        int i6 = i2 + i;
        View view2 = (View) getParent();
        view2.getLocationInWindow(iArr);
        int i7 = i6 - iArr[0];
        int i8 = dp - iArr[1];
        int measuredWidth = view2.getMeasuredWidth();
        if (this.isTopArrow) {
            setTranslationY((float) AndroidUtilities.dp(44.0f));
        } else {
            setTranslationY((float) (i8 - getMeasuredHeight()));
        }
        if (getLayoutParams() instanceof ViewGroup.MarginLayoutParams) {
            i4 = ((ViewGroup.MarginLayoutParams) getLayoutParams()).leftMargin;
            i3 = ((ViewGroup.MarginLayoutParams) getLayoutParams()).rightMargin;
        } else {
            i4 = 0;
            i3 = 0;
        }
        if (i7 > view2.getMeasuredWidth() / 2) {
            i5 = this.currentType == 3 ? (int) (((float) measuredWidth) - (((float) getMeasuredWidth()) * 1.5f)) : (measuredWidth - getMeasuredWidth()) - (i3 + i4);
        } else {
            i5 = this.currentType == 3 ? (i7 - (getMeasuredWidth() / 2)) - this.arrowImageView.getMeasuredWidth() : 0;
        }
        setTranslationX((float) i5);
        float measuredWidth2 = (float) ((i7 - (i4 + i5)) - (this.arrowImageView.getMeasuredWidth() / 2));
        this.arrowImageView.setTranslationX(measuredWidth2);
        if (i7 > view2.getMeasuredWidth() / 2) {
            if (measuredWidth2 < ((float) AndroidUtilities.dp(10.0f))) {
                float dp2 = measuredWidth2 - ((float) AndroidUtilities.dp(10.0f));
                setTranslationX(getTranslationX() + dp2);
                this.arrowImageView.setTranslationX(measuredWidth2 - dp2);
            }
        } else if (measuredWidth2 > ((float) (getMeasuredWidth() - AndroidUtilities.dp(24.0f)))) {
            float measuredWidth3 = (measuredWidth2 - ((float) getMeasuredWidth())) + ((float) AndroidUtilities.dp(24.0f));
            setTranslationX(measuredWidth3);
            this.arrowImageView.setTranslationX(measuredWidth2 - measuredWidth3);
        } else if (measuredWidth2 < ((float) AndroidUtilities.dp(10.0f))) {
            float dp3 = measuredWidth2 - ((float) AndroidUtilities.dp(10.0f));
            setTranslationX(getTranslationX() + dp3);
            this.arrowImageView.setTranslationX(measuredWidth2 - dp3);
        }
        this.currentView = view;
        AnimatorSet animatorSet2 = this.animatorSet;
        if (animatorSet2 != null) {
            animatorSet2.cancel();
            this.animatorSet = null;
        }
        setTag(1);
        setVisibility(0);
        if (z) {
            this.animatorSet = new AnimatorSet();
            this.animatorSet.playTogether(new Animator[]{ObjectAnimator.ofFloat(this, View.ALPHA, new float[]{0.0f, 1.0f})});
            this.animatorSet.addListener(new AnimatorListenerAdapter() {
                public void onAnimationEnd(Animator animator) {
                    AnimatorSet unused = HintView.this.animatorSet = null;
                    AndroidUtilities.runOnUIThread(HintView.this.hideRunnable = new Runnable() {
                        public final void run() {
                            HintView.AnonymousClass2.this.lambda$onAnimationEnd$0$HintView$2();
                        }
                    }, HintView.this.showingDuration);
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
            this.animatorSet = new AnimatorSet();
            this.animatorSet.playTogether(new Animator[]{ObjectAnimator.ofFloat(this, View.ALPHA, new float[]{0.0f})});
            this.animatorSet.addListener(new AnimatorListenerAdapter() {
                public void onAnimationEnd(Animator animator) {
                    HintView.this.setVisibility(4);
                    View unused = HintView.this.currentView = null;
                    ChatMessageCell unused2 = HintView.this.messageCell = null;
                    AnimatorSet unused3 = HintView.this.animatorSet = null;
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
