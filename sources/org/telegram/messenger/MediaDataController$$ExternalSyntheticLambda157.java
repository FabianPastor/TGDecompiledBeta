package org.telegram.messenger;

import org.telegram.tgnet.RequestDelegate;
import org.telegram.tgnet.TLObject;
import org.telegram.tgnet.TLRPC$TL_error;

public final /* synthetic */ class MediaDataController$$ExternalSyntheticLambda157 implements RequestDelegate {
    public static final /* synthetic */ MediaDataController$$ExternalSyntheticLambda157 INSTANCE = new MediaDataController$$ExternalSyntheticLambda157();

    private /* synthetic */ MediaDataController$$ExternalSyntheticLambda157() {
    }

    public final void run(TLObject tLObject, TLRPC$TL_error tLRPC$TL_error) {
        MediaDataController.lambda$removePeer$101(tLObject, tLRPC$TL_error);
    }
}