package org.telegram.messenger.voip;

import org.telegram.tgnet.RequestDelegate;
import org.telegram.tgnet.TLObject;
import org.telegram.tgnet.TLRPC$TL_error;

/* renamed from: org.telegram.messenger.voip.-$$Lambda$VoIPService$tD8E0bsgTzjrdDyIHEVobl6ZE_o  reason: invalid class name */
/* compiled from: lambda */
public final /* synthetic */ class $$Lambda$VoIPService$tD8E0bsgTzjrdDyIHEVobl6ZE_o implements RequestDelegate {
    public static final /* synthetic */ $$Lambda$VoIPService$tD8E0bsgTzjrdDyIHEVobl6ZE_o INSTANCE = new $$Lambda$VoIPService$tD8E0bsgTzjrdDyIHEVobl6ZE_o();

    private /* synthetic */ $$Lambda$VoIPService$tD8E0bsgTzjrdDyIHEVobl6ZE_o() {
    }

    public final void run(TLObject tLObject, TLRPC$TL_error tLRPC$TL_error) {
        VoIPService.lambda$createGroupInstance$37(tLObject, tLRPC$TL_error);
    }
}
