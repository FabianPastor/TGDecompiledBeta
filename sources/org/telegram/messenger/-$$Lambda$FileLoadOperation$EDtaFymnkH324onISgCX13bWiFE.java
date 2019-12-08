package org.telegram.messenger;

import org.telegram.tgnet.RequestDelegate;
import org.telegram.tgnet.TLObject;
import org.telegram.tgnet.TLRPC.TL_error;

/* compiled from: lambda */
public final /* synthetic */ class -$$Lambda$FileLoadOperation$EDtaFymnkH324onISgCX13bWiFE implements RequestDelegate {
    private final /* synthetic */ FileLoadOperation f$0;
    private final /* synthetic */ RequestInfo f$1;

    public /* synthetic */ -$$Lambda$FileLoadOperation$EDtaFymnkH324onISgCX13bWiFE(FileLoadOperation fileLoadOperation, RequestInfo requestInfo) {
        this.f$0 = fileLoadOperation;
        this.f$1 = requestInfo;
    }

    public final void run(TLObject tLObject, TL_error tL_error) {
        this.f$0.lambda$null$12$FileLoadOperation(this.f$1, tLObject, tL_error);
    }
}
