package org.telegram.messenger;

import java.util.Comparator;
import org.telegram.tgnet.TLRPC;

public final /* synthetic */ class MediaDataController$$ExternalSyntheticLambda30 implements Comparator {
    public static final /* synthetic */ MediaDataController$$ExternalSyntheticLambda30 INSTANCE = new MediaDataController$$ExternalSyntheticLambda30();

    private /* synthetic */ MediaDataController$$ExternalSyntheticLambda30() {
    }

    public final int compare(Object obj, Object obj2) {
        return MediaDataController.lambda$increaseInlineRaiting$112((TLRPC.TL_topPeer) obj, (TLRPC.TL_topPeer) obj2);
    }
}
