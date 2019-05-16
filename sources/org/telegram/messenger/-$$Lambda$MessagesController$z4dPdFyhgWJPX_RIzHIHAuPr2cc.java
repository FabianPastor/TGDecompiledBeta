package org.telegram.messenger;

import org.telegram.tgnet.RequestDelegate;
import org.telegram.tgnet.TLObject;
import org.telegram.tgnet.TLRPC.TL_error;

/* compiled from: lambda */
public final /* synthetic */ class -$$Lambda$MessagesController$z4dPdFyhgWJPX_RIzHIHAuPr2cc implements RequestDelegate {
    public static final /* synthetic */ -$$Lambda$MessagesController$z4dPdFyhgWJPX_RIzHIHAuPr2cc INSTANCE = new -$$Lambda$MessagesController$z4dPdFyhgWJPX_RIzHIHAuPr2cc();

    private /* synthetic */ -$$Lambda$MessagesController$z4dPdFyhgWJPX_RIzHIHAuPr2cc() {
    }

    public final void run(TLObject tLObject, TL_error tL_error) {
        MessagesController.lambda$reportSpam$24(tLObject, tL_error);
    }
}
