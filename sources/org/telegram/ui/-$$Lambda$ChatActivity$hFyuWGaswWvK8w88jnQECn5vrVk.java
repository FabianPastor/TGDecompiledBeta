package org.telegram.ui;

import org.telegram.tgnet.TLRPC.TL_messageMediaPoll;
import org.telegram.ui.PollCreateActivity.PollCreateActivityDelegate;

/* compiled from: lambda */
public final /* synthetic */ class -$$Lambda$ChatActivity$hFyuWGaswWvK8w88jnQECn5vrVk implements PollCreateActivityDelegate {
    private final /* synthetic */ ChatActivity f$0;

    public /* synthetic */ -$$Lambda$ChatActivity$hFyuWGaswWvK8w88jnQECn5vrVk(ChatActivity chatActivity) {
        this.f$0 = chatActivity;
    }

    public final void sendPoll(TL_messageMediaPoll tL_messageMediaPoll, boolean z, int i) {
        this.f$0.lambda$processSelectedAttach$50$ChatActivity(tL_messageMediaPoll, z, i);
    }
}
