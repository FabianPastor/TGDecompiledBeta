package org.telegram.messenger;

import org.telegram.tgnet.TLRPC.TL_messages_stickerSet;

final /* synthetic */ class FileRefController$$Lambda$23 implements Runnable {
    private final FileRefController arg$1;
    private final TL_messages_stickerSet arg$2;

    FileRefController$$Lambda$23(FileRefController fileRefController, TL_messages_stickerSet tL_messages_stickerSet) {
        this.arg$1 = fileRefController;
        this.arg$2 = tL_messages_stickerSet;
    }

    public void run() {
        this.arg$1.lambda$onRequestComplete$23$FileRefController(this.arg$2);
    }
}
