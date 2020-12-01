package org.telegram.messenger.voip;

import org.telegram.tgnet.RequestDelegate;
import org.telegram.tgnet.TLObject;
import org.telegram.tgnet.TLRPC$TL_error;

/* renamed from: org.telegram.messenger.voip.-$$Lambda$VoIPService$0yqB0NU11RJQEfb-pKRXpaloAMc  reason: invalid class name */
/* compiled from: lambda */
public final /* synthetic */ class $$Lambda$VoIPService$0yqB0NU11RJQEfbpKRXpaloAMc implements RequestDelegate {
    public static final /* synthetic */ $$Lambda$VoIPService$0yqB0NU11RJQEfbpKRXpaloAMc INSTANCE = new $$Lambda$VoIPService$0yqB0NU11RJQEfbpKRXpaloAMc();

    private /* synthetic */ $$Lambda$VoIPService$0yqB0NU11RJQEfbpKRXpaloAMc() {
    }

    public final void run(TLObject tLObject, TLRPC$TL_error tLRPC$TL_error) {
        VoIPService.lambda$callFailed$39(tLObject, tLRPC$TL_error);
    }
}
