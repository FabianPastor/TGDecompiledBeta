package org.telegram.messenger;

import org.telegram.tgnet.RequestDelegate;
import org.telegram.tgnet.TLObject;
import org.telegram.tgnet.TLRPC.TL_error;
import org.telegram.tgnet.TLRPC.TL_messages_saveGif;

/* compiled from: lambda */
public final /* synthetic */ class -$$Lambda$MessagesController$LLUnps7pG6ud439wW4t4mp0YY9o implements RequestDelegate {
    private final /* synthetic */ MessagesController f$0;
    private final /* synthetic */ Object f$1;
    private final /* synthetic */ TL_messages_saveGif f$2;

    public /* synthetic */ -$$Lambda$MessagesController$LLUnps7pG6ud439wW4t4mp0YY9o(MessagesController messagesController, Object obj, TL_messages_saveGif tL_messages_saveGif) {
        this.f$0 = messagesController;
        this.f$1 = obj;
        this.f$2 = tL_messages_saveGif;
    }

    public final void run(TLObject tLObject, TL_error tL_error) {
        this.f$0.lambda$saveGif$75$MessagesController(this.f$1, this.f$2, tLObject, tL_error);
    }
}
