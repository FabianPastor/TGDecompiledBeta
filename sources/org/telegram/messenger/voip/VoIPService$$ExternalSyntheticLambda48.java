package org.telegram.messenger.voip;

import org.telegram.tgnet.TLObject;
import org.telegram.tgnet.TLRPC$TL_error;
import org.telegram.tgnet.TLRPC$TL_phone_checkGroupCall;

public final /* synthetic */ class VoIPService$$ExternalSyntheticLambda48 implements Runnable {
    public final /* synthetic */ VoIPService f$0;
    public final /* synthetic */ TLObject f$1;
    public final /* synthetic */ TLRPC$TL_phone_checkGroupCall f$2;
    public final /* synthetic */ TLRPC$TL_error f$3;

    public /* synthetic */ VoIPService$$ExternalSyntheticLambda48(VoIPService voIPService, TLObject tLObject, TLRPC$TL_phone_checkGroupCall tLRPC$TL_phone_checkGroupCall, TLRPC$TL_error tLRPC$TL_error) {
        this.f$0 = voIPService;
        this.f$1 = tLObject;
        this.f$2 = tLRPC$TL_phone_checkGroupCall;
        this.f$3 = tLRPC$TL_error;
    }

    public final void run() {
        this.f$0.lambda$startGroupCheckShortpoll$33(this.f$1, this.f$2, this.f$3);
    }
}
