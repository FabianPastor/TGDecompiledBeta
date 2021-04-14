package org.telegram.messenger.voip;

import org.telegram.tgnet.RequestDelegate;
import org.telegram.tgnet.TLObject;
import org.telegram.tgnet.TLRPC$TL_error;

/* renamed from: org.telegram.messenger.voip.-$$Lambda$VoIPService$TGBJKDJxmUYg_xRu7uwKiyWWOPY  reason: invalid class name */
/* compiled from: lambda */
public final /* synthetic */ class $$Lambda$VoIPService$TGBJKDJxmUYg_xRu7uwKiyWWOPY implements RequestDelegate {
    public static final /* synthetic */ $$Lambda$VoIPService$TGBJKDJxmUYg_xRu7uwKiyWWOPY INSTANCE = new $$Lambda$VoIPService$TGBJKDJxmUYg_xRu7uwKiyWWOPY();

    private /* synthetic */ $$Lambda$VoIPService$TGBJKDJxmUYg_xRu7uwKiyWWOPY() {
    }

    public final void run(TLObject tLObject, TLRPC$TL_error tLRPC$TL_error) {
        VoIPService.lambda$onSignalingData$53(tLObject, tLRPC$TL_error);
    }
}
