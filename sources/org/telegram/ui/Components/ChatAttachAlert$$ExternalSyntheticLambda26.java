package org.telegram.ui.Components;

import org.telegram.tgnet.RequestDelegate;
import org.telegram.tgnet.TLObject;
import org.telegram.tgnet.TLRPC$TL_attachMenuBot;
import org.telegram.tgnet.TLRPC$TL_error;

public final /* synthetic */ class ChatAttachAlert$$ExternalSyntheticLambda26 implements RequestDelegate {
    public final /* synthetic */ ChatAttachAlert f$0;
    public final /* synthetic */ TLRPC$TL_attachMenuBot f$1;

    public /* synthetic */ ChatAttachAlert$$ExternalSyntheticLambda26(ChatAttachAlert chatAttachAlert, TLRPC$TL_attachMenuBot tLRPC$TL_attachMenuBot) {
        this.f$0 = chatAttachAlert;
        this.f$1 = tLRPC$TL_attachMenuBot;
    }

    public final void run(TLObject tLObject, TLRPC$TL_error tLRPC$TL_error) {
        this.f$0.lambda$onLongClickBotButton$18(this.f$1, tLObject, tLRPC$TL_error);
    }
}
