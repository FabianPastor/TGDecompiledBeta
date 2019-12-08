package org.telegram.messenger;

import org.telegram.tgnet.RequestDelegate;
import org.telegram.tgnet.TLObject;
import org.telegram.tgnet.TLRPC.TL_error;

/* compiled from: lambda */
public final /* synthetic */ class -$$Lambda$MessagesController$Jgi_tEEk_EEC_N2GFX9eynQPOfo implements RequestDelegate {
    public static final /* synthetic */ -$$Lambda$MessagesController$Jgi_tEEk_EEC_N2GFX9eynQPOfo INSTANCE = new -$$Lambda$MessagesController$Jgi_tEEk_EEC_N2GFX9eynQPOfo();

    private /* synthetic */ -$$Lambda$MessagesController$Jgi_tEEk_EEC_N2GFX9eynQPOfo() {
    }

    public final void run(TLObject tLObject, TL_error tL_error) {
        MessagesController.lambda$markMessageContentAsRead$155(tLObject, tL_error);
    }
}
