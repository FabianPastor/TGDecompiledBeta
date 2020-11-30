package org.telegram.messenger.voip;

import org.telegram.tgnet.RequestDelegate;
import org.telegram.tgnet.TLObject;
import org.telegram.tgnet.TLRPC$TL_error;

/* renamed from: org.telegram.messenger.voip.-$$Lambda$VoIPService$dRQ4EbS344LR4gFNxV5Tg4tnSzw  reason: invalid class name */
/* compiled from: lambda */
public final /* synthetic */ class $$Lambda$VoIPService$dRQ4EbS344LR4gFNxV5Tg4tnSzw implements RequestDelegate {
    public static final /* synthetic */ $$Lambda$VoIPService$dRQ4EbS344LR4gFNxV5Tg4tnSzw INSTANCE = new $$Lambda$VoIPService$dRQ4EbS344LR4gFNxV5Tg4tnSzw();

    private /* synthetic */ $$Lambda$VoIPService$dRQ4EbS344LR4gFNxV5Tg4tnSzw() {
    }

    public final void run(TLObject tLObject, TLRPC$TL_error tLRPC$TL_error) {
        VoIPService.lambda$callFailed$38(tLObject, tLRPC$TL_error);
    }
}
