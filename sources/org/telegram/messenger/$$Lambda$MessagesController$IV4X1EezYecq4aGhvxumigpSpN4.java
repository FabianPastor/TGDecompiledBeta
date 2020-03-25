package org.telegram.messenger;

import org.telegram.tgnet.RequestDelegate;
import org.telegram.tgnet.TLObject;
import org.telegram.tgnet.TLRPC$TL_error;

/* renamed from: org.telegram.messenger.-$$Lambda$MessagesController$IV4X1EezYecq4aGhvxumigpSpN4  reason: invalid class name */
/* compiled from: lambda */
public final /* synthetic */ class $$Lambda$MessagesController$IV4X1EezYecq4aGhvxumigpSpN4 implements RequestDelegate {
    public static final /* synthetic */ $$Lambda$MessagesController$IV4X1EezYecq4aGhvxumigpSpN4 INSTANCE = new $$Lambda$MessagesController$IV4X1EezYecq4aGhvxumigpSpN4();

    private /* synthetic */ $$Lambda$MessagesController$IV4X1EezYecq4aGhvxumigpSpN4() {
    }

    public final void run(TLObject tLObject, TLRPC$TL_error tLRPC$TL_error) {
        MessagesController.lambda$reportSpam$45(tLObject, tLRPC$TL_error);
    }
}
