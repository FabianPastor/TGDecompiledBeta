package org.telegram.messenger;

import java.util.ArrayList;
import org.telegram.tgnet.RequestDelegate;
import org.telegram.tgnet.TLObject;
import org.telegram.tgnet.TLRPC.Chat;
import org.telegram.tgnet.TLRPC.TL_error;

/* compiled from: lambda */
public final /* synthetic */ class -$$Lambda$MessagesController$Liv50krJ6cv1Bd4MsaAD3ZIs3gY implements RequestDelegate {
    private final /* synthetic */ MessagesController f$0;
    private final /* synthetic */ long f$1;
    private final /* synthetic */ Chat f$2;
    private final /* synthetic */ boolean f$3;
    private final /* synthetic */ ArrayList f$4;

    public /* synthetic */ -$$Lambda$MessagesController$Liv50krJ6cv1Bd4MsaAD3ZIs3gY(MessagesController messagesController, long j, Chat chat, boolean z, ArrayList arrayList) {
        this.f$0 = messagesController;
        this.f$1 = j;
        this.f$2 = chat;
        this.f$3 = z;
        this.f$4 = arrayList;
    }

    public final void run(TLObject tLObject, TL_error tL_error) {
        this.f$0.lambda$reloadMessages$32$MessagesController(this.f$1, this.f$2, this.f$3, this.f$4, tLObject, tL_error);
    }
}
