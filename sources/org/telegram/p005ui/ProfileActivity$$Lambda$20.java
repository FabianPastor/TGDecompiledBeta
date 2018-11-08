package org.telegram.p005ui;

import org.telegram.p005ui.ChannelRightsEditActivity.ChannelRightsEditActivityDelegate;
import org.telegram.tgnet.TLRPC.TL_channelAdminRights;
import org.telegram.tgnet.TLRPC.TL_channelBannedRights;

/* renamed from: org.telegram.ui.ProfileActivity$$Lambda$20 */
final /* synthetic */ class ProfileActivity$$Lambda$20 implements ChannelRightsEditActivityDelegate {
    private final ProfileActivity arg$1;

    ProfileActivity$$Lambda$20(ProfileActivity profileActivity) {
        this.arg$1 = profileActivity;
    }

    public void didSetRights(int i, TL_channelAdminRights tL_channelAdminRights, TL_channelBannedRights tL_channelBannedRights) {
        this.arg$1.lambda$null$11$ProfileActivity(i, tL_channelAdminRights, tL_channelBannedRights);
    }
}
