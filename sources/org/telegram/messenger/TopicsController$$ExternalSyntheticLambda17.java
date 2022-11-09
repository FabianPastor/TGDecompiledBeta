package org.telegram.messenger;

import j$.util.function.ToIntFunction;
import org.telegram.tgnet.TLRPC$TL_forumTopic;
/* loaded from: classes.dex */
public final /* synthetic */ class TopicsController$$ExternalSyntheticLambda17 implements ToIntFunction {
    public static final /* synthetic */ TopicsController$$ExternalSyntheticLambda17 INSTANCE = new TopicsController$$ExternalSyntheticLambda17();

    private /* synthetic */ TopicsController$$ExternalSyntheticLambda17() {
    }

    @Override // j$.util.function.ToIntFunction
    public final int applyAsInt(Object obj) {
        int lambda$sortTopics$6;
        lambda$sortTopics$6 = TopicsController.lambda$sortTopics$6((TLRPC$TL_forumTopic) obj);
        return lambda$sortTopics$6;
    }
}
