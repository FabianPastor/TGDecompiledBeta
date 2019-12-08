package org.telegram.messenger;

import org.telegram.tgnet.RequestDelegate;
import org.telegram.tgnet.TLObject;
import org.telegram.tgnet.TLRPC.Chat;
import org.telegram.tgnet.TLRPC.TL_error;

/* compiled from: lambda */
public final /* synthetic */ class -$$Lambda$MessagesController$VAJW75r5ALAtTw6hMiEJQY4ufUc implements RequestDelegate {
    private final /* synthetic */ MessagesController f$0;
    private final /* synthetic */ Chat f$1;
    private final /* synthetic */ long f$2;
    private final /* synthetic */ int f$3;
    private final /* synthetic */ int f$4;

    public /* synthetic */ -$$Lambda$MessagesController$VAJW75r5ALAtTw6hMiEJQY4ufUc(MessagesController messagesController, Chat chat, long j, int i, int i2) {
        this.f$0 = messagesController;
        this.f$1 = chat;
        this.f$2 = j;
        this.f$3 = i;
        this.f$4 = i2;
    }

    public final void run(TLObject tLObject, TL_error tL_error) {
        this.f$0.lambda$loadFullChat$27$MessagesController(this.f$1, this.f$2, this.f$3, this.f$4, tLObject, tL_error);
    }
}
