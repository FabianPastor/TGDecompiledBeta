package org.telegram.messenger;

import org.telegram.messenger.FileLoadOperation;
import org.telegram.tgnet.RequestDelegate;
import org.telegram.tgnet.TLObject;
import org.telegram.tgnet.TLRPC;

public final /* synthetic */ class FileLoadOperation$$ExternalSyntheticLambda4 implements RequestDelegate {
    public final /* synthetic */ FileLoadOperation f$0;
    public final /* synthetic */ FileLoadOperation.RequestInfo f$1;

    public /* synthetic */ FileLoadOperation$$ExternalSyntheticLambda4(FileLoadOperation fileLoadOperation, FileLoadOperation.RequestInfo requestInfo) {
        this.f$0 = fileLoadOperation;
        this.f$1 = requestInfo;
    }

    public final void run(TLObject tLObject, TLRPC.TL_error tL_error) {
        this.f$0.m1814xeb402var_(this.f$1, tLObject, tL_error);
    }
}
