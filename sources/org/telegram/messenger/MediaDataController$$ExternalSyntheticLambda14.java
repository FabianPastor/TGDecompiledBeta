package org.telegram.messenger;

import java.util.Comparator;
import org.telegram.tgnet.TLRPC;

public final /* synthetic */ class MediaDataController$$ExternalSyntheticLambda14 implements Comparator {
    public static final /* synthetic */ MediaDataController$$ExternalSyntheticLambda14 INSTANCE = new MediaDataController$$ExternalSyntheticLambda14();

    private /* synthetic */ MediaDataController$$ExternalSyntheticLambda14() {
    }

    public final int compare(Object obj, Object obj2) {
        return MediaDataController.lambda$increaseInlineRaiting$95((TLRPC.TL_topPeer) obj, (TLRPC.TL_topPeer) obj2);
    }
}
