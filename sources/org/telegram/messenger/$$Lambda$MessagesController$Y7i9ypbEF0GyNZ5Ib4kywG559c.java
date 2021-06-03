package org.telegram.messenger;

import org.telegram.tgnet.RequestDelegate;
import org.telegram.tgnet.TLObject;
import org.telegram.tgnet.TLRPC$TL_error;

/* renamed from: org.telegram.messenger.-$$Lambda$MessagesController$Y7i9ypbEF0GyNZ5I-b4kywG559c  reason: invalid class name */
/* compiled from: lambda */
public final /* synthetic */ class $$Lambda$MessagesController$Y7i9ypbEF0GyNZ5Ib4kywG559c implements RequestDelegate {
    public static final /* synthetic */ $$Lambda$MessagesController$Y7i9ypbEF0GyNZ5Ib4kywG559c INSTANCE = new $$Lambda$MessagesController$Y7i9ypbEF0GyNZ5Ib4kywG559c();

    private /* synthetic */ $$Lambda$MessagesController$Y7i9ypbEF0GyNZ5Ib4kywG559c() {
    }

    public final void run(TLObject tLObject, TLRPC$TL_error tLRPC$TL_error) {
        MessagesController.lambda$markMentionMessageAsRead$182(tLObject, tLRPC$TL_error);
    }
}
