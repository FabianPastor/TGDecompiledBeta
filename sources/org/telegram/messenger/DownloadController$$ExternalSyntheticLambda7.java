package org.telegram.messenger;

public final /* synthetic */ class DownloadController$$ExternalSyntheticLambda7 implements Runnable {
    public final /* synthetic */ DownloadController f$0;
    public final /* synthetic */ MessageObject f$1;

    public /* synthetic */ DownloadController$$ExternalSyntheticLambda7(DownloadController downloadController, MessageObject messageObject) {
        this.f$0 = downloadController;
        this.f$1 = messageObject;
    }

    public final void run() {
        this.f$0.lambda$onDownloadFail$9(this.f$1);
    }
}
