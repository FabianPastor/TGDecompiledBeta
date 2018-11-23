package org.telegram.messenger;

import java.util.ArrayList;
import java.util.Comparator;
import org.telegram.tgnet.TLRPC.TL_messages_stickerSet;

final /* synthetic */ class DataQuery$$Lambda$13 implements Comparator {
    private final ArrayList arg$1;

    DataQuery$$Lambda$13(ArrayList arrayList) {
        this.arg$1 = arrayList;
    }

    public int compare(Object obj, Object obj2) {
        return DataQuery.lambda$reorderStickers$16$DataQuery(this.arg$1, (TL_messages_stickerSet) obj, (TL_messages_stickerSet) obj2);
    }
}
