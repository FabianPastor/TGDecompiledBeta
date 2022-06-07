package org.telegram.ui.Components;

import org.telegram.tgnet.TLRPC$TL_attachMenuBot;

public final /* synthetic */ class ChatAttachAlert$$ExternalSyntheticLambda20 implements Runnable {
    public final /* synthetic */ ChatAttachAlert f$0;
    public final /* synthetic */ TLRPC$TL_attachMenuBot f$1;

    public /* synthetic */ ChatAttachAlert$$ExternalSyntheticLambda20(ChatAttachAlert chatAttachAlert, TLRPC$TL_attachMenuBot tLRPC$TL_attachMenuBot) {
        this.f$0 = chatAttachAlert;
        this.f$1 = tLRPC$TL_attachMenuBot;
    }

    public final void run() {
        this.f$0.lambda$onLongClickBotButton$17(this.f$1);
    }
}
