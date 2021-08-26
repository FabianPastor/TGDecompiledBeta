package org.telegram.messenger;

import java.io.File;

public final /* synthetic */ class SharedConfig$$ExternalSyntheticLambda1 implements Runnable {
    public final /* synthetic */ int f$0;
    public final /* synthetic */ File f$1;

    public /* synthetic */ SharedConfig$$ExternalSyntheticLambda1(int i, File file) {
        this.f$0 = i;
        this.f$1 = file;
    }

    public final void run() {
        SharedConfig.lambda$checkKeepMedia$1(this.f$0, this.f$1);
    }
}
