package org.telegram.messenger;

import java.util.Comparator;
import org.telegram.messenger.SecretChatHelper;
/* loaded from: classes.dex */
public final /* synthetic */ class SecretChatHelper$$ExternalSyntheticLambda24 implements Comparator {
    public static final /* synthetic */ SecretChatHelper$$ExternalSyntheticLambda24 INSTANCE = new SecretChatHelper$$ExternalSyntheticLambda24();

    private /* synthetic */ SecretChatHelper$$ExternalSyntheticLambda24() {
    }

    @Override // java.util.Comparator
    public final int compare(Object obj, Object obj2) {
        int lambda$checkSecretHoles$16;
        lambda$checkSecretHoles$16 = SecretChatHelper.lambda$checkSecretHoles$16((SecretChatHelper.TL_decryptedMessageHolder) obj, (SecretChatHelper.TL_decryptedMessageHolder) obj2);
        return lambda$checkSecretHoles$16;
    }
}
