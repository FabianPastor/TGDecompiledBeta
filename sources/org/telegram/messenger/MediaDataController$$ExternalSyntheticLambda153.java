package org.telegram.messenger;

import java.util.Comparator;
import org.telegram.tgnet.TLRPC$TL_topPeer;
/* loaded from: classes.dex */
public final /* synthetic */ class MediaDataController$$ExternalSyntheticLambda153 implements Comparator {
    public static final /* synthetic */ MediaDataController$$ExternalSyntheticLambda153 INSTANCE = new MediaDataController$$ExternalSyntheticLambda153();

    private /* synthetic */ MediaDataController$$ExternalSyntheticLambda153() {
    }

    @Override // java.util.Comparator
    public final int compare(Object obj, Object obj2) {
        int lambda$increasePeerRaiting$137;
        lambda$increasePeerRaiting$137 = MediaDataController.lambda$increasePeerRaiting$137((TLRPC$TL_topPeer) obj, (TLRPC$TL_topPeer) obj2);
        return lambda$increasePeerRaiting$137;
    }
}
