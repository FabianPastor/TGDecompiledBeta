package org.telegram.messenger;

import java.util.Comparator;
import org.telegram.tgnet.TLRPC$TL_topPeer;

public final /* synthetic */ class MediaDataController$$ExternalSyntheticLambda130 implements Comparator {
    public static final /* synthetic */ MediaDataController$$ExternalSyntheticLambda130 INSTANCE = new MediaDataController$$ExternalSyntheticLambda130();

    private /* synthetic */ MediaDataController$$ExternalSyntheticLambda130() {
    }

    public final int compare(Object obj, Object obj2) {
        return MediaDataController.lambda$increasePeerRaiting$119((TLRPC$TL_topPeer) obj, (TLRPC$TL_topPeer) obj2);
    }
}
