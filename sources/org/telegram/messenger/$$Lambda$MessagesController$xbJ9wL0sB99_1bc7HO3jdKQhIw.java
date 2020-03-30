package org.telegram.messenger;

import org.telegram.tgnet.RequestDelegate;
import org.telegram.tgnet.TLObject;
import org.telegram.tgnet.TLRPC$TL_error;

/* renamed from: org.telegram.messenger.-$$Lambda$MessagesController$xbJ9wL0sB99_1bc7HO3j-dKQhIw  reason: invalid class name */
/* compiled from: lambda */
public final /* synthetic */ class $$Lambda$MessagesController$xbJ9wL0sB99_1bc7HO3jdKQhIw implements RequestDelegate {
    public static final /* synthetic */ $$Lambda$MessagesController$xbJ9wL0sB99_1bc7HO3jdKQhIw INSTANCE = new $$Lambda$MessagesController$xbJ9wL0sB99_1bc7HO3jdKQhIw();

    private /* synthetic */ $$Lambda$MessagesController$xbJ9wL0sB99_1bc7HO3jdKQhIw() {
    }

    public final void run(TLObject tLObject, TLRPC$TL_error tLRPC$TL_error) {
        MessagesController.lambda$unregistedPush$210(tLObject, tLRPC$TL_error);
    }
}
