package org.telegram.ui.Components;

import android.util.LongSparseArray;
import java.util.ArrayList;
import org.telegram.tgnet.RequestDelegate;
import org.telegram.tgnet.TLObject;
import org.telegram.tgnet.TLRPC.TL_error;
import org.telegram.tgnet.TLRPC.TL_messages_getStickers;
import org.telegram.ui.Components.EmojiView.StickersSearchGridAdapter.AnonymousClass1;

final /* synthetic */ class EmojiView$StickersSearchGridAdapter$1$$Lambda$1 implements RequestDelegate {
    private final AnonymousClass1 arg$1;
    private final TL_messages_getStickers arg$2;
    private final ArrayList arg$3;
    private final LongSparseArray arg$4;

    EmojiView$StickersSearchGridAdapter$1$$Lambda$1(AnonymousClass1 anonymousClass1, TL_messages_getStickers tL_messages_getStickers, ArrayList arrayList, LongSparseArray longSparseArray) {
        this.arg$1 = anonymousClass1;
        this.arg$2 = tL_messages_getStickers;
        this.arg$3 = arrayList;
        this.arg$4 = longSparseArray;
    }

    public void run(TLObject tLObject, TL_error tL_error) {
        this.arg$1.lambda$run$3$EmojiView$StickersSearchGridAdapter$1(this.arg$2, this.arg$3, this.arg$4, tLObject, tL_error);
    }
}