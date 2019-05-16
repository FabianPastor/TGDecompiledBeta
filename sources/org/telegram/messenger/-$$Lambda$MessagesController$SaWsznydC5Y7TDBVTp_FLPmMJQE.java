package org.telegram.messenger;

import org.telegram.tgnet.RequestDelegate;
import org.telegram.tgnet.TLObject;
import org.telegram.tgnet.TLRPC.TL_error;

/* compiled from: lambda */
public final /* synthetic */ class -$$Lambda$MessagesController$SaWsznydC5Y7TDBVTp_FLPmMJQE implements RequestDelegate {
    private final /* synthetic */ MessagesController f$0;
    private final /* synthetic */ String f$1;
    private final /* synthetic */ String f$2;

    public /* synthetic */ -$$Lambda$MessagesController$SaWsznydC5Y7TDBVTp_FLPmMJQE(MessagesController messagesController, String str, String str2) {
        this.f$0 = messagesController;
        this.f$1 = str;
        this.f$2 = str2;
    }

    public final void run(TLObject tLObject, TL_error tL_error) {
        this.f$0.lambda$checkProxyInfoInternal$95$MessagesController(this.f$1, this.f$2, tLObject, tL_error);
    }
}
