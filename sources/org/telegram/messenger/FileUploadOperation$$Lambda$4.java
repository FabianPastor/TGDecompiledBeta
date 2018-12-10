package org.telegram.messenger;

import org.telegram.tgnet.RequestDelegate;
import org.telegram.tgnet.TLObject;
import org.telegram.tgnet.TLRPC.TL_error;

final /* synthetic */ class FileUploadOperation$$Lambda$4 implements RequestDelegate {
    private final FileUploadOperation arg$1;
    private final int arg$2;
    private final int arg$3;
    private final byte[] arg$4;
    private final int arg$5;
    private final int arg$6;
    private final int arg$7;
    private final long arg$8;
    private final TLObject arg$9;

    FileUploadOperation$$Lambda$4(FileUploadOperation fileUploadOperation, int i, int i2, byte[] bArr, int i3, int i4, int i5, long j, TLObject tLObject) {
        this.arg$1 = fileUploadOperation;
        this.arg$2 = i;
        this.arg$3 = i2;
        this.arg$4 = bArr;
        this.arg$5 = i3;
        this.arg$6 = i4;
        this.arg$7 = i5;
        this.arg$8 = j;
        this.arg$9 = tLObject;
    }

    public void run(TLObject tLObject, TL_error tL_error) {
        this.arg$1.lambda$startUploadRequest$4$FileUploadOperation(this.arg$2, this.arg$3, this.arg$4, this.arg$5, this.arg$6, this.arg$7, this.arg$8, this.arg$9, tLObject, tL_error);
    }
}
