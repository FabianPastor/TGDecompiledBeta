package org.telegram.messenger;

import java.util.Comparator;
import org.telegram.tgnet.TLRPC$MessageEntity;

public final /* synthetic */ class MediaDataController$$ExternalSyntheticLambda119 implements Comparator {
    public static final /* synthetic */ MediaDataController$$ExternalSyntheticLambda119 INSTANCE = new MediaDataController$$ExternalSyntheticLambda119();

    private /* synthetic */ MediaDataController$$ExternalSyntheticLambda119() {
    }

    public final int compare(Object obj, Object obj2) {
        return MediaDataController.lambda$getTextStyleRuns$129((TLRPC$MessageEntity) obj, (TLRPC$MessageEntity) obj2);
    }
}
