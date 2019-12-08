package org.telegram.messenger;

import org.telegram.tgnet.RequestDelegate;
import org.telegram.tgnet.TLObject;
import org.telegram.tgnet.TLRPC.Chat;
import org.telegram.tgnet.TLRPC.TL_error;

/* compiled from: lambda */
public final /* synthetic */ class -$$Lambda$MessagesController$y1t72jPbG3yOG4Wz_D5YBlSVRu0 implements RequestDelegate {
    private final /* synthetic */ MessagesController f$0;
    private final /* synthetic */ long f$1;
    private final /* synthetic */ Chat f$2;

    public /* synthetic */ -$$Lambda$MessagesController$y1t72jPbG3yOG4Wz_D5YBlSVRu0(MessagesController messagesController, long j, Chat chat) {
        this.f$0 = messagesController;
        this.f$1 = j;
        this.f$2 = chat;
    }

    public final void run(TLObject tLObject, TL_error tL_error) {
        this.f$0.lambda$loadUnknownChannel$210$MessagesController(this.f$1, this.f$2, tLObject, tL_error);
    }
}
