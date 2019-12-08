package org.telegram.messenger;

import org.telegram.tgnet.RequestDelegate;
import org.telegram.tgnet.TLObject;
import org.telegram.tgnet.TLRPC.TL_error;

/* compiled from: lambda */
public final /* synthetic */ class -$$Lambda$MessagesController$8KWRYvhh2CCxsDe_idGloBuFBZQ implements RequestDelegate {
    public static final /* synthetic */ -$$Lambda$MessagesController$8KWRYvhh2CCxsDe_idGloBuFBZQ INSTANCE = new -$$Lambda$MessagesController$8KWRYvhh2CCxsDe_idGloBuFBZQ();

    private /* synthetic */ -$$Lambda$MessagesController$8KWRYvhh2CCxsDe_idGloBuFBZQ() {
    }

    public final void run(TLObject tLObject, TL_error tL_error) {
        MessagesController.lambda$reportSpam$28(tLObject, tL_error);
    }
}
