package org.telegram.messenger;

import org.telegram.messenger.FileLoadOperation;
import org.telegram.tgnet.RequestDelegate;
import org.telegram.tgnet.TLObject;
import org.telegram.tgnet.TLRPC$TL_error;

public final /* synthetic */ class FileLoadOperation$$ExternalSyntheticLambda13 implements RequestDelegate {
    public final /* synthetic */ FileLoadOperation f$0;
    public final /* synthetic */ FileLoadOperation.RequestInfo f$1;

    public /* synthetic */ FileLoadOperation$$ExternalSyntheticLambda13(FileLoadOperation fileLoadOperation, FileLoadOperation.RequestInfo requestInfo) {
        this.f$0 = fileLoadOperation;
        this.f$1 = requestInfo;
    }

    public final void run(TLObject tLObject, TLRPC$TL_error tLRPC$TL_error) {
        this.f$0.lambda$startDownloadRequest$12(this.f$1, tLObject, tLRPC$TL_error);
    }
}
