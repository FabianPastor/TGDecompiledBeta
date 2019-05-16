package org.telegram.messenger;

import org.telegram.tgnet.RequestDelegate;
import org.telegram.tgnet.TLObject;
import org.telegram.tgnet.TLRPC.TL_error;

/* compiled from: lambda */
public final /* synthetic */ class -$$Lambda$FileUploadOperation$XMcVvcrqfWd56m49RmvLW8_t4Os implements RequestDelegate {
    private final /* synthetic */ FileUploadOperation f$0;
    private final /* synthetic */ int f$1;
    private final /* synthetic */ int f$2;
    private final /* synthetic */ byte[] f$3;
    private final /* synthetic */ int f$4;
    private final /* synthetic */ int f$5;
    private final /* synthetic */ int f$6;
    private final /* synthetic */ long f$7;
    private final /* synthetic */ TLObject f$8;

    public /* synthetic */ -$$Lambda$FileUploadOperation$XMcVvcrqfWd56m49RmvLW8_t4Os(FileUploadOperation fileUploadOperation, int i, int i2, byte[] bArr, int i3, int i4, int i5, long j, TLObject tLObject) {
        this.f$0 = fileUploadOperation;
        this.f$1 = i;
        this.f$2 = i2;
        this.f$3 = bArr;
        this.f$4 = i3;
        this.f$5 = i4;
        this.f$6 = i5;
        this.f$7 = j;
        this.f$8 = tLObject;
    }

    public final void run(TLObject tLObject, TL_error tL_error) {
        this.f$0.lambda$startUploadRequest$4$FileUploadOperation(this.f$1, this.f$2, this.f$3, this.f$4, this.f$5, this.f$6, this.f$7, this.f$8, tLObject, tL_error);
    }
}
