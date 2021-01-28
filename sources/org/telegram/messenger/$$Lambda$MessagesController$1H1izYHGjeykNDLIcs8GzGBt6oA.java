package org.telegram.messenger;

import org.telegram.tgnet.RequestDelegate;
import org.telegram.tgnet.TLObject;
import org.telegram.tgnet.TLRPC$TL_error;

/* renamed from: org.telegram.messenger.-$$Lambda$MessagesController$1H1izYHGjeykNDLIcs8GzGBt6oA  reason: invalid class name */
/* compiled from: lambda */
public final /* synthetic */ class $$Lambda$MessagesController$1H1izYHGjeykNDLIcs8GzGBt6oA implements RequestDelegate {
    public static final /* synthetic */ $$Lambda$MessagesController$1H1izYHGjeykNDLIcs8GzGBt6oA INSTANCE = new $$Lambda$MessagesController$1H1izYHGjeykNDLIcs8GzGBt6oA();

    private /* synthetic */ $$Lambda$MessagesController$1H1izYHGjeykNDLIcs8GzGBt6oA() {
    }

    public final void run(TLObject tLObject, TLRPC$TL_error tLRPC$TL_error) {
        MessagesController.lambda$unregistedPush$226(tLObject, tLRPC$TL_error);
    }
}
