package org.telegram.messenger;

import org.telegram.tgnet.RequestDelegate;
import org.telegram.tgnet.TLObject;
import org.telegram.tgnet.TLRPC.TL_error;

/* compiled from: lambda */
public final /* synthetic */ class -$$Lambda$MessagesController$psvnshpc_2L2GLJpT_Dmh0R8udI implements RequestDelegate {
    private final /* synthetic */ MessagesController f$0;
    private final /* synthetic */ int f$1;
    private final /* synthetic */ String f$2;
    private final /* synthetic */ String f$3;

    public /* synthetic */ -$$Lambda$MessagesController$psvnshpc_2L2GLJpT_Dmh0R8udI(MessagesController messagesController, int i, String str, String str2) {
        this.f$0 = messagesController;
        this.f$1 = i;
        this.f$2 = str;
        this.f$3 = str2;
    }

    public final void run(TLObject tLObject, TL_error tL_error) {
        this.f$0.lambda$checkProxyInfoInternal$98$MessagesController(this.f$1, this.f$2, this.f$3, tLObject, tL_error);
    }
}