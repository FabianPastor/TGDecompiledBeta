package org.telegram.ui.Adapters;

import java.util.ArrayList;
import java.util.concurrent.atomic.AtomicInteger;
import org.telegram.tgnet.RequestDelegate;
import org.telegram.tgnet.TLObject;
import org.telegram.tgnet.TLRPC$TL_error;

public final /* synthetic */ class SearchAdapterHelper$$ExternalSyntheticLambda8 implements RequestDelegate {
    public final /* synthetic */ SearchAdapterHelper f$0;
    public final /* synthetic */ ArrayList f$1;
    public final /* synthetic */ int f$2;
    public final /* synthetic */ AtomicInteger f$3;
    public final /* synthetic */ AtomicInteger f$4;
    public final /* synthetic */ ArrayList f$5;
    public final /* synthetic */ int f$6;
    public final /* synthetic */ Runnable f$7;

    public /* synthetic */ SearchAdapterHelper$$ExternalSyntheticLambda8(SearchAdapterHelper searchAdapterHelper, ArrayList arrayList, int i, AtomicInteger atomicInteger, AtomicInteger atomicInteger2, ArrayList arrayList2, int i2, Runnable runnable) {
        this.f$0 = searchAdapterHelper;
        this.f$1 = arrayList;
        this.f$2 = i;
        this.f$3 = atomicInteger;
        this.f$4 = atomicInteger2;
        this.f$5 = arrayList2;
        this.f$6 = i2;
        this.f$7 = runnable;
    }

    public final void run(TLObject tLObject, TLRPC$TL_error tLRPC$TL_error) {
        this.f$0.lambda$queryServerSearch$3(this.f$1, this.f$2, this.f$3, this.f$4, this.f$5, this.f$6, this.f$7, tLObject, tLRPC$TL_error);
    }
}
