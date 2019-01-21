package org.telegram.ui;

import java.util.Comparator;
import org.telegram.tgnet.TLRPC.ChannelParticipant;

final /* synthetic */ class ChatUsersActivity$$Lambda$11 implements Comparator {
    private final ChatUsersActivity arg$1;
    private final int arg$2;

    ChatUsersActivity$$Lambda$11(ChatUsersActivity chatUsersActivity, int i) {
        this.arg$1 = chatUsersActivity;
        this.arg$2 = i;
    }

    public int compare(Object obj, Object obj2) {
        return this.arg$1.lambda$null$18$ChatUsersActivity(this.arg$2, (ChannelParticipant) obj, (ChannelParticipant) obj2);
    }
}
