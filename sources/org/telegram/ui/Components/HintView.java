package org.telegram.ui.Components;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.content.Context;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffColorFilter;
import android.graphics.drawable.Drawable;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;
import org.telegram.messenger.AndroidUtilities;
import org.telegram.messenger.ImageReceiver;
import org.telegram.messenger.LocaleController;
import org.telegram.messenger.MessageObject;
import org.telegram.tgnet.TLRPC;
import org.telegram.ui.ActionBar.SimpleTextView;
import org.telegram.ui.ActionBar.Theme;
import org.telegram.ui.Cells.ChatMessageCell;

public class HintView extends FrameLayout {
    public static final int TYPE_COMMON = 4;
    public static final int TYPE_POLL_VOTE = 5;
    public static final int TYPE_SEARCH_AS_LIST = 3;
    /* access modifiers changed from: private */
    public AnimatorSet animatorSet;
    private ImageView arrowImageView;
    private int bottomOffset;
    /* access modifiers changed from: private */
    public int currentType;
    /* access modifiers changed from: private */
    public View currentView;
    private float extraTranslationY;
    /* access modifiers changed from: private */
    public Runnable hideRunnable;
    private ImageView imageView;
    private boolean isTopArrow;
    /* access modifiers changed from: private */
    public ChatMessageCell messageCell;
    private String overrideText;
    private final Theme.ResourcesProvider resourcesProvider;
    /* access modifiers changed from: private */
    public long showingDuration;
    private int shownY;
    private TextView textView;
    private float translationY;

    public HintView(Context context, int type) {
        this(context, type, false, (Theme.ResourcesProvider) null);
    }

    public HintView(Context context, int type, boolean topArrow) {
        this(context, type, topArrow, (Theme.ResourcesProvider) null);
    }

    public HintView(Context context, int type, Theme.ResourcesProvider resourcesProvider2) {
        this(context, type, false, resourcesProvider2);
    }

    /* JADX INFO: super call moved to the top of the method (can break code semantics) */
    public HintView(Context context, int type, boolean topArrow, Theme.ResourcesProvider resourcesProvider2) {
        super(context);
        Context context2 = context;
        int i = type;
        boolean z = topArrow;
        this.showingDuration = 2000;
        this.resourcesProvider = resourcesProvider2;
        this.currentType = i;
        this.isTopArrow = z;
        CorrectlyMeasuringTextView correctlyMeasuringTextView = new CorrectlyMeasuringTextView(context2);
        this.textView = correctlyMeasuringTextView;
        correctlyMeasuringTextView.setTextColor(getThemedColor("chat_gifSaveHintText"));
        this.textView.setTextSize(1, 14.0f);
        this.textView.setMaxLines(2);
        if (i == 7 || i == 8 || i == 9) {
            this.textView.setMaxWidth(AndroidUtilities.dp(310.0f));
        } else if (i == 4) {
            this.textView.setMaxWidth(AndroidUtilities.dp(280.0f));
        } else {
            this.textView.setMaxWidth(AndroidUtilities.dp(250.0f));
        }
        if (this.currentType == 3) {
            this.textView.setGravity(19);
            this.textView.setBackground(Theme.createRoundRectDrawable(AndroidUtilities.dp(5.0f), getThemedColor("chat_gifSaveHintBackground")));
            this.textView.setPadding(AndroidUtilities.dp(10.0f), 0, AndroidUtilities.dp(10.0f), 0);
            addView(this.textView, LayoutHelper.createFrame(-2, 30.0f, 51, 0.0f, z ? 6.0f : 0.0f, 0.0f, z ? 0.0f : 6.0f));
        } else {
            this.textView.setGravity(51);
            this.textView.setBackground(Theme.createRoundRectDrawable(AndroidUtilities.dp(6.0f), getThemedColor("chat_gifSaveHintBackground")));
            this.textView.setPadding(AndroidUtilities.dp(this.currentType == 0 ? 54.0f : 8.0f), AndroidUtilities.dp(7.0f), AndroidUtilities.dp(8.0f), AndroidUtilities.dp(8.0f));
            addView(this.textView, LayoutHelper.createFrame(-2, -2.0f, 51, 0.0f, z ? 6.0f : 0.0f, 0.0f, z ? 0.0f : 6.0f));
        }
        if (i == 0) {
            this.textView.setText(LocaleController.getString("AutoplayVideoInfo", NUM));
            ImageView imageView2 = new ImageView(context2);
            this.imageView = imageView2;
            imageView2.setImageResource(NUM);
            this.imageView.setScaleType(ImageView.ScaleType.CENTER);
            this.imageView.setColorFilter(new PorterDuffColorFilter(getThemedColor("chat_gifSaveHintText"), PorterDuff.Mode.MULTIPLY));
            addView(this.imageView, LayoutHelper.createFrame(38, 34.0f, 51, 7.0f, 7.0f, 0.0f, 0.0f));
        }
        ImageView imageView3 = new ImageView(context2);
        this.arrowImageView = imageView3;
        imageView3.setImageResource(z ? NUM : NUM);
        this.arrowImageView.setColorFilter(new PorterDuffColorFilter(getThemedColor("chat_gifSaveHintBackground"), PorterDuff.Mode.MULTIPLY));
        addView(this.arrowImageView, LayoutHelper.createFrame(14, 6.0f, (z ? 48 : 80) | 3, 0.0f, 0.0f, 0.0f, 0.0f));
    }

    public void setBackgroundColor(int background, int text) {
        this.textView.setTextColor(text);
        this.arrowImageView.setColorFilter(new PorterDuffColorFilter(background, PorterDuff.Mode.MULTIPLY));
        TextView textView2 = this.textView;
        int i = this.currentType;
        textView2.setBackground(Theme.createRoundRectDrawable(AndroidUtilities.dp((i == 7 || i == 8) ? 6.0f : 3.0f), background));
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

    public void setExtraTranslationY(float value) {
        this.extraTranslationY = value;
        setTranslationY(this.translationY + value);
    }

    public float getBaseTranslationY() {
        return this.translationY;
    }

    public boolean showForMessageCell(ChatMessageCell cell, boolean animated) {
        return showForMessageCell(cell, (Object) null, 0, 0, animated);
    }

    public boolean showForMessageCell(ChatMessageCell cell, Object object, int x, int y, boolean animated) {
        int top;
        int centerX;
        ChatMessageCell chatMessageCell = cell;
        int i = y;
        int i2 = this.currentType;
        if ((i2 == 5 && i == this.shownY && this.messageCell == chatMessageCell) || (i2 != 5 && ((i2 == 0 && getTag() != null) || this.messageCell == chatMessageCell))) {
            return false;
        }
        Runnable runnable = this.hideRunnable;
        if (runnable != null) {
            AndroidUtilities.cancelRunOnUIThread(runnable);
            this.hideRunnable = null;
        }
        int[] position = new int[2];
        chatMessageCell.getLocationInWindow(position);
        int top2 = position[1];
        ((View) getParent()).getLocationInWindow(position);
        int top3 = top2 - position[1];
        View parentView = (View) cell.getParent();
        int i3 = this.currentType;
        if (i3 == 0) {
            ImageReceiver imageReceiver = cell.getPhotoImage();
            top = (int) (((float) top3) + imageReceiver.getImageY());
            int height = (int) imageReceiver.getImageHeight();
            int bottom = top + height;
            int parentHeight = parentView.getMeasuredHeight();
            if (top <= getMeasuredHeight() + AndroidUtilities.dp(10.0f) || bottom > parentHeight + (height / 4)) {
                return false;
            }
            centerX = cell.getNoSoundIconCenterX();
        } else if (i3 == 5) {
            Integer count = (Integer) object;
            int centerX2 = x;
            top = top3 + i;
            this.shownY = i;
            if (count.intValue() == -1) {
                this.textView.setText(LocaleController.getString("PollSelectOption", NUM));
            } else if (cell.getMessageObject().isQuiz()) {
                if (count.intValue() == 0) {
                    this.textView.setText(LocaleController.getString("NoVotesQuiz", NUM));
                } else {
                    this.textView.setText(LocaleController.formatPluralString("Answer", count.intValue(), new Object[0]));
                }
            } else if (count.intValue() == 0) {
                this.textView.setText(LocaleController.getString("NoVotes", NUM));
            } else {
                this.textView.setText(LocaleController.formatPluralString("Vote", count.intValue(), new Object[0]));
            }
            measure(View.MeasureSpec.makeMeasureSpec(1000, Integer.MIN_VALUE), View.MeasureSpec.makeMeasureSpec(1000, Integer.MIN_VALUE));
            centerX = centerX2;
        } else {
            MessageObject messageObject = cell.getMessageObject();
            String str = this.overrideText;
            if (str == null) {
                this.textView.setText(LocaleController.getString("HidAccount", NUM));
            } else {
                this.textView.setText(str);
            }
            measure(View.MeasureSpec.makeMeasureSpec(1000, Integer.MIN_VALUE), View.MeasureSpec.makeMeasureSpec(1000, Integer.MIN_VALUE));
            TLRPC.User user = cell.getCurrentUser();
            if (user == null || user.id != 0) {
                top = top3 + AndroidUtilities.dp(22.0f);
                if (!messageObject.isOutOwner() && cell.isDrawNameLayout()) {
                    top += AndroidUtilities.dp(20.0f);
                }
            } else {
                top = top3 + ((cell.getMeasuredHeight() - Math.max(0, cell.getBottom() - parentView.getMeasuredHeight())) - AndroidUtilities.dp(50.0f));
            }
            if (!this.isTopArrow && top <= getMeasuredHeight() + AndroidUtilities.dp(10.0f)) {
                return false;
            }
            centerX = cell.getForwardNameCenterX();
        }
        int parentWidth = parentView.getMeasuredWidth();
        if (this.isTopArrow) {
            float f = this.extraTranslationY;
            float dp = (float) AndroidUtilities.dp(44.0f);
            this.translationY = dp;
            setTranslationY(f + dp);
        } else {
            float f2 = this.extraTranslationY;
            float measuredHeight = (float) (top - getMeasuredHeight());
            this.translationY = measuredHeight;
            setTranslationY(f2 + measuredHeight);
        }
        int iconX = cell.getLeft() + centerX;
        int left = AndroidUtilities.dp(19.0f);
        if (this.currentType == 5) {
            int offset = Math.max(0, (centerX - (getMeasuredWidth() / 2)) - AndroidUtilities.dp(19.1f));
            setTranslationX((float) offset);
            left += offset;
        } else if (iconX > parentView.getMeasuredWidth() / 2) {
            int offset2 = (parentWidth - getMeasuredWidth()) - AndroidUtilities.dp(38.0f);
            setTranslationX((float) offset2);
            left += offset2;
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
            animatorSet3.playTogether(new Animator[]{ObjectAnimator.ofFloat(this, View.ALPHA, new float[]{0.0f, 1.0f})});
            this.animatorSet.addListener(new AnimatorListenerAdapter() {
                public void onAnimationEnd(Animator animation) {
                    AnimatorSet unused = HintView.this.animatorSet = null;
                    AndroidUtilities.runOnUIThread(HintView.this.hideRunnable = new HintView$1$$ExternalSyntheticLambda0(this), HintView.this.currentType == 0 ? 10000 : 2000);
                }

                /* renamed from: lambda$onAnimationEnd$0$org-telegram-ui-Components-HintView$1  reason: not valid java name */
                public /* synthetic */ void m1025lambda$onAnimationEnd$0$orgtelegramuiComponentsHintView$1() {
                    HintView.this.hide();
                }
            });
            this.animatorSet.setDuration(300);
            this.animatorSet.start();
            return true;
        }
        setAlpha(1.0f);
        return true;
    }

    public boolean showForView(View view, boolean animated) {
        if (this.currentView == view || getTag() != null) {
            if (getTag() != null) {
                updatePosition(view);
            }
            return false;
        }
        Runnable runnable = this.hideRunnable;
        if (runnable != null) {
            AndroidUtilities.cancelRunOnUIThread(runnable);
            this.hideRunnable = null;
        }
        updatePosition(view);
        this.currentView = view;
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
            animatorSet3.playTogether(new Animator[]{ObjectAnimator.ofFloat(this, View.ALPHA, new float[]{0.0f, 1.0f})});
            this.animatorSet.addListener(new AnimatorListenerAdapter() {
                public void onAnimationEnd(Animator animation) {
                    AnimatorSet unused = HintView.this.animatorSet = null;
                    AndroidUtilities.runOnUIThread(HintView.this.hideRunnable = new HintView$2$$ExternalSyntheticLambda0(this), HintView.this.showingDuration);
                }

                /* renamed from: lambda$onAnimationEnd$0$org-telegram-ui-Components-HintView$2  reason: not valid java name */
                public /* synthetic */ void m1026lambda$onAnimationEnd$0$orgtelegramuiComponentsHintView$2() {
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

    private void updatePosition(View view) {
        int centerX;
        int offset;
        int i;
        View view2 = view;
        measure(View.MeasureSpec.makeMeasureSpec(AndroidUtilities.displaySize.x, Integer.MIN_VALUE), View.MeasureSpec.makeMeasureSpec(AndroidUtilities.displaySize.x, Integer.MIN_VALUE));
        int[] position = new int[2];
        view2.getLocationInWindow(position);
        int top = position[1] - AndroidUtilities.dp(4.0f);
        int i2 = this.currentType;
        if (i2 == 4) {
            top += AndroidUtilities.dp(4.0f);
        } else if (i2 == 6) {
            top += view.getMeasuredHeight() + getMeasuredHeight() + AndroidUtilities.dp(10.0f);
        } else if (i2 == 7 || (i2 == 8 && this.isTopArrow)) {
            top += view.getMeasuredHeight() + getMeasuredHeight() + AndroidUtilities.dp(8.0f);
        } else if (i2 == 8) {
            top -= AndroidUtilities.dp(10.0f);
        }
        int i3 = this.currentType;
        if (i3 != 8 || !this.isTopArrow) {
            if (i3 == 3) {
                centerX = position[0];
            } else {
                centerX = position[0] + (view.getMeasuredWidth() / 2);
            }
        } else if (view2 instanceof SimpleTextView) {
            SimpleTextView textView2 = (SimpleTextView) view2;
            Drawable drawable = textView2.getRightDrawable();
            centerX = (position[0] + (drawable != null ? drawable.getBounds().centerX() : textView2.getTextWidth() / 2)) - AndroidUtilities.dp(8.0f);
        } else if (view2 instanceof TextView) {
            centerX = (position[0] + ((TextView) view2).getMeasuredWidth()) - AndroidUtilities.dp(16.5f);
        } else {
            centerX = position[0];
        }
        View parentView = (View) getParent();
        parentView.getLocationInWindow(position);
        int centerX2 = centerX - position[0];
        int top2 = (top - position[1]) - this.bottomOffset;
        int parentWidth = parentView.getMeasuredWidth();
        if (!this.isTopArrow || (i = this.currentType) == 6 || i == 7 || i == 8) {
            float f = this.extraTranslationY;
            float measuredHeight = (float) (top2 - getMeasuredHeight());
            this.translationY = measuredHeight;
            setTranslationY(f + measuredHeight);
        } else {
            float f2 = this.extraTranslationY;
            float dp = (float) AndroidUtilities.dp(44.0f);
            this.translationY = dp;
            setTranslationY(f2 + dp);
        }
        int leftMargin = 0;
        int rightMargin = 0;
        if (getLayoutParams() instanceof ViewGroup.MarginLayoutParams) {
            leftMargin = ((ViewGroup.MarginLayoutParams) getLayoutParams()).leftMargin;
            rightMargin = ((ViewGroup.MarginLayoutParams) getLayoutParams()).rightMargin;
        }
        if (this.currentType == 8 && !this.isTopArrow) {
            offset = (((parentWidth - leftMargin) - rightMargin) - getMeasuredWidth()) / 2;
        } else if (centerX2 > parentView.getMeasuredWidth() / 2) {
            if (this.currentType == 3) {
                offset = (int) (((float) parentWidth) - (((float) getMeasuredWidth()) * 1.5f));
                if (offset < 0) {
                    offset = 0;
                }
            } else {
                offset = (parentWidth - getMeasuredWidth()) - (leftMargin + rightMargin);
            }
        } else if (this.currentType == 3) {
            offset = (centerX2 - (getMeasuredWidth() / 2)) - this.arrowImageView.getMeasuredWidth();
            if (offset < 0) {
                offset = 0;
            }
        } else {
            offset = 0;
        }
        setTranslationX((float) offset);
        float arrowX = (float) ((centerX2 - (leftMargin + offset)) - (this.arrowImageView.getMeasuredWidth() / 2));
        if (this.currentType == 7) {
            arrowX += (float) AndroidUtilities.dp(2.0f);
        }
        this.arrowImageView.setTranslationX(arrowX);
        if (centerX2 > parentView.getMeasuredWidth() / 2) {
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
            animatorSet3.playTogether(new Animator[]{ObjectAnimator.ofFloat(this, View.ALPHA, new float[]{0.0f})});
            this.animatorSet.addListener(new AnimatorListenerAdapter() {
                public void onAnimationEnd(Animator animation) {
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

    public void setText(CharSequence text) {
        this.textView.setText(text);
    }

    public ChatMessageCell getMessageCell() {
        return this.messageCell;
    }

    public void setShowingDuration(long showingDuration2) {
        this.showingDuration = showingDuration2;
    }

    public void setBottomOffset(int offset) {
        this.bottomOffset = offset;
    }

    private int getThemedColor(String key) {
        Theme.ResourcesProvider resourcesProvider2 = this.resourcesProvider;
        Integer color = resourcesProvider2 != null ? resourcesProvider2.getColor(key) : null;
        return color != null ? color.intValue() : Theme.getColor(key);
    }
}
