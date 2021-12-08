package org.telegram.ui.Components;

import android.util.LongSparseArray;
import java.util.ArrayList;
import org.telegram.tgnet.RequestDelegate;
import org.telegram.tgnet.TLObject;
import org.telegram.tgnet.TLRPC;
import org.telegram.ui.Components.StickerMasksAlert;

public final /* synthetic */ class StickerMasksAlert$StickersSearchGridAdapter$1$$ExternalSyntheticLambda2 implements RequestDelegate {
    public final /* synthetic */ StickerMasksAlert.StickersSearchGridAdapter.AnonymousClass1 f$0;
    public final /* synthetic */ TLRPC.TL_messages_getStickers f$1;
    public final /* synthetic */ ArrayList f$2;
    public final /* synthetic */ LongSparseArray f$3;

    public /* synthetic */ StickerMasksAlert$StickersSearchGridAdapter$1$$ExternalSyntheticLambda2(StickerMasksAlert.StickersSearchGridAdapter.AnonymousClass1 r1, TLRPC.TL_messages_getStickers tL_messages_getStickers, ArrayList arrayList, LongSparseArray longSparseArray) {
        this.f$0 = r1;
        this.f$1 = tL_messages_getStickers;
        this.f$2 = arrayList;
        this.f$3 = longSparseArray;
    }

    public final void run(TLObject tLObject, TLRPC.TL_error tL_error) {
        this.f$0.m2629x8b8e956d(this.f$1, this.f$2, this.f$3, tLObject, tL_error);
    }
}
