package org.telegram.messenger;

import org.telegram.tgnet.RequestDelegate;
import org.telegram.tgnet.TLObject;
import org.telegram.tgnet.TLRPC$TL_error;

/* renamed from: org.telegram.messenger.-$$Lambda$MessagesController$NKR1sAtHMkiKxeCXwPnbpw6sdNU  reason: invalid class name */
/* compiled from: lambda */
public final /* synthetic */ class $$Lambda$MessagesController$NKR1sAtHMkiKxeCXwPnbpw6sdNU implements RequestDelegate {
    public static final /* synthetic */ $$Lambda$MessagesController$NKR1sAtHMkiKxeCXwPnbpw6sdNU INSTANCE = new $$Lambda$MessagesController$NKR1sAtHMkiKxeCXwPnbpw6sdNU();

    private /* synthetic */ $$Lambda$MessagesController$NKR1sAtHMkiKxeCXwPnbpw6sdNU() {
    }

    public final void run(TLObject tLObject, TLRPC$TL_error tLRPC$TL_error) {
        MessagesController.lambda$saveTheme$80(tLObject, tLRPC$TL_error);
    }
}
