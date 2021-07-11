package org.telegram.messenger.voip;

import org.telegram.tgnet.RequestDelegate;
import org.telegram.tgnet.TLObject;
import org.telegram.tgnet.TLRPC$TL_error;

/* renamed from: org.telegram.messenger.voip.-$$Lambda$VoIPService$LLvwxjtYKlL0Si3GcnBBqaO7cyE  reason: invalid class name */
/* compiled from: lambda */
public final /* synthetic */ class $$Lambda$VoIPService$LLvwxjtYKlL0Si3GcnBBqaO7cyE implements RequestDelegate {
    public static final /* synthetic */ $$Lambda$VoIPService$LLvwxjtYKlL0Si3GcnBBqaO7cyE INSTANCE = new $$Lambda$VoIPService$LLvwxjtYKlL0Si3GcnBBqaO7cyE();

    private /* synthetic */ $$Lambda$VoIPService$LLvwxjtYKlL0Si3GcnBBqaO7cyE() {
    }

    public final void run(TLObject tLObject, TLRPC$TL_error tLRPC$TL_error) {
        VoIPService.lambda$onTgVoipStop$69(tLObject, tLRPC$TL_error);
    }
}
