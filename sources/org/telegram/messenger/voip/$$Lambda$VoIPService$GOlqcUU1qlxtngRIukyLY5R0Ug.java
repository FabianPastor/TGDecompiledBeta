package org.telegram.messenger.voip;

import org.telegram.tgnet.RequestDelegate;
import org.telegram.tgnet.TLObject;
import org.telegram.tgnet.TLRPC$TL_error;

/* renamed from: org.telegram.messenger.voip.-$$Lambda$VoIPService$GOlqcUU1ql-xtngRIukyLY5R0Ug  reason: invalid class name */
/* compiled from: lambda */
public final /* synthetic */ class $$Lambda$VoIPService$GOlqcUU1qlxtngRIukyLY5R0Ug implements RequestDelegate {
    public static final /* synthetic */ $$Lambda$VoIPService$GOlqcUU1qlxtngRIukyLY5R0Ug INSTANCE = new $$Lambda$VoIPService$GOlqcUU1qlxtngRIukyLY5R0Ug();

    private /* synthetic */ $$Lambda$VoIPService$GOlqcUU1qlxtngRIukyLY5R0Ug() {
    }

    public final void run(TLObject tLObject, TLRPC$TL_error tLRPC$TL_error) {
        VoIPService.lambda$callFailed$23(tLObject, tLRPC$TL_error);
    }
}
