package org.telegram.messenger;

import org.telegram.messenger.ImageLoader.AnonymousClass2;
import org.telegram.tgnet.TLRPC.InputEncryptedFile;
import org.telegram.tgnet.TLRPC.InputFile;

/* compiled from: lambda */
public final /* synthetic */ class -$$Lambda$ImageLoader$2$OJOkl6dXzCVsFC1n7bftktzMxsM implements Runnable {
    private final /* synthetic */ AnonymousClass2 f$0;
    private final /* synthetic */ int f$1;
    private final /* synthetic */ String f$2;
    private final /* synthetic */ InputFile f$3;
    private final /* synthetic */ InputEncryptedFile f$4;
    private final /* synthetic */ byte[] f$5;
    private final /* synthetic */ byte[] f$6;
    private final /* synthetic */ long f$7;

    public /* synthetic */ -$$Lambda$ImageLoader$2$OJOkl6dXzCVsFC1n7bftktzMxsM(AnonymousClass2 anonymousClass2, int i, String str, InputFile inputFile, InputEncryptedFile inputEncryptedFile, byte[] bArr, byte[] bArr2, long j) {
        this.f$0 = anonymousClass2;
        this.f$1 = i;
        this.f$2 = str;
        this.f$3 = inputFile;
        this.f$4 = inputEncryptedFile;
        this.f$5 = bArr;
        this.f$6 = bArr2;
        this.f$7 = j;
    }

    public final void run() {
        this.f$0.lambda$fileDidUploaded$2$ImageLoader$2(this.f$1, this.f$2, this.f$3, this.f$4, this.f$5, this.f$6, this.f$7);
    }
}
