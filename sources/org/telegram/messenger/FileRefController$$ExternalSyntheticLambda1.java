package org.telegram.messenger;

import org.telegram.messenger.FileRefController;

public final /* synthetic */ class FileRefController$$ExternalSyntheticLambda1 implements Runnable {
    public final /* synthetic */ FileRefController f$0;
    public final /* synthetic */ FileRefController.Requester f$1;

    public /* synthetic */ FileRefController$$ExternalSyntheticLambda1(FileRefController fileRefController, FileRefController.Requester requester) {
        this.f$0 = fileRefController;
        this.f$1 = requester;
    }

    public final void run() {
        this.f$0.lambda$onUpdateObjectReference$26(this.f$1);
    }
}
