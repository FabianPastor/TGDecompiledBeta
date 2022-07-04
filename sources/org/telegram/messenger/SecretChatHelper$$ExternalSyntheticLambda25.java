package org.telegram.messenger;

import java.util.Comparator;
import org.telegram.tgnet.TLRPC$Message;

public final /* synthetic */ class SecretChatHelper$$ExternalSyntheticLambda25 implements Comparator {
    public static final /* synthetic */ SecretChatHelper$$ExternalSyntheticLambda25 INSTANCE = new SecretChatHelper$$ExternalSyntheticLambda25();

    private /* synthetic */ SecretChatHelper$$ExternalSyntheticLambda25() {
    }

    public final int compare(Object obj, Object obj2) {
        return AndroidUtilities.compare(((TLRPC$Message) obj).seq_out, ((TLRPC$Message) obj2).seq_out);
    }
}
