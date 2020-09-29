package org.telegram.messenger;

import org.telegram.tgnet.RequestDelegate;
import org.telegram.tgnet.TLObject;
import org.telegram.tgnet.TLRPC$TL_error;

/* renamed from: org.telegram.messenger.-$$Lambda$MessagesController$7PfU2XqRFB8Ai3wS5YskIRrwN7M  reason: invalid class name */
/* compiled from: lambda */
public final /* synthetic */ class $$Lambda$MessagesController$7PfU2XqRFB8Ai3wS5YskIRrwN7M implements RequestDelegate {
    public static final /* synthetic */ $$Lambda$MessagesController$7PfU2XqRFB8Ai3wS5YskIRrwN7M INSTANCE = new $$Lambda$MessagesController$7PfU2XqRFB8Ai3wS5YskIRrwN7M();

    private /* synthetic */ $$Lambda$MessagesController$7PfU2XqRFB8Ai3wS5YskIRrwN7M() {
    }

    public final void run(TLObject tLObject, TLRPC$TL_error tLRPC$TL_error) {
        MessagesController.lambda$completeReadTask$179(tLObject, tLRPC$TL_error);
    }
}
