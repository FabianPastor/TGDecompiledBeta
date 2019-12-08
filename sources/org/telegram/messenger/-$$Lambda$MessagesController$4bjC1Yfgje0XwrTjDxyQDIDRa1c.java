package org.telegram.messenger;

import org.telegram.tgnet.RequestDelegate;
import org.telegram.tgnet.TLObject;
import org.telegram.tgnet.TLRPC.TL_error;
import org.telegram.tgnet.TLRPC.TL_help_proxyDataPromo;

/* compiled from: lambda */
public final /* synthetic */ class -$$Lambda$MessagesController$4bjC1Yfgje0XwrTjDxyQDIDRa1c implements RequestDelegate {
    private final /* synthetic */ MessagesController f$0;
    private final /* synthetic */ int f$1;
    private final /* synthetic */ TL_help_proxyDataPromo f$2;
    private final /* synthetic */ long f$3;

    public /* synthetic */ -$$Lambda$MessagesController$4bjC1Yfgje0XwrTjDxyQDIDRa1c(MessagesController messagesController, int i, TL_help_proxyDataPromo tL_help_proxyDataPromo, long j) {
        this.f$0 = messagesController;
        this.f$1 = i;
        this.f$2 = tL_help_proxyDataPromo;
        this.f$3 = j;
    }

    public final void run(TLObject tLObject, TL_error tL_error) {
        this.f$0.lambda$null$96$MessagesController(this.f$1, this.f$2, this.f$3, tLObject, tL_error);
    }
}
