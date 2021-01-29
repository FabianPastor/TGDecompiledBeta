package org.telegram.messenger;

import org.telegram.tgnet.RequestDelegate;
import org.telegram.tgnet.TLObject;
import org.telegram.tgnet.TLRPC$TL_error;

/* renamed from: org.telegram.messenger.-$$Lambda$MessagesController$b5ZVh5llvhbm5O84HPj-wKYV0Eo  reason: invalid class name */
/* compiled from: lambda */
public final /* synthetic */ class $$Lambda$MessagesController$b5ZVh5llvhbm5O84HPjwKYV0Eo implements RequestDelegate {
    public static final /* synthetic */ $$Lambda$MessagesController$b5ZVh5llvhbm5O84HPjwKYV0Eo INSTANCE = new $$Lambda$MessagesController$b5ZVh5llvhbm5O84HPjwKYV0Eo();

    private /* synthetic */ $$Lambda$MessagesController$b5ZVh5llvhbm5O84HPjwKYV0Eo() {
    }

    public final void run(TLObject tLObject, TLRPC$TL_error tLRPC$TL_error) {
        MessagesController.lambda$saveTheme$84(tLObject, tLRPC$TL_error);
    }
}
