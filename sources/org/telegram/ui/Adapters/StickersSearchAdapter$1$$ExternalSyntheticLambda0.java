package org.telegram.ui.Adapters;

import android.util.LongSparseArray;
import java.util.ArrayList;
import org.telegram.tgnet.TLObject;
import org.telegram.tgnet.TLRPC;
import org.telegram.ui.Adapters.StickersSearchAdapter;

public final /* synthetic */ class StickersSearchAdapter$1$$ExternalSyntheticLambda0 implements Runnable {
    public final /* synthetic */ StickersSearchAdapter.AnonymousClass1 f$0;
    public final /* synthetic */ TLRPC.TL_messages_getStickers f$1;
    public final /* synthetic */ TLObject f$2;
    public final /* synthetic */ ArrayList f$3;
    public final /* synthetic */ LongSparseArray f$4;

    public /* synthetic */ StickersSearchAdapter$1$$ExternalSyntheticLambda0(StickersSearchAdapter.AnonymousClass1 r1, TLRPC.TL_messages_getStickers tL_messages_getStickers, TLObject tLObject, ArrayList arrayList, LongSparseArray longSparseArray) {
        this.f$0 = r1;
        this.f$1 = tL_messages_getStickers;
        this.f$2 = tLObject;
        this.f$3 = arrayList;
        this.f$4 = longSparseArray;
    }

    public final void run() {
        this.f$0.m1404lambda$run$3$orgtelegramuiAdaptersStickersSearchAdapter$1(this.f$1, this.f$2, this.f$3, this.f$4);
    }
}
