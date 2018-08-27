package org.telegram.ui;

import java.util.ArrayList;
import org.telegram.tgnet.TLRPC.ChannelParticipant;
import org.telegram.tgnet.TLRPC.TL_channelAdminRights;
import org.telegram.tgnet.TLRPC.TL_channelBannedRights;
import org.telegram.tgnet.TLRPC.TL_chatChannelParticipant;
import org.telegram.ui.ChannelRightsEditActivity.ChannelRightsEditActivityDelegate;

final /* synthetic */ class ChannelEditActivity$$Lambda$6 implements ChannelRightsEditActivityDelegate {
    private final ChannelEditActivity arg$1;
    private final ChannelParticipant arg$2;
    private final ArrayList arg$3;
    private final int arg$4;
    private final TL_chatChannelParticipant arg$5;
    private final int arg$6;

    ChannelEditActivity$$Lambda$6(ChannelEditActivity channelEditActivity, ChannelParticipant channelParticipant, ArrayList arrayList, int i, TL_chatChannelParticipant tL_chatChannelParticipant, int i2) {
        this.arg$1 = channelEditActivity;
        this.arg$2 = channelParticipant;
        this.arg$3 = arrayList;
        this.arg$4 = i;
        this.arg$5 = tL_chatChannelParticipant;
        this.arg$6 = i2;
    }

    public void didSetRights(int i, TL_channelAdminRights tL_channelAdminRights, TL_channelBannedRights tL_channelBannedRights) {
        this.arg$1.lambda$null$5$ChannelEditActivity(this.arg$2, this.arg$3, this.arg$4, this.arg$5, this.arg$6, i, tL_channelAdminRights, tL_channelBannedRights);
    }
}
