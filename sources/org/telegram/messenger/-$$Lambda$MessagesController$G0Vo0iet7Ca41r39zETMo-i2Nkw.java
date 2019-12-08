package org.telegram.messenger;

import org.telegram.tgnet.RequestDelegate;
import org.telegram.tgnet.TLObject;
import org.telegram.tgnet.TLRPC.Chat;
import org.telegram.tgnet.TLRPC.TL_error;

/* compiled from: lambda */
public final /* synthetic */ class -$$Lambda$MessagesController$G0Vo0iet7Ca41r39zETMo-i2Nkw implements RequestDelegate {
    private final /* synthetic */ MessagesController f$0;
    private final /* synthetic */ Chat f$1;
    private final /* synthetic */ int f$2;

    public /* synthetic */ -$$Lambda$MessagesController$G0Vo0iet7Ca41r39zETMo-i2Nkw(MessagesController messagesController, Chat chat, int i) {
        this.f$0 = messagesController;
        this.f$1 = chat;
        this.f$2 = i;
    }

    public final void run(TLObject tLObject, TL_error tL_error) {
        this.f$0.lambda$null$236$MessagesController(this.f$1, this.f$2, tLObject, tL_error);
    }
}
