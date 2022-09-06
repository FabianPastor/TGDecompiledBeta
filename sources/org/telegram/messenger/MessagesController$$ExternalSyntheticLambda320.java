package org.telegram.messenger;

import androidx.collection.LongSparseArray;
import java.util.HashMap;
import org.telegram.tgnet.RequestDelegate;
import org.telegram.tgnet.TLObject;
import org.telegram.tgnet.TLRPC$TL_error;

public final /* synthetic */ class MessagesController$$ExternalSyntheticLambda320 implements RequestDelegate {
    public final /* synthetic */ MessagesController f$0;
    public final /* synthetic */ HashMap f$1;
    public final /* synthetic */ String f$2;
    public final /* synthetic */ LongSparseArray f$3;
    public final /* synthetic */ long f$4;
    public final /* synthetic */ boolean f$5;

    public /* synthetic */ MessagesController$$ExternalSyntheticLambda320(MessagesController messagesController, HashMap hashMap, String str, LongSparseArray longSparseArray, long j, boolean z) {
        this.f$0 = messagesController;
        this.f$1 = hashMap;
        this.f$2 = str;
        this.f$3 = longSparseArray;
        this.f$4 = j;
        this.f$5 = z;
    }

    public final void run(TLObject tLObject, TLRPC$TL_error tLRPC$TL_error) {
        this.f$0.lambda$reloadWebPages$153(this.f$1, this.f$2, this.f$3, this.f$4, this.f$5, tLObject, tLRPC$TL_error);
    }
}
