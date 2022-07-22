package org.telegram.messenger;

import java.util.ArrayList;
import org.telegram.messenger.Utilities;
import org.telegram.tgnet.TLRPC$StickerSet;

public final /* synthetic */ class MediaDataController$$ExternalSyntheticLambda146 implements Utilities.Callback {
    public final /* synthetic */ MediaDataController f$0;
    public final /* synthetic */ TLRPC$StickerSet f$1;

    public /* synthetic */ MediaDataController$$ExternalSyntheticLambda146(MediaDataController mediaDataController, TLRPC$StickerSet tLRPC$StickerSet) {
        this.f$0 = mediaDataController;
        this.f$1 = tLRPC$StickerSet;
    }

    public final void run(Object obj) {
        this.f$0.lambda$toggleStickerSetInternal$88(this.f$1, (ArrayList) obj);
    }
}
