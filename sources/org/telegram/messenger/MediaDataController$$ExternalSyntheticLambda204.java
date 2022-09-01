package org.telegram.messenger;

import org.telegram.tgnet.RequestDelegate;
import org.telegram.tgnet.TLObject;
import org.telegram.tgnet.TLRPC$TL_error;

public final /* synthetic */ class MediaDataController$$ExternalSyntheticLambda204 implements RequestDelegate {
    public static final /* synthetic */ MediaDataController$$ExternalSyntheticLambda204 INSTANCE = new MediaDataController$$ExternalSyntheticLambda204();

    private /* synthetic */ MediaDataController$$ExternalSyntheticLambda204() {
    }

    public final void run(TLObject tLObject, TLRPC$TL_error tLRPC$TL_error) {
        MediaDataController.lambda$removePeer$132(tLObject, tLRPC$TL_error);
    }
}
