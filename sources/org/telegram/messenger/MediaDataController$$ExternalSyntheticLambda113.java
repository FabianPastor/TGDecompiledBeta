package org.telegram.messenger;

import java.util.Comparator;
import org.telegram.tgnet.TLRPC$TL_topPeer;

public final /* synthetic */ class MediaDataController$$ExternalSyntheticLambda113 implements Comparator {
    public static final /* synthetic */ MediaDataController$$ExternalSyntheticLambda113 INSTANCE = new MediaDataController$$ExternalSyntheticLambda113();

    private /* synthetic */ MediaDataController$$ExternalSyntheticLambda113() {
    }

    public final int compare(Object obj, Object obj2) {
        return MediaDataController.lambda$increasePeerRaiting$98((TLRPC$TL_topPeer) obj, (TLRPC$TL_topPeer) obj2);
    }
}
