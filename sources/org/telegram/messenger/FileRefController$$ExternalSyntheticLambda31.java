package org.telegram.messenger;

import org.telegram.tgnet.RequestDelegate;
import org.telegram.tgnet.TLObject;
import org.telegram.tgnet.TLRPC$TL_error;

public final /* synthetic */ class FileRefController$$ExternalSyntheticLambda31 implements RequestDelegate {
    public static final /* synthetic */ FileRefController$$ExternalSyntheticLambda31 INSTANCE = new FileRefController$$ExternalSyntheticLambda31();

    private /* synthetic */ FileRefController$$ExternalSyntheticLambda31() {
    }

    public final void run(TLObject tLObject, TLRPC$TL_error tLRPC$TL_error) {
        FileRefController.lambda$onUpdateObjectReference$24(tLObject, tLRPC$TL_error);
    }
}
