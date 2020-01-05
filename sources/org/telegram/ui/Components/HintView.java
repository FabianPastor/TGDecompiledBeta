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
            if (i3 == 2) {
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

    /* JADX WARNING: Removed duplicated region for block: B:38:0x010c  */
    /* JADX WARNING: Removed duplicated region for block: B:35:0x00ed  */
    /* JADX WARNING: Removed duplicated region for block: B:46:0x0157  */
    /* JADX WARNING: Removed duplicated region for block: B:50:0x019a  */
    /* JADX WARNING: Removed duplicated region for block: B:49:0x0168  */
    /* JADX WARNING: Removed duplicated region for block: B:35:0x00ed  */
    /* JADX WARNING: Removed duplicated region for block: B:38:0x010c  */
    /* JADX WARNING: Removed duplicated region for block: B:46:0x0157  */
    /* JADX WARNING: Removed duplicated region for block: B:49:0x0168  */
    /* JADX WARNING: Removed duplicated region for block: B:50:0x019a  */
    public boolean showForView(android.view.View r11, boolean r12) {
        /*
        r10 = this;
        r0 = r10.currentView;
        r1 = 0;
        if (r0 == r11) goto L_0x01a0;
    L_0x0005:
        r0 = r10.getTag();
        if (r0 == 0) goto L_0x000d;
    L_0x000b:
        goto L_0x01a0;
    L_0x000d:
        r0 = r10.hideRunnable;
        r2 = 0;
        if (r0 == 0) goto L_0x0017;
    L_0x0012:
        org.telegram.messenger.AndroidUtilities.cancelRunOnUIThread(r0);
        r10.hideRunnable = r2;
    L_0x0017:
        r0 = -NUM; // 0xfffffffvar_ float:-0.0 double:NaN;
        r3 = 1000; // 0x3e8 float:1.401E-42 double:4.94E-321;
        r4 = android.view.View.MeasureSpec.makeMeasureSpec(r3, r0);
        r0 = android.view.View.MeasureSpec.makeMeasureSpec(r3, r0);
        r10.measure(r4, r0);
        r0 = 2;
        r3 = new int[r0];
        r11.getLocationInWindow(r3);
        r4 = 1;
        r5 = r3[r4];
        r6 = NUM; // 0x40800000 float:4.0 double:5.34643471E-315;
        r6 = org.telegram.messenger.AndroidUtilities.dp(r6);
        r5 = r5 - r6;
        r6 = r10.currentType;
        r7 = 3;
        if (r6 != r7) goto L_0x0050;
    L_0x003b:
        r6 = r11 instanceof org.telegram.ui.ActionBar.SimpleTextView;
        if (r6 == 0) goto L_0x004a;
    L_0x003f:
        r6 = r3[r1];
        r8 = r11;
        r8 = (org.telegram.ui.ActionBar.SimpleTextView) r8;
        r8 = r8.getTextWidth();
        r8 = r8 / r0;
        goto L_0x0057;
    L_0x004a:
        r11 = new java.lang.IllegalArgumentException;
        r11.<init>();
        throw r11;
    L_0x0050:
        r6 = r3[r1];
        r8 = r11.getMeasuredWidth();
        r8 = r8 / r0;
    L_0x0057:
        r6 = r6 + r8;
        r8 = r10.getParent();
        r8 = (android.view.View) r8;
        r8.getLocationInWindow(r3);
        r9 = r3[r1];
        r6 = r6 - r9;
        r3 = r3[r4];
        r5 = r5 - r3;
        r3 = android.os.Build.VERSION.SDK_INT;
        r9 = 21;
        if (r3 < r9) goto L_0x0070;
    L_0x006d:
        r3 = org.telegram.messenger.AndroidUtilities.statusBarHeight;
        r5 = r5 - r3;
    L_0x0070:
        r3 = r8.getMeasuredWidth();
        r9 = r10.isTopArrow;
        if (r9 == 0) goto L_0x0083;
    L_0x0078:
        r5 = NUM; // 0x42300000 float:44.0 double:5.48631236E-315;
        r5 = org.telegram.messenger.AndroidUtilities.dp(r5);
        r5 = (float) r5;
        r10.setTranslationY(r5);
        goto L_0x0091;
    L_0x0083:
        r9 = r10.getMeasuredHeight();
        r5 = r5 - r9;
        r9 = org.telegram.ui.ActionBar.ActionBar.getCurrentActionBarHeight();
        r5 = r5 - r9;
        r5 = (float) r5;
        r10.setTranslationY(r5);
    L_0x0091:
        r5 = r8.getMeasuredWidth();
        r5 = r5 / r0;
        if (r6 <= r5) goto L_0x00b5;
    L_0x0098:
        r5 = r10.currentType;
        if (r5 != r7) goto L_0x00a9;
    L_0x009c:
        r3 = (float) r3;
        r5 = r10.getMeasuredWidth();
        r5 = (float) r5;
        r7 = NUM; // 0x3fCLASSNAME float:1.5 double:5.28426686E-315;
        r5 = r5 * r7;
        r3 = r3 - r5;
        r3 = (int) r3;
        goto L_0x00c9;
    L_0x00a9:
        r5 = r10.getMeasuredWidth();
        r3 = r3 - r5;
        r5 = NUM; // 0x41e00000 float:28.0 double:5.46040909E-315;
        r5 = org.telegram.messenger.AndroidUtilities.dp(r5);
        goto L_0x00c6;
    L_0x00b5:
        r3 = r10.currentType;
        if (r3 != r7) goto L_0x00c8;
    L_0x00b9:
        r3 = r10.getMeasuredWidth();
        r3 = r3 / r0;
        r3 = r6 - r3;
        r5 = r10.arrowImageView;
        r5 = r5.getMeasuredWidth();
    L_0x00c6:
        r3 = r3 - r5;
        goto L_0x00c9;
    L_0x00c8:
        r3 = 0;
    L_0x00c9:
        r5 = (float) r3;
        r10.setTranslationX(r5);
        r5 = NUM; // 0x41980000 float:19.0 double:5.43709615E-315;
        r5 = org.telegram.messenger.AndroidUtilities.dp(r5);
        r5 = r5 + r3;
        r3 = r6 - r5;
        r5 = r10.arrowImageView;
        r5 = r5.getMeasuredWidth();
        r5 = r5 / r0;
        r3 = r3 - r5;
        r3 = (float) r3;
        r5 = r10.arrowImageView;
        r5.setTranslationX(r3);
        r5 = r8.getMeasuredWidth();
        r5 = r5 / r0;
        r7 = NUM; // 0x41200000 float:10.0 double:5.398241246E-315;
        if (r6 <= r5) goto L_0x010c;
    L_0x00ed:
        r5 = org.telegram.messenger.AndroidUtilities.dp(r7);
        r5 = (float) r5;
        r5 = (r3 > r5 ? 1 : (r3 == r5 ? 0 : -1));
        if (r5 >= 0) goto L_0x0151;
    L_0x00f6:
        r5 = org.telegram.messenger.AndroidUtilities.dp(r7);
        r5 = (float) r5;
        r5 = r3 - r5;
        r6 = r10.getTranslationX();
        r6 = r6 + r5;
        r10.setTranslationX(r6);
        r6 = r10.arrowImageView;
        r3 = r3 - r5;
        r6.setTranslationX(r3);
        goto L_0x0151;
    L_0x010c:
        r5 = r10.getMeasuredWidth();
        r6 = NUM; // 0x41CLASSNAME float:24.0 double:5.450047783E-315;
        r8 = org.telegram.messenger.AndroidUtilities.dp(r6);
        r5 = r5 - r8;
        r5 = (float) r5;
        r5 = (r3 > r5 ? 1 : (r3 == r5 ? 0 : -1));
        if (r5 <= 0) goto L_0x0133;
    L_0x011c:
        r5 = r10.getMeasuredWidth();
        r5 = (float) r5;
        r5 = r3 - r5;
        r6 = org.telegram.messenger.AndroidUtilities.dp(r6);
        r6 = (float) r6;
        r5 = r5 + r6;
        r10.setTranslationX(r5);
        r6 = r10.arrowImageView;
        r3 = r3 - r5;
        r6.setTranslationX(r3);
        goto L_0x0151;
    L_0x0133:
        r5 = org.telegram.messenger.AndroidUtilities.dp(r7);
        r5 = (float) r5;
        r5 = (r3 > r5 ? 1 : (r3 == r5 ? 0 : -1));
        if (r5 >= 0) goto L_0x0151;
    L_0x013c:
        r5 = org.telegram.messenger.AndroidUtilities.dp(r7);
        r5 = (float) r5;
        r5 = r3 - r5;
        r6 = r10.getTranslationX();
        r6 = r6 + r5;
        r10.setTranslationX(r6);
        r6 = r10.arrowImageView;
        r3 = r3 - r5;
        r6.setTranslationX(r3);
    L_0x0151:
        r10.currentView = r11;
        r11 = r10.animatorSet;
        if (r11 == 0) goto L_0x015c;
    L_0x0157:
        r11.cancel();
        r10.animatorSet = r2;
    L_0x015c:
        r11 = java.lang.Integer.valueOf(r4);
        r10.setTag(r11);
        r10.setVisibility(r1);
        if (r12 == 0) goto L_0x019a;
    L_0x0168:
        r11 = new android.animation.AnimatorSet;
        r11.<init>();
        r10.animatorSet = r11;
        r11 = r10.animatorSet;
        r12 = new android.animation.Animator[r4];
        r0 = new float[r0];
        r0 = {0, NUM};
        r2 = "alpha";
        r0 = android.animation.ObjectAnimator.ofFloat(r10, r2, r0);
        r12[r1] = r0;
        r11.playTogether(r12);
        r11 = r10.animatorSet;
        r12 = new org.telegram.ui.Components.HintView$2;
        r12.<init>();
        r11.addListener(r12);
        r11 = r10.animatorSet;
        r0 = 300; // 0x12c float:4.2E-43 double:1.48E-321;
        r11.setDuration(r0);
        r11 = r10.animatorSet;
        r11.start();
        goto L_0x019f;
    L_0x019a:
        r11 = NUM; // 0x3var_ float:1.0 double:5.263544247E-315;
        r10.setAlpha(r11);
    L_0x019f:
        return r4;
    L_0x01a0:
        return r1;
        */
        throw new UnsupportedOperationException("Method not decompiled: org.telegram.ui.Components.HintView.showForView(android.view.View, boolean):boolean");
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
}
