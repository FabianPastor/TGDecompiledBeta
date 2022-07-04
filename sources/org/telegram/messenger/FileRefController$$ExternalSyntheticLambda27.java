package org.telegram.messenger;

import org.telegram.tgnet.RequestDelegate;
import org.telegram.tgnet.TLObject;
import org.telegram.tgnet.TLRPC;

public final /* synthetic */ class FileRefController$$ExternalSyntheticLambda27 implements RequestDelegate {
    public static final /* synthetic */ FileRefController$$ExternalSyntheticLambda27 INSTANCE = new FileRefController$$ExternalSyntheticLambda27();

    private /* synthetic */ FileRefController$$ExternalSyntheticLambda27() {
    }

    public final void run(TLObject tLObject, TLRPC.TL_error tL_error) {
        FileRefController.lambda$onUpdateObjectReference$27(tLObject, tL_error);
    }
}
