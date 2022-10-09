package org.telegram.messenger;

import java.util.Comparator;
import org.telegram.tgnet.TLRPC$MessageEntity;
/* loaded from: classes.dex */
public final /* synthetic */ class MessageObject$$ExternalSyntheticLambda2 implements Comparator {
    public static final /* synthetic */ MessageObject$$ExternalSyntheticLambda2 INSTANCE = new MessageObject$$ExternalSyntheticLambda2();

    private /* synthetic */ MessageObject$$ExternalSyntheticLambda2() {
    }

    @Override // java.util.Comparator
    public final int compare(Object obj, Object obj2) {
        int lambda$addEntitiesToText$2;
        lambda$addEntitiesToText$2 = MessageObject.lambda$addEntitiesToText$2((TLRPC$MessageEntity) obj, (TLRPC$MessageEntity) obj2);
        return lambda$addEntitiesToText$2;
    }
}
