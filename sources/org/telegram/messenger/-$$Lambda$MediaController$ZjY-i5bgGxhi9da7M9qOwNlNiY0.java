package org.telegram.messenger;

import java.io.File;
import org.telegram.tgnet.TLRPC.TL_document;

/* compiled from: lambda */
public final /* synthetic */ class -$$Lambda$MediaController$ZjY-i5bgGxhi9da7M9qOwNlNiY0 implements Runnable {
    private final /* synthetic */ MediaController f$0;
    private final /* synthetic */ TL_document f$1;
    private final /* synthetic */ File f$2;
    private final /* synthetic */ int f$3;
    private final /* synthetic */ boolean f$4;
    private final /* synthetic */ int f$5;

    public /* synthetic */ -$$Lambda$MediaController$ZjY-i5bgGxhi9da7M9qOwNlNiY0(MediaController mediaController, TL_document tL_document, File file, int i, boolean z, int i2) {
        this.f$0 = mediaController;
        this.f$1 = tL_document;
        this.f$2 = file;
        this.f$3 = i;
        this.f$4 = z;
        this.f$5 = i2;
    }

    public final void run() {
        this.f$0.lambda$null$20$MediaController(this.f$1, this.f$2, this.f$3, this.f$4, this.f$5);
    }
}
