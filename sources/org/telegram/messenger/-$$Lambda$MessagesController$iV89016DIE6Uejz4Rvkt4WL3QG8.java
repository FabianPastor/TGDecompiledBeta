package org.telegram.messenger;

import org.telegram.tgnet.RequestDelegate;
import org.telegram.tgnet.TLObject;
import org.telegram.tgnet.TLRPC.TL_error;
import org.telegram.tgnet.TLRPC.TL_messages_saveRecentSticker;

/* compiled from: lambda */
public final /* synthetic */ class -$$Lambda$MessagesController$iV89016DIE6Uejz4Rvkt4WL3QG8 implements RequestDelegate {
    private final /* synthetic */ MessagesController f$0;
    private final /* synthetic */ Object f$1;
    private final /* synthetic */ TL_messages_saveRecentSticker f$2;

    public /* synthetic */ -$$Lambda$MessagesController$iV89016DIE6Uejz4Rvkt4WL3QG8(MessagesController messagesController, Object obj, TL_messages_saveRecentSticker tL_messages_saveRecentSticker) {
        this.f$0 = messagesController;
        this.f$1 = obj;
        this.f$2 = tL_messages_saveRecentSticker;
    }

    public final void run(TLObject tLObject, TL_error tL_error) {
        this.f$0.lambda$saveRecentSticker$87$MessagesController(this.f$1, this.f$2, tLObject, tL_error);
    }
}
