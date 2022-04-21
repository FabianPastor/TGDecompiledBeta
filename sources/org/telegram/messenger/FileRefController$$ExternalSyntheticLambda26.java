package org.telegram.messenger;

import org.telegram.tgnet.RequestDelegate;
import org.telegram.tgnet.TLObject;
import org.telegram.tgnet.TLRPC;

public final /* synthetic */ class FileRefController$$ExternalSyntheticLambda26 implements RequestDelegate {
    public static final /* synthetic */ FileRefController$$ExternalSyntheticLambda26 INSTANCE = new FileRefController$$ExternalSyntheticLambda26();

    private /* synthetic */ FileRefController$$ExternalSyntheticLambda26() {
    }

    public final void run(TLObject tLObject, TLRPC.TL_error tL_error) {
        FileRefController.lambda$onUpdateObjectReference$26(tLObject, tL_error);
    }
}
