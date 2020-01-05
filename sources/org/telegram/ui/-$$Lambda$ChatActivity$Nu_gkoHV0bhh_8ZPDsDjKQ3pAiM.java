package org.telegram.ui;

import org.telegram.tgnet.TLRPC.TL_messageMediaPoll;
import org.telegram.ui.PollCreateActivity.PollCreateActivityDelegate;

/* compiled from: lambda */
public final /* synthetic */ class -$$Lambda$ChatActivity$Nu_gkoHV0bhh_8ZPDsDjKQ3pAiM implements PollCreateActivityDelegate {
    private final /* synthetic */ ChatActivity f$0;

    public /* synthetic */ -$$Lambda$ChatActivity$Nu_gkoHV0bhh_8ZPDsDjKQ3pAiM(ChatActivity chatActivity) {
        this.f$0 = chatActivity;
    }

    public final void sendPoll(TL_messageMediaPoll tL_messageMediaPoll, boolean z, int i) {
        this.f$0.lambda$processSelectedAttach$54$ChatActivity(tL_messageMediaPoll, z, i);
    }
}
