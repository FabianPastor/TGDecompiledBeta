package org.telegram.messenger;

import org.telegram.tgnet.RequestDelegate;
import org.telegram.tgnet.TLObject;
import org.telegram.tgnet.TLRPC$TL_error;

public final /* synthetic */ class FileRefController$$ExternalSyntheticLambda33 implements RequestDelegate {
    public static final /* synthetic */ FileRefController$$ExternalSyntheticLambda33 INSTANCE = new FileRefController$$ExternalSyntheticLambda33();

    private /* synthetic */ FileRefController$$ExternalSyntheticLambda33() {
    }

    public final void run(TLObject tLObject, TLRPC$TL_error tLRPC$TL_error) {
        FileRefController.lambda$onUpdateObjectReference$25(tLObject, tLRPC$TL_error);
    }
}
