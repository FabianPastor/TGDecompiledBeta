package org.telegram.messenger;

import org.telegram.tgnet.RequestDelegate;
import org.telegram.tgnet.TLObject;
import org.telegram.tgnet.TLRPC$TL_error;

/* renamed from: org.telegram.messenger.-$$Lambda$MessagesController$KKcl_B4z5hiJkV3FoV0zNuErsKM  reason: invalid class name */
/* compiled from: lambda */
public final /* synthetic */ class $$Lambda$MessagesController$KKcl_B4z5hiJkV3FoV0zNuErsKM implements RequestDelegate {
    public static final /* synthetic */ $$Lambda$MessagesController$KKcl_B4z5hiJkV3FoV0zNuErsKM INSTANCE = new $$Lambda$MessagesController$KKcl_B4z5hiJkV3FoV0zNuErsKM();

    private /* synthetic */ $$Lambda$MessagesController$KKcl_B4z5hiJkV3FoV0zNuErsKM() {
    }

    public final void run(TLObject tLObject, TLRPC$TL_error tLRPC$TL_error) {
        MessagesController.lambda$reportSpam$46(tLObject, tLRPC$TL_error);
    }
}
