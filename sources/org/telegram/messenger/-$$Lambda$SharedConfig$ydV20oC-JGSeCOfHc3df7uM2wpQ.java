package org.telegram.messenger;

import java.io.File;

/* compiled from: lambda */
public final /* synthetic */ class -$$Lambda$SharedConfig$ydV20oC-JGSeCOfHc3df7uM2wpQ implements Runnable {
    private final /* synthetic */ int f$0;
    private final /* synthetic */ File f$1;

    public /* synthetic */ -$$Lambda$SharedConfig$ydV20oC-JGSeCOfHc3df7uM2wpQ(int i, File file) {
        this.f$0 = i;
        this.f$1 = file;
    }

    public final void run() {
        SharedConfig.lambda$checkKeepMedia$0(this.f$0, this.f$1);
    }
}
