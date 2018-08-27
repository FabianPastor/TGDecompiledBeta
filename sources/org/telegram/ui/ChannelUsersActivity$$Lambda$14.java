package org.telegram.ui;

import org.telegram.tgnet.TLRPC.ChannelParticipant;
import org.telegram.tgnet.TLRPC.TL_channelAdminRights;
import org.telegram.tgnet.TLRPC.TL_channelBannedRights;
import org.telegram.ui.ChannelRightsEditActivity.ChannelRightsEditActivityDelegate;

final /* synthetic */ class ChannelUsersActivity$$Lambda$14 implements ChannelRightsEditActivityDelegate {
    private final ChannelUsersActivity arg$1;
    private final ChannelParticipant arg$2;

    ChannelUsersActivity$$Lambda$14(ChannelUsersActivity channelUsersActivity, ChannelParticipant channelParticipant) {
        this.arg$1 = channelUsersActivity;
        this.arg$2 = channelParticipant;
    }

    public void didSetRights(int i, TL_channelAdminRights tL_channelAdminRights, TL_channelBannedRights tL_channelBannedRights) {
        this.arg$1.lambda$null$1$ChannelUsersActivity(this.arg$2, i, tL_channelAdminRights, tL_channelBannedRights);
    }
}
