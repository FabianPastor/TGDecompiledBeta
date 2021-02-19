package org.telegram.messenger;

import org.telegram.tgnet.RequestDelegate;
import org.telegram.tgnet.TLObject;
import org.telegram.tgnet.TLRPC$TL_error;

/* renamed from: org.telegram.messenger.-$$Lambda$MessagesController$UU07lw4uVffzSuktnWMKaObXMl0  reason: invalid class name */
/* compiled from: lambda */
public final /* synthetic */ class $$Lambda$MessagesController$UU07lw4uVffzSuktnWMKaObXMl0 implements RequestDelegate {
    public static final /* synthetic */ $$Lambda$MessagesController$UU07lw4uVffzSuktnWMKaObXMl0 INSTANCE = new $$Lambda$MessagesController$UU07lw4uVffzSuktnWMKaObXMl0();

    private /* synthetic */ $$Lambda$MessagesController$UU07lw4uVffzSuktnWMKaObXMl0() {
    }

    public final void run(TLObject tLObject, TLRPC$TL_error tLRPC$TL_error) {
        MessagesController.lambda$processUpdates$284(tLObject, tLRPC$TL_error);
    }
}
