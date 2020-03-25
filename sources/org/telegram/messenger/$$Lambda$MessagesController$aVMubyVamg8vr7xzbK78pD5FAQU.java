package org.telegram.messenger;

import org.telegram.tgnet.RequestDelegate;
import org.telegram.tgnet.TLObject;
import org.telegram.tgnet.TLRPC$TL_error;

/* renamed from: org.telegram.messenger.-$$Lambda$MessagesController$aVMubyVamg8vr7xzbK78pD5FAQU  reason: invalid class name */
/* compiled from: lambda */
public final /* synthetic */ class $$Lambda$MessagesController$aVMubyVamg8vr7xzbK78pD5FAQU implements RequestDelegate {
    public static final /* synthetic */ $$Lambda$MessagesController$aVMubyVamg8vr7xzbK78pD5FAQU INSTANCE = new $$Lambda$MessagesController$aVMubyVamg8vr7xzbK78pD5FAQU();

    private /* synthetic */ $$Lambda$MessagesController$aVMubyVamg8vr7xzbK78pD5FAQU() {
    }

    public final void run(TLObject tLObject, TLRPC$TL_error tLRPC$TL_error) {
        MessagesController.lambda$completeReadTask$173(tLObject, tLRPC$TL_error);
    }
}
