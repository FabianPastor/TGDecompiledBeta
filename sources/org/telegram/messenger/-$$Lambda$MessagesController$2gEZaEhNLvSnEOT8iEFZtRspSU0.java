package org.telegram.messenger;

import org.telegram.tgnet.RequestDelegate;
import org.telegram.tgnet.TLObject;
import org.telegram.tgnet.TLRPC.TL_error;

/* compiled from: lambda */
public final /* synthetic */ class -$$Lambda$MessagesController$2gEZaEhNLvSnEOT8iEFZtRspSU0 implements RequestDelegate {
    private final /* synthetic */ MessagesController f$0;
    private final /* synthetic */ Integer f$1;

    public /* synthetic */ -$$Lambda$MessagesController$2gEZaEhNLvSnEOT8iEFZtRspSU0(MessagesController messagesController, Integer num) {
        this.f$0 = messagesController;
        this.f$1 = num;
    }

    public final void run(TLObject tLObject, TL_error tL_error) {
        this.f$0.lambda$loadChannelParticipants$74$MessagesController(this.f$1, tLObject, tL_error);
    }
}
