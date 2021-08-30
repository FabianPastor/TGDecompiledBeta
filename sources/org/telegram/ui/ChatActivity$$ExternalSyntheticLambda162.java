package org.telegram.ui;

import org.telegram.tgnet.TLRPC$Document;
import org.telegram.ui.Components.ChatGreetingsView;

public final /* synthetic */ class ChatActivity$$ExternalSyntheticLambda162 implements ChatGreetingsView.Listener {
    public final /* synthetic */ ChatActivity f$0;

    public /* synthetic */ ChatActivity$$ExternalSyntheticLambda162(ChatActivity chatActivity) {
        this.f$0 = chatActivity;
    }

    public final void onGreetings(TLRPC$Document tLRPC$Document) {
        this.f$0.lambda$createView$11(tLRPC$Document);
    }
}
