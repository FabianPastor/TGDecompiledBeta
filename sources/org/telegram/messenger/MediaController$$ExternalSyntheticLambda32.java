package org.telegram.messenger;

import java.io.File;
import org.telegram.tgnet.TLRPC$TL_document;

public final /* synthetic */ class MediaController$$ExternalSyntheticLambda32 implements Runnable {
    public final /* synthetic */ MediaController f$0;
    public final /* synthetic */ TLRPC$TL_document f$1;
    public final /* synthetic */ File f$2;
    public final /* synthetic */ int f$3;
    public final /* synthetic */ boolean f$4;
    public final /* synthetic */ int f$5;

    public /* synthetic */ MediaController$$ExternalSyntheticLambda32(MediaController mediaController, TLRPC$TL_document tLRPC$TL_document, File file, int i, boolean z, int i2) {
        this.f$0 = mediaController;
        this.f$1 = tLRPC$TL_document;
        this.f$2 = file;
        this.f$3 = i;
        this.f$4 = z;
        this.f$5 = i2;
    }

    public final void run() {
        this.f$0.lambda$stopRecordingInternal$29(this.f$1, this.f$2, this.f$3, this.f$4, this.f$5);
    }
}
