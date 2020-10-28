package org.telegram.messenger;

import org.telegram.tgnet.RequestDelegate;
import org.telegram.tgnet.TLObject;
import org.telegram.tgnet.TLRPC$TL_error;

/* renamed from: org.telegram.messenger.-$$Lambda$MessagesController$EHPM_t8sNvd4733p0iL9Zyvv3PU  reason: invalid class name */
/* compiled from: lambda */
public final /* synthetic */ class $$Lambda$MessagesController$EHPM_t8sNvd4733p0iL9Zyvv3PU implements RequestDelegate {
    public static final /* synthetic */ $$Lambda$MessagesController$EHPM_t8sNvd4733p0iL9Zyvv3PU INSTANCE = new $$Lambda$MessagesController$EHPM_t8sNvd4733p0iL9Zyvv3PU();

    private /* synthetic */ $$Lambda$MessagesController$EHPM_t8sNvd4733p0iL9Zyvv3PU() {
    }

    public final void run(TLObject tLObject, TLRPC$TL_error tLRPC$TL_error) {
        MessagesController.lambda$markMentionsAsRead$182(tLObject, tLRPC$TL_error);
    }
}
