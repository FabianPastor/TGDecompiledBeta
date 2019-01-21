package org.telegram.ui;

import org.telegram.tgnet.TLRPC.TL_messageMediaPoll;
import org.telegram.ui.PollCreateActivity.PollCreateActivityDelegate;

final /* synthetic */ class ChatActivity$$Lambda$33 implements PollCreateActivityDelegate {
    private final ChatActivity arg$1;

    ChatActivity$$Lambda$33(ChatActivity chatActivity) {
        this.arg$1 = chatActivity;
    }

    public void sendPoll(TL_messageMediaPoll tL_messageMediaPoll) {
        this.arg$1.lambda$processSelectedAttach$40$ChatActivity(tL_messageMediaPoll);
    }
}
