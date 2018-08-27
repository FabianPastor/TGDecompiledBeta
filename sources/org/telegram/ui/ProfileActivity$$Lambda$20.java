package org.telegram.ui;

import org.telegram.tgnet.TLRPC.TL_channelAdminRights;
import org.telegram.tgnet.TLRPC.TL_channelBannedRights;
import org.telegram.ui.ChannelRightsEditActivity.ChannelRightsEditActivityDelegate;

final /* synthetic */ class ProfileActivity$$Lambda$20 implements ChannelRightsEditActivityDelegate {
    private final ProfileActivity arg$1;

    ProfileActivity$$Lambda$20(ProfileActivity profileActivity) {
        this.arg$1 = profileActivity;
    }

    public void didSetRights(int i, TL_channelAdminRights tL_channelAdminRights, TL_channelBannedRights tL_channelBannedRights) {
        this.arg$1.lambda$null$11$ProfileActivity(i, tL_channelAdminRights, tL_channelBannedRights);
    }
}
