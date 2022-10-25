package org.telegram.messenger;

import java.util.Comparator;
import org.telegram.tgnet.TLRPC$MessageEntity;
/* loaded from: classes.dex */
public final /* synthetic */ class MediaDataController$$ExternalSyntheticLambda152 implements Comparator {
    public static final /* synthetic */ MediaDataController$$ExternalSyntheticLambda152 INSTANCE = new MediaDataController$$ExternalSyntheticLambda152();

    private /* synthetic */ MediaDataController$$ExternalSyntheticLambda152() {
    }

    @Override // java.util.Comparator
    public final int compare(Object obj, Object obj2) {
        int lambda$getTextStyleRuns$160;
        lambda$getTextStyleRuns$160 = MediaDataController.lambda$getTextStyleRuns$160((TLRPC$MessageEntity) obj, (TLRPC$MessageEntity) obj2);
        return lambda$getTextStyleRuns$160;
    }
}
