package org.telegram.ui;

import java.util.HashMap;
import org.telegram.tgnet.TLRPC$TL_messageMediaPoll;
import org.telegram.ui.PollCreateActivity;

public final /* synthetic */ class ChatActivity$$ExternalSyntheticLambda257 implements PollCreateActivity.PollCreateActivityDelegate {
    public final /* synthetic */ ChatActivity f$0;

    public /* synthetic */ ChatActivity$$ExternalSyntheticLambda257(ChatActivity chatActivity) {
        this.f$0 = chatActivity;
    }

    public final void sendPoll(TLRPC$TL_messageMediaPoll tLRPC$TL_messageMediaPoll, HashMap hashMap, boolean z, int i) {
        this.f$0.lambda$openPollCreate$93(tLRPC$TL_messageMediaPoll, hashMap, z, i);
    }
}