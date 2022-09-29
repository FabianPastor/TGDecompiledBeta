package org.telegram.messenger;

import java.util.Comparator;
import org.telegram.tgnet.TLRPC$TL_topPeer;

public final /* synthetic */ class MediaDataController$$ExternalSyntheticLambda150 implements Comparator {
    public static final /* synthetic */ MediaDataController$$ExternalSyntheticLambda150 INSTANCE = new MediaDataController$$ExternalSyntheticLambda150();

    private /* synthetic */ MediaDataController$$ExternalSyntheticLambda150() {
    }

    public final int compare(Object obj, Object obj2) {
        return MediaDataController.lambda$increasePeerRaiting$133((TLRPC$TL_topPeer) obj, (TLRPC$TL_topPeer) obj2);
    }
}
