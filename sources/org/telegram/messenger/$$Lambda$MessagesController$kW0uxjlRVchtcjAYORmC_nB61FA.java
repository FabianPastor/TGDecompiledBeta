package org.telegram.messenger;

import org.telegram.tgnet.RequestDelegate;
import org.telegram.tgnet.TLObject;
import org.telegram.tgnet.TLRPC$TL_error;

/* renamed from: org.telegram.messenger.-$$Lambda$MessagesController$kW0uxjlRVchtcjAYORmC_nB61FA  reason: invalid class name */
/* compiled from: lambda */
public final /* synthetic */ class $$Lambda$MessagesController$kW0uxjlRVchtcjAYORmC_nB61FA implements RequestDelegate {
    public static final /* synthetic */ $$Lambda$MessagesController$kW0uxjlRVchtcjAYORmC_nB61FA INSTANCE = new $$Lambda$MessagesController$kW0uxjlRVchtcjAYORmC_nB61FA();

    private /* synthetic */ $$Lambda$MessagesController$kW0uxjlRVchtcjAYORmC_nB61FA() {
    }

    public final void run(TLObject tLObject, TLRPC$TL_error tLRPC$TL_error) {
        MessagesController.lambda$markMentionsAsRead$190(tLObject, tLRPC$TL_error);
    }
}
