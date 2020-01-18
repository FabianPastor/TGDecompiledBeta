package org.telegram.ui.Components;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.content.Context;
import android.graphics.PorterDuff.Mode;
import android.graphics.PorterDuffColorFilter;
import android.os.Build.VERSION;
import android.view.View;
import android.view.View.MeasureSpec;
import android.view.ViewGroup.MarginLayoutParams;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.ImageView.ScaleType;
import android.widget.TextView;
import org.telegram.messenger.AndroidUtilities;
import org.telegram.messenger.LocaleController;
import org.telegram.ui.ActionBar.ActionBar;
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
        return showForMessageCell(chatMessageCell, null, 0, 0, z);
    }

    /* JADX WARNING: Removed duplicated region for block: B:60:0x0176  */
    /* JADX WARNING: Removed duplicated region for block: B:57:0x0157  */
    /* JADX WARNING: Removed duplicated region for block: B:68:0x01c1  */
    /* JADX WARNING: Removed duplicated region for block: B:72:0x0205  */
    /* JADX WARNING: Removed duplicated region for block: B:71:0x01d3  */
    public boolean showForMessageCell(org.telegram.ui.Cells.ChatMessageCell r10, java.lang.Object r11, int r12, int r13, boolean r14) {
        /*
        r9 = this;
        r0 = r9.currentType;
        r1 = 5;
        r2 = 0;
        if (r0 != r1) goto L_0x000e;
    L_0x0006:
        r0 = r9.shownY;
        if (r13 != r0) goto L_0x000e;
    L_0x000a:
        r0 = r9.messageCell;
        if (r0 == r10) goto L_0x001e;
    L_0x000e:
        r0 = r9.currentType;
        if (r0 == r1) goto L_0x001f;
    L_0x0012:
        if (r0 != 0) goto L_0x001a;
    L_0x0014:
        r0 = r9.getTag();
        if (r0 != 0) goto L_0x001e;
    L_0x001a:
        r0 = r9.messageCell;
        if (r0 != r10) goto L_0x001f;
    L_0x001e:
        return r2;
    L_0x001f:
        r0 = r9.hideRunnable;
        r3 = 0;
        if (r0 == 0) goto L_0x0029;
    L_0x0024:
        org.telegram.messenger.AndroidUtilities.cancelRunOnUIThread(r0);
        r9.hideRunnable = r3;
    L_0x0029:
        r0 = r10.getTop();
        r4 = r10.getParent();
        r4 = (android.view.View) r4;
        r5 = r9.currentType;
        r6 = NUM; // 0x41200000 float:10.0 double:5.398241246E-315;
        if (r5 != 0) goto L_0x0064;
    L_0x0039:
        r11 = r10.getPhotoImage();
        r12 = r11.getImageY();
        r0 = r0 + r12;
        r11 = r11.getImageHeight();
        r12 = r0 + r11;
        r13 = r4.getMeasuredHeight();
        r5 = r9.getMeasuredHeight();
        r7 = org.telegram.messenger.AndroidUtilities.dp(r6);
        r5 = r5 + r7;
        if (r0 <= r5) goto L_0x0063;
    L_0x0057:
        r11 = r11 / 4;
        r13 = r13 + r11;
        if (r12 <= r13) goto L_0x005d;
    L_0x005c:
        goto L_0x0063;
    L_0x005d:
        r12 = r10.getNoSoundIconCenterX();
        goto L_0x00df;
    L_0x0063:
        return r2;
    L_0x0064:
        r7 = -NUM; // 0xfffffffvar_ float:-0.0 double:NaN;
        r8 = 1000; // 0x3e8 float:1.401E-42 double:4.94E-321;
        if (r5 != r1) goto L_0x008a;
    L_0x006a:
        r0 = r0 + r13;
        r9.shownY = r13;
        r13 = r9.textView;
        r11 = (java.lang.Integer) r11;
        r11 = r11.intValue();
        r5 = "Vote";
        r11 = org.telegram.messenger.LocaleController.formatPluralString(r5, r11);
        r13.setText(r11);
        r11 = android.view.View.MeasureSpec.makeMeasureSpec(r8, r7);
        r13 = android.view.View.MeasureSpec.makeMeasureSpec(r8, r7);
        r9.measure(r11, r13);
        goto L_0x00df;
    L_0x008a:
        r11 = r10.getMessageObject();
        r12 = r9.overrideText;
        if (r12 != 0) goto L_0x00a1;
    L_0x0092:
        r12 = r9.textView;
        r13 = NUM; // 0x7f0e0555 float:1.8877806E38 double:1.053162831E-314;
        r5 = "HidAccount";
        r13 = org.telegram.messenger.LocaleController.getString(r5, r13);
        r12.setText(r13);
        goto L_0x00a6;
    L_0x00a1:
        r13 = r9.textView;
        r13.setText(r12);
    L_0x00a6:
        r12 = android.view.View.MeasureSpec.makeMeasureSpec(r8, r7);
        r13 = android.view.View.MeasureSpec.makeMeasureSpec(r8, r7);
        r9.measure(r12, r13);
        r12 = NUM; // 0x41b00000 float:22.0 double:5.44486713E-315;
        r12 = org.telegram.messenger.AndroidUtilities.dp(r12);
        r0 = r0 + r12;
        r11 = r11.isOutOwner();
        if (r11 != 0) goto L_0x00cb;
    L_0x00be:
        r11 = r10.isDrawNameLayout();
        if (r11 == 0) goto L_0x00cb;
    L_0x00c4:
        r11 = NUM; // 0x41a00000 float:20.0 double:5.439686476E-315;
        r11 = org.telegram.messenger.AndroidUtilities.dp(r11);
        r0 = r0 + r11;
    L_0x00cb:
        r11 = r9.isTopArrow;
        if (r11 != 0) goto L_0x00db;
    L_0x00cf:
        r11 = r9.getMeasuredHeight();
        r12 = org.telegram.messenger.AndroidUtilities.dp(r6);
        r11 = r11 + r12;
        if (r0 > r11) goto L_0x00db;
    L_0x00da:
        return r2;
    L_0x00db:
        r12 = r10.getForwardNameCenterX();
    L_0x00df:
        r11 = r4.getMeasuredWidth();
        r13 = r9.isTopArrow;
        if (r13 == 0) goto L_0x00f2;
    L_0x00e7:
        r13 = NUM; // 0x42300000 float:44.0 double:5.48631236E-315;
        r13 = org.telegram.messenger.AndroidUtilities.dp(r13);
        r13 = (float) r13;
        r9.setTranslationY(r13);
        goto L_0x00fb;
    L_0x00f2:
        r13 = r9.getMeasuredHeight();
        r0 = r0 - r13;
        r13 = (float) r0;
        r9.setTranslationY(r13);
    L_0x00fb:
        r13 = r10.getLeft();
        r13 = r13 + r12;
        r0 = NUM; // 0x41980000 float:19.0 double:5.43709615E-315;
        r0 = org.telegram.messenger.AndroidUtilities.dp(r0);
        r5 = r9.currentType;
        r7 = 2;
        if (r5 != r1) goto L_0x0120;
    L_0x010b:
        r11 = r9.getMeasuredWidth();
        r11 = r11 / r7;
        r11 = r12 - r11;
        r1 = NUM; // 0x4198cccd float:19.1 double:5.437355183E-315;
        r1 = org.telegram.messenger.AndroidUtilities.dp(r1);
        r11 = r11 - r1;
        r1 = (float) r11;
        r9.setTranslationX(r1);
    L_0x011e:
        r0 = r0 + r11;
        goto L_0x013c;
    L_0x0120:
        r1 = r4.getMeasuredWidth();
        r1 = r1 / r7;
        if (r13 <= r1) goto L_0x0138;
    L_0x0127:
        r1 = r9.getMeasuredWidth();
        r11 = r11 - r1;
        r1 = NUM; // 0x42180000 float:38.0 double:5.47854138E-315;
        r1 = org.telegram.messenger.AndroidUtilities.dp(r1);
        r11 = r11 - r1;
        r1 = (float) r11;
        r9.setTranslationX(r1);
        goto L_0x011e;
    L_0x0138:
        r11 = 0;
        r9.setTranslationX(r11);
    L_0x013c:
        r11 = r10.getLeft();
        r11 = r11 + r12;
        r11 = r11 - r0;
        r12 = r9.arrowImageView;
        r12 = r12.getMeasuredWidth();
        r12 = r12 / r7;
        r11 = r11 - r12;
        r11 = (float) r11;
        r12 = r9.arrowImageView;
        r12.setTranslationX(r11);
        r12 = r4.getMeasuredWidth();
        r12 = r12 / r7;
        if (r13 <= r12) goto L_0x0176;
    L_0x0157:
        r12 = org.telegram.messenger.AndroidUtilities.dp(r6);
        r12 = (float) r12;
        r12 = (r11 > r12 ? 1 : (r11 == r12 ? 0 : -1));
        if (r12 >= 0) goto L_0x01bb;
    L_0x0160:
        r12 = org.telegram.messenger.AndroidUtilities.dp(r6);
        r12 = (float) r12;
        r12 = r11 - r12;
        r13 = r9.getTranslationX();
        r13 = r13 + r12;
        r9.setTranslationX(r13);
        r13 = r9.arrowImageView;
        r11 = r11 - r12;
        r13.setTranslationX(r11);
        goto L_0x01bb;
    L_0x0176:
        r12 = r9.getMeasuredWidth();
        r13 = NUM; // 0x41CLASSNAME float:24.0 double:5.450047783E-315;
        r0 = org.telegram.messenger.AndroidUtilities.dp(r13);
        r12 = r12 - r0;
        r12 = (float) r12;
        r12 = (r11 > r12 ? 1 : (r11 == r12 ? 0 : -1));
        if (r12 <= 0) goto L_0x019d;
    L_0x0186:
        r12 = r9.getMeasuredWidth();
        r12 = (float) r12;
        r12 = r11 - r12;
        r13 = org.telegram.messenger.AndroidUtilities.dp(r13);
        r13 = (float) r13;
        r12 = r12 + r13;
        r9.setTranslationX(r12);
        r13 = r9.arrowImageView;
        r11 = r11 - r12;
        r13.setTranslationX(r11);
        goto L_0x01bb;
    L_0x019d:
        r12 = org.telegram.messenger.AndroidUtilities.dp(r6);
        r12 = (float) r12;
        r12 = (r11 > r12 ? 1 : (r11 == r12 ? 0 : -1));
        if (r12 >= 0) goto L_0x01bb;
    L_0x01a6:
        r12 = org.telegram.messenger.AndroidUtilities.dp(r6);
        r12 = (float) r12;
        r12 = r11 - r12;
        r13 = r9.getTranslationX();
        r13 = r13 + r12;
        r9.setTranslationX(r13);
        r13 = r9.arrowImageView;
        r11 = r11 - r12;
        r13.setTranslationX(r11);
    L_0x01bb:
        r9.messageCell = r10;
        r10 = r9.animatorSet;
        if (r10 == 0) goto L_0x01c6;
    L_0x01c1:
        r10.cancel();
        r9.animatorSet = r3;
    L_0x01c6:
        r10 = 1;
        r11 = java.lang.Integer.valueOf(r10);
        r9.setTag(r11);
        r9.setVisibility(r2);
        if (r14 == 0) goto L_0x0205;
    L_0x01d3:
        r11 = new android.animation.AnimatorSet;
        r11.<init>();
        r9.animatorSet = r11;
        r11 = r9.animatorSet;
        r12 = new android.animation.Animator[r10];
        r13 = new float[r7];
        r13 = {0, NUM};
        r14 = "alpha";
        r13 = android.animation.ObjectAnimator.ofFloat(r9, r14, r13);
        r12[r2] = r13;
        r11.playTogether(r12);
        r11 = r9.animatorSet;
        r12 = new org.telegram.ui.Components.HintView$1;
        r12.<init>();
        r11.addListener(r12);
        r11 = r9.animatorSet;
        r12 = 300; // 0x12c float:4.2E-43 double:1.48E-321;
        r11.setDuration(r12);
        r11 = r9.animatorSet;
        r11.start();
        goto L_0x020a;
    L_0x0205:
        r11 = NUM; // 0x3var_ float:1.0 double:5.263544247E-315;
        r9.setAlpha(r11);
    L_0x020a:
        return r10;
        */
        throw new UnsupportedOperationException("Method not decompiled: org.telegram.ui.Components.HintView.showForMessageCell(org.telegram.ui.Cells.ChatMessageCell, java.lang.Object, int, int, boolean):boolean");
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
        if (this.currentType != 4) {
            dp -= iArr[1];
        }
        if (VERSION.SDK_INT >= 21) {
            dp -= AndroidUtilities.statusBarHeight;
        }
        int measuredWidth2 = view2.getMeasuredWidth();
        if (this.isTopArrow) {
            setTranslationY((float) AndroidUtilities.dp(44.0f));
        } else {
            setTranslationY((float) ((dp - getMeasuredHeight()) - ActionBar.getCurrentActionBarHeight()));
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
