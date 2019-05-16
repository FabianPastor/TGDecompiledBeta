package org.telegram.messenger;

import org.telegram.tgnet.RequestDelegate;
import org.telegram.tgnet.TLObject;
import org.telegram.tgnet.TLRPC.TL_error;
import org.telegram.tgnet.TLRPC.TL_help_proxyDataPromo;

/* compiled from: lambda */
public final /* synthetic */ class -$$Lambda$MessagesController$jGzHg4Vpn20r2Dvt16XQexkQnM8 implements RequestDelegate {
    private final /* synthetic */ MessagesController f$0;
    private final /* synthetic */ TL_help_proxyDataPromo f$1;
    private final /* synthetic */ long f$2;

    public /* synthetic */ -$$Lambda$MessagesController$jGzHg4Vpn20r2Dvt16XQexkQnM8(MessagesController messagesController, TL_help_proxyDataPromo tL_help_proxyDataPromo, long j) {
        this.f$0 = messagesController;
        this.f$1 = tL_help_proxyDataPromo;
        this.f$2 = j;
    }

    public final void run(TLObject tLObject, TL_error tL_error) {
        this.f$0.lambda$null$92$MessagesController(this.f$1, this.f$2, tLObject, tL_error);
    }
}
