package org.telegram.messenger;

import java.util.Comparator;
import org.telegram.tgnet.TLRPC;

public final /* synthetic */ class MediaDataController$$ExternalSyntheticLambda24 implements Comparator {
    public static final /* synthetic */ MediaDataController$$ExternalSyntheticLambda24 INSTANCE = new MediaDataController$$ExternalSyntheticLambda24();

    private /* synthetic */ MediaDataController$$ExternalSyntheticLambda24() {
    }

    public final int compare(Object obj, Object obj2) {
        return MediaDataController.lambda$increasePeerRaiting$107((TLRPC.TL_topPeer) obj, (TLRPC.TL_topPeer) obj2);
    }
}
