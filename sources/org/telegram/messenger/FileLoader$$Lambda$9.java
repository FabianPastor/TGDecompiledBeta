package org.telegram.messenger;

import org.telegram.tgnet.TLRPC.Document;
import org.telegram.tgnet.TLRPC.FileLocation;

final /* synthetic */ class FileLoader$$Lambda$9 implements Runnable {
    private final FileLoader arg$1;
    private final int arg$2;
    private final String arg$3;
    private final Document arg$4;
    private final WebFile arg$5;
    private final FileLocation arg$6;

    FileLoader$$Lambda$9(FileLoader fileLoader, int i, String str, Document document, WebFile webFile, FileLocation fileLocation) {
        this.arg$1 = fileLoader;
        this.arg$2 = i;
        this.arg$3 = str;
        this.arg$4 = document;
        this.arg$5 = webFile;
        this.arg$6 = fileLocation;
    }

    public void run() {
        this.arg$1.lambda$checkDownloadQueue$9$FileLoader(this.arg$2, this.arg$3, this.arg$4, this.arg$5, this.arg$6);
    }
}
