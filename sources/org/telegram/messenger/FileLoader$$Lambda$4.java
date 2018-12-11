package org.telegram.messenger;

import org.telegram.tgnet.TLRPC.Document;
import org.telegram.tgnet.TLRPC.FileLocation;

final /* synthetic */ class FileLoader$$Lambda$4 implements Runnable {
    private final FileLoader arg$1;
    private final String arg$2;
    private final Document arg$3;
    private final WebFile arg$4;
    private final SecureDocument arg$5;
    private final FileLocation arg$6;

    FileLoader$$Lambda$4(FileLoader fileLoader, String str, Document document, WebFile webFile, SecureDocument secureDocument, FileLocation fileLocation) {
        this.arg$1 = fileLoader;
        this.arg$2 = str;
        this.arg$3 = document;
        this.arg$4 = webFile;
        this.arg$5 = secureDocument;
        this.arg$6 = fileLocation;
    }

    public void run() {
        this.arg$1.lambda$cancelLoadFile$4$FileLoader(this.arg$2, this.arg$3, this.arg$4, this.arg$5, this.arg$6);
    }
}
