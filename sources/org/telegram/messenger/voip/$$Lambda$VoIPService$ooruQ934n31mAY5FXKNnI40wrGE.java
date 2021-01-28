package org.telegram.messenger.voip;

import org.telegram.tgnet.RequestDelegate;
import org.telegram.tgnet.TLObject;
import org.telegram.tgnet.TLRPC$TL_error;

/* renamed from: org.telegram.messenger.voip.-$$Lambda$VoIPService$ooruQ934n31mAY5FXKNnI40wrGE  reason: invalid class name */
/* compiled from: lambda */
public final /* synthetic */ class $$Lambda$VoIPService$ooruQ934n31mAY5FXKNnI40wrGE implements RequestDelegate {
    public static final /* synthetic */ $$Lambda$VoIPService$ooruQ934n31mAY5FXKNnI40wrGE INSTANCE = new $$Lambda$VoIPService$ooruQ934n31mAY5FXKNnI40wrGE();

    private /* synthetic */ $$Lambda$VoIPService$ooruQ934n31mAY5FXKNnI40wrGE() {
    }

    public final void run(TLObject tLObject, TLRPC$TL_error tLRPC$TL_error) {
        VoIPService.lambda$onSignalingData$44(tLObject, tLRPC$TL_error);
    }
}
