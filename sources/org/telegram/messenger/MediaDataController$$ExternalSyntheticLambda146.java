package org.telegram.messenger;

import androidx.collection.LongSparseArray;
import org.telegram.tgnet.RequestDelegate;
import org.telegram.tgnet.TLObject;
import org.telegram.tgnet.TLRPC$TL_error;

public final /* synthetic */ class MediaDataController$$ExternalSyntheticLambda146 implements RequestDelegate {
    public final /* synthetic */ MediaDataController f$0;
    public final /* synthetic */ long f$1;
    public final /* synthetic */ LongSparseArray f$2;
    public final /* synthetic */ boolean f$3;
    public final /* synthetic */ Runnable f$4;

    public /* synthetic */ MediaDataController$$ExternalSyntheticLambda146(MediaDataController mediaDataController, long j, LongSparseArray longSparseArray, boolean z, Runnable runnable) {
        this.f$0 = mediaDataController;
        this.f$1 = j;
        this.f$2 = longSparseArray;
        this.f$3 = z;
        this.f$4 = runnable;
    }

    public final void run(TLObject tLObject, TLRPC$TL_error tLRPC$TL_error) {
        this.f$0.lambda$loadReplyMessagesForMessages$126(this.f$1, this.f$2, this.f$3, this.f$4, tLObject, tLRPC$TL_error);
    }
}
