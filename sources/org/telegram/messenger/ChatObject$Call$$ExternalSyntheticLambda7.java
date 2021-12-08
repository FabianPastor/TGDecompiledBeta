package org.telegram.messenger;

import java.util.Comparator;
import org.telegram.tgnet.TLRPC$TL_updateGroupCallParticipants;

public final /* synthetic */ class ChatObject$Call$$ExternalSyntheticLambda7 implements Comparator {
    public static final /* synthetic */ ChatObject$Call$$ExternalSyntheticLambda7 INSTANCE = new ChatObject$Call$$ExternalSyntheticLambda7();

    private /* synthetic */ ChatObject$Call$$ExternalSyntheticLambda7() {
    }

    public final int compare(Object obj, Object obj2) {
        return AndroidUtilities.compare(((TLRPC$TL_updateGroupCallParticipants) obj).version, ((TLRPC$TL_updateGroupCallParticipants) obj2).version);
    }
}
