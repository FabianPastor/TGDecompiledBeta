package org.telegram.messenger;

import org.telegram.tgnet.RequestDelegate;
import org.telegram.tgnet.TLObject;
import org.telegram.tgnet.TLRPC$TL_error;

/* renamed from: org.telegram.messenger.-$$Lambda$MessagesController$4TTrx8qdspl4GXM9gQpEVwd3fts  reason: invalid class name */
/* compiled from: lambda */
public final /* synthetic */ class $$Lambda$MessagesController$4TTrx8qdspl4GXM9gQpEVwd3fts implements RequestDelegate {
    public static final /* synthetic */ $$Lambda$MessagesController$4TTrx8qdspl4GXM9gQpEVwd3fts INSTANCE = new $$Lambda$MessagesController$4TTrx8qdspl4GXM9gQpEVwd3fts();

    private /* synthetic */ $$Lambda$MessagesController$4TTrx8qdspl4GXM9gQpEVwd3fts() {
    }

    public final void run(TLObject tLObject, TLRPC$TL_error tLRPC$TL_error) {
        MessagesController.lambda$unregistedPush$211(tLObject, tLRPC$TL_error);
    }
}
