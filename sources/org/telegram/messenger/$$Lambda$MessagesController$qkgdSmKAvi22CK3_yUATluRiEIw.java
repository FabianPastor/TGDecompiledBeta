package org.telegram.messenger;

import org.telegram.tgnet.RequestDelegate;
import org.telegram.tgnet.TLObject;
import org.telegram.tgnet.TLRPC$TL_error;

/* renamed from: org.telegram.messenger.-$$Lambda$MessagesController$qkgdSmKAvi22CK3_yUATluRiEIw  reason: invalid class name */
/* compiled from: lambda */
public final /* synthetic */ class $$Lambda$MessagesController$qkgdSmKAvi22CK3_yUATluRiEIw implements RequestDelegate {
    public static final /* synthetic */ $$Lambda$MessagesController$qkgdSmKAvi22CK3_yUATluRiEIw INSTANCE = new $$Lambda$MessagesController$qkgdSmKAvi22CK3_yUATluRiEIw();

    private /* synthetic */ $$Lambda$MessagesController$qkgdSmKAvi22CK3_yUATluRiEIw() {
    }

    public final void run(TLObject tLObject, TLRPC$TL_error tLRPC$TL_error) {
        MessagesController.lambda$markMentionMessageAsRead$181(tLObject, tLRPC$TL_error);
    }
}
