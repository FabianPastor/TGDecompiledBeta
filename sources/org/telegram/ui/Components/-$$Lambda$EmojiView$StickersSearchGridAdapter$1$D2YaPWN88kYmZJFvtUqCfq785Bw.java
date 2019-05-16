package org.telegram.ui.Components;

import android.util.LongSparseArray;
import java.util.ArrayList;
import org.telegram.tgnet.RequestDelegate;
import org.telegram.tgnet.TLObject;
import org.telegram.tgnet.TLRPC.TL_error;
import org.telegram.tgnet.TLRPC.TL_messages_getStickers;
import org.telegram.ui.Components.EmojiView.StickersSearchGridAdapter.AnonymousClass1;

/* compiled from: lambda */
public final /* synthetic */ class -$$Lambda$EmojiView$StickersSearchGridAdapter$1$D2YaPWN88kYmZJFvtUqCfq785Bw implements RequestDelegate {
    private final /* synthetic */ AnonymousClass1 f$0;
    private final /* synthetic */ TL_messages_getStickers f$1;
    private final /* synthetic */ ArrayList f$2;
    private final /* synthetic */ LongSparseArray f$3;

    public /* synthetic */ -$$Lambda$EmojiView$StickersSearchGridAdapter$1$D2YaPWN88kYmZJFvtUqCfq785Bw(AnonymousClass1 anonymousClass1, TL_messages_getStickers tL_messages_getStickers, ArrayList arrayList, LongSparseArray longSparseArray) {
        this.f$0 = anonymousClass1;
        this.f$1 = tL_messages_getStickers;
        this.f$2 = arrayList;
        this.f$3 = longSparseArray;
    }

    public final void run(TLObject tLObject, TL_error tL_error) {
        this.f$0.lambda$run$3$EmojiView$StickersSearchGridAdapter$1(this.f$1, this.f$2, this.f$3, tLObject, tL_error);
    }
}
