package org.telegram.messenger;

import org.telegram.tgnet.RequestDelegate;
import org.telegram.tgnet.TLObject;
import org.telegram.tgnet.TLRPC.TL_error;

final /* synthetic */ class FileLoadOperation$$Lambda$13 implements RequestDelegate {
    private final FileLoadOperation arg$1;
    private final RequestInfo arg$2;

    FileLoadOperation$$Lambda$13(FileLoadOperation fileLoadOperation, RequestInfo requestInfo) {
        this.arg$1 = fileLoadOperation;
        this.arg$2 = requestInfo;
    }

    public void run(TLObject tLObject, TL_error tL_error) {
        this.arg$1.lambda$null$11$FileLoadOperation(this.arg$2, tLObject, tL_error);
    }
}
