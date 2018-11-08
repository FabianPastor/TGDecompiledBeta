package org.telegram.p005ui;

import java.util.Comparator;
import org.telegram.tgnet.TLRPC.ChannelParticipant;

/* renamed from: org.telegram.ui.ChannelUsersActivity$$Lambda$9 */
final /* synthetic */ class ChannelUsersActivity$$Lambda$9 implements Comparator {
    private final ChannelUsersActivity arg$1;

    ChannelUsersActivity$$Lambda$9(ChannelUsersActivity channelUsersActivity) {
        this.arg$1 = channelUsersActivity;
    }

    public int compare(Object obj, Object obj2) {
        return this.arg$1.lambda$null$12$ChannelUsersActivity((ChannelParticipant) obj, (ChannelParticipant) obj2);
    }
}
