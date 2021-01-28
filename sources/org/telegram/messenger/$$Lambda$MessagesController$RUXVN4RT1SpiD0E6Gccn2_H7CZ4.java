package org.telegram.messenger;

import org.telegram.tgnet.RequestDelegate;
import org.telegram.tgnet.TLObject;
import org.telegram.tgnet.TLRPC$TL_error;

/* renamed from: org.telegram.messenger.-$$Lambda$MessagesController$RUXVN4RT1SpiD0E6Gccn2_H7CZ4  reason: invalid class name */
/* compiled from: lambda */
public final /* synthetic */ class $$Lambda$MessagesController$RUXVN4RT1SpiD0E6Gccn2_H7CZ4 implements RequestDelegate {
    public static final /* synthetic */ $$Lambda$MessagesController$RUXVN4RT1SpiD0E6Gccn2_H7CZ4 INSTANCE = new $$Lambda$MessagesController$RUXVN4RT1SpiD0E6Gccn2_H7CZ4();

    private /* synthetic */ $$Lambda$MessagesController$RUXVN4RT1SpiD0E6Gccn2_H7CZ4() {
    }

    public final void run(TLObject tLObject, TLRPC$TL_error tLRPC$TL_error) {
        MessagesController.lambda$processUpdates$278(tLObject, tLRPC$TL_error);
    }
}
