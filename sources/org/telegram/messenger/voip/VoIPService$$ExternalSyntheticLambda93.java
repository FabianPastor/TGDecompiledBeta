package org.telegram.messenger.voip;

import org.telegram.tgnet.RequestDelegate;
import org.telegram.tgnet.TLObject;
import org.telegram.tgnet.TLRPC$TL_error;
import org.telegram.tgnet.TLRPC$TL_phone_checkGroupCall;

public final /* synthetic */ class VoIPService$$ExternalSyntheticLambda93 implements RequestDelegate {
    public final /* synthetic */ VoIPService f$0;
    public final /* synthetic */ TLRPC$TL_phone_checkGroupCall f$1;

    public /* synthetic */ VoIPService$$ExternalSyntheticLambda93(VoIPService voIPService, TLRPC$TL_phone_checkGroupCall tLRPC$TL_phone_checkGroupCall) {
        this.f$0 = voIPService;
        this.f$1 = tLRPC$TL_phone_checkGroupCall;
    }

    public final void run(TLObject tLObject, TLRPC$TL_error tLRPC$TL_error) {
        this.f$0.lambda$startGroupCheckShortpoll$34(this.f$1, tLObject, tLRPC$TL_error);
    }
}
