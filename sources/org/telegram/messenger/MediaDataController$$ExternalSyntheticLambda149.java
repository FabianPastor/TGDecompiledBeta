package org.telegram.messenger;

import java.util.Comparator;
import org.telegram.tgnet.TLRPC$MessageEntity;
/* loaded from: classes.dex */
public final /* synthetic */ class MediaDataController$$ExternalSyntheticLambda149 implements Comparator {
    public static final /* synthetic */ MediaDataController$$ExternalSyntheticLambda149 INSTANCE = new MediaDataController$$ExternalSyntheticLambda149();

    private /* synthetic */ MediaDataController$$ExternalSyntheticLambda149() {
    }

    @Override // java.util.Comparator
    public final int compare(Object obj, Object obj2) {
        int lambda$getTextStyleRuns$156;
        lambda$getTextStyleRuns$156 = MediaDataController.lambda$getTextStyleRuns$156((TLRPC$MessageEntity) obj, (TLRPC$MessageEntity) obj2);
        return lambda$getTextStyleRuns$156;
    }
}
