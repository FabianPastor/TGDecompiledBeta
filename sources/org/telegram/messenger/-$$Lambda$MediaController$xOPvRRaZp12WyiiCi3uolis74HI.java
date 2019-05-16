package org.telegram.messenger;

import java.io.File;
import org.telegram.tgnet.TLRPC.TL_document;

/* compiled from: lambda */
public final /* synthetic */ class -$$Lambda$MediaController$xOPvRRaZp12WyiiCi3uolis74HI implements Runnable {
    private final /* synthetic */ MediaController f$0;
    private final /* synthetic */ TL_document f$1;
    private final /* synthetic */ File f$2;
    private final /* synthetic */ int f$3;

    public /* synthetic */ -$$Lambda$MediaController$xOPvRRaZp12WyiiCi3uolis74HI(MediaController mediaController, TL_document tL_document, File file, int i) {
        this.f$0 = mediaController;
        this.f$1 = tL_document;
        this.f$2 = file;
        this.f$3 = i;
    }

    public final void run() {
        this.f$0.lambda$stopRecordingInternal$20$MediaController(this.f$1, this.f$2, this.f$3);
    }
}
