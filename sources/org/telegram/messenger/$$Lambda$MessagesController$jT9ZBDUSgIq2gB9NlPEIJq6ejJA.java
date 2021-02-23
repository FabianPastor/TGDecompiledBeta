package org.telegram.messenger;

import org.telegram.tgnet.RequestDelegate;
import org.telegram.tgnet.TLObject;
import org.telegram.tgnet.TLRPC$TL_error;

/* renamed from: org.telegram.messenger.-$$Lambda$MessagesController$jT9ZBDUSgIq2gB9NlPEIJq6ejJA  reason: invalid class name */
/* compiled from: lambda */
public final /* synthetic */ class $$Lambda$MessagesController$jT9ZBDUSgIq2gB9NlPEIJq6ejJA implements RequestDelegate {
    public static final /* synthetic */ $$Lambda$MessagesController$jT9ZBDUSgIq2gB9NlPEIJq6ejJA INSTANCE = new $$Lambda$MessagesController$jT9ZBDUSgIq2gB9NlPEIJq6ejJA();

    private /* synthetic */ $$Lambda$MessagesController$jT9ZBDUSgIq2gB9NlPEIJq6ejJA() {
    }

    public final void run(TLObject tLObject, TLRPC$TL_error tLRPC$TL_error) {
        MessagesController.lambda$completeReadTask$185(tLObject, tLRPC$TL_error);
    }
}
