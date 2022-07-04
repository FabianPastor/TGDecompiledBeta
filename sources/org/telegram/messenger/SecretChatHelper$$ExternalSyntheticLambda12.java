package org.telegram.messenger;

import java.util.ArrayList;

public final /* synthetic */ class SecretChatHelper$$ExternalSyntheticLambda12 implements Runnable {
    public final /* synthetic */ SecretChatHelper f$0;
    public final /* synthetic */ ArrayList f$1;

    public /* synthetic */ SecretChatHelper$$ExternalSyntheticLambda12(SecretChatHelper secretChatHelper, ArrayList arrayList) {
        this.f$0 = secretChatHelper;
        this.f$1 = arrayList;
    }

    public final void run() {
        this.f$0.lambda$processPendingEncMessages$0(this.f$1);
    }
}
