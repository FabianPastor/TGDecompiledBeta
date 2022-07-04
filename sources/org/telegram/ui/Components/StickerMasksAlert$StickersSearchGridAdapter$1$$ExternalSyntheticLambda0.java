package org.telegram.ui.Components;

import android.util.LongSparseArray;
import java.util.ArrayList;
import org.telegram.tgnet.TLObject;
import org.telegram.tgnet.TLRPC$TL_messages_getStickers;
import org.telegram.ui.Components.StickerMasksAlert;

public final /* synthetic */ class StickerMasksAlert$StickersSearchGridAdapter$1$$ExternalSyntheticLambda0 implements Runnable {
    public final /* synthetic */ StickerMasksAlert.StickersSearchGridAdapter.AnonymousClass1 f$0;
    public final /* synthetic */ TLRPC$TL_messages_getStickers f$1;
    public final /* synthetic */ TLObject f$2;
    public final /* synthetic */ ArrayList f$3;
    public final /* synthetic */ LongSparseArray f$4;

    public /* synthetic */ StickerMasksAlert$StickersSearchGridAdapter$1$$ExternalSyntheticLambda0(StickerMasksAlert.StickersSearchGridAdapter.AnonymousClass1 r1, TLRPC$TL_messages_getStickers tLRPC$TL_messages_getStickers, TLObject tLObject, ArrayList arrayList, LongSparseArray longSparseArray) {
        this.f$0 = r1;
        this.f$1 = tLRPC$TL_messages_getStickers;
        this.f$2 = tLObject;
        this.f$3 = arrayList;
        this.f$4 = longSparseArray;
    }

    public final void run() {
        this.f$0.lambda$run$1(this.f$1, this.f$2, this.f$3, this.f$4);
    }
}
