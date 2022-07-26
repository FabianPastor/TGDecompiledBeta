package org.telegram.messenger;

import org.telegram.tgnet.TLRPC$Document;

public final /* synthetic */ class DownloadController$$ExternalSyntheticLambda11 implements Runnable {
    public final /* synthetic */ DownloadController f$0;
    public final /* synthetic */ TLRPC$Document f$1;
    public final /* synthetic */ MessageObject f$2;

    public /* synthetic */ DownloadController$$ExternalSyntheticLambda11(DownloadController downloadController, TLRPC$Document tLRPC$Document, MessageObject messageObject) {
        this.f$0 = downloadController;
        this.f$1 = tLRPC$Document;
        this.f$2 = messageObject;
    }

    public final void run() {
        this.f$0.lambda$onDownloadComplete$7(this.f$1, this.f$2);
    }
}
