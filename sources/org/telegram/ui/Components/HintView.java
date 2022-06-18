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

public class HintView extends FrameLayout {
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

    public HintView(Context context, int i) {
        this(context, i, false, (Theme.ResourcesProvider) null);
    }

    public HintView(Context context, int i, boolean z) {
        this(context, i, z, (Theme.ResourcesProvider) null);
    }

    public HintView(Context context, int i, Theme.ResourcesProvider resourcesProvider2) {
        this(context, i, false, resourcesProvider2);
    }

    /* JADX INFO: super call moved to the top of the method (can break code semantics) */
    public HintView(Context context, int i, boolean z, Theme.ResourcesProvider resourcesProvider2) {
        super(context);
        Context context2 = context;
        int i2 = i;
        boolean z2 = z;
        this.showingDuration = 2000;
        this.resourcesProvider = resourcesProvider2;
        this.currentType = i2;
        this.isTopArrow = z2;
        CorrectlyMeasuringTextView correctlyMeasuringTextView = new CorrectlyMeasuringTextView(context2);
        this.textView = correctlyMeasuringTextView;
        correctlyMeasuringTextView.setTextColor(getThemedColor("chat_gifSaveHintText"));
        this.textView.setTextSize(1, 14.0f);
        this.textView.setMaxLines(2);
        if (i2 == 7 || i2 == 8 || i2 == 9) {
            this.textView.setMaxWidth(AndroidUtilities.dp(310.0f));
        } else if (i2 == 4) {
            this.textView.setMaxWidth(AndroidUtilities.dp(280.0f));
        } else {
            this.textView.setMaxWidth(AndroidUtilities.dp(250.0f));
        }
        if (this.currentType == 3) {
            this.textView.setGravity(19);
            this.textView.setBackground(Theme.createRoundRectDrawable(AndroidUtilities.dp(5.0f), getThemedColor("chat_gifSaveHintBackground")));
            this.textView.setPadding(AndroidUtilities.dp(10.0f), 0, AndroidUtilities.dp(10.0f), 0);
            addView(this.textView, LayoutHelper.createFrame(-2, 30.0f, 51, 0.0f, z2 ? 6.0f : 0.0f, 0.0f, z2 ? 0.0f : 6.0f));
        } else {
            this.textView.setGravity(51);
            this.textView.setBackground(Theme.createRoundRectDrawable(AndroidUtilities.dp(6.0f), getThemedColor("chat_gifSaveHintBackground")));
            this.textView.setPadding(AndroidUtilities.dp(this.currentType == 0 ? 54.0f : 8.0f), AndroidUtilities.dp(7.0f), AndroidUtilities.dp(8.0f), AndroidUtilities.dp(8.0f));
            addView(this.textView, LayoutHelper.createFrame(-2, -2.0f, 51, 0.0f, z2 ? 6.0f : 0.0f, 0.0f, z2 ? 0.0f : 6.0f));
        }
        if (i2 == 0) {
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
        imageView3.setImageResource(z2 ? NUM : NUM);
        this.arrowImageView.setColorFilter(new PorterDuffColorFilter(getThemedColor("chat_gifSaveHintBackground"), PorterDuff.Mode.MULTIPLY));
        addView(this.arrowImageView, LayoutHelper.createFrame(14, 6.0f, (z2 ? 48 : 80) | 3, 0.0f, 0.0f, 0.0f, 0.0f));
    }

    public void setBackgroundColor(int i, int i2) {
        this.textView.setTextColor(i2);
        this.arrowImageView.setColorFilter(new PorterDuffColorFilter(i, PorterDuff.Mode.MULTIPLY));
        TextView textView2 = this.textView;
        int i3 = this.currentType;
        textView2.setBackground(Theme.createRoundRectDrawable(AndroidUtilities.dp((i3 == 7 || i3 == 8) ? 6.0f : 3.0f), i));
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

    public void setExtraTranslationY(float f) {
        this.extraTranslationY = f;
        setTranslationY(f + this.translationY);
    }

    public float getBaseTranslationY() {
        return this.translationY;
    }

    public boolean showForMessageCell(ChatMessageCell chatMessageCell, boolean z) {
        return showForMessageCell(chatMessageCell, (Object) null, 0, 0, z);
    }

    /* JADX WARNING: Removed duplicated region for block: B:59:0x017e A[RETURN] */
    /* JADX WARNING: Removed duplicated region for block: B:60:0x017f  */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public boolean showForMessageCell(org.telegram.ui.Cells.ChatMessageCell r17, java.lang.Object r18, int r19, int r20, boolean r21) {
        /*
            r16 = this;
            r0 = r16
            r1 = r17
            r2 = r20
            int r3 = r0.currentType
            r4 = 5
            r5 = 0
            if (r3 != r4) goto L_0x0014
            int r6 = r0.shownY
            if (r2 != r6) goto L_0x0014
            org.telegram.ui.Cells.ChatMessageCell r6 = r0.messageCell
            if (r6 == r1) goto L_0x0022
        L_0x0014:
            if (r3 == r4) goto L_0x0023
            if (r3 != 0) goto L_0x001e
            java.lang.Object r3 = r16.getTag()
            if (r3 != 0) goto L_0x0022
        L_0x001e:
            org.telegram.ui.Cells.ChatMessageCell r3 = r0.messageCell
            if (r3 != r1) goto L_0x0023
        L_0x0022:
            return r5
        L_0x0023:
            java.lang.Runnable r3 = r0.hideRunnable
            r6 = 0
            if (r3 == 0) goto L_0x002d
            org.telegram.messenger.AndroidUtilities.cancelRunOnUIThread(r3)
            r0.hideRunnable = r6
        L_0x002d:
            r3 = 2
            int[] r7 = new int[r3]
            r1.getLocationInWindow(r7)
            r8 = 1
            r9 = r7[r8]
            android.view.ViewParent r10 = r16.getParent()
            android.view.View r10 = (android.view.View) r10
            r10.getLocationInWindow(r7)
            r7 = r7[r8]
            int r9 = r9 - r7
            android.view.ViewParent r7 = r17.getParent()
            android.view.View r7 = (android.view.View) r7
            int r10 = r0.currentType
            r11 = 1092616192(0x41200000, float:10.0)
            if (r10 != 0) goto L_0x007c
            org.telegram.messenger.ImageReceiver r2 = r17.getPhotoImage()
            float r9 = (float) r9
            float r10 = r2.getImageY()
            float r9 = r9 + r10
            int r9 = (int) r9
            float r2 = r2.getImageHeight()
            int r2 = (int) r2
            int r10 = r9 + r2
            int r12 = r7.getMeasuredHeight()
            int r13 = r16.getMeasuredHeight()
            int r14 = org.telegram.messenger.AndroidUtilities.dp(r11)
            int r13 = r13 + r14
            if (r9 <= r13) goto L_0x007b
            int r2 = r2 / 4
            int r12 = r12 + r2
            if (r10 <= r12) goto L_0x0075
            goto L_0x007b
        L_0x0075:
            int r2 = r17.getNoSoundIconCenterX()
            goto L_0x0183
        L_0x007b:
            return r5
        L_0x007c:
            r12 = -2147483648(0xfffffffvar_, float:-0.0)
            r13 = 1000(0x3e8, float:1.401E-42)
            if (r10 != r4) goto L_0x0105
            r10 = r18
            java.lang.Integer r10 = (java.lang.Integer) r10
            int r9 = r9 + r2
            r0.shownY = r2
            int r2 = r10.intValue()
            r14 = -1
            if (r2 != r14) goto L_0x009f
            android.widget.TextView r2 = r0.textView
            r10 = 2131627578(0x7f0e0e3a, float:1.8882424E38)
            java.lang.String r14 = "PollSelectOption"
            java.lang.String r10 = org.telegram.messenger.LocaleController.getString(r14, r10)
            r2.setText(r10)
            goto L_0x00f6
        L_0x009f:
            org.telegram.messenger.MessageObject r2 = r17.getMessageObject()
            boolean r2 = r2.isQuiz()
            if (r2 == 0) goto L_0x00d0
            int r2 = r10.intValue()
            if (r2 != 0) goto L_0x00be
            android.widget.TextView r2 = r0.textView
            r10 = 2131626871(0x7f0e0b77, float:1.888099E38)
            java.lang.String r14 = "NoVotesQuiz"
            java.lang.String r10 = org.telegram.messenger.LocaleController.getString(r14, r10)
            r2.setText(r10)
            goto L_0x00f6
        L_0x00be:
            android.widget.TextView r2 = r0.textView
            int r10 = r10.intValue()
            java.lang.Object[] r14 = new java.lang.Object[r5]
            java.lang.String r15 = "Answer"
            java.lang.String r10 = org.telegram.messenger.LocaleController.formatPluralString(r15, r10, r14)
            r2.setText(r10)
            goto L_0x00f6
        L_0x00d0:
            int r2 = r10.intValue()
            if (r2 != 0) goto L_0x00e5
            android.widget.TextView r2 = r0.textView
            r10 = 2131626870(0x7f0e0b76, float:1.8880988E38)
            java.lang.String r14 = "NoVotes"
            java.lang.String r10 = org.telegram.messenger.LocaleController.getString(r14, r10)
            r2.setText(r10)
            goto L_0x00f6
        L_0x00e5:
            android.widget.TextView r2 = r0.textView
            int r10 = r10.intValue()
            java.lang.Object[] r14 = new java.lang.Object[r5]
            java.lang.String r15 = "Vote"
            java.lang.String r10 = org.telegram.messenger.LocaleController.formatPluralString(r15, r10, r14)
            r2.setText(r10)
        L_0x00f6:
            int r2 = android.view.View.MeasureSpec.makeMeasureSpec(r13, r12)
            int r10 = android.view.View.MeasureSpec.makeMeasureSpec(r13, r12)
            r0.measure(r2, r10)
            r2 = r19
            goto L_0x0183
        L_0x0105:
            org.telegram.messenger.MessageObject r2 = r17.getMessageObject()
            java.lang.String r10 = r0.overrideText
            if (r10 != 0) goto L_0x011c
            android.widget.TextView r10 = r0.textView
            r14 = 2131626122(0x7f0e088a, float:1.8879471E38)
            java.lang.String r15 = "HidAccount"
            java.lang.String r14 = org.telegram.messenger.LocaleController.getString(r15, r14)
            r10.setText(r14)
            goto L_0x0121
        L_0x011c:
            android.widget.TextView r14 = r0.textView
            r14.setText(r10)
        L_0x0121:
            int r10 = android.view.View.MeasureSpec.makeMeasureSpec(r13, r12)
            int r12 = android.view.View.MeasureSpec.makeMeasureSpec(r13, r12)
            r0.measure(r10, r12)
            org.telegram.tgnet.TLRPC$User r10 = r17.getCurrentUser()
            if (r10 == 0) goto L_0x0155
            long r12 = r10.id
            r14 = 0
            int r10 = (r12 > r14 ? 1 : (r12 == r14 ? 0 : -1))
            if (r10 != 0) goto L_0x0155
            int r2 = r17.getMeasuredHeight()
            int r10 = r17.getBottom()
            int r12 = r7.getMeasuredHeight()
            int r10 = r10 - r12
            int r10 = java.lang.Math.max(r5, r10)
            int r2 = r2 - r10
            r10 = 1112014848(0x42480000, float:50.0)
            int r10 = org.telegram.messenger.AndroidUtilities.dp(r10)
            int r2 = r2 - r10
        L_0x0153:
            int r9 = r9 + r2
            goto L_0x016f
        L_0x0155:
            r10 = 1102053376(0x41b00000, float:22.0)
            int r10 = org.telegram.messenger.AndroidUtilities.dp(r10)
            int r9 = r9 + r10
            boolean r2 = r2.isOutOwner()
            if (r2 != 0) goto L_0x016f
            boolean r2 = r17.isDrawNameLayout()
            if (r2 == 0) goto L_0x016f
            r2 = 1101004800(0x41a00000, float:20.0)
            int r2 = org.telegram.messenger.AndroidUtilities.dp(r2)
            goto L_0x0153
        L_0x016f:
            boolean r2 = r0.isTopArrow
            if (r2 != 0) goto L_0x017f
            int r2 = r16.getMeasuredHeight()
            int r10 = org.telegram.messenger.AndroidUtilities.dp(r11)
            int r2 = r2 + r10
            if (r9 > r2) goto L_0x017f
            return r5
        L_0x017f:
            int r2 = r17.getForwardNameCenterX()
        L_0x0183:
            int r10 = r7.getMeasuredWidth()
            boolean r12 = r0.isTopArrow
            if (r12 == 0) goto L_0x019b
            float r9 = r0.extraTranslationY
            r12 = 1110441984(0x42300000, float:44.0)
            int r12 = org.telegram.messenger.AndroidUtilities.dp(r12)
            float r12 = (float) r12
            r0.translationY = r12
            float r9 = r9 + r12
            r0.setTranslationY(r9)
            goto L_0x01a9
        L_0x019b:
            float r12 = r0.extraTranslationY
            int r13 = r16.getMeasuredHeight()
            int r9 = r9 - r13
            float r9 = (float) r9
            r0.translationY = r9
            float r12 = r12 + r9
            r0.setTranslationY(r12)
        L_0x01a9:
            int r9 = r17.getLeft()
            int r9 = r9 + r2
            r12 = 1100480512(0x41980000, float:19.0)
            int r12 = org.telegram.messenger.AndroidUtilities.dp(r12)
            int r13 = r0.currentType
            if (r13 != r4) goto L_0x01d1
            int r4 = r16.getMeasuredWidth()
            int r4 = r4 / r3
            int r4 = r2 - r4
            r10 = 1100532941(0x4198cccd, float:19.1)
            int r10 = org.telegram.messenger.AndroidUtilities.dp(r10)
            int r4 = r4 - r10
            int r4 = java.lang.Math.max(r5, r4)
            float r10 = (float) r4
            r0.setTranslationX(r10)
            int r12 = r12 + r4
            goto L_0x01ee
        L_0x01d1:
            int r4 = r7.getMeasuredWidth()
            int r4 = r4 / r3
            if (r9 <= r4) goto L_0x01ea
            int r4 = r16.getMeasuredWidth()
            int r10 = r10 - r4
            r4 = 1108869120(0x42180000, float:38.0)
            int r4 = org.telegram.messenger.AndroidUtilities.dp(r4)
            int r10 = r10 - r4
            float r4 = (float) r10
            r0.setTranslationX(r4)
            int r12 = r12 + r10
            goto L_0x01ee
        L_0x01ea:
            r4 = 0
            r0.setTranslationX(r4)
        L_0x01ee:
            int r4 = r17.getLeft()
            int r4 = r4 + r2
            int r4 = r4 - r12
            android.widget.ImageView r2 = r0.arrowImageView
            int r2 = r2.getMeasuredWidth()
            int r2 = r2 / r3
            int r4 = r4 - r2
            float r2 = (float) r4
            android.widget.ImageView r4 = r0.arrowImageView
            r4.setTranslationX(r2)
            int r4 = r7.getMeasuredWidth()
            int r4 = r4 / r3
            if (r9 <= r4) goto L_0x0228
            int r4 = org.telegram.messenger.AndroidUtilities.dp(r11)
            float r4 = (float) r4
            int r4 = (r2 > r4 ? 1 : (r2 == r4 ? 0 : -1))
            if (r4 >= 0) goto L_0x026d
            int r4 = org.telegram.messenger.AndroidUtilities.dp(r11)
            float r4 = (float) r4
            float r4 = r2 - r4
            float r7 = r16.getTranslationX()
            float r7 = r7 + r4
            r0.setTranslationX(r7)
            android.widget.ImageView r7 = r0.arrowImageView
            float r2 = r2 - r4
            r7.setTranslationX(r2)
            goto L_0x026d
        L_0x0228:
            int r4 = r16.getMeasuredWidth()
            r7 = 1103101952(0x41CLASSNAME, float:24.0)
            int r9 = org.telegram.messenger.AndroidUtilities.dp(r7)
            int r4 = r4 - r9
            float r4 = (float) r4
            int r4 = (r2 > r4 ? 1 : (r2 == r4 ? 0 : -1))
            if (r4 <= 0) goto L_0x024f
            int r4 = r16.getMeasuredWidth()
            float r4 = (float) r4
            float r4 = r2 - r4
            int r7 = org.telegram.messenger.AndroidUtilities.dp(r7)
            float r7 = (float) r7
            float r4 = r4 + r7
            r0.setTranslationX(r4)
            android.widget.ImageView r7 = r0.arrowImageView
            float r2 = r2 - r4
            r7.setTranslationX(r2)
            goto L_0x026d
        L_0x024f:
            int r4 = org.telegram.messenger.AndroidUtilities.dp(r11)
            float r4 = (float) r4
            int r4 = (r2 > r4 ? 1 : (r2 == r4 ? 0 : -1))
            if (r4 >= 0) goto L_0x026d
            int r4 = org.telegram.messenger.AndroidUtilities.dp(r11)
            float r4 = (float) r4
            float r4 = r2 - r4
            float r7 = r16.getTranslationX()
            float r7 = r7 + r4
            r0.setTranslationX(r7)
            android.widget.ImageView r7 = r0.arrowImageView
            float r2 = r2 - r4
            r7.setTranslationX(r2)
        L_0x026d:
            r0.messageCell = r1
            android.animation.AnimatorSet r1 = r0.animatorSet
            if (r1 == 0) goto L_0x0278
            r1.cancel()
            r0.animatorSet = r6
        L_0x0278:
            java.lang.Integer r1 = java.lang.Integer.valueOf(r8)
            r0.setTag(r1)
            r0.setVisibility(r5)
            if (r21 == 0) goto L_0x02b4
            android.animation.AnimatorSet r1 = new android.animation.AnimatorSet
            r1.<init>()
            r0.animatorSet = r1
            android.animation.Animator[] r2 = new android.animation.Animator[r8]
            android.util.Property r4 = android.view.View.ALPHA
            float[] r3 = new float[r3]
            r3 = {0, NUM} // fill-array
            android.animation.ObjectAnimator r3 = android.animation.ObjectAnimator.ofFloat(r0, r4, r3)
            r2[r5] = r3
            r1.playTogether(r2)
            android.animation.AnimatorSet r1 = r0.animatorSet
            org.telegram.ui.Components.HintView$1 r2 = new org.telegram.ui.Components.HintView$1
            r2.<init>()
            r1.addListener(r2)
            android.animation.AnimatorSet r1 = r0.animatorSet
            r2 = 300(0x12c, double:1.48E-321)
            r1.setDuration(r2)
            android.animation.AnimatorSet r1 = r0.animatorSet
            r1.start()
            goto L_0x02b9
        L_0x02b4:
            r1 = 1065353216(0x3var_, float:1.0)
            r0.setAlpha(r1)
        L_0x02b9:
            return r8
        */
        throw new UnsupportedOperationException("Method not decompiled: org.telegram.ui.Components.HintView.showForMessageCell(org.telegram.ui.Cells.ChatMessageCell, java.lang.Object, int, int, boolean):boolean");
    }

    public boolean showForView(View view, boolean z) {
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
        if (z) {
            AnimatorSet animatorSet3 = new AnimatorSet();
            this.animatorSet = animatorSet3;
            animatorSet3.playTogether(new Animator[]{ObjectAnimator.ofFloat(this, View.ALPHA, new float[]{0.0f, 1.0f})});
            this.animatorSet.addListener(new AnimatorListenerAdapter() {
                public void onAnimationEnd(Animator animator) {
                    AnimatorSet unused = HintView.this.animatorSet = null;
                    AndroidUtilities.runOnUIThread(HintView.this.hideRunnable = new HintView$2$$ExternalSyntheticLambda0(this), HintView.this.showingDuration);
                }

                /* access modifiers changed from: private */
                public /* synthetic */ void lambda$onAnimationEnd$0() {
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

    /* JADX WARNING: Code restructure failed: missing block: B:54:0x0140, code lost:
        if (r1 < 0) goto L_0x0162;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:59:0x015e, code lost:
        if (r1 >= 0) goto L_0x0161;
     */
    /* JADX WARNING: Removed duplicated region for block: B:18:0x0072  */
    /* JADX WARNING: Removed duplicated region for block: B:29:0x00ae  */
    /* JADX WARNING: Removed duplicated region for block: B:42:0x0104  */
    /* JADX WARNING: Removed duplicated region for block: B:43:0x0115  */
    /* JADX WARNING: Removed duplicated region for block: B:48:0x011f  */
    /* JADX WARNING: Removed duplicated region for block: B:49:0x0129  */
    /* JADX WARNING: Removed duplicated region for block: B:63:0x0176  */
    /* JADX WARNING: Removed duplicated region for block: B:66:0x018a  */
    /* JADX WARNING: Removed duplicated region for block: B:69:0x01a9  */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    private void updatePosition(android.view.View r14) {
        /*
            r13 = this;
            android.graphics.Point r0 = org.telegram.messenger.AndroidUtilities.displaySize
            int r0 = r0.x
            r1 = -2147483648(0xfffffffvar_, float:-0.0)
            int r0 = android.view.View.MeasureSpec.makeMeasureSpec(r0, r1)
            android.graphics.Point r2 = org.telegram.messenger.AndroidUtilities.displaySize
            int r2 = r2.x
            int r1 = android.view.View.MeasureSpec.makeMeasureSpec(r2, r1)
            r13.measure(r0, r1)
            r0 = 2
            int[] r1 = new int[r0]
            r14.getLocationInWindow(r1)
            r2 = 1
            r3 = r1[r2]
            r4 = 1082130432(0x40800000, float:4.0)
            int r5 = org.telegram.messenger.AndroidUtilities.dp(r4)
            int r3 = r3 - r5
            int r5 = r13.currentType
            r6 = 1090519040(0x41000000, float:8.0)
            r7 = 6
            r8 = 7
            r9 = 8
            r10 = 1092616192(0x41200000, float:10.0)
            r11 = 4
            if (r5 != r11) goto L_0x0038
            int r4 = org.telegram.messenger.AndroidUtilities.dp(r4)
        L_0x0036:
            int r3 = r3 + r4
            goto L_0x0068
        L_0x0038:
            if (r5 != r7) goto L_0x0049
            int r4 = r14.getMeasuredHeight()
            int r5 = r13.getMeasuredHeight()
            int r4 = r4 + r5
            int r5 = org.telegram.messenger.AndroidUtilities.dp(r10)
        L_0x0047:
            int r4 = r4 + r5
            goto L_0x0036
        L_0x0049:
            if (r5 == r8) goto L_0x005a
            if (r5 != r9) goto L_0x0052
            boolean r4 = r13.isTopArrow
            if (r4 == 0) goto L_0x0052
            goto L_0x005a
        L_0x0052:
            if (r5 != r9) goto L_0x0068
            int r4 = org.telegram.messenger.AndroidUtilities.dp(r10)
            int r3 = r3 - r4
            goto L_0x0068
        L_0x005a:
            int r4 = r14.getMeasuredHeight()
            int r5 = r13.getMeasuredHeight()
            int r4 = r4 + r5
            int r5 = org.telegram.messenger.AndroidUtilities.dp(r6)
            goto L_0x0047
        L_0x0068:
            int r4 = r13.currentType
            r5 = 3
            r11 = 0
            if (r4 != r9) goto L_0x00ae
            boolean r12 = r13.isTopArrow
            if (r12 == 0) goto L_0x00ae
            boolean r4 = r14 instanceof org.telegram.ui.ActionBar.SimpleTextView
            if (r4 == 0) goto L_0x0095
            org.telegram.ui.ActionBar.SimpleTextView r14 = (org.telegram.ui.ActionBar.SimpleTextView) r14
            android.graphics.drawable.Drawable r4 = r14.getRightDrawable()
            r12 = r1[r11]
            if (r4 == 0) goto L_0x0089
            android.graphics.Rect r14 = r4.getBounds()
            int r14 = r14.centerX()
            goto L_0x008e
        L_0x0089:
            int r14 = r14.getTextWidth()
            int r14 = r14 / r0
        L_0x008e:
            int r12 = r12 + r14
            int r14 = org.telegram.messenger.AndroidUtilities.dp(r6)
            int r12 = r12 - r14
            goto L_0x00bc
        L_0x0095:
            boolean r4 = r14 instanceof android.widget.TextView
            if (r4 == 0) goto L_0x00ab
            android.widget.TextView r14 = (android.widget.TextView) r14
            r4 = r1[r11]
            int r14 = r14.getMeasuredWidth()
            int r4 = r4 + r14
            r14 = 1099169792(0x41840000, float:16.5)
            int r14 = org.telegram.messenger.AndroidUtilities.dp(r14)
            int r12 = r4 - r14
            goto L_0x00bc
        L_0x00ab:
            r12 = r1[r11]
            goto L_0x00bc
        L_0x00ae:
            if (r4 != r5) goto L_0x00b3
            r12 = r1[r11]
            goto L_0x00bc
        L_0x00b3:
            r4 = r1[r11]
            int r14 = r14.getMeasuredWidth()
            int r14 = r14 / r0
            int r12 = r4 + r14
        L_0x00bc:
            android.view.ViewParent r14 = r13.getParent()
            android.view.View r14 = (android.view.View) r14
            r14.getLocationInWindow(r1)
            r4 = r1[r11]
            int r12 = r12 - r4
            r1 = r1[r2]
            int r3 = r3 - r1
            int r1 = r13.bottomOffset
            int r3 = r3 - r1
            int r1 = r14.getMeasuredWidth()
            boolean r2 = r13.isTopArrow
            if (r2 == 0) goto L_0x00ee
            int r2 = r13.currentType
            if (r2 == r7) goto L_0x00ee
            if (r2 == r8) goto L_0x00ee
            if (r2 == r9) goto L_0x00ee
            float r2 = r13.extraTranslationY
            r3 = 1110441984(0x42300000, float:44.0)
            int r3 = org.telegram.messenger.AndroidUtilities.dp(r3)
            float r3 = (float) r3
            r13.translationY = r3
            float r2 = r2 + r3
            r13.setTranslationY(r2)
            goto L_0x00fc
        L_0x00ee:
            float r2 = r13.extraTranslationY
            int r4 = r13.getMeasuredHeight()
            int r3 = r3 - r4
            float r3 = (float) r3
            r13.translationY = r3
            float r2 = r2 + r3
            r13.setTranslationY(r2)
        L_0x00fc:
            android.view.ViewGroup$LayoutParams r2 = r13.getLayoutParams()
            boolean r2 = r2 instanceof android.view.ViewGroup.MarginLayoutParams
            if (r2 == 0) goto L_0x0115
            android.view.ViewGroup$LayoutParams r2 = r13.getLayoutParams()
            android.view.ViewGroup$MarginLayoutParams r2 = (android.view.ViewGroup.MarginLayoutParams) r2
            int r2 = r2.leftMargin
            android.view.ViewGroup$LayoutParams r3 = r13.getLayoutParams()
            android.view.ViewGroup$MarginLayoutParams r3 = (android.view.ViewGroup.MarginLayoutParams) r3
            int r3 = r3.rightMargin
            goto L_0x0117
        L_0x0115:
            r2 = 0
            r3 = 0
        L_0x0117:
            int r4 = r13.currentType
            if (r4 != r9) goto L_0x0129
            boolean r4 = r13.isTopArrow
            if (r4 != 0) goto L_0x0129
            int r1 = r1 - r2
            int r1 = r1 - r3
            int r3 = r13.getMeasuredWidth()
            int r1 = r1 - r3
            int r11 = r1 / 2
            goto L_0x0162
        L_0x0129:
            int r4 = r14.getMeasuredWidth()
            int r4 = r4 / r0
            if (r12 <= r4) goto L_0x014c
            int r4 = r13.currentType
            if (r4 != r5) goto L_0x0143
            float r1 = (float) r1
            int r3 = r13.getMeasuredWidth()
            float r3 = (float) r3
            r4 = 1069547520(0x3fCLASSNAME, float:1.5)
            float r3 = r3 * r4
            float r1 = r1 - r3
            int r1 = (int) r1
            if (r1 >= 0) goto L_0x0161
            goto L_0x0162
        L_0x0143:
            int r4 = r13.getMeasuredWidth()
            int r1 = r1 - r4
            int r3 = r3 + r2
            int r11 = r1 - r3
            goto L_0x0162
        L_0x014c:
            int r1 = r13.currentType
            if (r1 != r5) goto L_0x0162
            int r1 = r13.getMeasuredWidth()
            int r1 = r1 / r0
            int r1 = r12 - r1
            android.widget.ImageView r3 = r13.arrowImageView
            int r3 = r3.getMeasuredWidth()
            int r1 = r1 - r3
            if (r1 >= 0) goto L_0x0161
            goto L_0x0162
        L_0x0161:
            r11 = r1
        L_0x0162:
            float r1 = (float) r11
            r13.setTranslationX(r1)
            int r2 = r2 + r11
            int r1 = r12 - r2
            android.widget.ImageView r2 = r13.arrowImageView
            int r2 = r2.getMeasuredWidth()
            int r2 = r2 / r0
            int r1 = r1 - r2
            float r1 = (float) r1
            int r2 = r13.currentType
            if (r2 != r8) goto L_0x017e
            r2 = 1073741824(0x40000000, float:2.0)
            int r2 = org.telegram.messenger.AndroidUtilities.dp(r2)
            float r2 = (float) r2
            float r1 = r1 + r2
        L_0x017e:
            android.widget.ImageView r2 = r13.arrowImageView
            r2.setTranslationX(r1)
            int r14 = r14.getMeasuredWidth()
            int r14 = r14 / r0
            if (r12 <= r14) goto L_0x01a9
            int r14 = org.telegram.messenger.AndroidUtilities.dp(r10)
            float r14 = (float) r14
            int r14 = (r1 > r14 ? 1 : (r1 == r14 ? 0 : -1))
            if (r14 >= 0) goto L_0x01ee
            int r14 = org.telegram.messenger.AndroidUtilities.dp(r10)
            float r14 = (float) r14
            float r14 = r1 - r14
            float r0 = r13.getTranslationX()
            float r0 = r0 + r14
            r13.setTranslationX(r0)
            android.widget.ImageView r0 = r13.arrowImageView
            float r1 = r1 - r14
            r0.setTranslationX(r1)
            goto L_0x01ee
        L_0x01a9:
            int r14 = r13.getMeasuredWidth()
            r0 = 1103101952(0x41CLASSNAME, float:24.0)
            int r2 = org.telegram.messenger.AndroidUtilities.dp(r0)
            int r14 = r14 - r2
            float r14 = (float) r14
            int r14 = (r1 > r14 ? 1 : (r1 == r14 ? 0 : -1))
            if (r14 <= 0) goto L_0x01d0
            int r14 = r13.getMeasuredWidth()
            float r14 = (float) r14
            float r14 = r1 - r14
            int r0 = org.telegram.messenger.AndroidUtilities.dp(r0)
            float r0 = (float) r0
            float r14 = r14 + r0
            r13.setTranslationX(r14)
            android.widget.ImageView r0 = r13.arrowImageView
            float r1 = r1 - r14
            r0.setTranslationX(r1)
            goto L_0x01ee
        L_0x01d0:
            int r14 = org.telegram.messenger.AndroidUtilities.dp(r10)
            float r14 = (float) r14
            int r14 = (r1 > r14 ? 1 : (r1 == r14 ? 0 : -1))
            if (r14 >= 0) goto L_0x01ee
            int r14 = org.telegram.messenger.AndroidUtilities.dp(r10)
            float r14 = (float) r14
            float r14 = r1 - r14
            float r0 = r13.getTranslationX()
            float r0 = r0 + r14
            r13.setTranslationX(r0)
            android.widget.ImageView r0 = r13.arrowImageView
            float r1 = r1 - r14
            r0.setTranslationX(r1)
        L_0x01ee:
            return
        */
        throw new UnsupportedOperationException("Method not decompiled: org.telegram.ui.Components.HintView.updatePosition(android.view.View):void");
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

    public void setBottomOffset(int i) {
        this.bottomOffset = i;
    }

    private int getThemedColor(String str) {
        Theme.ResourcesProvider resourcesProvider2 = this.resourcesProvider;
        Integer color = resourcesProvider2 != null ? resourcesProvider2.getColor(str) : null;
        return color != null ? color.intValue() : Theme.getColor(str);
    }
}
