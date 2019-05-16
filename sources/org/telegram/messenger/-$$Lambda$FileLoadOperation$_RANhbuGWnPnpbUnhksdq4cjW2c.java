package org.telegram.messenger;

import org.telegram.tgnet.RequestDelegate;
import org.telegram.tgnet.TLObject;
import org.telegram.tgnet.TLRPC.TL_error;

/* compiled from: lambda */
public final /* synthetic */ class -$$Lambda$FileLoadOperation$_RANhbuGWnPnpbUnhksdq4cjW2c implements RequestDelegate {
    private final /* synthetic */ FileLoadOperation f$0;
    private final /* synthetic */ RequestInfo f$1;

    public /* synthetic */ -$$Lambda$FileLoadOperation$_RANhbuGWnPnpbUnhksdq4cjW2c(FileLoadOperation fileLoadOperation, RequestInfo requestInfo) {
        this.f$0 = fileLoadOperation;
        this.f$1 = requestInfo;
    }

    public final void run(TLObject tLObject, TL_error tL_error) {
        this.f$0.lambda$null$11$FileLoadOperation(this.f$1, tLObject, tL_error);
    }
}
