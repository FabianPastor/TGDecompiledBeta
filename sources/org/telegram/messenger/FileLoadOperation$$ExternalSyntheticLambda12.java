package org.telegram.messenger;

import org.telegram.tgnet.RequestDelegate;
import org.telegram.tgnet.TLObject;
import org.telegram.tgnet.TLRPC$TL_error;

public final /* synthetic */ class FileLoadOperation$$ExternalSyntheticLambda12 implements RequestDelegate {
    public final /* synthetic */ FileLoadOperation f$0;

    public /* synthetic */ FileLoadOperation$$ExternalSyntheticLambda12(FileLoadOperation fileLoadOperation) {
        this.f$0 = fileLoadOperation;
    }

    public final void run(TLObject tLObject, TLRPC$TL_error tLRPC$TL_error) {
        this.f$0.lambda$requestFileOffsets$9(tLObject, tLRPC$TL_error);
    }
}