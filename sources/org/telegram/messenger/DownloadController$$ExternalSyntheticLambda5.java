package org.telegram.messenger;

public final /* synthetic */ class DownloadController$$ExternalSyntheticLambda5 implements Runnable {
    public final /* synthetic */ DownloadController f$0;
    public final /* synthetic */ MessageObject f$1;

    public /* synthetic */ DownloadController$$ExternalSyntheticLambda5(DownloadController downloadController, MessageObject messageObject) {
        this.f$0 = downloadController;
        this.f$1 = messageObject;
    }

    public final void run() {
        this.f$0.lambda$onDownloadComplete$7(this.f$1);
    }
}
