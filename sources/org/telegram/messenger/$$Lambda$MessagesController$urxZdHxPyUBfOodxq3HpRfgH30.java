package org.telegram.messenger;

import org.telegram.tgnet.RequestDelegate;
import org.telegram.tgnet.TLObject;
import org.telegram.tgnet.TLRPC$TL_error;

/* renamed from: org.telegram.messenger.-$$Lambda$MessagesController$urxZdHxPyUBfOodxq3-HpRfgH30  reason: invalid class name */
/* compiled from: lambda */
public final /* synthetic */ class $$Lambda$MessagesController$urxZdHxPyUBfOodxq3HpRfgH30 implements RequestDelegate {
    public static final /* synthetic */ $$Lambda$MessagesController$urxZdHxPyUBfOodxq3HpRfgH30 INSTANCE = new $$Lambda$MessagesController$urxZdHxPyUBfOodxq3HpRfgH30();

    private /* synthetic */ $$Lambda$MessagesController$urxZdHxPyUBfOodxq3HpRfgH30() {
    }

    public final void run(TLObject tLObject, TLRPC$TL_error tLRPC$TL_error) {
        MessagesController.lambda$reportSpam$47(tLObject, tLRPC$TL_error);
    }
}
