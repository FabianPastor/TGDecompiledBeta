package org.telegram.messenger;

import androidx.collection.LongSparseArray;
import java.util.ArrayList;
import org.telegram.tgnet.RequestDelegate;
import org.telegram.tgnet.TLObject;
import org.telegram.tgnet.TLRPC$StickerSet;
import org.telegram.tgnet.TLRPC$TL_error;
import org.telegram.tgnet.TLRPC$TL_messages_allStickers;

public final /* synthetic */ class MediaDataController$$ExternalSyntheticLambda153 implements RequestDelegate {
    public final /* synthetic */ MediaDataController f$0;
    public final /* synthetic */ ArrayList f$1;
    public final /* synthetic */ int f$2;
    public final /* synthetic */ LongSparseArray f$3;
    public final /* synthetic */ TLRPC$StickerSet f$4;
    public final /* synthetic */ TLRPC$TL_messages_allStickers f$5;
    public final /* synthetic */ int f$6;

    public /* synthetic */ MediaDataController$$ExternalSyntheticLambda153(MediaDataController mediaDataController, ArrayList arrayList, int i, LongSparseArray longSparseArray, TLRPC$StickerSet tLRPC$StickerSet, TLRPC$TL_messages_allStickers tLRPC$TL_messages_allStickers, int i2) {
        this.f$0 = mediaDataController;
        this.f$1 = arrayList;
        this.f$2 = i;
        this.f$3 = longSparseArray;
        this.f$4 = tLRPC$StickerSet;
        this.f$5 = tLRPC$TL_messages_allStickers;
        this.f$6 = i2;
    }

    public final void run(TLObject tLObject, TLRPC$TL_error tLRPC$TL_error) {
        this.f$0.lambda$processLoadStickersResponse$48(this.f$1, this.f$2, this.f$3, this.f$4, this.f$5, this.f$6, tLObject, tLRPC$TL_error);
    }
}
