package org.telegram.p005ui;

import java.util.ArrayList;
import org.telegram.p005ui.ChannelRightsEditActivity.ChannelRightsEditActivityDelegate;
import org.telegram.tgnet.TLRPC.ChatParticipant;
import org.telegram.tgnet.TLRPC.TL_channelAdminRights;
import org.telegram.tgnet.TLRPC.TL_channelBannedRights;

/* renamed from: org.telegram.ui.ProfileActivity$$Lambda$20 */
final /* synthetic */ class ProfileActivity$$Lambda$20 implements ChannelRightsEditActivityDelegate {
    private final ProfileActivity arg$1;
    private final ArrayList arg$2;
    private final int arg$3;
    private final ChatParticipant arg$4;

    ProfileActivity$$Lambda$20(ProfileActivity profileActivity, ArrayList arrayList, int i, ChatParticipant chatParticipant) {
        this.arg$1 = profileActivity;
        this.arg$2 = arrayList;
        this.arg$3 = i;
        this.arg$4 = chatParticipant;
    }

    public void didSetRights(int i, TL_channelAdminRights tL_channelAdminRights, TL_channelBannedRights tL_channelBannedRights) {
        this.arg$1.lambda$null$5$ProfileActivity(this.arg$2, this.arg$3, this.arg$4, i, tL_channelAdminRights, tL_channelBannedRights);
    }
}
