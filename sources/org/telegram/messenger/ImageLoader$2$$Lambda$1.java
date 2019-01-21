package org.telegram.messenger;

import org.telegram.messenger.ImageLoader.AnonymousClass2;
import org.telegram.tgnet.TLRPC.InputEncryptedFile;
import org.telegram.tgnet.TLRPC.InputFile;

final /* synthetic */ class ImageLoader$2$$Lambda$1 implements Runnable {
    private final AnonymousClass2 arg$1;
    private final int arg$2;
    private final String arg$3;
    private final InputFile arg$4;
    private final InputEncryptedFile arg$5;
    private final byte[] arg$6;
    private final byte[] arg$7;
    private final long arg$8;

    ImageLoader$2$$Lambda$1(AnonymousClass2 anonymousClass2, int i, String str, InputFile inputFile, InputEncryptedFile inputEncryptedFile, byte[] bArr, byte[] bArr2, long j) {
        this.arg$1 = anonymousClass2;
        this.arg$2 = i;
        this.arg$3 = str;
        this.arg$4 = inputFile;
        this.arg$5 = inputEncryptedFile;
        this.arg$6 = bArr;
        this.arg$7 = bArr2;
        this.arg$8 = j;
    }

    public void run() {
        this.arg$1.lambda$fileDidUploaded$2$ImageLoader$2(this.arg$2, this.arg$3, this.arg$4, this.arg$5, this.arg$6, this.arg$7, this.arg$8);
    }
}
