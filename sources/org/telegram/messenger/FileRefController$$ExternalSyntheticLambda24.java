package org.telegram.messenger;

import org.telegram.tgnet.RequestDelegate;
import org.telegram.tgnet.TLObject;
import org.telegram.tgnet.TLRPC;

public final /* synthetic */ class FileRefController$$ExternalSyntheticLambda24 implements RequestDelegate {
    public static final /* synthetic */ FileRefController$$ExternalSyntheticLambda24 INSTANCE = new FileRefController$$ExternalSyntheticLambda24();

    private /* synthetic */ FileRefController$$ExternalSyntheticLambda24() {
    }

    public final void run(TLObject tLObject, TLRPC.TL_error tL_error) {
        FileRefController.lambda$onUpdateObjectReference$24(tLObject, tL_error);
    }
}
