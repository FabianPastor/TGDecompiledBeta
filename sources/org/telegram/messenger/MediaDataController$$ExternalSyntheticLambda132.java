package org.telegram.messenger;

import java.util.Comparator;
import org.telegram.tgnet.TLRPC$MessageEntity;

public final /* synthetic */ class MediaDataController$$ExternalSyntheticLambda132 implements Comparator {
    public static final /* synthetic */ MediaDataController$$ExternalSyntheticLambda132 INSTANCE = new MediaDataController$$ExternalSyntheticLambda132();

    private /* synthetic */ MediaDataController$$ExternalSyntheticLambda132() {
    }

    public final int compare(Object obj, Object obj2) {
        return MediaDataController.lambda$getTextStyleRuns$142((TLRPC$MessageEntity) obj, (TLRPC$MessageEntity) obj2);
    }
}
