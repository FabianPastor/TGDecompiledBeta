package org.telegram.messenger;

import org.telegram.tgnet.RequestDelegate;
import org.telegram.tgnet.TLObject;
import org.telegram.tgnet.TLRPC$TL_error;

/* renamed from: org.telegram.messenger.-$$Lambda$MessagesController$ZPXWXh9kYVMxeqqc0fSG7_zc4rg  reason: invalid class name */
/* compiled from: lambda */
public final /* synthetic */ class $$Lambda$MessagesController$ZPXWXh9kYVMxeqqc0fSG7_zc4rg implements RequestDelegate {
    public static final /* synthetic */ $$Lambda$MessagesController$ZPXWXh9kYVMxeqqc0fSG7_zc4rg INSTANCE = new $$Lambda$MessagesController$ZPXWXh9kYVMxeqqc0fSG7_zc4rg();

    private /* synthetic */ $$Lambda$MessagesController$ZPXWXh9kYVMxeqqc0fSG7_zc4rg() {
    }

    public final void run(TLObject tLObject, TLRPC$TL_error tLRPC$TL_error) {
        MessagesController.lambda$deleteUserPhoto$79(tLObject, tLRPC$TL_error);
    }
}
