package org.telegram.ui;

import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import org.telegram.tgnet.TLRPC.Chat;

final /* synthetic */ class ChannelEditInfoActivity$$Lambda$22 implements OnClickListener {
    private final ChannelEditInfoActivity arg$1;
    private final Chat arg$2;

    ChannelEditInfoActivity$$Lambda$22(ChannelEditInfoActivity channelEditInfoActivity, Chat chat) {
        this.arg$1 = channelEditInfoActivity;
        this.arg$2 = chat;
    }

    public void onClick(DialogInterface dialogInterface, int i) {
        this.arg$1.lambda$null$18$ChannelEditInfoActivity(this.arg$2, dialogInterface, i);
    }
}
