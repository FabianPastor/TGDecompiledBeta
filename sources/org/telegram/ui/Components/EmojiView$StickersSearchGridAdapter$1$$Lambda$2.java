package org.telegram.ui.Components;

import android.util.LongSparseArray;
import java.util.ArrayList;
import org.telegram.tgnet.TLObject;
import org.telegram.tgnet.TLRPC.TL_messages_getStickers;
import org.telegram.ui.Components.EmojiView.StickersSearchGridAdapter.AnonymousClass1;

final /* synthetic */ class EmojiView$StickersSearchGridAdapter$1$$Lambda$2 implements Runnable {
    private final AnonymousClass1 arg$1;
    private final TL_messages_getStickers arg$2;
    private final TLObject arg$3;
    private final ArrayList arg$4;
    private final LongSparseArray arg$5;

    EmojiView$StickersSearchGridAdapter$1$$Lambda$2(AnonymousClass1 anonymousClass1, TL_messages_getStickers tL_messages_getStickers, TLObject tLObject, ArrayList arrayList, LongSparseArray longSparseArray) {
        this.arg$1 = anonymousClass1;
        this.arg$2 = tL_messages_getStickers;
        this.arg$3 = tLObject;
        this.arg$4 = arrayList;
        this.arg$5 = longSparseArray;
    }

    public void run() {
        this.arg$1.lambda$null$2$EmojiView$StickersSearchGridAdapter$1(this.arg$2, this.arg$3, this.arg$4, this.arg$5);
    }
}
