package org.telegram.p005ui;

import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import org.telegram.tgnet.TLRPC.Chat;

/* renamed from: org.telegram.ui.ChannelEditInfoActivity$$Lambda$14 */
final /* synthetic */ class ChannelEditInfoActivity$$Lambda$14 implements OnClickListener {
    private final ChannelEditInfoActivity arg$1;
    private final Chat arg$2;

    ChannelEditInfoActivity$$Lambda$14(ChannelEditInfoActivity channelEditInfoActivity, Chat chat) {
        this.arg$1 = channelEditInfoActivity;
        this.arg$2 = chat;
    }

    public void onClick(DialogInterface dialogInterface, int i) {
        this.arg$1.lambda$null$8$ChannelEditInfoActivity(this.arg$2, dialogInterface, i);
    }
}
