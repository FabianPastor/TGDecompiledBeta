package org.telegram.messenger;

import java.util.Comparator;
import org.telegram.tgnet.TLRPC;

public final /* synthetic */ class MediaDataController$$ExternalSyntheticLambda20 implements Comparator {
    public static final /* synthetic */ MediaDataController$$ExternalSyntheticLambda20 INSTANCE = new MediaDataController$$ExternalSyntheticLambda20();

    private /* synthetic */ MediaDataController$$ExternalSyntheticLambda20() {
    }

    public final int compare(Object obj, Object obj2) {
        return MediaDataController.lambda$getTextStyleRuns$130((TLRPC.MessageEntity) obj, (TLRPC.MessageEntity) obj2);
    }
}
