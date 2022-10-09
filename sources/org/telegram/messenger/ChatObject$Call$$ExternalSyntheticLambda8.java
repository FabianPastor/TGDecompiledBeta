package org.telegram.messenger;

import java.util.Comparator;
import org.telegram.messenger.ChatObject;
import org.telegram.tgnet.TLRPC$TL_updateGroupCallParticipants;
/* loaded from: classes.dex */
public final /* synthetic */ class ChatObject$Call$$ExternalSyntheticLambda8 implements Comparator {
    public static final /* synthetic */ ChatObject$Call$$ExternalSyntheticLambda8 INSTANCE = new ChatObject$Call$$ExternalSyntheticLambda8();

    private /* synthetic */ ChatObject$Call$$ExternalSyntheticLambda8() {
    }

    @Override // java.util.Comparator
    public final int compare(Object obj, Object obj2) {
        int lambda$processUpdatesQueue$7;
        lambda$processUpdatesQueue$7 = ChatObject.Call.lambda$processUpdatesQueue$7((TLRPC$TL_updateGroupCallParticipants) obj, (TLRPC$TL_updateGroupCallParticipants) obj2);
        return lambda$processUpdatesQueue$7;
    }
}
