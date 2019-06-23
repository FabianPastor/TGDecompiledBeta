package org.telegram.messenger;

import org.telegram.tgnet.RequestDelegate;
import org.telegram.tgnet.TLObject;
import org.telegram.tgnet.TLRPC.TL_error;
import org.telegram.tgnet.TLRPC.TL_wallPaperSettings;

/* compiled from: lambda */
public final /* synthetic */ class -$$Lambda$MessagesController$WU2WvYrZcQ56eRMk656eXb1ldDw implements RequestDelegate {
    private final /* synthetic */ MessagesController f$0;
    private final /* synthetic */ TL_wallPaperSettings f$1;

    public /* synthetic */ -$$Lambda$MessagesController$WU2WvYrZcQ56eRMk656eXb1ldDw(MessagesController messagesController, TL_wallPaperSettings tL_wallPaperSettings) {
        this.f$0 = messagesController;
        this.f$1 = tL_wallPaperSettings;
    }

    public final void run(TLObject tLObject, TL_error tL_error) {
        this.f$0.lambda$didReceivedNotification$8$MessagesController(this.f$1, tLObject, tL_error);
    }
}
