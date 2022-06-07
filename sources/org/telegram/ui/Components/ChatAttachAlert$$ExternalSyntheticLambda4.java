package org.telegram.ui.Components;

import android.content.DialogInterface;
import org.telegram.tgnet.TLRPC$TL_attachMenuBot;
import org.telegram.tgnet.TLRPC$User;

public final /* synthetic */ class ChatAttachAlert$$ExternalSyntheticLambda4 implements DialogInterface.OnClickListener {
    public final /* synthetic */ ChatAttachAlert f$0;
    public final /* synthetic */ TLRPC$TL_attachMenuBot f$1;
    public final /* synthetic */ TLRPC$User f$2;

    public /* synthetic */ ChatAttachAlert$$ExternalSyntheticLambda4(ChatAttachAlert chatAttachAlert, TLRPC$TL_attachMenuBot tLRPC$TL_attachMenuBot, TLRPC$User tLRPC$User) {
        this.f$0 = chatAttachAlert;
        this.f$1 = tLRPC$TL_attachMenuBot;
        this.f$2 = tLRPC$User;
    }

    public final void onClick(DialogInterface dialogInterface, int i) {
        this.f$0.lambda$onLongClickBotButton$19(this.f$1, this.f$2, dialogInterface, i);
    }
}
