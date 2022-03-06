package org.telegram.messenger;

import org.telegram.tgnet.TLRPC$Document;
import org.telegram.tgnet.TLRPC$TL_fileLocationToBeDeprecated;

public final /* synthetic */ class FileLoader$$ExternalSyntheticLambda4 implements Runnable {
    public final /* synthetic */ FileLoader f$0;
    public final /* synthetic */ TLRPC$Document f$1;
    public final /* synthetic */ int f$10;
    public final /* synthetic */ SecureDocument f$2;
    public final /* synthetic */ WebFile f$3;
    public final /* synthetic */ TLRPC$TL_fileLocationToBeDeprecated f$4;
    public final /* synthetic */ ImageLocation f$5;
    public final /* synthetic */ Object f$6;
    public final /* synthetic */ String f$7;
    public final /* synthetic */ int f$8;
    public final /* synthetic */ int f$9;

    public /* synthetic */ FileLoader$$ExternalSyntheticLambda4(FileLoader fileLoader, TLRPC$Document tLRPC$Document, SecureDocument secureDocument, WebFile webFile, TLRPC$TL_fileLocationToBeDeprecated tLRPC$TL_fileLocationToBeDeprecated, ImageLocation imageLocation, Object obj, String str, int i, int i2, int i3) {
        this.f$0 = fileLoader;
        this.f$1 = tLRPC$Document;
        this.f$2 = secureDocument;
        this.f$3 = webFile;
        this.f$4 = tLRPC$TL_fileLocationToBeDeprecated;
        this.f$5 = imageLocation;
        this.f$6 = obj;
        this.f$7 = str;
        this.f$8 = i;
        this.f$9 = i2;
        this.f$10 = i3;
    }

    public final void run() {
        this.f$0.lambda$loadFile$9(this.f$1, this.f$2, this.f$3, this.f$4, this.f$5, this.f$6, this.f$7, this.f$8, this.f$9, this.f$10);
    }
}
