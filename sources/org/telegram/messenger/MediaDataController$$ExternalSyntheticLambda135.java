package org.telegram.messenger;

import java.util.Comparator;
import org.telegram.tgnet.TLRPC$TL_topPeer;

public final /* synthetic */ class MediaDataController$$ExternalSyntheticLambda135 implements Comparator {
    public static final /* synthetic */ MediaDataController$$ExternalSyntheticLambda135 INSTANCE = new MediaDataController$$ExternalSyntheticLambda135();

    private /* synthetic */ MediaDataController$$ExternalSyntheticLambda135() {
    }

    public final int compare(Object obj, Object obj2) {
        return MediaDataController.lambda$increasePeerRaiting$119((TLRPC$TL_topPeer) obj, (TLRPC$TL_topPeer) obj2);
    }
}
