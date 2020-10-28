package org.telegram.messenger;

import org.telegram.tgnet.RequestDelegate;
import org.telegram.tgnet.TLObject;
import org.telegram.tgnet.TLRPC$TL_error;

/* renamed from: org.telegram.messenger.-$$Lambda$MessagesController$x4__aWGGe_L-Y6ISFQBr06YkUtE  reason: invalid class name */
/* compiled from: lambda */
public final /* synthetic */ class $$Lambda$MessagesController$x4__aWGGe_LY6ISFQBr06YkUtE implements RequestDelegate {
    public static final /* synthetic */ $$Lambda$MessagesController$x4__aWGGe_LY6ISFQBr06YkUtE INSTANCE = new $$Lambda$MessagesController$x4__aWGGe_LY6ISFQBr06YkUtE();

    private /* synthetic */ $$Lambda$MessagesController$x4__aWGGe_LY6ISFQBr06YkUtE() {
    }

    public final void run(TLObject tLObject, TLRPC$TL_error tLRPC$TL_error) {
        MessagesController.lambda$markMentionMessageAsRead$174(tLObject, tLRPC$TL_error);
    }
}
