package org.telegram.messenger;

import org.telegram.tgnet.RequestDelegate;
import org.telegram.tgnet.TLObject;
import org.telegram.tgnet.TLRPC$TL_error;

/* renamed from: org.telegram.messenger.-$$Lambda$MessagesController$Szr-jBhpii84ZAKkSLSVebK-nKg  reason: invalid class name */
/* compiled from: lambda */
public final /* synthetic */ class $$Lambda$MessagesController$SzrjBhpii84ZAKkSLSVebKnKg implements RequestDelegate {
    public static final /* synthetic */ $$Lambda$MessagesController$SzrjBhpii84ZAKkSLSVebKnKg INSTANCE = new $$Lambda$MessagesController$SzrjBhpii84ZAKkSLSVebKnKg();

    private /* synthetic */ $$Lambda$MessagesController$SzrjBhpii84ZAKkSLSVebKnKg() {
    }

    public final void run(TLObject tLObject, TLRPC$TL_error tLRPC$TL_error) {
        MessagesController.lambda$markMessageContentAsRead$180(tLObject, tLRPC$TL_error);
    }
}
