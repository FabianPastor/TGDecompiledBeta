package org.telegram.messenger;

import org.telegram.tgnet.RequestDelegate;
import org.telegram.tgnet.TLObject;
import org.telegram.tgnet.TLRPC.FileLocation;
import org.telegram.tgnet.TLRPC.TL_error;

/* compiled from: lambda */
public final /* synthetic */ class -$$Lambda$MessagesController$wy09bZPmqKXvxXv6-hJzlanOWGw implements RequestDelegate {
    private final /* synthetic */ MessagesController f$0;
    private final /* synthetic */ FileLocation f$1;
    private final /* synthetic */ FileLocation f$2;

    public /* synthetic */ -$$Lambda$MessagesController$wy09bZPmqKXvxXv6-hJzlanOWGw(MessagesController messagesController, FileLocation fileLocation, FileLocation fileLocation2) {
        this.f$0 = messagesController;
        this.f$1 = fileLocation;
        this.f$2 = fileLocation2;
    }

    public final void run(TLObject tLObject, TL_error tL_error) {
        this.f$0.lambda$changeChatAvatar$200$MessagesController(this.f$1, this.f$2, tLObject, tL_error);
    }
}