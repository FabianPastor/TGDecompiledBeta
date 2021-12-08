package org.telegram.ui;

import android.content.DialogInterface;
import org.telegram.tgnet.TLRPC;

public final /* synthetic */ class ChatEditTypeActivity$$ExternalSyntheticLambda0 implements DialogInterface.OnClickListener {
    public final /* synthetic */ ChatEditTypeActivity f$0;
    public final /* synthetic */ TLRPC.Chat f$1;

    public /* synthetic */ ChatEditTypeActivity$$ExternalSyntheticLambda0(ChatEditTypeActivity chatEditTypeActivity, TLRPC.Chat chat) {
        this.f$0 = chatEditTypeActivity;
        this.f$1 = chat;
    }

    public final void onClick(DialogInterface dialogInterface, int i) {
        this.f$0.m1908x6855e815(this.f$1, dialogInterface, i);
    }
}
