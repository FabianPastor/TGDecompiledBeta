package org.telegram.messenger;

import org.telegram.tgnet.RequestDelegate;
import org.telegram.tgnet.TLObject;
import org.telegram.tgnet.TLRPC$TL_error;

/* renamed from: org.telegram.messenger.-$$Lambda$MessagesController$54zz1adgPhRHdqla2slrw9KcNhg  reason: invalid class name */
/* compiled from: lambda */
public final /* synthetic */ class $$Lambda$MessagesController$54zz1adgPhRHdqla2slrw9KcNhg implements RequestDelegate {
    public static final /* synthetic */ $$Lambda$MessagesController$54zz1adgPhRHdqla2slrw9KcNhg INSTANCE = new $$Lambda$MessagesController$54zz1adgPhRHdqla2slrw9KcNhg();

    private /* synthetic */ $$Lambda$MessagesController$54zz1adgPhRHdqla2slrw9KcNhg() {
    }

    public final void run(TLObject tLObject, TLRPC$TL_error tLRPC$TL_error) {
        MessagesController.lambda$deleteParticipantFromChat$226(tLObject, tLRPC$TL_error);
    }
}
