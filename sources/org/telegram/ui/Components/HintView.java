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
    /* access modifiers changed from: private */
    public long showingDuration;
    private int shownY;
    private TextView textView;
    private float translationY;

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
        CorrectlyMeasuringTextView correctlyMeasuringTextView = new CorrectlyMeasuringTextView(context2);
        this.textView = correctlyMeasuringTextView;
        correctlyMeasuringTextView.setTextColor(Theme.getColor("chat_gifSaveHintText"));
        this.textView.setTextSize(1, 14.0f);
        this.textView.setMaxLines(2);
        if (i2 == 7) {
            this.textView.setMaxWidth(AndroidUtilities.dp(310.0f));
        } else if (i2 == 4) {
            this.textView.setMaxWidth(AndroidUtilities.dp(280.0f));
        } else {
            this.textView.setMaxWidth(AndroidUtilities.dp(250.0f));
        }
        if (this.currentType == 3) {
            this.textView.setGravity(19);
            this.textView.setBackground(Theme.createRoundRectDrawable(AndroidUtilities.dp(5.0f), Theme.getColor("chat_gifSaveHintBackground")));
            this.textView.setPadding(AndroidUtilities.dp(10.0f), 0, AndroidUtilities.dp(10.0f), 0);
            addView(this.textView, LayoutHelper.createFrame(-2, 30.0f, 51, 0.0f, z2 ? 6.0f : 0.0f, 0.0f, z2 ? 0.0f : 6.0f));
        } else {
            this.textView.setGravity(51);
            this.textView.setBackground(Theme.createRoundRectDrawable(AndroidUtilities.dp(this.currentType == 7 ? 6.0f : 3.0f), Theme.getColor("chat_gifSaveHintBackground")));
            int i3 = this.currentType;
            if (i3 == 5 || i3 == 4) {
                this.textView.setPadding(AndroidUtilities.dp(9.0f), AndroidUtilities.dp(6.0f), AndroidUtilities.dp(9.0f), AndroidUtilities.dp(7.0f));
            } else if (i3 == 2) {
                this.textView.setPadding(AndroidUtilities.dp(7.0f), AndroidUtilities.dp(6.0f), AndroidUtilities.dp(7.0f), AndroidUtilities.dp(7.0f));
            } else if (i3 == 7) {
                this.textView.setPadding(AndroidUtilities.dp(8.0f), AndroidUtilities.dp(7.0f), AndroidUtilities.dp(8.0f), AndroidUtilities.dp(8.0f));
            } else {
                this.textView.setPadding(AndroidUtilities.dp(i3 == 0 ? 54.0f : 5.0f), AndroidUtilities.dp(6.0f), AndroidUtilities.dp(5.0f), AndroidUtilities.dp(7.0f));
            }
            addView(this.textView, LayoutHelper.createFrame(-2, -2.0f, 51, 0.0f, z2 ? 6.0f : 0.0f, 0.0f, z2 ? 0.0f : 6.0f));
        }
        if (i2 == 0) {
            this.textView.setText(LocaleController.getString("AutoplayVideoInfo", NUM));
            ImageView imageView2 = new ImageView(context2);
            this.imageView = imageView2;
            imageView2.setImageResource(NUM);
            this.imageView.setScaleType(ImageView.ScaleType.CENTER);
            this.imageView.setColorFilter(new PorterDuffColorFilter(Theme.getColor("chat_gifSaveHintText"), PorterDuff.Mode.MULTIPLY));
            addView(this.imageView, LayoutHelper.createFrame(38, 34.0f, 51, 7.0f, 7.0f, 0.0f, 0.0f));
        }
        ImageView imageView3 = new ImageView(context2);
        this.arrowImageView = imageView3;
        imageView3.setImageResource(z2 ? NUM : NUM);
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

    /* JADX WARNING: Removed duplicated region for block: B:59:0x0176 A[RETURN] */
    /* JADX WARNING: Removed duplicated region for block: B:60:0x0177  */
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
            goto L_0x017b
        L_0x007b:
            return r5
        L_0x007c:
            r12 = -2147483648(0xfffffffvar_, float:-0.0)
            r13 = 1000(0x3e8, float:1.401E-42)
            if (r10 != r4) goto L_0x0101
            r10 = r18
            java.lang.Integer r10 = (java.lang.Integer) r10
            int r9 = r9 + r2
            r0.shownY = r2
            int r2 = r10.intValue()
            r14 = -1
            if (r2 != r14) goto L_0x009f
            android.widget.TextView r2 = r0.textView
            r10 = 2131626938(0x7f0e0bba, float:1.8881126E38)
            java.lang.String r14 = "PollSelectOption"
            java.lang.String r10 = org.telegram.messenger.LocaleController.getString(r14, r10)
            r2.setText(r10)
            goto L_0x00f2
        L_0x009f:
            org.telegram.messenger.MessageObject r2 = r17.getMessageObject()
            boolean r2 = r2.isQuiz()
            if (r2 == 0) goto L_0x00ce
            int r2 = r10.intValue()
            if (r2 != 0) goto L_0x00be
            android.widget.TextView r2 = r0.textView
            r10 = 2131626286(0x7f0e092e, float:1.8879804E38)
            java.lang.String r14 = "NoVotesQuiz"
            java.lang.String r10 = org.telegram.messenger.LocaleController.getString(r14, r10)
            r2.setText(r10)
            goto L_0x00f2
        L_0x00be:
            android.widget.TextView r2 = r0.textView
            int r10 = r10.intValue()
            java.lang.String r14 = "Answer"
            java.lang.String r10 = org.telegram.messenger.LocaleController.formatPluralString(r14, r10)
            r2.setText(r10)
            goto L_0x00f2
        L_0x00ce:
            int r2 = r10.intValue()
            if (r2 != 0) goto L_0x00e3
            android.widget.TextView r2 = r0.textView
            r10 = 2131626285(0x7f0e092d, float:1.8879802E38)
            java.lang.String r14 = "NoVotes"
            java.lang.String r10 = org.telegram.messenger.LocaleController.getString(r14, r10)
            r2.setText(r10)
            goto L_0x00f2
        L_0x00e3:
            android.widget.TextView r2 = r0.textView
            int r10 = r10.intValue()
            java.lang.String r14 = "Vote"
            java.lang.String r10 = org.telegram.messenger.LocaleController.formatPluralString(r14, r10)
            r2.setText(r10)
        L_0x00f2:
            int r2 = android.view.View.MeasureSpec.makeMeasureSpec(r13, r12)
            int r10 = android.view.View.MeasureSpec.makeMeasureSpec(r13, r12)
            r0.measure(r2, r10)
            r2 = r19
            goto L_0x017b
        L_0x0101:
            org.telegram.messenger.MessageObject r2 = r17.getMessageObject()
            java.lang.String r10 = r0.overrideText
            if (r10 != 0) goto L_0x0118
            android.widget.TextView r10 = r0.textView
            r14 = 2131625726(0x7f0e06fe, float:1.8878668E38)
            java.lang.String r15 = "HidAccount"
            java.lang.String r14 = org.telegram.messenger.LocaleController.getString(r15, r14)
            r10.setText(r14)
            goto L_0x011d
        L_0x0118:
            android.widget.TextView r14 = r0.textView
            r14.setText(r10)
        L_0x011d:
            int r10 = android.view.View.MeasureSpec.makeMeasureSpec(r13, r12)
            int r12 = android.view.View.MeasureSpec.makeMeasureSpec(r13, r12)
            r0.measure(r10, r12)
            org.telegram.tgnet.TLRPC$User r10 = r17.getCurrentUser()
            if (r10 == 0) goto L_0x014d
            int r10 = r10.id
            if (r10 != 0) goto L_0x014d
            int r2 = r17.getMeasuredHeight()
            int r10 = r17.getBottom()
            int r12 = r7.getMeasuredHeight()
            int r10 = r10 - r12
            int r10 = java.lang.Math.max(r5, r10)
            int r2 = r2 - r10
            r10 = 1112014848(0x42480000, float:50.0)
            int r10 = org.telegram.messenger.AndroidUtilities.dp(r10)
            int r2 = r2 - r10
        L_0x014b:
            int r9 = r9 + r2
            goto L_0x0167
        L_0x014d:
            r10 = 1102053376(0x41b00000, float:22.0)
            int r10 = org.telegram.messenger.AndroidUtilities.dp(r10)
            int r9 = r9 + r10
            boolean r2 = r2.isOutOwner()
            if (r2 != 0) goto L_0x0167
            boolean r2 = r17.isDrawNameLayout()
            if (r2 == 0) goto L_0x0167
            r2 = 1101004800(0x41a00000, float:20.0)
            int r2 = org.telegram.messenger.AndroidUtilities.dp(r2)
            goto L_0x014b
        L_0x0167:
            boolean r2 = r0.isTopArrow
            if (r2 != 0) goto L_0x0177
            int r2 = r16.getMeasuredHeight()
            int r10 = org.telegram.messenger.AndroidUtilities.dp(r11)
            int r2 = r2 + r10
            if (r9 > r2) goto L_0x0177
            return r5
        L_0x0177:
            int r2 = r17.getForwardNameCenterX()
        L_0x017b:
            int r10 = r7.getMeasuredWidth()
            boolean r12 = r0.isTopArrow
            if (r12 == 0) goto L_0x0193
            float r9 = r0.extraTranslationY
            r12 = 1110441984(0x42300000, float:44.0)
            int r12 = org.telegram.messenger.AndroidUtilities.dp(r12)
            float r12 = (float) r12
            r0.translationY = r12
            float r9 = r9 + r12
            r0.setTranslationY(r9)
            goto L_0x01a1
        L_0x0193:
            float r12 = r0.extraTranslationY
            int r13 = r16.getMeasuredHeight()
            int r9 = r9 - r13
            float r9 = (float) r9
            r0.translationY = r9
            float r12 = r12 + r9
            r0.setTranslationY(r12)
        L_0x01a1:
            int r9 = r17.getLeft()
            int r9 = r9 + r2
            r12 = 1100480512(0x41980000, float:19.0)
            int r12 = org.telegram.messenger.AndroidUtilities.dp(r12)
            int r13 = r0.currentType
            if (r13 != r4) goto L_0x01c9
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
            goto L_0x01e6
        L_0x01c9:
            int r4 = r7.getMeasuredWidth()
            int r4 = r4 / r3
            if (r9 <= r4) goto L_0x01e2
            int r4 = r16.getMeasuredWidth()
            int r10 = r10 - r4
            r4 = 1108869120(0x42180000, float:38.0)
            int r4 = org.telegram.messenger.AndroidUtilities.dp(r4)
            int r10 = r10 - r4
            float r4 = (float) r10
            r0.setTranslationX(r4)
            int r12 = r12 + r10
            goto L_0x01e6
        L_0x01e2:
            r4 = 0
            r0.setTranslationX(r4)
        L_0x01e6:
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
            if (r9 <= r4) goto L_0x0220
            int r4 = org.telegram.messenger.AndroidUtilities.dp(r11)
            float r4 = (float) r4
            int r4 = (r2 > r4 ? 1 : (r2 == r4 ? 0 : -1))
            if (r4 >= 0) goto L_0x0265
            int r4 = org.telegram.messenger.AndroidUtilities.dp(r11)
            float r4 = (float) r4
            float r4 = r2 - r4
            float r7 = r16.getTranslationX()
            float r7 = r7 + r4
            r0.setTranslationX(r7)
            android.widget.ImageView r7 = r0.arrowImageView
            float r2 = r2 - r4
            r7.setTranslationX(r2)
            goto L_0x0265
        L_0x0220:
            int r4 = r16.getMeasuredWidth()
            r7 = 1103101952(0x41CLASSNAME, float:24.0)
            int r9 = org.telegram.messenger.AndroidUtilities.dp(r7)
            int r4 = r4 - r9
            float r4 = (float) r4
            int r4 = (r2 > r4 ? 1 : (r2 == r4 ? 0 : -1))
            if (r4 <= 0) goto L_0x0247
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
            goto L_0x0265
        L_0x0247:
            int r4 = org.telegram.messenger.AndroidUtilities.dp(r11)
            float r4 = (float) r4
            int r4 = (r2 > r4 ? 1 : (r2 == r4 ? 0 : -1))
            if (r4 >= 0) goto L_0x0265
            int r4 = org.telegram.messenger.AndroidUtilities.dp(r11)
            float r4 = (float) r4
            float r4 = r2 - r4
            float r7 = r16.getTranslationX()
            float r7 = r7 + r4
            r0.setTranslationX(r7)
            android.widget.ImageView r7 = r0.arrowImageView
            float r2 = r2 - r4
            r7.setTranslationX(r2)
        L_0x0265:
            r0.messageCell = r1
            android.animation.AnimatorSet r1 = r0.animatorSet
            if (r1 == 0) goto L_0x0270
            r1.cancel()
            r0.animatorSet = r6
        L_0x0270:
            java.lang.Integer r1 = java.lang.Integer.valueOf(r8)
            r0.setTag(r1)
            r0.setVisibility(r5)
            if (r21 == 0) goto L_0x02ac
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
            goto L_0x02b1
        L_0x02ac:
            r1 = 1065353216(0x3var_, float:1.0)
            r0.setAlpha(r1)
        L_0x02b1:
            return r8
        */
        throw new UnsupportedOperationException("Method not decompiled: org.telegram.ui.Components.HintView.showForMessageCell(org.telegram.ui.Cells.ChatMessageCell, java.lang.Object, int, int, boolean):boolean");
    }

    /* JADX WARNING: Removed duplicated region for block: B:18:0x0073  */
    /* JADX WARNING: Removed duplicated region for block: B:19:0x0076  */
    /* JADX WARNING: Removed duplicated region for block: B:29:0x00c4  */
    /* JADX WARNING: Removed duplicated region for block: B:30:0x00d5  */
    /* JADX WARNING: Removed duplicated region for block: B:33:0x00de  */
    /* JADX WARNING: Removed duplicated region for block: B:37:0x00f7  */
    /* JADX WARNING: Removed duplicated region for block: B:43:0x011f  */
    /* JADX WARNING: Removed duplicated region for block: B:46:0x0133  */
    /* JADX WARNING: Removed duplicated region for block: B:49:0x0152  */
    /* JADX WARNING: Removed duplicated region for block: B:57:0x019d  */
    /* JADX WARNING: Removed duplicated region for block: B:60:0x01ae  */
    /* JADX WARNING: Removed duplicated region for block: B:61:0x01de  */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public boolean showForView(android.view.View r14, boolean r15) {
        /*
            r13 = this;
            android.view.View r0 = r13.currentView
            r1 = 0
            if (r0 == r14) goto L_0x01e4
            java.lang.Object r0 = r13.getTag()
            if (r0 == 0) goto L_0x000d
            goto L_0x01e4
        L_0x000d:
            java.lang.Runnable r0 = r13.hideRunnable
            r2 = 0
            if (r0 == 0) goto L_0x0017
            org.telegram.messenger.AndroidUtilities.cancelRunOnUIThread(r0)
            r13.hideRunnable = r2
        L_0x0017:
            android.graphics.Point r0 = org.telegram.messenger.AndroidUtilities.displaySize
            int r0 = r0.x
            r3 = -2147483648(0xfffffffvar_, float:-0.0)
            int r0 = android.view.View.MeasureSpec.makeMeasureSpec(r0, r3)
            android.graphics.Point r4 = org.telegram.messenger.AndroidUtilities.displaySize
            int r4 = r4.x
            int r3 = android.view.View.MeasureSpec.makeMeasureSpec(r4, r3)
            r13.measure(r0, r3)
            r0 = 2
            int[] r3 = new int[r0]
            r14.getLocationInWindow(r3)
            r4 = 1
            r5 = r3[r4]
            r6 = 1082130432(0x40800000, float:4.0)
            int r7 = org.telegram.messenger.AndroidUtilities.dp(r6)
            int r5 = r5 - r7
            int r7 = r13.currentType
            r8 = 4
            r9 = 6
            r10 = 7
            r11 = 1092616192(0x41200000, float:10.0)
            if (r7 != r8) goto L_0x004b
            int r6 = org.telegram.messenger.AndroidUtilities.dp(r6)
        L_0x0049:
            int r5 = r5 + r6
            goto L_0x006e
        L_0x004b:
            if (r7 != r9) goto L_0x005c
            int r6 = r14.getMeasuredHeight()
            int r7 = r13.getMeasuredHeight()
            int r6 = r6 + r7
            int r7 = org.telegram.messenger.AndroidUtilities.dp(r11)
        L_0x005a:
            int r6 = r6 + r7
            goto L_0x0049
        L_0x005c:
            if (r7 != r10) goto L_0x006e
            int r6 = r14.getMeasuredHeight()
            int r7 = r13.getMeasuredHeight()
            int r6 = r6 + r7
            r7 = 1090519040(0x41000000, float:8.0)
            int r7 = org.telegram.messenger.AndroidUtilities.dp(r7)
            goto L_0x005a
        L_0x006e:
            int r6 = r13.currentType
            r7 = 3
            if (r6 != r7) goto L_0x0076
            r6 = r3[r1]
            goto L_0x007e
        L_0x0076:
            r6 = r3[r1]
            int r8 = r14.getMeasuredWidth()
            int r8 = r8 / r0
            int r6 = r6 + r8
        L_0x007e:
            android.view.ViewParent r8 = r13.getParent()
            android.view.View r8 = (android.view.View) r8
            r8.getLocationInWindow(r3)
            r12 = r3[r1]
            int r6 = r6 - r12
            r3 = r3[r4]
            int r5 = r5 - r3
            int r3 = r13.bottomOffset
            int r5 = r5 - r3
            int r3 = r8.getMeasuredWidth()
            boolean r12 = r13.isTopArrow
            if (r12 == 0) goto L_0x00ae
            int r12 = r13.currentType
            if (r12 == r9) goto L_0x00ae
            if (r12 == r10) goto L_0x00ae
            float r5 = r13.extraTranslationY
            r9 = 1110441984(0x42300000, float:44.0)
            int r9 = org.telegram.messenger.AndroidUtilities.dp(r9)
            float r9 = (float) r9
            r13.translationY = r9
            float r5 = r5 + r9
            r13.setTranslationY(r5)
            goto L_0x00bc
        L_0x00ae:
            float r9 = r13.extraTranslationY
            int r12 = r13.getMeasuredHeight()
            int r5 = r5 - r12
            float r5 = (float) r5
            r13.translationY = r5
            float r9 = r9 + r5
            r13.setTranslationY(r9)
        L_0x00bc:
            android.view.ViewGroup$LayoutParams r5 = r13.getLayoutParams()
            boolean r5 = r5 instanceof android.view.ViewGroup.MarginLayoutParams
            if (r5 == 0) goto L_0x00d5
            android.view.ViewGroup$LayoutParams r5 = r13.getLayoutParams()
            android.view.ViewGroup$MarginLayoutParams r5 = (android.view.ViewGroup.MarginLayoutParams) r5
            int r5 = r5.leftMargin
            android.view.ViewGroup$LayoutParams r9 = r13.getLayoutParams()
            android.view.ViewGroup$MarginLayoutParams r9 = (android.view.ViewGroup.MarginLayoutParams) r9
            int r9 = r9.rightMargin
            goto L_0x00d7
        L_0x00d5:
            r5 = 0
            r9 = 0
        L_0x00d7:
            int r12 = r8.getMeasuredWidth()
            int r12 = r12 / r0
            if (r6 <= r12) goto L_0x00f7
            int r12 = r13.currentType
            if (r12 != r7) goto L_0x00ef
            float r3 = (float) r3
            int r7 = r13.getMeasuredWidth()
            float r7 = (float) r7
            r9 = 1069547520(0x3fCLASSNAME, float:1.5)
            float r7 = r7 * r9
            float r3 = r3 - r7
            int r3 = (int) r3
            goto L_0x010b
        L_0x00ef:
            int r7 = r13.getMeasuredWidth()
            int r3 = r3 - r7
            int r9 = r9 + r5
            int r3 = r3 - r9
            goto L_0x010b
        L_0x00f7:
            int r3 = r13.currentType
            if (r3 != r7) goto L_0x010a
            int r3 = r13.getMeasuredWidth()
            int r3 = r3 / r0
            int r3 = r6 - r3
            android.widget.ImageView r7 = r13.arrowImageView
            int r7 = r7.getMeasuredWidth()
            int r3 = r3 - r7
            goto L_0x010b
        L_0x010a:
            r3 = 0
        L_0x010b:
            float r7 = (float) r3
            r13.setTranslationX(r7)
            int r5 = r5 + r3
            int r3 = r6 - r5
            android.widget.ImageView r5 = r13.arrowImageView
            int r5 = r5.getMeasuredWidth()
            int r5 = r5 / r0
            int r3 = r3 - r5
            float r3 = (float) r3
            int r5 = r13.currentType
            if (r5 != r10) goto L_0x0127
            r5 = 1073741824(0x40000000, float:2.0)
            int r5 = org.telegram.messenger.AndroidUtilities.dp(r5)
            float r5 = (float) r5
            float r3 = r3 + r5
        L_0x0127:
            android.widget.ImageView r5 = r13.arrowImageView
            r5.setTranslationX(r3)
            int r5 = r8.getMeasuredWidth()
            int r5 = r5 / r0
            if (r6 <= r5) goto L_0x0152
            int r5 = org.telegram.messenger.AndroidUtilities.dp(r11)
            float r5 = (float) r5
            int r5 = (r3 > r5 ? 1 : (r3 == r5 ? 0 : -1))
            if (r5 >= 0) goto L_0x0197
            int r5 = org.telegram.messenger.AndroidUtilities.dp(r11)
            float r5 = (float) r5
            float r5 = r3 - r5
            float r6 = r13.getTranslationX()
            float r6 = r6 + r5
            r13.setTranslationX(r6)
            android.widget.ImageView r6 = r13.arrowImageView
            float r3 = r3 - r5
            r6.setTranslationX(r3)
            goto L_0x0197
        L_0x0152:
            int r5 = r13.getMeasuredWidth()
            r6 = 1103101952(0x41CLASSNAME, float:24.0)
            int r7 = org.telegram.messenger.AndroidUtilities.dp(r6)
            int r5 = r5 - r7
            float r5 = (float) r5
            int r5 = (r3 > r5 ? 1 : (r3 == r5 ? 0 : -1))
            if (r5 <= 0) goto L_0x0179
            int r5 = r13.getMeasuredWidth()
            float r5 = (float) r5
            float r5 = r3 - r5
            int r6 = org.telegram.messenger.AndroidUtilities.dp(r6)
            float r6 = (float) r6
            float r5 = r5 + r6
            r13.setTranslationX(r5)
            android.widget.ImageView r6 = r13.arrowImageView
            float r3 = r3 - r5
            r6.setTranslationX(r3)
            goto L_0x0197
        L_0x0179:
            int r5 = org.telegram.messenger.AndroidUtilities.dp(r11)
            float r5 = (float) r5
            int r5 = (r3 > r5 ? 1 : (r3 == r5 ? 0 : -1))
            if (r5 >= 0) goto L_0x0197
            int r5 = org.telegram.messenger.AndroidUtilities.dp(r11)
            float r5 = (float) r5
            float r5 = r3 - r5
            float r6 = r13.getTranslationX()
            float r6 = r6 + r5
            r13.setTranslationX(r6)
            android.widget.ImageView r6 = r13.arrowImageView
            float r3 = r3 - r5
            r6.setTranslationX(r3)
        L_0x0197:
            r13.currentView = r14
            android.animation.AnimatorSet r14 = r13.animatorSet
            if (r14 == 0) goto L_0x01a2
            r14.cancel()
            r13.animatorSet = r2
        L_0x01a2:
            java.lang.Integer r14 = java.lang.Integer.valueOf(r4)
            r13.setTag(r14)
            r13.setVisibility(r1)
            if (r15 == 0) goto L_0x01de
            android.animation.AnimatorSet r14 = new android.animation.AnimatorSet
            r14.<init>()
            r13.animatorSet = r14
            android.animation.Animator[] r15 = new android.animation.Animator[r4]
            android.util.Property r2 = android.view.View.ALPHA
            float[] r0 = new float[r0]
            r0 = {0, NUM} // fill-array
            android.animation.ObjectAnimator r0 = android.animation.ObjectAnimator.ofFloat(r13, r2, r0)
            r15[r1] = r0
            r14.playTogether(r15)
            android.animation.AnimatorSet r14 = r13.animatorSet
            org.telegram.ui.Components.HintView$2 r15 = new org.telegram.ui.Components.HintView$2
            r15.<init>()
            r14.addListener(r15)
            android.animation.AnimatorSet r14 = r13.animatorSet
            r0 = 300(0x12c, double:1.48E-321)
            r14.setDuration(r0)
            android.animation.AnimatorSet r14 = r13.animatorSet
            r14.start()
            goto L_0x01e3
        L_0x01de:
            r14 = 1065353216(0x3var_, float:1.0)
            r13.setAlpha(r14)
        L_0x01e3:
            return r4
        L_0x01e4:
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
}
