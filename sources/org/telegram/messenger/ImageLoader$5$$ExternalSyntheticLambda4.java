package org.telegram.messenger;

import org.telegram.messenger.ImageLoader;
import org.telegram.tgnet.TLRPC;

public final /* synthetic */ class ImageLoader$5$$ExternalSyntheticLambda4 implements Runnable {
    public final /* synthetic */ ImageLoader.AnonymousClass5 f$0;
    public final /* synthetic */ int f$1;
    public final /* synthetic */ String f$2;
    public final /* synthetic */ TLRPC.InputFile f$3;
    public final /* synthetic */ TLRPC.InputEncryptedFile f$4;
    public final /* synthetic */ byte[] f$5;
    public final /* synthetic */ byte[] f$6;
    public final /* synthetic */ long f$7;

    public /* synthetic */ ImageLoader$5$$ExternalSyntheticLambda4(ImageLoader.AnonymousClass5 r1, int i, String str, TLRPC.InputFile inputFile, TLRPC.InputEncryptedFile inputEncryptedFile, byte[] bArr, byte[] bArr2, long j) {
        this.f$0 = r1;
        this.f$1 = i;
        this.f$2 = str;
        this.f$3 = inputFile;
        this.f$4 = inputEncryptedFile;
        this.f$5 = bArr;
        this.f$6 = bArr2;
        this.f$7 = j;
    }

    public final void run() {
        this.f$0.m1893lambda$fileDidUploaded$2$orgtelegrammessengerImageLoader$5(this.f$1, this.f$2, this.f$3, this.f$4, this.f$5, this.f$6, this.f$7);
    }
}
