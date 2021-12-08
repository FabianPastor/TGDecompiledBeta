package org.telegram.messenger;

import androidx.collection.LongSparseArray;
import java.util.ArrayList;
import org.telegram.tgnet.TLObject;
import org.telegram.tgnet.TLRPC;

public final /* synthetic */ class MediaDataController$$ExternalSyntheticLambda119 implements Runnable {
    public final /* synthetic */ MediaDataController f$0;
    public final /* synthetic */ TLObject f$1;
    public final /* synthetic */ ArrayList f$2;
    public final /* synthetic */ int f$3;
    public final /* synthetic */ LongSparseArray f$4;
    public final /* synthetic */ TLRPC.StickerSet f$5;
    public final /* synthetic */ TLRPC.TL_messages_allStickers f$6;
    public final /* synthetic */ int f$7;

    public /* synthetic */ MediaDataController$$ExternalSyntheticLambda119(MediaDataController mediaDataController, TLObject tLObject, ArrayList arrayList, int i, LongSparseArray longSparseArray, TLRPC.StickerSet stickerSet, TLRPC.TL_messages_allStickers tL_messages_allStickers, int i2) {
        this.f$0 = mediaDataController;
        this.f$1 = tLObject;
        this.f$2 = arrayList;
        this.f$3 = i;
        this.f$4 = longSparseArray;
        this.f$5 = stickerSet;
        this.f$6 = tL_messages_allStickers;
        this.f$7 = i2;
    }

    public final void run() {
        this.f$0.m850xdc2a34f7(this.f$1, this.f$2, this.f$3, this.f$4, this.f$5, this.f$6, this.f$7);
    }
}
