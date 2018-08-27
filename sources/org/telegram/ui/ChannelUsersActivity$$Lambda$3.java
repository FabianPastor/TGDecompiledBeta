package org.telegram.ui;

import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import org.telegram.tgnet.TLRPC.ChannelParticipant;

final /* synthetic */ class ChannelUsersActivity$$Lambda$3 implements OnClickListener {
    private final ChannelUsersActivity arg$1;
    private final ChannelParticipant arg$2;

    ChannelUsersActivity$$Lambda$3(ChannelUsersActivity channelUsersActivity, ChannelParticipant channelParticipant) {
        this.arg$1 = channelUsersActivity;
        this.arg$2 = channelParticipant;
    }

    public void onClick(DialogInterface dialogInterface, int i) {
        this.arg$1.lambda$createMenuForParticipant$9$ChannelUsersActivity(this.arg$2, dialogInterface, i);
    }
}
