package org.telegram.ui;

import java.util.Comparator;
import org.telegram.tgnet.TLRPC.ChannelParticipant;

final /* synthetic */ class ChatUsersActivity$$Lambda$12 implements Comparator {
    private final ChatUsersActivity arg$1;
    private final int arg$2;

    ChatUsersActivity$$Lambda$12(ChatUsersActivity chatUsersActivity, int i) {
        this.arg$1 = chatUsersActivity;
        this.arg$2 = i;
    }

    public int compare(Object obj, Object obj2) {
        return this.arg$1.lambda$null$19$ChatUsersActivity(this.arg$2, (ChannelParticipant) obj, (ChannelParticipant) obj2);
    }
}
