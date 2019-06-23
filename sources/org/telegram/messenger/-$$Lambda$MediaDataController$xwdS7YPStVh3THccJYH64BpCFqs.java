package org.telegram.messenger;

import java.util.ArrayList;
import java.util.Comparator;
import org.telegram.tgnet.TLRPC.TL_messages_stickerSet;

/* compiled from: lambda */
public final /* synthetic */ class -$$Lambda$MediaDataController$xwdS7YPStVh3THccJYH64BpCFqs implements Comparator {
    private final /* synthetic */ ArrayList f$0;

    public /* synthetic */ -$$Lambda$MediaDataController$xwdS7YPStVh3THccJYH64BpCFqs(ArrayList arrayList) {
        this.f$0 = arrayList;
    }

    public final int compare(Object obj, Object obj2) {
        return MediaDataController.lambda$reorderStickers$16(this.f$0, (TL_messages_stickerSet) obj, (TL_messages_stickerSet) obj2);
    }
}
