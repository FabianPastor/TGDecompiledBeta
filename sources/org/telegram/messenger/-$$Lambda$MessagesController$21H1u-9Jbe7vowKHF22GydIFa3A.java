package org.telegram.messenger;

import org.telegram.tgnet.RequestDelegate;
import org.telegram.tgnet.TLObject;
import org.telegram.tgnet.TLRPC.TL_error;

/* compiled from: lambda */
public final /* synthetic */ class -$$Lambda$MessagesController$21H1u-9Jbe7vowKHvar_GydIFa3A implements RequestDelegate {
    private final /* synthetic */ MessagesController f$0;
    private final /* synthetic */ int f$1;
    private final /* synthetic */ int f$2;
    private final /* synthetic */ int f$3;
    private final /* synthetic */ int f$4;

    public /* synthetic */ -$$Lambda$MessagesController$21H1u-9Jbe7vowKHvar_GydIFa3A(MessagesController messagesController, int i, int i2, int i3, int i4) {
        this.f$0 = messagesController;
        this.f$1 = i;
        this.f$2 = i2;
        this.f$3 = i3;
        this.f$4 = i4;
    }

    public final void run(TLObject tLObject, TL_error tL_error) {
        this.f$0.lambda$resetDialogs$135$MessagesController(this.f$1, this.f$2, this.f$3, this.f$4, tLObject, tL_error);
    }
}