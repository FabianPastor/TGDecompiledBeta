package org.telegram.ui;

import java.util.HashMap;
import org.telegram.tgnet.TLRPC;
import org.telegram.ui.PollCreateActivity;

public final /* synthetic */ class ChatActivity$$ExternalSyntheticLambda160 implements PollCreateActivity.PollCreateActivityDelegate {
    public final /* synthetic */ ChatActivity f$0;

    public /* synthetic */ ChatActivity$$ExternalSyntheticLambda160(ChatActivity chatActivity) {
        this.f$0 = chatActivity;
    }

    public final void sendPoll(TLRPC.TL_messageMediaPoll tL_messageMediaPoll, HashMap hashMap, boolean z, int i) {
        this.f$0.m1769lambda$openPollCreate$91$orgtelegramuiChatActivity(tL_messageMediaPoll, hashMap, z, i);
    }
}
