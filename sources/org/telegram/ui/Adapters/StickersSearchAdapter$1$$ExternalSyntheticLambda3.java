package org.telegram.ui.Adapters;

import android.util.LongSparseArray;
import java.util.ArrayList;
import org.telegram.tgnet.RequestDelegate;
import org.telegram.tgnet.TLObject;
import org.telegram.tgnet.TLRPC;
import org.telegram.ui.Adapters.StickersSearchAdapter;

public final /* synthetic */ class StickersSearchAdapter$1$$ExternalSyntheticLambda3 implements RequestDelegate {
    public final /* synthetic */ StickersSearchAdapter.AnonymousClass1 f$0;
    public final /* synthetic */ TLRPC.TL_messages_getStickers f$1;
    public final /* synthetic */ ArrayList f$2;
    public final /* synthetic */ LongSparseArray f$3;

    public /* synthetic */ StickersSearchAdapter$1$$ExternalSyntheticLambda3(StickersSearchAdapter.AnonymousClass1 r1, TLRPC.TL_messages_getStickers tL_messages_getStickers, ArrayList arrayList, LongSparseArray longSparseArray) {
        this.f$0 = r1;
        this.f$1 = tL_messages_getStickers;
        this.f$2 = arrayList;
        this.f$3 = longSparseArray;
    }

    public final void run(TLObject tLObject, TLRPC.TL_error tL_error) {
        this.f$0.m2659lambda$run$4$orgtelegramuiAdaptersStickersSearchAdapter$1(this.f$1, this.f$2, this.f$3, tLObject, tL_error);
    }
}
