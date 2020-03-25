package org.telegram.messenger;

import org.telegram.tgnet.RequestDelegate;
import org.telegram.tgnet.TLObject;
import org.telegram.tgnet.TLRPC$TL_error;

/* renamed from: org.telegram.messenger.-$$Lambda$MessagesController$k5LS8yQIZqbcjvzDvE00EFa_dtY  reason: invalid class name */
/* compiled from: lambda */
public final /* synthetic */ class $$Lambda$MessagesController$k5LS8yQIZqbcjvzDvE00EFa_dtY implements RequestDelegate {
    public static final /* synthetic */ $$Lambda$MessagesController$k5LS8yQIZqbcjvzDvE00EFa_dtY INSTANCE = new $$Lambda$MessagesController$k5LS8yQIZqbcjvzDvE00EFa_dtY();

    private /* synthetic */ $$Lambda$MessagesController$k5LS8yQIZqbcjvzDvE00EFa_dtY() {
    }

    public final void run(TLObject tLObject, TLRPC$TL_error tLRPC$TL_error) {
        MessagesController.lambda$markMessageContentAsRead$166(tLObject, tLRPC$TL_error);
    }
}
