package org.telegram.messenger;

import org.telegram.tgnet.RequestDelegate;
import org.telegram.tgnet.TLObject;
import org.telegram.tgnet.TLRPC.TL_error;

final /* synthetic */ class FileLoadOperation$$Lambda$9 implements RequestDelegate {
    private final FileLoadOperation arg$1;

    FileLoadOperation$$Lambda$9(FileLoadOperation fileLoadOperation) {
        this.arg$1 = fileLoadOperation;
    }

    public void run(TLObject tLObject, TL_error tL_error) {
        this.arg$1.lambda$requestFileOffsets$8$FileLoadOperation(tLObject, tL_error);
    }
}
