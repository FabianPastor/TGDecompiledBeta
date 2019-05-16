package org.telegram.messenger;

import org.telegram.tgnet.TLRPC.Document;
import org.telegram.tgnet.TLRPC.FileLocation;

/* compiled from: lambda */
public final /* synthetic */ class -$$Lambda$FileLoader$5pIYqCqDZLULELsU2WZGVaPNvhc implements Runnable {
    private final /* synthetic */ FileLoader f$0;
    private final /* synthetic */ String f$1;
    private final /* synthetic */ Document f$2;
    private final /* synthetic */ WebFile f$3;
    private final /* synthetic */ SecureDocument f$4;
    private final /* synthetic */ FileLocation f$5;

    public /* synthetic */ -$$Lambda$FileLoader$5pIYqCqDZLULELsU2WZGVaPNvhc(FileLoader fileLoader, String str, Document document, WebFile webFile, SecureDocument secureDocument, FileLocation fileLocation) {
        this.f$0 = fileLoader;
        this.f$1 = str;
        this.f$2 = document;
        this.f$3 = webFile;
        this.f$4 = secureDocument;
        this.f$5 = fileLocation;
    }

    public final void run() {
        this.f$0.lambda$cancelLoadFile$6$FileLoader(this.f$1, this.f$2, this.f$3, this.f$4, this.f$5);
    }
}
