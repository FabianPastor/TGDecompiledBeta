package org.telegram.messenger;

import org.telegram.messenger.FileLoader;
import org.telegram.tgnet.TLRPC;

public final /* synthetic */ class FileLoader$1$$ExternalSyntheticLambda1 implements Runnable {
    public final /* synthetic */ FileLoader.AnonymousClass1 f$0;
    public final /* synthetic */ boolean f$1;
    public final /* synthetic */ String f$2;
    public final /* synthetic */ boolean f$3;
    public final /* synthetic */ TLRPC.InputFile f$4;
    public final /* synthetic */ TLRPC.InputEncryptedFile f$5;
    public final /* synthetic */ byte[] f$6;
    public final /* synthetic */ byte[] f$7;
    public final /* synthetic */ FileUploadOperation f$8;

    public /* synthetic */ FileLoader$1$$ExternalSyntheticLambda1(FileLoader.AnonymousClass1 r1, boolean z, String str, boolean z2, TLRPC.InputFile inputFile, TLRPC.InputEncryptedFile inputEncryptedFile, byte[] bArr, byte[] bArr2, FileUploadOperation fileUploadOperation) {
        this.f$0 = r1;
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
        this.f$0.m643xdfee369(this.f$1, this.f$2, this.f$3, this.f$4, this.f$5, this.f$6, this.f$7, this.f$8);
    }
}
