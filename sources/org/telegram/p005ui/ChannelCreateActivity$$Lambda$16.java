package org.telegram.p005ui;

import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import org.telegram.tgnet.TLRPC.Chat;

/* renamed from: org.telegram.ui.ChannelCreateActivity$$Lambda$16 */
final /* synthetic */ class ChannelCreateActivity$$Lambda$16 implements OnClickListener {
    private final ChannelCreateActivity arg$1;
    private final Chat arg$2;

    ChannelCreateActivity$$Lambda$16(ChannelCreateActivity channelCreateActivity, Chat chat) {
        this.arg$1 = channelCreateActivity;
        this.arg$2 = chat;
    }

    public void onClick(DialogInterface dialogInterface, int i) {
        this.arg$1.lambda$null$14$ChannelCreateActivity(this.arg$2, dialogInterface, i);
    }
}
