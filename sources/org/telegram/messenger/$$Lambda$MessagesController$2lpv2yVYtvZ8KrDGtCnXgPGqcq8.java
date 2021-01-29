package org.telegram.messenger;

import org.telegram.tgnet.RequestDelegate;
import org.telegram.tgnet.TLObject;
import org.telegram.tgnet.TLRPC$TL_error;

/* renamed from: org.telegram.messenger.-$$Lambda$MessagesController$2lpv2yVYtvZ8KrDGtCnXgPGqcq8  reason: invalid class name */
/* compiled from: lambda */
public final /* synthetic */ class $$Lambda$MessagesController$2lpv2yVYtvZ8KrDGtCnXgPGqcq8 implements RequestDelegate {
    public static final /* synthetic */ $$Lambda$MessagesController$2lpv2yVYtvZ8KrDGtCnXgPGqcq8 INSTANCE = new $$Lambda$MessagesController$2lpv2yVYtvZ8KrDGtCnXgPGqcq8();

    private /* synthetic */ $$Lambda$MessagesController$2lpv2yVYtvZ8KrDGtCnXgPGqcq8() {
    }

    public final void run(TLObject tLObject, TLRPC$TL_error tLRPC$TL_error) {
        MessagesController.lambda$blockPeer$61(tLObject, tLRPC$TL_error);
    }
}
