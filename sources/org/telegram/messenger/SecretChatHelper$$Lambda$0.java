package org.telegram.messenger;

import java.util.ArrayList;

final /* synthetic */ class SecretChatHelper$$Lambda$0 implements Runnable {
    private final SecretChatHelper arg$1;
    private final ArrayList arg$2;

    SecretChatHelper$$Lambda$0(SecretChatHelper secretChatHelper, ArrayList arrayList) {
        this.arg$1 = secretChatHelper;
        this.arg$2 = arrayList;
    }

    public void run() {
        this.arg$1.lambda$processPendingEncMessages$0$SecretChatHelper(this.arg$2);
    }
}
