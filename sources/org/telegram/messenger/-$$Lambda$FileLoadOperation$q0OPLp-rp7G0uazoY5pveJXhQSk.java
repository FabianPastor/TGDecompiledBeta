package org.telegram.messenger;

import org.telegram.tgnet.RequestDelegate;
import org.telegram.tgnet.TLObject;
import org.telegram.tgnet.TLRPC.TL_error;

/* compiled from: lambda */
public final /* synthetic */ class -$$Lambda$FileLoadOperation$q0OPLp-rp7G0uazoY5pveJXhQSk implements RequestDelegate {
    private final /* synthetic */ FileLoadOperation f$0;
    private final /* synthetic */ RequestInfo f$1;
    private final /* synthetic */ TLObject f$2;

    public /* synthetic */ -$$Lambda$FileLoadOperation$q0OPLp-rp7G0uazoY5pveJXhQSk(FileLoadOperation fileLoadOperation, RequestInfo requestInfo, TLObject tLObject) {
        this.f$0 = fileLoadOperation;
        this.f$1 = requestInfo;
        this.f$2 = tLObject;
    }

    public final void run(TLObject tLObject, TL_error tL_error) {
        this.f$0.lambda$startDownloadRequest$13$FileLoadOperation(this.f$1, this.f$2, tLObject, tL_error);
    }
}