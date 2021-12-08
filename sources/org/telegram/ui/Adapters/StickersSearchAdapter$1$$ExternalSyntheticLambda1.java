package org.telegram.ui.Adapters;

import org.telegram.tgnet.TLObject;
import org.telegram.tgnet.TLRPC;
import org.telegram.ui.Adapters.StickersSearchAdapter;

public final /* synthetic */ class StickersSearchAdapter$1$$ExternalSyntheticLambda1 implements Runnable {
    public final /* synthetic */ StickersSearchAdapter.AnonymousClass1 f$0;
    public final /* synthetic */ TLRPC.TL_messages_searchStickerSets f$1;
    public final /* synthetic */ TLObject f$2;

    public /* synthetic */ StickersSearchAdapter$1$$ExternalSyntheticLambda1(StickersSearchAdapter.AnonymousClass1 r1, TLRPC.TL_messages_searchStickerSets tL_messages_searchStickerSets, TLObject tLObject) {
        this.f$0 = r1;
        this.f$1 = tL_messages_searchStickerSets;
        this.f$2 = tLObject;
    }

    public final void run() {
        this.f$0.m1402lambda$run$1$orgtelegramuiAdaptersStickersSearchAdapter$1(this.f$1, this.f$2);
    }
}
