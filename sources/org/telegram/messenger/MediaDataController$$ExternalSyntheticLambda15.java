package org.telegram.messenger;

import java.util.Comparator;
import org.telegram.tgnet.TLRPC;

public final /* synthetic */ class MediaDataController$$ExternalSyntheticLambda15 implements Comparator {
    public static final /* synthetic */ MediaDataController$$ExternalSyntheticLambda15 INSTANCE = new MediaDataController$$ExternalSyntheticLambda15();

    private /* synthetic */ MediaDataController$$ExternalSyntheticLambda15() {
    }

    public final int compare(Object obj, Object obj2) {
        return MediaDataController.lambda$increasePeerRaiting$98((TLRPC.TL_topPeer) obj, (TLRPC.TL_topPeer) obj2);
    }
}
