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
import org.telegram.messenger.LocaleController;
import org.telegram.ui.ActionBar.SimpleTextView;
import org.telegram.ui.ActionBar.Theme;
import org.telegram.ui.Cells.ChatMessageCell;
import org.telegram.ui.Components.HintView;

public class HintView extends FrameLayout {
    /* access modifiers changed from: private */
    public AnimatorSet animatorSet;
    private ImageView arrowImageView;
    private int bottomOffset;
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
        CorrectlyMeasuringTextView correctlyMeasuringTextView = new CorrectlyMeasuringTextView(context2);
        this.textView = correctlyMeasuringTextView;
        correctlyMeasuringTextView.setTextColor(Theme.getColor("chat_gifSaveHintText"));
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

    public boolean showForMessageCell(ChatMessageCell chatMessageCell, boolean z) {
        return showForMessageCell(chatMessageCell, (Object) null, 0, 0, z);
    }

    /* JADX WARNING: Removed duplicated region for block: B:60:0x0178 A[RETURN] */
    /* JADX WARNING: Removed duplicated region for block: B:61:0x0179  */
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
            int r3 = r0.shownY
            if (r2 != r3) goto L_0x0014
            org.telegram.ui.Cells.ChatMessageCell r3 = r0.messageCell
            if (r3 == r1) goto L_0x0024
        L_0x0014:
            int r3 = r0.currentType
            if (r3 == r4) goto L_0x0025
            if (r3 != 0) goto L_0x0020
            java.lang.Object r3 = r16.getTag()
            if (r3 != 0) goto L_0x0024
        L_0x0020:
            org.telegram.ui.Cells.ChatMessageCell r3 = r0.messageCell
            if (r3 != r1) goto L_0x0025
        L_0x0024:
            return r5
        L_0x0025:
            java.lang.Runnable r3 = r0.hideRunnable
            r6 = 0
            if (r3 == 0) goto L_0x002f
            org.telegram.messenger.AndroidUtilities.cancelRunOnUIThread(r3)
            r0.hideRunnable = r6
        L_0x002f:
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
            if (r10 != 0) goto L_0x007e
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
            if (r9 <= r13) goto L_0x007d
            int r2 = r2 / 4
            int r12 = r12 + r2
            if (r10 <= r12) goto L_0x0077
            goto L_0x007d
        L_0x0077:
            int r2 = r17.getNoSoundIconCenterX()
            goto L_0x017d
        L_0x007d:
            return r5
        L_0x007e:
            r12 = -2147483648(0xfffffffvar_, float:-0.0)
            r13 = 1000(0x3e8, float:1.401E-42)
            if (r10 != r4) goto L_0x0103
            r10 = r18
            java.lang.Integer r10 = (java.lang.Integer) r10
            int r9 = r9 + r2
            r0.shownY = r2
            int r2 = r10.intValue()
            r14 = -1
            if (r2 != r14) goto L_0x00a1
            android.widget.TextView r2 = r0.textView
            r10 = 2131626543(0x7f0e0a2f, float:1.8880325E38)
            java.lang.String r14 = "PollSelectOption"
            java.lang.String r10 = org.telegram.messenger.LocaleController.getString(r14, r10)
            r2.setText(r10)
            goto L_0x00f4
        L_0x00a1:
            org.telegram.messenger.MessageObject r2 = r17.getMessageObject()
            boolean r2 = r2.isQuiz()
            if (r2 == 0) goto L_0x00d0
            int r2 = r10.intValue()
            if (r2 != 0) goto L_0x00c0
            android.widget.TextView r2 = r0.textView
            r10 = 2131625972(0x7f0e07f4, float:1.8879167E38)
            java.lang.String r14 = "NoVotesQuiz"
            java.lang.String r10 = org.telegram.messenger.LocaleController.getString(r14, r10)
            r2.setText(r10)
            goto L_0x00f4
        L_0x00c0:
            android.widget.TextView r2 = r0.textView
            int r10 = r10.intValue()
            java.lang.String r14 = "Answer"
            java.lang.String r10 = org.telegram.messenger.LocaleController.formatPluralString(r14, r10)
            r2.setText(r10)
            goto L_0x00f4
        L_0x00d0:
            int r2 = r10.intValue()
            if (r2 != 0) goto L_0x00e5
            android.widget.TextView r2 = r0.textView
            r10 = 2131625971(0x7f0e07f3, float:1.8879165E38)
            java.lang.String r14 = "NoVotes"
            java.lang.String r10 = org.telegram.messenger.LocaleController.getString(r14, r10)
            r2.setText(r10)
            goto L_0x00f4
        L_0x00e5:
            android.widget.TextView r2 = r0.textView
            int r10 = r10.intValue()
            java.lang.String r14 = "Vote"
            java.lang.String r10 = org.telegram.messenger.LocaleController.formatPluralString(r14, r10)
            r2.setText(r10)
        L_0x00f4:
            int r2 = android.view.View.MeasureSpec.makeMeasureSpec(r13, r12)
            int r10 = android.view.View.MeasureSpec.makeMeasureSpec(r13, r12)
            r0.measure(r2, r10)
            r2 = r19
            goto L_0x017d
        L_0x0103:
            org.telegram.messenger.MessageObject r2 = r17.getMessageObject()
            java.lang.String r10 = r0.overrideText
            if (r10 != 0) goto L_0x011a
            android.widget.TextView r10 = r0.textView
            r14 = 2131625527(0x7f0e0637, float:1.8878264E38)
            java.lang.String r15 = "HidAccount"
            java.lang.String r14 = org.telegram.messenger.LocaleController.getString(r15, r14)
            r10.setText(r14)
            goto L_0x011f
        L_0x011a:
            android.widget.TextView r14 = r0.textView
            r14.setText(r10)
        L_0x011f:
            int r10 = android.view.View.MeasureSpec.makeMeasureSpec(r13, r12)
            int r12 = android.view.View.MeasureSpec.makeMeasureSpec(r13, r12)
            r0.measure(r10, r12)
            org.telegram.tgnet.TLRPC$User r10 = r17.getCurrentUser()
            if (r10 == 0) goto L_0x014f
            int r10 = r10.id
            if (r10 != 0) goto L_0x014f
            int r2 = r17.getMeasuredHeight()
            int r10 = r17.getBottom()
            int r12 = r7.getMeasuredHeight()
            int r10 = r10 - r12
            int r10 = java.lang.Math.max(r5, r10)
            int r2 = r2 - r10
            r10 = 1112014848(0x42480000, float:50.0)
            int r10 = org.telegram.messenger.AndroidUtilities.dp(r10)
            int r2 = r2 - r10
        L_0x014d:
            int r9 = r9 + r2
            goto L_0x0169
        L_0x014f:
            r10 = 1102053376(0x41b00000, float:22.0)
            int r10 = org.telegram.messenger.AndroidUtilities.dp(r10)
            int r9 = r9 + r10
            boolean r2 = r2.isOutOwner()
            if (r2 != 0) goto L_0x0169
            boolean r2 = r17.isDrawNameLayout()
            if (r2 == 0) goto L_0x0169
            r2 = 1101004800(0x41a00000, float:20.0)
            int r2 = org.telegram.messenger.AndroidUtilities.dp(r2)
            goto L_0x014d
        L_0x0169:
            boolean r2 = r0.isTopArrow
            if (r2 != 0) goto L_0x0179
            int r2 = r16.getMeasuredHeight()
            int r10 = org.telegram.messenger.AndroidUtilities.dp(r11)
            int r2 = r2 + r10
            if (r9 > r2) goto L_0x0179
            return r5
        L_0x0179:
            int r2 = r17.getForwardNameCenterX()
        L_0x017d:
            int r10 = r7.getMeasuredWidth()
            boolean r12 = r0.isTopArrow
            if (r12 == 0) goto L_0x0190
            r9 = 1110441984(0x42300000, float:44.0)
            int r9 = org.telegram.messenger.AndroidUtilities.dp(r9)
            float r9 = (float) r9
            r0.setTranslationY(r9)
            goto L_0x0199
        L_0x0190:
            int r12 = r16.getMeasuredHeight()
            int r9 = r9 - r12
            float r9 = (float) r9
            r0.setTranslationY(r9)
        L_0x0199:
            int r9 = r17.getLeft()
            int r9 = r9 + r2
            r12 = 1100480512(0x41980000, float:19.0)
            int r12 = org.telegram.messenger.AndroidUtilities.dp(r12)
            int r13 = r0.currentType
            if (r13 != r4) goto L_0x01c1
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
            goto L_0x01de
        L_0x01c1:
            int r4 = r7.getMeasuredWidth()
            int r4 = r4 / r3
            if (r9 <= r4) goto L_0x01da
            int r4 = r16.getMeasuredWidth()
            int r10 = r10 - r4
            r4 = 1108869120(0x42180000, float:38.0)
            int r4 = org.telegram.messenger.AndroidUtilities.dp(r4)
            int r10 = r10 - r4
            float r4 = (float) r10
            r0.setTranslationX(r4)
            int r12 = r12 + r10
            goto L_0x01de
        L_0x01da:
            r4 = 0
            r0.setTranslationX(r4)
        L_0x01de:
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
            if (r9 <= r4) goto L_0x0218
            int r4 = org.telegram.messenger.AndroidUtilities.dp(r11)
            float r4 = (float) r4
            int r4 = (r2 > r4 ? 1 : (r2 == r4 ? 0 : -1))
            if (r4 >= 0) goto L_0x025d
            int r4 = org.telegram.messenger.AndroidUtilities.dp(r11)
            float r4 = (float) r4
            float r4 = r2 - r4
            float r7 = r16.getTranslationX()
            float r7 = r7 + r4
            r0.setTranslationX(r7)
            android.widget.ImageView r7 = r0.arrowImageView
            float r2 = r2 - r4
            r7.setTranslationX(r2)
            goto L_0x025d
        L_0x0218:
            int r4 = r16.getMeasuredWidth()
            r7 = 1103101952(0x41CLASSNAME, float:24.0)
            int r9 = org.telegram.messenger.AndroidUtilities.dp(r7)
            int r4 = r4 - r9
            float r4 = (float) r4
            int r4 = (r2 > r4 ? 1 : (r2 == r4 ? 0 : -1))
            if (r4 <= 0) goto L_0x023f
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
            goto L_0x025d
        L_0x023f:
            int r4 = org.telegram.messenger.AndroidUtilities.dp(r11)
            float r4 = (float) r4
            int r4 = (r2 > r4 ? 1 : (r2 == r4 ? 0 : -1))
            if (r4 >= 0) goto L_0x025d
            int r4 = org.telegram.messenger.AndroidUtilities.dp(r11)
            float r4 = (float) r4
            float r4 = r2 - r4
            float r7 = r16.getTranslationX()
            float r7 = r7 + r4
            r0.setTranslationX(r7)
            android.widget.ImageView r7 = r0.arrowImageView
            float r2 = r2 - r4
            r7.setTranslationX(r2)
        L_0x025d:
            r0.messageCell = r1
            android.animation.AnimatorSet r1 = r0.animatorSet
            if (r1 == 0) goto L_0x0268
            r1.cancel()
            r0.animatorSet = r6
        L_0x0268:
            java.lang.Integer r1 = java.lang.Integer.valueOf(r8)
            r0.setTag(r1)
            r0.setVisibility(r5)
            if (r21 == 0) goto L_0x02a4
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
            goto L_0x02a9
        L_0x02a4:
            r1 = 1065353216(0x3var_, float:1.0)
            r0.setAlpha(r1)
        L_0x02a9:
            return r8
        */
        throw new UnsupportedOperationException("Method not decompiled: org.telegram.ui.Components.HintView.showForMessageCell(org.telegram.ui.Cells.ChatMessageCell, java.lang.Object, int, int, boolean):boolean");
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
        int i8 = (dp - iArr[1]) - this.bottomOffset;
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
            AnimatorSet animatorSet3 = new AnimatorSet();
            this.animatorSet = animatorSet3;
            animatorSet3.playTogether(new Animator[]{ObjectAnimator.ofFloat(this, View.ALPHA, new float[]{0.0f, 1.0f})});
            this.animatorSet.addListener(new AnimatorListenerAdapter() {
                public void onAnimationEnd(Animator animator) {
                    AnimatorSet unused = HintView.this.animatorSet = null;
                    HintView hintView = HintView.this;
                    $$Lambda$HintView$2$jvm1hL0MTRZE1LibhgmMBpjU0UA r0 = new Runnable() {
                        public final void run() {
                            HintView.AnonymousClass2.this.lambda$onAnimationEnd$0$HintView$2();
                        }
                    };
                    Runnable unused2 = hintView.hideRunnable = r0;
                    AndroidUtilities.runOnUIThread(r0, HintView.this.showingDuration);
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
