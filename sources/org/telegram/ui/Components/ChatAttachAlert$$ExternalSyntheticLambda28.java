package org.telegram.ui.Components;

import android.content.DialogInterface;
import org.telegram.tgnet.TLRPC;

public final /* synthetic */ class ChatAttachAlert$$ExternalSyntheticLambda28 implements DialogInterface.OnClickListener {
    public final /* synthetic */ ChatAttachAlert f$0;
    public final /* synthetic */ TLRPC.TL_attachMenuBot f$1;
    public final /* synthetic */ TLRPC.User f$2;

    public /* synthetic */ ChatAttachAlert$$ExternalSyntheticLambda28(ChatAttachAlert chatAttachAlert, TLRPC.TL_attachMenuBot tL_attachMenuBot, TLRPC.User user) {
        this.f$0 = chatAttachAlert;
        this.f$1 = tL_attachMenuBot;
        this.f$2 = user;
    }

    public final void onClick(DialogInterface dialogInterface, int i) {
        this.f$0.m3782xdf2da15f(this.f$1, this.f$2, dialogInterface, i);
    }
}
