package org.telegram.messenger;

import org.telegram.tgnet.RequestDelegate;
import org.telegram.tgnet.TLObject;
import org.telegram.tgnet.TLRPC.TL_error;

final /* synthetic */ class FileRefController$$Lambda$11 implements RequestDelegate {
    private final FileRefController arg$1;
    private final String arg$2;
    private final String arg$3;

    FileRefController$$Lambda$11(FileRefController fileRefController, String str, String str2) {
        this.arg$1 = fileRefController;
        this.arg$2 = str;
        this.arg$3 = str2;
    }

    public void run(TLObject tLObject, TL_error tL_error) {
        this.arg$1.lambda$requestReferenceFromServer$11$FileRefController(this.arg$2, this.arg$3, tLObject, tL_error);
    }
}
