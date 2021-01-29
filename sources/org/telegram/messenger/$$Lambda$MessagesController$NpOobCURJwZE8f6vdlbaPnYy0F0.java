package org.telegram.messenger;

import org.telegram.tgnet.RequestDelegate;
import org.telegram.tgnet.TLObject;
import org.telegram.tgnet.TLRPC$TL_error;

/* renamed from: org.telegram.messenger.-$$Lambda$MessagesController$NpOobCURJwZE8f6vdlbaPnYy0F0  reason: invalid class name */
/* compiled from: lambda */
public final /* synthetic */ class $$Lambda$MessagesController$NpOobCURJwZE8f6vdlbaPnYy0F0 implements RequestDelegate {
    public static final /* synthetic */ $$Lambda$MessagesController$NpOobCURJwZE8f6vdlbaPnYy0F0 INSTANCE = new $$Lambda$MessagesController$NpOobCURJwZE8f6vdlbaPnYy0F0();

    private /* synthetic */ $$Lambda$MessagesController$NpOobCURJwZE8f6vdlbaPnYy0F0() {
    }

    public final void run(TLObject tLObject, TLRPC$TL_error tLRPC$TL_error) {
        MessagesController.lambda$markMentionsAsRead$188(tLObject, tLRPC$TL_error);
    }
}
