package org.telegram.messenger;

import org.telegram.tgnet.TLRPC.Document;
import org.telegram.tgnet.TLRPC.FileLocation;

/* compiled from: lambda */
public final /* synthetic */ class -$$Lambda$FileLoader$zxmFDKJZWcEsmPLtOCvxbxeqvMw implements Runnable {
    private final /* synthetic */ FileLoader f$0;
    private final /* synthetic */ int f$1;
    private final /* synthetic */ String f$2;
    private final /* synthetic */ Document f$3;
    private final /* synthetic */ WebFile f$4;
    private final /* synthetic */ FileLocation f$5;

    public /* synthetic */ -$$Lambda$FileLoader$zxmFDKJZWcEsmPLtOCvxbxeqvMw(FileLoader fileLoader, int i, String str, Document document, WebFile webFile, FileLocation fileLocation) {
        this.f$0 = fileLoader;
        this.f$1 = i;
        this.f$2 = str;
        this.f$3 = document;
        this.f$4 = webFile;
        this.f$5 = fileLocation;
    }

    public final void run() {
        this.f$0.lambda$checkDownloadQueue$9$FileLoader(this.f$1, this.f$2, this.f$3, this.f$4, this.f$5);
    }
}
