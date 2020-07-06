package org.telegram.messenger;

import org.telegram.tgnet.RequestDelegate;
import org.telegram.tgnet.TLObject;
import org.telegram.tgnet.TLRPC$TL_error;

/* renamed from: org.telegram.messenger.-$$Lambda$MessagesController$pMtGSpENzS7HL-F3SHJXpdBnTyY  reason: invalid class name */
/* compiled from: lambda */
public final /* synthetic */ class $$Lambda$MessagesController$pMtGSpENzS7HLF3SHJXpdBnTyY implements RequestDelegate {
    public static final /* synthetic */ $$Lambda$MessagesController$pMtGSpENzS7HLF3SHJXpdBnTyY INSTANCE = new $$Lambda$MessagesController$pMtGSpENzS7HLF3SHJXpdBnTyY();

    private /* synthetic */ $$Lambda$MessagesController$pMtGSpENzS7HLF3SHJXpdBnTyY() {
    }

    public final void run(TLObject tLObject, TLRPC$TL_error tLRPC$TL_error) {
        MessagesController.lambda$removeSuggestion$16(tLObject, tLRPC$TL_error);
    }
}
