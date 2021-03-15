package org.telegram.messenger.voip;

import org.telegram.tgnet.RequestDelegate;
import org.telegram.tgnet.TLObject;
import org.telegram.tgnet.TLRPC$TL_error;

/* renamed from: org.telegram.messenger.voip.-$$Lambda$VoIPService$cpm__oqbtIH_QVZGEIIEgkykktw  reason: invalid class name */
/* compiled from: lambda */
public final /* synthetic */ class $$Lambda$VoIPService$cpm__oqbtIH_QVZGEIIEgkykktw implements RequestDelegate {
    public static final /* synthetic */ $$Lambda$VoIPService$cpm__oqbtIH_QVZGEIIEgkykktw INSTANCE = new $$Lambda$VoIPService$cpm__oqbtIH_QVZGEIIEgkykktw();

    private /* synthetic */ $$Lambda$VoIPService$cpm__oqbtIH_QVZGEIIEgkykktw() {
    }

    public final void run(TLObject tLObject, TLRPC$TL_error tLRPC$TL_error) {
        VoIPService.lambda$callFailed$53(tLObject, tLRPC$TL_error);
    }
}
