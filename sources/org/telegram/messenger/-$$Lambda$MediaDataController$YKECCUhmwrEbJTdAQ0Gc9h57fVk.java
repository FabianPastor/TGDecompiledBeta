package org.telegram.messenger;

import android.util.LongSparseArray;
import java.util.ArrayList;
import org.telegram.tgnet.TLObject;
import org.telegram.tgnet.TLRPC.StickerSet;
import org.telegram.tgnet.TLRPC.TL_messages_allStickers;

/* compiled from: lambda */
public final /* synthetic */ class -$$Lambda$MediaDataController$YKECCUhmwrEbJTdAQ0Gc9h57fVk implements Runnable {
    private final /* synthetic */ MediaDataController f$0;
    private final /* synthetic */ TLObject f$1;
    private final /* synthetic */ ArrayList f$2;
    private final /* synthetic */ int f$3;
    private final /* synthetic */ LongSparseArray f$4;
    private final /* synthetic */ StickerSet f$5;
    private final /* synthetic */ TL_messages_allStickers f$6;
    private final /* synthetic */ int f$7;

    public /* synthetic */ -$$Lambda$MediaDataController$YKECCUhmwrEbJTdAQ0Gc9h57fVk(MediaDataController mediaDataController, TLObject tLObject, ArrayList arrayList, int i, LongSparseArray longSparseArray, StickerSet stickerSet, TL_messages_allStickers tL_messages_allStickers, int i2) {
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
        this.f$0.lambda$null$33$MediaDataController(this.f$1, this.f$2, this.f$3, this.f$4, this.f$5, this.f$6, this.f$7);
    }
}
