package org.telegram.ui;

import org.telegram.tgnet.TLRPC;
import org.telegram.ui.Components.AlertsCreator;

public final /* synthetic */ class ChatActivity$$ExternalSyntheticLambda154 implements AlertsCreator.ScheduleDatePickerDelegate {
    public final /* synthetic */ ChatActivity f$0;
    public final /* synthetic */ TLRPC.BotInlineResult f$1;

    public /* synthetic */ ChatActivity$$ExternalSyntheticLambda154(ChatActivity chatActivity, TLRPC.BotInlineResult botInlineResult) {
        this.f$0 = chatActivity;
        this.f$1 = botInlineResult;
    }

    public final void didSelectDate(boolean z, int i) {
        this.f$0.m2965lambda$createView$45$orgtelegramuiChatActivity(this.f$1, z, i);
    }
}
