package org.telegram.messenger;

import org.telegram.tgnet.RequestDelegate;
import org.telegram.tgnet.TLObject;
import org.telegram.tgnet.TLRPC.TL_error;

/* compiled from: lambda */
public final /* synthetic */ class -$$Lambda$MessagesController$-WVTY6_RdUIMPb49C4mJ2VjWV4g implements RequestDelegate {
    public static final /* synthetic */ -$$Lambda$MessagesController$-WVTY6_RdUIMPb49C4mJ2VjWV4g INSTANCE = new -$$Lambda$MessagesController$-WVTY6_RdUIMPb49C4mJ2VjWV4g();

    private /* synthetic */ -$$Lambda$MessagesController$-WVTY6_RdUIMPb49C4mJ2VjWV4g() {
    }

    public final void run(TLObject tLObject, TL_error tL_error) {
        MessagesController.lambda$markMentionsAsRead$164(tLObject, tL_error);
    }
}
