package org.telegram.messenger.voip;

import org.telegram.tgnet.RequestDelegate;
import org.telegram.tgnet.TLObject;
import org.telegram.tgnet.TLRPC$TL_error;

/* renamed from: org.telegram.messenger.voip.-$$Lambda$VoIPService$2ZRlOIxyDe27zm_qOB0zL75EkzM  reason: invalid class name */
/* compiled from: lambda */
public final /* synthetic */ class $$Lambda$VoIPService$2ZRlOIxyDe27zm_qOB0zL75EkzM implements RequestDelegate {
    public static final /* synthetic */ $$Lambda$VoIPService$2ZRlOIxyDe27zm_qOB0zL75EkzM INSTANCE = new $$Lambda$VoIPService$2ZRlOIxyDe27zm_qOB0zL75EkzM();

    private /* synthetic */ $$Lambda$VoIPService$2ZRlOIxyDe27zm_qOB0zL75EkzM() {
    }

    public final void run(TLObject tLObject, TLRPC$TL_error tLRPC$TL_error) {
        VoIPService.lambda$createGroupInstance$35(tLObject, tLRPC$TL_error);
    }
}
