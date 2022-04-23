package org.telegram.messenger;

public final /* synthetic */ class DownloadController$$ExternalSyntheticLambda10 implements Runnable {
    public final /* synthetic */ DownloadController f$0;
    public final /* synthetic */ MessageObject f$1;
    public final /* synthetic */ int f$2;

    public /* synthetic */ DownloadController$$ExternalSyntheticLambda10(DownloadController downloadController, MessageObject messageObject, int i) {
        this.f$0 = downloadController;
        this.f$1 = messageObject;
        this.f$2 = i;
    }

    public final void run() {
        this.f$0.lambda$onDownloadFail$8(this.f$1, this.f$2);
    }
}
