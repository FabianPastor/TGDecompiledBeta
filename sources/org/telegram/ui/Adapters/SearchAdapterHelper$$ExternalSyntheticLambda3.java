package org.telegram.ui.Adapters;

import java.util.ArrayList;
import java.util.concurrent.atomic.AtomicInteger;
import org.telegram.tgnet.TLObject;
import org.telegram.tgnet.TLRPC;

public final /* synthetic */ class SearchAdapterHelper$$ExternalSyntheticLambda3 implements Runnable {
    public final /* synthetic */ SearchAdapterHelper f$0;
    public final /* synthetic */ ArrayList f$1;
    public final /* synthetic */ int f$2;
    public final /* synthetic */ TLObject f$3;
    public final /* synthetic */ TLRPC.TL_error f$4;
    public final /* synthetic */ AtomicInteger f$5;
    public final /* synthetic */ AtomicInteger f$6;
    public final /* synthetic */ ArrayList f$7;
    public final /* synthetic */ int f$8;
    public final /* synthetic */ Runnable f$9;

    public /* synthetic */ SearchAdapterHelper$$ExternalSyntheticLambda3(SearchAdapterHelper searchAdapterHelper, ArrayList arrayList, int i, TLObject tLObject, TLRPC.TL_error tL_error, AtomicInteger atomicInteger, AtomicInteger atomicInteger2, ArrayList arrayList2, int i2, Runnable runnable) {
        this.f$0 = searchAdapterHelper;
        this.f$1 = arrayList;
        this.f$2 = i;
        this.f$3 = tLObject;
        this.f$4 = tL_error;
        this.f$5 = atomicInteger;
        this.f$6 = atomicInteger2;
        this.f$7 = arrayList2;
        this.f$8 = i2;
        this.f$9 = runnable;
    }

    public final void run() {
        this.f$0.m1356x7e391ce2(this.f$1, this.f$2, this.f$3, this.f$4, this.f$5, this.f$6, this.f$7, this.f$8, this.f$9);
    }
}
