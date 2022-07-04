package org.telegram.ui;

import org.telegram.tgnet.TLRPC;
import org.telegram.ui.Components.ChatGreetingsView;

public final /* synthetic */ class ChatActivity$$ExternalSyntheticLambda158 implements ChatGreetingsView.Listener {
    public final /* synthetic */ ChatActivity f$0;

    public /* synthetic */ ChatActivity$$ExternalSyntheticLambda158(ChatActivity chatActivity) {
        this.f$0 = chatActivity;
    }

    public final void onGreetings(TLRPC.Document document) {
        this.f$0.m2946lambda$createView$26$orgtelegramuiChatActivity(document);
    }
}
