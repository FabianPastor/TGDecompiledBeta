package org.telegram.ui;

import org.telegram.tgnet.RequestDelegate;
import org.telegram.tgnet.TLObject;
import org.telegram.tgnet.TLRPC$TL_error;

/* renamed from: org.telegram.ui.-$$Lambda$ChatActivity$SKtWgHX_gi86PaHPOSzr8vRUZyY  reason: invalid class name */
/* compiled from: lambda */
public final /* synthetic */ class $$Lambda$ChatActivity$SKtWgHX_gi86PaHPOSzr8vRUZyY implements RequestDelegate {
    public static final /* synthetic */ $$Lambda$ChatActivity$SKtWgHX_gi86PaHPOSzr8vRUZyY INSTANCE = new $$Lambda$ChatActivity$SKtWgHX_gi86PaHPOSzr8vRUZyY();

    private /* synthetic */ $$Lambda$ChatActivity$SKtWgHX_gi86PaHPOSzr8vRUZyY() {
    }

    public final void run(TLObject tLObject, TLRPC$TL_error tLRPC$TL_error) {
        ChatActivity.lambda$markSponsoredAsRead$163(tLObject, tLRPC$TL_error);
    }
}
