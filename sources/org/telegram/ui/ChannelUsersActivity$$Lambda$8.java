package org.telegram.ui;

import java.util.Comparator;
import org.telegram.tgnet.TLRPC.ChannelParticipant;

final /* synthetic */ class ChannelUsersActivity$$Lambda$8 implements Comparator {
    private final ChannelUsersActivity arg$1;

    ChannelUsersActivity$$Lambda$8(ChannelUsersActivity channelUsersActivity) {
        this.arg$1 = channelUsersActivity;
    }

    public int compare(Object obj, Object obj2) {
        return this.arg$1.lambda$null$11$ChannelUsersActivity((ChannelParticipant) obj, (ChannelParticipant) obj2);
    }
}
