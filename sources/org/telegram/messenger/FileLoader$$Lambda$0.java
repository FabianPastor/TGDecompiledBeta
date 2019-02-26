package org.telegram.messenger;

import org.telegram.tgnet.TLRPC.Document;

final /* synthetic */ class FileLoader$$Lambda$0 implements Runnable {
    private final FileLoader arg$1;
    private final Document arg$2;
    private final boolean arg$3;

    FileLoader$$Lambda$0(FileLoader fileLoader, Document document, boolean z) {
        this.arg$1 = fileLoader;
        this.arg$2 = document;
        this.arg$3 = z;
    }

    public void run() {
        this.arg$1.lambda$setLoadingVideo$0$FileLoader(this.arg$2, this.arg$3);
    }
}
