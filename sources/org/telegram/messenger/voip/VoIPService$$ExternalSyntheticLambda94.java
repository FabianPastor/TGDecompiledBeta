package org.telegram.messenger.voip;

import org.telegram.tgnet.RequestDelegate;
import org.telegram.tgnet.TLObject;
import org.telegram.tgnet.TLRPC$TL_error;
/* loaded from: classes.dex */
public final /* synthetic */ class VoIPService$$ExternalSyntheticLambda94 implements RequestDelegate {
    public static final /* synthetic */ VoIPService$$ExternalSyntheticLambda94 INSTANCE = new VoIPService$$ExternalSyntheticLambda94();

    private /* synthetic */ VoIPService$$ExternalSyntheticLambda94() {
    }

    @Override // org.telegram.tgnet.RequestDelegate
    public final void run(TLObject tLObject, TLRPC$TL_error tLRPC$TL_error) {
        VoIPService.lambda$callFailed$81(tLObject, tLRPC$TL_error);
    }
}
