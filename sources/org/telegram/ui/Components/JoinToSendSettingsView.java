package org.telegram.ui.Components;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ValueAnimator;
import android.content.Context;
import android.view.View;
import android.widget.LinearLayout;
import org.telegram.messenger.AndroidUtilities;
import org.telegram.messenger.LocaleController;
import org.telegram.tgnet.TLRPC;
import org.telegram.ui.ActionBar.Theme;
import org.telegram.ui.Cells.HeaderCell;
import org.telegram.ui.Cells.TextCheckCell;
import org.telegram.ui.Cells.TextInfoPrivacyCell;

public class JoinToSendSettingsView extends LinearLayout {
    private final int MAXSPEC = View.MeasureSpec.makeMeasureSpec(999999, Integer.MIN_VALUE);
    private TLRPC.Chat currentChat;
    public boolean isJoinRequest;
    public boolean isJoinToSend;
    public HeaderCell joinHeaderCell;
    public TextCheckCell joinRequestCell;
    public TextInfoPrivacyCell joinRequestInfoCell;
    public TextCheckCell joinToSendCell;
    public TextInfoPrivacyCell joinToSendInfoCell;
    private ValueAnimator toggleAnimator;
    private float toggleValue;

    public JoinToSendSettingsView(Context context, TLRPC.Chat currentChat2) {
        super(context);
        this.currentChat = currentChat2;
        this.isJoinToSend = currentChat2.join_to_send;
        this.isJoinRequest = currentChat2.join_request;
        boolean z = true;
        setOrientation(1);
        HeaderCell headerCell = new HeaderCell(context, 23);
        this.joinHeaderCell = headerCell;
        headerCell.setText(LocaleController.getString("ChannelSettingsJoinTitle", NUM));
        this.joinHeaderCell.setBackgroundColor(Theme.getColor("windowBackgroundWhite"));
        addView(this.joinHeaderCell);
        AnonymousClass1 r1 = new TextCheckCell(context) {
        };
        this.joinToSendCell = r1;
        r1.setBackground(Theme.getSelectorDrawable(true));
        TextCheckCell textCheckCell = this.joinToSendCell;
        String string = LocaleController.getString("ChannelSettingsJoinToSend", NUM);
        boolean z2 = this.isJoinToSend;
        textCheckCell.setTextAndCheck(string, z2, z2);
        int i = 0;
        this.joinToSendCell.setEnabled(currentChat2.creator || (currentChat2.admin_rights != null && currentChat2.admin_rights.ban_users));
        this.joinToSendCell.setOnClickListener(new JoinToSendSettingsView$$ExternalSyntheticLambda1(this));
        addView(this.joinToSendCell);
        AnonymousClass2 r12 = new TextCheckCell(context) {
        };
        this.joinRequestCell = r12;
        r12.setBackground(Theme.getSelectorDrawable(true));
        this.joinRequestCell.setTextAndCheck(LocaleController.getString("ChannelSettingsJoinRequest", NUM), this.isJoinRequest, false);
        float f = 0.0f;
        this.joinRequestCell.setPivotY(0.0f);
        TextCheckCell textCheckCell2 = this.joinRequestCell;
        if (!currentChat2.creator && (currentChat2.admin_rights == null || !currentChat2.admin_rights.ban_users)) {
            z = false;
        }
        textCheckCell2.setEnabled(z);
        this.joinRequestCell.setOnClickListener(new JoinToSendSettingsView$$ExternalSyntheticLambda2(this));
        addView(this.joinRequestCell);
        TextInfoPrivacyCell textInfoPrivacyCell = new TextInfoPrivacyCell(context);
        this.joinToSendInfoCell = textInfoPrivacyCell;
        textInfoPrivacyCell.setText(LocaleController.getString("ChannelSettingsJoinToSendInfo", NUM));
        addView(this.joinToSendInfoCell);
        TextInfoPrivacyCell textInfoPrivacyCell2 = new TextInfoPrivacyCell(context);
        this.joinRequestInfoCell = textInfoPrivacyCell2;
        textInfoPrivacyCell2.setText(LocaleController.getString("ChannelSettingsJoinRequestInfo", NUM));
        addView(this.joinRequestInfoCell);
        boolean z3 = this.isJoinToSend;
        this.toggleValue = z3 ? 1.0f : f;
        this.joinRequestCell.setVisibility(!z3 ? 8 : i);
        updateToggleValue(this.toggleValue);
    }

    /* renamed from: lambda$new$2$org-telegram-ui-Components-JoinToSendSettingsView  reason: not valid java name */
    public /* synthetic */ void m1090lambda$new$2$orgtelegramuiComponentsJoinToSendSettingsView(View e) {
        boolean oldValue = this.isJoinToSend;
        boolean newValue = !this.isJoinToSend;
        if (onJoinToSendToggle(newValue, new JoinToSendSettingsView$$ExternalSyntheticLambda6(this, this.isJoinRequest, oldValue))) {
            m1091lambda$new$3$orgtelegramuiComponentsJoinToSendSettingsView(false);
            setJoinToSend(newValue);
        }
    }

    /* renamed from: lambda$new$1$org-telegram-ui-Components-JoinToSendSettingsView  reason: not valid java name */
    public /* synthetic */ void m1089lambda$new$1$orgtelegramuiComponentsJoinToSendSettingsView(boolean oldJoinToRequest, boolean oldValue) {
        AndroidUtilities.runOnUIThread(new JoinToSendSettingsView$$ExternalSyntheticLambda5(this, oldJoinToRequest, oldValue));
    }

    /* renamed from: lambda$new$0$org-telegram-ui-Components-JoinToSendSettingsView  reason: not valid java name */
    public /* synthetic */ void m1088lambda$new$0$orgtelegramuiComponentsJoinToSendSettingsView(boolean oldJoinToRequest, boolean oldValue) {
        m1091lambda$new$3$orgtelegramuiComponentsJoinToSendSettingsView(oldJoinToRequest);
        setJoinToSend(oldValue);
    }

    /* renamed from: lambda$new$5$org-telegram-ui-Components-JoinToSendSettingsView  reason: not valid java name */
    public /* synthetic */ void m1093lambda$new$5$orgtelegramuiComponentsJoinToSendSettingsView(View e) {
        boolean oldValue = this.isJoinRequest;
        boolean newValue = !this.isJoinRequest;
        if (onJoinRequestToggle(newValue, new JoinToSendSettingsView$$ExternalSyntheticLambda4(this, oldValue))) {
            m1091lambda$new$3$orgtelegramuiComponentsJoinToSendSettingsView(newValue);
        }
    }

    /* renamed from: lambda$new$4$org-telegram-ui-Components-JoinToSendSettingsView  reason: not valid java name */
    public /* synthetic */ void m1092lambda$new$4$orgtelegramuiComponentsJoinToSendSettingsView(boolean oldValue) {
        AndroidUtilities.runOnUIThread(new JoinToSendSettingsView$$ExternalSyntheticLambda3(this, oldValue));
    }

    public void setChat(TLRPC.Chat chat) {
        this.currentChat = chat;
        boolean z = false;
        this.joinToSendCell.setEnabled(chat.creator || (this.currentChat.admin_rights != null && this.currentChat.admin_rights.ban_users));
        TextCheckCell textCheckCell = this.joinRequestCell;
        if (this.currentChat.creator || (this.currentChat.admin_rights != null && this.currentChat.admin_rights.ban_users)) {
            z = true;
        }
        textCheckCell.setEnabled(z);
    }

    public boolean onJoinToSendToggle(boolean newValue, Runnable cancel) {
        return true;
    }

    public boolean onJoinRequestToggle(boolean newValue, Runnable cancel) {
        return true;
    }

    private void updateToggleValue(float value) {
        this.toggleValue = value;
        this.joinRequestCell.setAlpha(value);
        this.joinRequestCell.setTranslationY((1.0f - value) * ((float) (-AndroidUtilities.dp(16.0f))));
        this.joinRequestCell.setScaleY(1.0f - ((1.0f - value) * 0.1f));
        int joinRequestCellHeight = this.joinRequestCell.getMeasuredHeight() <= 0 ? AndroidUtilities.dp(50.0f) : this.joinRequestCell.getMeasuredHeight();
        this.joinToSendInfoCell.setAlpha(1.0f - value);
        this.joinToSendInfoCell.setTranslationY((((float) (-joinRequestCellHeight)) * (1.0f - value)) + (((float) (-AndroidUtilities.dp(4.0f))) * value));
        this.joinRequestInfoCell.setAlpha(value);
        this.joinRequestInfoCell.setTranslationY((((float) (-joinRequestCellHeight)) * (1.0f - value)) + (((float) AndroidUtilities.dp(4.0f)) * (1.0f - value)));
        requestLayout();
    }

    /* renamed from: setJoinRequest */
    public void m1091lambda$new$3$orgtelegramuiComponentsJoinToSendSettingsView(boolean newJoinRequest) {
        this.isJoinRequest = newJoinRequest;
        this.joinRequestCell.setChecked(newJoinRequest);
    }

    public void setJoinToSend(boolean newJoinToSend) {
        this.isJoinToSend = newJoinToSend;
        this.joinToSendCell.setChecked(newJoinToSend);
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
            public void onAnimationEnd(Animator animation) {
                if (!JoinToSendSettingsView.this.isJoinToSend) {
                    JoinToSendSettingsView.this.joinRequestCell.setVisibility(8);
                }
            }
        });
        this.joinRequestCell.setVisibility(0);
        this.toggleAnimator.start();
    }

    /* renamed from: lambda$setJoinToSend$6$org-telegram-ui-Components-JoinToSendSettingsView  reason: not valid java name */
    public /* synthetic */ void m1094x8640bff6(ValueAnimator a) {
        float floatValue = ((Float) a.getAnimatedValue()).floatValue();
        this.toggleValue = floatValue;
        updateToggleValue(floatValue);
    }

    /* access modifiers changed from: protected */
    public void onLayout(boolean changed, int l, int t, int r, int b) {
        HeaderCell headerCell = this.joinHeaderCell;
        int measuredHeight = headerCell.getMeasuredHeight() + 0;
        int y = measuredHeight;
        headerCell.layout(0, 0, r - l, measuredHeight);
        TextCheckCell textCheckCell = this.joinToSendCell;
        int measuredHeight2 = textCheckCell.getMeasuredHeight() + y;
        int y2 = measuredHeight2;
        textCheckCell.layout(0, y, r - l, measuredHeight2);
        TextCheckCell textCheckCell2 = this.joinRequestCell;
        int measuredHeight3 = textCheckCell2.getMeasuredHeight() + y2;
        int y3 = measuredHeight3;
        textCheckCell2.layout(0, y2, r - l, measuredHeight3);
        TextInfoPrivacyCell textInfoPrivacyCell = this.joinToSendInfoCell;
        textInfoPrivacyCell.layout(0, y3, r - l, textInfoPrivacyCell.getMeasuredHeight() + y3);
        TextInfoPrivacyCell textInfoPrivacyCell2 = this.joinRequestInfoCell;
        textInfoPrivacyCell2.layout(0, y3, r - l, textInfoPrivacyCell2.getMeasuredHeight() + y3);
    }

    private int calcHeight() {
        return (int) (((float) (this.joinHeaderCell.getMeasuredHeight() + this.joinToSendCell.getMeasuredHeight())) + (((float) this.joinRequestCell.getMeasuredHeight()) * this.toggleValue) + ((float) AndroidUtilities.lerp(this.joinToSendInfoCell.getMeasuredHeight(), this.joinRequestInfoCell.getMeasuredHeight(), this.toggleValue)));
    }

    /* access modifiers changed from: protected */
    public void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        this.joinHeaderCell.measure(widthMeasureSpec, this.MAXSPEC);
        this.joinToSendCell.measure(widthMeasureSpec, this.MAXSPEC);
        this.joinRequestCell.measure(widthMeasureSpec, this.MAXSPEC);
        this.joinToSendInfoCell.measure(widthMeasureSpec, this.MAXSPEC);
        this.joinRequestInfoCell.measure(widthMeasureSpec, this.MAXSPEC);
        super.onMeasure(widthMeasureSpec, View.MeasureSpec.makeMeasureSpec(calcHeight(), NUM));
    }
}
