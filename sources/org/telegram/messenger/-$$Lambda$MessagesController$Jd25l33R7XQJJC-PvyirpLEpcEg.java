package org.telegram.messenger;

import org.telegram.tgnet.RequestDelegate;
import org.telegram.tgnet.TLObject;
import org.telegram.tgnet.TLRPC.TL_error;

/* compiled from: lambda */
public final /* synthetic */ class -$$Lambda$MessagesController$Jd25l33R7XQJJC-PvyirpLEpcEg implements RequestDelegate {
    public static final /* synthetic */ -$$Lambda$MessagesController$Jd25l33R7XQJJC-PvyirpLEpcEg INSTANCE = new -$$Lambda$MessagesController$Jd25l33R7XQJJC-PvyirpLEpcEg();

    private /* synthetic */ -$$Lambda$MessagesController$Jd25l33R7XQJJC-PvyirpLEpcEg() {
    }

    public final void run(TLObject tLObject, TL_error tL_error) {
        MessagesController.lambda$completeReadTask$149(tLObject, tL_error);
    }
}
