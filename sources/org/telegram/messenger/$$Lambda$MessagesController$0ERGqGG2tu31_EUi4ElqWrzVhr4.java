package org.telegram.messenger;

import org.telegram.tgnet.RequestDelegate;
import org.telegram.tgnet.TLObject;
import org.telegram.tgnet.TLRPC$TL_error;

/* renamed from: org.telegram.messenger.-$$Lambda$MessagesController$0ERGqGG2tu31_EUi4ElqWrzVhr4  reason: invalid class name */
/* compiled from: lambda */
public final /* synthetic */ class $$Lambda$MessagesController$0ERGqGG2tu31_EUi4ElqWrzVhr4 implements RequestDelegate {
    public static final /* synthetic */ $$Lambda$MessagesController$0ERGqGG2tu31_EUi4ElqWrzVhr4 INSTANCE = new $$Lambda$MessagesController$0ERGqGG2tu31_EUi4ElqWrzVhr4();

    private /* synthetic */ $$Lambda$MessagesController$0ERGqGG2tu31_EUi4ElqWrzVhr4() {
    }

    public final void run(TLObject tLObject, TLRPC$TL_error tLRPC$TL_error) {
        MessagesController.lambda$markMessageContentAsRead$168(tLObject, tLRPC$TL_error);
    }
}
