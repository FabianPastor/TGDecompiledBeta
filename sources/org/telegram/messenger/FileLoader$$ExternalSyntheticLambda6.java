package org.telegram.messenger;

import org.telegram.tgnet.TLRPC$FileLocation;

public final /* synthetic */ class FileLoader$$ExternalSyntheticLambda6 implements Runnable {
    public final /* synthetic */ FileLoader f$0;
    public final /* synthetic */ TLRPC$FileLocation f$1;
    public final /* synthetic */ String f$2;

    public /* synthetic */ FileLoader$$ExternalSyntheticLambda6(FileLoader fileLoader, TLRPC$FileLocation tLRPC$FileLocation, String str) {
        this.f$0 = fileLoader;
        this.f$1 = tLRPC$FileLocation;
        this.f$2 = str;
    }

    public final void run() {
        this.f$0.lambda$setForceStreamLoadingFile$6(this.f$1, this.f$2);
    }
}
