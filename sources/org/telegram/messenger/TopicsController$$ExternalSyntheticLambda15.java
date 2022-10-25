package org.telegram.messenger;

import j$.util.function.ToIntFunction;
import org.telegram.tgnet.TLRPC$TL_forumTopic;
/* loaded from: classes.dex */
public final /* synthetic */ class TopicsController$$ExternalSyntheticLambda15 implements ToIntFunction {
    public static final /* synthetic */ TopicsController$$ExternalSyntheticLambda15 INSTANCE = new TopicsController$$ExternalSyntheticLambda15();

    private /* synthetic */ TopicsController$$ExternalSyntheticLambda15() {
    }

    @Override // j$.util.function.ToIntFunction
    public final int applyAsInt(Object obj) {
        int lambda$sortTopics$5;
        lambda$sortTopics$5 = TopicsController.lambda$sortTopics$5((TLRPC$TL_forumTopic) obj);
        return lambda$sortTopics$5;
    }
}
