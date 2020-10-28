package org.telegram.messenger.voip;

import org.telegram.tgnet.RequestDelegate;
import org.telegram.tgnet.TLObject;
import org.telegram.tgnet.TLRPC$TL_error;

/* renamed from: org.telegram.messenger.voip.-$$Lambda$VoIPService$TdjRXVf_0YtPqKXQQjHvuIWzfVk  reason: invalid class name */
/* compiled from: lambda */
public final /* synthetic */ class $$Lambda$VoIPService$TdjRXVf_0YtPqKXQQjHvuIWzfVk implements RequestDelegate {
    public static final /* synthetic */ $$Lambda$VoIPService$TdjRXVf_0YtPqKXQQjHvuIWzfVk INSTANCE = new $$Lambda$VoIPService$TdjRXVf_0YtPqKXQQjHvuIWzfVk();

    private /* synthetic */ $$Lambda$VoIPService$TdjRXVf_0YtPqKXQQjHvuIWzfVk() {
    }

    public final void run(TLObject tLObject, TLRPC$TL_error tLRPC$TL_error) {
        VoIPService.lambda$onSignalingData$22(tLObject, tLRPC$TL_error);
    }
}
