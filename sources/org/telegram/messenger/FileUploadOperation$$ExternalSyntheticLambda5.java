package org.telegram.messenger;

import org.telegram.tgnet.RequestDelegate;
import org.telegram.tgnet.TLObject;
import org.telegram.tgnet.TLRPC$TL_error;

public final /* synthetic */ class FileUploadOperation$$ExternalSyntheticLambda5 implements RequestDelegate {
    public final /* synthetic */ FileUploadOperation f$0;
    public final /* synthetic */ int f$1;
    public final /* synthetic */ int f$2;
    public final /* synthetic */ byte[] f$3;
    public final /* synthetic */ int f$4;
    public final /* synthetic */ int f$5;
    public final /* synthetic */ int f$6;
    public final /* synthetic */ long f$7;

    public /* synthetic */ FileUploadOperation$$ExternalSyntheticLambda5(FileUploadOperation fileUploadOperation, int i, int i2, byte[] bArr, int i3, int i4, int i5, long j) {
        this.f$0 = fileUploadOperation;
        this.f$1 = i;
        this.f$2 = i2;
        this.f$3 = bArr;
        this.f$4 = i3;
        this.f$5 = i4;
        this.f$6 = i5;
        this.f$7 = j;
    }

    public final void run(TLObject tLObject, TLRPC$TL_error tLRPC$TL_error) {
        this.f$0.lambda$startUploadRequest$4(this.f$1, this.f$2, this.f$3, this.f$4, this.f$5, this.f$6, this.f$7, tLObject, tLRPC$TL_error);
    }
}
