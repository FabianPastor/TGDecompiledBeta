package org.telegram.messenger;

import java.util.Comparator;
import org.telegram.tgnet.TLRPC$MessageEntity;

public final /* synthetic */ class MediaDataController$$ExternalSyntheticLambda125 implements Comparator {
    public static final /* synthetic */ MediaDataController$$ExternalSyntheticLambda125 INSTANCE = new MediaDataController$$ExternalSyntheticLambda125();

    private /* synthetic */ MediaDataController$$ExternalSyntheticLambda125() {
    }

    public final int compare(Object obj, Object obj2) {
        return MediaDataController.lambda$getTextStyleRuns$138((TLRPC$MessageEntity) obj, (TLRPC$MessageEntity) obj2);
    }
}
