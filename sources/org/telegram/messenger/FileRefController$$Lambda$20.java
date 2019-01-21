package org.telegram.messenger;

import org.telegram.tgnet.RequestDelegate;
import org.telegram.tgnet.TLObject;
import org.telegram.tgnet.TLRPC.TL_error;

final /* synthetic */ class FileRefController$$Lambda$20 implements RequestDelegate {
    static final RequestDelegate $instance = new FileRefController$$Lambda$20();

    private FileRefController$$Lambda$20() {
    }

    public void run(TLObject tLObject, TL_error tL_error) {
        FileRefController.lambda$onUpdateObjectReference$20$FileRefController(tLObject, tL_error);
    }
}
