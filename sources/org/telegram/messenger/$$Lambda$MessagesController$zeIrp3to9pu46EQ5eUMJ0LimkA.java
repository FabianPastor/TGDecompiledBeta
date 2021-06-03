package org.telegram.messenger;

import org.telegram.tgnet.RequestDelegate;
import org.telegram.tgnet.TLObject;
import org.telegram.tgnet.TLRPC$TL_error;

/* renamed from: org.telegram.messenger.-$$Lambda$MessagesController$zeIrp3to9pu46EQ5eUMJ0Li-mkA  reason: invalid class name */
/* compiled from: lambda */
public final /* synthetic */ class $$Lambda$MessagesController$zeIrp3to9pu46EQ5eUMJ0LimkA implements RequestDelegate {
    public static final /* synthetic */ $$Lambda$MessagesController$zeIrp3to9pu46EQ5eUMJ0LimkA INSTANCE = new $$Lambda$MessagesController$zeIrp3to9pu46EQ5eUMJ0LimkA();

    private /* synthetic */ $$Lambda$MessagesController$zeIrp3to9pu46EQ5eUMJ0LimkA() {
    }

    public final void run(TLObject tLObject, TLRPC$TL_error tLRPC$TL_error) {
        MessagesController.lambda$completeReadTask$188(tLObject, tLRPC$TL_error);
    }
}
