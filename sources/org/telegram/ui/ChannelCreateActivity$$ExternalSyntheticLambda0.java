package org.telegram.ui;

import android.content.DialogInterface;
import org.telegram.tgnet.TLRPC;

public final /* synthetic */ class ChannelCreateActivity$$ExternalSyntheticLambda0 implements DialogInterface.OnClickListener {
    public final /* synthetic */ ChannelCreateActivity f$0;
    public final /* synthetic */ TLRPC.Chat f$1;

    public /* synthetic */ ChannelCreateActivity$$ExternalSyntheticLambda0(ChannelCreateActivity channelCreateActivity, TLRPC.Chat chat) {
        this.f$0 = channelCreateActivity;
        this.f$1 = chat;
    }

    public final void onClick(DialogInterface dialogInterface, int i) {
        this.f$0.m1614xcfea1421(this.f$1, dialogInterface, i);
    }
}
