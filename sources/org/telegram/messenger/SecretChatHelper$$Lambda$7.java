package org.telegram.messenger;

import java.util.Comparator;
import org.telegram.messenger.SecretChatHelper.TL_decryptedMessageHolder;

final /* synthetic */ class SecretChatHelper$$Lambda$7 implements Comparator {
    static final Comparator $instance = new SecretChatHelper$$Lambda$7();

    private SecretChatHelper$$Lambda$7() {
    }

    public int compare(Object obj, Object obj2) {
        return SecretChatHelper.lambda$checkSecretHoles$15$SecretChatHelper((TL_decryptedMessageHolder) obj, (TL_decryptedMessageHolder) obj2);
    }
}
