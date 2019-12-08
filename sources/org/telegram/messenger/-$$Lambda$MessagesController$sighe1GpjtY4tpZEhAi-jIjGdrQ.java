package org.telegram.messenger;

import org.telegram.tgnet.RequestDelegate;
import org.telegram.tgnet.TLObject;
import org.telegram.tgnet.TLRPC.ChatFull;
import org.telegram.tgnet.TLRPC.TL_error;

/* compiled from: lambda */
public final /* synthetic */ class -$$Lambda$MessagesController$sighe1GpjtY4tpZEhAi-jIjGdrQ implements RequestDelegate {
    private final /* synthetic */ MessagesController f$0;
    private final /* synthetic */ ChatFull f$1;
    private final /* synthetic */ String f$2;

    public /* synthetic */ -$$Lambda$MessagesController$sighe1GpjtY4tpZEhAi-jIjGdrQ(MessagesController messagesController, ChatFull chatFull, String str) {
        this.f$0 = messagesController;
        this.f$1 = chatFull;
        this.f$2 = str;
    }

    public final void run(TLObject tLObject, TL_error tL_error) {
        this.f$0.lambda$updateChatAbout$188$MessagesController(this.f$1, this.f$2, tLObject, tL_error);
    }
}
