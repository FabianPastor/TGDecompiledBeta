package org.telegram.messenger;

import org.telegram.tgnet.RequestDelegate;
import org.telegram.tgnet.TLObject;
import org.telegram.tgnet.TLRPC$TL_error;

/* renamed from: org.telegram.messenger.-$$Lambda$MessagesController$Zcw9Vg1-1ffvuH_bfM76ci62ORQ  reason: invalid class name */
/* compiled from: lambda */
public final /* synthetic */ class $$Lambda$MessagesController$Zcw9Vg11ffvuH_bfM76ci62ORQ implements RequestDelegate {
    public static final /* synthetic */ $$Lambda$MessagesController$Zcw9Vg11ffvuH_bfM76ci62ORQ INSTANCE = new $$Lambda$MessagesController$Zcw9Vg11ffvuH_bfM76ci62ORQ();

    private /* synthetic */ $$Lambda$MessagesController$Zcw9Vg11ffvuH_bfM76ci62ORQ() {
    }

    public final void run(TLObject tLObject, TLRPC$TL_error tLRPC$TL_error) {
        MessagesController.lambda$markMentionMessageAsRead$175(tLObject, tLRPC$TL_error);
    }
}
