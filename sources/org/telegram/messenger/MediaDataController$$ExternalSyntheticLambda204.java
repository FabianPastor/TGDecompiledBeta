package org.telegram.messenger;

import org.telegram.tgnet.RequestDelegate;
import org.telegram.tgnet.TLObject;
import org.telegram.tgnet.TLRPC$TL_error;
/* loaded from: classes.dex */
public final /* synthetic */ class MediaDataController$$ExternalSyntheticLambda204 implements RequestDelegate {
    public static final /* synthetic */ MediaDataController$$ExternalSyntheticLambda204 INSTANCE = new MediaDataController$$ExternalSyntheticLambda204();

    private /* synthetic */ MediaDataController$$ExternalSyntheticLambda204() {
    }

    @Override // org.telegram.tgnet.RequestDelegate
    public final void run(TLObject tLObject, TLRPC$TL_error tLRPC$TL_error) {
        MediaDataController.lambda$removeInline$131(tLObject, tLRPC$TL_error);
    }
}
