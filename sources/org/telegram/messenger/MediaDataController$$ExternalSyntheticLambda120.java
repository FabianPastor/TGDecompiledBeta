package org.telegram.messenger;

import java.util.Comparator;
import org.telegram.tgnet.TLRPC$TL_topPeer;

public final /* synthetic */ class MediaDataController$$ExternalSyntheticLambda120 implements Comparator {
    public static final /* synthetic */ MediaDataController$$ExternalSyntheticLambda120 INSTANCE = new MediaDataController$$ExternalSyntheticLambda120();

    private /* synthetic */ MediaDataController$$ExternalSyntheticLambda120() {
    }

    public final int compare(Object obj, Object obj2) {
        return MediaDataController.lambda$increasePeerRaiting$107((TLRPC$TL_topPeer) obj, (TLRPC$TL_topPeer) obj2);
    }
}
