package org.telegram.messenger;

import org.telegram.messenger.FileLoader.AnonymousClass1;
import org.telegram.tgnet.TLRPC.InputEncryptedFile;
import org.telegram.tgnet.TLRPC.InputFile;

/* compiled from: lambda */
public final /* synthetic */ class -$$Lambda$FileLoader$1$8qI8Hd84Wsy2NQjG71Z0jivjIew implements Runnable {
    private final /* synthetic */ AnonymousClass1 f$0;
    private final /* synthetic */ boolean f$1;
    private final /* synthetic */ String f$2;
    private final /* synthetic */ boolean f$3;
    private final /* synthetic */ InputFile f$4;
    private final /* synthetic */ InputEncryptedFile f$5;
    private final /* synthetic */ byte[] f$6;
    private final /* synthetic */ byte[] f$7;
    private final /* synthetic */ FileUploadOperation f$8;

    public /* synthetic */ -$$Lambda$FileLoader$1$8qI8Hd84Wsy2NQjG71Z0jivjIew(AnonymousClass1 anonymousClass1, boolean z, String str, boolean z2, InputFile inputFile, InputEncryptedFile inputEncryptedFile, byte[] bArr, byte[] bArr2, FileUploadOperation fileUploadOperation) {
        this.f$0 = anonymousClass1;
        this.f$1 = z;
        this.f$2 = str;
        this.f$3 = z2;
        this.f$4 = inputFile;
        this.f$5 = inputEncryptedFile;
        this.f$6 = bArr;
        this.f$7 = bArr2;
        this.f$8 = fileUploadOperation;
    }

    public final void run() {
        this.f$0.lambda$didFinishUploadingFile$0$FileLoader$1(this.f$1, this.f$2, this.f$3, this.f$4, this.f$5, this.f$6, this.f$7, this.f$8);
    }
}
