package org.telegram.messenger;

import org.telegram.tgnet.RequestDelegate;
import org.telegram.tgnet.TLObject;
import org.telegram.tgnet.TLRPC.TL_error;

/* compiled from: lambda */
public final /* synthetic */ class -$$Lambda$MessagesController$WPG08GhvcQT48v_iCJsl-1gQRNw implements RequestDelegate {
    private final /* synthetic */ MessagesController f$0;
    private final /* synthetic */ Integer f$1;

    public /* synthetic */ -$$Lambda$MessagesController$WPG08GhvcQT48v_iCJsl-1gQRNw(MessagesController messagesController, Integer num) {
        this.f$0 = messagesController;
        this.f$1 = num;
    }

    public final void run(TLObject tLObject, TL_error tL_error) {
        this.f$0.lambda$loadChannelParticipants$89$MessagesController(this.f$1, tLObject, tL_error);
    }
}
