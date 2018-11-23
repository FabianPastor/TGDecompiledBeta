package org.telegram.messenger;

import org.telegram.tgnet.RequestDelegate;
import org.telegram.tgnet.TLObject;
import org.telegram.tgnet.TLRPC.TL_error;

final /* synthetic */ class FileLoadOperation$$Lambda$11 implements RequestDelegate {
    private final FileLoadOperation arg$1;
    private final RequestInfo arg$2;
    private final TLObject arg$3;

    FileLoadOperation$$Lambda$11(FileLoadOperation fileLoadOperation, RequestInfo requestInfo, TLObject tLObject) {
        this.arg$1 = fileLoadOperation;
        this.arg$2 = requestInfo;
        this.arg$3 = tLObject;
    }

    public void run(TLObject tLObject, TL_error tL_error) {
        this.arg$1.lambda$startDownloadRequest$11$FileLoadOperation(this.arg$2, this.arg$3, tLObject, tL_error);
    }
}
