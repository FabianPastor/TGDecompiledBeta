package org.telegram.messenger;

import org.telegram.tgnet.RequestDelegate;
import org.telegram.tgnet.TLObject;
import org.telegram.tgnet.TLRPC$TL_error;

public final /* synthetic */ class FileRefController$$ExternalSyntheticLambda32 implements RequestDelegate {
    public static final /* synthetic */ FileRefController$$ExternalSyntheticLambda32 INSTANCE = new FileRefController$$ExternalSyntheticLambda32();

    private /* synthetic */ FileRefController$$ExternalSyntheticLambda32() {
    }

    public final void run(TLObject tLObject, TLRPC$TL_error tLRPC$TL_error) {
        FileRefController.lambda$onUpdateObjectReference$26(tLObject, tLRPC$TL_error);
    }
}
