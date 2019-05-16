package org.telegram.ui;

import org.telegram.tgnet.TLRPC.TL_messageMediaPoll;
import org.telegram.ui.PollCreateActivity.PollCreateActivityDelegate;

/* compiled from: lambda */
public final /* synthetic */ class -$$Lambda$ChatActivity$vv6AcX9KHzT2Lbx3Kw5UlWaqK5U implements PollCreateActivityDelegate {
    private final /* synthetic */ ChatActivity f$0;

    public /* synthetic */ -$$Lambda$ChatActivity$vv6AcX9KHzT2Lbx3Kw5UlWaqK5U(ChatActivity chatActivity) {
        this.f$0 = chatActivity;
    }

    public final void sendPoll(TL_messageMediaPoll tL_messageMediaPoll) {
        this.f$0.lambda$processSelectedAttach$43$ChatActivity(tL_messageMediaPoll);
    }
}
