package org.telegram.messenger;

import org.telegram.tgnet.RequestDelegate;
import org.telegram.tgnet.TLObject;
import org.telegram.tgnet.TLRPC$TL_error;

public final /* synthetic */ class MediaDataController$$ExternalSyntheticLambda184 implements RequestDelegate {
    public static final /* synthetic */ MediaDataController$$ExternalSyntheticLambda184 INSTANCE = new MediaDataController$$ExternalSyntheticLambda184();

    private /* synthetic */ MediaDataController$$ExternalSyntheticLambda184() {
    }

    public final void run(TLObject tLObject, TLRPC$TL_error tLRPC$TL_error) {
        MediaDataController.lambda$removePeer$118(tLObject, tLRPC$TL_error);
    }
}
