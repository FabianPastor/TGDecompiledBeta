package org.telegram.ui;

import org.telegram.tgnet.TLRPC.TL_messageMediaPoll;
import org.telegram.ui.PollCreateActivity.PollCreateActivityDelegate;

/* compiled from: lambda */
public final /* synthetic */ class -$$Lambda$ChatActivity$xjME3A9fFFJMEuiO7cfeOzv2Q5w implements PollCreateActivityDelegate {
    private final /* synthetic */ ChatActivity f$0;

    public /* synthetic */ -$$Lambda$ChatActivity$xjME3A9fFFJMEuiO7cfeOzv2Q5w(ChatActivity chatActivity) {
        this.f$0 = chatActivity;
    }

    public final void sendPoll(TL_messageMediaPoll tL_messageMediaPoll) {
        this.f$0.lambda$processSelectedAttach$46$ChatActivity(tL_messageMediaPoll);
    }
}
