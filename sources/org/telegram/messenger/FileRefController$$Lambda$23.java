package org.telegram.messenger;

import org.telegram.tgnet.TLRPC.Chat;

final /* synthetic */ class FileRefController$$Lambda$23 implements Runnable {
    private final FileRefController arg$1;
    private final Chat arg$2;

    FileRefController$$Lambda$23(FileRefController fileRefController, Chat chat) {
        this.arg$1 = fileRefController;
        this.arg$2 = chat;
    }

    public void run() {
        this.arg$1.lambda$onRequestComplete$23$FileRefController(this.arg$2);
    }
}
