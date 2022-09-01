package org.telegram.messenger;

import java.util.Comparator;
import org.telegram.tgnet.TLRPC$TL_topPeer;

public final /* synthetic */ class MediaDataController$$ExternalSyntheticLambda149 implements Comparator {
    public static final /* synthetic */ MediaDataController$$ExternalSyntheticLambda149 INSTANCE = new MediaDataController$$ExternalSyntheticLambda149();

    private /* synthetic */ MediaDataController$$ExternalSyntheticLambda149() {
    }

    public final int compare(Object obj, Object obj2) {
        return MediaDataController.lambda$increasePeerRaiting$133((TLRPC$TL_topPeer) obj, (TLRPC$TL_topPeer) obj2);
    }
}
