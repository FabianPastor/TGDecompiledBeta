package org.telegram.messenger;

import org.telegram.tgnet.RequestDelegate;
import org.telegram.tgnet.TLObject;
import org.telegram.tgnet.TLRPC$TL_error;

/* renamed from: org.telegram.messenger.-$$Lambda$MessagesController$OnOxyU5sZREbK55YPrZgiDM3N-E  reason: invalid class name */
/* compiled from: lambda */
public final /* synthetic */ class $$Lambda$MessagesController$OnOxyU5sZREbK55YPrZgiDM3NE implements RequestDelegate {
    public static final /* synthetic */ $$Lambda$MessagesController$OnOxyU5sZREbK55YPrZgiDM3NE INSTANCE = new $$Lambda$MessagesController$OnOxyU5sZREbK55YPrZgiDM3NE();

    private /* synthetic */ $$Lambda$MessagesController$OnOxyU5sZREbK55YPrZgiDM3NE() {
    }

    public final void run(TLObject tLObject, TLRPC$TL_error tLRPC$TL_error) {
        MessagesController.lambda$markMessageContentAsRead$179(tLObject, tLRPC$TL_error);
    }
}
