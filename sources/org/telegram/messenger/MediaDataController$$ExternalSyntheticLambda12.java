package org.telegram.messenger;

import java.util.Comparator;
import org.telegram.tgnet.TLRPC;

public final /* synthetic */ class MediaDataController$$ExternalSyntheticLambda12 implements Comparator {
    public static final /* synthetic */ MediaDataController$$ExternalSyntheticLambda12 INSTANCE = new MediaDataController$$ExternalSyntheticLambda12();

    private /* synthetic */ MediaDataController$$ExternalSyntheticLambda12() {
    }

    public final int compare(Object obj, Object obj2) {
        return MediaDataController.lambda$getTextStyleRuns$120((TLRPC.MessageEntity) obj, (TLRPC.MessageEntity) obj2);
    }
}
