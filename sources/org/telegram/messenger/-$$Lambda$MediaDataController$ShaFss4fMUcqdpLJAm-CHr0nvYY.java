package org.telegram.messenger;

import android.util.LongSparseArray;
import java.util.ArrayList;
import org.telegram.tgnet.RequestDelegate;
import org.telegram.tgnet.TLObject;
import org.telegram.tgnet.TLRPC.StickerSet;
import org.telegram.tgnet.TLRPC.TL_error;
import org.telegram.tgnet.TLRPC.TL_messages_allStickers;

/* compiled from: lambda */
public final /* synthetic */ class -$$Lambda$MediaDataController$ShaFss4fMUcqdpLJAm-CHr0nvYY implements RequestDelegate {
    private final /* synthetic */ MediaDataController f$0;
    private final /* synthetic */ ArrayList f$1;
    private final /* synthetic */ int f$2;
    private final /* synthetic */ LongSparseArray f$3;
    private final /* synthetic */ StickerSet f$4;
    private final /* synthetic */ TL_messages_allStickers f$5;
    private final /* synthetic */ int f$6;

    public /* synthetic */ -$$Lambda$MediaDataController$ShaFss4fMUcqdpLJAm-CHr0nvYY(MediaDataController mediaDataController, ArrayList arrayList, int i, LongSparseArray longSparseArray, StickerSet stickerSet, TL_messages_allStickers tL_messages_allStickers, int i2) {
        this.f$0 = mediaDataController;
        this.f$1 = arrayList;
        this.f$2 = i;
        this.f$3 = longSparseArray;
        this.f$4 = stickerSet;
        this.f$5 = tL_messages_allStickers;
        this.f$6 = i2;
    }

    public final void run(TLObject tLObject, TL_error tL_error) {
        this.f$0.lambda$processLoadStickersResponse$33$MediaDataController(this.f$1, this.f$2, this.f$3, this.f$4, this.f$5, this.f$6, tLObject, tL_error);
    }
}
