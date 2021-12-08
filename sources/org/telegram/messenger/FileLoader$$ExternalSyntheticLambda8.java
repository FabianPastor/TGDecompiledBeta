package org.telegram.messenger;

import org.telegram.tgnet.TLRPC;

public final /* synthetic */ class FileLoader$$ExternalSyntheticLambda8 implements Runnable {
    public final /* synthetic */ FileLoader f$0;
    public final /* synthetic */ TLRPC.FileLocation f$1;
    public final /* synthetic */ String f$2;

    public /* synthetic */ FileLoader$$ExternalSyntheticLambda8(FileLoader fileLoader, TLRPC.FileLocation fileLocation, String str) {
        this.f$0 = fileLoader;
        this.f$1 = fileLocation;
        this.f$2 = str;
    }

    public final void run() {
        this.f$0.m639xbvar_c(this.f$1, this.f$2);
    }
}
