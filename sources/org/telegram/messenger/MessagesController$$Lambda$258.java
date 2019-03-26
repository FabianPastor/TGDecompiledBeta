package org.telegram.messenger;

import java.io.File;
import org.telegram.tgnet.TLRPC.TL_wallPaper;
import org.telegram.tgnet.TLRPC.TL_wallPaperSettings;

final /* synthetic */ class MessagesController$$Lambda$258 implements Runnable {
    private final MessagesController arg$1;
    private final TL_wallPaper arg$2;
    private final TL_wallPaperSettings arg$3;
    private final File arg$4;

    MessagesController$$Lambda$258(MessagesController messagesController, TL_wallPaper tL_wallPaper, TL_wallPaperSettings tL_wallPaperSettings, File file) {
        this.arg$1 = messagesController;
        this.arg$2 = tL_wallPaper;
        this.arg$3 = tL_wallPaperSettings;
        this.arg$4 = file;
    }

    public void run() {
        this.arg$1.lambda$null$5$MessagesController(this.arg$2, this.arg$3, this.arg$4);
    }
}
