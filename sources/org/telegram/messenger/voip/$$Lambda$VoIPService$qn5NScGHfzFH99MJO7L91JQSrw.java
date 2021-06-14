package org.telegram.messenger.voip;

import org.telegram.tgnet.RequestDelegate;
import org.telegram.tgnet.TLObject;
import org.telegram.tgnet.TLRPC$TL_error;

/* renamed from: org.telegram.messenger.voip.-$$Lambda$VoIPService$qn5NScGHfzFH99MJO7L91J-QSrw  reason: invalid class name */
/* compiled from: lambda */
public final /* synthetic */ class $$Lambda$VoIPService$qn5NScGHfzFH99MJO7L91JQSrw implements RequestDelegate {
    public static final /* synthetic */ $$Lambda$VoIPService$qn5NScGHfzFH99MJO7L91JQSrw INSTANCE = new $$Lambda$VoIPService$qn5NScGHfzFH99MJO7L91JQSrw();

    private /* synthetic */ $$Lambda$VoIPService$qn5NScGHfzFH99MJO7L91JQSrw() {
    }

    public final void run(TLObject tLObject, TLRPC$TL_error tLRPC$TL_error) {
        VoIPService.lambda$callFailed$73(tLObject, tLRPC$TL_error);
    }
}
