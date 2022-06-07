package org.telegram.messenger;

import org.telegram.tgnet.RequestDelegate;
import org.telegram.tgnet.TLObject;
import org.telegram.tgnet.TLRPC$TL_error;

public final /* synthetic */ class FileRefController$$ExternalSyntheticLambda28 implements RequestDelegate {
    public final /* synthetic */ FileRefController f$0;
    public final /* synthetic */ String f$1;
    public final /* synthetic */ String f$2;

    public /* synthetic */ FileRefController$$ExternalSyntheticLambda28(FileRefController fileRefController, String str, String str2) {
        this.f$0 = fileRefController;
        this.f$1 = str;
        this.f$2 = str2;
    }

    public final void run(TLObject tLObject, TLRPC$TL_error tLRPC$TL_error) {
        this.f$0.lambda$requestReferenceFromServer$3(this.f$1, this.f$2, tLObject, tLRPC$TL_error);
    }
}
