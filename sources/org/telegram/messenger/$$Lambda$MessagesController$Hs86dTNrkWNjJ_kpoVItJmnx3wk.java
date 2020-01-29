package org.telegram.messenger;

import org.telegram.tgnet.RequestDelegate;
import org.telegram.tgnet.TLObject;
import org.telegram.tgnet.TLRPC;

/* renamed from: org.telegram.messenger.-$$Lambda$MessagesController$Hs86dTNrkWNjJ_kpoVItJmnx3wk  reason: invalid class name */
/* compiled from: lambda */
public final /* synthetic */ class $$Lambda$MessagesController$Hs86dTNrkWNjJ_kpoVItJmnx3wk implements RequestDelegate {
    public static final /* synthetic */ $$Lambda$MessagesController$Hs86dTNrkWNjJ_kpoVItJmnx3wk INSTANCE = new $$Lambda$MessagesController$Hs86dTNrkWNjJ_kpoVItJmnx3wk();

    private /* synthetic */ $$Lambda$MessagesController$Hs86dTNrkWNjJ_kpoVItJmnx3wk() {
    }

    public final void run(TLObject tLObject, TLRPC.TL_error tL_error) {
        MessagesController.lambda$reportSpam$34(tLObject, tL_error);
    }
}
