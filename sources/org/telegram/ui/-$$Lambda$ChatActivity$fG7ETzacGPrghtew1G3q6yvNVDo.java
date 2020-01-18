package org.telegram.ui;

import org.telegram.tgnet.TLRPC.BotInlineResult;
import org.telegram.ui.Components.AlertsCreator.ScheduleDatePickerDelegate;

/* compiled from: lambda */
public final /* synthetic */ class -$$Lambda$ChatActivity$fG7ETzacGPrghtew1G3q6yvNVDo implements ScheduleDatePickerDelegate {
    private final /* synthetic */ ChatActivity f$0;
    private final /* synthetic */ BotInlineResult f$1;

    public /* synthetic */ -$$Lambda$ChatActivity$fG7ETzacGPrghtew1G3q6yvNVDo(ChatActivity chatActivity, BotInlineResult botInlineResult) {
        this.f$0 = chatActivity;
        this.f$1 = botInlineResult;
    }

    public final void didSelectDate(boolean z, int i) {
        this.f$0.lambda$null$20$ChatActivity(this.f$1, z, i);
    }
}
