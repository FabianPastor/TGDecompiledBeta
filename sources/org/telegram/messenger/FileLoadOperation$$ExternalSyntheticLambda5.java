package org.telegram.messenger;

import org.telegram.messenger.FileLoadOperation;
import org.telegram.tgnet.RequestDelegate;
import org.telegram.tgnet.TLObject;
import org.telegram.tgnet.TLRPC;

public final /* synthetic */ class FileLoadOperation$$ExternalSyntheticLambda5 implements RequestDelegate {
    public final /* synthetic */ FileLoadOperation f$0;
    public final /* synthetic */ FileLoadOperation.RequestInfo f$1;
    public final /* synthetic */ TLObject f$2;

    public /* synthetic */ FileLoadOperation$$ExternalSyntheticLambda5(FileLoadOperation fileLoadOperation, FileLoadOperation.RequestInfo requestInfo, TLObject tLObject) {
        this.f$0 = fileLoadOperation;
        this.f$1 = requestInfo;
        this.f$2 = tLObject;
    }

    public final void run(TLObject tLObject, TLRPC.TL_error tL_error) {
        this.f$0.m630xf2a564a8(this.f$1, this.f$2, tLObject, tL_error);
    }
}
