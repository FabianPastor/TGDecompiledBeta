package org.telegram.messenger.voip;

import org.telegram.tgnet.RequestDelegate;
import org.telegram.tgnet.TLObject;
import org.telegram.tgnet.TLRPC$TL_error;

/* renamed from: org.telegram.messenger.voip.-$$Lambda$VoIPService$FzCKblyGK0lj5OtXTRVuPcU98I8  reason: invalid class name */
/* compiled from: lambda */
public final /* synthetic */ class $$Lambda$VoIPService$FzCKblyGK0lj5OtXTRVuPcU98I8 implements RequestDelegate {
    public static final /* synthetic */ $$Lambda$VoIPService$FzCKblyGK0lj5OtXTRVuPcU98I8 INSTANCE = new $$Lambda$VoIPService$FzCKblyGK0lj5OtXTRVuPcU98I8();

    private /* synthetic */ $$Lambda$VoIPService$FzCKblyGK0lj5OtXTRVuPcU98I8() {
    }

    public final void run(TLObject tLObject, TLRPC$TL_error tLRPC$TL_error) {
        VoIPService.lambda$callFailed$43(tLObject, tLRPC$TL_error);
    }
}
