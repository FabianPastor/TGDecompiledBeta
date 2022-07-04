package org.telegram.ui;

import org.telegram.tgnet.TLRPC;
import org.telegram.ui.Components.ChatGreetingsView;

public final /* synthetic */ class ChatActivity$$ExternalSyntheticLambda157 implements ChatGreetingsView.Listener {
    public final /* synthetic */ ChatActivity f$0;

    public /* synthetic */ ChatActivity$$ExternalSyntheticLambda157(ChatActivity chatActivity) {
        this.f$0 = chatActivity;
    }

    public final void onGreetings(TLRPC.Document document) {
        this.f$0.m2945lambda$createView$25$orgtelegramuiChatActivity(document);
    }
}
