package org.telegram.messenger;

import org.telegram.tgnet.TLRPC.ChatParticipants;

/* compiled from: lambda */
public final /* synthetic */ class -$$Lambda$MessagesStorage$WNrK3eaGJN_z5azqahmhmIWkE3A implements Runnable {
    private final /* synthetic */ MessagesStorage f$0;
    private final /* synthetic */ ChatParticipants f$1;

    public /* synthetic */ -$$Lambda$MessagesStorage$WNrK3eaGJN_z5azqahmhmIWkE3A(MessagesStorage messagesStorage, ChatParticipants chatParticipants) {
        this.f$0 = messagesStorage;
        this.f$1 = chatParticipants;
    }

    public final void run() {
        this.f$0.lambda$updateChatParticipants$65$MessagesStorage(this.f$1);
    }
}
