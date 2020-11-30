package org.telegram.messenger;

import org.telegram.tgnet.RequestDelegate;
import org.telegram.tgnet.TLObject;
import org.telegram.tgnet.TLRPC$TL_error;

/* renamed from: org.telegram.messenger.-$$Lambda$MessagesController$70_XqE3NeDmzVo64lGxq7x3PoZw  reason: invalid class name */
/* compiled from: lambda */
public final /* synthetic */ class $$Lambda$MessagesController$70_XqE3NeDmzVo64lGxq7x3PoZw implements RequestDelegate {
    public static final /* synthetic */ $$Lambda$MessagesController$70_XqE3NeDmzVo64lGxq7x3PoZw INSTANCE = new $$Lambda$MessagesController$70_XqE3NeDmzVo64lGxq7x3PoZw();

    private /* synthetic */ $$Lambda$MessagesController$70_XqE3NeDmzVo64lGxq7x3PoZw() {
    }

    public final void run(TLObject tLObject, TLRPC$TL_error tLRPC$TL_error) {
        MessagesController.lambda$hidePromoDialog$97(tLObject, tLRPC$TL_error);
    }
}
