package org.telegram.messenger.voip;

import org.telegram.tgnet.RequestDelegate;
import org.telegram.tgnet.TLObject;
import org.telegram.tgnet.TLRPC$TL_error;
/* loaded from: classes.dex */
public final /* synthetic */ class VoIPService$$ExternalSyntheticLambda97 implements RequestDelegate {
    public static final /* synthetic */ VoIPService$$ExternalSyntheticLambda97 INSTANCE = new VoIPService$$ExternalSyntheticLambda97();

    private /* synthetic */ VoIPService$$ExternalSyntheticLambda97() {
    }

    @Override // org.telegram.tgnet.RequestDelegate
    public final void run(TLObject tLObject, TLRPC$TL_error tLRPC$TL_error) {
        VoIPService.lambda$createGroupInstance$37(tLObject, tLRPC$TL_error);
    }
}
