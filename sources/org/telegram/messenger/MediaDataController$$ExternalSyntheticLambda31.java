package org.telegram.messenger;

import java.util.Comparator;
import org.telegram.tgnet.TLRPC;

public final /* synthetic */ class MediaDataController$$ExternalSyntheticLambda31 implements Comparator {
    public static final /* synthetic */ MediaDataController$$ExternalSyntheticLambda31 INSTANCE = new MediaDataController$$ExternalSyntheticLambda31();

    private /* synthetic */ MediaDataController$$ExternalSyntheticLambda31() {
    }

    public final int compare(Object obj, Object obj2) {
        return MediaDataController.lambda$increasePeerRaiting$115((TLRPC.TL_topPeer) obj, (TLRPC.TL_topPeer) obj2);
    }
}
