package org.telegram.messenger;

import org.telegram.tgnet.RequestDelegate;
import org.telegram.tgnet.TLObject;
import org.telegram.tgnet.TLRPC.TL_error;

/* compiled from: lambda */
public final /* synthetic */ class -$$Lambda$MessagesController$U1W3nDL1EiGSD0rreMna_OmrdrA implements RequestDelegate {
    public static final /* synthetic */ -$$Lambda$MessagesController$U1W3nDL1EiGSD0rreMna_OmrdrA INSTANCE = new -$$Lambda$MessagesController$U1W3nDL1EiGSD0rreMna_OmrdrA();

    private /* synthetic */ -$$Lambda$MessagesController$U1W3nDL1EiGSD0rreMna_OmrdrA() {
    }

    public final void run(TLObject tLObject, TL_error tL_error) {
        MessagesController.lambda$markMentionMessageAsRead$159(tLObject, tL_error);
    }
}
