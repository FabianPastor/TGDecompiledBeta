package org.telegram.ui;

import android.content.DialogInterface;
import org.telegram.tgnet.TLRPC$Chat;

public final /* synthetic */ class ChannelCreateActivity$$ExternalSyntheticLambda0 implements DialogInterface.OnClickListener {
    public final /* synthetic */ ChannelCreateActivity f$0;
    public final /* synthetic */ TLRPC$Chat f$1;

    public /* synthetic */ ChannelCreateActivity$$ExternalSyntheticLambda0(ChannelCreateActivity channelCreateActivity, TLRPC$Chat tLRPC$Chat) {
        this.f$0 = channelCreateActivity;
        this.f$1 = tLRPC$Chat;
    }

    public final void onClick(DialogInterface dialogInterface, int i) {
        this.f$0.lambda$loadAdminedChannels$15(this.f$1, dialogInterface, i);
    }
}
