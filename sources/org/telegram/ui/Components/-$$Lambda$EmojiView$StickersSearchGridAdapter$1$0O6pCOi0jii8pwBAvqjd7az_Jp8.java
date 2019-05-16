package org.telegram.ui.Components;

import android.util.LongSparseArray;
import java.util.ArrayList;
import org.telegram.tgnet.TLObject;
import org.telegram.tgnet.TLRPC.TL_messages_getStickers;
import org.telegram.ui.Components.EmojiView.StickersSearchGridAdapter.AnonymousClass1;

/* compiled from: lambda */
public final /* synthetic */ class -$$Lambda$EmojiView$StickersSearchGridAdapter$1$0O6pCOi0jii8pwBAvqjd7az_Jp8 implements Runnable {
    private final /* synthetic */ AnonymousClass1 f$0;
    private final /* synthetic */ TL_messages_getStickers f$1;
    private final /* synthetic */ TLObject f$2;
    private final /* synthetic */ ArrayList f$3;
    private final /* synthetic */ LongSparseArray f$4;

    public /* synthetic */ -$$Lambda$EmojiView$StickersSearchGridAdapter$1$0O6pCOi0jii8pwBAvqjd7az_Jp8(AnonymousClass1 anonymousClass1, TL_messages_getStickers tL_messages_getStickers, TLObject tLObject, ArrayList arrayList, LongSparseArray longSparseArray) {
        this.f$0 = anonymousClass1;
        this.f$1 = tL_messages_getStickers;
        this.f$2 = tLObject;
        this.f$3 = arrayList;
        this.f$4 = longSparseArray;
    }

    public final void run() {
        this.f$0.lambda$null$2$EmojiView$StickersSearchGridAdapter$1(this.f$1, this.f$2, this.f$3, this.f$4);
    }
}
