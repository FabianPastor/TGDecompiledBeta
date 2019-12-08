package org.telegram.messenger;

import org.telegram.tgnet.RequestDelegate;
import org.telegram.tgnet.TLObject;
import org.telegram.tgnet.TLRPC.Chat;
import org.telegram.tgnet.TLRPC.TL_error;

/* compiled from: lambda */
public final /* synthetic */ class -$$Lambda$MessagesController$w5ObUHWa_1lAGv3jG-dJAsJESDY implements RequestDelegate {
    private final /* synthetic */ MessagesController f$0;
    private final /* synthetic */ Chat f$1;
    private final /* synthetic */ int f$2;

    public /* synthetic */ -$$Lambda$MessagesController$w5ObUHWa_1lAGv3jG-dJAsJESDY(MessagesController messagesController, Chat chat, int i) {
        this.f$0 = messagesController;
        this.f$1 = chat;
        this.f$2 = i;
    }

    public final void run(TLObject tLObject, TL_error tL_error) {
        this.f$0.lambda$null$248$MessagesController(this.f$1, this.f$2, tLObject, tL_error);
    }
}
