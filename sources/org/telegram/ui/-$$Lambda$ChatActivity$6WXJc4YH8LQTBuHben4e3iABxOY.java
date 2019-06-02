package org.telegram.ui;

import org.telegram.tgnet.TLRPC.TL_messageMediaPoll;
import org.telegram.ui.PollCreateActivity.PollCreateActivityDelegate;

/* compiled from: lambda */
public final /* synthetic */ class -$$Lambda$ChatActivity$6WXJc4YH8LQTBuHben4e3iABxOY implements PollCreateActivityDelegate {
    private final /* synthetic */ ChatActivity f$0;

    public /* synthetic */ -$$Lambda$ChatActivity$6WXJc4YH8LQTBuHben4e3iABxOY(ChatActivity chatActivity) {
        this.f$0 = chatActivity;
    }

    public final void sendPoll(TL_messageMediaPoll tL_messageMediaPoll) {
        this.f$0.lambda$processSelectedAttach$44$ChatActivity(tL_messageMediaPoll);
    }
}
