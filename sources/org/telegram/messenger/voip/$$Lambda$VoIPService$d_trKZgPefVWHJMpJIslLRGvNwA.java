package org.telegram.messenger.voip;

import org.telegram.tgnet.RequestDelegate;
import org.telegram.tgnet.TLObject;
import org.telegram.tgnet.TLRPC$TL_error;

/* renamed from: org.telegram.messenger.voip.-$$Lambda$VoIPService$d_trKZgPefVWHJMpJIslLRGvNwA  reason: invalid class name */
/* compiled from: lambda */
public final /* synthetic */ class $$Lambda$VoIPService$d_trKZgPefVWHJMpJIslLRGvNwA implements RequestDelegate {
    public static final /* synthetic */ $$Lambda$VoIPService$d_trKZgPefVWHJMpJIslLRGvNwA INSTANCE = new $$Lambda$VoIPService$d_trKZgPefVWHJMpJIslLRGvNwA();

    private /* synthetic */ $$Lambda$VoIPService$d_trKZgPefVWHJMpJIslLRGvNwA() {
    }

    public final void run(TLObject tLObject, TLRPC$TL_error tLRPC$TL_error) {
        VoIPService.lambda$onTgVoipStop$68(tLObject, tLRPC$TL_error);
    }
}
