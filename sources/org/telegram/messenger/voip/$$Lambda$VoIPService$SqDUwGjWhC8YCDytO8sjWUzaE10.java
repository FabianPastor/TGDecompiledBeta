package org.telegram.messenger.voip;

import org.telegram.tgnet.RequestDelegate;
import org.telegram.tgnet.TLObject;
import org.telegram.tgnet.TLRPC$TL_error;

/* renamed from: org.telegram.messenger.voip.-$$Lambda$VoIPService$SqDUwGjWhC8YCDytO8sjWUzaE10  reason: invalid class name */
/* compiled from: lambda */
public final /* synthetic */ class $$Lambda$VoIPService$SqDUwGjWhC8YCDytO8sjWUzaE10 implements RequestDelegate {
    public static final /* synthetic */ $$Lambda$VoIPService$SqDUwGjWhC8YCDytO8sjWUzaE10 INSTANCE = new $$Lambda$VoIPService$SqDUwGjWhC8YCDytO8sjWUzaE10();

    private /* synthetic */ $$Lambda$VoIPService$SqDUwGjWhC8YCDytO8sjWUzaE10() {
    }

    public final void run(TLObject tLObject, TLRPC$TL_error tLRPC$TL_error) {
        VoIPService.lambda$onTgVoipStop$3(tLObject, tLRPC$TL_error);
    }
}
