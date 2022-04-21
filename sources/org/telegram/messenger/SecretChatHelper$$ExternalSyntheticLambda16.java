package org.telegram.messenger;

import java.util.Comparator;
import org.telegram.messenger.SecretChatHelper;

public final /* synthetic */ class SecretChatHelper$$ExternalSyntheticLambda16 implements Comparator {
    public static final /* synthetic */ SecretChatHelper$$ExternalSyntheticLambda16 INSTANCE = new SecretChatHelper$$ExternalSyntheticLambda16();

    private /* synthetic */ SecretChatHelper$$ExternalSyntheticLambda16() {
    }

    public final int compare(Object obj, Object obj2) {
        return SecretChatHelper.lambda$checkSecretHoles$16((SecretChatHelper.TL_decryptedMessageHolder) obj, (SecretChatHelper.TL_decryptedMessageHolder) obj2);
    }
}
