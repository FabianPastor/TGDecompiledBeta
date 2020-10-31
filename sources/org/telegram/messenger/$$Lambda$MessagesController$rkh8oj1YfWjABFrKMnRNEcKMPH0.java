package org.telegram.messenger;

import org.telegram.tgnet.RequestDelegate;
import org.telegram.tgnet.TLObject;
import org.telegram.tgnet.TLRPC$TL_error;

/* renamed from: org.telegram.messenger.-$$Lambda$MessagesController$rkh8oj1YfWjABFrKMnRNEcKMPH0  reason: invalid class name */
/* compiled from: lambda */
public final /* synthetic */ class $$Lambda$MessagesController$rkh8oj1YfWjABFrKMnRNEcKMPH0 implements RequestDelegate {
    public static final /* synthetic */ $$Lambda$MessagesController$rkh8oj1YfWjABFrKMnRNEcKMPH0 INSTANCE = new $$Lambda$MessagesController$rkh8oj1YfWjABFrKMnRNEcKMPH0();

    private /* synthetic */ $$Lambda$MessagesController$rkh8oj1YfWjABFrKMnRNEcKMPH0() {
    }

    public final void run(TLObject tLObject, TLRPC$TL_error tLRPC$TL_error) {
        MessagesController.lambda$completeReadTask$179(tLObject, tLRPC$TL_error);
    }
}
