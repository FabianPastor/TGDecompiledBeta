package org.telegram.messenger;

import java.util.Comparator;
import org.telegram.tgnet.TLRPC$TL_topPeer;

public final /* synthetic */ class MediaDataController$$ExternalSyntheticLambda143 implements Comparator {
    public static final /* synthetic */ MediaDataController$$ExternalSyntheticLambda143 INSTANCE = new MediaDataController$$ExternalSyntheticLambda143();

    private /* synthetic */ MediaDataController$$ExternalSyntheticLambda143() {
    }

    public final int compare(Object obj, Object obj2) {
        return MediaDataController.lambda$increasePeerRaiting$129((TLRPC$TL_topPeer) obj, (TLRPC$TL_topPeer) obj2);
    }
}
