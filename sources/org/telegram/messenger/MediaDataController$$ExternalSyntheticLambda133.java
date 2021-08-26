package org.telegram.messenger;

import android.util.LongSparseArray;
import org.telegram.tgnet.RequestDelegate;
import org.telegram.tgnet.TLObject;
import org.telegram.tgnet.TLRPC$TL_error;

public final /* synthetic */ class MediaDataController$$ExternalSyntheticLambda133 implements RequestDelegate {
    public final /* synthetic */ MediaDataController f$0;
    public final /* synthetic */ LongSparseArray f$1;
    public final /* synthetic */ long f$2;
    public final /* synthetic */ boolean f$3;
    public final /* synthetic */ Runnable f$4;

    public /* synthetic */ MediaDataController$$ExternalSyntheticLambda133(MediaDataController mediaDataController, LongSparseArray longSparseArray, long j, boolean z, Runnable runnable) {
        this.f$0 = mediaDataController;
        this.f$1 = longSparseArray;
        this.f$2 = j;
        this.f$3 = z;
        this.f$4 = runnable;
    }

    public final void run(TLObject tLObject, TLRPC$TL_error tLRPC$TL_error) {
        this.f$0.lambda$loadReplyMessagesForMessages$116(this.f$1, this.f$2, this.f$3, this.f$4, tLObject, tLRPC$TL_error);
    }
}
