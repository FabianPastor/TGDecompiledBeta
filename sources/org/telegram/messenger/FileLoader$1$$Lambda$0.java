package org.telegram.messenger;

import org.telegram.messenger.FileLoader.C02051;
import org.telegram.tgnet.TLRPC.InputEncryptedFile;
import org.telegram.tgnet.TLRPC.InputFile;

final /* synthetic */ class FileLoader$1$$Lambda$0 implements Runnable {
    private final C02051 arg$1;
    private final boolean arg$2;
    private final String arg$3;
    private final boolean arg$4;
    private final InputFile arg$5;
    private final InputEncryptedFile arg$6;
    private final byte[] arg$7;
    private final byte[] arg$8;
    private final FileUploadOperation arg$9;

    FileLoader$1$$Lambda$0(C02051 c02051, boolean z, String str, boolean z2, InputFile inputFile, InputEncryptedFile inputEncryptedFile, byte[] bArr, byte[] bArr2, FileUploadOperation fileUploadOperation) {
        this.arg$1 = c02051;
        this.arg$2 = z;
        this.arg$3 = str;
        this.arg$4 = z2;
        this.arg$5 = inputFile;
        this.arg$6 = inputEncryptedFile;
        this.arg$7 = bArr;
        this.arg$8 = bArr2;
        this.arg$9 = fileUploadOperation;
    }

    public void run() {
        this.arg$1.lambda$didFinishUploadingFile$0$FileLoader$1(this.arg$2, this.arg$3, this.arg$4, this.arg$5, this.arg$6, this.arg$7, this.arg$8, this.arg$9);
    }
}
