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
import org.telegram.messenger.ImageReceiver;
import org.telegram.messenger.LocaleController;
import org.telegram.messenger.MessageObject;
import org.telegram.ui.ActionBar.Theme;
import org.telegram.ui.Cells.ChatMessageCell;
import org.telegram.ui.Components.HintView;

public class HintView extends FrameLayout {
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
        this.currentType = i2;
        this.isTopArrow = z2;
        this.textView = new CorrectlyMeasuringTextView(context2);
        this.textView.setTextColor(Theme.getColor("chat_gifSaveHintText"));
        this.textView.setTextSize(1, 14.0f);
        this.textView.setMaxLines(2);
        this.textView.setMaxWidth(AndroidUtilities.dp(250.0f));
        if (this.currentType == 3) {
            this.textView.setGravity(19);
            this.textView.setBackground(Theme.createRoundRectDrawable(AndroidUtilities.dp(5.0f), Theme.getColor("chat_gifSaveHintBackground")));
            this.textView.setPadding(AndroidUtilities.dp(10.0f), 0, AndroidUtilities.dp(10.0f), 0);
            addView(this.textView, LayoutHelper.createFrame(-2, 30.0f, 51, 0.0f, z2 ? 6.0f : 0.0f, 0.0f, z2 ? 0.0f : 6.0f));
        } else {
            this.textView.setGravity(51);
            this.textView.setBackground(Theme.createRoundRectDrawable(AndroidUtilities.dp(3.0f), Theme.getColor("chat_gifSaveHintBackground")));
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
        int i;
        int i2;
        if ((this.currentType == 0 && getTag() != null) || this.messageCell == chatMessageCell) {
            return false;
        }
        Runnable runnable = this.hideRunnable;
        if (runnable != null) {
            AndroidUtilities.cancelRunOnUIThread(runnable);
            this.hideRunnable = null;
        }
        int top = chatMessageCell.getTop();
        View view = (View) chatMessageCell.getParent();
        if (this.currentType == 0) {
            ImageReceiver photoImage = chatMessageCell.getPhotoImage();
            i2 = top + photoImage.getImageY();
            int imageHeight = photoImage.getImageHeight();
            int i3 = i2 + imageHeight;
            int measuredHeight = view.getMeasuredHeight();
            if (i2 <= getMeasuredHeight() + AndroidUtilities.dp(10.0f) || i3 > measuredHeight + (imageHeight / 4)) {
                return false;
            }
            i = chatMessageCell.getNoSoundIconCenterX();
        } else {
            MessageObject messageObject = chatMessageCell.getMessageObject();
            String str = this.overrideText;
            if (str == null) {
                this.textView.setText(LocaleController.getString("HidAccount", NUM));
            } else {
                this.textView.setText(str);
            }
            measure(View.MeasureSpec.makeMeasureSpec(1000, Integer.MIN_VALUE), View.MeasureSpec.makeMeasureSpec(1000, Integer.MIN_VALUE));
            i2 = top + AndroidUtilities.dp(22.0f);
            if (!messageObject.isOutOwner() && chatMessageCell.isDrawNameLayout()) {
                i2 += AndroidUtilities.dp(20.0f);
            }
            if (!this.isTopArrow && i2 <= getMeasuredHeight() + AndroidUtilities.dp(10.0f)) {
                return false;
            }
            i = chatMessageCell.getForwardNameCenterX();
        }
        int measuredWidth = view.getMeasuredWidth();
        if (this.isTopArrow) {
            setTranslationY((float) AndroidUtilities.dp(44.0f));
        } else {
            setTranslationY((float) (i2 - getMeasuredHeight()));
        }
        int left = chatMessageCell.getLeft() + i;
        int dp = AndroidUtilities.dp(19.0f);
        if (left > view.getMeasuredWidth() / 2) {
            int measuredWidth2 = (measuredWidth - getMeasuredWidth()) - AndroidUtilities.dp(38.0f);
            setTranslationX((float) measuredWidth2);
            dp += measuredWidth2;
        } else {
            setTranslationX(0.0f);
        }
        float left2 = (float) (((chatMessageCell.getLeft() + i) - dp) - (this.arrowImageView.getMeasuredWidth() / 2));
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
        this.messageCell = chatMessageCell;
        AnimatorSet animatorSet2 = this.animatorSet;
        if (animatorSet2 != null) {
            animatorSet2.cancel();
            this.animatorSet = null;
        }
        setTag(1);
        setVisibility(0);
        if (z) {
            this.animatorSet = new AnimatorSet();
            this.animatorSet.playTogether(new Animator[]{ObjectAnimator.ofFloat(this, "alpha", new float[]{0.0f, 1.0f})});
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

    /* JADX WARNING: Removed duplicated region for block: B:35:0x00ed  */
    /* JADX WARNING: Removed duplicated region for block: B:38:0x010c  */
    /* JADX WARNING: Removed duplicated region for block: B:46:0x0157  */
    /* JADX WARNING: Removed duplicated region for block: B:49:0x0168  */
    /* JADX WARNING: Removed duplicated region for block: B:50:0x019a  */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public boolean showForView(android.view.View r11, boolean r12) {
        /*
            r10 = this;
            android.view.View r0 = r10.currentView
            r1 = 0
            if (r0 == r11) goto L_0x01a0
            java.lang.Object r0 = r10.getTag()
            if (r0 == 0) goto L_0x000d
            goto L_0x01a0
        L_0x000d:
            java.lang.Runnable r0 = r10.hideRunnable
            r2 = 0
            if (r0 == 0) goto L_0x0017
            org.telegram.messenger.AndroidUtilities.cancelRunOnUIThread(r0)
            r10.hideRunnable = r2
        L_0x0017:
            r0 = -2147483648(0xfffffffvar_, float:-0.0)
            r3 = 1000(0x3e8, float:1.401E-42)
            int r4 = android.view.View.MeasureSpec.makeMeasureSpec(r3, r0)
            int r0 = android.view.View.MeasureSpec.makeMeasureSpec(r3, r0)
            r10.measure(r4, r0)
            r0 = 2
            int[] r3 = new int[r0]
            r11.getLocationInWindow(r3)
            r4 = 1
            r5 = r3[r4]
            r6 = 1082130432(0x40800000, float:4.0)
            int r6 = org.telegram.messenger.AndroidUtilities.dp(r6)
            int r5 = r5 - r6
            int r6 = r10.currentType
            r7 = 3
            if (r6 != r7) goto L_0x0050
            boolean r6 = r11 instanceof org.telegram.ui.ActionBar.SimpleTextView
            if (r6 == 0) goto L_0x004a
            r6 = r3[r1]
            r8 = r11
            org.telegram.ui.ActionBar.SimpleTextView r8 = (org.telegram.ui.ActionBar.SimpleTextView) r8
            int r8 = r8.getTextWidth()
            int r8 = r8 / r0
            goto L_0x0057
        L_0x004a:
            java.lang.IllegalArgumentException r11 = new java.lang.IllegalArgumentException
            r11.<init>()
            throw r11
        L_0x0050:
            r6 = r3[r1]
            int r8 = r11.getMeasuredWidth()
            int r8 = r8 / r0
        L_0x0057:
            int r6 = r6 + r8
            android.view.ViewParent r8 = r10.getParent()
            android.view.View r8 = (android.view.View) r8
            r8.getLocationInWindow(r3)
            r9 = r3[r1]
            int r6 = r6 - r9
            r3 = r3[r4]
            int r5 = r5 - r3
            int r3 = android.os.Build.VERSION.SDK_INT
            r9 = 21
            if (r3 < r9) goto L_0x0070
            int r3 = org.telegram.messenger.AndroidUtilities.statusBarHeight
            int r5 = r5 - r3
        L_0x0070:
            int r3 = r8.getMeasuredWidth()
            boolean r9 = r10.isTopArrow
            if (r9 == 0) goto L_0x0083
            r5 = 1110441984(0x42300000, float:44.0)
            int r5 = org.telegram.messenger.AndroidUtilities.dp(r5)
            float r5 = (float) r5
            r10.setTranslationY(r5)
            goto L_0x0091
        L_0x0083:
            int r9 = r10.getMeasuredHeight()
            int r5 = r5 - r9
            int r9 = org.telegram.ui.ActionBar.ActionBar.getCurrentActionBarHeight()
            int r5 = r5 - r9
            float r5 = (float) r5
            r10.setTranslationY(r5)
        L_0x0091:
            int r5 = r8.getMeasuredWidth()
            int r5 = r5 / r0
            if (r6 <= r5) goto L_0x00b5
            int r5 = r10.currentType
            if (r5 != r7) goto L_0x00a9
            float r3 = (float) r3
            int r5 = r10.getMeasuredWidth()
            float r5 = (float) r5
            r7 = 1069547520(0x3fCLASSNAME, float:1.5)
            float r5 = r5 * r7
            float r3 = r3 - r5
            int r3 = (int) r3
            goto L_0x00c9
        L_0x00a9:
            int r5 = r10.getMeasuredWidth()
            int r3 = r3 - r5
            r5 = 1105199104(0x41e00000, float:28.0)
            int r5 = org.telegram.messenger.AndroidUtilities.dp(r5)
            goto L_0x00c6
        L_0x00b5:
            int r3 = r10.currentType
            if (r3 != r7) goto L_0x00c8
            int r3 = r10.getMeasuredWidth()
            int r3 = r3 / r0
            int r3 = r6 - r3
            android.widget.ImageView r5 = r10.arrowImageView
            int r5 = r5.getMeasuredWidth()
        L_0x00c6:
            int r3 = r3 - r5
            goto L_0x00c9
        L_0x00c8:
            r3 = 0
        L_0x00c9:
            float r5 = (float) r3
            r10.setTranslationX(r5)
            r5 = 1100480512(0x41980000, float:19.0)
            int r5 = org.telegram.messenger.AndroidUtilities.dp(r5)
            int r5 = r5 + r3
            int r3 = r6 - r5
            android.widget.ImageView r5 = r10.arrowImageView
            int r5 = r5.getMeasuredWidth()
            int r5 = r5 / r0
            int r3 = r3 - r5
            float r3 = (float) r3
            android.widget.ImageView r5 = r10.arrowImageView
            r5.setTranslationX(r3)
            int r5 = r8.getMeasuredWidth()
            int r5 = r5 / r0
            r7 = 1092616192(0x41200000, float:10.0)
            if (r6 <= r5) goto L_0x010c
            int r5 = org.telegram.messenger.AndroidUtilities.dp(r7)
            float r5 = (float) r5
            int r5 = (r3 > r5 ? 1 : (r3 == r5 ? 0 : -1))
            if (r5 >= 0) goto L_0x0151
            int r5 = org.telegram.messenger.AndroidUtilities.dp(r7)
            float r5 = (float) r5
            float r5 = r3 - r5
            float r6 = r10.getTranslationX()
            float r6 = r6 + r5
            r10.setTranslationX(r6)
            android.widget.ImageView r6 = r10.arrowImageView
            float r3 = r3 - r5
            r6.setTranslationX(r3)
            goto L_0x0151
        L_0x010c:
            int r5 = r10.getMeasuredWidth()
            r6 = 1103101952(0x41CLASSNAME, float:24.0)
            int r8 = org.telegram.messenger.AndroidUtilities.dp(r6)
            int r5 = r5 - r8
            float r5 = (float) r5
            int r5 = (r3 > r5 ? 1 : (r3 == r5 ? 0 : -1))
            if (r5 <= 0) goto L_0x0133
            int r5 = r10.getMeasuredWidth()
            float r5 = (float) r5
            float r5 = r3 - r5
            int r6 = org.telegram.messenger.AndroidUtilities.dp(r6)
            float r6 = (float) r6
            float r5 = r5 + r6
            r10.setTranslationX(r5)
            android.widget.ImageView r6 = r10.arrowImageView
            float r3 = r3 - r5
            r6.setTranslationX(r3)
            goto L_0x0151
        L_0x0133:
            int r5 = org.telegram.messenger.AndroidUtilities.dp(r7)
            float r5 = (float) r5
            int r5 = (r3 > r5 ? 1 : (r3 == r5 ? 0 : -1))
            if (r5 >= 0) goto L_0x0151
            int r5 = org.telegram.messenger.AndroidUtilities.dp(r7)
            float r5 = (float) r5
            float r5 = r3 - r5
            float r6 = r10.getTranslationX()
            float r6 = r6 + r5
            r10.setTranslationX(r6)
            android.widget.ImageView r6 = r10.arrowImageView
            float r3 = r3 - r5
            r6.setTranslationX(r3)
        L_0x0151:
            r10.currentView = r11
            android.animation.AnimatorSet r11 = r10.animatorSet
            if (r11 == 0) goto L_0x015c
            r11.cancel()
            r10.animatorSet = r2
        L_0x015c:
            java.lang.Integer r11 = java.lang.Integer.valueOf(r4)
            r10.setTag(r11)
            r10.setVisibility(r1)
            if (r12 == 0) goto L_0x019a
            android.animation.AnimatorSet r11 = new android.animation.AnimatorSet
            r11.<init>()
            r10.animatorSet = r11
            android.animation.AnimatorSet r11 = r10.animatorSet
            android.animation.Animator[] r12 = new android.animation.Animator[r4]
            float[] r0 = new float[r0]
            r0 = {0, NUM} // fill-array
            java.lang.String r2 = "alpha"
            android.animation.ObjectAnimator r0 = android.animation.ObjectAnimator.ofFloat(r10, r2, r0)
            r12[r1] = r0
            r11.playTogether(r12)
            android.animation.AnimatorSet r11 = r10.animatorSet
            org.telegram.ui.Components.HintView$2 r12 = new org.telegram.ui.Components.HintView$2
            r12.<init>()
            r11.addListener(r12)
            android.animation.AnimatorSet r11 = r10.animatorSet
            r0 = 300(0x12c, double:1.48E-321)
            r11.setDuration(r0)
            android.animation.AnimatorSet r11 = r10.animatorSet
            r11.start()
            goto L_0x019f
        L_0x019a:
            r11 = 1065353216(0x3var_, float:1.0)
            r10.setAlpha(r11)
        L_0x019f:
            return r4
        L_0x01a0:
            return r1
        */
        throw new UnsupportedOperationException("Method not decompiled: org.telegram.ui.Components.HintView.showForView(android.view.View, boolean):boolean");
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
            this.animatorSet.playTogether(new Animator[]{ObjectAnimator.ofFloat(this, "alpha", new float[]{0.0f})});
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
}
