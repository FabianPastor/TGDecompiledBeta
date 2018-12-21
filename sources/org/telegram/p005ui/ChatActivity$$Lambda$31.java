package org.telegram.p005ui;

import org.telegram.p005ui.PollCreateActivity.PollCreateActivityDelegate;
import org.telegram.tgnet.TLRPC.TL_messageMediaPoll;

/* renamed from: org.telegram.ui.ChatActivity$$Lambda$31 */
final /* synthetic */ class ChatActivity$$Lambda$31 implements PollCreateActivityDelegate {
    private final ChatActivity arg$1;

    ChatActivity$$Lambda$31(ChatActivity chatActivity) {
        this.arg$1 = chatActivity;
    }

    public void sendPoll(TL_messageMediaPoll tL_messageMediaPoll) {
        this.arg$1.lambda$processSelectedAttach$38$ChatActivity(tL_messageMediaPoll);
    }
}
