package org.telegram.messenger;

import org.telegram.tgnet.RequestDelegate;
import org.telegram.tgnet.TLObject;
import org.telegram.tgnet.TLRPC$TL_error;

/* renamed from: org.telegram.messenger.-$$Lambda$MessagesController$dNIB5iAL9e61KHda3kykMP23xSk  reason: invalid class name */
/* compiled from: lambda */
public final /* synthetic */ class $$Lambda$MessagesController$dNIB5iAL9e61KHda3kykMP23xSk implements RequestDelegate {
    public static final /* synthetic */ $$Lambda$MessagesController$dNIB5iAL9e61KHda3kykMP23xSk INSTANCE = new $$Lambda$MessagesController$dNIB5iAL9e61KHda3kykMP23xSk();

    private /* synthetic */ $$Lambda$MessagesController$dNIB5iAL9e61KHda3kykMP23xSk() {
    }

    public final void run(TLObject tLObject, TLRPC$TL_error tLRPC$TL_error) {
        MessagesController.lambda$unregistedPush$213(tLObject, tLRPC$TL_error);
    }
}
