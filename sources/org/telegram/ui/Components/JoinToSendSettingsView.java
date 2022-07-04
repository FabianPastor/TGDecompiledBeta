package org.telegram.ui.Components;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ValueAnimator;
import android.view.View;
import android.widget.LinearLayout;
import org.telegram.messenger.AndroidUtilities;
import org.telegram.tgnet.TLRPC$Chat;
import org.telegram.ui.Cells.HeaderCell;
import org.telegram.ui.Cells.TextCheckCell;
import org.telegram.ui.Cells.TextInfoPrivacyCell;

public class JoinToSendSettingsView extends LinearLayout {
    private final int MAXSPEC = View.MeasureSpec.makeMeasureSpec(999999, Integer.MIN_VALUE);
    private TLRPC$Chat currentChat;
    public boolean isJoinRequest;
    public boolean isJoinToSend;
    public HeaderCell joinHeaderCell;
    public TextCheckCell joinRequestCell;
    public TextInfoPrivacyCell joinRequestInfoCell;
    public TextCheckCell joinToSendCell;
    public TextInfoPrivacyCell joinToSendInfoCell;
    private ValueAnimator toggleAnimator;
    private float toggleValue;

    public boolean onJoinRequestToggle(boolean z, Runnable runnable) {
        return true;
    }

    public boolean onJoinToSendToggle(boolean z, Runnable runnable) {
        return true;
    }

    /* JADX WARNING: Code restructure failed: missing block: B:2:0x0066, code lost:
        r2 = r7.admin_rights;
     */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public JoinToSendSettingsView(android.content.Context r6, org.telegram.tgnet.TLRPC$Chat r7) {
        /*
            r5 = this;
            r5.<init>(r6)
            r0 = 999999(0xvar_f, float:1.401297E-39)
            r1 = -2147483648(0xfffffffvar_, float:-0.0)
            int r0 = android.view.View.MeasureSpec.makeMeasureSpec(r0, r1)
            r5.MAXSPEC = r0
            r5.currentChat = r7
            boolean r0 = r7.join_to_send
            r5.isJoinToSend = r0
            boolean r0 = r7.join_request
            r5.isJoinRequest = r0
            r0 = 1
            r5.setOrientation(r0)
            org.telegram.ui.Cells.HeaderCell r1 = new org.telegram.ui.Cells.HeaderCell
            r2 = 23
            r1.<init>((android.content.Context) r6, (int) r2)
            r5.joinHeaderCell = r1
            java.lang.String r2 = "ChannelSettingsJoinTitle"
            r3 = 2131624961(0x7f0e0401, float:1.8877116E38)
            java.lang.String r2 = org.telegram.messenger.LocaleController.getString(r2, r3)
            r1.setText(r2)
            org.telegram.ui.Cells.HeaderCell r1 = r5.joinHeaderCell
            java.lang.String r2 = "windowBackgroundWhite"
            int r2 = org.telegram.ui.ActionBar.Theme.getColor(r2)
            r1.setBackgroundColor(r2)
            org.telegram.ui.Cells.HeaderCell r1 = r5.joinHeaderCell
            r5.addView(r1)
            org.telegram.ui.Components.JoinToSendSettingsView$1 r1 = new org.telegram.ui.Components.JoinToSendSettingsView$1
            r1.<init>(r5, r6)
            r5.joinToSendCell = r1
            android.graphics.drawable.Drawable r2 = org.telegram.ui.ActionBar.Theme.getSelectorDrawable(r0)
            r1.setBackground(r2)
            org.telegram.ui.Cells.TextCheckCell r1 = r5.joinToSendCell
            java.lang.String r2 = "ChannelSettingsJoinToSend"
            r3 = 2131624962(0x7f0e0402, float:1.8877119E38)
            java.lang.String r2 = org.telegram.messenger.LocaleController.getString(r2, r3)
            boolean r3 = r5.isJoinToSend
            r1.setTextAndCheck(r2, r3, r3)
            org.telegram.ui.Cells.TextCheckCell r1 = r5.joinToSendCell
            boolean r2 = r7.creator
            r3 = 0
            if (r2 != 0) goto L_0x0071
            org.telegram.tgnet.TLRPC$TL_chatAdminRights r2 = r7.admin_rights
            if (r2 == 0) goto L_0x006f
            boolean r2 = r2.ban_users
            if (r2 == 0) goto L_0x006f
            goto L_0x0071
        L_0x006f:
            r2 = 0
            goto L_0x0072
        L_0x0071:
            r2 = 1
        L_0x0072:
            r1.setEnabled(r2)
            org.telegram.ui.Cells.TextCheckCell r1 = r5.joinToSendCell
            org.telegram.ui.Components.JoinToSendSettingsView$$ExternalSyntheticLambda1 r2 = new org.telegram.ui.Components.JoinToSendSettingsView$$ExternalSyntheticLambda1
            r2.<init>(r5)
            r1.setOnClickListener(r2)
            org.telegram.ui.Cells.TextCheckCell r1 = r5.joinToSendCell
            r5.addView(r1)
            org.telegram.ui.Components.JoinToSendSettingsView$2 r1 = new org.telegram.ui.Components.JoinToSendSettingsView$2
            r1.<init>(r5, r6)
            r5.joinRequestCell = r1
            android.graphics.drawable.Drawable r2 = org.telegram.ui.ActionBar.Theme.getSelectorDrawable(r0)
            r1.setBackground(r2)
            org.telegram.ui.Cells.TextCheckCell r1 = r5.joinRequestCell
            r2 = 2131624959(0x7f0e03ff, float:1.8877112E38)
            java.lang.String r4 = "ChannelSettingsJoinRequest"
            java.lang.String r2 = org.telegram.messenger.LocaleController.getString(r4, r2)
            boolean r4 = r5.isJoinRequest
            r1.setTextAndCheck(r2, r4, r3)
            org.telegram.ui.Cells.TextCheckCell r1 = r5.joinRequestCell
            r2 = 0
            r1.setPivotY(r2)
            org.telegram.ui.Cells.TextCheckCell r1 = r5.joinRequestCell
            boolean r4 = r7.creator
            if (r4 != 0) goto L_0x00b8
            org.telegram.tgnet.TLRPC$TL_chatAdminRights r7 = r7.admin_rights
            if (r7 == 0) goto L_0x00b7
            boolean r7 = r7.ban_users
            if (r7 == 0) goto L_0x00b7
            goto L_0x00b8
        L_0x00b7:
            r0 = 0
        L_0x00b8:
            r1.setEnabled(r0)
            org.telegram.ui.Cells.TextCheckCell r7 = r5.joinRequestCell
            org.telegram.ui.Components.JoinToSendSettingsView$$ExternalSyntheticLambda2 r0 = new org.telegram.ui.Components.JoinToSendSettingsView$$ExternalSyntheticLambda2
            r0.<init>(r5)
            r7.setOnClickListener(r0)
            org.telegram.ui.Cells.TextCheckCell r7 = r5.joinRequestCell
            r5.addView(r7)
            org.telegram.ui.Cells.TextInfoPrivacyCell r7 = new org.telegram.ui.Cells.TextInfoPrivacyCell
            r7.<init>(r6)
            r5.joinToSendInfoCell = r7
            r0 = 2131624963(0x7f0e0403, float:1.887712E38)
            java.lang.String r1 = "ChannelSettingsJoinToSendInfo"
            java.lang.String r0 = org.telegram.messenger.LocaleController.getString(r1, r0)
            r7.setText(r0)
            org.telegram.ui.Cells.TextInfoPrivacyCell r7 = r5.joinToSendInfoCell
            r5.addView(r7)
            org.telegram.ui.Cells.TextInfoPrivacyCell r7 = new org.telegram.ui.Cells.TextInfoPrivacyCell
            r7.<init>(r6)
            r5.joinRequestInfoCell = r7
            r6 = 2131624960(0x7f0e0400, float:1.8877114E38)
            java.lang.String r0 = "ChannelSettingsJoinRequestInfo"
            java.lang.String r6 = org.telegram.messenger.LocaleController.getString(r0, r6)
            r7.setText(r6)
            org.telegram.ui.Cells.TextInfoPrivacyCell r6 = r5.joinRequestInfoCell
            r5.addView(r6)
            boolean r6 = r5.isJoinToSend
            if (r6 == 0) goto L_0x0100
            r2 = 1065353216(0x3var_, float:1.0)
        L_0x0100:
            r5.toggleValue = r2
            org.telegram.ui.Cells.TextCheckCell r7 = r5.joinRequestCell
            if (r6 == 0) goto L_0x0107
            goto L_0x0109
        L_0x0107:
            r3 = 8
        L_0x0109:
            r7.setVisibility(r3)
            float r6 = r5.toggleValue
            r5.updateToggleValue(r6)
            return
        */
        throw new UnsupportedOperationException("Method not decompiled: org.telegram.ui.Components.JoinToSendSettingsView.<init>(android.content.Context, org.telegram.tgnet.TLRPC$Chat):void");
    }

    /* access modifiers changed from: private */
    public /* synthetic */ void lambda$new$2(View view) {
        boolean z = this.isJoinToSend;
        boolean z2 = !z;
        if (onJoinToSendToggle(z2, new JoinToSendSettingsView$$ExternalSyntheticLambda5(this, this.isJoinRequest, z))) {
            lambda$new$3(false);
            setJoinToSend(z2);
        }
    }

    /* access modifiers changed from: private */
    public /* synthetic */ void lambda$new$1(boolean z, boolean z2) {
        AndroidUtilities.runOnUIThread(new JoinToSendSettingsView$$ExternalSyntheticLambda6(this, z, z2));
    }

    /* access modifiers changed from: private */
    public /* synthetic */ void lambda$new$0(boolean z, boolean z2) {
        lambda$new$3(z);
        setJoinToSend(z2);
    }

    /* access modifiers changed from: private */
    public /* synthetic */ void lambda$new$5(View view) {
        boolean z = this.isJoinRequest;
        boolean z2 = !z;
        if (onJoinRequestToggle(z2, new JoinToSendSettingsView$$ExternalSyntheticLambda3(this, z))) {
            lambda$new$3(z2);
        }
    }

    /* access modifiers changed from: private */
    public /* synthetic */ void lambda$new$4(boolean z) {
        AndroidUtilities.runOnUIThread(new JoinToSendSettingsView$$ExternalSyntheticLambda4(this, z));
    }

    /* JADX WARNING: Code restructure failed: missing block: B:2:0x000a, code lost:
        r5 = r5.admin_rights;
     */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public void setChat(org.telegram.tgnet.TLRPC$Chat r5) {
        /*
            r4 = this;
            r4.currentChat = r5
            org.telegram.ui.Cells.TextCheckCell r0 = r4.joinToSendCell
            boolean r1 = r5.creator
            r2 = 0
            r3 = 1
            if (r1 != 0) goto L_0x0015
            org.telegram.tgnet.TLRPC$TL_chatAdminRights r5 = r5.admin_rights
            if (r5 == 0) goto L_0x0013
            boolean r5 = r5.ban_users
            if (r5 == 0) goto L_0x0013
            goto L_0x0015
        L_0x0013:
            r5 = 0
            goto L_0x0016
        L_0x0015:
            r5 = 1
        L_0x0016:
            r0.setEnabled(r5)
            org.telegram.ui.Cells.TextCheckCell r5 = r4.joinRequestCell
            org.telegram.tgnet.TLRPC$Chat r0 = r4.currentChat
            boolean r1 = r0.creator
            if (r1 != 0) goto L_0x0029
            org.telegram.tgnet.TLRPC$TL_chatAdminRights r0 = r0.admin_rights
            if (r0 == 0) goto L_0x002a
            boolean r0 = r0.ban_users
            if (r0 == 0) goto L_0x002a
        L_0x0029:
            r2 = 1
        L_0x002a:
            r5.setEnabled(r2)
            return
        */
        throw new UnsupportedOperationException("Method not decompiled: org.telegram.ui.Components.JoinToSendSettingsView.setChat(org.telegram.tgnet.TLRPC$Chat):void");
    }

    private void updateToggleValue(float f) {
        this.toggleValue = f;
        this.joinRequestCell.setAlpha(f);
        float f2 = 1.0f - f;
        this.joinRequestCell.setTranslationY(((float) (-AndroidUtilities.dp(16.0f))) * f2);
        this.joinRequestCell.setScaleY(1.0f - (0.1f * f2));
        int dp = this.joinRequestCell.getMeasuredHeight() <= 0 ? AndroidUtilities.dp(50.0f) : this.joinRequestCell.getMeasuredHeight();
        this.joinToSendInfoCell.setAlpha(f2);
        float f3 = ((float) (-dp)) * f2;
        this.joinToSendInfoCell.setTranslationY((((float) (-AndroidUtilities.dp(4.0f))) * f) + f3);
        this.joinRequestInfoCell.setAlpha(f);
        this.joinRequestInfoCell.setTranslationY(f3 + (((float) AndroidUtilities.dp(4.0f)) * f2));
        requestLayout();
    }

    /* renamed from: setJoinRequest */
    public void lambda$new$3(boolean z) {
        this.isJoinRequest = z;
        this.joinRequestCell.setChecked(z);
    }

    public void setJoinToSend(boolean z) {
        this.isJoinToSend = z;
        this.joinToSendCell.setChecked(z);
        this.joinToSendCell.setDivider(this.isJoinToSend);
        this.joinRequestCell.setChecked(this.isJoinRequest);
        ValueAnimator valueAnimator = this.toggleAnimator;
        if (valueAnimator != null) {
            valueAnimator.cancel();
        }
        float[] fArr = new float[2];
        fArr[0] = this.toggleValue;
        fArr[1] = this.isJoinToSend ? 1.0f : 0.0f;
        ValueAnimator ofFloat = ValueAnimator.ofFloat(fArr);
        this.toggleAnimator = ofFloat;
        ofFloat.setDuration(200);
        this.toggleAnimator.setInterpolator(CubicBezierInterpolator.DEFAULT);
        this.toggleAnimator.addUpdateListener(new JoinToSendSettingsView$$ExternalSyntheticLambda0(this));
        this.toggleAnimator.addListener(new AnimatorListenerAdapter() {
            public void onAnimationEnd(Animator animator) {
                JoinToSendSettingsView joinToSendSettingsView = JoinToSendSettingsView.this;
                if (!joinToSendSettingsView.isJoinToSend) {
                    joinToSendSettingsView.joinRequestCell.setVisibility(8);
                }
            }
        });
        this.joinRequestCell.setVisibility(0);
        this.toggleAnimator.start();
    }

    /* access modifiers changed from: private */
    public /* synthetic */ void lambda$setJoinToSend$6(ValueAnimator valueAnimator) {
        float floatValue = ((Float) valueAnimator.getAnimatedValue()).floatValue();
        this.toggleValue = floatValue;
        updateToggleValue(floatValue);
    }

    /* access modifiers changed from: protected */
    public void onLayout(boolean z, int i, int i2, int i3, int i4) {
        HeaderCell headerCell = this.joinHeaderCell;
        int i5 = i3 - i;
        int measuredHeight = headerCell.getMeasuredHeight() + 0;
        headerCell.layout(0, 0, i5, measuredHeight);
        TextCheckCell textCheckCell = this.joinToSendCell;
        int measuredHeight2 = textCheckCell.getMeasuredHeight() + measuredHeight;
        textCheckCell.layout(0, measuredHeight, i5, measuredHeight2);
        TextCheckCell textCheckCell2 = this.joinRequestCell;
        int measuredHeight3 = textCheckCell2.getMeasuredHeight() + measuredHeight2;
        textCheckCell2.layout(0, measuredHeight2, i5, measuredHeight3);
        TextInfoPrivacyCell textInfoPrivacyCell = this.joinToSendInfoCell;
        textInfoPrivacyCell.layout(0, measuredHeight3, i5, textInfoPrivacyCell.getMeasuredHeight() + measuredHeight3);
        TextInfoPrivacyCell textInfoPrivacyCell2 = this.joinRequestInfoCell;
        textInfoPrivacyCell2.layout(0, measuredHeight3, i5, textInfoPrivacyCell2.getMeasuredHeight() + measuredHeight3);
    }

    private int calcHeight() {
        return (int) (((float) (this.joinHeaderCell.getMeasuredHeight() + this.joinToSendCell.getMeasuredHeight())) + (((float) this.joinRequestCell.getMeasuredHeight()) * this.toggleValue) + ((float) AndroidUtilities.lerp(this.joinToSendInfoCell.getMeasuredHeight(), this.joinRequestInfoCell.getMeasuredHeight(), this.toggleValue)));
    }

    /* access modifiers changed from: protected */
    public void onMeasure(int i, int i2) {
        this.joinHeaderCell.measure(i, this.MAXSPEC);
        this.joinToSendCell.measure(i, this.MAXSPEC);
        this.joinRequestCell.measure(i, this.MAXSPEC);
        this.joinToSendInfoCell.measure(i, this.MAXSPEC);
        this.joinRequestInfoCell.measure(i, this.MAXSPEC);
        super.onMeasure(i, View.MeasureSpec.makeMeasureSpec(calcHeight(), NUM));
    }
}
