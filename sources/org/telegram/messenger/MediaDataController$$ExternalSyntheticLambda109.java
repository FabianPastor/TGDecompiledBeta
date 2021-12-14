package org.telegram.messenger;

import java.util.ArrayList;
import java.util.Comparator;
import org.telegram.tgnet.TLRPC$TL_messages_stickerSet;

public final /* synthetic */ class MediaDataController$$ExternalSyntheticLambda109 implements Comparator {
    public final /* synthetic */ ArrayList f$0;

    public /* synthetic */ MediaDataController$$ExternalSyntheticLambda109(ArrayList arrayList) {
        this.f$0 = arrayList;
    }

    public final int compare(Object obj, Object obj2) {
        return MediaDataController.lambda$reorderStickers$20(this.f$0, (TLRPC$TL_messages_stickerSet) obj, (TLRPC$TL_messages_stickerSet) obj2);
    }
}
