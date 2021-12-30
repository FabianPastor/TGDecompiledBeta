package org.telegram.messenger;

import java.util.Comparator;
import org.telegram.tgnet.TLRPC$TL_topPeer;

public final /* synthetic */ class MediaDataController$$ExternalSyntheticLambda116 implements Comparator {
    public static final /* synthetic */ MediaDataController$$ExternalSyntheticLambda116 INSTANCE = new MediaDataController$$ExternalSyntheticLambda116();

    private /* synthetic */ MediaDataController$$ExternalSyntheticLambda116() {
    }

    public final int compare(Object obj, Object obj2) {
        return MediaDataController.lambda$increasePeerRaiting$102((TLRPC$TL_topPeer) obj, (TLRPC$TL_topPeer) obj2);
    }
}
