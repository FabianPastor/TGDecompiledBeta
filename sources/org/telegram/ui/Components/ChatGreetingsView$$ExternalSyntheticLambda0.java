package org.telegram.ui.Components;

import android.view.View;
import org.telegram.tgnet.TLRPC;

public final /* synthetic */ class ChatGreetingsView$$ExternalSyntheticLambda0 implements View.OnClickListener {
    public final /* synthetic */ ChatGreetingsView f$0;
    public final /* synthetic */ TLRPC.Document f$1;

    public /* synthetic */ ChatGreetingsView$$ExternalSyntheticLambda0(ChatGreetingsView chatGreetingsView, TLRPC.Document document) {
        this.f$0 = chatGreetingsView;
        this.f$1 = document;
    }

    public final void onClick(View view) {
        this.f$0.m2198lambda$setSticker$0$orgtelegramuiComponentsChatGreetingsView(this.f$1, view);
    }
}
