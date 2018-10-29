package org.telegram.ui;

import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import java.util.ArrayList;
import org.telegram.tgnet.TLRPC.ChannelParticipant;
import org.telegram.tgnet.TLRPC.User;

final /* synthetic */ class ChannelUsersActivity$$Lambda$2 implements OnClickListener {
    private final ChannelUsersActivity arg$1;
    private final ArrayList arg$2;
    private final User arg$3;
    private final ChannelParticipant arg$4;

    ChannelUsersActivity$$Lambda$2(ChannelUsersActivity channelUsersActivity, ArrayList arrayList, User user, ChannelParticipant channelParticipant) {
        this.arg$1 = channelUsersActivity;
        this.arg$2 = arrayList;
        this.arg$3 = user;
        this.arg$4 = channelParticipant;
    }

    public void onClick(DialogInterface dialogInterface, int i) {
        this.arg$1.lambda$createMenuForParticipant$6$ChannelUsersActivity(this.arg$2, this.arg$3, this.arg$4, dialogInterface, i);
    }
}
