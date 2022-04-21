package org.telegram.messenger;

import java.util.Comparator;
import org.telegram.tgnet.TLRPC;

public final /* synthetic */ class MediaDataController$$ExternalSyntheticLambda23 implements Comparator {
    public static final /* synthetic */ MediaDataController$$ExternalSyntheticLambda23 INSTANCE = new MediaDataController$$ExternalSyntheticLambda23();

    private /* synthetic */ MediaDataController$$ExternalSyntheticLambda23() {
    }

    public final int compare(Object obj, Object obj2) {
        return MediaDataController.lambda$increaseInlineRaiting$104((TLRPC.TL_topPeer) obj, (TLRPC.TL_topPeer) obj2);
    }
}
