package org.telegram.messenger;

import java.util.Comparator;
import org.telegram.tgnet.TLRPC$TL_topPeer;

public final /* synthetic */ class MediaDataController$$ExternalSyntheticLambda127 implements Comparator {
    public static final /* synthetic */ MediaDataController$$ExternalSyntheticLambda127 INSTANCE = new MediaDataController$$ExternalSyntheticLambda127();

    private /* synthetic */ MediaDataController$$ExternalSyntheticLambda127() {
    }

    public final int compare(Object obj, Object obj2) {
        return MediaDataController.lambda$increasePeerRaiting$115((TLRPC$TL_topPeer) obj, (TLRPC$TL_topPeer) obj2);
    }
}
