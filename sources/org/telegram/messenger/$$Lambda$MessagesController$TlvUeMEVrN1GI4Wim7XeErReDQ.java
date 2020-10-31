package org.telegram.messenger;

import org.telegram.tgnet.RequestDelegate;
import org.telegram.tgnet.TLObject;
import org.telegram.tgnet.TLRPC$TL_error;

/* renamed from: org.telegram.messenger.-$$Lambda$MessagesController$TlvUeMEVrN1G-I4Wim7XeErReDQ  reason: invalid class name */
/* compiled from: lambda */
public final /* synthetic */ class $$Lambda$MessagesController$TlvUeMEVrN1GI4Wim7XeErReDQ implements RequestDelegate {
    public static final /* synthetic */ $$Lambda$MessagesController$TlvUeMEVrN1GI4Wim7XeErReDQ INSTANCE = new $$Lambda$MessagesController$TlvUeMEVrN1GI4Wim7XeErReDQ();

    private /* synthetic */ $$Lambda$MessagesController$TlvUeMEVrN1GI4Wim7XeErReDQ() {
    }

    public final void run(TLObject tLObject, TLRPC$TL_error tLRPC$TL_error) {
        MessagesController.lambda$markMentionsAsRead$183(tLObject, tLRPC$TL_error);
    }
}
