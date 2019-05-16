package org.telegram.messenger;

import org.telegram.tgnet.TLRPC.Document;
import org.telegram.tgnet.TLRPC.TL_fileLocationToBeDeprecated;

/* compiled from: lambda */
public final /* synthetic */ class -$$Lambda$FileLoader$QveLJ1Gqcvj_9l-dGSaDY1G3t6s implements Runnable {
    private final /* synthetic */ FileLoader f$0;
    private final /* synthetic */ Document f$1;
    private final /* synthetic */ int f$10;
    private final /* synthetic */ SecureDocument f$2;
    private final /* synthetic */ WebFile f$3;
    private final /* synthetic */ TL_fileLocationToBeDeprecated f$4;
    private final /* synthetic */ ImageLocation f$5;
    private final /* synthetic */ Object f$6;
    private final /* synthetic */ String f$7;
    private final /* synthetic */ int f$8;
    private final /* synthetic */ int f$9;

    public /* synthetic */ -$$Lambda$FileLoader$QveLJ1Gqcvj_9l-dGSaDY1G3t6s(FileLoader fileLoader, Document document, SecureDocument secureDocument, WebFile webFile, TL_fileLocationToBeDeprecated tL_fileLocationToBeDeprecated, ImageLocation imageLocation, Object obj, String str, int i, int i2, int i3) {
        this.f$0 = fileLoader;
        this.f$1 = document;
        this.f$2 = secureDocument;
        this.f$3 = webFile;
        this.f$4 = tL_fileLocationToBeDeprecated;
        this.f$5 = imageLocation;
        this.f$6 = obj;
        this.f$7 = str;
        this.f$8 = i;
        this.f$9 = i2;
        this.f$10 = i3;
    }

    public final void run() {
        this.f$0.lambda$loadFile$7$FileLoader(this.f$1, this.f$2, this.f$3, this.f$4, this.f$5, this.f$6, this.f$7, this.f$8, this.f$9, this.f$10);
    }
}
