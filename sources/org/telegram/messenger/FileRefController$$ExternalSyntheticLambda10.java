package org.telegram.messenger;

import org.telegram.tgnet.RequestDelegate;
import org.telegram.tgnet.TLObject;
import org.telegram.tgnet.TLRPC$TL_error;

public final /* synthetic */ class FileRefController$$ExternalSyntheticLambda10 implements RequestDelegate {
    public final /* synthetic */ FileRefController f$0;

    public /* synthetic */ FileRefController$$ExternalSyntheticLambda10(FileRefController fileRefController) {
        this.f$0 = fileRefController;
    }

    public final void run(TLObject tLObject, TLRPC$TL_error tLRPC$TL_error) {
        this.f$0.lambda$requestReferenceFromServer$9(tLObject, tLRPC$TL_error);
    }
}
