package org.telegram.ui;

import org.telegram.tgnet.TLRPC$BotInlineResult;
import org.telegram.ui.Components.AlertsCreator;

public final /* synthetic */ class ChatActivity$$ExternalSyntheticLambda168 implements AlertsCreator.ScheduleDatePickerDelegate {
    public final /* synthetic */ ChatActivity f$0;
    public final /* synthetic */ TLRPC$BotInlineResult f$1;

    public /* synthetic */ ChatActivity$$ExternalSyntheticLambda168(ChatActivity chatActivity, TLRPC$BotInlineResult tLRPC$BotInlineResult) {
        this.f$0 = chatActivity;
        this.f$1 = tLRPC$BotInlineResult;
    }

    public final void didSelectDate(boolean z, int i) {
        this.f$0.lambda$createView$38(this.f$1, z, i);
    }
}
