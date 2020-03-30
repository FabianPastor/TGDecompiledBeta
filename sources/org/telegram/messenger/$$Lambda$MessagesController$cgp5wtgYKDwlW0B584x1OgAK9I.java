package org.telegram.messenger;

import org.telegram.tgnet.RequestDelegate;
import org.telegram.tgnet.TLObject;
import org.telegram.tgnet.TLRPC$TL_error;

/* renamed from: org.telegram.messenger.-$$Lambda$MessagesController$cgp5wt-gYKDwlW0B584x1OgAK9I  reason: invalid class name */
/* compiled from: lambda */
public final /* synthetic */ class $$Lambda$MessagesController$cgp5wtgYKDwlW0B584x1OgAK9I implements RequestDelegate {
    public static final /* synthetic */ $$Lambda$MessagesController$cgp5wtgYKDwlW0B584x1OgAK9I INSTANCE = new $$Lambda$MessagesController$cgp5wtgYKDwlW0B584x1OgAK9I();

    private /* synthetic */ $$Lambda$MessagesController$cgp5wtgYKDwlW0B584x1OgAK9I() {
    }

    public final void run(TLObject tLObject, TLRPC$TL_error tLRPC$TL_error) {
        MessagesController.lambda$markMessageContentAsRead$165(tLObject, tLRPC$TL_error);
    }
}
