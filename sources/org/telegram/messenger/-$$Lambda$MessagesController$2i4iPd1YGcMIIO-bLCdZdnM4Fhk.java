package org.telegram.messenger;

import org.telegram.tgnet.RequestDelegate;
import org.telegram.tgnet.TLObject;
import org.telegram.tgnet.TLRPC.TL_error;
import org.telegram.tgnet.TLRPC.User;

/* compiled from: lambda */
public final /* synthetic */ class -$$Lambda$MessagesController$2i4iPd1YGcMIIO-bLCdZdnM4Fhk implements RequestDelegate {
    private final /* synthetic */ MessagesController f$0;
    private final /* synthetic */ User f$1;
    private final /* synthetic */ int f$2;

    public /* synthetic */ -$$Lambda$MessagesController$2i4iPd1YGcMIIO-bLCdZdnM4Fhk(MessagesController messagesController, User user, int i) {
        this.f$0 = messagesController;
        this.f$1 = user;
        this.f$2 = i;
    }

    public final void run(TLObject tLObject, TL_error tL_error) {
        this.f$0.lambda$loadFullUser$30$MessagesController(this.f$1, this.f$2, tLObject, tL_error);
    }
}