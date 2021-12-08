package org.telegram.messenger;

import org.telegram.tgnet.RequestDelegate;
import org.telegram.tgnet.TLObject;
import org.telegram.tgnet.TLRPC;

public final /* synthetic */ class FileRefController$$ExternalSyntheticLambda25 implements RequestDelegate {
    public static final /* synthetic */ FileRefController$$ExternalSyntheticLambda25 INSTANCE = new FileRefController$$ExternalSyntheticLambda25();

    private /* synthetic */ FileRefController$$ExternalSyntheticLambda25() {
    }

    public final void run(TLObject tLObject, TLRPC.TL_error tL_error) {
        FileRefController.lambda$onUpdateObjectReference$25(tLObject, tL_error);
    }
}
