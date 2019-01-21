package org.telegram.messenger;

import org.telegram.tgnet.TLRPC.User;

final /* synthetic */ class FileRefController$$Lambda$21 implements Runnable {
    private final FileRefController arg$1;
    private final User arg$2;

    FileRefController$$Lambda$21(FileRefController fileRefController, User user) {
        this.arg$1 = fileRefController;
        this.arg$2 = user;
    }

    public void run() {
        this.arg$1.lambda$onRequestComplete$21$FileRefController(this.arg$2);
    }
}
