package org.telegram.messenger;

import org.telegram.tgnet.RequestDelegate;
import org.telegram.tgnet.TLObject;
import org.telegram.tgnet.TLRPC$TL_error;

/* renamed from: org.telegram.messenger.-$$Lambda$MessagesController$JbHOt2fpB35vZZZ6LSYTl2lTIrg  reason: invalid class name */
/* compiled from: lambda */
public final /* synthetic */ class $$Lambda$MessagesController$JbHOt2fpB35vZZZ6LSYTl2lTIrg implements RequestDelegate {
    public static final /* synthetic */ $$Lambda$MessagesController$JbHOt2fpB35vZZZ6LSYTl2lTIrg INSTANCE = new $$Lambda$MessagesController$JbHOt2fpB35vZZZ6LSYTl2lTIrg();

    private /* synthetic */ $$Lambda$MessagesController$JbHOt2fpB35vZZZ6LSYTl2lTIrg() {
    }

    public final void run(TLObject tLObject, TLRPC$TL_error tLRPC$TL_error) {
        MessagesController.lambda$blockPeer$58(tLObject, tLRPC$TL_error);
    }
}
