package org.telegram.messenger;

import org.telegram.tgnet.TLRPC.Document;
import org.telegram.tgnet.TLRPC.FileLocation;

final /* synthetic */ class FileLoader$$Lambda$5 implements Runnable {
    private final FileLoader arg$1;
    private final int arg$10;
    private final Document arg$2;
    private final SecureDocument arg$3;
    private final WebFile arg$4;
    private final FileLocation arg$5;
    private final Object arg$6;
    private final String arg$7;
    private final int arg$8;
    private final int arg$9;

    FileLoader$$Lambda$5(FileLoader fileLoader, Document document, SecureDocument secureDocument, WebFile webFile, FileLocation fileLocation, Object obj, String str, int i, int i2, int i3) {
        this.arg$1 = fileLoader;
        this.arg$2 = document;
        this.arg$3 = secureDocument;
        this.arg$4 = webFile;
        this.arg$5 = fileLocation;
        this.arg$6 = obj;
        this.arg$7 = str;
        this.arg$8 = i;
        this.arg$9 = i2;
        this.arg$10 = i3;
    }

    public void run() {
        this.arg$1.lambda$loadFile$5$FileLoader(this.arg$2, this.arg$3, this.arg$4, this.arg$5, this.arg$6, this.arg$7, this.arg$8, this.arg$9, this.arg$10);
    }
}
