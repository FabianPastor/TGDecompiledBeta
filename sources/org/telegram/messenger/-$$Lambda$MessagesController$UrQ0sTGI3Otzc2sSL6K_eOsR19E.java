package org.telegram.messenger;

import java.io.File;
import org.telegram.tgnet.TLRPC.TL_wallPaper;
import org.telegram.tgnet.TLRPC.TL_wallPaperSettings;

/* compiled from: lambda */
public final /* synthetic */ class -$$Lambda$MessagesController$UrQ0sTGI3Otzc2sSL6K_eOsR19E implements Runnable {
    private final /* synthetic */ MessagesController f$0;
    private final /* synthetic */ TL_wallPaper f$1;
    private final /* synthetic */ TL_wallPaperSettings f$2;
    private final /* synthetic */ File f$3;

    public /* synthetic */ -$$Lambda$MessagesController$UrQ0sTGI3Otzc2sSL6K_eOsR19E(MessagesController messagesController, TL_wallPaper tL_wallPaper, TL_wallPaperSettings tL_wallPaperSettings, File file) {
        this.f$0 = messagesController;
        this.f$1 = tL_wallPaper;
        this.f$2 = tL_wallPaperSettings;
        this.f$3 = file;
    }

    public final void run() {
        this.f$0.lambda$null$9$MessagesController(this.f$1, this.f$2, this.f$3);
    }
}
