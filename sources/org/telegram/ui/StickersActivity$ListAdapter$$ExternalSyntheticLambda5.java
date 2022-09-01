package org.telegram.ui;

import java.util.Comparator;
import org.telegram.tgnet.TLRPC$TL_messages_stickerSet;
import org.telegram.ui.StickersActivity;

public final /* synthetic */ class StickersActivity$ListAdapter$$ExternalSyntheticLambda5 implements Comparator {
    public final /* synthetic */ StickersActivity.ListAdapter f$0;

    public /* synthetic */ StickersActivity$ListAdapter$$ExternalSyntheticLambda5(StickersActivity.ListAdapter listAdapter) {
        this.f$0 = listAdapter;
    }

    public final int compare(Object obj, Object obj2) {
        return this.f$0.lambda$swapElements$5((TLRPC$TL_messages_stickerSet) obj, (TLRPC$TL_messages_stickerSet) obj2);
    }
}
