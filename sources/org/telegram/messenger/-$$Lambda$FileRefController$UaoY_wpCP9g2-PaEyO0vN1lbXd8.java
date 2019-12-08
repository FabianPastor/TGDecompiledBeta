package org.telegram.messenger;

import org.telegram.tgnet.TLRPC.TL_messages_stickerSet;

/* compiled from: lambda */
public final /* synthetic */ class -$$Lambda$FileRefController$UaoY_wpCP9g2-PaEyO0vN1lbXd8 implements Runnable {
    private final /* synthetic */ FileRefController f$0;
    private final /* synthetic */ TL_messages_stickerSet f$1;

    public /* synthetic */ -$$Lambda$FileRefController$UaoY_wpCP9g2-PaEyO0vN1lbXd8(FileRefController fileRefController, TL_messages_stickerSet tL_messages_stickerSet) {
        this.f$0 = fileRefController;
        this.f$1 = tL_messages_stickerSet;
    }

    public final void run() {
        this.f$0.lambda$onRequestComplete$29$FileRefController(this.f$1);
    }
}
