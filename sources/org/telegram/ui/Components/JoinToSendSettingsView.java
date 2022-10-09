package org.telegram.ui.Components;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ValueAnimator;
import android.content.Context;
import android.view.View;
import android.widget.LinearLayout;
import org.telegram.messenger.AndroidUtilities;
import org.telegram.messenger.LocaleController;
import org.telegram.messenger.R;
import org.telegram.tgnet.TLRPC$Chat;
import org.telegram.tgnet.TLRPC$TL_chatAdminRights;
import org.telegram.ui.ActionBar.Theme;
import org.telegram.ui.Cells.HeaderCell;
import org.telegram.ui.Cells.TextCheckCell;
import org.telegram.ui.Cells.TextInfoPrivacyCell;
/* loaded from: classes3.dex */
public class JoinToSendSettingsView extends LinearLayout {
    private final int MAXSPEC;
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

    public JoinToSendSettingsView(Context context, TLRPC$Chat tLRPC$Chat) {
        super(context);
        TLRPC$TL_chatAdminRights tLRPC$TL_chatAdminRights;
        TLRPC$TL_chatAdminRights tLRPC$TL_chatAdminRights2;
        this.MAXSPEC = View.MeasureSpec.makeMeasureSpec(999999, Integer.MIN_VALUE);
        this.currentChat = tLRPC$Chat;
        this.isJoinToSend = tLRPC$Chat.join_to_send;
        this.isJoinRequest = tLRPC$Chat.join_request;
        boolean z = true;
        setOrientation(1);
        HeaderCell headerCell = new HeaderCell(context, 23);
        this.joinHeaderCell = headerCell;
        headerCell.setText(LocaleController.getString("ChannelSettingsJoinTitle", R.string.ChannelSettingsJoinTitle));
        this.joinHeaderCell.setBackgroundColor(Theme.getColor("windowBackgroundWhite"));
        addView(this.joinHeaderCell);
        TextCheckCell textCheckCell = new TextCheckCell(this, context) { // from class: org.telegram.ui.Components.JoinToSendSettingsView.1
        };
        this.joinToSendCell = textCheckCell;
        textCheckCell.setBackground(Theme.getSelectorDrawable(true));
        TextCheckCell textCheckCell2 = this.joinToSendCell;
        String string = LocaleController.getString("ChannelSettingsJoinToSend", R.string.ChannelSettingsJoinToSend);
        boolean z2 = this.isJoinToSend;
        textCheckCell2.setTextAndCheck(string, z2, z2);
        int i = 0;
        this.joinToSendCell.setEnabled(tLRPC$Chat.creator || ((tLRPC$TL_chatAdminRights2 = tLRPC$Chat.admin_rights) != null && tLRPC$TL_chatAdminRights2.ban_users));
        this.joinToSendCell.setOnClickListener(new View.OnClickListener() { // from class: org.telegram.ui.Components.JoinToSendSettingsView$$ExternalSyntheticLambda1
            @Override // android.view.View.OnClickListener
            public final void onClick(View view) {
                JoinToSendSettingsView.this.lambda$new$2(view);
            }
        });
        addView(this.joinToSendCell);
        TextCheckCell textCheckCell3 = new TextCheckCell(this, context) { // from class: org.telegram.ui.Components.JoinToSendSettingsView.2
        };
        this.joinRequestCell = textCheckCell3;
        textCheckCell3.setBackground(Theme.getSelectorDrawable(true));
        this.joinRequestCell.setTextAndCheck(LocaleController.getString("ChannelSettingsJoinRequest", R.string.ChannelSettingsJoinRequest), this.isJoinRequest, false);
        float f = 0.0f;
        this.joinRequestCell.setPivotY(0.0f);
        TextCheckCell textCheckCell4 = this.joinRequestCell;
        if (!tLRPC$Chat.creator && ((tLRPC$TL_chatAdminRights = tLRPC$Chat.admin_rights) == null || !tLRPC$TL_chatAdminRights.ban_users)) {
            z = false;
        }
        textCheckCell4.setEnabled(z);
        this.joinRequestCell.setOnClickListener(new View.OnClickListener() { // from class: org.telegram.ui.Components.JoinToSendSettingsView$$ExternalSyntheticLambda2
            @Override // android.view.View.OnClickListener
            public final void onClick(View view) {
                JoinToSendSettingsView.this.lambda$new$5(view);
            }
        });
        addView(this.joinRequestCell);
        TextInfoPrivacyCell textInfoPrivacyCell = new TextInfoPrivacyCell(context);
        this.joinToSendInfoCell = textInfoPrivacyCell;
        textInfoPrivacyCell.setText(LocaleController.getString("ChannelSettingsJoinToSendInfo", R.string.ChannelSettingsJoinToSendInfo));
        addView(this.joinToSendInfoCell);
        TextInfoPrivacyCell textInfoPrivacyCell2 = new TextInfoPrivacyCell(context);
        this.joinRequestInfoCell = textInfoPrivacyCell2;
        textInfoPrivacyCell2.setText(LocaleController.getString("ChannelSettingsJoinRequestInfo", R.string.ChannelSettingsJoinRequestInfo));
        addView(this.joinRequestInfoCell);
        boolean z3 = this.isJoinToSend;
        this.toggleValue = z3 ? 1.0f : f;
        this.joinRequestCell.setVisibility(!z3 ? 8 : i);
        updateToggleValue(this.toggleValue);
    }

    /* JADX INFO: Access modifiers changed from: private */
    public /* synthetic */ void lambda$new$2(View view) {
        final boolean z = this.isJoinToSend;
        boolean z2 = !z;
        final boolean z3 = this.isJoinRequest;
        if (onJoinToSendToggle(z2, new Runnable() { // from class: org.telegram.ui.Components.JoinToSendSettingsView$$ExternalSyntheticLambda5
            @Override // java.lang.Runnable
            public final void run() {
                JoinToSendSettingsView.this.lambda$new$1(z3, z);
            }
        })) {
            lambda$new$3(false);
            setJoinToSend(z2);
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public /* synthetic */ void lambda$new$1(final boolean z, final boolean z2) {
        AndroidUtilities.runOnUIThread(new Runnable() { // from class: org.telegram.ui.Components.JoinToSendSettingsView$$ExternalSyntheticLambda6
            @Override // java.lang.Runnable
            public final void run() {
                JoinToSendSettingsView.this.lambda$new$0(z, z2);
            }
        });
    }

    /* JADX INFO: Access modifiers changed from: private */
    public /* synthetic */ void lambda$new$0(boolean z, boolean z2) {
        lambda$new$3(z);
        setJoinToSend(z2);
    }

    /* JADX INFO: Access modifiers changed from: private */
    public /* synthetic */ void lambda$new$5(View view) {
        final boolean z = this.isJoinRequest;
        boolean z2 = !z;
        if (onJoinRequestToggle(z2, new Runnable() { // from class: org.telegram.ui.Components.JoinToSendSettingsView$$ExternalSyntheticLambda3
            @Override // java.lang.Runnable
            public final void run() {
                JoinToSendSettingsView.this.lambda$new$4(z);
            }
        })) {
            lambda$new$3(z2);
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public /* synthetic */ void lambda$new$4(final boolean z) {
        AndroidUtilities.runOnUIThread(new Runnable() { // from class: org.telegram.ui.Components.JoinToSendSettingsView$$ExternalSyntheticLambda4
            @Override // java.lang.Runnable
            public final void run() {
                JoinToSendSettingsView.this.lambda$new$3(z);
            }
        });
    }

    public void setChat(TLRPC$Chat tLRPC$Chat) {
        TLRPC$TL_chatAdminRights tLRPC$TL_chatAdminRights;
        TLRPC$TL_chatAdminRights tLRPC$TL_chatAdminRights2;
        this.currentChat = tLRPC$Chat;
        boolean z = false;
        this.joinToSendCell.setEnabled(tLRPC$Chat.creator || ((tLRPC$TL_chatAdminRights2 = tLRPC$Chat.admin_rights) != null && tLRPC$TL_chatAdminRights2.ban_users));
        TextCheckCell textCheckCell = this.joinRequestCell;
        TLRPC$Chat tLRPC$Chat2 = this.currentChat;
        if (tLRPC$Chat2.creator || ((tLRPC$TL_chatAdminRights = tLRPC$Chat2.admin_rights) != null && tLRPC$TL_chatAdminRights.ban_users)) {
            z = true;
        }
        textCheckCell.setEnabled(z);
    }

    private void updateToggleValue(float f) {
        this.toggleValue = f;
        this.joinRequestCell.setAlpha(f);
        float f2 = 1.0f - f;
        this.joinRequestCell.setTranslationY((-AndroidUtilities.dp(16.0f)) * f2);
        this.joinRequestCell.setScaleY(1.0f - (0.1f * f2));
        int dp = this.joinRequestCell.getMeasuredHeight() <= 0 ? AndroidUtilities.dp(50.0f) : this.joinRequestCell.getMeasuredHeight();
        this.joinToSendInfoCell.setAlpha(f2);
        float f3 = (-dp) * f2;
        this.joinToSendInfoCell.setTranslationY(((-AndroidUtilities.dp(4.0f)) * f) + f3);
        this.joinRequestInfoCell.setAlpha(f);
        this.joinRequestInfoCell.setTranslationY(f3 + (AndroidUtilities.dp(4.0f) * f2));
        requestLayout();
    }

    public void showJoinToSend(boolean z) {
        this.joinToSendCell.setVisibility(z ? 0 : 8);
        if (!z) {
            this.isJoinToSend = true;
            this.joinRequestCell.setVisibility(0);
            updateToggleValue(1.0f);
        }
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
        ofFloat.setDuration(200L);
        this.toggleAnimator.setInterpolator(CubicBezierInterpolator.DEFAULT);
        this.toggleAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() { // from class: org.telegram.ui.Components.JoinToSendSettingsView$$ExternalSyntheticLambda0
            @Override // android.animation.ValueAnimator.AnimatorUpdateListener
            public final void onAnimationUpdate(ValueAnimator valueAnimator2) {
                JoinToSendSettingsView.this.lambda$setJoinToSend$6(valueAnimator2);
            }
        });
        this.toggleAnimator.addListener(new AnimatorListenerAdapter() { // from class: org.telegram.ui.Components.JoinToSendSettingsView.3
            @Override // android.animation.AnimatorListenerAdapter, android.animation.Animator.AnimatorListener
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

    /* JADX INFO: Access modifiers changed from: private */
    public /* synthetic */ void lambda$setJoinToSend$6(ValueAnimator valueAnimator) {
        float floatValue = ((Float) valueAnimator.getAnimatedValue()).floatValue();
        this.toggleValue = floatValue;
        updateToggleValue(floatValue);
    }

    @Override // android.widget.LinearLayout, android.view.ViewGroup, android.view.View
    protected void onLayout(boolean z, int i, int i2, int i3, int i4) {
        HeaderCell headerCell = this.joinHeaderCell;
        int i5 = i3 - i;
        int measuredHeight = headerCell.getMeasuredHeight() + 0;
        headerCell.layout(0, 0, i5, measuredHeight);
        if (this.joinToSendCell.getVisibility() == 0) {
            TextCheckCell textCheckCell = this.joinToSendCell;
            int measuredHeight2 = textCheckCell.getMeasuredHeight() + measuredHeight;
            textCheckCell.layout(0, measuredHeight, i5, measuredHeight2);
            measuredHeight = measuredHeight2;
        }
        TextCheckCell textCheckCell2 = this.joinRequestCell;
        int measuredHeight3 = textCheckCell2.getMeasuredHeight() + measuredHeight;
        textCheckCell2.layout(0, measuredHeight, i5, measuredHeight3);
        TextInfoPrivacyCell textInfoPrivacyCell = this.joinToSendInfoCell;
        textInfoPrivacyCell.layout(0, measuredHeight3, i5, textInfoPrivacyCell.getMeasuredHeight() + measuredHeight3);
        TextInfoPrivacyCell textInfoPrivacyCell2 = this.joinRequestInfoCell;
        textInfoPrivacyCell2.layout(0, measuredHeight3, i5, textInfoPrivacyCell2.getMeasuredHeight() + measuredHeight3);
    }

    private int calcHeight() {
        float measuredHeight;
        float measuredHeight2 = this.joinHeaderCell.getMeasuredHeight();
        if (this.joinToSendCell.getVisibility() == 0) {
            measuredHeight = this.joinToSendCell.getMeasuredHeight() + (this.joinRequestCell.getMeasuredHeight() * this.toggleValue);
        } else {
            measuredHeight = this.joinRequestCell.getMeasuredHeight();
        }
        return (int) (measuredHeight2 + measuredHeight + AndroidUtilities.lerp(this.joinToSendInfoCell.getMeasuredHeight(), this.joinRequestInfoCell.getMeasuredHeight(), this.toggleValue));
    }

    @Override // android.widget.LinearLayout, android.view.View
    protected void onMeasure(int i, int i2) {
        this.joinHeaderCell.measure(i, this.MAXSPEC);
        this.joinToSendCell.measure(i, this.MAXSPEC);
        this.joinRequestCell.measure(i, this.MAXSPEC);
        this.joinToSendInfoCell.measure(i, this.MAXSPEC);
        this.joinRequestInfoCell.measure(i, this.MAXSPEC);
        super.onMeasure(i, View.MeasureSpec.makeMeasureSpec(calcHeight(), NUM));
    }
}
